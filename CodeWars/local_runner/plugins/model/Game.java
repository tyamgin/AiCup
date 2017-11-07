package model;

/**
 * Предоставляет доступ к различным игровым константам.
 */
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
            double facilityCapturePointsPerVehiclePerTick, double facilityWidth, double facilityHeight) {
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
    }

    /**
     * @return Возвращает некоторое число, которое ваша стратегия может использовать для инициализации генератора
     * случайных чисел. Данное значение имеет рекомендательный характер, однако позволит более точно воспроизводить
     * прошедшие игры.
     */
    public long getRandomSeed() {
        return randomSeed;
    }

    /**
     * @return Возвращает базовую длительность игры в тиках. Реальная длительность может отличаться от этого значения в
     * меньшую сторону. Эквивалентно {@code world.tickCount}.
     */
    public int getTickCount() {
        return tickCount;
    }

    /**
     * @return Возвращает ширину карты.
     */
    public double getWorldWidth() {
        return worldWidth;
    }

    /**
     * @return Возвращает высоту карты.
     */
    public double getWorldHeight() {
        return worldHeight;
    }

    /**
     * @return Возвращает {@code true}, если и только если в данной игре включен режим частичной видимости.
     */
    public boolean isFogOfWarEnabled() {
        return fogOfWarEnabled;
    }

    /**
     * @return Возвращает количество баллов, получаемое игроком в случае уничтожения всех юнитов противника.
     */
    public int getVictoryScore() {
        return victoryScore;
    }

    /**
     * @return Возвращает количество баллов за захват сооружения.
     */
    public int getFacilityCaptureScore() {
        return facilityCaptureScore;
    }

    /**
     * @return Возвращает количество баллов за уничтожение юнита противника.
     */
    public int getVehicleEliminationScore() {
        return vehicleEliminationScore;
    }

    /**
     * @return Возвращает интервал, учитываемый в ограничении количества действий стратегии.
     */
    public int getActionDetectionInterval() {
        return actionDetectionInterval;
    }

    /**
     * @return Возвращает базовое количество действий, которое может совершить стратегия за
     * {@code actionDetectionInterval} последовательных тиков.
     */
    public int getBaseActionCount() {
        return baseActionCount;
    }

    /**
     * @return Возвращает дополнительное количество действий за каждый захваченный центр управления
     * ({@code FacilityType.CONTROL_CENTER}).
     */
    public int getAdditionalActionCountPerControlCenter() {
        return additionalActionCountPerControlCenter;
    }

    /**
     * @return Возвращает максимально возможный индекс группы юнитов.
     */
    public int getMaxUnitGroup() {
        return maxUnitGroup;
    }

    /**
     * @return Возвращает количество столбцов в картах местности и погоды.
     */
    public int getTerrainWeatherMapColumnCount() {
        return terrainWeatherMapColumnCount;
    }

    /**
     * @return Возвращает количество строк в картах местности и погоды.
     */
    public int getTerrainWeatherMapRowCount() {
        return terrainWeatherMapRowCount;
    }

    /**
     * @return Возвращает мультипликатор радиуса обзора наземной техники, находящейся на равнинной местности
     * ({@code TerrainType.PLAIN}).
     */
    public double getPlainTerrainVisionFactor() {
        return plainTerrainVisionFactor;
    }

    /**
     * @return Возвращает мультипликатор радиуса обзора любой техники при обнаружении наземной техники противника,
     * находящейся на равнинной местности ({@code TerrainType.PLAIN}).
     */
    public double getPlainTerrainStealthFactor() {
        return plainTerrainStealthFactor;
    }

    /**
     * @return Возвращает мультипликатор максимальной скорости наземной техники, находящейся на равнинной местности
     * ({@code TerrainType.PLAIN}).
     */
    public double getPlainTerrainSpeedFactor() {
        return plainTerrainSpeedFactor;
    }

    /**
     * @return Возвращает мультипликатор радиуса обзора наземной техники, находящейся в болотистой местности
     * ({@code TerrainType.SWAMP}).
     */
    public double getSwampTerrainVisionFactor() {
        return swampTerrainVisionFactor;
    }

    /**
     * @return Возвращает мультипликатор радиуса обзора любой техники при обнаружении наземной техники противника,
     * находящейся в болотистой местности ({@code TerrainType.SWAMP}).
     */
    public double getSwampTerrainStealthFactor() {
        return swampTerrainStealthFactor;
    }

    /**
     * @return Возвращает мультипликатор максимальной скорости наземной техники, находящейся в болотистой местности
     * ({@code TerrainType.SWAMP}).
     */
    public double getSwampTerrainSpeedFactor() {
        return swampTerrainSpeedFactor;
    }

    /**
     * @return Возвращает мультипликатор радиуса обзора наземной техники, находящейся в лесистой местности
     * ({@code TerrainType.FOREST}).
     */
    public double getForestTerrainVisionFactor() {
        return forestTerrainVisionFactor;
    }

    /**
     * @return Возвращает мультипликатор радиуса обзора любой техники при обнаружении наземной техники противника,
     * находящейся в лесистой местности ({@code TerrainType.FOREST}).
     */
    public double getForestTerrainStealthFactor() {
        return forestTerrainStealthFactor;
    }

    /**
     * @return Возвращает мультипликатор максимальной скорости наземной техники, находящейся в лесистой местности
     * ({@code TerrainType.FOREST}).
     */
    public double getForestTerrainSpeedFactor() {
        return forestTerrainSpeedFactor;
    }

    /**
     * @return Возвращает мультипликатор радиуса обзора воздушной техники, находящейся в области ясной погоды
     * ({@code WeatherType.CLEAR}).
     */
    public double getClearWeatherVisionFactor() {
        return clearWeatherVisionFactor;
    }

    /**
     * @return Возвращает мультипликатор радиуса обзора любой техники при обнаружении воздушной техники противника,
     * находящейся в области ясной погоды ({@code WeatherType.CLEAR}).
     */
    public double getClearWeatherStealthFactor() {
        return clearWeatherStealthFactor;
    }

    /**
     * @return Возвращает мультипликатор максимальной скорости воздушной техники, находящейся в области ясной погоды
     * ({@code WeatherType.CLEAR}).
     */
    public double getClearWeatherSpeedFactor() {
        return clearWeatherSpeedFactor;
    }

    /**
     * @return Возвращает мультипликатор радиуса обзора воздушной техники, находящейся в плотных облаках
     * ({@code WeatherType.CLOUD}).
     */
    public double getCloudWeatherVisionFactor() {
        return cloudWeatherVisionFactor;
    }

    /**
     * @return Возвращает мультипликатор радиуса обзора любой техники при обнаружении воздушной техники противника,
     * находящейся в плотных облаках ({@code WeatherType.CLOUD}).
     */
    public double getCloudWeatherStealthFactor() {
        return cloudWeatherStealthFactor;
    }

    /**
     * @return Возвращает мультипликатор максимальной скорости воздушной техники, находящейся в плотных облаках
     * ({@code WeatherType.CLOUD}).
     */
    public double getCloudWeatherSpeedFactor() {
        return cloudWeatherSpeedFactor;
    }

    /**
     * @return Возвращает мультипликатор радиуса обзора воздушной техники, находящейся в условиях сильного дождя
     * ({@code WeatherType.RAIN}).
     */
    public double getRainWeatherVisionFactor() {
        return rainWeatherVisionFactor;
    }

    /**
     * @return Возвращает мультипликатор радиуса обзора любой техники при обнаружении воздушной техники противника,
     * находящейся в условиях сильного дождя ({@code WeatherType.RAIN}).
     */
    public double getRainWeatherStealthFactor() {
        return rainWeatherStealthFactor;
    }

    /**
     * @return Возвращает мультипликатор максимальной скорости воздушной техники, находящейся в условиях сильного дождя
     * ({@code WeatherType.RAIN}).
     */
    public double getRainWeatherSpeedFactor() {
        return rainWeatherSpeedFactor;
    }

    /**
     * @return Возвращает радиус техники.
     */
    public double getVehicleRadius() {
        return vehicleRadius;
    }

    /**
     * @return Возвращает максимальную прочность танка.
     */
    public int getTankDurability() {
        return tankDurability;
    }

    /**
     * @return Возвращает максимальную скорость танка.
     */
    public double getTankSpeed() {
        return tankSpeed;
    }

    /**
     * @return Возвращает базовый радиус обзора танка.
     */
    public double getTankVisionRange() {
        return tankVisionRange;
    }

    /**
     * @return Возвращает дальность атаки танка по наземным целям.
     */
    public double getTankGroundAttackRange() {
        return tankGroundAttackRange;
    }

    /**
     * @return Возвращает дальность атаки танка по воздушным целям.
     */
    public double getTankAerialAttackRange() {
        return tankAerialAttackRange;
    }

    /**
     * @return Возвращает урон одной атаки танка по наземной технике.
     */
    public int getTankGroundDamage() {
        return tankGroundDamage;
    }

    /**
     * @return Возвращает урон одной атаки танка по воздушной технике.
     */
    public int getTankAerialDamage() {
        return tankAerialDamage;
    }

    /**
     * @return Возвращает защиту танка от атак наземной техники.
     */
    public int getTankGroundDefence() {
        return tankGroundDefence;
    }

    /**
     * @return Возвращает защиту танка от атак воздушной техники.
     */
    public int getTankAerialDefence() {
        return tankAerialDefence;
    }

    /**
     * @return Возвращает интервал в тиках между двумя последовательными атаками танка.
     */
    public int getTankAttackCooldownTicks() {
        return tankAttackCooldownTicks;
    }

    /**
     * @return Возвращает количество тиков, необхожимое для производства одного танка на заводе
     * ({@code FacilityType.VEHICLE_FACTORY}).
     */
    public int getTankProductionCost() {
        return tankProductionCost;
    }

    /**
     * @return Возвращает максимальную прочность БМП.
     */
    public int getIfvDurability() {
        return ifvDurability;
    }

    /**
     * @return Возвращает максимальную скорость БМП.
     */
    public double getIfvSpeed() {
        return ifvSpeed;
    }

    /**
     * @return Возвращает базовый радиус обзора БМП.
     */
    public double getIfvVisionRange() {
        return ifvVisionRange;
    }

    /**
     * @return Возвращает дальность атаки БМП по наземным целям.
     */
    public double getIfvGroundAttackRange() {
        return ifvGroundAttackRange;
    }

    /**
     * @return Возвращает дальность атаки БМП по воздушным целям.
     */
    public double getIfvAerialAttackRange() {
        return ifvAerialAttackRange;
    }

    /**
     * @return Возвращает урон одной атаки БМП по наземной технике.
     */
    public int getIfvGroundDamage() {
        return ifvGroundDamage;
    }

    /**
     * @return Возвращает урон одной атаки БМП по воздушной технике.
     */
    public int getIfvAerialDamage() {
        return ifvAerialDamage;
    }

    /**
     * @return Возвращает защиту БМП от атак наземной техники.
     */
    public int getIfvGroundDefence() {
        return ifvGroundDefence;
    }

    /**
     * @return Возвращает защиту БМП от атак воздушной техники.
     */
    public int getIfvAerialDefence() {
        return ifvAerialDefence;
    }

    /**
     * @return Возвращает интервал в тиках между двумя последовательными атаками БМП.
     */
    public int getIfvAttackCooldownTicks() {
        return ifvAttackCooldownTicks;
    }

    /**
     * @return Возвращает количество тиков, необхожимое для производства одной БМП на заводе
     * ({@code FacilityType.VEHICLE_FACTORY}).
     */
    public int getIfvProductionCost() {
        return ifvProductionCost;
    }

    /**
     * @return Возвращает максимальную прочность БРЭМ.
     */
    public int getArrvDurability() {
        return arrvDurability;
    }

    /**
     * @return Возвращает максимальную скорость БРЭМ.
     */
    public double getArrvSpeed() {
        return arrvSpeed;
    }

    /**
     * @return Возвращает базовый радиус обзора БРЭМ.
     */
    public double getArrvVisionRange() {
        return arrvVisionRange;
    }

    /**
     * @return Возвращает защиту БРЭМ от атак наземной техники.
     */
    public int getArrvGroundDefence() {
        return arrvGroundDefence;
    }

    /**
     * @return Возвращает защиту БРЭМ от атак воздушной техники.
     */
    public int getArrvAerialDefence() {
        return arrvAerialDefence;
    }

    /**
     * @return Возвращает количество тиков, необхожимое для производства одной БРЭМ на заводе
     * ({@code FacilityType.VEHICLE_FACTORY}).
     */
    public int getArrvProductionCost() {
        return arrvProductionCost;
    }

    /**
     * @return Возвращает максимальное расстояние (от центра до центра), на котором БРЭМ может ремонтировать
     * дружественную технику.
     */
    public double getArrvRepairRange() {
        return arrvRepairRange;
    }

    /**
     * @return Возвращает максимальное количество прочности, которое БРЭМ может восстановить дружественной технике за
     * один тик.
     */
    public double getArrvRepairSpeed() {
        return arrvRepairSpeed;
    }

    /**
     * @return Возвращает максимальную прочность ударного вертолёта.
     */
    public int getHelicopterDurability() {
        return helicopterDurability;
    }

    /**
     * @return Возвращает максимальную скорость ударного вертолёта.
     */
    public double getHelicopterSpeed() {
        return helicopterSpeed;
    }

    /**
     * @return Возвращает базовый радиус обзора ударного вертолёта.
     */
    public double getHelicopterVisionRange() {
        return helicopterVisionRange;
    }

    /**
     * @return Возвращает дальность атаки ударного вертолёта по наземным целям.
     */
    public double getHelicopterGroundAttackRange() {
        return helicopterGroundAttackRange;
    }

    /**
     * @return Возвращает дальность атаки ударного вертолёта по воздушным целям.
     */
    public double getHelicopterAerialAttackRange() {
        return helicopterAerialAttackRange;
    }

    /**
     * @return Возвращает урон одной атаки ударного вертолёта по наземной технике.
     */
    public int getHelicopterGroundDamage() {
        return helicopterGroundDamage;
    }

    /**
     * @return Возвращает урон одной атаки ударного вертолёта по воздушной технике.
     */
    public int getHelicopterAerialDamage() {
        return helicopterAerialDamage;
    }

    /**
     * @return Возвращает защиту ударного вертолёта от атак наземной техники.
     */
    public int getHelicopterGroundDefence() {
        return helicopterGroundDefence;
    }

    /**
     * @return Возвращает защиту ударного вертолёта от атак воздушной техники.
     */
    public int getHelicopterAerialDefence() {
        return helicopterAerialDefence;
    }

    /**
     * @return Возвращает интервал в тиках между двумя последовательными атаками ударного вертолёта.
     */
    public int getHelicopterAttackCooldownTicks() {
        return helicopterAttackCooldownTicks;
    }

    /**
     * @return Возвращает количество тиков, необхожимое для производства одного ударного вертолёта на заводе
     * ({@code FacilityType.VEHICLE_FACTORY}).
     */
    public int getHelicopterProductionCost() {
        return helicopterProductionCost;
    }

    /**
     * @return Возвращает максимальную прочность истребителя.
     */
    public int getFighterDurability() {
        return fighterDurability;
    }

    /**
     * @return Возвращает максимальную скорость истребителя.
     */
    public double getFighterSpeed() {
        return fighterSpeed;
    }

    /**
     * @return Возвращает базовый радиус обзора истребителя.
     */
    public double getFighterVisionRange() {
        return fighterVisionRange;
    }

    /**
     * @return Возвращает дальность атаки истребителя по наземным целям.
     */
    public double getFighterGroundAttackRange() {
        return fighterGroundAttackRange;
    }

    /**
     * @return Возвращает дальность атаки истребителя по воздушным целям.
     */
    public double getFighterAerialAttackRange() {
        return fighterAerialAttackRange;
    }

    /**
     * @return Возвращает урон одной атаки истребителя по наземной технике.
     */
    public int getFighterGroundDamage() {
        return fighterGroundDamage;
    }

    /**
     * @return Возвращает урон одной атаки истребителя по воздушной технике.
     */
    public int getFighterAerialDamage() {
        return fighterAerialDamage;
    }

    /**
     * @return Возвращает защиту истребителя от атак наземной техники.
     */
    public int getFighterGroundDefence() {
        return fighterGroundDefence;
    }

    /**
     * @return Возвращает защиту истребителя от атак воздушной техники.
     */
    public int getFighterAerialDefence() {
        return fighterAerialDefence;
    }

    /**
     * @return Возвращает интервал в тиках между двумя последовательными атаками истребителя.
     */
    public int getFighterAttackCooldownTicks() {
        return fighterAttackCooldownTicks;
    }

    /**
     * @return Возвращает количество тиков, необхожимое для производства одного истребителя на заводе
     * ({@code FacilityType.VEHICLE_FACTORY}).
     */
    public int getFighterProductionCost() {
        return fighterProductionCost;
    }

    /**
     * @return Возвращает максимально возможную абсолютную величину индикатора захвата сооружения
     * ({@code facility.capturePoints}).
     */
    public double getMaxFacilityCapturePoints() {
        return maxFacilityCapturePoints;
    }

    /**
     * @return Возвращает скорость изменения индикатора захвата сооружения ({@code facility.capturePoints}) за каждую
     * единицу техники, центр которой находится внутри сооружения.
     */
    public double getFacilityCapturePointsPerVehiclePerTick() {
        return facilityCapturePointsPerVehiclePerTick;
    }

    /**
     * @return Возвращает ширину сооружения.
     */
    public double getFacilityWidth() {
        return facilityWidth;
    }

    /**
     * @return Возвращает высоту сооружения.
     */
    public double getFacilityHeight() {
        return facilityHeight;
    }
}
