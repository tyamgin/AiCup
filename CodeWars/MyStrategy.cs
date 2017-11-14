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
        public static AVehicle[] MyVehicles, OppVehicles;

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

        private void _move(Game game)
        {
            Const.Initialize(World, game);

            if (TerrainType == null)
            {
                TerrainType = World.TerrainByCellXY;
                WeatherType = World.WeatherByCellXY;
            }

            VehiclesObserver.Update();
            MyVehicles = VehiclesObserver.Vehicles.Where(x => x.IsMy).ToArray();
            OppVehicles = VehiclesObserver.Vehicles.Where(x => !x.IsMy).ToArray();

            var tst = false;
            var lim = 0;

            if (!tst)
            {
                var groupVehicles = MyVehicles.Where(x => !x.IsAerial).ToArray();
                var rect = GetUnitsBoundingRect(groupVehicles);
                var minD = 150*Math.Sqrt(groupVehicles.Length)/Math.Sqrt(500);

                if (World.TickIndex == 0)
                {
                    ResultingMove.Action = ActionType.AddToSelection;
                    ResultingMove.VehicleType = VehicleType.Tank;
                    ResultingMove.Right = ResultingMove.Bottom = G.MapSize;
                    return;
                }
                if (World.TickIndex == 1)
                {
                    ResultingMove.Action = ActionType.AddToSelection;
                    ResultingMove.VehicleType = VehicleType.Ifv;
                    ResultingMove.Right = ResultingMove.Bottom = G.MapSize;
                    return;
                }
                if (World.TickIndex == 2)
                {
                    ResultingMove.Action = ActionType.AddToSelection;
                    ResultingMove.VehicleType = VehicleType.Arrv;
                    ResultingMove.Right = ResultingMove.Bottom = G.MapSize;
                    return;
                }
                if (World.TickIndex == 3)
                {
                    ResultingMove.Action = ActionType.Assign;
                    ResultingMove.Group = 1;
                    return;
                }
                if (World.TickIndex == 4)
                {
                    ResultingMove.Action = ActionType.Rotate;
                    ResultingMove.Point = rect.Center;
                    ResultingMove.Angle = Math.PI;
                    return;
                }
                if (World.TickIndex < 60)
                    return;


                var dx = rect.X2 - rect.X;
                var dy = rect.Y2 - rect.Y;

                var need = dx > minD || dy > minD;


                lim = 1000;
                var interv = 80;
                if (World.TickIndex > lim)
                    interv = 150;


                if (World.TickIndex%interv == interv/2 - 1)
                {
                    ResultingMove.Action = ActionType.ClearAndSelect;
                    ResultingMove.Group = 1;
                    return;
                }


                if (need && World.TickIndex%interv == interv/2 && World.TickIndex > 200)
                {
                    if (World.TickIndex%(interv*2) == interv/2)
                    {
                        ResultingMove.Action = ActionType.Rotate;
                        ResultingMove.Point = rect.Center;
                        ResultingMove.Angle = Math.PI;
                    }
                    else
                    {
                        ResultingMove.Action = ActionType.Scale;
                        ResultingMove.Point = rect.Center;
                        ResultingMove.Factor = 0.1;
                    }
                    return;
                }
                if (!need && World.TickIndex%interv == interv/2)
                {
                    ResultingMove.Action = ActionType.Move;
                    var target = OppVehicles.OrderBy(x => x.GetDistanceTo2(rect.Center)).FirstOrDefault();
                    if (target != null)
                    {
                        ResultingMove.SetVector(rect.Center, target);
                    }
                    ResultingMove.MaxSpeed = 0.2;
                    return;
                }
            }

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

                foreach (var vehType in new[] { VehicleType.Fighter, VehicleType.Helicopter })
                {
                    if (MyVehicles.All(x => x.Type != vehType))
                        continue;

                    var selectedIds = string.Join(",", MyVehicles.Where(x => x.IsSelected)
                        .Select(x => x.Id)
                        .OrderBy(id => id)
                        .Select(x => x.ToString()));

                    var needToSelectIds = string.Join(",", MyVehicles.Where(x => x.Type == vehType)
                        .Select(x => x.Id)
                        .OrderBy(id => id)
                        .Select(x => x.ToString()));

                    var startEnv = new Sandbox(
                        MyVehicles
                            .Where(x => x.IsAerial/* || x.Type == VehicleType.Arrv*/)
                            .Concat(OppVehicles)
                        ).Clone();

                    int ticksCount = 7;
                    var avg = GetAvg(startEnv.Vehicles.Where(x => x.Type == vehType));
                    double maxSpeed = 0;// startEnv.OppVehicles.Min(x => x.GetDistanceTo2(avg)) < 70 && World.TickIndex < 2000 ? 0.4 : 0;
                    AMove selectionMove = null;

                    if (selectedIds != needToSelectIds)
                    {
                        selectionMove = new AMove
                        {
                            Action = ActionType.ClearAndSelect,
                            VehicleType = vehType,
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
                            Point = Point.ByAngle(angle) * env.MyVehicles.Where(x => x.IsSelected).Max(x => x.ActualSpeed) * ticksCount * 6,
                            MaxSpeed = maxSpeed
                        };

                        var danger = GetDanger(env, move, ticksCount);
                        if (danger < minDanger)
                        {
                            minDanger = danger;
                            selMove = selectionMove ?? move;
                        }
                    }

                    var typeRect = GetUnitsBoundingRect(startEnv.MyVehicles.Where(x => x.Type == vehType));
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