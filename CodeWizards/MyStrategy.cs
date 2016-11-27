using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.Linq;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

/**
 * TODO:
 * - когда MM без задержек - не рубит деревья, т.к. отвлекается на стрельбу
 * - увеличить дальность атаки посохом, чтобы добивать фетишей и бороться за бонусы
 * 
 * -если атакуем башню - не убегать за бонусом
 * ?-прикрываться деревьями (особенно от визардов)
 * !!-сбегать когда мало хп
 */

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public static World World;
        public static Game Game;
        public static Wizard Self;
        public static FinalMove FinalMove;
        public static int PrevTickIndex;

        public static long[] FriendsIds;

        public static AWizard[] Wizards, OpponentWizards;
        public static AMinion[] Minions, OpponentMinions, NeutralMinions;
        public static ABuilding[] OpponentBuildings;
        public static ACombatUnit[] Combats, OpponentCombats, MyCombats;

        public static AProjectile[][] ProjectilesPaths;

        public void Move(Wizard self, World world, Game game, Move move)
        {
            // занулям чтобы случайно не использовать данные с предыдущего тика
            Wizards = null;
            OpponentWizards = null;
            Minions = null;
            OpponentMinions = null;
            NeutralMinions = null;
            OpponentBuildings = null;
            Combats = null;
            OpponentCombats = null;
            MyCombats = null;
            NextBonusWaypoint = null;

#if DEBUG
            while (Visualizer.Visualizer.Pause)
            {
                // pause here
            }
#endif

            TimerStart();
            _move(self, world, game, move);
            PrevTickIndex = World.TickIndex;
            TimerEndLog("All", 0);
            //if (world.TickIndex % 1000 == 999 || world.TickIndex == 3525)
            //    _recheckNeighbours();
#if DEBUG
            if (world.TickIndex == 0)
                Visualizer.Visualizer.DrawSince = 2000;
            Visualizer.Visualizer.CreateForm();
            if (world.TickIndex >= Visualizer.Visualizer.DrawSince)
                Visualizer.Visualizer.DangerPoints = CalculateDangerMap();
            else
                Visualizer.Visualizer.DangerPoints = null;
            Visualizer.Visualizer.LookUp(new Point(self));
            Visualizer.Visualizer.Draw();
            if (world.TickIndex >= Visualizer.Visualizer.DrawSince)
            {
                var timer = new Stopwatch();
                timer.Start();
                while (!Visualizer.Visualizer.Done || timer.ElapsedMilliseconds < 13)
                {
                }
                timer.Stop();
            }
#endif
        }

        private void _move(Wizard self, World world, Game game, Move move)
        {
            World = world;
            Game = game;
            Self = self;
            FinalMove = new FinalMove(move);
            
            Const.MapSize = Game.MapSize;
            Const.WizardRadius = Game.WizardRadius;

            var levelUpXpValues = Game.LevelUpXpValues;
            AWizard.Xps = new int[Game.LevelUpXpValues.Length + 1];
            for (var i = 1; i <= levelUpXpValues.Length; i++)
                AWizard.Xps[i] = AWizard.Xps[i - 1] + levelUpXpValues[i - 1];

            Wizards = world.Wizards
                .Select(x => new AWizard(x))
                .ToArray();

            OpponentWizards = Wizards
                .Where(x => x.IsOpponent)
                .ToArray();

            Minions = world.Minions
                .Select(x => x.Type == MinionType.OrcWoodcutter ? (AMinion) new AOrc(x) : new AFetish(x))
                .ToArray();

            NeutralMinions = Minions
                .Where(x => x.Faction == Faction.Neutral)
                .ToArray();

            Combats =
                Minions.Cast<ACombatUnit>()
                .Concat(Wizards)
                .Concat(BuildingsObserver.Buildings)//TODO перед BuildingsObserver.Update????
                .ToArray();

            MyCombats = Combats
                .Where(x => x.IsTeammate)
                .ToArray();

            FriendsIds = Combats
                .Where(x => x.IsTeammate)
                .Select(x => x.Id)
                .ToArray();

            NeutralMinionsObserver.Update();
            OpponentMinions = Minions
                .Where(x => x.IsOpponent)
                .ToArray();

            OpponentCombats = Combats
                .Where(x => x.IsOpponent)
                .ToArray();

            RoadsHelper.Initialize();
            BuildingsObserver.Update();

            OpponentBuildings = BuildingsObserver.Buildings
                .Where(x => x.IsOpponent)
                .ToArray();

            TreesObserver.Update();
            ProjectilesObserver.Update();
            BonusesObserver.Update();

            InitializeProjectiles();
            InitializeDijkstra();

            var my = new AWizard(Self);

            foreach (var building in OpponentBuildings)
            {
                building.OpponentsCount = MyCombats.Count(x => x.GetDistanceTo(building) <= building.VisionRange);
                var hisWizards = OpponentWizards.Count(x => x.GetDistanceTo(building) <= building.VisionRange);
                if (building.IsBase && building.OpponentsCount >= 7 || !building.IsBase && building.OpponentsCount >= 5)
                    building.IsBesieded = true;
            }

            if (Self.IsMaster && World.TickIndex == 0)
            {
                MasterSendMessages();
                return;
            }

            var bonusMoving = GoToBonus();
            var target = FindTarget(new AWizard(self), bonusMoving.Target);
            if (target == null && bonusMoving.Target == null)
            {
                var nearest = OpponentCombats
                    .Where(
                        x =>
                            Utility.IsBase(x) || RoadsHelper.GetLane(x) == MessagesObserver.GetLane() ||
                            RoadsHelper.GetLaneEx(my) == ALaneType.Middle && 
                            RoadsHelper.GetLaneEx(x) == ALaneType.Middle && CanRush(my, x))
                    .Where(x => x.IsAssailable)
                    .OrderBy(
                        x =>
                            x.GetDistanceTo(self) +
                            (x is AWizard ? -40 : (x is ABuilding && !((ABuilding) x).IsBesieded) ? 1500 : 0))
                    .ToArray();
                if (nearest.Length > 0 && nearest.FirstOrDefault(GoAround) == null)
                {
                    GoDirect(nearest[0], FinalMove);
                }
            }

            TimerStart();
            if (!TryDodgeProjectile())
            {
                if (target == null || 
                    (FinalMove.Action == ActionType.Staff ||
                    FinalMove.Action == ActionType.MagicMissile || 
                    FinalMove.Action == ActionType.Fireball ||
                    FinalMove.Action == ActionType.FrostBolt) && target.Type == TargetType.Opponent)
                {
                    if (bonusMoving.Target != null)
                    {
                        NextBonusWaypoint = bonusMoving.Target;
                        FinalMove.Turn = bonusMoving.Move.Turn;
                        if (bonusMoving.Move.Action != ActionType.None && bonusMoving.Move.Action != null)
                        {
                            FinalMove.Action = bonusMoving.Move.Action;
                            FinalMove.MinCastDistance = bonusMoving.Move.MinCastDistance;
                            FinalMove.MaxCastDistance = bonusMoving.Move.MaxCastDistance;
                            FinalMove.CastAngle = bonusMoving.Move.CastAngle;
                        }

                        var nearestToBonus = my;
                        var opp = OpponentWizards.FirstOrDefault(w => my.GetDistanceTo(w) < my.VisionRange);
                        var bon = BonusesObserver.Bonuses.ArgMin(b => b.GetDistanceTo(my));
                        if (opp != null && opp.GetDistanceTo(bon) < my.GetDistanceTo(bon))
                            nearestToBonus = opp;

                        GoToBonusDanger = bon.GetDistanceTo(nearestToBonus) < my.VisionRange &&
                                          OpponentWizards.Count(w => my.GetDistanceTo(w) < my.VisionRange) <= 1 &&
                                          nearestToBonus.Life <= my.Life
                            ? 41
                            : 7;

                        NextBonusWaypoint = my + (NextBonusWaypoint - my).Normalized() * (Self.Radius + 30);
                        TryGoByGradient(EstimateDanger, null, FinalMove);
                    }
                    else
                    {
                        TryGoByGradient(EstimateDanger, HasAnyTarget, FinalMove);
                    }
                }
            }
            TimerEndLog("Go", 1);

            if (my.CanLearnSkill)
            {
                move.SkillToLearn = MessagesObserver.GetSkill();
            }
        }

        public static Point NextBonusWaypoint;


        void GoDirect(Point target, FinalMove move)
        {
            move.MoveTo(null, target);
            var canGo = TryGoByGradient(w => w.GetDistanceTo2(target), null, move);
            TryCutTrees(!canGo, move);
        }

        bool TryCutTrees(bool cutNearest, FinalMove move)
        {
            var self = new AWizard(Self);
            var nearestTrees = TreesObserver.Trees.Where(
                t => self.GetDistanceTo(t) < self.CastRange + t.Radius + Game.MagicMissileRadius
                ).ToArray();

            if (nearestTrees.Length == 0)
                return false;

            if (self.RemainingActionCooldownTicks == 0)
            {
                if (self.GetStaffAttacked(nearestTrees).Length > 0)
                {
                    move.Action = ActionType.Staff;
                    return true;
                }
                if (self.RemainingMagicMissileCooldownTicks == 0)
                {
                    var proj = new AProjectile(self, 0, ProjectileType.MagicMissile);
                    var path = EmulateMagicMissile(proj);
                    if (path.Count == 0 || path[path.Count - 1].EndDistance < self.CastRange - Const.Eps)
                    {
                        move.MinCastDistance = path[path.Count - 1].EndDistance;
                        move.Action = ActionType.MagicMissile;
                        return true;
                    }
                }
            }
            if (cutNearest)
            {
                var nearest = nearestTrees.OrderBy(t => self.GetDistanceTo2(t)).FirstOrDefault();
                move.MoveTo(null, nearest);
            }

            return false;
        }

        DijkstraPointCostFunc MoveCostFunc(IEnumerable<ACombatUnit> buildings, ALaneType lane)
        {
            return pos =>
            {
                foreach (var building in buildings)
                    if (building != null && building.GetDistanceTo2(pos) <= building.CastRange*building.CastRange)
                        if (RoadsHelper.Roads.All(seg => seg.GetDistanceTo(pos) > 200))
                            return Const.Infinity;

                if (RoadsHelper.GetAllowedForLine(lane).All(r => r.GetDistanceTo(pos) > (World.TickIndex < 900 && World.TickIndex > 200 ? 300 : 700)))
                    return 1000;

                return 0;
            };
        }

        bool GoAround(ACombatUnit to)
        {
            TimerStart();
            var ret = _goAround(to);
            TimerEndLog("Dijkstra", 1);
            return ret;
        }

        bool _goAround(ACombatUnit target)
        {
            var my = new AWizard(Self);
            var selLane = Utility.IsBase(target) ? MessagesObserver.GetLane() : RoadsHelper.GetLane(target);
            var nearestBuilding = OpponentBuildings.ArgMin(b => b.GetDistanceTo2(my));
            var buildings = nearestBuilding.Id == target.Id ? new[] { nearestBuilding } : new[] { nearestBuilding, target};

            var path = DijkstraFindPath(new AWizard(Self), pos =>
            {
                // точка ОК, если с неё можно стрелять
                if (pos.GetDistanceTo2(target) < Geom.Sqr(Self.CastRange))
                {
                    if (TreesObserver.Trees
                        .Where(x => x.GetDistanceTo2(pos) < Geom.Sqr(Self.CastRange))
                        .All(x => !Geom.SegmentCircleIntersects(pos, target, x, x.Radius + Game.MagicMissileRadius))
                        )
                    {
                        return DijkstraStopStatus.TakeAndStop;
                    }
                }
                return DijkstraStopStatus.Continue;
            }, MoveCostFunc(buildings, selLane)).FirstOrDefault();

            if (path == null && my.GetDistanceTo(target) - my.Radius - target.Radius <= 1)
                path = new WizardPath { my }; // из-за эпсилон, если стою близко у цели, то он как бы с ней пересекается, но это не так
            if (path == null || path.Count == 0)
                return false;

            if (path.Count == 1)
            {
                FinalMove.Turn = my.GetAngleTo(target);
                return true;
            }

            var obstacles =
                Combats.Where(x => x.Id != Self.Id).Cast<ACircularUnit>()
                .Where(x => my.GetDistanceTo2(x) < Geom.Sqr(my.VisionRange)) //???
                .ToArray();

            path.Simplify(obstacles, MagicConst.SimplifyMaxLength);

            var nextPoint = path[1];
            var nextNextPoint = path.Count > 2 ? path[2] : target;
            FinalMove.MoveTo(nextPoint, my.GetDistanceTo(nextNextPoint) < Self.VisionRange * 1.2 ? nextNextPoint : nextPoint);

            var nextTree = path.GetNearestTree();
            CutTreesInPath(nextTree, FinalMove);
#if DEBUG
            Visualizer.Visualizer.SegmentsDrawQueue.Add(new object[] { path, Pens.Blue, 3 });
#endif
            return true;
        }
		
		void CutTreesInPath(ATree nextTree, FinalMove move) 
		{
			if (nextTree == null)
				return;
			var my = new AWizard(Self);
			
			var angleTo = my.GetAngleTo(nextTree);
			if (my.GetDistanceTo(nextTree) < my.VisionRange && Math.Abs(angleTo) > Game.StaffSector / 2)
				move.MoveTo(null, nextTree);

			if (my.RemainingActionCooldownTicks == 0 && Math.Abs(angleTo) <= Game.StaffSector / 2)
			{
				if (my.GetDistanceTo(nextTree) <= Game.StaffRange + nextTree.Radius && my.RemainingStaffCooldownTicks == 0)
					move.Action = ActionType.Staff;
				else if (my.GetDistanceTo(nextTree) <= my.CastRange + nextTree.Radius && my.RemainingMagicMissileCooldownTicks == 0)
				{
					move.Action = ActionType.MagicMissile;
					move.CastAngle = angleTo;
				    move.MinCastDistance = Math.Min(my.CastRange - 1, my.GetDistanceTo(nextTree));
				}
			}
		}
		
        MovingInfo GoToBonus()
        {
            TimerStart();
            var ret = _goToBonus();
            TimerEndLog("GoToBonus", 1);
            return ret;
        }

        MovingInfo _goToBonus()
        {
            const int magic = 45;
            var bonus = BonusesObserver.Bonuses.ArgMin(b => b.GetDistanceTo(Self));
            var selMovingInfo = new MovingInfo(null, int.MaxValue, new FinalMove(new Move()));

            if (bonus.RemainingAppearanceTicks > MagicConst.GoToBonusmaxTicks + magic)
                return selMovingInfo;

            var my = new AWizard(Self);
            var nearestBuilding = OpponentBuildings.ArgMin(b => b.GetDistanceTo2(my));

            var path = DijkstraFindPath(new AWizard(Self), pos =>
            {
                // точка ОК, если бонус совсем близко
                if (pos.GetDistanceTo2(bonus) < Geom.Sqr(bonus.Radius + Self.Radius + 35))
                    return DijkstraStopStatus.TakeAndStop;
                
                return DijkstraStopStatus.Continue;
            }, MoveCostFunc(new [] { nearestBuilding }, MessagesObserver.GetLane())).FirstOrDefault();

            if (path == null)
            {
                GoDirect(bonus, selMovingInfo.Move);
                selMovingInfo.Target = bonus;
                return selMovingInfo;
            }

            var obstacles =
                Combats.Where(x => x.Id != Self.Id).Cast<ACircularUnit>()
                    .Where(x => my.GetDistanceTo2(x) < Geom.Sqr(my.VisionRange))
                    .ToArray();

            path.Add(bonus);
            path.Simplify(obstacles, MagicConst.SimplifyMaxLength);
            
            var selPath = new List<Point>();
            ABonus selBonus = null;
            
            {
                var time = (int) (path.GetLength()/my.MaxForwardSpeed);
                
                if (time < selMovingInfo.Time && time < MagicConst.GoToBonusmaxTicks)
                {
                    selMovingInfo.Time = time;
                    selPath = path;
                    selBonus = bonus;

                    var nextPoint = path[1];
                    var nextNextPoint = path.Count > 2 ? path[2] : nextPoint;
                    selMovingInfo.Move = new FinalMove(new Move());
                    selMovingInfo.Move.MoveTo(nextPoint, my.GetDistanceTo(nextNextPoint) < my.Radius + 20 ? nextNextPoint : nextPoint);
                    selMovingInfo.Target = nextPoint;

                    var nextTree = path.GetNearestTree();
					CutTreesInPath(nextTree, selMovingInfo.Move);
                }
            }
            if (selBonus != null && selMovingInfo.Time <= selBonus.RemainingAppearanceTicks - magic/*запас*/)
                selMovingInfo.Target = null;
#if DEBUG
            if (selMovingInfo.Target != null)
                Visualizer.Visualizer.SegmentsDrawQueue.Add(new object[] { selPath, Pens.Red, 3 });
#endif
            return selMovingInfo;
        }

        private bool HasAnyTarget(AWizard self)
        {
            double wizardDangerRange = 40;
            var currentLane = RoadsHelper.GetLane(self);

            var my = new AWizard(self);
            foreach (var opp in OpponentCombats)
            {
                if (!opp.IsAssailable)
                    continue;

                var prevCastRange = my.CastRange;

                var bld = opp as ABuilding;
                if (opp is AWizard)
                    my.CastRange += wizardDangerRange; // чтобы держаться на расстоянии от визардов
                if (bld != null)
                {
                    var opps = bld.OpponentsCount - (bld.GetDistanceTo(Self) <= bld.CastRange ? 1 : 0);
                    var isLast = BuildingsObserver.OpponentBase.GetDistanceTo(bld) < Const.MapSize/2;
                    if (bld.IsBase || opps < 1 || isLast && bld.Lane != currentLane)
                    {
                        // чтобы не подходить близко к одиноким башням
                        if (my.GetDistanceTo(bld) < bld.CastRange + 6)
                            return true;
                    }
                }

                if (my.EthalonCanCastMagicMissile(opp, false))
                    return true;

                my.CastRange = prevCastRange;
            }
            return false;
        }

        enum TargetType
        {
            Opponent,
            Bonus,
        }

        class Target
        {
            public Point MoveTo;
            public TargetType Type;
        }

        Target FindTarget(AWizard self, Point moveTo = null)
        {
            TimerStart();
            var ret = _findTarget(self, moveTo);
            TimerEndLog("FindTarget", 1);
            return ret;
        }

        Target _findTarget(AWizard self, Point moveTo)
        {
            var t0 = FindBonusTarget(self);
            var t1 = FindCastTarget(self);
            var t2 = FindStaffTarget(self);
            var t3 = FindCastTarget2(self, t0.Target ?? moveTo);

            Point ret = null;
            if (t0.Target != null)
            {
                FinalMove.Apply(t0.Move);
                ret = t0.Target;
            }

            if (t1.Target != null && t1.Time <= Math.Min(t2.Time, t3.Time))
            {
                FinalMove.Action = t1.Move.Action;
                FinalMove.MinCastDistance = t1.Move.MinCastDistance;
                FinalMove.MaxCastDistance = t1.Move.MaxCastDistance;
                FinalMove.CastAngle = t1.Move.CastAngle;
                return new Target {MoveTo = t1.Target, Type = ret == null ? TargetType.Opponent : TargetType.Bonus};
            }
            if (t0.Target == null && t2.Target != null && t2.Time <= Math.Min(t1.Time, t3.Time))
            {
                FinalMove.Apply(t2.Move);
                return new Target { MoveTo = t2.Target, Type = TargetType.Opponent };
            }
            if (t3.Target != null && t3.Time <= Math.Min(t1.Time, t2.Time))
            {
                FinalMove.Apply(t3.Move);
                return new Target { MoveTo = t3.Target, Type = TargetType.Opponent };
            }

            if (ret == null)
                return null;

            return new Target { MoveTo = ret, Type = TargetType.Bonus };
        }

        class MovingInfo
        {
            public Point Target;
            public int Time;
            public FinalMove Move;

            public MovingInfo(Point target, int time, FinalMove move)
            {
                Target = target;
                Time = time;
                Move = move;
            }
        }

        MovingInfo FindBonusTarget(AWizard self)
        {
            const int grid = 24;
            var minTime = int.MaxValue;
            var selGo = 0;
            Point selMoveTo = null;
            foreach (var _bonus in BonusesObserver.Bonuses)
            {
                if (_bonus.GetDistanceTo(self) - self.Radius - _bonus.Radius > Game.StaffRange*3)
                    continue;
                if (_bonus.RemainingAppearanceTicks > 60)
                    continue;

                var nearest = Combats
                    .Where(x => x.Id != self.Id && self.GetDistanceTo2(x) < Geom.Sqr(self.VisionRange))
                    .ToArray();


                for (var i = 0; i < grid; i++)
                {
                    var bonus = new ABonus(_bonus);
                    var angle = Math.PI*2/grid*i + self.Angle;
                    var my = new AWizard(self);
                    var moveTo = my + Point.ByAngle(angle) * self.VisionRange;
                    int time = 0;
                    int go = 0;
                    while (my.GetDistanceTo(bonus) > my.Radius + bonus.Radius && time < 60)
                    {
                        if (!my.MoveTo(moveTo, null, w => !CheckIntersectionsAndTress(w, nearest)))
                        {
                            break;
                        }
                        var wait = !bonus.Exists;
                        bonus.SkipTick();
                        time++;
                        if (my.GetDistanceTo(bonus) <= my.Radius + bonus.Radius)
                        {
                            while (!bonus.Exists)
                            {
                                bonus.SkipTick();
                                time++;
                            }
                            if (wait)
                                time++;

                            if (time < minTime)
                            {
                                minTime = time;
                                selMoveTo = moveTo;
                                selGo = go;
                            }
                            break;
                        }
                        go++;
                    }

                }
            }
            var moving = new MovingInfo(selMoveTo, minTime, new FinalMove(new Move()));
            if (selMoveTo != null)
            {
                if (minTime == 1 || selGo > 0)
                    moving.Move.MoveTo(selMoveTo, null);
                else
                    moving.Target = self;
            }
            return moving;
        }

        bool CheckIntersectionsAndTress(AWizard self, IEnumerable<ACircularUnit> units)
        {
            if (self.CheckIntersections(units) != null)
                return true;
            var nearestTree = TreesObserver.GetNearestTree(self);
            return nearestTree != null && self.IntersectsWith(nearestTree);
        }

        MovingInfo FindStaffTarget(AWizard self)
        {
            var nearest = Combats
                .Where(x => x.Id != self.Id && self.GetDistanceTo2(x) < Geom.Sqr(Game.StaffRange*6))
                .ToArray();
            int minTicks = int.MaxValue;
            var move = new FinalMove(new Move());

            var attacked = self.GetStaffAttacked(nearest).Cast<ACombatUnit>().ToArray();

            ACircularUnit selTarget = attacked.FirstOrDefault(x => x.IsOpponent);
            if (selTarget != null) // если уже можно бить
            {
                move.Action = ActionType.Staff;
                return new MovingInfo(selTarget, 0, move);
            }

            if (self.MmSkillLevel == 5)
            {
                // т.к. стрелять можно без задержки
                // возможно, нужно сделать исключение, если прокачан посох
                return new MovingInfo(null, int.MaxValue, move);
            }

            Point selMoveTo = null;

            foreach (var opp in OpponentCombats)
            {
                var dist = self.GetDistanceTo(opp);
                if (dist > Game.StaffRange*3 || !opp.IsAssailable)
                    continue;

                var range = opp.Radius + Game.StaffRange;
                foreach (var delta in new [] { -range, -range / 2, 0, range / 2, range })
                {
                    var angle = Math.Atan2(delta, dist);
                    var moveTo = self + (opp - self).Normalized().RotateClockwise(angle)*self.VisionRange;

                    var nearstCombats = OpponentCombats
                        .Where(x => x.GetDistanceTo(self) <= x.VisionRange)
                        .Select(Utility.CloneCombat)
                        .ToArray();

                    var canHitNow = opp.EthalonCanHit(self);

                    var ticks = 0;
                    var my = new AWizard(self);
                    var his = Utility.CloneCombat(opp);
                    var ok = true;

                    while (my.GetDistanceTo2(his) > Geom.Sqr(Game.StaffRange + his.Radius))
                    {
                        his.EthalonMove(my);
                        if (!my.MoveTo(moveTo, his, w => !CheckIntersectionsAndTress(w, nearest)))
                        {
                            ok = false;
                            break;
                        }
                        foreach (var x in nearstCombats)
                            x.EthalonMove(my);
                        ticks++;
                    }

                    if (ok && !(opp is AOrc))
                    {
                        while (Math.Abs(my.GetAngleTo(his)) > Game.StaffSector/2)
                        {
                            my.MoveTo(null, his);
                            foreach (var x in nearstCombats)
                                x.EthalonMove(my);
                            his.EthalonMove(my);
                            ticks++;
                        }
                    }

                    if (ok && ticks < minTicks)
                    {
                        if (my.CanStaffAttack(his))
                        {
                            if (nearstCombats.All(x => canHitNow && x.Id == opp.Id || !x.EthalonCanHit(my)))
                            {
                                // успею-ли я вернуться обратно
                                while (my.GetDistanceTo(self) > my.MaxForwardSpeed)//TODO:HACK
                                {
                                    my.MoveTo(self, null);
                                    foreach (var x in nearstCombats)
                                        x.SkipTick();
                                }
                                if (nearstCombats.All(x => canHitNow && x.Id == opp.Id || !x.EthalonCanHit(my)))
                                {
                                    selTarget = opp;
                                    selMoveTo = moveTo;
                                    minTicks = ticks;
                                }
                            }
                        }
                    }
                }
            }
            if (selTarget != null)
            {
                bool angleOk = Math.Abs(self.GetAngleTo(selTarget)) <= Game.StaffSector/2,
                    distOk = self.GetDistanceTo2(selTarget) <= Geom.Sqr(Game.StaffRange + selTarget.Radius);
                
                if (!distOk)
                {
                    move.MoveTo(selMoveTo, selTarget);
                }
                else if (!angleOk)
                {
                    move.MoveTo(null, selTarget);
                }
            }
            return new MovingInfo(selTarget, minTicks, move);
        }

        static double GetCombatPriority(AWizard self, ACombatUnit unit)
        {
            // чем меньше - тем важнее стрелять в него первого
            var res = unit.Life;
            if (unit is AWizard)
                res /= 4;
            var dist = self.GetDistanceTo(unit);
            if (dist <= Game.StaffRange + unit.Radius + 10)
            {
                res -= 60;
                res += Math.Log(dist);
            }
            return res;
        }

        MovingInfo FindCastTarget(AWizard self)
        {
            var move = new FinalMove(new Move());
            if (self.RemainingMagicMissileCooldownTicks > 0 || self.RemainingActionCooldownTicks > 0)
                return new MovingInfo(null, int.MaxValue, move);

            var angles = new List<double>();
            foreach (var x in OpponentCombats)
            {
                var dist = self.GetDistanceTo(x);

                if (dist > self.CastRange + x.Radius + Game.MagicMissileRadius + 3) // TODO: возможно ограничить перебор, если угол слишком большой
                    continue;

                const int grid = 20;
                double left = -Game.StaffSector/2, right = -left;
                for (var i = 0; i <= grid; i++)
                {
                    var castAngle = (right - left)/grid*i + left;
                    angles.Add(castAngle);
                }
            }

            ACombatUnit selTarget = null;
            double
                selMinDist = 0,
                selMaxDist = self.CastRange + 20,
                selAngleTo = 0,
                selCastAngle = 0,
                selPriority = int.MaxValue;

            foreach (var angle in angles)
            {
                var proj = new AProjectile(new AWizard(self), angle, ProjectileType.MagicMissile);
                var path = EmulateMagicMissile(proj);
                for (var i = 0; i < path.Count; i++)
                {
                    if (path[i].State == AProjectile.ProjectilePathState.Fire)
                    {
                        var combat = path[i].Target;
                        if (!combat.IsAssailable)
                            continue;

                        var myAngle = self.Angle + angle;
                        var hisAngle = self.Angle + self.GetAngleTo(combat);
                        var angleTo = Geom.GetAngleBetween(myAngle, hisAngle);
                        
                        var priority = GetCombatPriority(self, combat);
                        if (combat.IsOpponent && (priority < selPriority || Utility.Equals(priority, selPriority) && angleTo < selAngleTo))
                        {
                            selTarget = combat;
                            selCastAngle = angle;
                            selAngleTo = angleTo;
                            selMinDist = i == 0 || path[i - 1].State == AProjectile.ProjectilePathState.Free && path[i - 1].Length < 40 
                                ? path[i].StartDistance - 1 
                                : path[i].StartDistance - 20;
                            selMaxDist = i >= path.Count - 2 ? (self.CastRange + 500) : (path[i + 1].EndDistance + path[i].EndDistance) / 2;
                            selPriority = priority;
                        }
                    }
                }
            }
            if (selTarget == null)
                return new MovingInfo(null, int.MaxValue, move);

            move.Action = ActionType.MagicMissile;
            move.MinCastDistance = selMinDist;
            move.MaxCastDistance = selMaxDist;
            move.CastAngle = selCastAngle;
#if DEBUG
            _lastProjectileTick = World.TickIndex;
            _lastProjectilePoints = new []
            {
                self + Point.ByAngle(self.Angle + selCastAngle) * selMinDist,
                self + Point.ByAngle(self.Angle + selCastAngle) * Math.Min(Self.CastRange, selMaxDist),
            };
#endif
            return new MovingInfo(selTarget, 0, move);
        }

        public static int _lastProjectileTick;
        public static Point[] _lastProjectilePoints;

        bool CanRush(AWizard self, ACombatUnit opp)
        {
            var wizard = opp as AWizard;
            var minion = opp as AMinion;

            if (wizard != null)
            {
                if (wizard.Life <= self.MagicMissileDamage)
                    return true;
                if (self.Life <= wizard.MagicMissileDamage)
                    return false;

                if (self.Life >= wizard.Life + 3*self.MagicMissileDamage)
                    return true;
            }
            else if (minion != null)
            {
                if (minion.Life <= self.MagicMissileDamage)
                    return true;
            }
            return false;
        }

        MovingInfo FindCastTarget2(AWizard self, Point moveTo = null)
        {
            var move = new FinalMove(new Move());
            var nearest = Combats
                .Where(x => x.Id != self.Id && self.GetDistanceTo2(x) < Geom.Sqr(self.VisionRange * 1.3))
                .ToArray();

            var targetsSelector = new TargetsSelector(nearest) { EnableMinionsCache = true };

            ACircularUnit selTarget = null;
            var minTicks = int.MaxValue;
            double minPriority = int.MaxValue;

            foreach (var opp in OpponentCombats)
            {
                if (self.GetDistanceTo2(opp) > Geom.Sqr(self.VisionRange) || !opp.IsAssailable)
                    continue;

                var nearstCombats = nearest
                    .Where(x => x.IsOpponent)
                    .Select(Utility.CloneCombat)
                    .ToArray();

                var canHitNow = opp.EthalonCanHit(self);

                var ticks = 0;
                var my = new AWizard(self);
                var his = Utility.CloneCombat(opp);
                var ok = true;

                while (!my.EthalonCanCastMagicMissile(his, checkCooldown: false))
                {
                    // только поворачиваться, если и так близко
                    if (!my.MoveTo(
                        moveTo ?? (my.GetDistanceTo2(his) < Geom.Sqr(my.CastRange) ? null : his),
                        his,
                        w => !CheckIntersectionsAndTress(w, nearest))

                        || ticks > 40 // снаряду может помешать дерево
                        )
                    {
                        ok = false;
                        break;
                    }
                    foreach (var x in nearstCombats)
                        x.EthalonMove(my);
                    ticks++;
                }

                if (his is AWizard && CanRush(my, his))
                    ticks -= 10;// чтобы дать больше приоритета визарду

                var priority = GetCombatPriority(self, his);
                if (ok && (ticks < minTicks || ticks == minTicks && priority < minPriority))
                {
                    if (my.EthalonCanCastMagicMissile(his))
                    {
                        if (nearstCombats.All(x =>
                        {
                            //TODO: возможно, скопировать эти условия и для staff
                            if (canHitNow && x.Id == opp.Id) // он и так доставал
                                return true;

                            if (!x.EthalonCanHit(my))
                                return true;

                            if (his.Id == x.Id && CanRush(my, x))
                                return true;

                            var target = targetsSelector.Select(x);
                            if (target != null && target.Id != my.Id)
                                return true;

                            return false;
                        })
                            )
                        {
                            minTicks = ticks;
                            minPriority = priority;
                            selTarget = opp;
                        }
                    }
                } 
            }

            if (selTarget == null)
                return new MovingInfo(null, int.MaxValue, move);

            minTicks = Math.Max(0, minTicks);
            move.MoveTo(moveTo ?? (self.GetDistanceTo2(selTarget) < Geom.Sqr(self.CastRange) ? null : selTarget), selTarget);
            return new MovingInfo(selTarget, minTicks, move);
        }

        List<AProjectile.ProjectilePathSegment> EmulateMagicMissile(AProjectile projectile)
        {
            return projectile.Emulate(Combats);
        }
    }
}