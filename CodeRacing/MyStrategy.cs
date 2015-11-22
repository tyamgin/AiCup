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

        public const double SafeMargin = 10.0;
        public const long TimerLogLimit = 5;

        public static double CarDiagonalHalfLength;
        public static double BonusDiagonalHalfLength;

        public Car[] Opponents;
        public ACar[][] OpponentsCars;
        public const int OpponentsTicksPrediction = 100;

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

            Opponents = world.Cars.Where(car => !car.IsTeammate).ToArray();
            PrepareOpponentsPath();
        }

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

        private PathBruteForce[] brutes, extraBrutes;

        //private AProjectile pr;

        private void _move()
        {
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
                    }, 8, useNitroInLastStage: false, id: 0),

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
                    }, 8, useNitroInLastStage: false, id: 1),

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
                            To = 28,
                            Step = 4,
                            Move =
                                new AMove
                                {
                                    EnginePower = 0.5,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.FromNext},
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
                    }, 8, useNitroInLastStage: false, id: 2),

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
                            To = 16,
                            Step = 2,
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
                            To = 16,
                            Step = 2,
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
                            To = 30,
                            Step = 3,
                            Move =
                                new AMove
                                {
                                    EnginePower = 0,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext},
                                    IsBrake = true
                                }
                        }
                    }, 8, useNitroInLastStage: true, id: 3),
                };
                extraBrutes = new[] {
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
                                    EnginePower = 0.1,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.FromNext},
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
                                    EnginePower = 0.1,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext},
                                    IsBrake = false
                                }
                        },
                        new PathPattern
                        {
                            From = 0,
                            To = 40,
                            Step = 5,
                            Move =
                                new AMove
                                {
                                    EnginePower = 0,
                                    WheelTurn = new TurnPattern {Pattern = TurnPatterns.ToNext},
                                    IsBrake = true
                                }
                        }
                    }, 8, useNitroInLastStage:false, id: 5),
                };
            }
            
            TimerStart();
            var sel = -1;
            double bestTime = Infinity;

            var allBrutes = brutes.Concat(extraBrutes).ToArray();
            var bestMoveStacks = new Moves[allBrutes.Length];
            for (var i = 0; i < allBrutes.Length && (sel == -1 || i < brutes.Length || allBrutes[sel].LastSuccess < world.Tick); i++)
            {
                if (!allBrutes[i].UseNitroInLastStage || self.NitroChargeCount > 0)
                    bestMoveStacks[i] = allBrutes[i].Do(new ACar(self), pts, i >= brutes.Length);
                if (bestMoveStacks[i] == null)
                    continue;
                var time = bestMoveStacks[i].ComputeImportance(new ACar(self));
                if (sel == -1 ||
                    allBrutes[i].LastSuccess > allBrutes[sel].LastSuccess ||
                    allBrutes[i].LastSuccess == allBrutes[sel].LastSuccess && time < bestTime)
                {
                    sel = i;
                    bestTime = time;
                }
            }
            TimeEndLog("brute");


            if (sel != -1)
            {
                allBrutes[sel].SelectThis();
                bestMoveStacks[sel][0].Apply(move, new ACar(self));
#if DEBUG
                DrawWays(bestMoveStacks, sel);
#endif
            }
            else
            {
                // TODO: придумать нормальный альтернативный алгоритм
                move.EnginePower = 0.2;
                move.WheelTurn = self.GetAngleTo(turnCenter.X, turnCenter.Y);
                var tmp = new ACar(self);
                //if (tmp.Speed.Length > 10)
                //    move.IsBrake = true;
                var aa = tmp + tmp.Speed;
                if (Math.Abs(tmp.GetAngleTo(aa)) > Math.PI/2)
                {
                    move.EnginePower = 1;
                    move.WheelTurn *= -1;
                }
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