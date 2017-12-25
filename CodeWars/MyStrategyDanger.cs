using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.CompilerServices;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        private const double ExpX1 = 1;
        private const double ExpY1 = 10;
        private const double ExpX2 = 1024;
        private const double ExpY2 = 0.002;
        private static double ExpB, ExpA;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static double DangerExp(double dist)
        {
            return ExpA*Math.Exp(-ExpB*Math.Max(dist, G.MaxAttackRange));
        }

        static double GetSumMaxAlmostAttacks(Sandbox env, IEnumerable<AVehicle> myVehicles)
        {
            Logger.CumulativeOperationStart("Danger0");
            var result = myVehicles.Sum(m =>
            {
                var additionalRadius = m.ActualSpeed;
                const int radiusFactor = 5;
                return env.GetOpponentNeighbours(m.X, m.Y, G.MaxAttackRange + additionalRadius*radiusFactor).DefaultIfEmpty(null)
                    .Max(opp =>
                    {
                        if (opp == null)
                            return 0;
                        var dmg = opp.GetAttackDamage(m, additionalRadius*radiusFactor);
                        if (dmg == 0)
                        {
                            return
                                -(m.GetAttackDamage(opp, additionalRadius) +
                                  m.GetAttackDamage(opp, additionalRadius*radiusFactor)/2.0);
                        }
                        return (opp.GetAttackDamage(m, additionalRadius) +
                                opp.GetAttackDamage(m, additionalRadius*2)/2.0 +
                                opp.GetAttackDamage(m, additionalRadius*3)/4.0 +
                                opp.GetAttackDamage(m, additionalRadius*4)/8.0 +
                                dmg/16.0
                            )*
                               (G.AttackDamage[(int) m.Type, (int) opp.Type] > 0
                                   ? Geom.Sqr(1.0*opp.Durability/G.MaxDurability)
                                   : 1)*
                               (m.Type == opp.Type ? 0.6 : 1)*
                               (G.IsAerialButerDetected && opp.Type == VehicleType.Fighter ? 3 : 1);
                    });
            });
            Logger.CumulativeOperationEnd("Danger0");
            return result;
        }

        public static DangerResult GetDanger(Sandbox startEnv, Sandbox env, List<MyGroup> myGroups, List<VehiclesCluster> myUngroups, double sumMaxAlmostAttacksCache = -1)
        {
            ExpB = Math.Log(ExpY1/ExpY2) / (ExpX2 - ExpX1);
            ExpA = ExpY1/Math.Exp(-ExpB*ExpX1);

            Logger.CumulativeOperationStart("GetDanger");

            var result = new DangerResult();

            if (sumMaxAlmostAttacksCache <= -1)
                result.SumMaxAlmostAttacks = GetSumMaxAlmostAttacks(env, env.MyVehicles);
            else
                result.SumMaxAlmostAttacks = sumMaxAlmostAttacksCache;

            Logger.CumulativeOperationStart("Danger1");

            var myDurabilityBefore = startEnv.MyVehicles.Sum(x => x.FullDurability);
            var oppDurabilityBefore = startEnv.OppVehicles.Sum(x => x.FullDurability);

            var myDurabilityAfter = env.MyVehicles.Sum(x => x.FullDurability);
            var oppDurabilityAfter = env.OppVehicles.Sum(x => x.FullDurability);

            result.MyDurabilityDiff = myDurabilityBefore - myDurabilityAfter;
            result.OppDurabilityDiff = oppDurabilityBefore - oppDurabilityAfter;

            result.MyDeadsCount = env.MyVehicles.Count(x => !x.IsAlive);
            result.OppDeadsCount = env.OppVehicles.Count(x => !x.IsAlive);

            result.FacilitiesPointsDiff = startEnv.Facilities.Sum(x => x.CapturePoints) - env.Facilities.Sum(x => x.CapturePoints);

            // nuclears
            foreach (var nuclear in env.Nuclears)
                foreach (var target in env.GetAllNeighbours(nuclear.X, nuclear.Y, nuclear.Radius))
                    result.NuclearsPotentialDamage += Utility.TrueFactor(target.IsMy)*target.GetNuclearDamage(nuclear);

            // groups intersections
            var groupsIsAerial = myGroups.Select(g => Utility.IsAerial(g.VehicleType))
                .Concat(myUngroups.Select(cl => cl[0].IsAerial))
                .ToArray();
            var groupsRects = myGroups.Select(g => Utility.BoundingRect(env.GetVehicles(true, g)))
                .Concat(myUngroups.Select(cl => cl.BoundingRect))
                .ToArray();

            for (var j = 0; j < groupsRects.Length; j++)
            {
                var rect = groupsRects[j];
                if (rect.X < G.VehicleRadius || rect.Y < G.VehicleRadius || rect.X + G.VehicleRadius > G.MapSize || rect.Y + G.VehicleRadius > G.MapSize)
                {
                    result.RectanglesIntersects1++;
                    continue;
                }

                for (var i = 0; i < j; i++)
                {
                    if (groupsIsAerial[i] != groupsIsAerial[j])
                        continue;
                    var rect1 = groupsRects[i].Clone();
                    var rect2 = groupsRects[j].Clone();

                    rect1.ExtendRadius(G.VehicleRadius * 1.5);
                    rect2.ExtendRadius(G.VehicleRadius * 1.5);

                    if (rect1.IntersectsWith(rect2))
                    {
                        result.RectanglesIntersects1++;
                        continue;
                    }
                    
                    rect1.ExtendRadius(G.VehicleRadius*1.5);
                    rect2.ExtendRadius(G.VehicleRadius*1.5);
                    if (rect1.IntersectsWith(rect2))
                        result.RectanglesIntersects2++;
                    
                }
            }


            Logger.CumulativeOperationEnd("Danger1");

            var clusters = OppClusters; // NOTE: юниты врага считаются неподвижными, поэтому берем инстансы из основного Environment

            Logger.CumulativeOperationStart("Danger2");
            var groundGroupsCenters = new List<Point>();
            var groundGroups = new List<List<AVehicle>>();
            var groundGroupsIdxes = new List<int>();
            var groundGroupsId = new List<MyGroup>();

            for (var s = 0; s < myGroups.Count + myUngroups.Count; s++)
            {
                VehicleType type;
                MyGroup gr = null;
                List<AVehicle> myGroup;
                if (s < myGroups.Count)
                {
                    gr = myGroups[s];
                    type = gr.VehicleType;
                    myGroup = env.GetVehicles(true, gr);
                }
                else
                {
                    myGroup = myUngroups[s - myGroups.Count];
                    type = myGroup[0].Type;
                }
                var myGroupCenter = Utility.Average(myGroup);

                var list = new List<DangerResult.ScoreDistancePair>();

                var myRatio = 1.0*myGroup.Count/env.MyVehicles.Count;

                foreach (var cl in clusters)
                {
                    for (var oppType = 0; oppType < 5; oppType++)
                    {
                        if (cl.CountByType[oppType] == 0)
                            continue;

                        var myAttack = G.AttackDamage[(int) type, oppType];
                        var oppAttack = G.AttackDamage[oppType, (int)type];

                        var avg = Utility.Average(cl.VehicleType((VehicleType) oppType));
                        var dist = avg.GetDistanceTo(myGroupCenter);

                        var score = myAttack - oppAttack*0.49;
                        if (type == VehicleType.Helicopter && oppType == (int) VehicleType.Fighter)
                            score *= 2.5;
                        else if (type == VehicleType.Helicopter && oppType == (int)VehicleType.Arrv)
                            score *= 0.8;

                        var maxDurability = cl.CountByType[oppType]*G.MaxDurability;
                        var durabilityCoeff = (maxDurability - cl.DurabilitySumByType[oppType])/G.MaxDurability;
                        if (score < 0)
                            durabilityCoeff *= -1;
                        score = score*(cl.CountByType[oppType]+durabilityCoeff)*myRatio;

                        var e = -(myAttack == 0 && dist > 100 || myAttack < oppAttack && dist > (type == VehicleType.Helicopter ? 350 : 140)
                            ? 0 
                            : DangerExp(dist));

                        list.Add(new DangerResult.ScoreDistancePair(score, e));
                    }
                }

                foreach (var helpGroup in myGroups)
                {
                    if (type == VehicleType.Helicopter && helpGroup.VehicleType == VehicleType.Ifv)
                    {
                        var fightersCount = env.GetVehicles(false, VehicleType.Fighter).Count;
                        if (fightersCount == 0)
                            continue;

                        var helpGroupVehicles = env.GetVehicles(true, helpGroup);
                        var helpGroupVehiclesCenter = Utility.BoundingRect(helpGroupVehicles).Center;

                        var myAttack = G.AttackDamage[(int)type, (int)VehicleType.Fighter];
                        var oppAttack = G.AttackDamage[(int)VehicleType.Fighter, (int)type];

                        var score = (myAttack - oppAttack * 0.49)*2.5;
                        score = score * fightersCount * myRatio;
                        var dist = myGroupCenter.GetDistanceTo(helpGroupVehiclesCenter);
                        var distToFighter = Math.Sqrt(env.GetVehicles(false, VehicleType.Fighter).Min(x => x.GetDistanceTo2(myGroupCenter)));
                        const double n = 400;
                        var coef = Math.Max(0, (n - distToFighter)/n);

                        list.Add(new DangerResult.ScoreDistancePair(coef * score, DangerExp(dist)));
                    }

                    if ((type == VehicleType.Helicopter || type == VehicleType.Fighter) &&
                        helpGroup.VehicleType == VehicleType.Arrv)
                    {
                        var helpGroupVehicles = env.GetVehicles(true, helpGroup);
                        var helpGroupVehiclesCenter = Utility.BoundingRect(helpGroupVehicles).Center;

                        var score = -(myGroup.Count * G.MaxDurability - myGroup.Sum(x => x.Durability)) * myRatio;
                        var dist = myGroupCenter.GetDistanceTo(helpGroupVehiclesCenter);
                        
                        list.Add(new DangerResult.ScoreDistancePair(score, DangerExp(dist)));
                    }
                }

                result.MoveToInfo.Add(new Tuple<MyGroup, int, List<DangerResult.ScoreDistancePair>>(gr, s, list));

                // scale groups
                var boundingRect = Utility.BoundingRect(myGroup);
                boundingRect.ExtendRadius(G.VehicleRadius);
                result.RectanglesAreas.Add(new Tuple<double, int>(boundingRect.Area, myGroup.Count));

                if (!Utility.IsAerial(type) && s < myGroups.Count)
                {
                    groundGroups.Add(myGroup);
                    groundGroupsCenters.Add(myGroupCenter);
                    groundGroupsId.Add(gr);
                    groundGroupsIdxes.Add(s);
                }
            }

            var targetFacilities = env.Facilities.Where(x => !x.IsMy).ToArray();
            if (groundGroups.Count > 0 && targetFacilities.Length > 0)
            {
                var mat = new double[groundGroups.Count][];
                for (var i = 0; i < groundGroups.Count; i++)
                {
                    mat[i] = new double[targetFacilities.Length];
                    for (var j = 0; j < targetFacilities.Length; j++)
                        mat[i][j] = groundGroupsCenters[i].GetDistanceTo(targetFacilities[j].Center);
                }
                var asg = HungarianAssignment.Minimize(mat, 2*G.MapSize);
                for (var i = 0; i < groundGroups.Count; i++)
                {
                    var myGroup = groundGroups[i];
                    var cen = groundGroupsCenters[i];
                    var myRatio = 1.0*myGroup.Count/env.MyVehicles.Count;

                    var flist = new List<DangerResult.ScoreDistancePair>();

                    if (asg[i] == -1)
                        continue;
                    var facility = targetFacilities[asg[i]];
                    
                    result.TargetFacility[groundGroupsId[i].Group] = facility.Center;
                    
                    var dist = cen.GetDistanceTo(facility.Center);
                    var score = -myRatio * (groundGroupsId[i].VehicleType == VehicleType.Arrv ? 4 : 1);
                    flist.Add(new DangerResult.ScoreDistancePair(score, DangerExp(dist)));    
                    
                    result.MoveToFacilitiesInfo.Add(new Tuple<MyGroup, int, List<DangerResult.ScoreDistancePair>>(groundGroupsId[i], groundGroupsIdxes[i], flist));
                }
            }

            Logger.CumulativeOperationEnd("Danger2");

            Logger.CumulativeOperationEnd("GetDanger");

            return result;
        }
    }
}
