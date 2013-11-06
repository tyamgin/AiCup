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
        Point Goal = null;

        Point ifGoNothingCommander()
        {
            if (Goal == null || self.GetDistanceTo(Goal.X, Goal.Y) < world.Height / 4)
            {
                ArrayList places = new ArrayList();
                for (int x = 1; x < world.Width; x += world.Width - 3)
                {
                    for (int y = 1; y < world.Height; y += world.Height - 3)
                    {
                        if (Goal == null || Goal.GetDistanceTo(x, y) > world.Height / 3)
                        {
                            places.Add(new Point(x, y));
                        }
                    }
                }
                var pl = places.ToArray();
                Goal = pl[random.Next(places.Count)] as Point;
            }
            return Goal != null ? Goal : new Point(self.X, self.Y);
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
