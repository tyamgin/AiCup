using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public class Projectile : CircularUnit {
        private readonly ProjectileType type;
        private readonly long ownerUnitId;
        private readonly long ownerPlayerId;

        public Projectile(long id, double x, double y, double speedX, double speedY, double angle, Faction faction,
                double radius, ProjectileType type, long ownerUnitId, long ownerPlayerId)
                : base(id, x, y, speedX, speedY, angle, faction, radius) {
            this.type = type;
            this.ownerUnitId = ownerUnitId;
            this.ownerPlayerId = ownerPlayerId;
        }

        public ProjectileType Type => type;
        public long OwnerUnitId => ownerUnitId;
        public long OwnerPlayerId => ownerPlayerId;
    }
}