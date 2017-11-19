using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Authentication.ExtendedProtection;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class Sandbox
    {
        public ANuclear[] Nuclears;
        public readonly Dictionary<long, AVehicle> VehicleById = new Dictionary<long, AVehicle>();
        public bool CheckCollisionsWithOpponent = true;

        private readonly List<AVehicle>[] _vehiclesByOwner =
        {
            new List<AVehicle>(),
            new List<AVehicle>()
        };

        private readonly List<AVehicle>[][] _vehiclesByOwnerAndType =
        {
            new [] { new List<AVehicle>(), new List<AVehicle>(), new List<AVehicle>(), new List<AVehicle>(), new List<AVehicle>() },
            new [] { new List<AVehicle>(), new List<AVehicle>(), new List<AVehicle>(), new List<AVehicle>(), new List<AVehicle>() },
        };

        private readonly QuadTree<AVehicle>[,] _trees = new QuadTree<AVehicle>[2, 2];

        private readonly List<AVehicle>[] _myVehiclesByGroup = new List<AVehicle>[2]
        {
            new List<AVehicle>(),
            new List<AVehicle>() 
        };

        private List<AVehicle> _nearestCache = new List<AVehicle>();

        private static AVehicle _cloneVehicle(AVehicle vehicle)
        {
            return new AVehicle(vehicle);
        }

        private List<AVehicle> _at(bool isMy)
        {
            return _vehiclesByOwner[isMy ? 1 : 0];
        }

        private QuadTree<AVehicle> _tree(bool isMy, bool isAerial)
        {
            var tree = _trees[isMy ? 1 : 0, isAerial ? 1 : 0];
            if (tree == null)
            {
                tree = new QuadTree<AVehicle>(0, 0, G.MapSize, G.MapSize, Const.Eps, _cloneVehicle);
                tree.AddRange(_vehiclesByOwner[isMy ? 1 : 0].Where(x => x.IsAerial == isAerial));
                _trees[isMy ? 1 : 0, isAerial ? 1 : 0] = tree;
            }
            return tree;
        }

        public List<AVehicle> GetVehicles(bool isMy, VehicleType type)
        {
            return _vehiclesByOwnerAndType[isMy ? 1 : 0][(int) type];
        }

        public List<AVehicle> GetVehicles(bool isMy, MyGroup group)
        {
            if (group.Type != null)
                return _vehiclesByOwnerAndType[isMy ? 1 : 0][(int) group.Type];
            if (group.Group == null)
                return new List<AVehicle>();
            if (!isMy)
                throw new Exception("Trying to access not my group");
            return _myVehiclesByGroup[(int) group.Group - 1];
        }

        public IEnumerable<AVehicle> MyVehicles => _at(true);

        public IEnumerable<AVehicle> OppVehicles => _at(false);

        public readonly List<AVehicle> Vehicles = new List<AVehicle>();

        public Sandbox(IEnumerable<AVehicle> vehicles, IEnumerable<ANuclear> nuclears, bool clone = false)
        {
            if (clone)
            {
                vehicles = vehicles.Select(x => new AVehicle(x));
                nuclears = nuclears.Select(x => new ANuclear(x));
            }

            //_nearestCache.Capacity = 50;
            Nuclears = nuclears.ToArray();
            AddRange(vehicles);
        }

        public void AddRange(IEnumerable<AVehicle> vehicles)
        {
            foreach (var veh in vehicles)
                Add(veh);
        }

        public void Add(AVehicle veh)
        {
            Vehicles.Add(veh);
            _nearestCache.Add(null);
            _at(veh.IsMy).Add(veh);
            _vehiclesByOwnerAndType[veh.IsMy ? 1 : 0][(int)veh.Type].Add(veh);
            VehicleById[veh.Id] = veh;
            if (veh.IsMy)
            {
                if (veh.HasGroup(1))
                    _myVehiclesByGroup[0].Add(veh);
                if (veh.HasGroup(2))
                    _myVehiclesByGroup[1].Add(veh);
                // TODO
            }
            var tree = _trees[veh.IsMy ? 1 : 0, veh.IsAerial ? 1 : 0];
            if (tree != null)
                tree.Add(veh);
        }

        public Sandbox Clone()
        {
            // TODO: use QuadTree.Clone()
            return new Sandbox(Vehicles.Select(x => new AVehicle(x)), Nuclears.Select(x => new ANuclear(x)));
        }

        public void ApplyMove(Move move)
        {
            // TODO: проверки на валидность

            switch (move.Action)
            {
                case ActionType.ClearAndSelect:
                    foreach (var unit in Vehicles)
                    {
                        if (!unit.IsMy)
                            continue;
                        if (move.Group != 0)
                        {
                            unit.IsSelected = unit.HasGroup(move.Group);
                        }
                        else
                        {
                            unit.IsSelected = Geom.Between(move.Left, move.Right, unit.X) &&
                                              Geom.Between(move.Top, move.Bottom, unit.Y) &&
                                              (move.VehicleType == null || move.VehicleType == unit.Type);
                        }
                    }
                    break;
                case ActionType.AddToSelection:
                    foreach (var unit in Vehicles)
                    {
                        if (!unit.IsMy)
                            continue;
                        if (move.Group != 0)
                        {
                            unit.IsSelected = unit.HasGroup(move.Group);
                        }
                        else
                        {
                            unit.IsSelected |= Geom.Between(move.Left, move.Right, unit.X) &&
                                               Geom.Between(move.Top, move.Bottom, unit.Y) &&
                                               (move.VehicleType == null || move.VehicleType == unit.Type);
                        }
                    }
                    break;
                case ActionType.Assign:
                    foreach (var unit in Vehicles)
                    {
                        if (!unit.IsMy)
                            continue;

                        unit.AddGroup(move.Group);
                    }
                    break;
                case ActionType.Move:
                    foreach (var unit in Vehicles)
                    {
                        if (!unit.IsSelected)
                            continue;
                        unit.MoveSpeed = move.MaxSpeed;
                        unit.MoveTarget = unit + new Point(move.X, move.Y);
                        unit.RotationAngularSpeed = 0;
                        unit.RotationAngle = 0;
                        unit.RotationCenter = null;
                    }
                    break;
                case ActionType.Rotate:
                    foreach (var unit in Vehicles)
                    {
                        if (!unit.IsSelected)
                            continue;
                        unit.MoveSpeed = 0;
                        unit.MoveTarget = null;
                        unit.RotationAngularSpeed = move.MaxAngularSpeed;
                        unit.RotationAngle = move.Angle;
                        unit.RotationCenter = new Point(move.X, move.Y);
                    }
                    break;
                case ActionType.Scale:
                    foreach (var unit in Vehicles)
                    {
                        if (!unit.IsSelected)
                            continue;
                        unit.MoveSpeed = move.MaxSpeed;
                        var scaleCenter = new Point(move.X, move.Y);
                        unit.MoveTarget = (unit - scaleCenter) * move.Factor + scaleCenter;
                        unit.RotationAngularSpeed = 0;
                        unit.RotationAngle = 0;
                        unit.RotationCenter = null;
                    }
                    break;
            }
        }

        public List<AVehicle> GetAllNeigbours(double x, double y, double radius)
        {
            var res = _tree(false, false).FindAllNearby(x, y, radius*radius, -1);
            res.AddRange(_tree(false, true).FindAllNearby(x, y, radius*radius, -1));
            res.AddRange(_tree(true, false).FindAllNearby(x, y, radius*radius, -1));
            res.AddRange(_tree(true, true).FindAllNearby(x, y, radius*radius, -1));
            return res;
        }

        public List<AVehicle> GetOpponentFightNeigbours(AVehicle veh, double radius)
        {
            var oppTree = _tree(!veh.IsMy, veh.IsAerial);
            var oppTree2 = _tree(!veh.IsMy, !veh.IsAerial);

            var nearestInteractors = oppTree.FindAllNearby(veh, radius*radius);

            if (veh.Type != VehicleType.Fighter)
                nearestInteractors.AddRange(oppTree2.FindAllNearby(veh, radius*radius));

            return nearestInteractors;
        }

        private void _doFight()
        {
            foreach(var veh in Vehicles)
            {
                if (!veh.IsAlive)
                    continue;
                if (veh.RemainingAttackCooldownTicks > 0)
                    continue;

                if (veh.Type == VehicleType.Arrv && !veh.IsMy)
                    continue; // TODO

                var vehTree = _tree(veh.IsMy, veh.IsAerial);
                var vehTree2 = _tree(veh.IsMy, !veh.IsAerial);
                var oppTree = _tree(!veh.IsMy, veh.IsAerial);
                var oppTree2 = _tree(!veh.IsMy, !veh.IsAerial);

                var nearestInteractors = veh.Type == VehicleType.Arrv
                    ? vehTree.FindAllNearby(veh, G.ArrvRepairRange*G.ArrvRepairRange)
                    : oppTree.FindAllNearby(veh, G.MaxAttackRange*G.MaxAttackRange);

                if (veh.Type != VehicleType.Fighter)
                    nearestInteractors.AddRange(oppTree2.FindAllNearby(veh, G.MaxAttackRange*G.MaxAttackRange));

                if (veh.Type == VehicleType.Arrv)
                    nearestInteractors.AddRange(vehTree2.FindAllNearby(veh, G.ArrvRepairRange*G.ArrvRepairRange));

                
                var probabilities = new List<double>();
                var candidates = new List<AVehicle>();

                foreach (var oppVeh in nearestInteractors)
                {
                    var damage = veh.Type == VehicleType.Arrv ? (G.MaxDurability - oppVeh.FullDurability) : veh.GetAttackDamage(oppVeh);
                    if (damage > Const.Eps)
                    {
                        probabilities.Add(damage);
                        candidates.Add(oppVeh);
                    }
                }

                if (candidates.Count > 0)
                {
                    var choise = probabilities.ArgMax();
                    var choiseUnit = candidates[choise];

                    if (veh.Type == VehicleType.Arrv)
                        choiseUnit.Repair();
                    else
                        veh.Attack(choiseUnit);
                }
            }
        }

        void _upd(ref AVehicle result, AVehicle unit, AVehicle nearest)
        {
            if (nearest == null)
                return;
            if (result == null)
            {
                result = nearest;
                return;
            }
            if (nearest.GetDistanceTo2(unit) < result.GetDistanceTo2(unit))
                result = nearest;
        }

        private void _doMove()
        {
            var total = 0;
            var positive = 0;
            var notMoved = Enumerable.Range(0, Vehicles.Count).ToArray();
            var notMovedLength = notMoved.Length;

            while (notMovedLength > 0)
            {
                var notMovedNewLength = 0;

                for (var i = 0; i < notMovedLength; i++)
                {
                    var idx = notMoved[i];
                    var unit = Vehicles[idx];
                    var unitTree = _tree(unit.IsMy, unit.IsAerial);
                    var oppTree = _tree(!unit.IsMy, unit.IsAerial);

                    var nearestWithNotMoved = _nearestCache[idx];
                    var nearestWithMoved = nearestWithNotMoved;

                    var prevX = unit.X;
                    var prevY = unit.Y;

                    var vehicleMoved = unit.Move(x =>
                    {
                        total++;
                        if (nearestWithMoved != null && nearestWithMoved.IntersectsWith(x))
                        {
                            positive++;
                            return true;
                        }

                        {
                            var nearest = unitTree.FindFirstNearby(x, Geom.Sqr(2 * x.Radius));

                            if (nearest != null)
                            {
                                _upd(ref nearestWithMoved, x, nearest);
                                if (nearestWithMoved.IntersectsWith(x))
                                    return true;
                                _upd(ref nearestWithNotMoved, unit, nearest);
                            }
                        }

                        if (!unit.IsAerial && CheckCollisionsWithOpponent)
                        {
                            var nearest = oppTree.FindFirstNearby(x, Geom.Sqr(2 * x.Radius));
                            if (nearest != null)
                            {
                                _upd(ref nearestWithMoved, x, nearest);
                                if (nearestWithMoved.IntersectsWith(x))
                                    return true;
                                _upd(ref nearestWithNotMoved, unit, nearest);
                            }
                        }
                        
                        return false;
                    });

                    if (!vehicleMoved)
                    {
                        _upd(ref nearestWithNotMoved, unit, nearestWithMoved);
                        _nearestCache[idx] = nearestWithNotMoved;
                        notMoved[notMovedNewLength++] = idx;
                    }
                    else if (!Geom.PointsEquals(prevX, prevY, unit.X, unit.Y))
                    {
                        Utility.Swap(ref prevX, ref unit.X);
                        Utility.Swap(ref prevY, ref unit.Y);
                        if (!unitTree.ChangeXY(unit, prevX, prevY))
                            throw new Exception("Can't change unit coordinates, id=" + unit.Id);

                        _upd(ref nearestWithMoved, unit, nearestWithNotMoved);
                        _nearestCache[idx] = nearestWithMoved;
                    }
                }

                if (notMovedLength == notMovedNewLength)
                    break;
                notMovedLength = notMovedNewLength;
            }
            //Console.WriteLine(1.0 * positive / total);
        }

        private void _doMoveDebug()
        {
            var moved = new bool[Vehicles.Count];
            var movedCount = 0;

            while (movedCount < moved.Length)
            {
                var anyMoved = false;
                for (var i = 0; i < moved.Length; i++)
                {
                    if (moved[i])
                        continue;
                    var unit = Vehicles[i];

                    if (
                        !unit.Move(
                            x =>
                                Vehicles.Any(
                                    opp =>
                                    {
                                        if (x.Id == opp.Id)
                                            return false;
                                        if (x.IsAerial != opp.IsAerial)
                                            return false;

                                        if ((x.IsAerial || !CheckCollisionsWithOpponent) && x.IsMy != opp.IsMy)
                                            return false;

                                        return opp.IntersectsWith(x);
                                    })))
                    {
                        continue;
                    }

                    moved[i] = true;
                    anyMoved = true;
                    movedCount++;
                }

                if (!anyMoved)
                    break;
            }
        }

        private void _doNuclears()
        {
            bool needRemove = false;
            foreach (var nuclear in Nuclears)
            {
                if (VehicleById.ContainsKey(nuclear.VehicleId)) // если нет информации о владельце, то оставлять бомбу
                {
                    var owner = VehicleById[nuclear.VehicleId];
                    if (!owner.IsAlive || owner.GetDistanceTo2(nuclear) + Const.Eps > Geom.Sqr(owner.ActualVisionRange))
                    {
                        needRemove = true;
                        nuclear.RemainingTicks = 0;
                        continue;
                    }
                }

                nuclear.RemainingTicks--;
                if (nuclear.RemainingTicks == 0)
                {
                    needRemove = true;

                    foreach (var target in GetAllNeigbours(nuclear.X, nuclear.Y, nuclear.Radius))
                        target.Durability -= target.GetNuclearDamage(nuclear);
                }
            }
            if (needRemove)
                Nuclears = Nuclears.Where(x => x.RemainingTicks > 0).ToArray();
        }

        public void DoTick()
        {
            Logger.CumulativeOperationStart("DoMove");
            _doMove();
            Logger.CumulativeOperationEnd("DoMove");

            Logger.CumulativeOperationStart("DoFight");
            _doFight();
            Logger.CumulativeOperationEnd("DoFight");

            _doNuclears();
        }

        public class Cluster
        {
            public List<AVehicle> Vehicles;
            public Point Avg;
        }

        public List<Cluster> GetClusters(bool isMy, double margin)
        {
            Logger.CumulativeOperationStart("Clustering");

            var res = new List<Cluster>();

            foreach (var isAerial in new[] { false, true })
            {
                var tree = _tree(isMy, isAerial).Clone();
                while (tree.Count > 0)
                {
                    var val = tree.FirstOrDefault();
                    var currentCluster = new List<AVehicle>();
                    tree.Remove(val);
                    currentCluster.Add(val);
                    var processed = 0;
                    while (processed < currentCluster.Count)
                    {
                        var pt = currentCluster[processed++];
                        foreach (var nw in tree.FindAllNearby(pt, margin*margin))
                        {
                            tree.Remove(nw);
                            currentCluster.Add(nw);
                        }
                    }
                    res.Add(new Cluster {Vehicles = currentCluster, Avg = MyStrategy.GetAvg(currentCluster)});
                }
            }

            Logger.CumulativeOperationEnd("Clustering");
            return res;
        }
    }
}
