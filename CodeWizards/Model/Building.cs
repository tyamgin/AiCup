using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public class Building : LivingUnit {
        private readonly BuildingType type;
        private readonly double visionRange;
        private readonly double attackRange;
        private readonly int damage;
        private readonly int cooldownTicks;
        private readonly int remainingActionCooldownTicks;

        public Building(long id, double x, double y, double speedX, double speedY, double angle, Faction faction,
                double radius, int life, int maxLife, Status[] statuses, BuildingType type, double visionRange,
                double attackRange, int damage, int cooldownTicks, int remainingActionCooldownTicks)
                : base(id, x, y, speedX, speedY, angle, faction, radius, life, maxLife, statuses) {
            this.type = type;
            this.visionRange = visionRange;
            this.attackRange = attackRange;
            this.damage = damage;
            this.cooldownTicks = cooldownTicks;
            this.remainingActionCooldownTicks = remainingActionCooldownTicks;
        }

        public BuildingType Type => type;
        public double VisionRange => visionRange;
        public double AttackRange => attackRange;
        public int Damage => damage;
        public int CooldownTicks => cooldownTicks;
        public int RemainingActionCooldownTicks => remainingActionCooldownTicks;
    }
}