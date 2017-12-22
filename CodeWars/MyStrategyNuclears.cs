using System;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public AMove NuclearStrategy()
        {
            AMove result = null;
            if (Me.RemainingNuclearStrikeCooldownTicks == 0)
            {
                Logger.CumulativeOperationStart("NuclearStrategy");
                result = _nuclearStrategy();
                Logger.CumulativeOperationEnd("NuclearStrategy");
            }
            return result;
        }

        private double _nuclearGetDamage(AVehicle veh, Sandbox env, double lowerBound, List<AVehicle> targets, out ANuclear nuclearResult)
        {
            var vr = veh.ActualVisionRange * 0.9;
            var cen = Utility.Average(targets);
            cen = veh + (cen - veh).Normalized() * Math.Min(vr, veh.GetDistanceTo(cen));
            var nuclear = new ANuclear(cen.X, cen.Y, true, veh.Id, G.TacticalNuclearStrikeDelay);
            nuclearResult = nuclear;

            var totalOpponentDamage = targets.Sum(x => x.GetNuclearDamage(nuclear));
            if (totalOpponentDamage <= lowerBound)
                return totalOpponentDamage;

            var totalDamage = totalOpponentDamage -
                  env.GetMyNeighbours(nuclear.X, nuclear.Y, nuclear.Radius)
                      .Sum(x => x.GetNuclearDamage(nuclear));

            return totalDamage;
        }

        private Tuple<double, AMove> _nuclearFindMove(Sandbox env, double selTotalDamage, bool checkOnly)
        {
            AMove selMove = null;

            for (var s = 0; s < GroupsManager.MyGroups.Count + MyUngroupedClusters.Count; s++)
            {
                var vehicles = s < GroupsManager.MyGroups.Count
                    ? env.GetVehicles(true, GroupsManager.MyGroups[s])
                    : MyUngroupedClusters[s - GroupsManager.MyGroups.Count];
                var myAvg = Utility.Average(vehicles);

                var vrg = G.VisionRange[(int) VehicleType.Fighter] + G.MaxTacticalNuclearStrikeDamage;

                var oppGroups = OppClusters
                    .Where(cl =>
                        cl.Avg.GetDistanceTo(myAvg) < vrg
                        && cl.Any(x => env.VehicleById.ContainsKey(x.Id)))// пропускать полностью фантомные группы
                    .OrderBy(cl => cl.Avg.GetDistanceTo(myAvg))
                    .Take(3)
                    .ToArray();

                foreach (var veh in vehicles)
                {
                    if (!veh.IsSelected && MoveObserver.AvailableActions < 3)
                        continue;
                    if (veh.IsSelected && MoveObserver.AvailableActions < 2)
                        continue;

                    var vr = veh.ActualVisionRange * 0.9;

                    foreach (
                        var oppGroup in
                            new[] {env.GetOpponentNeighbours(veh.X, veh.Y, vr + G.TacticalNuclearStrikeRadius)}.Concat(oppGroups))
                    {
                        ANuclear nuclear;
                        var totalDamage = _nuclearGetDamage(veh, env, selTotalDamage, oppGroup, out nuclear);

                        if (totalDamage <= selTotalDamage)
                            continue;

                        var vehNextMove = new AVehicle(veh);
                        for (var t = 0; t < 3; t++)
                            vehNextMove.Move();
                        if (vehNextMove.GetDistanceTo2(nuclear) + Const.Eps >= Geom.Sqr(vehNextMove.ActualVisionRange))
                            continue;

                        const int n = 10;
                        if (vehicles.Count > n)
                        {
                            var myDist2 = veh.GetDistanceTo2(nuclear);
                            var myNearestCount = vehicles.Count(x => x.GetDistanceTo2(nuclear) <= myDist2);
                            if (myNearestCount < n)
                                continue;
                        }

                        selTotalDamage = totalDamage;
                        selMove = new AMove
                        {
                            Action = ActionType.TacticalNuclearStrike,
                            VehicleId = veh.Id,
                            Point = nuclear,
                        };

                        if (checkOnly)
                            return new Tuple<double, AMove>(selTotalDamage, selMove);
                    }
                }
            }

            if (selMove == null)
            {
                return null;
            }

            return new Tuple<double, AMove>(selTotalDamage, selMove);
        }

        private AMove _nuclearStrategy()
        {
            var countMultiplier = Math.Min(500, Environment.OppVehicles.Count
                + VehiclesObserver.OppUncheckedVehicles.Count*0.85 
                + VehiclesObserver.OppCheckedVehicles.Count*0.75);

            var damageBound2 = 8000.0*countMultiplier/500;
            var damageBound1 = 3000.0*countMultiplier/500;

            var cur = _nuclearFindMove(Environment, damageBound1, false);
            if (cur == null)
            {
                _prevNuclearTotalDamage = 0;
                return null;
            }

            if (cur.Item1 >= damageBound2)
            {
                _prevNuclearTotalDamage = 0;
                return cur.Item2;
            }

            // нужно проверить, что в следующий тик не будет лучше

            // предыдущее предсказание не оправдалось:
            if (cur.Item1 < _prevNuclearTotalDamage)
            {
                _prevNuclearTotalDamage = 0;
                // возвращает то что есть
                return cur.Item2;
            }

            var env = Environment.Clone();
            env.DoTick(fight: false);

            var next = _nuclearFindMove(env, cur.Item1, true);
            if (next == null)
            {
                _prevNuclearTotalDamage = 0;
                return cur.Item2;
            }

            // должно буть лучше
            _prevNuclearTotalDamage = cur.Item1;
            return null;
        }

        private double _prevNuclearTotalDamage;
    }
}
