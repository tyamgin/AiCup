using System;
using System.Collections.Generic;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;
using System.Diagnostics;
using System.Globalization;
using System.Linq;
using System.Threading;

/**
 * TODO:
 * - лечение в ПП
 * - прятать вертолеты от самолетов
 * - оптимизировать начальное построение (?)
 * 
 * - ядерка: перебрать несколько центров, учитывать уклонения
 * - починить упирание в стену
 * - упирание в ungrouped
 */

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public static World World;
        public static Player Me, Opp;
        public static AMove ResultingMove;

        public static TerrainType[][] TerrainType;
        public static WeatherType[][] WeatherType;
        public static int[][] FacilityIdx;
        public static Sandbox Environment;
        public static List<VehiclesCluster> OppClusters;
        public static List<VehiclesCluster> MyUngroupedClusters;

        Stopwatch _globalTimer = new Stopwatch();

        ~MyStrategy()
        {
            _globalTimer.Stop();
            Console.WriteLine("Total time: " + _globalTimer.ElapsedMilliseconds + " ms");
        }

#if DEBUG
        public MyStrategy()
        {
            CultureInfo.DefaultThreadCurrentCulture = CultureInfo.InvariantCulture;
        }
#endif

        public void Move(Player me, World world, Game game, Move move)
        {
            if (world.TickIndex == 0)
                _globalTimer.Start();

            World = world;
            Me = me;
            Opp = world.GetOpponentPlayer();
            ResultingMove = new AMove();


#if DEBUG
            while (Visualizer.Visualizer.Pause)
            {
                // pause here
                Thread.Sleep(20);
            }
#endif
            Logger.TimerStart();
            _move(game);
            Logger.CumulativeOperationPrintAndReset(2);
            ResultingMove.ApplyTo(move);
            Logger.TimerEndLog("All", 0);
#if DEBUG
            Visualizer.Visualizer.CreateForm();

            if (World.TickIndex == 0)
            {
                Visualizer.Visualizer.LookAt = new Point(0, 0);
                Visualizer.Visualizer.Zoom = 0.5;
            }
            Visualizer.Visualizer.Draw();
            if (world.TickIndex >= Visualizer.Visualizer.DrawSince)
            {
                var timer = new Stopwatch();
                timer.Start();
                while (!Visualizer.Visualizer.Done/* || timer.ElapsedMilliseconds < 13*/)
                {
                    Thread.Sleep(10);
                }
                timer.Stop();
            }
#endif

            MoveObserver.Update();
        }


        Tuple<AMove[], DangerResult> DoMain(bool opt)
        {
            var baseTicksCount = Const.ActionsBruteforceDepth;
            if (Environment.Nuclears.Length > 0)
                baseTicksCount *= 2;

            var selMoves = new[] { new AMove() };

            var env1 = Environment.Clone();
            for (var i = 0; i < baseTicksCount; i++)
                env1.DoTick();
            DangerResult selDanger = GetDanger(Environment, env1, GroupsManager.MyGroups, MyUngroupedClusters);
            var doNothingDanger = selDanger;
            var targetFacilities = selDanger.TargetFacility;

            for (var s = 0; s < GroupsManager.MyGroups.Count + MyUngroupedClusters.Count; s++)
            {
                MyGroup group;
                List<AVehicle> newGroupVehicles = null;
                if (s < GroupsManager.MyGroups.Count)
                    group = GroupsManager.MyGroups[s];
                else
                {
                    newGroupVehicles = MyUngroupedClusters[s - GroupsManager.MyGroups.Count];
                    if (newGroupVehicles.Count < NewGroupMinSize)
                        continue;
                    if (MoveObserver.AvailableActions < 3)
                        continue;
                    group = new MyGroup(GroupsManager.NextGroupId, newGroupVehicles[0].Type);
                }

                var startEnv = Environment.Clone();
                var ticksCount = baseTicksCount;

                if (newGroupVehicles != null)
                {
                    foreach (var newVeh in newGroupVehicles)
                        startEnv.AddVehicleGroup(startEnv.VehicleById[newVeh.Id], group.Group);
                }

                var selectedIds = Utility.UnitsHash(startEnv.MyVehicles.Where(x => x.IsSelected));
                var needToSelectIds = Utility.UnitsHash(startEnv.GetVehicles(true, group));

                AMove selectionMove = null;
                var availableActions = MoveObserver.AvailableActions;

                if (selectedIds != needToSelectIds)
                {
                    if (MoveObserver.AvailableActions < 2)
                        continue;

                    selectionMove = newGroupVehicles == null
                        ? new AMove
                        {
                            Action = ActionType.ClearAndSelect,
                            Group = group.Group,
                            Rect = G.MapRect,
                        }
                        : new AMove
                        {
                            Action = ActionType.ClearAndSelect,
                            VehicleType = group.VehicleType,
                            Rect = Utility.BoundingRect(newGroupVehicles),
                        };
                    startEnv.ApplyMove(selectionMove);
                    startEnv.DoTick();
                    availableActions--;
                    ticksCount--;
                }
                availableActions--;

                // availableActions теперь - это сколько действий останется после выполнения текущего тика
                // должно остаться хотябы 2, или 1, если обладатель ядерки selected
                var myNuclear = startEnv.Nuclears.FirstOrDefault(n => n.IsMy);
                if (myNuclear != null && availableActions < 2 - (startEnv.VehicleById[myNuclear.VehicleId].IsSelected ? 1 : 0))
                    continue;

                var typeRect = Utility.BoundingRect(startEnv.GetVehicles(true, group));

                Sandbox partialEnv = null;
                var sumMaxAlmostAttacksCache = -1.0;
                if (Environment.Nuclears.Length == 0)
                {
                    partialEnv = new Sandbox(
                        startEnv.Vehicles.Where(x => !x.IsSelected),
                        new ANuclear[] { },
                        startEnv.Facilities,
                        clone: true
                        );
                    partialEnv.CheckCollisionsWithOpponent = false;

                    for (var i = 0; i < ticksCount; i++)
                        partialEnv.DoTick();
                    sumMaxAlmostAttacksCache = GetSumMaxAlmostAttacks(partialEnv, partialEnv.MyVehicles);
                }

                List<Point> pos = new List<Point>(), neg = new List<Point>();
                
                for (var clIdx = 0; clIdx < OppClusters.Count; clIdx++)
                {
                    var cl = OppClusters[clIdx];
                    foreach (var oppType in Const.AllTypes)
                    {
                        if (cl.CountByType[(int)oppType] > 0)
                        {
                            var avg = Utility.Average(cl.VehicleType(oppType));
                            if (G.AttackDamage[(int)group.VehicleType, (int)oppType] > 0)
                                pos.Add(avg);
                            else if (G.AttackDamage[(int)oppType, (int)group.VehicleType] > 0)
                                neg.Add(avg);
                        }
                    }
                }

                Func<AMove, double> checkAction = move =>
                {
                    Sandbox env = partialEnv != null
                        ? new Sandbox(partialEnv.OppVehicles.Concat(startEnv.GetVehicles(true, group)),
                            startEnv.Nuclears, partialEnv.Facilities, clone: true)
                        : startEnv.Clone();

                    env.CheckCollisionsWithOpponent = false;

                    if (partialEnv != null)
                    {
                        foreach (var veh in env.Vehicles)
                        {
                            if (veh.IsMy && veh.IsGroup(group))
                                continue;

                            veh.ForgotTarget(); // чтобы не шли повторно
                            veh.CanChargeFacility = false; // чтобы не захватывали повторно
                            // TODO: лечение

                            if (veh.RemainingAttackCooldownTicks > 0) // у тех, кто стрелял давно, откатываем кд
                                veh.RemainingAttackCooldownTicks += ticksCount;
                            // TODO: если кд только восстановилось
                        }
                    }

                    Logger.CumulativeOperationStart("End of simulation");
                    env.ApplyMove(move);
                    env.DoTicksApprox(ticksCount, moveApprox: move.Action == ActionType.Move);

                    var cache = sumMaxAlmostAttacksCache;
                    if (partialEnv != null)
                    {
                        env.AddRange(partialEnv.MyVehicles.Where(x => !x.IsGroup(group)));
                        cache += GetSumMaxAlmostAttacks(env,
                            env.MyVehicles.Where(x => x.IsGroup(group)));
                    }
                    Logger.CumulativeOperationEnd("End of simulation");

                    var myGroups = GroupsManager.MyGroups.AsEnumerable();
                    var myUngroups = MyUngroupedClusters.AsEnumerable();
                    if (newGroupVehicles != null)
                    {
                        myGroups = myGroups.ConcatSingle(group);
                        myUngroups = myUngroups.Where(cl => !cl.Equals(newGroupVehicles));
                    }

                    var danger = GetDanger(Environment, env, myGroups.ToList(), myUngroups.ToList(), cache);
                    if (selDanger == null || danger.Score < selDanger.Score)
                    {
                        selDanger = danger;
                        selMoves = new[]
                        {
                            selectionMove,
                            move,
                            newGroupVehicles == null
                                ? null
                                : AMovePresets.AssignGroup((int) group.Group)
                        }
                            .Where(x => x != null)
                            .ToArray();
                    }
                    return danger.Score;
                };

                Func<int, AMove> idxToMove = idx => new AMove
                {
                    Action = ActionType.Move,
                    Point =
                        Point.ByAngle(2*Math.PI/12*idx)*
                        startEnv.MyVehicles.Where(x => x.IsSelected).Max(x => x.ActualSpeed)*
                        ticksCount*40,
                    MaxSpeed = group.Group == GroupsManager.StartingTanksGroupId && Const.MixArrvsWithGrounds
                        ? Math.Min(G.MaxSpeed[(int) VehicleType.Tank], G.MaxSpeed[(int) VehicleType.Arrv])
                        : group.Group == GroupsManager.StartingIfvsGroupId && Const.MixArrvsWithGrounds
                            ? Math.Min(G.MaxSpeed[(int) VehicleType.Ifv], G.MaxSpeed[(int) VehicleType.Arrv])
                            : 0
                };

                var dangers = opt 
                    ? Enumerable.Range(0, 6).Select(i => checkAction(idxToMove(i*2))).ToArray()
                    : Enumerable.Range(0, 12).Select(i => checkAction(idxToMove(i))).ToArray();

                if (opt)
                {
                    var dangers2 = dangers.Select((x, i) => new Tuple<double, int>(x, i)).OrderBy(x => x.Item1).Select(x => x.Item2).Take(2).ToArray();

                    foreach (var i in dangers2.Select(i => i*2 + 1).Concat(dangers2.Select(i => i*2 - 1)).Distinct())
                        checkAction(idxToMove(i));
                }

                foreach (var move in 
                    Environment.Nuclears.Select(nuclear => AMovePresets.Scale(nuclear, 1.5))
                        .Concat(new[]
                        {
                            AMovePresets.Scale(typeRect.Center, 0.1),
                            AMovePresets.Rotate(typeRect.Center, Math.PI/4),
                            AMovePresets.Rotate(typeRect.Center, -Math.PI/4),
                        })
                        .Concat(
                            pos.OrderBy(cen => cen.GetDistanceTo2(typeRect.Center))
                                .Take(2)
                                .Select(cen => AMovePresets.MoveTo(typeRect.Center, cen))
                                )
                        .Concat(
                            neg.OrderBy(cen => cen.GetDistanceTo2(typeRect.Center))
                                .Take(1)
                                .Select(cen => AMovePresets.Move((typeRect.Center - cen).Take(150)))
                                ))
                {
                    checkAction(move);
                }

                if (targetFacilities.ContainsKey(group.Group))
                {
                    checkAction(AMovePresets.MoveTo(typeRect.Center, targetFacilities[group.Group]));
                }
            }

            return new Tuple<AMove[], DangerResult>(selMoves, selDanger);
        }

        private void _move(Game game)
        {
            Const.Initialize(World, game);
            Initialize();

            GroupsManager.Update(Environment);

            if (World.TickIndex == 0)
                MoveFirstTicks();
            ActionsQueue.Process();
            var ret = !MoveQueue.Free;
            MoveQueue.Run();
            if (ret)
                return;

            if (Me.RemainingActionCooldownTicks > 0)
                return;

            if (GroupsManager.MyGroups.Count == 0)
                return;

            var actionsBaseInterval = MoveObserver.ActionsBaseInterval;
            if (MyUngroupedClusters.Any(x => x.Count >= NewGroupMinSize))
                actionsBaseInterval++;
            if (World.TickIndex % actionsBaseInterval == 0 
                || MoveObserver.AvailableActions >= 4 && FirstMovesComplete && World.TickIndex >= _noMoveLastTick + actionsBaseInterval
                || Environment.Nuclears.Any(x => x.RemainingTicks >= G.TacticalNuclearStrikeDelay - 2))
            {
                var nuclearMove = NuclearStrategy();
                if (nuclearMove != null)
                {
                    ResultingMove = nuclearMove;
                    return;
                }

                var mainNew = DoMain(true);

                if (mainNew.Item1[0].Action == null || mainNew.Item1[0].Action == ActionType.None)
                    _noMoveLastTick = World.TickIndex;

                ResultingMove = mainNew.Item1[0];
                for (var i = 1; i < mainNew.Item1.Length; i++)
                {
                    var mv = mainNew.Item1[i];
                    MoveQueue.Add(mv);
                    if (mv.Action == ActionType.Assign && mainNew.Item1[0].VehicleType != null)
                        GroupsManager.AddPendingGroup(new MyGroup(mv.Group, mainNew.Item1[0].VehicleType.Value));
                }

            }

            if (ResultingMove == null || ResultingMove.Action == null || ResultingMove.Action == ActionType.None)
            {
                if (FirstMovesComplete)
                {
                    var facilitiesMove = FacilitiesStrategy();
                    if (facilitiesMove != null)
                        ResultingMove = facilitiesMove[0];
                }
            }
        }

        private int _noMoveLastTick = -Const.Infinity;
    }
}