using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    class VehiclesObserver
    {
        private static Dictionary<long, Vehicle> _vehicleById = new Dictionary<long, Vehicle>();
        public static Dictionary<long, AVehicle> VehicleById = new Dictionary<long, AVehicle>();
        public static Dictionary<long, AVehicle> VehicleById2 = new Dictionary<long, AVehicle>();

        public static AVehicle[] Vehicles = new AVehicle[0];

        public static void Update()
        {
            foreach (Vehicle vehicle in MyStrategy.World.NewVehicles)
            {
                _vehicleById[vehicle.Id] = vehicle;
                VehicleById[vehicle.Id] = new AVehicle(vehicle);
            }

            foreach (VehicleUpdate vehicleUpdate in MyStrategy.World.VehicleUpdates)
            {
                var vehicleId = vehicleUpdate.Id;

                if (vehicleUpdate.Durability == 0)
                {
                    _vehicleById.Remove(vehicleId);
                    VehicleById.Remove(vehicleId);
                }
                else
                {
                    _vehicleById[vehicleId] = new Vehicle(_vehicleById[vehicleId], vehicleUpdate);
                }
            }

            var errorsCount = 0;

            foreach (var veh in _vehicleById.Values)
            {
                var prev = VehicleById2.ContainsKey(veh.Id) ? VehicleById2[veh.Id] : null;
                var cur = VehicleById.ContainsKey(veh.Id) ? VehicleById[veh.Id] : null;
                
                var updatedVehicle = new AVehicle(veh);

                if (updatedVehicle.IsMy)
                {
                    AVehicle aveh = cur;
                    if (prev != null && !Geom.PointsEquals(prev, updatedVehicle)) ; // в local runner сдвинулся
                    else if (!Geom.PointsEquals(cur, updatedVehicle)) // мой сдвинулся, а в local runner нет
                        aveh = prev;

                    if (aveh != null)
                    {
                        updatedVehicle.Speed = aveh.Speed;
                        updatedVehicle.Target = aveh.Target;
                        updatedVehicle.AngularSpeed = aveh.AngularSpeed;
                        updatedVehicle.RotationAngle = aveh.RotationAngle;
                        updatedVehicle.RotationCenter = aveh.RotationCenter;
                    }

                    // дополнительные проверки
                    if (prev != null && !Geom.PointsEquals(prev, updatedVehicle)) // в local runner сдвинулся
                    {
                        var tmp = new AVehicle(cur);
                        if (Geom.PointsEquals(prev, cur))
                        {
                            tmp = new AVehicle(prev);
                            tmp.Move();
                            errorsCount++;
                        }

                        if (!Geom.PointsEquals(tmp, updatedVehicle))
                            Console.WriteLine("Looks vehicle updated wrong (X, Y)");
                    }
                    else if (!Geom.PointsEquals(cur, updatedVehicle)) // мой сдвинулся, а в local runner нет
                    {
                        errorsCount++;
                        var tmp = new AVehicle(updatedVehicle);
                        tmp.Move();
                        if (!Geom.PointsEquals(cur, tmp))
                            Console.WriteLine("Looks vehicle updated wrong (X, Y)");
                    }
                }

                VehicleById[veh.Id] = updatedVehicle;


                if (cur != null && updatedVehicle.IsSelected != cur.IsSelected)
                    Console.WriteLine("Looks vehicle updated wrong (IsSelected)");
            }

            VehicleById2.Clear();

            foreach (var veh in VehicleById.Values)
                VehicleById2[veh.Id] = new AVehicle(veh);

            Vehicles = VehicleById.Values.ToArray();

            if (errorsCount > 0)
                MyStrategy.Log("Move errors count: " + errorsCount);
        }
    }
}
