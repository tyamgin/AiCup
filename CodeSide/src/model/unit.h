#ifndef CODESIDE_UNIT_H
#define CODESIDE_UNIT_H

#include "../constants.h"
#include "point.h"
#include "rectangle.h"
#include "level.h"
#include "action.h"
#include "lootbox.h"
#include "weapon.h"

class TUnit : public TRectangle {
public:
    int playerId;
    int id;
    int health;

    bool canJump;
    double jumpMaxTime;
    bool jumpCanCancel;
    int mines;

    TWeapon weapon;
    TAction action;

    TUnit() : TRectangle(0, 0, 0, 0) {
        playerId = -1;
        id = -1;
        health = 0;
        canJump = false;
        canJump = false;
        jumpMaxTime = 0;
        jumpCanCancel = 0;
        mines = 0;
    }

    explicit TUnit(const Unit& unit) : TRectangle(unit.position, UNIT_SIZE_X, UNIT_SIZE_Y) {
        playerId = unit.playerId;
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
        playerId = unit.playerId;
        id = unit.id;
        health = unit.health;
        canJump = unit.canJump;
        jumpMaxTime = unit.jumpMaxTime;
        jumpCanCancel = unit.jumpCanCancel;
        mines = unit.mines;
        weapon = unit.weapon;
        action = unit.action;
    }

    bool isOnLadder() const {
        double x = x1 + UNIT_HALF_WIDTH;
        return TLevel::getTileType(x, y1) == ETile::LADDER ||
               TLevel::getTileType(x, y1 + UNIT_HALF_HEIGHT) == ETile::LADDER;
    }

    bool isStandOnLadder(double dy) const {
        double x = x1 + UNIT_HALF_WIDTH;
        return !(TLevel::getTileType(x, y1) == ETile::LADDER || TLevel::getTileType(x, y1 + UNIT_HALF_HEIGHT) == ETile::LADDER) &&
               (TLevel::getTileType(x, y1 + dy) == ETile::LADDER || TLevel::getTileType(x, y1 + dy + UNIT_HALF_HEIGHT) == ETile::LADDER);
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
                    health = UNIT_MAX_HEALTH;
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
                }
                return;
        }
    }

    bool approxIdValid() const {
        const double px = 1 / 6.0;
        for (int i = int(x1 - px); i <= int(x2 + px); i++) {
            for (int j = int(y1); j <= int(y2 + px); j++) {
                if (getTile(i, j) == ETile::WALL) {
                    return false;
                }
            }
        }
        return true;
    }

    bool approxIsStand() const {
        if (isOnLadder()) {
            return true;
        }
        return (TLevel::getTileType(x1 + 1e-8, y1) == ETile::EMPTY && TLevel::getTileType(x1 + 1e-8, y1 - 1e-8) != ETile::EMPTY) ||
               (TLevel::getTileType(x2 - 1e-8, y1) == ETile::EMPTY && TLevel::getTileType(x2 - 1e-8, y1 - 1e-8) != ETile::EMPTY);
    }

    TPoint center() const {
        return TPoint(x1 + UNIT_HALF_WIDTH, y1 + UNIT_HALF_HEIGHT);
    }

    TBullet shot() {
        TBullet res;
        res.weaponType = weapon.type;
        res.unitId = id;
        weapon.magazine -= 1;
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
                res.velocity = action.aim.normalized() * PISTOL_BULLET_SPEED;
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
                res.velocity = action.aim.normalized() * ASSAULT_RIFLE_BULLET_SPEED;
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
                res.velocity = action.aim.normalized() * ROCKET_LAUNCHER_BULLET_SPEED;
                break;
        }
        if (weapon.spread > WEAPON_MAX_SPREAD) {
            weapon.spread = WEAPON_MAX_SPREAD;
        }
        return res;
    }
};


#endif //CODESIDE_UNIT_H
