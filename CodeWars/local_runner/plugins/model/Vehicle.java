package model;

import java.util.Arrays;

/**
 * Класс, определяющий технику. Содержит также все свойства круглых объектов.
 */
public class Vehicle extends CircularUnit {
    private final long playerId;
    private final int durability;
    private final int maxDurability;
    private final double maxSpeed;
    private final double visionRange;
    private final double squaredVisionRange;
    private final double groundAttackRange;
    private final double squaredGroundAttackRange;
    private final double aerialAttackRange;
    private final double squaredAerialAttackRange;
    private final int groundDamage;
    private final int aerialDamage;
    private final int groundDefence;
    private final int aerialDefence;
    private final int attackCooldownTicks;
    private final int remainingAttackCooldownTicks;
    private final VehicleType type;
    private final boolean aerial;
    private final boolean selected;
    private final int[] groups;

    public Vehicle(
            long id, double x, double y, double radius, long playerId, int durability, int maxDurability,
            double maxSpeed, double visionRange, double squaredVisionRange, double groundAttackRange,
            double squaredGroundAttackRange, double aerialAttackRange, double squaredAerialAttackRange,
            int groundDamage, int aerialDamage, int groundDefence, int aerialDefence, int attackCooldownTicks,
            int remainingAttackCooldownTicks, VehicleType type, boolean aerial, boolean selected, int[] groups) {
        super(id, x, y, radius);

        this.playerId = playerId;
        this.durability = durability;
        this.maxDurability = maxDurability;
        this.maxSpeed = maxSpeed;
        this.visionRange = visionRange;
        this.squaredVisionRange = squaredVisionRange;
        this.groundAttackRange = groundAttackRange;
        this.squaredGroundAttackRange = squaredGroundAttackRange;
        this.aerialAttackRange = aerialAttackRange;
        this.squaredAerialAttackRange = squaredAerialAttackRange;
        this.groundDamage = groundDamage;
        this.aerialDamage = aerialDamage;
        this.groundDefence = groundDefence;
        this.aerialDefence = aerialDefence;
        this.attackCooldownTicks = attackCooldownTicks;
        this.remainingAttackCooldownTicks = remainingAttackCooldownTicks;
        this.type = type;
        this.aerial = aerial;
        this.selected = selected;
        this.groups = Arrays.copyOf(groups, groups.length);
    }

    public Vehicle(Vehicle vehicle, VehicleUpdate vehicleUpdate) {
        super(vehicle.getId(), vehicleUpdate.getX(), vehicleUpdate.getY(), vehicle.getRadius());

        this.playerId = vehicle.playerId;
        this.durability = vehicleUpdate.getDurability();
        this.maxDurability = vehicle.maxDurability;
        this.maxSpeed = vehicle.maxSpeed;
        this.visionRange = vehicle.visionRange;
        this.squaredVisionRange = vehicle.squaredVisionRange;
        this.groundAttackRange = vehicle.groundAttackRange;
        this.squaredGroundAttackRange = vehicle.squaredGroundAttackRange;
        this.aerialAttackRange = vehicle.aerialAttackRange;
        this.squaredAerialAttackRange = vehicle.squaredAerialAttackRange;
        this.groundDamage = vehicle.groundDamage;
        this.aerialDamage = vehicle.aerialDamage;
        this.groundDefence = vehicle.groundDefence;
        this.aerialDefence = vehicle.aerialDefence;
        this.attackCooldownTicks = vehicle.attackCooldownTicks;
        this.remainingAttackCooldownTicks = vehicleUpdate.getRemainingAttackCooldownTicks();
        this.type = vehicle.type;
        this.aerial = vehicle.aerial;
        this.selected = vehicleUpdate.isSelected();

        int[] updateGroups = vehicleUpdate.getGroups();
        this.groups = Arrays.copyOf(updateGroups, updateGroups.length);
    }

    /**
     * @return Возвращает идентификатор игрока, которому принадлежит техника.
     */
    public long getPlayerId() {
        return playerId;
    }

    /**
     * @return Возвращает текущую прочность.
     */
    public int getDurability() {
        return durability;
    }

    /**
     * @return Возвращает максимальную прочность.
     */
    public int getMaxDurability() {
        return maxDurability;
    }

    /**
     * @return Возвращает максимальное расстояние, на которое данная техника может переместиться за один игровой тик,
     * без учёта типа местности и погоды. При перемещении по дуге учитывается длина дуги,
     * а не кратчайшее расстояние между начальной и конечной точками.
     */
    public double getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * @return Возвращает максимальное расстояние (от центра до центра),
     * на котором данная техника обнаруживает другие объекты, без учёта типа местности и погоды.
     */
    public double getVisionRange() {
        return visionRange;
    }

    /**
     * @return Возвращает квадрат максимального расстояния (от центра до центра),
     * на котором данная техника обнаруживает другие объекты, без учёта типа местности и погоды.
     */
    public double getSquaredVisionRange() {
        return squaredVisionRange;
    }

    /**
     * @return Возвращает максимальное расстояние (от центра до центра),
     * на котором данная техника может атаковать наземные объекты.
     */
    public double getGroundAttackRange() {
        return groundAttackRange;
    }

    /**
     * @return Возвращает квадрат максимального расстояния (от центра до центра),
     * на котором данная техника может атаковать наземные объекты.
     */
    public double getSquaredGroundAttackRange() {
        return squaredGroundAttackRange;
    }

    /**
     * @return Возвращает максимальное расстояние (от центра до центра),
     * на котором данная техника может атаковать воздушные объекты.
     */
    public double getAerialAttackRange() {
        return aerialAttackRange;
    }

    /**
     * @return Возвращает квадрат максимального расстояния (от центра до центра),
     * на котором данная техника может атаковать воздушные объекты.
     */
    public double getSquaredAerialAttackRange() {
        return squaredAerialAttackRange;
    }

    /**
     * @return Возвращает урон одной атаки по наземному объекту.
     */
    public int getGroundDamage() {
        return groundDamage;
    }

    /**
     * @return Возвращает урон одной атаки по воздушному объекту.
     */
    public int getAerialDamage() {
        return aerialDamage;
    }

    /**
     * @return Возвращает защиту от атак наземных юнитов.
     */
    public int getGroundDefence() {
        return groundDefence;
    }

    /**
     * @return Возвращает защиту от атак воздушых юнитов.
     */
    public int getAerialDefence() {
        return aerialDefence;
    }

    /**
     * @return Возвращает минимально возможный интервал между двумя последовательными атаками данной техники.
     */
    public int getAttackCooldownTicks() {
        return attackCooldownTicks;
    }

    /**
     * @return Возвращает количество тиков, оставшееся до следующей атаки.
     * Для совершения атаки необходимо, чтобы это значение было равно нулю.
     */
    public int getRemainingAttackCooldownTicks() {
        return remainingAttackCooldownTicks;
    }

    /**
     * @return Возвращает тип техники.
     */
    public VehicleType getType() {
        return type;
    }

    /**
     * @return Возвращает {@code true} в том и только том случае, если эта техника воздушная.
     */
    public boolean isAerial() {
        return aerial;
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
