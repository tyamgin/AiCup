#ifndef CODESIDE_WEAPON_H
#define CODESIDE_WEAPON_H

class TWeapon {
public:
    ELootType type;
    int magazine;
    bool wasShooting;
    double spread;
    double fireTimer; // -1
    double lastAngle; // 1000
    int lastFireTick; // -1

    // WeaponParams
//    int magazineSize;
//    double fireRate;
//    double reloadTime;
//    double minSpread;
//    double maxSpread;
//    double recoil;
//    double aimSpeed;

//    BulletParams bullet;
//    std::shared_ptr<ExplosionParams> explosion;

    TWeapon() {
        type = ELootType::NONE;
        magazine = 0;
        wasShooting = false;
        spread = 0;
        fireTimer = -1;
        lastAngle = 1000;
        lastFireTick = -1;
    }

    explicit TWeapon(const Weapon& weapon) {
        type = (ELootType) weapon.typ;
        magazine = weapon.magazine;
        wasShooting = weapon.wasShooting;
        spread = weapon.spread;
        fireTimer = weapon.fireTimer ? *weapon.fireTimer : -1;
        lastAngle = weapon.lastAngle ? *weapon.lastAngle : 1000;
        lastFireTick = weapon.lastFireTick ? *weapon.lastFireTick : -1;
    }

    explicit TWeapon(ELootType lootType) {
        type = lootType;
        wasShooting = false; // TODO: точно?
        switch (lootType) {
            case ELootType::PISTOL:
                spread = PISTOL_MIN_SPREAD;
                fireTimer = PISTOL_RELOAD_TIME;
                magazine = PISTOL_MAGAZINE_SIZE;
                break;
            case ELootType::ASSAULT_RIFLE:
                spread = ASSAULT_RIFLE_MIN_SPREAD;
                fireTimer = ASSAULT_RIFLE_RELOAD_TIME;
                magazine = ASSAULT_RIFLE_MAGAZINE_SIZE;
                break;
            default:
                spread = ROCKET_LAUNCHER_MIN_SPREAD;
                fireTimer = ROCKET_LAUNCHER_RELOAD_TIME;
                magazine = ROCKET_LAUNCHER_MAGAZINE_SIZE;
                break;
        }
        lastAngle = 1000;
        lastFireTick = -1;
    }

    TWeapon(const TWeapon& weapon) {
        type = weapon.type;
        magazine = weapon.magazine;
        wasShooting = weapon.wasShooting;
        spread = weapon.spread;
        fireTimer = weapon.fireTimer;
        lastAngle = weapon.lastAngle;
        lastFireTick = weapon.lastFireTick;
    }

    void decreaseFireTimer() {
        if (fireTimer < 0.5) {
            return;
        }
        fireTimer -= 1.0 / TICKS_PER_SECOND;
        if (fireTimer < EPS) {
            fireTimer = -1;
        }
    }
};

#endif //CODESIDE_WEAPON_H
