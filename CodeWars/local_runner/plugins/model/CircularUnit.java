package model;

@SuppressWarnings({"AbstractClassWithoutAbstractMethods", "AbstractClassNeverImplemented"})
public abstract class CircularUnit extends Unit {
    private final double radius;

    protected CircularUnit(long id, double x, double y, double radius) {
        super(id, x, y);

        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }
}
