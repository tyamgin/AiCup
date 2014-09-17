using System;
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

        double StrikeProbability(Point puckPos, Point strikerSpeed, double StrikePower, double AngleStriker)
        {
            double range = game.StrikeAngleDeviation * 2,
                dx = game.StrikeAngleDeviation / 20,
                result = 0;

            for (double L = -range; L + dx <= range; L += dx)
            {
                double x = L + dx;
                if (Strike(puckPos, strikerSpeed, StrikePower, AngleStriker + x))
                    result += dx*Gauss(x, 0, game.StrikeAngleDeviation);
            }
            return result;
        }

        bool Strike(Point puckPos, Point strikerSpeed, double StrikePower, double AngleStriker)
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
            var pk = new APuck(puckPos, puckSpeedDirection, new Point(oppGoalie));
            return pk.Move(300, true) == 1;
        }
    }
}
