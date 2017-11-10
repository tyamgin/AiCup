package model;

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

    public long getId() {
        return id;
    }

    public boolean isMe() {
        return me;
    }

    public boolean isStrategyCrashed() {
        return strategyCrashed;
    }

    public int getScore() {
        return score;
    }

    public int getRemainingActionCooldownTicks() {
        return remainingActionCooldownTicks;
    }
}
