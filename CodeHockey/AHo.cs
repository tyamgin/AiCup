using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public class AHo : AUnit
    {
        public static readonly double FrictionCoeff = 0.98;
        public static readonly double AngularSpeedCoeff = 0.972981;

        public Hockeyist baseParams;
        public double AngularSpeed;

        public AHo(Point pos, Point speed, double angle, double angularSpeed, Hockeyist from)
            : base(pos, speed, angle)
        {
            baseParams = from;
            AngularSpeed = angularSpeed;
            Angle = angle;
        }

        public void Move(double speedUp, double turn)
        {
            if (speedUp < -1 || speedUp > 1 || turn > MyStrategy.Game.HockeyistTurnAngleFactor || turn < -MyStrategy.Game.HockeyistTurnAngleFactor)
                throw new Exception();

            turn += AngularSpeed;
            AngularSpeed *= AngularSpeedCoeff;
            var force = (speedUp >= 0 ? MyStrategy.Game.HockeyistSpeedUpFactor : MyStrategy.Game.HockeyistSpeedDownFactor) * speedUp;

            var dir = new Point(Angle).Normalized();
            Speed = (dir * force + Speed) * AHo.FrictionCoeff;
            Angle = MyStrategy.AngleNormalize(Angle + turn);

            X += Speed.X;
            Y += Speed.Y;
        }

        public void Move(double speedUp, double turn, int ticks)
        {
            for(int tick = 0; tick < ticks; tick++)
                Move(speedUp, turn);
        }

        public AHo Clone()
        {
            return new AHo(this, Speed, Angle, AngularSpeed, baseParams);
        }
        public Point PuckPos()
        {
            return MyStrategy.GetPuckPos(this, Angle);
        }
        public Point TakePos()
        {
            return this + new Point(Angle) * MyStrategy.Game.StickLength;
        }
    }
}
