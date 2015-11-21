using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public const int Infinity = 0x3f3f3f3f;
        public const double Eps = 1e-9;

        public double GetSpeed(Unit u)
        {
            return Math.Sqrt(u.SpeedX * u.SpeedX + u.SpeedY * u.SpeedY);
        }
        
        public Cell GetNextWayPoint(Car car, int delta = 1)
        {
            return waypoints[(car.NextWaypointIndex + delta - 1) % waypoints.Length];
        }

        public static Cell GetCell(double x, double y)
        {
            return new Cell((int)(y / game.TrackTileSize), (int)(x / game.TrackTileSize));
        }

        public static Cell GetCell(Point p)
        {
            return GetCell(p.X, p.Y);
        }

        public static Point GetCenter(int I, int J)
        {
            return new Point((J + 0.5) * game.TrackTileSize, (I + 0.5) * game.TrackTileSize);
        }

        public static Point GetCenter(Cell cell)
        {
            return GetCenter(cell.I, cell.J);
        }

        public static double TurnRound(double x)
        {
            if (x < -1)
                return -1;
            if (x > 1)
                return 1;
            return x;
        }


        private readonly List<Stopwatch> _timers = new List<Stopwatch>();

        public void TimerStart()
        {
#if DEBUG
            var timer = new Stopwatch();
            timer.Start();
            _timers.Add(timer);
#endif
        }

        public long TimerStop()
        {
#if DEBUG
            var res = _timers[_timers.Count - 1];
            res.Stop();
            _timers.RemoveAt(_timers.Count - 1);
            return res.ElapsedMilliseconds;
#else
            return 0;
#endif
        }

        public void TimeEndLog(string caption, long limit = TimerLogLimit)
        {
#if DEBUG
            var time = TimerStop();
            if (time > limit)
                Log(world.Tick + "> " + caption + ":" + time);
#endif
        }

        public void Log(object msg)
        {
#if DEBUG
            Console.WriteLine(msg);
#endif
        }
    }
}
