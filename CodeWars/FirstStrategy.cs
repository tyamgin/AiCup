using System;
using System.Linq;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public static Point GetUnitsAvg(AVehicle[] units)
        {
            return units.Aggregate(Point.Zero, (point, vehicle) => point + vehicle) / units.Length;
        }

        Rect GetUnitsBoundingRect(AVehicle[] units)
        {
            var ret = new Rect
            {
                X = units.Select(x => x.X).Min(),
                Y = units.Select(x => x.Y).Min(),
				X2 = units.Select(x => x.X).Max(),
				Y2 = units.Select(x => x.Y).Max(),
            };
            return ret;
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
