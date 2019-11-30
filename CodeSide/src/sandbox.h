#ifndef CODESIDE_SANDBOX_H
#define CODESIDE_SANDBOX_H

#include "model/unit.h"
#include "model/mine.h"
#include "model/lootbox.h"
#include "model/bullet.h"

class TSandbox {
public:
    int currentTick;
    Level level;
    std::vector<Player> players;
    std::vector<Unit> units;
    std::vector<Bullet> bullets;
    std::vector<Mine> mines;
    std::vector<LootBox> lootBoxes;

    TSandbox(const TUnit& unit, const Game& game) {

    }
};

#endif //CODESIDE_SANDBOX_H
