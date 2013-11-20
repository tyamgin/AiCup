using System;
using System.Collections;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

// TODO: Пересмотреть кидать-ли медику гранату если нужно лечить
// TODO:!!!!100% Когда shotting отодвигаться если кто-то не может стрелять

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
            //if (world.MoveIndex == 6 && self.Type == TrooperType.Commander)
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

            bool needMove = false;
            //Point ifThrowGrenade = IfThrowGrenade(ref needMove);
            //if (ifThrowGrenade != null)
            //{
            //    Go(needMove ? ActionType.Move : ActionType.ThrowGrenade, ifThrowGrenade);
            //    return;
            //}

            //Point ifUseMedikit = IfUseMedikit();
            //if (ifUseMedikit != null)
            //{
            //    Go(ActionType.UseMedikit, ifUseMedikit);
            //    return;
            //}

            Point ifShot = IfShot();

            //if (self.Type == TrooperType.FieldMedic && (friend.Count != 0 || ifShot == null))
            //{
            //    Point ifHelp = ifHelpTeammate();
            //    if (ifHelp != null)
            //    {
            //        Trooper goal = getTrooperAt(ifHelp.X, ifHelp.Y);
            //        if (goal != null && goal.Hitpoints < goal.MaximalHitpoints && ifHelp.Nearest(self) && game.FieldMedicHealCost <= self.ActionPoints)
            //        {
            //            Go(ActionType.Heal, ifHelp);
            //            return;
            //        }
            //        if (canMove())
            //        {
            //            Point to = goToUnit(self, ifHelp, map, beginFree: true, endFree: true);
            //            if (to != null)
            //            {
            //                Go(ActionType.Move, to);
            //                return;
            //            }
            //        }
            //    }
            //}

            if (opponents.Count != 0)
            {
                var action = BruteForceDo();
                if (action != null)
                {
                    Go(action.Action, new Point(action.X, action.Y));
                    return;
                }

                if (canLower())
                {
                    if (howManyCanShoot(new Point(self.X, self.Y), Low(self.Stance)) > 0 && self.Type != TrooperType.FieldMedic &&
                        (self.ActionPoints - game.StanceChangeCost + game.FieldRationBonusActionPoints - game.FieldRationEatCost) / self.ShootCost >= (self.ActionPoints + game.FieldRationBonusActionPoints - game.FieldRationEatCost) / self.ShootCost)
                    {
                        Go(ActionType.LowerStance);
                        return;
                    }
                    if (howManyCanShoot(new Point(self.X, self.Y), Low(self.Stance)) > 1 && self.Type == TrooperType.Soldier)
                    {
                        Go(ActionType.LowerStance);
                        return;
                    }
                }
                Go(ActionType.Shoot, ifShot);
                return;
            }

            // Если нужно идти атаковать, то тот кто находится на самой опасной зоне выполняет IfGoAtack,
            // остальные приближаются к EncirclingPoints того кто в опастности
            Point mostDanger = getMostDanger();
            bool busy = howManyCanShoot(new Point(self), self.Stance) != 0;
            // TODO: getMostDanger <- добавить если я вижу
            if (mostDanger != null && !busy)
            {
                if (Equal(mostDanger, self))
                {
                    Point ifGoAtack = IfGoAtack();
                    if (ifGoAtack != null && canMove())
                    {
                        busy = true;
                        if (mustAtack())
                        {
                            Point to = goToUnit(self, ifGoAtack, map, beginFree: true, endFree: true);
                            if (getTeamRadius(self.Id, to) <= MaxTeamRadius)
                            {
                                Go(move.Action = ActionType.Move, to);
                                return;
                            }
                        }
                        else if (self.Type != TrooperType.FieldMedic && canLower())
                        {
                            Go(ActionType.LowerStance);
                            return;
                        }
                    }
                }
                else
                {
                    Point goToEncircling = GoToEncircling(getTrooperAt(mostDanger), null, self.Type != TrooperType.FieldMedic);
                    if (goToEncircling != null && canMove())
                    {
                        Point to = goToUnit(self, goToEncircling, map, beginFree: true, endFree: false);
                        if (to == null || Equal(self, to))
                        {
                            if (self.Type != TrooperType.FieldMedic)
                            {
                                int mySt = getStanceId(self.Stance);
                                for(int st = 0; st < 3; st++)
                                {
                                    if (howManyCanShoot(new Point(self), getStance(st)) != 0)
                                    {
                                        if (st < mySt && canLower())
                                            Go(ActionType.LowerStance);
                                        else if (st > mySt && canUpper())
                                            Go(ActionType.RaiseStance);
                                        else
                                            Go(ActionType.EndTurn);
                                        return;
                                    }
                                }
                            }
                            // Значит либо я медик, либо кто-то должен подвинуться
                            Go(ActionType.EndTurn);
                            return;
                        }
                        Go(ActionType.Move, to);
                        return;
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
            
            bool waitingHelp = allowHill && IfNeedHelp() && self.Type != TrooperType.FieldMedic && getBestHelper() != null;
            waitingHelp = false;
            bool allowNothing = true;
            if (!busy && !waitingHelp && canMove() && BonusGoal != null && MyStrategy.whoseBonus == self.Id)
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
            if (!busy && canMove() && BonusGoal != null && MyStrategy.whoseBonus != self.Id
                //&& getShoterPath(getTrooper(MyStrategy.whoseBonus), BonusGoal, notFilledMap, beginFree: true, endFree: true) < 6
                )
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
            if (!busy && allowNothing && ifNothing != null && canMove())
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
        }
    }
}