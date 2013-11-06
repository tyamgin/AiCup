﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

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
            if (self.Hitpoints + game.FieldMedicHealSelfBonusHitpoints <= self.MaximalHitpoints) // лечить себя
                return new Point(self.X, self.Y);
            Point bestGoal = new Point(0, 0, -Inf);
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
