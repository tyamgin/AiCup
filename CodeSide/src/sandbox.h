#ifndef CODESIDE_SANDBOX_H
#define CODESIDE_SANDBOX_H

#define FUCKING_EPS 1e-9

#include "model/unit.h"
#include "model/mine.h"
#include "model/lootbox.h"
#include "model/bullet.h"

#include <iostream>
#include <tuple>

class TSandbox {
public:
    int currentTick;
    Player players[2];
    std::vector<TUnit> units;
    std::vector<TBullet> bullets;
    std::vector<TMine> mines;
    std::vector<TLootBox> lootBoxes;
    std::shared_ptr<std::vector<std::vector<unsigned>>> lootBoxIndex;

    TSandbox() = default;

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
        std::sort(lootBoxes.begin(), lootBoxes.end(), [](const TLootBox& a, const TLootBox& b) {
            return std::make_pair(a.getRow(), a.getCol()) < std::make_pair(b.getRow(), b.getCol());
        });
        std::sort(units.begin(), units.end(), [](const TUnit& a, const TUnit& b) {
            return a.id < b.id;
        });
        lootBoxIndex = std::make_shared<std::vector<std::vector<unsigned>>>(TLevel::width, std::vector<unsigned>(TLevel::height, UINT32_MAX));
        for (const LootBox& l : game.lootBoxes) {
            TLootBox lb(l);
            (*lootBoxIndex)[lb.getRow()][lb.getCol()] = (unsigned) lootBoxes.size();
            lootBoxes.emplace_back(lb);
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
        lootBoxIndex = sandbox.lootBoxIndex;
    }


