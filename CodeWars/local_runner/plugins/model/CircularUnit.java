package model;

/**
 * Базовый класс для определения круглых объектов. Содержит также все свойства юнита.
 */
@SuppressWarnings({"AbstractClassWithoutAbstractMethods", "AbstractClassNeverImplemented"})
public abstract class CircularUnit extends Unit {
    private final double radius;

    protected CircularUnit(long id, double x, double y, double radius) {
        super(id, x, y);

        this.radius = radius;
    }

    /**
     * @return Возвращает радиус объекта.
     */
    public double getRadius() {
        return radius;
    }
}
