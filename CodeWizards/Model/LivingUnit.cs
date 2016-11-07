using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public abstract class LivingUnit : CircularUnit {
        private readonly int life;
        private readonly int maxLife;
        private readonly Status[] statuses;

        protected LivingUnit(long id, double x, double y, double speedX, double speedY, double angle, Faction faction,
                double radius, int life, int maxLife, Status[] statuses)
                : base(id, x, y, speedX, speedY, angle, faction, radius) {
            this.life = life;
            this.maxLife = maxLife;

            this.statuses = new Status[statuses.Length];
            Array.Copy(statuses, this.statuses, statuses.Length);
        }

        public int Life => life;
        public int MaxLife => maxLife;

        public Status[] Statuses {
            get {
                if (this.statuses == null) {
                    return null;
                }

                Status[] statuses = new Status[this.statuses.Length];
                Array.Copy(this.statuses, statuses, this.statuses.Length);
                return statuses;
            }
        }
    }
}