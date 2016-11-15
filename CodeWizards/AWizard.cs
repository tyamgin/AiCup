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

        public bool Move(double forwardSpeed, double strafeSpeed, CheckWizard check = null)
        {
            if (RemainingActionCooldownTicks > 0)
                RemainingActionCooldownTicks--;
            for (var i = 0; i < RemainingCooldownTicksByAction.Length; i++)
                if (RemainingCooldownTicksByAction[i] > 0)
                    RemainingCooldownTicksByAction[i]--;

            var dx = Math.Sin(Angle)*forwardSpeed + Math.Cos(Angle)*strafeSpeed;
            var dy = Math.Cos(Angle)*forwardSpeed - Math.Sin(Angle)*strafeSpeed;

            Y += dx;
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
                return Move(0, 0);// check не нужен
            
            
            var angle = GetAngleTo(to);
            var cos = Math.Cos(angle);
            var fs = cos * (cos >= 0 ? MyStrategy.Game.WizardForwardSpeed : MyStrategy.Game.WizardBackwardSpeed);
            var ss = Math.Sin(angle) * MyStrategy.Game.WizardStrafeSpeed;
            //MyStrategy.Game.WizardBackwardSpeed TODO!!!!
            return Move(fs, ss, check);
            //TODO can be optimized
        }

        public bool CanStaffAttack(ACircularUnit unit)
        {
            // пока без учета cooldown
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

        public int RemainingStaffCooldownTicks => RemainingCooldownTicksByAction[(int) ActionType.Staff];
        public int RemainingMagicMissileCooldownTicks => RemainingCooldownTicksByAction[(int)ActionType.MagicMissile];
    }
}
