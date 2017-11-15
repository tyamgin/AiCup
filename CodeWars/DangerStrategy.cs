using System;
using System.Linq;
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

            var myTypes = env.MyVehicles.Select(x => x.Type).Distinct();

            res += myTypes.Average(type =>
            {
                var rect = GetUnitsBoundingRect(env.GetVehicles(true, type));
                return Math.Sqrt(rect.Area)*0.00005;
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

            var intersects = false;
            if (rectF.X <= rectF.X2 && rectH.X <= rectH.X2)
            {
                rectF.X -= G.VehicleRadius;
                rectF.X2 += G.VehicleRadius;
                rectF.Y -= G.VehicleRadius;
                rectF.Y2 += G.VehicleRadius;

                rectH.X -= G.VehicleRadius;
                rectH.X2 += G.VehicleRadius;
                rectH.Y -= G.VehicleRadius;
                rectH.Y2 += G.VehicleRadius;

                if (rectF.IntersectsWith(rectH))
                    intersects = true;
            }
            
            if (intersects)
                res += 100;

            Logger.CumulativeOperationEnd("Danger1");

            Logger.CumulativeOperationStart("Danger2");

            var s = 0.0;
            var c = 0;
            foreach (var type in myTypes)
            {
                if (type == VehicleType.Arrv)
                    continue;

                var myGroup = env.GetVehicles(true, type);
                var cen = GetAvg(myGroup);
                foreach (var opp in env.OppVehicles)
                {
                    var myAttack = G.AttackDamage[(int)type, (int)opp.Type];
                    var ret = opp.GetDistanceTo2(cen);
                    s += ret * myAttack * myGroup.Count;
                    c += myGroup.Count;
                }
            }
            res += s / c / 200000 / 10;

            Logger.CumulativeOperationEnd("Danger2");

            return res;
        }
    }
}
