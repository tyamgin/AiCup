using System;
using System.Collections.Generic;
using System.Diagnostics.Eventing.Reader;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public static double GetDanger(Sandbox env, AMove move, int ticksCount)
        {
            var myDurabilityBefore = env.MyDurability;
            var oppDurabilityBefore = env.OppDurability;
            var myCenterBefore = GetAvg(env.MyVehicles);

            env.ApplyMove(move);
            for (var i = 0; i < ticksCount; i++)
            {
                env.DoTick();
            }

            var myDurabilityAfter = env.MyDurability;
            var oppDurabilityAfter = env.OppDurability;

            var res = (myDurabilityBefore - myDurabilityAfter) - (oppDurabilityBefore - oppDurabilityAfter);
            res += env.OppVehicles.Count(x => !x.IsAlive) * 30;
            res -= env.MyVehicles.Count(x => !x.IsAlive) * 30;

            var myCenter = GetAvg(env.MyVehicles);

            res += env.MyVehicles.Where(m => m.IsSelected).Average(m => 
                env.OppVehicles
                    .Select(x =>
                    {
                        var myAttack = G.AttackDamage[(int) m.Type, (int) x.Type];
                        var ret = x.GetDistanceTo2(m)/200000;
                        if (myAttack < Const.Eps)
                            return 0;
                        return ret/myAttack;
                    })
                    .Where(x => x > 0)
                    .Concat(new []{0.0})
                    .Average()
            );

            var rectF = GetUnitsBoundingRect(env.MyVehicles.Where(x => x.Type == VehicleType.Fighter));
            var rectH = GetUnitsBoundingRect(env.MyVehicles.Where(x => x.Type == VehicleType.Helicopter));

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
            
            foreach (var veh in MyVehicles)
                if (env.FirslCollider.ContainsKey(veh.Id) && env.FirslCollider[veh.Id].Type != veh.Type)
                    intersects = true;
            
            if (intersects)
                res += 100;

            return res;
        }
    }
}
