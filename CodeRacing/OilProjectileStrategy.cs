using System;
using System.Linq;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public bool IsSomeoneAhead(ACar car)
        {
            var carRect = car.GetRect();
            var p1 = carRect[0] + Point.ByAngle(car.Angle)*20;
            var p2 = carRect[3] + Point.ByAngle(car.Angle)*20;
            var p3 = car + Point.ByAngle(car.Angle) * (car.Original.Width / 2 + 20);
            return world.Cars.Select(x => new ACar(x).GetRect()).Any(
                rect => Geom.ContainPoint(rect, p1) ||
                        Geom.ContainPoint(rect, p2) ||
                        Geom.ContainPoint(rect, p3)
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
            if (self.RemainingOilCooldownTicks != 0 || world.Tick < game.InitialFreezeDurationTicks)
                return false;

            var slick = new AOilSlick(new ACar(self));
            var rad = slick.Radius * 0.8;
            var result = false;

            for (var t = 0; t < OpponentsTicksPrediction; t++)
            {
                for (var i = 0; i < Opponents.Length; i++)
                {
                    if (slick.GetDistanceTo2(OpponentsCars[t][i]) < rad * rad
                        && (self.OilCanisterCount > 1 || Math.Abs(OpponentsCars[t][i].WheelTurn) > 0.2))
                    {
                        result = true;
                    }
                }
            }
            return result;
        }

        public bool CheckUseProjectile()
        {
            if (world.Tick < game.InitialFreezeDurationTicks)
                return false;
            if (self.ProjectileCount == 0)
                return false;
            if (self.RemainingProjectileCooldownTicks > 0)
                return false;

            var projectiles = GetProjectiles(new ACar(self));

            var shot = new bool[projectiles.Length];
            for (var t = 1; t < OpponentsTicksPrediction; t++)
            {
                for(var prId = 0; prId < projectiles.Length; prId++)
                {
                    var pr = projectiles[prId];
                    if (!pr.Exists)
                        continue;

                    pr.Move();

                    foreach (var opp in OpponentsCars[t])
                    {
                        if (Geom.ContainPoint(opp.GetRect(), pr))
                        {
                            if (DurabilityObserver.ReactivationTime(opp.Original) + 2 < world.Tick + t)
                            {
                                // если он не мертв
                                shot[prId] = true;
                            }
                            pr.Exists = false;
                        }
                    }
                }
            }
            var shotCount = shot.Count(val => val);
            return shotCount >= 3 || shotCount == 2 && self.ProjectileCount > 1;
        }
    }
}
