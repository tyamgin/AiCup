using System;
using System.Collections.Generic;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;
using System.Diagnostics;
using System.Globalization;
using System.Linq;
using System.Threading;

/**
 * TODO:
 * - неправильная сумма полей
 * - лечение в ПП
 * - прятать вертолеты от самолетов
 * - оптимизировать начальное построение
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
        public static Sandbox Environment;
        public static List<VehiclesCluster> OppClusters;

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



        public static int TanksGroup = 1;
        public static int IfvsGroup = 2;

        public static MyGroup[] MyGroups =
        {
            new MyGroup(VehicleType.Fighter), new MyGroup(VehicleType.Helicopter),
            new MyGroup(TanksGroup), new MyGroup(IfvsGroup),
        };

        static VehicleType GroupFighter(MyGroup group)
        {
            VehicleType type;
            if (group.Group == TanksGroup)
                type = VehicleType.Tank;
            else if (group.Group == IfvsGroup)
                type = VehicleType.Ifv;
            else
                type = (VehicleType)group.Type;
            return type;
        }

        private void _move(Game game)
        {
            Const.Initialize(World, game);

            if (TerrainType == null)
            {
                TerrainType = World.TerrainByCellXY;
                WeatherType = World.WeatherByCellXY;
            }

            var nuclears = World.Players
                .Where(player => player.NextNuclearStrikeVehicleId != -1)
                .Select(player => new ANuclear(
                    player.NextNuclearStrikeX,
                    player.NextNuclearStrikeY,
                    player.IsMe,
                    player.NextNuclearStrikeVehicleId,
                    player.NextNuclearStrikeTickIndex - World.TickIndex)
                )
                .ToArray();

            VehiclesObserver.Update();
            MoveObserver.Init();
            Environment = new Sandbox(VehiclesObserver.Vehicles, nuclears) {TickIndex = World.TickIndex};
            OppClusters = Environment.GetClusters(false, Const.ClusteringMargin);

            if (World.TickIndex == 0)
                MoveFirstTicks();
            ActionsQueue.Process();
            var ret = !MoveQueue.Free;
            MoveQueue.Run();
            if (ret)
                return;

            if (!FirstMovesComplete)
                return;

            if (Me.RemainingActionCooldownTicks > 0)
                return;


            if (World.TickIndex % MoveObserver.ActionsBaseInterval == 0 
                || MoveObserver.AvailableActions >= 4 
                || Environment.Nuclears.Any(x => x.RemainingTicks >= G.TacticalNuclearStrikeDelay - 2)
                || Environment.Nuclears.Any(x => x.RemainingTicks == MoveObserver.ActionsBaseInterval / 2))
            {
                var nuclearMove = NuclearStrategy();
                if (nuclearMove != null)
                {
                    ResultingMove = nuclearMove;
                    return;
                }

                DangerResult selDanger = null;
                AMove selMove = new AMove();
                AMove selNextMove = null;

                foreach (var group in MyGroups)
                {
                    if (Environment.GetVehicles(true, group).Count == 0)
                        continue;

                    var selectedIds = Utility.UnitsHash(Environment.MyVehicles.Where(x => x.IsSelected));
                    var needToSelectIds = Utility.UnitsHash(Environment.GetVehicles(true, group));

                    var startEnv = Environment.Clone();

                    var ticksCount = Const.ActionsBruteforceDepth;
                    var maxSpeed = 0.0;
                    AMove selectionMove = null;

                    if (selectedIds != needToSelectIds)
                    {
                        if (MoveObserver.AvailableActions < 2)
                            continue;

                        selectionMove = new AMove
                        {
                            Action = ActionType.ClearAndSelect,
                            MyGroup = group,
                            Rect = G.MapRect,
                        };
                        startEnv.ApplyMove(selectionMove);
                        startEnv.DoTick();
                        ticksCount--;
                    }

                    var typeRect = Utility.BoundingRect(startEnv.GetVehicles(true, group));

                    Sandbox partialEnv = null;
                    if (Environment.Nuclears.Length == 0)
                    {
                        partialEnv = new Sandbox(
                            startEnv.Vehicles.Where(x => !x.IsSelected),
                            new ANuclear[] {},
                            clone: true
                            );
                        partialEnv.CheckCollisionsWithOpponent = false;

                        for (var i = 0; i < ticksCount; i++)
                            partialEnv.DoTick();
                    }

                    List<Point> pos = new List<Point>(), neg = new List<Point>();
                    var myType = GroupFighter(group);
                    for (var clIdx = 0; clIdx < OppClusters.Count; clIdx++)
                    {
                        var cl = OppClusters[clIdx];
                        foreach (var oppType in Const.AllTypes)
                        {
                            if (cl.CountByType[(int) oppType] > 0)
                            {
                                var avg = Utility.Average(cl.Where(x => x.Type == oppType));
                                if (G.AttackDamage[(int) myType, (int) oppType] > 0)
                                    pos.Add(avg);
                                else if (G.AttackDamage[(int) oppType, (int) myType] > 0)
                                    neg.Add(avg);
                            }
                        }
                    }

                    var actions = Utility.Range(0, 2*Math.PI, 12).Select(angle => new AMove
                    {
                        Action = ActionType.Move,
                        Point =
                            Point.ByAngle(angle)*startEnv.MyVehicles.Where(x => x.IsSelected).Max(x => x.ActualSpeed)*
                            ticksCount*(group.Type == null ? 40 : 7),
                        MaxSpeed = maxSpeed
                    })
                        .Concat(Environment.Nuclears.Select(nuclear => new AMove
                        {
                            Action = ActionType.Scale,
                            Factor = 1.5,
                            Point = nuclear
                        }))
                        .Concat(new[]
                        {
                            new AMove
                            {
                                Action = ActionType.Scale,
                                Factor = 0.1,
                                Point = typeRect.Center,
                                MaxSpeed = maxSpeed,
                            },
                            new AMove
                            {
                                Action = ActionType.Rotate,
                                Point = typeRect.Center,
                                Angle = Math.PI/4,
                            },
                            new AMove
                            {
                                Action = ActionType.Rotate,
                                Point = typeRect.Center,
                                Angle = -Math.PI/4,
                            },
                        })
                        .Concat(
                            pos.OrderBy(cen => cen.GetDistanceTo2(typeRect.Center))
                                .Take(2)
                                .Select(cen => new AMove
                                {
                                    Action = ActionType.Move,
                                    Point = cen - typeRect.Center
                                }))
                        .Concat(
                            neg.OrderBy(cen => cen.GetDistanceTo2(typeRect.Center))
                                .Take(1)
                                .Select(cen => new AMove
                                {
                                    Action = ActionType.Move,
                                    Point = (typeRect.Center - cen).Take(150)
                                }));



                    foreach (var move in actions)
                    {
                        Sandbox env = partialEnv != null
                            ? new Sandbox(partialEnv.OppVehicles.Concat(startEnv.GetVehicles(true, group)), startEnv.Nuclears, clone: true)
                            : startEnv.Clone();

                        env.CheckCollisionsWithOpponent = false;

                        if (partialEnv != null)
                        {
                            foreach (var veh in env.Vehicles)
                            {
                                if (veh.IsMy && veh.IsGroup(group))
                                    continue;

                                veh.ForgotTarget(); // чтобы не шли повторно
                                // TODO: лечение

                                if (veh.RemainingAttackCooldownTicks > 0) // у тех, кто стрелял давно, откатываем кд
                                    veh.RemainingAttackCooldownTicks += ticksCount; // TODO: если кд только восстановилось
                            }
                        }

                        Logger.CumulativeOperationStart("End of simulation");
                        env.ApplyMove(move);
                        for (var i = 0; i < ticksCount; i++)
                            env.DoTick();
                        if (partialEnv != null)
                            env.AddRange(partialEnv.MyVehicles.Where(x => !x.IsGroup(group)));
                        Logger.CumulativeOperationEnd("End of simulation");

                        var danger = GetDanger(Environment, env);
                        if (selDanger == null || danger.Score < selDanger.Score)
                        {
                            selDanger = danger;
                            if (selectionMove == null)
                            {
                                selMove = move;
                                selNextMove = null;
                            }
                            else
                            {
                                selMove = selectionMove;
                                selNextMove = move;
                            }
                        }
                    }
                }
                ResultingMove = selMove;
                if (selNextMove != null)
                    MoveQueue.Add(selNextMove);
            }
        }
    }
}