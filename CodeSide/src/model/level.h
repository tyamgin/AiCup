#ifndef CODESIDE_LEVEL_H
#define CODESIDE_LEVEL_H

class TLevel {
public:
    static std::vector<std::vector<Tile>> tiles;
    static int myId;

    static Tile getTileType(double x, double y) {
        return tiles[(int) x][(int) y];
    }

    static bool isUpperWall(double x, double y) {
        auto tile = tiles[(int) x][(int) (y - EPS)];
        return tile == WALL;
    }

    static bool isGround(double x, double y, bool jumpDown) {
        auto tile = tiles[(int) x][(int) (y + EPS)];
        return tile == WALL || (jumpDown && tile == PLATFORM);
    }
};

#endif //CODESIDE_LEVEL_H
