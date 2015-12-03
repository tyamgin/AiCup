using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        private static readonly int[] _dx = { 0, 0, -1, 1 };
        private static readonly int[] _dy = { -1, 1, 0, 0 };

        public delegate void CellCallback(Cell cell);

        public static void EnumerateNeigbours(Cell cell, CellCallback callback)
        {
            for (var k = 0; k < 4; k++)
            {
                var ni = cell.I + _dx[k];
                var nj = cell.J + _dy[k];
                if (ni >= 0 && nj >= 0 && ni < world.Height && nj < world.Width)
                    callback(new Cell(ni, nj));
            }
        }

        private static Cell DijkstraNextCell(Cell start, Cell end, Cell[] forbidden)
        {
            return DijkstraNextCell(start.I, start.J, end.I, end.J, forbidden);
        }

        public static bool CanPass(int i1, int j1, int i2, int j2)
        {
            if (i2 < 0 || i2 >= world.Height || j2 < 0 || j2 >= world.Width)
                return false;

            if (i1 == i2)
            {
                if (j2 == j1 + 1) // go right
                    return MyTiles[i1, j1].IsFreeRight && MyTiles[i2, j2].IsFreeLeft;
                if (j2 == j1 - 1)  // go left
                    return MyTiles[i1, j1].IsFreeLeft && MyTiles[i2, j2].IsFreeRight;
            }
            else if (j1 == j2)
            {
                if (i2 == i1 + 1) // go bottom
                    return MyTiles[i1, j1].IsFreeBottom && MyTiles[i2, j2].IsFreeTop;
                if (i2 == i1 - 1) // go top
                    return MyTiles[i1, j1].IsFreeTop && MyTiles[i2, j2].IsFreeBottom;
            }
            throw new Exception("something wrong in CanPass(" + i1 + ", " + j1 + ", " + i2 + ", " + j2 + ")");
        }

        private static double[,] _distMap;
        private static int[,] _bfsDistMap;
        private static Cell[,] _distPrev;

        public static double GetCost(Cell cell)
        {
            var tile = MyTiles[cell.I, cell.J];
            return Math.Max(0.01, 1 + tile.Weight);
        }

        public static double BfsDist(int startI, int startJ, int endI, int endJ, Cell[] forbidden)
        {
            if (_bfsDistMap == null)
                _bfsDistMap = new int[world.Height, world.Width];

            var q = new Queue<int>();
            q.Enqueue(endI);
            q.Enqueue(endJ);
            
            for (var i = 0; i < world.Height; i++)
                for (var j = 0; j < world.Width; j++)
                    _bfsDistMap[i, j] = Infinity;

            _bfsDistMap[endI, endJ] = 0;
            while (q.Count > 0)
            {
                var curI = q.Dequeue();
                var curJ = q.Dequeue();
                var cur = new Cell(curI, curJ);

                EnumerateNeigbours(cur, to =>
                {
                    if (!CanPass(cur.I, cur.J, to.I, to.J) || forbidden.Any(x => x.Equals(to.I, to.J)))
                        return;

                    var distTo = _bfsDistMap[cur.I, cur.J] + 1;
                    if (distTo < _bfsDistMap[to.I, to.J])
                    {
                        _bfsDistMap[to.I, to.J] = distTo;
                        q.Enqueue(to.I);
                        q.Enqueue(to.J);
                    }
                });
            }

            if (_bfsDistMap[startI, startJ] == Infinity)
            {
                if (forbidden.Length == 0)
                    throw new Exception("bfs path not found");
                return BfsDist(startI, startJ, endI, endJ, new Cell[] {});
            }

            return _bfsDistMap[startI, startJ];
        }

        public static Cell DijkstraNextCell(int startI, int startJ, int endI, int endJ, Cell[] forbidden)
        {
            if (_distMap == null)
            {
                _distMap = new double[world.Height, world.Width];
                _distPrev = new Cell[world.Height, world.Width];
            }

            var q = new PriorityQueue<Pair<double, Cell>>();
            q.Push(new Pair<double, Cell>(0.0, new Cell(endI, endJ)));
            for (var i = 0; i < world.Height; i++)
                for (var j = 0; j < world.Width; j++)
                    _distMap[i, j] = Infinity;
            _distMap[endI, endJ] = 0;
            while (q.Count > 0)
            {
                var cur = q.Top().Second;
                var minDist = -q.Top().First;
                q.Pop();

                if (minDist > _distMap[cur.I, cur.J])
                    continue;

                EnumerateNeigbours(cur, to =>
                {
                    if (!CanPass(cur.I, cur.J, to.I, to.J) || forbidden.Any(x => x.Equals(to.I, to.J)))
                        return;

                    var distTo = _distMap[cur.I, cur.J] + GetCost(to);
                    if (distTo < _distMap[to.I, to.J])
                    {
                        _distMap[to.I, to.J] = distTo;
                        _distPrev[to.I, to.J] = cur;
                        q.Push(new Pair<double, Cell>(-distTo, to));
                    }
                });
            }

            if (_distPrev[startI, startJ] == null)
            {
                if (forbidden.Length == 0)
                    throw new Exception("path not found");
                return DijkstraNextCell(startI, startJ, endI, endJ, new Cell[] {});
            }

            return _distPrev[startI, startJ];
        }

        public static double CellDistance(Point from, Point to)
        {
            var cellFrom = GetCell(from);
            var cellTo = GetCell(to);
            return cellFrom.Equals(cellTo)
                ? 0
                : BfsDist(cellFrom.I, cellFrom.J, cellTo.I, cellTo.J, new Cell[] {});
        }

        public static bool IntersectTail(Point p, double additionalMargin)
        {
            if (p.X < 0 || p.X >= Const.MapWidth || p.Y < 0 || p.Y >= Const.MapHeight)
                return true;

            var cell = GetCell(p.X, p.Y);
            var tile = MyTiles[cell.I, cell.J];
            if (tile.Type == TileType.Empty)
                return true;

            var c = GetCenter(cell);
            var margin = Const.TileSize / 2 - Const.TileMargin;
            var lx = c.X - margin;
            var rx = c.X + margin;
            var ly = c.Y - margin;
            var ry = c.Y + margin;

            // внутри квадрата
            if (lx <= p.X && p.X <= rx && ly <= p.Y && p.Y <= ry) // optimization
                return false;

            var cornerDist2 = Geom.Sqr(Const.TileMargin + Math.Max(-5, additionalMargin));

            // в углу
            foreach (var corner in MyTiles[cell.I, cell.J].Parts)
            {
                if (corner.Type != TilePartType.Circle)
                    continue;
                if (corner.Circle.GetDistanceTo2(p) < cornerDist2)
                    return true;
            }

            // по бокам
            if (p.X < lx + additionalMargin && !tile.IsFreeLeft)
                return true;
            if (p.X > rx - additionalMargin && !tile.IsFreeRight)
                return true;
            if (p.Y < ly + additionalMargin && !tile.IsFreeTop)
                return true;
            if (p.Y > ry - additionalMargin && !tile.IsFreeBottom)
                return true;

            return false;
        }

        public delegate bool PointDelegate(Point point);

        public static bool EnumeratePointsBetween(Point from, Point to, double maxDelta, PointDelegate callback)
        {
            if (from.Equals(to))
                return callback(from.Clone());

            var c = (int)(from.GetDistanceTo(to) / maxDelta + 2);
            maxDelta = from.GetDistanceTo(to) / c;
            var dir = (to - from).Normalized();
            for (var i = 0; i <= c; i++)
            {
                // from + dir * (maxDelta * i)
                if (!callback(new Point(from.X + dir.X * maxDelta * i, from.Y + dir.Y * maxDelta * i)))
                    return false;
            }
            return true;
        }

        public static bool CheckVisibility(Car car, Point from, Point to, double width)
        {
            return EnumeratePointsBetween(from, to, 10.0, point => !IntersectTail(point, width));
        }

        public Points GetWaySegments(Car car)
        {
            var result = new Points();
            var isWayPoint = new List<bool>();
            var myCell = GetCell(car.X, car.Y);
            result.Add(new Point(car));
            isWayPoint.Add(false);
            Cell prevCell = null;

            for (var e = 1; result.Count < 5; e++)
            {
                var nextWp = GetNextWayPoint(car, e);
                for (var curCell = myCell; !curCell.Equals(nextWp);)
                {
                    var nextCell = DijkstraNextCell(curCell, nextWp, prevCell == null ? new Cell[] { } : new[] { prevCell });
                    var nextPoint = GetCenter(nextCell);
                    while (result.Count > 1 && !isWayPoint[isWayPoint.Count - 1] && CheckVisibility(car, result[result.Count - 2], nextPoint, car.Height / 2 + 10))
                    {
                        result.Pop();
                        isWayPoint.RemoveAt(isWayPoint.Count - 1);
                    }
                    result.Add(nextPoint);
                    isWayPoint.Add(nextCell.Equals(nextWp));
                    prevCell = curCell;
                    curCell = nextCell;
                }
                myCell = nextWp;
            }
            return result;
        }

        public static Points ExtendWaySegments(Points pts, double delta)
        {
            var res = new Points();

            for (var idx = 1; idx < pts.Count; idx++)
            {
                EnumeratePointsBetween(pts[idx - 1], pts[idx], delta, point =>
                {
                    res.Add(point);
                    return true;
                });
            }
            return res;
        }
    }
}
