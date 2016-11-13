using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class ABuilding : ACombatUnit
    {
        public double Damage;
        public bool IsBesieded;

        public ABuilding(Building unit) : base(unit)
        {
            Damage = unit.Damage;
        }

        public ABuilding(ABuilding unit) : base(unit)
        {
            Damage = unit.Damage;
            IsBesieded = unit.IsBesieded;
        }
    }
}
