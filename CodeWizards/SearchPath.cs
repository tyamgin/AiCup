using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Windows.Forms;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        private static readonly int[] _dx = { 0, 0, -1, 1, 1, 1, -1, -1 };
        private static readonly int[] _dy = { -1, 1, 0, 0, 1, -1, 1, -1 };

        private static bool[,] _isLocked;
        private static double[,] _distMap;
        private static Cell[,] _distPrev;
        private static Point[,] _points;
        private static List<Cell>[,] _neighbours; 
        public static int GridSize = 40;
        public static double CellLength;
        public static double CellDiagLength;

        private static int _prevTreesSize = 0;
        private static int _prevBuildingsSize = 0;

        public static List<Point> GetFreePoints()
        {
            var res = new List<Point>();
            for (var i = 0; i <= GridSize; i++)
            {
                for (var j = 0; j <= GridSize; j++)
                {
                    if (!_isLocked[i, j])
                    {
                        res.Add(_points[i, j]);
                    }
                }
            }
            return res;
        }

        private void _calculateNeighbours(int i, int j)
        {
            _isLocked[i, j] = false;
            _neighbours[i, j] = new List<Cell>();
            for (var k = 0; k < _dx.Length; k++)
            {
                var ni = i + _dx[k];
                var nj = j + _dy[k];
                if (ni >= 0 && nj >= 0 && ni <= GridSize && nj <= GridSize)
                {
                    _neighbours[i, j].Add(new Cell(ni, nj));
                }
            }
        }

        private void _filterNeighbours(int I, int J)
        {
            if (_isLocked[I, J])
                return;

            var list = _neighbours[I, J];
            var point = _points[I, J];

            for (var idx = 0; idx < list.Count; idx++)
            {
                var pt = _points[list[idx].I, list[idx].J];
                var remove = false;
                for (var k = _prevTreesSize; k < TreesObserver.Trees.Count; k++)
                {
                    var tree = TreesObserver.Trees[k];
                    
                    if (point.GetDistanceTo2(tree) < Geom.Sqr(tree.Radius + Self.Radius))
                    {
                        _isLocked[I, J] = true;
                        return;
                    }
                    if (Geom.SegmentCircleIntersect(point, pt, tree, tree.Radius + Self.Radius).Length > 0)
                    {
                        remove = true;
                        break;
                    }
                }


                for (var k = _prevBuildingsSize; k < BuildingsObserver.Buildings.Count; k++)
                {
                    var building = BuildingsObserver.Buildings[k];

                    if (point.GetDistanceTo2(building) < Geom.Sqr(building.Radius + Self.Radius))
                    {
                        _isLocked[I, J] = true;
                        return;
                    }
                    if (Geom.SegmentCircleIntersect(point, pt, building, building.Radius + Self.Radius).Length > 0)
                    {
                        remove = true;
                        break;
                    }
                }
                if (remove)
                {
                    list.RemoveAt(idx);
                    idx--;
                }
            }

        }

        private void InitializeDijkstra()
        {
            if (_distMap == null)
            {
                CellLength = Const.Width/GridSize;
                CellDiagLength = CellLength*Math.Sqrt(2);

                _neighbours = new List<Cell>[GridSize + 1, GridSize + 1];
                _isLocked = new bool[GridSize + 1, GridSize + 1];
                _distMap = new double[GridSize + 1, GridSize + 1];
                _distPrev = new Cell[GridSize + 1, GridSize + 1];
                _points = new Point[GridSize + 1, GridSize + 1];
                for (var i = 0; i <= GridSize; i++)
                {
                    for (var j = 0; j <= GridSize; j++)
                    {
                        _points[i, j] = new Point(Const.Width/GridSize*i, Const.Height/GridSize*j);

                        _calculateNeighbours(i, j);
                    }
                }
            }

            for (var i = 0; i <= GridSize; i++)
            {
                for (var j = 0; j <= GridSize; j++)
                {
                    _filterNeighbours(i, j);
                }
            }

            _prevTreesSize = TreesObserver.Trees.Count;

        }

        static Cell FindNearestCell(Point point)
        {
            double 
                dx = Const.Width/GridSize,
                dy = Const.Height/GridSize;

            var I = (int) (point.X/dx + Const.Eps);
            var J = (int) (point.Y/dy + Const.Eps);
            int seldI = -1, seldJ = -1;
            double minDist = int.MaxValue;

            for (var di = 0; di < 2; di++)
            {
                for (var dj = 0; dj < 2; dj++)
                {
                    var dst = _points[I + di, J + dj].GetDistanceTo2(point);
                    if (dst < minDist && !_isLocked[I + di, J + dj])
                    {
                        minDist = dst;
                        seldI = di;
                        seldJ = dj;
                    }
                }
            }

            return new Cell(I + seldI, J + seldJ);
        }

        static bool CanPassToCell(Cell from, Cell cell)
        {
            return !_isLocked[cell.I, cell.J];
        }

        static double GetDist(Cell from, Cell to)
        {
            return Math.Abs(from.I - to.I) + Math.Abs(from.J - to.J) <= 1
                ? CellLength
                : CellDiagLength;
        }

        public static List<Cell> DijkstraFindPath(Cell start, Cell end)
        {
            var q = new PriorityQueue<Pair<double, Cell>>();
            q.Push(new Pair<double, Cell>(0.0, start));
            for (var i = 0; i <= GridSize; i++)
            {
                for (var j = 0; j <= GridSize; j++)
                {
                    _distMap[i, j] = Infinity;
                    _distPrev[i, j] = null;
                }
            }

            _distMap[start.I, start.J] = 0;
            while (q.Count > 0)
            {
                var cur = q.Top().Second;
                var minDist = -q.Top().First;
                q.Pop();

                if (minDist > _distMap[cur.I, cur.J])
                    continue;

                foreach(var to in _neighbours[cur.I, cur.J])
                {
                    if (!CanPassToCell(cur, to))
                        continue;

                    var distTo = _distMap[cur.I, cur.J] + GetDist(cur, to);
                    if (distTo < _distMap[to.I, to.J])
                    {
                        _distMap[to.I, to.J] = distTo;
                        _distPrev[to.I, to.J] = cur;
                        q.Push(new Pair<double, Cell>(-distTo, to));
                    }
                };
            }

            if (_distPrev[end.I, end.J] == null)
                throw new Exception("path not found");
            
            var res = new List<Cell>();
            var c = end.Clone();
            do
            {
                res.Add(c.Clone());

                if (c.Equals(start))
                    break;

                c = _distPrev[c.I, c.J];
            } while (true);

            res.Reverse();
            return res;
        }

        public static List<Point> DijkstraFindPath(Point start, Point end)
        {
            var startCell = FindNearestCell(start);
            var endCell = FindNearestCell(end);
            var cellsPath = DijkstraFindPath(startCell, endCell);
            var res = new List<Point> {start};
            foreach (var cell in cellsPath)
            {
                res.Add(_points[cell.I, cell.J]);
            }
            res.Add(end);
            return res;
        }

    }
}
