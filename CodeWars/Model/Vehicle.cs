using System;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model {
    public class Vehicle : CircularUnit {
        private readonly long playerId;
        private readonly int durability;
        private readonly int maxDurability;
        private readonly double maxSpeed;
        private readonly double visionRange;
        private readonly double squaredVisionRange;
        private readonly double groundAttackRange;
        private readonly double squaredGroundAttackRange;
        private readonly double aerialAttackRange;
        private readonly double squaredAerialAttackRange;
        private readonly int groundDamage;
        private readonly int aerialDamage;
        private readonly int groundDefence;
        private readonly int aerialDefence;
        private readonly int attackCooldownTicks;
        private readonly int remainingAttackCooldownTicks;
        private readonly VehicleType type;
        private readonly bool isAerial;
        private readonly bool isSelected;
        private readonly int[] groups;

        public Vehicle(long id, double x, double y, double radius, long playerId, int durability, int maxDurability,
                double maxSpeed, double visionRange, double squaredVisionRange, double groundAttackRange,
                double squaredGroundAttackRange, double aerialAttackRange, double squaredAerialAttackRange,
                int groundDamage, int aerialDamage, int groundDefence, int aerialDefence, int attackCooldownTicks,
                int remainingAttackCooldownTicks, VehicleType type, bool isAerial, bool isSelected, int[] groups)
                : base(id, x, y, radius) {
            this.playerId = playerId;
            this.durability = durability;
            this.maxDurability = maxDurability;
            this.maxSpeed = maxSpeed;
            this.visionRange = visionRange;
            this.squaredVisionRange = squaredVisionRange;
            this.groundAttackRange = groundAttackRange;
            this.squaredGroundAttackRange = squaredGroundAttackRange;
            this.aerialAttackRange = aerialAttackRange;
            this.squaredAerialAttackRange = squaredAerialAttackRange;
            this.groundDamage = groundDamage;
            this.aerialDamage = aerialDamage;
            this.groundDefence = groundDefence;
            this.aerialDefence = aerialDefence;
            this.attackCooldownTicks = attackCooldownTicks;
            this.remainingAttackCooldownTicks = remainingAttackCooldownTicks;
            this.type = type;
            this.isAerial = isAerial;
            this.isSelected = isSelected;

            this.groups = new int[groups.Length];
            Array.Copy(groups, this.groups, groups.Length);
        }

        public Vehicle(Vehicle vehicle, VehicleUpdate vehicleUpdate)
                : base(vehicle.Id, vehicleUpdate.X, vehicleUpdate.Y, vehicle.Radius) {
            this.playerId = vehicle.playerId;
            this.durability = vehicleUpdate.Durability;
            this.maxDurability = vehicle.maxDurability;
            this.maxSpeed = vehicle.maxSpeed;
            this.visionRange = vehicle.visionRange;
            this.squaredVisionRange = vehicle.squaredVisionRange;
            this.groundAttackRange = vehicle.groundAttackRange;
            this.squaredGroundAttackRange = vehicle.squaredGroundAttackRange;
            this.aerialAttackRange = vehicle.aerialAttackRange;
            this.squaredAerialAttackRange = vehicle.squaredAerialAttackRange;
            this.groundDamage = vehicle.groundDamage;
            this.aerialDamage = vehicle.aerialDamage;
            this.groundDefence = vehicle.groundDefence;
            this.aerialDefence = vehicle.aerialDefence;
            this.attackCooldownTicks = vehicle.attackCooldownTicks;
            this.remainingAttackCooldownTicks = vehicleUpdate.RemainingAttackCooldownTicks;
            this.type = vehicle.type;
            this.isAerial = vehicle.isAerial;
            this.isSelected = vehicleUpdate.IsSelected;

            int[] updateGroups = vehicleUpdate.Groups;
            this.groups = new int[updateGroups.Length];
            Array.Copy(updateGroups, this.groups, updateGroups.Length);
        }

        public long PlayerId => playerId;
        public int Durability => durability;
        public int MaxDurability => maxDurability;
        public double MaxSpeed => maxSpeed;
        public double VisionRange => visionRange;
        public double SquaredVisionRange => squaredVisionRange;
        public double GroundAttackRange => groundAttackRange;
        public double SquaredGroundAttackRange => squaredGroundAttackRange;
        public double AerialAttackRange => aerialAttackRange;
        public double SquaredAerialAttackRange => squaredAerialAttackRange;
        public int GroundDamage => groundDamage;
        public int AerialDamage => aerialDamage;
        public int GroundDefence => groundDefence;
        public int AerialDefence => aerialDefence;
        public int AttackCooldownTicks => attackCooldownTicks;
        public int RemainingAttackCooldownTicks => remainingAttackCooldownTicks;
        public VehicleType Type => type;
        public bool IsAerial => isAerial;
        public bool IsSelected => isSelected;

        public int[] Groups {
            get {
                if (this.groups == null) {
                    return null;
                }

                int[] groups = new int[this.groups.Length];
                Array.Copy(this.groups, groups, this.groups.Length);
                return groups;
            }
        }
    }
}