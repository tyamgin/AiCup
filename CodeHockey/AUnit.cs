using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public class AUnit : Point
    {
        public Point Speed;
        public double Angle;

        public AUnit(Point pos, Point speed, double angle)
            : base(pos)
        {
            Speed = new Point(speed);
            Angle = angle;
        }

        // copy pasted from Unit.cs
        public double GetAngleTo(double x, double y)
        {
            var absoluteAngleTo = Math.Atan2(y - this.Y, x - this.X);
            var relativeAngleTo = absoluteAngleTo - Angle;
            while (relativeAngleTo > Math.PI)
                relativeAngleTo -= 2.0D * Math.PI;
            while (relativeAngleTo < -Math.PI)
                relativeAngleTo += 2.0D * Math.PI;
            return relativeAngleTo;
        }

        public double GetAngleTo(Point to)
        {
            return GetAngleTo(to.X, to.Y);
        }
    }
}
