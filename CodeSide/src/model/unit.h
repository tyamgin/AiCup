#ifndef CODESIDE_UNIT_H
#define CODESIDE_UNIT_H

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
    //double jumpSpeed;
    double jumpMaxTime;
    bool jumpCanCancel;

    //bool walkedRight;
    //bool stand;
    //bool onGround;
    bool onLadder;
    int mines;

    TWeapon weapon;
    TAction action;

    explicit TUnit(const Unit& unit) : TRectangle(unit.position, UNIT_SIZE_X, UNIT_SIZE_Y) {
        playerId = unit.playerId;
        id = unit.id;
        health = unit.health;
        canJump = unit.jumpState.canJump;
        //jumpSpeed = unit.jumpState.speed;
        jumpMaxTime = unit.jumpState.maxTime;
        jumpCanCancel = unit.jumpState.canCancel;
        //walkedRight = unit.walkedRight;
        //stand = unit.stand;
        //onGround = unit.onGround;
        onLadder = unit.onLadder;
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
        //jumpSpeed = unit.jumpSpeed;
        jumpMaxTime = unit.jumpMaxTime;
        jumpCanCancel = unit.jumpCanCancel;
        //walkedRight = unit.walkedRight;
        //stand = unit.stand;
        //onGround = unit.onGround;
        onLadder = unit.onLadder;
        mines = unit.mines;
        weapon = unit.weapon;
        action = unit.action;
    }

    bool isOnLadder() const {
        double x = (x1 + x2) / 2;
        double y = (y1 + y2) / 2;
        return TLevel::getTileType(x, y) == LADDER || TLevel::getTileType(x, y2) == LADDER;
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
};


#endif //CODESIDE_UNIT_H
