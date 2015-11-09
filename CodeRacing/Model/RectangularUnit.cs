using System;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model {
    public abstract class RectangularUnit : Unit {
        private readonly double width;
        private readonly double height;

        protected RectangularUnit(long id, double mass, double x, double y, double speedX, double speedY, double angle,
                double angularSpeed, double width, double height)
                : base(id, mass, x, y, speedX, speedY, angle, angularSpeed) {
            this.width = width;
            this.height = height;
        }

        public double Width {
            get { return width; }
        }

        public double Height {
            get { return height; }
        }
    }
}