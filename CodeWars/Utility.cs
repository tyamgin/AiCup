using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.CompilerServices;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
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

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static bool Equals(double x, double y)
        {
            return Math.Abs(x - y) < Const.Eps;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static void GetCell(double x, double y, out int I, out int J)
        {
            I = (int) ((x)/G.CellSize); // TODO: need Eps: http://russianaicup.ru/forum/index.php?topic=804.0
            J = (int) ((y)/G.CellSize);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static void Dec(ref int value)
        {
            if (value > 0)
                value--;
        }

        public static IEnumerable<double> Range(double min, double max, int segments, bool rightInclusive = true)
        {
            for (var i = 0; i < segments; i++)
                yield return (max - min)/segments*i + min;
            if (rightInclusive)
                yield return max;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static void Swap<T>(ref T lhs, ref T rhs)
        {
            var temp = lhs;
            lhs = rhs;
            rhs = temp;
        }

        public static int ResizeArray<T>(ref T[] arr, int size, T defaultValue = default(T))
        {
            if (arr == null)
            {
                arr = new T[size];
                for (var i = 0; i < arr.Length; i++)
                    arr[i] = defaultValue;
                return 0;
            }
            if (arr.Length == size)
                return 0;

            var offset = arr.Length;
            Array.Resize(ref arr, size);
            for (var i = offset; i < arr.Length; i++)
                arr[i] = defaultValue;

            return offset;
        }

        public static Point Average<T>(IEnumerable<T> units) where T : Point
        {
            var sum = Point.Zero;
            var count = 0;
            foreach (var unit in units)
            {
                sum.X += unit.X;
                sum.Y += unit.Y;
                count++;
            }
            return sum/count;
        }

        public static Rect BoundingRect<T>(IEnumerable<T> units) where T : Point
        {
            var minX = double.MaxValue;
            var maxX = double.MinValue;
            var minY = double.MaxValue;
            var maxY = double.MinValue;

            foreach (var unit in units)
            {
                if (unit.X < minX)
                    minX = unit.X;
                if (unit.Y < minY)
                    minY = unit.Y;
                if (unit.X > maxX)
                    maxX = unit.X;
                if (unit.Y > maxY)
                    maxY = unit.Y;
            }

            return new Rect
            {
                X = minX,
                Y = minY,
                X2 = maxX,
                Y2 = maxY,
            };
        }

        public static string UnitsHash<T>(IEnumerable<T> units) where T : AUnit
        {
            return string.Join(",", units
                .Select(x => x.Id)
                .OrderBy(id => id)
                .Select(x => x.ToString()));
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static bool IsAerial(VehicleType type)
        {
            return type == VehicleType.Fighter || type == VehicleType.Helicopter;
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

        public static IEnumerable<T> ConcatSingle<T>(this IEnumerable<T> enumerable, T value)
        {
            return enumerable.Concat(new[] { value });
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

        public static long MinimalExcluded(this IEnumerable<long> ie, long startingValue)
        {
            var set = new HashSet<long>();
            foreach (var x in ie)
                set.Add(x);
            while (set.Contains(startingValue))
                startingValue++;
            return startingValue;
        }
    }

    public static class AVehicleListExtension
    {
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static IEnumerable<AVehicle> VehicleType(this IEnumerable<AVehicle> ie, VehicleType type)
        {
            return ie.Where(x => x.Type == type);
        }
    }
}
