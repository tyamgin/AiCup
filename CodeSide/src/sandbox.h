#ifndef CODESIDE_SANDBOX_H
#define CODESIDE_SANDBOX_H

#include "model/unit.h"
#include "model/mine.h"
#include "model/lootbox.h"
#include "model/bullet.h"

class TSandbox {
public:
    int currentTick;
    Player players[2];
    std::vector<TUnit> units;
    std::vector<TBullet> bullets;
    std::vector<TMine> mines;
    std::vector<TLootBox> lootBoxes;

    TSandbox(const TUnit& unit, const Game& game) {
        currentTick = game.currentTick;
        players[0] = game.players[0];
        players[1] = game.players[1];
        for (const Unit& u : game.units) {
            units.emplace_back(u);
        }
        for (const Bullet& b : game.bullets) {
            bullets.emplace_back(b);
        }
        for (const Mine& m : game.mines) {
            mines.emplace_back(m);
        }
        for (const LootBox& l : game.lootBoxes) {
            lootBoxes.emplace_back(l);
        }
    }

    TSandbox(const TSandbox& sandbox) {
        currentTick = sandbox.currentTick;
        players[0] = sandbox.players[0];
        players[1] = sandbox.players[1];
        units = sandbox.units;
        bullets = sandbox.bullets;
        mines = sandbox.mines;
        lootBoxes = sandbox.lootBoxes;
    }


    void doTick(int updatesPerTick = UPDATES_PER_TICK) {
        for (int microTick = 0; microTick < updatesPerTick; microTick++) {
            _doMicroTick(updatesPerTick);
        }
    }

private:
    void _doMicroTick(int updatesPerTick) {
        for (auto& unit : units) {
            // do move
            for (int i = 0; i < (int) lootBoxes.size(); i++) {
                auto& loot = lootBoxes[i];
                // TODO: loop can be optimized by lookup table
                if (loot.intersectsWith(unit)) {
                    if (unit.applyLoot(loot)) {
                        lootBoxes.erase(lootBoxes.begin() + i);
                        i--;
                    }
                }
            }
        }
    }
};

#endif //CODESIDE_SANDBOX_H
