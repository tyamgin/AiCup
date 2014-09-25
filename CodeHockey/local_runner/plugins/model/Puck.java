package model;

/**
 * Класс, определяющий хоккейную шайбу. Содержит также все свойства юнита.
 */
public class Puck extends Unit {
    private final long ownerHockeyistId;
    private final long ownerPlayerId;

    public Puck(long id, double mass, double radius, double x, double y, double speedX, double speedY,
                long ownerHockeyistId, long ownerPlayerId) {
        super(id, mass, radius, x, y, speedX, speedY, 0.0D, 0.0D);
        this.ownerHockeyistId = ownerHockeyistId;
        this.ownerPlayerId = ownerPlayerId;
    }

    /**
     * @return Возвращает идентификатор хоккеиста, контролирующего шайбу, или {@code -1}.
     */
    public long getOwnerHockeyistId() {
        return ownerHockeyistId;
    }

    /**
     * @return Возвращает идентификатор игрока, контролирующего шайбу, или {@code -1}.
     */
    public long getOwnerPlayerId() {
        return ownerPlayerId;
    }
}
