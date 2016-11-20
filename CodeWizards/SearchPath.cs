using System;
using System.Collections.Generic;
using System.Linq;
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
        public static int GridSize = 80;
        public static double CellLength;
        public static double CellDiagLength;

        public static List<Point> GetFreePoints()
        {
            var res = new List<Point>();
            for (var i = 0; i <= GridSize; i++)
                for (var j = 0; j <= GridSize; j++)
                    if (!_isLocked[i, j])
                        res.Add(_points[i, j]);
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

        private void _recheckNeighbours()
        {
#if !DEBUG
            throw new Exception("for DEBUG porposes only");
#else
            Log("Neighbours test start");
            var tmpNeighbours = _neighbours;
            var tmpIsLocked = _isLocked;
            _neighbours = new List<Cell>[GridSize + 1, GridSize + 1];
            _isLocked = new bool[GridSize + 1, GridSize + 1];
            for (var i = 0; i <= GridSize; i++)
            {
                for (var j = 0; j <= GridSize; j++)
                {
                    _calculateNeighbours(i, j);
                    foreach(var ob in _getStaticUnits())
                        _filterNeighbours(i, j, ob);
                }
            }
            for (var i = 0; i <= GridSize; i++)
            {
                for (var j = 0; j <= GridSize; j++)
                {
                    if (_isLocked[i, j] != tmpIsLocked[i, j])
                        throw new Exception("test failed");
                    if (_neighbours[i, j].Count != tmpNeighbours[i, j].Count)
                        throw new Exception("test failed");
                    for(var k = 0; k < _neighbours[i, j].Count; k++)
                        if (!_neighbours[i, j][k].Equals(tmpNeighbours[i, j][k]))
                            throw new Exception("test failed");
                }
            }
            _neighbours = tmpNeighbours;
            _isLocked = tmpIsLocked;
            Log("Neighbours test end");
#endif
        }

        private void _filterNeighbours(int I, int J, ACircularUnit unit)
        {
            var list = _neighbours[I, J];
            var point = _points[I, J];

            if (point.GetDistanceTo2(unit) < Geom.Sqr(unit.Radius + Self.Radius + MagicConst.RadiusAdditionalEpsilon) ||
                point.X < Self.Radius || point.Y < Self.Radius ||
                point.X > Const.MapSize - Self.Radius || point.Y > Const.MapSize - Self.Radius
                )
            {
                _isLocked[I, J] = true; //TODO maybe return
            }

            for (var idx = list.Count - 1; idx >= 0; idx--)
            {
                var pt = _points[list[idx].I, list[idx].J];
                
                if (Geom.SegmentCircleIntersects(point, pt, unit, unit.Radius + Self.Radius + MagicConst.RadiusAdditionalEpsilon))
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
            return unit.GetDistanceTo2(p) < Geom.Sqr(unit.Radius + Self.Radius + 1 + CellDiagLength);
        }

        private void InitializeDijkstra()
        {
            if (_distMap == null)
            {
                CellLength = Const.MapSize / GridSize;
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
                        _points[i, j] = new Point(Const.MapSize / GridSize*i, Const.MapSize / GridSize*j);

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
                    .Where(x => x is ABuilding || x.GetDistanceTo2(oldUnit) < Geom.Sqr(2*(Const.TreeMaxRadius + Self.Radius + 1 + CellDiagLength)))
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

        static Cell FindNearestCell(ACircularUnit my)
        {
            double ds = Const.MapSize/GridSize;

            var I = (int) (my.X/ds + Const.Eps);
            var J = (int) (my.Y/ds + Const.Eps);
            int seldI = int.MaxValue, seldJ = int.MaxValue;
            double minDist = int.MaxValue;
            var obstacles =
                _obstacles.Cast<ACircularUnit>()
                    .Concat(BuildingsObserver.Buildings)
                    .Concat(TreesObserver.Trees)
                    .ToArray();

            for (var di = 0; di < 2; di++)
            {
                for (var dj = 0; dj < 2; dj++)
                {
                    var dst = _points[I + di, J + dj].GetDistanceTo2(my);
                    if (dst < minDist && 
                        !_isLocked[I + di, J + dj] &&
                        obstacles.All(ob =>
                            !Geom.SegmentCircleIntersects(my, _points[I + di, J + dj], ob, ob.Radius + my.Radius + MagicConst.RadiusAdditionalEpsilon))
                        )
                    {
                        minDist = dst;
                        seldI = di;
                        seldJ = dj;
                    }
                }
            }
            if (seldI == int.MaxValue)
                return null;
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
                            ob.Radius + _selfRadius + MagicConst.RadiusAdditionalEpsilon));
        }

        static double GetDist(Cell from, Cell to)
        {
            return Math.Abs(from.I - to.I) + Math.Abs(from.J - to.J) <= 1
                ? CellLength
                : CellDiagLength;
        }

        public enum DijkstraStopStatus
        {
            Continue,
            Take,
            Stop,
            TakeAndStop,
        }

        public delegate bool DijkstraStopFuncCell(Cell pos);
        public delegate DijkstraStopStatus DijkstraStopFuncPoint(Point pos);

        public static void DijkstraStart(Cell start, DijkstraStopFuncCell condition)
        {
            var q = new PriorityQueue<Pair<double, Cell>>();
            q.Push(new Pair<double, Cell>(0.0, start));
            for (var i = 0; i <= GridSize; i++)
            {
                for (var j = 0; j <= GridSize; j++)
                {
                    _distMap[i, j] = Const.Infinity;
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
                return new List<Cell> {start};

            if (_distPrev[end.I, end.J] == null)
            {
                // path not found
                return null;
            }

            var res = new List<Cell>();
            var cur = end.Clone();
            do
            {
                res.Add(cur.Clone());

                if (cur.Equals(start))
                    break;

                cur = _distPrev[cur.I, cur.J];
            } while (true);

            res.Reverse();
            return res;
        }

        public static List<Point>[] DijkstraFindPath(ACombatUnit start, DijkstraStopFuncPoint stopFunc)
        {
            _selfRadius = start.Radius;
            _obstacles = Combats
                .Where(x => !x.IsOpponent && x.Id != start.Id && x.GetDistanceTo2(start) < Geom.Sqr(start.VisionRange)) // (нейтральные включительно)
                .ToArray();

            var startCell = FindNearestCell(start);
            if (startCell == null)
                return new List<Point>[] {};

            var endCells = new List<Cell>();

            DijkstraStart(startCell, cell =>
            {
                var point = _points[cell.I, cell.J];
                var status = stopFunc(point);

                if (status == DijkstraStopStatus.Take || status == DijkstraStopStatus.TakeAndStop)
                    endCells.Add(cell);

                if (status == DijkstraStopStatus.Stop || status == DijkstraStopStatus.TakeAndStop)
                    return true;
                return false;
            });

            return endCells.Select(endCell =>
            {
                var cellsPath = DijkstraGeneratePath(startCell, endCell);

                var res = new List<Point> {start};
                res.AddRange(cellsPath.Select(cell => _points[cell.I, cell.J]));
                return res;
            }).ToArray();
        }

        public void SimplifyPath(AWizard self, ACircularUnit[] obstacles, List<Point> path)
        {
            for (var i = 2; i < path.Count; i++)
            {
                var a = path[i - 2];
                var c = path[i];
                if (obstacles.All(ob => !Geom.SegmentCircleIntersects(a, c, ob, self.Radius + ob.Radius + MagicConst.RadiusAdditionalEpsilon)))
                {
                    path.RemoveAt(i - 1);
                    i--;
                }
            }
        }
    }
}
