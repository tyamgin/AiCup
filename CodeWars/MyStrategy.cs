using System;
using System.Collections.Generic;
using System.Data;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;
using System.Diagnostics;
using System.Linq;
using System.Threading;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public static World World;
        public static Player Me;
        public static AMove ResultingMove;

        public static TerrainType[][] TerrainType;
        public static WeatherType[][] WeatherType;
        public static Sandbox Environment;

        public void Move(Player me, World world, Game game, Move move)
        {
            // занулям чтобы случайно не использовать данные с предыдущего тика
            // ...

            World = world;
            Me = me;
            ResultingMove = new AMove();

#if DEBUG
            while (Visualizer.Visualizer.Pause)
            {
                // pause here
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

        public static int FirstFroup = 1;
        public static int SecondGroup = 2;

        public static MyGroup[] MyGroups =
        {
            new MyGroup(VehicleType.Fighter), new MyGroup(VehicleType.Helicopter),
            new MyGroup(FirstFroup), new MyGroup(SecondGroup),
            //new MyGroup(VehicleType.Ifv), new MyGroup(VehicleType.Tank), new MyGroup(VehicleType.Arrv),   
        };

        private void _move(Game game)
        {
            Const.Initialize(World, game);

            if (TerrainType == null)
            {
                TerrainType = World.TerrainByCellXY;
                WeatherType = World.WeatherByCellXY;
            }

            VehiclesObserver.Update();
            Environment = new Sandbox(VehiclesObserver.Vehicles);

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
                    Group = FirstFroup,
                }, 0, 0);
                MoveQueue.Add(new AMove
                {
                    Action = ActionType.ClearAndSelect,
                    Rect = GetUnitsBoundingRect(Environment.MyVehicles.Where(x => x.Type == VehicleType.Ifv || x.Type == VehicleType.Arrv && ifvArrvs.Contains(x.Id))),
                }, 0, 0);
                MoveQueue.Add(new AMove
                {
                    Action = ActionType.Assign,
                    Group = SecondGroup,
                }, 0, 0);
                return;
            }

            if (Me.RemainingActionCooldownTicks > 0)
                return;

            if (World.TickIndex % 10 == 0)
            {
                var minDanger = double.MaxValue;
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

                    var startEnv = new Sandbox(Environment.Vehicles).Clone();

                    int ticksCount = 7;
                    double maxSpeed = 0;
                    AMove selectionMove = null;

                    if (selectedIds != needToSelectIds)
                    {
                        selectionMove = new AMove
                        {
                            Action = ActionType.ClearAndSelect,
                            MyGroup = group,
                            Right = G.MapSize,
                            Bottom = G.MapSize
                        };
                        startEnv.ApplyMove(selectionMove);
                        startEnv.DoTick();
                        ticksCount--;
                    }

                    foreach (var angle in Utility.Range(0, 2 * Math.PI, 12))
                    {
                        var env = startEnv.Clone();
                        var move = new AMove
                        {
                            Action = ActionType.Move,
                            Point = Point.ByAngle(angle) * env.MyVehicles.Where(x => x.IsSelected).Max(x => x.ActualSpeed) * ticksCount * (group.Type == null ? 40 : 7),
                            MaxSpeed = maxSpeed
                        };

                        var danger = GetDanger(env, move, ticksCount);
                        if (danger < minDanger)
                        {
                            minDanger = danger;
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

                    var typeRect = GetUnitsBoundingRect(startEnv.GetVehicles(true, group));
                    //foreach (var angle in Utility.Range(0, 2 * Math.PI, 4))
                    {
                        var env = startEnv.Clone();
                        
                        var move = new AMove
                        {
                            Action = ActionType.Scale,
                            Factor = 0.1,
                            Point = typeRect.Center,// + Point.ByAngle(angle) * (Math.Max(typeRect.Height, typeRect.Width)/2),
                            MaxSpeed = maxSpeed
                        };

                        var danger = GetDanger(env, move, ticksCount);
                        if (danger < minDanger)
                        {
                            minDanger = danger;
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