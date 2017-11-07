using System;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model {
    public abstract class CircularUnit : Unit {
        private readonly double radius;

        protected CircularUnit(long id, double x, double y, double radius)
                : base(id, x, y) {
            this.radius = radius;
        }

        public double Radius => radius;
    }
}