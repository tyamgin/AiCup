using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public static bool IsPointVisible(Point point)
        {
            return Combats.Any(x => x.IsTeammate && point.GetDistanceTo2(x) <= x.VisionRange*x.VisionRange);
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

        public static bool PointsEqual(Point a, Point b)
        {
            return Equals(a.X, b.X) && Equals(a.Y, b.Y);
        }

        public static ACombatUnit CloneCombat(ACombatUnit combat)
        {
            if (combat is AWizard)
                return new AWizard(combat as AWizard);
            if (combat is ABuilding)
                return new ABuilding(combat as ABuilding);
            if (combat is AOrc)
                return new AOrc(combat as AOrc);
            if (combat is AFetish)
                return new AFetish(combat as AFetish);
            throw new Exception("unknown combat type");
        }

        public static void Dec(ref int value)
        {
            if (value > 0)
                value--;
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

        public delegate double OrderFunc<in T>(T value);

        public static T ArgMin<T>(this IEnumerable<T> ie, OrderFunc<T> func)
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
