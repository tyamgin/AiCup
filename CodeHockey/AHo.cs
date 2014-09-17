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
        public const double AngularSpeedCoeff = 0.972981;
        public Hockeyist baseParams;
        public double AngularSpeed;

        public AHo(double x, double y, double speedX, double speedY, double angle, double angleSpeed, Hockeyist from) 
            : base(x, y, speedX, speedY, angle)
        {
            baseParams = from;
            AngularSpeed = angleSpeed;
        }

        public AHo(Point pos, Point speed, double angle, double angleSpeed, Hockeyist from)
            : base(pos, speed, angle)
        {
            baseParams = from;
            AngularSpeed = angleSpeed;
            Angle = angle;
        }

        public void Move(double speedUp, double turn)
        {
            if (speedUp < -1 || speedUp > 1 || turn > Global.game.HockeyistTurnAngleFactor || turn < -Global.game.HockeyistTurnAngleFactor)
                throw new Exception();

            turn += AngularSpeed;
            AngularSpeed *= AngularSpeedCoeff;
            var force = (speedUp >= 0 ? Global.game.HockeyistSpeedUpFactor : Global.game.HockeyistSpeedDownFactor) * speedUp;

            var dir = new Point(Angle).Normalized();
            Speed = (dir * force + Speed) * Global.FrictionHockCoeff;
            Angle = MyStrategy.AngleNormalize(Angle + turn);

            X += Speed.X;
            Y += Speed.Y;
        }

        public void Move(double speedUp, double turn, int ticks)
        {
            for(int tick = 0; tick < ticks; tick++)
                Move(speedUp, turn);
        }
    }
}
