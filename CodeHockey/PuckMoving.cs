using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
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

        double StrikeProbability(Point puckPos, Point strikerSpeed, double StrikePower, double AngleStriker, Point goalie)
        {
            double range = Game.StrikeAngleDeviation * 2,
                dx = Game.StrikeAngleDeviation / 20,
                result = 0;

            for (double L = -range; L + dx <= range; L += dx)
            {
                double x = L + dx;
                if (Strike(puckPos, strikerSpeed, StrikePower, AngleStriker + x, goalie))
                    result += dx*Gauss(x, 0, Game.StrikeAngleDeviation);
            }
            return result;
        }

        bool Strike(Point puckPos, Point strikerSpeed, double StrikePower, double AngleStriker, Point goalie)
        {
            if (Math.Abs(puckPos.X - Opp.NetFront) > RinkWidth/2)
                return false;

            // TODO: временный костыль
            if (Math.Abs(puckPos.X - Opp.NetFront) < 2*HoRadius)
                return false;

            if (MyRight())
            {
                if (Math.Cos(AngleStriker) > 0)
                    return false;
            }
            else
            {
                if (Math.Cos(AngleStriker) < 0)
                    return false;
            }

            var strikerDirection = new Point(AngleStriker);
            var SpeedStriker = strikerSpeed.Length;
            var SpeedAngleStriker = strikerSpeed.GetAngle();
            var puckSpeed = 20.0 * StrikePower + SpeedStriker * Math.Cos(AngleStriker - SpeedAngleStriker);
            var puckSpeedDirection = strikerDirection * puckSpeed;
            var pk = new APuck(puckPos, puckSpeedDirection, goalie);
            return pk.Move(300, true) == 1;
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

        double ProbabStrikeAfter(int wait, int swingTime, Hockeyist self, IEnumerable<Tuple<int, double, double>> move)
        {
            var power = GetPower(swingTime);
            var I = new AHo(Get(self), GetSpeed(self), self.Angle, self.AngularSpeed, self);
            var totalTime = 0;
            var opps = World.Hockeyists.Where(
                x => x.PlayerId == Opp.Id
                     && x.State == HockeyistState.Active
                     && x.Type != HockeyistType.Goalie
                ).Select(x => new AHo(Get(x), GetSpeed(x), x.Angle, x.AngularSpeed, x)).ToArray();

            foreach (var action in move)
            {
                for (int i = 0; i < action.First; i++)
                {
                    I.Move(action.Second, action.Third);
                    foreach (var opp in opps)
                    {
                        opp.Move(1, TurnNorm(opp.GetAngleTo(I)));
                        if (CanStrike(opp, I) || CanStrike(opp, GetPuckPos(I, I.Angle)))
                            return 0.0;
                    }
                }
                totalTime += action.First;
            }
            var pk = GetPuckPos(I, I.Angle);
            var goalie = Get(OppGoalie);
            GoalieMove(goalie, totalTime, pk);
            return StrikeProbability(pk, I.Speed, power, I.Angle, goalie);
        }

        int GetFirstOnPuck(IEnumerable<Hockeyist> except, APuck pk)
        {
            var cands = World.Hockeyists.Where(
                x => x.State == HockeyistState.Active
                     && x.Type != HockeyistType.Goalie
                     && x.RemainingCooldownTicks == 0
                     && except.Count(y => y.Id == x.Id) == 0
                ).ToArray();
            var times = cands.Select(x => GetTicksToPuck(Get(x), GetSpeed(x), x.Angle, x.AngularSpeed, x, pk)).ToArray();
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

            const int psss = 3;
            const int pwrs = 4;
            var bestAngle = 0.0;
            var minTime = Inf;
            var bestPower = 0.0;
            for (var power = 0.0; power <= 1.0; power += 1.0/pwrs)
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
            var puckAngle = AngleNormalize(PassAngle + GetSpeed(puck).GetAngle());
            var PuckSpeed = new Point(puckAngle)*puckSpeed;
            return new APuck(Get(puck), PuckSpeed, Get(OppGoalie)); 
        }
    }
}
