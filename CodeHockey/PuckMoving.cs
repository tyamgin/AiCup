using System;
using System.Collections.Generic;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        Point PuckMove(int ticks, Point _pos, Point _speed)
        {
            var pk = new APuck(_pos, _speed, new Point(oppGoalie));
            pk.Move(ticks);
            return pk;
        }

        double StrikeProbability(Point puckPos, Point strikerSpeed, double StrikePower, double AngleStriker, Point goalie)
        {
            double range = game.StrikeAngleDeviation * 2,
                dx = game.StrikeAngleDeviation / 20,
                result = 0;

            for (double L = -range; L + dx <= range; L += dx)
            {
                double x = L + dx;
                if (Strike(puckPos, strikerSpeed, StrikePower, AngleStriker + x, goalie))
                    result += dx*Gauss(x, 0, game.StrikeAngleDeviation);
            }
            return result;
        }

        bool Strike(Point puckPos, Point strikerSpeed, double StrikePower, double AngleStriker, Point goalie)
        {
            if (Math.Abs(puckPos.X - opp.NetFront) > RinkWidth/2)
                return false;

            // TODO: временный костыль
            if (Math.Abs(puckPos.X - opp.NetFront) < 2*HoRadius)
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
            for (var tick = 0; tick < ticks; tick++)
            {
                if (goalie.Y > to.Y)
                    goalie.Y -= Math.Min(game.GoalieMaxSpeed, goalie.Y - to.Y);
                else
                    goalie.Y += Math.Min(game.GoalieMaxSpeed, to.Y - goalie.Y);

                var minY = opp.NetTop + HoRadius;
                var maxY = opp.NetBottom - HoRadius;
                if (goalie.Y < minY)
                    goalie.Y = minY;
                if (goalie.Y > maxY)
                    goalie.Y = maxY;
            }
        }

        double ProbabStrikeAfter(int wait, int swingTime, Hockeyist self, IEnumerable<Tuple<int, double, double>> move)
        {
            double pDist = self.GetDistanceTo(puck);
            var power = GetPower(swingTime);
            var I = new AHo(Get(self), GetSpeed(self), self.Angle, self.AngularSpeed, self);
            var totalTime = 0;
            foreach (var action in move)
            {
                I.Move(action.Second, action.Third, action.First);
                totalTime += action.First;
            }
            var pk = new Point(I.Angle) * pDist + I;
            var goalie = new Point(oppGoalie);
            //GoalieMove(goalie, totalTime, pk);
            return StrikeProbability(pk, I.Speed, power, I.Angle, goalie);
        }
    }
}
