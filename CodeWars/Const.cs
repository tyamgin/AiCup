using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    class Const
    {
        public static double Eps = 1e-9;
        public static int Infinity = 0x3f3f3f3f;
        public static double MapSize;

        public static void Initialize(World world, Game game)
        {
            MapSize = world.Width; // Width == Height
        }
    }

    class G
    {
        public static double CellSize = 32;
    }
}
