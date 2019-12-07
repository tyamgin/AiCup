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

    TSandbox() {
        currentTick = -1;
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
        currentTick++;
    }

private:
    void _doMicrotickWeapon(TUnit& unit, double updatesPerSecond) {
        auto& weapon = unit.weapon;
        if (weapon.type == ELootType::NONE) {
            weapon.fireTimer = -1;
            weapon.lastAngle = DEFAULT_LAST_ANGLE;
            return;
        }
        if (unit.action.aim.length2() >= SQR(0.5)) {
            auto newAngle = unit.action.aim.getAngle();
            if (weapon.lastAngle < 100) {
                weapon.spread = std::min(weapon.spread + std::abs(newAngle - weapon.lastAngle), WEAPON_MAX_SPREAD);
            }
            weapon.lastAngle = newAngle;
            if (unit.action.shoot && weapon.magazine > 0 && weapon.fireTimer < -0.5) {
                bullets.emplace_back(unit.shot());
                weapon.lastFireTick = currentTick;
            }
        }
        weapon.spread = std::max(weapon.spread - WEAPON_AIM_SPEED[(int) weapon.type] / updatesPerSecond, WEAPON_MIN_SPREAD[(int) weapon.type]);
        if (weapon.fireTimer > -0.5) {
            weapon.fireTimer -= WEAPON_RELOAD_TIME / updatesPerSecond;
            if (weapon.fireTimer < 0) {
                weapon.fireTimer = -1;
            }
        }
    }

    void _doMicroTick(int updatesPerTick) {
        auto updatesPerSecond = updatesPerTick * TICKS_PER_SECOND;

        for (auto& unit : units) {
            _doMicrotickWeapon(unit, updatesPerSecond);

            for (int i = int(unit.x1); i <= int(unit.x2); i++) {
                for (int j = int(unit.y1 + 1e-8); j <= int(unit.y2 + 1e-8); j++) {
                    unsigned idx = (*lootBoxIndex)[i][j];
                    if (idx != UINT32_MAX) {
                        unit.maybeApplyLoot(lootBoxes[idx]);
                    }
                }
            }

            const auto& action = unit.action;
            auto onLadder = unit.isOnLadder();
            double dx = action.velocity / updatesPerSecond;

            if (action.velocity > 0) {
                int xcell = int(unit.x2 + dx);
                if (TLevel::tiles[xcell][int(unit.y1)] == ETile::WALL ||
                    TLevel::tiles[xcell][int(unit.y2)] == ETile::WALL ||
                    TLevel::tiles[xcell][int(unit.y1 + UNIT_HALF_HEIGHT)] == ETile::WALL) {

                    unit.x2 = xcell - FUCKING_EPS;
                    unit.x1 = unit.x2 - UNIT_SIZE_X;
                } else {
                    unit.x1 += dx;
                    unit.x2 += dx;
                }
            } else if (action.velocity < 0) {
                int xcell = int(unit.x1 + dx);
                if (TLevel::tiles[xcell][int(unit.y1)] == ETile::WALL ||
                    TLevel::tiles[xcell][int(unit.y2)] == ETile::WALL ||
                    TLevel::tiles[xcell][int(unit.y1 + UNIT_HALF_HEIGHT)] == ETile::WALL) {

                    unit.x1 = xcell + 1 + FUCKING_EPS;
                    unit.x2 = unit.x1 + UNIT_SIZE_X;
                } else {
                    unit.x1 += dx;
                    unit.x2 += dx;
                }
            }

            if (unit.canJump && (action.jump || !unit.jumpCanCancel)) {
                double dy = (unit.jumpCanCancel ? UNIT_JUMP_SPEED : JUMP_PAD_JUMP_SPEED) / updatesPerSecond;

                int ycell = int(unit.y2 + dy);
                if (TLevel::tiles[int(unit.x1)][ycell] == ETile::WALL || TLevel::tiles[int(unit.x2)][ycell] == ETile::WALL) {
                    unit.jumpMaxTime = 0;
                    unit.canJump = false;
                    unit.jumpCanCancel = false;
                    unit.y2 = std::max(unit.y2, ycell - FUCKING_EPS);
                    unit.y1 = unit.y2 - UNIT_SIZE_Y;
                } else {
                    unit.y1 += dy;
                    unit.y2 += dy;
                    if (unit.isOnLadder()) {
                        unit.jumpMaxTime = UNIT_JUMP_TIME;
                        unit.canJump = true;
                        unit.jumpCanCancel = true;
                    } else {
                        unit.jumpMaxTime -= 1.0 / updatesPerSecond;
                    }
                }
            } else if (action.jumpDown || !onLadder) {
                double dy = -UNIT_FALL_SPEED / updatesPerSecond;

                auto wallMask = uint32_t(ETile::WALL);
                if (!action.jumpDown) {
                    if (TLevel::getTileType(unit.x1, unit.y1) != ETile::PLATFORM && TLevel::getTileType(unit.x2, unit.y1) != ETile::PLATFORM) {
                        wallMask |= uint32_t(ETile::PLATFORM);
                    }
                    if (unit.isStandOnLadder(dy)) {
                        wallMask |= uint32_t(ETile::LADDER);
                    }
                }

                int ycell = int(unit.y1 + dy);
                if (((uint32_t)TLevel::tiles[int(unit.x1)][ycell] & wallMask) ||
                    ((uint32_t)TLevel::tiles[int(unit.x2)][ycell] & wallMask)) {

                    unit.jumpMaxTime = UNIT_JUMP_TIME;
                    unit.canJump = true;
                    unit.jumpCanCancel = true;
                    unit.y1 = std::min(unit.y1, ycell + (1 + FUCKING_EPS));
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

            if (!unit.jumpCanCancel && unit.isOnLadder()) {
                unit.canJump = true;
                unit.jumpMaxTime = UNIT_JUMP_TIME;
                unit.jumpCanCancel = true;
            }

            for (int i = int(unit.x1); i <= int(unit.x2); i++) {
                for (int j = int(unit.y1); j <= int(unit.y2); j++) {
                    if (TLevel::tiles[i][j] == ETile::JUMP_PAD) {
                        unit.jumpCanCancel = false;
                        unit.canJump = true;
                        unit.jumpMaxTime = JUMP_PAD_JUMP_TIME;
                        goto endPadLoop;
                    }
                }
            }
            endPadLoop:;
        }

        _updateBullets(updatesPerSecond, true);
        _updateBullets(updatesPerSecond, false);
    }

    void _updateBullets(double updatesPerSecond, bool checkUnitCollisionsOnly) {
        for (int i = 0; i < (int) bullets.size(); i++) {
            auto& bullet = bullets[i];
            if (!checkUnitCollisionsOnly) {
                auto dx = bullet.velocity.x / updatesPerSecond;
                auto dy = bullet.velocity.y / updatesPerSecond;
                bullet.x1 += dx;
                bullet.x2 += dx;
                bullet.y1 += dy;
                bullet.y2 += dy;
            }
            if (_bulletOut(bullet, checkUnitCollisionsOnly)) {
                bullets.erase(bullets.begin() + i);
                i--;
            }
        }
    }

    bool _bulletOut(const TBullet& bullet, bool checkUnitCollisionsOnly) {
        if (!_bulletOutIn(bullet, checkUnitCollisionsOnly)) {
            return false;
        }
        if (bullet.weaponType == ELootType::ROCKET_LAUNCHER) {
            for (auto& unit : units) {
                if (unit.intersectsWith(bullet.x1, bullet.y1, bullet.x2, bullet.y2)) {
                    unit.health -= bullet.damage();
                }
            }
        }
        return true;
    }

    bool _bulletOutIn(const TBullet& bullet, bool checkUnitCollisionsOnly) {
        if (checkUnitCollisionsOnly) {
            for (auto& unit : units) {
                if (unit.id != bullet.unitId && unit.intersectsWith(bullet)) {
                    if (bullet.weaponType != ELootType::ROCKET_LAUNCHER) {
                        unit.health -= bullet.damage();
                    }
                    return true;
                }
            }
        } else {
            for (int i = int(bullet.x1); i <= int(bullet.x2); i++) {
                for (int j = int(bullet.y1); j <= int(bullet.y2); j++) {
                    if (getTile(i, j) == ETile::WALL) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
};

#endif //CODESIDE_SANDBOX_H