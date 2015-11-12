using System;
using System.Collections.Generic;
using System.Linq;
using System.Security;
using System.Security.Policy;
using System.Text;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class ACar
    {
        public Car Original;
        public Point Position;
        public Point Speed;
        public double Angle;
        public double EnginePower;
        public double WheelTurn;
        public double AngularSpeed;

        private const double UpdateIterations = 10;
        private const double UpdateFactor = 1.0 / UpdateIterations;
        private static double _frictionMultiplier;
        private static double _rotationFrictionMultiplier;
        private static double _carAccelerationUp;
        private static double _carAccelerationDown;

        public ACar(Car original, Point position = null)
        {
            if (position != null)
                Position = new Point(position);
            else
                Position = new Point(original);
                
            Speed = new Point(original.SpeedX, original.SpeedY);
            Angle = original.Angle;
            EnginePower = original.EnginePower;
            WheelTurn = original.WheelTurn;
            AngularSpeed = original.AngularSpeed;
            Original = original;

            _frictionMultiplier = Math.Pow(1.0 - MyStrategy.game.CarMovementAirFrictionFactor, UpdateFactor);
            _rotationFrictionMultiplier = Math.Pow(1.0 - MyStrategy.game.CarRotationFrictionFactor, UpdateFactor);
            if (original.Type == CarType.Buggy)
            {
                _carAccelerationUp = MyStrategy.game.BuggyEngineForwardPower/original.Mass;
                _carAccelerationDown = MyStrategy.game.BuggyEngineRearPower/original.Mass;
            }
            else
            {
                _carAccelerationUp = MyStrategy.game.JeepEngineForwardPower/original.Mass;
                _carAccelerationDown = MyStrategy.game.JeepEngineRearPower/original.Mass;
            }
        }

        public ACar(ACar car)
        {
            Original = car.Original;
            Position = car.Position.Clone();
            Speed = car.Speed.Clone();
            Angle = car.Angle;
            EnginePower = car.EnginePower;
            WheelTurn = car.WheelTurn;
            AngularSpeed = car.AngularSpeed;
        }

        static double _limit(double speed, double frictionDelta)
        {
            if (speed >= 0)
            {
                speed -= frictionDelta;
                if (speed < 0)
                    speed = 0;
            }
            else
            {
                speed += frictionDelta;
                if (speed > 0)
                    speed = 0;
            }
            return speed;
        }

        public void Move(double enginePower, double wheelTurn, bool isBreak)
        {
            if (enginePower > 1 + Point.Eps || enginePower < -1 - Point.Eps)
                throw new Exception("invalid enginePower " + enginePower);

            if (wheelTurn > 1 + Point.Eps || wheelTurn < -1 - Point.Eps)
                throw new Exception("invalid wheelTurn " + wheelTurn);

            if (enginePower > EnginePower)
                EnginePower = Math.Min(EnginePower + MyStrategy.game.CarEnginePowerChangePerTick, enginePower);
            else
                EnginePower = Math.Max(EnginePower - MyStrategy.game.CarEnginePowerChangePerTick, enginePower);


            if (wheelTurn > WheelTurn)
                WheelTurn = Math.Min(WheelTurn + MyStrategy.game.CarWheelTurnChangePerTick, wheelTurn);
            else
                WheelTurn = Math.Max(WheelTurn - MyStrategy.game.CarWheelTurnChangePerTick, wheelTurn);
                

            if (MyStrategy.world.Tick >= MyStrategy.game.InitialFreezeDurationTicks)
            {
                // http://russianaicup.ru/forum/index.php?topic=394.msg3888#msg3888

                var dir = Point.ByAngle(Angle);

                var baseAngSpd = AngularSpeed; // WTF???
                AngularSpeed -= baseAngSpd;
                baseAngSpd = MyStrategy.game.CarAngularSpeedFactor * WheelTurn * (Speed * dir);
                AngularSpeed += baseAngSpd;

                var carAcceleration = EnginePower >= 0
                    ? _carAccelerationUp
                    : _carAccelerationDown;

                var accelerationDelta = dir * (carAcceleration * EnginePower * UpdateFactor);

                var lengthwiseMovementFrictionFactor = isBreak
                    ? MyStrategy.game.CarCrosswiseMovementFrictionFactor
                    : MyStrategy.game.CarLengthwiseMovementFrictionFactor;

                for (var i = 0; i < UpdateIterations; i++)
                {
                    Position += Speed * UpdateFactor;
                    if (!isBreak)
                        Speed += accelerationDelta;
                    Speed *= _frictionMultiplier;
                    Speed = dir * _limit(Speed * dir, UpdateFactor * lengthwiseMovementFrictionFactor)
                        + (~dir) * _limit(Speed * ~dir, UpdateFactor * MyStrategy.game.CarCrosswiseMovementFrictionFactor);

                    Angle += AngularSpeed*UpdateFactor;
                    dir = Point.ByAngle(Angle);
                    AngularSpeed = baseAngSpd + (AngularSpeed - baseAngSpd) * _rotationFrictionMultiplier;
                }
            }
        }

        public Point[] GetRect()
        {
            var result = new Point[4];

            var dir = new Point(Original.Width/2, Original.Height/2);
            var angle = Math.Atan2(dir.Y, dir.X);
            var angles = new[] { Angle + angle, Angle + Math.PI - angle, Angle + Math.PI + angle, Angle - angle };
            for(var i = 0; i < 4; i++)
                result[i] = Position + Point.ByAngle(angles[i]) * dir.Length;

            return result;
        }

        public double GetAngleTo(double x, double y)
        {
            var absoluteAngleTo = Math.Atan2(y - Position.Y, x - Position.X);
            var relativeAngleTo = absoluteAngleTo - Angle;

            while (relativeAngleTo > Math.PI)
            {
                relativeAngleTo -= 2.0D * Math.PI;
            }

            while (relativeAngleTo < -Math.PI)
            {
                relativeAngleTo += 2.0D * Math.PI;
            }

            return relativeAngleTo;
        }

        public double GetAngleTo(Unit unit)
        {
            return GetAngleTo(unit.X, unit.Y);
        }

        public double GetAngleTo(Point unit)
        {
            return GetAngleTo(unit.X, unit.Y);
        }
    }
}
