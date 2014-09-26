using System;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        Point PuckMove(int ticks, APuck pk, AHock hock)
        {
            if (hock == null)
            {
                pk.Move(ticks);
                return new Point(pk);
            }
            if (Math.Abs(hock.GetAngleTo(hock + hock.Speed)) < Deg(15) && hock.Speed.Length > 2)
                hock.Move(1, 0, ticks); // TODO
            return hock.PuckPos();
        }

        public static void GoalieMove(Point goalie, int ticks, Point to)
        {
            if (goalie == null)
                return;
            for (var tick = 0; tick < ticks; tick++)
            {
                if (goalie.Y > to.Y)
                    goalie.Y -= Math.Min(Game.GoalieMaxSpeed, goalie.Y - to.Y);
                else
                    goalie.Y += Math.Min(Game.GoalieMaxSpeed, to.Y - goalie.Y);

                var minY = Opp.NetTop + HoRadius;
                var maxY = Opp.NetBottom - HoRadius;
                if (goalie.Y < minY)
                    goalie.Y = minY;
                if (goalie.Y > maxY)
                    goalie.Y = maxY;
            }
        }

        int GetFirstOnPuck(IEnumerable<Hockeyist> except, APuck pk)
        {
            var cands = World.Hockeyists.Where(
                x => x.State == HockeyistState.Active
                     && x.Type != HockeyistType.Goalie
                     && x.RemainingCooldownTicks == 0
                     && except.Count(y => y.Id == x.Id) == 0
                ).ToArray();
            var times = cands.Select(x => GoToPuck(x, pk).Third).ToArray();
            int whereMin = 0;
            for(var i = 1; i < times.Count(); i++)
                if (times[i] < times[whereMin])
                    whereMin = i;
            if (cands[whereMin].PlayerId != My.Id)
                return -1;
            return times[whereMin];
        }

        bool TryPass(Point striker, Point strikerSpeed, double angleStriker, Hockeyist self)
        {
            if (self.RemainingCooldownTicks != 0)
                return false;

            const int passAnglesCount = 5;
            var bestAngle = 0.0;
            var minTime = Inf;
            var bestPower = 0.0;
            foreach (var power in new[] { 0.1, 0.2, 0.3, 0.5, 0.6, 0.8, 1.0 })
            {
                for (var passDir = -1; passDir <= 1; passDir += 2)
                {
                    for (var absPassAngle = 0.0; absPassAngle <= Game.PassSector/2; absPassAngle += Game.PassSector/passAnglesCount)
                    {
                        var passAngle = absPassAngle*passDir;
                        var pk = GetPassPuck(striker, strikerSpeed, angleStriker, power, passAngle);
                        var tm = GetFirstOnPuck(new[] {self}, pk);
                        if (tm == -1)
                            continue;
                        if (tm < minTime)
                        {
                            minTime = tm;
                            bestAngle = passAngle;
                            bestPower = power;
                        }
                    }
                }
            }
            if (minTime == Inf)
                return false;
            move.Action = ActionType.Pass;
            move.PassAngle = bestAngle;
            move.PassPower = bestPower;
            return true;
        }

        public APuck GetPassPuck(Point striker, Point strikerSpeed, double angleStriker, double PassPower, double PassAngle)
        {
            var puckSpeedAbs = 15.0*PassPower + strikerSpeed.Length*Math.Cos(angleStriker + PassAngle - strikerSpeed.GetAngle());
            var puckAngle = AngleNormalize(PassAngle + angleStriker);
            var puckSpeed = new Point(puckAngle)*puckSpeedAbs;
            return new APuck(Get(puck), puckSpeed, Get(OppGoalie)); 
        }
    }
}
