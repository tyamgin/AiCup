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
            if (self.Id == MyStrategy.Self.Id)
                RemainingDistance -= 20; //TODO HACK 
            OwnerUnitId = self.Id;
        }

        public bool Exists
        {
            get
            {
                if (RemainingDistance < Speed / MicroTicks)
                    return false;

                if (X < 0 || Y < 0 || X > Const.Width || Y > Const.Height)
                    return false;
                return true;
            }
        }

        public void MicroMove()
        {
            if (!Exists)
                return;

            RemainingDistance -= Speed / MicroTicks;
            X += SpeedX / MicroTicks;
            Y += SpeedY / MicroTicks;
        }

        public delegate bool CheckProjectile(AProjectile proj);

        public bool Move(CheckProjectile check = null)
        {
            var prev = new Point(this);
            var nearestTree = TreesObserver.GetNearestTree(this);
            for (var i = 0; i < MicroTicks; i++)
            {
                if (!Exists)
                    return false;

                MicroMove();
                if (nearestTree != null && GetDistanceTo2(nearestTree) <= Math.Sqrt(Radius + nearestTree.Radius))
                {
                    // снаряд ударился об дерево
                    RemainingDistance = 0;
                    return false;
                }

                if (check != null && !check(this))
                    return false;
            }
            if (nearestTree != null &&
                Geom.SegmentCircleIntersects(prev, this, nearestTree, nearestTree.Radius + Radius))
            {
                // снаряд ударился об дерево (это более точная проверка)
                RemainingDistance = 0;
                return false;
            }

            return true;
        }

        public bool IsFriendly => MyStrategy.FriendsIds.Contains(OwnerUnitId);
    }
}
