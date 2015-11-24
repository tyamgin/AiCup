using System.Linq;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class AProjectile : ACircularUnit
    {
        public ProjectileType Type;
        public Point Speed;
        public bool Exists = true;

        public const int UpdateIterations = 10;

        public void Move()
        {
            if (Type == ProjectileType.Washer)
            {
                X += Speed.X;
                Y += Speed.Y;
                return;
            }

            return;
            // TODO: выход за пределы карты
            for (int it = 0; it < UpdateIterations; it++)
            {
                X += Speed.X / UpdateIterations;
                Y += Speed.Y / UpdateIterations;

                var currentCell = MyStrategy.GetCell(this);
                foreach (var part in MyStrategy.MyTiles[currentCell.I, currentCell.J].Parts)
                {
                    var intersection = part.GetIntersectionPoint(this);
                }
            }
        }

        public AProjectile()
        {
            
        }

        public AProjectile(Projectile proj)
        {
            Type = proj.Type;
            X = proj.X;
            Y = proj.Y;
            Speed = new Point(proj.SpeedX, proj.SpeedY);
            Radius = proj.Radius;
        }

        public AProjectile Clone()
        {
            return new AProjectile
            {
                Type = Type,
                X = X,
                Y = Y,
                Speed = Speed.Clone(),
                Radius = Radius,
                Exists = Exists
            };
        }

        public static AProjectile[] GetProjectiles(ACar car)
        {
            if (car.Original.Type == CarType.Jeep)
            {
                return new[] { new AProjectile
                {
                    Radius = MyStrategy.game.TireRadius,
                    Type = ProjectileType.Tire,
                    X = car.X,
                    Y = car.Y,
                    Speed = Point.ByAngle(car.Angle) * MyStrategy.game.TireInitialSpeed
                }};
            }

            return new[] { 0.0, -MyStrategy.game.SideWasherAngle, MyStrategy.game.SideWasherAngle }.Select(angle => new AProjectile
            {
                Radius = MyStrategy.game.WasherRadius,
                Type = ProjectileType.Washer,
                X = car.X,
                Y = car.Y,
                Speed = Point.ByAngle(angle + car.Angle) * MyStrategy.game.WasherInitialSpeed
            }).ToArray();
        }

        public bool Intersect(ACar car, double safeMargin)//TODO: это не правильно
        {
            return GetDistanceTo2(car) < Geom.Sqr(Radius + safeMargin);
        }

        public double GetDanger()
        {
            return 1.0;
            // TODO: шины
        }
    }
}
