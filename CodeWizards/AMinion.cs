using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;
using Microsoft.Win32.SafeHandles;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public abstract class AMinion : ACombatUnit
    {
        public bool IsAggressiveNeutral;

        protected AMinion(Minion unit) : base(unit)
        {
        }

        protected AMinion(AMinion unit) : base(unit)
        {
            IsAggressiveNeutral = unit.IsAggressiveNeutral;
        }

        public static AMinion New(Minion minion)
        {
            return minion.Type == MinionType.FetishBlowdart
                ? (AMinion) new AFetish(minion)
                : (AMinion) new AOrc(minion);
        }

        public static AMinion New(AMinion minion)
        {
            return minion is AFetish
                ? (AMinion)new AFetish(minion)
                : (AMinion)new AOrc(minion);
        }

        public void Move()
        {
            if (RemainingActionCooldownTicks > 0)
                RemainingActionCooldownTicks--;
        }

        public override bool IsOpponent => IsAggressiveNeutral || !IsTeammate && (Faction == Faction.Academy || Faction == Faction.Renegades);
        public bool IsNeutral => Faction == Faction.Neutral;
    }

    public class AOrc : AMinion
    {
        public AOrc(Minion minion) : base(minion)
        {
            
        }

        public AOrc(AMinion minion) : base(minion)
        {

        }
    }

    public class AFetish : AMinion
    {
        public AFetish(Minion minion) : base(minion)
        {

        }

        public AFetish(AMinion minion) : base(minion)
        {

        }
    }
}
