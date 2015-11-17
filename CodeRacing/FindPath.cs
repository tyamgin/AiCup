using System;
using System.Collections.Generic;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        private int[] _dx = { 0, 0, -1, 1 };
        private int[] _dy = { -1, 1, 0, 0 };

        private Cell _bfs(Cell start, Cell end)
        {
            return _bfs(start.I, start.J, end.I, end.J);
        }

        private bool _canPass(int i1, int j1, int i2, int j2)
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
            throw new Exception("something wrong");
        }

        private Cell _bfs(int startI, int startJ, int endI, int endJ)
        {
            var d = new int[world.Height, world.Width];
            var q = new Queue<int>();
            q.Enqueue(endI);
            q.Enqueue(endJ);
            for (var i = 0; i < world.Height; i++)
                for (var j = 0; j < world.Width; j++)
                    d[i, j] = Infinity;
            d[endI, endJ] = 0;
            while (q.Count > 0)
            {
                var i = q.Dequeue();
                var j = q.Dequeue();
                for (var k = 0; k < 4; k++)
                {
                    var ni = _dx[k] + i;
                    var nj = _dy[k] + j;
                    if (_canPass(i, j, ni, nj) && d[ni, nj] == Infinity)
                    {
                        d[ni, nj] = d[i, j] + 1;
                        q.Enqueue(ni);
                        q.Enqueue(nj);
                    }
                }
            }
            var dist = d[startI, startJ];
            if (dist == Infinity)
                throw new Exception("path not found");
            for (var k = 0; k < 4; k++)
            {
                var ni = _dx[k] + startI;
                var nj = _dy[k] + startJ;
                if (ni >= 0 && nj >= 0 && ni < world.Height && nj < world.Width && d[ni, nj] == dist - 1)
                {
                    return new Cell(ni, nj);
                }
            }
            throw new Exception("something wrong");
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
            var LX = c.X - margin;
            var RX = c.X + margin;
            var LY = c.Y - margin;
            var RY = c.Y + margin;
            
            // внутри квадрата
            if (LX <= p.X && p.X <= RX && LY <= p.Y && p.Y <= RY)
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
            if (p.X < LX)
                return !_tileFreeLeft(tileType);
            if (p.X > RX)
                return !_tileFreeRight(tileType);
            if (p.Y < LY)
                return !_tileFreeTop(tileType);
            if (p.Y > RY)
                return !_tileFreeBottom(tileType);

            throw new Exception("something wrong");
        }

        private static bool _intersectTail(Point p, double additionalMargin = SafeMargin)
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

        public static bool CheckVisibility(Car car, Point from, Point to)
        {
            var delta = 10.0;
            var c = (int)(from.GetDistanceTo(to) / delta + 2);
            delta = from.GetDistanceTo(to)/c;
            var dir = (to - from).Normalized();
            for (var i = 0; i <= c; i++)
            {
                var p = from + dir*(delta*i);
                if (_intersectTail(p, car.Height / 2 + 10))
                    return false;
            }
            return true;
        }

        public Points GetWaySegments(Car car)
        {
            var res = new Points();
            var myCell = GetCell(car.X, car.Y);
            res.Add(new Point(car));

            for (var e = 1; res.Count < 5; e++)
            {
                var nextWp = GetNextWayPoint(car, e);
                for (var cur = myCell; !cur.Equals(nextWp);)
                {
                    var cCell = _bfs(cur, nextWp);
                    var nxt = GetCenter(cCell);
                    while (res.Count > 1 && CheckVisibility(car, res[res.Count - 2], nxt))
                    {
                        res.Pop();
                    }
                    res.Add(nxt);
                    cur = cCell;
                }
                myCell = nextWp;
            }
            return res;
        }

        //public Points Closify(Points pts)
        //{
        //    for (var i = 2; i < pts.Count; i++)
        //    {
        //        pts[i - 1] = _closify(pts[i - 2], pts[i - 1], pts[i]);
        //    }
        //    return pts;
        //}

        //private Point _closify(Point a, Point b, Point c)
        //{
        //    Point corner = null;
        //    var cell = GetCell(b.X, b.Y);
        //    for (var i = 0; i < 2; i++)
        //    {
        //        for (var j = 0; j < 2; j++)
        //        {
        //            var p = new Point(game.TrackTileSize*(cell.J + j), game.TrackTileSize*(cell.I + i));
        //            if (new Points { a, b, c }.ContainPoint(p))
        //            {
        //                corner = p;
        //                break;
        //            }
        //        }
        //    }
        //    if (corner == null)
        //        return b;

        //    var dir = (corner - b).Normalized();
            
        //    double L = 0, R = b.GetDistanceTo(corner);
        //    for (var it = 0; it < 10; it++)
        //    {
        //        var m = (L + R)/2;
        //        var f = b + dir*m;
        //        if (!CheckVisibility(a, f) || !CheckVisibility(f, c))
        //            R = m;
        //        else
        //            L = m;
        //    }
        //    return b + dir*L;
        //}
    }
}
