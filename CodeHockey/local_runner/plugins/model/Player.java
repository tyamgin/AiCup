package model;

/**
 * Содержит данные о текущем состоянии игрока.
 */
public class Player {
    private final long id;
    private final boolean me;
    private final String name;
    private final int goalCount;
    private final boolean strategyCrashed;

    private final double netTop;
    private final double netLeft;
    private final double netBottom;
    private final double netRight;

    private final double netFront;
    private final double netBack;

    private final boolean justScoredGoal;
    private final boolean justMissedGoal;

    public Player(
            long id, boolean me, String name, int goalCount, boolean strategyCrashed,
            double netTop, double netLeft, double netBottom, double netRight,
            double netFront, double netBack, boolean justScoredGoal, boolean justMissedGoal) {
        this.id = id;
        this.me = me;
        this.name = name;
        this.goalCount = goalCount;
        this.strategyCrashed = strategyCrashed;
        this.netTop = netTop;
        this.netLeft = netLeft;
        this.netBottom = netBottom;
        this.netRight = netRight;
        this.netFront = netFront;
        this.netBack = netBack;
        this.justScoredGoal = justScoredGoal;
        this.justMissedGoal = justMissedGoal;
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
     * @return Возвращает имя игрока.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Возвращает количество шайб, заброшенных хоккеистами данного игрока в сетку противника.
     *         Шайбы, заброшенные во время состояния вне игры, не влияют на этот счётчик.
     */
    public int getGoalCount() {
        return goalCount;
    }

    /**
     * @return Возвращает специальный флаг --- показатель того, что стратегия игрока <<упала>>.
     *         Более подробную информацию можно найти в документации к игре.
     */
    public boolean isStrategyCrashed() {
        return strategyCrashed;
    }

    /**
     * @return Возвращает ординату верхней штанги ворот.
     */
    public double getNetTop() {
        return netTop;
    }

    /**
     * @return Возвращает абсциссу левой границы ворот.
     */
    public double getNetLeft() {
        return netLeft;
    }

    /**
     * @return Возвращает ординату нижней штанги ворот.
     */
    public double getNetBottom() {
        return netBottom;
    }

    /**
     * @return Возвращает абсциссу правой границы ворот.
     */
    public double getNetRight() {
        return netRight;
    }

    /**
     * @return Возвращает абсциссу ближайшей к вратарю вертикальной границы ворот.
     *         Соответствует одному из значений {@code netLeft} или {@code netRight}.
     */
    public double getNetFront() {
        return netFront;
    }

    /**
     * @return Возвращает абсциссу дальней от вратаря вертикальной границы ворот.
     *         Соответствует одному из значений {@code netLeft} или {@code netRight}.
     */
    public double getNetBack() {
        return netBack;
    }

    /**
     * @return Возвращает {@code true} в том и только в том случае, если игрок только что забил гол.
     *         <p/>
     *         Вместе с установленным флагом {@code justMissedGoal} другого игрока означает,
     *         что сейчас состояние вне игры и новые голы не будут засчитаны.
     *         Длительность состояния вне игры составляет {@code game.afterGoalStateTickCount} тиков.
     */
    public boolean isJustScoredGoal() {
        return justScoredGoal;
    }

    /**
     * @return Возвращает {@code true} в том и только в том случае, если игрок только что пропустил гол.
     *         <p/>
     *         Вместе с установленным флагом {@code justScoredGoal} другого игрока означает,
     *         что сейчас состояние вне игры и новые голы не будут засчитаны.
     *         Длительность состояния вне игры составляет {@code game.afterGoalStateTickCount} тиков.
     */
    public boolean isJustMissedGoal() {
        return justMissedGoal;
    }
}
