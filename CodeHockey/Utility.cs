using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public static double Eps = 1e-9;
        public const int Inf = 0x3f3f3f3f;

        public static double Deg(double deg)
        {
            return Math.PI / 180 * deg;
        }

        public static Point GetSpeed(Unit unit)
        {
            return unit == null ? null : new Point(unit.SpeedX, unit.SpeedY);
        }

        public static Point Get(Unit unit)
        {
            return unit == null ? null : new Point(unit.X, unit.Y);
        }

        public bool MyLeft()
        {
            return !MyRight();
        }

        public bool MyRight()
        {
            return Opp.NetFront < RinkCenter.X;
        }

        public static bool IsBetween(double left, double x, double right)
        {
            return left <= x && x <= right;
        }

        public static bool Eq(double a, double b)
        {
            return Math.Abs(a - b) < Eps;
        }
        public static double AngleNormalize(double angle)
        {
            for (; angle < -Math.PI; angle += Math.PI * 2) ;
            for (; angle > Math.PI; angle -= Math.PI * 2) ;
            return angle;
        }

        public static double Gauss(double x, double mu, double sigma)
        {
            return Math.Exp(-Math.Pow(x - mu, 2)/2/sigma/sigma)/sigma/Math.Sqrt(2*Math.PI);
        }

        public static double GaussIntegral(double a, double b, double deviation)
        {
            const int N = 50; // количество шагов (уже умноженное на 2)
            double s = 0, h = (b - a)/N;
            for (var i = 0; i <= N; i++)
            {
                var x = a + h*i;
                s += Gauss(x, 0, deviation)*((i == 0 || i == N) ? 1 : ((i & 1) == 0) ? 2 : 4);
            }
            return s*h/3;
        }

        public static bool CanStrike(Hockeyist ho, Unit to)
        {
            return Math.Abs(ho.GetAngleTo(to)) <= Game.StickSector/2
                   && ho.GetDistanceTo(to) <= Game.StickLength
                   && ho.RemainingKnockdownTicks == 0;
        }

        public static bool CanStrike(AHock hock, Point to)
        {
            return Math.Abs(hock.GetAngleTo(to)) <= Game.StickSector/2
                   && hock.GetDistanceTo2(to) <= Game.StickLength*Game.StickLength
                   && hock.KnockDown == 0 && hock.CoolDown == 0;
        }

        public static double GetPower(AHock self, int swingTime)
        {
            var res = Math.Min(Game.MaxEffectiveSwingTicks, swingTime) * 0.25 / Game.MaxEffectiveSwingTicks + 0.75;
            res = res*self.AStrength/100;
            return res;
        }

        public static Point GetPuckPos(Point hoPos, double hoAngle)
        {
            return hoPos + new Point(hoAngle)*HoPuckDist;
        }

        public static void Pop(ArrayList a)
        {
            a.RemoveAt(a.Count - 1);
        }

        public static double TurnNorm(double turn, double agility)
        {
            if (turn > TurnRange(agility))
                turn = TurnRange(agility);
            else if (turn < -TurnRange(agility))
                turn = -TurnRange(agility);
            return turn;
        }

        public static double GetSpeedTo(double turn)
        {
            var speedUp = 1.0;
            if (Math.Abs(turn) > Deg(40))
                speedUp = 0.2;
            else if (Math.Abs(turn) > Deg(60))
                speedUp = 0.05;
            return speedUp;
        }

        public double RevAngle(double angle)
        {
            if (Eq(angle, Math.PI))
                return 0.0;
            if (angle > 0)
                return angle - Math.PI;
            return Math.PI + angle;
        }

        public static double TurnRange(double agility)
        {
            return Game.HockeyistTurnAngleFactor*agility/100;
        }

        public static void Swap<T>(ref T lhs, ref T rhs)
        {
            T temp = lhs;
            lhs = rhs;
            rhs = temp;
        }

        public static Hockeyist Get(long id)
        {
            return World.Hockeyists.First(x => x.Id == id);
        }

        public static bool IsInGame(Hockeyist hock)
        {
            return (hock.State == HockeyistState.Active || hock.State == HockeyistState.KnockedDown)
                   && hock.Type != HockeyistType.Goalie;
        }

        void DoMove(Hockeyist self, Point to, int direction)
        {
            if (direction > 0)
            {
                move.Turn = self.GetAngleTo(to.X, to.Y);
                move.SpeedUp = GetSpeedTo(move.Turn);
            }
            else
            {
                move.Turn = RevAngle(self.GetAngleTo(to.X, to.Y));
                move.SpeedUp = -GetSpeedTo(move.Turn);
            }
        }


        public class MoveAction
        {
            public double SpeedUp;
            public double Turn;
            public int Ticks;
        }

        ArrayList timers = new ArrayList();

        void TimerStart()
        {
#if DEBUG
            var timer = new Stopwatch();
            timer.Start();
            timers.Add(timer);
#endif
        }

        long TimerStop()
        {
#if DEBUG
            var res = timers[timers.Count - 1] as Stopwatch;
            res.Stop();
            Pop(timers);
            return res.ElapsedMilliseconds;
#else
            return 0;
#endif
        }

        void Log(object msg)
        {
#if DEBUG
            Console.WriteLine(msg);
#endif
        }
    }
}
