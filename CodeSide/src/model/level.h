#ifndef CODESIDE_LEVEL_H
#define CODESIDE_LEVEL_H

#include <climits>
#include <string>

enum class ETile : uint32_t {
    EMPTY = 0,
    WALL = 1,
    PLATFORM = 2,
    LADDER = 4,
    JUMP_PAD = 8,
};

class TLevel {
public:
    static int width;
    static int height;
    static std::vector<std::vector<ETile>> tiles;
    static int myId;
    static int teamSize;
    static std::vector<int> unitIdToPlayerIdx;

    static ETile getTileType(double x, double y) {
        return tiles[(int) x][(int) y];
    }

    static void init(const Unit& unit, const Game& game) {
        myId = unit.playerId;
        teamSize = game.properties.teamSize;
        const auto& inpTiles = game.level.tiles;
        width = (int) inpTiles.size();
        height = (int) inpTiles[0].size();
        if (tiles.empty()) {
            tiles = std::vector<std::vector<ETile>>((size_t) width, std::vector<ETile>((size_t) height));
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    switch (inpTiles[i][j]) {
                        case WALL:
                            tiles[i][j] = ETile::WALL;
                            break;
                        case PLATFORM:
                            tiles[i][j] = ETile::PLATFORM;
                            break;
                        case LADDER:
                            tiles[i][j] = ETile::LADDER;
                            break;
                        case JUMP_PAD:
                            tiles[i][j] = ETile::JUMP_PAD;
                            break;
                        case EMPTY:
                        default:
                            tiles[i][j] = ETile::EMPTY;
                            break;
                    }
                }
            }
        }
        for (const auto& u : game.units) {
            while (unitIdToPlayerIdx.size() <= u.id) {
                unitIdToPlayerIdx.push_back(0);
            }
            unitIdToPlayerIdx[unit.id] = unit.playerId == myId ? 0 : 1;
        }
    }

    static std::string toString() {
        std::string res;
        for (int j = TLevel::height - 1; j >= 0; j--) {
            for (int i = 0; i < TLevel::width; i++) {
                res += tileChar(tiles[i][j]);
            }
            res += '\n';
        }
        return res;
    }

    static char tileChar(ETile tile) {
        switch(tile) {
            case ETile::EMPTY:
                return '.';
            case ETile::WALL:
                return '#';
            case ETile::PLATFORM:
                return '^';
            case ETile::LADDER:
                return 'H';
            case ETile::JUMP_PAD:
                return 'T';
            default:
                return 0;
        }
    }
};

inline ETile getTile(int i, int j) {
    return TLevel::tiles[i][j];
}

inline bool tileMatch(ETile tile, ETile v1) {
    return tile == v1;
}

inline bool tileMatch(ETile tile, ETile v1, ETile v2) {
    return tile == v1 || tile == v2;
}

inline bool isStayableTile(int i, int j) {
    if (getTile(i, j + 1) == ETile::WALL) {
        return false;
    }
    if (getTile(i, j) == ETile::WALL) {
        return false;
    }
    if (getTile(i, j) == ETile::LADDER) {
        return true;
    }
    return getTile(i, j - 1) == ETile::WALL || getTile(i, j - 1) == ETile::PLATFORM;
}

#endif //CODESIDE_LEVEL_H
