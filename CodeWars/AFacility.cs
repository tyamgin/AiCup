using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class AFacility : Rect
    {
        public long Id;
        public bool IsMy, IsOpp;
        public FacilityType Type;
        public double CapturePoints;
        public int ProductionProgress;
        public VehicleType? VehicleType;

        public AFacility(Facility facility)
        {
            X = facility.Left;
            Y = facility.Top;
            Id = facility.Id;
            IsMy = facility.OwnerPlayerId == MyStrategy.Me.Id;
            IsOpp = facility.OwnerPlayerId == MyStrategy.Opp.Id;
            Type = facility.Type;
            CapturePoints = facility.CapturePoints;
            ProductionProgress = facility.ProductionProgress;
            VehicleType = facility.VehicleType;

            X2 = X + G.FacilitySize;
            Y2 = Y + G.FacilitySize;
        }

        public AFacility(AFacility facility)
        {
            X = facility.X;
            Y = facility.Y;
            Id = facility.Id;
            IsMy = facility.IsMy;
            IsOpp = facility.IsOpp;
            Type = facility.Type;
            CapturePoints = facility.CapturePoints;
            ProductionProgress = facility.ProductionProgress;
            VehicleType = facility.VehicleType;

            X2 = X + G.FacilitySize;
            Y2 = Y + G.FacilitySize;
        }

        public void Charge(AVehicle veh)
        {
            if (!veh.CanChargeFacility)
                return;

            if (veh.IsMy)
            {
                if (CapturePoints < G.MaxFacilityCapturePoints && ContainsPoint(veh))
                {
                    CapturePoints += G.FacilityCapturePointsPerVehiclePerTick;
                    if (CapturePoints > G.MaxFacilityCapturePoints)
                        CapturePoints = G.MaxFacilityCapturePoints;
                }
            }
            else
            {
                if (CapturePoints > -G.MaxFacilityCapturePoints && ContainsPoint(veh))
                {
                    CapturePoints -= G.FacilityCapturePointsPerVehiclePerTick;
                    if (CapturePoints < -G.MaxFacilityCapturePoints)
                        CapturePoints = -G.MaxFacilityCapturePoints;
                }
            }
        }

        public bool IsNeutral => !IsMy && !IsOpp;

        public bool IsCompletelyMy => Geom.DoublesEquals(CapturePoints, G.MaxFacilityCapturePoints);

        public bool IsCompletelyOpp => Geom.DoublesEquals(CapturePoints, -G.MaxFacilityCapturePoints);
    }
}
