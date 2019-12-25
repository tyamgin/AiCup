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

#define MAX_UNITS_COUNT 4

struct TSandboxCloneOptions {
    std::vector<int> unitIds = {-1};
    bool needLootboxes = true;
    bool needMines = true;
    bool needBullets = true;
};

class TSandbox {
private:
    static std::shared_ptr<std::vector<std::vector<unsigned>>> _emptyBonusIndex;
    const TUnit* _nearestUnitCache[MAX_UNITS_COUNT] = {nullptr};
    std::vector<int> _unitNearestBulletCache[MAX_UNITS_COUNT];
    std::vector<std::vector<int>> _mineNearestBulletCache;
    std::shared_ptr<TActionsVec> _unitActionsSuggests[MAX_UNITS_COUNT] = {nullptr};
    int _unitActionsSuggestsOffset[MAX_UNITS_COUNT] = {0};
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
    std::pair<int, int> oppShotSimpleStrategy = {-1, -1};
    double shotSpreadToss = 0;
    bool oppFallFreeze = false;
    bool oppTotalFreeze = false;

    TSandbox() {
        currentTick = -1;
        myCount = 0;
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
        for (int i = 0; i < (int) units.size(); i++) {
            _unitActionsSuggests[i] = nullptr;
            _unitActionsSuggestsOffset[i] = 0;
        }
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
        oppTotalFreeze = sandbox.oppTotalFreeze;
        myCount = sandbox.myCount;
        for (int i = 0; i < (int) units.size(); i++) {
            _unitActionsSuggests[i] = sandbox._unitActionsSuggests[i];
            _unitActionsSuggestsOffset[i] = sandbox._unitActionsSuggestsOffset[i];
        }
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
        oppTotalFreeze = sandbox.oppTotalFreeze;
        myCount = sandbox.myCount;
        for (int i = 0; i < (int) units.size(); i++) {
            _unitActionsSuggests[i] = sandbox._unitActionsSuggests[i];
            _unitActionsSuggestsOffset[i] = sandbox._unitActionsSuggestsOffset[i];
        }
    }

