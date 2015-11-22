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

        private static Cell _bfs(Cell start, Cell end, Cell[] forbidden)
        {
            return BfsNextCell(start.I, start.J, end.I, end.J, forbidden);
        }

        public static bool CanPass(int i1, int j1, int i2, int j2)
        {
            if (i2 < 0 || i2 >= world.Height || j2 < 0 || j2 >= world.Width)
                return false;

            if (i1 == i2)
            {
                if (j2 == j1 + 1) // go right
                    return _tileFreeRight(tiles[i1, j1]) && _tileFreeLeft(tiles[i2, j2]);
                if (j2 == j1 - 1)  // go left
                    return _tileFreeLeft(tiles[i1, j1]) && _tileFreeRight(tiles[i2, j2]);
            }
            else if (j1 == j2)
            {
                if (i2 == i1 + 1) // go bottom
                    return _tileFreeBottom(tiles[i1, j1]) && _tileFreeTop(tiles[i2, j2]);
                if (i2 == i1 - 1) // go top
                    return _tileFreeTop(tiles[i1, j1]) && _tileFreeBottom(tiles[i2, j2]);
            }
            throw new Exception("something wrong in _canPass");
        }

        private static int[,] _distMap;

        public static int BfsDist(int startI, int startJ, int endI, int endJ, Cell[] forbidden)
        {
            BfsNextCell(startI, startJ, endI, endJ, forbidden);
            return _distMap[startI, startJ];
        }

        public static Cell BfsNextCell(int startI, int startJ, int endI, int endJ, Cell[] forbidden)
        {
            if (_distMap == null)
                _distMap = new int[world.Height, world.Width];

            var q = new Queue<int>();
            q.Enqueue(endI);
            q.Enqueue(endJ);
            for (var i = 0; i < world.Height; i++)
                for (var j = 0; j < world.Width; j++)
                    _distMap[i, j] = Infinity;
            _distMap[endI, endJ] = 0;
            while (q.Count > 0)
            {
                var i = q.Dequeue();
                var j = q.Dequeue();
                for (var k = 0; k < 4; k++)
                {
                    var ni = _dx[k] + i;
                    var nj = _dy[k] + j;
                    if (CanPass(i, j, ni, nj) && _distMap[ni, nj] == Infinity && !forbidden.Any(x => x.Equals(ni, nj)))
                    {
                        _distMap[ni, nj] = _distMap[i, j] + 1;
                        q.Enqueue(ni);
                        q.Enqueue(nj);
                    }
                }
            }
            var dist = _distMap[startI, startJ];
            if (dist == Infinity)
                throw new Exception("path not found");
            for (var k = 0; k < 4; k++)
            {
                var ni = _dx[k] + startI;
                var nj = _dy[k] + startJ;
                if (CanPass(startI, startJ, ni, nj) && _distMap[ni, nj] == dist - 1)
                {
                    return new Cell(ni, nj);
                }
            }
            throw new Exception("something wrong in bfs");
        }

        private static bool _tileFreeLeft(TileType type)
        {
            return type == TileType.Crossroads ||
                   type == TileType.Horizontal ||
                   type == TileType.RightBottomCorner ||
                   type == TileType.RightTopCorner ||
                   type == TileType.LeftHeadedT ||
                   type == TileType.TopHeadedT ||
                   type == TileType.BottomHeadedT ||
                   type == TileType.Unknown; // TODO
        }

        private static bool _tileFreeRight(TileType type)
        {
            return type == TileType.Crossroads ||
                   type == TileType.Horizontal ||
                   type == TileType.LeftBottomCorner ||
                   type == TileType.LeftTopCorner ||
                   type == TileType.RightHeadedT ||
                   type == TileType.TopHeadedT ||
                   type == TileType.BottomHeadedT ||
                   type == TileType.Unknown; // TODO
        }

        private static bool _tileFreeTop(TileType type)
        {
            return type == TileType.Crossroads ||
                   type == TileType.Vertical ||
                   type == TileType.LeftBottomCorner ||
                   type == TileType.RightBottomCorner ||
                   type == TileType.LeftHeadedT ||
                   type == TileType.TopHeadedT ||
                   type == TileType.RightHeadedT ||
                   type == TileType.Unknown; // TODO
        }

        private static bool _tileFreeBottom(TileType type)
        {
            return type == TileType.Crossroads ||
                   type == TileType.Vertical ||
                   type == TileType.LeftTopCorner ||
                   type == TileType.RightTopCorner ||
                   type == TileType.LeftHeadedT ||
                   type == TileType.BottomHeadedT ||
                   type == TileType.RightHeadedT ||
                   type == TileType.Unknown; // TODO
        }

        private static BitSet[] _intersectTailCacheSafe;
        private static BitSet[] _intersectTailCacheSafeIsComputed;

        private static bool _intersectTailNoCache(Point p, double additionalMargin)
        {
            var cell = GetCell(p.X, p.Y);
            var tileType = tiles[cell.I, cell.J];
            if (tileType == TileType.Empty)
                return true;

            var c = GetCenter(cell);
            var margin = game.TrackTileSize/2 - game.TrackTileMargin - additionalMargin;
            var lx = c.X - margin;
            var rx = c.X + margin;
            var ly = c.Y - margin;
            var ry = c.Y + margin;
            
            // внутри квадрата
            if (lx <= p.X && p.X <= rx && ly <= p.Y && p.Y <= ry)
                return false;

            // в углу
            for (var k = 0; k < 4; k++)
            {
                if (TileCorner[cell.I, cell.J, k].GetDistanceTo2(p) <
                    (game.TrackTileMargin + additionalMargin)*(game.TrackTileMargin + additionalMargin))
                {
                    return true;
                }
            }

            // по бокам
            if (p.X < lx)
                return !_tileFreeLeft(tileType);
            if (p.X > rx)
                return !_tileFreeRight(tileType);
            if (p.Y < ly)
                return !_tileFreeTop(tileType);
            if (p.Y > ry)
                return !_tileFreeBottom(tileType);

            throw new Exception("something wrong");
        }

        public static bool IntersectTail(Point p, double additionalMargin = SafeMargin)
        {
            if (_intersectTailCacheSafe == null)
            {
                // TODO: if UNKNOWN?
                var w = (int) (MapWidth + Eps);
                var h = (int) (MapHeight + Eps);
                _intersectTailCacheSafe = new BitSet[w];
                _intersectTailCacheSafeIsComputed = new BitSet[w];
                for (var k = 0; k < w; k++)
                {
                    _intersectTailCacheSafe[k] = new BitSet(h);
                    _intersectTailCacheSafeIsComputed[k] = new BitSet(h);
                }
            }
            if (p.X < 0 || p.X >= MapWidth || p.Y < 0 || p.Y >= MapHeight)
                return true;

            if (Math.Abs(SafeMargin - additionalMargin) > Eps)
                return _intersectTailNoCache(p, additionalMargin);

            var i = (int) p.X;
            var j = (int) p.Y;
            if (!_intersectTailCacheSafeIsComputed[i][j])
            {
                _intersectTailCacheSafe[i][j] = _intersectTailNoCache(p, SafeMargin);
                _intersectTailCacheSafeIsComputed[i][j] = true;
            }
            return _intersectTailCacheSafe[i][j];
        }

        public delegate bool PointDelegate(Point point);

        public static bool PointsBetween(Point from, Point to, double maxDelta, PointDelegate callback)
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
            return PointsBetween(from, to, 10.0, point => !IntersectTail(point, car.Height / 2 + 10));
        }

        public static bool CheckVisibilityAndWp(Car car, Point from, Point to, List<Cell> wayPoints)
        {
            wayPoints = wayPoints.GetRange(0, wayPoints.Count);
            if (!PointsBetween(from, to, 10.0, point =>
            {
                if (IntersectTail(point, car.Height/2 + 5))
                    return false;
                var cell = GetCell(point);
                if (wayPoints.Count > 0 && cell.Equals(wayPoints[0]))
                    wayPoints.RemoveAt(0);

                return true;
            }))
            {
                return false;
            }
            return wayPoints.Count == 0;
        }

        public Points GetWaySegments(Car car)
        {
            var result = new Points();
            var myCell = GetCell(car.X, car.Y);
            result.Add(new Point(car));
            Cell prevCell = null;

            var passedWayPoints = new List<Cell>();

            for (var e = 1; result.Count < 8; e++)
            {
                var nextWp = GetNextWayPoint(car, e);
                for (var curCell = myCell; !curCell.Equals(nextWp); )
                {
                    var nextCell = _bfs(curCell, nextWp, prevCell == null ? new Cell[] { } : new[] { prevCell });
                    var nextCenter = GetCenter(nextCell);
                    for (var i = 0; i < result.Count; i++)
                    {
                        if (CheckVisibilityAndWp(car, result[i], nextCenter, passedWayPoints))
                        {
                            result.RemoveRange(i + 1, result.Count - i - 1);
                            break;
                        }
                    }
                    result.Add(nextCenter);
                    prevCell = curCell;
                    curCell = nextCell;
                }
                myCell = nextWp;
                passedWayPoints.Add(nextWp);
            }
            var extended = ExtendWaySegments(result, 100);
            result.Clear();
            
            foreach (var t in extended)
            {
                if (result.Count > 0 && result.Last().Equals(t))
                    continue;

                while(result.Count > 1 && CheckVisibility(car, result[result.Count - 2], t))
                    result.Pop();
                result.Add(t);
            }
            return result;
        }

        public static Points ExtendWaySegments(Points pts, double delta)
        {
            var res = new Points();

            for (var idx = 1; idx < pts.Count; idx++)
            {
                PointsBetween(pts[idx - 1], pts[idx], delta, point =>
                {
                    res.Add(point);
                    return true;
                });
            }
            return res;
        }

        //public Points old_GetWaySegments(Car car)
        //{
        //    var result = new Points();
        //    var isWayPoint = new List<bool>();
        //    var myCell = GetCell(car.X, car.Y);
        //    result.Add(new Point(car));
        //    isWayPoint.Add(false);
        //    Cell prevCell = null;

        //    for (var e = 1; result.Count < 8; e++)
        //    {
        //        var nextWp = GetNextWayPoint(car, e);
        //        for (var cur = myCell; !cur.Equals(nextWp);)
        //        {
        //            var cCell = _bfs(cur, nextWp, prevCell == null ? new Cell[] { } : new[] { prevCell });
        //            var nxt = GetCenter(cCell);
        //            while (result.Count > 1 && !isWayPoint[isWayPoint.Count - 1] && CheckVisibility(car, result[result.Count - 2], nxt))
        //            {
        //                result.Pop();
        //                isWayPoint.RemoveAt(isWayPoint.Count - 1);
        //            }
        //            result.Add(nxt);
        //            isWayPoint.Add(cCell.Equals(nextWp));
        //            prevCell = cur;
        //            cur = cCell;
        //        }
        //        myCell = nextWp;
        //    }
        //    return result;
        //}
    }
}
