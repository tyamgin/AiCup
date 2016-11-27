using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.Linq;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

/**
 * TODO:
 * - учитывать изменение маны
 * - когда MM без задержек - не рубит деревь€, т.к. отвлекаетс€ на стрельбу
 * - увеличить дальность атаки посохом, чтобы добивать фетишей и боротьс€ за бонусы
 * 
 * -если атакуем башню - не убегать за бонусом
 * ?-прикрыватьс€ деревь€ми (особенно от визардов)
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
            // занул€м чтобы случайно не использовать данные с предыдущего тика
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
                Visualizer.Visualizer.DrawSince = 5000;
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
            
            Const.Initialize();

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

                        GoToBonusDanger = BonusesObserver.Bonuses.Min(b => b.GetDistanceTo(my)) < my.VisionRange &&
                                          OpponentWizards.Count(w => my.GetDistanceTo(w) < my.VisionRange) <= 1
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
                // точка ќ , если с неЄ можно стрел€ть
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
                path = new WizardPath { my }; // из-за эпсилон, если стою близко у цели, то он как бы с ней пересекаетс€, но это не так
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
            const int magic = 45; // запас
            var bonus = BonusesObserver.Bonuses.ArgMin(b => b.GetDistanceTo(Self));
            var selMovingInfo = new MovingInfo(null, int.MaxValue, new FinalMove(new Move()));

            if (bonus.RemainingAppearanceTicks > MagicConst.GoToBonusMaxTicks + magic)
                return selMovingInfo;

            var my = new AWizard(Self);
            var nearestBuilding = OpponentBuildings.ArgMin(b => b.GetDistanceTo2(my));

            var path = DijkstraFindPath(new AWizard(Self), pos =>
            {
                // точка ќ , если бонус совсем близко
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
            
            
            var time = (int) (path.GetLength()/my.MaxForwardSpeed);
                
            if (time < MagicConst.GoToBonusMaxTicks)
            {
                selMovingInfo.Time = time;

                var nextPoint = path[1];
                var nextNextPoint = path.Count > 2 ? path[2] : nextPoint;
                selMovingInfo.Move = new FinalMove(new Move());
                selMovingInfo.Move.MoveTo(nextPoint, my.GetDistanceTo(nextNextPoint) < my.Radius + 20 ? nextNextPoint : nextPoint);
                selMovingInfo.Target = nextPoint;

                var nextTree = path.GetNearestTree();
				CutTreesInPath(nextTree, selMovingInfo.Move);
            }
           
            if (selMovingInfo.Time <= bonus.RemainingAppearanceTicks - magic)
                selMovingInfo.Target = null;
#if DEBUG
            if (selMovingInfo.Target != null)
                Visualizer.Visualizer.SegmentsDrawQueue.Add(new object[] { path, Pens.Red, 3 });
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
                    my.CastRange += wizardDangerRange; // чтобы держатьс€ на рассто€нии от визардов
                if (bld != null)
                {
                    var opps = bld.OpponentsCount - (bld.GetDistanceTo(Self) <= bld.CastRange ? 1 : 0);
                    var isLast = BuildingsObserver.OpponentBase.GetDistanceTo(bld) < Const.MapSize/2;
                    if (bld.IsBase || opps < 1 || isLast && bld.Lane != currentLane)
                    {
                        // чтобы не подходить близко к одиноким башн€м
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
    }
}