using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
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
            var dist = Const.Game.OilSlickInitialRange + car.Original.Width / 2 + Const.Game.OilSlickRadius;
            var slick = car - Point.ByAngle(car.Angle) * dist;
            X = slick.X;
            Y = slick.Y;
            Radius = Const.Game.OilSlickRadius;
            RemainingLifetime = Const.Game.OilSlickLifetime;
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

            return 0.6 + 0.4 * RemainingLifetime / Const.Game.OilSlickLifetime;
        }
    }
}
