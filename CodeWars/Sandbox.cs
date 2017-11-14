﻿using System;
using System.Collections.Generic;
using System.Diagnostics;
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
        private AVehicle[] _nearestCache;

        public IEnumerable<AVehicle> MyVehicles => _at(true);

        public IEnumerable<AVehicle> OppVehicles => _at(false);

        public readonly AVehicle[] Vehicles;

        public Sandbox(IEnumerable<AVehicle> vehicles)
        {
            Vehicles = vehicles.ToArray();
            _vehiclesByType[0].Clear();
            _vehiclesByType[1].Clear();
            _treesByType[0].Clear();
            _treesByType[1].Clear();
            _treesByPlayer[0].Clear();
            _treesByPlayer[1].Clear();
            foreach (var veh in Vehicles)
            {
                _at(veh.IsMy).Add(veh);
                _tree(veh.IsMy, veh.IsAerial).Add(veh);
            }
            FirstCollider1 = new AVehicle[Vehicles.Length];
            _nearestCache = new AVehicle[Vehicles.Length];
        }

        public Sandbox Clone()
        {
            return new Sandbox(Vehicles.Select(x => new AVehicle(x)));
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

                    var nearestWithNotMoved = _nearestCache[idx];
                    var nearestWithMoved = nearestWithNotMoved;

                    var removed = false;
                    var vehicleMoved = unit.Move(x =>
                    {
                        total++;
                        if (nearestWithMoved != null && nearestWithMoved.IntersectsWith(x))
                        {
                            positive++;
                            return true;
                        }

                        if (!unitTree.Remove(unit))
                            throw new Exception("Vehicle Id=" + unit.Id + " not found");
                        removed = true;

                        {
                            var nearest = unitTree.FindNearest(x);
                            if (nearest != null)
                            {
                                _upd(ref nearestWithMoved, x, nearest);
                                if (nearestWithMoved.IntersectsWith(x))
                                    return true;
                                _upd(ref nearestWithNotMoved, unit, nearest);
                            }
                        }

                        if (!unit.IsAerial)
                        {
                            var nearest = oppTree.FindNearest(x);
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

                    if (removed)
                    {
                        unitTree.Add(unit);
                    }
                    if (!vehicleMoved)
                    {
                        _upd(ref nearestWithNotMoved, unit, nearestWithMoved);
                        _nearestCache[idx] = nearestWithNotMoved;
                        notMoved[notMovedNewLength++] = idx;
                    }
                    else
                    {
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

        public void DoTick()
        {
            Logger.CumulativeOperationStart("DoMove");
            _doMove();
            Logger.CumulativeOperationEnd("DoMove");

            Logger.CumulativeOperationStart("DoFight");
            _doFight();
            Logger.CumulativeOperationEnd("DoFight");

            for (var i = 0; i < FirstCollider1.Length; i++)
                FirstCollider1[i] = _nearestCache[i] == null || !_nearestCache[i].IntersectsWith(Vehicles[i]) ? null : _nearestCache[i];
        }
    }
}
