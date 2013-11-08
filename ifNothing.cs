using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;
using System.Collections;

// TODO:!!! радиус зависит от плотности местности
// TODO: если я выигрываю и остался 1 противник, то залечь
// TODO: ходить за тимлидом, чтобы не обходить препятствия - искать кратчайший путь без учета юнитов
// TODO: поле опастности

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        Point Goal = null;

        Point IfNothingCommander()
        {
            if (Goal == null || self.GetDistanceTo(Goal.X, Goal.Y) < height / 4)
            {
                ArrayList places = new ArrayList();
                for (int x = 1; x < width; x += width - 3)
                {
                    for (int y = 1; y < height; y += height - 3)
                    {
                        if (Goal == null || Goal.GetDistanceTo(x, y) > height / 3)
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

        Point IfNothing()
        {
            if (commander.Id == self.Id)
                return IfNothingCommander();
            int[] _i = { 1, 1, -1, -1, 2, -2, 0, 0, 0, 0, 1, -1 };
            int[] _j = { 1, -1, 1, -1, 0, 0, 2, -2, 1, -1, 0, 0 };
            Point bestPoint = Point.Inf;
            for (int k = 0; k < _i.Length; k++)
            {
                int ni = commander.X + _i[k];
                int nj = commander.Y + _j[k];
                if (ni >= 0 && nj >= 0 && ni < width && nj < height && map[ni, nj] == 0)
                {
                    double quality = 1.0 / getShoterPath(new Point(ni, nj), false);
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
