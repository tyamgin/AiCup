using System.Linq;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public static AMove FacilitiesStrategy()
        {
            if (MoveObserver.AvailableActions == 0)
                return null;
            if (Environment.Nuclears.Any(x => x.IsMy))
                return null;

            var newFactories = Environment.MyVehicleFactories.Where(f => f.VehicleType == null).ToArray();
            if (newFactories.Length == 0)
                return null;

            var requiredType = Environment.GetVehicles(true, VehicleType.Fighter).Count <
                               Environment.GetVehicles(true, VehicleType.Helicopter).Count
                ? VehicleType.Fighter
                : VehicleType.Helicopter;

            return new AMove
            {
                Action = ActionType.SetupVehicleProduction,
                VehicleType = requiredType,
                FacilityId = newFactories[0].Id,
            };
        }
    }
}
