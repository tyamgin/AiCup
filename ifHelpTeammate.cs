using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

// TODO:!! приоритет меньше жизней, учитывать аптечку

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        Point ifHelpTeammate()
        {
            // Перебираю кого лечить: min(max(0, maxhitpoints - hitpoints), 
            //                            (Очки - (длина пути - 1) * (стоимость пути)) / (стоимость лечения) * (сколько жизней восстанавливыает)
            //                        ) 
            Point bestPoint = Point.Inf;
            foreach (Trooper tr in team)
            {
                double profit = Math.Min(Math.Max(0, tr.MaximalHitpoints - tr.Hitpoints),
                                         (self.ActionPoints - Math.Max(0, getShoterPath(self, tr, map, beginFree: true, endFree: true) - 1) * getMoveCost()) / game.FieldMedicHealCost * (tr.Id == self.Id ? game.FieldMedicHealSelfBonusHitpoints : game.FieldMedicHealBonusHitpoints)
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
