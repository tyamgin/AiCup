using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Runtime.InteropServices;
using System.Threading;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk 
{
    // TODO: обработать столкновения с бонусами и убрать костыль
    // TODO: не юзать нитро если лужа

    public partial class MyStrategy : IStrategy
    {
        public static World world;
        public static Game game;
        public Move move;
        public Car self;
        public static TileType[,] tiles;
        public static ATile[,] MyTiles;
        public Cell[] waypoints;
        public static double MapWidth, MapHeight;
        public static Point[,,] TileCorner;
        public static Dictionary<long, Player> Players; 

        public const double SafeMargin = 10.0;
        public const long TimerLogLimit = 5;

        public static double CarDiagonalHalfLength;
        public static double BonusDiagonalHalfLength;

        public const int OpponentsTicksPrediction = 100;
        public const int ProjectilePredictionTicks = 60;

        public static AProjectile[][] Projectiles;
        public static ABonus[] Bonuses;
        public static AOilSlick[] OilSlicks;

        public static Car[] Opponents;
        public static ACar[][] OpponentsCars;
        

        public Points PositionsHistory = new Points();
        public int BackModeRemainTicks;
        public double BackModeTurn;

        void Initialize()
        {
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


            Players = new Dictionary<long, Player>();
            foreach (var player in world.Players)
                Players[player.Id] = player;

            foreach (var car in world.Cars)
            {
                DurabilityObserver.Watch(car);
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


                MapWidth = game.TrackTileSize*world.Width;
                MapHeight = game.TrackTileSize*world.Height;

                CarDiagonalHalfLength = Geom.Gypot(game.CarWidth, game.CarHeight)/2;
                BonusDiagonalHalfLength = Geom.Gypot(game.BonusSize, game.BonusSize)/2-6;//HACK

                MyTiles = new ATile[world.Height, world.Width];
                for (var i = 0; i < world.Height; i++)
                {
                    for (var j = 0; j < world.Width; j++)
                    {
                        MyTiles[i, j] = new ATile(i, j, tiles[i, j]);
                    }
                }
            }

            Projectiles = new AProjectile[world.Projectiles.Length][];
            for (var i = 0; i < world.Projectiles.Length; i++)
            {
                Projectiles[i] = new AProjectile[ProjectilePredictionTicks];
                Projectiles[i][0] = new AProjectile(world.Projectiles[i]);
                for (var j = 1; j < ProjectilePredictionTicks; j++)
                {
                    Projectiles[i][j] = Projectiles[i][j - 1].Clone();
                    Projectiles[i][j].Move();
                }
            }

            Bonuses = world.Bonuses.Select(b => new ABonus(b)).ToArray();
            OilSlicks = world.OilSlicks.Select(s => new AOilSlick(s)).ToArray();

            Opponents = world.Cars.Where(car => !car.IsTeammate).ToArray();
            PrepareOpponentsPath();
        }

        public void PrepareOpponentsPath()
        {
            if (world.Tick < game.InitialFreezeDurationTicks)
                return;

            TimerStart();
            var ways = Opponents.Select(GetWaySegments).ToArray();
#if DEBUG
            var segs = Opponents.Select(x => new Points()).ToArray();
#endif

            OpponentsCars = new ACar[Opponents.Length][];
            for (var i = 0; i < Opponents.Length; i++)
            {
                OpponentsCars[i] = new ACar[OpponentsTicksPrediction];
                OpponentsCars[i][0] = new ACar(Opponents[i]);
                for (var t = 1; t < OpponentsTicksPrediction; t++)
                {
                    OpponentsCars[i][t] = OpponentsCars[i][t - 1].Clone();
                    _simulateOpponentMove(ways[i], OpponentsCars[i][t]);
#if DEBUG
                    segs[i].Add(new Point(OpponentsCars[i][t]));
#endif
                }
            }

#if DEBUG
            foreach (var seg in segs)
                SegmentsDrawQueue.Add(new object[] { Brushes.Indigo, seg, 0.0 });
#endif
            TimeEndLog("PrepareOpponentsPath");
        }

        public static bool ModelMove(ACar car, AMove m, bool simpleMode = false, bool exactlyBorders = false)
        {
            var prevStateX = car.X;
            var prevStateY = car.Y;
            var prevStateAngle = car.Angle;

            var turn = m.WheelTurn is Point ? TurnRound(car.GetAngleTo(m.WheelTurn as Point)) : Convert.ToDouble(m.WheelTurn);
            car.Move(m.EnginePower, turn, m.IsBrake, m.IsUseNitro, simpleMode);
            var ok = car.GetRect().All(p => !IntersectTail(p, exactlyBorders ? 0 : SafeMargin));
            if (!ok)
            {
                // HACK
                car.X = prevStateX;
                car.Y = prevStateY;
                car.Angle = prevStateAngle;
                car.Speed = Point.Zero;
                car.AngularSpeed = 0;
            }
            return ok;
        }

        private PathBruteForce[] brutes;

        private AProjectile pr;

        private void _move()
        {
            //if (world.Tick < 250)
            //    return;
            var pts = GetWaySegments(self);
            var turnCenter = pts[1];

            if (world.Tick > game.InitialFreezeDurationTicks)
                PositionsHistory.Add(new Point(self));

            if (!DurabilityObserver.IsActive(self))
                return;

            if (CheckUseProjectile())
                move.IsThrowProjectile = true;

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
                    md.EnginePower = 1;
                    var cn = 0;
                    for(var i = 0; i < 80; i++)
                        if (ModelMove(md, new AMove { EnginePower = 1, IsBrake = false, WheelTurn = 0 }, simpleMode:false, exactlyBorders:true))
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
                return;
            }

            if (world.Tick < game.InitialFreezeDurationTicks)
            {
                move.EnginePower = 1;
                return;
            }

            //if (pr == null)
            //{
            //    //var ttt = Geom.LineCircleIntersect(new Point(2, 0), new Point(0, 2), new Point(-1, -1), 2*Math.Sqrt(2));

            //    pr = AProjectile.GetProjectiles(new ACar(self))[0];
            //    move.IsThrowProjectile = true;
            //}
            //else
            //{
            //    pr.Move();
            //    return;
            //}
            //return;

            if (brutes == null)
            {
                brutes = new[]
                {
                    /*
                     * - ехать в сторону поворота на полной можности
                     * - поворачивать в сторону цели на пол-мощности
                     * - тормозить
                     */
                    new PathBruteForce(new[]
                    {
                        new PathPattern
                        {
                            From = 0,
                            To = 40,
                            Step = 4,
                            Move =
                                new AMove
                                {
                                    EnginePower = 1,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToCenter},
                                    IsBrake = false
                                }
                        },
                        new PathPattern
                        {
                            From = 0,
                            To = 20,
                            Step = 4,
                            Move =
                                new AMove
                                {
                                    EnginePower = 0.5,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext},
                                    IsBrake = false
                                }
                        },
                        new PathPattern
                        {
                            From = 0,
                            To = 34,
                            Step = 2,
                            Move =
                                new AMove
                                {
                                    EnginePower = 0,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext},
                                    IsBrake = true
                                }
                        }
                    }, 8, useNitroInLastStage:false, id: 0),

                    /*
                     * - снизить мощность
                     * - тормозить
                     */
                    new PathBruteForce(new[]
                    {
                        new PathPattern
                        {
                            From = 0,
                            To = 25,
                            Step = 1,
                            Move =
                                new AMove
                                {
                                    EnginePower = 0.2,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext},
                                    IsBrake = false
                                }
                        },
                        new PathPattern
                        {
                            From = 0,
                            To = 34,
                            Step = 2,
                            Move =
                                new AMove
                                {
                                    EnginePower = 0,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext},
                                    IsBrake = true
                                }
                        }
                    }, 8, useNitroInLastStage:false, id: 1),

                    /*
                     * - ехать от поворота на пол-мощности
                     * - поворачивать в сторону цели на полной мощности
                     * - тормозить
                     */
                    new PathBruteForce(new[]
                    {
                        new PathPattern
                        {
                            From = 0,
                            To = 20,
                            Step = 4,
                            Move =
                                new AMove
                                {
                                    EnginePower = 0.5,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.FromCenter},
                                    IsBrake = false
                                }
                        },
                        new PathPattern
                        {
                            From = 0,
                            To = 24,
                            Step = 2,
                            Move =
                                new AMove
                                {
                                    EnginePower = 1,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext},
                                    IsBrake = false
                                }
                        },
                        new PathPattern
                        {
                            From = 0,
                            To = 32,
                            Step = 4,
                            Move =
                                new AMove
                                {
                                    EnginePower = 0,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext},
                                    IsBrake = true
                                }
                        }
                    }, 8, useNitroInLastStage:false, id: 2),

                    /*
                     * - ехать в сторону поворота на полной можности
                     * - поворачивать в сторону цели на пол-мощности
                     * - тормозить
                     * - НИТРО!!!
                     */
                    new PathBruteForce(new[]
                    {
                        new PathPattern
                        {
                            From = 0,
                            To = 20,
                            Step = 4,
                            Move =
                                new AMove
                                {
                                    EnginePower = 1,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToCenter},
                                    IsBrake = false
                                }
                        },
                        new PathPattern
                        {
                            From = 0,
                            To = 20,
                            Step = 4,
                            Move =
                                new AMove
                                {
                                    EnginePower = 0.5,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext},
                                    IsBrake = false
                                }
                        },
                        new PathPattern
                        {
                            From = 0,
                            To = 34,
                            Step = 3,
                            Move =
                                new AMove
                                {
                                    EnginePower = 0,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext},
                                    IsBrake = true
                                }
                        }
                    }, 8, useNitroInLastStage:true, id: 0),
                };
            }
           
            TimerStart();
            var bestMoveStacks = new Moves[brutes.Length];
            for (var i = 0; i < brutes.Length; i++)
            {
                if (true)
                {
                    if (!brutes[i].UseNitroInLastStage || self.NitroChargeCount > 0)
                        bestMoveStacks[i] = brutes[i].Do(new ACar(self), pts);
                }
            }
            TimeEndLog("brute");


            var sel = -1;
            double bestTime = Infinity;
            for (var i = 0; i < brutes.Length; i++)
            {
                if (bestMoveStacks[i] == null)
                    continue;
                var time = bestMoveStacks[i].ComputeImportance(new ACar(self));
                if (sel == -1 ||
                    brutes[i].LastSuccess > brutes[sel].LastSuccess ||
                    brutes[i].LastSuccess == brutes[sel].LastSuccess && time < bestTime)
                {
                    sel = i;
                    bestTime = time;
                }
            }

            if (sel != -1)
            {
                brutes[sel].SelectThis();
                bestMoveStacks[sel][0].Apply(move, new ACar(self));
#if DEBUG
                DrawWays(bestMoveStacks, sel);
#endif
            }
            else
            {
                //if (world.Tick >= 400)
                //    throw new Exception("test exception");
                AlternativeMove();
            }
        }

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
            _drawMap();
#endif
            if (!self.IsFinishedTrack)
                _move();
            else if (_finishTime == Infinity)
                _finishTime = world.Tick;
            if (_finishTime < Infinity)
                Log(_finishTime);
#if DEBUG
            TimeEndLog("All");
            Draw();
            Thread.Sleep(12);
#endif
        }

        private int _finishTime = Infinity;
    }
}