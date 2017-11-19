using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class MoveObserver
    {
        public static Dictionary<long, AVehicle> BeforeMoveUnits = new Dictionary<long, AVehicle>();

        public class HistoryItem
        {
            public int TickIndex;
            public AMove Move;
        }

        public static readonly List<HistoryItem> History = new List<HistoryItem>();
        public static int AvailableActions;
        public static double Rate;

        public static void Init()
        {
            var maxAvailableActions = 12 + MyStrategy.World.Facilities.Count(
                x => x.Type == FacilityType.ControlCenter &&
                     x.OwnerPlayerId == MyStrategy.Me.Id)*G.AdditionalActionCountPerControlCenter;
            AvailableActions = maxAvailableActions;
            Rate = 0;

            var tick = MyStrategy.World.TickIndex;

            for (var i = History.Count - 1; i >= 0; i--)
            {
                var historyItem = History[i];
                if (historyItem.TickIndex <= tick - G.ActionDetectionInterval)
                    break;
                AvailableActions--;
                Rate += (historyItem.TickIndex - (tick - G.ActionDetectionInterval)) / (G.ActionDetectionInterval - 1.0);
            }
            Rate /= maxAvailableActions;

            //Logger.Log("----------------------- Rate: " + Rate);
            //Logger.Log("----------------------- Actions: " + AvailableActions);
        }

        public static void Update()
        {
            var move = MyStrategy.ResultingMove;
            MyStrategy.Environment.ApplyMove(move);
            BeforeMoveUnits.Clear();
            foreach (var veh in MyStrategy.Environment.Vehicles)
                BeforeMoveUnits[veh.Id] = new AVehicle(veh);
            MyStrategy.Environment.DoTick();

            if (move.Action != null && move.Action != ActionType.None)
                History.Add(new HistoryItem {TickIndex = MyStrategy.World.TickIndex, Move = move});

            if (move.Action != null && move.Action != ActionType.None && MyStrategy.Me.RemainingActionCooldownTicks > 0)
                throw new Exception("Trying to do action when RemainingActionCooldownTicks=" + MyStrategy.Me.RemainingActionCooldownTicks);
        }
    }
}
