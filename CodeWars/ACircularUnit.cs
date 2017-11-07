using System.Collections.Generic;
using System.Linq;
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

        public virtual bool IntersectsWith(ACircularUnit unit)
        {
            // если касаются, то false
            return GetDistanceTo2(unit) < Geom.Sqr(Radius + unit.Radius);
        }

        public ACircularUnit GetFirstIntersection(IEnumerable<ACircularUnit> units)
        {
            return units.FirstOrDefault(x => x.Id != Id && IntersectsWith(x));
        }

        public override string ToString()
        {
            return "(" + X.ToString().Replace(',', '.') + ", " + Y.ToString().Replace(',', '.') + "; " + Radius.ToString().Replace(',', '.') + ")";
        }
    }
}
