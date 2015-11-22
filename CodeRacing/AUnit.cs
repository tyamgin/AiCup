using System;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class AUnit : Point
    {
        public double Angle;

        public double GetAngleTo(double x, double y)
        {
            var absoluteAngleTo = Math.Atan2(y - Y, x - X);
            var relativeAngleTo = absoluteAngleTo - Angle;

            while (relativeAngleTo > Math.PI)
                relativeAngleTo -= 2.0D * Math.PI;

            while (relativeAngleTo < -Math.PI)
                relativeAngleTo += 2.0D * Math.PI;

            return relativeAngleTo;
        }

        public double GetAngleTo(Unit unit)
        {
            return GetAngleTo(unit.X, unit.Y);
        }

        public double GetAngleTo(Point unit)
        {
            return GetAngleTo(unit.X, unit.Y);
        }
    }

    public class ARectangularUnit : AUnit
    {
        public double Width, Height;

        public Point[] GetRect(double reduceBorder = 0)
        {
            // 3  o---o  0
            //    o ^ o
            //    o   o
            // 2  o---o  1
            var dir = new Point(Width/2 - reduceBorder, Height/2 - reduceBorder);
            var angle = Math.Atan2(dir.Y, dir.X);
            var angles = new[] { Angle + angle, Angle + Math.PI - angle, Angle + Math.PI + angle, Angle - angle };
            var result = new Point[4];
            for (var i = 0; i < 4; i++)
                result[i] = this + ByAngle(angles[i]) * dir.Length;

            return result;
        }
    }

    public class ACircularUnit : AUnit
    {
        public double Radius;
    }

    public class AOilSlick : ACircularUnit
    {
        public int RemainingLifetime;

        public AOilSlick(OilSlick slick)
        {
            X = slick.X;
            Y = slick.Y;
            Radius = slick.Radius;
            RemainingLifetime = slick.RemainingLifetime;
        }

        public AOilSlick(ACar car)
        {        
            var dist = MyStrategy.game.OilSlickInitialRange + car.Original.Width/2 + MyStrategy.game.OilSlickRadius;
            var slick = car - Point.ByAngle(car.Angle)*dist;
            X = slick.X;
            Y = slick.Y;
            Radius = MyStrategy.game.OilSlickRadius;
            RemainingLifetime = MyStrategy.game.OilSlickLifetime;
        }

        public bool Intersect(ACar car, double safeMargin)
        {
            return GetDistanceTo2(car) < Geom.Sqr(Radius + safeMargin);
        }

        public double GetDanger()
        {
            // HACK
            if (RemainingLifetime < -2)
                return 0;
            if (RemainingLifetime <= 0)
                return 0.6;

            return 0.6 + 0.4*RemainingLifetime/MyStrategy.game.OilSlickLifetime;
        }
    }
}
