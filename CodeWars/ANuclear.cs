namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class ANuclear : ACircularUnit
    {
        public bool IsMy;
        public long VehicleId;
        public int RemainingTicks;

        public ANuclear(double x, double y, bool isMy, long vehicleId, int remainingTicks)
        {
            X = x;
            Y = y;
            Radius = G.TacticalNuclearStrikeRadius;
            IsMy = isMy;
            VehicleId = vehicleId;
            RemainingTicks = remainingTicks;
        }

        public ANuclear(ANuclear nuclear)
        {
            X = nuclear.X;
            Y = nuclear.Y;
            Radius = G.TacticalNuclearStrikeRadius;
            IsMy = nuclear.IsMy;
            VehicleId = nuclear.VehicleId;
            RemainingTicks = nuclear.RemainingTicks;
        }
    }
}
