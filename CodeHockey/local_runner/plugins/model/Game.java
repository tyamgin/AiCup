package model;

/**
 * Предоставляет доступ к различным игровым константам.
 */
public class Game {
    private final long randomSeed;

    private final int tickCount;

    private final double worldWidth;
    private final double worldHeight;

    private final double goalNetTop;
    private final double goalNetWidth;
    private final double goalNetHeight;

    private final double rinkTop;
    private final double rinkLeft;
    private final double rinkBottom;
    private final double rinkRight;

    private final int afterGoalStateTickCount;
    private final int overtimeTickCount;

    private final int defaultActionCooldownTicks;
    private final int swingActionCooldownTicks;
    private final int cancelStrikeActionCooldownTicks;
    private final int actionCooldownTicksAfterLosingPuck;

    private final double stickLength;
    private final double stickSector;
    private final double passSector;

    private final int hockeyistAttributeBaseValue;

    private final double minActionChance;
    private final double maxActionChance;

    private final double strikeAngleDeviation;
    private final double passAngleDeviation;

    private final double pickUpPuckBaseChance;
    private final double takePuckAwayBaseChance;

    private final int maxEffectiveSwingTicks;
    private final double strikePowerBaseFactor;
    private final double strikePowerGrowthFactor;

    private final double strikePuckBaseChance;

    private final double knockdownChanceFactor;
    private final double knockdownTicksFactor;

    private final double maxSpeedToAllowSubstitute;
    private final double substitutionAreaHeight;

    private final double passPowerFactor;

    private final double hockeyistMaxStamina;
    private final double activeHockeyistStaminaGrowthPerTick;
    private final double restingHockeyistStaminaGrowthPerTick;
    private final double zeroStaminaHockeyistEffectivenessFactor;
    private final double speedUpStaminaCostFactor;
    private final double turnStaminaCostFactor;
    private final double takePuckStaminaCost;
    private final double swingStaminaCost;
    private final double strikeStaminaBaseCost;
    private final double strikeStaminaCostGrowthFactor;
    private final double cancelStrikeStaminaCost;
    private final double passStaminaCost;

    private final double goalieMaxSpeed;
    private final double hockeyistMaxSpeed;

    private final double struckHockeyistInitialSpeedFactor;

    private final double hockeyistSpeedUpFactor;
    private final double hockeyistSpeedDownFactor;
    private final double hockeyistTurnAngleFactor;

    private final int versatileHockeyistStrength;
    private final int versatileHockeyistEndurance;
    private final int versatileHockeyistDexterity;
    private final int versatileHockeyistAgility;

    private final int forwardHockeyistStrength;
    private final int forwardHockeyistEndurance;
    private final int forwardHockeyistDexterity;
    private final int forwardHockeyistAgility;

    private final int defencemanHockeyistStrength;
    private final int defencemanHockeyistEndurance;
    private final int defencemanHockeyistDexterity;
    private final int defencemanHockeyistAgility;

    private final int minRandomHockeyistParameter;
    private final int maxRandomHockeyistParameter;

    private final double struckPuckInitialSpeedFactor;

    private final double puckBindingRange;

