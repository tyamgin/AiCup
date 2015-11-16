using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class PathPattern
    {
        public int From;
        public int To;
        public int Step;

        public AMove Move;
    }

    public enum TurnPatterns
    {
        ToNext,
        ToCenter
    }

    public class TurnPattern
    {
        public TurnPatterns Pattern;
    }

    public class PathBruteForce
    {
        public readonly PathPattern[] Patterns;
        public ACar Self;

        private PathPattern[] _patterns;
        private Moves _movesStack, _bestMovesStack;
        private int _bestTime;
        private int _bestPointIdx;
        private int _bestPointTime;
        private Point[] _bruteWayPoints;

        private delegate void CarCallback(ACar car, int time);

        private Moves _cache;
        private int _lastSuccess; // Когда последний раз брут что-то находил
        private int _lastCall; // Когда последний раз вызывали. Если не вызывали - значит был success

        private Point _turnCenter, _turnTo;
        private double _needDist;
        private int _interval;

        public PathBruteForce(PathPattern[] patterns, int interval)
        {
            Patterns = patterns;
            _interval = interval;
        }

        private void _doRecursive(ACar model, int idx, int totalTime)
        {
            model = model.Clone();

            if (idx == _patterns.Length)
            {
                var m = new AMove
                {
                    EnginePower = 1,
                    IsBrake = false,
                    WheelTurn = _turnTo.Clone(),
                    Times = 0
                };

                var end = true;
                for (; totalTime < _bestTime && _turnTo.GetDistanceTo2(model) > _needDist * _needDist; totalTime++)
                {
                    if (!MyStrategy.ModelMove(model, m))
                    {
                        end = false;
                        break;
                    }
                    m.Times++;
                }

                if (end && totalTime < _bestTime)
                {
                    _bestTime = totalTime;
                    _bestMovesStack = _movesStack.Clone();
                    _bestMovesStack.Add(m);
                }
                if (_bestTime == MyStrategy.Infinity)
                {
                    var sel = 0;
                    double minDist = MyStrategy.Infinity;
                    for (var i = 1; i < _bruteWayPoints.Length; i++)
                    {
                        if (_bruteWayPoints[i].GetDistanceTo2(model) <= minDist)
                        {
                            minDist = _bruteWayPoints[i].GetDistanceTo2(model);
                            sel = i;
                        }
                    }
                    if (sel > _bestPointIdx || sel == _bestPointIdx && totalTime < _bestPointTime)
                    {
                        _bestPointIdx = sel;
                        _bestPointTime = totalTime;
                        _bestMovesStack = _movesStack.Clone();
                        _bestMovesStack.Add(m);
                    }
                }

                return;
            }

            var pattern = _patterns[idx];
            _carMoveFunc(model, pattern.From, pattern.To, pattern.Step,
                new AMove
                {
                    EnginePower = pattern.Move.EnginePower,
                    IsBrake = pattern.Move.IsBrake,
                    WheelTurn = pattern.Move.WheelTurn,
                    Times = 0
                }, totalTime, (aCar, totalTimeAfter) =>
                {
                    if (totalTime >= _bestTime)
                        return;
                    _doRecursive(aCar.Clone(), idx + 1, totalTimeAfter);
                });
        }

        public Moves Do(ACar car, Points pts)
        {
            Self = car.Clone();
            if (_lastCall == _lastSuccess)
                _lastSuccess = _lastCall;
            _lastCall = MyStrategy.world.Tick;

            // Если был success на прошлом тике, то продолжаем. Или каждые _interval тиков.
            if (_lastSuccess != MyStrategy.world.Tick - 1 && (MyStrategy.world.Tick - (_lastSuccess + 1))%_interval != 0)
                return null;

            _turnCenter = pts[1];
            _turnTo = pts[2];

            _bruteWayPoints = ExtendWaySegments(pts).GetRange(0, Math.Min(100, pts.Count)).ToArray();
//#if DEBUG
//            var bruteWayPoints = new Points();
//            bruteWayPoints.AddRange(_bruteWayPoints);
//            MyStrategy.SegmentsDrawQueue.Add(new Tuple<Brush, Points>(Brushes.Brown, bruteWayPoints));
//#endif
            _needDist = _turnTo.GetDistanceTo(_turnCenter) - 1.5 * MyStrategy.game.TrackTileSize;

            for (var r = 0; r < 2 && _needDist < 10; r++)
            {
                _turnTo = pts[3 + r];
                _needDist = _turnTo.GetDistanceTo(_turnCenter) - 1.5 * MyStrategy.game.TrackTileSize;
            }

            _patterns = Patterns.Select(pt => new PathPattern
            {
                From = pt.From,
                To = pt.To,
                Step = pt.Step,
                Move = pt.Move.Clone()
            }).ToArray();
            foreach (var p in _patterns)
            {
                if (p.Move.WheelTurn is TurnPattern)
                {
                    var turnPattern = p.Move.WheelTurn as TurnPattern;
                    p.Move.WheelTurn = turnPattern.Pattern == TurnPatterns.ToCenter ? _turnCenter : _turnTo;
                }
            }

            _movesStack = new Moves();
            _bestMovesStack = new Moves();
            _bestTime = MyStrategy.Infinity;
            _bestPointTime = MyStrategy.Infinity;
            _bestPointIdx = 0;


            if (_cache != null)
            {
                for (var k = 0; k < _patterns.Length; k++)
                {
                    _patterns[k].From = Math.Max(0, _cache[k].Times - 2);
                    _patterns[k].To = _cache[k].Times + 2;
                    _patterns[k].Step = 1;
                }
            }

            _doRecursive(Self, 0, 0);
            _cache = null;
            if (_bestTime == MyStrategy.Infinity)
                return null;

            if (_bestMovesStack.ComputeTime() != _bestTime)
                throw new Exception("ComputeTime != BestTime");

            _lastSuccess = MyStrategy.world.Tick;
            _cache = _bestMovesStack.Clone();
            _bestMovesStack.Normalize();
            return _bestMovesStack;
        }


        private void _carMoveFunc(ACar model, int from, int to, int step, AMove m, int time, CarCallback callback)
        {
            model = model.Clone();
            m.Times = 0;

            for (var i = 0; i < from; i++)
            {
                if (!MyStrategy.ModelMove(model, m))
                    return;
                m.Times++;
            }

            for (var t = from; t <= to; t += step)
            {
                _movesStack.Add(m);
                callback(model, time + t);
                _movesStack.Pop();
                for (var r = 0; r < step; r++)
                {
                    if (!MyStrategy.ModelMove(model, m))
                        return;
                    m.Times++;
                }
            }
        }


        public Points ExtendWaySegments(Points pts)
        {
            var res = new Points();
            for (var idx = 1; idx < pts.Count; idx++)
            {
                var a = pts[idx - 1];
                var b = pts[idx];

                var delta = 50.0;
                var c = (int)(a.GetDistanceTo(b) / delta + 2);
                delta = a.GetDistanceTo(b) / c;
                var dir = (b - a).Normalized();
                for (var i = 0; i <= c; i++)
                    res.Add(a + dir * (delta * i));
            }
            return res;
        }
    }
}
