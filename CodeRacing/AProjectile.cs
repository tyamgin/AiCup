using System.Linq;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class AProjectile : ACircularUnit
    {
        public ProjectileType Type;
        public Point Speed;
        public bool Exists = true; // Вышел-ли за пределы поля, или исчез

        public const int UpdateIterations = 30;

        public void Move()
        {
            if (!Exists)
                return;

            if (Type == ProjectileType.Washer)
            {
                X += Speed.X;
                Y += Speed.Y;

                if (X < 0 || Y < 0 || X > MyStrategy.MapWidth || Y > MyStrategy.MapHeight)
                    Exists = false;
                return;
            }

            var reflected = false;

            for (int it = 0; it < UpdateIterations; it++)
            {
                X += Speed.X / UpdateIterations;
                Y += Speed.Y / UpdateIterations;

                // может выйти за пределы если тайлы unknown
                if (X < 1 || Y < 1 || X >= MyStrategy.MapWidth - 1 || Y >= MyStrategy.MapHeight - 1)
                {
                    Exists = false;
                    break;
                }

                if (reflected)
                    continue;

                var currentCell = MyStrategy.GetCell(this);
                foreach (var part in MyStrategy.MyTiles[currentCell.I, currentCell.J].Parts)
                {
                    Point e = null, n = null;
                    // e - направление стены
                    // n - нормаль от стены
                    double en = 0, et = 0, d = 0, d1 = 0, d2 = 0;

                    if (part.Type == TilePartType.Segment)
                    {
                        var pts = Geom.LineCircleIntersect(part.Start, part.End, this, this.Radius);
                        if (pts.Length == 0)
                            continue;

                        
                        e = (part.End - part.Start).Normalized();
                        n = ~e * -1;
                    }
                    else
                    {
                        if (GetDistanceTo2(part.Circle) <= Geom.Sqr(Radius + part.Circle.Radius))
                        {
                            n = (this - part.Circle).Normalized();
                            e = ~n;
                        }
                    }

                    if (e == null || n == null)
                        continue;

                    en = Speed * n; // норм.
                    et = Speed * e; // танг.

                    en *= -0.5;

                    d = n.X * e.Y - n.Y * e.X;
                    d1 = n.X * et - en * e.X;
                    d2 = en * e.Y - n.Y * et;
                    Speed.X = d2 / d;
                    Speed.Y = d1 / d;

                    reflected = true;
                    break;
                }
            }
            if (Speed.Length <= MyStrategy.game.TireDisappearSpeedFactor*MyStrategy.game.TireInitialSpeed)
                Exists = false;
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

        public new AProjectile Clone()
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

        public bool Intersect(ACar car, double extendRadius)
        {
            if (Type == ProjectileType.Washer)
                return Geom.ContainPoint(car.GetRect(-extendRadius), this);

            var r = Radius + extendRadius;
            if (GetDistanceTo2(car) > Geom.Sqr(r + MyStrategy.CarDiagonalHalfLength))
                return false;

            return car.GetRectEx().Any(p => GetDistanceTo(p) < r);
        }

        public double GetDanger()
        {
            if (Type == ProjectileType.Washer)
                return 1.0;
            return 2.0;
        }
    }
}
