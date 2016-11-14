using System.Collections.Generic;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    class BuildingsObserver
    {
        private static Dictionary<long, ABuilding> _prevState = new Dictionary<long, ABuilding>();
        public static List<ABuilding> NewBuildings = new List<ABuilding>(), DisappearedBuildings = new List<ABuilding>();

        private static long _getCoordinatesKey(Point p)
        {
            var mx = (long) (Const.MapSize + Const.Eps);
            var x = (long) p.X;
            var y = (long) p.Y;
            return x*mx + y;
        }

        public static void Update(World world)
        {
            var newState = new Dictionary<long, ABuilding>();
            NewBuildings.Clear();
            DisappearedBuildings.Clear();

            foreach (var bld in world.Buildings)
            {
                var a = new ABuilding(bld);
                var key = _getCoordinatesKey(a);
                if (!_prevState.ContainsKey(key))
                {
                    // новое здание
                    NewBuildings.Add(a);
                }
                newState[key] = a;

                if (world.TickIndex == 0)
                {
                    var opposit = new ABuilding(bld);
                    opposit.X = Const.MapSize - opposit.X;
                    opposit.Y = Const.MapSize - opposit.Y;
                    opposit.Faction = opposit.Faction == Faction.Academy ? Faction.Renegades : Faction.Academy;
                    opposit.Id *= -1;
                    opposit.IsTeammate = false;
                    var oppositKey = _getCoordinatesKey(opposit);
                    if (!_prevState.ContainsKey(oppositKey))
                    {
                        // новое здание
                        NewBuildings.Add(opposit);
                    }
                    newState[oppositKey] = opposit;
                }
            }

            foreach (var it in _prevState)
            {
                var key = _getCoordinatesKey(it.Value);
                if (!MyStrategy.IsPointVisible(it.Value))
                {
                    // его не видно, считаем что осталось
                    newState[key] = it.Value;
                }
                else if (!newState.ContainsKey(key))
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
