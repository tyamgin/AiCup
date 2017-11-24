using System.Collections.Generic;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class MoveQueue
    {
        private class MoveItem
        {
            public AMove Move;
        }

        private static readonly List<MoveItem> _queue = new List<MoveItem>();

        public static void Add(AMove move)
        {
            _queue.Add(new MoveItem { Move = move });
        }

        public static void Run()
        {
            if (_queue.Count == 0 || MyStrategy.Me.RemainingActionCooldownTicks > 0)
                return;
            var action = _queue[0];
            
            MyStrategy.ResultingMove = action.Move;
            _queue.RemoveAt(0);
        }

        public static bool Free => _queue.Count == 0;
    }
}
