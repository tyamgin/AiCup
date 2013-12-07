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
        private void _Go(ActionType type, int toX, int toY)
        {
            move.X = toX;
            move.Y = toY;
            SaveHitpoints();
            RemoveKilledOpponents();
            ValidateMove();
        }

        void Go(ActionType type, Point to = null)
        {
            move.Action = type;
            if (to == null)
                _Go(type, -1, -1);
            else
                _Go(type, to.X, to.Y);
        }

        int GetMoveCost()
        {
            return GetMoveCost(this.self);
        }

        int GetMoveCost(Trooper self)
        {
            return GetMoveCost(self.Stance);
        }

        int GetMoveCost(TrooperStance stance)
        {
            if (stance == TrooperStance.Prone)
                return game.ProneMoveCost;
            if (stance == TrooperStance.Kneeling)
                return game.KneelingMoveCost;
            if (stance == TrooperStance.Standing)
                return game.StandingMoveCost;
            throw new InvalidDataException();
        }

        int GetMoveCost(int stance)
        {
            if (stance == 0)
                return game.ProneMoveCost;
            if (stance == 1)
                return game.KneelingMoveCost;
            if (stance == 2)
                return game.StandingMoveCost;
            throw new InvalidDataException();
        }

        void RemoveKilledOpponents()
        {
            if (move.Action == ActionType.Shoot)
            {
                var trooper = GetTrooperAt(move.X, move.Y);
                
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
                var at = GetTrooperAt(new Point(move.X, move.Y));
                if (at.Id == self.Id)
                    Hitpoints[at.Id] = Math.Min(at.MaximalHitpoints, (int)Hitpoints[at.Id] + game.FieldMedicHealSelfBonusHitpoints);
                else
                    Hitpoints[at.Id] = Math.Min(at.MaximalHitpoints, (int)Hitpoints[at.Id] + game.FieldMedicHealBonusHitpoints);
            }
            if (move.Action == ActionType.UseMedikit)
            {
                var at = GetTrooperAt(new Point(move.X, move.Y));
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

        Trooper GetTrooperAt(Point point)
        {
            return GetTrooperAt(point.X, point.Y);
        }

        Trooper GetTrooperAt(int x, int y)
        {
            return troopers.FirstOrDefault(tr => tr.X == x && tr.Y == y);
        }

        Trooper GetTrooper(long id)
        {
            return troopers.FirstOrDefault(tr => tr.Id == id);
        }

        Bonus GetBonusAt(Point p)
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

        Trooper GetCommander()
        {
            var cnt = 0;
            foreach (var type in CommanderPriority)
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
            throw new InvalidDataException();
        }

        void InitializeVariables()
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


            // Менять радиус в зависимости от количества оставшихся ходов:
            // Тогда будет возможность отбежать обратно
            MaxTeamRadius = 2;
            if (Team.Count() > 3 && self.ActionPoints >= 2*GetMoveCost(self))
                MaxTeamRadius += 1;

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

            if (changedCommander != -1 && world.MoveIndex - changedCommander >= 3)
                ChangeCommander();
            commander = GetCommander();

            danger = new int[Width, Height];
            foreach (var tr in Opponents)
            {
                for (var i = 0; i < Width; i++)
                {
                    for (var j = 0; j < Height; j++)
                    {
                        if (world.IsVisible(GetShootingRange(tr, tr.Stance), tr.X, tr.Y, tr.Stance, i, j, self.Stance))
                            danger[i, j]++;
                        if (world.IsVisible(GetVisionRange(tr, self, self.Stance), tr.X, tr.Y, tr.Stance, i, j, self.Stance))
                            danger[i, j]++;
                    }
                }
            }

            if (queue.Count == 0 || (long)queue[queue.Count - 1] != self.Id)
                queue.Add(self.Id);

            if (MapHash == -1)
            {
                MapHash = GetMapHash();
#if DEBUG
                Console.WriteLine(MapHash);
#endif
            }
        }

        bool IsVisible(int x, int y, TrooperStance stance = TrooperStance.Prone)
        {
            return Team.Any(
                trooper => world.IsVisible(trooper.VisionRange, trooper.X, trooper.Y, trooper.Stance, x, y, stance)
            );
        }

        static int[] _i = { 0, 0, 1, -1 };
        static int[] _j = { 1, -1, 0, 0 };

        static int[] _i_ = { 0, 0, 0, 1, -1 };
        static int[] _j_ = { 0, 1, -1, 0, 0 };

        ArrayList Nearest(Unit unit, int[,] map)
        {
            return Nearest(unit.X, unit.Y, map);
        }

        ArrayList Nearest(Point unit, int[,] map)
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

        bool IsCanMove()
        {
            return self.ActionPoints >= GetMoveCost();
        }

        bool IsCanLower()
        {
            return self.Stance != TrooperStance.Prone && self.ActionPoints >= game.StanceChangeCost;
        }

        bool IsCanUpper()
        {
            return IsCanUpper(this.self);
        }

        bool IsCanUpper(TrooperStance stance, int actionPoints)
        {
            return stance != TrooperStance.Standing && actionPoints >= game.StanceChangeCost;
        }

        bool IsCanUpper(Trooper self)
        {
            return IsCanUpper(self.Stance, self.ActionPoints);
        }

        int GetStanceId(TrooperStance stance)
        {
            if (stance == TrooperStance.Prone)
                return 0;
            if (stance == TrooperStance.Kneeling)
                return 1;
            if (stance == TrooperStance.Standing)
                return 2;
            throw new InvalidDataException();
        }

        TrooperStance GetStance(int stance)
        {
            if (stance == 0)
                return TrooperStance.Prone;
            if (stance == 1)
                return TrooperStance.Kneeling;
            if (stance == 2)
                return TrooperStance.Standing;
            throw new InvalidDataException();
        }

        // Возврящает ShootingRange без учета бонуса
        double GetInitialShootingRange(Trooper trooper)
        {
            if (trooper.Type != TrooperType.Sniper)
                return trooper.ShootingRange;
            if (trooper.Stance == TrooperStance.Prone)
                return trooper.ShootingRange - game.SniperProneShootingRangeBonus;
            if (trooper.Stance == TrooperStance.Kneeling)
                return trooper.ShootingRange - game.SniperKneelingShootingRangeBonus;
            if (trooper.Stance == TrooperStance.Standing)
                return trooper.ShootingRange - game.SniperStandingShootingRangeBonus;
            throw new InvalidDataException();
        }

        // Возврящает ShootingRange с учетом бонуса
        double GetShootingRange(Trooper trooper, TrooperStance stance)
        {
            return GetShootingRange(trooper, GetStanceId(stance));
        }

        // Возврящает ShootingRange с учетом бонуса
        double GetShootingRange(Trooper trooper, int stance)
        {
            var range = GetInitialShootingRange(trooper);
            if (trooper.Type != TrooperType.Sniper)
                return range;
            if (stance == 0)
                return range + game.SniperProneShootingRangeBonus;
            if (stance == 1)
                return range + game.SniperKneelingShootingRangeBonus;
            if (stance == 2)
                return range + game.SniperStandingShootingRangeBonus;
            throw new InvalidDataException();
        }

        public double GetVisionRange(Trooper viewer, Trooper objectTr, TrooperStance objectStance)
        {
            return GetVisionRange(viewer, objectTr, GetStanceId(objectStance));
        }

        public double GetVisionRange(Trooper viewer, Trooper objectTr, int objectStance)
        {
            if (viewer.Type != TrooperType.Scout && objectTr.Type == TrooperType.Sniper)
            {
                if (objectStance == 2)
                    return viewer.VisionRange - game.SniperStandingStealthBonus;
                if (objectStance == 1)
                    return viewer.VisionRange - game.SniperKneelingStealthBonus;
                if (objectStance == 0)
                    return viewer.VisionRange - game.SniperProneStealthBonus;
                throw new InvalidDataException();
            }
            return viewer.VisionRange;
        }

        long GetCurrentLeaderId()
        {
            if (BonusGoal != null)
                return MyStrategy.WhoseBonus;
            return commander.Id;
        }

        Trooper GetCurrentLeader()
        {
            if (BonusGoal != null)
                return GetTrooper(MyStrategy.WhoseBonus);
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
        static bool Equal(Unit unit, Move move)
        {
            return unit.X == move.X && unit.Y == move.Y;
        }
        static bool Equal(Point point, Move move)
        {
            return point.X == move.X && point.Y == move.Y;
        }
        static bool Equal(Unit point, Unit unit)
        {
            return point.X == unit.X && point.Y == unit.Y;
        }
        static bool EqualF(double a, double b)
        {
            return Math.Abs(a - b) < Eps;
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

        Trooper GetClone(Trooper a, int minusHitpoints)
        {
            return new Trooper(a.Id, a.X, a.Y, a.PlayerId, a.TeammateIndex, a.IsTeammate, a.Type, a.Stance, a.Hitpoints - minusHitpoints, a.MaximalHitpoints, a.ActionPoints, a.InitialActionPoints, a.VisionRange, a.ShootingRange, a.ShootCost, a.StandingDamage, a.KneelingDamage, a.ProneDamage, a.Damage, a.IsHoldingGrenade, a.IsHoldingMedikit, a.IsHoldingFieldRation);
        }

        int GetCellTypeId(CellType type)
        {
            switch (type)
            {
                case CellType.Free:
                    return 0;
                case CellType.LowCover:
                    return 1;
                case CellType.MediumCover:
                    return 2;
                case CellType.HighCover:
                    return 3;
                default:
                    throw new ArgumentException();
            }
        }

        long GetMapHash()
        {
            long hash = 0;
            unchecked
            {
                for(var i = 0; i < Width; i++)
                    for (var j = 0; j < Height; j++)
                        hash = hash*31 + GetCellTypeId(Cells[i][j]);
            }
            return hash;
        }
    }
}
