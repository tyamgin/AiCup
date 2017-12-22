using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;
using System;
using System.Globalization;
using System.Linq;
using System.Runtime.CompilerServices;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class Point : IComparable<Point>
    {
        public double X, Y;

        public double Length => Math.Sqrt(X * X + Y * Y);


        /// <summary>
        /// Вектор длины 1 того-же направления, или (0, 0), если вектор нулевой
        /// </summary>
        /// <returns></returns>
        public Point Normalized()
        {
            var length = Length;
            if (length < Const.Eps)
                length = 1;
            return new Point(X / length, Y / length);
        }

        /// <summary>
        /// Вектор длины newLength того-же направления, или (0, 0), если вектор нулевой
        /// </summary>
        /// <param name="newLength">newLength</param>
        /// <returns></returns>
        public Point Take(double newLength)
        {
            var length = Length;
            if (length < Const.Eps)
                length = 1;
            return new Point(X / length * newLength, Y / length * newLength);
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

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public double GetAngle()
        {
            return Math.Atan2(Y, X);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static double GetDistanceTo2(double x1, double y1, double x2, double y2)
        {
            return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
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
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public double GetDistanceTo2(double x, double y)
        {
            return (X - x) * (X - x) + (Y - y) * (Y - y);
        }

        /// <summary>
        /// Расстояние до юнита 
        /// </summary>
        /// <param name="unit"></param>
        /// <returns></returns>
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public double GetDistanceTo(Unit unit)
        {
            return GetDistanceTo(unit.X, unit.Y);
        }

        /// <summary>
        /// Расстояние до точки 
        /// </summary>
        /// <param name="point"></param>
        /// <returns></returns>
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public double GetDistanceTo(Point point)
        {
            return GetDistanceTo(point.X, point.Y);
        }

        /// <summary>
        /// Расстояние до точки в квадрате
        /// </summary>
        /// <param name="point"></param>
        /// <returns></returns>
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public double GetDistanceTo2(Point point)
        {
            return GetDistanceTo2(point.X, point.Y);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public bool Equals(Unit other)
        {
            return Equals(other.X, other.Y);
        }

        public override string ToString()
        {
            return "(" + X.ToString(CultureInfo.InvariantCulture) + ", " + Y.ToString(CultureInfo.InvariantCulture) + ")";
        }

        public int CompareTo(Point other)
        {
            if (Utility.Equals(X, other.X))
                return Y.CompareTo(other.Y);
            return X.CompareTo(other.X);
        }

        public static Point Zero => new Point(0, 0);

        public static Point One => new Point(1, 1).Normalized();

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public Point Clone()
        {
            return new Point(this);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
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

        public Point RotateClockwise(double angle, Point center)
        {
            var pt = this - center;
            return pt.RotateClockwise(angle) + center;
        }

        public Point RotateCounterClockwise(double angle)
        {
            angle = Math.PI*2 - angle;
            var cos = Math.Cos(angle);
            var sin = Math.Sin(angle);
            return new Point(cos * X + sin * Y, -sin * X + cos * Y);
        }

        public Point RotateCounterClockwise(double angle, Point center)
        {
            var pt = this - center;
            return pt.RotateCounterClockwise(angle) + center;
        }

        public double GetDistanceToCircle(ACircularUnit circle)
        {
            var distToCenter = GetDistanceTo(circle);
            if (distToCenter <= circle.Radius)
                return 0;
            return distToCenter - circle.Radius;
        }
    }

    public class Segment
    {
        public Point A, B;

        public Segment(Point a, Point b)
        {
            A = a;
            B = b;
        }

        public double GetDistanceTo(Point point)
        {
            if (A.Equals(B))
                return A.GetDistanceTo(point);

            var a = A - point;
            var b = B - point;

            double aa, bb, cc;
            Geom.GetABC(a, b, out aa, out bb, out cc);

            if (Geom.Sign((a.Y + bb) * aa - (a.X + aa) * bb) * Geom.Sign((b.Y + bb) * aa - (b.X + aa) * bb) <= 0)
                return Math.Abs(cc / Math.Sqrt(aa * aa + bb * bb));
            return Math.Min(a.Length, b.Length);
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

    public class Rect
    {
        public double X, Y, X2, Y2;

        public double Width => X2 - X;

        public double Height => Y2 - Y;

        public Point Center => new Point((X + X2)/2, (Y + Y2)/2);

        public bool IntersectsWith(Rect rect)
        {
            return Geom.Intersect1D(X, X2, rect.X, rect.X2) && Geom.Intersect1D(Y, Y2, rect.Y, rect.Y2);
        }

        public Rect IntersectionWith(Rect rect)
        {
            if (!IntersectsWith(rect))
                return null;

            return new Rect
            {
                X = Math.Max(X, rect.X),
                Y = Math.Max(Y, rect.Y),
                X2 = Math.Min(X2, rect.X2),
                Y2 = Math.Min(Y2, rect.Y2)
            };
        }

        public double Area => Width*Height;

        public Rect[] SplitVertically()
        {
            return new[]
            {
                new Rect {X = X, Y = Y, X2 = X2, Y2 = (Y + Y2) / 2},
                new Rect {X = X, Y = (Y + Y2) / 2, X2 = X2, Y2 = Y2},
            };
        }

        public Rect[] Split4X()
        {
            var x = (X + X2)/2;
            var y = (Y + Y2)/2;
            return new[]
            {
                new Rect {X = X, Y = Y, X2 = x, Y2 = y},
                new Rect {X = x, Y = Y, X2 = X2, Y2 = y},
                new Rect {X = X, Y = y, X2 = x, Y2 = Y2},
                new Rect {X = x, Y = y, X2 = X2, Y2 = Y2},
            };
        }

        public void ExtendRadius(double additionalRadius)
        {
            X -= additionalRadius;
            Y -= additionalRadius;
            X2 += additionalRadius;
            Y2 += additionalRadius;
        }

        public bool ContainsPoint(Point point)
        {
            return 
                X <= point.X && point.X <= X2 && 
                Y <= point.Y && point.Y <= Y2; // TODO: need eps?
        }

        public Rect Clone()
        {
            return new Rect
            {
                X = X,
                Y = Y,
                X2 = X2,
                Y2 = Y2,
            };
        }

        public override string ToString()
        {
            return "[" + X + ", " + X2 + "][" + Y + ", " + Y2 + "]";
        }

        public bool IsFinite => X <= X2 && Y <= Y2;
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

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static int Sign(double x)
        {
            if (Math.Abs(x) < Const.Eps)
                return 0;
            return x < 0 ? -1 : 1;
        }

        public static bool Intersect1D(double a, double b, double c, double d)
        {
            if (a > b)
                Utility.Swap(ref a, ref b);
            if (c > d)
                Utility.Swap(ref c, ref d);
            return Math.Max(a, c) <= Math.Min(b, d);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static void GetABC(Point a, Point b, out double A, out double B, out double C)
        {
            A = a.Y - b.Y;
            B = b.X - a.X;
            C = -a.X * A - a.Y * B;
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
            return Intersect1D(start1.X, end1.X, start2.X, end2.X)
                   && Intersect1D(start1.Y, end1.Y, start2.Y, end2.Y)
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
        /// Проверка пренадлежности точки треугольнику
        /// </summary>
        /// <param name="a">Вершина треугольника</param>
        /// <param name="b">Вершина треугольника</param>
        /// <param name="c">Вершина треугольника</param>
        /// <param name="point">Точка</param>
        /// <returns></returns>
        public static bool ContainPoint(Point a, Point b, Point c, Point point)
        {
            return ContainPoint(new[] { a, b, c }, point);
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

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static double Sqr(double x)
        {
            return x * x;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static double SumSqr(double numberA, double numberB)
        {
            return numberA * numberA + numberB * numberB;
        }


        public static double Hypot(double x, double y)
        {
            return Math.Sqrt(x * x + y * y);
        }

        public static bool Between(double left, double right, double middle)
        {
            if (left > right)
                Utility.Swap(ref left, ref right);
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
        public static Point[] SegmentCircleIntersection(Point a, Point b, Point circleCenter, double radius)
        {
            var result = LineCircleIntersection(a, b, circleCenter, radius)
                .Where(pt => Between(a.X, b.X, pt.X) && Between(a.Y, b.Y, pt.Y))
                .ToList();

            if (circleCenter.GetDistanceTo2(a) <= radius * radius)
                result.Add(a);
            if (circleCenter.GetDistanceTo2(b) <= radius * radius)
                result.Add(b);
            return result.ToArray();
            // maybe optimize
        }

        public static bool SegmentCircleIntersects(Point a, Point b, Point circleCenter, double radius)
        {
            if (circleCenter.GetDistanceTo2(a) <= radius * radius)
                return true;
            if (circleCenter.GetDistanceTo2(b) <= radius * radius)
                return true;

            return LineCircleIntersection(a, b, circleCenter, radius)
                .Any(pt => Between(a.X, b.X, pt.X) && Between(a.Y, b.Y, pt.Y));
        }

        /// <summary>
        /// Точки пересечения прямой ab с окружностью с центром circleCenter и радиусом radius 
        /// </summary>
        /// <param name="a"></param>
        /// <param name="b"></param>
        /// <param name="circleCenter"></param>
        /// <param name="radius"></param>
        /// <returns></returns>
        public static Point[] LineCircleIntersection(Point a, Point b, Point circleCenter, double radius)
        {
            a = a - circleCenter;
            b = b - circleCenter;
            double
                A = a.Y - b.Y,
                B = b.X - a.X,
                C = -a.X * A - a.Y * B;

            var res = LineCircleIntersection(A, B, C, radius);
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
        public static Point[] LineCircleIntersection(double a, double b, double c, double radius)
        {
            double
                aabb = a * a + b * b,
                x0 = -a * c / aabb,
                y0 = -b * c / aabb;

            if (c * c > radius * radius * aabb + Const.Eps)
                return new Point[] { };

            if (Math.Abs(c * c - radius * radius * aabb) < Const.Eps)
                return new[] { new Point(x0, y0) };

            double d = radius * radius - c * c / aabb,
                mult = Math.Sqrt(d / aabb),
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

        public static double GetAngleBetween(double alpha, double beta)
        {
            alpha = AngleNormalize(alpha);
            beta = AngleNormalize(beta);
            return Math.Abs(AngleNormalize(alpha - beta));
        }

        /// <summary>
        /// Приводит угол в промежуток [-Pi, Pi]
        /// </summary>
        /// <param name="angle"></param>
        public static double AngleNormalize(double angle)
        {
            while (angle > Math.PI)
                angle -= 2.0D * Math.PI;

            while (angle < -Math.PI)
                angle += 2.0D * Math.PI;

            return angle;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static double ToDegrees(double angle)
        {
            return angle * 180 / Math.PI;
        }


        private static Point[] _findTangentPoints(double CenterX, double CenterY, double R, double XX, double YY)
        {
            // see http://stackoverflow.com/questions/21087065/tangent-to-circle-from-external-point-p

            var nx = (XX - CenterX) / R; //shift and scale
            var ny = (YY - CenterY) / R;
            var xy = nx * nx + ny * ny;
            if (Utility.Equals(xy, 1.0)) //point lies at circumference, one tangent
            {
                return new[] { new Point(XX, YY) };
            }

            if (xy < 1.0) //point lies inside the circle, no tangents
            {
                return new Point[] { };
            }

            double D, XT0, YT0, XT1, YT1;
            //common case, two tangents
            D = ny * Math.Sqrt(xy - 1);
            var tx0 = (nx - D) / xy;
            var tx1 = (nx + D) / xy;
            if (!Utility.Equals(ny, 0)) //common case
            {
                YT0 = CenterY + R * (1 - tx0 * nx) / ny;
                YT1 = CenterY + R * (1 - tx1 * nx) / ny;
            }
            else //point at the center horizontal, Y=0 
            {
                D = R * Math.Sqrt(1 - tx0 * tx0);
                YT0 = CenterY + D;
                YT1 = CenterY - D;
            }
            XT0 = CenterX + R * tx0; //restore scale and position
            XT1 = CenterX + R * tx1;
            return new[] { new Point(XT0, YT0), new Point(XT1, YT1) };
        }

        public static Point[] GetTangentPoints(Point center, double Radius, Point external)
        {
            return _findTangentPoints(center.X, center.Y, Radius, external.X, external.Y);
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static bool DoublesEquals(double a, double b)
        {
            return Math.Abs(a - b) < Const.Eps;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static bool PointsEquals(double ax, double ay, double bx, double by)
        {
            return Math.Abs(ax - bx) < Const.Eps && Math.Abs(ay - by) < Const.Eps;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public static bool PointsEquals(Point a, Point b)
        {
            return Math.Abs(a.X - b.X) < Const.Eps && Math.Abs(a.Y - b.Y) < Const.Eps;
        }
    }
}

