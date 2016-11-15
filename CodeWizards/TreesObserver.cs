using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class TreesObserver
    {
        private static Dictionary<long, ATree> _prevState = new Dictionary<long, ATree>();

        public static List<ATree> NewTrees = new List<ATree>(), DisappearedTrees = new List<ATree>();

        public static IEnumerable<ATree> Trees => _prevState.Values;

        public static void Update()
        {
            CellLength = Const.MapSize / GridSize;

            var newState = new Dictionary<long, ATree>();
            NewTrees.Clear();
            DisappearedTrees.Clear();

            foreach (var tree in MyStrategy.World.Trees)
            {
                var a = new ATree(tree);
                if (!_prevState.ContainsKey(tree.Id))
                {
                    // новое дерево
                    NewTrees.Add(a);
                }
                newState[tree.Id] = a;
            }

            foreach (var it in _prevState)
            {
                if (!MyStrategy.IsPointVisible(it.Value))
                {
                    // его не видно, считаем что осталось
                    newState[it.Key] = it.Value;
                }
                else if (!newState.ContainsKey(it.Key))
                {
                    // видно, но исчезло
                    DisappearedTrees.Add(it.Value);
                }
            }

            _prevState = newState;
            _updateGrid();
        }


        public static int GridSize = 130;
        public static double CellLength;
        private static ATree[,] _nearest = new ATree[GridSize + 1, GridSize + 1];


        private static Cell _findNearestCell(Point point)
        {           
            var I = (int)(point.X / CellLength + Const.Eps);
            var J = (int)(point.Y / CellLength + Const.Eps);

            double
                x1 = I*CellLength,
                x2 = x1 + CellLength,
                y1 = J*CellLength,
                y2 = y1 + CellLength;
            return new Cell(
                point.X - x1 <= x2 - point.X ? I : I + 1,
                point.Y - y1 <= y2 - point.Y ? J : J + 1
            );
        }

        private static void _updateGridByTree(ATree tree, ATree value)
        {
            var r = 1.4 * tree.Radius;
            var am = (int) (r/CellLength) + 1;
            var center = _findNearestCell(tree);
            for (var di = -am; di <= am; di++)
            {
                for (var dj = -am; dj <= am; dj++)
                {
                    var i = di + center.I;
                    var j = dj + center.J;
                    if (i >= 0 && j >= 0 && i <= GridSize && j <= GridSize)
                    {
                        var pt = new Point(i * CellLength, j * CellLength);
                        if (value == null || _nearest[i, j] == null || pt.GetDistanceToCircle(_nearest[i, j]) > pt.GetDistanceToCircle(tree))
                            _nearest[i, j] = value;
                    }
                }
            }
        }

        private static void _updateGrid()
        {
            foreach (var tree in NewTrees)
            {
                _updateGridByTree(tree, tree);
            }
            foreach (var tree in DisappearedTrees)
            {
                _updateGridByTree(tree, null);
                const int nearestPointsCount = 5;
                foreach (var tr in Trees.OrderBy(x => tree.GetDistanceTo2(x)).Take(nearestPointsCount))
                {
                    _updateGridByTree(tr, tr);
                }
            }
        }

        public static ATree GetNearestTree(Point point)
        {
            var cell = _findNearestCell(point);
            return _nearest[cell.I, cell.J];
        }

        private static List<object[]> _prevSegments = new List<object[]>();

        public static void RecheckNearestTrees()
        {
#if DEBUG
            if (MyStrategy.World.TickIndex % 1000 == 999)
            {
                _prevSegments.Clear();
                int found = 0;
                int errors = 0;
                for (var i = 0; i < GridSize; i++)
                {
                    for (var j = 0; j < GridSize; j++)
                    {
                        var pt = new Point(i * CellLength, j * CellLength);
                        var tr = GetNearestTree(pt);
                        if (tr == null)
                            continue;
                        found++;

                        var dst = pt.GetDistanceToCircle(tr);
                        var trees = Trees.ToArray();
                        for (var k = 0; k < trees.Length; k++)
                        {
                            if (pt.GetDistanceToCircle(trees[k]) < dst)
                            {
                                _prevSegments.Add(new object[] {
                                    new List<Point> { pt, tr },
                                    Pens.DarkOrange,
                                    3
                                });
                                errors++;
                            }
                        }
                    }
                }
                MyStrategy.Log("GetNearestTree errors " + errors + "/" + found);
            }
            Visualizer.Visualizer.SegmentsDrawQueue.AddRange(_prevSegments);
#endif
        }
    }
}
