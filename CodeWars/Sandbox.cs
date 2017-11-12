using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Runtime.Remoting.Messaging;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class Sandbox
    {
        private AVehicle[] _vehicles;
        private List<AVehicle>[] _vehiclesByType =
        {
            new List<AVehicle>(),
            new List<AVehicle>()
        };

        private QuadTree<AVehicle>[] _treesByType =
        {
            new QuadTree<AVehicle>(0, 0, Const.MapSize, Const.MapSize, Const.Eps),
            new QuadTree<AVehicle>(0, 0, Const.MapSize, Const.MapSize, Const.Eps)
        };

        private QuadTree<AVehicle>[] _treesByPlayer =
        {
            new QuadTree<AVehicle>(0, 0, Const.MapSize, Const.MapSize, Const.Eps),
            new QuadTree<AVehicle>(0, 0, Const.MapSize, Const.MapSize, Const.Eps)
        };

        private List<AVehicle> _at(bool isMy)
        {
            return _vehiclesByType[isMy == IsMy ? 1 : 0];
        }

        public Dictionary<long, AVehicle> FirslCollider = new Dictionary<long, AVehicle>(); 

        public IEnumerable<AVehicle> MyVehicles => _at(IsMy);

        public IEnumerable<AVehicle> OppVehicles => _at(!IsMy);

        public AVehicle[] Vehicles
        {
            get { return _vehicles; }
            set
            {
                _vehicles = value;
                _vehiclesByType[0].Clear();
                _vehiclesByType[1].Clear();
                _treesByType[0].Clear();
                _treesByType[1].Clear();
                _treesByPlayer[0].Clear();
                _treesByPlayer[1].Clear();
                foreach (var veh in _vehicles)
                {
                    _at(veh.IsMy == IsMy).Add(veh);
                    _treesByType[veh.IsAerial ? 1 : 0].Add(veh);
                    _treesByPlayer[veh.IsMy == IsMy ? 1 : 0].Add(veh);
                }
            }
        }

        public bool IsMy = true; // TODO

        public void ApplyMove(Move move)
        {
            switch (move.Action)
            {
                case ActionType.ClearAndSelect:
                    foreach (var unit in Vehicles)
                    {
                        if (IsMy != unit.IsMy)
                            continue;

                        unit.IsSelected = Geom.Between(move.Left, move.Right, unit.X) &&
                                          Geom.Between(move.Top, move.Bottom, unit.Y) &&
                                          (move.VehicleType == null || move.VehicleType == unit.Type);
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

        private void _doFight()
        {
            foreach (var veh in Vehicles)
            {
                if (!veh.IsAlive)
                    continue;
                if (veh.RemainingAttackCooldownTicks > 0)
                    continue;

                var probabilities = new List<double>();
                var candidates = new List<AVehicle>();

                var nearestOpponents = _treesByPlayer[veh.IsMy != IsMy ? 1 : 0].FindAllNearby(veh, G.MaxAttackRange*G.MaxAttackRange);

                foreach (var oppVeh in nearestOpponents)
                {
                    var damage = veh.GetAttackDamage(oppVeh);
                    if (damage > 0)
                    {
                        probabilities.Add(damage);
                        candidates.Add(oppVeh);
                    }
                }
                if (probabilities.Count > 0)
                {
                    var choise = probabilities.ArgMax();
                    veh.Attack(candidates[choise]);
                }
            }
        }

        private void _doMove()
        {
            FirslCollider.Clear();
            var moved = new bool[Vehicles.Length];
            var movedCount = 0;

            while (movedCount < Vehicles.Length)
            {
                var anyMoved = false;
                for (var i = 0; i < Vehicles.Length; i++)
                {
                    if (moved[i])
                        continue;
                    var unit = Vehicles[i];
                    var treeByType = _treesByType[unit.IsAerial ? 1 : 0];
                    var treeByPlayer = _treesByPlayer[unit.IsMy == IsMy ? 1 : 0];

                    var removed = false;
                    var vehicleMoved = unit.Move(x =>
                    {
                        if (!treeByType.Remove(unit))
                            throw new Exception("Vehicle Id=" + unit.Id + " not found");
                        if (!treeByPlayer.Remove(unit))
                            throw new Exception("Vehicle Id=" + unit.Id + " not found");
                        removed = true;

                        var nearest = treeByType.FindNearest(x);
                        if (nearest == null || !nearest.IntersectsWith(x))
                            return false;

                        FirslCollider[x.Id] = nearest;
                        return true;
                    });

                    if (removed)
                    {
                        treeByType.Add(unit);
                        treeByPlayer.Add(unit);
                    }
                    if (!vehicleMoved)
                        continue;

                    moved[i] = true;
                    anyMoved = true;
                    movedCount++;
                }

                if (!anyMoved)
                    break;
            }
        }


        public void DoTick()
        {
            _doMove();
            _doFight();
        }

        public double MyDurability => MyVehicles.Sum(x => x.Durability);
        public double OppDurability => OppVehicles.Sum(x => x.Durability);
    }
}
