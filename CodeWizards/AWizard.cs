using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class AWizard : ACombatUnit
    {
        public bool IsMaster;
        public int[] RemainingCooldownTicksByAction;

        public int RemainingHastened;
        public int RemainingEmpowered;
        public int RemainingFrozen;
        public int RemainingShielded;

        public AWizard(Wizard unit) : base(unit)
        {
            IsMaster = unit.IsMaster;
            RemainingCooldownTicksByAction = unit.RemainingCooldownTicksByAction.Select(x => x).ToArray();
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
                }
            }
        }

        public AWizard(AWizard unit) : base(unit)
        {
            IsMaster = unit.IsMaster;
            RemainingCooldownTicksByAction = unit.RemainingCooldownTicksByAction.Select(x => x).ToArray();
            RemainingHastened = unit.RemainingHastened;
            RemainingEmpowered = unit.RemainingEmpowered;
            RemainingFrozen = unit.RemainingFrozen;
            RemainingShielded = unit.RemainingShielded;
        }

        public delegate bool CheckWizard(AWizard wizard);

        public override void SkipTick()
        {
            Utility.Dec(ref RemainingActionCooldownTicks);
            for (var i = 0; i < RemainingCooldownTicksByAction.Length; i++)
                Utility.Dec(ref RemainingCooldownTicksByAction[i]);
            Utility.Dec(ref RemainingHastened);
            Utility.Dec(ref RemainingEmpowered);
            Utility.Dec(ref RemainingFrozen);
            Utility.Dec(ref RemainingShielded);
        }

        public bool Move(double forwardSpeed, double strafeSpeed, CheckWizard check = null)
        {
            SkipTick();

            var dx = Math.Sin(Angle)*forwardSpeed + Math.Cos(Angle)*strafeSpeed;
            var dy = Math.Cos(Angle)*forwardSpeed - Math.Sin(Angle)*strafeSpeed;

            Y += dx;// TODO: WTF? перепутаны имена переменных?
            X += dy;

            if (X - Radius < 0 || Y - Radius < 0 || X + Radius > Const.MapSize || Y + Radius > Const.MapSize)
            {
                Y -= dx;
                X -= dy;
                return false;
            }
            if (check != null)
                return check(this);
            return true;
        }

        public bool MoveTo(Point to, Point turnTo, CheckWizard check = null)
        {
            if (turnTo != null)
            {
                Angle += Utility.EnsureInterval(GetAngleTo(turnTo), MaxTurnAngle);
            }

            if (to == null || Utility.PointsEqual(this, to))
                return Move(0, 0); // check не нужен


            var angle = GetAngleTo(to);
            var cos = Math.Cos(angle);
            var fs = cos*(cos >= 0 ? MaxForwardSpeed : MaxBackwardSpeed);
            var ss = Math.Sin(angle)*MaxStrafeSpeed;
            return Move(fs, ss, check);
            //TODO can be optimized
        }

        public override void EthalonMove(ACircularUnit target)
        {
            MoveTo(target, target);
        }

        public bool EthalonCanCastMagicMissile(ACircularUnit opp, bool checkCooldown = true)
        {
            if (checkCooldown)
            {
                if (RemainingMagicMissileCooldownTicks > 0 || RemainingActionCooldownTicks > 0)
                    return false;
            }

            var distTo = GetDistanceTo(opp);
            if (distTo > CastRange + opp.Radius + MyStrategy.Game.MagicMissileRadius)
                return false;

            var angleTo = GetAngleTo(opp);
            var deltaAngle = Math.Atan2(opp.Radius, distTo);
            var angles = new[] {angleTo, angleTo + deltaAngle, angleTo - deltaAngle};

            foreach (var angle in angles)
            {
                if (Math.Abs(angle) > MyStrategy.Game.StaffSector/2)
                    continue;

                var proj = new AProjectile(this, angle, ProjectileType.MagicMissile);
                var path = proj.Emulate(new[] {(ACombatUnit) opp});
                if (path.Any(x => x.State == AProjectile.ProjectilePathState.Fire && x.Target.Faction != Faction)) //IsOpponent?
                    return true;
            }
            return false;
        }

        public override bool EthalonCanHit(ACircularUnit target)
        {
            if (CanStaffAttack(target))
                return true;

            return EthalonCanCastMagicMissile(target);
        }

        public bool CanStaffAttack(ACircularUnit unit)
        {
            if (RemainingActionCooldownTicks > 0 || RemainingStaffCooldownTicks > 0)
                return false;
            if (GetDistanceTo2(unit) > Geom.Sqr(MyStrategy.Game.StaffRange + unit.Radius))
                return false;
            if (Math.Abs(GetAngleTo(unit)) > MyStrategy.Game.StaffSector/2)
                return false;
            return true;
        }

        public ACircularUnit[] GetStaffAttacked(IEnumerable<ACircularUnit> candidates)
        {
            return candidates
                .Where(CanStaffAttack)
                .ToArray();
        }

        public int RemainingStaffCooldownTicks
        {
            get { return RemainingCooldownTicksByAction[(int)ActionType.Staff]; }
            set { RemainingCooldownTicksByAction[(int)ActionType.Staff] = value; }
        }

        public int RemainingMagicMissileCooldownTicks
        {
            get { return RemainingCooldownTicksByAction[(int) ActionType.MagicMissile]; }
            set { RemainingCooldownTicksByAction[(int) ActionType.MagicMissile] = value; }
        }

        public double MagicMissileDamage
        {
            get
            {
                // TODO: умения и ауры
                double damage = MyStrategy.Game.MagicMissileDirectDamage;
                if (RemainingEmpowered > 0)
                    damage *= MyStrategy.Game.EmpoweredDamageFactor;
                return damage;
            }
        }

        public double StaffDamage
        {
            get
            {
                // TODO: умения и ауры
                double damage = MyStrategy.Game.StaffDamage;
                if (RemainingEmpowered > 0)
                    damage *= MyStrategy.Game.EmpoweredDamageFactor;
                return damage;
            }
        }

        private double _getHastenedFactor()
        {
            // TODO: умения и ауры
            if (RemainingHastened > 0)
                return 1.0 + MyStrategy.Game.HastenedMovementBonusFactor;
            return 1.0;
        }

        public double MaxForwardSpeed => MyStrategy.Game.WizardForwardSpeed*_getHastenedFactor();

        public double MaxBackwardSpeed => MyStrategy.Game.WizardBackwardSpeed*_getHastenedFactor();

        public double MaxStrafeSpeed => MyStrategy.Game.WizardStrafeSpeed*_getHastenedFactor();

        public double MaxTurnAngle => MyStrategy.Game.WizardMaxTurnAngle*
                                      (RemainingHastened > 0 ? 1.0 + MyStrategy.Game.HastenedRotationBonusFactor : 1.0);
        // NOTE: Эффективное ограничение может быть выше в 1.0 + hastenedRotationBonusFactor раз в результате действия статуса HASTENED.
        // т.е. ауры и умения никак не влияют на угол поворота
    }
}
