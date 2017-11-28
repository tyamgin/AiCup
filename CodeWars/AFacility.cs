using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class AFacility : Rect
    {
        public long Id;
        public bool IsMy, IsOpp;
        public FacilityType Type;
        public double CapturePoints;

        public AFacility(Facility facility)
        {
            X = facility.Left;
            Y = facility.Top;
            Id = facility.Id;
            IsMy = facility.OwnerPlayerId == MyStrategy.Me.Id;
            IsOpp = facility.OwnerPlayerId == MyStrategy.Opp.Id;
            Type = facility.Type;
            CapturePoints = facility.CapturePoints;

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

            X2 = X + G.FacilitySize;
            Y2 = Y + G.FacilitySize;
        }

        public bool IsNeutral => !IsMy && !IsOpp;
    }
}
