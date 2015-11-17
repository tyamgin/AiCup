using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Threading;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk 
{
    // TODO: откатывается назад после восстановления

    public partial class MyStrategy : IStrategy
    {
        public static World world;
        public static Game game;
        public Move move;
        public Car self;
        public static TileType[,] tiles;
        public Cell[] waypoints;
        public static double MapWidth, MapHeight;
        public static Point[,,] TileCorner;

        public const double SafeMargin = 3.0;
        public const long TimerLogLimit = 2;

        void Initialize()
        {
            MapWidth = game.TrackTileSize*world.Width;
            MapHeight = game.TrackTileSize*world.Height;

            // intialize tiles
            tiles = new TileType[world.Height, world.Width];
            var t = world.TilesXY;
            for(var i = 0; i < world.Height; i++)
                for (var j = 0; j < world.Width; j++)
                    tiles[i, j] = t[j][i];

            // intialize waypoints
            var wp = world.Waypoints;
            waypoints = new Cell[wp.Length];
            for(var i = 0; i < waypoints.Length; i++)
                waypoints[i] = new Cell(wp[i][1], wp[i][0]);

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
        public const int OpponentsTicksPrediction = 100;

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
                SegmentsDrawQueue.Add(new Tuple<Brush, Points>(Brushes.Indigo, seg));
#endif
            TimeEndLog("PrepareOpponentsPath");
        }

        public static bool ModelMove(ACar car, AMove m, bool simpleMode = false)
        {
            var turn = m.WheelTurn is Point ? TurnRound(car.GetAngleTo(m.WheelTurn as Point)) : Convert.ToDouble(m.WheelTurn);
            car.Move(m.EnginePower, turn, m.IsBrake, simpleMode);
            return car.GetRect().All(p => !_intersectTail(p));
        }

#if DEBUG
        void DrawWay(Moves stack, Brush brush)
        {
            if (stack == null)
                return;
            stack = stack.Clone();
            var drawPts = new Points();
            var drawModel = new ACar(self);
            while (stack.Count > 0)
            {
                var m = stack[0];

                drawPts.Add(new Point(drawModel));
                ModelMove(drawModel, m);
                m.Times--;
                stack.Normalize();
            }
            SegmentsDrawQueue.Add(new Tuple<Brush, Points>(brush, drawPts));
        }
#endif

        private PathBruteForce brute, brute2, brute3;

        private void _move()
        {
            var pts = GetWaySegments(self);
//#if DEBUG
//            _segmentsQueue.Add(new Tuple<Brush, Points>(Brushes.Brown, GetWaySegments(self)));
//#endif
            var to = pts[1];

            if (CheckUseProjectile())
                move.IsThrowProjectile = true;

            if (world.Tick > game.InitialFreezeDurationTicks)
                PositionsHistory.Add(new Point(self));

            //if (CheckUseNitro(to))
            //    move.IsUseNitro = true;

            if (CheckUseOil())
                move.IsSpillOil = true;

            const int ln = 50;
            if (BackModeRemainTicks == 0 && PositionsHistory.Count > ln)
            {
                if (
                    PositionsHistory[PositionsHistory.Count - 1].GetDistanceTo(
                        PositionsHistory[PositionsHistory.Count - ln]) < 20)
                {
                    var md = new ACar(self);
                    var cn = 0;
                    while (ModelMove(md, new AMove { EnginePower = 1, IsBrake = false, WheelTurn = 0 }))
                        cn++;
                    if (cn < 30 || IsSomeoneAhead(new ACar(self)))
                    {
                        BackModeRemainTicks = 50;
                        BackModeTurn = self.GetAngleTo(to.X, to.Y) < 0 ? 1 : -1;
                    }
                }
            }

            if (BackModeRemainTicks > 0)
            {
                BackModeRemainTicks--;
                move.EnginePower = -1;
                move.WheelTurn = BackModeTurn;
                return;
            }

            if (world.Tick < game.InitialFreezeDurationTicks)
            {
                move.EnginePower = 1;
                return;
            }

            if (brute == null)
            {
                brute = new PathBruteForce(new []
                {
                    new PathPattern
                    {
                        From = 0,
                        To = 40,
                        Step = 4,
                        Move = new AMove { EnginePower = 1, WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToCenter}, IsBrake = false}
                    },
                    new PathPattern
                    {
                        From = 0,
                        To = 20,
                        Step = 4,
                        Move = new AMove { EnginePower = 0.5, WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext}, IsBrake = false }
                    },
                    new PathPattern
                    {
                        From = 0,
                        To = 34,
                        Step = 2,
                        Move = new AMove { EnginePower = 0, WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext}, IsBrake = true }
                    }
                }, 8, id:0);
                
                brute2 = new PathBruteForce(new[]
                {
                    new PathPattern
                    {
                        From = 0,
                        To = 25,
                        Step = 1,
                        Move = new AMove { EnginePower = 0.2, WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext}, IsBrake = false }
                    },
                    new PathPattern
                    {
                        From = 0,
                        To = 34,
                        Step = 2,
                        Move = new AMove { EnginePower = 0, WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext}, IsBrake = true }
                    }
                }, 8, id:1);

                brute3 = new PathBruteForce(new[]
                {
                    new PathPattern
                    {
                        From = 0,
                        To = 20,
                        Step = 4,
                        Move = new AMove { EnginePower = 0.5, WheelTurn = new TurnPattern {Pattern = TurnPatterns.FromCenter}, IsBrake = false}
                    },
                    new PathPattern
                    {
                        From = 0,
                        To = 24,
                        Step = 2,
                        Move = new AMove { EnginePower = 1, WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext}, IsBrake = false }
                    },
                    new PathPattern
                    {
                        From = 0,
                        To = 32,
                        Step = 4,
                        Move = new AMove { EnginePower = 0, WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext}, IsBrake = true }
                    }
                }, 8, id:2);
            }

            if (self.GetDistanceTo(to.X, to.Y) > game.TrackTileSize * 2.7)
            {
                move.EnginePower = 1;
                move.WheelTurn = self.GetAngleTo(to.X, to.Y);
            }
            else
            {
                TimerStart();
                var brutes = new[] { brute, brute2, brute3 };
                var bestMoveStacks = new Moves[brutes.Length];
                for(var i = 0; i < brutes.Length; i++)
                    bestMoveStacks[i] = brutes[i].Do(new ACar(self), pts);
                TimeEndLog("brute");

                var sel = -1;
                for (var i = 0; i < brutes.Length; i++)
                {
                    if (bestMoveStacks[i] == null)
                        continue;
                    if (sel == -1 || bestMoveStacks[i].ComputeTime() < bestMoveStacks[sel].ComputeTime())
                        sel = i;
                }

                if (sel != -1)
                {
                    bestMoveStacks[sel][0].Apply(move, new ACar(self));
#if DEBUG
                    if (bestMoveStacks.Length > 0)
                        DrawWay(bestMoveStacks[0], Brushes.BlueViolet);
                    if (bestMoveStacks.Length > 1)
                        DrawWay(bestMoveStacks[1], Brushes.Red);
                    if (bestMoveStacks.Length > 2)
                        DrawWay(bestMoveStacks[2], Brushes.DeepPink);
#endif
                }
//                else if (BestPointTime < Infinity && BestPointIdx > 20)
//                {
//                    _bruteUnsuccess = true;
//                    if (BestMovesStack.ComputeTime() != BestPointTime)
//                        throw new Exception("ComputeTime != BestPointTime");
//                    BestMovesStack.Normalize();
//                    BestMovesStack[0].Apply(move, new ACar(self));
//#if DEBUG
//                    DrawWay(Brushes.Red);
//#endif
//                }
                else
                {
                    move.EnginePower = 0.2;
                    move.WheelTurn = self.GetAngleTo(to.X, to.Y);
                    var tmp = new ACar(self);
                    var aa = tmp + tmp.Speed;
                    if (Math.Abs(tmp.GetAngleTo(aa)) > Math.PI/2)
                    {
                        move.EnginePower = 1;
                        move.WheelTurn *= -1;
                    }
                }
            }
        }

        
        public Points PositionsHistory = new Points();
        public int BackModeRemainTicks;
        public double BackModeTurn;

        private ACar _tmp;

        public void Move(Car self, World world, Game game, Move move)
        {
            TimerStart();
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
            if (world.Tick >= 738)
            {
                _tmp = new ACar(self);
                _tmp.Move(1.0, -0.580192616161978, false, false);
                move.EnginePower = 1.0;
                move.WheelTurn = -0.580192616161978;
                return;
            }
            _move();
#if DEBUG
            TimeEndLog("All");
            draw();
            Thread.Sleep(12);
#endif
        }
    }
}