using System;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    class VehiclesObserver
    {
        private static readonly Dictionary<long, Vehicle> _vehicleById = new Dictionary<long, Vehicle>();
        public static Dictionary<long, AVehicle> VehicleById = new Dictionary<long, AVehicle>();

        public static Dictionary<long, AVehicle> OppUncheckedVehicles = new Dictionary<long, AVehicle>();
        public static Dictionary<long, AVehicle> OppCheckedVehicles = new Dictionary<long, AVehicle>();

        private static readonly List<long> _disappearedIds = new List<long>(); 
        private static readonly HashSet<long> _visibleOnce = new HashSet<long>(); 

        public static void Update()
        {
            _disappearedIds.Clear();
            foreach (Vehicle vehicle in MyStrategy.World.NewVehicles)
            {
                _vehicleById[vehicle.Id] = vehicle;
                VehicleById[vehicle.Id] = new AVehicle(vehicle);

                if (G.IsFogOfWarEnabled && vehicle.PlayerId != MyStrategy.Me.Id)
                {
                    if (vehicle.Id <= 1000 && !_visibleOnce.Contains(vehicle.Id))
                    {
                        var fakeId = _idsPool[(int) vehicle.Type].Last();
                        _idsPool[(int)vehicle.Type].Pop();

                        OppUncheckedVehicles.Remove(fakeId);
                        OppCheckedVehicles.Remove(fakeId);
                    }

                    OppCheckedVehicles.Remove(vehicle.Id);
                    OppUncheckedVehicles.Remove(vehicle.Id);

                    _visibleOnce.Add(vehicle.Id);
                }
            }

            foreach (VehicleUpdate vehicleUpdate in MyStrategy.World.VehicleUpdates)
            {
                var vehicleId = vehicleUpdate.Id;

                if (vehicleUpdate.Durability == 0)
                {
                    _vehicleById.Remove(vehicleId);
                    VehicleById.Remove(vehicleId);

                    _disappearedIds.Add(vehicleId);
                }
                else
                {
                    var veh = new Vehicle(_vehicleById[vehicleId], vehicleUpdate);
                    _vehicleById[vehicleId] = veh;
                    if (veh.PlayerId != MyStrategy.Me.Id)
                    {
                        OppCheckedVehicles.Remove(veh.Id);
                        OppUncheckedVehicles.Remove(veh.Id);
                    }
                }
            }

            var errorsCount = 0;

            foreach (var veh in _vehicleById.Values)
            {
                var prev = MoveObserver.BeforeMoveUnits.ContainsKey(veh.Id) ? MoveObserver.BeforeMoveUnits[veh.Id] : null;
                var cur = VehicleById.ContainsKey(veh.Id) ? VehicleById[veh.Id] : null;
                
                var updatedVehicle = new AVehicle(veh);

                if (updatedVehicle.IsMy)
                {
                    if (prev != null)
                    {
                        AVehicle aveh = new AVehicle(prev);
                        if (!Geom.PointsEquals(prev, updatedVehicle)) // в local runner сдвинулся
                            aveh.Move();
                        if (!Geom.PointsEquals(aveh, updatedVehicle))
                            Console.WriteLine("Looks vehicle updated wrong (X, Y)");

                        if ((aveh.Action == AVehicle.MoveType.Move || aveh.Action == AVehicle.MoveType.Scale) && Geom.DoublesEquals(aveh.MoveLengthOrRotationAngle, 0) ||
                            aveh.Action == AVehicle.MoveType.Rotate && Geom.DoublesEquals(aveh.MoveLengthOrRotationAngle, 0))
                        {
                            aveh.ForgotTarget();
                        }
                        updatedVehicle.Action = aveh.Action;
                        updatedVehicle.MoveVectorOrRotationCenter = aveh.MoveVectorOrRotationCenter;
                        updatedVehicle.MoveSpeedOrAngularSpeed = aveh.MoveSpeedOrAngularSpeed;
                        updatedVehicle.MoveLengthOrRotationAngle = aveh.MoveLengthOrRotationAngle;
                        updatedVehicle.DurabilityPool = cur.DurabilityPool;
                        //TODO: поддерживать DurabilityPool if (!updatedVehicle.IsMy)
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

            if (errorsCount > 0)
                Logger.Log("Move errors count: " + errorsCount);
        }

        private static bool _isVehicleVisible(Sandbox env, AVehicle veh)
        {
            foreach (var x in env.GetMyNeighbours(veh.X, veh.Y, 120 * veh.StealthFactor))
                if (x.IsVisible(veh))
                    return true;
            return false;
        }

        public static void Update2(Sandbox prevEnv, Sandbox curEnv)
        {
            if (G.IsFogOfWarEnabled)
            {
                foreach (var veh in OppUncheckedVehicles.Values.ToArray())
                {
                    if (_isVehicleVisible(curEnv, veh))
                    {
                        OppCheckedVehicles.Add(veh.Id, veh);
                        OppUncheckedVehicles.Remove(veh.Id);
                    }
                }

                foreach (var disappearedId in _disappearedIds)
                {
                    if (curEnv.VehicleById.ContainsKey(disappearedId))
                        throw new Exception("Something wrong");

                    var veh = prevEnv.VehicleById[disappearedId];
                    if (_isVehicleVisible(curEnv, veh))
                    {
                        // убит
                    }
                    else
                    {
                        OppUncheckedVehicles[veh.Id] = veh;
                    }
                }

                if (MyStrategy.World.TickIndex == 0)
                    _fillStartingPosition(curEnv.MyVehicles);
            }

            if (MyStrategy.World.TickIndex > 0)
                _facilitiesProduction(prevEnv, curEnv);
        }

        private static void _fillStartingPosition(IEnumerable<AVehicle> myVehicles)
        {
            foreach (var my in myVehicles)
            {
                var clone = new AVehicle(my);
                clone.X = G.MapSize - clone.X;
                clone.Y = G.MapSize - clone.Y;
                clone.IsMy = !clone.IsMy;
                clone.Id *= -1;

                OppUncheckedVehicles[clone.Id] = clone;
                _idsPool[(int) clone.Type].Add(clone.Id);
            }
        }

        private static long _nextFreeId = 1001;

        private static AVehicle _doFacilityProd(Sandbox env, AFacility facility)
        {
            var vehicleType = facility.VehicleType.Value;
            var isMy = facility.IsMy;

            var pt = new ACircularUnit();
            pt.Radius = G.VehicleRadius;

            for (var j = 0; j < 11; j++)
            {
                for (var i = 0; i < 11; i++)
                {
                    if (facility.IsMy)
                    {
                        pt.X = facility.X + i*6 + G.VehicleRadius;
                        pt.Y = facility.Y + j*6 + G.VehicleRadius;
                    }
                    else
                    {
                        pt.X = facility.X2 - i*6 - G.VehicleRadius;
                        pt.Y = facility.Y2 - j*6 - G.VehicleRadius;
                    }

                    var nearby = env.GetFirstIntersector(pt, Utility.IsAerial(vehicleType));
                    if (nearby == null && !facility.IsMy)
                    {
                        nearby = OppUncheckedVehicles.Values
                            .Where(x => Utility.IsAerial(x.Type) == Utility.IsAerial(vehicleType))
                            .ArgMin(x => x.GetDistanceTo2(pt));
                    }

                    if (nearby == null || !nearby.IntersectsWith(pt) || nearby.Id == _nextFreeId && Geom.PointsEquals(nearby, pt))
                    {
                        return new AVehicle(pt, _nextFreeId++, vehicleType, isMy);
                    }
                }
            }
            return null;
        }

        private static void _facilitiesProduction(Sandbox prevEnv, Sandbox curEnv)
        {
            // ReSharper disable once CollectionNeverQueried.Local
            var newVehiclesList = new List<AVehicle>();
            for (var i = 0; i < curEnv.Facilities.Length; i++)
            {
                var prevState = prevEnv.Facilities[i];
                if (prevState.Type != FacilityType.VehicleFactory)
                    continue;
                var curState = curEnv.Facilities[i];

                var type = prevState.VehicleType;
                if (type != null && prevState.ProductionProgress == G.ProductionCost[(int) type] - 1
                    && curState.VehicleType == type && prevState.IsMy == curState.IsMy)
                {
                    Logger.CumulativeOperationStart("Do production");
                    var newVehicle = _doFacilityProd(curEnv, prevState);
                    Logger.CumulativeOperationEnd("Do production");
                    if (newVehicle != null)
                    {
                        newVehiclesList.Add(newVehicle);
                        if (curEnv.VehicleById.ContainsKey(newVehicle.Id))
                        {
                            var actualVehicle = curEnv.VehicleById[newVehicle.Id];
                            if (!Geom.PointsEquals(actualVehicle, newVehicle) || actualVehicle.Type != newVehicle.Type || actualVehicle.IsMy != newVehicle.IsMy)
                                Console.WriteLine("Vehicle production simulation error");
                        }
                        else
                        {
                            if (newVehicle.IsMy)
                                Console.WriteLine("Vehicle production simulation error (my)");
                            else
                            {
                                if (G.IsFogOfWarEnabled)
                                    OppUncheckedVehicles[newVehicle.Id] = newVehicle;
                            }
                        }
                    }
                } 
            }
        }

        private static readonly List<long>[] _idsPool =
        {
            new List<long>(),
            new List<long>(),
            new List<long>(),
            new List<long>(),
            new List<long>(),
        };
    }
}
