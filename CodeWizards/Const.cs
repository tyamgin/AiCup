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
    }

    public class MagicConst
    {
        public static double RadiusAdditionalEpsilon = 0.5;
        public static Point[] MinionAppearencePoints;
        public static int GoToBonusmaxTicks = 650;
        public static double SimplifyMaxLength = 1000;
		public static double TreeObstacleWeight = 25;
    }
}
