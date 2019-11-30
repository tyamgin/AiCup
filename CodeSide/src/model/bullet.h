#ifndef CODESIDE_BULLET_H
#define CODESIDE_BULLET_H

#include "rectangle.h"

class TBullet : public TRectangle {
public:
    WeaponType weaponType;
    int unitId;
    int playerId;
    TPoint velocity;
    int damage;
    std::shared_ptr<ExplosionParams> explosionParams;

    explicit TBullet(const Bullet& bullet) : TRectangle(bullet.position, bullet.size, bullet.size) {
        weaponType = bullet.weaponType;
        unitId = bullet.unitId;
        playerId = bullet.playerId;
        velocity.x = bullet.velocity.x;
        velocity.y = bullet.velocity.y;
        damage = bullet.damage;
        // TODO: explosionParams
    }

    TBullet(const TBullet& bullet) : TRectangle(bullet) {
        weaponType = bullet.weaponType;
        unitId = bullet.unitId;
        playerId = bullet.playerId;
        velocity.x = bullet.velocity.x;
        velocity.y = bullet.velocity.y;
        damage = bullet.damage;
        // TODO: explosionParams
    }
};

#endif //CODESIDE_BULLET_H
