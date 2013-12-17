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

        double GetHelperQuality(Trooper tr)
        {
            double quality = 1.0 / GetShoterPath(self, tr, notFilledMap, beginFree: true, endFree: true);
            if (tr.Type == TrooperType.FieldMedic)
                return quality * 2;
            if (!tr.IsHoldingMedikit)
                return -Inf;
            return quality;
        }

        Point IfUseMedikit()
        {
            if (!self.IsHoldingMedikit || self.ActionPoints < game.MedikitUseCost)
                return null;
            foreach (var friend in Friends)
                if (IfNeedHelp(friend) && new Point(friend).Nearest(self))
                    return new Point(friend);
             
            var bestHeal = Point.MInf;
            foreach (var trooper in Team)
            {
                double profit = (double)self.MaximalHitpoints - Math.Min(self.MaximalHitpoints, self.Hitpoints + (trooper.Id == self.Id ? game.MedikitHealSelfBonusHitpoints : game.MedikitBonusHitpoints));
                if (profit > bestHeal.profit && new Point(trooper).Nearest(self))
                    bestHeal = new Point(trooper.X, trooper.Y, profit);
            }
            if (bestHeal.profit <= 0)
                bestHeal = null;
            return bestHeal;
        }

        Point IfHelpTeammate()
        {
            // Перебираю кого лечить: min(max(0, maxhitpoints - hitpoints), 
            //                            (Очки - (длина пути - 1) * (стоимость пути)) / (стоимость лечения) * (сколько жизней восстанавливыает)
            //                        ) 
            var bestPoint = Point.MInf;
            foreach (var trooper in Team)
            {
                double profit = Math.Min(Math.Max(0, trooper.MaximalHitpoints - trooper.Hitpoints),
                                         (self.ActionPoints - Math.Max(0.0, (double)GetShoterPath(self, trooper, map, beginFree: true, endFree: true) - 1) * GetMoveCost()) / game.FieldMedicHealCost * (trooper.Id == self.Id ? game.FieldMedicHealSelfBonusHitpoints : game.FieldMedicHealBonusHitpoints)
                                );
                if (profit > bestPoint.profit)
                    bestPoint = new Point(trooper.X, trooper.Y, profit);
            }
            return bestPoint.profit <= 0 ? null : bestPoint;
        }
    }
}
