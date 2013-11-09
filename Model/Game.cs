namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model
{
    public sealed class Game
    {
        private readonly int moveCount;

        private readonly int lastPlayerEliminationScore;
        private readonly int playerEliminationScore;
        private readonly int trooperEliminationScore;
        private readonly double trooperDamageScoreFactor;

        private readonly int stanceChangeCost;
        private readonly int standingMoveCost;
        private readonly int kneelingMoveCost;
        private readonly int proneMoveCost;

        private readonly int commanderAuraBonusActionPoints;
        private readonly double commanderAuraRange;
        
        private readonly int commanderRequestEnemyDispositionCost;
        private readonly int commanderRequestEnemyDispositionMaxOffset;

        private readonly int fieldMedicHealCost;
        private readonly int fieldMedicHealBonusHitpoints;
        private readonly int fieldMedicHealSelfBonusHitpoints;

        private readonly double sniperStandingStealthBonus;
        private readonly double sniperKneelingStealthBonus;
        private readonly double sniperProneStealthBonus;

        private readonly double sniperStandingShootingRangeBonus;
        private readonly double sniperKneelingShootingRangeBonus;
        private readonly double sniperProneShootingRangeBonus;

        private readonly double scoutStealthBonusNegation;

        private readonly int grenadeThrowCost;
        private readonly double grenadeThrowRange;
        private readonly int grenadeDirectDamage;
        private readonly int grenadeCollateralDamage;

        private readonly int medikitUseCost;
        private readonly int medikitBonusHitpoints;
        private readonly int medikitHealSelfBonusHitpoints;

        private readonly int fieldRationEatCost;
        private readonly int fieldRationBonusActionPoints;

        public Game(int moveCount,
            int lastPlayerEliminationScore, int playerEliminationScore,
            int trooperEliminationScore, double trooperDamageScoreFactor,
            int stanceChangeCost, int standingMoveCost, int kneelingMoveCost, int proneMoveCost,
            int commanderAuraBonusActionPoints, double commanderAuraRange,
            int commanderRequestEnemyDispositionCost, int commanderRequestEnemyDispositionMaxOffset,
            int fieldMedicHealCost, int fieldMedicHealBonusHitpoints, int fieldMedicHealSelfBonusHitpoints,
            double sniperStandingStealthBonus, double sniperKneelingStealthBonus, double sniperProneStealthBonus,
            double sniperStandingShootingRangeBonus, double sniperKneelingShootingRangeBonus,
            double sniperProneShootingRangeBonus, double scoutStealthBonusNegation,
            int grenadeThrowCost, double grenadeThrowRange, int grenadeDirectDamage, int grenadeCollateralDamage,
            int medikitUseCost, int medikitBonusHitpoints, int medikitHealSelfBonusHitpoints,
            int fieldRationEatCost, int fieldRationBonusActionPoints)
        {
            this.moveCount = moveCount;
            this.lastPlayerEliminationScore = lastPlayerEliminationScore;
            this.playerEliminationScore = playerEliminationScore;
            this.trooperEliminationScore = trooperEliminationScore;
            this.trooperDamageScoreFactor = trooperDamageScoreFactor;
            this.stanceChangeCost = stanceChangeCost;
            this.standingMoveCost = standingMoveCost;
            this.kneelingMoveCost = kneelingMoveCost;
            this.proneMoveCost = proneMoveCost;
            this.commanderAuraBonusActionPoints = commanderAuraBonusActionPoints;
            this.commanderAuraRange = commanderAuraRange;
            this.commanderRequestEnemyDispositionCost = commanderRequestEnemyDispositionCost;
            this.commanderRequestEnemyDispositionMaxOffset = commanderRequestEnemyDispositionMaxOffset;
            this.fieldMedicHealCost = fieldMedicHealCost;
            this.fieldMedicHealBonusHitpoints = fieldMedicHealBonusHitpoints;
            this.fieldMedicHealSelfBonusHitpoints = fieldMedicHealSelfBonusHitpoints;
            this.sniperStandingStealthBonus = sniperStandingStealthBonus;
            this.sniperKneelingStealthBonus = sniperKneelingStealthBonus;
            this.sniperProneStealthBonus = sniperProneStealthBonus;
            this.sniperStandingShootingRangeBonus = sniperStandingShootingRangeBonus;
            this.sniperKneelingShootingRangeBonus = sniperKneelingShootingRangeBonus;
            this.sniperProneShootingRangeBonus = sniperProneShootingRangeBonus;
            this.scoutStealthBonusNegation = scoutStealthBonusNegation;
            this.grenadeThrowCost = grenadeThrowCost;
            this.grenadeThrowRange = grenadeThrowRange;
            this.grenadeDirectDamage = grenadeDirectDamage;
            this.grenadeCollateralDamage = grenadeCollateralDamage;
            this.medikitUseCost = medikitUseCost;
            this.medikitBonusHitpoints = medikitBonusHitpoints;
            this.medikitHealSelfBonusHitpoints = medikitHealSelfBonusHitpoints;
            this.fieldRationEatCost = fieldRationEatCost;
            this.fieldRationBonusActionPoints = fieldRationBonusActionPoints;
        }

        public int FieldRationBonusActionPoints
        {
            get { return fieldRationBonusActionPoints; }
        }

        public int FieldRationEatCost
        {
            get { return fieldRationEatCost; }
        }

        public int MedikitHealSelfBonusHitpoints
        {
            get { return medikitHealSelfBonusHitpoints; }
        }

        public int MedikitBonusHitpoints
        {
            get { return medikitBonusHitpoints; }
        }

        public int MedikitUseCost
        {
            get { return medikitUseCost; }
        }

        public int GrenadeCollateralDamage
        {
            get { return grenadeCollateralDamage; }
        }

        public int GrenadeDirectDamage
        {
            get { return grenadeDirectDamage; }
        }

        public double GrenadeThrowRange
        {
            get { return grenadeThrowRange; }
        }

        public int GrenadeThrowCost
        {
            get { return grenadeThrowCost; }
        }

        public double ScoutStealthBonusNegation
        {
            get { return scoutStealthBonusNegation; }
        }

        public double SniperProneShootingRangeBonus
        {
            get { return sniperProneShootingRangeBonus; }
        }

        public double SniperKneelingShootingRangeBonus
        {
            get { return sniperKneelingShootingRangeBonus; }
        }

        public double SniperStandingShootingRangeBonus
        {
            get { return sniperStandingShootingRangeBonus; }
        }

        public double SniperProneStealthBonus
        {
            get { return sniperProneStealthBonus; }
        }

        public double SniperKneelingStealthBonus
        {
            get { return sniperKneelingStealthBonus; }
        }

        public double SniperStandingStealthBonus
        {
            get { return sniperStandingStealthBonus; }
        }

        public int FieldMedicHealSelfBonusHitpoints
        {
            get { return fieldMedicHealSelfBonusHitpoints; }
        }

        public int FieldMedicHealBonusHitpoints
        {
            get { return fieldMedicHealBonusHitpoints; }
        }

        public int FieldMedicHealCost
        {
            get { return fieldMedicHealCost; }
        }

        public double CommanderAuraRange
        {
            get { return commanderAuraRange; }
        }

        public int CommanderAuraBonusActionPoints
        {
            get { return commanderAuraBonusActionPoints; }
        }
        
        public int CommanderRequestEnemyDispositionCost
        {
            get { return commanderRequestEnemyDispositionCost; }
        }
        
        public int CommanderRequestEnemyDispositionMaxOffset
        {
            get { return commanderRequestEnemyDispositionMaxOffset; }
        }

        public int ProneMoveCost
        {
            get { return proneMoveCost; }
        }

        public int KneelingMoveCost
        {
            get { return kneelingMoveCost; }
        }

        public int StandingMoveCost
        {
            get { return standingMoveCost; }
        }

        public int StanceChangeCost
        {
            get { return stanceChangeCost; }
        }

        public double TrooperDamageScoreFactor
        {
            get { return trooperDamageScoreFactor; }
        }

        public int TrooperEliminationScore
        {
            get { return trooperEliminationScore; }
        }

        public int PlayerEliminationScore
        {
            get { return playerEliminationScore; }
        }

        public int LastPlayerEliminationScore
        {
            get { return lastPlayerEliminationScore; }
        }

        public int MoveCount
        {
            get { return moveCount; }
        }
    }
}