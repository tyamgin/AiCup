using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public class Bonus : CircularUnit {
        private readonly BonusType type;

        public Bonus(long id, double x, double y, double speedX, double speedY, double angle, Faction faction,
                double radius, BonusType type)
                : base(id, x, y, speedX, speedY, angle, faction, radius) {
            this.type = type;
        }

        public BonusType Type => type;
    }
}