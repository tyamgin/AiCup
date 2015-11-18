using System;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class AUnit : Point
    {
        public double Angle;

        public double GetAngleTo(double x, double y)
        {
            var absoluteAngleTo = Math.Atan2(y - Y, x - X);
            var relativeAngleTo = absoluteAngleTo - Angle;

            while (relativeAngleTo > Math.PI)
            {
                relativeAngleTo -= 2.0D * Math.PI;
            }

            while (relativeAngleTo < -Math.PI)
            {
                relativeAngleTo += 2.0D * Math.PI;
            }

            return relativeAngleTo;
        }

        public double GetAngleTo(Unit unit)
        {
            return GetAngleTo(unit.X, unit.Y);
        }

        public double GetAngleTo(Point unit)
        {
            return GetAngleTo(unit.X, unit.Y);
        }
    }

    public class ARectUnit : AUnit
    {
        public double Width, Height;

        public Point[] GetRect()
        {
            // 3  o---o  0
            //    o ^ o
            //    o   o
            // 2  o---o  1
            var dir = new Point(Width / 2, Height / 2);
            var angle = Math.Atan2(dir.Y, dir.X);
            var angles = new[] { Angle + angle, Angle + Math.PI - angle, Angle + Math.PI + angle, Angle - angle };
            var result = new Point[4];
            for (var i = 0; i < 4; i++)
                result[i] = this + ByAngle(angles[i]) * dir.Length;

            return result;
        }
    }

    public class ACircle : AUnit
    {
        public double Radius;
    }
}
