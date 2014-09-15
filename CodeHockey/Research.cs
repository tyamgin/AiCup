using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
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
                var prevTick = world.Tick - t;
                if (iPoint.ContainsKey(prevTick))
                {
                    var sp = (Point) iSpeed[prevTick];
                    var m = (sp.Length - GetSpeed(puck).Length)/t;
                    Console.WriteLine(m / sp.Length);
                }
                iPoint[world.Tick] = nPoint;
                iSpeed[world.Tick] = GetSpeed(puck);
            }
        }

        // Расчет отскока (пока нет результатов)
        void Research2()
        {
            if (puck.OwnerPlayerId == -1)
            {
                Point nPoint = new Point(puck);
                var t = 1;
                var prevTick = world.Tick - t;
                if (iPoint.ContainsKey(prevTick))
                {
                    var sp = (Point)iSpeed[prevTick];
                    var m = (sp.Length - GetSpeed(puck).Length) / t;
                    if (!Double.IsNaN(m) && Math.Abs(m / sp.Length - 0.001) > 1e-5)
                    {
                        var str = (sp.Length / GetSpeed(puck).Length / sp.X).ToString().Replace(',', '.');
                        //System.IO.File.AppendAllText("a.txt", str);
                        Console.WriteLine(str);
                    }
                }
                iPoint[world.Tick] = nPoint;
                iSpeed[world.Tick] = GetSpeed(puck);
            }
        }

        // Проверка движение хоккеиста
        void Research3(Hockeyist self, Move move)
        {
            move.Turn = Global.game.HockeyistTurnAngleFactor / 2;
            move.SpeedUp = 0.8;
            var pl = new AHo(new Point(self), GetSpeed(self), self.Angle, self.AngularSpeed, self);
            pl.Move(0.8, Global.game.HockeyistTurnAngleFactor / 2, 20);
        }

        // Проверка движение шайбы
        void Research4(Hockeyist self, Move move)
        {
            var pk = new APuck(Get(puck), GetSpeed(puck), Get(oppGoalie));
            pk.Move(30);
        }
    }
}