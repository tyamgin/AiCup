using System;
using System.Linq;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public class AVehicle : ACircularUnit
    {
        public bool IsMy;
        public VehicleType Type;
        public double Durability;
        public bool IsSelected;
        public double MaxSpeed; // TODO: computable property

        public Point Target;
        public double Speed;
        public Point RotationCenter;
        public double RotationAngle;
        public double AngularSpeed;

        public AVehicle(Vehicle unit) : base(unit)
        {
            IsMy = unit.PlayerId == MyStrategy.Me.Id;
            Type = unit.Type;
            Durability = unit.Durability;
            IsSelected = unit.IsSelected;
            MaxSpeed = unit.MaxSpeed;
        }

        public AVehicle(AVehicle unit) : base(unit)
        {
            IsMy = unit.IsMy;
            Type = unit.Type;
            Durability = unit.Durability;
            IsSelected = unit.IsSelected;
            MaxSpeed = unit.MaxSpeed;

            Target = unit.Target;
            Speed = unit.Speed;
            RotationCenter = unit.RotationCenter;
            RotationAngle = unit.RotationAngle;
            AngularSpeed = unit.AngularSpeed;
        }

        private bool IsAerial => Type == VehicleType.Helicopter || Type == VehicleType.Fighter;

        public bool Move(Func<ACircularUnit, bool> checkCollisions = null)
        {
            var prevX = X;
            var prevY = Y;
            var prevRotationAngle = RotationAngle;
            Point delta = null;
            var done = false;

            if (Target != null)
            {
                var vec = Target - this;
                double length;
                var speed = ActualSpeed;

                if (vec.Length <= speed)
                {
                    done = true;
                    length = vec.Length;
                }
                else
                {
                    length = speed;
                }
                delta = vec.Normalized()*length;
            }
            else if (RotationCenter != null)
            {
                var angle = ActualAngularSpeed;
                if (angle >= Math.Abs(RotationAngle))
                {
                    done = true;
                    angle = RotationAngle;
                }
                else
                {
                    if (RotationAngle < 0)
                        angle = -angle;
                    
                    RotationAngle -= angle;
                }
                var to = RotateCounterClockwise(angle, RotationCenter);
                delta = to - this;
            }

            if (delta != null)
            {
                X += delta.X;
                Y += delta.Y;
                if (X < Radius - Const.Eps || 
                    Y < Radius - Const.Eps || 
                    X > Const.MapSize - Radius + Const.Eps ||
                    Y > Const.MapSize - Radius + Const.Eps || 
                    checkCollisions != null && checkCollisions(this))
                {
                    X = prevX;
                    Y = prevY;
                    RotationAngle = prevRotationAngle;
                    return false;
                }
                if (done)
                {
                    Target = null;
                    Speed = 0;
                    RotationCenter = null;
                    RotationAngle = 0;
                    AngularSpeed = 0;
                }
            }


            return true;
        }

        public static void Move(AVehicle[] units)
        {
            var moved = new bool[units.Length];
            var movedCount = 0;
            //units = units.OrderBy(x => -x.Id).ToArray();
            while (movedCount < units.Length)
            {
                var anyMoved = false;
                for (var i = 0; i < units.Length; i++)
                {
                    if (moved[i])
                        continue;
                    var isAerial = units[i].IsAerial;
                    if (!units[i].Move(x => x.GetFirstIntersection(units.Where(u => u.IsAerial == isAerial)) != null))
                        continue;
                    moved[i] = true;
                    anyMoved = true;
                    movedCount++;
                }

                if (!anyMoved)
                    break;
            }
        }

        public double ActualSpeed
        {
            get
            {
                var speed = MaxSpeed;
                int I, J;
                Utility.GetCell(this, out I, out J);
                if (IsAerial)
                {
                    var weather = MyStrategy.WeatherType[I][J];
                    if (weather == WeatherType.Cloud)
                        speed *= G.CloudWeatherSpeedFactor;
                    else if (weather == WeatherType.Rain)
                        speed *= G.RainWeatherSpeedFactor;
                }
                else
                {
                    var terrian = MyStrategy.TerrainType[I][J];
                    if (terrian == TerrainType.Swamp)
                        speed *= G.SwampTerrainSpeedFactor;
                    else if (terrian == TerrainType.Forest)
                        speed *= G.ForestTerrainSpeedFactor;
                }
                if (Speed > 0 && Speed < speed)
                    speed = Speed;
                return speed;
            }
        }

        public double ActualAngularSpeed
        {
            get
            {
                var speed = ActualSpeed;
                var angle = speed/GetDistanceTo(RotationCenter);
                if (AngularSpeed > 0 && AngularSpeed < angle)
                    angle = AngularSpeed;
                return angle;
            }
        }
    }
}
