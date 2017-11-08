using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;

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
            };
            ret.X2 = units.Select(x => x.X).Max();
            ret.Y2 = units.Select(x => x.Y).Max();
            return ret;
        }

        void ApplyREct(Rect rect)
        {
            ResultingMove.Left = rect.X;
            ResultingMove.Right = rect.X2;
            ResultingMove.Top = rect.Y;
            ResultingMove.Bottom = rect.Y2;
        }
    }
}
