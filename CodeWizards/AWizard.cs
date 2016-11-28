using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class AWizard : ACombatUnit
    {
        public static int[] Xps;

        public bool IsMaster;
        public int[] RemainingCooldownTicksByAction;

        public int Level;
        public int Xp; // TODO: не используется
        public double Mana;

        public int[] SkillsLearnedArr;
        public int[] SkillsFactorsArr; // какие у меня умения (без учета аур)
        public int[] AurasFactorsArr; // какие на меня действуют ауры (в т.ч. мои)

        public AWizard(Wizard unit) : base(unit)
        {
            IsMaster = unit.IsMaster;
            RemainingCooldownTicksByAction = unit.RemainingCooldownTicksByAction.ToArray();
            Level = unit.Level;
            Xp = unit.Xp;
            Mana = unit.Mana;

            SkillsLearnedArr = new int[5];
            SkillsFactorsArr = new int[5];
            AurasFactorsArr = new int[5]; // массив заполняется внешним кодом
            foreach (var skill in unit.Skills)
            {
                var skillOrder = Utility.GetSkillOrder(skill);
                var skillGroup = Utility.GetSkillGroup(skill);
                SkillsLearnedArr[skillGroup] = Math.Max(SkillsLearnedArr[skillGroup], skillOrder + 1);
            }
            for (var i = 0; i < 5; i++)
                SkillsFactorsArr[i] = (Math.Min(4, SkillsLearnedArr[i]) + 1)/2;
        }

        public AWizard(AWizard unit) : base(unit)
        {
            IsMaster = unit.IsMaster;
            RemainingCooldownTicksByAction = unit.RemainingCooldownTicksByAction.ToArray();            
            Level = unit.Level;
            Xp = unit.Xp;
            Mana = unit.Mana;
            SkillsLearnedArr = unit.SkillsLearnedArr.ToArray();
            SkillsFactorsArr = unit.SkillsFactorsArr.ToArray();
            AurasFactorsArr = unit.AurasFactorsArr.ToArray();
        }

        public delegate bool CheckWizardCollisionsFunc(AWizard wizard);

        public override void SkipTick()
        {
            base.SkipTick();
            for (var i = 0; i < RemainingCooldownTicksByAction.Length; i++)
                Utility.Dec(ref RemainingCooldownTicksByAction[i]);
        }

        public static Point _getHalfEllipseDxDy(double a, double b, double c, double angle)
        {
            // http://russianaicup.ru/forum/index.php?topic=708.msg6889#msg6889
            if (Math.Abs(angle) > Math.PI/2)
                return new Point(Math.Sin(angle) * c, Math.Cos(angle) * c);
            
            if (Math.Abs(angle) < Const.Eps)
                return new Point(0, b);

            var tan = Math.Tan(angle);
            double dx = Math.Sqrt(1.0 / (Geom.Sqr(1.0 / a) + Geom.Sqr(1.0 / tan / b)));

            if (angle < 0) dx *= -1;
            return new Point(dx, Math.Abs(dx / tan));
        }

        public bool MoveTo(Point to, Point turnTo, CheckWizardCollisionsFunc checkCollisions = null)
        {
            if (turnTo != null && RemainingFrozen == 0)
                Angle += Utility.EnsureInterval(GetAngleTo(turnTo), MaxTurnAngle);

            var isFrozen = RemainingFrozen > 0;
            SkipTick();

            if (!isFrozen && to != null && !Utility.PointsEqual(this, to))
            {
                var angle = GetAngleTo(to);
                var myDir = Point.ByAngle(Angle);
                var myStrafeDir = new Point(-myDir.Y, myDir.X);
                var d = _getHalfEllipseDxDy(MaxStrafeSpeed, MaxForwardSpeed, MaxBackwardSpeed, angle);
                var move = myDir*d.Y + myStrafeDir*d.X;
                X += move.X;
                Y += move.Y;

                if (X - Radius < 0 || Y - Radius < 0 || X + Radius > Const.MapSize || Y + Radius > Const.MapSize || checkCollisions != null && !checkCollisions(this))
                {
                    X -= move.X;
                    Y -= move.Y;
                    return false;
                }
            }
            return true;
        }

        public override void EthalonMove(ACircularUnit target)
        {
            MoveTo(target, target);
        }

        public bool EthalonCanCastMagicMissile(ACircularUnit opp, bool checkCooldown = true)
        {
            if (checkCooldown)
            {
                if (RemainingMagicMissileCooldownTicks > 0 || RemainingActionCooldownTicks > 0 || Mana < MyStrategy.Game.MagicMissileManacost)
                    return false;
            }
            if (RemainingFrozen > 0)
                return false;

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
                if (path.Any(x => x.State == AProjectile.ProjectilePathState.Shot && x.Target.Faction != Faction)) //IsOpponent?
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
            if (RemainingFrozen > 0)
                return false;
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

        public int RemainingStaffCooldownTicks => RemainingCooldownTicksByAction[(int)ActionType.Staff];
            
        public int RemainingMagicMissileCooldownTicks => RemainingCooldownTicksByAction[(int) ActionType.MagicMissile];
            
        public int RemainingFrostBoltCooldownTicks => RemainingCooldownTicksByAction[(int)ActionType.FrostBolt];
            
        public int RemainingFireballCooldownTicks => RemainingCooldownTicksByAction[(int)ActionType.Fireball];
            

        private double _getDamageFactor()
        {
            // действие статуса
            return RemainingEmpowered > 0 ? MyStrategy.Game.EmpoweredDamageFactor : 1;
        }

        private double _getCastDamageAddition()
        {
            // действие скиллов и аур
            return MyStrategy.Game.MagicalDamageBonusPerSkillLevel*(SkillsFactorsArr[1] + AurasFactorsArr[1]);
        }

        private double _getStaffDamageAddition()
        {
            // действие скиллов и аур
            return MyStrategy.Game.StaffDamageBonusPerSkillLevel * (SkillsFactorsArr[2] + AurasFactorsArr[2]);
        }

        public double MagicMissileDamage => MyStrategy.Game.MagicMissileDirectDamage*_getDamageFactor() + _getCastDamageAddition();

        public double StaffDamage => MyStrategy.Game.StaffDamage*_getDamageFactor() + _getStaffDamageAddition();

        public double FrostBoltDamage => MyStrategy.Game.FrostBoltDirectDamage*_getDamageFactor() + _getCastDamageAddition();


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

        public bool CanLearnSkill => SkillsLearned < Level;

        public int SkillsLearned => SkillsLearnedArr.Sum(x => x);


        public int MmSkillLevel => SkillsLearnedArr[0];

        public int FrozenSkillLevel => SkillsLearnedArr[1];

        public int FireballSkillLevel => SkillsLearnedArr[2];
            
        public int HasteSkillLevel => SkillsLearnedArr[3];

        public int ShieldSkillLevel => SkillsLearnedArr[4];
        
            
        public bool IsActionAvailable(ActionType action)
        {
            switch (action)
            {
                case ActionType.FrostBolt:
                    return FrozenSkillLevel == 5;
                case ActionType.Fireball:
                    return FireballSkillLevel == 5;
                case ActionType.Haste:
                    return HasteSkillLevel == 5;
                case ActionType.Shield:
                    return ShieldSkillLevel == 5;
                default:
                    return true;
            }
        }
    }
}
