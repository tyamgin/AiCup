package model;

import java.util.Arrays;

/**
 * Класс, частично определяющий технику. Содержит уникальный идентификатор техники, а также все поля техники,
 * значения которых могут изменяться в процессе игры.
 */
public class VehicleUpdate {
    private final long id;
    private final double x;
    private final double y;
    private final int durability;
    private final int remainingAttackCooldownTicks;
    private final boolean selected;
    private final int[] groups;

    public VehicleUpdate(
            long id, double x, double y, int durability, int remainingAttackCooldownTicks, boolean selected,
            int[] groups) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.durability = durability;
        this.remainingAttackCooldownTicks = remainingAttackCooldownTicks;
        this.selected = selected;
        this.groups = Arrays.copyOf(groups, groups.length);
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
    public double getX() {
        return x;
    }

    /**
     * @return Возвращает Y-координату центра объекта. Ось ординат направлена сверху вниз.
     */
    public double getY() {
        return y;
    }

    /**
     * @return Возвращает текущую прочность или {@code 0}, если техника была уничтожена либо ушла из зоны видимости.
     */
    public int getDurability() {
        return durability;
    }

    /**
     * @return Возвращает количество тиков, оставшееся до следующей атаки.
     * Для совершения атаки необходимо, чтобы это значение было равно нулю.
     */
    public int getRemainingAttackCooldownTicks() {
        return remainingAttackCooldownTicks;
    }

    /**
     * @return Возвращает {@code true} в том и только том случае, если эта техника выделена.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @return Возвращает группы, в которые входит эта техника.
     */
    public int[] getGroups() {
        return Arrays.copyOf(groups, groups.length);
    }
}
