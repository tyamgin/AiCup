using System;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model {
    public class Tree : LivingUnit {

        public Tree(long id, double x, double y, double speedX, double speedY, double angle, Faction faction,
                double radius, int life, int maxLife, Status[] statuses)
                : base(id, x, y, speedX, speedY, angle, faction, radius, life, maxLife, statuses) {
        }
    }
}