using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public class Minion : LivingUnit {
        private readonly MinionType type;
        private readonly double visionRange;
        private readonly int damage;
        private readonly int cooldownTicks;
        private readonly int remainingActionCooldownTicks;

        public Minion(long id, double x, double y, double speedX, double speedY, double angle, Faction faction,
                double radius, int life, int maxLife, Status[] statuses, MinionType type, double visionRange,
                int damage, int cooldownTicks, int remainingActionCooldownTicks)
                : base(id, x, y, speedX, speedY, angle, faction, radius, life, maxLife, statuses) {
            this.type = type;
            this.visionRange = visionRange;
            this.damage = damage;
            this.cooldownTicks = cooldownTicks;
            this.remainingActionCooldownTicks = remainingActionCooldownTicks;
        }

        public MinionType Type => type;
        public double VisionRange => visionRange;
        public int Damage => damage;
        public int CooldownTicks => cooldownTicks;
        public int RemainingActionCooldownTicks => remainingActionCooldownTicks;
    }
}