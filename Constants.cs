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
        public static Bonus BonusGoal = null;
        public static Point PointGoal = null;
        public static int playersCount = 4; // TODO: в зависимости от типа игры

        public static int Inf = 0x3f3f3f3f;
        public static double MaxTeamRadius = 2;

        public static int DangerNothing = 0;
        public static int DangerVisible = 1;
        public static int DangerShoot = 2;
        public static int DangerHighShoot = 3;

        public static Random random = new Random();

        TrooperType[] commanderPriority = { TrooperType.Commander, TrooperType.Sniper, TrooperType.Soldier, TrooperType.FieldMedic, TrooperType.Scout };

        World world;
        Move move;
        Game game;
        Trooper self, commander;
        Trooper[] troopers;
        ArrayList team, friend, opponents;
        Bonus[] bonuses;
        CellType[][] cells;
        int[,] map, notFilledMap;
        int[,] danger;
        int width, height;









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
