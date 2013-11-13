using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;
using System.Collections;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        int getDamage(Trooper tr, TrooperStance stance)
        {
            if (stance == TrooperStance.Standing)
                return tr.StandingDamage;
            if (stance == TrooperStance.Kneeling)
                return tr.KneelingDamage;
            if (stance == TrooperStance.Prone)
                return tr.ProneDamage;
            throw new Exception("Unknown TrooperStance");
        }

        int getVisibleShotSteps(TrooperStance stance, int current)
        {
            int shots = 0;
            foreach (Trooper tr in opponents)
            {
                if (world.IsVisible(self.VisionRange, self.X, self.Y, stance, tr.X, tr.Y, tr.Stance))
                {
                    int damage = getDamage(self, stance);
                    shots += (tr.Hitpoints + damage - 1) / damage;
                }
            }
            int step;
            for (step = 1; ; step++)
            {
                int c = step == 1 ? current : self.ActionPoints;
                if (c / self.ShootCost >= shots)
                    break;
                shots -= c / self.ShootCost;
            }
            return step;
        }

        double getShotProfit(Trooper goal)
        {
            if (goal.IsTeammate)
                return -Inf;
            double profit = 1.0 / goal.Hitpoints;
            if (isLastInTeam(goal))
                return profit * 1.5;
            return profit;
        }

        Point IfShot()
        {
            if (self.ActionPoints < self.ShootCost)
                return null;
            Point bestGoal = Point.Inf;
            foreach (Trooper tr in troopers)
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

        bool mustAtack()
        {
            if (self.Type == TrooperType.Commander)
            {
                return self.ActionPoints >= getMoveCost() + 2 * self.ShootCost; // 1 сходить + 2 выстрелить
            }
            else if (self.Type == TrooperType.FieldMedic)
            {
                if (friend.Count == 0)
                    return self.ActionPoints == self.InitialActionPoints;
                return false; // TODO: подумать
            }
            else if (self.Type == TrooperType.Scout)
            {
                return true;
                // TODO:
            }
            else if (self.Type == TrooperType.Sniper)
            {
                return true;
                // TODO:
            }
            else if (self.Type == TrooperType.Soldier)
            {
                return self.ActionPoints == self.InitialActionPoints;
            }
            else
            {
                throw new Exception("Unknown TrooperType");
            }
        }
    }
}
