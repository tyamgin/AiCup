using System;
using System.Collections;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

// TODO!!!: вместо того чтобы явно стрелять и убивать - шел группироваться
// TODO!!!: пытался пройти к медику через трупера, но не мог

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

        // Направляться к этому юниту.
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
            
            if (bo.X == self.X && bo.Y == self.Y) // застрявает в угол ??
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
            Point bestTurn = new Point(0, 0, Inf);
            foreach (Point n in Nearest(self, 1))
                if (d[n.X, n.Y] + 1 == d[self.X, self.Y] && bestTurn.profit > getTeamRadius(self.Id, n))
                    bestTurn = new Point(n.X, n.Y, getTeamRadius(self.Id, n));
            return bestTurn;
        }

        double getGoAtackProfit(Trooper goal)
        {
            if (goal.IsTeammate)
                return -1;
            return 1.0 / self.GetDistanceTo(goal);
        }

        Point IfGoAtack()
        {
            Point bestGoal = Point.Inf;
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

        Point getMostDanger()
        {
            Point mostDanger = Point.Inf;
            foreach (Trooper tr in team)
                if (danger[tr.X, tr.Y] > mostDanger.profit)
                    mostDanger = new Point(tr.X, tr.Y, danger[tr.X, tr.Y]);
            if (mostDanger.profit <= DangerNothing)
                return null;
            return mostDanger;
        }

        bool haveDanger()
        {
            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++)
                    if (danger[i, j] != DangerNothing)
                        return true;
            return false;
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
            Reached(new Point(self));

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

            // может возникнуть такое, что очков на выстрел нету, и он попытается пойти на клетку врага
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
                    Trooper goal = getTrooperAt(ifHelp.X, ifHelp.Y);
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

            if (canMove() && self.Id != commander.Id && getTeamRadius() > MaxTeamRadius)
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

            // Если нужно идти атаковать, то тот кто находится на самой опасной зоне выполняет IfGoAtack,
            // остальные приближаются к EncirclingPoints того кто в опастности
            Point mostDanger = getMostDanger();
            if (mostDanger != null)
            {
                if (mostDanger.X == self.X && mostDanger.Y == self.Y)
                {
                    Point ifGoAtack = IfGoAtack();
                    if (ifGoAtack != null && canMove())
                    {
                        Point to = goToUnit(ifGoAtack);
                        if (getTeamRadius(self.Id, to) <= MaxTeamRadius)
                        {
                            move.Action = ActionType.Move;
                            Go(to);
                            return;
                        }
                    }
                }
                else
                {
                    Point goToEncircling = GoToEncircling(getTrooperAt(mostDanger));
                    if (goToEncircling != null && canMove())
                    {
                        Point to = goToUnit(goToEncircling);
                        if (to == null || to.X == self.X && to.Y == self.Y) // TODO: если to = null, то значит мы застряли
                        {
                            move.Action = ActionType.EndTurn; // Тут буду ложиться/садиться
                            return;
                        }
                        if (getTeamRadius(self.Id, to) <= MaxTeamRadius) // ??
                        {
                            move.Action = ActionType.Move;
                            Go(to);
                            return;
                        }
                    }
                }
            }
            

            Point ifTakeBonus = IfTakeBonus();
            if (ifTakeBonus != null && canMove())
            {
                Point to = goToUnit(ifTakeBonus);
                if (getTeamRadius(self.Id, ifTakeBonus) <= MaxTeamRadius && getTeamRadius(self.Id, to) <= MaxTeamRadius)
                {
                    move.Action = ActionType.Move;
                    Go(to);
                    return;
                }
            }

            Point ifTeamBonus = IfTeamBonus();
            if (ifTeamBonus != null && (Goal == null || !isBonusExistAt(Goal)))
            {
                Goal = ifTeamBonus;
            }

            Point ifNothing = IfNothing();
            if (ifNothing != null && canMove())
            {
                Point to = goToUnit(ifNothing);
                if (to == null || to.X == self.X && to.Y == self.Y) // TODO: если to = null, то значит мы застряли
                {
                    if (to == null)
                    {
                        to = to;
                        // передать коммандование
                        //ChangeCommander(); // TODO:!!!
                    }
                    move.Action = ActionType.EndTurn; // Тут буду ложиться/садиться
                    return;
                }
                else if (getTeamRadius(self.Id, to) <= MaxTeamRadius)
                {
                    move.Action = ActionType.Move;
                    Go(to);
                    return;
                }
            }
            // Тут буду ложиться/садиться
            validateMove();
        }

        bool canMove()
        {
            return self.ActionPoints >= getMoveCost();
        }
    }
}