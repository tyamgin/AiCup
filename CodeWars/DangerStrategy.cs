using System;
using System.Linq;
using System.Windows.Forms.VisualStyles;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public static double GetDanger(Sandbox env, AMove move, int ticksCount)
        {
            var myDurabilityBefore = env.MyVehicles.Sum(x => x.FullDurability);
            var oppDurabilityBefore = env.OppVehicles.Sum(x => x.FullDurability);

            Logger.CumulativeOperationStart("Simulate n ticks");
            env.ApplyMove(move);
            for (var i = 0; i < ticksCount; i++)
            {
                env.DoTick();
            }
            Logger.CumulativeOperationEnd("Simulate n ticks");

            Logger.CumulativeOperationStart("Danger1");

            var myDurabilityAfter = env.MyVehicles.Sum(x => x.FullDurability);
            var oppDurabilityAfter = env.OppVehicles.Sum(x => x.FullDurability);

            var res = (myDurabilityBefore - myDurabilityAfter) - (oppDurabilityBefore - oppDurabilityAfter);
            res += env.OppVehicles.Count(x => !x.IsAlive) * 30;
            res -= env.MyVehicles.Count(x => !x.IsAlive) * 30;

            res += MyGroups.Sum(type =>
            {
                var vehs = env.GetVehicles(true, type);
                if (vehs.Count == 0)
                    return 0;
                var rect = GetUnitsBoundingRect(vehs);
                return Math.Sqrt(rect.Area)*0.002;
            });

            var additionalDanger = env.OppVehicles.Sum(opp =>
            {
                var additionalRadius = opp.ActualSpeed;
                return env.GetOpponentFightNeigbours(opp, G.MaxAttackRange + additionalRadius).DefaultIfEmpty(null)
                    .Max(m => m == null ? 0 :  opp.GetAttackDamage(m, additionalRadius));
            });
            res += additionalDanger/3.0;

            var rectF = GetUnitsBoundingRect(env.GetVehicles(true, VehicleType.Fighter));
            var rectH = GetUnitsBoundingRect(env.GetVehicles(true, VehicleType.Helicopter));
            var rectT = GetUnitsBoundingRect(env.GetVehicles(true, new MyGroup(FirstFroup)));
            var rectI = GetUnitsBoundingRect(env.GetVehicles(true, new MyGroup(SecondGroup)));

            foreach (var rectPair in new[] {new Tuple<Rect, Rect>(rectF, rectH), new Tuple<Rect, Rect>(rectT, rectI)})
            {
                var r1 = rectPair.Item1;
                var r2 = rectPair.Item2;
                if (r1.IsFinite && r2.IsFinite)
                {
                    r1.ExtendedRadius(G.VehicleRadius*1.5);
                    r2.ExtendedRadius(G.VehicleRadius*1.5);

                    if (r1.IntersectsWith(r2))
                        res += 2000;
                    else
                    {
                        r1.ExtendedRadius(G.VehicleRadius*1.5);
                        r2.ExtendedRadius(G.VehicleRadius*1.5);
                        if (r1.IntersectsWith(r2))
                            res += 500;
                    }
                }
            }

            Logger.CumulativeOperationEnd("Danger1");

            Logger.CumulativeOperationStart("Danger2");

            var s = 0.0;
            var c = 0;
            foreach (var gr in MyGroups)
            {
                VehicleType type;
                if (gr.Group == FirstFroup)
                    type = VehicleType.Tank;
                else if (gr.Group == SecondGroup)
                    type = VehicleType.Ifv;
                else
                    type = (VehicleType) gr.Type;

                if (type == VehicleType.Arrv)
                    continue;

                var myGroup = env.GetVehicles(true, type);
                if (myGroup.Count == 0)
                    continue;

                var cen = GetAvg(myGroup);
                foreach (var opp in env.OppVehicles)
                {
                    var myAttack = G.AttackDamage[(int)type, (int)opp.Type];
                    var oppAttack = G.AttackDamage[(int)opp.Type, (int)type];
                    var ret = opp.GetDistanceTo2(cen);
                    s += ret * (myAttack - oppAttack / 2) * myGroup.Count;
                    c += myGroup.Count;
                }
            }
            res += s / c / 200000 / 10;

            Logger.CumulativeOperationEnd("Danger2");

            return res;
        }
    }
}
