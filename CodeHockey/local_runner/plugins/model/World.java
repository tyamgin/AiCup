package model;

import java.util.Arrays;

/**
 * Этот класс описывает игровой мир. Содержит также описания всех игроков и игровых объектов (<<юнитов>>).
 */
public class World {
    private final int tick;
    private final int tickCount;
    private final double width;
    private final double height;
    private final Player[] players;
    private final Hockeyist[] hockeyists;
    private final Puck puck;

    public World(
            int tick, int tickCount, double width, double height, Player[] players,
            Hockeyist[] hockeyists, Puck puck) {
        this.tick = tick;
        this.tickCount = tickCount;
        this.width = width;
        this.height = height;
        this.players = Arrays.copyOf(players, players.length);
        this.hockeyists = Arrays.copyOf(hockeyists, hockeyists.length);
        this.puck = puck;
    }

    /**
     * @return Возвращает номер текущего тика.
     */
    public int getTick() {
        return tick;
    }

    /**
     * @return Возвращает базовую длительность игры в тиках.
     *         Реальная длительность может отличаться от этого значения в большую сторону.
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
     *         После каждого тика объекты, задающие игроков, пересоздаются.
     */
    public Player[] getPlayers() {
        return Arrays.copyOf(players, players.length);
    }

    /**
     * @return Возвращает список хоккеистов (в случайном порядке), включая вратарей и хоккеиста стратегии,
     *         вызвавшей этот метод. После каждого тика объекты, задающие хоккеистов, пересоздаются.
     */
    public Hockeyist[] getHockeyists() {
        return Arrays.copyOf(hockeyists, hockeyists.length);
    }

    /**
     * @return Возвращает шайбу.
     */
    public Puck getPuck() {
        return puck;
    }

    /**
     * @return Возвращает вашего игрока.
     */
    public Player getMyPlayer() {
        for (int playerIndex = players.length - 1; playerIndex >= 0; --playerIndex) {
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
        for (int playerIndex = players.length - 1; playerIndex >= 0; --playerIndex) {
            Player player = players[playerIndex];
            if (!player.isMe()) {
                return player;
            }
        }

        return null;
    }
}
