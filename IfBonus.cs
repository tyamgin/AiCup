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

        bool haveSuchBonus(Trooper self, Bonus bonus)
        {
            if (bonus == null)
                return false;
            return haveSuchBonus(self, bonus.Type);
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

        //int getInitialActionPoints(Trooper tr, int x, int y)
        //{
        //    int points = tr.InitialActionPoints;
        //    Trooper cmd = getCmd();
        //    if (cmd != null && tr.Type != TrooperType.Commander && tr.Type == TrooperType.Scout && cmd.GetDistanceTo(x, y) <= game.CommanderAuraRange)
        //        points += game.CommanderAuraBonusActionPoints;
        //    return points;
        //}

        double getTeamBonusProfit(Bonus bonus, ref Trooper trooper)
        {
            // нужен минимальный вес
            double bestWeight = Inf;
            foreach (Trooper tr in team)
            {
                //if (getShoterPath(tr, bonus, map, beginFree: true, endFree: false) < Inf)
                {
                    double weight = getShoterPath(tr, bonus, notFilledMap, beginFree: true, endFree: false) * (1 + 0.5 * getQueuePlace(tr, self.Id == tr.Id && self.ActionPoints >= self.InitialActionPoints));
                    if (bonus.Type != BonusType.Medikit && tr.Type == TrooperType.FieldMedic)
                        weight *= 4;
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



        Point SkipPath(Trooper center, Point goal, bool needShootingPosition)
        {
            // В первую очередь минимизировать путь center до goal
            Point bestPoint = new Point(0, 0, Inf);
            int minDistToCenter = Inf;
            for (int i = 0; i < width; i++)
            {
                for (int j = 0; j < height; j++)
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
    }
}
