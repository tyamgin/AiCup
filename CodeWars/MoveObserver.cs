using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class MoveObserver
    {
        public static void Update()
        {
            var move = MyStrategy.ResultingMove;
            var sandbox = new Sandbox {Vehicles = VehiclesObserver.Vehicles};
            sandbox.ApplyMove(move);
        }
    }
}
