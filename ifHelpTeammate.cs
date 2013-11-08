using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

// TODO: пересмотреть коммандные бонусы
// TODO: капитан почему-то отходит от диаметра

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        double getHelpTeammateProfit(Trooper goal)
        {
            // TODO:
            if (goal.Hitpoints >= goal.MaximalHitpoints)
                return -1;
            return 1 / (goal.Hitpoints / (double)goal.MaximalHitpoints);
        }

        Point ifHelpTeammate()
        {
            Point bestGoal = new Point(0, 0, -Inf);
            foreach (Trooper tr in friend)
            {
                if (new Point(tr).Nearest(self))
                {
                    double profit = getHelpTeammateProfit(tr);
                    if (profit > bestGoal.profit)
                        bestGoal = new Point(tr.X, tr.Y, profit);
                }
            }
            if (bestGoal.profit > 0)
                return bestGoal;
            if (self.Hitpoints + game.FieldMedicHealSelfBonusHitpoints <= self.MaximalHitpoints) // лечить себя
                return new Point(self.X, self.Y);
            foreach (Trooper tr in friend)
            {
                double profit = getHelpTeammateProfit(tr);
                if (profit > bestGoal.profit)
                    bestGoal = new Point(tr.X, tr.Y, profit);
            }
            if (bestGoal.profit <= 0)
                bestGoal = null;
            return bestGoal;
        }
    }
}
