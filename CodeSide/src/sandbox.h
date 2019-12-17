#ifndef CODESIDE_SANDBOX_H
#define CODESIDE_SANDBOX_H

#define FUCKING_EPS 1e-9

#include "model/unit.h"
#include "model/mine.h"
#include "model/lootbox.h"
#include "model/bullet.h"

#include <algorithm>
#include <iostream>
#include <tuple>

struct TSandboxCloneOptions {
    std::vector<int> unitIds = {-1};
    bool needLootboxes = true;
    bool needMines = true;
    bool needBullets = true;
};

class TSandbox {
private:
    static std::shared_ptr<std::vector<std::vector<unsigned>>> _emptyBonusIndex;
    std::vector<const TUnit*> _nearestUnitCache;
public:
    int currentTick;
    int score[2] = {0, 0};
    int friendlyLoss[2] = {0, 0};
    std::vector<TUnit> units;
    std::vector<TBullet> bullets;
    std::vector<TMine> mines;
    std::vector<TLootBox> lootBoxes;
    std::shared_ptr<std::vector<std::vector<unsigned>>> lootBoxIndex;
    int myCount;
    bool oppShotSimpleStrategy = false;
    double shotSpreadToss = 0;
    bool oppFallFreeze = false;

    TSandbox() {
        currentTick = -1;
    }

    explicit TSandbox(const Game& game) {
        currentTick = game.currentTick;
        myCount = 0;
        for (const Unit& u : game.units) {
            units.emplace_back(u);
            myCount += units.back().isMy();
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
        for (const auto& player : game.players) {
            score[player.id != TLevel::myId] = player.score;
        }
        friendlyLoss[0] = friendlyLoss[1] = 0;
    }

    TSandbox(const TSandbox& sandbox) {
        currentTick = sandbox.currentTick;
        score[0] = sandbox.score[0];
        score[1] = sandbox.score[1];
        friendlyLoss[0] = sandbox.friendlyLoss[0];
        friendlyLoss[1] = sandbox.friendlyLoss[1];
        units = sandbox.units;
        bullets = sandbox.bullets;
        mines = sandbox.mines;
        lootBoxes = sandbox.lootBoxes;
        lootBoxIndex = sandbox.lootBoxIndex;
        oppShotSimpleStrategy = sandbox.oppShotSimpleStrategy;
        shotSpreadToss = sandbox.shotSpreadToss;
        oppFallFreeze = sandbox.oppFallFreeze;
        myCount = sandbox.myCount;
    }

    TSandbox(const TSandbox& sandbox, const TSandboxCloneOptions& options) {
        currentTick = sandbox.currentTick;
        score[0] = sandbox.score[0];
        score[1] = sandbox.score[1];
        friendlyLoss[0] = sandbox.friendlyLoss[0];
        friendlyLoss[1] = sandbox.friendlyLoss[1];
        if (options.unitIds.size() == 1 && options.unitIds[0] == -1) {
            units = sandbox.units;
        } else {
            for (auto& unit : sandbox.units) {
                for (auto id : options.unitIds) {
                    if (unit.id == id) {
                        units.emplace_back(unit);
                        break;
                    }
                }
            }
        }
        if (options.needBullets) {
            bullets = sandbox.bullets;
        }
        if (options.needMines) {
            mines = sandbox.mines;
        }
        if (options.needLootboxes) {
            lootBoxes = sandbox.lootBoxes;
            lootBoxIndex = sandbox.lootBoxIndex;
        } else {
            if (!_emptyBonusIndex) {
                _emptyBonusIndex = std::make_shared<std::vector<std::vector<unsigned>>>(TLevel::width, std::vector<unsigned>(TLevel::height, UINT32_MAX));
            }
            lootBoxIndex = _emptyBonusIndex;
        }
        oppShotSimpleStrategy = sandbox.oppShotSimpleStrategy;
        shotSpreadToss = sandbox.shotSpreadToss;
        oppFallFreeze = sandbox.oppFallFreeze;
        myCount = sandbox.myCount;
    }

    void doTick(int updatesPerTick = UPDATES_PER_TICK) {
        std::vector<bool> swapWeaponBackup;
        _nearestUnitCache.resize(units.size());
        for (int unitIdx = 0; unitIdx < (int) units.size(); unitIdx++) {
            auto& unit = units[unitIdx];

            if (!unit.isMy()) {
                _applyOppStrategy(unit);
            }

            while (swapWeaponBackup.size() <= unit.id) {
                swapWeaponBackup.push_back(false);
            }
            swapWeaponBackup[unit.id] = unit.action.swapWeapon;
            if (unit.mines > 0 && unit.action.plantMine && unit.canJump && unit.isStandOnGround()) { // TODO: check is stand
                mines.emplace_back(unit.plantMine());
            }

            const TUnit* nearest = nullptr;
            double nearestDist = INT_MAX;
            for (const auto& other : units) {
                if (other.id == unit.id) {
                    continue;
                }
                auto dist = unit.getManhattanDistTo(other);
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = &other;
                }
            }
            _nearestUnitCache[unitIdx] = nearest;
        }



        for (int microTick = 0; microTick < updatesPerTick; microTick++) {
            _doMicroTick(updatesPerTick);
            _doBulletsDamageTick(updatesPerTick);
            _doMinesTick(updatesPerTick);
            _doBulletsMoveTick(updatesPerTick);
        }
        for (auto& unit : units) {
            unit.action.swapWeapon = swapWeaponBackup[unit.id];
        }
        currentTick++;
    }

