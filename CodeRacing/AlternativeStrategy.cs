using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;
using System;
using System.Collections.Generic;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public static bool CheckVisibilityAndWp(Car car, Point from, Point to, List<Cell> wayPoints)
        {
            wayPoints = wayPoints.GetRange(0, wayPoints.Count);
            if (!EnumeratePointsBetween(from, to, 10.0, point =>
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
                    var nextCell = DijkstraNextCell(curCell, nextWp, prevCell == null ? new Cell[] { } : new[] { prevCell });
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

            passedWayPoints.Clear();
            foreach (var t in extended)
            {
                if (result.Count > 0 && result.Last().Equals(t))
                    continue;
                if (GetNextWayPoint(car).Equals(GetCell(t)))
                    passedWayPoints.Add(GetCell(t));

                while (result.Count > 1 && CheckVisibilityAndWp(car, result[result.Count - 2], t, passedWayPoints))
                    result.Pop();
                result.Add(t);
            }
            return result;
        }

        public int BackModeRemainTicks;
        public double BackModeTurn;

        bool CheckBackMove(Point turnCenter)
        {
            const int ln = 40;
            if (BackModeRemainTicks == 0 && PositionsHistory.Count > ln)
            {
                if (
                    PositionsHistory[PositionsHistory.Count - 1].GetDistanceTo(
                        PositionsHistory[PositionsHistory.Count - ln]) < 20)
                {
                    var md = new ACar(self);
                    md.EnginePower = 1;
                    var cn = 0;
                    for (var i = 0; i < 80; i++)
                        if (ModelMove(md, new AMove { EnginePower = 1, IsBrake = false, WheelTurn = 0 }, simpleMode: false, exactlyBorders: true))
                            cn++;
                    if (cn < 25 || IsSomeoneAhead(new ACar(self)))
                    {
                        BackModeRemainTicks = 50;
                        BackModeTurn = self.GetAngleTo(turnCenter.X, turnCenter.Y) < 0 ? 1 : -1;
                    }
                }
            }

            if (BackModeRemainTicks > 0)
            {
                BackModeRemainTicks--;
                move.EnginePower = -1;
                move.WheelTurn = BackModeTurn;
                return true;
            }
            return false;
        }

        void AlternativeMove(Points pts)
        {
            var car = new ACar(self);

            var backBruteRes = _doAndSelectBrute(BackBrutes, pts);
            
            if (backBruteRes.Item1 != -1)
            {
                BackBrutes[backBruteRes.Item1].SelectThis();
                var mv = backBruteRes.Item2[backBruteRes.Item1];
                if (mv.Count > 0)
                {
                    mv[0].Apply(move, new ACar(self));
#if DEBUG
                    Visualizer.DrawWays(self, backBruteRes.Item2, backBruteRes.Item1);
#endif
                    return;
                }
            }

            // change points
            pts = GetAlternativeWaySegments(self);
            var turnCenter = pts[1];

            var tmp = new ACar(self);
            var aa = tmp + tmp.Speed;
            if (Math.Abs(tmp.GetAngleTo(aa)) > Math.PI / 2)
            {
                move.EnginePower = 1;
                move.WheelTurn *= -1;
                return;
            }


            move.EnginePower = 1.0;

            if (car.GetDistanceTo(turnCenter) < 1.6 * game.TrackTileSize)
            {
                move.EnginePower = 0.8;
            }

            if (car.GetDistanceTo(turnCenter) < 1.0 * game.TrackTileSize)
            {
                if (GetSpeed(self) > 11)
                    move.IsBrake = true;
            }
            move.WheelTurn = car.GetAngleTo(turnCenter);

            if (BAD_TESTING_STRATEGY)
            {
                if (turnCenter.GetDistanceTo(self) >= 7*game.TrackTileSize &&
                    Math.Abs(car.GetAngleTo(turnCenter)) < Math.PI/6)
                {
                    move.IsUseNitro = true;
                }
            }
        }
    }
}
