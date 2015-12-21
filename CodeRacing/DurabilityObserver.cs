using System.Collections.Generic;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    class DurabilityObserver
    {
        private static Dictionary<long, int> _remainTicks = new Dictionary<long, int>();

        public static void Watch(Car car)
        {
            if (IsActive(car))
            {
                _remainTicks.Remove(car.Id);
                return;
            }
            if (!_remainTicks.ContainsKey(car.Id))
                _remainTicks[car.Id] = MyStrategy.world.Tick;
        }

        public static int ReactivationTime(Car car)
        {
            if (!_remainTicks.ContainsKey(car.Id))
                return MyStrategy.world.Tick;
            return Const.Game.CarReactivationTimeTicks + _remainTicks[car.Id];
        }

        public static bool IsActive(Car car)
        {
            return car.Durability > MyStrategy.Eps;
        }
    }
}
