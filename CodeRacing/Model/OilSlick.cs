using System;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model {
    public class OilSlick : CircularUnit {
        private readonly int remainingLifetime;

        public OilSlick(long id, double mass, double x, double y, double speedX, double speedY, double angle,
                double angularSpeed, double radius, int remainingLifetime)
                : base(id, mass, x, y, speedX, speedY, angle, angularSpeed, radius) {
            this.remainingLifetime = remainingLifetime;
        }

        public int RemainingLifetime {
            get { return remainingLifetime; }
        }
    }
}