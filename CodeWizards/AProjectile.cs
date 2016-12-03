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
        public double Damage;
        public double MinDamage; // for Fireball

        public AProjectile(Projectile unit) : base(unit)
        {
            SpeedX = unit.SpeedX;
            SpeedY = unit.SpeedY;
            Speed = Geom.Hypot(SpeedX, SpeedY);
            Type = unit.Type;
            OwnerUnitId = unit.OwnerUnitId;
            RemainingDistance = 0; // это значение должно перезаписываться использующим кодом
            _setupDefaultDamage();
        }

        public AProjectile(AProjectile unit) : base(unit)
        {
            SpeedX = unit.SpeedX;
            SpeedY = unit.SpeedY;
            Speed = Geom.Hypot(SpeedX, SpeedY);
            Type = unit.Type;
            OwnerUnitId = unit.OwnerUnitId;
            RemainingDistance = unit.RemainingDistance;
            Damage = unit.Damage;
            MinDamage = unit.MinDamage;
        }

        public AProjectile(ACombatUnit self, double castAngle, ProjectileType type)
        {
            Type = type;
            Speed = Const.ProjectileInfo[(int) type].Speed;
            Radius = Const.ProjectileInfo[(int) type].Radius;
            X = self.X;
            Y = self.Y;
            SpeedX = Math.Cos(self.Angle + castAngle) *Speed;
            SpeedY = Math.Sin(self.Angle + castAngle) *Speed;
            RemainingDistance = self.CastRange;
            OwnerUnitId = self.Id;
            Faction = self.Faction;
            SetupDamage(self);
        }

        private void _setupDefaultDamage()
        {
            switch (Type)
            {
                case ProjectileType.Fireball:
                    Damage = MyStrategy.Game.FireballExplosionMaxDamage;
                    MinDamage = MyStrategy.Game.FireballExplosionMinDamage;
                    break;
                case ProjectileType.MagicMissile:
                    Damage = MyStrategy.Game.MagicMissileDirectDamage;
                    break;
                case ProjectileType.FrostBolt:
                    Damage = MyStrategy.Game.FrostBoltDirectDamage;
                    break;
                case ProjectileType.Dart:
                    Damage = MyStrategy.Game.DartDirectDamage;
                    break;
            }
        }

        public void SetupDamage(ACombatUnit unit)
        {
            var self = unit as AWizard;
            switch (Type)
            {
                case ProjectileType.Fireball:
                    Damage = self.FireballMaxDamage;
                    MinDamage = self.FireballMinDamage;
                    break;
                case ProjectileType.MagicMissile:
                    Damage = self.MagicMissileDamage;
                    break;
                case ProjectileType.FrostBolt:
                    Damage = self.FrostBoltDamage;
                    break;
                case ProjectileType.Dart:
                    Damage = MyStrategy.Game.DartDirectDamage;
                    break;
            }
        }

        public bool Exists
        {
            get
            {
                if (RemainingDistance < -Const.Eps)
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

            RemainingDistance -= Speed/MicroTicks;
            X += SpeedX/MicroTicks;
            Y += SpeedY/MicroTicks;
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

                if (!Exists)
                    return false;
                if (check != null && !check(this))
                    return false;
            }
            if (nearestTree != null && Geom.SegmentCircleIntersects(prev, this, nearestTree, nearestTree.Radius + Radius))
            {
                // снаряд ударился об дерево (это более точная проверка)
                RemainingDistance = 0;
                return false;
            }

            return true;
        }

        public bool IsFriendly => Faction == MyStrategy.Self.Faction;


        public enum ProjectilePathState
        {
            Free,
            Shot,
            Fireball,
        }

        public class ProjectilePathSegment
        {
            public ProjectilePathState State;
            public ACombatUnit Target;
            public int OpponentDeadsCount;
            public int SelfDeadsCount;
            public double OpponentDamage;
            public double SelfDamage;
            public int OpponentBurned;
            public int SelfBurned;
            public double StartDistance, EndDistance;

            public double Length => EndDistance - StartDistance;

            public bool Same(ProjectilePathSegment seg)
            {
                throw new NotImplementedException();
            }
        }

        public static double GetFireballDamage(AProjectile proj, ACombatUnit unit) // без учета сколько жизней осталось
        {
            var dist = proj.GetDistanceTo(unit) - unit.Radius;
            double damage = 0;
            if (dist <= MyStrategy.Game.FireballExplosionMaxDamageRange)
                damage += proj.Damage;
            else if (dist <= MyStrategy.Game.FireballExplosionMinDamageRange)
            {
                var ratio = 1 - (dist - MyStrategy.Game.FireballExplosionMaxDamageRange)/(MyStrategy.Game.FireballExplosionMinDamageRange - MyStrategy.Game.FireballExplosionMaxDamageRange);
                damage += ratio*(proj.Damage - proj.MinDamage) + proj.MinDamage;
            }
            return damage;
        }

        public List<ProjectilePathSegment> Emulate(ACombatUnit[] _units)
        {
            if (Type != ProjectileType.MagicMissile && Type != ProjectileType.FrostBolt && Type != ProjectileType.Fireball)
                throw new NotImplementedException();

            var list = new List<ProjectilePathSegment>();
            var projectile = new AProjectile(this);
            var units = _units.Where(x => x.Id != OwnerUnitId).Select(Utility.CloneCombat).ToArray();
            var owner = _units.FirstOrDefault(x => x.Id == OwnerUnitId);

            var minionsTargetsSelector = new TargetsSelector(_units) {EnableMinionsCache = true};

            while (projectile.Exists)
            {
                projectile.Move(proj =>
                {
                    if (Type == ProjectileType.Fireball)
                    {
                        double selfDamage = 0;
                        double oppDamage = 0;
                        var selfBurned = 0;
                        var oppBurned = 0;
                        var selfDeads = 0;
                        var oppDeads = 0;

                        ACombatUnit firstTarget = null;

                        foreach (var unit in units)
                        {
                            var damage = GetFireballDamage(proj, unit);

                            if (damage > 0)
                            {
                                var deads = 0;
                                if (damage >= unit.Life - Const.Eps)
                                {
                                    // killed
                                    deads++;
                                    damage = unit.Life;
                                }

                                if (unit.Faction == proj.Faction)
                                {
                                    if (unit is AWizard)
                                    {
                                        selfDamage += damage;
                                        selfBurned++;
                                        selfDeads += deads;
                                    }
                                }
                                else if (Utility.HasConflicts(proj, unit))
                                {
                                    oppDamage += damage;
                                    oppBurned++;
                                    oppDeads += deads;
                                    firstTarget = unit;
                                }
                            }
                        }
                        if (owner != null && !projectile.IntersectsWith(owner))
                        {
                            var damage = GetFireballDamage(proj, owner);
                            if (damage > 0)
                            {
                                selfBurned++;
                                if (damage >= owner.Life - Const.Eps)
                                {
                                    // killed
                                    selfDamage += owner.Life;
                                    selfDeads++;
                                }
                                else
                                {
                                    selfDamage += damage;
                                }
                            }
                        }
                        list.Add(new ProjectilePathSegment
                        {
                            StartDistance = list.Count == 0 ? 0 : list.Last().EndDistance, 
                            EndDistance = list.Count == 0 ? 0 : list.Last().EndDistance, 
                            OpponentDamage = oppDamage, 
                            SelfDamage = selfDamage, 
                            OpponentDeadsCount = oppDeads, 
                            SelfDeadsCount = selfDeads, 
                            State = (selfDamage + oppDamage < Const.Eps) ? ProjectilePathState.Free : ProjectilePathState.Fireball, 
                            OpponentBurned = oppBurned, 
                            SelfBurned = selfBurned, 
                            Target = firstTarget,
                        });
                    }
                    else
                    {
                        var inter = proj.CheckIntersections(units);

                        if (inter != null)
                        {
                            if (list.Count == 0 || list.Last().State != ProjectilePathState.Shot)
                            {
                                var opp = inter as ACombatUnit;
                                list.Add(new ProjectilePathSegment
                                {
                                    StartDistance = list.Count == 0 ? 0 : list.Last().EndDistance, 
                                    EndDistance = list.Count == 0 ? 0 : list.Last().EndDistance, 
                                    State = ProjectilePathState.Shot, 
                                    Target = Utility.CloneCombat(opp),
                                    SelfDamage = proj.Faction == inter.Faction ? Math.Min(opp.Life, Damage) : 0,
                                    OpponentDamage = Utility.HasConflicts(proj, opp) ? Math.Min(opp.Life, Damage) : 0,
                                });
                            }
                        }
                        else
                        {
                            if (list.Count == 0 || list.Last().State != ProjectilePathState.Free)
                            {
                                list.Add(new ProjectilePathSegment
                                {
                                    StartDistance = list.Count == 0 ? 0 : list.Last().EndDistance,
                                    EndDistance = list.Count == 0 ? 0 : list.Last().EndDistance,
                                    State = ProjectilePathState.Free,
                                });
                            }
                        }
                    }
                    list.Last().EndDistance += proj.Speed/AProjectile.MicroTicks;

                    return true;
                });
                foreach (var unit in units)
                {
                    if (unit is AWizard)
                    {
                        // TODO: он может убежать боком
                        var wizard = unit as AWizard;
                        var dir = wizard - this + wizard; // вдоль снаряда
                        wizard.MoveTo(dir, null, w =>
                        {
                            var tree = TreesObserver.GetNearestTree(w);
                            return tree == null || !w.IntersectsWith(tree);
                        });
                    }
                    else if (unit is AMinion)
                    {
                        var target = minionsTargetsSelector.Select(unit);
                        unit.EthalonMove(target);
                    }
                }
            }
            return list;
        }
    }
}
