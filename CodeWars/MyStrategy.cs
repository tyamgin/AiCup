using System;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;
using System.Diagnostics;
using System.Globalization;
using System.Linq;
using System.Threading;

/**
 * TODO:
 * - отталкивающие поля
 * - nuclears - откладывать на следующий тик, бомбить маленькие группы тоже
 * - добавить в перебор прямые ходы
 * 
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

#if DEBUG
        public MyStrategy()
        {
            CultureInfo.DefaultThreadCurrentCulture = CultureInfo.InvariantCulture;
        }
#endif


        public void Move(Player me, World world, Game game, Move move)
        {
            World = world;
            Me = me;
            Opp = World.Players.FirstOrDefault(x => !x.IsMe);
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

        private bool hasGroups = false;

        public static int TanksGroup = 1;
        public static int IfvsGroup = 2;

        public static MyGroup[] MyGroups =
        {
            new MyGroup(VehicleType.Fighter), new MyGroup(VehicleType.Helicopter),
            new MyGroup(TanksGroup), new MyGroup(IfvsGroup),
        };

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
            Environment = new Sandbox(VehiclesObserver.Vehicles, nuclears);

            //if (World.TickIndex == 0)
            //{
            //    ResultingMove.Action = ActionType.ClearAndSelect;
            //    ResultingMove.Right = ResultingMove.Bottom = G.MapSize;
            //    return;
            //}

            //if (World.TickIndex == 1)
            //{
            //    ResultingMove.Action = ActionType.Move;
            //    ResultingMove.X = 1000;
            //    ResultingMove.Y = 100;
            //    return;
            //}
            //return;

            MoveFirstTicks();
            var ret = !MoveQueue.Free || !FirstMovesComplete;
            MoveQueue.Run();
            if (ret)
                return;

            if (!hasGroups)
            {
                hasGroups = true;
                MoveQueue.Add(new AMove
                {
                    Action = ActionType.ClearAndSelect,
                    Rect = GetUnitsBoundingRect(Environment.MyVehicles.Where(x => x.Type == VehicleType.Tank || x.Type == VehicleType.Arrv && tankArrvs.Contains(x.Id))),
                }, 0, 0);
                MoveQueue.Add(new AMove
                {
                    Action = ActionType.Assign,
                    Group = TanksGroup,
                }, 0, 0);
                MoveQueue.Add(new AMove
                {
                    Action = ActionType.ClearAndSelect,
                    Rect = GetUnitsBoundingRect(Environment.MyVehicles.Where(x => x.Type == VehicleType.Ifv || x.Type == VehicleType.Arrv && ifvArrvs.Contains(x.Id))),
                }, 0, 0);
                MoveQueue.Add(new AMove
                {
                    Action = ActionType.Assign,
                    Group = IfvsGroup,
                }, 0, 0);
                return;
            }

            if (Me.RemainingActionCooldownTicks > 0)
                return;

            if (World.TickIndex % 10 == 0 
                || MoveObserver.AvailableActions >= 4 
                || Environment.Nuclears.Any(x => x.RemainingTicks >= G.TacticalNuclearStrikeDelay - 2))
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

                    var selectedIds = string.Join(",", Environment.MyVehicles.Where(x => x.IsSelected)
                        .Select(x => x.Id)
                        .OrderBy(id => id)
                        .Select(x => x.ToString()));

                    var needToSelectIds = string.Join(",", Environment.GetVehicles(true, group)
                        .Select(x => x.Id)
                        .OrderBy(id => id)
                        .Select(x => x.ToString()));

                    var startEnv = Environment.Clone();

                    int ticksCount = 7;
                    double maxSpeed = 0;
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

                    var partialEnv = new Sandbox(startEnv.Vehicles.Where(x => !x.IsSelected), new ANuclear[] {}, clone: true);
                    partialEnv.CheckCollisionsWithOpponent = false;

                    for (var i = 0; i < ticksCount; i++)
                        partialEnv.DoTick();

                    var typeRect = GetUnitsBoundingRect(startEnv.GetVehicles(true, group));

                    var actions = Utility.Range(0, 2*Math.PI, 12).Select(angle => new AMove
                    {
                        Action = ActionType.Move,
                        Point = Point.ByAngle(angle)*startEnv.MyVehicles.Where(x => x.IsSelected).Max(x => x.ActualSpeed)*ticksCount*(group.Type == null ? 40 : 7),
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
                            Angle = Math.PI / 4,
                        },
                        new AMove
                        {
                            Action = ActionType.Rotate,
                            Point = typeRect.Center,
                            Angle = -Math.PI / 4,
                        },
                    });

                    foreach (var move in actions)
                    {
                        var env = new Sandbox(partialEnv.OppVehicles.Concat(startEnv.GetVehicles(true, group)), startEnv.Nuclears, clone: true);
                        env.CheckCollisionsWithOpponent = false;

                        foreach (var veh in env.Vehicles)
                        {
                            if (veh.IsMy && veh.IsGroup(group))
                                continue;

                            veh.ForgotTarget(); // чтобы не шли повторно
                            // TODO: лечение
                            
                            if (veh.RemainingAttackCooldownTicks > 0)// у тех, кто стрелял давно, откатываем кд
                                veh.RemainingAttackCooldownTicks += ticksCount; // TODO: если кд только восстановилось
                        }

                        env.ApplyMove(move);
                        for (var i = 0; i < ticksCount; i++)
                            env.DoTick();
                        env.AddRange(partialEnv.MyVehicles.Where(x => !x.IsGroup(group)));

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
                    MoveQueue.Add(selNextMove, 0, 0);
            }
        }
    }
}