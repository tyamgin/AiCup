using System;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    partial class MyStrategy
    {
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
                    var inner = (Game.StaffRange + my.Radius) + 1; // (куда достаёт посохом) + запас
                    var outer = (opp.CastRange + my.Radius + Game.MagicMissileRadius) + 1; // (куда достанет MagicMissile) + запас
                    var coeff = 4;
                    if (dist < inner)
                        res += Game.StaffDamage + Game.MagicMissileDirectDamage;// TODO: обработать его навыки
                    else if (dist < outer)
                        res += Game.MagicMissileDirectDamage - (dist - inner)/(outer - inner)*coeff;
                }
                else if (opp is ABuilding)
                {
                    var building = opp as ABuilding;
                    
                    if (building.IsBesieded)
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
                    var inner = Game.OrcWoodcutterAttackRange + my.Radius + Game.MinionSpeed + 7/*запас*/;
                    var outer = 2*my.VisionRange;
                    if (dist < inner)
                        res += Game.OrcWoodcutterDamage + 10-dist/inner*10;
                    else if (dist < outer)
                        res -= 3 - (dist - inner)/(outer - inner)*3;
                }
                else if (opp is AFetish)
                {
                    var inner = opp.CastRange + my.Radius + Game.DartRadius + 1;
                    var delta = 1;
                    if (dist < inner)
                        res += delta - dist/inner*delta + Game.DartDirectDamage;
                }
                else
                {
                    throw new Exception("Unknown type of combat");
                }
            }
            // прижиматься к центру дорожки
            //var distToLine = Roads.Min(seg => seg.GetDistanceTo(my));
            //var maxDist = 500.0;
            //res -= 1 - (distToLine/maxDist)*1;

            // не прижиматься к деревьям
            var nearestTree = TreesObserver.GetNearestTree(my);
            if (nearestTree != null)
            {
                var dist = my.GetDistanceTo(nearestTree) - nearestTree.Radius;
                if (dist < 60)
                    res += 1.5-dist/60*1.5;
            }

            // не прижиматься к своим
            foreach (var co in MyCombats)
            {
                var dist = my.GetDistanceTo(co) - co.Radius - my.Radius;
                if (my.Id != co.Id && dist < 15)
                    res += 1 - dist/15*1;
            }

            // прижиматься за главную башню
            var corner = new Point(World.Width - 150, 150);
            var cornerMaxDist = 600;
            var distToCorner = my.GetDistanceTo(corner);
            if (distToCorner < cornerMaxDist)
                res -= 10 - (distToCorner/cornerMaxDist)*10;
            return res;
        }

        public static void InitializeRoads()
        {
            if (Roads == null)
            {
                var myMainBuilding = BuildingsObserver.Buildings.FirstOrDefault(x => x.IsTeammate && Math.Abs(x.Radius - Game.FactionBaseRadius) < 1);
                var dx = myMainBuilding.X / 2;
                var s = Const.MapSize;
                var a = new Point(dx, s - dx);
                var b = new Point(dx, dx);
                var c = new Point(s - dx, dx);
                var d = new Point(s - dx, s - dx);
                Roads = new[]
                {
                    new Segment(a, b),
                    new Segment(b, c),
                    new Segment(c, d),
                    new Segment(d, a),
                    new Segment(a, c),
                    new Segment(b, d),
                };
            }
        }

        List<Tuple<Point, double>> CalculateDangerMap()
        {
            double range = Self.VisionRange * 1.3,
                left = Self.X - range,
                right = Self.X + range,
                top = Self.Y - range,
                bottom = Self.Y + range;

            int grid = 60;
            var res = new List<Tuple<Point, double>>();
            var my = new AWizard(Self);
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


        private bool TryDodgeDanger()
        {
            return TryGoByGradient(EstimateDanger, HasAnyTarget);
        }

        private bool TryGoByGradient(PositionCostFunction costFunction, PositionCondition condition = null)
        {
            var my = new AWizard(Self);

            var obstacles = 
                Combats.Where(x => x.Id != Self.Id).Cast<ACircularUnit>()
                .Concat(TreesObserver.Trees)
                .Concat(BuildingsObserver.Buildings)
                .Where(x => my.GetDistanceTo2(x) < Geom.Sqr(my.VisionRange))
                .ToArray();

            var danger = costFunction(my);
            var minDanger = danger;
            Point selMoveTo = null;

            const int grid = 40;
            for (var i = 0; i < grid; i++)
            {
                var angle = Math.PI*2/grid*i + my.Angle;
                var moveTo = my + Point.ByAngle(angle);
                
                var self = new AWizard(Self);
                if (self.MoveTo(moveTo, null, 
                        w => obstacles.All(ob => !Geom.SegmentCircleIntersects(my, w, ob, ob.Radius + my.Radius)))
                    )
                {
                    var newDanger = costFunction(self);
                    if (newDanger < minDanger && (condition == null || condition(self)))
                    {
                        minDanger = newDanger;
                        selMoveTo = moveTo;
                    }
                }
            }
            if (minDanger < danger)
            {
                FinalMove.MoveTo(selMoveTo, null);
                return true;
            }
            return false;
        }

        bool TryDodgeProjectile()
        {
            var obstacles = Combats.Where(x => x.Id != Self.Id).ToArray();//TODO деревья

            const int grid = 40;
            for (var i = 0; i < grid; i++)
            {
                var angle = Math.PI*2/grid*i;
                var ticks = 0;
                var my = new AWizard(Self);
                var moveTo = my + Point.ByAngle(angle);

                while (ticks < ProjectilesCheckTicks)
                {
                    var shot = false;
                    for (var futureTick = ticks; futureTick < ProjectilesCheckTicks && !shot; futureTick++)
                    {
                        for (var mt = 0; mt < AProjectile.MicroTicks && !shot; mt++)
                        {
                            var microTick = futureTick*AProjectile.MicroTicks + mt + 1;
                            foreach (var proj in ProjectilesPaths[microTick])
                            {
                                if (!proj.Exists)
                                    continue;

                                if (proj.IntersectsWith(my))
                                {
                                    shot = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!shot)
                    {
                        if (ticks == 0)
                            return false;

                        FinalMove.MoveTo(moveTo, null);
                        return true;
                    }

                    my.MoveTo(moveTo, null, w => my.CheckIntersections(obstacles) == null);

                    for (var mt = 0; mt < AProjectile.MicroTicks; mt++)
                    {
                        foreach (var proj in ProjectilesPaths[ticks*AProjectile.MicroTicks + mt + 1])
                        {
                            if (!proj.Exists)
                                continue;

                            if (proj.IntersectsWith(my))
                            {
                                // выход из 3-х циклов
                                ticks = ProjectilesCheckTicks;
                                mt = AProjectile.MicroTicks;
                                break;
                            }
                        }
                    }

                    ticks++;
                }
            }
            return false;
        }

        public static int ProjectilesCheckTicks = 15;


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

        
    }
}