    void doTick(int updatesPerTick = UPDATES_PER_TICK) {
        for (int microTick = 0; microTick < updatesPerTick; microTick++) {
            _doMicroTick(updatesPerTick);
        }
        for (auto& unit : units) {
            unit.weapon.decreaseFireTimer();
        }
        currentTick++;
    }

private:
    void _doMicroTick(int updatesPerTick) {
        for (auto& unit : units) {
            const auto& action = unit.action;
            auto onLadder = unit.isOnLadder();
            double dx = action.velocity / (TICKS_PER_SECOND * updatesPerTick);
            double side_x = action.velocity > 0 ? unit.x2 : unit.x1;
            if (action.velocity > 0) {
                int xcell = int(unit.x2 + dx);
                if (TLevel::tiles[xcell][int(unit.y1)] == WALL ||
                    TLevel::tiles[xcell][int(unit.y2)] == WALL ||
                    TLevel::tiles[xcell][int(unit.y1 + UNIT_HALF_HEIGHT)] == WALL) {

                    unit.x2 = xcell - FUCKING_EPS;
                    unit.x1 = unit.x2 - UNIT_SIZE_X;
                } else {
                    unit.x1 += dx;
                    unit.x2 += dx;
                }
            } else if (action.velocity < 0) {
                int xcell = int(unit.x1 + dx);
                if (TLevel::tiles[xcell][int(unit.y1)] == WALL ||
                    TLevel::tiles[xcell][int(unit.y2)] == WALL ||
                    TLevel::tiles[xcell][int(unit.y1 + UNIT_HALF_HEIGHT)] == WALL) {

                    unit.x1 = xcell + FUCKING_EPS;
                    unit.x2 = unit.x1 + UNIT_SIZE_X;
                } else {
                    unit.x1 += dx;
                    unit.x2 += dx;
                }
            }

            if (unit.canJump && (action.jump || !unit.jumpCanCancel)) {
                double dy = (unit.jumpCanCancel ? UNIT_JUMP_SPEED : JUMP_PAD_JUMP_SPEED) / (TICKS_PER_SECOND * updatesPerTick);

                int ycell = int(unit.y2 + dy);
                if (TLevel::tiles[int(unit.x1)][ycell] == WALL || TLevel::tiles[int(unit.x2)][ycell] == WALL) {
                    unit.jumpMaxTime = 0;
                    unit.canJump = false;
                    unit.jumpCanCancel = false;
                    unit.y2 = ycell - FUCKING_EPS;
                    unit.y1 = unit.y2 - UNIT_SIZE_Y;
                } else {
                    unit.y1 += dy;
                    unit.y2 += dy;
                    if (unit.isOnLadder()) {
                        if (!onLadder) {

                        }
                    } else {
                        unit.jumpMaxTime -= 1.0 / TICKS_PER_SECOND / updatesPerTick;
                    }
                }
            } else if (action.jumpDown || !onLadder) {
                double dy = -UNIT_FALL_SPEED / (TICKS_PER_SECOND * updatesPerTick);

                int ycell = int(unit.y1 + dy);
                int x1cell = int(unit.x1);
                int x2cell = int(unit.x2);
                auto tile1 = TLevel::tiles[x1cell][ycell];
                auto tile2 = TLevel::tiles[x2cell][ycell];

                if (tile1 == WALL || tile2 == WALL || (!action.jumpDown && (tile1 == PLATFORM || tile2 == PLATFORM))) {
                    unit.jumpMaxTime = UNIT_JUMP_TIME;
                    unit.canJump = true;
                    unit.jumpCanCancel = true;
                    unit.y1 = ycell + (1 + FUCKING_EPS);
                    unit.y2 = unit.y1 + UNIT_SIZE_Y;
                } else {
                    unit.y1 += dy;
                    unit.y2 += dy;
                    if (unit.isOnLadder()) {
                        unit.jumpMaxTime = UNIT_JUMP_TIME;
                        unit.canJump = true;
                        unit.jumpCanCancel = true;
                    } else {
                        unit.jumpMaxTime = 0;
                        unit.canJump = false;
                        unit.jumpCanCancel = false;
                    }
                }
            } else {
                if (unit.isOnLadder()) {
                    unit.jumpMaxTime = UNIT_JUMP_TIME;
                    unit.canJump = true;
                    unit.jumpCanCancel = true;
                } else {
                    unit.jumpMaxTime = 0;
                    unit.canJump = false;
                    unit.jumpCanCancel = false;
                }
            }

            if (unit.jumpMaxTime <= -1e-11) {
                unit.canJump = false;
                unit.jumpMaxTime = 0;
                unit.jumpCanCancel = false;
            }

            bool intersectsWithPad = TLevel::getTileType(unit.x1, unit.y1) == JUMP_PAD ||
                                     TLevel::getTileType(unit.x1, unit.y2) == JUMP_PAD ||
                                     TLevel::getTileType(unit.x1, unit.y1 + UNIT_HALF_HEIGHT) == JUMP_PAD ||
                                     TLevel::getTileType(unit.x2, unit.y1) == JUMP_PAD ||
                                     TLevel::getTileType(unit.x2, unit.y2) == JUMP_PAD ||
                                     TLevel::getTileType(unit.x2, unit.y1 + UNIT_HALF_HEIGHT) == JUMP_PAD;
            if (intersectsWithPad) {
                unit.jumpCanCancel = false;
                unit.canJump = true;
                unit.jumpMaxTime = JUMP_PAD_JUMP_TIME;
            }

            {
                int x1 = int(unit.x1);
                int x2 = int(unit.x2);
                int y = int(unit.y1 + 1e-8);
                unsigned idx1 = (*lootBoxIndex)[x1][y], idx2 = (*lootBoxIndex)[x2][y];
                if (idx1 != UINT32_MAX) {
                    unit.maybeApplyLoot(lootBoxes[idx1]);
                }
                if (idx2 != UINT32_MAX && idx1 != idx2) {
                    unit.maybeApplyLoot(lootBoxes[idx2]);
                }
            }
        }
    }
};

#endif //CODESIDE_SANDBOX_H
