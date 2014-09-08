using System;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model {
    public class Puck : Unit {
        private readonly long ownerHockeyistId;
        private readonly long ownerPlayerId;

        public Puck(long id, double mass, double radius, double x, double y, double speedX, double speedY,
                long ownerHockeyistId, long ownerPlayerId)
                : base(id, mass, radius, x, y, speedX, speedY, 0.0D, 0.0D) {
            this.ownerHockeyistId = ownerHockeyistId;
            this.ownerPlayerId = ownerPlayerId;
        }

        public long OwnerHockeyistId {
            get { return ownerHockeyistId; }
        }

        public long OwnerPlayerId {
            get { return ownerPlayerId; }
        }
    }
}