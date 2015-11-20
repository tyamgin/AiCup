using System.Collections.Generic;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    class DurabilityObserver
    {
        private static Dictionary<long, int> _remainTicks = new Dictionary<long, int>();

        public static void Watch(Car car)
        {
            if (car.Durability > MyStrategy.Eps)
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
            return MyStrategy.game.CarReactivationTimeTicks + _remainTicks[car.Id];
        }

        public static bool IsActive(Car car)
        {
            return car.Durability > MyStrategy.Eps;
        }
    }
}
