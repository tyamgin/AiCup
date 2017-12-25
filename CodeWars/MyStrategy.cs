using System;
using System.Collections.Generic;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;
using System.Diagnostics;
using System.Globalization;
using System.Linq;
using System.Threading;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public static World World;
        public static Player Me, Opp;
        public static AMove ResultingMove;

        public static TerrainType[][] TerrainType;
        public static WeatherType[][] WeatherType;
        public static int[][] FacilityIdx;
        public static Sandbox Environment;
        public static List<VehiclesCluster> OppClusters;
        public static List<VehiclesCluster> MyUngroupedClusters;

        private readonly Stopwatch _globalTimer = new Stopwatch();
        private int _myLastScore;
        private int _oppLastScore;

        ~MyStrategy()
        {
            _globalTimer.Stop();
            Console.WriteLine($"Last score: {_myLastScore}:{_oppLastScore}");
            Console.WriteLine("Total time: " + _globalTimer.ElapsedMilliseconds + " ms");
        }

        public MyStrategy()
        {
            _globalTimer.Start();
#if DEBUG // <-- на сервере почему-то падает
            CultureInfo.DefaultThreadCurrentCulture = CultureInfo.InvariantCulture;
#endif
        }

        public void Move(Player me, World world, Game game, Move move)
        {
            World = world;
            Me = me;
            Opp = world.GetOpponentPlayer();
            ResultingMove = new AMove();

            _myLastScore = Me.Score;
            _oppLastScore = Opp.Score;


#if DEBUG
            while (Visualizer.Visualizer.Pause)
            {
                // pause here
                Thread.Sleep(20);
            }
#endif
            Logger.TimerStart();
            if (Me.NextNuclearStrikeVehicleId != -1)
                Logger.TimerStart();

            _move(game);
            Logger.CumulativeOperationPrintAndReset(2);
            if (world.TickIndex % 500 == 0)
                Logger.CumulativeOperationSummary();
            ResultingMove.ApplyTo(move);

            if (Me.NextNuclearStrikeVehicleId != -1)
                Logger.TimerEndLog("AllN", 1);
            Logger.TimerEndLog("All", 0);
#if DEBUG
            Visualizer.Visualizer.CreateForm();

            if (World.TickIndex == 0)
            {
                Visualizer.Visualizer.LookAt = new Point(0, 0);
                Visualizer.Visualizer.Zoom = 0.5;
            }
            Visualizer.Visualizer.SegmentsDrawQueue.AddRange(VisualSegments);
            Visualizer.Visualizer.Draw();
            if (world.TickIndex >= Visualizer.Visualizer.DrawSince)
            {
                var timer = new Stopwatch();
                timer.Start();
                while (!Visualizer.Visualizer.Done/* || timer.ElapsedMilliseconds < 13*/)
                {
                    Thread.Sleep(10);
                }
                timer.Stop();
            }
#endif

            MoveObserver.Update();
        }

        private int _doMainsCount = 0;
        private MyGroup _doMainLastGroup;
        private readonly Dictionary<int, Tuple<int, AMove>> _doMainLastUnscale = new Dictionary<int, Tuple<int, AMove>>();

        private void _move(Game game)
        {
            Const.Initialize(World, game);
            Initialize();

            GroupsManager.Update(Environment);

            if (World.TickIndex == 0)
                MoveFirstTicks();
            ActionsQueue.Process();
            var ret = !MoveQueue.Free;
            MoveQueue.Run();
            if (ret)
                return;

            if (Me.RemainingActionCooldownTicks > 0)
                return;

            if (GroupsManager.MyGroups.Count == 0)
                return;

            var actionsBaseInterval = MoveObserver.ActionsBaseInterval;
            if (MyUngroupedClusters.Any(x => x.Count >= NewGroupMinSize))
                actionsBaseInterval++;
            if (World.TickIndex % actionsBaseInterval == 0 
                || MoveObserver.AvailableActions >= 4 && FirstMovesCompleted && World.TickIndex >= _noMoveLastTick + actionsBaseInterval
                || Environment.Nuclears.Any(x => x.RemainingTicks >= G.TacticalNuclearStrikeDelay - 2))
            {
                var nuclearMove = NuclearStrategy();
                if (nuclearMove != null)
                {
                    ResultingMove = nuclearMove;
                    return;
                }

                Logger.CumulativeOperationStart("Unstuck");
                var unstuckMove = UnstuckStrategy();
                Logger.CumulativeOperationEnd("Unstuck");
                if (unstuckMove != null)
                {
                    Console.WriteLine("Unstuck");
                    ResultingMove = unstuckMove[0];
                    for (var i = 1; i < unstuckMove.Length; i++)
                        MoveQueue.Add(unstuckMove[i]);
                    return;
                }

                var mainMove = MainLoopStrategy(true);
                _doMainsCount++;
                _doMainLastGroup = mainMove.Item2;
                foreach (var move in mainMove.Item1)
                    if (move.Action == ActionType.Scale && move.Factor > 1)
                        _doMainLastUnscale[mainMove.Item2.Group] = new Tuple<int, AMove>(World.TickIndex, move);
                 foreach (var groupId in _doMainLastUnscale.Keys.ToArray())
                     if (_doMainLastUnscale[groupId].Item1 + 10 + G.TacticalNuclearStrikeDelay < World.TickIndex)
                         _doMainLastUnscale.Remove(groupId);

                if (mainMove.Item1[0].Action == null || mainMove.Item1[0].Action == ActionType.None)
                    _noMoveLastTick = World.TickIndex;

                ResultingMove = mainMove.Item1[0];
                for (var i = 1; i < mainMove.Item1.Length; i++)
                {
                    var mv = mainMove.Item1[i];
                    MoveQueue.Add(mv);
                    if (mv.Action == ActionType.Assign && mainMove.Item1[0].VehicleType != null)
                    {
                        GroupsManager.AddPendingGroup(new MyGroup(mv.Group, mainMove.Item1[0].VehicleType.Value));
                    }
                }
                if (mainMove.Item1.Length >= 3)
                {
                    var nearestFacility = Environment.Facilities.ArgMin(
                        f => f.Center.GetDistanceTo2(ResultingMove.Rect.Center));
                    var changeProdMove = ChangeFactoryProduction(nearestFacility);
                    if (changeProdMove != null)
                        MoveQueue.Add(changeProdMove);
                }
            }

            if (ResultingMove == null || ResultingMove.Action == null || ResultingMove.Action == ActionType.None)
            {
                if (FirstMovesCompleted)
                {
                    var facilitiesMove = FacilitiesStrategy();
                    if (facilitiesMove != null)
                        ResultingMove = facilitiesMove;
                }
            }
        }

        private int _noMoveLastTick = -Const.Infinity;
    }
}