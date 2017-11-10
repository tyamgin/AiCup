package model;

import java.util.Arrays;

public class Vehicle extends CircularUnit {
    private final long playerId;
    private final int durability;
    private final int maxDurability;
    private final double maxSpeed;
    private final double visionRange;
    private final double squaredVisionRange;
    private final double groundAttackRange;
    private final double squaredGroundAttackRange;
    private final double aerialAttackRange;
    private final double squaredAerialAttackRange;
    private final int groundDamage;
    private final int aerialDamage;
    private final int groundDefence;
    private final int aerialDefence;
    private final int attackCooldownTicks;
    private final int remainingAttackCooldownTicks;
    private final VehicleType type;
    private final boolean aerial;
    private final boolean selected;
    private final int[] groups;

    public Vehicle(
            long id, double x, double y, double radius, long playerId, int durability, int maxDurability,
            double maxSpeed, double visionRange, double squaredVisionRange, double groundAttackRange,
            double squaredGroundAttackRange, double aerialAttackRange, double squaredAerialAttackRange,
            int groundDamage, int aerialDamage, int groundDefence, int aerialDefence, int attackCooldownTicks,
            int remainingAttackCooldownTicks, VehicleType type, boolean aerial, boolean selected, int[] groups) {
        super(id, x, y, radius);

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
        this.aerial = aerial;
        this.selected = selected;
        this.groups = Arrays.copyOf(groups, groups.length);
    }

    public long getPlayerId() {
        return playerId;
    }

    public int getDurability() {
        return durability;
    }

    public int getMaxDurability() {
        return maxDurability;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getVisionRange() {
        return visionRange;
    }

    public double getSquaredVisionRange() {
        return squaredVisionRange;
    }

    public double getGroundAttackRange() {
        return groundAttackRange;
    }

    public double getSquaredGroundAttackRange() {
        return squaredGroundAttackRange;
    }

    public double getAerialAttackRange() {
        return aerialAttackRange;
    }

    public double getSquaredAerialAttackRange() {
        return squaredAerialAttackRange;
    }

    public int getGroundDamage() {
        return groundDamage;
    }

    public int getAerialDamage() {
        return aerialDamage;
    }

    public int getGroundDefence() {
        return groundDefence;
    }

    public int getAerialDefence() {
        return aerialDefence;
    }

    public int getAttackCooldownTicks() {
        return attackCooldownTicks;
    }

    public int getRemainingAttackCooldownTicks() {
        return remainingAttackCooldownTicks;
    }

    public VehicleType getType() {
        return type;
    }

    public boolean isAerial() {
        return aerial;
    }

    public boolean isSelected() {
        return selected;
    }

    public int[] getGroups() {
        return Arrays.copyOf(groups, groups.length);
    }
}
