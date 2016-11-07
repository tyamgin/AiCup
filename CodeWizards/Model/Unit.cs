using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public abstract class Unit {
        private readonly long id;
        private readonly double x;
        private readonly double y;
        private readonly double speedX;
        private readonly double speedY;
        private readonly double angle;
        private readonly Faction faction;

        protected Unit(long id, double x, double y, double speedX, double speedY, double angle, Faction faction) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
            this.angle = angle;
            this.faction = faction;
        }

        public long Id => id;
        public double X => x;
        public double Y => y;
        public double SpeedX => speedX;
        public double SpeedY => speedY;
        public double Angle => angle;
        public Faction Faction => faction;

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