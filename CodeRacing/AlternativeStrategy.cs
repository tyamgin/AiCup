﻿using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public static bool CheckVisibilityAndWp(Car car, Point from, Point to, List<Cell> wayPoints)
        {
            wayPoints = wayPoints.GetRange(0, wayPoints.Count);
            if (!PointsBetween(from, to, 10.0, point =>
            {
                if (IntersectTail(point, car.Height / 2 + 5))
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

        public Points GetAlternativeWaySegments(Car car)
        {
            var result = new Points();
            var myCell = GetCell(car.X, car.Y);
            result.Add(new Point(car));
            Cell prevCell = null;

            var passedWayPoints = new List<Cell>();

            for (var e = 1; result.Count < 5; e++)
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

                while (result.Count > 1 && CheckVisibility(car, result[result.Count - 2], t))
                    result.Pop();
                result.Add(t);
            }
            return result;
        }

    }
}
