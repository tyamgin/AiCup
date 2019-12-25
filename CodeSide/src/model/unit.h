#ifndef CODESIDE_UNIT_H
#define CODESIDE_UNIT_H

#include "../constants.h"
#include "point.h"
#include "rectangle.h"
#include "level.h"
#include "action.h"
#include "lootbox.h"
#include "weapon.h"
#include "mine.h"

class TUnit : public TRectangle {
public:
    int playerIdx;
    int id;
    int health;

    bool canJump;
    double jumpMaxTime;
    bool jumpCanCancel;
    int mines;

    TWeapon weapon;
    TAction action;

    TUnit() : TRectangle(0, 0, 0, 0) {
        playerIdx = 0;
        id = -1;
        health = 0;
        canJump = false;
        canJump = false;
        jumpMaxTime = 0;
        jumpCanCancel = false;
        mines = 0;
    }

    explicit TUnit(const Unit& unit) : TRectangle(unit.position, UNIT_WIDTH, UNIT_HEIGHT) {
        playerIdx = unit.playerId == TLevel::myId ? 0 : 1;
        id = unit.id;
        health = unit.health;
        canJump = unit.jumpState.canJump;
        jumpMaxTime = unit.jumpState.maxTime;
        jumpCanCancel = unit.jumpState.canCancel;
        mines = unit.mines;
        if (unit.weapon) {
            weapon = TWeapon(*unit.weapon);
        }
    }

    TUnit(const TUnit& unit) : TRectangle(unit) {
        playerIdx = unit.playerIdx;
        id = unit.id;
        health = unit.health;
        canJump = unit.canJump;
        jumpMaxTime = unit.jumpMaxTime;
        jumpCanCancel = unit.jumpCanCancel;
        mines = unit.mines;
        weapon = unit.weapon;
        action = unit.action;
    }

    bool isOnLadder(double dy = 0) const {
        double x = x1 + UNIT_HALF_WIDTH;
        return TLevel::getTileType(x, y1 + dy) == ETile::LADDER ||
               TLevel::getTileType(x, y1 + dy + UNIT_HALF_HEIGHT) == ETile::LADDER;
    }

    bool isStandOnLadder(double dy) const {
        double x = x1 + UNIT_HALF_WIDTH;
        return !(TLevel::getTileType(x, y1) == ETile::LADDER || TLevel::getTileType(x, y1 + UNIT_HALF_HEIGHT) == ETile::LADDER) &&
               (TLevel::getTileType(x, y1 + dy) == ETile::LADDER || TLevel::getTileType(x, y1 + dy + UNIT_HALF_HEIGHT) == ETile::LADDER);
    }

    bool isStandOnGround() const {
        return (!tileMatch(TLevel::getTileType(x1, y1), ETile::WALL, ETile::PLATFORM) && tileMatch(TLevel::getTileType(x1, y1 - 1e-8), ETile::WALL, ETile::PLATFORM)) ||
               (!tileMatch(TLevel::getTileType(x2, y1), ETile::WALL, ETile::PLATFORM) && tileMatch(TLevel::getTileType(x2, y1 - 1e-8), ETile::WALL, ETile::PLATFORM));
    }

    void maybeApplyLoot(TLootBox& loot) {
        if (loot.type != ELootType::NONE && intersectsWith(loot)) {
            applyLoot(loot);
        }
    }

    void applyLoot(TLootBox& loot) {
        switch (loot.type) {
            case ELootType::HEALTH_PACK:
                if (health < UNIT_MAX_HEALTH) {
                    health = std::min(health + HEALTH_PACK_HEALTH, UNIT_MAX_HEALTH);
                    loot.type = ELootType::NONE;
                }
                return;
            case ELootType::MINE:
                mines++;
                loot.type = ELootType::NONE;
                return;
            default: // weapon
                if (weapon.type == ELootType::NONE) {
                    weapon = TWeapon(loot.type);
                    loot.type = ELootType::NONE;
                    return;
                }
                if (action.swapWeapon) {
                    auto oldType = weapon.type;
                    weapon = TWeapon(loot.type);
                    loot.type = oldType;
                    action.swapWeapon = false;
                }
                return;
        }
    }

    TMine plantMine() {
        mines--;
        TMine mine;
        mine.playerIdx = playerIdx;
        mine.x1 = x1 + UNIT_HALF_WIDTH - MINE_SIZE / 2;
        mine.x2 = mine.x1 + MINE_SIZE;
        mine.y1 = y1;
        mine.y2 = mine.y1 + MINE_SIZE;
        return mine;
    }

