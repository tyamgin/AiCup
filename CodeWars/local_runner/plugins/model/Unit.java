package model;

import static java.lang.StrictMath.hypot;

@SuppressWarnings({"AbstractClassWithoutAbstractMethods", "WeakerAccess"})
public abstract class Unit {
    private final long id;
    private final double x;
    private final double y;

    protected Unit(long id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public long getId() {
        return id;
    }

    public final double getX() {
        return x;
    }

    public final double getY() {
        return y;
    }

    public double getDistanceTo(double x, double y) {
        return hypot(x - this.x, y - this.y);
    }

    public double getDistanceTo(Unit unit) {
        return getDistanceTo(unit.x, unit.y);
    }

    public double getSquaredDistanceTo(double x, double y) {
        double dx = x - this.x;
        double dy = y - this.y;
        return dx * dx + dy * dy;
    }

    public double getSquaredDistanceTo(Unit unit) {
        return getSquaredDistanceTo(unit.x, unit.y);
    }
}
