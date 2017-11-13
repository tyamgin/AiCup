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
        private readonly List<AVehicle>[] _vehiclesByType =
        {
            new List<AVehicle>(),
            new List<AVehicle>()
        };

        private readonly QuadTree<AVehicle>[] _treesByType =
        {
            new QuadTree<AVehicle>(0, 0, G.MapSize, G.MapSize, Const.Eps),
            new QuadTree<AVehicle>(0, 0, G.MapSize, G.MapSize, Const.Eps)
        };

        private readonly QuadTree<AVehicle>[] _treesByPlayer =
        {
            new QuadTree<AVehicle>(0, 0, G.MapSize, G.MapSize, Const.Eps),
            new QuadTree<AVehicle>(0, 0, G.MapSize, G.MapSize, Const.Eps)
        };

        private readonly QuadTree<AVehicle>[,] _trees = new QuadTree<AVehicle>[2, 2]
        {
            {
                new QuadTree<AVehicle>(0, 0, G.MapSize, G.MapSize, Const.Eps),
                new QuadTree<AVehicle>(0, 0, G.MapSize, G.MapSize, Const.Eps)
            },
            {
                new QuadTree<AVehicle>(0, 0, G.MapSize, G.MapSize, Const.Eps),
                new QuadTree<AVehicle>(0, 0, G.MapSize, G.MapSize, Const.Eps)
            }
        };

        private List<AVehicle> _at(bool isMy)
        {
            return _vehiclesByType[isMy ? 1 : 0];
        }
        private QuadTree<AVehicle> _tree(bool isMy, bool isAerial)
        {
            return _trees[isMy ? 1 : 0, isAerial ? 1 : 0];
        }

        public AVehicle[] FirstCollider1;

        public IEnumerable<AVehicle> MyVehicles => _at(true);

        public IEnumerable<AVehicle> OppVehicles => _at(false);

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
                    _at(veh.IsMy).Add(veh);
                    _tree(veh.IsMy, veh.IsAerial).Add(veh);
                }
                FirstCollider1 = new AVehicle[_vehicles.Length];
            }
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

        private void _doFight()
        {
            foreach(var veh in Vehicles)
            {
                if (!veh.IsAlive)
                    continue;
                if (veh.RemainingAttackCooldownTicks > 0)
                    continue;
                
                var oppTree = _tree(!veh.IsMy, veh.IsAerial);
                var oppTree2 = _tree(!veh.IsMy, !veh.IsAerial);

                var nearestOpponents = oppTree.FindAllNearby(veh, G.MaxAttackRange*G.MaxAttackRange);
                if (veh.Type != VehicleType.Fighter)
                    nearestOpponents.AddRange(oppTree2.FindAllNearby(veh, G.MaxAttackRange * G.MaxAttackRange));

                if (nearestOpponents.Count > 0)
                {
                    var probabilities = new List<double>();
                    var candidates = new List<AVehicle>();

                    foreach (var oppVeh in nearestOpponents)
                    {
                        var damage = veh.GetAttackDamage(oppVeh);
                        if (damage > 0)
                        {
                            probabilities.Add(damage);
                            candidates.Add(oppVeh);
                        }
                    }
                    var choise = probabilities.ArgMax();
                    veh.Attack(candidates[choise]);
                }
            }
        }

        private void _doMove()
        {
            var notMoved = Enumerable.Range(0, Vehicles.Length).ToArray();
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

                    var removed = false;
                    var vehicleMoved = unit.Move(x =>
                    {
                        if (!unitTree.Remove(unit))
                            throw new Exception("Vehicle Id=" + unit.Id + " not found");
                        removed = true;

                        {
                            var nearest = unitTree.FindNearest(x);
                            if (nearest != null && nearest.IntersectsWith(x))
                            {
                                FirstCollider1[idx] = nearest;
                                return true;
                            }
                        }

                        if (!unit.IsAerial)
                        {
                            var nearest = oppTree.FindNearest(x);
                            if (nearest != null && nearest.IntersectsWith(x))
                            {
                                FirstCollider1[idx] = nearest;
                                return true;
                            }
                        }
                        
                        return false;
                    });

                    if (removed)
                    {
                        unitTree.Add(unit);
                    }
                    if (!vehicleMoved)
                    {
                        notMoved[notMovedNewLength++] = idx;
                    }
                }

                if (notMovedLength == notMovedNewLength)
                    break;
                notMovedLength = notMovedNewLength;
            }
        }


        public void DoTick()
        {
            for (var i = 0; i < FirstCollider1.Length; i++)
                FirstCollider1[i] = null;
            _doMove();
            _doFight();
        }

        public double MyDurability => MyVehicles.Sum(x => x.Durability);
        public double OppDurability => OppVehicles.Sum(x => x.Durability);
    }
}
