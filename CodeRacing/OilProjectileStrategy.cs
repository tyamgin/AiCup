using System;
using System.Linq;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        /* 
         * Проверка что кто-то стоит впереди
         * p2   p5  p3  p4   p1
         * |    |   |   |    |
         * o-----------------o
         * |                 |
         * |                 |
         */
        public bool IsSomeoneAhead(ACar car, int direction)
        {
            var carRect = car.GetRect(0);
            Point p1, p2;
            if (direction > 0)
            {
                p1 = carRect[0] + Point.ByAngle(car.Angle)*20;
                p2 = carRect[3] + Point.ByAngle(car.Angle)*20;
            }
            else
            {
                p1 = carRect[1] - Point.ByAngle(car.Angle) * 20;
                p2 = carRect[2] - Point.ByAngle(car.Angle) * 20;
            }
            var p3 = (p1 + p2)/2;
            var p4 = (p1 + p3)/2;
            var p5 = (p2 + p3)/2;
            return world.Cars.Select(x => new ACar(x).GetRect(0)).Any(
                rect => Geom.ContainPoint(rect, p1) ||
                        Geom.ContainPoint(rect, p2) ||
                        Geom.ContainPoint(rect, p3) ||
                        Geom.ContainPoint(rect, p4) ||
                        Geom.ContainPoint(rect, p5)
                );
        }

        private void _simulateOpponentMove(Points pts, ACar car)
        {
            if (car.OutOfMap)
                return;

            if (pts.Count == 0)
            {
                Log("need more points!");
                return;
            }
            var p = pts[0];
            if (car.GetDistanceTo2(p) < 500 * 500)
            {
                pts.RemoveAt(0);
                _simulateOpponentMove(pts, car);
                return;
            }
            var turn = car.GetAngleTo(p);
            var power = 1.0;
            if (Math.Abs(turn) > 0.5)
                power = 0.5;
            if (Math.Abs(turn) > 1)
                power = 0.3;

            if (!ModelMove(car,
                new AMove {EnginePower = power, IsBrake = false, WheelTurn = TurnRound(turn)},
                simpleMode: true))
            {
                car.OutOfMap = true;
            }
        }

        public bool CheckUseOil()
        {
            if (self.RemainingOilCooldownTicks != 0 || world.Tick < Const.Game.InitialFreezeDurationTicks)
                return false;
            if (self.EnginePower < 0)
                return false;

            var slick = new AOilSlick(new ACar(self));
            var rad = slick.Radius * 0.8;
            var result = false;

            for (var t = 0; t < MagicConst.OpponentsTicksPrediction; t++)
            {
                for (var i = 0; i < Opponents.Length; i++)
                {
                    if (slick.GetDistanceTo2(OpponentsCars[i][t]) < rad * rad
                        && (self.OilCanisterCount > 1 || Math.Abs(OpponentsCars[i][t].WheelTurn) > 0.2))
                    {
                        result = true;
                    }
                }
            }
            if (!result)
                return false;

            foreach (var my in MyTeam)
            {
                if (my == null)
                    continue;
                foreach(var pos in my)
                    if (pos.GetDistanceTo2(slick) < Geom.Sqr(slick.Radius))
                        return false;
            }
            return true;
        }

        public bool CheckUseProjectile()
        {
            if (world.Tick < Const.Game.InitialFreezeDurationTicks)
                return false;
            if (self.ProjectileCount == 0)
                return false;
            if (self.RemainingProjectileCooldownTicks > 0)
                return false;

            var projectiles = AProjectile.GetProjectiles(new ACar(self));

            var shot = new bool[projectiles.Length];
            var shotSpeed = 0.0;

            var checkTicks = MagicConst.OpponentsTicksPrediction*(self.Type == CarType.Buggy ? 0.5 : 0.4);

            for (var t = 1; t < checkTicks; t++)
            {
                for(var prId = 0; prId < projectiles.Length; prId++)
                {
                    var pr = projectiles[prId];
                    if (!pr.Exists)
                        continue;

                    pr.Move();

                    for(var i = 0; i < All.Length; i++)
                    {
                        if (t >= All[i].Length)
                            continue;

                        var car = All[i][t];

                        /*
                         * Чужие машинки считать меньшими по размеру
                         * Свои - большими
                         */
                        if (pr.Intersect(car, car.Original.IsTeammate ? 5 : -(pr.Type == ProjectileType.Tire ? 40 : 5)))
                        {
                            if (pr.Type == ProjectileType.Tire)
                            {
                                // если это я только что выпустил шину
                                if (car.Original.Id == self.Id && Math.Abs(pr.Speed.Length - Const.Game.TireInitialSpeed) < Eps)
                                    continue;
                            }
                            else
                            {
                                // если это я выпустил шайбу
                                if (car.Original.Id == self.Id)
                                    continue;
                            }

                            if (car.Original.IsTeammate) // попал в своего
                                return false;

                            if (DurabilityObserver.ReactivationTime(car.Original) + 2 < world.Tick + t || DurabilityObserver.IsActive(car.Original))
                            {
                                // если он не мертв
                                shot[prId] = true;
                                if (pr.Type == ProjectileType.Tire)
                                    shotSpeed = pr.Speed.Length;
                            }
                            pr.Exists = false;
                        }
                    }
                }
            }
            var shotCount = shot.Count(val => val);
            
            if (self.Type == CarType.Buggy)
            {
                return shotCount >= 3 || shotCount == 2 && self.ProjectileCount > 2;
            }
            return shotCount == 1 &&
                   (shotSpeed >= Const.Game.TireInitialSpeed - Eps ||
                    shotSpeed >= Const.Game.TireInitialSpeed / 2.5 && self.ProjectileCount > 2);
        }
    }
}