    void doTick(int updatesPerTick = UPDATES_PER_TICK) {
        std::vector<bool> swapWeaponBackup;
        for (int unitIdx = 0; unitIdx < (int) units.size(); unitIdx++) {
            auto& unit = units[unitIdx];

            _applySuggestedStrategy(unitIdx);
            _applyOppStrategy(unit);

            while (swapWeaponBackup.size() <= unit.id) {
                swapWeaponBackup.push_back(false);
            }
            swapWeaponBackup[unit.id] = unit.action.swapWeapon;
            if (unit.health > 0 && unit.mines > 0 && unit.action.plantMine && unit.canJump && unit.isStandOnGround()) { // TODO: check is stand
                // NOTE: _mineNearestBulletCache заполняется после
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

            _unitNearestBulletCache[unitIdx].clear();
            for (int bulletIdx = 0; bulletIdx < (int) bullets.size(); bulletIdx++) {
                const auto& bullet = bullets[bulletIdx];
                if (bullet.unitId != unit.id && unit.isNearBullet(bullet)) {
                    _unitNearestBulletCache[unitIdx].push_back(bulletIdx);
                }
            }
        }

        _mineNearestBulletCache.resize(mines.size());
        for (int mineIdx = 0; mineIdx < (int) mines.size(); mineIdx++) {
            _mineNearestBulletCache[mineIdx].clear();
            auto& mine = mines[mineIdx];
            for (int bulletIdx = 0; bulletIdx < (int) bullets.size(); bulletIdx++) {
                const auto& bullet = bullets[bulletIdx];
                if (mine.isNearBullet(bullet)) {
                    _mineNearestBulletCache[mineIdx].push_back(bulletIdx);
                }
            }
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
        for (int bulletIdx = (int) bullets.size() - 1; bulletIdx >= 0; bulletIdx--) {
            if (bullets[bulletIdx].weaponType == ELootType::NONE) {
                bullets.erase(bullets.begin() + bulletIdx);
            }
        }
        for (int mineIdx = (int) mines.size() - 1; mineIdx >= 0; mineIdx--) {
            if (mines[mineIdx].state == NONE) {
                mines.erase(mines.begin() + mineIdx);
            }
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

    void setUnitActionsSuggest(int unitId, const std::shared_ptr<TActionsVec>& actions) {
        for (int i = 0; i < (int) units.size(); i++) {
            if (units[i].id == unitId) {
                _unitActionsSuggests[i] = actions;
                _unitActionsSuggestsOffset[i] = 0;
                break;
            }
        }
    }

    void setUnitActionsSuggest(int unitId, const TAction& actions, int ticksCount) {
        for (int i = 0; i < (int) units.size(); i++) {
            if (units[i].id == unitId) {
                _unitActionsSuggests[i] = std::make_shared<TActionsVec>(ticksCount, actions);
                _unitActionsSuggestsOffset[i] = 0;
                break;
            }
        }
    }

    bool _isWin(int playerIdx) const {
        if (score[playerIdx] <= score[1 - playerIdx] || score[playerIdx] < TLevel::teamSize * KILL_SCORE) {
            return false;
        }
        for (const auto& unit : units) {
            if (unit.playerIdx != playerIdx && unit.health > 0) {
                return false;
            }
        }
        return true;
    }

    bool isWin() const {
        return _isWin(0);
    }

    bool isLose() const {
        return _isWin(1);
    }

private:
    void _applySuggestedStrategy(int unitIdx) {
        if (_unitActionsSuggests[unitIdx] == nullptr) {
            return;
        }
        auto& offset = _unitActionsSuggestsOffset[unitIdx];
        if (offset < _unitActionsSuggests[unitIdx]->size()) {
            units[unitIdx].action = (*_unitActionsSuggests[unitIdx])[offset];
            offset++;
        }
    }

    void _applyOppStrategy(TUnit& opp) {
        if (oppShotSimpleStrategy.first == -1
            || oppShotSimpleStrategy.first == opp.id
            || oppShotSimpleStrategy.second < currentTick) {
            return;
        }

        if (opp.weapon.isRocketLauncher()) {
            TUnit* target = nullptr;
            double minDist2 = SQR(3.5);
            for (auto &my : units) {
                if (my.playerIdx == opp.playerIdx) {
                    continue;
                }
                auto dst2 = opp.getDistanceTo2(my);
                if (dst2 < minDist2) {
                    minDist2 = dst2;
                    target = &my;
                }
            }
            if (target != nullptr) {
                opp.action.shoot = true;
                if (!opp.isMy()) {
                    opp.action.aim = target->center() - opp.center();
                }
            }
        }
    }

    void _blowUpMine(const TMine& mine) {
        for (auto& unit : units) {
            // HACK: чтобы случайно не убиться самому
            double mineRadiusChangeHack = unit.isMy() ? 0 : -0.16667; // чуть больше, чем смещение на 1 тик
            if (mine.isTouch(unit, mineRadiusChangeHack)) {
                auto damage = std::min(unit.health, MINE_EXPLOSION_DAMAGE);
                unit.health -= damage;
                if (unit.health <= 0) {
                    score[1 - unit.playerIdx] += KILL_SCORE;
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
            if (otherMine.state != NONE && otherMine.state != EXPLODED && mine.isTouch(otherMine)) {
                otherMine.state = EXPLODED;
                otherMine.timer = -1;
            }
        }
    }

    void _doMinesTick(int updatesPerTick) {
        for (auto& mine : mines) {
            if (mine.state == NONE) {
                continue;
            }

            if (mine.state == PREPARING) {
                mine.timer -= 1.0 / (updatesPerTick * TICKS_PER_SECOND);
                if (mine.timer < -1e-10) {
                    mine.timer = -1;
                    mine.state = IDLE;
                }
            } else if (mine.state == IDLE) {
                bool trigger = false;
                for (auto& unit : units) {
                    if (unit.health > 0 && mine.isTriggerOn(unit)) {
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
                mine.state = NONE;
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
            if (unit.health > 0 && unit.action.shoot && weapon.magazine > 0 && weapon.fireTimer < -0.5) {
                auto newBullet = unit.shot(shotSpreadToss);
                for (int unitIdx = 0; unitIdx < (int) units.size(); unitIdx++) {
                    if (newBullet.unitId != units[unitIdx].id && units[unitIdx].isNearBullet(newBullet)) {
                        _unitNearestBulletCache[unitIdx].push_back((int) bullets.size());
                    }
                }
                for (int mineIdx = 0; mineIdx < (int) mines.size(); mineIdx++) {
                    if (mines[mineIdx].isNearBullet(newBullet)) {
                        _mineNearestBulletCache[mineIdx].push_back((int) bullets.size());
                    }
                }
                bullets.emplace_back(newBullet);
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
            if (oppTotalFreeze && !unit.isMy()) {
                continue;
            }
            if (unit.health <= 0) {
                continue;
            }

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
            } else if (action.jumpDown || !onLadder) {
                if (!oppFallFreeze || unit.isMy() || !unit.canJump) {
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

                    int yCell = int(unit.y1 + dy);
                    double yMax = unit.y1 + dy;
                    bool stopped = false;
                    if (yCell + 1 > yMax) {
                        if (((uint32_t)TLevel::tiles[int(unit.x1)][yCell] & wallMask) ||
                            ((uint32_t)TLevel::tiles[int(unit.x2)][yCell] & wallMask)) {

                            stopped = true;
                            yMax = yCell + 1;
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
        for (int unitIdx = 0; unitIdx < (int) units.size(); unitIdx++) {
            auto& unit = units[unitIdx];
            if (unit.health <= 0) {
                continue;
            }
            for (int bulletIdx : _unitNearestBulletCache[unitIdx]) {
                auto& bullet = bullets[bulletIdx];
                if (bullet.weaponType != ELootType::NONE && _collideBulletAndUnit(unit, bullet)) {
                    _applyRocketExplosionDamage(bullet);
                    bullet.weaponType = ELootType::NONE;
                }
            }
        }

        for (int mineIdx = 0; mineIdx < (int) mines.size(); mineIdx++) {
            auto& mine = mines[mineIdx];
            if (mine.state == NONE) {
                continue;
            }
            for (int bulletIdx : _mineNearestBulletCache[mineIdx]) {
                auto& bullet = bullets[bulletIdx];
                if (bullet.weaponType != ELootType::NONE && mine.intersectsWith(bullet)) {
                    mine.state = EXPLODED;
                    _applyRocketExplosionDamage(bullet);
                    bullet.weaponType = ELootType::NONE;
                    break;
                }
            }
        }
    }

    void _doBulletsMoveTick(int updatesPerTick) {
        auto updatesPerSecond = updatesPerTick * TICKS_PER_SECOND;
        for (auto& bullet : bullets) {
            if (bullet.weaponType == ELootType::NONE) {
                continue;
            }
            auto dx = bullet.velocity.x / updatesPerSecond;
            auto dy = bullet.velocity.y / updatesPerSecond;
            bullet.x1 += dx;
            bullet.x2 += dx;
            bullet.y1 += dy;
            bullet.y2 += dy;

            if (bullet.isInWall()) {
                _applyRocketExplosionDamage(bullet);
                bullet.weaponType = ELootType::NONE;
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
                        score[1 - unit.playerIdx] += KILL_SCORE;
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
                if (mine.state != NONE && bullet.isRocketLauncherExplosionTouch(mine)) {
                    mine.state = EXPLODED;
                }
            }
        }
    }

    bool _collideBulletAndUnit(TUnit& unit, const TBullet& bullet) {
        if (unit.id != bullet.unitId && unit.intersectsWith(bullet)) {
            auto damage = std::min(unit.health, bullet.damage());
            unit.health -= damage;
            if (unit.health <= 0) {
                score[1 - unit.playerIdx] += KILL_SCORE;
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
        return false;
    }
};

#endif //CODESIDE_SANDBOX_H
