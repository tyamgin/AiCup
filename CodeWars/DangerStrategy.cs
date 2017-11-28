using System;
using System.Collections.Generic;
using System.ComponentModel;
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
                    var res = MyDurabilityDiff*1.2 - OppDurabilityDiff;
                    res += MyDeadsCount*100;
                    res -= OppDeadsCount*60;
                    res += SumRectanglesAreas*0.001;
                    res += SumMaxAlmostAttacks/4;
                    res += NuclearsPotentialDamage;
                    res += RectanglesIntersects1*7000;
                    res += RectanglesIntersects2*1000;
                    res += MoveToSum;
                    return res;
                }
            }

            public double GetMoveToSum(List<Tuple<double, double>> arr)
            {
                return arr.Sum(x => x.Item1*x.Item2);
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

        private const double ExpX1 = 1;
        private const double ExpY1 = 10;
        private const double ExpX2 = 1024;
        private const double ExpY2 = 0.002;
        private static double ExpB, ExpA;

        static double GetSumMaxAlmostAttacks(Sandbox env, IEnumerable<AVehicle> myVehicles)
        {
            Logger.CumulativeOperationStart("Danger0");
            var result = myVehicles.Sum(m =>
            {
                var additionalRadius = m.ActualSpeed;
                return env.GetOpponentFightNeighbours(m, G.MaxAttackRange + additionalRadius * 5).DefaultIfEmpty(null)
                    .Max(
                        opp =>
                            opp == null
                                ? 0
                                : opp.GetAttackDamage(m, additionalRadius) +
                                  opp.GetAttackDamage(m, additionalRadius * 2) / 2 +
                                  opp.GetAttackDamage(m, additionalRadius * 3) / 4 +
                                  opp.GetAttackDamage(m, additionalRadius * 4) / 8 +
                                  opp.GetAttackDamage(m, additionalRadius * 5) / 16
                                  );
            });
            Logger.CumulativeOperationEnd("Danger0");
            return result;
        }

        public static DangerResult GetDanger(Sandbox startEnv, Sandbox env, double sumMaxAlmostAttacksCache = -1)
        {
            ExpB = Math.Log(ExpY1/ExpY2) / (ExpX2 - ExpX1);
            ExpA = ExpY1/Math.Exp(-ExpB*ExpX1);

            Logger.CumulativeOperationStart("GetDanger");

            var result = new DangerResult();

            if (sumMaxAlmostAttacksCache <= -1)
            {
                result.SumMaxAlmostAttacks = GetSumMaxAlmostAttacks(env, env.MyVehicles);
            }
            else
            {
                result.SumMaxAlmostAttacks = sumMaxAlmostAttacksCache;
            }

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

            for (var j = 0; j < MyGroups.Length; j++)
            {
                for (var i = 0; i < j; i++)
                {
                    if (Utility.IsAerial(GroupFighter(MyGroups[j])) != Utility.IsAerial(GroupFighter(MyGroups[i])))
                        continue;
                    var r1 = Utility.BoundingRect(env.GetVehicles(true, MyGroups[i]));
                    var r2 = Utility.BoundingRect(env.GetVehicles(true, MyGroups[j]));

                    if (r1.IsFinite && r2.IsFinite)
                    {
                        r1.ExtendedRadius(G.VehicleRadius * 1.5);
                        r2.ExtendedRadius(G.VehicleRadius * 1.5);

                        if (r1.IntersectsWith(r2))
                            result.RectanglesIntersects1++;
                        else
                        {
                            r1.ExtendedRadius(G.VehicleRadius * 1.5);
                            r2.ExtendedRadius(G.VehicleRadius * 1.5);
                            if (r1.IntersectsWith(r2))
                                result.RectanglesIntersects2++;
                        }
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
                    for (var oppType = 0; oppType < 5; oppType++)
                    {
                        if (cl.CountByType[oppType] == 0)
                            continue;

                        var myAttack = G.AttackDamage[(int) type, oppType];
                        var oppAttack = G.AttackDamage[oppType, (int)type];

                        var avg = Utility.Average(cl.VehicleType((VehicleType) oppType));
                        var dist = avg.GetDistanceTo(cen);

                        var score = (myAttack - oppAttack*0.49);
                        //if (score >= 0)
                        //    score = score*myGroup.Count/cl.CountByType[oppType];
                        //else
                            score = score*cl.CountByType[oppType]/ myGroup.Count;

                        lst.Add(new Tuple<double, double>(score, ExpA - ExpA * Math.Exp(-ExpB * dist)));
                    }
                }
                
                result.MoveToInfo.Add(new Tuple<MyGroup, List<Tuple<double, double>>>(gr, lst));
            }

            Logger.CumulativeOperationEnd("Danger2");

            Logger.CumulativeOperationEnd("GetDanger");

            return result;
        }
    }
}
