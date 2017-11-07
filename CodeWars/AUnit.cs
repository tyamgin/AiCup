using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class AUnit : Point
    {
        public long Id;

        public AUnit(Unit unit) : base(unit)
        {
            Id = unit.Id;
        }

        public AUnit(AUnit unit) : base(unit)
        {
            Id = unit.Id;
        }

        public AUnit()
        {
        }
    }
}
