using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

// TODO: пересмотреть коммандные бонусы

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        bool haveSuchBonus(Trooper self, Bonus bonus)
        {
            if (bonus.Type == BonusType.Medikit)
                return self.IsHoldingMedikit;
            if (bonus.Type == BonusType.Grenade)
                return self.IsHoldingGrenade;
            if (bonus.Type == BonusType.FieldRation)
                return self.IsHoldingFieldRation;
            throw new Exception("Unknown bonus type");
        }

        double getBonusProfit(Trooper self, Bonus bonus)
        {
            if (haveSuchBonus(self, bonus))
                return -1;
            return 1.0 / getShoterPath(bonus, true);
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
    }
}
