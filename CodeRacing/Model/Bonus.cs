using System;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model {
    public class Bonus : RectangularUnit {
        private readonly BonusType type;

        public Bonus(long id, double mass, double x, double y, double speedX, double speedY, double angle,
                double angularSpeed, double width, double height, BonusType type)
                : base(id, mass, x, y, speedX, speedY, angle, angularSpeed, width, height) {
            this.type = type;
        }

        public BonusType Type {
            get { return type; }
        }
    }
}