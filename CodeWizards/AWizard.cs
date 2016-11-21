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

        public AWizard(Wizard unit) : base(unit)
        {
            IsMaster = unit.IsMaster;
            RemainingCooldownTicksByAction = unit.RemainingCooldownTicksByAction.Select(x => x).ToArray();
        }

        public AWizard(AWizard unit) : base(unit)
        {
            IsMaster = unit.IsMaster;
            RemainingCooldownTicksByAction = unit.RemainingCooldownTicksByAction.Select(x => x).ToArray();
        }

        public delegate bool CheckWizard(AWizard wizard);

        public override void SkipTick()
        {
            if (RemainingActionCooldownTicks > 0)
                RemainingActionCooldownTicks--;
            for (var i = 0; i < RemainingCooldownTicksByAction.Length; i++)
                if (RemainingCooldownTicksByAction[i] > 0)
                    RemainingCooldownTicksByAction[i]--;
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
                Angle += Utility.EnsureInterval(GetAngleTo(turnTo), MyStrategy.Game.WizardMaxTurnAngle);
            }

            if (to == null)
                return Move(0, 0); // check не нужен


            var angle = GetAngleTo(to);
            var cos = Math.Cos(angle);
            var fs = cos*(cos >= 0 ? MyStrategy.Game.WizardForwardSpeed : MyStrategy.Game.WizardBackwardSpeed);
            var ss = Math.Sin(angle)*MyStrategy.Game.WizardStrafeSpeed;
            //MyStrategy.Game.WizardBackwardSpeed TODO!!!!
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

        public int MagicMissileDamage => MyStrategy.Game.MagicMissileDirectDamage; // TODO: умения и статусы
    }
}
