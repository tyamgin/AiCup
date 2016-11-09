using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Threading;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

/**
 * TODO:
 * - стрелять минуя своих (устанавливать в move minDist & maxDist)
 * - моделирование снаряда, моделирование уворота соперника
 * - - пересечание отрезка и окружности?
 * - - проверять деревья
 */

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public static World world;
        public static Game game;
        public static Wizard self;
        public static Move move;

        public AWizard[] Wizards, OpponentWizards;
        public AMinion[] Minions, OpponentMinions;
        public ABuilding[] Buildings, OpponentBuildings;
        public ACombatUnit[] Combats, OpponentCombats;

        public void Move(Wizard self, World world, Game game, Move move)
        {
            MyStrategy.world = world;
            MyStrategy.game = game;
            MyStrategy.self = self;
            MyStrategy.move = move;

            Const.Width = world.Width;
            Const.Height = world.Height;

            TreesObserver.Update(world);
            BuildingsObserver.Update(world);

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

            InitializeDijkstra();

#if DEBUG
            while (Visualizer.Visualizer.Pause)
            {
                // pause here
            }
            Visualizer.Visualizer.CreateForm();
#endif
            //_testMagicMissile();

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

            var target = FindTarget();
            if (target != null)
            {
                move.Turn = self.GetAngleTo(nearest.X, nearest.Y);
            }
            else
            {
                GoAround(nearest ?? goTo);
            }
#if DEBUG
            Visualizer.Visualizer.LookUp(new Point(self.X, self.Y));
            Visualizer.Visualizer.Draw();
            Thread.Sleep(10);
#endif
        }

        void GoAround(Point to)
        {
            var path = DijkstraFindPath(new Point(self), to);
            while (path.Count > 0 && path[0].GetDistanceTo(self) <= CellLength)
                path.RemoveAt(0);
            if (path.Count == 0)
                return;
            var pt = path[0];

            var angle = self.GetAngleTo(pt.X, pt.Y);
            var forwardSpeed = Math.Cos(angle)*game.WizardForwardSpeed;
            var strafeSpeed = Math.Sin(angle)*game.WizardStrafeSpeed;
            move.Speed = forwardSpeed;
            move.StrafeSpeed = strafeSpeed;
            move.Turn = angle;

#if DEBUG
            var tmp = Geom.Hypot(forwardSpeed / game.WizardForwardSpeed, strafeSpeed / game.WizardStrafeSpeed);
            Visualizer.Visualizer.SegmentsDrawQueue.Add(new object[] { path, Pens.Aqua });
#endif
        }

        Point FindTarget()
        {
            var angles = new List<double>();
            foreach (var x in OpponentCombats)
            {
                var angle = self.GetAngleTo(x.X, x.Y);
                var dist = self.GetDistanceTo(x.X, x.Y);

                if (dist > self.CastRange*1.2 || Math.Abs(angle) > game.StaffSector / 2)
                    continue;

                const int grid = 20;
                double left = -game.StaffSector/2, right = -left;
                for (var i = 0; i <= grid; i++)
                {
                    var castAngle = angle + (right - left)/grid*i + left;
                    angles.Add(castAngle);
                }
            }

            double selCastAngle = 0;
            ACombatUnit selTarget = null;
            double selMinDist = 0, selMaxDist = self.CastRange;

            foreach (var angle in angles)
            {
                var proj = new AProjectile(new AWizard(self), angle, ProjectileType.MagicMissile);
                var path = EmulateMagicMissile(proj);
                for (var i = 0; i < path.Count; i++)
                {
                    if (path[i].State == ProjectilePathState.Fire && path[i].Target is ACombatUnit)
                    {
                        var combat = (ACombatUnit) path[i].Target;
                        if (combat.IsOpponent && (selTarget == null || combat.Life < selTarget.Life))
                        {
                            selTarget = combat;
                            selCastAngle = angle;
                            selMinDist = i == 0 ? 0 : (path[i - 1].StartDistance + path[i].StartDistance)/2;
                            selMaxDist = i == path.Count - 1 ? self.CastRange : (path[i + 1].EndDistance + path[i].EndDistance) / 2;
                        }
                    }
                }
            }
            if (selTarget == null)
                return null;
            move.Action = ActionType.MagicMissile;
            move.MinCastDistance = selMinDist;
            move.MaxCastDistance = selMaxDist;
            move.CastAngle = selCastAngle;
#if DEBUG
            Visualizer.Visualizer.SegmentsDrawQueue.Add(new object[]
            {
                new List<Point>
                {
                    new Point(self) + Point.ByAngle(self.Angle) * selMinDist,
                    new Point(self) + Point.ByAngle(self.Angle) * selMaxDist
                },
                Pens.DarkOrchid,
                5
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
            var units = Combats.Where(x => x.Id != self.Id).ToArray();
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