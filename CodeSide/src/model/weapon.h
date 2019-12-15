#ifndef CODESIDE_WEAPON_H
#define CODESIDE_WEAPON_H

#include "bullet.h"

#define DEFAULT_LAST_ANGLE 1000

class TWeapon {
public:
    ELootType type;
    int magazine;
    double spread;
    double fireTimer; // -1
    double lastAngle; // DEFAULT_LAST_ANGLE
    int lastFireTick; // -1

    TWeapon() {
        type = ELootType::NONE;
        magazine = 0;
        //wasShooting = false;
        spread = 0;
        fireTimer = -1;
        lastAngle = DEFAULT_LAST_ANGLE;
        lastFireTick = -1;
    }

    explicit TWeapon(const Weapon& weapon) {
        type = (ELootType) weapon.typ;
        magazine = weapon.magazine;
        spread = weapon.spread;
        fireTimer = weapon.fireTimer ? *weapon.fireTimer : -1;
        lastAngle = weapon.lastAngle ? *weapon.lastAngle : DEFAULT_LAST_ANGLE;
        lastFireTick = weapon.lastFireTick ? *weapon.lastFireTick : -1;
    }

    explicit TWeapon(ELootType lootType) {
        type = lootType;
        switch (lootType) {
            case ELootType::PISTOL:
                spread = PISTOL_MIN_SPREAD;
                magazine = PISTOL_MAGAZINE_SIZE;
                break;
            case ELootType::ASSAULT_RIFLE:
                spread = ASSAULT_RIFLE_MIN_SPREAD;
                magazine = ASSAULT_RIFLE_MAGAZINE_SIZE;
                break;
            default:
                spread = ROCKET_LAUNCHER_MIN_SPREAD;
                magazine = ROCKET_LAUNCHER_MAGAZINE_SIZE;
                break;
        }
        fireTimer = WEAPON_RELOAD_TIME;
        lastAngle = DEFAULT_LAST_ANGLE;
        lastFireTick = -1;
    }

    TWeapon(const TWeapon& weapon) {
        type = weapon.type;
        magazine = weapon.magazine;
        //wasShooting = weapon.wasShooting;
        spread = weapon.spread;
        fireTimer = weapon.fireTimer;
        lastAngle = weapon.lastAngle;
        lastFireTick = weapon.lastFireTick;
    }
};

#endif //CODESIDE_WEAPON_H
