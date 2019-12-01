#ifndef CODESIDE_SANDBOX_H
#define CODESIDE_SANDBOX_H

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
            double dx = action.velocity / (TICKS_PER_SECOND * updatesPerTick);
            double side_x = action.velocity > 0 ? unit.x2 : unit.x1;
            if (action.velocity > 0) {
                if (!TLevel::isRightWall(unit.x2 + dx, unit.y1) &&
                    !TLevel::isRightWall(unit.x2 + dx, unit.y2) &&
                    !TLevel::isRightWall(unit.x2 + dx, unit.y1 + UNIT_HALF_HEIGHT)) {

                    unit.x1 += dx;
                    unit.x2 += dx;
                }
            } else if (action.velocity < 0) {
                if (!TLevel::isLeftWall(unit.x1 + dx, unit.y1) &&
                    !TLevel::isLeftWall(unit.x1 + dx, unit.y2) &&
                    !TLevel::isLeftWall(unit.x1 + dx, unit.y1 + UNIT_HALF_HEIGHT)) {

                    unit.x1 += dx;
                    unit.x2 += dx;
                }
            }

            if (unit.jumpMaxTime < -EPS) {
                unit.canJump = false;
            }

            for (int rep = 0; rep < 2; rep++) {
                if (unit.canJump && (action.jump || !unit.jumpCanCancel)) {
                    double dy = (unit.jumpCanCancel ? UNIT_JUMP_SPEED : JUMP_PAD_JUMP_SPEED) / (TICKS_PER_SECOND * updatesPerTick);
                    if (!TLevel::isUpperWall(unit.x1, unit.x2, unit.y2 + dy)) {
                        unit.y1 += dy;
                        unit.y2 += dy;
                        unit.jumpMaxTime -= 1.0 / TICKS_PER_SECOND / updatesPerTick;
                        break;
                    } else {
                        unit.jumpMaxTime = 0;
                        unit.canJump = false;
                    }
                } else if (action.jumpDown || !unit.canJump) {
                    double dy = -UNIT_FALL_SPEED / (TICKS_PER_SECOND * updatesPerTick);
                    if (!TLevel::isGround(unit.x1, unit.y1 + dy, action.jumpDown) &&
                        !TLevel::isGround(unit.x2, unit.y1 + dy, action.jumpDown)) {

                        unit.y1 += dy;
                        unit.y2 += dy;
                        break;
                    } else {
                        unit.jumpMaxTime = UNIT_JUMP_TIME;
                        unit.canJump = true;
                        unit.jumpCanCancel = true;
                    }
                } else {
                    break;
                }
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
