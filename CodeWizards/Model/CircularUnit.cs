using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public abstract class CircularUnit : Unit {
        private readonly double radius;

        protected CircularUnit(long id, double x, double y, double speedX, double speedY, double angle, Faction faction,
                double radius)
                : base(id, x, y, speedX, speedY, angle, faction) {
            this.radius = radius;
        }

        public double Radius => radius;
    }
}