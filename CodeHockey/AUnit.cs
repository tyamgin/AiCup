using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public class AUnit : Point
    {
        public Point Speed;
        public double Angle;

        public AUnit(double x, double y, double speedX, double speedY, double angle) : base(x, y)
        {
            Speed = new Point(speedX, speedY);
            Angle = angle;
        }

        public AUnit(Point pos, Point speed, double angle)
            : base(pos)
        {
            Speed = new Point(speed);
            Angle = angle;
        }
    }
}
