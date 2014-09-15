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
        private const double angleSpeedCoeff = 0.0270190131;
        public Hockeyist baseParams;
        public double AngleSpeed;

        public AHo(double x, double y, double speedX, double speedY, double angle, double angleSpeed, Hockeyist from) 
            : base(x, y, speedX, speedY, angle)
        {
            baseParams = from;
            AngleSpeed = angleSpeed;
        }

        public AHo(Point pos, Point speed, double angle, double angleSpeed, Hockeyist from)
            : base(pos, speed, angle)
        {
            baseParams = from;
            AngleSpeed = angleSpeed;
            Angle = angle;
        }

        public void Move(double speedUp, double turn)
        {
            if (speedUp < -1 || speedUp > 1 || turn > Global.game.HockeyistTurnAngleFactor || turn < -Global.game.HockeyistTurnAngleFactor)
                throw new Exception();

            var force = (speedUp >= 0 ? Global.game.HockeyistSpeedUpFactor : Global.game.HockeyistSpeedDownFactor) * speedUp;

            var dir = new Point(Angle).Normalized();
            Speed = dir.Mul(force).Add(Speed).Mul(Global.FrictionHockCoeff);
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
