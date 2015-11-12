using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.ExceptionServices;
using System.Text;
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
                    if (ni >= 0 && nj >= 0 && ni < world.Height && nj < world.Width && tiles[ni, nj] != TileType.Empty && d[ni, nj] == Infinity)
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

        private bool _tileFreeLeft(TileType type)
        {
            return type == TileType.Crossroads ||
                   type == TileType.Horizontal ||
                   type == TileType.RightBottomCorner ||
                   type == TileType.RightTopCorner;
        }

        private bool _tileFreeRight(TileType type)
        {
            return type == TileType.Crossroads ||
                   type == TileType.Horizontal ||
                   type == TileType.LeftBottomCorner ||
                   type == TileType.LeftTopCorner;
        }

        private bool _tileFreeTop(TileType type)
        {
            return type == TileType.Crossroads ||
                   type == TileType.Vertical ||
                   type == TileType.LeftBottomCorner ||
                   type == TileType.RightBottomCorner;
        }

        private bool _tileFreeBottom(TileType type)
        {
            return type == TileType.Crossroads ||
                   type == TileType.Vertical ||
                   type == TileType.LeftTopCorner ||
                   type == TileType.RightTopCorner;
        }

        private bool _intersectTail(Point p, double additionalMargin = 0.0)
        {
            var cell = GetCell(p.X, p.Y);
            var tileType = tiles[cell.I, cell.J];
            if (tileType == TileType.Empty)
                return true;

            var c = GetCenter(cell);
            var margin = game.TrackTileSize/2 - game.TrackTileMargin - additionalMargin;//-self.Height / 2;
            var LX = c.X - margin;
            var RX = c.X + margin;
            var LY = c.Y - margin;
            var RY = c.Y + margin;
            
            // внутри квадрата
            if (LX <= p.X && p.X <= RX && LY <= p.Y && p.Y <= RY)
                return false;

            // в углу
            for(var k = 0; k < 4; k++)
                if (TileCorner[cell.I, cell.J, k].GetDistanceTo2(p) < game.TrackTileMargin*game.TrackTileMargin)
                    return true;

            // TODO: обработать T-образные

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

        private bool _visible(Point from, Point to)
        {
            var delta = 10.0;
            var c = (int)(from.GetDistanceTo(to) / delta + 2);
            delta = from.GetDistanceTo(to)/c;
            var dir = (to - from).Normalized();
            for (var i = 0; i <= c; i++)
            {
                var p = from + dir*(delta*i);
                if (_intersectTail(p, self.Height / 2))
                    return false;
            }
            return true;
        }

        public ArrayList GetSegments()
        {
            var res = new ArrayList();
            var myCell = GetCell(self.X, self.Y);
            res.Add(new Point(self));

            for (int e = 1; res.Count < 5; e++)
            {
                var nextWp = GetNextWayPoint(e);
                for (var cur = myCell; !cur.Equals(nextWp);)
                {
                    var cCell = _bfs(cur, nextWp);
                    var nxt = GetCenter(cCell);
                    while (res.Count > 1 && _visible(res[res.Count - 2] as Point, nxt))
                    {
                        res.RemoveAt(res.Count - 1);
                    }
                    res.Add(nxt);
                    cur = cCell;
                }
                myCell = nextWp;
            }

            //for (var i = 2; i < res.Count; i++)
            //{
            //    res[i - 1] = _closify(res[i - 2] as Point, res[i - 1] as Point, res[i] as Point);
            //}
            return res;
        }

        private Point _closify(Point a, Point b, Point c)
        {
            Point corner = null;
            var cell = GetCell(b.X, b.Y);
            for (var i = 0; i < 2; i++)
            {
                for (var j = 0; j < 2; j++)
                {
                    var p = new Point(game.TrackTileSize*(cell.J + j), game.TrackTileSize*(cell.I + i));
                    if (p.InTriangle(a, b, c))
                    {
                        corner = p;
                        break;
                    }
                }
            }
            if (corner == null)
                return b;

            var dir = (corner - b).Normalized();
            
            double L = 0, R = b.GetDistanceTo(corner);
            for (int it = 0; it < 10; it++)
            {
                double m = (L + R)/2;
                var f = b + dir*m;
                if (!_visible(a, f) || !_visible(f, c))
                    R = m;
                else
                    L = m;
            }
            return b + dir*L;
        }
    }
}
