using System;
using System.Collections.Generic;
using System.Configuration;
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
            foreach (Trooper tr in Team)
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
            if (Hitpoints == null || troopers.Count() != Team.Count())
                return false;
            foreach (Trooper tr in Team)
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
            return Bonuses.FirstOrDefault(bo => bo.X == p.X && bo.Y == p.Y);
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
                foreach (Trooper tr in Team)
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
            this.Bonuses = world.Bonuses;
            this.Cells = world.Cells;
            this.Width = world.Width;
            this.Height = world.Height;
            if (AlivePlayers == null)
            {
                AlivePlayers = new ArrayList();
                foreach (Player pl in world.Players)
                    AlivePlayers.Add(pl);
            }
            if (map == null)
                map = new int[Width, Height];
            if (notFilledMap == null)
                notFilledMap = new int[Width, Height];
            for (int i = 0; i < Width; i++)
            {
                for (int j = 0; j < Height; j++)
                {
                    map[i, j] = Cells[i][j] == 0 ? 0 : 1;
                    notFilledMap[i, j] = map[i, j];
                }
            }
            Opponents = new Trooper[0];
            Team = new Trooper[0];
            Friends = new Trooper[0];
            OpponentsAppearTime = new int[0];

            foreach (var tr in troopers)
            {
                map[tr.X, tr.Y] = 1;
                if (tr.IsTeammate)
                {
                    Team = Append(Team, tr);
                    if (tr.Id != self.Id)
                        Friends = Append(Friends, tr);
                }
                else
                {
                    Opponents = Append(Opponents, tr);
                    OpponentsAppearTime = Append(OpponentsAppearTime, 0);
                }
            }
            
            MaxTeamRadius = Team.Count() <= 3 ? 2 : 3;

            // Загружаем труперов с прошлого хода, и сохраняем с текущего
            for(int i = 0; i < PastTroopers.Count; i += 2)
            {
                var past = PastTroopers[i] as Trooper;
                var when = (int) PastTroopers[i + 1];
                if (world.MoveIndex - when > 1)
                    continue;

                bool exist = false;
                foreach(Trooper opp in Opponents)
                    if (opp.Id == past.Id)
                        exist = true;
                if (!exist && !IsVisible(past.X, past.Y))
                {
                    Opponents = Append(Opponents, past);
                    OpponentsAppearTime = Append(OpponentsAppearTime, when);
                    troopers = Append(troopers, past);
                }
            }
            var tmp = PastTroopers.Clone() as ArrayList;
            PastTroopers.Clear();
            foreach (Trooper tr in Opponents)
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

            danger = new int[Width, Height];

            foreach (Trooper tr in Opponents)
            {
                for (int i = 0; i < Width; i++)
                {
                    for (int j = 0; j < Height; j++)
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
            return Team.Any(
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
                if (ni >= 0 && nj >= 0 && ni < Width && nj < Height && map[ni, nj] == 0)
                    List.Add(new Point(ni, nj));
            }
            return List;
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
                return MyStrategy.WhoseBonus;
            return commander.Id;
        }

        Trooper getCurrentLeader()
        {
            if (BonusGoal != null)
                return getTrooper(MyStrategy.WhoseBonus);
            return commander;
        }

        void Swap(ref int a, ref int b)
        {
            int tmp = a;
            a = b;
            b = tmp;
        }

        Template[] Append<Template>(Template[] array, Template element)
        {
            Array.Resize(ref array, array.Count() + 1);
            array[array.Count() - 1] = element;
            return array;
        }
    }
}
