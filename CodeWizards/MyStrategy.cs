using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Runtime.Remoting.Messaging;
using System.Threading;
using System.Windows.Forms;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

/**
 * TODO:
 * - бить посохом
 * - учитывать препятствия-юниты в дейкстре
 * - отдельная дейкстра для зоны боя
 * 
 * - бить посохом башни
 * - наблюдение за агрессивными (двигающимися) нейтралами
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

        public static AWizard[] Wizards, OpponentWizards;
        public static AMinion[] Minions, OpponentMinions;
        public static ABuilding[] OpponentBuildings;
        public static ACombatUnit[] Combats, OpponentCombats;

        public static AProjectile[][] ProjectilesPaths;

        public void Move(Wizard self, World world, Game game, Move move)
        {
            TimerStart();
            _move(self, world, game, move);
            TimerEndLog("All", 0);

#if DEBUG
            Visualizer.Visualizer.LookUp(new Point(self));
            Visualizer.Visualizer.Draw();
            Thread.Sleep(20);
#endif
        }

        private void _move(Wizard self, World world, Game game, Move move)
        {
            World = world;
            Game = game;
            Self = self;
            FinalMove = move;

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
            
            OpponentBuildings = BuildingsObserver.Buildings
                .Where(x => x.IsOpponent)
                .ToArray();

            Combats =
                Minions.Cast<ACombatUnit>()
                .Concat(Wizards)
                .Concat(BuildingsObserver.Buildings)
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

            //TreesObserver.RecheckAll();

            InitializeProjectiles();
            InitializeDijkstra();

#if DEBUG
            while (Visualizer.Visualizer.Pause)
            {
                // pause here
            }
            Visualizer.Visualizer.CreateForm();
            Visualizer.Visualizer.DangerPoints = CalculateDangerMap();
#endif


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
                if (nearest == null || !GoAround(nearest))
                    GoAround(goTo);
            }

            if (!TryDodge())
            {
                TryDodge2();
            }
        }

        void SimplifyPath(AWizard self, ACircularUnit[] obstacles, List<Point> path)
        {
            for (var i = 2; i < path.Count; i++)
            {
                var a = path[i - 2];
                var c = path[i];
                if (obstacles.All(ob =>
                {
                    return Geom.SegmentCircleIntersect(a, c, ob, self.Radius + ob.Radius).Length == 0;
                }))
                {
                    path.RemoveAt(i - 1);
                    i--;
                }
            }
        }

        bool GoAround(Point to)
        {
            TimerStart();
            var ret = _goAround(to);
            TimerEndLog("Dijkstra", 1);
            return ret;
        }

        bool _goAround(Point to)
        {
            var path = DijkstraFindPath(new AWizard(Self), to);
            if (path == null)
                return false;

            if (path.Count < 2)
                return true;

            var my = new AWizard(Self);
            var obstacles =
                Combats.Where(x => x.Id != Self.Id).Cast<ACircularUnit>()
                .Concat(TreesObserver.Trees)
                .Where(x => my.GetDistanceTo2(x) < Geom.Sqr(my.VisionRange)) //???
                .ToArray();

            SimplifyPath(my, obstacles, path);

            var pt = path[1];

            var angle = Self.GetAngleTo(pt.X, pt.Y);
            var forwardSpeed = Math.Cos(angle)*Game.WizardForwardSpeed;
            var strafeSpeed = Math.Sin(angle)*Game.WizardStrafeSpeed;
            FinalMove.Speed = forwardSpeed;
            FinalMove.StrafeSpeed = strafeSpeed;
            FinalMove.Turn = angle;

#if DEBUG
            Visualizer.Visualizer.SegmentsDrawQueue.Add(new object[] { path, Pens.Aqua });
#endif
            return true;
        }

        private bool HasAnyTarget(AWizard self)
        {
            foreach (var opp in OpponentCombats)
            {
                var dist = self.GetDistanceTo(opp);
                if (dist > self.VisionRange)
                    continue;

                var angleTo = self.GetAngleTo(opp);
                var deltaAngle = Math.Atan2(opp.Radius, dist);
                var angles = new[] { angleTo, angleTo + deltaAngle, angleTo - deltaAngle };

                foreach (var angle in angles)
                {
                    if (Math.Abs(angle) > Game.StaffSector / 2)
                        continue;

                    var proj = new AProjectile(self, angle, ProjectileType.MagicMissile);
                    var path = EmulateMagicMissile(proj);
                    if (path.Any(x => x.State == ProjectilePathState.Fire && x.Target.IsOpponent))
                        return true;
                }
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
            double selMinDist = 0, selMaxDist = self.CastRange + 20, selAngleTo = 0;

            foreach (var angle in angles)
            {
                var proj = new AProjectile(new AWizard(self), angle, ProjectileType.MagicMissile);
                var path = EmulateMagicMissile(proj);
                for (var i = 0; i < path.Count; i++)
                {
                    if (path[i].State == ProjectilePathState.Fire)
                    {
                        var combat = path[i].Target;
                        var angleTo = self.GetAngleTo(combat) - angle;
                        Geom.AngleNormalize(ref angleTo);
                        angleTo = Math.Abs(angleTo);
                        if (combat.IsOpponent && (selTarget == null || combat.Life < selTarget.Life || Math.Abs(combat.Life - selTarget.Life) < Const.Eps && angleTo < selAngleTo))
                        {
                            selTarget = combat;
                            selCastAngle = angle;
                            selAngleTo = angleTo;
                            selMinDist = i == 0 ? 0 : (path[i - 1].StartDistance + path[i].StartDistance)/2;
                            selMaxDist = i == path.Count - 1 ? (self.CastRange + 20) : (path[i + 1].EndDistance + path[i].EndDistance) / 2;
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
            public ACombatUnit Target;
            public double StartDistance, EndDistance;
        }

        List<ProjectilePathSegment> EmulateMagicMissile(AProjectile projectile)
        {
            var units = Combats.Where(x => x.Id != Self.Id).ToArray();
            var list = new List<ProjectilePathSegment>();
            while (projectile.Exists)
            {
                projectile.Move(proj =>
                {
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
                                Target = inter as ACombatUnit,
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

                    return true;
                });

            }
            return list;
        }
    }
}