using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public class Game {
        private readonly long randomSeed;
        private readonly int tickCount;
        private readonly double mapSize;
        private readonly bool isSkillsEnabled;
        private readonly bool isRawMessagesEnabled;
        private readonly double friendlyFireDamageFactor;
        private readonly double buildingDamageScoreFactor;
        private readonly double buildingEliminationScoreFactor;
        private readonly double minionDamageScoreFactor;
        private readonly double minionEliminationScoreFactor;
        private readonly double wizardDamageScoreFactor;
        private readonly double wizardEliminationScoreFactor;
        private readonly double teamWorkingScoreFactor;
        private readonly int victoryScore;
        private readonly double scoreGainRange;
        private readonly int rawMessageMaxLength;
        private readonly double rawMessageTransmissionSpeed;
        private readonly double wizardRadius;
        private readonly double wizardCastRange;
        private readonly double wizardVisionRange;
        private readonly double wizardForwardSpeed;
        private readonly double wizardBackwardSpeed;
        private readonly double wizardStrafeSpeed;
        private readonly int wizardBaseLife;
        private readonly int wizardLifeGrowthPerLevel;
        private readonly int wizardBaseMana;
        private readonly int wizardManaGrowthPerLevel;
        private readonly double wizardBaseLifeRegeneration;
        private readonly double wizardLifeRegenerationGrowthPerLevel;
        private readonly double wizardBaseManaRegeneration;
        private readonly double wizardManaRegenerationGrowthPerLevel;
        private readonly double wizardMaxTurnAngle;
        private readonly int wizardMaxResurrectionDelayTicks;
        private readonly int wizardMinResurrectionDelayTicks;
        private readonly int wizardActionCooldownTicks;
        private readonly int staffCooldownTicks;
        private readonly int magicMissileCooldownTicks;
        private readonly int frostBoltCooldownTicks;
        private readonly int fireballCooldownTicks;
        private readonly int hasteCooldownTicks;
        private readonly int shieldCooldownTicks;
        private readonly int magicMissileManacost;
        private readonly int frostBoltManacost;
        private readonly int fireballManacost;
        private readonly int hasteManacost;
        private readonly int shieldManacost;
        private readonly int staffDamage;
        private readonly double staffSector;
        private readonly double staffRange;
        private readonly int[] levelUpXpValues;
        private readonly double minionRadius;
        private readonly double minionVisionRange;
        private readonly double minionSpeed;
        private readonly double minionMaxTurnAngle;
        private readonly int minionLife;
        private readonly int factionMinionAppearanceIntervalTicks;
        private readonly int orcWoodcutterActionCooldownTicks;
        private readonly int orcWoodcutterDamage;
        private readonly double orcWoodcutterAttackSector;
        private readonly double orcWoodcutterAttackRange;
        private readonly int fetishBlowdartActionCooldownTicks;
        private readonly double fetishBlowdartAttackRange;
        private readonly double fetishBlowdartAttackSector;
        private readonly double bonusRadius;
        private readonly int bonusAppearanceIntervalTicks;
        private readonly int bonusScoreAmount;
        private readonly double dartRadius;
        private readonly double dartSpeed;
        private readonly int dartDirectDamage;
        private readonly double magicMissileRadius;
        private readonly double magicMissileSpeed;
        private readonly int magicMissileDirectDamage;
        private readonly double frostBoltRadius;
        private readonly double frostBoltSpeed;
        private readonly int frostBoltDirectDamage;
        private readonly double fireballRadius;
        private readonly double fireballSpeed;
        private readonly double fireballExplosionMaxDamageRange;
        private readonly double fireballExplosionMinDamageRange;
        private readonly int fireballExplosionMaxDamage;
        private readonly int fireballExplosionMinDamage;
        private readonly double guardianTowerRadius;
        private readonly double guardianTowerVisionRange;
        private readonly double guardianTowerLife;
        private readonly double guardianTowerAttackRange;
        private readonly int guardianTowerDamage;
        private readonly int guardianTowerCooldownTicks;
        private readonly double factionBaseRadius;
        private readonly double factionBaseVisionRange;
        private readonly double factionBaseLife;
        private readonly double factionBaseAttackRange;
        private readonly int factionBaseDamage;
        private readonly int factionBaseCooldownTicks;
        private readonly int burningDurationTicks;
        private readonly int burningSummaryDamage;
        private readonly int empoweredDurationTicks;
        private readonly double empoweredDamageFactor;
        private readonly int frozenDurationTicks;
        private readonly int hastenedDurationTicks;
        private readonly double hastenedBonusDurationFactor;
        private readonly double hastenedMovementBonusFactor;
        private readonly double hastenedRotationBonusFactor;
        private readonly int shieldedDurationTicks;
        private readonly double shieldedBonusDurationFactor;
        private readonly double shieldedDirectDamageAbsorptionFactor;
        private readonly double auraSkillRange;
        private readonly double rangeBonusPerSkillLevel;
        private readonly int magicalDamageBonusPerSkillLevel;
        private readonly int staffDamageBonusPerSkillLevel;
        private readonly double movementBonusFactorPerSkillLevel;
        private readonly int magicalDamageAbsorptionPerSkillLevel;

        public Game(long randomSeed, int tickCount, double mapSize, bool isSkillsEnabled, bool isRawMessagesEnabled,
                double friendlyFireDamageFactor, double buildingDamageScoreFactor,
                double buildingEliminationScoreFactor, double minionDamageScoreFactor,
                double minionEliminationScoreFactor, double wizardDamageScoreFactor,
                double wizardEliminationScoreFactor, double teamWorkingScoreFactor, int victoryScore,
                double scoreGainRange, int rawMessageMaxLength, double rawMessageTransmissionSpeed, double wizardRadius,
                double wizardCastRange, double wizardVisionRange, double wizardForwardSpeed, double wizardBackwardSpeed,
                double wizardStrafeSpeed, int wizardBaseLife, int wizardLifeGrowthPerLevel, int wizardBaseMana,
                int wizardManaGrowthPerLevel, double wizardBaseLifeRegeneration,
                double wizardLifeRegenerationGrowthPerLevel, double wizardBaseManaRegeneration,
                double wizardManaRegenerationGrowthPerLevel, double wizardMaxTurnAngle,
                int wizardMaxResurrectionDelayTicks, int wizardMinResurrectionDelayTicks, int wizardActionCooldownTicks,
                int staffCooldownTicks, int magicMissileCooldownTicks, int frostBoltCooldownTicks,
                int fireballCooldownTicks, int hasteCooldownTicks, int shieldCooldownTicks, int magicMissileManacost,
                int frostBoltManacost, int fireballManacost, int hasteManacost, int shieldManacost, int staffDamage,
                double staffSector, double staffRange, int[] levelUpXpValues, double minionRadius,
                double minionVisionRange, double minionSpeed, double minionMaxTurnAngle, int minionLife,
                int factionMinionAppearanceIntervalTicks, int orcWoodcutterActionCooldownTicks, int orcWoodcutterDamage,
                double orcWoodcutterAttackSector, double orcWoodcutterAttackRange,
                int fetishBlowdartActionCooldownTicks, double fetishBlowdartAttackRange,
                double fetishBlowdartAttackSector, double bonusRadius, int bonusAppearanceIntervalTicks,
                int bonusScoreAmount, double dartRadius, double dartSpeed, int dartDirectDamage,
                double magicMissileRadius, double magicMissileSpeed, int magicMissileDirectDamage,
                double frostBoltRadius, double frostBoltSpeed, int frostBoltDirectDamage, double fireballRadius,
                double fireballSpeed, double fireballExplosionMaxDamageRange, double fireballExplosionMinDamageRange,
                int fireballExplosionMaxDamage, int fireballExplosionMinDamage, double guardianTowerRadius,
                double guardianTowerVisionRange, double guardianTowerLife, double guardianTowerAttackRange,
                int guardianTowerDamage, int guardianTowerCooldownTicks, double factionBaseRadius,
                double factionBaseVisionRange, double factionBaseLife, double factionBaseAttackRange,
                int factionBaseDamage, int factionBaseCooldownTicks, int burningDurationTicks, int burningSummaryDamage,
                int empoweredDurationTicks, double empoweredDamageFactor, int frozenDurationTicks,
                int hastenedDurationTicks, double hastenedBonusDurationFactor, double hastenedMovementBonusFactor,
                double hastenedRotationBonusFactor, int shieldedDurationTicks, double shieldedBonusDurationFactor,
                double shieldedDirectDamageAbsorptionFactor, double auraSkillRange, double rangeBonusPerSkillLevel,
                int magicalDamageBonusPerSkillLevel, int staffDamageBonusPerSkillLevel,
                double movementBonusFactorPerSkillLevel, int magicalDamageAbsorptionPerSkillLevel) {
            this.randomSeed = randomSeed;
            this.tickCount = tickCount;
            this.mapSize = mapSize;
            this.isSkillsEnabled = isSkillsEnabled;
            this.isRawMessagesEnabled = isRawMessagesEnabled;
            this.friendlyFireDamageFactor = friendlyFireDamageFactor;
            this.buildingDamageScoreFactor = buildingDamageScoreFactor;
            this.buildingEliminationScoreFactor = buildingEliminationScoreFactor;
            this.minionDamageScoreFactor = minionDamageScoreFactor;
            this.minionEliminationScoreFactor = minionEliminationScoreFactor;
            this.wizardDamageScoreFactor = wizardDamageScoreFactor;
            this.wizardEliminationScoreFactor = wizardEliminationScoreFactor;
            this.teamWorkingScoreFactor = teamWorkingScoreFactor;
            this.victoryScore = victoryScore;
            this.scoreGainRange = scoreGainRange;
            this.rawMessageMaxLength = rawMessageMaxLength;
            this.rawMessageTransmissionSpeed = rawMessageTransmissionSpeed;
            this.wizardRadius = wizardRadius;
            this.wizardCastRange = wizardCastRange;
            this.wizardVisionRange = wizardVisionRange;
            this.wizardForwardSpeed = wizardForwardSpeed;
            this.wizardBackwardSpeed = wizardBackwardSpeed;
            this.wizardStrafeSpeed = wizardStrafeSpeed;
            this.wizardBaseLife = wizardBaseLife;
            this.wizardLifeGrowthPerLevel = wizardLifeGrowthPerLevel;
            this.wizardBaseMana = wizardBaseMana;
            this.wizardManaGrowthPerLevel = wizardManaGrowthPerLevel;
            this.wizardBaseLifeRegeneration = wizardBaseLifeRegeneration;
            this.wizardLifeRegenerationGrowthPerLevel = wizardLifeRegenerationGrowthPerLevel;
            this.wizardBaseManaRegeneration = wizardBaseManaRegeneration;
            this.wizardManaRegenerationGrowthPerLevel = wizardManaRegenerationGrowthPerLevel;
            this.wizardMaxTurnAngle = wizardMaxTurnAngle;
            this.wizardMaxResurrectionDelayTicks = wizardMaxResurrectionDelayTicks;
            this.wizardMinResurrectionDelayTicks = wizardMinResurrectionDelayTicks;
            this.wizardActionCooldownTicks = wizardActionCooldownTicks;
            this.staffCooldownTicks = staffCooldownTicks;
            this.magicMissileCooldownTicks = magicMissileCooldownTicks;
            this.frostBoltCooldownTicks = frostBoltCooldownTicks;
            this.fireballCooldownTicks = fireballCooldownTicks;
            this.hasteCooldownTicks = hasteCooldownTicks;
            this.shieldCooldownTicks = shieldCooldownTicks;
            this.magicMissileManacost = magicMissileManacost;
            this.frostBoltManacost = frostBoltManacost;
            this.fireballManacost = fireballManacost;
            this.hasteManacost = hasteManacost;
            this.shieldManacost = shieldManacost;
            this.staffDamage = staffDamage;
            this.staffSector = staffSector;
            this.staffRange = staffRange;

            this.levelUpXpValues = new int[levelUpXpValues.Length];
            Array.Copy(levelUpXpValues, this.levelUpXpValues, levelUpXpValues.Length);

            this.minionRadius = minionRadius;
            this.minionVisionRange = minionVisionRange;
            this.minionSpeed = minionSpeed;
            this.minionMaxTurnAngle = minionMaxTurnAngle;
            this.minionLife = minionLife;
            this.factionMinionAppearanceIntervalTicks = factionMinionAppearanceIntervalTicks;
            this.orcWoodcutterActionCooldownTicks = orcWoodcutterActionCooldownTicks;
            this.orcWoodcutterDamage = orcWoodcutterDamage;
            this.orcWoodcutterAttackSector = orcWoodcutterAttackSector;
            this.orcWoodcutterAttackRange = orcWoodcutterAttackRange;
            this.fetishBlowdartActionCooldownTicks = fetishBlowdartActionCooldownTicks;
            this.fetishBlowdartAttackRange = fetishBlowdartAttackRange;
            this.fetishBlowdartAttackSector = fetishBlowdartAttackSector;
            this.bonusRadius = bonusRadius;
            this.bonusAppearanceIntervalTicks = bonusAppearanceIntervalTicks;
            this.bonusScoreAmount = bonusScoreAmount;
            this.dartRadius = dartRadius;
            this.dartSpeed = dartSpeed;
            this.dartDirectDamage = dartDirectDamage;
            this.magicMissileRadius = magicMissileRadius;
            this.magicMissileSpeed = magicMissileSpeed;
            this.magicMissileDirectDamage = magicMissileDirectDamage;
            this.frostBoltRadius = frostBoltRadius;
            this.frostBoltSpeed = frostBoltSpeed;
            this.frostBoltDirectDamage = frostBoltDirectDamage;
            this.fireballRadius = fireballRadius;
            this.fireballSpeed = fireballSpeed;
            this.fireballExplosionMaxDamageRange = fireballExplosionMaxDamageRange;
            this.fireballExplosionMinDamageRange = fireballExplosionMinDamageRange;
            this.fireballExplosionMaxDamage = fireballExplosionMaxDamage;
            this.fireballExplosionMinDamage = fireballExplosionMinDamage;
            this.guardianTowerRadius = guardianTowerRadius;
            this.guardianTowerVisionRange = guardianTowerVisionRange;
            this.guardianTowerLife = guardianTowerLife;
            this.guardianTowerAttackRange = guardianTowerAttackRange;
            this.guardianTowerDamage = guardianTowerDamage;
            this.guardianTowerCooldownTicks = guardianTowerCooldownTicks;
            this.factionBaseRadius = factionBaseRadius;
            this.factionBaseVisionRange = factionBaseVisionRange;
            this.factionBaseLife = factionBaseLife;
            this.factionBaseAttackRange = factionBaseAttackRange;
            this.factionBaseDamage = factionBaseDamage;
            this.factionBaseCooldownTicks = factionBaseCooldownTicks;
            this.burningDurationTicks = burningDurationTicks;
            this.burningSummaryDamage = burningSummaryDamage;
            this.empoweredDurationTicks = empoweredDurationTicks;
            this.empoweredDamageFactor = empoweredDamageFactor;
            this.frozenDurationTicks = frozenDurationTicks;
            this.hastenedDurationTicks = hastenedDurationTicks;
            this.hastenedBonusDurationFactor = hastenedBonusDurationFactor;
            this.hastenedMovementBonusFactor = hastenedMovementBonusFactor;
            this.hastenedRotationBonusFactor = hastenedRotationBonusFactor;
            this.shieldedDurationTicks = shieldedDurationTicks;
            this.shieldedBonusDurationFactor = shieldedBonusDurationFactor;
            this.shieldedDirectDamageAbsorptionFactor = shieldedDirectDamageAbsorptionFactor;
            this.auraSkillRange = auraSkillRange;
            this.rangeBonusPerSkillLevel = rangeBonusPerSkillLevel;
            this.magicalDamageBonusPerSkillLevel = magicalDamageBonusPerSkillLevel;
            this.staffDamageBonusPerSkillLevel = staffDamageBonusPerSkillLevel;
            this.movementBonusFactorPerSkillLevel = movementBonusFactorPerSkillLevel;
            this.magicalDamageAbsorptionPerSkillLevel = magicalDamageAbsorptionPerSkillLevel;
        }

        public long RandomSeed => randomSeed;
        public int TickCount => tickCount;
        public double MapSize => mapSize;
        public bool IsSkillsEnabled => isSkillsEnabled;
        public bool IsRawMessagesEnabled => isRawMessagesEnabled;
        public double FriendlyFireDamageFactor => friendlyFireDamageFactor;
        public double BuildingDamageScoreFactor => buildingDamageScoreFactor;
        public double BuildingEliminationScoreFactor => buildingEliminationScoreFactor;
        public double MinionDamageScoreFactor => minionDamageScoreFactor;
        public double MinionEliminationScoreFactor => minionEliminationScoreFactor;
        public double WizardDamageScoreFactor => wizardDamageScoreFactor;
        public double WizardEliminationScoreFactor => wizardEliminationScoreFactor;
        public double TeamWorkingScoreFactor => teamWorkingScoreFactor;
        public int VictoryScore => victoryScore;
        public double ScoreGainRange => scoreGainRange;
        public int RawMessageMaxLength => rawMessageMaxLength;
        public double RawMessageTransmissionSpeed => rawMessageTransmissionSpeed;
        public double WizardRadius => wizardRadius;
        public double WizardCastRange => wizardCastRange;
        public double WizardVisionRange => wizardVisionRange;
        public double WizardForwardSpeed => wizardForwardSpeed;
        public double WizardBackwardSpeed => wizardBackwardSpeed;
        public double WizardStrafeSpeed => wizardStrafeSpeed;
        public int WizardBaseLife => wizardBaseLife;
        public int WizardLifeGrowthPerLevel => wizardLifeGrowthPerLevel;
        public int WizardBaseMana => wizardBaseMana;
        public int WizardManaGrowthPerLevel => wizardManaGrowthPerLevel;
        public double WizardBaseLifeRegeneration => wizardBaseLifeRegeneration;
        public double WizardLifeRegenerationGrowthPerLevel => wizardLifeRegenerationGrowthPerLevel;
        public double WizardBaseManaRegeneration => wizardBaseManaRegeneration;
        public double WizardManaRegenerationGrowthPerLevel => wizardManaRegenerationGrowthPerLevel;
        public double WizardMaxTurnAngle => wizardMaxTurnAngle;
        public int WizardMaxResurrectionDelayTicks => wizardMaxResurrectionDelayTicks;
        public int WizardMinResurrectionDelayTicks => wizardMinResurrectionDelayTicks;
        public int WizardActionCooldownTicks => wizardActionCooldownTicks;
        public int StaffCooldownTicks => staffCooldownTicks;
        public int MagicMissileCooldownTicks => magicMissileCooldownTicks;
        public int FrostBoltCooldownTicks => frostBoltCooldownTicks;
        public int FireballCooldownTicks => fireballCooldownTicks;
        public int HasteCooldownTicks => hasteCooldownTicks;
        public int ShieldCooldownTicks => shieldCooldownTicks;
        public int MagicMissileManacost => magicMissileManacost;
        public int FrostBoltManacost => frostBoltManacost;
        public int FireballManacost => fireballManacost;
        public int HasteManacost => hasteManacost;
        public int ShieldManacost => shieldManacost;
        public int StaffDamage => staffDamage;
        public double StaffSector => staffSector;
        public double StaffRange => staffRange;

        public int[] LevelUpXpValues {
            get {
                if (this.levelUpXpValues == null) {
                    return null;
                }

                int[] levelUpXpValues = new int[this.levelUpXpValues.Length];
                Array.Copy(this.levelUpXpValues, levelUpXpValues, this.levelUpXpValues.Length);
                return levelUpXpValues;
            }
        }

        public double MinionRadius => minionRadius;
        public double MinionVisionRange => minionVisionRange;
        public double MinionSpeed => minionSpeed;
        public double MinionMaxTurnAngle => minionMaxTurnAngle;
        public int MinionLife => minionLife;
        public int FactionMinionAppearanceIntervalTicks => factionMinionAppearanceIntervalTicks;
        public int OrcWoodcutterActionCooldownTicks => orcWoodcutterActionCooldownTicks;
        public int OrcWoodcutterDamage => orcWoodcutterDamage;
        public double OrcWoodcutterAttackSector => orcWoodcutterAttackSector;
        public double OrcWoodcutterAttackRange => orcWoodcutterAttackRange;
        public int FetishBlowdartActionCooldownTicks => fetishBlowdartActionCooldownTicks;
        public double FetishBlowdartAttackRange => fetishBlowdartAttackRange;
        public double FetishBlowdartAttackSector => fetishBlowdartAttackSector;
        public double BonusRadius => bonusRadius;
        public int BonusAppearanceIntervalTicks => bonusAppearanceIntervalTicks;
        public int BonusScoreAmount => bonusScoreAmount;
        public double DartRadius => dartRadius;
        public double DartSpeed => dartSpeed;
        public int DartDirectDamage => dartDirectDamage;
        public double MagicMissileRadius => magicMissileRadius;
        public double MagicMissileSpeed => magicMissileSpeed;
        public int MagicMissileDirectDamage => magicMissileDirectDamage;
        public double FrostBoltRadius => frostBoltRadius;
        public double FrostBoltSpeed => frostBoltSpeed;
        public int FrostBoltDirectDamage => frostBoltDirectDamage;
        public double FireballRadius => fireballRadius;
        public double FireballSpeed => fireballSpeed;
        public double FireballExplosionMaxDamageRange => fireballExplosionMaxDamageRange;
        public double FireballExplosionMinDamageRange => fireballExplosionMinDamageRange;
        public int FireballExplosionMaxDamage => fireballExplosionMaxDamage;
        public int FireballExplosionMinDamage => fireballExplosionMinDamage;
        public double GuardianTowerRadius => guardianTowerRadius;
        public double GuardianTowerVisionRange => guardianTowerVisionRange;
        public double GuardianTowerLife => guardianTowerLife;
        public double GuardianTowerAttackRange => guardianTowerAttackRange;
        public int GuardianTowerDamage => guardianTowerDamage;
        public int GuardianTowerCooldownTicks => guardianTowerCooldownTicks;
        public double FactionBaseRadius => factionBaseRadius;
        public double FactionBaseVisionRange => factionBaseVisionRange;
        public double FactionBaseLife => factionBaseLife;
        public double FactionBaseAttackRange => factionBaseAttackRange;
        public int FactionBaseDamage => factionBaseDamage;
        public int FactionBaseCooldownTicks => factionBaseCooldownTicks;
        public int BurningDurationTicks => burningDurationTicks;
        public int BurningSummaryDamage => burningSummaryDamage;
        public int EmpoweredDurationTicks => empoweredDurationTicks;
        public double EmpoweredDamageFactor => empoweredDamageFactor;
        public int FrozenDurationTicks => frozenDurationTicks;
        public int HastenedDurationTicks => hastenedDurationTicks;
        public double HastenedBonusDurationFactor => hastenedBonusDurationFactor;
        public double HastenedMovementBonusFactor => hastenedMovementBonusFactor;
        public double HastenedRotationBonusFactor => hastenedRotationBonusFactor;
        public int ShieldedDurationTicks => shieldedDurationTicks;
        public double ShieldedBonusDurationFactor => shieldedBonusDurationFactor;
        public double ShieldedDirectDamageAbsorptionFactor => shieldedDirectDamageAbsorptionFactor;
        public double AuraSkillRange => auraSkillRange;
        public double RangeBonusPerSkillLevel => rangeBonusPerSkillLevel;
        public int MagicalDamageBonusPerSkillLevel => magicalDamageBonusPerSkillLevel;
        public int StaffDamageBonusPerSkillLevel => staffDamageBonusPerSkillLevel;
        public double MovementBonusFactorPerSkillLevel => movementBonusFactorPerSkillLevel;
        public int MagicalDamageAbsorptionPerSkillLevel => magicalDamageAbsorptionPerSkillLevel;
    }
}