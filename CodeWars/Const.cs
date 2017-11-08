using System;
using System.Collections.Generic;
using System.IO.Compression;
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

            _expect(game.ArrvDurability, 100);
            _expect(game.FighterDurability, 100);
            _expect(game.HelicopterDurability, 100);
            _expect(game.IfvDurability, 100);
            _expect(game.TankDurability, 100);
        }

        private static void _expect<T>(T source, T value)
        {
            if (!value.Equals(source))
                throw new Exception("Expected " + source + " to equal " + value);
        }
    }

    class G
    {
        public static double CellSize = 32;
        public static double MaxDurability = 100;
    }


}
