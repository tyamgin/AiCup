using System;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        private class ActionsQueue
        {
            private class ActionQueueItem
            {
                public Func<Sandbox, Predicate<Sandbox>> Action;
                public Predicate<Sandbox> EndCondition;
            }

            static List<ActionQueueItem> queue = new List<ActionQueueItem>(); 

            public static void Add(Func<Sandbox, Predicate<Sandbox>> action)
            {
                queue.Add(new ActionQueueItem {Action = action});
            }

            public static void Process()
            {
                while (queue.Count > 0)
                {
                    if (!MoveQueue.Free)
                        break;

                    var item = queue[0];
                    if (item.Action != null)
                    {
                        item.EndCondition = item.Action(Environment);
                        item.Action = null;
                    }
                    if (item.EndCondition(Environment))
                    {
                        queue.RemoveAt(0);
                    }
                    else
                    {
                        break;
                    }
                }
            }
        }

        void _selectIfNotSelected(Sandbox env, AMove move)
        {
            if (move.Action != ActionType.ClearAndSelect)
                throw new Exception("Invalid arguments for _selectIfNotSelected");
            var selHash = Utility.UnitsHash(env.MyVehicles.Where(x =>
                move.Rect.ContainsPoint(x)
                && (move.VehicleType == null || move.VehicleType == x.Type)
                && (move.Group == 0 || x.HasGroup(move.Group))));

            var curHash = Utility.UnitsHash(env.MyVehicles.Where(x => x.IsSelected));
            if (curHash != selHash)
            {
                MoveQueue.Add(move);
            }
        }

        void MoveFirstTicks()
        {
            Action<double, double, double, double> expandSquares = (x, y, dx, dy) =>
            {
                ActionsQueue.Add(env =>
                {
                    var rect = new Rect { X = x, Y = y, X2 = G.MapSize, Y2 = G.MapSize };

                    var groundUnits = env.MyVehicles.Where(u => !u.IsAerial).ToArray();
                    if (groundUnits.All(veh => !rect.ContainsPoint(veh)) || groundUnits.All(veh => rect.ContainsPoint(veh)))
                        return e => true;

                    _selectIfNotSelected(env, new AMove
                    {
                        Action = ActionType.ClearAndSelect,
                        Rect = rect
                    });

                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.Move,
                        X = dx,
                        Y = dy
                    });

                    return env2 =>
                    {
                        return env2.TickIndex > env.TickIndex &&
                               env2.MyVehicles.Where(u => !u.IsAerial).All(u => u.Stopped);
                    };
                });
            };

            var sh = 10;
            var cs = G.CellSize * 2.5;

            if (Const.MixArrvsWithGrounds)
            {
                expandSquares(0, 2*cs, 0, sh);
                expandSquares(0, cs, 0, sh);
                expandSquares(2*cs, 0, sh, 0);
                expandSquares(cs, 0, sh, 0);
            }

            ActionsQueue.Add(env =>
            {
                var groups = new[]
                {
                    GroupsManager.StartingFightersGroupId,
                    GroupsManager.StartingHelicoptersGroupId,
                    GroupsManager.StartingTanksGroupId,
                    GroupsManager.StartingIfvsGroupId,
                     
                };
                var groupsLeaders = new[]
                {
                    (VehicleType)0,
                    VehicleType.Fighter,
                    VehicleType.Helicopter,
                    VehicleType.Tank,
                    VehicleType.Ifv,
                    VehicleType.Arrv
                };
                if (!Const.MixArrvsWithGrounds)
                    groups = groups.ConcatSingle(GroupsManager.StartingArrvsGroupId).ToArray();

                foreach (var group in groups)
                {
                    MoveQueue.Add(AMovePresets.ClearAndSelectType(groupsLeaders[group]));
                    MoveQueue.Add(AMovePresets.AssignGroup(group));
                }
                return e => true;
            });

            ActionsQueue.Add(env =>
            {
                GroupsManager.MyGroups.Add(new MyGroup(GroupsManager.StartingFightersGroupId, VehicleType.Fighter));
                GroupsManager.MyGroups.Add(new MyGroup(GroupsManager.StartingHelicoptersGroupId, VehicleType.Helicopter));
                
                if (!Const.MixArrvsWithGrounds)
                {
                    GroupsManager.MyGroups.Add(new MyGroup(GroupsManager.StartingArrvsGroupId, VehicleType.Arrv));
                    GroupsManager.MyGroups.Add(new MyGroup(GroupsManager.StartingIfvsGroupId, VehicleType.Ifv));
                    GroupsManager.MyGroups.Add(new MyGroup(GroupsManager.StartingTanksGroupId, VehicleType.Tank));
                    FirstMovesComplete = true;
                }
                return e => true;
            });

            if (Const.MixArrvsWithGrounds)
            {
                ActionsQueue.Add(env =>
                {
                    var arrvs = env.GetVehicles(true, VehicleType.Arrv);
                    var tanks = env.GetVehicles(true, VehicleType.Tank);
                    var ifvs = env.GetVehicles(true, VehicleType.Ifv);

                    var arrvsRect = Utility.BoundingRect(arrvs);
                    var tanksRect = Utility.BoundingRect(tanks);
                    var ifvsRect = Utility.BoundingRect(ifvs);

                    var d = 1.38;
                    _selectIfNotSelected(env, AMovePresets.ClearAndSelectType(VehicleType.Arrv));
                    MoveQueue.Add(AMovePresets.Scale(arrvsRect.Center, d));

                    MoveQueue.Add(AMovePresets.ClearAndSelectType(VehicleType.Tank));
                    MoveQueue.Add(AMovePresets.Scale(tanksRect.Center, d));

                    MoveQueue.Add(AMovePresets.ClearAndSelectType(VehicleType.Ifv));
                    MoveQueue.Add(AMovePresets.Scale(ifvsRect.Center, d));

                    return env2 =>
                    {
                        return env2.TickIndex > env.TickIndex + 20 &&
                               env2.MyVehicles.Where(u => !u.IsAerial).All(u => u.Stopped);
                    };
                });

                var shift = 4.02;

                ActionsQueue.Add(env =>
                {
                    _selectIfNotSelected(env, AMovePresets.ClearAndSelectType(VehicleType.Arrv));
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.Move,
                        X = shift,
                        Y = shift,
                    });

                    return env2 =>
                    {
                        return env2.TickIndex > env.TickIndex &&
                               env2.GetVehicles(true, VehicleType.Arrv).All(u => u.Stopped);
                    };
                });

                ActionsQueue.Add(env =>
                {
                    var arrvs = env.GetVehicles(true, VehicleType.Arrv);
                    var tanks = env.GetVehicles(true, VehicleType.Tank);
                    var ifvs = env.GetVehicles(true, VehicleType.Ifv);

                    var arrvsRect = Utility.BoundingRect(arrvs);
                    var tanksRect = Utility.BoundingRect(tanks);
                    var ifvsRect = Utility.BoundingRect(ifvs);

                    var aVert = arrvsRect.SplitVertically();

                    var topArrvs = arrvs.Where(x => x.Y <= arrvsRect.Center.Y).Select(x => x.Id).ToArray();
                    var bottomArrvs = arrvs.Where(x => x.Y > arrvsRect.Center.Y).Select(x => x.Id).ToArray();

                    if (Math.Max(aVert[0].Center.GetDistanceTo2(tanksRect.Center),
                        aVert[1].Center.GetDistanceTo2(ifvsRect.Center))
                        <
                        Math.Max(aVert[1].Center.GetDistanceTo2(tanksRect.Center),
                            aVert[0].Center.GetDistanceTo2(ifvsRect.Center)))
                    {
                        tankArrvs = topArrvs;
                        ifvArrvs = bottomArrvs;
                    }
                    else
                    {
                        tankArrvs = bottomArrvs;
                        ifvArrvs = topArrvs;
                    }


                    var tankArrvsRect = Utility.BoundingRect(tankArrvs.Select(id => env.VehicleById[id]));
                    var ifvArrvsRect = Utility.BoundingRect(ifvArrvs.Select(id => env.VehicleById[id]));


                    _selectIfNotSelected(env, new AMove
                    {
                        Action = ActionType.ClearAndSelect,
                        Rect = tankArrvsRect,
                        VehicleType = VehicleType.Arrv,
                    });
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.Move,
                        X = tanksRect.Center.X - (tankArrvsRect.Center.X - shift),
                    });

                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.ClearAndSelect,
                        Rect = ifvArrvsRect,
                        VehicleType = VehicleType.Arrv,
                    });
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.Move,
                        X = ifvsRect.Center.X - (ifvArrvsRect.Center.X - shift),
                    });

                    return env2 =>
                    {
                        return env2.TickIndex > env.TickIndex + 5 &&
                               env2.GetVehicles(true, VehicleType.Arrv).All(u => u.Stopped);
                    };
                });

                ActionsQueue.Add(env =>
                {
                    var tanks = env.GetVehicles(true, VehicleType.Tank);
                    var ifvs = env.GetVehicles(true, VehicleType.Ifv);

                    var tanksRect = Utility.BoundingRect(tanks);
                    var ifvsRect = Utility.BoundingRect(ifvs);

                    var tankArrvsRect = Utility.BoundingRect(tankArrvs.Select(id => env.VehicleById[id]));
                    var ifvArrvsRect = Utility.BoundingRect(ifvArrvs.Select(id => env.VehicleById[id]));

                    var proportionI = 0.55;
                    var proportionT = 0.55;

                    if (Math.Abs(ifvsRect.Center.X - tanksRect.Center.X) < 10 &&
                        ifvsRect.Center.GetDistanceTo(tanksRect.Center) < 100)
                    {
                        proportionI = 0.95;
                        proportionT = 0.95;
                    }

                    if (ifvArrvsRect.Center.Y < 30 && ifvArrvsRect.Center.GetDistanceTo(ifvsRect.Center) < 20)
                        proportionI = 0.95;
                    if (tankArrvsRect.Center.Y < 30 && tankArrvsRect.Center.GetDistanceTo(tanksRect.Center) < 20)
                        proportionT = 0.95;

                    _selectIfNotSelected(env, new AMove
                    {
                        Action = ActionType.ClearAndSelect,
                        Rect = ifvArrvsRect,
                        VehicleType = VehicleType.Arrv,
                    });
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.Move,
                        Y = (ifvsRect.Center.Y - ifvArrvsRect.Center.Y)*proportionI,
                    });

                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.ClearAndSelect,
                        Rect = tankArrvsRect,
                        VehicleType = VehicleType.Arrv,
                    });
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.Move,
                        Y = (tanksRect.Center.Y - tankArrvsRect.Center.Y)*proportionT,
                    });

                    MoveQueue.Add(AMovePresets.ClearAndSelectType(VehicleType.Ifv));
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.Move,
                        Y = -(ifvsRect.Center.Y - ifvArrvsRect.Center.Y)*(1 - proportionI),
                    });

                    MoveQueue.Add(AMovePresets.ClearAndSelectType(VehicleType.Tank));
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.Move,
                        Y = -(tanksRect.Center.Y - tankArrvsRect.Center.Y)*(1 - proportionT),
                    });

                    return env2 =>
                    {
                        return env2.TickIndex > env.TickIndex + 30 &&
                               env2.MyVehicles.Where(u => !u.IsAerial).All(u => u.Stopped);
                    };
                });

                ActionsQueue.Add(env =>
                {
                    // tanks already selected
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.ClearAndSelect,
                        VehicleType = VehicleType.Arrv,
                        Rect = Utility.BoundingRect(
                                env.GetVehicles(true, VehicleType.Arrv).Where(x => tankArrvs.Contains(x.Id)))
                    });
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.Assign,
                        Group = GroupsManager.StartingTanksGroupId,
                    });

                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.ClearAndSelect,
                        VehicleType = VehicleType.Arrv,
                        Rect = Utility.BoundingRect(
                                env.GetVehicles(true, VehicleType.Arrv).Where(x => ifvArrvs.Contains(x.Id)))
                    });
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.Assign,
                        Group = GroupsManager.StartingIfvsGroupId,
                    });

                    return e => true;
                });

                ActionsQueue.Add(env =>
                {
                    FirstMovesComplete = true;
                    GroupsManager.MyGroups.Add(new MyGroup(GroupsManager.StartingIfvsGroupId, VehicleType.Ifv));
                    GroupsManager.MyGroups.Add(new MyGroup(GroupsManager.StartingTanksGroupId, VehicleType.Tank));
                    return e => true;
                });

            }
        }

        private long[] tankArrvs, ifvArrvs;

        public bool FirstMovesComplete;
    }

    public class MyGroup
    {
        public readonly int Group;
        public readonly VehicleType VehicleType;

        public MyGroup(int group, VehicleType type)
        {
            Group = group;
            VehicleType = type;
        }

        public override string ToString()
        {
            return VehicleType + "(" + Group + ")";
        }
    }
}
