using System;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model {
    public class Facility {
        private readonly long id;
        private readonly FacilityType type;
        private readonly long ownerPlayerId;
        private readonly double left;
        private readonly double top;
        private readonly double capturePoints;
        private readonly VehicleType? vehicleType;
        private readonly int productionProgress;

        public Facility(long id, FacilityType type, long ownerPlayerId, double left, double top, double capturePoints,
                VehicleType? vehicleType, int productionProgress) {
            this.id = id;
            this.type = type;
            this.ownerPlayerId = ownerPlayerId;
            this.left = left;
            this.top = top;
            this.capturePoints = capturePoints;
            this.vehicleType = vehicleType;
            this.productionProgress = productionProgress;
        }

        public long Id => id;
        public FacilityType Type => type;
        public long OwnerPlayerId => ownerPlayerId;
        public double Left => left;
        public double Top => top;
        public double CapturePoints => capturePoints;
        public VehicleType? VehicleType => vehicleType;
        public int ProductionProgress => productionProgress;
    }
}