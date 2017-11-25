using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public class DangerResult
        {
            public double MyDurabilityDiff;
            public double OppDurabilityDiff;
            public double MyDeadsCount;
            public double OppDeadsCount;
            public double SumRectanglesAreas;
            public double SumMaxAlmostAttacks;
            public double NuclearsPotentialDamage;
            public double RectanglesIntersects1;
            public double RectanglesIntersects2;
            public List<VehiclesCluster> Clusters;
            public List<Tuple<MyGroup, List<Tuple<double, double>>>> MoveToInfo = new List<Tuple<MyGroup, List<Tuple<double, double>>>>(); 

            public double Score 
            {
                get
                {
                    var res = MyDurabilityDiff - OppDurabilityDiff;
                    res += MyDeadsCount*100;
                    res -= OppDeadsCount*100;
                    res += SumRectanglesAreas*0.2;
                    res += SumMaxAlmostAttacks/6;
                    res += NuclearsPotentialDamage;
                    res += RectanglesIntersects1*7000;
                    res += RectanglesIntersects2*1000;
                    res += MoveToSum*0.0001;
                    return res;
                }
            }

            public double GetMoveToSum(List<Tuple<double, double>> arr)
            {
                var res = double.MinValue;
                foreach (var tpl in arr)
                {
                    res = Math.Max(res, tpl.Item2 * tpl.Item1);
                }
                return res;
            }

            public double[] MoveToSumByGroup
            {
                get { return MoveToInfo.Select(tpl => GetMoveToSum(tpl.Item2)).ToArray(); }
            }

            public double MoveToSum
            {
                get { return MoveToSumByGroup.Sum(); }
            }
        }

        public static DangerResult GetDanger(Sandbox startEnv, Sandbox env)
        {
            Logger.CumulativeOperationStart("GetDanger");

            var result = new DangerResult();

            Logger.CumulativeOperationStart("Danger1");

            var myDurabilityBefore = startEnv.MyVehicles.Sum(x => x.FullDurability);
            var oppDurabilityBefore = startEnv.OppVehicles.Sum(x => x.FullDurability);

            var myDurabilityAfter = env.MyVehicles.Sum(x => x.FullDurability);
            var oppDurabilityAfter = env.OppVehicles.Sum(x => x.FullDurability);

            result.MyDurabilityDiff = myDurabilityBefore - myDurabilityAfter;
            result.OppDurabilityDiff = oppDurabilityBefore - oppDurabilityAfter;

            result.MyDeadsCount = env.MyVehicles.Count(x => !x.IsAlive);
            result.OppDeadsCount = env.OppVehicles.Count(x => !x.IsAlive);

            result.SumRectanglesAreas = MyGroups.Sum(type =>
            {
                var vehs = env.GetVehicles(true, type);
                if (vehs.Count == 0)
                    return 0;
                var rect = Utility.BoundingRect(vehs);
                return Math.Sqrt(rect.Area);
            });

            result.SumMaxAlmostAttacks = env.OppVehicles.Sum(opp =>
            {
                var additionalRadius = opp.ActualSpeed;
                return env.GetOpponentFightNeighbours(opp, G.MaxAttackRange + additionalRadius * 3).DefaultIfEmpty(null)
                    .Max(
                        m =>
                            m == null
                                ? 0
                                : opp.GetAttackDamage(m, additionalRadius) +
                                  opp.GetAttackDamage(m, additionalRadius * 2) / 2 +
                                  opp.GetAttackDamage(m, additionalRadius * 3) / 4);
            });

            foreach (var nuclear in env.Nuclears)
            {
                foreach (var target in env.GetAllNeighbours(nuclear.X, nuclear.Y, nuclear.Radius))
                {
                    var damage = target.GetNuclearDamage(nuclear);
                    if (target.IsMy)
                        result.NuclearsPotentialDamage += damage;
                    else
                        result.NuclearsPotentialDamage -= damage;
                }
            }

            var rectF = Utility.BoundingRect(env.GetVehicles(true, VehicleType.Fighter));
            var rectH = Utility.BoundingRect(env.GetVehicles(true, VehicleType.Helicopter));
            var rectT = Utility.BoundingRect(env.GetVehicles(true, new MyGroup(TanksGroup)));
            var rectI = Utility.BoundingRect(env.GetVehicles(true, new MyGroup(IfvsGroup)));

            foreach (var rectPair in new[] {new Tuple<Rect, Rect>(rectF, rectH), new Tuple<Rect, Rect>(rectT, rectI)})
            {
                var r1 = rectPair.Item1;
                var r2 = rectPair.Item2;
                if (r1.IsFinite && r2.IsFinite)
                {
                    r1.ExtendedRadius(G.VehicleRadius*1.5);
                    r2.ExtendedRadius(G.VehicleRadius*1.5);

                    if (r1.IntersectsWith(r2))
                        result.RectanglesIntersects1++;
                    else
                    {
                        r1.ExtendedRadius(G.VehicleRadius*1.5);
                        r2.ExtendedRadius(G.VehicleRadius*1.5);
                        if (r1.IntersectsWith(r2))
                            result.RectanglesIntersects2++;
                    }
                }
            }

            Logger.CumulativeOperationEnd("Danger1");

            var clusters = env.GetClusters(false, Const.ClusteringMargin);
            result.Clusters = clusters;

            Logger.CumulativeOperationStart("Danger2");

            foreach (var gr in MyGroups)
            {
                VehicleType type = GroupFighter(gr);

                var myGroup = env.GetVehicles(true, type);
                if (myGroup.Count == 0)
                    continue;

                var cen = Utility.Average(myGroup);

                var lst = new List<Tuple<double, double>>();

                foreach (var cl in clusters)
                {
                    var score = 0.0;
                    foreach (var opp in cl)
                    {
                        var myAttack = G.AttackDamage[(int) type, (int) opp.Type];
                        var oppAttack = G.AttackDamage[(int) opp.Type, (int) type];

                        score += myAttack - oppAttack*0.9;
                    }
                    score *= 1.0 * myGroup.Count / cl.Count;
                    var dist = cl.Avg.GetDistanceTo(cen);

                    lst.Add(new Tuple<double, double>(score, dist));
                }
                
                result.MoveToInfo.Add(new Tuple<MyGroup, List<Tuple<double, double>>>(gr, lst));
            }

            Logger.CumulativeOperationEnd("Danger2");

            Logger.CumulativeOperationEnd("GetDanger");

            return result;
        }
    }
}
