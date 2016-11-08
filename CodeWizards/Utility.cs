using System;
using System.Collections.Generic;
using System.Diagnostics;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public const int Infinity = 0x3f3f3f3f;
        public const double Eps = 1e-9;

        public double GetSpeed(Unit u)
        {
            return Math.Sqrt(u.SpeedX * u.SpeedX + u.SpeedY * u.SpeedY);
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

        public void TimerEndLog(string caption, long limit = 30)
        {
#if DEBUG
            var time = TimerStop();
            if (time > limit)
                Log(world.TickIndex + "> " + caption + ":" + time);
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
