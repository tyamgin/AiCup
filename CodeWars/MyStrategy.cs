using System;
using System.Collections.Generic;
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

        private static void _move(Player me, World world, Game game, Move move)
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



            if (world.TickIndex == 0)
            {
                move.Action = ActionType.ClearAndSelect;
                move.Right = world.Width;
                move.Bottom = world.Height;
                return;
            }

            if (world.TickIndex == 1)
            {
                move.Action = ActionType.Move;
                move.X = world.Width / 2.0D;
                move.Y = world.Height / 4.0D;
            }

            if (world.TickIndex % 100 == 0)
            {
                move.Action = ActionType.Rotate;
                move.X = 320;
                move.Y = 250;
                move.Angle = Math.PI / 4;
                move.MaxSpeed = 100;
                move.MaxSpeed = 0.1;
                return;
            }

            //if (world.TickIndex == 700)
            //{
            //    move.Action = ActionType.Move;
            //    move.X = world.Width / 2.0D;
            //    move.Y = world.Height / 2.0D;
            //    move.MaxSpeed = 0.2;
            //}
        }
    }
}