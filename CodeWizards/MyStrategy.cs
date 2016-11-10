using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Runtime.Remoting.Messaging;
using System.Threading;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

/**
 * TODO:
 * - моделирование уворота соперника
 * - - для этого нужно определять сколько осталось лететь снаряду
 * - - проверять деревья???
 * 
 * - бить посохом
 * - учитывать препятствия-юниты в дейкстре
 * - отдельная дейкстра для зоны боя
 * 
 * - бить посохом башни
 */

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public static World World;
        public static Game Game;
        public static Wizard Self;
        public static Move FinalMove;

        public static long[] FriendsIds;

        public AWizard[] Wizards, OpponentWizards;
        public AMinion[] Minions, OpponentMinions;
        public ABuilding[] Buildings, OpponentBuildings;
        public ACombatUnit[] Combats, OpponentCombats;

        public static AProjectile[][] ProjectilesPaths; 

        public void Move(Wizard self, World world, Game game, Move move)
        {
            MyStrategy.World = world;
            MyStrategy.Game = game;
            MyStrategy.Self = self;
            MyStrategy.FinalMove = move;

            Const.Width = world.Width;
            Const.Height = world.Height;

            Wizards = world.Wizards
                .Select(x => new AWizard(x))
                .ToArray();

            OpponentWizards = Wizards
                .Where(x => x.IsOpponent)
                .ToArray();

            Minions = world.Minions
                .Select(x => new AMinion(x))
                .ToArray();

            OpponentMinions = Minions
                .Where(x => x.IsOpponent)
                .ToArray();

            Buildings = world.Buildings
                .Select(x => new ABuilding(x))
                .ToArray();

            OpponentBuildings = Buildings
                .Where(x => x.IsOpponent)
                .ToArray();

            Combats =
                Minions.Cast<ACombatUnit>()
                .Concat(Wizards)
                .Concat(Buildings)
                .ToArray();

            OpponentCombats = Combats
                .Where(x => x.IsOpponent)
                .ToArray();

            FriendsIds = Combats
                .Where(x => x.IsTeammate)
                .Select(x => x.Id)
                .ToArray();


            TreesObserver.Update(world);
            BuildingsObserver.Update(world);
            ProjectilesObserver.Update(world);

            InitializeProjectiles();
            InitializeDijkstra();

#if DEBUG
            while (Visualizer.Visualizer.Pause)
            {
                // pause here
            }
            Visualizer.Visualizer.CreateForm();
#endif
            //_testMagicMissile();
            Visualizer.Visualizer.DangerPoints = CalculateDangerMap();

            var goTo = new Point(Const.Width - 120, 120);

            double minDist = int.MaxValue;
            ACombatUnit nearest = null;

            foreach (var x in OpponentCombats)
            {
                var dst = x.GetDistanceTo(self);
                if (dst < minDist)
                {
                    minDist = dst;
                    nearest = x;
                }
            }

            var target = FindTarget(new AWizard(self));
            if (target != null)
            {
                move.Turn = self.GetAngleTo(nearest.X, nearest.Y);
            }
            else
            {
                GoAround(nearest ?? goTo);
            }

            if (!TryDodge())
            {
                TryDodge2();
            }
#if DEBUG
            Visualizer.Visualizer.LookUp(new Point(self.X, self.Y));
            Visualizer.Visualizer.Draw();
            Thread.Sleep(15);
#endif
        }

        void GoAround(Point to)
        {
            var path = DijkstraFindPath(new Point(Self), to);
            while (path.Count > 0 && path[0].GetDistanceTo(Self) <= CellLength)
                path.RemoveAt(0);
            if (path.Count == 0)
                return;
            var pt = path[0];

            var angle = Self.GetAngleTo(pt.X, pt.Y);
            var forwardSpeed = Math.Cos(angle)*Game.WizardForwardSpeed;
            var strafeSpeed = Math.Sin(angle)*Game.WizardStrafeSpeed;
            FinalMove.Speed = forwardSpeed;
            FinalMove.StrafeSpeed = strafeSpeed;
            FinalMove.Turn = angle;

#if DEBUG
            Visualizer.Visualizer.SegmentsDrawQueue.Add(new object[] { path, Pens.Aqua });
#endif
        }

        private bool HasAnyTarget(AWizard self)
        {
            foreach (var x in OpponentCombats)
            {
                var angle = self.GetAngleTo(x);
                var dist = self.GetDistanceTo(x);

                if (dist > self.CastRange + x.Radius)
                    continue;
                if (Math.Abs(angle) > Game.StaffSector/2)
                    continue;

                return true;
            }
            return false;
        }

        Point FindTarget(AWizard self)
        {
            var angles = new List<double>();
            foreach (var x in OpponentCombats)
            {
                var angle = self.GetAngleTo(x);
                var dist = self.GetDistanceTo(x);

                if (dist > self.CastRange*1.2 || Math.Abs(angle) > Game.StaffSector / 2)
                    continue;

                const int grid = 20;
                double left = -Game.StaffSector/2, right = -left;
                for (var i = 0; i <= grid; i++)
                {
                    var castAngle = angle + (right - left)/grid*i + left;
                    angles.Add(castAngle);
                }
            }

            double selCastAngle = 0;
            ACombatUnit selTarget = null;
            double selMinDist = 0, selMaxDist = self.CastRange, selAngleTo = 0;

            foreach (var angle in angles)
            {
                var proj = new AProjectile(new AWizard(self), angle, ProjectileType.MagicMissile);
                var path = EmulateMagicMissile(proj);
                for (var i = 0; i < path.Count; i++)
                {
                    if (path[i].State == ProjectilePathState.Fire && path[i].Target is ACombatUnit)
                    {
                        var combat = (ACombatUnit) path[i].Target;
                        var angleTo = self.GetAngleTo(combat) - angle;
                        Geom.AngleNormalize(ref angleTo);
                        angleTo = Math.Abs(angleTo);
                        if (combat.IsOpponent && (selTarget == null || combat.Life < selTarget.Life || Math.Abs(combat.Life - selTarget.Life) < Const.Eps && angleTo < selAngleTo))
                        {
                            selTarget = combat;
                            selCastAngle = angle;
                            selAngleTo = angleTo;
                            selMinDist = i == 0 ? 0 : (path[i - 1].StartDistance + path[i].StartDistance)/2;
                            selMaxDist = i == path.Count - 1 ? self.CastRange : (path[i + 1].EndDistance + path[i].EndDistance) / 2;
                        }
                    }
                }
            }
            if (selTarget == null)
                return null;
            FinalMove.Action = ActionType.MagicMissile;
            FinalMove.MinCastDistance = selMinDist;
            FinalMove.MaxCastDistance = selMaxDist;
            FinalMove.CastAngle = selCastAngle;
#if DEBUG
            Visualizer.Visualizer.SegmentsDrawQueue.Add(new object[]
            {
                new List<Point>
                {
                    new Point(self) + Point.ByAngle(self.Angle + selCastAngle) * selMinDist,
                    new Point(self) + Point.ByAngle(self.Angle + selCastAngle) * selMaxDist
                },
                Pens.DarkOrchid,
                2
            });
#endif
            return selTarget;
        }

        enum ProjectilePathState
        {
            Free,
            Fire,
        }

        class ProjectilePathSegment
        {
            public ProjectilePathState State;
            public ACircularUnit Target;
            public double StartDistance, EndDistance;
        }

        List<ProjectilePathSegment> EmulateMagicMissile(AProjectile proj)
        {
            var units = Combats.Where(x => x.Id != Self.Id).ToArray();//TODO: деревья
            var list = new List<ProjectilePathSegment>();
            while (proj.Exists)
            {
                proj.MicroMove();
                var inter = proj.CheckIntersections(units);

                if (inter != null)
                {
                    if (list.Count == 0 || list[list.Count - 1].State != ProjectilePathState.Fire)
                    {
                        list.Add(new ProjectilePathSegment
                        {
                            StartDistance = list.Count == 0 ? 0 : list[list.Count - 1].EndDistance,
                            EndDistance = list.Count == 0 ? 0 : list[list.Count - 1].EndDistance,
                            State = ProjectilePathState.Fire,
                            Target = inter,
                        });
                    }
                }
                else
                {
                    if (list.Count == 0 || list[list.Count - 1].State != ProjectilePathState.Free)
                    {
                        list.Add(new ProjectilePathSegment
                        {
                            StartDistance = list.Count == 0 ? 0 : list[list.Count - 1].EndDistance,
                            EndDistance = list.Count == 0 ? 0 : list[list.Count - 1].EndDistance,
                            State = ProjectilePathState.Free,
                        });
                    }
                }
                var last = list[list.Count - 1];
                last.EndDistance += proj.Speed / AProjectile.MicroTicks;
            }
            return list;
        }
    }
}