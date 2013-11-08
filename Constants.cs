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
        public static int Inf = 0x3f3f3f3f;
        public static double MaxTeamDiametr = -1; // Инициализация позже, как teamSize + 1
        public static double MaxTeamRadius = 3;

        public static int DangerNothing = 0;
        public static int DangerVisible = 1;
        public static int DangerShoot = 2;
        public static int DangerHighShoot = 3;

        Random random = new Random();

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
    }
}
