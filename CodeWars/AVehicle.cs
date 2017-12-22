using System;
using System.Collections.Generic;
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

        public MoveType Action;
        public Point MoveVectorOrRotationCenter;
        public double MoveSpeedOrAngularSpeed;
        public double MoveLengthOrRotationAngle;
        public int DurabilityPool;

        public int Index; // служебное свойство

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

            Action = unit.Action;
            MoveVectorOrRotationCenter = unit.MoveVectorOrRotationCenter;
            MoveSpeedOrAngularSpeed = unit.MoveSpeedOrAngularSpeed;
            MoveLengthOrRotationAngle = unit.MoveLengthOrRotationAngle;

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

        public AVehicle(Point center, long id, VehicleType type, bool isMy)
        {
            Radius = G.VehicleRadius;
            X = center.X;
            Y = center.Y;
            Id = id;

            IsMy = isMy;
            Type = type;
            Durability = G.MaxDurability;
            IsSelected = false;
            RemainingAttackCooldownTicks = 0;
            Groups = 0;
            CanChargeFacility = !Utility.IsAerial(type);
            DurabilityPool = 0;
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
            var newMoveLengthOrRotationAngle = MoveLengthOrRotationAngle;

            double deltaX = 0;
            double deltaY = 0;
            var done = false;

            if (Action == MoveType.Move || Action == MoveType.Scale)
            {
                var vecX = MoveVectorOrRotationCenter.X;
                var vecY = MoveVectorOrRotationCenter.Y;
                
                var speed = ActualSpeed;

                if (MoveLengthOrRotationAngle - Const.Eps <= speed)
                {
                    done = true;
                    deltaX = MoveLengthOrRotationAngle*vecX;
                    deltaY = MoveLengthOrRotationAngle*vecY;
                    newMoveLengthOrRotationAngle = 0;
                }
                else
                {
                    deltaX = speed*vecX;
                    deltaY = speed*vecY;
                    newMoveLengthOrRotationAngle -= speed;
                }
            }
            else if (Action == MoveType.Rotate)
            {
                var angle = ActualAngularSpeed;
                if (angle + Const.Eps >= Math.Abs(MoveLengthOrRotationAngle))
                {
                    done = true;
                    angle = MoveLengthOrRotationAngle;
                    newMoveLengthOrRotationAngle = 0;
                }
                else
                {
                    if (MoveLengthOrRotationAngle < 0)
                        angle = -angle;

                    newMoveLengthOrRotationAngle -= angle;
                }
                var to = RotateCounterClockwise(angle, MoveVectorOrRotationCenter);
                deltaX = to.X - X;
                deltaY = to.Y - Y;
            }

            if (Action != MoveType.None)
            {
                if (X + deltaX < Radius - Const.Eps ||
                    Y + deltaY < Radius - Const.Eps ||
                    X + deltaX > G.MapSize - Radius + Const.Eps ||
                    Y + deltaY > G.MapSize - Radius + Const.Eps)
                {
                    return false;
                }

                X += deltaX;
                Y += deltaY;
                MoveLengthOrRotationAngle = newMoveLengthOrRotationAngle;

                if (done)
                {
                    ForgotTarget();
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
                if (MoveSpeedOrAngularSpeed > 0 && MoveSpeedOrAngularSpeed < speed)
                    speed = MoveSpeedOrAngularSpeed;
                return speed;
            }
        }

        public double ActualAngularSpeed
        {
            get
            {
                var speed = ActualSpeed;
                var angle = speed/GetDistanceTo(MoveVectorOrRotationCenter);
                if (MoveSpeedOrAngularSpeed > 0 && MoveSpeedOrAngularSpeed < angle)
                    angle = MoveSpeedOrAngularSpeed;
                return angle;
            }
        }

        public bool IsAlive => Durability > Const.Eps;

        public double StealthFactor
        {
            get
            {
                if (IsAerial)
                {
                    var weather = MyStrategy.Weather(X, Y);
                    if (weather == WeatherType.Cloud)
                        return G.CloudWeatherStealthFactor;
                    if (weather == WeatherType.Rain)
                        return G.RainWeatherStealthFactor;
                }
                else
                {
                    var terrian = MyStrategy.Terrain(X, Y);
                    if (terrian == TerrainType.Forest)
                        return G.ForestTerrainStealthFactor;
                }
                return 1;
            }
        }

        public bool IsVisible(AVehicle vehicle)
        {
            var visionRange = vehicle.ActualVisionRange * vehicle.StealthFactor;
            return visionRange*visionRange + Const.Eps >= GetDistanceTo2(vehicle);
        }

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

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public void ForgotTarget()
        {
            Action = MoveType.None;
            MoveVectorOrRotationCenter = null;
            MoveSpeedOrAngularSpeed = 0;
            MoveLengthOrRotationAngle = 0;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
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

        public bool Stopped => Action == MoveType.None;

        public override string ToString()
        {
            var groupsStr = Groups == 0 ? "" : "(" + string.Join(", ", GroupsList) + ")";
            return "(" + X + ", " + Y + ") " + Type + groupsStr + " " + Durability + "%";
        }

        public enum MoveType
        {
            None,
            Move,
            Scale,
            Rotate,
        }
    }
}
