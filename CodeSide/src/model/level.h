#ifndef CODESIDE_LEVEL_H
#define CODESIDE_LEVEL_H

#include <climits>

constexpr const double TILE_EPS = 1e-8;

class TLevel {
public:
    static int width;
    static int height;
    static std::vector<std::vector<Tile>> tiles;
    static int myId;

    static Tile getTileType(double x, double y) {
        return tiles[(int) x][(int) y];
    }

    static bool isLeftWall(double x, double y) {
        auto tile = tiles[(int) (x + TILE_EPS)][(int) y];
        return tile == WALL;
    }

    static bool isRightWall(double x, double y) {
        auto tile = tiles[(int) (x - TILE_EPS)][(int) y];
        return tile == WALL;
    }

    static bool isUpperWall(double x1, double x2, double y) {
        return tiles[(int) (x1 + TILE_EPS)][(int) (y - TILE_EPS)] == WALL ||
               tiles[(int) (x2 - TILE_EPS)][(int) (y - TILE_EPS)] == WALL;
    }

    static bool isGround(double x, double y, bool jumpDown) {
        auto tile = tiles[(int) x][(int) (y + TILE_EPS)];
        return tile == WALL || (jumpDown && tile == PLATFORM);
    }
};

#endif //CODESIDE_LEVEL_H
