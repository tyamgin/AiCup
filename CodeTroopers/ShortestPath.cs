using System;
using System.Collections.Generic;
using System.Data;
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
                bfsDist = new int[Width, Height];
                clearBfsDist = new int[2 * Width * Height + 1];
                for (int i = 0; i < Width; i++)
                    for (int j = 0; j < Height; j++)
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
                        if (nX >= 0 && nY >= 0 && nX < Width && nY < Height && map[nX, nY] == 0 && bfsDist[nX, nY] == Inf)
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
        Point GoToUnit(Point self, Point to, int[,] map, bool beginFree, bool endFree, ref int distance)
        {
            if (Equal(to, self))
                return to;
            q.Clear();
            q.Enqueue(to.X);
            q.Enqueue(to.Y);
            var d = new int[Width, Height];
            for (var i = 0; i < Width; i++)
                for (var j = 0; j < Height; j++)
                    d[i, j] = Inf;
            if (map[to.X, to.Y] != 0 && !endFree)
            {
                distance = Inf;
                return null;
            }
            var tmp = 0;
            if (beginFree)
            {
                tmp = map[self.X, self.Y];
                map[self.X, self.Y] = 0;
            }
            d[to.X, to.Y] = 0;
            while (q.Count != 0 && d[self.X, self.Y] >= Inf)
            {
                var x = q.Dequeue();
                var y = q.Dequeue();
                for (var k = 0; k < 4; k++)
                {
                    var n = new Point(x + _i[k], y + _j[k]);
                    if (n.X >= 0 && n.Y >= 0 && n.X < Width && n.Y < Height && map[n.X, n.Y] == 0 && d[n.X, n.Y] == Inf)
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
            // Если вариантов несколько - выбрать любой
            foreach (Point n in Nearest(self, map))
                if (d[n.X, n.Y] + 1 == d[self.X, self.Y])
                    return new Point(n.X, n.Y, 0);
            return new Point(0, 0, Inf);
        }

        Point GoToUnit(Trooper self, Unit unit, int[,] map, bool beginFree, bool endFree)
        {
            int distance = 0;
            return GoToUnit(new Point(self), new Point(unit), map, beginFree, endFree, ref distance);
        }

        int GetShoterPath(Trooper self, Unit unit, int[,] map, bool beginFree, bool endFree)
        {
            int distance = 0;
            GoToUnit(new Point(self), new Point(unit), map, beginFree, endFree, ref distance);
            return distance;
        }

        int GetShoterPath(Point from, Point to, int[,] map, bool beginFree, bool endFree)
        {
            int distance = 0;
            GoToUnit(from, to, map, beginFree, endFree, ref distance);
            return distance;
        }

        Point GoToUnit(Trooper self, Point to, int[,] map, bool beginFree, bool endFree)
        {
            int distance = 0;
            return GoToUnit(new Point(self), to, map, beginFree, endFree, ref distance);
        }

        int GetShoterPath(Trooper self, Point to, int[,] map, bool beginFree, bool endFree)
        {
            int distance = 0;
            GoToUnit(new Point(self), to, map, beginFree, endFree, ref distance);
            return distance;
        }

        private static int[, ,] sh;

        void fillDistanceToShoot(int shootingRange, int x, int y, int z, int[,,] result)
        {
            if (sh == null)
                sh = new int[Width, Height, 3];
            for (var i = 0; i < Width; i++)
            {
                for (var j = 0; j < Height; j++)
                {
                    for (var k = 0; k < 3; k++)
                    {
                        sh[i, j, k] = Inf;
                        result[i, j, k] = Inf;
                    }
                }
            }
            sh[x, y, z] = 0;
            q.Clear();
            q.Enqueue(x);
            q.Enqueue(y);
            q.Enqueue(z);
            while (q.Count != 0)
            {
                x = q.Dequeue();
                y = q.Dequeue();
                z = q.Dequeue();

                // mark visible cells
                for(var i = x - shootingRange; i <= x + shootingRange; i++)
                    for(var j = y - shootingRange; j <= y + shootingRange; j++)
                        if (i >= 0 && j >= 0 && i < Width && j < Height)
                            for(var k = 0; k < 3; k++)
                                if (sh[x, y, z] < result[i, j, k] && world.IsVisible(shootingRange, x, y, GetStance(z), i, j, GetStance(k)))
                                    result[i, j, k] = sh[x, y, z];

                // Change stance
                for (var dz = -1; dz <= 1; dz += 2)
                {
                    var nz = dz + z;
                    if (nz >= 0 && nz < 3 && sh[x, y, nz] == Inf)
                    {
                        sh[x, y, nz] = sh[x, y, z] + 1;
                        q.Enqueue(x);
                        q.Enqueue(y);
                        q.Enqueue(nz);
                    }
                }

                // change Position
                if (z == 2) // ходить можно только стоя
                {
                    for (var k = 0; k < 4; k++)
                    {
                        var nx = _i[k] + x;
                        var ny = _j[k] + y;
                        if (nx >= 0 && ny >= 0 && nx < Width && ny < Height && notFilledMap[nx, ny] == 0 && sh[nx, ny, z] == Inf)
                        {
                            sh[nx, ny, z] = sh[x, y, z] + 1;
                            q.Enqueue(nx);
                            q.Enqueue(ny);
                            q.Enqueue(z);
                        }
                    }
                }
            }
        }

        int GetDistanceToShoot(int fromShootingRange, int fromX, int fromY, int fromStance, int toX, int toY, int toStance)
        {
            return GetDistanceTo(fromShootingRange, fromX, fromY, fromStance, toX, toY, toStance);
        }

        int GetDistanceTo(int fromRange, int fromX, int fromY, int fromStance, int toX, int toY, int toStance)
        {
            if (Distance[fromRange] == null)
                Distance[fromRange] = new int[Width, Height, 3][,,];
            if (Distance[fromRange][fromX, fromY, fromStance] == null)
            {
                Distance[fromRange][fromX, fromY, fromStance] = new int[Width, Height, 3];
                fillDistanceToShoot(fromRange, fromX, fromY, fromStance,
                    Distance[fromRange][fromX, fromY, fromStance]);
            }
            return Distance[fromRange][fromX, fromY, fromStance][toX, toY, toStance];
        }
    }
}