    public Game(
            long randomSeed, int tickCount, double worldWidth, double worldHeight, double goalNetTop,
            double goalNetWidth, double goalNetHeight, double rinkTop, double rinkLeft, double rinkBottom,
            double rinkRight, int afterGoalStateTickCount, int overtimeTickCount, int defaultActionCooldownTicks,
            int swingActionCooldownTicks, int cancelStrikeActionCooldownTicks, int actionCooldownTicksAfterLosingPuck,
            double stickLength, double stickSector, double passSector, int hockeyistAttributeBaseValue,
            double minActionChance, double maxActionChance, double strikeAngleDeviation, double passAngleDeviation,
            double pickUpPuckBaseChance, double takePuckAwayBaseChance, int maxEffectiveSwingTicks,
            double strikePowerBaseFactor, double strikePowerGrowthFactor, double strikePuckBaseChance,
            double knockdownChanceFactor, double knockdownTicksFactor, double maxSpeedToAllowSubstitute,
            double substitutionAreaHeight, double passPowerFactor, double hockeyistMaxStamina,
            double activeHockeyistStaminaGrowthPerTick, double restingHockeyistStaminaGrowthPerTick,
            double zeroStaminaHockeyistEffectivenessFactor, double speedUpStaminaCostFactor,
            double turnStaminaCostFactor, double takePuckStaminaCost, double swingStaminaCost,
            double strikeStaminaBaseCost, double strikeStaminaCostGrowthFactor, double cancelStrikeStaminaCost,
            double passStaminaCost, double goalieMaxSpeed, double hockeyistMaxSpeed,
            double struckHockeyistInitialSpeedFactor, double hockeyistSpeedUpFactor, double hockeyistSpeedDownFactor,
            double hockeyistTurnAngleFactor, int versatileHockeyistStrength, int versatileHockeyistEndurance,
            int versatileHockeyistDexterity, int versatileHockeyistAgility, int forwardHockeyistStrength,
            int forwardHockeyistEndurance, int forwardHockeyistDexterity, int forwardHockeyistAgility,
            int defencemanHockeyistStrength, int defencemanHockeyistEndurance, int defencemanHockeyistDexterity,
            int defencemanHockeyistAgility, int minRandomHockeyistParameter, int maxRandomHockeyistParameter,
            double struckPuckInitialSpeedFactor, double puckBindingRange) {
        this.randomSeed = randomSeed;
        this.tickCount = tickCount;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.goalNetTop = goalNetTop;
        this.goalNetWidth = goalNetWidth;
        this.goalNetHeight = goalNetHeight;
        this.rinkTop = rinkTop;
        this.rinkLeft = rinkLeft;
        this.rinkBottom = rinkBottom;
        this.rinkRight = rinkRight;
        this.afterGoalStateTickCount = afterGoalStateTickCount;
        this.overtimeTickCount = overtimeTickCount;
        this.defaultActionCooldownTicks = defaultActionCooldownTicks;
        this.swingActionCooldownTicks = swingActionCooldownTicks;
        this.cancelStrikeActionCooldownTicks = cancelStrikeActionCooldownTicks;
        this.actionCooldownTicksAfterLosingPuck = actionCooldownTicksAfterLosingPuck;
        this.stickLength = stickLength;
        this.stickSector = stickSector;
        this.passSector = passSector;
        this.hockeyistAttributeBaseValue = hockeyistAttributeBaseValue;
        this.minActionChance = minActionChance;
        this.maxActionChance = maxActionChance;
        this.strikeAngleDeviation = strikeAngleDeviation;
        this.passAngleDeviation = passAngleDeviation;
        this.pickUpPuckBaseChance = pickUpPuckBaseChance;
        this.takePuckAwayBaseChance = takePuckAwayBaseChance;
        this.maxEffectiveSwingTicks = maxEffectiveSwingTicks;
        this.strikePowerBaseFactor = strikePowerBaseFactor;
        this.strikePowerGrowthFactor = strikePowerGrowthFactor;
        this.strikePuckBaseChance = strikePuckBaseChance;
        this.knockdownChanceFactor = knockdownChanceFactor;
        this.knockdownTicksFactor = knockdownTicksFactor;
        this.maxSpeedToAllowSubstitute = maxSpeedToAllowSubstitute;
        this.substitutionAreaHeight = substitutionAreaHeight;
        this.passPowerFactor = passPowerFactor;
        this.hockeyistMaxStamina = hockeyistMaxStamina;
        this.activeHockeyistStaminaGrowthPerTick = activeHockeyistStaminaGrowthPerTick;
        this.restingHockeyistStaminaGrowthPerTick = restingHockeyistStaminaGrowthPerTick;
        this.zeroStaminaHockeyistEffectivenessFactor = zeroStaminaHockeyistEffectivenessFactor;
        this.speedUpStaminaCostFactor = speedUpStaminaCostFactor;
        this.turnStaminaCostFactor = turnStaminaCostFactor;
        this.takePuckStaminaCost = takePuckStaminaCost;
        this.swingStaminaCost = swingStaminaCost;
        this.strikeStaminaBaseCost = strikeStaminaBaseCost;
        this.strikeStaminaCostGrowthFactor = strikeStaminaCostGrowthFactor;
        this.cancelStrikeStaminaCost = cancelStrikeStaminaCost;
        this.passStaminaCost = passStaminaCost;
        this.goalieMaxSpeed = goalieMaxSpeed;
        this.hockeyistMaxSpeed = hockeyistMaxSpeed;
        this.struckHockeyistInitialSpeedFactor = struckHockeyistInitialSpeedFactor;
        this.hockeyistSpeedUpFactor = hockeyistSpeedUpFactor;
        this.hockeyistSpeedDownFactor = hockeyistSpeedDownFactor;
        this.hockeyistTurnAngleFactor = hockeyistTurnAngleFactor;
        this.versatileHockeyistStrength = versatileHockeyistStrength;
        this.versatileHockeyistEndurance = versatileHockeyistEndurance;
        this.versatileHockeyistDexterity = versatileHockeyistDexterity;
        this.versatileHockeyistAgility = versatileHockeyistAgility;
        this.forwardHockeyistStrength = forwardHockeyistStrength;
        this.forwardHockeyistEndurance = forwardHockeyistEndurance;
        this.forwardHockeyistDexterity = forwardHockeyistDexterity;
        this.forwardHockeyistAgility = forwardHockeyistAgility;
        this.defencemanHockeyistStrength = defencemanHockeyistStrength;
        this.defencemanHockeyistEndurance = defencemanHockeyistEndurance;
        this.defencemanHockeyistDexterity = defencemanHockeyistDexterity;
        this.defencemanHockeyistAgility = defencemanHockeyistAgility;
        this.minRandomHockeyistParameter = minRandomHockeyistParameter;
        this.maxRandomHockeyistParameter = maxRandomHockeyistParameter;
        this.struckPuckInitialSpeedFactor = struckPuckInitialSpeedFactor;
        this.puckBindingRange = puckBindingRange;
    }

