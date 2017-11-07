package model;

/**
 * Содержит данные о текущем состоянии игрока.
 */
public class Player {
    private final long id;
    private final boolean me;
    private final boolean strategyCrashed;
    private final int score;
    private final int remainingActionCooldownTicks;

    public Player(long id, boolean me, boolean strategyCrashed, int score, int remainingActionCooldownTicks) {
        this.id = id;
        this.me = me;
        this.strategyCrashed = strategyCrashed;
        this.score = score;
        this.remainingActionCooldownTicks = remainingActionCooldownTicks;
    }

    /**
     * @return Возвращает уникальный идентификатор игрока.
     */
    public long getId() {
        return id;
    }

    /**
     * @return Возвращает {@code true} в том и только в том случае, если этот игрок ваш.
     */
    public boolean isMe() {
        return me;
    }

    /**
     * @return Возвращает специальный флаг --- показатель того, что стратегия игрока <<упала>>.
     * Более подробную информацию можно найти в документации к игре.
     */
    public boolean isStrategyCrashed() {
        return strategyCrashed;
    }

    /**
     * @return Возвращает количество баллов, набранное игроком.
     */
    public int getScore() {
        return score;
    }

    /**
     * @return Возвращает количество тиков, оставшееся до любого следующего действия.
     * Если значение равно {@code 0}, игрок может совершить действие в данный тик.
     */
    public int getRemainingActionCooldownTicks() {
        return remainingActionCooldownTicks;
    }
}
