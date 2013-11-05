using System;
using System.Collections;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        bool canThrowTo(int x, int y)
        {
            if (x < 0 || y < 0 || x >= world.Width || y >= world.Height)
                return false;
            if (cells[x][y] != 0)
                return false;
            return self.GetDistanceTo(x, y) <= game.GrenadeThrowRange;
        }

        double getThrowGranadeProfit(int x, int y)
        {
            // TODO: проверить что в troopers есть self
            Point to = new Point(x, y);
            double sum = 0;
            foreach(Trooper tr in troopers)
            {
                if (tr.IsTeammate && to.Nearest(tr.X, tr.Y))
                    return -Inf;
                if (to.Same(tr.X, tr.Y))
                    sum += game.GrenadeDirectDamage;
                else if (to.Nearest(tr.X, tr.Y))
                    sum += game.GrenadeCollateralDamage;
            }
            return sum;
        }

        Point ifThrowGrenade()
        {
            if (game.GrenadeThrowCost > self.ActionPoints)
                return null;
            if (!self.IsHoldingGrenade)
                return null;
            int grenadeRange = (int)(game.GrenadeThrowRange + 1);
            Point bestPoint = new Point(0, 0, -Inf);
            for(int x = self.X - grenadeRange; x <= self.X + grenadeRange; x++)
            {
                for(int y = self.Y - grenadeRange; y <= self.Y + grenadeRange; y++)
                {
                    if (canThrowTo(x, y))
                    {
                        double profit = getThrowGranadeProfit(x, y);
                        if (bestPoint.profit < profit)
                            bestPoint = new Point(x, y, profit);
                    }
                }
            }
            if (bestPoint.profit <= 0)
                bestPoint = null;
            return bestPoint;
        }

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
            if (self.ActionPoints < self.ShotCost)
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
            return goToUnit(new Point(bo.X, bo.Y));
        }
        
        Point goToUnit(Point bo)
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
                if (profit > bestGoal.profit)
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

        /*double getIfNothingProfit(int x, int y)
        {
            double sum = 0;
            if (map[x, y] != 0)
                return -Inf;
            
            foreach (Trooper tr in troopers)
            {
                if (tr.IsTeammate)
                {
                    sum += tr.GetDistanceTo(x, y);
                }
            }
            return sum;
        }*/

        Point ifGoNothing()
        {
            //Point bestPoint = new Point(0, 0, -Inf);
            if (game.MoveCount < 10)
                return new Point(0, 0);
            if (game.MoveCount < 20)
                return new Point(world.Width - 1, 0);
            if (game.MoveCount < 30)
                return new Point(world.Width - 1, world.Height - 1);
            return new Point(0, world.Height - 1);

            /*for (int i = 0; i < world.Width; i++)
            {
                for (int j = 0; j < world.Height; j++)
                {
                    double profit = getIfNothingProfit(i, j);
                    if (profit > bestPoint.profit)
                        bestPoint = new Point(i, j, profit);
                }
            }
            return bestPoint;*/
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

        Point needUseMedikit()
        {
            if (!self.IsHoldingMedikit || self.ActionPoints < game.MedikitUseCost)
                return null;
            // TODO: хилить не себ€
            return self.MaximalHitpoints >= self.Hitpoints + game.MedikitHealSelfBonusHitpoints ? new Point(self.X, self.Y) : null;
        }

        double getHelpTeammateProfit(Trooper goal)
        {
            // TODO:
            return 1 / (goal.Hitpoints / (double)goal.MaximalHitpoints);
        }

        Point ifHelpTeammate()
        {
            if (self.Hitpoints + game.FieldMedicHealSelfBonusHitpoints <= self.MaximalHitpoints) // лечить себ€
                return new Point(self.X, self.Y);
            Point bestGoal = new Point(0, 0, -Inf);
            foreach(Trooper tr in troopers)
            {
                if (tr.IsTeammate && tr.Id != self.Id)
                {
                    double profit = getHelpTeammateProfit(tr);
                    if (profit > bestGoal.profit)
                        bestGoal = new Point(tr.X, tr.Y, profit);
                }
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
            foreach (Trooper tr in troopers)
                if (tr.Id != self.Id)
                    map[tr.X, tr.Y] = 1;
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

            Point ifBonus = ifTakeBonus();
            if (ifBonus != null && canMove())
            {
                move.Action = ActionType.Move;
                Point to = goToUnit(ifBonus);
                Go(to);
                return;
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