    /**
     * @return Возвращает некоторое число, которое ваша стратегия может использовать для инициализации генератора
     *         случайных чисел. Данное значение имеет рекомендательный характер, однако позволит более точно
     *         воспроизводить прошедшие игры.
     */
    public long getRandomSeed() {
        return randomSeed;
    }

    /**
     * @return Возвращает длительность игры в тиках.
     */
    public int getTickCount() {
        return tickCount;
    }

    /**
     * @return Возвращает ширину игрового мира.
     */
    public double getWorldWidth() {
        return worldWidth;
    }

    /**
     * @return Возвращает высоту игрового мира.
     */
    public double getWorldHeight() {
        return worldHeight;
    }

    /**
     * @return Возвращает ординату верхней штанги ворот.
     */
    public double getGoalNetTop() {
        return goalNetTop;
    }

    /**
     * @return Возвращает ширину ворот.
     */
    public double getGoalNetWidth() {
        return goalNetWidth;
    }

    /**
     * @return Возвращает высоту ворот.
     */
    public double getGoalNetHeight() {
        return goalNetHeight;
    }

    /**
     * @return Возвращает ординату верхней границы игрового поля.
     */
    public double getRinkTop() {
        return rinkTop;
    }

    /**
     * @return Возвращает абсциссу левой границы игрового поля.
     */
    public double getRinkLeft() {
        return rinkLeft;
    }

    /**
     * @return Возвращает ординату нижней границы игрового поля.
     */
    public double getRinkBottom() {
        return rinkBottom;
    }

    /**
     * @return Возвращает абсциссу правой границы игрового поля.
     */
    public double getRinkRight() {
        return rinkRight;
    }

    /**
     * @return Возвращает длительность состояния вне игры после гола.
     *         В течение этого времени новые забитые голы игнорируются,
     *         а действия не требуют затрат выносливости.
     */
    public int getAfterGoalStateTickCount() {
        return afterGoalStateTickCount;
    }

    /**
     * @return Возвращает длительность дополнительного времени.
     *         Дополнительное время наступает в случае ничейного счёта на момент окончания основного времени.
     *         Если за основное время не было забито ни одного гола, вратари обоих игроков убираются с поля.
     */
    public int getOvertimeTickCount() {
        return overtimeTickCount;
    }

