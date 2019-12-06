#ifndef CODESIDE_WEAPON_H
#define CODESIDE_WEAPON_H

#define DEFAULT_LAST_ANGLE 1000

class TWeapon {
public:
    ELootType type;
    int magazine;
    //bool wasShooting;
    double spread;
    double fireTimer; // -1
    double lastAngle; // DEFAULT_LAST_ANGLE
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
        //wasShooting = false;
        spread = 0;
        fireTimer = -1;
        lastAngle = DEFAULT_LAST_ANGLE;
        lastFireTick = -1;
    }

    explicit TWeapon(const Weapon& weapon) {
        type = (ELootType) weapon.typ;
        magazine = weapon.magazine;
        //wasShooting = weapon.wasShooting;
        spread = weapon.spread;
        fireTimer = weapon.fireTimer ? *weapon.fireTimer : -1;
        lastAngle = weapon.lastAngle ? *weapon.lastAngle : DEFAULT_LAST_ANGLE;
        lastFireTick = weapon.lastFireTick ? *weapon.lastFireTick : -1;
    }

    explicit TWeapon(ELootType lootType) {
        type = lootType;
        //wasShooting = false; // TODO: точно?
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

    void shot() {
        magazine -= 1;
        switch (type) {
            case ELootType::PISTOL:
                if (magazine == 0) {
                    magazine = PISTOL_MAGAZINE_SIZE;
                    fireTimer = WEAPON_RELOAD_TIME;
                } else {
                    fireTimer = PISTOL_FIRE_RATE;
                }
                spread += PISTOL_RECOIL;
                break;
            case ELootType::ASSAULT_RIFLE:
                if (magazine == 0) {
                    magazine = ASSAULT_RIFLE_MAGAZINE_SIZE;
                    fireTimer = WEAPON_RELOAD_TIME;
                } else {
                    fireTimer = ASSAULT_RIFLE_FIRE_RATE;
                }
                spread += ASSAULT_RIFLE_RECOIL;
                break;
            default:
                if (magazine == 0) {
                    magazine = ROCKET_LAUNCHER_MAGAZINE_SIZE;
                    fireTimer = WEAPON_RELOAD_TIME;
                } else {
                    fireTimer = ROCKET_LAUNCHER_FIRE_RATE;
                }
                spread += ROCKET_LAUNCHER_RECOIL;
                break;
        }
        if (spread > WEAPON_MAX_SPREAD) {
            spread = WEAPON_MAX_SPREAD;
        }
    }
};

#endif //CODESIDE_WEAPON_H
