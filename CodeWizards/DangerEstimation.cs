using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    partial class MyStrategy
    {
        double EstimatePoint(AWizard my)
        {
            double res = 0;
            foreach (var opp in OpponentCombats)////////////////////////////////////
            {
                var dist = opp.GetDistanceTo(my);
                if (dist > 2*my.VisionRange)
                    continue;

                if (opp is AWizard)
                {
                    if (dist < Game.StaffRange + my.Radius)
                        res += Game.StaffDamage;// TODO: обработать его навыки
                    if (dist < opp.CastRange + my.Radius)
                        res += Game.MagicMissileDirectDamage; // TODO: обработать его навыки
                }
                else if (opp is ABuilding)
                {
                    if (dist < opp.CastRange + my.Radius)
                        res += (opp as ABuilding).Damage;
                }
                else if (opp is AMinion)
                {
                    if ((opp as AMinion).Type == MinionType.FetishBlowdart && dist < opp.CastRange + my.Radius)
                        res += Game.DartDirectDamage;
                    if ((opp as AMinion).Type == MinionType.OrcWoodcutter && dist < Game.OrcWoodcutterAttackRange + my.Radius)
                        res += Game.OrcWoodcutterDamage;
                }
                else
                {
                    throw new Exception("Unknown type of combat");
                }
                res += 1 / my.GetDistanceTo(opp);//TODO
            }
            return res;
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
                    if (pt.X < 0 || pt.Y < 0 || pt.X > Const.Width || pt.Y > Const.Height)
                        continue;

                    my.X = pt.X;
                    my.Y = pt.Y;
                    res.Add(new Tuple<Point, double>(pt, EstimatePoint(my)));
                }
            }
            return res;
        }

        private bool TryDodge2()
        {
            var my = new AWizard(Self);

            var obstacles = 
                Combats.Where(x => x.Id != Self.Id).Cast<ACircularUnit>()
                .Concat(TreesObserver.Trees)
                .Where(x => my.GetDistanceTo2(x) < Geom.Sqr(my.VisionRange))
                .ToArray();

            var danger = EstimatePoint(my);
            var minDanger = danger;
            double selSpeed = 0, selStrafeSpeed = 0;

            const int grid = 40;
            for (var i = 0; i < grid; i++)
            {
                var angle = Math.PI*2/grid*i;
                var forwardSpeed = Math.Cos(angle - Self.Angle)*Game.WizardForwardSpeed;
                var strafeSpeed = Math.Sin(angle - Self.Angle)*Game.WizardStrafeSpeed;

                var self = new AWizard(Self);
                self.Move(forwardSpeed, strafeSpeed);
                var newDanlge = EstimatePoint(self);
                if (newDanlge < minDanger && HasAnyTarget(self))
                {
                    var oldCond = self.CheckIntersections(obstacles) == null;
                    var newCond = obstacles.All(ob => Geom.SegmentCircleIntersect(my, self, ob, ob.Radius + my.Radius).Length == 0);
                    if (newCond != oldCond)
                    {
                        var t = 0;
                    }
                    if (newCond)
                    {
                        minDanger = newDanlge;
                        selSpeed = forwardSpeed;
                        selStrafeSpeed = strafeSpeed;
                    }
                }
            }
            if (minDanger < danger)
            {
                FinalMove.Speed = selSpeed;
                FinalMove.StrafeSpeed = selStrafeSpeed;
                return true;
            }
            return false;
        }

        bool TryDodge()
        {
            var obstacles = Combats.Where(x => x.Id != Self.Id).ToArray();//TODO деревья

            const int grid = 40;
            for (var i = 0; i < grid; i++)
            {
                var angle = Math.PI*2/grid*i;
                var forwardSpeed = Math.Cos(angle - Self.Angle) * Game.WizardForwardSpeed;
                var strafeSpeed = Math.Sin(angle - Self.Angle) * Game.WizardStrafeSpeed;

                var ticks = 0;
                var my = new AWizard(Self);

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

                        FinalMove.Speed = forwardSpeed;
                        FinalMove.StrafeSpeed = strafeSpeed;
                        return true;
                    }

                    double prevX = my.X, prevY = my.Y;
                    my.Move(forwardSpeed, strafeSpeed);
                    if (my.CheckIntersections(obstacles) != null)
                    {
                        my.X = prevX;
                        my.Y = prevY;
                    }

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
