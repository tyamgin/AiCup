package model;

/**
 * Класс, определяющий сооружение --- прямоугольную область на карте.
 */
public class Facility {
    private final long id;
    private final FacilityType type;
    private final long ownerPlayerId;
    private final double left;
    private final double top;
    private final double capturePoints;
    private final VehicleType vehicleType;
    private final int productionProgress;

    public Facility(
            long id, FacilityType type, long ownerPlayerId, double left, double top, double capturePoints,
            VehicleType vehicleType, int productionProgress) {
        this.id = id;
        this.type = type;
        this.ownerPlayerId = ownerPlayerId;
        this.left = left;
        this.top = top;
        this.capturePoints = capturePoints;
        this.vehicleType = vehicleType;
        this.productionProgress = productionProgress;
    }

    /**
     * @return Возвращает уникальный идентификатор сооружения.
     */
    public long getId() {
        return id;
    }

    /**
     * @return Возвращает тип сооружения.
     */
    public FacilityType getType() {
        return type;
    }

    /**
     * @return Возвращает идентификатор игрока, захватившего сооружение, или {@code -1}, если сооружение никем не
     * контролируется.
     */
    public long getOwnerPlayerId() {
        return ownerPlayerId;
    }

    /**
     * @return Возвращает абсциссу левой границы сооружения.
     */
    public double getLeft() {
        return left;
    }

    /**
     * @return Возвращает ординату верхней границы сооружения.
     */
    public double getTop() {
        return top;
    }

    /**
     * @return Возвращает индикатор захвата сооружения в интервале от {@code -game.maxFacilityCapturePoints} до
     * {@code game.maxFacilityCapturePoints}. Если индикатор находится в положительной зоне, очки захвата принадлежат
     * вам, иначе вашему противнику.
     */
    public double getCapturePoints() {
        return capturePoints;
    }

    /**
     * @return Возвращает тип техники, производящейся в данном сооружении, или {@code null}. Применимо только к заводу
     * ({@code FacilityType.VEHICLE_FACTORY}).
     */
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    /**
     * @return Возвращает неотрицательное число --- прогресс производства техники. Применимо только к заводу
     * ({@code FacilityType.VEHICLE_FACTORY}).
     */
    public int getProductionProgress() {
        return productionProgress;
    }
}
