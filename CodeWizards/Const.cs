using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class Const
    {
        public static double MapSize;
        public static double WizardRadius;

        public static double Eps = 1e-9;
        public static int Infinity = 0x3f3f3f3f;
        public static double TreeMaxRadius = 50;

        public static double BaseBuildingDistance = 400;

        public static Point[] BonusAppearencePoints =
        {
            new Point(1200, 1200),
            new Point(2800, 2800),
        };

        public static Point[] MapCorners = new Point[4];

        public static ProjectileInfo[] ProjectileInfo = new ProjectileInfo[4];

        public static void Initialize()
        {
            MapSize = MyStrategy.Game.MapSize;
            WizardRadius = MyStrategy.Game.WizardRadius;

            MapCorners[0] = new Point(0, 0);
            MapCorners[1] = new Point(0, MapSize);
            MapCorners[2] = new Point(MapSize, 0);
            MapCorners[3] = new Point(MapSize, MapSize);

            ProjectileInfo[(int) ProjectileType.MagicMissile] = new ProjectileInfo
            {
                ManaCost = MyStrategy.Game.MagicMissileManacost,
                Radius = MyStrategy.Game.MagicMissileRadius,
                DamageRadius = MyStrategy.Game.MagicMissileRadius,
                Speed = MyStrategy.Game.MagicMissileSpeed,
            };
            ProjectileInfo[(int)ProjectileType.FrostBolt] = new ProjectileInfo
            {
                ManaCost = MyStrategy.Game.FrostBoltManacost,
                Radius = MyStrategy.Game.FrostBoltRadius,
                DamageRadius = MyStrategy.Game.FrostBoltRadius,
                Speed = MyStrategy.Game.FrostBoltSpeed,
            };
            ProjectileInfo[(int)ProjectileType.Fireball] = new ProjectileInfo
            {
                ManaCost = MyStrategy.Game.FireballManacost,
                Radius = MyStrategy.Game.FireballRadius,
                DamageRadius = MyStrategy.Game.FireballExplosionMinDamageRange,
                Speed = MyStrategy.Game.FireballSpeed,
            };
            ProjectileInfo[(int)ProjectileType.Dart] = new ProjectileInfo
            {
                ManaCost = 0,
                Radius = MyStrategy.Game.DartRadius,
                DamageRadius = MyStrategy.Game.DartRadius,
                Speed = MyStrategy.Game.DartSpeed,
            };
        }
    }

    public class ProjectileInfo
    {
        public double Radius;
        public double DamageRadius;
        public double Speed;
        public double ManaCost;
    }

    public class MagicConst
    {
        public static double RadiusAdditionalEpsilon = 0.001;
        public static Point[] MinionAppearencePoints;
        public static int GoToBonusMaxTicks = 550;
        public static double SimplifyMaxLength = 1000;
		public static double TreeObstacleWeight = 35;
    }
}
