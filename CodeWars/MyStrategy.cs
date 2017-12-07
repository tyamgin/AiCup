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
        private int _myLastScore;
        private int _oppLastScore;

        ~MyStrategy()
        {
            _globalTimer.Stop();
            Console.WriteLine($"Last score: {_myLastScore}:{_oppLastScore}");
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

            _myLastScore = Me.Score;
            _oppLastScore = Opp.Score;


#if DEBUG
            while (Visualizer.Visualizer.Pause)
            {
                // pause here
                Thread.Sleep(20);
            }
#endif
            Logger.TimerStart();
            if (Me.NextNuclearStrikeVehicleId != -1)
                Logger.TimerStart();

            _move(game);
            Logger.CumulativeOperationPrintAndReset(2);
            if (world.TickIndex % 500 == 0)
                Logger.CumulativeOperationSummary();
            ResultingMove.ApplyTo(move);

            if (Me.NextNuclearStrikeVehicleId != -1)
                Logger.TimerEndLog("AllN", 1);
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


        Tuple<AMove[], DangerResult> DoMain()
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

                Logger.CumulativeOperationStart("First env actions");

                var startEnv = Environment.Clone();
                var ticksCount = baseTicksCount;

                if (newGroupVehicles != null)
                {
                    foreach (var newVeh in newGroupVehicles)
                        startEnv.AddVehicleGroup(startEnv.VehicleById[newVeh.Id], group.Group);
                }

                var selectedIds = Utility.UnitsHash(startEnv.MyVehicles.Where(x => x.IsSelected));
                var needToSelectIds = Utility.UnitsHash(startEnv.GetVehicles(true, group));

                List<AMove> selectionMoves = new List<AMove>();
                var availableActions = MoveObserver.AvailableActions;

                if (selectedIds != needToSelectIds)
                {
                    if (MoveObserver.AvailableActions < 2)
                    {
                        Logger.CumulativeOperationEnd("First env actions");
                        continue;
                    }

                    if (newGroupVehicles == null)
                        selectionMoves.Add(AMovePresets.ClearAndSelectGroup(group.Group));
                    else
                    {
                        selectionMoves.Add(AMovePresets.ClearAndSelectType(group.VehicleType, Utility.BoundingRect(newGroupVehicles)));
                        
                        foreach (var mg in GroupsManager.MyGroups)
                            if (mg.VehicleType == group.VehicleType && startEnv.MyVehicles.Where(x => x.IsSelected).Any(x => x.IsGroup(mg)))
                                selectionMoves.Add(new AMove {Action = ActionType.Deselect, Group = mg.Group});
                    }

                    if (MoveObserver.AvailableActions < selectionMoves.Count + 1)
                    {
                        Logger.CumulativeOperationEnd("First env actions");
                        continue;
                    }

                    foreach (var mv in selectionMoves)
                    {
                        startEnv.ApplyMove(mv);
                        startEnv.DoTick();
                        availableActions--;
                        ticksCount--;
                    }
                }
                availableActions--;

                Logger.CumulativeOperationEnd("First env actions");

                // availableActions теперь - это сколько действий останется после выполнения текущего тика
                // должно остаться хотябы 2, или 1, если обладатель ядерки selected
                var myNuclear = startEnv.Nuclears.FirstOrDefault(n => n.IsMy);
                if (myNuclear != null && availableActions < 2 - (startEnv.VehicleById[myNuclear.VehicleId].IsSelected ? 1 : 0))
                    continue;

                var typeRect = Utility.BoundingRect(startEnv.GetVehicles(true, group));

                Logger.CumulativeOperationStart("Partial env actions");

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


                    var grps = GroupsManager.MyGroups.Select(g => partialEnv.GetVehicles(true, g).ToArray()).ToArray();
                    for (var i = 0; i < ticksCount; i++)
                    {
                        for (var j = 0; j < grps.Length; j++)
                        {
                            var pgr = grps[j];
                            if (pgr.Length > 0)
                                partialEnv.DoMoveApprox(pgr, pgr[0].Action != AVehicle.MoveType.Scale);
                        }
                        partialEnv._doFacilities();
                        partialEnv._doNuclears();
                    }
                    partialEnv._doFight();

                    sumMaxAlmostAttacksCache = GetSumMaxAlmostAttacks(partialEnv, partialEnv.MyVehicles);
                }

                Logger.CumulativeOperationEnd("Partial env actions");

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


                Logger.CumulativeOperationStart("Pre last env actions");

                Sandbox preLastEnv = null;
                AVehicle[] nearOpponents = null;
                List<AVehicle> currentVehicles = null;
                AVehicle[] preLastEnvVehiclesCopy = null;

                if (partialEnv != null)
                {
                    currentVehicles = startEnv.GetVehicles(true, group);
                    var currentVehiclesCenter = Utility.BoundingRect(currentVehicles).Center;
                    nearOpponents = partialEnv.OppVehicles
                        .Where(x => x.GetDistanceTo2(currentVehiclesCenter) < Geom.Sqr(2*G.TacticalNuclearStrikeRadius))
                        .ToArray();

                    preLastEnv = new Sandbox(
                        partialEnv.Vehicles.Where(x => !x.IsGroup(group)).Concat(currentVehicles),
                        new ANuclear[] {}, 
                        new AFacility[] {},
                        clone: true
                    );
                    preLastEnvVehiclesCopy = preLastEnv.Vehicles.Select(x => new AVehicle(x)).ToArray();
                }

                Logger.CumulativeOperationEnd("Pre last env actions");

                Func<AMove, double> checkAction = move =>
                {
                    Logger.CumulativeOperationStart("Building nearby environment");
                    Sandbox env;

                    if (partialEnv == null)
                        env = startEnv.Clone();
                    else
                        env = new Sandbox(nearOpponents.Concat(currentVehicles),
                            startEnv.Nuclears, partialEnv.Facilities, clone: true);

                    env.CheckCollisionsWithOpponent = false;
                    env.UseFightOptimization = false;

                    if (partialEnv != null)
                    {
                        foreach (var veh in env.Vehicles)
                        {
                            if (veh.IsGroup(group))
                                continue;

                            veh.ForgotTarget(); // чтобы не шли повторно
                            veh.CanChargeFacility = false; // чтобы не захватывали повторно
                            // TODO: лечение

                            if (veh.RemainingAttackCooldownTicks > 0) // у тех, кто стрелял давно, откатываем кд
                                veh.RemainingAttackCooldownTicks += ticksCount;
                            // TODO: если кд только восстановилось
                        }
                    }
                    Logger.CumulativeOperationEnd("Building nearby environment");

                    Logger.CumulativeOperationStart("End of simulation");
                    env.ApplyMove(move);
                    try
                    {
                        env.DoTicksApprox(ticksCount,
                        moveApprox: move.Action == ActionType.Move || move.Action == ActionType.Rotate,
                        fightApprox: true);
                    }
                    catch(QuadTree<AVehicle>.PointAlreadyExistsException e)
                    {
                        Logger.Log(e.Message);
                        return double.PositiveInfinity;
                    }
                    finally
                    {
                        Logger.CumulativeOperationEnd("End of simulation");
                    }

                    Logger.CumulativeOperationStart("Building last environment");
                    var cache = sumMaxAlmostAttacksCache;
                    if (partialEnv != null)
                    {
                        var tmp = env;
                        env = preLastEnv;

                        for (var i = 0; i < env.Vehicles.Length; i++)
                        {
                            var veh = env.Vehicles[i];
                            if (tmp.VehicleById.ContainsKey(veh.Id))
                                env.UpdateVehicle(veh, tmp.VehicleById[veh.Id]);
                            else
                                env.UpdateVehicle(veh, preLastEnvVehiclesCopy[i]);
                        }

                        env.Nuclears = tmp.Nuclears;
                        env.Facilities = tmp.Facilities;
                        cache += GetSumMaxAlmostAttacks(env, env.GetVehicles(true, group));
                    }

                    var myGroups = GroupsManager.MyGroups.AsEnumerable();
                    var myUngroups = MyUngroupedClusters.AsEnumerable();
                    if (newGroupVehicles != null)
                    {
                        myGroups = myGroups.ConcatSingle(group);
                        myUngroups = myUngroups.Where(cl => !cl.Equals(newGroupVehicles));
                    }

                    if (env.Vehicles.Length != Environment.Vehicles.Length)
                        throw new Exception("Final sandbox size mismatch");
                    Logger.CumulativeOperationEnd("Building last environment");


                    var danger = GetDanger(Environment, env, myGroups.ToList(), myUngroups.ToList(), cache);
                    
                    if (selDanger == null || danger.Score < selDanger.Score)
                    {
                        selDanger = danger;
                        selMoves = selectionMoves.Concat(new[]
                        {
                            move,
                            newGroupVehicles == null
                                ? null
                                : AMovePresets.AssignGroup(group.Group)
                        })
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

                var dangers = Enumerable.Range(0, 6).Select(i => checkAction(idxToMove(i*2))).ToArray();
                    
                var dangers2 = dangers.Select((x, i) => new Tuple<double, int>(x, i)).OrderBy(x => x.Item1).Select(x => x.Item2).Take(2).ToArray();
                foreach (var i in dangers2.Select(i => i*2 + 1).Concat(dangers2.Select(i => i*2 - 1)).Distinct())
                    checkAction(idxToMove(i));

                foreach (var move in 
                    Environment.Nuclears
                        .Where(n =>
                            n.GetDistanceTo(Utility.Average(startEnv.MyVehicles.Where(x => x.IsSelected))) <
                            G.TacticalNuclearStrikeRadius*2)
                        .Select(nuclear => AMovePresets.Scale(nuclear, 1.5)))
                {
                    checkAction(move);
                }

                checkAction(AMovePresets.Scale(typeRect.Center, 0.1));
                checkAction(AMovePresets.Rotate(typeRect.Center, Math.PI/4));
                checkAction(AMovePresets.Rotate(typeRect.Center, -Math.PI/4));

                foreach (var move in 
                    pos.OrderBy(cen => cen.GetDistanceTo2(typeRect.Center))
                        .Take(2)
                        .Select(cen => AMovePresets.MoveTo(typeRect.Center, cen)))
                {
                    checkAction(move);
                }

                foreach (var move in
                    neg.OrderBy(cen => cen.GetDistanceTo2(typeRect.Center))
                        .Take(1)
                        .Select(cen => AMovePresets.Move((typeRect.Center - cen).Take(150))))
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

                var mainNew = DoMain();

                if (mainNew.Item1[0].Action == null || mainNew.Item1[0].Action == ActionType.None)
                    _noMoveLastTick = World.TickIndex;

                ResultingMove = mainNew.Item1[0];
                for (var i = 1; i < mainNew.Item1.Length; i++)
                {
                    var mv = mainNew.Item1[i];
                    MoveQueue.Add(mv);
                    if (mv.Action == ActionType.Assign && mainNew.Item1[0].VehicleType != null)
                    {
                        GroupsManager.AddPendingGroup(new MyGroup(mv.Group, mainNew.Item1[0].VehicleType.Value));
                    }
                }
                if (mainNew.Item1.Length >= 3)
                {
                    var nearestFacility = Environment.Facilities.ArgMin(
                        f => f.Center.GetDistanceTo2(ResultingMove.Rect.Center));
                    var changeProdMove = ChangeFactoryProduction(nearestFacility);
                    if (changeProdMove != null)
                        MoveQueue.Add(changeProdMove);
                }

            }

            if (ResultingMove == null || ResultingMove.Action == null || ResultingMove.Action == ActionType.None)
            {
                if (FirstMovesComplete)
                {
                    var facilitiesMove = FacilitiesStrategy();
                    if (facilitiesMove != null)
                        ResultingMove = facilitiesMove;
                }
            }
        }

        private int _noMoveLastTick = -Const.Infinity;
    }
}