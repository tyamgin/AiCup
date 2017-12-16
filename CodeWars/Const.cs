using System;
using System.Linq;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    class Const
    {
        public const double Eps = 1e-9;
        public const int Infinity = 0x3f3f3f3f;

        // Magic consts
        public const double ClusteringMargin = 20;
        public const int ActionsBruteforceDepth = 10;
        public static bool MixArrvsWithGrounds;

        public static VehicleType[] AerialTypes =
        {
            VehicleType.Fighter, VehicleType.Helicopter, 
        };

        public static VehicleType[] GroundTypes =
        {
            VehicleType.Arrv, VehicleType.Ifv, VehicleType.Tank,
        };

        public static VehicleType[] AllTypes =
        {
            VehicleType.Arrv, VehicleType.Fighter, VehicleType.Helicopter, VehicleType.Ifv, VehicleType.Tank,
        };

        public static void Initialize(World world, Game game)
        {
            if (world.TickIndex != 0)
                return;

            var hungTest = HungarianAssignment.Minimize(new double[3][] { new []{3.0, 10}, new [] {10.0, 2}, new [] {1.0, 10}, }, 2*G.MapSize);
            _expect(hungTest[0], -1);
            _expect(hungTest[1], 1);
            _expect(hungTest[2], 0);

            G.IsTopLeftStartingPosition = world.Players.Any(x => x.IsMe && x.Id == 1);

            _expect(world.Width, G.MapSize);
            _expect(world.Height, G.MapSize);

            _expect(game.ArrvDurability, G.MaxDurability);
            _expect(game.FighterDurability, G.MaxDurability);
            _expect(game.HelicopterDurability, G.MaxDurability);
            _expect(game.IfvDurability, G.MaxDurability);
            _expect(game.TankDurability, G.MaxDurability);

            _expect(game.CloudWeatherSpeedFactor, G.CloudWeatherSpeedFactor);
            _expect(game.RainWeatherSpeedFactor, G.RainWeatherSpeedFactor);
            _expect(game.SwampTerrainSpeedFactor, G.SwampTerrainSpeedFactor);
            _expect(game.ForestTerrainSpeedFactor, G.ForestTerrainSpeedFactor);

            const int
                arrv = (int) VehicleType.Arrv,
                fighter = (int) VehicleType.Fighter,
                helicopter = (int) VehicleType.Helicopter,
                ifv = (int) VehicleType.Ifv,
                tank = (int) VehicleType.Tank;

            _expect(Math.Max(0, game.FighterGroundDamage - game.ArrvAerialDefence), G.AttackDamage[fighter, arrv]);
            _expect(Math.Max(0, game.FighterAerialDamage - game.FighterAerialDefence), G.AttackDamage[fighter, fighter]);
            _expect(Math.Max(0, game.FighterAerialDamage - game.HelicopterAerialDefence), G.AttackDamage[fighter, helicopter]);
            _expect(Math.Max(0, game.FighterGroundDamage - game.IfvAerialDefence), G.AttackDamage[fighter, ifv]);
            _expect(Math.Max(0, game.FighterGroundDamage - game.TankAerialDefence), G.AttackDamage[fighter, tank]);

            _expect(Math.Max(0, game.HelicopterGroundDamage - game.ArrvAerialDefence), G.AttackDamage[helicopter, arrv]);
            _expect(Math.Max(0, game.HelicopterAerialDamage - game.FighterAerialDefence), G.AttackDamage[helicopter, fighter]);
            _expect(Math.Max(0, game.HelicopterAerialDamage - game.HelicopterAerialDefence), G.AttackDamage[helicopter, helicopter]);
            _expect(Math.Max(0, game.HelicopterGroundDamage - game.IfvAerialDefence), G.AttackDamage[helicopter, ifv]);
            _expect(Math.Max(0, game.HelicopterGroundDamage - game.TankAerialDefence), G.AttackDamage[helicopter, tank]);

            _expect(Math.Max(0, game.IfvGroundDamage - game.ArrvGroundDefence), G.AttackDamage[ifv, arrv]);
            _expect(Math.Max(0, game.IfvAerialDamage - game.FighterGroundDefence), G.AttackDamage[ifv, fighter]);
            _expect(Math.Max(0, game.IfvAerialDamage - game.HelicopterGroundDefence), G.AttackDamage[ifv, helicopter]);
            _expect(Math.Max(0, game.IfvGroundDamage - game.IfvGroundDefence), G.AttackDamage[ifv, ifv]);
            _expect(Math.Max(0, game.IfvGroundDamage - game.TankGroundDefence), G.AttackDamage[ifv, tank]);

            _expect(Math.Max(0, game.TankGroundDamage - game.ArrvGroundDefence), G.AttackDamage[tank, arrv]);
            _expect(Math.Max(0, game.TankAerialDamage - game.FighterGroundDefence), G.AttackDamage[tank, fighter]);
            _expect(Math.Max(0, game.TankAerialDamage - game.HelicopterGroundDefence), G.AttackDamage[tank, helicopter]);
            _expect(Math.Max(0, game.TankGroundDamage - game.IfvGroundDefence), G.AttackDamage[tank, ifv]);
            _expect(Math.Max(0, game.TankGroundDamage - game.TankGroundDefence), G.AttackDamage[tank, tank]);

            _expect(game.FighterGroundAttackRange, G.AttackRange[fighter, arrv]);
            _expect(game.FighterAerialAttackRange, G.AttackRange[fighter, fighter]);
            _expect(game.FighterAerialAttackRange, G.AttackRange[fighter, helicopter]);
            _expect(game.FighterGroundAttackRange, G.AttackRange[fighter, ifv]);
            _expect(game.FighterGroundAttackRange, G.AttackRange[fighter, tank]);

            _expect(game.HelicopterGroundAttackRange, G.AttackRange[helicopter, arrv]);
            _expect(game.HelicopterAerialAttackRange, G.AttackRange[helicopter, fighter]);
            _expect(game.HelicopterAerialAttackRange, G.AttackRange[helicopter, helicopter]);
            _expect(game.HelicopterGroundAttackRange, G.AttackRange[helicopter, ifv]);
            _expect(game.HelicopterGroundAttackRange, G.AttackRange[helicopter, tank]);

            _expect(game.IfvGroundAttackRange, G.AttackRange[ifv, arrv]);
            _expect(game.IfvAerialAttackRange, G.AttackRange[ifv, fighter]);
            _expect(game.IfvAerialAttackRange, G.AttackRange[ifv, helicopter]);
            _expect(game.IfvGroundAttackRange, G.AttackRange[ifv, ifv]);
            _expect(game.IfvGroundAttackRange, G.AttackRange[ifv, tank]);

            _expect(game.TankGroundAttackRange, G.AttackRange[tank, arrv]);
            _expect(game.TankAerialAttackRange, G.AttackRange[tank, fighter]);
            _expect(game.TankAerialAttackRange, G.AttackRange[tank, helicopter]);
            _expect(game.TankGroundAttackRange, G.AttackRange[tank, ifv]);
            _expect(game.TankGroundAttackRange, G.AttackRange[tank, tank]);

            _expect(game.FighterAttackCooldownTicks, G.AttackCooldownTicks);
            _expect(game.HelicopterAttackCooldownTicks, G.AttackCooldownTicks);
            _expect(game.IfvAttackCooldownTicks, G.AttackCooldownTicks);
            _expect(game.TankAttackCooldownTicks, G.AttackCooldownTicks);

            _expect(game.VehicleRadius, G.VehicleRadius);

            foreach (var a in G.AttackRange)
                _expect(true, a <= G.MaxAttackRange);

            _expect(1, game.ArrvRepairSpeed * G.ArrvRepairPoints);
            _expect(game.ArrvRepairRange, G.ArrvRepairRange);


            _expect(game.TacticalNuclearStrikeRadius, G.TacticalNuclearStrikeRadius);
            _expect(game.TacticalNuclearStrikeDelay, G.TacticalNuclearStrikeDelay);
            _expect(game.MaxTacticalNuclearStrikeDamage, G.MaxTacticalNuclearStrikeDamage);

            _expect(game.CloudWeatherVisionFactor, G.CloudWeatherVisionFactor);
            _expect(game.ForestTerrainVisionFactor, G.ForestTerrainVisionFactor);
            _expect(game.RainWeatherVisionFactor, G.RainWeatherVisionFactor);
            _expect(game.SwampTerrainVisionFactor, 1);

            _expect(game.ActionDetectionInterval, G.ActionDetectionInterval);
            _expect(game.BaseActionCount, G.BaseActionCount);

            _expect(game.AdditionalActionCountPerControlCenter, G.AdditionalActionCountPerControlCenter);

            _expect(game.ArrvSpeed, G.MaxSpeed[arrv]);
            _expect(game.FighterSpeed, G.MaxSpeed[fighter]);
            _expect(game.HelicopterSpeed, G.MaxSpeed[helicopter]);
            _expect(game.IfvSpeed, G.MaxSpeed[ifv]);
            _expect(game.TankSpeed, G.MaxSpeed[tank]);

            _expect(game.ArrvVisionRange, G.VisionRange[arrv]);
            _expect(game.FighterVisionRange, G.VisionRange[fighter]);
            _expect(game.HelicopterVisionRange, G.VisionRange[helicopter]);
            _expect(game.IfvVisionRange, G.VisionRange[ifv]);
            _expect(game.TankVisionRange, G.VisionRange[tank]);

            _expect(G.MaxSpeed.Max(), G.MaxVehicleSpeed);

            _expect(game.FacilityHeight, G.FacilitySize);
            _expect(game.FacilityWidth, G.FacilitySize);
            _expect(game.MaxFacilityCapturePoints, G.MaxFacilityCapturePoints);
            _expect(game.FacilityCapturePointsPerVehiclePerTick, G.FacilityCapturePointsPerVehiclePerTick);

            _expect(game.MaxUnitGroup, G.MaxUnitGroup);

            _expect(game.RainWeatherStealthFactor, G.RainWeatherStealthFactor);
            _expect(game.CloudWeatherStealthFactor, G.CloudWeatherStealthFactor);
            _expect(game.ForestTerrainStealthFactor, G.ForestTerrainStealthFactor);
            _expect(game.SwampTerrainStealthFactor, 1);
            _expect(game.ClearWeatherStealthFactor, 1);
            _expect(game.PlainTerrainStealthFactor, 1);

            _expect(game.ArrvProductionCost, G.ProductionCost[arrv]);
            _expect(game.FighterProductionCost, G.ProductionCost[fighter]);
            _expect(game.HelicopterProductionCost, G.ProductionCost[helicopter]);
            _expect(game.IfvProductionCost, G.ProductionCost[ifv]);
            _expect(game.TankProductionCost, G.ProductionCost[tank]);

            G.IsFacilitiesEnabled = world.Facilities.Length > 0;
            G.IsFogOfWarEnabled = game.IsFogOfWarEnabled;
            MixArrvsWithGrounds = !G.IsFacilitiesEnabled;
        }

        private static void _expect<T>(T source, T value)
        {
            if (!value.Equals(source))
                throw new Exception("Expected " + source + " to equal " + value);
        }
    }

    class G
    {
        public static bool IsTopLeftStartingPosition;
        public const double MapSize = 1024;
        public const double CellSize = 32;
        public const int MaxUnitsCount = 1000;
        public const int MaxDurability = 100;
        public const double CloudWeatherSpeedFactor = 0.8;
        public const double RainWeatherSpeedFactor = 0.6;
        public const double SwampTerrainSpeedFactor = 0.6;
        public const double ForestTerrainSpeedFactor = 0.8;
        public const int AttackCooldownTicks = 60;
        public const double VehicleRadius = 2;
        public const double MaxAttackRange = 20;
        public const int ArrvRepairPoints = 10;
        public const double ArrvRepairRange = 10;
        public const int TacticalNuclearStrikeRadius = 50;
        public const int TacticalNuclearStrikeDelay = 30;
        public const int MaxTacticalNuclearStrikeDamage = 99;
        public const double CloudWeatherVisionFactor = 0.8;
        public const double ForestTerrainVisionFactor = 0.8;
        public const double RainWeatherVisionFactor = 0.6;
        public const double MaxVehicleSpeed = 1.2;
        public const int ActionDetectionInterval = 60;
        public const int BaseActionCount = 12;
        public const int AdditionalActionCountPerControlCenter = 3;
        public static bool IsFacilitiesEnabled;
        public static bool IsFogOfWarEnabled;
        public const double FacilitySize = 64;
        public const double MaxFacilityCapturePoints = 100;
        public const double FacilityCapturePointsPerVehiclePerTick = 0.005;
        public const int MaxUnitGroup = 100;
        public const double RainWeatherStealthFactor = 0.6;
        public const double CloudWeatherStealthFactor = 0.8;
        public const double ForestTerrainStealthFactor= 0.6;

        public static bool IsAerialButerDetected;

        public static readonly int[,] AttackDamage = new int[5, 5]
        {
            //  Arrv Fighter Helicopter Ifv Tank
            {0, 0, 0, 0, 0},
            {0, 30, 60, 0, 0},
            {80, 10, 40, 20, 40},
            {40, 10, 40, 30, 10},
            {50, 0, 20, 40, 20}
        };

        public static readonly int[,] AttackRange = new int[5, 5]
        {
            {0, 0, 0, 0, 0},
            {0, 20, 20, 0, 0},
            {20, 18, 18, 20, 20},
            {18, 20, 20, 18, 18},
            {20, 18, 18, 20, 20}
        };

        public static readonly double[] MaxSpeed = {0.4, 1.2, 0.9, 0.4, 0.3};

        public static readonly double[] VisionRange = {60, 120, 100, 80, 80};

        public static readonly int[] ProductionCost = {60, 90, 75, 60, 60};

        public static readonly Rect MapRect = new Rect {X = 0, Y = 0, X2 = MapSize, Y2 = MapSize};
    }


}
