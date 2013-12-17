using System;
using System.Collections;
using System.Linq;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public void Move(Trooper self, World world, Game game, Move move)
        {
            this.self = self;
            this.world = world;
            this.game = game;
            this.move = move;
            InitializeVariables();
            ProcessApproximation();
            if (world.MoveIndex == 37 && self.Type == TrooperType.Commander)
                world = world;
            var allowHill = !CheckShootMe();
            if (BonusGoal != null && GetTrooper(MyStrategy.WhoseBonus) == null)
                BonusGoal = null;
            if (BonusGoal != null && IsHaveBonus(GetTrooper(MyStrategy.WhoseBonus), GetBonusAt(BonusGoal)))
                BonusGoal = null;

            // Карта где медик и снайпер отдельно (map03)
            // Координаты где собираться:
            // 18 13
            // 11 6
            if (MapHash == Lab2Map
                && world.MoveIndex <= 2
                && (self.Type == TrooperType.FieldMedic || self.Type == TrooperType.Sniper)
                && Opponents.Count() == 0
               )
            {
                var rightLower = new Point(18, 14);
                var leftUpper = new Point(11, 5);
                var goal = rightLower.GetDistanceTo(self) < leftUpper.GetDistanceTo(self) ? rightLower : leftUpper;
                var to = GoScouting(goal, goal);
                if (to != null)
                {
                    Go(ActionType.Move, to);
                    return;
                }
                
            }

            if (IfFieldRationNeed())
            {
                Go(ActionType.EatFieldRation);
                return;
            }
            Reached(new Point(self));

            if (Opponents.Count() != 0)
            {
                AllowTakeBonus = false;
                // Чтобы знали куда бежать если противник отступит
                PointGoal = new Point(Opponents[0]);
                PointGoal.profit = world.MoveIndex;

                var action = BruteForceDo();
                if (action != null)
                {
                    if (Equal(self, action) && action.Action == ActionType.Move && self.ActionPoints < GetMoveCost())
                    {
                        Go(ActionType.EndTurn);
                    }
                    else
                    {
                        Go(action.Action, new Point(action.X, action.Y));
                    }
                    return;
                }
            }

            if (self.Type == TrooperType.FieldMedic)
            {
                var ifHelp = IfHelpTeammate();
                if (ifHelp != null)
                {
                    var goal = GetTrooperAt(ifHelp.X, ifHelp.Y);
                    if (goal != null && goal.Hitpoints < goal.MaximalHitpoints && ifHelp.Nearest(self) && game.FieldMedicHealCost <= self.ActionPoints)
                    {
                        Go(ActionType.Heal, ifHelp);
                        return;
                    }
                    if (IsCanMove())
                    {
                        var to = GoToUnit(self, ifHelp, map, beginFree: true, endFree: true);
                        if (to != null)
                        {
                            Go(ActionType.Move, to);
                            return;
                        }
                    }
                }
            }

            var ifUseMedikit = IfUseMedikit();
            if (ifUseMedikit != null)
            {
                Go(ActionType.UseMedikit, ifUseMedikit);
                return;
            }

            if (allowHill && IfRequestEnemyDisposition())
            {
                Go(ActionType.RequestEnemyDisposition);
                return;
            }

            // Группировка
            if ((GetTeamRadius() > MaxTeamRadius && self.Id == commander.Id || 
                GetTeamRadius() > MaxTeamRadius/2 && self.Id != commander.Id) 
                && self.ActionPoints >= GetMoveCost())
            {
                var bestTurn = new Point(0, 0, Inf);
                for (var i = 0; i < Width; i++)
                {
                    for (var j = 0; j < Height; j++)
                    {
                        var r = Math.Max(MaxTeamRadius, GetTeamRadius(self.Id, new Point(i, j)));
                        if (r < bestTurn.profit && r < GetTeamRadius())
                        {
                            bestTurn.Set(i, j, r);
                        }
                    }
                }
                if (bestTurn.profit < Inf)
                {
                    var to = GoScouting(bestTurn, PointGoal ?? (BonusGoal ?? bestTurn));
                    if (to != null)
                    {
                        Go(ActionType.Move, to);
                        return;
                    }
                }
            }
            
            Trooper whoseBonus = null;
            var ifTeamBonus = IfTeamBonus(ref whoseBonus, AllowTakeBonus);
            if (ifTeamBonus != null && BonusGoal == null && map[ifTeamBonus.X, ifTeamBonus.Y] == 0 && !Equal(ifTeamBonus, self))
            {
                BonusGoal = ifTeamBonus;
                MyStrategy.WhoseBonus = whoseBonus.Id;
            }

            var waitingHelp = false; //allowHill && IfNeedHelp() && self.Type != TrooperType.FieldMedic && GetBestHelper() != null;
            var allowNothing = true;

            if (!waitingHelp && IsCanMove() && BonusGoal != null && MyStrategy.WhoseBonus == self.Id)
            {
                if (IsCanUpper())
                {
                    Go(ActionType.RaiseStance);
                    return;
                }
                allowNothing = false;
                var to = GoScouting(BonusGoal, PointGoal ?? BonusGoal); //GoToUnit(self, BonusGoal, map, beginFree: true, endFree: false);
                // Если путь до бонуса пока что занят, то все равно идти к нему
                if (to == null)
                {
                    to = GoToUnit(self, BonusGoal, notFilledMap, beginFree: true, endFree: true);
                    if (to != null && map[to.X, to.Y] == 0 && self.ActionPoints >= 2 * GetMoveCost(self)) // TODO: ???
                    {
                        Go(ActionType.Move, to);
                        return;
                    }
                }
                else
                {
                    if (GetTeamRadius(self.Id, to) > MaxTeamRadius && GetTeamRadius() > GetTeamRadius(self.Id, to))
                        to = GoScouting(new Point(self), PointGoal ?? BonusGoal);
                    Go(ActionType.Move, to);
                    return;
                }
            }

            // Пытаюсь освободить дорогу до бонуса
            if (IsCanMove() && BonusGoal != null && MyStrategy.WhoseBonus != self.Id)
            {
                if (IsCanUpper())
                {
                    Go(ActionType.RaiseStance);
                    return;
                }
                var bestTurn = SkipPath(GetTrooper(MyStrategy.WhoseBonus), PointGoal ?? BonusGoal);
                var to = bestTurn == null ? null : GoScouting(bestTurn, PointGoal ?? (BonusGoal ?? new Point(commander)));//GoToUnit(self, bestTurn, map, beginFree: true, endFree: false);
                if (to == null || Equal(to, self) && self.ActionPoints < GetMoveCost()) // если Equal(to, self)) тоже делаем move, иначе он не дойдет обратно
                    Go(ActionType.EndTurn);
                else
                    Go(ActionType.Move, to);
                return;
            }

            var ifNothing = IfNothing();
            if (allowNothing && ifNothing != null && IsCanMove())
            {
                if (IsCanUpper())
                {
                    Go(ActionType.RaiseStance);
                    return;
                }

                Point to;
                if (self.Id == commander.Id)
                {
                    to = GoToUnit(self, ifNothing, map, beginFree: true, endFree: false);
                    if (GetTeamRadius(self.Id, to) > MaxTeamRadius)
                        to = GoScouting(new Point(self), ifNothing);
                }
                else
                {
                    to = GoScouting(ifNothing, ifNothing);
                }
                if (to == null || Equal(self, to) && self.ActionPoints < GetMoveCost())
                {
                    if (to == null && changedCommander == -1) 
                    {
                        // значит мы застряли
                        // передать коммандование
                        ChangeCommander();
                    }
                    else
                    {
                        Go(ActionType.EndTurn);
                        return;
                    }
                }
                else if (!waitingHelp)
                {
                    Go(ActionType.Move, to);
                    return;
                }
            }

            Point go = GoScouting(new Point(self), IfNothingCommander() ?? new Point(self)); // подумать что делать
            if (Equal(self, go) && self.ActionPoints < GetMoveCost())
                Go(ActionType.EndTurn);
            else
                Go(ActionType.Move, go);
        }
    }
}