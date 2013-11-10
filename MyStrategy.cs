using System;
using System.Collections;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

// TODO!!!: вместо того чтобы явно стрелять и убивать - шел группироваться
// TODO!!!: пытался пройти к медику через трупера, но не мог

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

            if (ifFieldRationNeed())
            {
                Go(ActionType.EatFieldRation);
                return;
            }
            Reached(new Point(self));

            bool needMove = false;
            Point ifThrowGrenade = IfThrowGrenade(ref needMove);
            if (ifThrowGrenade != null)
            {
                Go(needMove ? ActionType.Move : ActionType.ThrowGrenade, ifThrowGrenade);
                return;
            }

            Point ifUseMedikit = IfUseMedikit();
            if (ifUseMedikit != null)
            {
                Go(ActionType.UseMedikit, ifUseMedikit);
                return;
            }

            // может возникнуть такое, что очков на выстрел нету, и он попытается пойти на клетку врага
            Point ifShot = IfShot();
            if (ifShot != null)
            {
                Go(ActionType.Shoot, ifShot);
                return;
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
                        Point to = goToUnit(self, ifHelp, map, true);
                        if (map[to.X, to.Y] == 0)
                        {
                            Go(ActionType.Move, to);
                            return;
                        }
                    }
                }
            }

            // Если радиус большой, то нужно сгруппироваться
            if (canMove() && self.Id != commander.Id && getTeamRadius() > MaxTeamRadius)
            {
                Point grouping = ifGrouping();
                // grouping != null !!!
                if (!Equal(grouping, self))
                {
                    Point to = goToUnit(self, grouping, map, false);
                    if (to != null)
                    {
                        Go(ActionType.Move, to);
                        return;
                    }
                }
            }

            if (IfNeedHelp() && self.Type != TrooperType.FieldMedic)
            {
                Point helper = getBestHelper();
                if (helper != null)
                {
                    if (helper.Nearest(self) || !canMove())
                    {
                        Go(ActionType.EndTurn);
                        return;
                    }
                    Point to = goToUnit(self, helper, map, true);
                    Go(move.Action = ActionType.Move, to);
                    return;
                }
            }

            // Если нужно идти атаковать, то тот кто находится на самой опасной зоне выполняет IfGoAtack,
            // остальные приближаются к EncirclingPoints того кто в опастности
            // TODO: нужно чтобы они стремились к квадрату
            Point mostDanger = getMostDanger();
            if (mostDanger != null)
            {
                if (Equal(mostDanger, self))
                {
                    Point ifGoAtack = IfGoAtack();
                    if (ifGoAtack != null && canMove())
                    {
                        if (mustAtack())
                        {
                            Point to = goToUnit(self, ifGoAtack, map, true);
                            if (getTeamRadius(self.Id, to) <= MaxTeamRadius)
                            {
                                Go(move.Action = ActionType.Move, to);
                                return;
                            }
                        }
                    }
                }
                else
                {
                    Point goToEncircling = GoToEncircling(getTrooperAt(mostDanger), true);
                    if (goToEncircling != null && canMove())
                    {
                        Point to = goToUnit(self, goToEncircling, map, false);
                        if (to == null || Equal(self, to)) // TODO: если to = null, то значит мы застряли, или не хватило очков на стрельбу
                        {
                            // можно сесть
                            Go(ActionType.EndTurn);
                            return;    
                        }
                        if (getTeamRadius(self.Id, to) <= MaxTeamRadius) // ??
                        {
                            Go(ActionType.Move, to);
                            return;
                        }
                    }
                }
            }

            Point ifTeamBonus = IfTeamBonus();
            if (ifTeamBonus != null && (BonusGoal == null || !isBonusExistAt(BonusGoal)) && 
                map[ifTeamBonus.X, ifTeamBonus.Y] == 0 && !Equal(ifTeamBonus, self))
            {
                Goal = ifTeamBonus;
            }

            Point ifTakeBonus = IfTakeBonus();
            if (ifTakeBonus != null && canMove() && (Goal == null || Equal(Goal, ifTakeBonus)))
            {
                Point to = goToUnit(self, ifTakeBonus, map, false);
                if (getTeamRadius(self.Id, ifTakeBonus) <= MaxTeamRadius && 
                    getTeamRadius(self.Id, to) <= MaxTeamRadius && 
                    getShoterPath(self, ifTakeBonus, map, false) <= 6)
                {
                    Go(move.Action = ActionType.Move, to);
                    return;
                }
            }

            Point ifNothing = IfNothing();
            if (ifNothing != null && canMove())
            {
                Point to = goToUnit(self, ifNothing, map, false);
                if (to == null || Equal(self, to))
                {
                    if (to == null && changedCommander == -1) // значит мы застряли
                    {
                        // передать коммандование
                        ChangeCommander();
                    }
                    // Тут буду ложиться/садиться
                    Go(ActionType.EndTurn);
                    return;
                }
                else if (getTeamRadius(self.Id, to) <= MaxTeamRadius)
                {
                    Go(ActionType.Move, to);
                    return;
                }
            }
            // Тут буду ложиться/садиться
            validateMove();
        }
    }
}