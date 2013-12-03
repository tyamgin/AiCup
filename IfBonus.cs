using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;
using System.Collections;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        int GetQueuePlace(Trooper trooper, bool MayFirst)
        {
            int current = queue.IndexOf(self.Id);
            for (int idx = current + (MayFirst ? 0 : 1); idx < queue.Count; idx++)
                if ((long)queue[idx] == trooper.Id)
                    return idx - current + 1;
            return 1;
        }

        int GetQueuePlace2(Trooper trooper, bool MayFirst)
        {
            for (var i = 0; i < queue.Count; i++)
            {
                if (GetTrooper((long)queue[i]) == null)
                {
                    queue.RemoveAt(i);
                    i--;
                }
            }
            var current = queue.IndexOf(self.Id);
            for (var idx = current + (MayFirst ? 0 : 1); ; idx++)
                if ((long)queue[idx % queue.Count] == trooper.Id)
                    return idx - current + 1;
        }

        bool IsHaveBonus(Trooper self, Bonus bonus)
        {
            return bonus != null && IsHaveBonus(self, bonus.Type);
        }

        bool IsHaveBonus(Trooper self, BonusType bonus)
        {
            if (bonus == BonusType.Medikit)
                return self.IsHoldingMedikit;
            if (bonus == BonusType.Grenade)
                return self.IsHoldingGrenade;
            if (bonus == BonusType.FieldRation)
                return self.IsHoldingFieldRation;
            throw new InvalidDataException();
        }

        double GetTeamBonusProfit(Bonus bonus, ref Trooper trooper)
        {
            // нужен минимальный вес
            double bestWeight = Inf;
            foreach (var tr in Team)
            {
                double weight = GetShoterPath(tr, bonus, notFilledMap, beginFree: true, endFree: false) * (1 + 0.5 * GetQueuePlace(tr, self.Id == tr.Id && self.ActionPoints >= self.InitialActionPoints));
                if (tr.Id != commander.Id && tr.Type != TrooperType.Commander && tr.Type != TrooperType.Scout)
                    weight = GetShoterPath(tr, bonus, map, beginFree: true, endFree: false) <= 2 ? weight : Inf;
                if (!IsHaveBonus(tr, bonus) && weight < bestWeight)
                {
                    bestWeight = weight;
                    trooper = tr;
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
                double profit = GetTeamBonusProfit(bo, ref whose);
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

        Point SkipPath(Trooper center, Point goal)
        {
            // В первую очередь минимизировать путь center до goal
            var bestPoint = new Point(0, 0, Inf);
            double minPenalty = Inf;
            for (var i = 0; i < Width; i++)
            {
                for (var j = 0; j < Height; j++)
                {
                    if (map[i, j] == 0 || i == self.X && j == self.Y)
                    {
                        if (self.GetDistanceTo(i, j) > 10) // немного ускорит
                            continue;
                        // Нужно чтобы хватило ходов
                        int steps = GetShoterPath(self, new Point(i, j), map, beginFree: true, endFree: true);
                        if (self.ActionPoints / GetMoveCost() >= steps)
                        {
                            // и чтобы не закрывали кратчайший путь:
                            map[self.X, self.Y] = 0;
                            map[i, j] = 1;
                            int after = GetShoterPath(center, goal, map, beginFree: true, endFree: false);
                            map[i, j] = 0;
                            map[self.X, self.Y] = 1;

                            double penalty = GetShoterPath(center, new Point(i, j), notFilledMap, beginFree: true, endFree: true);
                            penalty += 2*Math.Max(0, goal.GetDistanceTo(center) - goal.GetDistanceTo(i, j) + 1);

                            if (after < bestPoint.profit || EqualF(after, bestPoint.profit) && penalty < minPenalty)
                            {
                                bestPoint = new Point(i, j, after);
                                minPenalty = penalty;
                            }
                        }
                    }
                }
            }
            return bestPoint.profit >= Inf ? null : bestPoint;
        }

        bool IfFieldRationNeed()
        {
            if (!self.IsHoldingFieldRation || self.ActionPoints < game.FieldRationEatCost || self.ActionPoints + game.FieldRationBonusActionPoints - game.FieldRationEatCost > self.InitialActionPoints)
                return false;
            if (HowManyCanShoot(new Point(self), self.Stance) != 0)
                return true;
            return false;
        }
    }
}
