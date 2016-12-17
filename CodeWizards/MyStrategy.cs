using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.Linq;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

/**
 * TODO:
 * - !!свои углы в ПП!!
 * - уворот от своих фаерболов
 * - Не правильно убегает
 * - отбегать назад
 * 
 * - если перевес сил на бонусе, то бросать его
 * - Улучшить cancastmagicmissile
 * - когда мало жизней от фаербольшика держаться подальше
 * - не идти за нейтралами
 * - учитывать что бонусов скорее всего нет (или кто-то рядом ходит со статусом)
 * - когда MM без задержек - не рубит деревья, т.к. отвлекается на стрельбу
 * 
 * - тормозит CanCastMM 
 */

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public static World World;
        public static Game Game;
        public static Wizard Self;
        public static AWizard ASelf;
        public static FinalMove FinalMove;
        public static int PrevTickIndex;

        public static AWizard[] Wizards, OpponentWizards, MyWizards;
        public static AMinion[] Minions, OpponentMinions, NeutralMinions;
        public static ABuilding[] OpponentBuildings;
        public static ACombatUnit[] Combats, OpponentCombats, MyCombats;

        public static AWizard[] MyWizardsPrevState = new AWizard[0];

        public static AProjectile[][] ProjectilesPaths1;

        public void Move(Wizard self, World world, Game game, Move move)
        {
            // занулям чтобы случайно не использовать данные с предыдущего тика
            Wizards = null;
            OpponentWizards = null;
            MyWizards = null;
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

            if (move.Action == ActionType.Fireball)
            {
                move.Turn = ProjectilesObserver.EncodeFbCastDist(move.Turn, move.MinCastDistance);
            }
            MyWizardsPrevState = MyWizards;

            TimerEndLog("All", 0);
#if DEBUG
            if (world.TickIndex == 0)
                Visualizer.Visualizer.DrawSince = 4740;
            Visualizer.Visualizer.CreateForm();
            if (world.TickIndex >= Visualizer.Visualizer.DrawSince)
                Visualizer.Visualizer.DangerPoints = CalculateDangerMap();
            else
                Visualizer.Visualizer.DangerPoints = null;
            if (World.TickIndex == 0)
                Visualizer.Visualizer.LookAt(new Point(self));
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
            MagicConst.TreeObstacleWeight = Const.IsFinal ? 25 : 35;

            Wizards = world.Wizards
                .Select(x => new AWizard(x))
                .ToArray();

            foreach (var wizard in Wizards)
            {
                foreach (var other in Wizards)
                {
                    if (wizard.Faction != other.Faction)
                        continue;
                    if (wizard.GetDistanceTo2(other) > Geom.Sqr(Game.AuraSkillRange))
                        continue;
                    
                    for (var i = 0; i < 5; i++)
                        wizard.AurasFactorsArr[i] = Math.Max(wizard.AurasFactorsArr[i], other.SkillsLearnedArr[i] / 2);
                }
                var orig = World.Wizards.FirstOrDefault(w => w.Id == wizard.Id);
                var player = World.Players.FirstOrDefault(p => orig != null && p.Id == orig.OwnerPlayerId);
                if (player != null && player.IsStrategyCrashed)
                    wizard.RemainingFrozen = 100500;
            }

            OpponentWizards = Wizards
                .Where(x => x.IsOpponent)
                .ToArray();

            MyWizards = Wizards
                .Where(x => x.IsTeammate)
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
            MessagesObserver.Update();

            InitializeProjectiles();
            InitializeDijkstra();

            ASelf = Wizards.FirstOrDefault(x => x.Id == Self.Id);
            if (ASelf == null)
                throw new Exception("Self not found in wizards list");

            InitializeDangerEstimation();
            SupportObserver.Update();

            if (Self.IsMaster && World.TickIndex == 0)
            {
                MasterSendMessages();
            }
            if (Self.IsMaster)
            {
                MasterCheckRearrange();
            }

            var nearestBonus = BonusesObserver.Bonuses.ArgMin(b => b.GetDistanceTo(ASelf));
            var opponentsAroundBonus = OpponentWizards.Where(w => nearestBonus.GetDistanceTo(w) < ASelf.VisionRange * 1.5).ToArray();
            var teammatesAroundBonus = MyWizards.Where(w => ASelf.GetDistanceTo(w) < ASelf.VisionRange * 1.5).ToArray();



            WizardPath path = null;
            AUnit pathTarget = null;
            var goAway = GoAwayDetect();
            var bonusMoving = goAway ? new MovingInfo(null, int.MaxValue, null) : GoToBonus();
            var target = FindTarget(new AWizard(ASelf), bonusMoving.Target);
            if (target == null && bonusMoving.Target == null && !goAway)
            {
                var nearest = OpponentCombats
                    .Where(
                        x =>
                            Utility.IsBase(x) || RoadsHelper.GetLane(x) == MessagesObserver.GetLane() ||
                            RoadsHelper.GetLaneEx(ASelf) == ALaneType.Middle && 
                            RoadsHelper.GetLaneEx(x) == ALaneType.Middle && CanRush(ASelf, x))
                    .Where(x => x.IsAssailable && x.Faction != Faction.Neutral)
                    .OrderBy(
                        x =>
                            x.GetDistanceTo(self) +
                            (x is AWizard ? -40 : (x is ABuilding && !((ABuilding) x).IsBesieded) ? 1500 : 0))
                    .ToArray();
                
                foreach (var n in nearest)
                {
                    path = GoAgainst(n);
                    if (path != null)
                    {
                        pathTarget = n;
                        break;
                    }
                }
                if (nearest.Length > 0 && path == null)
                {
                    GoDirect(nearest[0], FinalMove);
                }
            }

            TimerStart();
            if (!TryDodgeProjectile())
            {
                if (target == null)
                    TryPreDodgeProjectile();

                if (goAway)
                {
                    GoAway();
                }
                else if (target == null || target.Type == TargetType.Teammate ||
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

                        NextBonusWaypoint = ASelf + (NextBonusWaypoint - ASelf).Normalized() * (Self.Radius + 30);

                        if (nearestBonus.GetDistanceTo(ASelf) < ASelf.VisionRange*1.5 &&
                            nearestBonus.GetDistanceTo(ASelf) > 100 &&
                                          opponentsAroundBonus.Length <= 1 &&
                                          ASelf.Life + 10 >= (opponentsAroundBonus.FirstOrDefault() ?? ASelf).Life &&
                                          OpponentMinions.Count(x => x.GetDistanceTo(ASelf) < Game.FetishBlowdartAttackRange) == 0
                                          )
                            FinalMove.MoveTo(NextBonusWaypoint, null);
                        else
                            TryGoByGradient(x => EstimateDanger(x), null, FinalMove);
                        
                    }
                    else
                    {
                        var all = Combats.Select(Utility.CloneCombat).ToArray();
                        var my = all.FirstOrDefault(x => x.Id == ASelf.Id) as AWizard;

                        my?.Move(FinalMove.Speed, FinalMove.StrafeSpeed);
                        var ts = new TargetsSelector(all);

                        var skipBuildings = path == null 
                            || path.GetLength() < 300
                            || path.GetLength() < 600 && OpponentBuildings.Any(x =>
                            {
                                var tar = ts.Select(x);
                                return tar != null && tar.Id == ASelf.Id;
                            });


                        if (TryGoByGradient(x => EstimateDanger(x), x => HasAnyTarget(x, skipBuildings), FinalMove))
                        {
                            var cutTreeMovingInfo = FindTreeTarget(ASelf);
                            if (cutTreeMovingInfo.Target != null)
                            {
                                FinalMove.Turn = cutTreeMovingInfo.Move.Turn;
                                if (cutTreeMovingInfo.Move.Action != null && FinalMove.Action == null)
                                    FinalMove.Action = cutTreeMovingInfo.Move.Action;
                                // не будет мешать TryPreDodgeProjectile?
                            }
                        }
                    }
                }

                PostDodgeProjectile();
            }
            TimerEndLog("Go", 1);

            if (ASelf.CanLearnSkill)
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
            var self = new AWizard(ASelf);
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
                    var path = EmulateProjectileWithNearest(proj);
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

                if (RoadsHelper.GetAllowedForLine(lane).All(r => r.GetDistanceTo(pos) > (World.TickIndex < 900 && World.TickIndex > 200 ? 200 : 700)))
                    return 1000;

                foreach(var opp in OpponentWizards)
                    if (pos.GetDistanceTo2(opp) < Geom.Sqr(opp.CastRange + Const.WizardRadius + 20))
                        return 400;

                return 0.0;
            };
        }

        WizardPath GoAround(ACombatUnit to)
        {
            TimerStart();
            var ret = _goAround(to, false);
            TimerEndLog("Dijkstra", 1);
            return ret;
        }

        WizardPath GoAgainst(ACombatUnit to)
        {
            TimerStart();
            var ret = _goAround(to, true);
            TimerEndLog("Dijkstra", 1);
            return ret;
        }

        WizardPath _goAround(ACombatUnit target, bool goAgainst)
        {
            var my = new AWizard(ASelf);
            var selLane = Utility.IsBase(target) ? MessagesObserver.GetLane() : RoadsHelper.GetLane(target);
            var nearestBuilding = OpponentBuildings.ArgMin(b => b.GetDistanceTo2(my));

            var buildings = new List<ABuilding>();
            if (nearestBuilding.GetDistanceTo(my) > nearestBuilding.VisionRange)
                buildings.Add(nearestBuilding);
            if (target.IsOpponent && target.Id != nearestBuilding.Id && target is ABuilding)
                buildings.Add((ABuilding) target);

            var threshold = Self.CastRange - 200;
            
            if (ASelf.GetDistanceTo(target) < Self.CastRange || !goAgainst)
                threshold = 0;
            
            var path = DijkstraFindPath(ASelf, pos =>
            {
                // точка ОК, если с неё можно стрелять
                var dist2 = pos.GetDistanceTo2(target);
                if (dist2 < Geom.Sqr(Self.CastRange) && dist2 > Geom.Sqr(threshold))
                {
                    var distToLine = RoadsHelper.Roads.Where(seg => seg.LaneType == selLane).Min(seg => seg.GetDistanceTo(pos));

                    if (distToLine < 200
                        && (!goAgainst || BuildingsObserver.MyBase.GetDistanceTo2(pos) < BuildingsObserver.MyBase.GetDistanceTo2(target))
                        && TreesObserver.Trees
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
                return null;

            if (path.Count == 1)
            {
                FinalMove.Turn = my.GetAngleTo(target);
                return null;
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
            return path;
        }
		
		void CutTreesInPath(ATree nextTree, FinalMove move) 
		{
			if (nextTree == null)
				return;
			var my = new AWizard(ASelf);
			
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

        bool _skipBonusCond(ABonus bonus)
        {
            var oppFirst = BuildingsObserver.Buildings.FirstOrDefault(x => x.IsOpponent && x.Lane == MessagesObserver.GetLane() && x.Order == 0);
            if (oppFirst == null || ASelf.GetDistanceTo(oppFirst) <= oppFirst.CastRange)
                return true;

            var myFirst = BuildingsObserver.Buildings.FirstOrDefault(x => x.IsTeammate && x.Lane == MessagesObserver.GetLane() && x.Order == 0);
            if (myFirst == null || OpponentWizards.Any(x => x.GetDistanceTo(myFirst) <= myFirst.Radius))
                return true;

            // TODO
            return false;
        }

        ABonus SelectBonus(AWizard self)
        {
            var bonus = BonusesObserver.Bonuses.ArgMin(b => b.GetDistanceTo2(self));

            if (bonus.RemainingAppearanceTicks > MagicConst.GoToBonusMaxTicks + MagicConst.BonusTimeReserve)
                return null;

            if (self.GetDistanceTo(BuildingsObserver.OpponentBase) < BuildingsObserver.OpponentBase.CastRange * 1.4)
                return null;

            if (Game.IsSkillsEnabled && _skipBonusCond(bonus))
                return null;

            return bonus;
        }

        MovingInfo _goToBonus()
        {
            var bonus = SelectBonus(ASelf);
            var selMovingInfo = new MovingInfo(null, int.MaxValue, new FinalMove(new Move()));

            if (bonus == null)
                return selMovingInfo;

            if (Const.IsFinal)
            {
                var teammates = MyWizards
                    .Where(x => x.Id != ASelf.Id)
                    .Where(x =>
                    {
                        var b = SelectBonus(x);
                        return b != null && b.Id == bonus.Id;
                    })
                    .ToArray();
                if (teammates.Any(x => ASelf.GetDistanceTo(bonus) > x.GetDistanceTo(bonus)))
                {
                    return selMovingInfo;
                }
            }

            var my = new AWizard(ASelf);
            var nearestBuilding = OpponentBuildings.ArgMin(b => b.GetDistanceTo2(my));

            var path = DijkstraFindPath(ASelf, pos =>
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
           
            if (selMovingInfo.Time <= bonus.RemainingAppearanceTicks - MagicConst.BonusTimeReserve)
                selMovingInfo.Target = null;
#if DEBUG
            if (selMovingInfo.Target != null)
                Visualizer.Visualizer.SegmentsDrawQueue.Add(new object[] { path, Pens.Red, 3 });
#endif
            return selMovingInfo;
        }

        private bool HasAnyTarget(AWizard self, bool skipBuildings)
        {
            var my = new AWizard(self);
            foreach (var opp in OpponentCombats)
            {
                if (!opp.IsAssailable)
                    continue;

                var prevCastRange = my.CastRange;

                var bld = opp as ABuilding;
                if (opp is AWizard)
                {
                    if (my.CastRange <= opp.CastRange)
                    {
                        if (GoAwayCond(my, opp as AWizard))
                            my.CastRange = opp.CastRange + GoAwaySafeDist;
                        else if (my.CastRange < opp.CastRange)
                            my.CastRange = my.CastRange + 25; // HACK: чтобы не бояться подходить к тем у кого прокачан range 
                    }
                }
                if (bld != null && (skipBuildings || bld.IsBase))
                {
                    if (!bld.IsBase && bld.Lane != MessagesObserver.GetLane())
                        continue;

                    if (!bld.IsBesieded)
                    {
                        // чтобы не подходить близко к одиноким башням
                        if (my.GetDistanceTo(bld) < bld.CastRange + 6)
                            return true;
                    }
                }

                if (my.GetDistanceTo(opp) <= my.CastRange + opp.Radius + Game.MagicMissileRadius)
                {
                    var tmp = opp.RemainingFrozen;
                    opp.RemainingFrozen = 100500;
                    var canCast = my.EthalonCanCastMagicMissile(opp, checkCooldown: false, checkAngle: false);
                    opp.RemainingFrozen = tmp;
                    if (canCast)
                        return true;
                }

                my.CastRange = prevCastRange;
            }
            return false;
        }

        void _rushTo(AWizard self, AWizard opp)
        {
            // TODO: check angle

            if (self.CanUseFrostBolt()
                && self.GetDistanceTo(opp) <= Game.WizardCastRange + opp.Radius)
            {
                opp.ApplyMagicalDamage(self.FrostBoltDamage);
                opp.RemainingFrozen = Game.FrozenDurationTicks;
                self.RemainingFrostBoltCooldownTicks = Game.FrostBoltCooldownTicks;
                self.RemainingActionCooldownTicks = Game.WizardActionCooldownTicks;
            }

            if (self.CanUseStaff()
                && self.GetDistanceTo(opp) <= Game.StaffRange + opp.Radius)
            {
                opp.ApplyDamage(self.StaffDamage);
                self.RemainingStaffCooldownTicks = Game.StaffCooldownTicks;
                self.RemainingActionCooldownTicks = Game.WizardActionCooldownTicks;
            }

            if (self.CanUseMagicMissile()
                && self.GetDistanceTo(opp) <= Game.WizardCastRange + opp.Radius)
            {
                opp.ApplyMagicalDamage(self.MagicMissileDamage);
                self.RemainingMagicMissileCooldownTicks = self.MmSkillLevel == 5 ? 0 : Game.MagicMissileCooldownTicks;
                self.RemainingActionCooldownTicks = Game.WizardActionCooldownTicks;
            }

            if (self.GetDistanceTo(opp) > Game.StaffRange + opp.Radius)
            {
                self.MoveTo(opp, opp);
            }
            self.SkipTick();
        }

        double EmulateRush(AWizard self, AWizard opp)
        {
            TimerStart();
            var ret = _emulateRush(self, opp);
            TimerEndLog("EmulateRush", 1);
            return ret;
        }

        double _emulateRush(AWizard self, AWizard opp)
        {
            var nearest = Combats.Where(x => x.GetDistanceTo(ASelf) < ASelf.VisionRange*1.4).Select(Utility.CloneCombat).ToArray();
            self = nearest.FirstOrDefault(x => x.Id == self.Id) as AWizard;
            opp = nearest.FirstOrDefault(x => x.Id == opp.Id) as AWizard;
            
            if (self == null || opp == null)
                return int.MinValue;

            var tergetsSelector = new TargetsSelector(nearest);
            if (opp.Id == _LastMmTarget && World.Projectiles.Any(x => x.OwnerUnitId == self.Id))
                opp.ApplyMagicalDamage(self.MagicMissileDamage);

            while (true)
            {
                _rushTo(self, opp);
                foreach (var unit in nearest)
                {
                    if (unit.Id == self.Id)
                        continue;
                    if (unit is AWizard)
                    {
                        if (unit.IsOpponent)
                            _rushTo(unit as AWizard, self);
                        else
                        {
                            if (Const.IsFinal)
                                _rushTo(unit as AWizard, opp);
                            else
                                unit.SkipTick();
                        }
                    }
                    else if (unit is AMinion)
                    {
                        var tar = tergetsSelector.Select(unit);
                        unit.EthalonMove(tar);
                        if (tar != null && unit.EthalonCanHit(tar))
                        {
                            if (unit is AFetish)
                            {
                                unit.RemainingActionCooldownTicks = Game.FetishBlowdartActionCooldownTicks - 1;
                                tar.ApplyDamage(Game.DartDirectDamage);
                            }
                            else
                            {
                                unit.RemainingActionCooldownTicks =  Game.OrcWoodcutterActionCooldownTicks - 1;
                                tar.ApplyDamage(Game.OrcWoodcutterDamage);
                            }
                        }
                    }
                }
                
                if (!self.IsAlive)
                    return -opp.Life;
                if (!opp.IsAlive)
                {
                    var mines = nearest.Where(x => x.IsTeammate && x is AWizard).ToArray();
                    if (mines.Length >= 2 && Const.IsFinal)
                    {
                        if (!mines.All(x => x.Life > 10))
                            return int.MinValue;
                        return 50.0;//hack
                    }
                    else
                    {
                        if (nearest.Where(x => x.IsOpponent && x is AWizard).Sum(x => x.Life) > self.Life)
                            return int.MinValue;
                        return self.Life;
                    }
                }
            }
        }
    }
}