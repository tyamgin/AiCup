using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Data.Common;
using System.Data.SqlTypes;
using System.Diagnostics;
using System.Drawing;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Threading;
using System.Windows.Forms;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk 
{
    public partial class MyStrategy : IStrategy
    {
        public static World world;
        public static Game game;
        public Move move;
        public Car self;
        public TileType[,] tiles;
        public Cell[] waypoints;
        public static double MapWidth, MapHeight;
        public static Point[,,] TileCorner;

        public const double SafeMargin = 3.0;

        void Initialize()
        {
            MapWidth = game.TrackTileSize*world.Width;
            MapHeight = game.TrackTileSize*world.Height;

            // intialize tiles
            this.tiles = new TileType[world.Height, world.Width];
            var t = world.TilesXY;
            for(var i = 0; i < world.Height; i++)
                for (var j = 0; j < world.Width; j++)
                    tiles[i, j] = t[j][i];

            // intialize waypoints
            var wp = world.Waypoints;
            this.waypoints = new Cell[wp.Length];
            for(var i = 0; i < waypoints.Length; i++)
                this.waypoints[i] = new Cell(wp[i][1], wp[i][0]);

            if (_waypointIterator == null)
            {
                _waypointIterator = new Dictionary<long, int>();
                foreach (var car in world.Cars)
                    _waypointIterator[car.Id] = 0;
            }
            foreach (var car in world.Cars)
            {
                if (waypoints[_waypointIterator[car.Id]].I != car.NextWaypointY ||
                    waypoints[_waypointIterator[car.Id]].J != car.NextWaypointX)
                {
                    _waypointIterator[car.Id] = (_waypointIterator[car.Id] + 1)%waypoints.Length;
                }
            }

            if (TileCorner == null)
            {
                TileCorner = new Point[world.Height, world.Width, 4];
                for (var i = 0; i < world.Height; i++)
                    for (var j = 0; j < world.Width; j++)
                        for (var di = 0; di < 2; di++)
                            for (var dj = 0; dj < 2; dj++)
                                TileCorner[i, j, di*2 + dj]
                                    = new Point((j + dj)*game.TrackTileSize, (i + di)*game.TrackTileSize);

            }

            Opponents = world.Cars.Where(car => !car.IsTeammate).ToArray();
            PrepareOpponentsPath();
        }

        public Car[] Opponents;
        public ACar[][] OpponentsCars;
        public const int OpponentsTicksPrediction = 50;

        public void PrepareOpponentsPath()
        {
            if (world.Tick < game.InitialFreezeDurationTicks)
                return;

            TimerStart();
            OpponentsCars = new ACar[OpponentsTicksPrediction][];
            OpponentsCars[0] = Opponents.Select(car => new ACar(car)).ToArray();
            var ways = Opponents.Select(GetWaySegments).ToArray();
#if DEBUG
            var segs = Opponents.Select(x => new Points()).ToArray();
#endif
            for (var t = 1; t < OpponentsTicksPrediction; t++)
            {
                OpponentsCars[t] = new ACar[Opponents.Length];
                for (var i = 0; i < Opponents.Length; i++)
                {
                    OpponentsCars[t][i] = OpponentsCars[t - 1][i].Clone();
                    _simulateOpponentMove(ways[i], OpponentsCars[t][i]);
#if DEBUG
                    segs[i].Add(new Point(OpponentsCars[t][i]));
#endif
                }
            }
#if DEBUG
            foreach (var seg in segs)
                _segmentsQueue.Add(new Tuple<Brush, Points>(Brushes.Indigo, seg));
#endif
            Log("PrepareOpponentsPath: " + TimerStop() + "ms");
        }

        private long _bruteSumTime1;
        private long _bruteSumTime2;
        private long _bruteSumTime3;

        private bool ModelMove(ACar car, AMove m, bool simpleMode = false)
        {
            var turn = m.WheelTurn is Point ? TurnRound(car.GetAngleTo(m.WheelTurn as Point)) : Convert.ToDouble(m.WheelTurn);
            car.Move(m.EnginePower, turn, m.IsBrake, simpleMode);
            return car.GetRect().All(p => !_intersectTail(p));
        }

        private List<Moves> _cache = new List<Moves>(); 

        private void _move()
        {
            var t = GetWaySegments(self);
#if DEBUG
            _segmentsQueue.Add(new Tuple<Brush, Points>(Brushes.Brown, t));
#endif

            var pts = GetWaySegments(self);
            var to = pts[1];
            var to2 = pts[2];

            if (CheckUseOil())
                move.IsSpillOil = true;

            if (world.Tick < game.InitialFreezeDurationTicks)
            {
                move.EnginePower = 1;
            }
            else if (self.GetDistanceTo(to.X, to.Y) > game.TrackTileSize * 2.7)
            {
                move.EnginePower = 1;
                move.WheelTurn = self.GetAngleTo(to.X, to.Y);
            }
            else
            {
                BestTime = Infinity;
                BestMovesStack = new Moves();

                //TimerStart();
                //BruteFunc(pts, new[]
                //{
                //    new[] {0, 0, 1},
                //    new[] {0, 30, 2},
                //    new[] {0, 40, 1}
                //}, 1);
                //_bruteSumTime1 = TimerStop();

                var timings = new[]
                {
                    new[] {0, 50, 4},
                    new[] {0, 20, 4},
                    new[] {0, 34, 2}
                };

                if (_cache.Count > 0 && _cache[0] != null)
                {
                    for (var k = 0; k < 3; k++)
                    {
                        timings[k][0] = Math.Max(0, _cache[0][k].Times - 2);
                        timings[k][1] = _cache[0][k].Times + 2;
                        timings[k][2] = 1;
                    }
                }

                TimerStart();
                BruteFunc(pts, timings, 1);
                _bruteSumTime2 = TimerStop();

                //TimerStart();
                //BruteFunc(pts, new[]
                //{
                //    new[] {0, 40, 4},
                //    new[] {0, 6, 3},
                //    new[] {0, 21, 3}
                //}, 0.4);
                //_bruteSumTime3 = TimerStop();

                _cache.Clear();

                if (BestTime < Infinity)
                {
                    _cache.Add(BestMovesStack.Clone());

                    if (BestMovesStack.ComputeTime() != BestTime)
                        throw new Exception("something wrong");

                    BestMovesStack.Normalize();
                    BestMovesStack[0].Apply(move, new ACar(self));

#if DEBUG
                    var drawPts = new Points();
                    var drawModel = new ACar(self);
                    while (BestMovesStack.Count > 0)
                    {
                        var m = BestMovesStack[0];

                        drawPts.Add(new Point(drawModel));
                        ModelMove(drawModel, m);
                        m.Times--;
                        BestMovesStack.Normalize();
                    }
                    _segmentsQueue.Add(new Tuple<Brush, Points>(Brushes.BlueViolet, drawPts));
#endif
                }
                else
                {
//                    pts = Closify(pts);
//                    to = pts[1];

//#if DEBUG
//                    _segmentsQueue.Add(new Tuple<Brush, Points>(Brushes.Gold, pts));
//#endif

                    move.EnginePower = 0.2;
                    move.WheelTurn = self.GetAngleTo(to.X, to.Y);
                }
            }
        }

        delegate void CarCallback(ACar car, int time);

        public Moves MovesStack, BestMovesStack;
        public int BestTime;

        private void BruteFunc(Points pts, int[][] timingInfo, double startPower)
        {
            var turnCenter = pts[1];
            var turnTo = pts[2];

            var needDist = turnTo.GetDistanceTo(turnCenter) - 1.5 * game.TrackTileSize;
            if (needDist < 10)
            {
                turnTo = pts[3];
                needDist = turnTo.GetDistanceTo(turnCenter) - 1.5 * game.TrackTileSize;
            }

            var modelA = new ACar(self);
            MovesStack = new Moves();

            CarMoveFunc(modelA, timingInfo[0][0], timingInfo[0][1], timingInfo[0][2], new AMove { EnginePower = startPower, WheelTurn = turnCenter, IsBrake = false}, 0, (aCar, time1) =>
            {
                if (time1 >= BestTime)
                    return;

                var turn = TurnRound(aCar.GetAngleTo(turnTo));
                CarMoveFunc(aCar, timingInfo[1][0], timingInfo[1][1], timingInfo[1][2], new AMove { EnginePower = 0.5, WheelTurn = turn, IsBrake = false }, time1, (bCar, time2) =>
                {
                    if (time2 >= BestTime)
                        return;

                    CarMoveFunc(bCar, timingInfo[2][0], timingInfo[2][1], timingInfo[2][2], new AMove { EnginePower = 0, WheelTurn = turn, IsBrake = true }, time2, (cCar, time3) =>
                    {
                        var model = new ACar(cCar);
                        var m = new AMove
                        {
                            EnginePower = 1,
                            IsBrake = false,
                            WheelTurn = turnTo.Clone(),
                            Times = 0
                        };

                        var totalTime = time3;
                        for (; totalTime < BestTime && turnTo.GetDistanceTo2(model) > needDist * needDist; totalTime++)
                        {
                            if (!ModelMove(model, m, simpleMode: false))
                                return;
                            m.Times++;
                        }

                        if (totalTime < BestTime)
                        {
                            BestTime = totalTime;
                            BestMovesStack = MovesStack.Clone();
                            BestMovesStack.Add(m);
                        }
                    });
                });
            });
        }

       
        private void CarMoveFunc(ACar _model, int from, int to, int step, AMove m, int time, CarCallback callback)
        {
            m.Times = 0;

            var model = new ACar(_model);
            for (var i = 0; i < from; i++)
            {
                if (!ModelMove(model, m))
                    return;
                m.Times++;
            }

            for (var t = from; t <= to; t += step)
            {
                MovesStack.Add(m);
                callback(new ACar(model), time + t);
                MovesStack.Pop();
                for (var r = 0; r < step; r++)
                {
                    if (!ModelMove(model, m))
                        return;
                    m.Times++;
                }
            }
        }

        public void Move(Car self, World world, Game game, Move move)
        {
            MyStrategy.world = world;
            MyStrategy.game = game;
            this.move = move;
            this.self = self;
            Initialize();

#if DEBUG
            while (Pause)
            {
                // pass
            }
            if (Debug)
            {
                Debug = false;
            }
            DrawMap();
#endif
            _move();
#if DEBUG
            draw();
            Thread.Sleep(8);
            Log(_bruteSumTime1 + " " + _bruteSumTime2 + " " + _bruteSumTime3);
#endif
        }
    }
}