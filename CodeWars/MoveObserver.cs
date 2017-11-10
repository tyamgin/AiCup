using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class MoveObserver
    {
        public static Dictionary<long, AVehicle> BeforeMoveUnits = new Dictionary<long, AVehicle>();

        public static void Update()
        {
            var move = MyStrategy.ResultingMove;
            var sandbox = new Sandbox {Vehicles = VehiclesObserver.Vehicles};
            sandbox.ApplyMove(move);
            BeforeMoveUnits.Clear();
            foreach (var veh in sandbox.Vehicles)
                BeforeMoveUnits[veh.Id] = new AVehicle(veh);
            sandbox.DoTick();
        }
    }
}
