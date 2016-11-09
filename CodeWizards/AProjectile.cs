using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class AProjectile : ACircularUnit
    {
        public static readonly int MicroTicks = 10;

        public double SpeedX, SpeedY, Speed;
        public ProjectileType Type;
        public double RemainingDistance;


        public AProjectile(Projectile unit) : base(unit)
        {
            SpeedX = unit.SpeedX;
            SpeedY = unit.SpeedY;
            Speed = Geom.Hypot(SpeedX, SpeedY);
            Type = unit.Type;
            RemainingDistance = MyStrategy.game.WizardCastRange;
        }

        public AProjectile(ACombatUnit self, double castAngle, ProjectileType type)
        {
            Type = type;
            switch (type)
            {
                case ProjectileType.MagicMissile:
                    Speed = MyStrategy.game.MagicMissileSpeed;
                    Radius = MyStrategy.game.MagicMissileRadius;
                    break;
                default:
                    throw new NotImplementedException(type.ToString());
            }
            X = self.X;
            Y = self.Y;
            SpeedX = Math.Cos(self.Angle + castAngle) *Speed;
            SpeedY = Math.Sin(self.Angle + castAngle) *Speed;
            RemainingDistance = MyStrategy.game.WizardCastRange;
        }

        public bool Exists
        {
            get
            {
                if (RemainingDistance < Speed)
                    return false;///TODO TODO TODO

                if (X < 0 || Y < 0 || X > Const.Width || Y > Const.Height)
                    return false;
                return true;
            }
        } 

        public bool Move()
        {
            if (!Exists)
                return false;

            RemainingDistance -= Speed;
            X += SpeedX;
            Y += SpeedY;
            return true;
        }

        public bool MicroMove()
        {
            if (!Exists)
                return false;

            RemainingDistance -= Speed / MicroTicks;
            X += SpeedX / MicroTicks;
            Y += SpeedY / MicroTicks;
            return true;
        }

        public ACircularUnit CheckIntersections(ACircularUnit[] units)
        {
            foreach (var unit in units)
            {
                if (IntersectsWith(unit))
                    return unit;
            }
            return null;
        }
    }
}
