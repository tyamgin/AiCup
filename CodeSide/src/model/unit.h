#ifndef CODESIDE_UNIT_H
#define CODESIDE_UNIT_H

#include "point.h"
#include "rectangle.h"

class TUnit : public TRectangle {
public:
    int playerId;
    int id;
    int health;

    bool canJump;
    double jumpSpeed;
    double jumpMaxTime;
    bool jumpCanCancel;

    bool walkedRight;
    bool stand;
    bool onGround;
    bool onLadder;
    int mines;

    std::optional<Weapon> weapon;

    explicit TUnit(const Unit& unit) : TRectangle(unit.position, UNIT_SIZE_X, UNIT_SIZE_Y) {
        playerId = unit.playerId;
        id = unit.id;
        health = unit.health;
        canJump = unit.jumpState.canJump;
        jumpSpeed = unit.jumpState.speed;
        jumpMaxTime = unit.jumpState.maxTime;
        jumpCanCancel = unit.jumpState.canCancel;
        walkedRight = unit.walkedRight;
        stand = unit.stand;
        onGround = unit.onGround;
        onLadder = unit.onLadder;
        mines = unit.mines;
    }

    TUnit(const TUnit& unit) : TRectangle(unit) {
        playerId = unit.playerId;
        id = unit.id;
        health = unit.health;
        canJump = unit.canJump;
        jumpSpeed = unit.jumpSpeed;
        jumpMaxTime = unit.jumpMaxTime;
        jumpCanCancel = unit.jumpCanCancel;
        walkedRight = unit.walkedRight;
        stand = unit.stand;
        onGround = unit.onGround;
        onLadder = unit.onLadder;
        mines = unit.mines;
    }
};


#endif //CODESIDE_UNIT_H
