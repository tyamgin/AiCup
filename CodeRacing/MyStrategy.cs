using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Threading;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk 
{
    public class Const
    {
        
    }

    public partial class MyStrategy : IStrategy
    {
        public static bool BAD_TESTING_STRATEGY = false;

        public static World world;
        public static Game game;
        public Move move;
        public Car self;

        public static ATile[,] MyTiles;
        public static Cell[] Waypoints;
        public static double MapWidth, MapHeight;
        public static Dictionary<long, Player> Players; 

        public const double SafeMargin = 10.0;
        public const long TimerLogLimit = 5;

        public static double CarDiagonalHalfLength;
        public static double BonusDiagonalHalfLength;

        public const int OpponentsTicksPrediction = 100;
        public const int ProjectilePredictionTicks = 60;

        public static AProjectile[][] Tires;
        public static ABonus[] Bonuses;
        public static AOilSlick[] OilSlicks;

        public static Car[] Opponents;
        public static ACar[][] OpponentsCars, Others, MyTeam, All;

        public static Dictionary<long, ACar[]> TeammatePath = new Dictionary<long, ACar[]>();
        public static ACar[] TeammateCar;
        public static Car Teammate;

        public Points PositionsHistory = new Points();

        void Initialize()
        {
            Teammate = world.Cars.FirstOrDefault(car => car.IsTeammate && car.Id != self.Id);

            TeammatePath.Remove(self.Id);
            if (Teammate != null)
                TeammateCar = TeammatePath.ContainsKey(Teammate.Id) ? TeammatePath[Teammate.Id] : null;

            TeammatePath.Clear();

            if (Players == null) // check for first call
            {
                MapWidth = game.TrackTileSize * world.Width;
                MapHeight = game.TrackTileSize * world.Height;

                CarDiagonalHalfLength = Geom.Gypot(game.CarWidth, game.CarHeight) / 2;
                BonusDiagonalHalfLength = Geom.Gypot(game.BonusSize/2 - ABonus.SafeMargin, game.BonusSize/2 - ABonus.SafeMargin);

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
                Tires[i] = new AProjectile[ProjectilePredictionTicks];
                Tires[i][0] = new AProjectile(tires[i]);
                for (var j = 1; j < ProjectilePredictionTicks; j++)
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
            var ok = car.GetRect(0).All(p => !IntersectTail(p, exactlyBorders ? 0 : SafeMargin));
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

            if (world.Tick > game.InitialFreezeDurationTicks)
                PositionsHistory.Add(new Point(self));

            if (!DurabilityObserver.IsActive(self))
                return;

            if (CheckBackMove(pts[1]))
                return;

            if (CheckUseOil())
                move.IsSpillOil = true;

            InitBrutes();

            if (world.Tick < game.InitialFreezeDurationTicks)
            {
                move.EnginePower = 1;
                return;
            }

            if (BAD_TESTING_STRATEGY)
            {
                AlternativeMove(pts);
                return;
            }

            // ���� ��� �����, �� ��������� ������ ������ �������
            // ���� ��������� ��������, �� 1-� � 2-�
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
                TeammatePath[self.Id] = GetCarPath(self, bestMoveStacks[sel]);
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
            MyStrategy.game = game;
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
                    if (TeammatePath.ContainsKey(self.Id))
                        myTeam.Add(TeammatePath[self.Id]);
                    if (TeammateCar != null)
                        myTeam.Add(TeammateCar);
                    MyTeam = myTeam.ToArray();

                    All = MyTeam.Concat(OpponentsCars).ToArray();

                    TimerStart();
                    if (CheckUseProjectile())
                        move.IsThrowProjectile = true;
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