    bool approxIdValid(double dx = 0, double dy = 0) const {
        for (int i = int(x1 + dx); i <= int(x2 + dx); i++) {
            for (int j = int(y1 + dy); j <= int(y2 + dy); j++) {
                if (getTile(i, j) == ETile::WALL) {
                    return false;
                }
            }
        }
        return true;
    }

    bool isTouchJumpPad(double dx = 0, double dy = 0) const {
        for (int i = int(x1 + dx); i <= int(x2 + dx); i++) {
            for (int j = int(y1 + dy); j <= int(y2 + dy); j++) {
                if (TLevel::tiles[i][j] == ETile::JUMP_PAD) {
                    return true;
                }
            }
        }
        return false;
    }

    bool approxIsStand() const {
        if (isOnLadder() || isOnLadder(-1e-8)) {
            return true;
        }
        return (TLevel::getTileType(x1 + 1e-8, y1) == ETile::EMPTY && tileMatch(TLevel::getTileType(x1 + 1e-8, y1 - 1e-8), ETile::WALL, ETile::PLATFORM)) ||
               (TLevel::getTileType(x2 - 1e-8, y1) == ETile::EMPTY && tileMatch(TLevel::getTileType(x2 - 1e-8, y1 - 1e-8), ETile::WALL, ETile::PLATFORM));
    }

    bool approxIsStandGround(double dx = 0) const {
        return (!tileMatch(TLevel::getTileType(x1 + dx + 1e-8, y1), ETile::WALL, ETile::PLATFORM) && tileMatch(TLevel::getTileType(x1 + dx + 1e-8, y1 - 1e-8), ETile::WALL, ETile::PLATFORM)) ||
               (!tileMatch(TLevel::getTileType(x2 + dx - 1e-8, y1), ETile::WALL, ETile::PLATFORM) && tileMatch(TLevel::getTileType(x2 + dx - 1e-8, y1 - 1e-8), ETile::WALL, ETile::PLATFORM));
    }

    TPoint center() const {
        return TPoint(x1 + UNIT_HALF_WIDTH, y1 + UNIT_HALF_HEIGHT);
    }

    TPoint position() const {
        return TPoint(x1 + UNIT_HALF_WIDTH, y1);
    }

    TBullet shot(double shotSpreadToss) {
        TBullet res;
        res.weaponType = weapon.type;
        res.unitId = id;
        weapon.magazine -= 1;
        TPoint aim = shotSpreadToss == 0 ? action.aim : action.aim.rotatedClockwise(shotSpreadToss * weapon.spread);
        aim.normalize();
        switch (weapon.type) {
            case ELootType::PISTOL:
                if (weapon.magazine == 0) {
                    weapon.magazine = PISTOL_MAGAZINE_SIZE;
                    weapon.fireTimer = WEAPON_RELOAD_TIME;
                } else {
                    weapon.fireTimer = PISTOL_FIRE_RATE;
                }
                weapon.spread += PISTOL_RECOIL;
                res.x1 = x1 + (UNIT_HALF_WIDTH - PISTOL_BULLET_SIZE / 2);
                res.x2 = res.x1 + PISTOL_BULLET_SIZE;
                res.y1 = y1 + (UNIT_HALF_HEIGHT - PISTOL_BULLET_SIZE / 2);
                res.y2 = res.y1 + PISTOL_BULLET_SIZE;
                res.velocity = aim * PISTOL_BULLET_SPEED;
                break;
            case ELootType::ASSAULT_RIFLE:
                if (weapon.magazine == 0) {
                    weapon.magazine = ASSAULT_RIFLE_MAGAZINE_SIZE;
                    weapon.fireTimer = WEAPON_RELOAD_TIME;
                } else {
                    weapon.fireTimer = ASSAULT_RIFLE_FIRE_RATE;
                }
                weapon.spread += ASSAULT_RIFLE_RECOIL;
                res.x1 = x1 + (UNIT_HALF_WIDTH - ASSAULT_RIFLE_BULLET_SIZE / 2);
                res.x2 = res.x1 + ASSAULT_RIFLE_BULLET_SIZE;
                res.y1 = y1 + (UNIT_HALF_HEIGHT - ASSAULT_RIFLE_BULLET_SIZE / 2);
                res.y2 = res.y1 + ASSAULT_RIFLE_BULLET_SIZE;
                res.velocity = aim * ASSAULT_RIFLE_BULLET_SPEED;
                break;
            default:
                if (weapon.magazine == 0) {
                    weapon.magazine = ROCKET_LAUNCHER_MAGAZINE_SIZE;
                    weapon.fireTimer = WEAPON_RELOAD_TIME;
                } else {
                    weapon.fireTimer = ROCKET_LAUNCHER_FIRE_RATE;
                }
                weapon.spread += ROCKET_LAUNCHER_RECOIL;
                res.x1 = x1 + (UNIT_HALF_WIDTH - ROCKET_LAUNCHER_BULLET_SIZE / 2);
                res.x2 = res.x1 + ROCKET_LAUNCHER_BULLET_SIZE;
                res.y1 = y1 + (UNIT_HALF_HEIGHT - ROCKET_LAUNCHER_BULLET_SIZE / 2);
                res.y2 = res.y1 + ROCKET_LAUNCHER_BULLET_SIZE;
                res.velocity = aim * ROCKET_LAUNCHER_BULLET_SPEED;
                break;
        }
        if (weapon.spread > WEAPON_MAX_SPREAD) {
            weapon.spread = WEAPON_MAX_SPREAD;
        }
        return res;
    }

