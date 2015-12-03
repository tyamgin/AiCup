using System;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public class ACar : ARectangularUnit
    {
        public Car Original;
        public Point Speed;
        public double EnginePower;
        public double WheelTurn;
        public double AngularSpeed;
        public bool OutOfMap;
        public int RemainingNitroTicks;
        public int RemainingNitroCooldownTicks;
        public int RemainingInactiveTicks;

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
            RemainingInactiveTicks = DurabilityObserver.ReactivationTime(original) - MyStrategy.world.Tick;

            Width = original.Width;
            Height = original.Height;

            if (original.Type == CarType.Buggy)
            {
                _carAccelerationUp = Const.Game.BuggyEngineForwardPower / original.Mass;
                _carAccelerationDown = Const.Game.BuggyEngineRearPower / original.Mass;
            }
            else
            {
                _carAccelerationUp = Const.Game.JeepEngineForwardPower / original.Mass;
                _carAccelerationDown = Const.Game.JeepEngineRearPower / original.Mass;
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
            RemainingInactiveTicks = car.RemainingInactiveTicks;

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
            if (RemainingInactiveTicks > 0 || MyStrategy.IsCrashed(Original))
            {
                isBreak = false;
                useNitro = false;
                enginePower = 0;
                wheelTurn = WheelTurn;
            }

            useNitro = useNitro && Original.NitroChargeCount > 0;
            if (useNitro && RemainingNitroTicks == 0 && RemainingNitroCooldownTicks == 0)
            {
                RemainingNitroTicks = Const.Game.NitroDurationTicks;
                RemainingNitroCooldownTicks = Const.Game.UseNitroCooldownTicks;
            }

            var updateIterations = simpleMode ? 2 : 10;
            var frictionMultiplier = Math.Pow(1.0 - Const.Game.CarMovementAirFrictionFactor, 1.0/updateIterations);
            var rotationFrictionMultiplier = Math.Pow(1.0 - Const.Game.CarRotationFrictionFactor,
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
                    ? Math.Min(EnginePower + Const.Game.CarEnginePowerChangePerTick, enginePower)
                    : Math.Max(EnginePower - Const.Game.CarEnginePowerChangePerTick, enginePower);
            }


            WheelTurn = wheelTurn > WheelTurn
                ? Math.Min(WheelTurn + Const.Game.CarWheelTurnChangePerTick, wheelTurn)
                : Math.Max(WheelTurn - Const.Game.CarWheelTurnChangePerTick, wheelTurn);


            if (MyStrategy.world.Tick >= Const.Game.InitialFreezeDurationTicks)
            {
                // http://russianaicup.ru/forum/index.php?topic=394.msg3888#msg3888

                var dir = Point.ByAngle(Angle);

                var baseAngSpd = AngularSpeed; // HACK
                AngularSpeed -= baseAngSpd;
                baseAngSpd = Const.Game.CarAngularSpeedFactor*WheelTurn*(Speed*dir);
                AngularSpeed += baseAngSpd;

                var carAcceleration = EnginePower >= 0
                    ? _carAccelerationUp
                    : _carAccelerationDown;

                var accelerationDelta = dir*(carAcceleration*EnginePower/updateIterations);

                var lengthwiseMovementFrictionFactor = isBreak
                    ? Const.Game.CarCrosswiseMovementFrictionFactor
                    : Const.Game.CarLengthwiseMovementFrictionFactor;
                lengthwiseMovementFrictionFactor /= updateIterations;
                var crosswiseMovementFrictionFactor = Const.Game.CarCrosswiseMovementFrictionFactor/
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

                Geom.AngleNormalize(ref Angle);
                if (RemainingNitroCooldownTicks > 0)
                    RemainingNitroCooldownTicks--;
                if (RemainingNitroTicks > 0)
                    RemainingNitroTicks--;
                if (RemainingInactiveTicks > 0)
                    RemainingInactiveTicks--;
            }

            // clear rectangles cache
            _rect = null;
            _rectEx = null;
        }

        public bool TakeBonus(ABonus bonus)
        {
            if (GetDistanceTo2(bonus) > Geom.Sqr(Const.CarDiagonalHalfLength + Const.BonusDiagonalHalfLength))
                return false;
            return Geom.PolygonsIntersect(GetRect(0), bonus.GetRect());
        }

        public bool IntersectWith(ACar car, double safeMargin)
        {
            if (GetDistanceTo2(car) > Geom.Sqr(Const.CarDiagonalHalfLength + Const.CarDiagonalHalfLength))
                return false;
            return Geom.PolygonsIntersect(GetRect(0), car.GetRect(safeMargin));
        }

        /*
         * Rectangles cache
         */
        private Point[] _rectEx, _rect;

        public Point[] GetRectEx()
        {
            return _rectEx ?? (_rectEx = base.GetRectEx());
        }

        public new Point[] GetRect(double safeMargin)
        {
            if (Math.Abs(safeMargin) > MyStrategy.Eps)
                return base.GetRect(safeMargin);

            return _rect ?? (_rect = base.GetRect(safeMargin));
        }
    }
}
