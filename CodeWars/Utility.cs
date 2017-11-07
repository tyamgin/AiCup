using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
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

        public static void TimerEndLog(string caption, long limit = 30)
        {
#if DEBUG
            var time = TimerStop();
            if (time > limit)
                Log(World.TickIndex + ">" + new string('-', _timers.Count * 2) + " " + caption + ":" + time);
#endif
        }

        public static void Log(object msg)
        {
#if DEBUG
            Console.WriteLine(msg);
#endif
        }
    }

    public class Utility
    {
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

        public static bool Equals(double x, double y)
        {
            return Math.Abs(x - y) < Const.Eps;
        }

        public static bool Less(double x, double y)
        {
            return x + Const.Eps < y;
        }

        public static bool PointsEqual(Point a, Point b)
        {
            return Equals(a.X, b.X) && Equals(a.Y, b.Y);
        }

        public static void Dec(ref int value)
        {
            if (value > 0)
                value--;
        }

        public static IEnumerable<double> Range(double min, double max, int segments, bool rightInclusive = true)
        {
            for (var i = 0; i < segments; i++)
                yield return (max - min) / segments * i + min;
            if (rightInclusive)
                yield return max;
        }
    }

    public static class ListExtension
    {
        public static T Last<T>(this List<T> list)
        {
            return list[list.Count - 1];
        }

        public static void Pop<T>(this List<T> list)
        {
            list.RemoveAt(list.Count - 1);
        }

        public static T ArgMin<T>(this IEnumerable<T> ie, Func<T, double> func)
        {
            var minValue = double.MaxValue;
            var res = default(T);
            foreach (var x in ie)
            {
                var value = func(x);
                if (value < minValue)
                {
                    minValue = value;
                    res = x;
                }
            }
            return res;
        }
    }
}
