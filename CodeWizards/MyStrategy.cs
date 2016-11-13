using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Threading;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

/**
 * TODO:
 * 
 * - наблюдение за агрессивными (двигающимися) нейтралами
 * 
 * - если застрял, рубить деревья http://russianaicup.ru/game/view/7490
 * - разбивать деревья, если противник спрятался за ними ???
 * - идти по уже разбитой ветке, если убили ???
 * 
 */

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public static World World;
        public static Game Game;
        public static Wizard Self;
        public static FinalMove FinalMove;

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
            Visualizer.Visualizer.CreateForm();
            Visualizer.Visualizer.DangerPoints = CalculateDangerMap();
            Visualizer.Visualizer.LookUp(new Point(self));
            Visualizer.Visualizer.Draw();
            Thread.Sleep(20); // чтобы успело отрисоваться
#endif
        }

        private void _move(Wizard self, World world, Game game, Move move)
        {
            World = world;
            Game = game;
            Self = self;
            FinalMove = new FinalMove(move);

            Const.Width = world.Width;
            Const.Height = world.Height;

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
            ProjectilesObserver.Update(world);

            //TreesObserver.RecheckAll();

            InitializeProjectiles();
            InitializeDijkstra();

            foreach (var bld in OpponentBuildings)
            {
                var his = OpponentCombats.Count(x => x.Id != bld.Id && bld.GetDistanceTo(x) < Self.VisionRange*1.1);
                var mines = Combats.Count(x => x.IsTeammate && bld.GetDistanceTo(x) < Self.VisionRange * 1.1);
                if (his == 0 && mines > 2 || his == 1 && mines > 3 || his == 2 && mines > 5)
                    bld.IsBesieded = true;
            }

            if (Self.IsMaster && World.TickIndex == 0)
            {
                MasterSendMessages();
                //FinalMove.Messages = new[]
                //{
                //    new Model.Message(LaneType.Bottom, SkillType.Shield, new byte[] {}),
                //    new Model.Message(LaneType.Bottom, SkillType.Shield, new byte[] {}),
                //    new Model.Message(LaneType.Bottom, SkillType.Shield, new byte[] {}),
                //    new Model.Message(LaneType.Bottom, SkillType.Shield, new byte[] {}),
                //};
                return;
            }

#if DEBUG
            var master = Wizards.FirstOrDefault(x => x.IsTeammate && x.IsMaster);
            var masterName = master == null ? "" : World.Players.FirstOrDefault(x => x.Id == master.Id).Name;
#endif

#if DEBUG
            while (Visualizer.Visualizer.Pause)
            {
                // pause here
            }
#endif
            var target = FindTarget(new AWizard(self));
            if (target != null)
            {
                move.Turn = self.GetAngleTo(target.X, target.Y);
            }
            else
            {
                var nearest = OpponentCombats
                    .OrderBy(x => x.GetDistanceTo(self) + (x is AWizard ? -20 : (x is ABuilding && ((ABuilding) x).IsBesieded) ? 20 : 0))
                    .Where((x, i) => i == 0 || x.GetDistanceTo(self) < self.VisionRange * 1.7)// чтобы не переходить на другую линию
                    .ToArray();
                if (nearest.Length > 0 && nearest.FirstOrDefault(GoAround) == null)
                {
                    FinalMove.MoveTo(nearest[0], nearest[0]);
                }
            }

            if (!TryDodge())
            {
                if (target == null || FinalMove.Action == ActionType.Staff)
                    TryDodge2();
            }
        }

        void SimplifyPath(AWizard self, ACircularUnit[] obstacles, List<Point> path)
        {
            for (var i = 2; i < path.Count; i++)
            {
                var a = path[i - 2];
                var c = path[i];
                if (obstacles.All(ob => !Geom.SegmentCircleIntersects(a, c, ob, self.Radius + ob.Radius + 1/*(epsilon)*/)))
                {
                    path.RemoveAt(i - 1);
                    i--;
                }
            }
        }

        bool GoAround(ACircularUnit to)
        {
            TimerStart();
            var ret = _goAround(to);
            TimerEndLog("Dijkstra", 1);
            return ret;
        }

        bool _goAround(ACircularUnit target)
        {
            var path = DijkstraFindPath(new AWizard(Self), target);
            var my = new AWizard(Self);
            if (path == null || path.Count == 0)
                return false;

            if (path.Count == 1)
            {
                FinalMove.Turn = my.GetAngleTo(target);
                return true;
            }

            var obstacles =
                Combats.Where(x => x.Id != Self.Id).Cast<ACircularUnit>()
                .Concat(TreesObserver.Trees)
                .Where(x => my.GetDistanceTo2(x) < Geom.Sqr(my.VisionRange)) //???
                .ToArray();

            SimplifyPath(my, obstacles, path);

            var nextPoint = path[1];
            FinalMove.MoveTo(nextPoint, path.Count > 2 && my.GetDistanceTo(path[2]) < Self.VisionRange ? path[2] : nextPoint);
#if DEBUG
            Visualizer.Visualizer.SegmentsDrawQueue.Add(new object[] { path, Pens.Blue, 3 });
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
            var castTarget = FindCastTarget(self);
            if (castTarget != null)
                return castTarget;

            var staffTarget = FindStaffTarget(self);
            return staffTarget;
        }

        Point FindStaffTarget(AWizard self)
        {
            var nearest = Combats
                .Where(x => x.Id != self.Id && self.GetDistanceTo2(x) < Geom.Sqr(Game.StaffRange*3))
                .ToArray();
            ACircularUnit selTarget = null;
            int minTime = int.MaxValue;

            foreach (var opp in OpponentCombats)
            {
                if (self.GetDistanceTo2(opp) > Geom.Sqr(Game.StaffRange*3))
                    continue;

                var my = new AWizard(self);
                if (opp is AMinion)
                {
                    var his = new AMinion((AMinion) opp);
                    if (his.Type == MinionType.OrcWoodcutter)
                    {
                        int timer = 0;
                        while (my.GetDistanceTo2(his) > Geom.Sqr(Game.StaffRange + his.Radius))
                        {
                            his.Move();
                            if (!my.MoveTo(his, his, w => my.CheckIntersections(nearest) == null))
                                break;
                            timer++;
                        }
                        while (Math.Abs(my.GetAngleTo(his)) > Game.StaffSector / 2)
                        {
                            my.MoveTo(null, his);
                            timer++;
                        }

                        if (my.GetDistanceTo2(his) <= Geom.Sqr(Game.StaffRange + his.Radius) &&
                            Math.Abs(my.GetAngleTo(his)) <= Game.StaffSector / 2 &&
                            my.RemainingStaffCooldownTicks == 0 &&
                            my.RemainingActionCooldownTicks == 0
                            //&&his.RemainingActionCooldownTicks > 0
                            )
                        {
                            if (selTarget == null || timer < minTime)
                            {
                                selTarget = opp;
                                minTime = timer;
                            }
                        }
                    }
                }
                else if (opp is ABuilding)
                {
                    var his = new ABuilding((ABuilding) opp);
                   
                    int timer = 0;
                    while (my.GetDistanceTo2(his) > Geom.Sqr(Game.StaffRange + his.Radius))
                    {
                        if (!my.MoveTo(his, his, w => my.CheckIntersections(nearest) == null))
                            break;
                        timer++;
                    }
                    while (Math.Abs(my.GetAngleTo(his)) > Game.StaffSector / 2)
                    {
                        my.MoveTo(null, his);
                        timer++;
                    }

                    if (my.GetDistanceTo2(his) <= Geom.Sqr(Game.StaffRange + his.Radius) &&
                        Math.Abs(my.GetAngleTo(his)) <= Game.StaffSector / 2 &&
                        my.RemainingStaffCooldownTicks == 0 &&
                        my.RemainingActionCooldownTicks == 0 &&
                        (his.IsBesieded || timer == 0))
                    {
                        if (selTarget == null || timer < minTime)
                        {
                            selTarget = opp;
                            minTime = timer;
                        }
                    }
                }
            }
            if (selTarget != null)
            {
                bool angleOk = Math.Abs(self.GetAngleTo(selTarget)) <= Game.StaffSector / 2,
                    distOk = self.GetDistanceTo2(selTarget) <= Geom.Sqr(Game.StaffRange + selTarget.Radius);
                if (angleOk && distOk)
                {
                    FinalMove.Action = ActionType.Staff;
                }
                if (!distOk)
                {
                    FinalMove.MoveTo(selTarget, selTarget);
                }
                else if (!angleOk)
                {
                    FinalMove.MoveTo(null, selTarget);
                }
            }
            return selTarget;
        }

        Point FindCastTarget(AWizard self)
        {
            if (self.RemainingMagicMissileCooldownTicks > 0 || self.RemainingActionCooldownTicks > 0)
                return null;

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
                    self + Point.ByAngle(self.Angle + selCastAngle) * selMinDist,
                    self + Point.ByAngle(self.Angle + selCastAngle) * selMaxDist
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