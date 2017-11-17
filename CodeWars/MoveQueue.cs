using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class MoveQueue
    {
        private class MoveItem
        {
            public AMove Move;
            public int DelayBefore;
            public int DelayAfter;
        }

        private static List<MoveItem> _queue = new List<MoveItem>();
        private static int _delayAfter = 0;

        public static void Add(AMove move, int delayBefore, int delayAfter)
        {
            _queue.Add(new MoveItem { Move = move, DelayBefore = delayBefore, DelayAfter = delayAfter });
        }

        public static void Run()
        {
            if (_delayAfter > 0)
            {
                _delayAfter--;
                return;
            }

            if (_queue.Count == 0 || MyStrategy.Me.RemainingActionCooldownTicks > 0)
                return;
            var action = _queue[0];
            if (action.DelayBefore > 0)
            {
                action.DelayBefore--;
                return;
            }
            
            MyStrategy.ResultingMove = action.Move;
            _delayAfter = action.DelayAfter;
            _queue.RemoveAt(0);
        }

        public static bool Free => _queue.Count == 0 && _delayAfter == 0;
    }
}
