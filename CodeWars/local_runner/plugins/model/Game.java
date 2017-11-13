package model;

public class Game {
    private final long randomSeed;
    private final int tickCount;
    private final double worldWidth;
    private final double worldHeight;
    private final boolean fogOfWarEnabled;
    private final int victoryScore;
    private final int facilityCaptureScore;
    private final int vehicleEliminationScore;
    private final int actionDetectionInterval;
    private final int baseActionCount;
    private final int additionalActionCountPerControlCenter;
    private final int maxUnitGroup;
    private final int terrainWeatherMapColumnCount;
    private final int terrainWeatherMapRowCount;
    private final double plainTerrainVisionFactor;
    private final double plainTerrainStealthFactor;
    private final double plainTerrainSpeedFactor;
    private final double swampTerrainVisionFactor;
    private final double swampTerrainStealthFactor;
    private final double swampTerrainSpeedFactor;
    private final double forestTerrainVisionFactor;
    private final double forestTerrainStealthFactor;
    private final double forestTerrainSpeedFactor;
    private final double clearWeatherVisionFactor;
    private final double clearWeatherStealthFactor;
    private final double clearWeatherSpeedFactor;
    private final double cloudWeatherVisionFactor;
    private final double cloudWeatherStealthFactor;
    private final double cloudWeatherSpeedFactor;
    private final double rainWeatherVisionFactor;
    private final double rainWeatherStealthFactor;
    private final double rainWeatherSpeedFactor;
    private final double vehicleRadius;
    private final int tankDurability;
    private final double tankSpeed;
    private final double tankVisionRange;
    private final double tankGroundAttackRange;
    private final double tankAerialAttackRange;
    private final int tankGroundDamage;
    private final int tankAerialDamage;
    private final int tankGroundDefence;
    private final int tankAerialDefence;
    private final int tankAttackCooldownTicks;
    private final int tankProductionCost;
    private final int ifvDurability;
    private final double ifvSpeed;
    private final double ifvVisionRange;
    private final double ifvGroundAttackRange;
    private final double ifvAerialAttackRange;
    private final int ifvGroundDamage;
    private final int ifvAerialDamage;
    private final int ifvGroundDefence;
    private final int ifvAerialDefence;
    private final int ifvAttackCooldownTicks;
    private final int ifvProductionCost;
    private final int arrvDurability;
    private final double arrvSpeed;
    private final double arrvVisionRange;
    private final int arrvGroundDefence;
    private final int arrvAerialDefence;
    private final int arrvProductionCost;
    private final double arrvRepairRange;
    private final double arrvRepairSpeed;
    private final int helicopterDurability;
    private final double helicopterSpeed;
    private final double helicopterVisionRange;
    private final double helicopterGroundAttackRange;
    private final double helicopterAerialAttackRange;
    private final int helicopterGroundDamage;
    private final int helicopterAerialDamage;
    private final int helicopterGroundDefence;
    private final int helicopterAerialDefence;
    private final int helicopterAttackCooldownTicks;
    private final int helicopterProductionCost;
    private final int fighterDurability;
    private final double fighterSpeed;
    private final double fighterVisionRange;
    private final double fighterGroundAttackRange;
    private final double fighterAerialAttackRange;
    private final int fighterGroundDamage;
    private final int fighterAerialDamage;
    private final int fighterGroundDefence;
    private final int fighterAerialDefence;
    private final int fighterAttackCooldownTicks;
    private final int fighterProductionCost;
    private final double maxFacilityCapturePoints;
    private final double facilityCapturePointsPerVehiclePerTick;
    private final double facilityWidth;
    private final double facilityHeight;
    private final int baseTacticalNuclearStrikeCooldown;
    private final int tacticalNuclearStrikeCooldownDecreasePerControlCenter;
    private final double maxTacticalNuclearStrikeDamage;
    private final double tacticalNuclearStrikeRadius;
    private final int tacticalNuclearStrikeDelay;

