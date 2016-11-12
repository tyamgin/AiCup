using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
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
        public static int GridSize = 60;
        public static double CellLength;
        public static double CellDiagLength;

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

        private IEnumerable<ACircularUnit> _getStaticUnits()
        {
            return TreesObserver.Trees.Cast<ACircularUnit>()
                .Concat(BuildingsObserver.Buildings);
        }

        private List<ACircularUnit> _getNewStaticUnits()
        {
            return TreesObserver.NewTrees.Cast<ACircularUnit>()
                .Concat(BuildingsObserver.NewBuildings)
                .ToList();
        }

        private List<ACircularUnit> _getDisappearedStaticUnits()
        {
            return TreesObserver.DisappearedTrees.Cast<ACircularUnit>()
                .Concat(BuildingsObserver.DisappearedBuildings)
                .ToList();
        }

        private void _filterNeighbours(int I, int J, ACircularUnit unit)
        {
            var list = _neighbours[I, J];
            var point = _points[I, J];

            if (point.GetDistanceTo2(unit) < Geom.Sqr(unit.Radius + Self.Radius))
                _isLocked[I, J] = true;//TODO maybe return

            for (var idx = list.Count - 1; idx >= 0; idx--)
            {
                var pt = _points[list[idx].I, list[idx].J];
                
                if (Geom.SegmentCircleIntersects(point, pt, unit, unit.Radius + Self.Radius))
                {
                    list.RemoveAt(idx);
                }
            }
        }

        /**
         * Может-ли исчезновение unit повлиять на появление соседей у p
         */
        private bool _canAffect(ACircularUnit unit, Point p)
        {
            return unit.GetDistanceTo2(p) < Geom.Sqr(2*CellDiagLength + unit.Radius);
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

            foreach (var newUnit in _getNewStaticUnits())
            {
                for (var i = 0; i <= GridSize; i++)
                {
                    for (var j = 0; j <= GridSize; j++)
                    {
                        if (!_canAffect(newUnit, _points[i, j]))
                            continue;

                        _filterNeighbours(i, j, newUnit);
                    }
                }
            }

            foreach (var oldUnit in _getDisappearedStaticUnits())
            {
                var nearest = _getStaticUnits()
                    .Where(x => x.GetDistanceTo2(oldUnit) < Geom.Sqr(CellDiagLength*2))
                    .ToArray();

                for (var i = 0; i <= GridSize; i++)
                {
                    for (var j = 0; j <= GridSize; j++)
                    {
                        if (!_canAffect(oldUnit, _points[i, j]))
                            continue;

                        _calculateNeighbours(i, j);
                        foreach (var n in nearest)
                            _filterNeighbours(i, j, n);
                    }
                }
            }

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

        private static ACombatUnit[] _obstacles;
        private static double _selfRadius;

        private static bool CanPassToCell(Cell from, Cell cell)
        {
            if (_isLocked[cell.I, cell.J])
                return false;
            return
                _obstacles.All(
                    ob =>
                        !Geom.SegmentCircleIntersects(_points[from.I, from.J], _points[cell.I, cell.J], ob,
                            ob.Radius + _selfRadius));
        }

        static double GetDist(Cell from, Cell to)
        {
            return Math.Abs(from.I - to.I) + Math.Abs(from.J - to.J) <= 1
                ? CellLength
                : CellDiagLength;
        }

        public delegate bool DijkstraStopFunc(Cell pos);

        public static void DijkstraStart(Cell start, DijkstraStopFunc condition)
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

                if (condition(cur))
                {
                    break;
                }

                foreach(var to in _neighbours[cur.I, cur.J])
                {
                    var distTo = _distMap[cur.I, cur.J] + GetDist(cur, to);
                    if (distTo < _distMap[to.I, to.J] && CanPassToCell(cur, to))
                    {
                        _distMap[to.I, to.J] = distTo;
                        _distPrev[to.I, to.J] = cur;
                        q.Push(new Pair<double, Cell>(-distTo, to));
                    }
                };
            }
        }

        public static List<Cell> DijkstraGeneratePath(Cell start, Cell end)
        {
            if (start.Equals(end))
                return new List<Cell>();

            if (_distPrev[end.I, end.J] == null)
            {
                // path not found
                return null;
            }

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

        public static List<Point> DijkstraFindPath(ACombatUnit start, ACircularUnit target)
        {
            _selfRadius = start.Radius;
            _obstacles = Combats
                .Where(x => x.IsTeammate && x.Id != start.Id && x.GetDistanceTo2(start) < Geom.Sqr(start.VisionRange))
                .ToArray();

            var startCell = FindNearestCell(start);
            Cell endCell = null;
            DijkstraStart(startCell, cell =>
            {
                var pos = _points[cell.I, cell.J];
                //if (pos.GetDistanceTo(target) /*- target.Radius*/ < start.CastRange)
                if (pos.GetDistanceTo2(target) < Geom.Sqr(start.CastRange /*+ target.Radius*/))
                {
                    endCell = cell;
                    return true;
                }
                return false;
            });
            if (endCell == null)
                return null;
            var cellsPath = DijkstraGeneratePath(startCell, endCell);

            var res = new List<Point> {start};
            res.AddRange(cellsPath.Select(cell => _points[cell.I, cell.J]));
            return res;
        }
    }
}