    /**
     * @return Возвращает длительность задержки, применяемой к хоккеисту
     *         после совершения им большинства действий ({@code move.action}).
     *         В течение этого времени хоккеист не может совершать новые действия.
     */
    public int getDefaultActionCooldownTicks() {
        return defaultActionCooldownTicks;
    }

    /**
     * @return Возвращает длительность задержки, применяемой к хоккеисту
     *         после совершения им действия замах ({@code ActionType.SWING}).
     *         В течение этого времени хоккеист не может совершать новые действия.
     */
    public int getSwingActionCooldownTicks() {
        return swingActionCooldownTicks;
    }

    /**
     * @return Возвращает длительность задержки, применяемой к хоккеисту
     *         после отмены им удара ({@code ActionType.CANCEL_STRIKE}).
     *         В течение этого времени хоккеист не может совершать новые действия.
     */
    public int getCancelStrikeActionCooldownTicks() {
        return cancelStrikeActionCooldownTicks;
    }

    /**
     * @return Возвращает длительность задержки, применяемой к хоккеисту
     *         в случае потери шайбы вследствие воздействия других хоккеистов.
     *         В течение этого времени хоккеист не может совершать действия.
     */
    public int getActionCooldownTicksAfterLosingPuck() {
        return actionCooldownTicksAfterLosingPuck;
    }

    /**
     * @return Возвращает длину клюшки хоккеиста. Хоккеист может воздействовать на игровой объект,
     *         если и только если расстояние от центра хоккеиста до центра объекта не превышает эту величину.
     */
    public double getStickLength() {
        return stickLength;
    }

    /**
     * @return Возвращает сектор клюшки хоккеиста. Хоккеист может воздействовать на игровой объект,
     *         если и только если угол между направлением хоккеиста и вектором из центра хоккеиста в центр объекта
     *         не превышает половину этой величины.
     */
    public double getStickSector() {
        return stickSector;
    }

    /**
     * @return Возвращает сектор, ограничивающий направление паса.
     */
    public double getPassSector() {
        return passSector;
    }

    /**
     * @return Возвращает базовое значение атрибута хоккеиста.
     *         Данная величина используется как коэффициент в различных игровых формулах.
     */
    public int getHockeyistAttributeBaseValue() {
        return hockeyistAttributeBaseValue;
    }

    /**
     * @return Возвращает минимальный шанс на совершение любого вероятностного действия.
     */
    public double getMinActionChance() {
        return minActionChance;
    }

    /**
     * @return Возвращает максимальный шанс на совершение любого вероятностного действия.
     */
    public double getMaxActionChance() {
        return maxActionChance;
    }

    /**
     * @return Возвращает стандартное отклонение распределения Гаусса для угла удара ({@code ActionType.STRIKE})
     *         хоккеиста при базовом значении атрибута ловкость. Чем выше ловкость конкретного хоккеиста,
     *         тем точнее его удар.
     */
    public double getStrikeAngleDeviation() {
        return strikeAngleDeviation;
    }

    /**
     * @return Возвращает стандартное отклонение распределения Гаусса для угла паса ({@code ActionType.PASS})
     *         хоккеиста при базовом значении атрибута ловкость. Чем выше ловкость конкретного хоккеиста,
     *         тем точнее его пас.
     */
    public double getPassAngleDeviation() {
        return passAngleDeviation;
    }

    /**
     * @return Возвращает шанс подобрать шайбу, не контролируемую другим хоккеистом, без учёта влияния атрибутов
     *         хоккеиста и скорости шайбы. Равен шансу подобрать шайбу в случае, когда влияющие на действие атрибуты
     *         хоккеиста равны {@code hockeyistAttributeBaseValue}, а шайба двигается со скоростью
     *         {@code struckPuckInitialSpeedFactor}.
     *         <p/>
     *         Максимальный из атрибутов ловкость и подвижность хоккеиста увеличивает шанс на захват.
     *         Скорость шайбы уменьшает шанс на захват.
     */
    public double getPickUpPuckBaseChance() {
        return pickUpPuckBaseChance;
    }

