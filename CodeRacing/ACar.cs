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
                if (EnginePower >= 0)
                {
                    Speed = Speed + Point.ByAngle(Angle)*EnginePower*0.25;
                    Speed = Speed - Speed*MyStrategy.game.CarMovementAirFrictionFactor;
                }
                else
                {
                    throw new NotImplementedException();
                }

                // прямо пропорционально текущему относительному углу поворота колёс кодемобиля
                // (car.wheelTurn), коэффцициенту game.carAngularSpeedFactor, а также скалярному
                // произведению вектора скорости кодемобиля и единичного вектора, направление которого
                // совпадает с направлением кодемобиля.

                var additionalAngularSpeed = WheelTurn*MyStrategy.game.CarAngularSpeedFactor*
                                             (Speed*Point.ByAngle(Angle));
                AngularSpeed += additionalAngularSpeed;
                Angle += AngularSpeed;

                Speed += Point.One * AngularSpeed;
            }

            Position += Speed;
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
