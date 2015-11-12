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

        private bool _intersectTail(Point p)
        {
            var cell = GetCell(p.X, p.Y);
            var tileType = tiles[cell.I, cell.J];
            if (tileType == TileType.Empty)
                return true;

            var c = GetCenter(cell);
            var margin = game.TrackTileSize/2 - game.TrackTileMargin - self.Height/2;
            var LX = c.X - margin;
            var RX = c.X + margin;
            var LY = c.Y - margin;
            var RY = c.Y + margin;
            
            if (LX <= p.X && p.X <= RX && LY <= p.Y && p.Y <= RY)
                return false;

            if (p.X < LX)
                return p.Y < LY || p.Y > RY || !_tileFreeLeft(tileType);
            if (p.X > RX)
                return p.Y < LY || p.Y > RY || !_tileFreeRight(tileType);
            if (p.Y < LY)
                return p.X < LX || p.X > RX || !_tileFreeTop(tileType);
            if (p.Y > RY)
                return p.X < LX || p.X > RX || !_tileFreeBottom(tileType);

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
                if (_intersectTail(p))
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
                for (var cur = myCell; !cur.Same(nextWp);)
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

    public class Pair<TFirst, TSecond> : IComparable<Pair<TFirst, TSecond>>
        where TFirst : IComparable<TFirst>
        where TSecond : IComparable<TSecond>
    {
        public TFirst First;
        public TSecond Second;

        public int CompareTo(Pair<TFirst, TSecond> other)
        {
            if (First.CompareTo(other.First) == 0)
                return Second.CompareTo(other.Second);
            return First.CompareTo(other.First);
        }

        public Pair(TFirst first, TSecond second)
        {
            this.First = first;
            this.Second = second;
        }

        public override string ToString()
        {
            return "(" + First + "; " + Second + ")";
        }
    }

    public class Tuple<TFirst, TSecond, TThird> : Pair<TFirst, TSecond>
        where TFirst : IComparable<TFirst>
        where TSecond : IComparable<TSecond>
        where TThird : IComparable<TThird>
    {
        public TThird Third;

        public Tuple(TFirst first, TSecond second, TThird third)
            : base(first, second)
        {
            this.Third = third;
        }

        public int CompareTo(Tuple<TFirst, TSecond, TThird> other)
        {
            if (First.CompareTo(other.First) != 0)
                return First.CompareTo(other.First);
            if (Second.CompareTo(other.Second) != 0)
                return Second.CompareTo(other.Second);
            return Third.CompareTo(other.Third);
        }

        public override string ToString()
        {
            return "(" + First + "; " + Second + "; " + Third + ")";
        }
    }

    public class PriorityQueue<T>
    {
        private readonly List<T> _data = new List<T>();
        private readonly IComparer<T> _comparer;

        public PriorityQueue()
        {
            _comparer = Comparer<T>.Default;
        }

        public PriorityQueue(IComparer<T> comparer)
        {
            if (comparer == null)
            {
                _comparer = Comparer<T>.Default;
                return;
            }
            _comparer = comparer;
        }

        public int Count
        {
            get { return _data.Count; }
        }

        public bool Empty()
        {
            return Count == 0;
        }

        public T Top()
        {
            return _data[0];
        }

        public void Push(T item)
        {
            _data.Add(item);
            var curPlace = Count;
            while (curPlace > 1 && _comparer.Compare(item, _data[curPlace / 2 - 1]) > 0)
            {
                _data[curPlace - 1] = _data[curPlace / 2 - 1];
                _data[curPlace / 2 - 1] = item;
                curPlace /= 2;
            }
        }

        public void Pop()
        {
            _data[0] = _data[Count - 1];
            _data.RemoveAt(Count - 1);
            var curPlace = 1;
            while (true)
            {
                var max = curPlace;
                if (Count >= curPlace * 2 && _comparer.Compare(_data[max - 1], _data[2 * curPlace - 1]) < 0)
                    max = 2 * curPlace;
                if (Count >= curPlace * 2 + 1 && _comparer.Compare(_data[max - 1], _data[2 * curPlace]) < 0)
                    max = 2 * curPlace + 1;
                if (max == curPlace) break;
                var item = _data[max - 1];
                _data[max - 1] = _data[curPlace - 1];
                _data[curPlace - 1] = item;
                curPlace = max;
            }
        }
    }
}
