using System;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class Sandbox
    {
        private const int MagicMaxSize = 5000;
        private const int MagicMaxDependenciesCount = 50;

        public int TickIndex;
        public AVehicle[] Vehicles;
        public ANuclear[] Nuclears;
        public AFacility[] Facilities;
        public readonly Dictionary<long, AVehicle> VehicleById = new Dictionary<long, AVehicle>();

        public bool CheckCollisionsWithOpponent = true;
        public bool UseFightOptimization = true;

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

        private readonly List<List<AVehicle>> _myVehiclesByGroup = new List<List<AVehicle>>();

        private double[] _nearestFightersCacheDist;
        private int[] _nearestFightersCacheTick;

        private static AVehicle _cloneVehicle(AVehicle vehicle)
        {
            return new AVehicle(vehicle);
        }

        private QuadTree<AVehicle> _tree(bool isMy, bool isAerial)
        {
            var tree = _trees[isMy ? 1 : 0, isAerial ? 1 : 0];
            if (tree == null)
            {
                Logger.CumulativeOperationStart("QuadTree construct");
                tree = new QuadTree<AVehicle>(0, 0, G.MapSize, G.MapSize, Const.Eps, _cloneVehicle);
                tree.AddRange(_vehiclesByOwner[isMy ? 1 : 0].Where(x => x.IsAerial == isAerial));
                _trees[isMy ? 1 : 0, isAerial ? 1 : 0] = tree;
                Logger.CumulativeOperationEnd("QuadTree construct");
            }
            return tree;
        }

        public void AddVehicleGroup(AVehicle veh, int groupId)
        {
            if (veh.HasGroup(groupId))
                return;
            veh.AddGroup(groupId);
            if (veh.IsMy)
            {
                while (_myVehiclesByGroup.Count < groupId)
                    _myVehiclesByGroup.Add(new List<AVehicle>());
                _myVehiclesByGroup[groupId - 1].Add(veh);
            }
        }

        public List<AVehicle> GetVehicles(bool isMy, VehicleType type)
        {
            return _vehiclesByOwnerAndType[isMy ? 1 : 0][(int) type];
        }

        public List<AVehicle> GetVehicles(bool isMy, int group)
        {
            if (!isMy)
                throw new Exception("Trying to access not my group");

            var groupIdx = group - 1;
            if (groupIdx < _myVehiclesByGroup.Count)
                return _myVehiclesByGroup[groupIdx];

            return new List<AVehicle>();
        }

        public List<AVehicle> GetVehicles(bool isMy, MyGroup group)
        {
            return GetVehicles(isMy, group.Group);
        }

        public List<AVehicle> MyVehicles => _vehiclesByOwner[1];

        public List<AVehicle> OppVehicles => _vehiclesByOwner[0];

        public IEnumerable<AFacility> MyVehicleFactories => Facilities.Where(x => x.IsMy && x.Type == FacilityType.VehicleFactory);

        public Sandbox(IEnumerable<AVehicle> vehicles, IEnumerable<ANuclear> nuclears, IEnumerable<AFacility> facilities, bool clone = false)
        {
            if (clone)
            {
                vehicles = vehicles.Select(x => new AVehicle(x));
                nuclears = nuclears.Select(x => new ANuclear(x));
                facilities = facilities.Select(x => new AFacility(x));
            }

            Nuclears = nuclears.ToArray();
            Facilities = facilities.ToArray();
            AddRange(vehicles);
        }

        public void AddRange(IEnumerable<AVehicle> newVehicles)
        {
            var vehicles = newVehicles.ToArray();
            int offset = Utility.ResizeArray(ref Vehicles, (Vehicles?.Length ?? 0) + vehicles.Length);

            for (var i = 0; i < vehicles.Length; i++)
            {
                var veh = vehicles[i];
                Vehicles[offset + i] = veh;
                _nearestFightersCacheDist = null;
                _nearestFightersCacheTick = null; //TODO: maybe optimize

                _vehiclesByOwner[veh.IsMy ? 1 : 0].Add(veh);
                _vehiclesByOwnerAndType[veh.IsMy ? 1 : 0][(int)veh.Type].Add(veh);
                VehicleById[veh.Id] = veh;
                if (veh.IsMy)
                {
                    foreach (var g in veh.GroupsList)
                    {
                        while (_myVehiclesByGroup.Count < g)
                            _myVehiclesByGroup.Add(new List<AVehicle>());
                        _myVehiclesByGroup[g - 1].Add(veh);
                    }
                }
                _trees[veh.IsMy ? 1 : 0, veh.IsAerial ? 1 : 0]?.Add(veh);
            }
        }

        private Sandbox()
        {
        }

        public Sandbox Clone()
        {
            var clone = new Sandbox
            {
                CheckCollisionsWithOpponent = CheckCollisionsWithOpponent,
                UseFightOptimization = UseFightOptimization,
                TickIndex = TickIndex,

                Nuclears = Nuclears.Select(x => new ANuclear(x)).ToArray(),
                Facilities = Facilities.Select(x => new AFacility(x)).ToArray(),
                Vehicles = new AVehicle[Vehicles.Length],
            };
            var ptr = 0;


            for (var i = 0; i < _myVehiclesByGroup.Count; i++)
            {
                clone._myVehiclesByGroup.Add(new List<AVehicle>(_myVehiclesByGroup[i].Count));
            }

            for (var isMy = 0; isMy < 2; isMy++)
            {
                clone._vehiclesByOwner[isMy].Capacity = _vehiclesByOwner[isMy].Count;
                for (var j = 0; j < 5; j++)
                    clone._vehiclesByOwnerAndType[isMy][j].Capacity = _vehiclesByOwnerAndType[isMy][j].Count;

                for (var isAerial = 0; isAerial < 2; isAerial++)
                {
                    IEnumerable<AVehicle> vehiclesClones;
                    clone._trees[isMy, isAerial] = _trees[isMy, isAerial];
                    if (clone._trees[isMy, isAerial] == null)
                    {
                        vehiclesClones = _vehiclesByOwner[isMy].Where(x => x.IsAerial == (isAerial == 1)).Select(_cloneVehicle);
                    }
                    else
                    {
                        var treeNodes = new List<AVehicle>();
                        clone._trees[isMy, isAerial] = clone._trees[isMy, isAerial].Clone(ref treeNodes);
                        vehiclesClones = treeNodes;
                    }
                    foreach (var veh in vehiclesClones)
                    {
                        clone.Vehicles[ptr++] = veh;

                        clone._vehiclesByOwner[isMy].Add(veh);
                        clone._vehiclesByOwnerAndType[isMy][(int)veh.Type].Add(veh);
                        clone.VehicleById[veh.Id] = veh;
                        if (veh.IsMy)
                            foreach (var g in veh.GroupsList)
                                clone._myVehiclesByGroup[g - 1].Add(veh);
                    }
                }
            }
            return clone;
        }

        public void ApplyMove(Move move)
        {
            // TODO: проверки на валидность
            var movePt = new Point(move.X, move.Y);
            var moveVecNorm = movePt.Normalized();
            var moveVecLength = movePt.Length;

            switch (move.Action)
            {
                case ActionType.ClearAndSelect:
                    foreach (var unit in MyVehicles)
                    {
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
                    foreach (var unit in MyVehicles)
                    {
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
                case ActionType.Deselect:
                    foreach (var unit in MyVehicles)
                    {
                        if (!unit.IsSelected)
                            continue;

                        if (move.Group != 0)
                        {
                            if (unit.HasGroup(move.Group))
                                unit.IsSelected = false;
                        }
                        else
                        {
                            if (Geom.Between(move.Left, move.Right, unit.X) &&
                                Geom.Between(move.Top, move.Bottom, unit.Y) &&
                                (move.VehicleType == null || move.VehicleType == unit.Type))
                                unit.IsSelected = false;
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
                        unit.Action = AVehicle.MoveType.Move;
                        unit.MoveSpeedOrAngularSpeed = move.MaxSpeed;
                        unit.MoveVectorOrRotationCenter = moveVecNorm;
                        unit.MoveLengthOrRotationAngle = moveVecLength;
                    }
                    break;
                case ActionType.Rotate:
                    foreach (var unit in Vehicles)
                    {
                        if (!unit.IsSelected)
                            continue;
                        unit.Action = AVehicle.MoveType.Rotate;
                        unit.MoveSpeedOrAngularSpeed = move.MaxAngularSpeed;
                        unit.MoveLengthOrRotationAngle = move.Angle;
                        unit.MoveVectorOrRotationCenter = movePt;
                    }
                    break;
                case ActionType.Scale:
                    foreach (var unit in Vehicles)
                    {
                        if (!unit.IsSelected)
                            continue;
                        unit.Action = AVehicle.MoveType.Scale;
                        unit.MoveSpeedOrAngularSpeed = move.MaxSpeed;
                        
                        var vec = (unit - movePt)*move.Factor + movePt - unit;
                        unit.MoveVectorOrRotationCenter = vec.Normalized();
                        unit.MoveLengthOrRotationAngle = vec.Length;
                    }
                    break;
            }
        }

        public List<AVehicle> GetAllNeighbours(double x, double y, double radius)
        {
            var res = GetMyNeighbours(x, y, radius);
            res.AddRange(GetOpponentNeighbours(x, y, radius));
            return res;
        }

        public AVehicle GetFirstIntersector(ACircularUnit circle, bool isAerial)
        {
            for (var isMy = 0; isMy < 2; isMy++)
            {
                var nearby = _tree(isMy == 1, isAerial).FindFirstNearby(circle.X, circle.Y, Geom.Sqr(circle.Radius + G.VehicleRadius), -1);
                if (nearby != null && nearby.IntersectsWith(circle))
                    return nearby;
            }
            return null;
        }

        public List<AVehicle> GetOpponentNeighbours(double x, double y, double radius)
        {
            var res = _tree(false, false).FindAllNearby(x, y, radius * radius, -1);
            res.AddRange(_tree(false, true).FindAllNearby(x, y, radius * radius, -1));
            return res;
        }

        public List<AVehicle> GetMyNeighbours(double x, double y, double radius)
        {
            var res = _tree(true, false).FindAllNearby(x, y, radius * radius, -1);
            res.AddRange(_tree(true, true).FindAllNearby(x, y, radius * radius, -1));
            return res;
        }

        public void DoFight()
        {
            Utility.ResizeArray(ref _nearestFightersCacheDist, Vehicles.Length, double.MaxValue/2);
            if (UseFightOptimization)
                Utility.ResizeArray(ref _nearestFightersCacheTick, Vehicles.Length, -1);

            for (var i = 0; i < Vehicles.Length; i++)
            {
                var veh = Vehicles[i];
                if (!veh.IsAlive)
                    continue;
                if (veh.RemainingAttackCooldownTicks > 0)
                {
                    veh.RemainingAttackCooldownTicks--;
                    continue;
                }

                if (veh.Type == VehicleType.Arrv && !veh.IsMy)
                    continue; // TODO

                var vehTree = _tree(veh.IsMy, veh.IsAerial);
                var vehTree2 = _tree(veh.IsMy, !veh.IsAerial);
                var oppTree = _tree(!veh.IsMy, veh.IsAerial);
                var oppTree2 = _tree(!veh.IsMy, !veh.IsAerial);

                if (UseFightOptimization && _nearestFightersCacheTick[i] == -1 && veh.Type != VehicleType.Arrv)
                {
                    double maxDist = Const.ActionsBruteforceDepth * (G.MaxVehicleSpeed + G.MaxSpeed[(int) veh.Type]) + G.MaxAttackRange;
                    var interactors = new List<AVehicle>
                    {
                        oppTree.FindNearest(veh, maxDist*maxDist)
                    };

                    if (veh.Type != VehicleType.Fighter)
                        interactors.Add(oppTree2.FindNearest(veh, maxDist * maxDist));

                    _nearestFightersCacheDist[i] = interactors.Min(x => x?.GetDistanceTo(veh) ?? maxDist);
                    _nearestFightersCacheTick[i] = TickIndex;
                }

                var irad = veh.Type == VehicleType.Arrv ? G.ArrvRepairRange : G.MaxAttackRange;

                if (UseFightOptimization && _nearestFightersCacheDist[i] - (TickIndex - _nearestFightersCacheTick[i]) * G.MaxVehicleSpeed > irad + Const.Eps)
                    continue;

                var nearestInteractors = veh.Type == VehicleType.Arrv
                    ? vehTree.FindAllNearby(veh, irad * irad)
                    : oppTree.FindAllNearby(veh, irad * irad);

                if (veh.Type == VehicleType.Arrv)
                    nearestInteractors.AddRange(vehTree2.FindAllNearby(veh, irad * irad));
                else if (veh.Type != VehicleType.Fighter)
                    nearestInteractors.AddRange(oppTree2.FindAllNearby(veh, irad * irad));
                
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

        private static int[] _unblocked = new int[MagicMaxSize];
        private static int[] _unblocked2 = new int[MagicMaxSize];
        private static readonly bool[] _complete = new bool[MagicMaxSize];
        private static readonly int[][] _deps = new int[MagicMaxSize][];
        private static readonly int[] _depsLen = new int[MagicMaxSize];
        private static readonly AVehicle[] _movedState = new AVehicle[MagicMaxSize];

        private void _doMove()
        {
            _doMove1(Vehicles);
        }

        private void _doMove1(AVehicle[] vehicles)
        {
            
            var unblockedLength = 0;

            for (var i = 0; i < vehicles.Length; i++)
            {
                var veh = vehicles[i];
                if (veh.Action == AVehicle.MoveType.None)
                {
                    veh.Move();
                    _complete[i] = true;
                    continue;
                }
                if (_movedState[i] == null)
                    _movedState[i] = new AVehicle(veh);
                else
                    _movedState[i].CopyFrom(veh);

                if (!_movedState[i].Move())
                {
                    veh.CopyFrom(_movedState[i]);
                    _complete[i] = true;
                    continue;
                }

                _unblocked[unblockedLength++] = i;
                _complete[i] = false;
                veh.Index = i;

                if (_deps[i] == null)
                    _deps[i] = new int[MagicMaxDependenciesCount];
                _depsLen[i] = 0;
            }

            while (unblockedLength > 0)
            {
                var unblockedNewLength = 0;

                for (var i = 0; i < unblockedLength; i++)
                {
                    var idx = _unblocked[i];
                    var movedUnit = _movedState[idx];
                    var unitTree = _tree(movedUnit.IsMy, movedUnit.IsAerial);
                    var oppTree = _tree(!movedUnit.IsMy, movedUnit.IsAerial);

                    var intersectsWith = -1;

                    do
                    {
                        {
                            var nearest = unitTree.FindFirstNearby(movedUnit, Geom.Sqr(2 * movedUnit.Radius));

                            if (nearest != null)
                            {
                                if (nearest.IntersectsWith(movedUnit))
                                {
                                    intersectsWith = nearest.Index;
                                    break;
                                }
                            }
                        }

                        if (!movedUnit.IsAerial && CheckCollisionsWithOpponent)
                        {
                            var nearest = oppTree.FindFirstNearby(movedUnit, Geom.Sqr(2 * movedUnit.Radius));

                            if (nearest != null)
                            {
                                if (nearest.IntersectsWith(movedUnit))
                                {
                                    intersectsWith = nearest.Index;
                                    break;
                                }
                            }
                        }
                    } while (false);

                    if (intersectsWith != -1)
                    {
                        if (!_complete[intersectsWith])
                            _deps[intersectsWith][_depsLen[intersectsWith]++] = idx;
                    }
                    else
                    {
                        var unit = vehicles[idx];
                        var prevX = unit.X;
                        var prevY = unit.Y;
                        unit.CopyFrom(movedUnit);

                        if (!Geom.PointsEquals(prevX, prevY, movedUnit.X, movedUnit.Y))
                        {
                            unit.X = prevX;
                            unit.Y = prevY;

                            if (!unitTree.ChangeXY(unit, movedUnit.X, movedUnit.Y))
                                throw new Exception("Can't change unit coordinates, id=" + unit.Id);

                            
                            if (_nearestFightersCacheTick != null && _nearestFightersCacheTick[idx] != -1)
                                _nearestFightersCacheDist[idx] -= unit.GetDistanceTo(prevX, prevY);
                        }
                    }

                    if (intersectsWith == -1 || _complete[intersectsWith])
                    {
                        // resolve dependencies
                        _complete[idx] = true;
                        for (var k = 0; k < _depsLen[idx]; k++)
                            _unblocked2[unblockedNewLength++] = _deps[idx][k];
                    }
                }

                unblockedLength = unblockedNewLength;
                Utility.Swap(ref _unblocked, ref _unblocked2);
            }
        }

        public void DoNuclears()
        {
            var needRemove = false;
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

                    foreach (var target in GetAllNeighbours(nuclear.X, nuclear.Y, nuclear.Radius))
                        target.Durability -= target.GetNuclearDamage(nuclear);
                }
            }
            if (needRemove)
                Nuclears = Nuclears.Where(x => x.RemainingTicks > 0).ToArray();
        }

        public void DoFacilities()
        {
            if (Facilities.Length == 0)
                return;

            Logger.CumulativeOperationStart("DoFacilities");
            foreach (var veh in Vehicles)
            {
                if (!veh.CanChargeFacility)
                    continue;
                if (!veh.IsMy) // HACK: из-за неоднозначного порядка захвата лучше вообще не моделировать действие соперника
                    continue;

                var facilityIdx = MyStrategy.FacilityIndex(veh.X, veh.Y);
                if (facilityIdx != -1)
                    Facilities[facilityIdx].Charge(veh);
            }
            Logger.CumulativeOperationEnd("DoFacilities");
        }

        public void DoTick(bool fight = true)
        {
            Logger.CumulativeOperationStart("DoMove");
            _doMove();
            Logger.CumulativeOperationEnd("DoMove");

            if (fight)
            {
                Logger.CumulativeOperationStart("DoFight");
                DoFight();
                Logger.CumulativeOperationEnd("DoFight");
            }

            DoNuclears();
            DoFacilities();

            TickIndex++;
        }

        static readonly AVehicle[] _prevStateCache = new AVehicle[MagicMaxSize];

        public void UpdateVehicle(AVehicle cur, AVehicle from)
        {
            var prevX = cur.X;
            var prevY = cur.Y;
            cur.CopyFrom(from);
            _updateVehicleCoordinates(cur, prevX, prevY);
        }

        private void _updateVehicleCoordinates(AVehicle veh, double prevX, double prevY)
        {
            var unitTree = _trees[veh.IsMy ? 1 : 0, veh.IsAerial ? 1 : 0];
            if (unitTree == null)
                return; // Дерево ещё не создано. Когда будет создаваться - тогда и наполнится обновленными данными.

            var moveX = veh.X;
            var moveY = veh.Y;
            if (!Geom.PointsEquals(prevX, prevY, moveX, moveY))
            {
                veh.X = prevX;
                veh.Y = prevY;

                if (!unitTree.ChangeXY(veh, moveX, moveY))
                    throw new Exception("Can't change unit coordinates, id=" + veh.Id);
            }
        }

        public bool DoMoveApprox(AVehicle[] vehicles, bool moveApprox)
        {
            var result = true;
            if (moveApprox)
            {
                Logger.CumulativeOperationStart("DoMoveA");
                for (var i = 0; i < vehicles.Length; i++)
                {
                    var veh = vehicles[i];
                    if (_prevStateCache[i] == null)
                        _prevStateCache[i] = new AVehicle(veh);
                    else
                        _prevStateCache[i].CopyFrom(veh);

                    if (!veh.Move())
                    {
                        // откатываем изменения
                        for (var j = 0; j < i; j++)
                        {
                            var prevX = vehicles[j].X;
                            var prevY = vehicles[j].Y;
                            vehicles[j].CopyFrom(_prevStateCache[j]);
                            _updateVehicleCoordinates(vehicles[j], prevX, prevY);
                        }
                        result = false;
                        break;
                    }
                    _updateVehicleCoordinates(veh, _prevStateCache[i].X, _prevStateCache[i].Y);
                }
                Logger.CumulativeOperationEnd("DoMoveA");
            }
            else
            {
                Logger.CumulativeOperationStart("DoMove1");
                _doMove1(vehicles);
                Logger.CumulativeOperationEnd("DoMove1");
            }
            return result;
        }

        public void DoTicksApprox(int ticksCount, bool moveApprox, bool fightApprox)
        {
            var canMove = true;
            for (var t = 0; t < ticksCount; t++)
            {
                if (canMove)
                {
                    canMove = DoMoveApprox(Vehicles, moveApprox);
                }

                if (!fightApprox || t == ticksCount - 1)
                {
                    // TODO: FIXME! arrvs repair apply ticksCount times
                    Logger.CumulativeOperationStart("DoFight1");
                    DoFight();
                    Logger.CumulativeOperationEnd("DoFight1");
                }

                DoNuclears();
                DoFacilities();

                TickIndex++;
            }
        }
    }
}
