using System;
using System.Collections.Generic;
using System.Security.Cryptography.X509Certificates;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk 
{
    public class Point : IComparable<Point>
    {
        //public static double Eps = 1e-9;
        public double X, Y;

        public double Length
        {
            get { return Math.Sqrt(X*X + Y*Y); }
        }

        public Point Normalized()
        {
            return new Point(X / Length, Y / Length);
        }

        /*
         * Скалярное произведение a и b
         */
        public static double operator *(Point a, Point b)
        {
            return a.X * b.X + a.Y * b.Y;
        }

        /*
         * Повернутый на 90* вектор
         */
        public static Point operator ~(Point a)
        {
            return new Point(a.Y, -a.X);
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

        public Point()
        {
            X = 0;
            Y = 0;
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

        public double GetAngle()
        {
            return Math.Atan2(Y, X);
        }

        public double GetDistanceTo(double x, double y)
        {
            return Math.Sqrt((X - x) * (X - x) + (Y - y) * (Y - y));
        }

        public double GetDistanceTo2(double x, double y)
        {
            return (X - x) * (X - x) + (Y - y) * (Y - y);
        }

        public double GetDistanceTo(Unit unit)
        {
            return GetDistanceTo(unit.X, unit.Y);
        }

        public double GetDistanceTo(Point point)
        {
            return GetDistanceTo(point.X, point.Y);
        }

        public double GetDistanceTo2(Point point)
        {
            return GetDistanceTo2(point.X, point.Y);
        }

        public bool Equals(double otherX, double otherY)
        {
            return Math.Abs(X - otherX) < MyStrategy.Eps && Math.Abs(Y - otherY) < MyStrategy.Eps;
        }

        public bool Equals(Point other)
        {
            return Equals(other.X, other.Y);
        }

        public bool Equals(Unit other)
        {
            return Equals(other.X, other.Y);
        }

        public override string ToString()
        {
            return "(" + X + "; " + Y + ")";
        }

        public int CompareTo(Point other)
        {
            if (Math.Abs(X - other.X) < MyStrategy.Eps)
                return Y.CompareTo(other.Y);
            return X.CompareTo(other.X);
        }

        public void Set(double x, double y)
        {
            X = x;
            Y = y;
        }

        public static Point Zero
        {
            get { return new Point(0, 0); }
        }

        public static Point One
        {
            get { return new Point(1, 1).Normalized(); }
        }

        public Point Clone()
        {
            return new Point(this);
        }

        public static double VectorProduct(Point a, Point b, Point c)
        {
            return (c.X - b.X)*(b.Y - a.Y) - (c.Y - b.Y)*(b.X - a.X);
        }

        public static int Sign(double x)
        {
            if (Math.Abs(x) < MyStrategy.Eps)
                return 0;
            return x < 0 ? -1 : 1;
        }

        public static Point ByAngle(double angle)
        {
            return new Point(Math.Cos(angle), Math.Sin(angle));
        }
    }

    public class Points : List<Point>
    {
        public void Pop()
        {
            RemoveAt(Count - 1);
        }

        public bool ContainPoint(Point p)
        {
            bool allLess = true, allGreater = true;
            for (var i = 0; i < Count; i++)
            {
                var vec = Point.Sign(Point.VectorProduct(this[i], this[(i + 1) % Count], p));
                allLess &= vec <= 0;
                allGreater &= vec >= 0;
            }
            return allLess || allGreater;
        }
    }

    public class Cell
    {
        public int I, J;

        public Cell(int i, int j)
        {
            I = i;
            J = j;
        }

        public bool Equals(int otherI, int otherJ)
        {
            return I == otherI && J == otherJ;
        }

        public bool Equals(Cell other)
        {
            return Equals(other.I, other.J);
        }

        public override string ToString()
        {
            return "(" + I + ", " + J + ")";
        }
    }
}
