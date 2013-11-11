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

        double getTeamBonusProfit(Bonus bonus, ref Trooper trooper)
        {
            int bestPath = Inf;
            foreach (Trooper tr in team)
            {
                int path = getShoterPath(tr, bonus, notFilledMap, beginFree:true, endFree: false);
                if (!haveSuchBonus(tr, bonus) && path < bestPath)
                {
                    bestPath = path;
                    trooper = tr;
                }
            }
            if (bestPath == Inf)
                return -1;
            return 1.0 / bestPath;
        }

        Point IfTeamBonus(ref Trooper result)
        {
            Point bestPoint = Point.Inf;
            result = null;
            foreach (Bonus bo in bonuses)
            {
                Trooper whose = null; ;
                double profit = getTeamBonusProfit(bo, ref whose);
                if (profit > bestPoint.profit)
                {
                    bestPoint = new Point(bo.X, bo.Y, profit);
                    result = whose;
                }
            }
            if (bestPoint.profit <= 0)
                bestPoint = null;
            return bestPoint;
        }
    }
}
