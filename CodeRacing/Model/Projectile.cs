using System;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model {
    public class Projectile : CircularUnit {
        private readonly long carId;
        private readonly long playerId;
        private readonly ProjectileType type;

        public Projectile(long id, double mass, double x, double y, double speedX, double speedY, double angle,
                double angularSpeed, double radius, long carId, long playerId, ProjectileType type)
                : base(id, mass, x, y, speedX, speedY, angle, angularSpeed, radius) {
            this.carId = carId;
            this.playerId = playerId;
            this.type = type;
        }

        public long CarId {
            get { return carId; }
        }

        public long PlayerId {
            get { return playerId; }
        }

        public ProjectileType Type {
            get { return type; }
        }
    }
}