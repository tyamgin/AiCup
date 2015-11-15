using System;
using System.CodeDom;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
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

            var slick = GetOilSlick(new ACar(self));
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
#if DEBUG
            var projPoints = projectiles.Select(x => new Points()).ToArray();
#endif
            var shot = new bool[projectiles.Length];
            for (var t = 1; t < OpponentsTicksPrediction; t++)
            {
                for(var prId = 0; prId < projectiles.Length; prId++)
                {
                    var pr = projectiles[prId];
                    pr.Move();
#if DEBUG
                    projPoints[prId].Add(new Point(pr));
#endif
                    foreach (var opp in OpponentsCars[t])
                    {
                        if (opp.GetRect().ContainPoint(pr))
                        {
                            shot[prId] = true;
                        }
                    }
                }
            }
#if DEBUG
            foreach(var projP in projPoints)
                _segmentsQueue.Add(new Tuple<Brush, Points>(Brushes.Blue, projP));
#endif
            var shotCount = shot.Count(val => val);
            return shotCount >= 2 || shotCount == 1 && self.ProjectileCount > 1;
        }
    }
}
