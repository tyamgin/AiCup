using System.Globalization;
using System.Runtime.CompilerServices;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class ACircularUnit : AUnit
    {
        public double Radius;

        public ACircularUnit(CircularUnit unit) : base(unit)
        {
            Radius = unit.Radius;
        }

        public ACircularUnit(ACircularUnit unit) : base(unit)
        {
            Radius = unit.Radius;
        }

        public ACircularUnit()
        {
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public virtual bool IntersectsWith(ACircularUnit unit)
        {
            // если касаются, то false
            return GetDistanceTo2(unit) < Geom.Sqr(Radius + unit.Radius);
        }

        public override string ToString()
        {
            return "(" + X.ToString(CultureInfo.InvariantCulture) + ", " + Y.ToString(CultureInfo.InvariantCulture) + "; " + Radius.ToString(CultureInfo.InvariantCulture) + ")";
        }
    }
}
