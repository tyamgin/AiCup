using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    class Point
    {
        public int X, Y;
        public double profit;
        
        public Point(int X, int Y, double profit = 0)
        {
            this.X = X;
            this.Y = Y;
            this.profit = profit;
        }

        public Point(Unit unit)
        {
            profit = 0;
            X = unit.X;
            Y = unit.Y;
        }

        public double GetDistanceTo(int x, int y)
        {
            return Math.Sqrt((X - x) * (X - x) + (Y - y) * (Y - y));
        }

        public bool Same(int otherX, int otherY)
        {
            return X == otherX && Y == otherY;
        }

        public bool Same(Point other)
        {
            return Same(other.X, other.Y);
        }

        public bool Same(Unit other)
        {
            return Same(other.X, other.Y);
        }

        public bool Nearest(int otherX, int otherY)
        {
            return Math.Abs(X - otherX) + Math.Abs(Y - otherY) <= 1;
        }

        public bool Nearest(Point other)
        {
            return Nearest(other.X, other.Y);
        }

        public bool Nearest(Unit other)
        {
            return Nearest(other.X, other.Y);
        }

        public static Point Inf
        {
            get
            {
                return new Point(0, 0, -MyStrategy.Inf);
            }
        }

    }
}
