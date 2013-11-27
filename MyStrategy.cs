using System;
using System.Collections;
using System.Linq;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

// TODO: ѕам€ть, приоритеты

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
            if (world.MoveIndex == 11 && self.Type == TrooperType.Sniper)
                world = world;
            InitializeConstants();
            ProcessApproximation();
            bool allowHill = !CheckShootMe();
            if (BonusGoal != null && getTrooper(MyStrategy.WhoseBonus) == null)
                BonusGoal = null;
            if (BonusGoal != null && haveSuchBonus(getTrooper(MyStrategy.WhoseBonus), getBonusAt(BonusGoal)))
                BonusGoal = null;
            if (IfFieldRationNeed())
            {
                Go(ActionType.EatFieldRation);
                return;
            }
            Reached(new Point(self));

            if (Opponents.Count() != 0)
            {
                // „тобы знали куда бежать если противник отступит
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
                    var goal = getTrooperAt(ifHelp.X, ifHelp.Y);
                    if (goal != null && goal.Hitpoints < goal.MaximalHitpoints && ifHelp.Nearest(self) && game.FieldMedicHealCost <= self.ActionPoints)
                    {
                        Go(ActionType.Heal, ifHelp);
                        return;
                    }
                    if (canMove())
                    {
                        var to = goToUnit(self, ifHelp, map, beginFree: true, endFree: true);
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
            
            bool waitingHelp = allowHill && IfNeedHelp() && self.Type != TrooperType.FieldMedic && getBestHelper() != null;
            waitingHelp = false;
            bool allowNothing = true;
            if (!waitingHelp && canMove() && BonusGoal != null && MyStrategy.WhoseBonus == self.Id)
            {
                if (canUpper())
                {
                    Go(ActionType.RaiseStance);
                    return;
                }
                allowNothing = false;
                var to = goToUnit(self, BonusGoal, map, beginFree: true, endFree: false);
                // ≈сли путь до бонуса пока что зан€т, то все равно идти к нему
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

            // ѕытаюсь освободить дорогу до бонуса
            if (canMove() && BonusGoal != null && MyStrategy.WhoseBonus != self.Id)
            {
                if (canUpper())
                {
                    Go(ActionType.RaiseStance);
                    return;
                }
                var bestTurn = SkipPath(getTrooper(MyStrategy.WhoseBonus), BonusGoal, needShootingPosition: false);
                var to = bestTurn == null ? null : goToUnit(self, bestTurn, map, beginFree: true, endFree: false);
                if (to == null || Equal(to, self))
                    Go(ActionType.EndTurn);
                else
                    Go(ActionType.Move, to);
                return;
            }

            var ifNothing = IfNothing();
            if (allowNothing && ifNothing != null && canMove())
            {
                if (canUpper())
                {
                    Go(ActionType.RaiseStance);
                    return;
                }
                var to = goToUnit(self, ifNothing, map, beginFree: true, endFree: false);
                if (to == null || Equal(self, to))
                {
                    if (to == null && changedCommander == -1) // значит мы застр€ли
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