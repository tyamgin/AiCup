package model;

/**
 * Класс, определяющий хоккеиста. Содержит также все свойства юнита.
 */
public class Hockeyist extends Unit {
    private final long playerId;
    private final int teammateIndex;
    private final boolean teammate;
    private final HockeyistType type;
    private final int strength;
    private final int endurance;
    private final int dexterity;
    private final int agility;
    private final double stamina;
    private final HockeyistState state;
    private final int originalPositionIndex;
    private final int remainingKnockdownTicks;
    private final int remainingCooldownTicks;
    private final int swingTicks;
    private final ActionType lastAction;
    private final Integer lastActionTick;

    public Hockeyist(
            long id, long playerId, int teammateIndex,
            double mass, double radius, double x, double y, double speedX, double speedY,
            double angle, double angularSpeed, boolean teammate, HockeyistType type,
            int strength, int endurance, int dexterity, int agility, double stamina,
            HockeyistState state, int originalPositionIndex,
            int remainingKnockdownTicks, int remainingCooldownTicks, int swingTicks,
            ActionType lastAction, Integer lastActionTick) {
        super(id, mass, radius, x, y, speedX, speedY, angle, angularSpeed);

        this.playerId = playerId;
        this.teammateIndex = teammateIndex;
        this.teammate = teammate;
        this.type = type;
        this.strength = strength;
        this.endurance = endurance;
        this.dexterity = dexterity;
        this.agility = agility;
        this.stamina = stamina;
        this.state = state;
        this.originalPositionIndex = originalPositionIndex;
        this.remainingKnockdownTicks = remainingKnockdownTicks;
        this.remainingCooldownTicks = remainingCooldownTicks;
        this.swingTicks = swingTicks;
        this.lastAction = lastAction;
        this.lastActionTick = lastActionTick;
    }

    /**
     * @return Возвращает идентификатор игрока, в команду которого входит хоккеист.
     */
    public long getPlayerId() {
        return playerId;
    }

    /**
     * @return Возвращает 0-индексированный номер хоккеиста в команде.
     */
    public int getTeammateIndex() {
        return teammateIndex;
    }

    /**
     * @return Возвращает {@code true}, если и только если данный хоккеист входит в команду вашего игрока.
     */
    public boolean isTeammate() {
        return teammate;
    }

    /**
     * @return Возвращает тип хоккеиста.
     */
    public HockeyistType getType() {
        return type;
    }

    /**
     * @return Возвращает значение атрибута сила.
     */
    public int getStrength() {
        return strength;
    }

    /**
     * @return Возвращает значение атрибута стойкость.
     */
    public int getEndurance() {
        return endurance;
    }

    /**
     * @return Возвращает значение атрибута ловкость.
     */
    public int getDexterity() {
        return dexterity;
    }

    /**
     * @return Возвращает значение атрибута подвижность.
     */
    public int getAgility() {
        return agility;
    }

    /**
     * @return Возвращает текущее значение выносливости.
     */
    public double getStamina() {
        return stamina;
    }

    /**
     * @return Возвращает состояние хоккеиста.
     */
    public HockeyistState getState() {
        return state;
    }

    /**
     * @return Возвращает индекс исходной позиции хоккеиста или {@code -1} для вратаря или хоккеиста,
     *         отдыхающего за пределами игрового поля. На эту позицию хоккеист будет помещён при разыгрывании шайбы.
     *         При выполнении действия замена {@code ActionType.SUBSTITUTE} индексы исходных позиций хоккеистов,
     *         участвующих в замене, меняются местами.
     */
    public int getOriginalPositionIndex() {
        return originalPositionIndex;
    }

    /**
     * @return Возвращает количество тиков, по прошествии которого хоккеист восстановится после падения,
     *         или {@code 0}, если хоккеист не сбит с ног.
     */
    public int getRemainingKnockdownTicks() {
        return remainingKnockdownTicks;
    }

    /**
     * @return Возвращает количество тиков, по прошествии которого хоккеист сможет совершить какое-либо
     *         действие ({@code move.action}), или {@code 0}, если хоккеист может совершить действие в данный тик.
     */
    public int getRemainingCooldownTicks() {
        return remainingCooldownTicks;
    }

    /**
     * @return Для хоккеиста, находящегося в состоянии замаха ({@code HockeyistState.SWINGING}),
     *         возвращает количество тиков, прошедших от начала замаха. В противном случае возвращает {@code 0}.
     */
    public int getSwingTicks() {
        return swingTicks;
    }

    /**
     * @return Возвращает последнее действие ({@code move.action}), совершённое хоккеистом, или {@code null}
     *         ({@code UNKNOWN_ACTION} в пакетах некоторых языков) в случае, если хоккеист ещё не совершил
     *         ни одного действия.
     */
    public ActionType getLastAction() {
        return lastAction;
    }

    /**
     * @return Возвращает номер тика, в который хоккеист совершил своё последние действие ({@code move.action}),
     *         или {@code null} ({@code -1} в пакетах некоторых языков) в случае, если хоккеист ещё не совершил
     *         ни одного действия.
     */
    public Integer getLastActionTick() {
        return lastActionTick;
    }
}
