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

        bool haveSuchBonus(Bonus bo)
        {
            if (bo.Type == BonusType.Medikit)
                return self.IsHoldingMedikit;
            if (bo.Type == BonusType.Grenade)
                return self.IsHoldingGrenade;
            if (bo.Type == BonusType.FieldRation)
                return self.IsHoldingFieldRation;
            throw new Exception("Unknown bonus type");
        }

        double getBonusPriority(Bonus bo)
        {
            if (bo.Type == BonusType.Medikit)
                return 3;
            if (bo.Type == BonusType.Grenade)
                return 2;
            if (bo.Type == BonusType.FieldRation)
                return 1;
            throw new Exception("Unknown bonus type");
        }

        // TODO: сухпаЄк

        double getBonusProfit(Bonus bo)
        {
            // TODO: брать бонус только чтобы другой не вз€л
            if (haveSuchBonus(bo))
                return -1;
            return getBonusPriority(bo);
        }


        // Ќаправл€тьс€ к этому юниту.
        Point goToUnit(Unit bo)
        {
            int distance = 0;
            return goToUnit(new Point(bo.X, bo.Y), ref distance);
        }

        int getShoterPath(Unit tr)
        {
            int distance = 0;
            goToUnit(new Point(tr.X, tr.Y), ref distance);
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

        Point goToUnit(Point bo, ref int distance)
        {
            // TODO: если по пути можно вз€ть бонус
            if (bo.X == self.X && bo.Y == self.Y) // застр€вает в угол
                return bo;
            Queue q = new Queue();
            q.Enqueue(bo.X);
            q.Enqueue(bo.Y);
            int[,] d = new int[world.Width, world.Height];
            for (int i = 0; i < world.Width; i++)
                for (int j = 0; j < world.Height; j++)
                    d[i, j] = Inf;
            d[bo.X, bo.Y] = 0;
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

        Point ifTakeBonus()
        {
            Point bestGoal = new Point(0, 0, -Inf);
            foreach (Bonus bo in bonuses)
            {
                double profit = getBonusProfit(bo);
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

        public void Move(Trooper self, World world, Game game, Move move)
        {
            this.self = self;
            this.world = world;
            this.game = game;
            this.move = move;
            this.troopers = world.Troopers;
            this.bonuses = world.Bonuses;
            this.cells = world.Cells;
            map = new int[world.Width, world.Height];
            for (int i = 0; i < world.Width; i++)
                for (int j = 0; j < world.Height; j++)
                    map[i, j] = cells[i][j] == 0 ? 0 : 1;
            team = new ArrayList();
            friend = new ArrayList();
            foreach (Trooper tr in troopers)
            {
                if (tr.Id != self.Id)
                    map[tr.X, tr.Y] = 1;
                if (tr.IsTeammate)
                {
                    team.Add(tr);
                    if (tr.Id != self.Id)
                        friend.Add(tr);
                }
            }
            map[self.X, self.Y] = 0;

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
                move.Action = ActionType.Move;
                Point to = goToUnit(ifBonus);
                Go(to);
                return;
            }

            Point ifGo = ifGoAtack();
            if (ifGo != null && canMove())
            {
                move.Action = ActionType.Move;
                Point to = goToUnit(ifGo);
                Go(to);
                return;
            }

            Point ifNothing = ifGoNothing();
            if (ifNothing != null && canMove())
            {
                move.Action = ActionType.Move;
                Point to = goToUnit(ifNothing);
                if (to == null || to.X == self.X && to.Y == self.Y)
                    move.Action = ActionType.EndTurn;
                else
                    Go(to);
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