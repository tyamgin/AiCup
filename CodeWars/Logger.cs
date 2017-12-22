using System;
using System.Collections.Generic;
using System.Diagnostics;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class Logger
    {
        private static readonly List<Stopwatch> _timers = new List<Stopwatch>();

        public static void TimerStart()
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
            var res = _timers.Last();
            res.Stop();
            _timers.Pop();
            return res.ElapsedMilliseconds;
#else
            return 0;
#endif
        }

        private static long _allSum, _allSumN;

        public static void TimerEndLog(string caption, long limit = 30)
        {
#if DEBUG
            var time = TimerStop();
            if (time > limit)
                Log(MyStrategy.World.TickIndex + ">" + new string('-', _timers.Count*2) + " " + caption + ":" + time);
            if (caption == "All")
                _allSum += time;
            if (caption == "AllN")
                _allSumN += time;
#endif
        }

        public static void Log(object msg)
        {
#if DEBUG
            Console.WriteLine(msg);
#endif
        }

        private static readonly Dictionary<string, Stopwatch> _tickTimers = new Dictionary<string, Stopwatch>();
        private static readonly Dictionary<string, long> _timesSum = new Dictionary<string, long>();

        public static void CumulativeOperationStart(string key)
        {
#if DEBUG
            if (!_tickTimers.ContainsKey(key))
                _tickTimers[key] = new Stopwatch();

            _tickTimers[key].Start();
#endif
        }

        public static void CumulativeOperationEnd(string key)
        {
#if DEBUG
            _tickTimers[key].Stop();
#endif
        }

        public static void CumulativeOperationPrintAndReset(long limit = 30)
        {
#if DEBUG
            foreach (var item in _tickTimers)
            {
                var time = item.Value.ElapsedMilliseconds;
                if (time >= limit)
                    Log("[Cumulative] " + item.Key + ": " + time);
                if (_timesSum.ContainsKey(item.Key))
                    _timesSum[item.Key] += time;
                else
                    _timesSum[item.Key] = time;
                item.Value.Reset();
            }
#endif
        }

        public static void CumulativeOperationSummary()
        {
#if DEBUG
            foreach (var item in _timesSum)
            {
                Log("[Cumulative Summary] " + item.Key + ": " + item.Value);
            }
            Log("[Cumulative Summary] All: " + _allSum);
            Log("[Cumulative Summary] All nuclears: " + _allSumN);
#endif
        }
    }
}
