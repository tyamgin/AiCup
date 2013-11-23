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
        private static Queue<int> q = new Queue<int>();
        private static int[,] bfsDist;
        private static int[] clearBfsDist;
        private static int clearBfsDistSize = 0;

        ArrayList FastBfs(int fromX, int fromY, int length, int[,] map, Point[] filled)
        {
            q.Clear();
            q.Enqueue(fromX);
            q.Enqueue(fromY);
            if (bfsDist == null)
            {
                bfsDist = new int[width, height];
                clearBfsDist = new int[2 * width * height + 1];
                for (int i = 0; i < width; i++)
                    for (int j = 0; j < height; j++)
                        bfsDist[i, j] = Inf;
            }
            for (int i = 0; i < clearBfsDistSize; i += 2)
                bfsDist[clearBfsDist[i], clearBfsDist[i + 1]] = Inf;
            clearBfsDistSize = 0;
            foreach (var p in filled)
                map[p.X, p.Y] = 1;
            
            bfsDist[fromX, fromY] = 0;
            clearBfsDist[clearBfsDistSize++] = fromX;
            clearBfsDist[clearBfsDistSize++] = fromY;
            var result = new ArrayList();
            while (q.Count != 0)
            {
                int x = q.Dequeue();
                int y = q.Dequeue();
                result.Add(new Point(x, y, bfsDist[x, y]));
                if (bfsDist[x, y] < length)
                {
                    for (int k = 0; k < 4; k++)
                    {
                        var nX = x + _i[k];
                        var nY = y + _j[k];
                        if (nX >= 0 && nY >= 0 && nX < width && nY < height && map[nX, nY] == 0 && bfsDist[nX, nY] == Inf)
                        {
                            bfsDist[nX, nY] = bfsDist[x, y] + 1;
                            clearBfsDist[clearBfsDistSize++] = nX;
                            clearBfsDist[clearBfsDistSize++] = nY;
                            q.Enqueue(nX);
                            q.Enqueue(nY);
                        }
                    }
                }
            }
            foreach (var p in filled)
                map[p.X, p.Y] = 0;
            return result;
        }


        // Указывает путь на to.
        Point goToUnit(Trooper self, Point to, int[,] map, bool beginFree, bool endFree, ref int distance)
        {
            if (Equal(to, self))
                return to;
            q.Clear();
            q.Enqueue(to.X);
            q.Enqueue(to.Y);
            int[,] d = new int[width, height];
            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++)
                    d[i, j] = Inf;
            if (map[to.X, to.Y] != 0 && !endFree)
            {
                distance = Inf;
                return null;
            }
            int tmp = 0;
            if (beginFree)
            {
                tmp = map[self.X, self.Y];
                map[self.X, self.Y] = 0;
            }
            d[to.X, to.Y] = 0;
            while (q.Count != 0 && d[self.X, self.Y] >= Inf)
            {
                int x = (int)q.Dequeue();
                int y = (int)q.Dequeue();
                for (int k = 0; k < 4; k++)
                {
                    Point n = new Point(x + _i[k], y + _j[k]);
                    if (n.X >= 0 && n.Y >= 0 && n.X < width && n.Y < height && map[n.X, n.Y] == 0 && d[n.X, n.Y] == Inf)
                    {
                        d[n.X, n.Y] = d[x, y] + 1;
                        q.Enqueue(n.X);
                        q.Enqueue(n.Y);
                    }
                }
            }
            distance = d[self.X, self.Y];
            if (beginFree)
                map[self.X, self.Y] = tmp;
            if (distance >= Inf)
                return null;
            Point bestTurn = new Point(0, 0, Inf);
            // Если вариантов несколько - выбрать где будет меньше радиус
            foreach (Point n in Nearest(self, map))
            {
                double radius = getTeamRadius(self.Id, n);
                Point point = new Point(n.X, n.Y, radius);
                if (d[n.X, n.Y] + 1 == d[self.X, self.Y] && bestTurn.profit > radius)
                    bestTurn = point;
            }
            return bestTurn;
        }

        Point goToUnit(Trooper self, Unit unit, int[,] map, bool beginFree, bool endFree)
        {
            int distance = 0;
            return goToUnit(self, new Point(unit), map, beginFree, endFree, ref distance);
        }

        int getShoterPath(Trooper self, Unit unit, int[,] map, bool beginFree, bool endFree)
        {
            int distance = 0;
            goToUnit(self, new Point(unit), map, beginFree, endFree, ref distance);
            return distance;
        }

        Point goToUnit(Trooper self, Point to, int[,] map, bool beginFree, bool endFree)
        {
            int distance = 0;
            return goToUnit(self, to, map, beginFree, endFree, ref distance);
        }

        int getShoterPath(Trooper self, Point to, int[,] map, bool beginFree, bool endFree)
        {
            int distance = 0;
            goToUnit(self, to, map, beginFree, endFree, ref distance);
            return distance;
        }
    }
}
