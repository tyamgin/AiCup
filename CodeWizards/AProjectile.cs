using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Runtime.Remoting.Messaging;
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
        public long OwnerUnitId;


        public AProjectile(Projectile unit) : base(unit)
        {
            SpeedX = unit.SpeedX;
            SpeedY = unit.SpeedY;
            Speed = Geom.Hypot(SpeedX, SpeedY);
            Type = unit.Type;
            OwnerUnitId = unit.OwnerUnitId;
            RemainingDistance = MyStrategy.Game.WizardCastRange;
        }

        public AProjectile(AProjectile unit) : base(unit)
        {
            SpeedX = unit.SpeedX;
            SpeedY = unit.SpeedY;
            Speed = Geom.Hypot(SpeedX, SpeedY);
            Type = unit.Type;
            OwnerUnitId = unit.OwnerUnitId;
            RemainingDistance = MyStrategy.Game.WizardCastRange;
        }

        public AProjectile(ACombatUnit self, double castAngle, ProjectileType type)
        {
            Type = type;
            switch (type)
            {
                case ProjectileType.MagicMissile:
                    Speed = MyStrategy.Game.MagicMissileSpeed;
                    Radius = MyStrategy.Game.MagicMissileRadius;
                    break;
                default:
                    throw new NotImplementedException(type.ToString());
            }
            X = self.X;
            Y = self.Y;
            SpeedX = Math.Cos(self.Angle + castAngle) *Speed;
            SpeedY = Math.Sin(self.Angle + castAngle) *Speed;
            RemainingDistance = MyStrategy.Game.WizardCastRange;
            OwnerUnitId = self.Id;
        }

        public bool Exists
        {
            get
            {
                if (RemainingDistance < Speed/ MicroTicks)
                    return false;

                if (X < 0 || Y < 0 || X > Const.Width || Y > Const.Height)
                    return false;
                return true;
            }
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

        public void Move()
        {
            for (var i = 0; i < MicroTicks; i++)
                MicroMove();
        }

        public bool IsFriendly => MyStrategy.FriendsIds.Contains(OwnerUnitId);
    }
}
