using System;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class AUnit : Point
    {
        public double Angle;

        public double GetAngleTo(double x, double y)
        {
            var absoluteAngleTo = Math.Atan2(y - Y, x - X);
            var relativeAngleTo = absoluteAngleTo - Angle;

            Geom.AngleNormalize(ref relativeAngleTo);
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

        public AUnit(Unit unit) : base(unit)
        {
            Angle = unit.Angle;
        }
    }

    public class ACircularUnit : AUnit
    {
        public double Radius;

        public ACircularUnit(CircularUnit unit) : base(unit)
        {
            Radius = unit.Radius;
        }
    }
}