    bool isPadFly() const {
        return canJump && !jumpCanCancel;
    }

    bool isStand() const {
        return canJump && jumpMaxTime >= UNIT_JUMP_TIME - 1e-10;
    }

    bool intersectsWithByY(const TUnit& other) const {
        return other.y1 < y2 && other.y2 > y1;
    }

    bool intersectsWithByX(const TUnit& other) const {
        return other.x1 < x2 && other.x2 > x1;
    }

    double getManhattanDistTo(const TUnit& other) const {
        return std::abs(x1 - other.x1) + std::abs(y1 - other.y1);
    }

    bool isNearBullet(const TBullet& bullet) const {
        const double d = 1.2;
        return bullet.x1 < x2 + d && bullet.x2 > x1 - d &&
               bullet.y1 < y2 + d && bullet.y2 > y1 - d;
    }

    double getDistanceTo(const TUnit& unit) const {
        return sqrt(SQR(x1 - unit.x1) + SQR(y1 - unit.y1));
    }

    double getDistanceTo2(const TUnit& unit) const {
        return SQR(x1 - unit.x1) + SQR(y1 - unit.y1);
    }

    bool canShotInCurrentTick() const {
        auto updatesPerSecond = UPDATES_PER_TICK * TICKS_PER_SECOND;
        auto dTimerInMicrotick = WEAPON_RELOAD_TIME / updatesPerSecond;
        return weapon.fireTimer - (UPDATES_PER_TICK - 1) * dTimerInMicrotick < 0;
    }

    bool isMagazineFull() const {
        if (weapon.type == ELootType::PISTOL) {
            return weapon.magazine == PISTOL_MAGAZINE_SIZE;
        }
        if (weapon.type == ELootType::ASSAULT_RIFLE) {
            return weapon.magazine == ASSAULT_RIFLE_MAGAZINE_SIZE;
        }
        if (weapon.type == ELootType::ROCKET_LAUNCHER) {
            return weapon.magazine == ROCKET_LAUNCHER_MAGAZINE_SIZE;
        }
        return true;
    }

    bool isMy() const {
        return playerIdx == 0;
    }

    bool noWeapon() const {
        return weapon.type == ELootType::NONE;
    }

    int getPossibleShotDamage() const {
        switch (weapon.type) {
            case ELootType::ROCKET_LAUNCHER:
                return ROCKET_LAUNCHER_EXPLOSION_DAMAGE + ROCKET_LAUNCHER_BULLET_DAMAGE;
            case ELootType::PISTOL:
                return PISTOL_BULLET_DAMAGE;
            case ELootType::ASSAULT_RIFLE:
                return ASSAULT_RIFLE_BULLET_DAMAGE;
            default:
                return 0;
        }
    }

    void setPosition(const TPoint& pos) {
        x1 = pos.x - UNIT_HALF_WIDTH;
        x2 = x1 + UNIT_WIDTH;
        y1 = pos.y;
        y2 = y1 + UNIT_HEIGHT;
    }
};


#endif //CODESIDE_UNIT_H
