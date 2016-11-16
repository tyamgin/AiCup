using System;
using System.Collections.Generic;
using System.Linq;
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
                if (RemainingDistance + Const.Eps < Speed / MicroTicks)
                    return false;

                if (X - Radius < 0 || Y - Radius < 0 || X + Radius > Const.MapSize || Y + Radius > Const.MapSize)
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


        public enum ProjectilePathState
        {
            Free,
            Fire,
        }

        public class ProjectilePathSegment
        {
            public ProjectilePathState State;
            public ACombatUnit Target;
            public double StartDistance, EndDistance;
        }

        public List<ProjectilePathSegment> Emulate(IEnumerable<ACircularUnit> units)
        {
            if (Type != ProjectileType.MagicMissile)
                throw new NotImplementedException();

            var list = new List<ProjectilePathSegment>();
            var projectile = new AProjectile(this);
            while (projectile.Exists)
            {
                projectile.Move(proj =>
                {
                    var inter = proj.CheckIntersections(units);

                    if (inter != null)
                    {
                        if (list.Count == 0 || list[list.Count - 1].State != ProjectilePathState.Fire)
                        {
                            list.Add(new ProjectilePathSegment
                            {
                                StartDistance = list.Count == 0 ? 0 : list[list.Count - 1].EndDistance,
                                EndDistance = list.Count == 0 ? 0 : list[list.Count - 1].EndDistance,
                                State = ProjectilePathState.Fire,
                                Target = inter as ACombatUnit,
                            });
                        }
                    }
                    else
                    {
                        if (list.Count == 0 || list[list.Count - 1].State != ProjectilePathState.Free)
                        {
                            list.Add(new ProjectilePathSegment
                            {
                                StartDistance = list.Count == 0 ? 0 : list[list.Count - 1].EndDistance,
                                EndDistance = list.Count == 0 ? 0 : list[list.Count - 1].EndDistance,
                                State = ProjectilePathState.Free,
                            });
                        }
                    }
                    var last = list[list.Count - 1];
                    last.EndDistance += proj.Speed / AProjectile.MicroTicks;

                    return true;
                });

            }
            return list;
        }
    }
}
