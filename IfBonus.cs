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
        int getQueuePlace(Trooper trooper, bool MayFirst)
        {
            int current = queue.IndexOf(self.Id);
            for (int idx = current + (MayFirst ? 0 : 1); idx < queue.Count; idx++)
                if ((long)queue[idx] == trooper.Id)
                    return idx - current + 1;
            return 1;
        }

        int getQueuePlace2(Trooper trooper, bool MayFirst)
        {
            for (int i = 0; i < queue.Count; i++)
            {
                if (getTrooper((long)queue[i]) == null)
                {
                    queue.RemoveAt(i);
                    i--;
                }
            }
            int current = queue.IndexOf(self.Id);
            for (int idx = current + (MayFirst ? 0 : 1); ; idx++)
                if ((long)queue[idx % queue.Count] == trooper.Id)
                    return idx - current + 1;
            return -1;
        }

        bool haveSuchBonus(Trooper self, Bonus bonus)
        {
            return bonus != null && haveSuchBonus(self, bonus.Type);
        }

        bool haveSuchBonus(Trooper self, BonusType bonus)
        {
            if (bonus == BonusType.Medikit)
                return self.IsHoldingMedikit;
            if (bonus == BonusType.Grenade)
                return self.IsHoldingGrenade;
            if (bonus == BonusType.FieldRation)
                return self.IsHoldingFieldRation;
            throw new Exception("Unknown bonus type");
        }

        double getTeamBonusProfit(Bonus bonus, ref Trooper trooper)
        {
            // нужен минимальный вес
            double bestWeight = Inf;
            foreach (var tr in Team)
            {
                //if (getShoterPath(tr, bonus, map, beginFree: true, endFree: false) < Inf)
                {
                    double weight = getShoterPath(tr, bonus, notFilledMap, beginFree: true, endFree: false) * (1 + 0.5 * getQueuePlace(tr, self.Id == tr.Id && self.ActionPoints >= self.InitialActionPoints));
                    if (tr.Type == TrooperType.FieldMedic || tr.Type == TrooperType.Sniper)
                        weight = getShoterPath(tr, bonus, map, beginFree: true, endFree: false) <= 2 ? weight : Inf;
                    if (!haveSuchBonus(tr, bonus) && weight < bestWeight)
                    {
                        bestWeight = weight;
                        trooper = tr;
                    }
                }
            }
            if (bestWeight >= Inf)
                return -1;
            return 1.0 / bestWeight;
        }

        Point IfTeamBonus(ref Trooper result)
        {
            var bestPoint = Point.Inf;
            result = null;
            foreach (var bo in Bonuses)
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

        Point SkipPath(Trooper center, Point goal, bool needShootingPosition)
        {
            // В первую очередь минимизировать путь center до goal
            var bestPoint = new Point(0, 0, Inf);
            int minDistToCenter = Inf;
            for (var i = 0; i < Width; i++)
            {
                for (var j = 0; j < Height; j++)
                {
                    if (map[i, j] == 0 || i == self.X && j == self.Y)
                    {
                        if (self.GetDistanceTo(i, j) > 10) // немного ускорит
                            continue;
                        if (needShootingPosition && howManyCanShoot(new Point(i, j), self.Stance) == 0)
                            continue;
                        // Нужно чтобы хватило ходов
                        int steps = getShoterPath(self, new Point(i, j), map, beginFree: true, endFree: true);
                        if (self.ActionPoints / getMoveCost() >= steps)
                        {
                            // и чтобы не закрывали кратчайший путь:
                            map[self.X, self.Y] = 0;
                            map[i, j] = 1;
                            int after = getShoterPath(center, goal, map, beginFree: true, endFree: false);
                            map[i, j] = 0;
                            map[self.X, self.Y] = 1;

                            int path = getShoterPath(center, new Point(i, j), notFilledMap, beginFree: true, endFree: true);
                            if (after < bestPoint.profit || after == bestPoint.profit && path < minDistToCenter)
                            {
                                bestPoint = new Point(i, j, after);
                                minDistToCenter = path;
                            }
                        }
                    }
                }
            }
            if (bestPoint.profit >= Inf)
            {
                if (needShootingPosition)
                    return SkipPath(center, goal, false);
                return null;
            }
            return bestPoint;
        }

        bool IfFieldRationNeed()
        {
            if (!self.IsHoldingFieldRation || self.ActionPoints < game.FieldRationEatCost || self.ActionPoints + game.FieldRationBonusActionPoints - game.FieldRationEatCost > self.InitialActionPoints)
                return false;
            if (howManyCanShoot(new Point(self), self.Stance) != 0)
                return true;
            return false;
        }
    }
}