    /**
     * @return Возвращает базовый шанс отнять шайбу у другого хоккеиста.
     *         <p/>
     *         Максимальный из атрибутов сила и ловкость хоккеиста, отнимающего шайбу, увеличивает шанс на захват.
     *         Максимальный из атрибутов стойкость и подвижность текущего владельца шайбы уменьшает шанс на её потерю.
     */
    public double getTakePuckAwayBaseChance() {
        return takePuckAwayBaseChance;
    }

    /**
     * @return Возвращает длительность замаха, после достижения которой сила удара не увеличивается.
     */
    public int getMaxEffectiveSwingTicks() {
        return maxEffectiveSwingTicks;
    }

    /**
     * @return Возвращает коэффициент силы удара без замаха.
     */
    public double getStrikePowerBaseFactor() {
        return strikePowerBaseFactor;
    }

    /**
     * @return Возвращает увеличение коэффициента силы удара за каждый тик замаха.
     *         Максимальное количество учитываемых тиков ограничено значением {@code maxEffectiveSwingTicks}.
     */
    public double getStrikePowerGrowthFactor() {
        return strikePowerGrowthFactor;
    }

    /**
     * @return Возвращает базовый шанс ударить шайбу. Базовый шанс не зависит от того,
     *         контролирует шайбу другой хоккеист или нет, однако на результирующий шанс удара
     *         по свободной и контролируемой шайбе влияют разные атрибуты хоккеиста
     *         (смотрите документацию к {@code pickUpPuckBaseChance} и {@code takePuckAwayBaseChance}).
     *         Если хоккеист, совершающий удар, контролирует шайбу, то вероятность удара всегда будет 100%.
     */
    public double getStrikePuckBaseChance() {
        return strikePuckBaseChance;
    }

    /**
     * @return Возвращает шанс ударом ({@code ActionType.STRIKE}) сбить с ног другого хоккеиста при максимальной
     *         длительности замаха. Среднее значение атрибутов сила и ловкость хоккеиста, совершающего удар,
     *         увеличивает шанс сбить с ног. Значение атрибута стойкость атакуемого хоккеиста уменьшает шанс на падение.
     */
    public double getKnockdownChanceFactor() {
        return knockdownChanceFactor;
    }

    /**
     * @return Возвращает количество тиков, по прошествии которого хоккеист восстановится после падения при базовом
     *         значении атрибута подвижность. Чем выше подвижность, тем быстрее восстановление.
     */
    public double getKnockdownTicksFactor() {
        return knockdownTicksFactor;
    }

    /**
     * @return Возвращает максимальную допустимую скорость для выполнения замены хоккеиста.
     */
    public double getMaxSpeedToAllowSubstitute() {
        return maxSpeedToAllowSubstitute;
    }

    /**
     * @return Возвращает высоту зоны, в которой может быть выполнена замена хоккеиста. Зона расположена вдоль верхней
     *         границы игровой площадки. Замена может быть выполнена только на своей половине поля.
     */
    public double getSubstitutionAreaHeight() {
        return substitutionAreaHeight;
    }

    /**
     * @return Возвращает коэффициент силы паса. Умножается на устанавливаемое стратегией в интервале
     *         [{@code 0.0}, {@code 1.0}] значение силы паса ({@code move.passPower}).
     */
    public double getPassPowerFactor() {
        return passPowerFactor;
    }

    /**
     * @return Возвращает максимальное значение выносливости хоккеиста. Выносливость тратится на перемещение
     *         и совершение хоккеистом различных действий. Каждый тик может восстановиться небольшое количество
     *         выносливости в зависимости от состояния хоккеиста ({@code hockeyist.state}). По мере расходования
     *         выносливости все атрибуты (соответственно, и эффективность всех действий) хоккеиста равномерно
     *         уменьшаются и достигают значения {@code zeroStaminaHockeyistEffectivenessFactor} (от начальных
     *         показателей) при падении выносливости до нуля. Хоккеист не восстанавливает выносливость в состояниях
     *         {@code HockeyistState.SWINGING} и {@code HockeyistState.KNOCKED_DOWN}.
     */
    public double getHockeyistMaxStamina() {
        return hockeyistMaxStamina;
    }

