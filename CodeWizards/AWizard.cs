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

        public int Level;
        public int Xp; // TODO: не используется
        public double Mana;
        public int MaxMana;

        public int[] SkillsLearnedArr;
        public int[] SkillsFactorsArr; // какие у меня умения (без учета аур)
        public int[] AurasFactorsArr; // какие на меня действуют ауры (в т.ч. мои)

        public bool IsBesieded;

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

            MaxMana = unit.MaxMana;
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
            MaxMana = unit.MaxMana;
            IsBesieded = unit.IsBesieded;
        }

        public override void SkipTick()
        {
            base.SkipTick();
            for (var i = 0; i < RemainingCooldownTicksByAction.Length; i++)
                Utility.Dec(ref RemainingCooldownTicksByAction[i]);

            if (Life > Const.Eps)
            {
                Mana += MyStrategy.Game.WizardBaseManaRegeneration +
                        MyStrategy.Game.WizardManaRegenerationGrowthPerLevel*Level;
                if (Mana > MaxMana)
                    Mana = MaxMana;

                Life += MyStrategy.Game.WizardBaseLifeRegeneration +
                        MyStrategy.Game.WizardLifeRegenerationGrowthPerLevel*Level;
                if (Life > MaxLife)
                    Life = MaxLife;
            }
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

        public void Move(double speed, double strafeSpeed)
        {
            var myDir = Point.ByAngle(Angle);
            var myStrafeDir = new Point(-myDir.Y, myDir.X);
            var d = new Point(strafeSpeed, speed);
            var move = myDir * d.Y + myStrafeDir * d.X;
            X += move.X;
            Y += move.Y;
        }

        public virtual bool MoveTo(Point to, Point turnTo, Func<AWizard, bool> checkCollisions = null)
        {
            var result = true;
            var turn = RemainingFrozen == 0 && turnTo != null ? GetAngleTo(turnTo) : 0;

            if (RemainingFrozen == 0 && to != null && !Utility.PointsEqual(this, to))
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
                    result = false;
                }
            }

            Angle += Utility.EnsureInterval(turn, MaxTurnAngle);

            SkipTick();
            return result;
        }

        public override void EthalonMove(ACircularUnit target)
        {
            MoveTo(target, target, w => !w.IntersectsWith(target));
        }

        public bool CanUseMagicMissile(bool checkCooldown = true)
        {
            if (checkCooldown)
                if (RemainingMagicMissileCooldownTicks > 0 || RemainingActionCooldownTicks > 0 || Mana < MyStrategy.Game.MagicMissileManacost)
                    return false;
            
            if (RemainingFrozen > 0)
                return false;

            return true;
        }

        public bool EthalonCanCastMagicMissile(ACircularUnit opp, bool checkCooldown = true, bool checkAngle = true)
        {
            if (!CanUseMagicMissile(checkCooldown))
                return false;

            var tmp = Angle;
            if (!checkAngle)
                Angle += GetAngleTo(opp); // поворачиваем, чтобы угол до цели был 0
            var ret = _ethalonCanCastMagicMissile(opp, checkCooldown);
            if (!checkAngle)
                Angle = tmp;
            return ret;
        }

        public bool _ethalonCanCastMagicMissile(ACircularUnit opp, bool checkCooldown)
        {
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
                if (CheckProjectileCantDodge(proj, opp as ACombatUnit))
                    return true;
            }
            return false;
        }

        public bool CheckProjectileCantDodge(AProjectile proj, ACombatUnit opp)
        {
            return (opp is AWizard ? Utility.Range(-Math.PI/2, Math.PI/2, 4) : new[] {0.0}).All(changeAngle =>
            {
                var path = proj.Emulate(opp, changeAngle);
                return path.Any(x => x.State == AProjectile.ProjectilePathState.Shot && x.Target.Faction != Faction);
            });
        }

        public override bool EthalonCanHit(ACircularUnit target, bool checkCooldown = true)
        {
            if (CanStaffAttack(target, checkCooldown))
                return true;

            return EthalonCanCastMagicMissile(target, checkCooldown);
        }

        public bool CanUseStaff(bool checkCooldown = true)
        {
            if (RemainingFrozen > 0)
                return false;
            if (checkCooldown && (RemainingActionCooldownTicks > 0 || RemainingStaffCooldownTicks > 0))
                return false;
            return true;
        }

        public bool CanStaffAttack(ACircularUnit unit, bool checkCooldown = true)
        {
            if (!CanUseStaff(checkCooldown))
                return false;

            if (GetDistanceTo2(unit) > Geom.Sqr(MyStrategy.Game.StaffRange + unit.Radius))
                return false;
            if (Math.Abs(GetAngleTo(unit)) > MyStrategy.Game.StaffSector/2)
                return false;
            return true;
        }

        public bool CanUseFrostBolt(bool checkCooldown = true)
        {
            if (FrozenSkillLevel != 5)
                return false;

            if (checkCooldown)
                if (RemainingFrostBoltCooldownTicks > 0 || RemainingActionCooldownTicks > 0 || Mana < MyStrategy.Game.FrostBoltManacost)
                    return false;

            if (RemainingFrozen > 0)
                return false;

            return true;
        }

        public ACircularUnit[] GetStaffAttacked(IEnumerable<ACircularUnit> candidates)
        {
            return candidates
                .Where(x => CanStaffAttack(x))
                .ToArray();
        }

        public int RemainingStaffCooldownTicks
        {
            get { return RemainingCooldownTicksByAction[(int) ActionType.Staff]; }
            set { RemainingCooldownTicksByAction[(int)ActionType.Staff] = value; }
        }

        public int RemainingMagicMissileCooldownTicks
        {
            get { return RemainingCooldownTicksByAction[(int) ActionType.MagicMissile]; }
            set { RemainingCooldownTicksByAction[(int)ActionType.MagicMissile] = value; }
        }

        public int RemainingFrostBoltCooldownTicks
        {
            get { return RemainingCooldownTicksByAction[(int) ActionType.FrostBolt]; }
            set { RemainingCooldownTicksByAction[(int)ActionType.FrostBolt] = value; }
        }

        public int RemainingFireballCooldownTicks => RemainingCooldownTicksByAction[(int)ActionType.Fireball];

        public int RemainingHasteCooldownTicks => RemainingCooldownTicksByAction[(int)ActionType.Haste];

        public int RemainingShieldCooldownTicks => RemainingCooldownTicksByAction[(int)ActionType.Shield];


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

        public double FireballMaxDamage => MyStrategy.Game.FireballExplosionMaxDamage * _getDamageFactor() + _getCastDamageAddition();

        public double FireballMinDamage => MyStrategy.Game.FireballExplosionMinDamage * _getDamageFactor() + _getCastDamageAddition();

        private double _getHastenedFactor()
        {
            return 1 
                + (RemainingHastened > 0 ? MyStrategy.Game.HastenedMovementBonusFactor : 0) 
                + (SkillsFactorsArr[3] + AurasFactorsArr[3]) * MyStrategy.Game.MovementBonusFactorPerSkillLevel;
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
