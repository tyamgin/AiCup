using System;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model {
    public abstract class CircularUnit : Unit {
        private readonly double radius;

        protected CircularUnit(long id, double mass, double x, double y, double speedX, double speedY, double angle,
                double angularSpeed, double radius)
                : base(id, mass, x, y, speedX, speedY, angle, angularSpeed) {
            this.radius = radius;
        }

        public double Radius {
            get { return radius; }
        }
    }
}