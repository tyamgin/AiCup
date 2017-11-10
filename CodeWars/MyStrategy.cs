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

            TimerStart();
            _move(game);
            ResultingMove.ApplyTo(move);
            TimerEndLog("All", 0);
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

            var rect = GetUnitsBoundingRect(MyVehicles);
            var rectL = new Rect { X = rect.X,                Y = rect.Y,                 X2 = rect.X + rect.Width/2, Y2 = rect.Y2                };
            var rectR = new Rect { X = rect.X + rect.Width/2, Y = rect.Y,                 X2 = rect.X2,               Y2 = rect.Y2                };
            var rectT = new Rect { X = rect.X,                Y = rect.Y,                 X2 = rect.X2,               Y2 = rect.Y + rect.Height/2 };
            var rectB = new Rect { X = rect.X,                Y = rect.Y + rect.Height/2, X2 = rect.X2,               Y2 = rect.Y2                };
			
			var totalInterval = 160;
			var subInterval = 25;
			var curInterval = 0;

            if (World.TickIndex == 0)
            {
                ResultingMove.Action = ActionType.ClearAndSelect;
                ApplyREct(rect);
                return;
            }
            if (World.TickIndex == 1)
            {
                ResultingMove.Action = ActionType.Scale;
                ResultingMove.X = 60;
                ResultingMove.Y = 60;
                ResultingMove.Factor = 0.5;
                return;
            }
            return;

            if (_tryMoveRect(rect, rectL, rectR, totalInterval, curInterval * subInterval))
			{
				return;
			}
			curInterval++;
			
			if (_tryMoveRect(rect, rectT, rectB, totalInterval, curInterval * subInterval))
			{
				return;
			}
			curInterval++;
			
			if (_tryMoveRect(rect, rectR, rectL, totalInterval, curInterval * subInterval))
			{
				return;
			}
			curInterval++;
			
			if (_tryMoveRect(rect, rectB, rectT, totalInterval, curInterval * subInterval))
			{
				return;
			}
			curInterval++;
			

			if (_tryMoveRect(rect, null, null, totalInterval, curInterval * subInterval, true))
			{
				return;
			}
			curInterval++;
			
			var massCenter = GetUnitsAvg(MyVehicles);
			var minD = 110 * Math.Sqrt(MyVehicles.Length) / Math.Sqrt(500);
			var dx = rect.X2 - rect.X;
			var dy = rect.Y2 - rect.Y;
            if (dx < minD * 1.2 && dy < minD * 1.2)
            {
                if (World.TickIndex%totalInterval == curInterval * subInterval)
                {
                    ResultingMove.Action = ActionType.ClearAndSelect;
                    ApplyREct(rect);
                    return;
                }
                if (World.TickIndex%totalInterval == curInterval * subInterval + 1)
                {
                    ResultingMove.Action = ActionType.Move;
                    var target = OppVehicles.OrderBy(x => x.GetDistanceTo2(massCenter)).FirstOrDefault();
                    if (target != null)
                    {
                        var cng = target - massCenter;
                        ResultingMove.X = cng.X;
                        ResultingMove.Y = cng.Y;
                    }
                    ResultingMove.MaxSpeed = 0.2;
                    return;
                }
            }
        }
    }
}