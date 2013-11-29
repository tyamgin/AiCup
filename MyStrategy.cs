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
            //if (world.MoveIndex == 2 && self.Type == TrooperType.Commander)
            //    world = world;
            InitializeConstants();
            ProcessApproximation();
            bool allowHill = !CheckShootMe();
            if (BonusGoal != null && GetTrooper(MyStrategy.WhoseBonus) == null)
                BonusGoal = null;
            if (BonusGoal != null && IsHaveBonus(GetTrooper(MyStrategy.WhoseBonus), GetBonusAt(BonusGoal)))
                BonusGoal = null;
            if (IfFieldRationNeed())
            {
                Go(ActionType.EatFieldRation);
                return;
            }
            Reached(new Point(self));

            if (Opponents.Count() != 0)
            {
                // Чтобы знали куда бежать если противник отступит
                PointGoal = new Point(Opponents[0]);

                var action = BruteForceDo();
                if (action != null)
                {
                    Go(action.Action, new Point(action.X, action.Y));
                    return;
                }
            }

            if (self.Type == TrooperType.FieldMedic)
            {
                var ifHelp = ifHelpTeammate();
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

            Trooper whoseBonus = null;
            var ifTeamBonus = IfTeamBonus(ref whoseBonus);
            if (ifTeamBonus != null && BonusGoal == null && map[ifTeamBonus.X, ifTeamBonus.Y] == 0 && !Equal(ifTeamBonus, self))
            {
                BonusGoal = ifTeamBonus;
                MyStrategy.WhoseBonus = whoseBonus.Id;
            }
            
            bool waitingHelp = allowHill && IfNeedHelp() && self.Type != TrooperType.FieldMedic && GetBestHelper() != null;
            waitingHelp = false;
            bool allowNothing = true;
            if (!waitingHelp && IsCanMove() && BonusGoal != null && MyStrategy.WhoseBonus == self.Id)
            {
                if (IsCanUpper())
                {
                    Go(ActionType.RaiseStance);
                    return;
                }
                allowNothing = false;
                var to = GoToUnit(self, BonusGoal, map, beginFree: true, endFree: false);
                // Если путь до бонуса пока что занят, то все равно идти к нему
                if (to == null)
                {
                    to = GoToUnit(self, BonusGoal, notFilledMap, beginFree: true, endFree: true);
                    if (to != null && map[to.X, to.Y] == 0 && getTeamRadius(self.Id, to) <= MaxTeamRadius)
                    {
                        Go(ActionType.Move, to);
                        return;
                    }
                }
                if (getTeamRadius(self.Id, to) <= MaxTeamRadius)
                {
                    Go(ActionType.Move, to);
                    return;
                }
            }

            if (IfMakeQuery())
            {
                Go(ActionType.RequestEnemyDisposition);
                return;
            }

            // Пытаюсь освободить дорогу до бонуса
            if (IsCanMove() && BonusGoal != null && MyStrategy.WhoseBonus != self.Id)
            {
                if (IsCanUpper())
                {
                    Go(ActionType.RaiseStance);
                    return;
                }
                var bestTurn = SkipPath(GetTrooper(MyStrategy.WhoseBonus), BonusGoal, needShootingPosition: false);
                var to = bestTurn == null ? null : GoToUnit(self, bestTurn, map, beginFree: true, endFree: false);
                if (to == null || Equal(to, self))
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
                var to = GoToUnit(self, ifNothing, map, beginFree: true, endFree: false);
                if (to == null || Equal(self, to))
                {
                    if (to == null && changedCommander == -1) // значит мы застряли
                    {
                        // передать коммандование
                        // невалидные бонусы
                        ChangeCommander();
                    }
                    else
                    {
                        Go(ActionType.EndTurn);
                        return;
                    }
                }
                else if (!waitingHelp && (self.Id != GetCurrentLeaderId() || getTeamRadius(self.Id, to) <= MaxTeamRadius))
                {
                    Go(ActionType.Move, to);
                    return;
                }
            }

            Go(ActionType.EndTurn);
        }
    }
}