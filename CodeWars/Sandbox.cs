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

        private QuadTree<AVehicle>[] _trees =
        {
            new QuadTree<AVehicle>(0, 0, Const.MapSize, Const.MapSize, Const.Eps),
            new QuadTree<AVehicle>(0, 0, Const.MapSize, Const.MapSize, Const.Eps)
        };

        private List<AVehicle> _at(bool isMy)
        {
            return _vehiclesByType[isMy == IsMy ? 1 : 0];
        }

        private IEnumerable<AVehicle> _at(bool isMy, bool isAerial)
        {
            return _vehiclesByType[isMy == IsMy ? 1 : 0].Where(x => x.IsAerial);
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
                _trees[0].Clear();
                _trees[1].Clear();
                foreach (var veh in _vehicles)
                {
                    _at(veh.IsMy == IsMy).Add(veh);
                    _trees[veh.IsAerial ? 1 : 0].Add(veh);
                }
                //_trees[0].check();
                //_trees[1].check();
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

                var probabilities = new List<double>();
                var candidates = new List<AVehicle>();
                foreach (var oppVeh in _at(IsMy != veh.IsMy))
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
                    var isAerial = unit.IsAerial;
                    var tree = _trees[isAerial ? 1 : 0];

                    if (!tree.Remove(unit))
                        throw new Exception("Vehicle Id=" + unit.Id + " not found");

                    if (!unit.Move(x =>
                    {
                        var nearest = tree.FindNearest(x);
                        if (nearest == null || !nearest.IntersectsWith(x))
                            return false;

                        FirslCollider[x.Id] = nearest;   
                        return true;
                    }))
                    {
                        tree.Add(unit);
                        continue;
                    }
                    tree.Add(unit);

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
