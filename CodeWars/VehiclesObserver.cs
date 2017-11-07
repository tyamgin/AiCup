using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    class VehiclesObserver
    {
        public static Dictionary<long, Vehicle> VehicleById = new Dictionary<long, Vehicle>();

        public static IEnumerable<Vehicle> Vehicles => VehicleById.Values;

        public static void Update()
        {
            foreach (Vehicle vehicle in MyStrategy.World.NewVehicles)
            {
                VehicleById[vehicle.Id] = vehicle;
            }

            foreach (VehicleUpdate vehicleUpdate in MyStrategy.World.VehicleUpdates)
            {
                long vehicleId = vehicleUpdate.Id;

                if (vehicleUpdate.Durability == 0)
                {
                    VehicleById.Remove(vehicleId);
                }
                else
                {
                    VehicleById[vehicleId] = new Vehicle(VehicleById[vehicleId], vehicleUpdate);
                }
            }
        }
    }
}
