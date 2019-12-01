#ifndef CODESIDE_SANDBOX_H
#define CODESIDE_SANDBOX_H

#include "model/unit.h"
#include "model/mine.h"
#include "model/lootbox.h"
#include "model/bullet.h"

#include <iostream>

class TSandbox {
public:
    int currentTick;
    Player players[2];
    std::vector<TUnit> units;
    std::vector<TBullet> bullets;
    std::vector<TMine> mines;
    std::vector<TLootBox> lootBoxes;

    TSandbox() {
    }

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
        std::sort(units.begin(), units.end(), [](const TUnit& a, const TUnit& b) {
            return a.id < b.id;
        });
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
        currentTick++;
    }

private:
    void _doMicroTick(int updatesPerTick) {
        for (auto& unit : units) {
            const auto& action = unit.action;
            double dx = action.velocity / (TICKS_PER_SECOND * updatesPerTick);
            double side_x = action.velocity > 0 ? unit.x2 : unit.x1;
            if (TLevel::getTileType(side_x + dx, unit.y1) != WALL &&
                TLevel::getTileType(side_x + dx, unit.y2) != WALL &&
                TLevel::getTileType(side_x + dx, unit.y1 + UNIT_HALF_HEIGHT) != WALL) {

                unit.x1 += dx;
                unit.x2 += dx;
            }

            for (int rep = 0; rep < 2; rep++) {
                if (action.jump && unit.canJump) {
                    double dy = UNIT_JUMP_SPEED / (TICKS_PER_SECOND * updatesPerTick);
                    if (!TLevel::isUpperWall(unit.x1, unit.y2 + dy) &&
                        !TLevel::isUpperWall(unit.x2, unit.y2 + dy)) {

                        unit.y1 += dy;
                        unit.y2 += dy;
                        break;
                    } else {
                        unit.canJump = false;
                    }
                } else if (action.jumpDown || !unit.canJump) {
                    double dy = -UNIT_FALL_SPEED / (TICKS_PER_SECOND * updatesPerTick);
                    auto tl = TLevel::getTileType(unit.x1, unit.y1 + dy);
                    auto tr = TLevel::getTileType(unit.x2, unit.y1 + dy);
                    if (!TLevel::isGround(unit.x1, unit.y1 + dy, action.jumpDown) &&
                        !TLevel::isGround(unit.x2, unit.y1 + dy, action.jumpDown)) {

                        unit.y1 += dy;
                        unit.y2 += dy;
                        break;
                    } else {
                        unit.canJump = true;
                    }
                } else {
                    break;
                }
            }

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
