using System;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public abstract class ACombatUnit : ACircularUnit
    {
        public bool IsTeammate;
        public virtual bool IsOpponent => !IsTeammate;

        public double Life, MaxLife;
        public double VisionRange;
        public double CastRange;
        public int RemainingActionCooldownTicks;

        public int RemainingHastened;
        public int RemainingEmpowered;
        public int RemainingFrozen;
        public int RemainingShielded;

        public bool IsBurning; // пока только для отображения в визуализаторе

        protected ACombatUnit(LivingUnit unit) : base(unit)
        {
            IsTeammate = unit.Faction == MyStrategy.Self.Faction;
            Life = unit.Life;
            MaxLife = unit.MaxLife;
            var wizard = unit as Wizard;
            if (wizard != null)
            {
                VisionRange = wizard.VisionRange;
                CastRange = wizard.CastRange;
                RemainingActionCooldownTicks = wizard.RemainingActionCooldownTicks;
            }
            var building = unit as Building;
            if (building != null)
            {
                VisionRange = building.VisionRange;
                CastRange = building.AttackRange;
                RemainingActionCooldownTicks = building.RemainingActionCooldownTicks;
            }
            var minion = unit as Minion;
            if (minion != null)
            {
                VisionRange = minion.VisionRange;
                if (minion.Type == MinionType.FetishBlowdart)
                    CastRange = MyStrategy.Game.FetishBlowdartAttackRange;
                RemainingActionCooldownTicks = minion.RemainingActionCooldownTicks;
            }

            foreach (var status in unit.Statuses)
            {
                switch (status.Type)
                {
                    case StatusType.Empowered:
                        RemainingEmpowered = Math.Max(RemainingEmpowered, status.RemainingDurationTicks);
                        break;
                    case StatusType.Frozen:
                        RemainingFrozen = Math.Max(RemainingFrozen, status.RemainingDurationTicks);
                        break;
                    case StatusType.Shielded:
                        RemainingShielded = Math.Max(RemainingShielded, status.RemainingDurationTicks);
                        break;
                    case StatusType.Hastened:
                        RemainingHastened = Math.Max(RemainingHastened, status.RemainingDurationTicks);
                        break;
                    case StatusType.Burning:
                        IsBurning = true;
                        break;
                }
            }
        }

        protected ACombatUnit(ACombatUnit unit) : base(unit)
        {
            IsTeammate = unit.IsTeammate;
            Life = unit.Life;
            MaxLife = unit.MaxLife;
            VisionRange = unit.VisionRange;
            CastRange = unit.CastRange;
            RemainingActionCooldownTicks = unit.RemainingActionCooldownTicks;

            RemainingHastened = unit.RemainingHastened;
            RemainingEmpowered = unit.RemainingEmpowered;
            RemainingFrozen = unit.RemainingFrozen;
            RemainingShielded = unit.RemainingShielded;
            IsBurning = unit.IsBurning;
        }

        protected ACombatUnit()
        {
            
        }

        public virtual ACombatUnit SelectTarget(ACombatUnit[] candidates)
        {
            return null;
        }

        public virtual void SkipTick()
        {
            Utility.Dec(ref RemainingActionCooldownTicks);

            Utility.Dec(ref RemainingHastened);
            Utility.Dec(ref RemainingEmpowered);
            Utility.Dec(ref RemainingFrozen);
            Utility.Dec(ref RemainingShielded);
        }

        public virtual void EthalonMove(ACircularUnit target)
        {
            throw new NotImplementedException();
        }

        public virtual bool EthalonCanHit(ACircularUnit target, bool checkCooldown = true)
        {
            throw new NotImplementedException();
        }

        public virtual bool IsAssailable => true;

        public void ApplyDamage(double damage)
        {
            if (RemainingShielded > 0)
                damage -= damage * MyStrategy.Game.ShieldedDirectDamageAbsorptionFactor;
            Life -= damage;
            if (Life < 0)
                Life = 0;
        }

        public void ApplyMagicalDamage(double damage)
        {
            // TODO: умения и ауры щита
            ApplyDamage(damage);
        }

        public bool IsAlive => Life > Const.Eps;
    }
}
