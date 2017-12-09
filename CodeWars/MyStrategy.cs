using System;
using System.Collections.Generic;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;
using System.Diagnostics;
using System.Globalization;
using System.Linq;
using System.Threading;

/**
 * TODO:
 * - лечение в ПП
 * - прятать вертолеты от самолетов
 * - оптимизировать начальное построение (?)
 * 
 * - ядерка: перебрать несколько центров, учитывать уклонения
 * - ядерка: убивают крайнего
 */

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

        Stopwatch _globalTimer = new Stopwatch();
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
#if DEBUG
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

            //if (World.TickIndex == 200)
            //{
            //    var avg = Utility.Average(Environment.GetVehicles(true, VehicleType.Ifv));
            //    var ifv = Environment.GetVehicles(true, VehicleType.Ifv).ArgMin(x => x.GetDistanceTo2(avg));
            //    ResultingMove = new AMove {Action = ActionType.TacticalNuclearStrike, VehicleId = ifv.Id, Point = ifv};
            //    return;
            //}
            //if (World.TickIndex == 231)
            //{
            //    var sum = Environment.MyVehicles.Sum(x => x.Durability);
            //    sum = sum;
            //}

            var actionsBaseInterval = MoveObserver.ActionsBaseInterval;
            if (MyUngroupedClusters.Any(x => x.Count >= NewGroupMinSize))
                actionsBaseInterval++;
            if (World.TickIndex % actionsBaseInterval == 0 
                || MoveObserver.AvailableActions >= 4 && FirstMovesComplete && World.TickIndex >= _noMoveLastTick + actionsBaseInterval
                || Environment.Nuclears.Any(x => x.RemainingTicks >= G.TacticalNuclearStrikeDelay - 2))
            {
                var nuclearMove = NuclearStrategy();
                if (nuclearMove != null)
                {
                    ResultingMove = nuclearMove;
                    return;
                }

                var mainNew = DoMainLoop(true);
                _doMainsCount++;
                _doMainLastGroup = mainNew.Item2;

                if (mainNew.Item1[0].Action == null || mainNew.Item1[0].Action == ActionType.None)
                    _noMoveLastTick = World.TickIndex;

                ResultingMove = mainNew.Item1[0];
                for (var i = 1; i < mainNew.Item1.Length; i++)
                {
                    var mv = mainNew.Item1[i];
                    MoveQueue.Add(mv);
                    if (mv.Action == ActionType.Assign && mainNew.Item1[0].VehicleType != null)
                    {
                        GroupsManager.AddPendingGroup(new MyGroup(mv.Group, mainNew.Item1[0].VehicleType.Value));
                    }
                }
                if (mainNew.Item1.Length >= 3)
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
                if (FirstMovesComplete)
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