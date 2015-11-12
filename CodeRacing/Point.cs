using System;
using System.Security.Cryptography.X509Certificates;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk 
{
    public class Point : IComparable<Point>
    {
        public static double Eps = 1e-9;
        public double X, Y;

        //public static double GetAngleBetween(Point a, Point b, Point c)
        //{
        //    var ac = a.GetDistanceTo(c);
        //    var ab = a.GetDistanceTo(b);
        //    var bc = b.GetDistanceTo(c);
        //    var arg = -(ac*ac - ab*ab - bc*bc)/2/ab/bc;
        //    if (arg < -1)
        //        arg = -1;
        //    if (arg > 1)
        //        arg = 1;
        //    return MyStrategy.AngleNormalize(Math.Acos(arg));
        //}

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

        public bool Same(double otherX, double otherY)
        {
            return Math.Abs(X - otherX) < Eps && Math.Abs(Y - otherY) < Eps;
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
            if (Math.Abs(X - other.X) < Eps)
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

        public bool InTriangle(Point a, Point b, Point c)
        {
            var sign1 = Sign(VectorProduct(a, b, this));
            var sign2 = Sign(VectorProduct(b, c, this));
            var sign3 = Sign(VectorProduct(c, a, this));
            return sign1 <= 0 && sign2 <= 0 && sign3 <= 0 ||
                   sign1 >= 0 && sign2 >= 0 && sign3 >= 0;
        }

        public static double VectorProduct(Point a, Point b, Point c)
        {
            return (c.X - b.X)*(b.Y - a.Y) - (c.Y - b.Y)*(b.X - a.X);
        }

        public static int Sign(double x)
        {
            if (Math.Abs(x) < Eps)
                return 0;
            return x < 0 ? -1 : 1;
        }

        public static Point ByAngle(double angle)
        {
            return new Point(Math.Cos(angle), Math.Sin(angle));
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

        public bool Same(int otherI, int otherJ)
        {
            return I == otherI && J == otherJ;
        }

        public bool Same(Cell other)
        {
            return Same(other.I, other.J);
        }

        public override string ToString()
        {
            return "(" + I + ", " + J + ")";
        }
    }
}
