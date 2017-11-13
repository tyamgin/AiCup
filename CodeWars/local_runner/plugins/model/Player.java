package model;

public class Player {
    private final long id;
    private final boolean me;
    private final boolean strategyCrashed;
    private final int score;
    private final int remainingActionCooldownTicks;
    private final int remainingNuclearStrikeCooldownTicks;
    private final long nextNuclearStrikeVehicleId;
    private final int nextNuclearStrikeTickIndex;
    private final double nextNuclearStrikeX;
    private final double nextNuclearStrikeY;

    public Player(
            long id, boolean me, boolean strategyCrashed, int score, int remainingActionCooldownTicks,
            int remainingNuclearStrikeCooldownTicks, long nextNuclearStrikeVehicleId, int nextNuclearStrikeTickIndex,
            double nextNuclearStrikeX, double nextNuclearStrikeY) {
        this.id = id;
        this.me = me;
        this.strategyCrashed = strategyCrashed;
        this.score = score;
        this.remainingActionCooldownTicks = remainingActionCooldownTicks;
        this.remainingNuclearStrikeCooldownTicks = remainingNuclearStrikeCooldownTicks;
        this.nextNuclearStrikeVehicleId = nextNuclearStrikeVehicleId;
        this.nextNuclearStrikeTickIndex = nextNuclearStrikeTickIndex;
        this.nextNuclearStrikeX = nextNuclearStrikeX;
        this.nextNuclearStrikeY = nextNuclearStrikeY;
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

    public int getRemainingNuclearStrikeCooldownTicks() {
        return remainingNuclearStrikeCooldownTicks;
    }

    public long getNextNuclearStrikeVehicleId() {
        return nextNuclearStrikeVehicleId;
    }

    public int getNextNuclearStrikeTickIndex() {
        return nextNuclearStrikeTickIndex;
    }

    public double getNextNuclearStrikeX() {
        return nextNuclearStrikeX;
    }

    public double getNextNuclearStrikeY() {
        return nextNuclearStrikeY;
    }
}
