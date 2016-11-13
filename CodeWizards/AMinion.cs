using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class AMinion : ACombatUnit
    {
        public MinionType Type;

        public AMinion(Minion unit) : base(unit)
        {
            Type = unit.Type;
        }

        public AMinion(AMinion unit) : base(unit)
        {
            Type = unit.Type;
        }

        public void Move()
        {
            if (RemainingActionCooldownTicks > 0)
                RemainingActionCooldownTicks--;
        }

        public override bool IsOpponent => !IsTeammate && (Faction == Faction.Academy || Faction == Faction.Renegades);
    }
}
