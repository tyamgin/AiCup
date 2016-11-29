using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Windows.Forms;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    partial class MyStrategy
    {
        public static double CantMoveDanger = 300;
        public static double GoToBonusDanger;

        double EstimateDanger(AWizard my)
        {
            double res = 0;
            foreach (var opp in OpponentCombats)
            {
                var dist = opp.GetDistanceTo(my);
                if (dist > 2*my.VisionRange)
                    continue;

                if (opp is AWizard)
                {
                    var wizard = opp as AWizard;
                    var inner = (Game.StaffRange + my.Radius) + 1; // (куда достаёт посохом) + запас
                    var outer = (opp.CastRange + my.Radius + Game.MagicMissileRadius) + 1; // (куда достанет MagicMissile) + запас
                    var coeff = 45;
                    if (dist < inner)
                        res += (wizard.StaffDamage + wizard.MagicMissileDamage) * 2;
                    else if (dist < outer)
                        res += (wizard.MagicMissileDamage + coeff - (dist - inner)/(outer - inner)*coeff) * 2;
                }
                else if (opp is ABuilding)
                {
                    var building = opp as ABuilding;

                    if (building.IsBase)
                    {
                        var outer = building.CastRange;
                        if (dist <= outer)
                            res += 30 - dist/outer*30 + building.Damage;
                    }
                    else if (building.IsBesieded)
                    {
                        var outer = building.VisionRange*1.2;
                        double delta = 2;
                        if (dist < outer)
                            res -= delta - dist/outer*delta;
                    }
                    else
                    {
                        var inner = Game.StaffRange + building.Radius; // откуда можно достать посохом
                        var outer = building.CastRange + my.Radius; // куда достреливает башня
                        double delta = -10;
                        if (dist < inner)
                            res += building.Damage - delta;
                        else if (dist < outer)
                            res += (dist - inner)/(outer - inner)*delta + building.Damage - delta;
                    }
                }
                else if (opp is AOrc)
                {
                    var inner = Game.OrcWoodcutterAttackRange + my.Radius + Game.MinionSpeed + 20/*запас*/;
                    var outer = 800;
                    if (dist < inner)
                        res += Game.OrcWoodcutterDamage + 15-dist/inner*15;
                    else if (dist < outer && my.MmSkillLevel < 5)
                        res -= 3 - (dist - inner)/(outer - inner)*3;
                }
                else if (opp is AFetish)
                {
                    var inner = opp.CastRange + my.Radius + Game.DartRadius + 10;
                    if (dist < inner)
                        res += 15 - dist/inner*15 + Game.DartDirectDamage;
                }
            }

            // не прижиматься к деревьям
            //var nearestTree = TreesObserver.GetNearestTree(my);
            //if (nearestTree != null)
            //{
            //    var dist = my.GetDistanceTo(nearestTree) - nearestTree.Radius;
            //    if (dist < 60)
            //        res += 1.5-dist/60*1.5;
            //}

            // не прижиматься к своим
            foreach (var co in MyCombats)
            {
                var dist = my.GetDistanceTo(co) - co.Radius - my.Radius;
                if (my.Id != co.Id && dist < 15)
                    res += 1 - dist/15*1;
            }

            // держаться подальше от места появления минионов
            var spawnDelta = Game.FactionMinionAppearanceIntervalTicks*0.33;
            var spawnRemains = World.TickIndex%Game.FactionMinionAppearanceIntervalTicks;
            if (spawnRemains < spawnDelta)
            {
                foreach (var pt in MagicConst.MinionAppearencePoints)
                {
                    var dist = pt.GetDistanceTo(my);
                    var inner = 400;
                    if (dist < inner)
                        res += 14 - dist/inner*14;
                }
            }

            // двигаться по пути к бонусу
            if (NextBonusWaypoint != null)
            {
                var dist = my.GetDistanceTo(NextBonusWaypoint);
                var outer = 100.0;
                if (dist < outer)
                    res -= GoToBonusDanger - dist/outer*GoToBonusDanger;
            }
            
            // прижиматься к центру дорожки
            var distToLine = RoadsHelper.Roads.Min(seg => seg.GetDistanceTo(my));
            var linePadding = 150.0;
            var outerPadding = 500;
            if (distToLine > linePadding && distToLine < outerPadding)
                res += (distToLine - linePadding)/(outerPadding - linePadding)*10;
            else if (distToLine >= outerPadding)
                res += 10;

            distToLine = RoadsHelper.Roads.Where(seg => seg.LaneType != ALaneType.Middle2).Min(seg => seg.GetDistanceTo(my));
            outerPadding = 500;
            if (distToLine > linePadding && distToLine < outerPadding)
                res -= 1 - (distToLine - linePadding) / (outerPadding - linePadding) * 1;
            else if (distToLine <= linePadding)
                res -= 1;

            // не прижиматься к стене
            var distToBorders = Math.Min(Math.Min(my.X, my.Y), Math.Min(Const.MapSize - my.X, Const.MapSize - my.Y));
            var bordersPadding = 45;
            if (distToBorders < bordersPadding)
                res += 4 - distToBorders/bordersPadding*4;

            // не перекрывать бонус
            foreach (var bonus in BonusesObserver.Bonuses)
            {
                var inner = bonus.Radius + my.Radius;
                var dist = my.GetDistanceTo(bonus);
                if (dist <= inner && !bonus.Exists && bonus.RemainingAppearanceTicks < 100) 
                    res += 30 + 20 - dist / inner * 20;
            }

            return res;
        }

        List<Tuple<Point, double>> CalculateDangerMap()
        {
            double range = Self.VisionRange * 1.1,
                left = Self.X - range,
                right = Self.X + range,
                top = Self.Y - range,
                bottom = Self.Y + range;

            int grid = 60;
            var res = new List<Tuple<Point, double>>();
            var my = new AWizard(ASelf);
            for (int i = 0; i <= grid; i++)
            {
                for (int j = 0; j <= grid; j++)
                {
                    var pt = new Point((right - left) / grid * i + left, (bottom - top) / grid * j + top);
                    if (pt.X < 0 || pt.Y < 0 || pt.X > Const.MapSize || pt.Y > Const.MapSize)
                        continue;

                    my.X = pt.X;
                    my.Y = pt.Y;
                    res.Add(new Tuple<Point, double>(pt, EstimateDanger(my)));
                }
            }
            return res;
        }

        public delegate double PositionCostFunction(AWizard wizard);
        public delegate bool PositionCondition(AWizard wizard);

        private bool TryGoByGradient(PositionCostFunction costFunction, PositionCondition condition, FinalMove move)
        {
            TimerStart();
            var ret = _TryGoByGradient(costFunction, condition, move);
            TimerEndLog("TryGoByGradient", 1);
            return ret;
        }

        private bool _TryGoByGradient(PositionCostFunction costFunction, PositionCondition condition, FinalMove move)
        {
            var self = new AWizard(ASelf);

            var obstacles = 
                Combats.Where(x => x.Id != Self.Id && !(x is ABuilding)).Cast<ACircularUnit>()
                .Concat(BuildingsObserver.Buildings)
                .Where(x => self.GetDistanceTo2(x) < Geom.Sqr(x.Radius + 150))
                .ToArray();

            var danger = costFunction(self);
            List<double> selVec = null;
            var minDanger = double.MaxValue;
            Point selMoveTo = null;

            const int grid = 24;
            for (var i = 0; i < grid; i++)
            {
                var angle = Math.PI*2/grid*i + self.Angle;
                var moveTo = self + Point.ByAngle(angle) * self.VisionRange;
                var nearest = Combats
                    .Where(x => x.GetDistanceTo(self) < Math.Max(self.VisionRange, x.VisionRange) * 1.3)
                    .Select(Utility.CloneCombat)
                    .ToArray();
                var tergetsSelector = new TargetsSelector(nearest);
                var opponents = nearest.Where(x => x.IsOpponent).ToArray();

                var vec = new List<double>();
                const int steps = 18;

                var my = (AWizard) nearest.FirstOrDefault(x => x.Id == self.Id);
                var ok = true;
                var canMove = true;

                while (vec.Count < steps)
                {
                    if (canMove)
                    {
                        canMove = my.MoveTo(moveTo, null, w =>
                            obstacles.All(ob => !Geom.SegmentCircleIntersects(self, w, ob, ob.Radius + self.Radius)));
                        if (TreesObserver.GetNearestTrees(my).Any(t => t.IntersectsWith(my)))
                            break;
                    }
                    else
                    {
                        my.SkipTick();
                    }

                    var tmp = OpponentCombats;//HACK
                    OpponentCombats = opponents;
                    vec.Add(costFunction(my));
                    OpponentCombats = tmp;
                    foreach (var x in opponents)
                    {
                        var tar = tergetsSelector.Select(x);
                        if (tar != null || x is AWizard)
                            x.EthalonMove(tar);
                    }
                    if (vec.Count == 1 && condition != null && !condition(my))
                    {
                        ok = false;
                        break;
                    }
                }

                if (!ok || vec.Count == 0)
                    continue;

                while(vec.Count < steps)
                    vec.Add(CantMoveDanger);

                //if (vec[0] < danger)
                {
                    var newDanger = 0.0;
                    for (var k = 0; k < steps; k++)
                        newDanger += vec[k]*Math.Pow(0.87, k);
                    newDanger += 3 * vec[0];

                    if (newDanger < minDanger)
                    {
                        minDanger = newDanger;
                        selMoveTo = moveTo;
                        selVec = vec;
                    }
                }
            }
            if (selMoveTo != null)
            {
                move.MoveTo(selMoveTo, null);
                return true;
            }
            return false;
        }

        bool TryDodgeProjectile()
        {
            TimerStart();
            var ret = _tryDodgeProjectile();
            TimerEndLog("TryDodgeProjectile", 1);
            return ret;
        }

        bool _tryDodgeProjectile()
        {
            var obstacles = Combats.Where(x => x.Id != Self.Id).ToArray();
            var minTicks = int.MaxValue;
            var maxDist = 0.0;
            Point selFirstMoveTo = null;

            const int grid = 40;
            for (var i = 0; i < grid; i++)
            {
                if (minTicks == 0 && maxDist > 999)
                    break;

                var angle = Math.PI*2/grid*i;
                var ticks = 0;
                var my = new AWizard(ASelf);
                var bonus = new ABonus(BonusesObserver.Bonuses.ArgMin(b => b.GetDistanceTo(Self)));
                Point firstMoveTo = null;
                var myStates = new List<AWizard> { new AWizard(my) };

                while (ticks < ProjectilesCheckTicks)
                {
                    var fireballMinDist = 1000.0;
                    var fireballMinDistTicks = 0;
                    var moveTo = my + Point.ByAngle(angle);
                    var shot = false;
                    for (var ticksPassed = 0; ticksPassed < ProjectilesCheckTicks; ticksPassed++)
                    {
                        var cur = myStates[Math.Min(ticksPassed, myStates.Count - 1)];
                        for (var mt = 0; mt < AProjectile.MicroTicks/* && !shot*/; mt++)
                        {
                            var microTick = ticksPassed*AProjectile.MicroTicks + mt + 1;
                            foreach (var proj in ProjectilesPaths[microTick])
                            {
                                if (!proj.Exists)
                                    continue;

                                if (proj.Type == ProjectileType.Fireball)
                                {
                                    var dist = Math.Max(0, cur.GetDistanceTo(proj) - cur.Radius - Game.FireballExplosionMaxDamageRange);
                                    if (dist <= Game.FireballExplosionMinDamageRange - Game.FireballExplosionMaxDamageRange)
                                    {
                                        if (dist < fireballMinDist)
                                        {
                                            fireballMinDist = dist;
                                            fireballMinDistTicks = ticks;
                                        }
                                        shot = true;
                                    }
                                }
                                else
                                {
                                    if (proj.IntersectsWith(cur))
                                    {
                                        shot = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (!shot)
                    {
                        if (ticks < minTicks)
                        {
                            minTicks = ticks;
                            maxDist = 1000;
                            selFirstMoveTo = firstMoveTo;
                        }
                        break;
                    }

                    if (fireballMinDist <= Game.FireballExplosionMinDamageRange - Game.FireballExplosionMaxDamageRange)
                    {
                        if (fireballMinDist > maxDist ||
                            Utility.Equals(fireballMinDist, maxDist) && fireballMinDistTicks < minTicks)
                        {
                            maxDist = fireballMinDist;
                            minTicks = fireballMinDistTicks;
                            selFirstMoveTo = firstMoveTo;
                        }
                    }

                    if (firstMoveTo == null)
                        firstMoveTo = moveTo;
                    bonus.SkipTick();
                    my.MoveTo(moveTo, null, w =>
                    {
                        if (CheckIntersectionsAndTress(w, obstacles))
                            return false;
                        if (bonus.RemainingAppearanceTicks < 15 && bonus.IntersectsWith(w))
                            return false;
                        return true;
                    });
                    myStates.Add(new AWizard(my));

                    ticks++;
                }
            }
            if (minTicks == 0 || minTicks == int.MaxValue) // нет необходимости уворачиваться
                return false;

            FinalMove.MoveTo(selFirstMoveTo, null);
            return true;
        }

        public static int ProjectilesCheckTicks = 25;


        public void InitializeProjectiles()
        {
            ProjectilesPaths = new AProjectile[ProjectilesCheckTicks * AProjectile.MicroTicks + 1][];
            ProjectilesPaths[0] = ProjectilesObserver.Projectiles.Select(x => new AProjectile(x)).ToArray();
            for (var i = 1; i <= ProjectilesCheckTicks * AProjectile.MicroTicks; i++)
            {
                ProjectilesPaths[i] = ProjectilesPaths[i - 1].Select(x =>
                {
                    var next = new AProjectile(x);
                    next.MicroMove();
                    return next;
                }).ToArray();
            }
        }

        void GoAway()
        {
            GoAround(BuildingsObserver.MyBase);
        }

        bool GoAwayDetect()
        {
            if (ASelf.Life >  Self.MaxLife/2)
                return false;

            var nearest = OpponentWizards
                .Where(x => ASelf.GetDistanceTo(x) < x.CastRange + ASelf.Radius + 10 && CanRush(x, ASelf))
                .ArgMin(x => x.GetDistanceTo2(ASelf));

            if (nearest != null)
            {
                 return true;
            }
            return false;
        }
    }
}
