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

        public override void SkipTick()
        {
            if (RemainingActionCooldownTicks > 0)
                RemainingActionCooldownTicks--;
        }

        public override void EthalonMove(ACircularUnit target)
        {
            SkipTick();
        }

        public override bool EthalonCanHit(ACircularUnit target)
        {
            return GetDistanceTo2(target) <= Geom.Sqr(CastRange + target.Radius);
        }

        public bool IsBase => Utility.Equals(MyStrategy.Game.FactionBaseRadius, Radius);
    }
}
