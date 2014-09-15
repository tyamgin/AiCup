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

        bool Strike(Point puckPos, Point strikerSpeed, double StrikePower, double AngleStriker)
        {
            if (MyRight() && Math.Cos(AngleStriker) > 0)
                return false;
            if (MyLeft() && Math.Cos(AngleStriker) < 0)
                return false;

            var strikerDirection = new Point(AngleStriker);
            var SpeedStriker = strikerSpeed.Length;
            var SpeedAngleStriker = strikerSpeed.Angle;
            var puckSpeed = 20.0 * StrikePower + SpeedStriker * Math.Cos(AngleStriker - SpeedAngleStriker);
            var puckSpeedDirection = strikerDirection.Mul(puckSpeed);
            var pk = new APuck(puckPos, puckSpeedDirection, new Point(oppGoalie));
            return pk.Move(500) == 1;
        }
    }
}
