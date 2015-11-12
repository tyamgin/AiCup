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
        private const double CarAcceleration = 0.25; // = Power / Mass
        private static double _frictionMultiplier;
        private static double _rotationFrictionMultiplier;

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

        public void Move(double enginePower, double wheelTurn)
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

                var accelerationDelta = dir * (CarAcceleration * EnginePower * UpdateFactor);

                for (var i = 0; i < UpdateIterations; i++)
                {
                    Position += Speed * UpdateFactor;
                    Speed += accelerationDelta; 
                    Speed *= _frictionMultiplier;
                    Speed = dir * _limit(Speed * dir, UpdateFactor * MyStrategy.game.CarLengthwiseMovementFrictionFactor)
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
    }
}
