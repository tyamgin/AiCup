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
		
		bool _tryMoveRect(Rect rect, Rect rectFrom, Rect rectTo, int totalInterval, int intervalShift, bool isRotate = false) 
		{
			if (isRotate)
			{
				if (World.TickIndex % totalInterval == intervalShift)
				{
					ResultingMove.Action = ActionType.ClearAndSelect;
					ApplyREct(rect);
					return true;
				}

				if (World.TickIndex % totalInterval == intervalShift + 1)
				{
					ResultingMove.Action = ActionType.Rotate;
					ResultingMove.X = rect.Center.X;
					ResultingMove.Y = rect.Center.Y;
					ResultingMove.Angle = Math.PI / 4;
					return true;
				}
			}
			else
			{
				var minD = 110 * Math.Sqrt(MyVehicles.Length) / Math.Sqrt(500);
				var dx = rect.X2 - rect.X;
				var dy = rect.Y2 - rect.Y;
				var isHorizontal = rectFrom.X2 < rectTo.X2 || rectFrom.X2 > rectTo.X2;
				var need = isHorizontal && dx > minD || !isHorizontal && dy > minD;
				
				if (need && World.TickIndex%totalInterval == intervalShift)
				{
					ResultingMove.Action = ActionType.ClearAndSelect;
					ApplyREct(rectFrom);
					return true;
				}
				if (need && World.TickIndex%totalInterval == intervalShift + 1)
				{
					ResultingMove.Action = ActionType.Move;
					var delta = rect.Center - rectFrom.Center;
					ResultingMove.X = delta.X;
					ResultingMove.Y = delta.Y;
					return true;
				}
			}
			return false;
		}
    }
}
