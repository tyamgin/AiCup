using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public const int Infinity = 0x3f3f3f3f;
        public const double Eps = 1e-9;

        public static bool IsPointVisible(Point point)
        {
            return Combats.Any(x => x.IsTeammate && point.GetDistanceTo2(x) <= x.VisionRange*x.VisionRange);
        }

        public static double EnsureInterval(double x, double left, double right)
        {
            if (x < left)
                x = left;
            if (x > right)
                x = right;
            return x;
        }

        public static double EnsureInterval(double x, double right)
        {
            if (x < -right)
                x = -right;
            if (x > right)
                x = right;
            return x;
        }

        private static readonly List<Stopwatch> _timers = new List<Stopwatch>();

        public void TimerStart()
        {
#if DEBUG
            var timer = new Stopwatch();
            timer.Start();
            _timers.Add(timer);
#endif
        }

        public static long TimerStop()
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

        public static void TimerEndLog(string caption, long limit = 30)
        {
#if DEBUG
            var time = TimerStop();
            if (time > limit)
                Log(World.TickIndex + "> " + caption + ":" + time);
#endif
        }

        public static void Log(object msg)
        {
#if DEBUG
            Console.WriteLine(msg);
#endif
        }
    }
}