    TLootBox* findLootBox(const TUnit& unit) {
        for (int i = int(unit.x1); i <= int(unit.x2); i++) {
            for (int j = int(unit.y1 + 1e-8); j <= int(unit.y2 + 1e-8); j++) {
                unsigned idx = (*lootBoxIndex)[i][j];
                if (idx != UINT32_MAX && lootBoxes[idx].type != ELootType::NONE && lootBoxes[idx].intersectsWith(unit)) {
                    return &lootBoxes[idx];
                }
            }
        }
        return nullptr;
    }

    TUnit* getUnit(int id) {
        for (auto& unit : units) {
            if (unit.id == id) {
                return &unit;
            }
        }
        return nullptr;
    }

private:
    void _applyOppStrategy(TUnit& opp) {
        if (oppShotSimpleStrategy) {
            TUnit* target = nullptr;
            double minDist2 = INT_MAX;
            for (auto &my : units) {
                if (!my.isMy()) {
                    continue;
                }
                if (target == nullptr || opp.center().getDistanceTo2(my.center()) < minDist2) {
                    minDist2 = opp.center().getDistanceTo2(my.center());
                    target = &my;
                }
            }
            if (target != nullptr) {
                opp.action.shoot = true;
                opp.action.aim = target->center() - opp.center();
            }
        }
    }

    void _blowUpMine(const TMine& mine) {
        for (auto& unit : units) {
            if (mine.isTouch(unit)) {
                auto damage = std::min(unit.health, MINE_EXPLOSION_DAMAGE);
                unit.health -= damage;
                if (unit.health <= 0) {
                    score[unit.playerIdx ^ 1] += KILL_SCORE;
                }
                if (mine.playerIdx != unit.playerIdx) {
                    score[mine.playerIdx] += damage;
                } else {
                    friendlyLoss[mine.playerIdx] += damage;
                    if (unit.health <= 0) {
                        friendlyLoss[mine.playerIdx] += KILL_SCORE;
                    }
                }
            }
        }
        for (auto& otherMine : mines) {
            if (otherMine.state != EXPLODED && mine.isTouch(otherMine)) {
                otherMine.state = EXPLODED;
                otherMine.timer = -1;
            }
        }
    }

    void _doMinesTick(int updatesPerTick) {
        for (int i = 0; i < (int) mines.size(); i++) {
            auto& mine = mines[i];
            if (mine.state == PREPARING) {
                mine.timer -= 1.0 / (updatesPerTick * TICKS_PER_SECOND);
                if (mine.timer < -1e-10) {
                    mine.timer = -1;
                    mine.state = IDLE;
                }
            } else if (mine.state == IDLE) {
                bool trigger = false;
                for (auto& unit : units) {
                    if (mine.isTriggerOn(unit)) {
                        trigger = true;
                        break;
                    }
                }
                if (trigger) {
                    mine.state = TRIGGERED;
                    mine.timer = MINE_TRIGGER_TIME;
                }
            } else if (mine.state == TRIGGERED) {
                mine.timer -= 1.0 / (updatesPerTick * TICKS_PER_SECOND);
                if (mine.timer < -1e-10) {
                    mine.state = EXPLODED;
                }
            }

            if (mine.state == EXPLODED) {
                _blowUpMine(mine);
                mines.erase(mines.begin() + i);
                i--;
            }
        }
    }