    @SuppressWarnings("OverlyLongMethod")
    public Game(
            long randomSeed, int tickCount, double worldWidth, double worldHeight, boolean fogOfWarEnabled,
            int victoryScore, int facilityCaptureScore, int vehicleEliminationScore, int actionDetectionInterval,
            int baseActionCount, int additionalActionCountPerControlCenter, int maxUnitGroup,
            int terrainWeatherMapColumnCount, int terrainWeatherMapRowCount, double plainTerrainVisionFactor,
            double plainTerrainStealthFactor, double plainTerrainSpeedFactor, double swampTerrainVisionFactor,
            double swampTerrainStealthFactor, double swampTerrainSpeedFactor, double forestTerrainVisionFactor,
            double forestTerrainStealthFactor, double forestTerrainSpeedFactor, double clearWeatherVisionFactor,
            double clearWeatherStealthFactor, double clearWeatherSpeedFactor, double cloudWeatherVisionFactor,
            double cloudWeatherStealthFactor, double cloudWeatherSpeedFactor, double rainWeatherVisionFactor,
            double rainWeatherStealthFactor, double rainWeatherSpeedFactor, double vehicleRadius, int tankDurability,
            double tankSpeed, double tankVisionRange, double tankGroundAttackRange, double tankAerialAttackRange,
            int tankGroundDamage, int tankAerialDamage, int tankGroundDefence, int tankAerialDefence,
            int tankAttackCooldownTicks, int tankProductionCost, int ifvDurability, double ifvSpeed,
            double ifvVisionRange, double ifvGroundAttackRange, double ifvAerialAttackRange, int ifvGroundDamage,
            int ifvAerialDamage, int ifvGroundDefence, int ifvAerialDefence, int ifvAttackCooldownTicks,
            int ifvProductionCost, int arrvDurability, double arrvSpeed, double arrvVisionRange, int arrvGroundDefence,
            int arrvAerialDefence, int arrvProductionCost, double arrvRepairRange, double arrvRepairSpeed,
            int helicopterDurability, double helicopterSpeed, double helicopterVisionRange,
            double helicopterGroundAttackRange, double helicopterAerialAttackRange, int helicopterGroundDamage,
            int helicopterAerialDamage, int helicopterGroundDefence, int helicopterAerialDefence,
            int helicopterAttackCooldownTicks, int helicopterProductionCost, int fighterDurability, double fighterSpeed,
            double fighterVisionRange, double fighterGroundAttackRange, double fighterAerialAttackRange,
            int fighterGroundDamage, int fighterAerialDamage, int fighterGroundDefence, int fighterAerialDefence,
            int fighterAttackCooldownTicks, int fighterProductionCost, double maxFacilityCapturePoints,
            double facilityCapturePointsPerVehiclePerTick, double facilityWidth, double facilityHeight,
            int baseTacticalNuclearStrikeCooldown, int tacticalNuclearStrikeCooldownDecreasePerControlCenter,
            double maxTacticalNuclearStrikeDamage, double tacticalNuclearStrikeRadius, int tacticalNuclearStrikeDelay) {
        this.randomSeed = randomSeed;
        this.tickCount = tickCount;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.fogOfWarEnabled = fogOfWarEnabled;
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

    public long getRandomSeed() {
        return randomSeed;
    }

    public int getTickCount() {
        return tickCount;
    }

    public double getWorldWidth() {
        return worldWidth;
    }

    public double getWorldHeight() {
        return worldHeight;
    }

    public boolean isFogOfWarEnabled() {
        return fogOfWarEnabled;
    }

    public int getVictoryScore() {
        return victoryScore;
    }

    public int getFacilityCaptureScore() {
        return facilityCaptureScore;
    }

    public int getVehicleEliminationScore() {
        return vehicleEliminationScore;
    }

    public int getActionDetectionInterval() {
        return actionDetectionInterval;
    }

    public int getBaseActionCount() {
        return baseActionCount;
    }

    public int getAdditionalActionCountPerControlCenter() {
        return additionalActionCountPerControlCenter;
    }

    public int getMaxUnitGroup() {
        return maxUnitGroup;
    }

    public int getTerrainWeatherMapColumnCount() {
        return terrainWeatherMapColumnCount;
    }

    public int getTerrainWeatherMapRowCount() {
        return terrainWeatherMapRowCount;
    }

    public double getPlainTerrainVisionFactor() {
        return plainTerrainVisionFactor;
    }

    public double getPlainTerrainStealthFactor() {
        return plainTerrainStealthFactor;
    }

    public double getPlainTerrainSpeedFactor() {
        return plainTerrainSpeedFactor;
    }

    public double getSwampTerrainVisionFactor() {
        return swampTerrainVisionFactor;
    }

    public double getSwampTerrainStealthFactor() {
        return swampTerrainStealthFactor;
    }

    public double getSwampTerrainSpeedFactor() {
        return swampTerrainSpeedFactor;
    }

    public double getForestTerrainVisionFactor() {
        return forestTerrainVisionFactor;
    }

    public double getForestTerrainStealthFactor() {
        return forestTerrainStealthFactor;
    }

    public double getForestTerrainSpeedFactor() {
        return forestTerrainSpeedFactor;
    }

    public double getClearWeatherVisionFactor() {
        return clearWeatherVisionFactor;
    }

    public double getClearWeatherStealthFactor() {
        return clearWeatherStealthFactor;
    }

    public double getClearWeatherSpeedFactor() {
        return clearWeatherSpeedFactor;
    }

    public double getCloudWeatherVisionFactor() {
        return cloudWeatherVisionFactor;
    }

    public double getCloudWeatherStealthFactor() {
        return cloudWeatherStealthFactor;
    }

    public double getCloudWeatherSpeedFactor() {
        return cloudWeatherSpeedFactor;
    }

    public double getRainWeatherVisionFactor() {
        return rainWeatherVisionFactor;
    }

    public double getRainWeatherStealthFactor() {
        return rainWeatherStealthFactor;
    }

    public double getRainWeatherSpeedFactor() {
        return rainWeatherSpeedFactor;
    }

    public double getVehicleRadius() {
        return vehicleRadius;
    }

    public int getTankDurability() {
        return tankDurability;
    }

    public double getTankSpeed() {
        return tankSpeed;
    }

    public double getTankVisionRange() {
        return tankVisionRange;
    }

    public double getTankGroundAttackRange() {
        return tankGroundAttackRange;
    }

    public double getTankAerialAttackRange() {
        return tankAerialAttackRange;
    }

    public int getTankGroundDamage() {
        return tankGroundDamage;
    }

    public int getTankAerialDamage() {
        return tankAerialDamage;
    }

    public int getTankGroundDefence() {
        return tankGroundDefence;
    }

    public int getTankAerialDefence() {
        return tankAerialDefence;
    }

    public int getTankAttackCooldownTicks() {
        return tankAttackCooldownTicks;
    }

    public int getTankProductionCost() {
        return tankProductionCost;
    }

    public int getIfvDurability() {
        return ifvDurability;
    }

    public double getIfvSpeed() {
        return ifvSpeed;
    }

    public double getIfvVisionRange() {
        return ifvVisionRange;
    }

    public double getIfvGroundAttackRange() {
        return ifvGroundAttackRange;
    }

    public double getIfvAerialAttackRange() {
        return ifvAerialAttackRange;
    }

    public int getIfvGroundDamage() {
        return ifvGroundDamage;
    }

    public int getIfvAerialDamage() {
        return ifvAerialDamage;
    }

    public int getIfvGroundDefence() {
        return ifvGroundDefence;
    }

    public int getIfvAerialDefence() {
        return ifvAerialDefence;
    }

    public int getIfvAttackCooldownTicks() {
        return ifvAttackCooldownTicks;
    }

    public int getIfvProductionCost() {
        return ifvProductionCost;
    }

    public int getArrvDurability() {
        return arrvDurability;
    }

    public double getArrvSpeed() {
        return arrvSpeed;
    }

    public double getArrvVisionRange() {
        return arrvVisionRange;
    }

    public int getArrvGroundDefence() {
        return arrvGroundDefence;
    }

    public int getArrvAerialDefence() {
        return arrvAerialDefence;
    }

    public int getArrvProductionCost() {
        return arrvProductionCost;
    }

    public double getArrvRepairRange() {
        return arrvRepairRange;
    }

    public double getArrvRepairSpeed() {
        return arrvRepairSpeed;
    }

    public int getHelicopterDurability() {
        return helicopterDurability;
    }

    public double getHelicopterSpeed() {
        return helicopterSpeed;
    }

    public double getHelicopterVisionRange() {
        return helicopterVisionRange;
    }

    public double getHelicopterGroundAttackRange() {
        return helicopterGroundAttackRange;
    }

    public double getHelicopterAerialAttackRange() {
        return helicopterAerialAttackRange;
    }

    public int getHelicopterGroundDamage() {
        return helicopterGroundDamage;
    }

    public int getHelicopterAerialDamage() {
        return helicopterAerialDamage;
    }

    public int getHelicopterGroundDefence() {
        return helicopterGroundDefence;
    }

    public int getHelicopterAerialDefence() {
        return helicopterAerialDefence;
    }

    public int getHelicopterAttackCooldownTicks() {
        return helicopterAttackCooldownTicks;
    }

    public int getHelicopterProductionCost() {
        return helicopterProductionCost;
    }

    public int getFighterDurability() {
        return fighterDurability;
    }

    public double getFighterSpeed() {
        return fighterSpeed;
    }

    public double getFighterVisionRange() {
        return fighterVisionRange;
    }

    public double getFighterGroundAttackRange() {
        return fighterGroundAttackRange;
    }

    public double getFighterAerialAttackRange() {
        return fighterAerialAttackRange;
    }

    public int getFighterGroundDamage() {
        return fighterGroundDamage;
    }

    public int getFighterAerialDamage() {
        return fighterAerialDamage;
    }

    public int getFighterGroundDefence() {
        return fighterGroundDefence;
    }

    public int getFighterAerialDefence() {
        return fighterAerialDefence;
    }

    public int getFighterAttackCooldownTicks() {
        return fighterAttackCooldownTicks;
    }

    public int getFighterProductionCost() {
        return fighterProductionCost;
    }

    public double getMaxFacilityCapturePoints() {
        return maxFacilityCapturePoints;
    }

    public double getFacilityCapturePointsPerVehiclePerTick() {
        return facilityCapturePointsPerVehiclePerTick;
    }

    public double getFacilityWidth() {
        return facilityWidth;
    }

    public double getFacilityHeight() {
        return facilityHeight;
    }
}
