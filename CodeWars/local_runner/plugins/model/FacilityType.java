package model;

/**
 * Тип сооружения.
 */
public enum FacilityType {
    /**
     * Центр управления. Увеличивает возможное количество действий игрока на
     * {@code game.additionalActionCountPerControlCenter} за {@code game.actionDetectionInterval} игровых тиков.
     */
    CONTROL_CENTER,

    /**
     * Завод. Может производить технику любого типа по выбору игрока.
     */
    VEHICLE_FACTORY
}
