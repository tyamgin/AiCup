using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Security.Cryptography;
using System.Security.Policy;
using System.Threading;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Point = Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Point;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        private Hashtable iPoint = new Hashtable();
        private Hashtable iSpeed = new Hashtable();

        // Определение трения шайбы
        void Research1()
        {
            if (puck.OwnerPlayerId == -1)
            {
                Point nPoint = new Point(puck);
                var t = 1;
                var prevTick = World.Tick - t;
                if (iPoint.ContainsKey(prevTick))
                {
                    var sp = (Point) iSpeed[prevTick];
                    var m = (sp.Length - GetSpeed(puck).Length)/t;
                    Console.WriteLine(m / sp.Length);
                }
                iPoint[World.Tick] = nPoint;
                iSpeed[World.Tick] = GetSpeed(puck);
            }
        }

        // Расчет отскока
        void Research2()
        {
            if (TK(90))
            {
                __puck = new APuck(Get(puck), GetSpeed(puck), Get(OppGoalie));
                __puck.Move(30);
            }

            if (puck.OwnerPlayerId == -1)
            {
                Point nPoint = Get(puck);
                var t = 1;
                var prevTick = World.Tick - t;
                if (iPoint.ContainsKey(prevTick))
                {
                    var sp = (Point)iSpeed[prevTick];
                    if (sp.Length > 0.0001 && Math.Abs(sp.Length * APuck.FrictionCoeff - GetSpeed(puck).Length) > 0.00001)
                    {
                        var pk = new APuck(iPoint[prevTick] as Point, sp, Get(OppGoalie));
                        pk.Move(1);
                        pk.Move(1);
                        pk.Move(1);
                        if (IsBetween(0, puck.Y, Game.RinkTop + 2*PuckRadius) ||
                            IsBetween(Game.RinkBottom - 2*PuckRadius, puck.Y, Inf))
                        {
                            var ut = (sp * APuck.FrictionCoeff).Y / puck.SpeedY;
                            Console.WriteLine("          " + ut.ToString().Replace(',', '.'));
                        }
                        else
                        {
                            var ut = (sp * APuck.FrictionCoeff).X / puck.SpeedX;
                            Console.WriteLine(ut.ToString().Replace(',', '.'));
                        }
                    }
                }
                iPoint[World.Tick] = nPoint;
                iSpeed[World.Tick] = GetSpeed(puck);
            }
        }

        // Проверка движение хоккеиста
        void Research3(Hockeyist self)
        {
            move.Turn = TurnRange(self.Agility) / 2;
            move.SpeedUp = 0.8;
            var pl = new AHock(self);
            pl.Move(0.8, TurnRange(self.Agility) / 2, 20);
        }

        private APuck __puck;

        // Проверка движение шайбы - OK
        void Research4(Hockeyist self)
        {
            if (TK(190))
            {
                __puck = new APuck(Get(puck), GetSpeed(puck), Get(OppGoalie));
                __puck.Move(10);
            }
            if (TK(190 + 10))
            {     
            }
        }
    }
}