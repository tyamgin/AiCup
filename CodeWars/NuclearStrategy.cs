﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        double asdf(AVehicle veh, Sandbox env, double lowerBound, out ANuclear nuclearResult)
        {
            var vr = veh.ActualVisionRange * 0.9;
            var targets = env.GetOpponentNeighbours(veh.X, veh.Y, vr + G.TacticalNuclearStrikeRadius);
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

        AMove NuclearStrategy()
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

        AMove _nuclearStrategy()
        {
            var damageBound2 = 8000.0*Environment.Vehicles.Length/1000;
            var damageBound1 = 3000.0*Environment.Vehicles.Length/1000;

            var selTotalDamage = damageBound1;
            AMove selMove = null;

            foreach (var veh in Environment.MyVehicles)
            {
                ANuclear nuclear;
                var totalDamage = asdf(veh, Environment, selTotalDamage, out nuclear);

                if (totalDamage <= selTotalDamage)
                    continue;

                selTotalDamage = totalDamage;
                selMove = new AMove
                {
                    Action = ActionType.TacticalNuclearStrike,
                    VehicleId = veh.Id,
                    Point = nuclear,
                };
            }

            if (selMove == null)
            {
                _prevNuclearTotalDamage = 0;
                return null;
            }

            if (selTotalDamage >= damageBound2)
            {
                _prevNuclearTotalDamage = 0;
                return selMove;
            }

            // нужно проверить, что в следующий тик не будет лучше
            
            // предыдущее предсказание не оправдалось:
            if (selTotalDamage < _prevNuclearTotalDamage)
            {
                _prevNuclearTotalDamage = 0;
                return selMove;
            }

            var env = Environment.Clone();
            env.DoTick(fight: false);

            foreach (var veh in env.MyVehicles)
            {
                ANuclear nuclear;
                var totalDamage = asdf(veh, env, selTotalDamage, out nuclear);

                if (totalDamage > selTotalDamage)
                {
                    _prevNuclearTotalDamage = totalDamage;
                    return null; // будет лучше
                }
            }

            _prevNuclearTotalDamage = 0;
            return selMove;
        }

        private double _prevNuclearTotalDamage;
    }
}
