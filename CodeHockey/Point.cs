using System;
using System.Reflection;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public class Point : IComparable<Point>
    {
        private static double eps = 1e-9;
        public double X, Y;

        public static double GetAngleBetween(Point a, Point b, Point c)
        {
            var ac = a.GetDistanceTo(c);
            var ab = a.GetDistanceTo(b);
            var bc = b.GetDistanceTo(c);
            var arg = -(ac*ac - ab*ab - bc*bc)/2/ab/bc;
            if (arg < -1)
                arg = -1;
            if (arg > 1)
                arg = 1;
            return Math.Acos(arg);
        }

        public double Length
        {
            get { return Math.Sqrt(X*X + Y*Y); }
        }

        public Point Normalized()
        {
            return new Point(X / Length, Y / Length);
        }

        public static Point operator *(Point a, double b)
        {
            return new Point(a.X * b, a.Y * b);
        }

        public static Point operator +(Point a, Point b)
        {
            return new Point(a.X + b.X, a.Y + b.Y);
        }

        public static Point operator -(Point a, Point b)
        {
            return new Point(a.X - b.X, a.Y - b.Y);
        }

        public Point(double x, double y)
        {
            X = x;
            Y = y;
        }

        public Point(Unit unit)
        {
            X = unit.X;
            Y = unit.Y;
        }

        public Point(Point point)
        {
            X = point.X;
            Y = point.Y;
        }

        public Point(double angle)
        {
            X = Math.Cos(angle);
            Y = Math.Sin(angle);
        }

        public double Angle
        {
            get { return Math.Atan2(Y, X); }
        }

        public double GetDistanceTo(double x, double y)
        {
            return Math.Sqrt((X - x) * (X - x) + (Y - y) * (Y - y));
        }

        public double GetDistanceTo(Unit unit)
        {
            return GetDistanceTo(unit.X, unit.Y);
        }

        public double GetDistanceTo(Point point)
        {
            return GetDistanceTo(point.X, point.Y);
        }

        public bool Same(double otherX, double otherY)
        {
            return Math.Abs(X - otherX) < eps && Math.Abs(Y - otherY) < eps;
        }

        public bool Same(Point other)
        {
            return Same(other.X, other.Y);
        }

        public bool Same(Unit other)
        {
            return Same(other.X, other.Y);
        }

        public override string ToString()
        {
            return "(" + X + "; " + Y + ")";
        }

        public int CompareTo(Point other)
        {
            if (Math.Abs(X - other.X) < eps)
                return Y.CompareTo(other.Y);
            return X.CompareTo(other.X);
        }

        public void Set(int x, int y)
        {
            X = x;
            Y = y;
        }

        public static Point Zero
        {
            get
            {
                return new Point(0, 0);
            }
        }

    }
}
