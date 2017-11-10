package model;

import java.util.Arrays;

@SuppressWarnings("ForLoopWithMissingComponent")
public class World {
    private final int tickIndex;
    private final int tickCount;
    private final double width;
    private final double height;
    private final Player[] players;
    private final Vehicle[] vehicles;
    private final TerrainType[][] terrainByCellXY;
    private final WeatherType[][] weatherByCellXY;
    private final Facility[] facilities;

    public World(
            int tickIndex, int tickCount, double width, double height, Player[] players, Vehicle[] vehicles,
            TerrainType[][] terrainByCellXY, WeatherType[][] weatherByCellXY, Facility[] facilities) {
        this.tickIndex = tickIndex;
        this.tickCount = tickCount;
        this.width = width;
        this.height = height;
        this.players = Arrays.copyOf(players, players.length);
        this.vehicles = Arrays.copyOf(vehicles, vehicles.length);

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

    public int getTickIndex() {
        return tickIndex;
    }

    public int getTickCount() {
        return tickCount;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Player[] getPlayers() {
        return Arrays.copyOf(players, players.length);
    }

    public Vehicle[] getVehicles() {
        return Arrays.copyOf(vehicles, vehicles.length);
    }

    public TerrainType[][] getTerrainByCellXY() {
        @SuppressWarnings("LocalVariableHidesMemberVariable") TerrainType[][] terrainByCellXY = this.terrainByCellXY;
        TerrainType[][] copiedTerrainByCellXY = new TerrainType[terrainByCellXY.length][];
        for (int x = terrainByCellXY.length; --x >= 0; ) {
            copiedTerrainByCellXY[x] = Arrays.copyOf(terrainByCellXY[x], terrainByCellXY[x].length);
        }
        return copiedTerrainByCellXY;
    }

    public WeatherType[][] getWeatherByCellXY() {
        @SuppressWarnings("LocalVariableHidesMemberVariable") WeatherType[][] weatherByCellXY = this.weatherByCellXY;
        WeatherType[][] copiedWeatherByCellXY = new WeatherType[weatherByCellXY.length][];
        for (int x = weatherByCellXY.length; --x >= 0; ) {
            copiedWeatherByCellXY[x] = Arrays.copyOf(weatherByCellXY[x], weatherByCellXY[x].length);
        }
        return copiedWeatherByCellXY;
    }

    public Facility[] getFacilities() {
        return Arrays.copyOf(facilities, facilities.length);
    }

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
