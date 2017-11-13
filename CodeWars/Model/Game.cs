using System;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model {
    public class Game {
        private readonly long randomSeed;
        private readonly int tickCount;
        private readonly double worldWidth;
        private readonly double worldHeight;
        private readonly bool isFogOfWarEnabled;
        private readonly int victoryScore;
        private readonly int facilityCaptureScore;
        private readonly int vehicleEliminationScore;
        private readonly int actionDetectionInterval;
        private readonly int baseActionCount;
        private readonly int additionalActionCountPerControlCenter;
        private readonly int maxUnitGroup;
        private readonly int terrainWeatherMapColumnCount;
        private readonly int terrainWeatherMapRowCount;
        private readonly double plainTerrainVisionFactor;
        private readonly double plainTerrainStealthFactor;
        private readonly double plainTerrainSpeedFactor;
        private readonly double swampTerrainVisionFactor;
        private readonly double swampTerrainStealthFactor;
        private readonly double swampTerrainSpeedFactor;
        private readonly double forestTerrainVisionFactor;
        private readonly double forestTerrainStealthFactor;
        private readonly double forestTerrainSpeedFactor;
        private readonly double clearWeatherVisionFactor;
        private readonly double clearWeatherStealthFactor;
        private readonly double clearWeatherSpeedFactor;
        private readonly double cloudWeatherVisionFactor;
        private readonly double cloudWeatherStealthFactor;
        private readonly double cloudWeatherSpeedFactor;
        private readonly double rainWeatherVisionFactor;
        private readonly double rainWeatherStealthFactor;
        private readonly double rainWeatherSpeedFactor;
        private readonly double vehicleRadius;
        private readonly int tankDurability;
        private readonly double tankSpeed;
        private readonly double tankVisionRange;
        private readonly double tankGroundAttackRange;
        private readonly double tankAerialAttackRange;
        private readonly int tankGroundDamage;
        private readonly int tankAerialDamage;
        private readonly int tankGroundDefence;
        private readonly int tankAerialDefence;
        private readonly int tankAttackCooldownTicks;
        private readonly int tankProductionCost;
        private readonly int ifvDurability;
        private readonly double ifvSpeed;
        private readonly double ifvVisionRange;
        private readonly double ifvGroundAttackRange;
        private readonly double ifvAerialAttackRange;
        private readonly int ifvGroundDamage;
        private readonly int ifvAerialDamage;
        private readonly int ifvGroundDefence;
        private readonly int ifvAerialDefence;
        private readonly int ifvAttackCooldownTicks;
        private readonly int ifvProductionCost;
        private readonly int arrvDurability;
        private readonly double arrvSpeed;
        private readonly double arrvVisionRange;
        private readonly int arrvGroundDefence;
        private readonly int arrvAerialDefence;
        private readonly int arrvProductionCost;
        private readonly double arrvRepairRange;
        private readonly double arrvRepairSpeed;
        private readonly int helicopterDurability;
        private readonly double helicopterSpeed;
        private readonly double helicopterVisionRange;
        private readonly double helicopterGroundAttackRange;
        private readonly double helicopterAerialAttackRange;
        private readonly int helicopterGroundDamage;
        private readonly int helicopterAerialDamage;
        private readonly int helicopterGroundDefence;
        private readonly int helicopterAerialDefence;
        private readonly int helicopterAttackCooldownTicks;
        private readonly int helicopterProductionCost;
        private readonly int fighterDurability;
        private readonly double fighterSpeed;
        private readonly double fighterVisionRange;
        private readonly double fighterGroundAttackRange;
        private readonly double fighterAerialAttackRange;
        private readonly int fighterGroundDamage;
        private readonly int fighterAerialDamage;
        private readonly int fighterGroundDefence;
        private readonly int fighterAerialDefence;
        private readonly int fighterAttackCooldownTicks;
        private readonly int fighterProductionCost;
        private readonly double maxFacilityCapturePoints;
        private readonly double facilityCapturePointsPerVehiclePerTick;
        private readonly double facilityWidth;
        private readonly double facilityHeight;
        private readonly int baseTacticalNuclearStrikeCooldown;
        private readonly int tacticalNuclearStrikeCooldownDecreasePerControlCenter;
        private readonly double maxTacticalNuclearStrikeDamage;
        private readonly double tacticalNuclearStrikeRadius;
        private readonly int tacticalNuclearStrikeDelay;

        public Game(long randomSeed, int tickCount, double worldWidth, double worldHeight, bool isFogOfWarEnabled,
                int victoryScore, int facilityCaptureScore, int vehicleEliminationScore, int actionDetectionInterval,
                int baseActionCount, int additionalActionCountPerControlCenter, int maxUnitGroup,
                int terrainWeatherMapColumnCount, int terrainWeatherMapRowCount, double plainTerrainVisionFactor,
                double plainTerrainStealthFactor, double plainTerrainSpeedFactor, double swampTerrainVisionFactor,
                double swampTerrainStealthFactor, double swampTerrainSpeedFactor, double forestTerrainVisionFactor,
                double forestTerrainStealthFactor, double forestTerrainSpeedFactor, double clearWeatherVisionFactor,
                double clearWeatherStealthFactor, double clearWeatherSpeedFactor, double cloudWeatherVisionFactor,
                double cloudWeatherStealthFactor, double cloudWeatherSpeedFactor, double rainWeatherVisionFactor,
                double rainWeatherStealthFactor, double rainWeatherSpeedFactor, double vehicleRadius,
                int tankDurability, double tankSpeed, double tankVisionRange, double tankGroundAttackRange,
                double tankAerialAttackRange, int tankGroundDamage, int tankAerialDamage, int tankGroundDefence,
                int tankAerialDefence, int tankAttackCooldownTicks, int tankProductionCost, int ifvDurability,
                double ifvSpeed, double ifvVisionRange, double ifvGroundAttackRange, double ifvAerialAttackRange,
                int ifvGroundDamage, int ifvAerialDamage, int ifvGroundDefence, int ifvAerialDefence,
                int ifvAttackCooldownTicks, int ifvProductionCost, int arrvDurability, double arrvSpeed,
                double arrvVisionRange, int arrvGroundDefence, int arrvAerialDefence, int arrvProductionCost,
                double arrvRepairRange, double arrvRepairSpeed, int helicopterDurability, double helicopterSpeed,
                double helicopterVisionRange, double helicopterGroundAttackRange, double helicopterAerialAttackRange,
                int helicopterGroundDamage, int helicopterAerialDamage, int helicopterGroundDefence,
                int helicopterAerialDefence, int helicopterAttackCooldownTicks, int helicopterProductionCost,
                int fighterDurability, double fighterSpeed, double fighterVisionRange, double fighterGroundAttackRange,
                double fighterAerialAttackRange, int fighterGroundDamage, int fighterAerialDamage,
                int fighterGroundDefence, int fighterAerialDefence, int fighterAttackCooldownTicks,
                int fighterProductionCost, double maxFacilityCapturePoints,
                double facilityCapturePointsPerVehiclePerTick, double facilityWidth, double facilityHeight,
                int baseTacticalNuclearStrikeCooldown, int tacticalNuclearStrikeCooldownDecreasePerControlCenter,
                double maxTacticalNuclearStrikeDamage, double tacticalNuclearStrikeRadius,
                int tacticalNuclearStrikeDelay) {
            this.randomSeed = randomSeed;
            this.tickCount = tickCount;
            this.worldWidth = worldWidth;
            this.worldHeight = worldHeight;
            this.isFogOfWarEnabled = isFogOfWarEnabled;
            this.victoryScore = victoryScore;
            this.facilityCaptureScore = facilityCaptureScore;
            this.vehicleEliminationScore = vehicleEliminationScore;
            this.actionDetectionInterval = actionDetectionInterval;
            this.baseActionCount = baseActionCount;
            this.additionalActionCountPerControlCenter = additionalActionCountPerControlCenter;
            this.maxUnitGroup = maxUnitGroup;
            this.terrainWeatherMapColumnCount = terrainWeatherMapColumnCount;
            this.terrainWeatherMapRowCount = terrainWeatherMapRowCount;
            this.plainTerrainVisionFactor = plainTerrainVisionFactor;
            this.plainTerrainStealthFactor = plainTerrainStealthFactor;
            this.plainTerrainSpeedFactor = plainTerrainSpeedFactor;
            this.swampTerrainVisionFactor = swampTerrainVisionFactor;
            this.swampTerrainStealthFactor = swampTerrainStealthFactor;
            this.swampTerrainSpeedFactor = swampTerrainSpeedFactor;
            this.forestTerrainVisionFactor = forestTerrainVisionFactor;
            this.forestTerrainStealthFactor = forestTerrainStealthFactor;
            this.forestTerrainSpeedFactor = forestTerrainSpeedFactor;
            this.clearWeatherVisionFactor = clearWeatherVisionFactor;
            this.clearWeatherStealthFactor = clearWeatherStealthFactor;
            this.clearWeatherSpeedFactor = clearWeatherSpeedFactor;
            this.cloudWeatherVisionFactor = cloudWeatherVisionFactor;
            this.cloudWeatherStealthFactor = cloudWeatherStealthFactor;
            this.cloudWeatherSpeedFactor = cloudWeatherSpeedFactor;
            this.rainWeatherVisionFactor = rainWeatherVisionFactor;
            this.rainWeatherStealthFactor = rainWeatherStealthFactor;
            this.rainWeatherSpeedFactor = rainWeatherSpeedFactor;
            this.vehicleRadius = vehicleRadius;
            this.tankDurability = tankDurability;
            this.tankSpeed = tankSpeed;
            this.tankVisionRange = tankVisionRange;
            this.tankGroundAttackRange = tankGroundAttackRange;
            this.tankAerialAttackRange = tankAerialAttackRange;
            this.tankGroundDamage = tankGroundDamage;
            this.tankAerialDamage = tankAerialDamage;
            this.tankGroundDefence = tankGroundDefence;
            this.tankAerialDefence = tankAerialDefence;
            this.tankAttackCooldownTicks = tankAttackCooldownTicks;
            this.tankProductionCost = tankProductionCost;
            this.ifvDurability = ifvDurability;
            this.ifvSpeed = ifvSpeed;
            this.ifvVisionRange = ifvVisionRange;
            this.ifvGroundAttackRange = ifvGroundAttackRange;
            this.ifvAerialAttackRange = ifvAerialAttackRange;
            this.ifvGroundDamage = ifvGroundDamage;
            this.ifvAerialDamage = ifvAerialDamage;
            this.ifvGroundDefence = ifvGroundDefence;
            this.ifvAerialDefence = ifvAerialDefence;
            this.ifvAttackCooldownTicks = ifvAttackCooldownTicks;
            this.ifvProductionCost = ifvProductionCost;
            this.arrvDurability = arrvDurability;
            this.arrvSpeed = arrvSpeed;
            this.arrvVisionRange = arrvVisionRange;
            this.arrvGroundDefence = arrvGroundDefence;
            this.arrvAerialDefence = arrvAerialDefence;
            this.arrvProductionCost = arrvProductionCost;
            this.arrvRepairRange = arrvRepairRange;
            this.arrvRepairSpeed = arrvRepairSpeed;
            this.helicopterDurability = helicopterDurability;
            this.helicopterSpeed = helicopterSpeed;
            this.helicopterVisionRange = helicopterVisionRange;
            this.helicopterGroundAttackRange = helicopterGroundAttackRange;
            this.helicopterAerialAttackRange = helicopterAerialAttackRange;
            this.helicopterGroundDamage = helicopterGroundDamage;
            this.helicopterAerialDamage = helicopterAerialDamage;
            this.helicopterGroundDefence = helicopterGroundDefence;
            this.helicopterAerialDefence = helicopterAerialDefence;
            this.helicopterAttackCooldownTicks = helicopterAttackCooldownTicks;
            this.helicopterProductionCost = helicopterProductionCost;
            this.fighterDurability = fighterDurability;
            this.fighterSpeed = fighterSpeed;
            this.fighterVisionRange = fighterVisionRange;
            this.fighterGroundAttackRange = fighterGroundAttackRange;
            this.fighterAerialAttackRange = fighterAerialAttackRange;
            this.fighterGroundDamage = fighterGroundDamage;
            this.fighterAerialDamage = fighterAerialDamage;
            this.fighterGroundDefence = fighterGroundDefence;
            this.fighterAerialDefence = fighterAerialDefence;
            this.fighterAttackCooldownTicks = fighterAttackCooldownTicks;
            this.fighterProductionCost = fighterProductionCost;
            this.maxFacilityCapturePoints = maxFacilityCapturePoints;
            this.facilityCapturePointsPerVehiclePerTick = facilityCapturePointsPerVehiclePerTick;
            this.facilityWidth = facilityWidth;
            this.facilityHeight = facilityHeight;
            this.baseTacticalNuclearStrikeCooldown = baseTacticalNuclearStrikeCooldown;
            this.tacticalNuclearStrikeCooldownDecreasePerControlCenter = tacticalNuclearStrikeCooldownDecreasePerControlCenter;
            this.maxTacticalNuclearStrikeDamage = maxTacticalNuclearStrikeDamage;
            this.tacticalNuclearStrikeRadius = tacticalNuclearStrikeRadius;
            this.tacticalNuclearStrikeDelay = tacticalNuclearStrikeDelay;
        }

        public long RandomSeed => randomSeed;
        public int TickCount => tickCount;
        public double WorldWidth => worldWidth;
        public double WorldHeight => worldHeight;
        public bool IsFogOfWarEnabled => isFogOfWarEnabled;
        public int VictoryScore => victoryScore;
        public int FacilityCaptureScore => facilityCaptureScore;
        public int VehicleEliminationScore => vehicleEliminationScore;
        public int ActionDetectionInterval => actionDetectionInterval;
        public int BaseActionCount => baseActionCount;
        public int AdditionalActionCountPerControlCenter => additionalActionCountPerControlCenter;
        public int MaxUnitGroup => maxUnitGroup;
        public int TerrainWeatherMapColumnCount => terrainWeatherMapColumnCount;
        public int TerrainWeatherMapRowCount => terrainWeatherMapRowCount;
        public double PlainTerrainVisionFactor => plainTerrainVisionFactor;
        public double PlainTerrainStealthFactor => plainTerrainStealthFactor;
        public double PlainTerrainSpeedFactor => plainTerrainSpeedFactor;
        public double SwampTerrainVisionFactor => swampTerrainVisionFactor;
        public double SwampTerrainStealthFactor => swampTerrainStealthFactor;
        public double SwampTerrainSpeedFactor => swampTerrainSpeedFactor;
        public double ForestTerrainVisionFactor => forestTerrainVisionFactor;
        public double ForestTerrainStealthFactor => forestTerrainStealthFactor;
        public double ForestTerrainSpeedFactor => forestTerrainSpeedFactor;
        public double ClearWeatherVisionFactor => clearWeatherVisionFactor;
        public double ClearWeatherStealthFactor => clearWeatherStealthFactor;
        public double ClearWeatherSpeedFactor => clearWeatherSpeedFactor;
        public double CloudWeatherVisionFactor => cloudWeatherVisionFactor;
        public double CloudWeatherStealthFactor => cloudWeatherStealthFactor;
        public double CloudWeatherSpeedFactor => cloudWeatherSpeedFactor;
        public double RainWeatherVisionFactor => rainWeatherVisionFactor;
        public double RainWeatherStealthFactor => rainWeatherStealthFactor;
        public double RainWeatherSpeedFactor => rainWeatherSpeedFactor;
        public double VehicleRadius => vehicleRadius;
        public int TankDurability => tankDurability;
        public double TankSpeed => tankSpeed;
        public double TankVisionRange => tankVisionRange;
        public double TankGroundAttackRange => tankGroundAttackRange;
        public double TankAerialAttackRange => tankAerialAttackRange;
        public int TankGroundDamage => tankGroundDamage;
        public int TankAerialDamage => tankAerialDamage;
        public int TankGroundDefence => tankGroundDefence;
        public int TankAerialDefence => tankAerialDefence;
        public int TankAttackCooldownTicks => tankAttackCooldownTicks;
        public int TankProductionCost => tankProductionCost;
        public int IfvDurability => ifvDurability;
        public double IfvSpeed => ifvSpeed;
        public double IfvVisionRange => ifvVisionRange;
        public double IfvGroundAttackRange => ifvGroundAttackRange;
        public double IfvAerialAttackRange => ifvAerialAttackRange;
        public int IfvGroundDamage => ifvGroundDamage;
        public int IfvAerialDamage => ifvAerialDamage;
        public int IfvGroundDefence => ifvGroundDefence;
        public int IfvAerialDefence => ifvAerialDefence;
        public int IfvAttackCooldownTicks => ifvAttackCooldownTicks;
        public int IfvProductionCost => ifvProductionCost;
        public int ArrvDurability => arrvDurability;
        public double ArrvSpeed => arrvSpeed;
        public double ArrvVisionRange => arrvVisionRange;
        public int ArrvGroundDefence => arrvGroundDefence;
        public int ArrvAerialDefence => arrvAerialDefence;
        public int ArrvProductionCost => arrvProductionCost;
        public double ArrvRepairRange => arrvRepairRange;
        public double ArrvRepairSpeed => arrvRepairSpeed;
        public int HelicopterDurability => helicopterDurability;
        public double HelicopterSpeed => helicopterSpeed;
        public double HelicopterVisionRange => helicopterVisionRange;
        public double HelicopterGroundAttackRange => helicopterGroundAttackRange;
        public double HelicopterAerialAttackRange => helicopterAerialAttackRange;
        public int HelicopterGroundDamage => helicopterGroundDamage;
        public int HelicopterAerialDamage => helicopterAerialDamage;
        public int HelicopterGroundDefence => helicopterGroundDefence;
        public int HelicopterAerialDefence => helicopterAerialDefence;
        public int HelicopterAttackCooldownTicks => helicopterAttackCooldownTicks;
        public int HelicopterProductionCost => helicopterProductionCost;
        public int FighterDurability => fighterDurability;
        public double FighterSpeed => fighterSpeed;
        public double FighterVisionRange => fighterVisionRange;
        public double FighterGroundAttackRange => fighterGroundAttackRange;
        public double FighterAerialAttackRange => fighterAerialAttackRange;
        public int FighterGroundDamage => fighterGroundDamage;
        public int FighterAerialDamage => fighterAerialDamage;
        public int FighterGroundDefence => fighterGroundDefence;
        public int FighterAerialDefence => fighterAerialDefence;
        public int FighterAttackCooldownTicks => fighterAttackCooldownTicks;
        public int FighterProductionCost => fighterProductionCost;
        public double MaxFacilityCapturePoints => maxFacilityCapturePoints;
        public double FacilityCapturePointsPerVehiclePerTick => facilityCapturePointsPerVehiclePerTick;
        public double FacilityWidth => facilityWidth;
        public double FacilityHeight => facilityHeight;
        public int BaseTacticalNuclearStrikeCooldown => baseTacticalNuclearStrikeCooldown;
        public int TacticalNuclearStrikeCooldownDecreasePerControlCenter => tacticalNuclearStrikeCooldownDecreasePerControlCenter;
        public double MaxTacticalNuclearStrikeDamage => maxTacticalNuclearStrikeDamage;
        public double TacticalNuclearStrikeRadius => tacticalNuclearStrikeRadius;
        public int TacticalNuclearStrikeDelay => tacticalNuclearStrikeDelay;
    }
}