using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using System.Threading;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;
using System.IO;

// TODO: идет за бонусом и попадает под обстрел (не хватает очков) http://russianaicup.ru/game/view/22729
// TODO:!!! Если юнит находится в окружении, но не может стрелять - поменять позицию 

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        static StreamWriter file = null;


        void Go(ActionType type, int toX, int toY)
        {
            move.X = toX;
            move.Y = toY;
            if (move.Action == ActionType.Move && self.X == toX && self.Y == toY) // это костыль
            {
                int x = move.X, y = move.Y;
                foreach(Point n in Nearest(move.X, move.Y, map))
                {
                    move.X = n.X;
                    move.Y = n.Y;
                    break;
                }
            }
            if (move.Action == ActionType.Move && map[move.X, move.Y] != 0) // это костыль
                move.Action = ActionType.EndTurn;
#if DEBUG
            Thread.Sleep(50);
#endif
            //Debugger("Commander Move 3 1");
            //Debugger("8");
            validateMove();
        }

        void Go(ActionType type, Point to = null)
        {
            move.Action = type;
            if (to == null)
                Go(type, -1, -1);
            else
                Go(type, to.X, to.Y);
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

        Trooper getTrooperAt(Point point)
        {
            return getTrooperAt(point.X, point.Y);
        }

        Trooper getTrooperAt(int x, int y)
        {
            foreach(Trooper tr in troopers)
                if (tr.X == x && tr.Y == y)
                    return tr;
            return null;
        }

        Trooper getTrooper(long id)
        {
            foreach (Trooper tr in troopers)
                if (tr.Id == id)
                    return tr;
            return null;
        }

        Bonus getBonusAt(Point p)
        {
            foreach (Bonus bo in bonuses)
                if (bo.X == p.X && bo.Y == p.Y)
                    return bo;
            return null;
        }

        void ChangeCommander()
        {
            if (changedCommander == -1)
                changedCommander = world.MoveIndex;
            else
                changedCommander = -1;
        }

        Trooper getCommander()
        {
            int cnt = 0;
            foreach (TrooperType type in commanderPriority)
            {
                foreach (Trooper tr in team)
                {
                    if (tr.Type == type)
                    {
                        cnt++;
                        if (changedCommander != -1 && cnt == 2)
                            return tr;
                        if (changedCommander == -1 && cnt == 1)
                            return tr;
                        break;
                    }
                }
            }
            throw new Exception("Have no player in my team");
        }

        void InitializeConstants()
        {
            this.troopers = world.Troopers;
            this.bonuses = world.Bonuses;
            this.cells = world.Cells;
            this.width = world.Width;
            this.height = world.Height;
            if (alivePlayers == null)
            {
                alivePlayers = new ArrayList();
                foreach (Player pl in world.Players)
                    alivePlayers.Add(pl);
            }
            if (map == null)
                map = new int[width, height];
            if (notFilledMap == null)
                notFilledMap = new int[width, height];
            for (int i = 0; i < width; i++)
            {
                for (int j = 0; j < height; j++)
                {
                    map[i, j] = cells[i][j] == 0 ? 0 : 1;
                    notFilledMap[i, j] = map[i, j];
                }
            }
            team = new ArrayList();
            friend = new ArrayList();
            opponents = new ArrayList();
            foreach (Trooper tr in troopers)
            {
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
            if (changedCommander != -1 && world.MoveIndex - changedCommander >= 6)
                ChangeCommander();
            commander = getCommander();

            danger = new int[width, height];

            foreach (Trooper tr in opponents)
            {
                for (int i = 0; i < width; i++)
                {
                    for (int j = 0; j < height; j++)
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

            if (queue.Count == 0 || (long)queue[queue.Count - 1] != self.Id)
                queue.Add(self.Id);

        }

        static int[] _i = { 0, 0, 1, -1 };
        static int[] _j = { 1, -1, 0, 0 };

        ArrayList Nearest(Unit unit, int[,] map)
        {
            return Nearest(unit.X, unit.Y, map);
        }

        ArrayList Nearest(int x, int y, int[,] map)
        {
            ArrayList List = new ArrayList();
            for (int k = 0; k < 4; k++)
            {
                int ni = _i[k] + x;
                int nj = _j[k] + y;
                if (ni >= 0 && nj >= 0 && ni < width && nj < height && map[ni, nj] == 0)
                    List.Add(new Point(ni, nj));
            }
            return List;
        }

        bool isLastInTeam(Trooper tr)
        {
            // TODO: implement this?
            return false;
        }

        bool canMove()
        {
            return self.ActionPoints >= getMoveCost();
        }

        bool canLower()
        {
            return self.Stance != TrooperStance.Prone && self.ActionPoints >= game.StanceChangeCost;
        }

        bool canUpper()
        {
            return self.Stance != TrooperStance.Standing && self.ActionPoints >= game.StanceChangeCost;
        }

        TrooperStance Low(TrooperStance stance)
        {
            if (stance == TrooperStance.Standing)
                return TrooperStance.Kneeling;
            if (stance == TrooperStance.Kneeling)
                return TrooperStance.Prone;
            throw new Exception("");
        }

        TrooperStance High(TrooperStance stance)
        {
            if (stance == TrooperStance.Prone)
                return TrooperStance.Kneeling;
            if (stance == TrooperStance.Kneeling)
                return TrooperStance.Standing;
            throw new Exception("");
        }
    }
}