    void _doMicrotickWeapon(TUnit& unit, double updatesPerSecond) {
        auto& weapon = unit.weapon;
        if (weapon.type == ELootType::NONE) {
            weapon.fireTimer = -1;
            weapon.lastAngle = DEFAULT_LAST_ANGLE;
            return;
        }
        if (unit.action.aim.length2() >= SQR(0.5)) {
            auto newAngle = unit.action.aim.getAngle();
            if (weapon.hasLastAngle()) {
                weapon.spread = std::min(weapon.spread + std::abs(newAngle - weapon.lastAngle), WEAPON_MAX_SPREAD);
            }
            weapon.lastAngle = newAngle;
            if (unit.action.shoot && weapon.magazine > 0 && weapon.fireTimer < -0.5) {
                bullets.emplace_back(unit.shot(shotSpreadToss));
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

        for (int unitIdx = 0; unitIdx < (int) units.size(); unitIdx++) {
            auto& unit = units[unitIdx];
            auto nearest = _nearestUnitCache[unitIdx];
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
                double xMin = unit.x2 + dx;
                auto xCell = int(xMin);
                bool stopped = false;
                if (xCell < xMin) {
                    if (TLevel::tiles[xCell][int(unit.y1)] == ETile::WALL ||
                        TLevel::tiles[xCell][int(unit.y2)] == ETile::WALL ||
                        TLevel::tiles[xCell][int(unit.y1 + UNIT_HALF_HEIGHT)] == ETile::WALL) {

                        xMin = xCell;
                        stopped = true;
                    }
                }
                if (nearest != nullptr && nearest->x1 < xMin && unit.x2 <= nearest->x1 && unit.x2 + dx > nearest->x1 && unit.intersectsWithByY(*nearest)) {
                    xMin = nearest->x1;
                    stopped = true;
                }
                unit.x2 = std::max(unit.x2, xMin - stopped * FUCKING_EPS);
                unit.x1 = unit.x2 - UNIT_WIDTH;
            } else if (action.velocity < 0) {
                double xMax = unit.x1 + dx;
                int xCell = int(xMax);
                bool stopped = false;
                if (xCell + 1 > xMax) {
                    if (TLevel::tiles[xCell][int(unit.y1)] == ETile::WALL ||
                        TLevel::tiles[xCell][int(unit.y2)] == ETile::WALL ||
                        TLevel::tiles[xCell][int(unit.y1 + UNIT_HALF_HEIGHT)] == ETile::WALL) {

                        xMax = xCell + 1;
                        stopped = true;
                    }
                }
                if (nearest != nullptr && nearest->x2 > xMax && unit.x1 >= nearest->x2 && unit.x1 + dx < nearest->x2 && unit.intersectsWithByY(*nearest)) {
                    xMax = nearest->x2;
                    stopped = true;
                }
                unit.x1 = std::min(unit.x1, xMax + stopped * FUCKING_EPS);
                unit.x2 = unit.x1 + UNIT_WIDTH;
            }

            if (unit.canJump && (action.jump || !unit.jumpCanCancel)) {
                double dy = (unit.jumpCanCancel ? UNIT_JUMP_SPEED : JUMP_PAD_JUMP_SPEED) / updatesPerSecond;

                double yMin = unit.y2 + dy;
                int yCell = int(yMin);
                bool stopped = false;
                if (yCell < yMin) {
                    if (TLevel::tiles[int(unit.x1)][yCell] == ETile::WALL || TLevel::tiles[int(unit.x2)][yCell] == ETile::WALL) {
                        yMin = yCell;
                        stopped = true;
                    }
                }
                if (nearest != nullptr && nearest->y1 < yMin && unit.y2 <= nearest->y1 && unit.y2 + dy > nearest->y1 && unit.intersectsWithByX(*nearest)) {
                    yMin = nearest->y1;
                    stopped = true;
                }

                if (stopped) {
                    unit.jumpMaxTime = 0;
                    unit.canJump = false;
                    unit.jumpCanCancel = false;
                    unit.y2 = std::max(unit.y2, yMin - FUCKING_EPS);
                    unit.y1 = unit.y2 - UNIT_HEIGHT;
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
            } else if ((action.jumpDown || !onLadder) && (!oppFallFreeze || unit.isMy() || !unit.canJump)) {
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
                double yMax = unit.y1 + dy;
                bool stopped = false;
                if (ycell + 1 > yMax) {
                    if (((uint32_t)TLevel::tiles[int(unit.x1)][ycell] & wallMask) ||
                        ((uint32_t)TLevel::tiles[int(unit.x2)][ycell] & wallMask)) {

                        stopped = true;
                        yMax = ycell + 1;
                    }
                }
                if (nearest != nullptr && nearest->y2 > yMax && unit.y1 >= nearest->y2 && unit.y1 + dy < nearest->y2 && unit.intersectsWithByX(*nearest)) {
                    stopped = true;
                    yMax = nearest->y2;
                }


                if (stopped) {
                    unit.jumpMaxTime = UNIT_JUMP_TIME;
                    unit.canJump = true;
                    unit.jumpCanCancel = true;
                    unit.y1 = std::min(unit.y1, yMax + FUCKING_EPS);
                    unit.y2 = unit.y1 + UNIT_HEIGHT;
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

            if (unit.isTouchJumpPad()) {
                unit.jumpCanCancel = false;
                unit.canJump = true;
                unit.jumpMaxTime = JUMP_PAD_JUMP_TIME;
            }
        }
    }

    void _doBulletsDamageTick(int updatesPerTick) {
        for (int i = 0; i < (int) bullets.size(); i++) {
            auto& bullet = bullets[i];
            if (_collideBulletAndUnits(bullet) || _collideBulletAndMines(bullet)) {
                _applyRocketExplosionDamage(bullet);
                bullets.erase(bullets.begin() + i);
                i--;
            }
        }
    }

    void _doBulletsMoveTick(int updatesPerTick) {
        auto updatesPerSecond = updatesPerTick * TICKS_PER_SECOND;
        for (int i = 0; i < (int) bullets.size(); i++) {
            auto& bullet = bullets[i];
            auto dx = bullet.velocity.x / updatesPerSecond;
            auto dy = bullet.velocity.y / updatesPerSecond;
            bullet.x1 += dx;
            bullet.x2 += dx;
            bullet.y1 += dy;
            bullet.y2 += dy;

            if (bullet.isInWall()) {
                _applyRocketExplosionDamage(bullet);
                bullets.erase(bullets.begin() + i);
                i--;
            }
        }
    }

    void _applyRocketExplosionDamage(const TBullet& bullet) {
        if (bullet.weaponType == ELootType::ROCKET_LAUNCHER) {
            for (auto& unit : units) {
                if (bullet.isRocketLauncherExplosionTouch(unit)) {
                    auto damage = std::min(unit.health, ROCKET_LAUNCHER_EXPLOSION_DAMAGE);
                    unit.health -= damage;
                    if (unit.health <= 0) {
                        score[unit.playerIdx ^ 1] += KILL_SCORE;
                    }
                    if (bullet.playerIdx() != unit.playerIdx) {
                        score[bullet.playerIdx()] += damage;
                    } else {
                        friendlyLoss[bullet.playerIdx()] += damage;
                        if (unit.health <= 0) {
                            friendlyLoss[unit.playerIdx] += KILL_SCORE;
                        }
                    }
                }
            }
            for (auto& mine : mines) {
                if (bullet.isRocketLauncherExplosionTouch(mine)) {
                    mine.state = EXPLODED;
                    //mine.timer = -1;
                }
            }
        }
    }

    bool _collideBulletAndUnits(const TBullet& bullet) {
        for (auto& unit : units) {
            if (unit.id != bullet.unitId && unit.intersectsWith(bullet)) {
                auto damage = std::min(unit.health, bullet.damage());
                unit.health -= damage;
                if (unit.health <= 0) {
                    score[unit.playerIdx ^ 1] += KILL_SCORE;
                }
                if (unit.playerIdx != bullet.playerIdx()) {
                    score[bullet.playerIdx()] += damage;
                } else {
                    friendlyLoss[bullet.playerIdx()] += damage;
                    if (unit.health <= 0) {
                        friendlyLoss[unit.playerIdx] += KILL_SCORE;
                    }
                }
                return true;
            }
        }
        return false;
    }

    bool _collideBulletAndMines(const TBullet& bullet) {
        for (auto& mine : mines) {
            if (mine.intersectsWith(bullet)) {
                mine.state = EXPLODED;
                return true;
            }
        }
        return false;
    }
};

#endif //CODESIDE_SANDBOX_H
