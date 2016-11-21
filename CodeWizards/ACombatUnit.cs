using System;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public abstract class ACombatUnit : ACircularUnit
    {
        public bool IsTeammate;
        public virtual bool IsOpponent => !IsTeammate;

        public Faction Faction;
        public double Life;
        public double VisionRange;
        public double CastRange;
        public int RemainingActionCooldownTicks;

        protected ACombatUnit(CircularUnit unit) : base(unit)
        {
            IsTeammate = unit.Faction == MyStrategy.Self.Faction;
            Faction = unit.Faction;
            var wizard = unit as Wizard;
            if (wizard != null)
            {
                Life = wizard.Life;
                VisionRange = wizard.VisionRange;
                CastRange = wizard.CastRange;
                RemainingActionCooldownTicks = wizard.RemainingActionCooldownTicks;
            }
            var building = unit as Building;
            if (building != null)
            {
                Life = building.Life;
                VisionRange = building.VisionRange;
                CastRange = building.AttackRange;
                RemainingActionCooldownTicks = building.RemainingActionCooldownTicks;
            }
            var minion = unit as Minion;
            if (minion != null)
            {
                Life = minion.Life;
                VisionRange = minion.VisionRange;
                if (minion.Type == MinionType.FetishBlowdart)
                    CastRange = MyStrategy.Game.FetishBlowdartAttackRange;
                RemainingActionCooldownTicks = minion.RemainingActionCooldownTicks;
            }
        }

        protected ACombatUnit(ACombatUnit unit) : base(unit)
        {
            IsTeammate = unit.IsTeammate;
            Faction = unit.Faction;
            Life = unit.Life;
            VisionRange = unit.VisionRange;
            CastRange = unit.CastRange;
            RemainingActionCooldownTicks = unit.RemainingActionCooldownTicks;
        }

        protected ACombatUnit()
        {
            
        }

        public virtual void SkipTick()
        {
            throw new NotImplementedException();
        }

        public virtual void EthalonMove(ACircularUnit target)
        {
            throw new NotImplementedException();
        }

        public virtual bool EthalonCanHit(ACircularUnit target)
        {
            throw new NotImplementedException();
        }

        public virtual bool IsAssailable => true;
    }
}
