using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        private const double Eps = 1e-9;
        private const int Inf = 0x3f3f3f3f;

        public double Deg(double deg)
        {
            return Math.PI / 180 * deg;
        }

        public static Point GetSpeed(Unit unit)
        {
            return new Point(unit.SpeedX, unit.SpeedY);
        }

        public Point Get(Unit unit)
        {
            return new Point(unit.X, unit.Y);
        }

        public bool MyLeft()
        {
            return !MyRight();
        }

        public bool MyRight()
        {
            return opp.NetFront < RinkCenter.X;
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

        bool CanStrike(Hockeyist ho, Unit to)
        {
            return Math.Abs(ho.GetAngleTo(to)) <= game.StickSector/2
                   && ho.GetDistanceTo(to) <= game.StickLength
                   && ho.RemainingKnockdownTicks == 0;
        }

        double GetPower(int swingTime)
        {
            // TODO: use game.StrikePowerGrowthFactor
            return Math.Min(game.MaxEffectiveSwingTicks, swingTime) * 0.25 / game.MaxEffectiveSwingTicks + 0.75;
        }

        public static Point GetPuckPos(Point hoPos, double hoAngle)
        {
            return hoPos + new Point(hoAngle)*HoPuckDist;
        }
    }
}
