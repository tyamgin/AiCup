using System;
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

            var t1 = game.CloudWeatherSpeedFactor;
            var t2 = game.RainWeatherSpeedFactor;
            var t3 = game.SwampTerrainSpeedFactor;
            var t4 = game.ForestTerrainSpeedFactor;
            return;
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
        public static double CloudWeatherSpeedFactor = 0.8;
        public static double RainWeatherSpeedFactor = 0.6;
        public static double SwampTerrainSpeedFactor = 0.6;
        public static double ForestTerrainSpeedFactor = 0.8;
    }


}
