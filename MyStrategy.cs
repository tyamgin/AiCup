using System;
using System.Collections;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

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

        Point ifShotting()
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
            return goToUnit(new Point(bo.X, bo.Y), ref distance);
        }

        int getShoterPath(Unit tr, Trooper self = null)
        {
            if (self == null)
                self = this.self;
            int distance = 0;
            goToUnit(new Point(tr.X, tr.Y), ref distance, self);
            return distance;
        }

        int getShoterPath(Point tr)
        {
            int distance = 0;
            goToUnit(new Point(tr.X, tr.Y), ref distance);
            return distance;
        }

        Point goToUnit(Point bo)
        {
            int distance = 0;
            return goToUnit(bo, ref distance);
        }

        Point goToUnit(Point bo, ref int distance, Trooper self = null)
        {
            if (self == null)
                self = this.self;
            // TODO: если по пути можно взять бонус
            if (bo.X == self.X && bo.Y == self.Y) // застрявает в угол ??
                return bo;
            Queue q = new Queue();
            q.Enqueue(bo.X);
            q.Enqueue(bo.Y);
            int[,] d = new int[world.Width, world.Height];
            for (int i = 0; i < world.Width; i++)
                for (int j = 0; j < world.Height; j++)
                    d[i, j] = Inf;
            d[bo.X, bo.Y] = 0;
            // ???? если self != this.self, то map[self.X, selfY] = 1
            if (self.Id != this.self.Id)
                map[self.X, self.Y] = 0;
            int[] _i = { 0, 0, 1, -1 };
            int[] _j = { 1, -1, 0, 0 };
            while (q.Count != 0)
            {
                int x = (int)q.Dequeue();
                int y = (int)q.Dequeue();
                for (int k = 0; k < 4; k++)
                {
                    int ni = _i[k] + x;
                    int nj = _j[k] + y;
                    if (ni >= 0 && nj >= 0 && ni < world.Width && nj < world.Height && d[ni, nj] == Inf && map[ni, nj] == 0)
                    {
                        d[ni, nj] = d[x, y] + 1;
                        q.Enqueue(ni);
                        q.Enqueue(nj);
                    }
                }
            }
            if (self.Id != this.self.Id)
                map[self.X, self.Y] = 1;
            distance = d[self.X, self.Y];
            if (d[self.X, self.Y] == Inf)
                return null;
            for (int k = 0; k < 4; k++)
            {
                int ni = _i[k] + self.X;
                int nj = _j[k] + self.Y;
                if (ni >= 0 && nj >= 0 && ni < world.Width && nj < world.Height && d[ni, nj] + 1 == d[self.X, self.Y])
                    return new Point(ni, nj);
            }
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
            // TODO: брать бонус только чтобы другой не взял
            if (haveSuchBonus(self, bo))
                return -1;
            return 1.0 / getShoterPath(bo);
        }

        Point ifTakeBonus(Trooper self = null)
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

        Point ifGoAtack()
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

        double getTeamRadius()
        {
            double maxDist = 0;
            foreach(Trooper tr in team)
                maxDist = Math.Max(maxDist, commander.GetDistanceTo(tr));
            return maxDist;
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

            Point ifGrenade = ifThrowGrenade();
            if (ifGrenade != null)
            {
                move.Action = ActionType.ThrowGrenade;
                Go(ifGrenade);
                return;
            }

            Point ifMedkit = needUseMedikit();
            if (ifMedkit != null)
            {
                move.Action = ActionType.UseMedikit;
                Go(ifMedkit);
                return;
            }

            Point ifShot = ifShotting();
            if (ifShot != null)
            {
                move.Action = ActionType.Shoot;
                Go(ifShot);
                return;
            }

            if (needHelp() && self.Type != TrooperType.FieldMedic)
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

            if (self.Type == TrooperType.FieldMedic)
            {
                Point ifHelp = ifHelpTeammate();
                if (ifHelp != null)
                {
                    Trooper goal = get(ifHelp.X, ifHelp.Y);
                    if (goal != null && goal.Hitpoints < goal.MaximalHitpoints && ifHelp.Nearest(new Point(self.X, self.Y)) && game.FieldMedicHealCost <= self.ActionPoints)
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

            Point ifBonus = ifTakeBonus();
            if (ifBonus != null && canMove())
            {
                if (commander.Id == self.Id || ifBonus.GetDistanceTo(commander) <= MaxTeamRadius)
                {
                    move.Action = ActionType.Move;
                    Point to = goToUnit(ifBonus);
                    Go(to);
                    return;
                }
            }

            Point ifGo = ifGoAtack();
            if (ifGo != null && canMove())
            {
                move.Action = ActionType.Move;
                Point to = goToUnit(ifGo);
                Go(to);
                return;
            }

            if (self.Id == commander.Id && canMove())
            {
                // Проверить что тиммейтам нужен бонус. Если нужен, то идти к туда.
                foreach(Trooper tr in friend)
                {
                    Point bonus = ifTakeBonus(tr);
                    // если я не стою на бонусе
                    if (bonus != null && !(bonus.X == self.X && bonus.Y == self.Y))
                    {
                        Point to = goToUnit(bonus);
                        // если я не наступлю на бонус и он без меня не может взять
                        if (to != null && !(to.X == bonus.X && to.Y == bonus.Y) && bonus.GetDistanceTo(commander) > MaxTeamRadius)
                        {
                            move.Action = ActionType.Move;
                            Go(to);
                            return;
                        }
                    }
                }
            }

            Point ifNothing = ifGoNothing();
            if (ifNothing != null && canMove())
            {
                move.Action = ActionType.Move;
                Point to = goToUnit(ifNothing);
                if (to == null || to.X == self.X && to.Y == self.Y)
                    move.Action = ActionType.EndTurn;
                else
                {
                    if (commander.Id != self.Id || getTeamRadius() <= MaxTeamRadius)
                        Go(to);
                }
                return;
            }


            validateMove();
        }

        bool canMove()
        {
            return self.ActionPoints >= getMoveCost();
        }
    }
}