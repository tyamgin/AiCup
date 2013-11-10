using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        bool canThrowTo(Point pos, int x, int y)
        {
            if (x < 0 || y < 0 || x >= width || y >= height)
                return false;
            if (cells[x][y] != 0)
                return false;
            return pos.GetDistanceTo(x, y) <= game.GrenadeThrowRange;
        }

        double getThrowGranadeProfit(int x, int y)
        {
            Point to = new Point(x, y);
            double sum = 0;
            foreach (Trooper tr in troopers)
            {
                if (tr.IsTeammate)
                {
                    if (to.Nearest(tr))
                        return -Inf;
                }
                else
                {
                    if (to.Same(tr.X, tr.Y))
                        sum += game.GrenadeDirectDamage;
                    else if (to.Nearest(tr.X, tr.Y))
                        sum += game.GrenadeCollateralDamage;
                }
            }
            return sum;
        }

        Point throwGrenadeByPos(Point pos)
        {
            int grenadeRange = (int)(game.GrenadeThrowRange + 1);
            Point bestPoint = Point.Inf;
            for (int x = pos.X - grenadeRange; x <= pos.X + grenadeRange; x++)
            {
                for (int y = pos.Y - grenadeRange; y <= pos.Y + grenadeRange; y++)
                {
                    if (canThrowTo(pos, x, y))
                    {
                        double profit = getThrowGranadeProfit(x, y);
                        if (bestPoint.profit < profit)
                            bestPoint = new Point(x, y, profit);
                    }
                }
            }
            return bestPoint;
        }

        // needMove - нужно ли сдвигаться чтобы потом кинуть
        Point IfThrowGrenade(ref bool needMove)
        {
            needMove = false;
            if (game.GrenadeThrowCost > self.ActionPoints)
                return null;
            if (!self.IsHoldingGrenade)
                return null;

            Point bestPoint = throwGrenadeByPos(new Point(self)); // если сразу кидать
            Point moveTo = null;
            if (game.GrenadeThrowCost + getMoveCost() <= self.ActionPoints)
            {
                foreach(Point p in Nearest(self, map))
                {
                    Point profit = throwGrenadeByPos(p);
                    if (profit.profit > bestPoint.profit)
                    {
                        bestPoint = profit;
                        moveTo = new Point(p.X, p.Y);
                        needMove = true;
                    }
                }
            }
            if (bestPoint.profit <= game.GrenadeCollateralDamage) // граната должна задеть хотябы двоих или точно попадет в одного
                return null;
            if (needMove)
                bestPoint = moveTo;
            return bestPoint;
        }
    }
}
