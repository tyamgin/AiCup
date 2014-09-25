using System;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        Point PuckMove(int ticks, APuck pk, AHo ho)
        {
            if (ho == null)
            {
                pk.Move(ticks);
                return new Point(pk);
            }
            if (Math.Abs(ho.GetAngleTo(ho + ho.Speed)) < Deg(15) && ho.Speed.Length > 2)
                ho.Move(1, 0, ticks); // TODO
            return ho.PuckPos();
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

            const int psss = 4;
            var bestAngle = 0.0;
            var minTime = Inf;
            var bestPower = 0.0;
            foreach (var power in new[] { 0.1, 0.3, 0.5, 0.8, 1.0 })
            {
                for (var dir = -1; dir <= 1; dir += 2)
                {
                    for (var _angle = 0.0; _angle <= Game.PassSector/2; _angle += Game.PassSector/psss)
                    {
                        var angle = _angle*dir;
                        var pk = GetPassPuck(striker, strikerSpeed, angleStriker, power, angle);
                        var tm = GetFirstOnPuck(new[] {self}, pk);
                        if (tm == -1)
                            continue;
                        if (tm < minTime)
                        {
                            minTime = tm;
                            bestAngle = angle;
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
            var puckSpeed = 15.0*PassPower + strikerSpeed.Length*Math.Cos(angleStriker + PassAngle - strikerSpeed.GetAngle());
            var puckAngle = AngleNormalize(PassAngle + angleStriker);
            var PuckSpeed = new Point(puckAngle)*puckSpeed;
            return new APuck(Get(puck), PuckSpeed, Get(OppGoalie)); 
        }
    }
}
