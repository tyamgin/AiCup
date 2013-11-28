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

        void _Go(ActionType type, int toX, int toY)
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
            RemoveKilledOpponents();
            validateMove();
        }

        void Go(ActionType type, Point to = null)
        {
            move.Action = type;
            if (to == null)
                _Go(type, -1, -1);
            else
                _Go(type, to.X, to.Y);
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

        void RemoveKilledOpponents()
        {
            if (move.Action == ActionType.Shoot)
            {
                var trooper = getTrooperAt(move.X, move.Y);
                
                for (var i = 0; i < PastTroopersInfo.Count; i += 3)
                {
                    if ((PastTroopersInfo[i] as Trooper).Id == trooper.Id)
                    {
                        if (trooper.Hitpoints <= self.GetDamage(self.Stance))
                        {
                            PastTroopersInfo.RemoveRange(i, 3);
                            break;
                        }
                        else
                        {
                            PastTroopersInfo[i] = GetClone(PastTroopersInfo[i] as Trooper,
                                self.GetDamage(self.Stance));
                        }
                    }
                }
            }
        }

        void SaveHitpoints()
        {
            Hitpoints = new Hashtable();
            foreach (var tr in Team)
            {
                Hitpoints[tr.Id] = tr.Hitpoints;
            }
            if (move.Action == ActionType.Heal)
            {
                var at = getTrooperAt(new Point(move.X, move.Y));
                if (at.Id == self.Id)
                    Hitpoints[at.Id] = Math.Min(at.MaximalHitpoints, (int)Hitpoints[at.Id] + game.FieldMedicHealSelfBonusHitpoints);
                else
                    Hitpoints[at.Id] = Math.Min(at.MaximalHitpoints, (int)Hitpoints[at.Id] + game.FieldMedicHealBonusHitpoints);
            }
            if (move.Action == ActionType.UseMedikit)
            {
                var at = getTrooperAt(new Point(move.X, move.Y));
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
            foreach (var tr in Team)
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
            var cnt = 0;
            foreach (var type in commanderPriority)
            {
                foreach (var trooper in Team)
                {
                    if (trooper.Type == type)
                    {
                        cnt++;
                        if (changedCommander != -1 && cnt == 2)
                            return trooper;
                        if (changedCommander == -1 && cnt == 1)
                            return trooper;
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
                foreach (var pl in world.Players)
                    AlivePlayers.Add(pl);
            }
            if (map == null)
                map = new int[Width, Height];
            if (notFilledMap == null)
                notFilledMap = new int[Width, Height];
            for (var i = 0; i < Width; i++)
            {
                for (var j = 0; j < Height; j++)
                {
                    map[i, j] = Cells[i][j] == 0 ? 0 : 1;
                    notFilledMap[i, j] = map[i, j];
                }
            }
            Opponents = new Trooper[0];
            Team = new Trooper[0];
            Friends = new Trooper[0];
            OpponentsMemoryAppearTime = new int[0];
            OpponentsMemoryId = new long[0];

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
                    OpponentsMemoryAppearTime = Append(OpponentsMemoryAppearTime, world.MoveIndex);
                    OpponentsMemoryId = Append(OpponentsMemoryId, self.Id);
                }
            }
            
            MaxTeamRadius = Team.Count() <= 3 ? 2 : 3;

            // Загружаем труперов с прошлого хода, и сохраняем с текущего
            // past - трупер
            // when - время, в которое был последний раз виден трупер
            // who - кто его видел последний раз
            for(var i = 0; i < PastTroopersInfo.Count; i += 3)
            {
                var past = PastTroopersInfo[i] as Trooper;
                var when = (int) PastTroopersInfo[i + 1];
                var who = (long) PastTroopersInfo[i + 2];
                if (world.MoveIndex - when > 1)
                    continue;

                if (!Opponents.Any(trooper => trooper.Id == past.Id) && !IsVisible(past.X, past.Y))
                {
                    Opponents = Append(Opponents, past);
                    OpponentsMemoryAppearTime = Append(OpponentsMemoryAppearTime, when);
                    OpponentsMemoryId = Append(OpponentsMemoryId, who);
                    troopers = Append(troopers, past);
                }
            }
            PastTroopersInfo.Clear();
            for(var i = 0; i < Opponents.Count(); i++)
            {
                PastTroopersInfo.Add(GetClone(Opponents[i], 0));
                PastTroopersInfo.Add(OpponentsMemoryAppearTime[i]);
                PastTroopersInfo.Add(OpponentsMemoryId[i]);
            }

            if (changedCommander != -1 && world.MoveIndex - changedCommander >= 6)
                ChangeCommander();
            commander = getCommander();

            danger = new int[Width, Height];
            foreach (var tr in Opponents)
            {
                for (var i = 0; i < Width; i++)
                {
                    for (var j = 0; j < Height; j++)
                    {
                        if (world.IsVisible(tr.ShootingRange, tr.X, tr.Y, tr.Stance, i, j, self.Stance))
                            danger[i, j]++;
                        if (world.IsVisible(tr.VisionRange, tr.X, tr.Y, tr.Stance, i, j, self.Stance))
                            danger[i, j]++;
                    }
                }
            }

            if (queue.Count == 0 || (long)queue[queue.Count - 1] != self.Id)
                queue.Add(self.Id);
        }

        bool IsVisible(int x, int y, TrooperStance stance = TrooperStance.Prone)
        {
            return Team.Any(
                trooper => world.IsVisible(trooper.VisionRange, trooper.X, trooper.Y, trooper.Stance, x, y, stance)
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

        Trooper GetClone(Trooper a, int minus_hitpoints)
        {
            return new Trooper(a.Id, a.X, a.Y, a.PlayerId, a.TeammateIndex, a.IsTeammate, a.Type, a.Stance, a.Hitpoints - minus_hitpoints, a.MaximalHitpoints, a.ActionPoints, a.InitialActionPoints, a.VisionRange, a.ShootingRange, a.ShootCost, a.StandingDamage, a.KneelingDamage, a.ProneDamage, a.Damage, a.IsHoldingGrenade, a.IsHoldingMedikit, a.IsHoldingFieldRation);
        }
    }
}