    /**
     * @return Возвращает значение, на которое увеличивается выносливость хоккеиста за каждый тик в состоянии
     *         {@code HockeyistType.ACTIVE}.
     */
    public double getActiveHockeyistStaminaGrowthPerTick() {
        return activeHockeyistStaminaGrowthPerTick;
    }

    /**
     * @return Возвращает значение, на которое увеличивается выносливость хоккеиста за каждый тик в состоянии
     *         {@code HockeyistType.RESTING}.
     */
    public double getRestingHockeyistStaminaGrowthPerTick() {
        return restingHockeyistStaminaGrowthPerTick;
    }

    /**
     * @return Возвращает коэффициент эффективности действий хоккеиста при падении его выносливости до нуля.
     */
    public double getZeroStaminaHockeyistEffectivenessFactor() {
        return zeroStaminaHockeyistEffectivenessFactor;
    }

    /**
     * @return Возвращает количество выносливости, которое необходимо затратить на максимальное по модулю
     *         ускорение/замедление хоккеиста ({@code move.speedUp}) за 1 тик. Для меньших значений ускорения затраты
     *         выносливости пропорционально падают.
     */
    public double getSpeedUpStaminaCostFactor() {
        return speedUpStaminaCostFactor;
    }

    /**
     * @return Возвращает количество выносливости, которое необходимо затратить на максимальный по модулю
     *         угол поворота хоккеиста ({@code move.turn}) за 1 тик. Для меньших значений угла поворота затраты
     *         выносливости пропорционально падают.
     */
    public double getTurnStaminaCostFactor() {
        return turnStaminaCostFactor;
    }

    /**
     * @return Возвращает количество выносливости, которое необходимо затратить на совершение действия
     *         {@code ActionType.TAKE_PUCK}.
     */
    public double getTakePuckStaminaCost() {
        return takePuckStaminaCost;
    }

    /**
     * @return Возвращает количество выносливости, которое необходимо затратить на совершение действия
     *         {@code ActionType.SWING}.
     */
    public double getSwingStaminaCost() {
        return swingStaminaCost;
    }

    /**
     * @return Возвращает базовое количество выносливости, которое необходимо затратить на совершение действия
     *         {@code ActionType.STRIKE}.
     */
    public double getStrikeStaminaBaseCost() {
        return strikeStaminaBaseCost;
    }

    /**
     * @return Возвращает увеличение затрат выносливости на удар ({@code ActionType.STRIKE}) за каждый тик замаха.
     *         Максимальное количество учитываемых тиков ограничено значением {@code maxEffectiveSwingTicks}.
     */
    public double getStrikeStaminaCostGrowthFactor() {
        return strikeStaminaCostGrowthFactor;
    }

    /**
     * @return Возвращает количество выносливости, которое необходимо затратить на совершение действия
     *         {@code ActionType.CANCEL_STRIKE}.
     */
    public double getCancelStrikeStaminaCost() {
        return cancelStrikeStaminaCost;
    }

    /**
     * @return Возвращает количество выносливости, которое необходимо затратить на совершение действия
     *         {@code ActionType.PASS}.
     */
    public double getPassStaminaCost() {
        return passStaminaCost;
    }

    /**
     * @return Возвращает максимальную скорость перемещения вратаря.
     */
    public double getGoalieMaxSpeed() {
        return goalieMaxSpeed;
    }

    /**
     * @return Возвращает максимальную скорость перемещения полевого хоккеиста.
     */
    public double getHockeyistMaxSpeed() {
        return hockeyistMaxSpeed;
    }

    /**
     * @return Возвращает модуль скорости, добавляемой хоккеисту, попавшему под удар силы 1.0.
     */
    public double getStruckHockeyistInitialSpeedFactor() {
        return struckHockeyistInitialSpeedFactor;
    }

    /**
     * @return Возвращает модуль ускорения, приобретаемого хоккеистом, при {@code move.speedUp} равном 1.0,
     *         базовом значении атрибута подвижность и максимальном запасе выносливости.
     *         Направление ускорения совпадает с направлением хоккеиста.
     *         В игре отсутствует специальное ограничение на максимальную скорость хоккеиста, однако все
     *         игровые объекты подвержены воздействию силы трения, которая уменьшает модуль их скорости каждый тик.
     *         Чем больше скорость, тем на большую величину она уменьшается.
     */
    public double getHockeyistSpeedUpFactor() {
        return hockeyistSpeedUpFactor;
    }

