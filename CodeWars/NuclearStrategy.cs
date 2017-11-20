using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        AMove NuclearStrategy()
        {
            if (Me.RemainingNuclearStrikeCooldownTicks > 0)
                return null;

            Logger.CumulativeOperationStart("NuclearStrategy");

            var selTotalDamage = 0;
            AMove selMove = null;

            foreach (var veh in Environment.MyVehicles)
            {
                var vr = veh.ActualVisionRange*0.9;
                var targets = Environment.GetAllNeigbours(veh.X, veh.Y, vr + G.TacticalNuclearStrikeRadius);
                var cen = GetAvg(targets);
                cen = veh + (cen - veh).Normalized()*Math.Min(vr, veh.GetDistanceTo(cen));
                var nuclear = new ANuclear(cen.X, cen.Y, true, veh.Id, G.TacticalNuclearStrikeDelay);

                var totalDamage = targets.Sum(x =>
                {
                    var d = x.GetNuclearDamage(nuclear);
                    if (x.IsMy)
                        return -d;
                    return d;
                });
                if (totalDamage > selTotalDamage)
                {
                    selTotalDamage = totalDamage;
                    selMove = new AMove
                    {
                        Action = ActionType.TacticalNuclearStrike,
                        VehicleId = veh.Id,
                        Point = cen,
                    };
                }
            }

            Logger.CumulativeOperationEnd("NuclearStrategy");

            if (selTotalDamage >= 8000.0 * Environment.Vehicles.Length / 1000)
                return selMove;
            return null;
        }
    }
}
