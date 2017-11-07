package model;

import static java.lang.StrictMath.hypot;

/**
 * Базовый класс для определения объектов (<<юнитов>>) на игровом поле.
 */
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

    /**
     * @return Возвращает уникальный идентификатор объекта.
     */
    public long getId() {
        return id;
    }

    /**
     * @return Возвращает X-координату центра объекта. Ось абсцисс направлена слева направо.
     */
    public final double getX() {
        return x;
    }

    /**
     * @return Возвращает Y-координату центра объекта. Ось ординат направлена сверху вниз.
     */
    public final double getY() {
        return y;
    }

    /**
     * @param x X-координата точки.
     * @param y Y-координата точки.
     * @return Возвращает расстояние до точки от центра данного объекта.
     */
    public double getDistanceTo(double x, double y) {
        return hypot(x - this.x, y - this.y);
    }

    /**
     * @param unit Объект, до центра которого необходимо определить расстояние.
     * @return Возвращает расстояние от центра данного объекта до центра указанного объекта.
     */
    public double getDistanceTo(Unit unit) {
        return getDistanceTo(unit.x, unit.y);
    }

    /**
     * @param x X-координата точки.
     * @param y Y-координата точки.
     * @return Возвращает квадрат расстояния до точки от центра данного объекта.
     */
    public double getSquaredDistanceTo(double x, double y) {
        double dx = x - this.x;
        double dy = y - this.y;
        return dx * dx + dy * dy;
    }

    /**
     * @param unit Объект, до центра которого необходимо определить квадрат расстояния.
     * @return Возвращает квадрат расстояния от центра данного объекта до центра указанного объекта.
     */
    public double getSquaredDistanceTo(Unit unit) {
        return getSquaredDistanceTo(unit.x, unit.y);
    }
}
