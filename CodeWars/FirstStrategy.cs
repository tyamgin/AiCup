using System;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public static Point GetAvg <T>(IEnumerable<T> units) where T : Point
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

        public static Rect GetUnitsBoundingRect(IEnumerable<AVehicle> units)
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

        void ApplyREct(Rect rect)
        {
            ResultingMove.Left = rect.X - 1e-6;
            ResultingMove.Right = rect.X2 + 1e-6;
            ResultingMove.Top = rect.Y - 1e-6;
            ResultingMove.Bottom = rect.Y2 + 1e-6;
        }
    }
}
