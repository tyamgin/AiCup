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

        public Point GetSpeed(Unit unit)
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

        bool Eq(double a, double b)
        {
            return Math.Abs(a - b) < Eps;
        }
    }
}
