using System;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model {
    public abstract class Unit {
        private readonly long id;
        private readonly double mass;
        private readonly double radius;
        private readonly double x;
        private readonly double y;
        private readonly double speedX;
        private readonly double speedY;
        private readonly double angle;
        private readonly double angularSpeed;

        protected Unit(long id, double mass, double radius, double x, double y, double speedX, double speedY,
                double angle, double angularSpeed) {
            this.id = id;
            this.mass = mass;
            this.radius = radius;
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
            this.angle = angle;
            this.angularSpeed = angularSpeed;
        }

        public long Id {
            get { return id; }
        }

        public double Mass {
            get { return mass; }
        }

        public double Radius {
            get { return radius; }
        }

        public double X {
            get { return x; }
        }

        public double Y {
            get { return y; }
        }

        public double SpeedX {
            get { return speedX; }
        }

        public double SpeedY {
            get { return speedY; }
        }

        public double Angle {
            get { return angle; }
        }

        public double AngularSpeed {
            get { return angularSpeed; }
        }

        public double GetAngleTo(double x, double y) {
            double absoluteAngleTo = Math.Atan2(y - this.y, x - this.x);
            double relativeAngleTo = absoluteAngleTo - angle;

            while (relativeAngleTo > Math.PI) {
                relativeAngleTo -= 2.0D * Math.PI;
            }

            while (relativeAngleTo < -Math.PI) {
                relativeAngleTo += 2.0D * Math.PI;
            }

            return relativeAngleTo;
        }

        public double GetAngleTo(Unit unit) {
            return GetAngleTo(unit.x, unit.y);
        }

        public double GetDistanceTo(double x, double y) {
            double xRange = x - this.x;
            double yRange = y - this.y;
            return Math.Sqrt(xRange * xRange + yRange * yRange);
        }

        public double GetDistanceTo(Unit unit) {
            return GetDistanceTo(unit.x, unit.y);
        }
    }
}