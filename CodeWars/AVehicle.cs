using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.CompilerServices;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class AVehicle : ACircularUnit
    {
        public bool IsMy;
        public VehicleType Type;
        public int Durability;
        public bool IsSelected;
        public int RemainingAttackCooldownTicks;
        public ulong Groups;
        public bool CanChargeFacility;

        public Point MoveTarget;
        public double MoveSpeed;
        public Point RotationCenter;
        public double RotationAngle;
        public double RotationAngularSpeed;
        public int DurabilityPool;

        public int Index; //

        public AVehicle(Vehicle unit) : base(unit)
        {
            IsMy = unit.PlayerId == MyStrategy.Me.Id;
            Type = unit.Type;
            Durability = unit.Durability;
            IsSelected = unit.IsSelected;
            RemainingAttackCooldownTicks = unit.RemainingAttackCooldownTicks;
            foreach (var group in unit.Groups)
                AddGroup(group);
            CanChargeFacility = !unit.IsAerial;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        private void _copyFrom(AVehicle unit)
        {
            IsMy = unit.IsMy;
            Type = unit.Type;
            Durability = unit.Durability;
            IsSelected = unit.IsSelected;
            RemainingAttackCooldownTicks = unit.RemainingAttackCooldownTicks;
            Groups = unit.Groups;
            CanChargeFacility = unit.CanChargeFacility;

            MoveTarget = unit.MoveTarget;
            MoveSpeed = unit.MoveSpeed;
            RotationCenter = unit.RotationCenter;
            RotationAngle = unit.RotationAngle;
            RotationAngularSpeed = unit.RotationAngularSpeed;
            DurabilityPool = unit.DurabilityPool;
        }

        public IEnumerable<int> GroupsList
        {
            get
            {
                var groupId = 0;
                var mask = Groups;
                while (mask != 0)
                {
                    if ((mask & 1) != 0)
                        yield return groupId;
                    mask >>= 1;
                    groupId++;
                }
            }
        }

        public void CopyFrom(AVehicle unit)
        {
            Radius = unit.Radius;
            X = unit.X;
            Y = unit.Y;
            Id = unit.Id;
            _copyFrom(unit);
        }

        public AVehicle(AVehicle unit) : base(unit)
        {
            _copyFrom(unit);
        }

        public double FullDurability => Durability + (double) DurabilityPool/G.ArrvRepairPoints;

        public void Repair()
        {
            if (Durability == G.MaxDurability)
                return;
            DurabilityPool++;
            if (DurabilityPool == G.ArrvRepairPoints)
            {
                Durability++;
                DurabilityPool = 0;
            }
        }

        public bool HasGroup(int groupId)
        {
            return (Groups & (1UL << groupId)) != 0;
        }

        public void AddGroup(int groupId)
        {
            Groups |= 1UL << groupId;
        }

        public void RemoveGroup(int groupId)
        {
            Groups &= ~(1UL << groupId);
        }

        public bool IsAerial => Type == VehicleType.Helicopter || Type == VehicleType.Fighter;

        public bool Move()
        {
            var newRotationAngle = RotationAngle;

            double deltaX = 0;
            double deltaY = 0;
            var done = false;

            if (MoveTarget != null)
            {
                var vecX = MoveTarget.X - X;
                var vecY = MoveTarget.Y - Y;
                
                var speed = ActualSpeed;

                var vecLen2 = vecX*vecX + vecY*vecY;
                if (vecLen2 - Const.Eps <= speed*speed)
                {
                    done = true;
                    deltaX = vecX;
                    deltaY = vecY;
                }
                else
                {
                    var factor = speed/Math.Sqrt(vecLen2);
                    deltaX = vecX*factor;
                    deltaY = vecY*factor;
                }
            }
            else if (RotationCenter != null)
            {
                var angle = ActualAngularSpeed;
                if (angle + Const.Eps >= Math.Abs(RotationAngle))
                {
                    done = true;
                    angle = RotationAngle;
                }
                else
                {
                    if (RotationAngle < 0)
                        angle = -angle;

                    newRotationAngle -= angle;
                }
                var to = RotateCounterClockwise(angle, RotationCenter);
                deltaX = to.X - X;
                deltaY = to.Y - Y;
            }

            if (MoveTarget != null || RotationCenter != null)
            {
                if (X + deltaX < Radius - Const.Eps)
                {
                    return false;
                }
                if (Y + deltaY < Radius - Const.Eps)
                {
                    return false;
                }
                if (X + deltaX > G.MapSize - Radius + Const.Eps)
                {
                    return false;
                }
                if (Y + deltaY > G.MapSize - Radius + Const.Eps)
                {
                    return false;
                }

                X += deltaX;
                Y += deltaY;
                RotationAngle = newRotationAngle;

                if (done)
                {
                    MoveTarget = null;
                    MoveSpeed = 0;
                    RotationCenter = null;
                    RotationAngle = 0;
                    RotationAngularSpeed = 0;
                }
            }
            return true;
        }

        public double ActualSpeed
        {
            get
            {
                var speed = G.MaxSpeed[(int) Type];
                if (IsAerial)
                {
                    var weather = MyStrategy.Weather(X, Y);
                    if (weather == WeatherType.Cloud)
                        speed *= G.CloudWeatherSpeedFactor;
                    else if (weather == WeatherType.Rain)
                        speed *= G.RainWeatherSpeedFactor;
                }
                else
                {
                    var terrian = MyStrategy.Terrain(X, Y);
                    if (terrian == TerrainType.Swamp)
                        speed *= G.SwampTerrainSpeedFactor;
                    else if (terrian == TerrainType.Forest)
                        speed *= G.ForestTerrainSpeedFactor;
                }
                if (MoveSpeed > 0 && MoveSpeed < speed)
                    speed = MoveSpeed;
                return speed;
            }
        }

        public double ActualAngularSpeed
        {
            get
            {
                var speed = ActualSpeed;
                var angle = speed/GetDistanceTo(RotationCenter);
                if (RotationAngularSpeed > 0 && RotationAngularSpeed < angle)
                    angle = RotationAngularSpeed;
                return angle;
            }
        }

        public bool IsAlive => Durability > Const.Eps;

        public int GetAttackDamage(AVehicle veh, double additionalRadius = 0)
        {
            var damage = GetAttackDamage2(veh, additionalRadius);
            if (damage >= veh.Durability)
                return veh.Durability;
            return damage;
        }

        public int GetAttackDamage2(AVehicle veh, double additionalRadius = 0)
        {
            var attackRange = Geom.Sqr(G.AttackRange[(int)Type, (int)veh.Type] + additionalRadius);

            if (GetDistanceTo2(veh) - Const.Eps > attackRange)
                return 0;
            return G.AttackDamage[(int)Type, (int)veh.Type];
        }

        public void Attack(AVehicle veh)
        {
            if (RemainingAttackCooldownTicks > 0)
                return;

            var damage = GetAttackDamage(veh);
            veh.Durability -= damage;
            RemainingAttackCooldownTicks = G.AttackCooldownTicks;
        }

        public int GetNuclearDamage(ANuclear nuclear)
        {
            var dist2 = GetDistanceTo2(nuclear);
            if (dist2 >= G.TacticalNuclearStrikeRadius * G.TacticalNuclearStrikeRadius)
                return 0;
            var damage = (int)((1 - Math.Sqrt(dist2) / G.TacticalNuclearStrikeRadius) * G.MaxTacticalNuclearStrikeDamage);
            return Math.Min(damage, Durability);
        }

        public void ForgotTarget()
        {
            MoveTarget = null;
            MoveSpeed = 0;
            RotationCenter = null;
            RotationAngle = 0;
            RotationAngularSpeed = 0;
        }

        public bool IsGroup(MyGroup group)
        {
            return HasGroup(group.Group);
        }

        public double ActualVisionRange
        {
            get
            {
                var res = G.VisionRange[(int) Type];
                if (IsAerial)
                {
                    var weather = MyStrategy.Weather(X, Y);
                    if (weather == WeatherType.Cloud)
                        res *= G.CloudWeatherVisionFactor;
                    else if (weather == WeatherType.Rain)
                        res *= G.RainWeatherVisionFactor;
                }
                else
                {
                    var terrian = MyStrategy.Terrain(X, Y);
                    if (terrian == TerrainType.Forest)
                        res *= G.ForestTerrainVisionFactor;
                }
                return res;
            }
        }

        public bool Stopped => MoveTarget == null && RotationCenter == null;

        public override string ToString()
        {
            var groupsStr = Groups == 0 ? "" : "(" + string.Join(", ", GroupsList) + ")";
            return "(" + X + ", " + Y + ") " + Type + groupsStr + " " + Durability + "%";
        }
    }
}
