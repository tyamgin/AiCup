using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public static WeatherType Weather(double x, double y)
        {
            int I, J;
            Utility.GetCell(x, y, out I, out J);
            return WeatherType[I][J];
        }

        public static TerrainType Terrain(double x, double y)
        {
            int I, J;
            Utility.GetCell(x, y, out I, out J);
            return TerrainType[I][J];
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

        public static void GetCell(double x, double y, out int I, out int J)
        {
            I = (int) ((x)/G.CellSize); // TODO: need Eps: http://russianaicup.ru/forum/index.php?topic=804.0
            J = (int) ((y)/G.CellSize);
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

        public static int ArgMax<T>(this IEnumerable<T> ie) where T : IComparable<T>
        {
            var res = 0;
            var idx = 0;
            var maxValue = default(T);
            foreach (var x in ie)
            {
                if (idx == 0 || x.CompareTo(maxValue) > 0)
                {
                    res = idx;
                    maxValue = x;
                }
                idx++;
            }
            return res;
        }

        public static void ForEach<T>(this IEnumerable<T> enumeration, Action<T> action)
        {
            foreach (var item in enumeration)
                action(item);
        }
    }
}
