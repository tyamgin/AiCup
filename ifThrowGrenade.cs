using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        bool canThrowTo(int x, int y)
        {
            if (x < 0 || y < 0 || x >= world.Width || y >= world.Height)
                return false;
            if (cells[x][y] != 0)
                return false;
            return self.GetDistanceTo(x, y) <= game.GrenadeThrowRange;
        }

        double getThrowGranadeProfit(int x, int y)
        {
            // TODO: проверить что в troopers есть self
            Point to = new Point(x, y);
            double sum = 0;
            foreach (Trooper tr in troopers)
            {
                if (tr.IsTeammate && to.Nearest(tr.X, tr.Y))
                    return -Inf;
                if (to.Same(tr.X, tr.Y))
                    sum += game.GrenadeDirectDamage;
                else if (to.Nearest(tr.X, tr.Y))
                    sum += game.GrenadeCollateralDamage;
            }
            return sum;
        }

        Point ifThrowGrenade()
        {
            if (game.GrenadeThrowCost > self.ActionPoints)
                return null;
            if (!self.IsHoldingGrenade)
                return null;
            int grenadeRange = (int)(game.GrenadeThrowRange + 1);
            Point bestPoint = new Point(0, 0, -Inf);
            for (int x = self.X - grenadeRange; x <= self.X + grenadeRange; x++)
            {
                for (int y = self.Y - grenadeRange; y <= self.Y + grenadeRange; y++)
                {
                    if (canThrowTo(x, y))
                    {
                        double profit = getThrowGranadeProfit(x, y);
                        if (bestPoint.profit < profit)
                            bestPoint = new Point(x, y, profit);
                    }
                }
            }
            if (bestPoint.profit <= 0)
                bestPoint = null;
            return bestPoint;
        }
    }
}
