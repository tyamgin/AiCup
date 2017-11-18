using System;
using System.Collections.Generic;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class MoveObserver
    {
        public static Dictionary<long, AVehicle> BeforeMoveUnits = new Dictionary<long, AVehicle>();


        public static void Update()
        {
            var move = MyStrategy.ResultingMove;
            MyStrategy.Environment.ApplyMove(move);
            BeforeMoveUnits.Clear();
            foreach (var veh in MyStrategy.Environment.Vehicles)
                BeforeMoveUnits[veh.Id] = new AVehicle(veh);
            MyStrategy.Environment.DoTick();

            if (move.Action != null && move.Action != ActionType.None && MyStrategy.Me.RemainingActionCooldownTicks > 0)
                throw new Exception("Trying to do action when RemainingActionCooldownTicks=" + MyStrategy.Me.RemainingActionCooldownTicks);
        }
    }
}
