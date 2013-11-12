using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;
using System.Collections;

// TODO: пересмотреть коммандные бонусы

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        int getQueuePlace(Trooper trooper, bool MayFirst)
        {
            int current = queue.IndexOf(self.Id);
            for (int idx = current + (MayFirst ? 0 : 1); idx < queue.Count; idx++)
                if ((long)queue[idx] == trooper.Id)
                    return idx - current + 1;
            return 1;
        }

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
            // нужен минимальный вес
            double bestWeight = Inf;
            foreach (Trooper tr in team)
            {
                double weight = getShoterPath(tr, bonus, notFilledMap, beginFree:true, endFree: false) * (1 + 0.5 * getQueuePlace(tr, self.Id == tr.Id && self.ActionPoints >= self.InitialActionPoints));
                if (bonus.Type != BonusType.Medikit && tr.Type == TrooperType.FieldMedic)
                    weight *= 2;
                if (!haveSuchBonus(tr, bonus) && weight < bestWeight)
                {
                    bestWeight = weight;
                    trooper = tr;
                }
            }
            if (bestWeight == Inf)
                return -1;
            return 1.0 / bestWeight;
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
