#ifndef CODESIDE_LEVEL_H
#define CODESIDE_LEVEL_H

class TLevel {
public:
    static std::vector<std::vector<Tile>> tiles;

    static Tile getTileType(double x, double y) {
        return tiles[(int) x][(int) y];
    }
};

#endif //CODESIDE_LEVEL_H
