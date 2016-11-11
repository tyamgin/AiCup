using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    class BuildingsObserver
    {
        private static Dictionary<long, ABuilding> _prevState = new Dictionary<long, ABuilding>();
        public static List<ABuilding> NewBuildings = new List<ABuilding>(), DisappearedBuildings = new List<ABuilding>();

        public static void Update(World world)
        {
            var newState = new Dictionary<long, ABuilding>();
            NewBuildings.Clear();
            DisappearedBuildings.Clear();

            foreach (var bld in world.Buildings)
            {
                var a = new ABuilding(bld);
                if (!_prevState.ContainsKey(bld.Id))
                {
                    // новое здание
                    NewBuildings.Add(a);
                }
                newState[bld.Id] = a;
            }

            foreach (var it in _prevState)
            {
                if (!MyStrategy.IsPointVisible(it.Value))
                {
                    // его не видно, считаем что осталось
                    newState[it.Key] = it.Value;
                }
                else if (!newState.ContainsKey(it.Key))
                {
                    // видно, но исчезло
                    DisappearedBuildings.Add(it.Value);
                }
            }

            _prevState = newState;
        }

        public static IEnumerable<ABuilding> Buildings => _prevState.Values;
    }
}
