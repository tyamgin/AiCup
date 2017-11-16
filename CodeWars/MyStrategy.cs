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

        private AMove nextMove;
        private bool hasGroups = false;

        public static int FirstFroup = 1;
        public static int SecondGroup = 2;

        public static MyGroup[] MyGroups =
        {
            new MyGroup(VehicleType.Fighter), new MyGroup(VehicleType.Helicopter),
            new MyGroup(FirstFroup), new MyGroup(SecondGroup),
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
                    Rect = GetUnitsBoundingRect(Environment.MyVehicles.Where(x => x.Type == VehicleType.Tank || x.Type == VehicleType.Arrv && tankArrvs.Contains(x.Id) )),
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

            var tst = false;
            var lim = 300;


            if (World.TickIndex >= lim && World.TickIndex % 10 == 1 && nextMove != null)
            {
                ResultingMove = nextMove;
                nextMove = null;
                return;
            }
            nextMove = null;

            if (World.TickIndex >= lim && World.TickIndex % 10 == 0)
            {
                var minDanger = double.MaxValue;
                var selMove = new AMove();

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

                    int ticksCount = 8;
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
                            Point = Point.ByAngle(angle) * env.MyVehicles.Where(x => x.IsSelected).Max(x => x.ActualSpeed) * ticksCount * 10,
                            MaxSpeed = maxSpeed
                        };

                        var danger = GetDanger(env, move, ticksCount);
                        if (danger < minDanger)
                        {
                            minDanger = danger;
                            selMove = selectionMove ?? move;
                        }
                    }

                    var typeRect = GetUnitsBoundingRect(startEnv.GetVehicles(true, group));
                    foreach (var angle in Utility.Range(0, 2 * Math.PI, 4))
                    {
                        var env = startEnv.Clone();
                        
                        var move = new AMove
                        {
                            Action = ActionType.Scale,
                            Factor = 0.1,
                            Point = typeRect.Center + Point.ByAngle(angle) * (Math.Max(typeRect.Height, typeRect.Width)/2),
                            MaxSpeed = maxSpeed
                        };

                        var danger = GetDanger(env, move, ticksCount);
                        if (danger < minDanger)
                        {
                            minDanger = danger;
                            if (selectionMove == null)
                            {
                                selMove = move;
                            }
                            else
                            {
                                selMove = selectionMove;
                                nextMove = move;
                            }
                        }
                    }
                }
                ResultingMove = selMove;
            }

            return;

   //         var rect = GetUnitsBoundingRect(MyVehicles);
   //         var rectL = new Rect { X = rect.X, Y = rect.Y, X2 = rect.X + rect.Width / 2, Y2 = rect.Y2 };
   //         var rectR = new Rect { X = rect.X + rect.Width / 2, Y = rect.Y, X2 = rect.X2, Y2 = rect.Y2 };
   //         var rectT = new Rect { X = rect.X, Y = rect.Y, X2 = rect.X2, Y2 = rect.Y + rect.Height / 2 };
   //         var rectB = new Rect { X = rect.X, Y = rect.Y + rect.Height / 2, X2 = rect.X2, Y2 = rect.Y2 };

   //         var totalInterval = 160;
   //         var subInterval = 25;
   //         var curInterval = 0;



   //         if (_tryMoveRect(rect, rectL, rectR, totalInterval, curInterval * subInterval))
			//{
			//	return;
			//}
			//curInterval++;
			
			//if (_tryMoveRect(rect, rectT, rectB, totalInterval, curInterval * subInterval))
			//{
			//	return;
			//}
			//curInterval++;
			
			//if (_tryMoveRect(rect, rectR, rectL, totalInterval, curInterval * subInterval))
			//{
			//	return;
			//}
			//curInterval++;
			
			//if (_tryMoveRect(rect, rectB, rectT, totalInterval, curInterval * subInterval))
			//{
			//	return;
			//}
			//curInterval++;
			

			//if (_tryMoveRect(rect, null, null, totalInterval, curInterval * subInterval, true))
			//{
			//	return;
			//}
			//curInterval++;
			
			//var massCenter = GetAvg(MyVehicles);
			//var minD = 110 * Math.Sqrt(MyVehicles.Length) / Math.Sqrt(500);
			//var dx = rect.X2 - rect.X;
			//var dy = rect.Y2 - rect.Y;
   //         if (dx < minD * 1.2 && dy < minD * 1.2)
   //         {
   //             if (World.TickIndex%totalInterval == curInterval * subInterval)
   //             {
   //                 ResultingMove.Action = ActionType.ClearAndSelect;
   //                 ApplyREct(rect);
   //                 return;
   //             }
   //             if (World.TickIndex%totalInterval == curInterval * subInterval + 1)
   //             {
   //                 ResultingMove.Action = ActionType.Move;
   //                 var target = OppVehicles.OrderBy(x => x.GetDistanceTo2(massCenter)).FirstOrDefault();
   //                 if (target != null)
   //                 {
   //                     var cng = target - massCenter;
   //                     ResultingMove.X = cng.X;
   //                     ResultingMove.Y = cng.Y;
   //                 }
   //                 ResultingMove.MaxSpeed = 0.2;
   //                 return;
   //             }
   //         }
        }
    }
}