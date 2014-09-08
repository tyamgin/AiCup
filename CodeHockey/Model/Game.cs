using System;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model {
    public class Game {
        private readonly long randomSeed;
        private readonly int tickCount;
        private readonly double worldWidth;
        private readonly double worldHeight;
        private readonly double goalNetTop;
        private readonly double goalNetWidth;
        private readonly double goalNetHeight;
        private readonly double rinkTop;
        private readonly double rinkLeft;
        private readonly double rinkBottom;
        private readonly double rinkRight;
        private readonly int afterGoalStateTickCount;
        private readonly int overtimeTickCount;
        private readonly int defaultActionCooldownTicks;
        private readonly int swingActionCooldownTicks;
        private readonly int cancelStrikeActionCooldownTicks;
        private readonly int actionCooldownTicksAfterLosingPuck;
        private readonly double stickLength;
        private readonly double stickSector;
        private readonly double passSector;
        private readonly int hockeyistAttributeBaseValue;
        private readonly double minActionChance;
        private readonly double maxActionChance;
        private readonly double strikeAngleDeviation;
        private readonly double passAngleDeviation;
        private readonly double pickUpPuckBaseChance;
        private readonly double takePuckAwayBaseChance;
        private readonly int maxEffectiveSwingTicks;
        private readonly double strikePowerBaseFactor;
        private readonly double strikePowerGrowthFactor;
        private readonly double strikePuckBaseChance;
        private readonly double knockdownChanceFactor;
        private readonly double knockdownTicksFactor;
        private readonly double maxSpeedToAllowSubstitute;
        private readonly double substitutionAreaHeight;
        private readonly double passPowerFactor;
        private readonly double hockeyistMaxStamina;
        private readonly double activeHockeyistStaminaGrowthPerTick;
        private readonly double restingHockeyistStaminaGrowthPerTick;
        private readonly double zeroStaminaHockeyistEffectivenessFactor;
        private readonly double speedUpStaminaCostFactor;
        private readonly double turnStaminaCostFactor;
        private readonly double takePuckStaminaCost;
        private readonly double swingStaminaCost;
        private readonly double strikeStaminaBaseCost;
        private readonly double strikeStaminaCostGrowthFactor;
        private readonly double cancelStrikeStaminaCost;
        private readonly double passStaminaCost;
        private readonly double goalieMaxSpeed;
        private readonly double hockeyistMaxSpeed;
        private readonly double struckHockeyistInitialSpeedFactor;
        private readonly double hockeyistSpeedUpFactor;
        private readonly double hockeyistSpeedDownFactor;
        private readonly double hockeyistTurnAngleFactor;
        private readonly int versatileHockeyistStrength;
        private readonly int versatileHockeyistEndurance;
        private readonly int versatileHockeyistDexterity;
        private readonly int versatileHockeyistAgility;
        private readonly int forwardHockeyistStrength;
        private readonly int forwardHockeyistEndurance;
        private readonly int forwardHockeyistDexterity;
        private readonly int forwardHockeyistAgility;
        private readonly int defencemanHockeyistStrength;
        private readonly int defencemanHockeyistEndurance;
        private readonly int defencemanHockeyistDexterity;
        private readonly int defencemanHockeyistAgility;
        private readonly int minRandomHockeyistParameter;
        private readonly int maxRandomHockeyistParameter;
        private readonly double struckPuckInitialSpeedFactor;
        private readonly double puckBindingRange;

        public Game(long randomSeed, int tickCount, double worldWidth, double worldHeight, double goalNetTop,
                double goalNetWidth, double goalNetHeight, double rinkTop, double rinkLeft, double rinkBottom,
                double rinkRight, int afterGoalStateTickCount, int overtimeTickCount, int defaultActionCooldownTicks,
                int swingActionCooldownTicks, int cancelStrikeActionCooldownTicks,
                int actionCooldownTicksAfterLosingPuck, double stickLength, double stickSector, double passSector,
                int hockeyistAttributeBaseValue, double minActionChance, double maxActionChance,
                double strikeAngleDeviation, double passAngleDeviation, double pickUpPuckBaseChance,
                double takePuckAwayBaseChance, int maxEffectiveSwingTicks, double strikePowerBaseFactor,
                double strikePowerGrowthFactor, double strikePuckBaseChance, double knockdownChanceFactor,
                double knockdownTicksFactor, double maxSpeedToAllowSubstitute, double substitutionAreaHeight,
                double passPowerFactor, double hockeyistMaxStamina, double activeHockeyistStaminaGrowthPerTick,
                double restingHockeyistStaminaGrowthPerTick, double zeroStaminaHockeyistEffectivenessFactor,
                double speedUpStaminaCostFactor, double turnStaminaCostFactor, double takePuckStaminaCost,
                double swingStaminaCost, double strikeStaminaBaseCost, double strikeStaminaCostGrowthFactor,
                double cancelStrikeStaminaCost, double passStaminaCost, double goalieMaxSpeed, double hockeyistMaxSpeed,
                double struckHockeyistInitialSpeedFactor, double hockeyistSpeedUpFactor,
                double hockeyistSpeedDownFactor, double hockeyistTurnAngleFactor, int versatileHockeyistStrength,
                int versatileHockeyistEndurance, int versatileHockeyistDexterity, int versatileHockeyistAgility,
                int forwardHockeyistStrength, int forwardHockeyistEndurance, int forwardHockeyistDexterity,
                int forwardHockeyistAgility, int defencemanHockeyistStrength, int defencemanHockeyistEndurance,
                int defencemanHockeyistDexterity, int defencemanHockeyistAgility, int minRandomHockeyistParameter,
                int maxRandomHockeyistParameter, double struckPuckInitialSpeedFactor, double puckBindingRange) {
            this.randomSeed = randomSeed;
            this.tickCount = tickCount;
            this.worldWidth = worldWidth;
            this.worldHeight = worldHeight;
            this.goalNetTop = goalNetTop;
            this.goalNetWidth = goalNetWidth;
            this.goalNetHeight = goalNetHeight;
            this.rinkTop = rinkTop;
            this.rinkLeft = rinkLeft;
            this.rinkBottom = rinkBottom;
            this.rinkRight = rinkRight;
            this.afterGoalStateTickCount = afterGoalStateTickCount;
            this.overtimeTickCount = overtimeTickCount;
            this.defaultActionCooldownTicks = defaultActionCooldownTicks;
            this.swingActionCooldownTicks = swingActionCooldownTicks;
            this.cancelStrikeActionCooldownTicks = cancelStrikeActionCooldownTicks;
            this.actionCooldownTicksAfterLosingPuck = actionCooldownTicksAfterLosingPuck;
            this.stickLength = stickLength;
            this.stickSector = stickSector;
            this.passSector = passSector;
            this.hockeyistAttributeBaseValue = hockeyistAttributeBaseValue;
            this.minActionChance = minActionChance;
            this.maxActionChance = maxActionChance;
            this.strikeAngleDeviation = strikeAngleDeviation;
            this.passAngleDeviation = passAngleDeviation;
            this.pickUpPuckBaseChance = pickUpPuckBaseChance;
            this.takePuckAwayBaseChance = takePuckAwayBaseChance;
            this.maxEffectiveSwingTicks = maxEffectiveSwingTicks;
            this.strikePowerBaseFactor = strikePowerBaseFactor;
            this.strikePowerGrowthFactor = strikePowerGrowthFactor;
            this.strikePuckBaseChance = strikePuckBaseChance;
            this.knockdownChanceFactor = knockdownChanceFactor;
            this.knockdownTicksFactor = knockdownTicksFactor;
            this.maxSpeedToAllowSubstitute = maxSpeedToAllowSubstitute;
            this.substitutionAreaHeight = substitutionAreaHeight;
            this.passPowerFactor = passPowerFactor;
            this.hockeyistMaxStamina = hockeyistMaxStamina;
            this.activeHockeyistStaminaGrowthPerTick = activeHockeyistStaminaGrowthPerTick;
            this.restingHockeyistStaminaGrowthPerTick = restingHockeyistStaminaGrowthPerTick;
            this.zeroStaminaHockeyistEffectivenessFactor = zeroStaminaHockeyistEffectivenessFactor;
            this.speedUpStaminaCostFactor = speedUpStaminaCostFactor;
            this.turnStaminaCostFactor = turnStaminaCostFactor;
            this.takePuckStaminaCost = takePuckStaminaCost;
            this.swingStaminaCost = swingStaminaCost;
            this.strikeStaminaBaseCost = strikeStaminaBaseCost;
            this.strikeStaminaCostGrowthFactor = strikeStaminaCostGrowthFactor;
            this.cancelStrikeStaminaCost = cancelStrikeStaminaCost;
            this.passStaminaCost = passStaminaCost;
            this.goalieMaxSpeed = goalieMaxSpeed;
            this.hockeyistMaxSpeed = hockeyistMaxSpeed;
            this.struckHockeyistInitialSpeedFactor = struckHockeyistInitialSpeedFactor;
            this.hockeyistSpeedUpFactor = hockeyistSpeedUpFactor;
            this.hockeyistSpeedDownFactor = hockeyistSpeedDownFactor;
            this.hockeyistTurnAngleFactor = hockeyistTurnAngleFactor;
            this.versatileHockeyistStrength = versatileHockeyistStrength;
            this.versatileHockeyistEndurance = versatileHockeyistEndurance;
            this.versatileHockeyistDexterity = versatileHockeyistDexterity;
            this.versatileHockeyistAgility = versatileHockeyistAgility;
            this.forwardHockeyistStrength = forwardHockeyistStrength;
            this.forwardHockeyistEndurance = forwardHockeyistEndurance;
            this.forwardHockeyistDexterity = forwardHockeyistDexterity;
            this.forwardHockeyistAgility = forwardHockeyistAgility;
            this.defencemanHockeyistStrength = defencemanHockeyistStrength;
            this.defencemanHockeyistEndurance = defencemanHockeyistEndurance;
            this.defencemanHockeyistDexterity = defencemanHockeyistDexterity;
            this.defencemanHockeyistAgility = defencemanHockeyistAgility;
            this.minRandomHockeyistParameter = minRandomHockeyistParameter;
            this.maxRandomHockeyistParameter = maxRandomHockeyistParameter;
            this.struckPuckInitialSpeedFactor = struckPuckInitialSpeedFactor;
            this.puckBindingRange = puckBindingRange;
        }

        public long RandomSeed {
            get { return randomSeed; }
        }

        public int TickCount {
            get { return tickCount; }
        }

        public double WorldWidth {
            get { return worldWidth; }
        }

        public double WorldHeight {
            get { return worldHeight; }
        }

        public double GoalNetTop {
            get { return goalNetTop; }
        }

        public double GoalNetWidth {
            get { return goalNetWidth; }
        }

        public double GoalNetHeight {
            get { return goalNetHeight; }
        }

        public double RinkTop {
            get { return rinkTop; }
        }

        public double RinkLeft {
            get { return rinkLeft; }
        }

        public double RinkBottom {
            get { return rinkBottom; }
        }

        public double RinkRight {
            get { return rinkRight; }
        }

        public int AfterGoalStateTickCount {
            get { return afterGoalStateTickCount; }
        }

        public int OvertimeTickCount {
            get { return overtimeTickCount; }
        }

        public int DefaultActionCooldownTicks {
            get { return defaultActionCooldownTicks; }
        }

        public int SwingActionCooldownTicks {
            get { return swingActionCooldownTicks; }
        }

        public int CancelStrikeActionCooldownTicks {
            get { return cancelStrikeActionCooldownTicks; }
        }

        public int ActionCooldownTicksAfterLosingPuck {
            get { return actionCooldownTicksAfterLosingPuck; }
        }

        public double StickLength {
            get { return stickLength; }
        }

        public double StickSector {
            get { return stickSector; }
        }

        public double PassSector {
            get { return passSector; }
        }

        public int HockeyistAttributeBaseValue {
            get { return hockeyistAttributeBaseValue; }
        }

        public double MinActionChance {
            get { return minActionChance; }
        }

        public double MaxActionChance {
            get { return maxActionChance; }
        }

        public double StrikeAngleDeviation {
            get { return strikeAngleDeviation; }
        }

        public double PassAngleDeviation {
            get { return passAngleDeviation; }
        }

        public double PickUpPuckBaseChance {
            get { return pickUpPuckBaseChance; }
        }

        public double TakePuckAwayBaseChance {
            get { return takePuckAwayBaseChance; }
        }

        public int MaxEffectiveSwingTicks {
            get { return maxEffectiveSwingTicks; }
        }

        public double StrikePowerBaseFactor {
            get { return strikePowerBaseFactor; }
        }

        public double StrikePowerGrowthFactor {
            get { return strikePowerGrowthFactor; }
        }

        public double StrikePuckBaseChance {
            get { return strikePuckBaseChance; }
        }

        public double KnockdownChanceFactor {
            get { return knockdownChanceFactor; }
        }

        public double KnockdownTicksFactor {
            get { return knockdownTicksFactor; }
        }

        public double MaxSpeedToAllowSubstitute {
            get { return maxSpeedToAllowSubstitute; }
        }

        public double SubstitutionAreaHeight {
            get { return substitutionAreaHeight; }
        }

        public double PassPowerFactor {
            get { return passPowerFactor; }
        }

        public double HockeyistMaxStamina {
            get { return hockeyistMaxStamina; }
        }

        public double ActiveHockeyistStaminaGrowthPerTick {
            get { return activeHockeyistStaminaGrowthPerTick; }
        }

        public double RestingHockeyistStaminaGrowthPerTick {
            get { return restingHockeyistStaminaGrowthPerTick; }
        }

        public double ZeroStaminaHockeyistEffectivenessFactor {
            get { return zeroStaminaHockeyistEffectivenessFactor; }
        }

        public double SpeedUpStaminaCostFactor {
            get { return speedUpStaminaCostFactor; }
        }

        public double TurnStaminaCostFactor {
            get { return turnStaminaCostFactor; }
        }

        public double TakePuckStaminaCost {
            get { return takePuckStaminaCost; }
        }

        public double SwingStaminaCost {
            get { return swingStaminaCost; }
        }

        public double StrikeStaminaBaseCost {
            get { return strikeStaminaBaseCost; }
        }

        public double StrikeStaminaCostGrowthFactor {
            get { return strikeStaminaCostGrowthFactor; }
        }

        public double CancelStrikeStaminaCost {
            get { return cancelStrikeStaminaCost; }
        }

        public double PassStaminaCost {
            get { return passStaminaCost; }
        }

        public double GoalieMaxSpeed {
            get { return goalieMaxSpeed; }
        }

        public double HockeyistMaxSpeed {
            get { return hockeyistMaxSpeed; }
        }

        public double StruckHockeyistInitialSpeedFactor {
            get { return struckHockeyistInitialSpeedFactor; }
        }

        public double HockeyistSpeedUpFactor {
            get { return hockeyistSpeedUpFactor; }
        }

        public double HockeyistSpeedDownFactor {
            get { return hockeyistSpeedDownFactor; }
        }

        public double HockeyistTurnAngleFactor {
            get { return hockeyistTurnAngleFactor; }
        }

        public int VersatileHockeyistStrength {
            get { return versatileHockeyistStrength; }
        }

        public int VersatileHockeyistEndurance {
            get { return versatileHockeyistEndurance; }
        }

        public int VersatileHockeyistDexterity {
            get { return versatileHockeyistDexterity; }
        }

        public int VersatileHockeyistAgility {
            get { return versatileHockeyistAgility; }
        }

        public int ForwardHockeyistStrength {
            get { return forwardHockeyistStrength; }
        }

        public int ForwardHockeyistEndurance {
            get { return forwardHockeyistEndurance; }
        }

        public int ForwardHockeyistDexterity {
            get { return forwardHockeyistDexterity; }
        }

        public int ForwardHockeyistAgility {
            get { return forwardHockeyistAgility; }
        }

        public int DefencemanHockeyistStrength {
            get { return defencemanHockeyistStrength; }
        }

        public int DefencemanHockeyistEndurance {
            get { return defencemanHockeyistEndurance; }
        }

        public int DefencemanHockeyistDexterity {
            get { return defencemanHockeyistDexterity; }
        }

        public int DefencemanHockeyistAgility {
            get { return defencemanHockeyistAgility; }
        }

        public int MinRandomHockeyistParameter {
            get { return minRandomHockeyistParameter; }
        }

        public int MaxRandomHockeyistParameter {
            get { return maxRandomHockeyistParameter; }
        }

        public double StruckPuckInitialSpeedFactor {
            get { return struckPuckInitialSpeedFactor; }
        }

        public double PuckBindingRange {
            get { return puckBindingRange; }
        }
    }
}