    /**
     * @return Возвращает модуль ускорения, приобретаемого хоккеистом, при {@code move.speedUp} равном -1.0,
     *         базовом значении атрибута подвижность и максимальном запасе выносливости.
     *         Направление ускорения противоположно направлению хоккеиста.
     *         В игре отсутствует специальное ограничение на максимальную скорость хоккеиста, однако все
     *         игровые объекты подвержены воздействию силы трения, которая уменьшает модуль их скорости каждый тик.
     *         Чем больше скорость, тем на большую величину она уменьшается.
     */
    public double getHockeyistSpeedDownFactor() {
        return hockeyistSpeedDownFactor;
    }

    /**
     * @return Возвращает максимальный модуль угла поворота хоккеиста за тик при базовом значении атрибута подвижность
     *         и максимальном запасе выносливости.
     */
    public double getHockeyistTurnAngleFactor() {
        return hockeyistTurnAngleFactor;
    }

    /**
     * @return Возвращает значение атрибута сила для хоккеиста-универсала.
     */
    public int getVersatileHockeyistStrength() {
        return versatileHockeyistStrength;
    }

    /**
     * @return Возвращает значение атрибута стойкость для хоккеиста-универсала.
     */
    public int getVersatileHockeyistEndurance() {
        return versatileHockeyistEndurance;
    }

    /**
     * @return Возвращает значение атрибута ловкость для хоккеиста-универсала.
     */
    public int getVersatileHockeyistDexterity() {
        return versatileHockeyistDexterity;
    }

    /**
     * @return Возвращает значение атрибута подвижность для хоккеиста-универсала.
     */
    public int getVersatileHockeyistAgility() {
        return versatileHockeyistAgility;
    }

    /**
     * @return Возвращает значение атрибута сила для нападающего.
     */
    public int getForwardHockeyistStrength() {
        return forwardHockeyistStrength;
    }

    /**
     * @return Возвращает значение атрибута стойкость для нападающего.
     */
    public int getForwardHockeyistEndurance() {
        return forwardHockeyistEndurance;
    }

    /**
     * @return Возвращает значение атрибута ловкость для нападающего.
     */
    public int getForwardHockeyistDexterity() {
        return forwardHockeyistDexterity;
    }

    /**
     * @return Возвращает значение атрибута подвижность для нападающего.
     */
    public int getForwardHockeyistAgility() {
        return forwardHockeyistAgility;
    }

    /**
     * @return Возвращает значение атрибута сила для защитника.
     */
    public int getDefencemanHockeyistStrength() {
        return defencemanHockeyistStrength;
    }

    /**
     * @return Возвращает значение атрибута стойкость для защитника.
     */
    public int getDefencemanHockeyistEndurance() {
        return defencemanHockeyistEndurance;
    }

    /**
     * @return Возвращает значение атрибута ловкость для защитника.
     */
    public int getDefencemanHockeyistDexterity() {
        return defencemanHockeyistDexterity;
    }

    /**
     * @return Возвращает значение атрибута подвижность для защитника.
     */
    public int getDefencemanHockeyistAgility() {
        return defencemanHockeyistAgility;
    }

    /**
     * @return Возвращает минимально возможное значение любого атрибута для хоккеиста со случайными параметрами.
     */
    public int getMinRandomHockeyistParameter() {
        return minRandomHockeyistParameter;
    }

    /**
     * @return Возвращает максимально возможное значение любого атрибута для хоккеиста со случайными параметрами.
     */
    public int getMaxRandomHockeyistParameter() {
        return maxRandomHockeyistParameter;
    }

    /**
     * @return Возвращает модуль скорости, устанавливаемой шайбе, попавшей под удар силы 1.0.
     */
    public double getStruckPuckInitialSpeedFactor() {
        return struckPuckInitialSpeedFactor;
    }

    /**
     * @return Возвращает расстояние от центра хоккеиста, контролирующего шайбу, до центра шайбы.
     */
    public double getPuckBindingRange() {
        return puckBindingRange;
    }
}
