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
        public static Move ResultingMove;

        public static TerrainType[][] TerrainType;
        public static WeatherType[][] WeatherType;
        public static AVehicle[] MyVehicles, OppVehicles;

        public void Move(Player me, World world, Game game, Move move)
        {
            // занулям чтобы случайно не использовать данные с предыдущего тика
            // ...

            World = world;
            Me = me;
            ResultingMove = move;

#if DEBUG
            while (Visualizer.Visualizer.Pause)
            {
                // pause here
            }
#endif

            TimerStart();
            _move(me, world, game, move);
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
        }

        private void _move(Player me, World world, Game game, Move move)
        {
            Const.Initialize(world, game);

            if (TerrainType == null)
            {
                TerrainType = world.TerrainByCellXY;
                WeatherType = world.WeatherByCellXY;
            }

            VehiclesObserver.Update();
            MyVehicles = VehiclesObserver.Vehicles.Where(x => x.IsMy).ToArray();
            OppVehicles = VehiclesObserver.Vehicles.Where(x => !x.IsMy).ToArray();

            var rect = GetUnitsBoundingRect(MyVehicles);
            var massCenter = GetUnitsAvg(MyVehicles);
            var rectL = new Rect { X = rect.X, Y = rect.Y, X2 = rect.X + rect.Width/2, Y2 = rect.Y2 };
            var rectR = new Rect { X = rect.X + rect.Width / 2, Y = rect.Y, X2 = rect.X2, Y2 = rect.Y2 };
            var rectT = new Rect { X = rect.X, Y = rect.Y, X2 = rect.X2, Y2 = rect.Y + rect.Height / 2 };
            var rectB = new Rect { X = rect.X, Y = rect.Y + rect.Height / 2, X2 = rect.X2, Y2 = rect.Y2 };
            var minD = 120 * MyVehicles.Length/ 500;
            var dx = rect.X2 - rect.X;
            if (dx > minD && world.TickIndex%200 == 0)
            {
                ResultingMove.Action = ActionType.ClearAndSelect;
                ApplyREct(rectL);
                return;
            }
            if (dx > minD && world.TickIndex%200 == 1)
            {
                ResultingMove.Action = ActionType.Move;
                var delta = rect.Center - rectL.Center;
                ResultingMove.X = delta.X;
                ResultingMove.Y = delta.Y;
                return;
            }

            var dy = rect.Y2 - rect.Y;
            if (dy > minD && world.TickIndex % 200 == 50)
            {
                ResultingMove.Action = ActionType.ClearAndSelect;
                ApplyREct(rectT);
                return;
            }
            if (dy > minD && world.TickIndex % 200 == 51)
            {
                ResultingMove.Action = ActionType.Move;
                var delta = rectB.Center - rect.Center;
                ResultingMove.X = delta.X;
                ResultingMove.Y = delta.Y;
                return;
            }

            if (world.TickIndex % 200 == 100)
            {
                ResultingMove.Action = ActionType.ClearAndSelect;
                ApplyREct(rect);
                return;
            }

            if (world.TickIndex % 200 == 101)
            {
                ResultingMove.Action = ActionType.Rotate;
                ResultingMove.X = rect.Center.X;
                ResultingMove.Y = rect.Center.Y;
                ResultingMove.Angle = Math.PI / 2;
                return;
            }

            if (dx < minD && dy < minD)
            {
                if (world.TickIndex%200 == 170)
                {
                    ResultingMove.Action = ActionType.ClearAndSelect;
                    ApplyREct(rect);
                    return;
                }
                if (world.TickIndex%200 == 171)
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