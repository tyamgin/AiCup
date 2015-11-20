using System;
using System.Linq;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class ACar : ARectUnit
    {
        public Car Original;
        public Point Speed;
        public double EnginePower;
        public double WheelTurn;
        public double AngularSpeed;
        public bool OutOfMap;
        public int RemainingNitroTicks;
        public int RemainingNitroCooldownTicks;

        private readonly double _carAccelerationUp;
        private readonly double _carAccelerationDown;

        public ACar(Car original)
        {
            X = original.X;
            Y = original.Y;
                
            Speed = new Point(original.SpeedX, original.SpeedY);
            Angle = original.Angle;
            EnginePower = original.EnginePower;
            WheelTurn = original.WheelTurn;
            AngularSpeed = original.AngularSpeed;
            Original = original;
            OutOfMap = false;
            RemainingNitroTicks = original.RemainingNitroTicks;
            RemainingNitroCooldownTicks = original.RemainingNitroCooldownTicks;

            Width = original.Width;
            Height = original.Height;

            if (original.Type == CarType.Buggy)
            {
                _carAccelerationUp = MyStrategy.game.BuggyEngineForwardPower / original.Mass;
                _carAccelerationDown = MyStrategy.game.BuggyEngineRearPower / original.Mass;
            }
            else
            {
                _carAccelerationUp = MyStrategy.game.JeepEngineForwardPower / original.Mass;
                _carAccelerationDown = MyStrategy.game.JeepEngineRearPower / original.Mass;
            }
        }

        public ACar(ACar car)
        {
            X = car.X;
            Y = car.Y;
            Original = car.Original;
            Speed = car.Speed.Clone();
            Angle = car.Angle;
            EnginePower = car.EnginePower;
            WheelTurn = car.WheelTurn;
            AngularSpeed = car.AngularSpeed;
            OutOfMap = car.OutOfMap;
            RemainingNitroTicks = car.RemainingNitroTicks;
            RemainingNitroCooldownTicks = car.RemainingNitroCooldownTicks;

            Width = car.Width;
            Height = car.Height;

            _carAccelerationUp = car._carAccelerationUp;
            _carAccelerationDown = car._carAccelerationDown;
        }

        public new ACar Clone()
        {
            return new ACar(this);
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

        public void Move(double enginePower, double wheelTurn, bool isBreak, bool useNitro, bool simpleMode)
        {
            useNitro = useNitro && Original.NitroChargeCount > 0;
            if (useNitro && RemainingNitroTicks == 0 && RemainingNitroCooldownTicks == 0)
            {
                RemainingNitroTicks = MyStrategy.game.NitroDurationTicks;
                RemainingNitroCooldownTicks = MyStrategy.game.UseNitroCooldownTicks;
            }

            double updateIterations = simpleMode ? 2 : 10; // TODO: make it int
            var frictionMultiplier = Math.Pow(1.0 - MyStrategy.game.CarMovementAirFrictionFactor, 1.0/updateIterations);
            var rotationFrictionMultiplier = Math.Pow(1.0 - MyStrategy.game.CarRotationFrictionFactor,
                1.0/updateIterations);

            if (enginePower > 1 + MyStrategy.Eps || enginePower < -1 - MyStrategy.Eps)
                throw new Exception("invalid enginePower " + enginePower);

            if (wheelTurn > 1 + MyStrategy.Eps || wheelTurn < -1 - MyStrategy.Eps)
                throw new Exception("invalid wheelTurn " + wheelTurn);

            if (RemainingNitroTicks > 0)
                EnginePower = 2.0;
            else
            {
                EnginePower = enginePower > EnginePower
                    ? Math.Min(EnginePower + MyStrategy.game.CarEnginePowerChangePerTick, enginePower)
                    : Math.Max(EnginePower - MyStrategy.game.CarEnginePowerChangePerTick, enginePower);
            }


            WheelTurn = wheelTurn > WheelTurn
                ? Math.Min(WheelTurn + MyStrategy.game.CarWheelTurnChangePerTick, wheelTurn)
                : Math.Max(WheelTurn - MyStrategy.game.CarWheelTurnChangePerTick, wheelTurn);


            if (MyStrategy.world.Tick >= MyStrategy.game.InitialFreezeDurationTicks)
            {
                // http://russianaicup.ru/forum/index.php?topic=394.msg3888#msg3888

                var dir = Point.ByAngle(Angle);

                var baseAngSpd = AngularSpeed; // WTF???
                AngularSpeed -= baseAngSpd;
                baseAngSpd = MyStrategy.game.CarAngularSpeedFactor*WheelTurn*(Speed*dir);
                AngularSpeed += baseAngSpd;

                var carAcceleration = EnginePower >= 0
                    ? _carAccelerationUp
                    : _carAccelerationDown;

                var accelerationDelta = dir*(carAcceleration*EnginePower/updateIterations);

                var lengthwiseMovementFrictionFactor = isBreak
                    ? MyStrategy.game.CarCrosswiseMovementFrictionFactor
                    : MyStrategy.game.CarLengthwiseMovementFrictionFactor;
                lengthwiseMovementFrictionFactor /= updateIterations;
                var crosswiseMovementFrictionFactor = MyStrategy.game.CarCrosswiseMovementFrictionFactor/
                                                      updateIterations;

                for (var i = 0; i < updateIterations; i++)
                {
                    X += Speed.X/updateIterations;
                    Y += Speed.Y/updateIterations;
                    if (!isBreak)
                    {
                        Speed.X += accelerationDelta.X;
                        Speed.Y += accelerationDelta.Y;
                    }
                    Speed.X *= frictionMultiplier;
                    Speed.Y *= frictionMultiplier;

                    var t1 = _limit(Speed.X*dir.X + Speed.Y*dir.Y, lengthwiseMovementFrictionFactor);
                    var t2 = _limit(Speed.X*dir.Y - Speed.Y*dir.X, crosswiseMovementFrictionFactor);

                    Speed.X = dir.X*t1 + dir.Y*t2;
                    Speed.Y = dir.Y*t1 - dir.X*t2;

                    Angle += AngularSpeed/updateIterations;
                    dir = Point.ByAngle(Angle);
                    AngularSpeed = baseAngSpd + (AngularSpeed - baseAngSpd)*rotationFrictionMultiplier;
                }

                if (RemainingNitroCooldownTicks > 0)
                    RemainingNitroCooldownTicks--;
                if (RemainingNitroTicks > 0)
                    RemainingNitroTicks--;
            }
        }

        public bool TakeBonus(ABonus bonus)
        {
            if (GetDistanceTo2(bonus) > Geom.Sqr(MyStrategy.CarDiagonalHalfLength + MyStrategy.BonusDiagonalHalfLength))
                return false;
            return Geom.PolygonsIntersect(GetRect(), bonus.GetRect());
        }
    }
}
