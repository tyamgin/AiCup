using System;
using System.Collections;
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
            InitializeConstants();
            ProcessApproximation();
            //if (world.MoveIndex == 15 && self.Type == TrooperType.FieldMedic)
            //    world = world;
            bool allowHill = !CheckShootMe();
            if (BonusGoal != null && getTrooper(MyStrategy.whoseBonus) == null)
                BonusGoal = null;
            if (BonusGoal != null && haveSuchBonus(getTrooper(MyStrategy.whoseBonus), getBonusAt(BonusGoal)))
                BonusGoal = null;
            if (ifFieldRationNeed())
            {
                Go(ActionType.EatFieldRation);
                return;
            }
            Reached(new Point(self));

            if (opponents.Count != 0)
            {
                var action = BruteForceDo();
                if (action != null)
                {
                    Go(action.Action, new Point(action.X, action.Y));
                    return;
                }
            }

            if (self.Type == TrooperType.FieldMedic)
            {
                Point ifHelp = ifHelpTeammate();
                if (ifHelp != null)
                {
                    Trooper goal = getTrooperAt(ifHelp.X, ifHelp.Y);
                    if (goal != null && goal.Hitpoints < goal.MaximalHitpoints && ifHelp.Nearest(self) && game.FieldMedicHealCost <= self.ActionPoints)
                    {
                        Go(ActionType.Heal, ifHelp);
                        return;
                    }
                    if (canMove())
                    {
                        Point to = goToUnit(self, ifHelp, map, beginFree: true, endFree: true);
                        if (to != null)
                        {
                            Go(ActionType.Move, to);
                            return;
                        }
                    }
                }
            }

            Point ifUseMedikit = IfUseMedikit();
            if (ifUseMedikit != null)
            {
                Go(ActionType.UseMedikit, ifUseMedikit);
                return;
            }

            Trooper whoseBonus = null;
            Point ifTeamBonus = IfTeamBonus(ref whoseBonus);
            if (ifTeamBonus != null && BonusGoal == null && map[ifTeamBonus.X, ifTeamBonus.Y] == 0 && !Equal(ifTeamBonus, self))
            {
                BonusGoal = ifTeamBonus;
                MyStrategy.whoseBonus = whoseBonus.Id;
            }
            
            bool waitingHelp = allowHill && IfNeedHelp() && self.Type != TrooperType.FieldMedic && getBestHelper() != null;
            waitingHelp = false;
            bool allowNothing = true;
            if (!waitingHelp && canMove() && BonusGoal != null && MyStrategy.whoseBonus == self.Id)
            {
                if (canUpper())
                {
                    Go(ActionType.RaiseStance);
                    return;
                }
                allowNothing = false;
                Point to = goToUnit(self, BonusGoal, map, beginFree: true, endFree: false);
                // Если путь до бонуса пока что занят, то все равно идти к нему
                if (to == null)
                {
                    to = goToUnit(self, BonusGoal, notFilledMap, beginFree: true, endFree: true);
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
            if (canMove() && BonusGoal != null && MyStrategy.whoseBonus != self.Id)
            {
                if (canUpper())
                {
                    Go(ActionType.RaiseStance);
                    return;
                }
                Point bestTurn = SkipPath(getTrooper(MyStrategy.whoseBonus), BonusGoal, needShootingPosition: false);
                Point to = bestTurn == null ? null : goToUnit(self, bestTurn, map, beginFree: true, endFree: false);
                if (to == null || Equal(to, self))
                    Go(ActionType.EndTurn);
                else
                    Go(ActionType.Move, to);
                return;
            }

            Point ifNothing = IfNothing();
            if (allowNothing && ifNothing != null && canMove())
            {
                if (canUpper())
                {
                    Go(ActionType.RaiseStance);
                    return;
                }
                Point to = goToUnit(self, ifNothing, map, beginFree: true, endFree: false);
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
                else if (!waitingHelp && (self.Id != getCurrentLeaderId() || getTeamRadius(self.Id, to) <= MaxTeamRadius))
                {
                    Go(ActionType.Move, to);
                    return;
                }
            }

            Go(ActionType.EndTurn);
        }
    }
}