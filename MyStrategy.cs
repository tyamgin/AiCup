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
            ProcessApproximation();
            if (BonusGoal != null && getTrooper(MyStrategy.whoseBonus) == null)
                BonusGoal = null;
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
                if (canLower() && canShootSomeone(new Point(self.X, self.Y), Low(self.Stance)) && self.Type != TrooperType.FieldMedic &&
                    (self.ActionPoints - game.StanceChangeCost) / self.ShootCost >= self.ActionPoints / self.ShootCost)
                {
                    Go(ActionType.LowerStance);
                    return;
                }
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
                        Point to = goToUnit(self, ifHelp, map, beginFree:true, endFree:true);
                        if (to != null)
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
                    Point to = goToUnit(self, grouping, map, beginFree: true, endFree: false);
                    if (to != null)
                    {
                        Go(ActionType.Move, to);
                        return;
                    }
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
                            Point to = goToUnit(self, ifGoAtack, map, beginFree: true, endFree: true);
                            if (getTeamRadius(self.Id, to) <= MaxTeamRadius)
                            {
                                Go(move.Action = ActionType.Move, to);
                                return;
                            }
                        }
                        else
                        {
                            // TODO!!!: мб ложиться??????
                        }
                    }
                }
                else
                {
                    Point goToEncircling = GoToEncircling(getTrooperAt(mostDanger), true);
                    if (goToEncircling != null && canMove())
                    {
                        Point to = goToUnit(self, goToEncircling, map, beginFree: true, endFree: false);
                        if (to == null || Equal(self, to)) // TODO: если to = null, то значит мы застряли, или не хватило очков на стрельбу
                        {
                            // можно сесть
                            if (canLower() && self.Type != TrooperType.FieldMedic)
                                Go(ActionType.LowerStance);
                            else
                                Go(ActionType.EndTurn);
                            return;    
                        }
                        if (getTeamRadius(self.Id, to) <= MaxTeamRadius)
                        {
                            Go(ActionType.Move, to);
                            return;
                        }
                    }
                }
            }

            Trooper whoseBonus = null;
            Point ifTeamBonus = IfTeamBonus(ref whoseBonus);
            if (ifTeamBonus != null && BonusGoal == null && map[ifTeamBonus.X, ifTeamBonus.Y] == 0 && !Equal(ifTeamBonus, self))
            {
                BonusGoal = ifTeamBonus;
                MyStrategy.whoseBonus = whoseBonus.Id;
            }
            
            bool waitingHelp = IfNeedHelp() && self.Type != TrooperType.FieldMedic && getBestHelper() != null;
            bool allowNothing = true;
            if (!waitingHelp && canMove() && BonusGoal != null && MyStrategy.whoseBonus == self.Id
                //&& getShoterPath(self, BonusGoal, map, beginFree: true, endFree: false) <= 6
                )
            {
                allowNothing = false;
                Point to = goToUnit(self, BonusGoal, map, beginFree: true, endFree: false);
                // Если путь до бонуса пока что занят, то все равно идти к нему
                if (to == null)
                {
                    to = goToUnit(self, BonusGoal, map, beginFree: true, endFree: true);
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

            //if (IWin())
            //{
            //    // TODO: что делать с медиком?
            //    if (canLower())
            //        Go(ActionType.LowerStance);
            //    else
            //        Go(ActionType.EndTurn);
            //    return;
            //}

            // Пытаюсь освободить дорогу до бонуса
            if (canMove() && BonusGoal != null && MyStrategy.whoseBonus != self.Id
                //&& getShoterPath(getTrooper(MyStrategy.whoseBonus), BonusGoal, notFilledMap, beginFree: true, endFree: true) < 6
                )
            {
                Point bestTurn = new Point(self.X, self.Y, getShoterPath(getTrooper(MyStrategy.whoseBonus), BonusGoal, map, beginFree: true, endFree: false));
                foreach (Point p in Nearest(self, map))
                {
                    map[self.X, self.Y] = 0;
                    map[p.X, p.Y] = 1;
                    int path = getShoterPath(getTrooper(MyStrategy.whoseBonus), BonusGoal, map, beginFree: true, endFree: false);
                    if (path < bestTurn.profit)
                        bestTurn = new Point(p.X, p.Y, path);
                    map[self.X, self.Y] = 1;
                    map[p.X, p.Y] = 0;
                }
                if (bestTurn.profit < Inf)
                {
                    if (Equal(bestTurn, self))
                    {   
                        // Либо двигаться в окружение, чтобы только не загородить(увеличить кратчайший путь до бонуса)
                        bestTurn = new Point(0, 0, Inf);
                        foreach (Point p in getEncirclingPoints(getTrooper(MyStrategy.whoseBonus), false, 1))
                        {
                            int pathBefore = getShoterPath(getTrooper(MyStrategy.whoseBonus), BonusGoal, map, beginFree: true, endFree: false);
                            int path = getShoterPath(self, p, map, beginFree: true, endFree: false);
                            Point to = goToUnit(self, p, map, beginFree: true, endFree: false);
                            if (to != null)
                            {
                                map[self.X, self.Y] = 0;    
                                map[to.X, to.Y] = 1;
                                int pathAfter = getShoterPath(getTrooper(MyStrategy.whoseBonus), BonusGoal, map, beginFree: true, endFree: false);
                                if (pathBefore >= pathAfter && path < bestTurn.profit)
                                    bestTurn = new Point(to.X, to.Y, path);
                                map[self.X, self.Y] = 1;
                                map[to.X, to.Y] = 0;
                            }
                        }
                        if (bestTurn.profit >= Inf || Equal(bestTurn, self))
                            Go(ActionType.EndTurn);
                        else
                            Go(ActionType.Move, bestTurn);
                    }
                    else
                    {
                        Go(ActionType.Move, bestTurn);
                    }
                    return;
                }
                else
                {
                    self = self;
                }
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
                        // TODO: стремиться к квадрату
                        Go(ActionType.EndTurn);
                        return;
                    }
                }
                else if (!waitingHelp && getTeamRadius(self.Id, to) <= MaxTeamRadius)
                {
                    Go(ActionType.Move, to);
                    return;
                }
            }
        }
    }
}