using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        Point ifGoNothingCommander()
        {
            for (int x = 1; x < world.Width; x += world.Width - 3)
                for (int y = 1; y < world.Height; y += world.Height - 3)
                    if (self.GetDistanceTo(x, y) > world.Height / 3)
                        return new Point(x, y);
            return new Point(self.X, self.Y);
        }

        Point ifGoNothing()
        {
            var commander = getCommander();
            if (commander.Id == self.Id)
                return ifGoNothingCommander();
            int[] _i = { 1, 1, -1, -1, 2, 2, -2, -2 };
            int[] _j = { 1, -1, 1, -1, 2, -2, 2, -2 };
            Point bestPoint = Point.Inf;
            for (int k = 0; k < _i.Length; k++)
            {
                int ni = commander.X + _i[k];
                int nj = commander.Y + _j[k];
                if (ni >= 0 && nj >= 0 && ni < world.Width && nj < world.Height && map[ni, nj] == 0)
                {
                    double quality = 1.0 / getShoterPath(new Point(ni, nj));
                    if (quality > bestPoint.profit)
                        bestPoint = new Point(ni, nj, quality);
                }
            }
            if (bestPoint.profit <= 0)
                bestPoint = null;
            return bestPoint;
        }
    }
}
