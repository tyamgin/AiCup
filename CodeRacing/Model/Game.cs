using System;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model {
    public class Game {
        private readonly long randomSeed;
        private readonly int tickCount;
        private readonly int worldWidth;
        private readonly int worldHeight;
        private readonly double trackTileSize;
        private readonly double trackTileMargin;
        private readonly int lapCount;
        private readonly int lapTickCount;
        private readonly int initialFreezeDurationTicks;
        private readonly double burningTimeDurationFactor;
        private readonly int[] finishTrackScores;
        private readonly int finishLapScore;
        private readonly double lapWaypointsSummaryScoreFactor;
        private readonly double carDamageScoreFactor;
        private readonly int carEliminationScore;
        private readonly double carWidth;
        private readonly double carHeight;
        private readonly double carEnginePowerChangePerTick;
        private readonly double carWheelTurnChangePerTick;
        private readonly double carAngularSpeedFactor;
        private readonly double carMovementAirFrictionFactor;
        private readonly double carRotationAirFrictionFactor;
        private readonly double carLengthwiseMovementFrictionFactor;
        private readonly double carCrosswiseMovementFrictionFactor;
        private readonly double carRotationFrictionFactor;
        private readonly int throwProjectileCooldownTicks;
        private readonly int useNitroCooldownTicks;
        private readonly int spillOilCooldownTicks;
        private readonly double nitroEnginePowerFactor;
        private readonly int nitroDurationTicks;
        private readonly int carReactivationTimeTicks;
        private readonly double buggyMass;
        private readonly double buggyEngineForwardPower;
        private readonly double buggyEngineRearPower;
        private readonly double jeepMass;
        private readonly double jeepEngineForwardPower;
        private readonly double jeepEngineRearPower;
        private readonly double bonusSize;
        private readonly double bonusMass;
        private readonly int pureScoreAmount;
        private readonly double washerRadius;
        private readonly double washerMass;
        private readonly double washerInitialSpeed;
        private readonly double washerDamage;
        private readonly double sideWasherAngle;
        private readonly double tireRadius;
        private readonly double tireMass;
        private readonly double tireInitialSpeed;
        private readonly double tireDamageFactor;
        private readonly double tireDisappearSpeedFactor;
        private readonly double oilSlickInitialRange;
        private readonly double oilSlickRadius;
        private readonly int oilSlickLifetime;
        private readonly int maxOiledStateDurationTicks;

        public Game(long randomSeed, int tickCount, int worldWidth, int worldHeight, double trackTileSize,
                double trackTileMargin, int lapCount, int lapTickCount, int initialFreezeDurationTicks,
                double burningTimeDurationFactor, int[] finishTrackScores, int finishLapScore,
                double lapWaypointsSummaryScoreFactor, double carDamageScoreFactor, int carEliminationScore,
                double carWidth, double carHeight, double carEnginePowerChangePerTick, double carWheelTurnChangePerTick,
                double carAngularSpeedFactor, double carMovementAirFrictionFactor, double carRotationAirFrictionFactor,
                double carLengthwiseMovementFrictionFactor, double carCrosswiseMovementFrictionFactor,
                double carRotationFrictionFactor, int throwProjectileCooldownTicks, int useNitroCooldownTicks,
                int spillOilCooldownTicks, double nitroEnginePowerFactor, int nitroDurationTicks,
                int carReactivationTimeTicks, double buggyMass, double buggyEngineForwardPower,
                double buggyEngineRearPower, double jeepMass, double jeepEngineForwardPower, double jeepEngineRearPower,
                double bonusSize, double bonusMass, int pureScoreAmount, double washerRadius, double washerMass,
                double washerInitialSpeed, double washerDamage, double sideWasherAngle, double tireRadius,
                double tireMass, double tireInitialSpeed, double tireDamageFactor, double tireDisappearSpeedFactor,
                double oilSlickInitialRange, double oilSlickRadius, int oilSlickLifetime,
                int maxOiledStateDurationTicks) {
            this.randomSeed = randomSeed;
            this.tickCount = tickCount;
            this.worldWidth = worldWidth;
            this.worldHeight = worldHeight;
            this.trackTileSize = trackTileSize;
            this.trackTileMargin = trackTileMargin;
            this.lapCount = lapCount;
            this.lapTickCount = lapTickCount;
            this.initialFreezeDurationTicks = initialFreezeDurationTicks;
            this.burningTimeDurationFactor = burningTimeDurationFactor;

            this.finishTrackScores = new int[finishTrackScores.Length];
            Array.Copy(finishTrackScores, this.finishTrackScores, finishTrackScores.Length);

            this.finishLapScore = finishLapScore;
            this.lapWaypointsSummaryScoreFactor = lapWaypointsSummaryScoreFactor;
            this.carDamageScoreFactor = carDamageScoreFactor;
            this.carEliminationScore = carEliminationScore;
            this.carWidth = carWidth;
            this.carHeight = carHeight;
            this.carEnginePowerChangePerTick = carEnginePowerChangePerTick;
            this.carWheelTurnChangePerTick = carWheelTurnChangePerTick;
            this.carAngularSpeedFactor = carAngularSpeedFactor;
            this.carMovementAirFrictionFactor = carMovementAirFrictionFactor;
            this.carRotationAirFrictionFactor = carRotationAirFrictionFactor;
            this.carLengthwiseMovementFrictionFactor = carLengthwiseMovementFrictionFactor;
            this.carCrosswiseMovementFrictionFactor = carCrosswiseMovementFrictionFactor;
            this.carRotationFrictionFactor = carRotationFrictionFactor;
            this.throwProjectileCooldownTicks = throwProjectileCooldownTicks;
            this.useNitroCooldownTicks = useNitroCooldownTicks;
            this.spillOilCooldownTicks = spillOilCooldownTicks;
            this.nitroEnginePowerFactor = nitroEnginePowerFactor;
            this.nitroDurationTicks = nitroDurationTicks;
            this.carReactivationTimeTicks = carReactivationTimeTicks;
            this.buggyMass = buggyMass;
            this.buggyEngineForwardPower = buggyEngineForwardPower;
            this.buggyEngineRearPower = buggyEngineRearPower;
            this.jeepMass = jeepMass;
            this.jeepEngineForwardPower = jeepEngineForwardPower;
            this.jeepEngineRearPower = jeepEngineRearPower;
            this.bonusSize = bonusSize;
            this.bonusMass = bonusMass;
            this.pureScoreAmount = pureScoreAmount;
            this.washerRadius = washerRadius;
            this.washerMass = washerMass;
            this.washerInitialSpeed = washerInitialSpeed;
            this.washerDamage = washerDamage;
            this.sideWasherAngle = sideWasherAngle;
            this.tireRadius = tireRadius;
            this.tireMass = tireMass;
            this.tireInitialSpeed = tireInitialSpeed;
            this.tireDamageFactor = tireDamageFactor;
            this.tireDisappearSpeedFactor = tireDisappearSpeedFactor;
            this.oilSlickInitialRange = oilSlickInitialRange;
            this.oilSlickRadius = oilSlickRadius;
            this.oilSlickLifetime = oilSlickLifetime;
            this.maxOiledStateDurationTicks = maxOiledStateDurationTicks;
        }

        public long RandomSeed {
            get { return randomSeed; }
        }

        public int TickCount {
            get { return tickCount; }
        }

        public int WorldWidth {
            get { return worldWidth; }
        }

        public int WorldHeight {
            get { return worldHeight; }
        }

        public double TrackTileSize {
            get { return trackTileSize; }
        }

        public double TrackTileMargin {
            get { return trackTileMargin; }
        }

        public int LapCount {
            get { return lapCount; }
        }

        public int LapTickCount {
            get { return lapTickCount; }
        }

        public int InitialFreezeDurationTicks {
            get { return initialFreezeDurationTicks; }
        }

        public double BurningTimeDurationFactor {
            get { return burningTimeDurationFactor; }
        }

        public int[] FinishTrackScores {
            get {
                int[] finishTrackScores = new int[this.finishTrackScores.Length];
                Array.Copy(this.finishTrackScores, finishTrackScores, this.finishTrackScores.Length);
                return finishTrackScores;
            }
        }

        public int FinishLapScore {
            get { return finishLapScore; }
        }

        public double LapWaypointsSummaryScoreFactor {
            get { return lapWaypointsSummaryScoreFactor; }
        }

        public double CarDamageScoreFactor {
            get { return carDamageScoreFactor; }
        }

        public int CarEliminationScore {
            get { return carEliminationScore; }
        }

        public double CarWidth {
            get { return carWidth; }
        }

        public double CarHeight {
            get { return carHeight; }
        }

        public double CarEnginePowerChangePerTick {
            get { return carEnginePowerChangePerTick; }
        }

        public double CarWheelTurnChangePerTick {
            get { return carWheelTurnChangePerTick; }
        }

        public double CarAngularSpeedFactor {
            get { return carAngularSpeedFactor; }
        }

        public double CarMovementAirFrictionFactor {
            get { return carMovementAirFrictionFactor; }
        }

        public double CarRotationAirFrictionFactor {
            get { return carRotationAirFrictionFactor; }
        }

        public double CarLengthwiseMovementFrictionFactor {
            get { return carLengthwiseMovementFrictionFactor; }
        }

        public double CarCrosswiseMovementFrictionFactor {
            get { return carCrosswiseMovementFrictionFactor; }
        }

        public double CarRotationFrictionFactor {
            get { return carRotationFrictionFactor; }
        }

        public int ThrowProjectileCooldownTicks {
            get { return throwProjectileCooldownTicks; }
        }

        public int UseNitroCooldownTicks {
            get { return useNitroCooldownTicks; }
        }

        public int SpillOilCooldownTicks {
            get { return spillOilCooldownTicks; }
        }

        public double NitroEnginePowerFactor {
            get { return nitroEnginePowerFactor; }
        }

        public int NitroDurationTicks {
            get { return nitroDurationTicks; }
        }

        public int CarReactivationTimeTicks {
            get { return carReactivationTimeTicks; }
        }

        public double BuggyMass {
            get { return buggyMass; }
        }

        public double BuggyEngineForwardPower {
            get { return buggyEngineForwardPower; }
        }

        public double BuggyEngineRearPower {
            get { return buggyEngineRearPower; }
        }

        public double JeepMass {
            get { return jeepMass; }
        }

        public double JeepEngineForwardPower {
            get { return jeepEngineForwardPower; }
        }

        public double JeepEngineRearPower {
            get { return jeepEngineRearPower; }
        }

        public double BonusSize {
            get { return bonusSize; }
        }

        public double BonusMass {
            get { return bonusMass; }
        }

        public int PureScoreAmount {
            get { return pureScoreAmount; }
        }

        public double WasherRadius {
            get { return washerRadius; }
        }

        public double WasherMass {
            get { return washerMass; }
        }

        public double WasherInitialSpeed {
            get { return washerInitialSpeed; }
        }

        public double WasherDamage {
            get { return washerDamage; }
        }

        public double SideWasherAngle {
            get { return sideWasherAngle; }
        }

        public double TireRadius {
            get { return tireRadius; }
        }

        public double TireMass {
            get { return tireMass; }
        }

        public double TireInitialSpeed {
            get { return tireInitialSpeed; }
        }

        public double TireDamageFactor {
            get { return tireDamageFactor; }
        }

        public double TireDisappearSpeedFactor {
            get { return tireDisappearSpeedFactor; }
        }

        public double OilSlickInitialRange {
            get { return oilSlickInitialRange; }
        }

        public double OilSlickRadius {
            get { return oilSlickRadius; }
        }

        public int OilSlickLifetime {
            get { return oilSlickLifetime; }
        }

        public int MaxOiledStateDurationTicks {
            get { return maxOiledStateDurationTicks; }
        }
    }
}