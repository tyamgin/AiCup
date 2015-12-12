using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Threading;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk 
{
    public class MagicConst
    {
        public const double BonusImportanceCoeff = 90;
        public const double OilSlickDangerCoeff = 70;
        public const double TireDangerCoeff = 120;
        public const double InactiveCarDangerCoeff = 55;
        public const double InactiveCarNitroDangerCoeff = 80;
        public const double ExactlyBorderDangerCoeff = 50;
        public const double SecondDistDangerCoeff = 80;
        public const double BackMoveDangerCoeff = 60;
        public const double OutOfBorederDangerCoeff = 80;

        public const double SafeMargin = 10.0;
        public const int BonusSafeMargin = 3;
        public const long TimerLogLimit = 5;

        public const int OpponentsTicksPrediction = 100;
        public const int ProjectilePredictionTicks = 60;
    }

    public class Const
    {
        public static Game Game;
        public static double TileSize;
        public static double TileMargin;
        public static double CarDiagonalHalfLength;
        public static double BonusDiagonalHalfLength;
        public static double MapWidth;
        public static double MapHeight;

        public static bool Initialized;
    }

    public partial class MyStrategy : IStrategy
    {
        public static bool BAD_TESTING_STRATEGY = false;

        public static World world;
        public Move move;
        public Car self;

        public static ATile[,] MyTiles;
        public static Cell[] Waypoints;
        public static Dictionary<long, Player> Players; 

        public static AProjectile[][] Tires;
        public static ABonus[] Bonuses;
        public static AOilSlick[] OilSlicks;

        public static Car[] Opponents;
        public static ACar[][] OpponentsCars, Others, MyTeam, All;

        public static Dictionary<long, ACar[]> ComputedPath = new Dictionary<long, ACar[]>();
        public static ACar[] TeammateCar;
        public static Car Teammate;

        public Points PositionsHistory = new Points();

        void Initialize()
        {
            if (!Const.Initialized)
            {
                Const.Initialized = true;

                Const.TileSize = Const.Game.TrackTileSize;
                Const.TileMargin = Const.Game.TrackTileMargin;

                Const.CarDiagonalHalfLength = Geom.Gypot(Const.Game.CarWidth, Const.Game.CarHeight) / 2;
                Const.BonusDiagonalHalfLength = Geom.Gypot(Const.Game.BonusSize / 2 - MagicConst.BonusSafeMargin, Const.Game.BonusSize / 2 - MagicConst.BonusSafeMargin);

                Const.MapWidth = Const.TileSize * world.Width;
                Const.MapHeight = Const.TileSize * world.Height;
            }

            Teammate = world.Cars.FirstOrDefault(car => car.IsTeammate && car.Id != self.Id);

            ComputedPath.Remove(self.Id);
            if (Teammate != null)
                TeammateCar = ComputedPath.ContainsKey(Teammate.Id) ? ComputedPath[Teammate.Id] : null;

            if (Players == null) // check for first call
            {
                MyTiles = new ATile[world.Height, world.Width];
                for (var i = 0; i < world.Height; i++)
                    for (var j = 0; j < world.Width; j++)
                        MyTiles[i, j] = new ATile(i, j, TileType.Unknown);
            }

            // intialize tiles
            var t = world.TilesXY;
            for (var i = 0; i < world.Height; i++)
            {
                for (var j = 0; j < world.Width; j++)
                {
                    if (MyTiles[i, j].Type == TileType.Unknown && t[j][i] != TileType.Unknown)
                        MyTiles[i, j] = new ATile(i, j, t[j][i]);

                    MyTiles[i, j].Weight = 0;
                }
            }

            // intialize waypoints
            var wp = world.Waypoints;
            Waypoints = new Cell[wp.Length];
            for(var i = 0; i < Waypoints.Length; i++)
                Waypoints[i] = new Cell(wp[i][1], wp[i][0]);

            Players = new Dictionary<long, Player>();
            foreach (var player in world.Players)
                Players[player.Id] = player;

            foreach (var car in world.Cars)
            {
                DurabilityObserver.Watch(car);
            }

            var tires = world.Projectiles.Where(x => x.Type == ProjectileType.Tire).ToArray();

            Tires = new AProjectile[tires.Length][];
#if DEBUG
            var trajectories = Tires.Select(x => new Points()).ToArray();
#endif
            for (var i = 0; i < tires.Length; i++)
            {
                Tires[i] = new AProjectile[MagicConst.ProjectilePredictionTicks];
                Tires[i][0] = new AProjectile(tires[i]);
                for (var j = 1; j < MagicConst.ProjectilePredictionTicks; j++)
                {
                    Tires[i][j] = Tires[i][j - 1].Clone();
                    Tires[i][j].Move();
#if DEBUG
                    trajectories[i].Add(new Point(Tires[i][j]));
#endif
                }
            }
#if DEBUG
            foreach (var tr in trajectories)
                Visualizer.SegmentsDrawQueue.Add(new object[] {Brushes.Indigo, tr, 0.0});
#endif

            Bonuses = world.Bonuses.Select(b => new ABonus(b)).ToArray();
            OilSlicks = world.OilSlicks.Select(s => new AOilSlick(s)).ToArray();

            EnumerateNeigbours(GetCell(self), to =>
            {
                var center = GetCenter(to);
                MyTiles[to.I, to.J].Weight += Math.Abs(self.GetAngleTo(center.X, center.Y));
            });

            foreach (var bonus in Bonuses)
            {
                var cell = GetCell(bonus);
                MyTiles[cell.I, cell.J].AddBonus(bonus);
            }
            foreach (var slick in OilSlicks)
            {
                var cell = GetCell(slick);
                MyTiles[cell.I, cell.J].AddSlick(slick);
            }

            Opponents = world.Cars.Where(car => !car.IsTeammate && !car.IsFinishedTrack).ToArray();
            PrepareOpponentsPath();
            if (OpponentsCars != null)
                Others = OpponentsCars
                    .Concat(TeammateCar == null ? new ACar[][] { } : new[] { TeammateCar })
                    .ToArray();

            ComputedPath.Clear();
            if (self.TeammateIndex == 1 && OpponentsCars != null)
            {
                foreach (var opp in OpponentsCars)
                    ComputedPath[opp[0].Original.Id] = opp;
            }
        }

        public void PrepareOpponentsPath()
        {
            if (world.Tick < Const.Game.InitialFreezeDurationTicks)
                return;

            TimerStart();
            var ways = Opponents.Select(GetWaySegments).ToArray();
#if DEBUG
            var segs = Opponents.Select(x => new Points()).ToArray();
#endif

            OpponentsCars = new ACar[Opponents.Length][];
            for (var i = 0; i < Opponents.Length; i++)
            {
                if (ComputedPath.ContainsKey(Opponents[i].Id))
                    OpponentsCars[i] = ComputedPath[Opponents[i].Id];
                else
                {
                    OpponentsCars[i] = new ACar[MagicConst.OpponentsTicksPrediction];
                    OpponentsCars[i][0] = new ACar(Opponents[i]);
                    for (var t = 1; t < MagicConst.OpponentsTicksPrediction; t++)
                    {
                        OpponentsCars[i][t] = OpponentsCars[i][t - 1].Clone();
                        _simulateOpponentMove(ways[i], OpponentsCars[i][t]);
#if DEBUG
                        segs[i].Add(new Point(OpponentsCars[i][t]));
#endif
                    }
                }
            }

#if DEBUG
            foreach (var seg in segs)
                Visualizer.SegmentsDrawQueue.Add(new object[] { Brushes.Indigo, seg, 0.0 });
#endif
            TimerEndLog("PrepareOpponentsPath");
        }

        public static bool ModelMove(ACar car, AMove m, bool simpleMode = false, bool exactlyBorders = false)
        {
            var prevStateX = car.X;
            var prevStateY = car.Y;
            var prevStateAngle = car.Angle;

            var turn = m.WheelTurn is Point ? TurnRound(car.GetAngleTo(m.WheelTurn as Point)) : Convert.ToDouble(m.WheelTurn);
            car.Move(m.EnginePower, turn, m.IsBrake, m.IsUseNitro, simpleMode);
            var ok = car.GetRect(0).All(p => !IntersectTail(p, exactlyBorders ? 0 : MagicConst.SafeMargin));
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

        private Tuple<int, Moves[]> _doAndSelectBrute(PathBruteForcer[] brutes, Points pts)
        {
            TimerStart();
            var bestMoveStacks = new Moves[brutes.Length];
            for (var i = 0; i < brutes.Length; i++)
            {
                if (brutes[i].LastStageMove.IsUseNitro && self.NitroChargeCount == 0)
                    continue;
                if (brutes[i].LastStageMove.IsUseNitro && self.RemainingOiledTicks > 0)
                    continue;

                bestMoveStacks[i] = brutes[i].Do(new ACar(self), pts);
            }

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

            TimerEndLog("brute");
            return new Tuple<int, Moves[]>(sel, bestMoveStacks);
        }

        private void _move()
        {
            var pts = GetWaySegments(self);

            if (world.Tick > Const.Game.InitialFreezeDurationTicks)
                PositionsHistory.Add(new Point(self));

            if (!DurabilityObserver.IsActive(self))
                return;

            if (CheckBackMove(pts[1]))
                return;

            InitBrutes();

            if (world.Tick < Const.Game.InitialFreezeDurationTicks)
            {
                var nextCell = DijkstraNextCell(GetCell(self), GetNextWayPoint(self), new Cell[] {});
                var nextCenter = GetCenter(nextCell);
                move.EnginePower = self.GetAngleTo(nextCenter.X, nextCenter.Y) < Math.PI/2 ? 1 : -1;
                return;
            }

            if (BAD_TESTING_STRATEGY)
            {
                AlternativeMove(pts);
                return;
            }

            // если еду назад, то запускать только первый перебор
            // если маленькая скорость, то 1-й и 2-й
            var bruteRes =
                _doAndSelectBrute(
                    self.EnginePower < 0
                        ? new[] {Brutes[0]}
                        : (GetSpeed(self) < 5 ? new[] {Brutes[0], Brutes[1]} : Brutes), pts);

            var sel = bruteRes.Item1;
            var bestMoveStacks = bruteRes.Item2;

            if (sel != -1 && bestMoveStacks[sel].Count > 0)
            {
                Brutes[sel].SelectThis();
                bestMoveStacks[sel][0].Apply(move, new ACar(self));
                ComputedPath[self.Id] = GetCarPath(self, bestMoveStacks[sel]);
#if DEBUG
                Visualizer.DrawWays(self, bestMoveStacks, sel);
#endif
            }
            else
            {
                TimerStart();
                AlternativeMove(pts);
                TimerEndLog("AlternativeMove", 30);
            }
        }

        public static ACar[] GetCarPath(Car self, Moves stack)
        {
            if (stack == null)
                return null;

            stack = stack.Clone();
            var res = new List<ACar>();
            var model = new ACar(self);
            while (stack.Count > 0)
            {
                var m = stack[0];
                AMove.ModelMove(model, m, new PassedInfo(), Bonuses, OilSlicks, Tires, Others);
                m.Times--;
                stack.Normalize();
                res.Add(model.Clone());
            }
            return res.ToArray();
        }

        public void Move(Car self, World world, Game game, Move move)
        {
            TimerStart();
            MyStrategy.world = world;
            Const.Game = game;
            this.move = move;
            this.self = self;
            Initialize();

#if DEBUG
            while (Visualizer.Pause)
            {
                // pause here
            }
            Visualizer.CreateForm(world.Cars.Count(x => x.IsTeammate));
#endif
            if (!self.IsFinishedTrack)
            {
                All = null;
                MyTeam = null;

                _move();

                if (OpponentsCars != null)
                {
                    var myTeam = new List<ACar[]>();
                    if (!ComputedPath.ContainsKey(self.Id))
                        ComputedPath[self.Id] = new int[MagicConst.OpponentsTicksPrediction].Select(x => new ACar(self)).ToArray();
                    myTeam.Add(ComputedPath[self.Id]);
                    if (TeammateCar != null)
                        myTeam.Add(TeammateCar);
                    MyTeam = myTeam.ToArray();

                    All = MyTeam.Concat(OpponentsCars).ToArray();

                    TimerStart();
                    if (CheckUseProjectile())
                        move.IsThrowProjectile = true;
                    if (CheckUseOil())
                        move.IsSpillOil = true;

                    TimerEndLog("CheckUseProjectile", 2);
                }
            }
            else if (_finishTime == Infinity)
                _finishTime = world.Tick;
            if (_finishTime < Infinity)
                Log(_finishTime);

#if DEBUG
            if (move.IsBrake)
                Visualizer.CircleFillQueue.Add(new Tuple<Brush, ACircularUnit>(Brushes.Red,
                    new ACircularUnit {X = self.X, Y = self.Y, Radius = 30}));


            TimerEndLog("All");

            if (Brutes != null)
            {
                for (var i = 0; i < Brutes.Length; i++)
                {
                    var info = Brutes[i].GetMaxTicksInfo();
                    if (info == null)
                        continue;
                    Console.Write(i + ": ");
                    foreach (var a in info)
                        Console.Write(" " + a);
                    Console.WriteLine("(" + Brutes[i].SelectedCount + ")");
                }
            }
            Console.WriteLine();
            Visualizer.Draw();
            Thread.Sleep(12);
#endif
        }

        private int _finishTime = Infinity;
    }
}