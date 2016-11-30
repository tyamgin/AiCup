using System;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        Target _findTarget(AWizard self, Point moveTo)
        {
            var t0 = FindBonusTarget(self);
            var tfrost = FindCastTarget(self, ProjectileType.FrostBolt);
            var tfball = FindCastTarget(self, ProjectileType.Fireball);
            var tmm = FindCastTarget(self, ProjectileType.MagicMissile);
            var t2 = FindStaffTarget(self);
            var t3 = FindCastTarget2(self, t0.Target ?? moveTo);

            Point ret = null;
            if (t0.Target != null)
            {
                FinalMove.Apply(t0.Move);
                ret = t0.Target;
            }

            if (tfball.Target != null && tfball.Time <= Math.Min(t2.Time, t3.Time))
            {
                FinalMove.Action = tfball.Move.Action;
                FinalMove.MinCastDistance = tfball.Move.MinCastDistance;
                FinalMove.MaxCastDistance = tfball.Move.MaxCastDistance;
                FinalMove.CastAngle = tfball.Move.CastAngle;
                return new Target { MoveTo = tfball.Target, Type = ret == null ? TargetType.Opponent : TargetType.Bonus };
            }
            if (tfrost.Target != null && tfrost.Time <= Math.Min(t2.Time, t3.Time))
            {
                FinalMove.Action = tfrost.Move.Action;
                FinalMove.MinCastDistance = tfrost.Move.MinCastDistance;
                FinalMove.MaxCastDistance = tfrost.Move.MaxCastDistance;
                FinalMove.CastAngle = tfrost.Move.CastAngle;
                return new Target { MoveTo = tfrost.Target, Type = ret == null ? TargetType.Opponent : TargetType.Bonus };
            }
            if (tmm.Target != null && tmm.Time <= Math.Min(t2.Time, t3.Time))
            {
                FinalMove.Action = tmm.Move.Action;
                FinalMove.MinCastDistance = tmm.Move.MinCastDistance;
                FinalMove.MaxCastDistance = tmm.Move.MaxCastDistance;
                FinalMove.CastAngle = tmm.Move.CastAngle;
                return new Target { MoveTo = tmm.Target, Type = ret == null ? TargetType.Opponent : TargetType.Bonus };
            }
            if (t0.Target == null && t2.Target != null && t2.Time <= Math.Min(tmm.Time, t3.Time))
            {
                FinalMove.Apply(t2.Move);
                return new Target { MoveTo = t2.Target, Type = TargetType.Opponent };
            }
            if (t3.Target != null && t3.Time <= Math.Min(tmm.Time, t2.Time))
            {
                FinalMove.Apply(t3.Move);
                return new Target { MoveTo = t3.Target, Type = TargetType.Opponent };
            }

            if (ret == null)
                return null;

            return new Target { MoveTo = ret, Type = TargetType.Bonus };
        }

        MovingInfo FindBonusTarget(AWizard self)
        {
            const int grid = 24;
            var minTime = int.MaxValue;
            var selGo = 0;
            Point selMoveTo = null;
            foreach (var _bonus in BonusesObserver.Bonuses)
            {
                if (_bonus.GetDistanceTo(self) - self.Radius - _bonus.Radius > Game.StaffRange * 3)
                    continue;
                if (_bonus.RemainingAppearanceTicks > 60)
                    continue;

                var nearest = Combats
                    .Where(x => x.Id != self.Id && self.GetDistanceTo2(x) < Geom.Sqr(self.VisionRange))
                    .ToArray();


                for (var i = 0; i < grid; i++)
                {
                    var bonus = new ABonus(_bonus);
                    var angle = Math.PI * 2 / grid * i + self.Angle;
                    var my = new AWizard(self);
                    var moveTo = my + Point.ByAngle(angle) * self.VisionRange;
                    int time = 0;
                    int go = 0;
                    while (my.GetDistanceTo(bonus) > my.Radius + bonus.Radius && time < 60)
                    {
                        if (!my.MoveTo(moveTo, null, w => !CheckIntersectionsAndTress(w, nearest)))
                        {
                            break;
                        }
                        var wait = !bonus.Exists;
                        bonus.SkipTick();
                        time++;
                        if (my.GetDistanceTo(bonus) <= my.Radius + bonus.Radius)
                        {
                            while (!bonus.Exists)
                            {
                                bonus.SkipTick();
                                time++;
                            }
                            if (wait)
                                time++;

                            if (time < minTime)
                            {
                                minTime = time;
                                selMoveTo = moveTo;
                                selGo = go;
                            }
                            break;
                        }
                        go++;
                    }

                }
            }
            var moving = new MovingInfo(selMoveTo, minTime, new FinalMove(new Move()));
            if (selMoveTo != null)
            {
                if (minTime == 1 || selGo > 0)
                    moving.Move.MoveTo(selMoveTo, null);
                else
                    moving.Target = self;
            }
            return moving;
        }

        MovingInfo _findStaffTarget(AWizard self)
        {
            var nearest = Combats
                .Where(x => x.Id != self.Id && self.GetDistanceTo2(x) < Geom.Sqr(Game.StaffRange * 6))
                .ToArray();
            int minTicks = int.MaxValue;
            var move = new FinalMove(new Move());

            var attacked = self.GetStaffAttacked(nearest).Cast<ACombatUnit>().ToArray();

            ACircularUnit selTarget = attacked.FirstOrDefault(x => x.IsOpponent);
            if (selTarget != null) // если уже можно бить
            {
                move.Action = ActionType.Staff;
                return new MovingInfo(selTarget, 0, move);
            }

            if (self.MmSkillLevel == 5)
            {
                // т.к. стрелять можно без задержки
                // возможно, нужно сделать исключение, если прокачан посох
                return new MovingInfo(null, int.MaxValue, move);
            }

            Point selMoveTo = null;

            foreach (var opp in OpponentCombats)
            {
                var dist = self.GetDistanceTo(opp);
                if (dist > Game.StaffRange * 3 || !opp.IsAssailable)
                    continue;

                var range = opp.Radius + Game.StaffRange;
                foreach (var delta in new[] { -range, -range / 2, 0, range / 2, range })
                {
                    var angle = Math.Atan2(delta, dist);
                    var moveTo = self + (opp - self).Normalized().RotateClockwise(angle) * self.VisionRange;

                    var nearstCombats = OpponentCombats
                        .Where(x => x.GetDistanceTo(self) <= x.VisionRange)
                        .Select(Utility.CloneCombat)
                        .ToArray();

                    var canHitNow = opp.EthalonCanHit(self);

                    var ticks = 0;
                    var my = new AWizard(self);
                    var his = Utility.CloneCombat(opp);
                    var ok = true;

                    while (my.GetDistanceTo2(his) > Geom.Sqr(Game.StaffRange + his.Radius))
                    {
                        his.EthalonMove(my);
                        if (!my.MoveTo(moveTo, his, w => !CheckIntersectionsAndTress(w, nearest)))
                        {
                            ok = false;
                            break;
                        }
                        foreach (var x in nearstCombats)
                            x.EthalonMove(my);
                        ticks++;
                    }

                    if (ok && !(opp is AOrc))
                    {
                        while (Math.Abs(my.GetAngleTo(his)) > Game.StaffSector / 2)
                        {
                            my.MoveTo(null, his);
                            foreach (var x in nearstCombats)
                                x.EthalonMove(my);
                            his.EthalonMove(my);
                            ticks++;
                        }
                    }

                    if (ok && ticks < minTicks)
                    {
                        if (my.CanStaffAttack(his))
                        {
                            if (nearstCombats.All(x => canHitNow && x.Id == opp.Id || !x.EthalonCanHit(my)))
                            {
                                // успею-ли я вернуться обратно
                                while (my.GetDistanceTo(self) > my.MaxForwardSpeed)//TODO:HACK
                                {
                                    my.MoveTo(self, null);
                                    foreach (var x in nearstCombats)
                                        x.SkipTick();
                                }
                                if (nearstCombats.All(x => canHitNow && x.Id == opp.Id || !x.EthalonCanHit(my)))
                                {
                                    selTarget = opp;
                                    selMoveTo = moveTo;
                                    minTicks = ticks;
                                }
                            }
                        }
                    }
                }
            }
            if (selTarget != null)
            {
                bool angleOk = Math.Abs(self.GetAngleTo(selTarget)) <= Game.StaffSector / 2,
                    distOk = self.GetDistanceTo2(selTarget) <= Geom.Sqr(Game.StaffRange + selTarget.Radius);

                if (!distOk)
                {
                    move.MoveTo(selMoveTo, selTarget);
                }
                else if (!angleOk)
                {
                    move.MoveTo(null, selTarget);
                }
            }
            return new MovingInfo(selTarget, minTicks, move);
        }

        MovingInfo _findCastTarget(AWizard self, ProjectileType projectileType)
        {
            var actionType = Utility.GetActionByProjectileType(projectileType);

            var move = new FinalMove(new Move());
            if (self.RemainingActionCooldownTicks > 0 || 
                self.RemainingCooldownTicksByAction[(int) actionType] > 0 ||
                self.Mana < Const.ProjectileInfo[(int) projectileType].ManaCost ||
                !self.IsActionAvailable(actionType)
                )
                return new MovingInfo(null, int.MaxValue, move);

            var angles = new List<double>();
            foreach (var x in OpponentCombats)
            {
                var distTo = self.GetDistanceTo(x);
                if (distTo > self.CastRange + x.Radius + Const.ProjectileInfo[(int)projectileType].DamageRadius + 3)
                    continue;

                var angleTo = self.GetAngleTo(x);
                if (Math.Abs(angleTo) > Math.PI/3)
                    continue;

                const int grid = 16;
                double left = -Game.StaffSector / 2, right = -left;
                for (var i = 0; i <= grid; i++)
                {
                    var castAngle = (right - left) / grid * i + left;
                    angles.Add(castAngle);
                }
                // нужно найти хотябы 1
                break;
            }

            ACombatUnit selTarget = null;
            double
                selMinDist = 0,
                selMaxDist = self.CastRange + 20,
                selCastAngle = 0;

            if (projectileType == ProjectileType.Fireball)
            {
                double
                    selPriority = int.MaxValue,
                    maxDamage = 0;
                int maxBurned = 0;

                foreach (var angle in angles)
                {
                    var proj = new AProjectile(new AWizard(self), angle, projectileType);
                    var path = EmulateMagicMissile(proj);
                    for (var i = 0; i < path.Count; i++)
                    {
                        var seg = path[i];
                        if (seg.State == AProjectile.ProjectilePathState.Free)
                            continue;

                        if (seg.SelfDamage > 0) // TODO: можно и пожертвовать
                            continue;

                        if (seg.OpponentBurned > maxBurned 
                            || seg.OpponentBurned == maxBurned && seg.OpponentDamage > maxDamage 
                            //|| seg.OpponentBurned == maxBurned && Utility.Equals(seg.OpponentDamage, maxDamage)
                            //TODO: combare by angle and priority
                            )
                        {
                            if (seg.OpponentBurned > 2 
                                || self.Mana >= 2*Game.FireballManacost && seg.OpponentBurned > 1 
                                || seg.OpponentBurned == 1 && seg.Target is AWizard
                                || seg.OpponentBurned == 1 && seg.Target is ABuilding && self.Mana >= 2 * Game.FireballManacost
                                )
                            {
                                maxBurned = seg.OpponentBurned;
                                maxDamage = seg.OpponentDamage;
                                selCastAngle = angle;
                                selMinDist = selMaxDist = seg.StartDistance;
                                selTarget = seg.Target;
                            }
                        }
                    }
                }
            }
            else
            {
                double
                    selPriority = int.MaxValue,
                    selAngleTo = 0;

                foreach (var angle in angles)
                {
                    var proj = new AProjectile(new AWizard(self), angle, projectileType);
                    var path = EmulateMagicMissile(proj);
                    for (var i = 0; i < path.Count; i++)
                    {
                        if (path[i].State == AProjectile.ProjectilePathState.Free)
                            continue;

                        // TODO: если можно убить нескольких, убивать того, у кого больше жизней
                        var combat = path[i].Target;
                        if (!combat.IsAssailable)
                            continue;

                        var myAngle = self.Angle + angle;
                        var hisAngle = self.Angle + self.GetAngleTo(combat);
                        var angleTo = Geom.GetAngleBetween(myAngle, hisAngle);

                        var priority = GetCombatPriority(self, combat);
                        if (combat.IsOpponent && (priority < selPriority || Utility.Equals(priority, selPriority) && angleTo < selAngleTo))
                        {
                            selTarget = combat;
                            selCastAngle = angle;
                            selAngleTo = angleTo;
                            selMinDist = i == 0 || path[i - 1].State == AProjectile.ProjectilePathState.Free && path[i - 1].Length < 40
                                ? path[i].StartDistance - 1
                                : path[i].StartDistance - 20;
                            selMaxDist = i >= path.Count - 2
                                ? (self.CastRange + 500)
                                : (path[i + 1].EndDistance + path[i].EndDistance)/2;
                            selPriority = priority;
                        }
                    }
                }
            }
            if (selTarget == null)
                return new MovingInfo(null, int.MaxValue, move);

            move.Action = actionType;
            move.MinCastDistance = selMinDist;
            move.MaxCastDistance = selMaxDist;
            move.CastAngle = selCastAngle;
#if DEBUG
            _lastProjectileTick = World.TickIndex;
            _lastProjectilePoints = new[]
            {
                self + Point.ByAngle(self.Angle + selCastAngle) * selMinDist,
                self + Point.ByAngle(self.Angle + selCastAngle) * Math.Min(Self.CastRange, selMaxDist),
            };
#endif
            return new MovingInfo(selTarget, 0, move);
        }

        MovingInfo _findCastTarget2(AWizard self, Point moveTo)
        {
            var move = new FinalMove(new Move());
            var nearest = Combats
                .Where(x => x.Id != self.Id && self.GetDistanceTo2(x) < Geom.Sqr(self.VisionRange * 1.3))
                .ToArray();

            var targetsSelector = new TargetsSelector(nearest) { EnableMinionsCache = true };

            ACircularUnit selTarget = null;
            var minTicks = int.MaxValue;
            double minPriority = int.MaxValue;

            foreach (var opp in OpponentCombats)
            {
                if (self.GetDistanceTo2(opp) > Geom.Sqr(self.CastRange + opp.Radius + 60) || !opp.IsAssailable)
                    continue;

                var nearstCombats = nearest
                    .Where(x => x.IsOpponent)
                    .Select(Utility.CloneCombat)
                    .ToArray();

                var canHitNow = opp.EthalonCanHit(self);

                var ticks = 0;
                var my = new AWizard(self);
                var his = Utility.CloneCombat(opp);
                var ok = true;

                while (!my.EthalonCanCastMagicMissile(his, checkCooldown: false))
                {
                    // только поворачиваться, если и так близко
                    if (!my.MoveTo(
                        moveTo ?? (my.GetDistanceTo2(his) < Geom.Sqr(my.CastRange) ? null : his),
                        his,
                        w => !CheckIntersectionsAndTress(w, nearest))

                        || ticks > 40 // снаряду может помешать дерево
                        )
                    {
                        ok = false;
                        break;
                    }
                    foreach (var x in nearstCombats)
                        x.EthalonMove(my);
                    ticks++;
                }

                if (his is AWizard && CanRush(my, his))
                    ticks -= 10;// чтобы дать больше приоритета визарду

                var priority = GetCombatPriority(self, his);
                if (ok && (ticks < minTicks || ticks == minTicks && priority < minPriority))
                {
                    if (my.EthalonCanCastMagicMissile(his))
                    {
                        if (nearstCombats.All(x =>
                        {
                            //TODO: возможно, скопировать эти условия и для staff
                            if (canHitNow && x.Id == opp.Id) // он и так доставал
                                return true;

                            if (!x.EthalonCanHit(my))
                                return true;

                            if (his.Id == x.Id && CanRush(my, x))
                                return true;

                            var target = targetsSelector.Select(x);
                            if (target != null && target.Id != my.Id)
                                return true;

                            return false;
                        })
                            )
                        {
                            minTicks = ticks;
                            minPriority = priority;
                            selTarget = opp;
                        }
                    }
                }
            }

            if (selTarget == null)
                return new MovingInfo(null, int.MaxValue, move);

            minTicks = Math.Max(0, minTicks);
            move.MoveTo(moveTo ?? (self.GetDistanceTo2(selTarget) < Geom.Sqr(self.CastRange) ? null : selTarget), selTarget);
            return new MovingInfo(selTarget, minTicks, move);
        }

        Target FindTarget(AWizard self, Point moveTo = null)
        {
            TimerStart();
            var ret = _findTarget(self, moveTo);
            TimerEndLog("FindTarget", 1);
            return ret;
        }

        MovingInfo FindStaffTarget(AWizard self)
        {
            TimerStart();
            var ret = _findStaffTarget(self);
            TimerEndLog("FindStaffTarget", 1);
            return ret;
        }

        MovingInfo FindCastTarget(AWizard self, ProjectileType projectileType)
        {
            TimerStart();
            var ret = _findCastTarget(self, projectileType);
            TimerEndLog("FindCastTarget", 1);
            return ret;
        }

        MovingInfo FindCastTarget2(AWizard self, Point moveTo = null)
        {
            TimerStart();
            var ret = _findCastTarget2(self, moveTo);
            TimerEndLog("FindCastTarget2", 1);
            return ret;
        }

        List<AProjectile.ProjectilePathSegment> EmulateMagicMissile(AProjectile projectile)
        {
            // TODO: не передавать всех, а только ближайших
            return projectile.Emulate(Combats);
        }

        static double GetCombatPriority(AWizard self, ACombatUnit unit)
        {
            // чем меньше - тем важнее стрелять в него первого
            var res = unit.Life;
            if (unit is AWizard)
                res /= 4;
            var dist = self.GetDistanceTo(unit);
            if (dist <= Game.StaffRange + unit.Radius + 10)
            {
                res -= 60;
                res += Math.Log(dist);
            }
            return res;
        }

        public static int _lastProjectileTick;
        public static Point[] _lastProjectilePoints;

        bool CanRush(AWizard self, ACombatUnit opp)
        {
            var wizard = opp as AWizard;
            var minion = opp as AMinion;

            if (wizard != null)
            {
                if (wizard.Life <= self.MagicMissileDamage)
                    return true;
                if (self.Life <= wizard.MagicMissileDamage)
                    return false;

                if (self.Life >= wizard.Life + 3 * self.MagicMissileDamage)
                    return true;
            }
            else if (minion != null)
            {
                if (minion.Life <= self.MagicMissileDamage)
                    return true;
            }
            return false;
        }

        bool CheckIntersectionsAndTress(AWizard self, IEnumerable<ACircularUnit> units)
        {
            if (self.CheckIntersections(units) != null)
                return true;
            var nearestTree = TreesObserver.GetNearestTree(self);
            return nearestTree != null && self.IntersectsWith(nearestTree);
        }




        enum TargetType
        {
            Opponent,
            Bonus,
        }

        class Target
        {
            public Point MoveTo;
            public TargetType Type;
        }

        class MovingInfo
        {
            public Point Target;
            public int Time;
            public FinalMove Move;

            public MovingInfo(Point target, int time, FinalMove move)
            {
                Target = target;
                Time = time;
                Move = move;
            }
        }
    }
}
