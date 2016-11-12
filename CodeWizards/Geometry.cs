using System;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class Point : IComparable<Point>
    {
        public double X, Y;

        public double Length
        {
            get { return Math.Sqrt(X * X + Y * Y); }
        }


        /// <summary>
        /// Вектор длины 1 того-же направления
        /// </summary>
        /// <returns></returns>
        public Point Normalized()
        {
            return new Point(X / Length, Y / Length);
        }

        /// <summary>
        /// Скалярное произведение a и b
        /// </summary>
        /// <param name="a">a</param>
        /// <param name="b">b</param>
        /// <returns></returns>
        public static double operator *(Point a, Point b)
        {
            return a.X * b.X + a.Y * b.Y;
        }

        /// <summary>
        /// Повернутый на 90* вектор
        /// </summary>
        /// <param name="a">a</param>
        /// <returns></returns>
        public static Point operator ~(Point a)
        {
            return new Point(a.Y, -a.X);
        }

        public static Point operator *(Point a, double b)
        {
            return new Point(a.X * b, a.Y * b);
        }

        public static Point operator /(Point a, double b)
        {
            return new Point(a.X / b, a.Y / b);
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

        /// <summary>
        /// Расстояние то точки в квадтаре
        /// </summary>
        /// <param name="x">x</param>
        /// <param name="y">y</param>
        /// <returns></returns>
        public double GetDistanceTo2(double x, double y)
        {
            return (X - x) * (X - x) + (Y - y) * (Y - y);
        }

        /// <summary>
        /// Расстояние до юнита 
        /// </summary>
        /// <param name="point"></param>
        /// <returns></returns>
        public double GetDistanceTo(Unit unit)
        {
            return GetDistanceTo(unit.X, unit.Y);
        }

        /// <summary>
        /// Расстояние до точки 
        /// </summary>
        /// <param name="point"></param>
        /// <returns></returns>
        public double GetDistanceTo(Point point)
        {
            return GetDistanceTo(point.X, point.Y);
        }

        /// <summary>
        /// Расстояние до точки в квадрате
        /// </summary>
        /// <param name="point"></param>
        /// <returns></returns>
        public double GetDistanceTo2(Point point)
        {
            return GetDistanceTo2(point.X, point.Y);
        }

        public bool Equals(double otherX, double otherY)
        {
            return Math.Abs(X - otherX) < Const.Eps && Math.Abs(Y - otherY) < Const.Eps;
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
            return "(" + X.ToString().Replace(',', '.') + ", " + Y.ToString().Replace(',', '.') + ")";
        }

        public int CompareTo(Point other)
        {
            if (Math.Abs(X - other.X) < Const.Eps)
                return Y.CompareTo(other.Y);
            return X.CompareTo(other.X);
        }

        public static Point Zero => new Point(0, 0);

        public static Point One => new Point(1, 1).Normalized();

        public Point Clone()
        {
            return new Point(this);
        }

        public static Point ByAngle(double angle)
        {
            return new Point(Math.Cos(angle), Math.Sin(angle));
        }

        public Point RotateClockwise(double angle)
        {
            var cos = Math.Cos(angle);
            var sin = Math.Sin(angle);
            return new Point(cos * X + sin * Y, -sin * X + cos * Y);
        }

        public double GetDistanceToCircle(ACircularUnit circle)
        {
            var distToCenter = GetDistanceTo(circle);
            if (distToCenter <= circle.Radius)
                return 0;
            return distToCenter - circle.Radius;
        }
    }

    public class Points : List<Point>
    {
        public void Pop()
        {
            RemoveAt(Count - 1);
        }

        public Point Last()
        {
            return this[Count - 1];
        }
    }

    public class Cell : IComparable<Cell>
    {
        public int I, J;

        public Cell(int i, int j)
        {
            I = i;
            J = j;
        }

        public Cell Clone()
        {
            return new Cell(I, J);
        }

        public bool Equals(int otherI, int otherJ)
        {
            return I == otherI && J == otherJ;
        }

        public bool Equals(Cell other)
        {
            return Equals(other.I, other.J);
        }

        public int CompareTo(Cell other)
        {
            if (I != other.I)
                return I < other.I ? -1 : 1;
            if (J != other.J)
                return J < other.J ? -1 : 1;
            return 0;
        }

        public override string ToString()
        {
            return "(" + I + ", " + J + ")";
        }
    }

    public class Geom
    {
        /// <summary>
        /// Знаковая площадь abc
        /// </summary>
        /// <param name="a"></param>
        /// <param name="b"></param>
        /// <param name="c"></param>
        /// <returns></returns>
        public static double VectorProduct(Point a, Point b, Point c)
        {
            return (c.X - b.X) * (b.Y - a.Y) - (c.Y - b.Y) * (b.X - a.X);
        }

        public static int Sign(double x)
        {
            if (Math.Abs(x) < Const.Eps)
                return 0;
            return x < 0 ? -1 : 1;
        }

        private static void _swap<T>(ref T lhs, ref T rhs)
        {
            var temp = lhs;
            lhs = rhs;
            rhs = temp;
        }

        private static bool _intersect_1D(double a, double b, double c, double d)
        {
            if (a > b)
                _swap(ref a, ref b);
            if (c > d)
                _swap(ref c, ref d);
            return Math.Max(a, c) <= Math.Min(b, d);
        }

        /// <summary>
        /// Проверка пересечения отрезков
        /// </summary>
        /// <param name="start1">Начало первого отрезка</param>
        /// <param name="end1">Конец первого отрезка</param>
        /// <param name="start2">Начало второго отрезка</param>
        /// <param name="end2">Конец второго отрезка</param>
        /// <returns></returns>
        public static bool SegmentsIntersect(Point start1, Point end1, Point start2, Point end2)
        {
            return _intersect_1D(start1.X, end1.X, start2.X, end2.X)
                   && _intersect_1D(start1.Y, end1.Y, start2.Y, end2.Y)
                   && Sign(VectorProduct(start1, end1, start2)) * Sign(VectorProduct(start1, end1, end2)) <= 0
                   && Sign(VectorProduct(start2, end2, start1)) * Sign(VectorProduct(start2, end2, end1)) <= 0;
        }

        /// <summary>
        /// Проверка пренадлежности точки выпуклому полигону
        /// </summary>
        /// <param name="polygon">Выпуклый полигон</param>
        /// <param name="point">Точка</param>
        /// <returns></returns>
        public static bool ContainPoint(Point[] polygon, Point point)
        {
            bool allLess = true, allGreater = true;
            for (var i = 0; i < polygon.Length; i++)
            {
                var vec = Sign(VectorProduct(polygon[i], polygon[(i + 1) % polygon.Length], point));
                allLess &= vec <= 0;
                allGreater &= vec >= 0;
            }
            return allLess || allGreater;
        }

        /// <summary>
        /// Проверка двух выпуклых полигона на пересечение
        /// </summary>
        /// <param name="a">Полигон 1</param>
        /// <param name="b">Полигон 2</param>
        /// <returns></returns>
        public static bool PolygonsIntersect(Point[] a, Point[] b)
        {
            for (var i = 0; i < a.Length; i++)
            {
                // ReSharper disable once LoopCanBeConvertedToQuery
                for (var j = 0; j < b.Length; j++)
                {
                    if (SegmentsIntersect(a[i], a[(i + 1) % a.Length], b[j], b[(j + 1) % b.Length]))
                        return true;
                }
            }
            return false;
        }

        public static double Sqr(double x)
        {
            return x * x;
        }

        public static double Hypot(double x, double y)
        {
            return Math.Sqrt(x*x + y*y);
        }

        public static bool Between(double left, double right, double middle)
        {
            if (left > right)
                _swap(ref left, ref right);
            return left - Const.Eps <= middle && middle <= right + Const.Eps;
        }

        /// <summary>
        /// Точки пересечения отрезка ab с окружностью с центром circleCenter и радиусом radius 
        /// NOTE: если отрезок полностью находится в окружности, то считается пересечением
        /// </summary>
        /// <param name="a"></param>
        /// <param name="b"></param>
        /// <param name="circleCenter"></param>
        /// <param name="radius"></param>
        /// <returns></returns>
        public static Point[] SegmentCircleIntersect(Point a, Point b, Point circleCenter, double radius)
        {
            var result = LineCircleIntersect(a, b, circleCenter, radius)
                .Where(pt => Between(a.X, b.X, pt.X) && Between(a.Y, b.Y, pt.Y))
                .ToList();

            if (circleCenter.GetDistanceTo2(a) <= radius*radius)
                result.Add(a);
            if (circleCenter.GetDistanceTo2(b) <= radius * radius)
                result.Add(b);
            return result.ToArray();
            // maybe optimize
        }

        /// <summary>
        /// Точки пересечения прямой ab с окружностью с центром circleCenter и радиусом radius 
        /// </summary>
        /// <param name="a"></param>
        /// <param name="b"></param>
        /// <param name="circleCenter"></param>
        /// <param name="radius"></param>
        /// <returns></returns>
        public static Point[] LineCircleIntersect(Point a, Point b, Point circleCenter, double radius)
        {
            a = a - circleCenter;
            b = b - circleCenter;
            double 
                A = a.Y - b.Y,
                B = b.X - a.X,
                C = -a.X * A - a.Y * B;

            var res = LineCircleIntersect(A, B, C, radius);
            foreach (var p in res)
            {
                p.X += circleCenter.X;
                p.Y += circleCenter.Y;
            }
            return res;
        }

        /// <summary>
        /// Точки пересечения прямой a*x + b*y + c с окружностью с центром (0, 0) и радиусом radius 
        /// </summary>
        /// <param name="a"></param>
        /// <param name="b"></param>
        /// <param name="c"></param>
        /// <param name="radius"></param>
        /// <returns></returns>
        public static Point[] LineCircleIntersect(double a, double b, double c, double radius)
        {
            double x0 = -a * c / (a * a + b * b),
                y0 = -b * c / (a * a + b * b);

            if (c * c > radius * radius * (a * a + b * b) + Const.Eps)
                return new Point[] { };

            if (Math.Abs(c * c - radius * radius * (a * a + b * b)) < Const.Eps)
                return new[] { new Point(x0, y0) };

            double d = radius * radius - c * c / (a * a + b * b),
                mult = Math.Sqrt(d / (a * a + b * b)),
                ax = x0 + b * mult,
                bx = x0 - b * mult,
                ay = y0 - a * mult,
                by = y0 + a * mult;

            return new[]
            {
                new Point(ax, ay),
                new Point(bx, by)
            };
        }

        /// <summary>
        /// Угол между векторами
        /// </summary>
        /// <param name="vec1">Первый вектор</param>
        /// <param name="vec2">Второй вектор</param>
        /// <returns></returns>
        public static double GetAngleBetween(Point vec1, Point vec2)
        {
            return Math.Acos(vec1 * vec2 / vec1.Length / vec2.Length);
        }

        /// <summary>
        /// Приводит угол в промежуток [-Pi, Pi]
        /// </summary>
        /// <param name="angle"></param>
        public static void AngleNormalize(ref double angle)
        {
            while (angle > Math.PI)
                angle -= 2.0D * Math.PI;

            while (angle < -Math.PI)
                angle += 2.0D * Math.PI;
        }

        public static double ToDegrees(double angle)
        {
            return angle*180/Math.PI;
        }
    }
}
