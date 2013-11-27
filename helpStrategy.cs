using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        bool IfNeedHelp(Trooper self = null)
        {
            if (self == null)
                self = this.self;
            return self.Hitpoints / (double)self.MaximalHitpoints < 0.8;
        }

        double getHelperQuality(Trooper tr)
        {
            double quality = 1.0 / getShoterPath(self, tr, notFilledMap, beginFree: true, endFree: true);
            if (tr.Type == TrooperType.FieldMedic)
                return quality * 2;
            if (!tr.IsHoldingMedikit)
                return -Inf;
            return quality;
        }

        Point getBestHelper()
        {
            var bestHelper = Point.Inf;
            foreach(var tr in Friends)
            {
                double quality = getHelperQuality(tr);
                if (quality > bestHelper.profit)
                    bestHelper = new Point(tr.X, tr.Y, quality);
            }
            if (bestHelper.profit <= 0)
                bestHelper = null;
            return bestHelper;
        }

        Point IfUseMedikit()
        {
            if (!self.IsHoldingMedikit || self.ActionPoints < game.MedikitUseCost)
                return null;
            foreach (var tr in Friends)
                if (IfNeedHelp(tr) && new Point(tr).Nearest(self))
                    return new Point(tr);
             
            var bestHeal = Point.Inf;
            foreach (var tr in Team)
            {
                double profit = (double)self.MaximalHitpoints - Math.Min(self.MaximalHitpoints, self.Hitpoints + (tr.Id == self.Id ? game.MedikitHealSelfBonusHitpoints : game.MedikitBonusHitpoints));
                if (profit > bestHeal.profit && new Point(tr).Nearest(self))
                    bestHeal = new Point(tr);
            }
            if (bestHeal.profit <= 0)
                bestHeal = null;
            return bestHeal;
        }

        Point ifHelpTeammate()
        {
            // Перебираю кого лечить: min(max(0, maxhitpoints - hitpoints), 
            //                            (Очки - (длина пути - 1) * (стоимость пути)) / (стоимость лечения) * (сколько жизней восстанавливыает)
            //                        ) 
            Point bestPoint = Point.Inf;
            foreach (Trooper tr in Team)
            {
                double profit = Math.Min(Math.Max(0, tr.MaximalHitpoints - tr.Hitpoints),
                                         (self.ActionPoints - Math.Max(0.0, (double)getShoterPath(self, tr, map, beginFree: true, endFree: true) - 1) * getMoveCost()) / game.FieldMedicHealCost * (tr.Id == self.Id ? game.FieldMedicHealSelfBonusHitpoints : game.FieldMedicHealBonusHitpoints)
                                );
                if (profit > bestPoint.profit)
                    bestPoint = new Point(tr.X, tr.Y, profit);
            }
            if (bestPoint.profit <= 0)
                return null;
            return bestPoint;
        }
    }
}
