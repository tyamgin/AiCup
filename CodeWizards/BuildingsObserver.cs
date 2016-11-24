using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    class BuildingsObserver
    {
        private static Dictionary<long, ABuilding> _prevState = new Dictionary<long, ABuilding>();
        public static List<ABuilding> NewBuildings = new List<ABuilding>(), DisappearedBuildings = new List<ABuilding>();
        public static ABuilding OpponentBase, MyBase;

        private static long _getCoordinatesKey(Point p)
        {
            var mx = (long) (Const.MapSize + Const.Eps);
            var x = (long) p.X;
            var y = (long) p.Y;
            return x*mx + y;
        }

        public static void Update()
        {
            var newState = new Dictionary<long, ABuilding>();
            NewBuildings.Clear();
            DisappearedBuildings.Clear();

            foreach (var bld in MyStrategy.World.Buildings)
            {
                var a = new ABuilding(bld);
                var key = _getCoordinatesKey(a);
                if (!_prevState.ContainsKey(key))
                {
                    // новое здание
                    NewBuildings.Add(a);
                }
                newState[key] = a;

                if (MyStrategy.World.TickIndex == 0)
                {
                    var opposit = new ABuilding(bld);
                    opposit.X = Const.MapSize - opposit.X;
                    opposit.Y = Const.MapSize - opposit.Y;
                    opposit.Faction = opposit.Faction == Faction.Academy ? Faction.Renegades : Faction.Academy;
                    opposit.Id *= -1; // temp Id
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
                var bld = it.Value;
                var key = _getCoordinatesKey(bld);
                if (!MyStrategy.IsPointVisible(bld))
                {
                    // его не видно, считаем что осталось
                    if (bld.RemainingActionCooldownTicks == 0)
                    {
                        // значит, если кто-то был поблизости, башня 100% выстрелила
                        if (MyStrategy.Combats.Any(x => x.IsTeammate && bld.GetDistanceTo2(x) <= Geom.Sqr(bld.CastRange)))
                        {
                            bld.RemainingActionCooldownTicks = (bld.IsBase
                                ? MyStrategy.Game.FactionBaseCooldownTicks
                                : MyStrategy.Game.GuardianTowerCooldownTicks) - 1;
                        }
                    }
                    else
                    {
                        bld.RemainingActionCooldownTicks--;
                    }
                    newState[key] = bld;
                }
                else if (!newState.ContainsKey(key))
                {
                    // видно, но исчезло
                    DisappearedBuildings.Add(bld);
                }
            }

            _prevState = newState;

            OpponentBase = Buildings.FirstOrDefault(x => x.IsBase && x.IsOpponent);
            MyBase = Buildings.FirstOrDefault(x => x.IsBase && x.IsTeammate);

            foreach (var building in Buildings)
            {
                if (building.IsBase)
                {
                    building._isAssailable = Buildings.Count(x => x.Faction == building.Faction && x.Order == 1) < 3;
                }
                else
                {
                    building._isAssailable =
                        Buildings.Count(
                            x =>
                                x.Faction == building.Faction && x.Order + 1 == building.Order &&
                                x.Lane == building.Lane) == 0;
                }
            }
        }

        public static IEnumerable<ABuilding> Buildings => _prevState.Values;
    }
}
