using System;
using System.Collections;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

// TODO!!!: вместо того чтобы €вно стрел€ть и убивать - шел группироватьс€
// TODO!!!: пыталс€ пройти к медику через трупера, но не мог

// TODO: если € нахожусь далеко от тимлида (по радиусу), то нужно идти к его компоненте св€зности.
// труперы смежны, если рассто€ние между ними (кратчайший путь) <= 2 

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        bool isLastInTeam(Trooper tr)
        {
            // TODO: implement this
            return false;
        }

        double getShotProfit(Trooper goal)
        {
            if (goal.IsTeammate)
                return -Inf;
            // TODO: проверка на последнего игрока в команде
            double profit = 1.0 / goal.Hitpoints;
            if (isLastInTeam(goal))
                return profit * 1.5;
            return profit;
        }

        Point IfShot()
        {
            if (self.ActionPoints < self.ShootCost)
                return null;
            Point bestGoal = new Point(0, 0, -Inf);
            foreach(Trooper tr in troopers)
            {
                if (world.IsVisible(self.ShootingRange, self.X, self.Y, self.Stance, tr.X, tr.Y, tr.Stance))
                {
                    double profit = getShotProfit(tr);
                    if (profit > bestGoal.profit)
                        bestGoal = new Point(tr.X, tr.Y, profit);
                }
            }
            if (bestGoal.profit <= 0)
                bestGoal = null;
            return bestGoal;
        }

        // Ќаправл€тьс€ к этому юниту.
        Point goToUnit(Unit bo)
        {
            int distance = 0;
            return goToUnit(new Point(bo.X, bo.Y), ref distance, true);
        }

        int getShoterPath(Unit tr, bool filledMap, Trooper self = null)
        {
            if (self == null)
                self = this.self;
            int distance = 0;
            goToUnit(new Point(tr.X, tr.Y), ref distance, filledMap, self);
            return distance;
        }

        int getShoterPath(Point tr, bool filledMap = true)
        {
            int distance = 0;
            goToUnit(new Point(tr.X, tr.Y), ref distance, filledMap);
            return distance;
        }

        Point goToUnit(Point bo)
        {
            int distance = 0;
            return goToUnit(bo, ref distance, true);
        }

        Point goToUnit(Point bo, ref int distance, bool filledMap, Trooper self = null)
        {
            if (self == null)
                self = this.self;
            int[,] map;
            if (filledMap)
                map = this.map;
            else
                map = this.notFilledMap;
            
            if (bo.X == self.X && bo.Y == self.Y) // застр€вает в угол ??
                return bo;
            Queue q = new Queue();
            q.Enqueue(bo.X);
            q.Enqueue(bo.Y);
            int[,] d = new int[width, height];
            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++)
                    d[i, j] = Inf;
            d[bo.X, bo.Y] = 0;
            // если self != this.self, то map[self.X, selfY] = 1
            if (self.Id != this.self.Id)
                map[self.X, self.Y] = 0;
            
            while (q.Count != 0)
            {
                int x = (int)q.Dequeue();
                int y = (int)q.Dequeue();
                foreach(Point n in Nearest(x, y))
                {
                    if (d[n.X, n.Y] == Inf)
                    {
                        d[n.X, n.Y] = d[x, y] + 1;
                        q.Enqueue(n.X);
                        q.Enqueue(n.Y);
                    }
                }
            }
            if (self.Id != this.self.Id)
                map[self.X, self.Y] = 1;
            distance = d[self.X, self.Y];
            if (d[self.X, self.Y] == Inf)
                return null;
            foreach(Point n in Nearest(self, 1))
                if (d[n.X, n.Y] + 1 == d[self.X, self.Y])
                    return new Point(n.X, n.Y);
            throw new Exception("Something wrong");
        }

        bool haveSuchBonus(Trooper self, Bonus bo)
        {
            if (bo.Type == BonusType.Medikit)
                return self.IsHoldingMedikit;
            if (bo.Type == BonusType.Grenade)
                return self.IsHoldingGrenade;
            if (bo.Type == BonusType.FieldRation)
                return self.IsHoldingFieldRation;
            throw new Exception("Unknown bonus type");
        }

        double getBonusProfit(Trooper self, Bonus bo)
        {
            // TODO: брать бонус только чтобы другой не вз€л
            if (haveSuchBonus(self, bo))
                return -1;
            return 1.0 / getShoterPath(bo, true);
        }

        Point IfTakeBonus(Trooper self = null)
        {
            if (self == null)
                self = this.self;
            Point bestGoal = new Point(0, 0, -Inf);
            foreach (Bonus bo in bonuses)
            {
                double profit = getBonusProfit(self, bo);
                if (profit > bestGoal.profit && map[bo.X, bo.Y] == 0)
                    bestGoal = new Point(bo.X, bo.Y, profit);
            }
            if (bestGoal.profit <= 0)
                bestGoal = null;
            return bestGoal;
        }

        double getGoAtackProfit(Trooper goal)
        {
            if (goal.IsTeammate)
                return -1;
            return 1.0 / self.GetDistanceTo(goal);
        }

        Point IfGoAtack()
        {
            Point bestGoal = new Point(0, 0, -Inf);
            foreach (Trooper tr in troopers)
            {
                double profit = getGoAtackProfit(tr);
                if (profit > bestGoal.profit)
                    bestGoal = new Point(tr.X, tr.Y, profit);
            }
            if (bestGoal.profit <= 0)
                bestGoal = null;
            return bestGoal;
        }

        public void Move(Trooper self, World world, Game game, Move move)
        {
            this.self = self;
            this.world = world;
            this.game = game;
            this.move = move;
            InitializeConstants();

            if (ifFieldRationNeed())
            {
                move.Action = ActionType.EatFieldRation;
                return;
            }

            bool needMove = false;
            Point ifThrowGrenade = IfThrowGrenade(ref needMove);
            if (ifThrowGrenade != null)
            {
                if (needMove)
                    move.Action = ActionType.Move;
                else
                    move.Action = ActionType.ThrowGrenade;
                Go(ifThrowGrenade);
                return;
            }

            Point ifUseMedikit = IfUseMedikit();
            if (ifUseMedikit != null)
            {
                move.Action = ActionType.UseMedikit;
                Go(ifUseMedikit);
                return;
            }

            Point ifShot = IfShot();
            if (ifShot != null)
            {
                move.Action = ActionType.Shoot;
                Go(ifShot);
                return;
            }

            if (self.Type == TrooperType.FieldMedic)
            {
                Point ifHelp = ifHelpTeammate();
                if (ifHelp != null)
                {
                    Trooper goal = get(ifHelp.X, ifHelp.Y);
                    if (goal != null && goal.Hitpoints < goal.MaximalHitpoints && ifHelp.Nearest(self) && game.FieldMedicHealCost <= self.ActionPoints)
                    {
                        move.Action = ActionType.Heal;
                        Go(ifHelp);
                        return;
                    }
                    if (canMove())
                    {
                        Point to = goToUnit(ifHelp);
                        if (map[to.X, to.Y] == 0)
                        {
                            move.Action = ActionType.Move;
                            Go(to);
                            return;
                        }
                    }
                }
            }

            if (canMove() && self.Id != commander.Id && getTeamDiametr() > MaxTeamDiametr)
            {
                Point grouping = ifGrouping();
                // grouping != null !!!
                if (grouping.X != self.X || grouping.Y != self.Y)
                {
                    Point to = goToUnit(grouping);
                    if (to != null)
                    {
                        move.Action = ActionType.Move;
                        Go(to);
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
                        return;
                    Point to = goToUnit(helper);
                    move.Action = ActionType.Move;
                    Go(to);
                    return;
                }
            }

            Point ifGoAtack = IfGoAtack();
            if (ifGoAtack != null && canMove())
            {
                Point to = goToUnit(ifGoAtack);
                if (getTeamDiametr(self.Id, to) <= MaxTeamDiametr)
                {
                    move.Action = ActionType.Move;
                    Go(to);
                    return;
                }
            }

            Point ifTakeBonus = IfTakeBonus();
            if (ifTakeBonus != null && canMove())
            {
                Point to = goToUnit(ifTakeBonus);
                if (getTeamDiametr(self.Id, to) <= MaxTeamDiametr)
                {
                    move.Action = ActionType.Move;
                    Go(to);
                    return;
                }
            }

            if (self.Id == commander.Id && canMove())
            {
                // ѕроверить что тиммейтам нужен бонус. ≈сли нужен, то идти к туда.
                foreach(Trooper tr in friend)
                {
                    Point bonus = IfTakeBonus(tr);
                    // если € не стою на бонусе
                    if (bonus != null && !(bonus.X == self.X && bonus.Y == self.Y))
                    {
                        Point to = goToUnit(bonus);
                        // если € не наступлю на бонус //////и он без мен€ не может вз€ть
                        if (to != null && !(to.X == bonus.X && to.Y == bonus.Y)) ;// && bonus.GetDistanceTo(commander) > MaxTeamRadius)
                        {
                            if (getTeamDiametr(self.Id, to) <= MaxTeamDiametr)
                            {
                                move.Action = ActionType.Move;
                                Go(to);
                                return;
                            }
                        }
                    }
                }
            }

            Point ifNothing = IfNothing();
            if (ifNothing != null && canMove())
            {
                Point to = goToUnit(ifNothing);
                if (to == null || to.X == self.X && to.Y == self.Y)
                {
                    move.Action = ActionType.EndTurn; // “ут буду ложитьс€/садитьс€
                    return;
                }
                else if (getTeamDiametr(self.Id, to) <= MaxTeamDiametr)
                {
                    move.Action = ActionType.Move;
                    Go(to);
                    return;
                }
            }
            // “ут буду ложитьс€/садитьс€
            validateMove();
        }

        bool canMove()
        {
            return self.ActionPoints >= getMoveCost();
        }
    }
}