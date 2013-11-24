using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;
using System.Collections;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public static int changedCommander = -1;
        public static Point BonusGoal = null;
        public static long WhoseBonus = -1;
        public static Point PointGoal = null;
        public static ArrayList AlivePlayers = null;
        public static ArrayList queue = new ArrayList();
        public static int Inf = 0x3f3f3f3f;
        public static double Eps = 1e-9;
        public static double MaxTeamRadius;
        public static Hashtable Hitpoints = null;

        public static int DangerNothing = 0;
        public static int DangerVisible = 1;
        public static int DangerShoot = 2;
        public static int DangerHighShoot = 3;

        public static Random random = new Random();

        TrooperType[] commanderPriority = { TrooperType.Commander, TrooperType.Sniper, TrooperType.Soldier, TrooperType.FieldMedic, TrooperType.Scout };

        public World world;
        public Move move;
        public Game game;
        public Trooper self, commander;
        public Trooper[] troopers;
        public Trooper[] Team;
        public Trooper[] Friends; 
        public Trooper[] Opponents;
        public Bonus[] Bonuses;
        public CellType[][] Cells;
        public int[,] map, notFilledMap;
        public int[,] danger;
        public int Width, Height;
        private static ArrayList PastTroopers = new ArrayList();






        static bool Equal(Point point, Unit unit)
        {
            return point.X == unit.X && point.Y == unit.Y;
        }
        static bool Equal(Unit unit, Point point)
        {
            return point.X == unit.X && point.Y == unit.Y;
        }
        static bool Equal(Point point, Point unit)
        {
            return point.X == unit.X && point.Y == unit.Y;
        }
        static bool Equal(Unit point, Unit unit)
        {
            return point.X == unit.X && point.Y == unit.Y;
        }
    }
}
