package model;

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

    public long getId() {
        return id;
    }

    public FacilityType getType() {
        return type;
    }

    public long getOwnerPlayerId() {
        return ownerPlayerId;
    }

    public double getLeft() {
        return left;
    }

    public double getTop() {
        return top;
    }

    public double getCapturePoints() {
        return capturePoints;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public int getProductionProgress() {
        return productionProgress;
    }
}
