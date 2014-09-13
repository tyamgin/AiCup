using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Point = Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Point;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        private Hashtable iPoint = new Hashtable();
        private Hashtable iSpeed = new Hashtable();

        void Research1()
        {
            if (puck.OwnerPlayerId == -1)
            {
                Point nPoint = new Point(puck);
                var t = 10;
                var prevTick = world.Tick - t;
                if (iPoint.ContainsKey(prevTick))
                {
                    var S = nPoint.GetDistanceTo(iPoint[prevTick] as Point);
                    Console.WriteLine(((double)iSpeed[prevTick] - GetSpeed(puck).Length) / t + " " + puck.AngularSpeed);
                }
                iPoint[world.Tick] = nPoint;
                iSpeed[world.Tick] = GetSpeed(puck).Length;
                Thread.Sleep(50);
            }
        }
    }
}