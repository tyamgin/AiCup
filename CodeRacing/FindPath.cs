using System;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        private static readonly int[] _dx = { 0, 0, -1, 1 };
        private static readonly int[] _dy = { -1, 1, 0, 0 };

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
        private static Cell[,] _distPrev;

        public static double GetCost(Cell cell)
        {
            //TODO
            return 1;
        }

        public static double DijkstraDist(int startI, int startJ, int endI, int endJ, Cell[] forbidden)
        {
            DijkstraNextCell(startI, startJ, endI, endJ, forbidden);
            return _distMap[startI, startJ];
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
                
                for (var k = 0; k < 4; k++)
                {
                    var to = new Cell(_dx[k] + cur.I, _dy[k] + cur.J);
                    var distTo = _distMap[cur.I, cur.J] + GetCost(to);
                    if (CanPass(cur.I, cur.J, to.I, to.J) && !forbidden.Any(x => x.Equals(to.I, to.J)) && distTo < _distMap[to.I, to.J])
                    {
                        _distMap[to.I, to.J] = distTo;
                        _distPrev[to.I, to.J] = cur;
                        q.Push(new Pair<double, Cell>(-distTo, to));
                    }
                }
            }

            if (_distPrev[startI, startJ] == null)
                throw new Exception("path not found");

            return _distPrev[startI, startJ];
        }

        public static double CellDistance(Point from, Point to)
        {
            var cellFrom = GetCell(from);
            var cellTo = GetCell(to);
            return cellFrom.Equals(cellTo)
                ? 0
                : DijkstraDist(cellFrom.I, cellFrom.J, cellTo.I, cellTo.J, new Cell[] {});
        }

        public static bool IntersectTail(Point p, double additionalMargin)
        {
            if (p.X < 0 || p.X >= MapWidth || p.Y < 0 || p.Y >= MapHeight)
                return true;

            var cell = GetCell(p.X, p.Y);
            var tile = MyTiles[cell.I, cell.J];
            if (tile.Type == TileType.Empty)
                return true;

            var c = GetCenter(cell);
            var margin = game.TrackTileSize / 2 - game.TrackTileMargin - additionalMargin;
            var lx = c.X - margin;
            var rx = c.X + margin;
            var ly = c.Y - margin;
            var ry = c.Y + margin;

            // внутри квадрата
            if (lx <= p.X && p.X <= rx && ly <= p.Y && p.Y <= ry)
                return false;

            var cornerDist2 = Geom.Sqr(game.TrackTileMargin + additionalMargin);

            // в углу
            foreach (var corner in MyTiles[cell.I, cell.J].Parts)
            {
                if (corner.Type != TilePartType.Circle)
                    continue;
                if (corner.Circle.GetDistanceTo2(p) < cornerDist2)
                    return true;
            }

            // по бокам
            if (p.X < lx && !tile.IsFreeLeft)
                return true;
            if (p.X > rx && !tile.IsFreeRight)
                return true;
            if (p.Y < ly && !tile.IsFreeTop)
                return true;
            if (p.Y > ry && !tile.IsFreeBottom)
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

        public static bool CheckVisibility(Car car, Point from, Point to)
        {
            return EnumeratePointsBetween(from, to, 10.0, point => !IntersectTail(point, car.Height / 2 + 10));
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
                    while (result.Count > 1 && !isWayPoint[isWayPoint.Count - 1] && CheckVisibility(car, result[result.Count - 2], nextPoint))
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
