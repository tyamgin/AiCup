using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Drawing;
using System.Linq;
using System.Runtime.InteropServices;
using System.Threading;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk 
{
    // TODO: обработать столкновения с бонусами и убрать костыль

    public partial class MyStrategy : IStrategy
    {
        public static bool BAD_TESTING_STRATEGY = false;

        public static World world;
        public static Game game;
        public Move move;
        public Car self;
        public static TileType[,] tiles;
        public static ATile[,] MyTiles;
        public Cell[] waypoints;
        public static double MapWidth, MapHeight;
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

            if (MyTiles == null)
            {
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

            Opponents = world.Cars.Where(car => !car.IsTeammate && !car.IsFinishedTrack).ToArray();
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

        //private AProjectile pr;

        private Tuple<int, Moves[]> _doAndSelectBrute(PathBruteForce[] brutes, Points pts)
        {
            TimerStart();
            var bestMoveStacks = new Moves[brutes.Length];
            for (var i = 0; i < brutes.Length; i++)
            {
                if (!BAD_TESTING_STRATEGY)
                {
                    if (brutes[i].LastStageMove.IsUseNitro && self.NitroChargeCount == 0)
                        continue;
                    if (brutes[i].LastStageMove.IsUseNitro && self.RemainingOiledTicks > 0)
                        continue;

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

            return new Tuple<int, Moves[]>(sel, bestMoveStacks);
        }

        private void _move()
        {
            var pts = GetWaySegments(self);

            if (world.Tick > game.InitialFreezeDurationTicks)
                PositionsHistory.Add(new Point(self));

            if (!DurabilityObserver.IsActive(self))
                return;

            if (CheckUseProjectile())
                move.IsThrowProjectile = true;

            if (CheckUseOil())
                move.IsSpillOil = true;

            InitBrutes();

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


            var bruteRes = _doAndSelectBrute(Brutes, pts);
            var sel = bruteRes.Item1;
            var bestMoveStacks = bruteRes.Item2;

            if (sel != -1 && bestMoveStacks[sel].Count > 0) // FIXME!!!!!!!!!!!!!!!! Пропуск waypoint'a
            {
                Brutes[sel].SelectThis();
                bestMoveStacks[sel][0].Apply(move, new ACar(self));
#if DEBUG
                DrawWays(bestMoveStacks, sel);
#endif
            }
            else
            {
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
            //if (world.Tick >= 350)
            //    BAD_TESTING_STRATEGY = true;
            if (!self.IsFinishedTrack)
                _move();
            else if (_finishTime == Infinity)
                _finishTime = world.Tick;
            if (_finishTime < Infinity)
                Log(_finishTime);

#if DEBUG
            TimeEndLog("All");
            for (var i = 0; i < Brutes.Length; i++)
            {
                var info = Brutes[i].GetMaxTicksInfo();
                if (info == null)
                    continue;
                Console.Write(i + ": ");
                foreach(var a in info)
                    Console.Write(" " + a);
                Console.WriteLine("\n");
            }
            Draw();
            Thread.Sleep(12);
#endif
        }

        private int _finishTime = Infinity;
    }
}