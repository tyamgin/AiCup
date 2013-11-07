using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using System.Threading;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;
using System.IO;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public static int Inf = 0x3f3f3f3f;
        public static double MaxTeamRadius = 5;

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
        int[,] map;
        int[,] danger;

        void Go(int toX, int toY)
        {
            move.X = toX;
            move.Y = toY;
            if (move.Action == ActionType.Move && self.X == toX && self.Y == toY) // это костыль
            {
                foreach(Point n in Nearest(move.X, move.Y))
                {
                    move.X = n.X;
                    move.Y = n.Y;
                    break;
                }
                move = move; //move.Action = ActionType.EndTurn;
            }
            if (map[move.X, move.Y] != 0 && move.Action == ActionType.Move) // это костыль
                move.Action = ActionType.EndTurn;
#if DEBUG
            Console.WriteLine(self.Type.ToString() + " " + move.Action.ToString() + " " + move.X + " " + move.Y);
            //Thread.Sleep(100);
#endif
            validateMove();
        }

        void Go(Point to)
        {
            Go(to.X, to.Y);
        }

        int getMoveCost()
        {
            if (self.Stance == TrooperStance.Prone)
                return game.ProneMoveCost;
            if (self.Stance == TrooperStance.Kneeling)
                return game.KneelingMoveCost;
            if (self.Stance == TrooperStance.Standing)
                return game.StandingMoveCost;
            throw new Exception("something wrong");
        }

        Trooper get(int x, int y)
        {
            foreach(Trooper tr in troopers)
                if (tr.X == x && tr.Y == y)
                    return tr;
            return null;
        }

        TrooperType[] commanderPriority = { TrooperType.Commander, TrooperType.Sniper, TrooperType.Soldier, TrooperType.FieldMedic, TrooperType.Scout };

        Trooper getCommander()
        {
            foreach(TrooperType type in commanderPriority)
                foreach (Trooper tr in team)
                    if (tr.Type == type)
                        return tr;
            throw new Exception("Have no player in my team");
        }

        void InitializeConstants()
        {
            this.troopers = world.Troopers;
            this.bonuses = world.Bonuses;
            this.cells = world.Cells;
            if (map == null)
                map = new int[world.Width, world.Height];
            for (int i = 0; i < world.Width; i++)
                for (int j = 0; j < world.Height; j++)
                    map[i, j] = cells[i][j] == 0 ? 0 : 1;
            team = new ArrayList();
            friend = new ArrayList();
            opponents = new ArrayList();
            foreach (Trooper tr in troopers)
            {
                if (tr.Id != self.Id)
                    map[tr.X, tr.Y] = 1;
                if (tr.IsTeammate)
                {
                    team.Add(tr);
                    if (tr.Id != self.Id)
                        friend.Add(tr);
                }
                else
                {
                    opponents.Add(tr);
                }
            }
            map[self.X, self.Y] = 0;
            commander = getCommander();
            danger = new int[world.Width, world.Height];

            foreach (Trooper tr in opponents)
            {
                for (int i = 0; i < world.Width; i++)
                {
                    for (int j = 0; j < world.Height; j++)
                    {
                        if (world.IsVisible(tr.ShootingRange, tr.X, tr.Y, tr.Stance, i, j, self.Stance))
                        {
                            danger[i, j]++;
                        }
                        if (world.IsVisible(tr.VisionRange, tr.X, tr.Y, tr.Stance, i, j, self.Stance))
                        {
                            danger[i, j]++;
                        }
                    }
                }
            }

#if DEBUG
            string path = "TestFolder\\" + "a" + game.MoveCount + ".txt";
            FileStream fs = File.Create(path);
            fs.Close();
            using (System.IO.StreamWriter file = new System.IO.StreamWriter(path, true))
            {
                for(int j = 0; j < world.Height; j++)
                {
                    string str = "";
                    for(int i = 0; i < world.Width; i++)
                        str += danger[i, j];
                    file.WriteLine(str);
                }
                file.WriteLine("");
            }
#endif
        }

        int[] _i = { 0, 0, 1, -1 };
        int[] _j = { 1, -1, 0, 0 };

        ArrayList Nearest(Unit unit, int mp = 0)
        {
            return Nearest(unit.X, unit.Y, mp);
        }

        ArrayList Nearest(int x, int y, int mp = 0)
        {
            ArrayList List = new ArrayList();
            for (int k = 0; k < 4; k++)
            {
                int ni = _i[k] + x;
                int nj = _j[k] + y;
                if (ni >= 0 && nj >= 0 && ni < world.Width && nj < world.Height && map[ni, nj] <= mp)
                    List.Add(new Point(ni, nj));
            }
            return List;
        }

        void validateMove()
        {
            if (move.Action == ActionType.EatFieldRation)
            {
                if (!self.IsHoldingFieldRation || game.FieldRationEatCost > self.ActionPoints)
                    throw new Exception("");
            }
            else if (move.Action == ActionType.EndTurn)
            {
                
            }
            else if (move.Action == ActionType.Heal)
            {
                // TODO: implement
                if (self.Type != TrooperType.FieldMedic)
                    throw new Exception("");
                if (!new Point(self.X, self.Y).Nearest(new Point(move.X, move.Y)))
                    throw new Exception("");
            }
            else if (move.Action == ActionType.LowerStance)
            {
                // TODO: implement
                throw new NotImplementedException();
            }
            else if (move.Action == ActionType.Move)
            {
                if (self.ActionPoints < getMoveCost())
                    throw new Exception("");
                Point to = new Point(move.X, move.Y);
                Point ths = new Point(self.X, self.Y);
                if (!to.Nearest(ths) || to.X < 0 || to.Y < 0 || to.X >= world.Width || to.Y >= world.Height || map[to.X, to.Y] != 0)
                    throw new Exception("");
            }
            else if (move.Action == ActionType.RaiseStance)
            {
                // TODO: implement
                throw new NotImplementedException();
            }
            else if (move.Action == ActionType.Shoot)
            {
                if (self.ShootCost > self.ActionPoints)
                    throw new Exception("");
                if (!world.IsVisible(self.ShootingRange, self.X, self.Y, self.Stance, move.X, move.Y, TrooperStance.Standing))
                    throw new Exception("");
                if (move.X == self.X && move.Y == self.Y)
                    throw new Exception("");
                if (move.X < 0 || move.Y < 0 || move.X >= world.Width || move.Y >= world.Height || cells[move.X][move.Y] != 0)
                    throw new Exception("");
            }
            else if (move.Action == ActionType.ThrowGrenade)
            {
                if (self.ActionPoints < game.GrenadeThrowCost)
                    throw new Exception("");
                if (move.X < 0 || move.Y < 0 || move.X >= world.Width || move.Y >= world.Height || cells[move.X][move.Y] != 0)
                    throw new Exception("");
                if (!self.IsHoldingGrenade || game.GrenadeThrowRange < self.GetDistanceTo(move.X, move.Y))
                    throw new Exception("");
            }
            else if (move.Action == ActionType.UseMedikit)
            {
                if (game.MedikitUseCost > self.ActionPoints || !self.IsHoldingMedikit)
                    throw new Exception("");
                if (move.X < 0 || move.Y < 0 || move.X >= world.Width || move.Y >= world.Height || !new Point(self.X, self.Y).Nearest(new Point(move.X, move.Y)))
                    throw new Exception("");

                // TODO:
            }
        }
    }
}
