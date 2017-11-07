package model;

import java.util.Arrays;

/**
 * Этот класс описывает игровой мир. Содержит также описания всех игроков, игровых объектов (<<юнитов>>) и сооружений.
 */
@SuppressWarnings("ForLoopWithMissingComponent")
public class World {
    private final int tickIndex;
    private final int tickCount;
    private final double width;
    private final double height;
    private final Player[] players;
    private final Vehicle[] newVehicles;
    private final VehicleUpdate[] vehicleUpdates;
    private final TerrainType[][] terrainByCellXY;
    private final WeatherType[][] weatherByCellXY;
    private final Facility[] facilities;

    public World(
            int tickIndex, int tickCount, double width, double height, Player[] players, Vehicle[] newVehicles,
            VehicleUpdate[] vehicleUpdates, TerrainType[][] terrainByCellXY, WeatherType[][] weatherByCellXY,
            Facility[] facilities) {
        this.tickIndex = tickIndex;
        this.tickCount = tickCount;
        this.width = width;
        this.height = height;
        this.players = Arrays.copyOf(players, players.length);
        this.newVehicles = Arrays.copyOf(newVehicles, newVehicles.length);
        this.vehicleUpdates = Arrays.copyOf(vehicleUpdates, vehicleUpdates.length);

        this.terrainByCellXY = new TerrainType[terrainByCellXY.length][];
        for (int x = terrainByCellXY.length; --x >= 0; ) {
            this.terrainByCellXY[x] = Arrays.copyOf(terrainByCellXY[x], terrainByCellXY[x].length);
        }

        this.weatherByCellXY = new WeatherType[weatherByCellXY.length][];
        for (int x = weatherByCellXY.length; --x >= 0; ) {
            this.weatherByCellXY[x] = Arrays.copyOf(weatherByCellXY[x], weatherByCellXY[x].length);
        }

        this.facilities = Arrays.copyOf(facilities, facilities.length);
    }

    /**
     * @return Возвращает номер текущего тика.
     */
    public int getTickIndex() {
        return tickIndex;
    }

    /**
     * @return Возвращает базовую длительность игры в тиках. Реальная длительность может отличаться от этого значения в
     * меньшую сторону. Эквивалентно {@code game.tickCount}.
     */
    public int getTickCount() {
        return tickCount;
    }

    /**
     * @return Возвращает ширину мира.
     */
    public double getWidth() {
        return width;
    }

    /**
     * @return Возвращает высоту мира.
     */
    public double getHeight() {
        return height;
    }

    /**
     * @return Возвращает список игроков (в случайном порядке).
     * В зависимости от реализации, объекты, задающие игроков, могут пересоздаваться после каждого тика.
     */
    public Player[] getPlayers() {
        return Arrays.copyOf(players, players.length);
    }

    /**
     * @return Возвращает список техники, о которой у стратегии не было информации в предыдущий игровой тик. В этот
     * список попадает как только что произведённая техника, так и уже существующая, но находящаяся вне зоны видимости
     * до этого момента.
     */
    public Vehicle[] getNewVehicles() {
        return Arrays.copyOf(newVehicles, newVehicles.length);
    }

    /**
     * @return Возвращает значения изменяемых полей для каждой видимой техники, если хотя бы одно поле этой техники
     * изменилось. Нулевая прочность означает, что техника была уничтожена либо ушла из зоны видимости.
     */
    public VehicleUpdate[] getVehicleUpdates() {
        return Arrays.copyOf(vehicleUpdates, vehicleUpdates.length);
    }

    /**
     * @return Возвращает карту местности.
     */
    public TerrainType[][] getTerrainByCellXY() {
        @SuppressWarnings("LocalVariableHidesMemberVariable") TerrainType[][] terrainByCellXY = this.terrainByCellXY;
        TerrainType[][] copiedTerrainByCellXY = new TerrainType[terrainByCellXY.length][];
        for (int x = terrainByCellXY.length; --x >= 0; ) {
            copiedTerrainByCellXY[x] = Arrays.copyOf(terrainByCellXY[x], terrainByCellXY[x].length);
        }
        return copiedTerrainByCellXY;
    }

    /**
     * @return Возвращает карту погоды.
     */
    public WeatherType[][] getWeatherByCellXY() {
        @SuppressWarnings("LocalVariableHidesMemberVariable") WeatherType[][] weatherByCellXY = this.weatherByCellXY;
        WeatherType[][] copiedWeatherByCellXY = new WeatherType[weatherByCellXY.length][];
        for (int x = weatherByCellXY.length; --x >= 0; ) {
            copiedWeatherByCellXY[x] = Arrays.copyOf(weatherByCellXY[x], weatherByCellXY[x].length);
        }
        return copiedWeatherByCellXY;
    }

    /**
     * @return Возвращает список сооружений (в случайном порядке).
     * В зависимости от реализации, объекты, задающие сооружения, могут пересоздаваться после каждого тика.
     */
    public Facility[] getFacilities() {
        return Arrays.copyOf(facilities, facilities.length);
    }

    /**
     * @return Возвращает вашего игрока.
     */
    public Player getMyPlayer() {
        int playerIndex = players.length;

        while (--playerIndex >= 0) {
            Player player = players[playerIndex];
            if (player.isMe()) {
                return player;
            }
        }

        return null;
    }

    /**
     * @return Возвращает игрока, соревнующегося с вами.
     */
    public Player getOpponentPlayer() {
        int playerIndex = players.length;

        while (--playerIndex >= 0) {
            Player player = players[playerIndex];
            if (!player.isMe()) {
                return player;
            }
        }

        return null;
    }
}
