using System;
using System.Collections.Generic;
using System.Configuration;
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
            SaveHitpoints();
            //// Чищу, т.к.  инфа может устареть
            //if (move.Action == ActionType.EndTurn
            //    || move.Action == ActionType.UseMedikit && self.ActionPoints - game.MedikitUseCost == 0
            //    || move.Action == ActionType.Heal && self.ActionPoints - game.FieldMedicHealCost == 0
            //    || (move.Action == ActionType.LowerStance || move.Action == ActionType.RaiseStance) && self.ActionPoints - game.StanceChangeCost == 0
            //    || move.Action == ActionType.Move && self.ActionPoints - getMoveCost(self) == 0
            //    || move.Action == ActionType.Shoot && self.ActionPoints - self.ShootCost == 0
            //    || move.Action == ActionType.ThrowGrenade && self.ActionPoints - game.GrenadeThrowCost == 0
            //    || move.Action == ActionType.EatFieldRation && self.ActionPoints - game.FieldRationEatCost == 0
            //   )
            //    PastTroopers.Clear();
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
            return getMoveCost(this.self);
        }

        int getMoveCost(Trooper self)
        {
            return getMoveCost(self.Stance);
        }

        int getMoveCost(TrooperStance stance)
        {
            if (stance == TrooperStance.Prone)
                return game.ProneMoveCost;
            if (stance == TrooperStance.Kneeling)
                return game.KneelingMoveCost;
            if (stance == TrooperStance.Standing)
                return game.StandingMoveCost;
            throw new Exception("something wrong");
        }

        int getMoveCost(int stance)
        {
            if (stance == 0)
                return game.ProneMoveCost;
            if (stance == 1)
                return game.KneelingMoveCost;
            if (stance == 2)
                return game.StandingMoveCost;
            throw new Exception("something wrong");
        }

        void SaveHitpoints()
        {
            Hitpoints = new Hashtable();
            foreach (Trooper tr in team)
            {
                Hitpoints[tr.Id] = tr.Hitpoints;
            }
            if (move.Action == ActionType.Heal)
            {
                Trooper at = getTrooperAt(new Point(move.X, move.Y));
                if (at.Id == self.Id)
                    Hitpoints[at.Id] = Math.Min(at.MaximalHitpoints, (int)Hitpoints[at.Id] + game.FieldMedicHealSelfBonusHitpoints);
                else
                    Hitpoints[at.Id] = Math.Min(at.MaximalHitpoints, (int)Hitpoints[at.Id] + game.FieldMedicHealBonusHitpoints);
            }
            if (move.Action == ActionType.UseMedikit)
            {
                Trooper at = getTrooperAt(new Point(move.X, move.Y));
                if (at.Id == self.Id)
                    Hitpoints[at.Id] = Math.Min(at.MaximalHitpoints, (int)Hitpoints[at.Id] + game.MedikitHealSelfBonusHitpoints);
                else
                    Hitpoints[at.Id] = Math.Min(at.MaximalHitpoints, (int)Hitpoints[at.Id] + game.MedikitBonusHitpoints);
            }
        }

        public static int notAllowHillMoveIndex = -1;

        bool CheckShootMe()
        {
            if (Hitpoints == null || troopers.Count() != team.Count)
                return false;
            foreach (Trooper tr in team)
            {
                if ((int)Hitpoints[tr.Id] != tr.Hitpoints)
                {
                    notAllowHillMoveIndex = world.MoveIndex;
                    return true;
                }
            }
            if (notAllowHillMoveIndex == -1)
                return false;
            if (world.MoveIndex - notAllowHillMoveIndex > 1)
            {
                notAllowHillMoveIndex = -1;
                return false;
            }
            return true;
        }

        Trooper getTrooperAt(Point point)
        {
            return getTrooperAt(point.X, point.Y);
        }

        Trooper getTrooperAt(int x, int y)
        {
            return troopers.FirstOrDefault(tr => tr.X == x && tr.Y == y);
        }

        Trooper getTrooper(long id)
        {
            return troopers.FirstOrDefault(tr => tr.Id == id);
        }

        Bonus getBonusAt(Point p)
        {
            return bonuses.FirstOrDefault(bo => bo.X == p.X && bo.Y == p.Y);
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

            // Загружаем труперов с прошлого хода, и сохраняем с текущего
            for(int i = 0; i < PastTroopers.Count; i += 2)
            {
                Trooper past = PastTroopers[i] as Trooper;
                int when = (int) PastTroopers[i + 1];
                if (world.MoveIndex - when > 1)
                    continue;

                bool exist = false;
                foreach(Trooper opp in opponents)
                    if (opp.Id == past.Id)
                        exist = true;
                if (!exist && !IsVisible(past.X, past.Y))
                {
                    opponents.Add(past);
                    Array.Resize(ref troopers, troopers.Count() + 1);
                    troopers[troopers.Count() - 1] = past;
                }
            }
            var tmp = PastTroopers.Clone() as ArrayList;
            PastTroopers.Clear();
            foreach (Trooper tr in opponents)
            {
                PastTroopers.Add(tr);
                if (world.Troopers.FirstOrDefault(trooper => trooper.Id == tr.Id) != null)
                    PastTroopers.Add(world.MoveIndex);
                else
                {
                    for(int i = 0; i < tmp.Count; i += 2)
                        if ((tmp[i] as Trooper).Id == tr.Id)
                            PastTroopers.Add((int)tmp[i + 1]);
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

        bool IsVisible(int x, int y)
        {
            return team.Cast<Trooper>().Any(
                trooper => world.IsVisible(trooper.VisionRange, trooper.X, trooper.Y, trooper.Stance, x, y, TrooperStance.Prone)
            );
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
            return canUpper(this.self);
        }

        bool canUpper(TrooperStance stance, int actionPoints)
        {
            return stance != TrooperStance.Standing && actionPoints >= game.StanceChangeCost;
        }

        bool canUpper(Trooper self)
        {
            return canUpper(self.Stance, self.ActionPoints);
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

        int getStanceId(TrooperStance stance)
        {
            if (stance == TrooperStance.Prone)
                return 0;
            if (stance == TrooperStance.Kneeling)
                return 1;
            if (stance == TrooperStance.Standing)
                return 2;
            throw new Exception("Unknown TrooperStance");
        }

        TrooperStance getStance(int stance)
        {
            if (stance == 0)
                return TrooperStance.Prone;
            if (stance == 1)
                return TrooperStance.Kneeling;
            if (stance == 2)
                return TrooperStance.Standing;
            throw new Exception("Unknown TrooperStance");
        }

        long getCurrentLeaderId()
        {
            if (BonusGoal != null)
                return MyStrategy.whoseBonus;
            return commander.Id;
        }

        Trooper getCurrentLeader()
        {
            if (BonusGoal != null)
                return getTrooper(MyStrategy.whoseBonus);
            return commander;
        }

        void Swap(ref int a, ref int b)
        {
            int tmp = a;
            a = b;
            b = tmp;
        }
    }
}
