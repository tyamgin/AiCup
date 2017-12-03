using System.Linq;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public static AMove[] FacilitiesStrategy()
        {
            if (MoveObserver.AvailableActions == 0)
                return null;
            if (Environment.Nuclears.Any(x => x.IsMy)) // лучше приберечь ходы
                return null;

            return _setFactoryProduction();
        }


        private static AMove[] _setFactoryProduction()
        {
            var newFactories = Environment.MyVehicleFactories.Where(f => f.VehicleType == null).ToArray();
            if (newFactories.Length == 0)
                return null;

            var requiredType = Environment.GetVehicles(false, VehicleType.Fighter).Count > 5
                ? VehicleType.Fighter
                : VehicleType.Helicopter;

            return new[]
            {
                new AMove
                {
                    Action = ActionType.SetupVehicleProduction,
                    VehicleType = requiredType,
                    FacilityId = newFactories[0].Id,
                }
            };
        }
    }
}
