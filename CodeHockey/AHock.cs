using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Point = Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Point;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public class AHock : AUnit
    {
        public static readonly double FrictionCoeff = 0.98;
        public static readonly double AngularSpeedCoeff = 0.972981;

        public Hockeyist Base;
        public double AngularSpeed;
        public int CoolDown;
        public int KnockDown;

        public AHock(Hockeyist self) : base(MyStrategy.Get(self), MyStrategy.GetSpeed(self), self.Angle)
        {
            Base = self;
            AngularSpeed = self.AngularSpeed;
            Angle = self.Angle;
            CoolDown = self.RemainingCooldownTicks;
            KnockDown = self.RemainingKnockdownTicks;
        }

        public AHock(Point pos, Point speed, double angle, double angularSpeed, int coolDown, int knockDown, Hockeyist from)
            : base(pos, speed, angle)
        {
            Base = from;
            AngularSpeed = angularSpeed;
            Angle = angle;
            CoolDown = coolDown;
            KnockDown = knockDown;
        }

        public void Move(double speedUp, double turn)
        {
            if (speedUp < -1 || speedUp > 1 || turn > MyStrategy.TurnRange(Base.Agility) || turn < -MyStrategy.TurnRange(Base.Agility))
                throw new Exception("AHo Move: " + speedUp + " " + turn);

            speedUp = speedUp*Base.Agility/100;
            if (CoolDown > 0)
                CoolDown--;
            if (KnockDown > 0)
            {
                KnockDown--;
                speedUp = 0;
                turn = 0;
            }

            turn += AngularSpeed;
            AngularSpeed *= AngularSpeedCoeff;
            var force = (speedUp >= 0 ? MyStrategy.Game.HockeyistSpeedUpFactor : MyStrategy.Game.HockeyistSpeedDownFactor) * speedUp;

            var dir = new Point(Angle).Normalized();
            Speed = (dir * force + Speed) * AHock.FrictionCoeff;
            Angle = MyStrategy.AngleNormalize(Angle + turn);

            X += Speed.X;
            Y += Speed.Y;
        }

        public void Move(double speedUp, double turn, int ticks)
        {
            for(var tick = 0; tick < ticks; tick++)
                Move(speedUp, turn);
        }

        public new AHock Clone()
        {
            return new AHock(this, Speed, Angle, AngularSpeed, CoolDown, KnockDown, Base);
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
