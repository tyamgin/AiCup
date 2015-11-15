using System;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model {
    public class Car : RectangularUnit {
        private readonly long playerId;
        private readonly int teammateIndex;
        private readonly bool isTeammate;
        private readonly CarType type;
        private readonly int projectileCount;
        private readonly int nitroChargeCount;
        private readonly int oilCanisterCount;
        private readonly int remainingProjectileCooldownTicks;
        private readonly int remainingNitroCooldownTicks;
        private readonly int remainingOilCooldownTicks;
        private readonly int remainingNitroTicks;
        private readonly int remainingOiledTicks;
        private readonly double durability;
        private readonly double enginePower;
        private readonly double wheelTurn;
        private readonly int nextWaypointIndex;
        private readonly int nextWaypointX;
        private readonly int nextWaypointY;
        private readonly bool isFinishedTrack;

        public Car(long id, double mass, double x, double y, double speedX, double speedY, double angle,
                double angularSpeed, double width, double height, long playerId, int teammateIndex, bool isTeammate,
                CarType type, int projectileCount, int nitroChargeCount, int oilCanisterCount,
                int remainingProjectileCooldownTicks, int remainingNitroCooldownTicks, int remainingOilCooldownTicks,
                int remainingNitroTicks, int remainingOiledTicks, double durability, double enginePower,
                double wheelTurn, int nextWaypointIndex, int nextWaypointX, int nextWaypointY, bool isFinishedTrack)
                : base(id, mass, x, y, speedX, speedY, angle, angularSpeed, width, height) {
            this.playerId = playerId;
            this.teammateIndex = teammateIndex;
            this.isTeammate = isTeammate;
            this.type = type;
            this.projectileCount = projectileCount;
            this.nitroChargeCount = nitroChargeCount;
            this.oilCanisterCount = oilCanisterCount;
            this.remainingProjectileCooldownTicks = remainingProjectileCooldownTicks;
            this.remainingNitroCooldownTicks = remainingNitroCooldownTicks;
            this.remainingOilCooldownTicks = remainingOilCooldownTicks;
            this.remainingNitroTicks = remainingNitroTicks;
            this.remainingOiledTicks = remainingOiledTicks;
            this.durability = durability;
            this.enginePower = enginePower;
            this.wheelTurn = wheelTurn;
            this.nextWaypointIndex = nextWaypointIndex;
            this.nextWaypointX = nextWaypointX;
            this.nextWaypointY = nextWaypointY;
            this.isFinishedTrack = isFinishedTrack;
        }

        public long PlayerId {
            get { return playerId; }
        }

        public int TeammateIndex {
            get { return teammateIndex; }
        }

        public bool IsTeammate {
            get { return isTeammate; }
        }

        public CarType Type {
            get { return type; }
        }

        public int ProjectileCount {
            get { return projectileCount; }
        }

        public int NitroChargeCount {
            get { return nitroChargeCount; }
        }

        public int OilCanisterCount {
            get { return oilCanisterCount; }
        }

        public int RemainingProjectileCooldownTicks {
            get { return remainingProjectileCooldownTicks; }
        }

        public int RemainingNitroCooldownTicks {
            get { return remainingNitroCooldownTicks; }
        }

        public int RemainingOilCooldownTicks {
            get { return remainingOilCooldownTicks; }
        }

        public int RemainingNitroTicks {
            get { return remainingNitroTicks; }
        }

        public int RemainingOiledTicks {
            get { return remainingOiledTicks; }
        }

        public double Durability {
            get { return durability; }
        }

        public double EnginePower {
            get { return enginePower; }
        }

        public double WheelTurn {
            get { return wheelTurn; }
        }

        public int NextWaypointIndex {
            get { return nextWaypointIndex; }
        }

        public int NextWaypointX {
            get { return nextWaypointX; }
        }

        public int NextWaypointY {
            get { return nextWaypointY; }
        }

        public bool IsFinishedTrack {
            get { return isFinishedTrack; }
        }
    }
}