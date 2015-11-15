using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        private void _simulateOpponentMove(Points pts, ACar car)
        {
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

            ModelMove(car, new AMove { EnginePower = power, IsBrake = false, WheelTurn = TurnRound(turn) }, simpleMode: true);
        }

        public bool CheckUseOil()
        {
            if (self.RemainingOilCooldownTicks != 0 || world.Tick < game.InitialFreezeDurationTicks)
                return false;

            var slick = GetOilSlick(new ACar(self));
            var rad = slick.Radius * 0.8;
            var result = false;

            for (var t = 0; t < OpponentsTicksPrediction; t++)
            {
                for (var i = 0; i < Opponents.Length; i++)
                {
                    if (slick.GetDistanceTo2(OpponentsCars[t][i]) < rad * rad
                        && Math.Abs(OpponentsCars[t][i].WheelTurn) > 0.2)
                    {
                        result = true;
                    }
                }
            }
            return result;
        }
    }
}
