#ifndef CODEBALL_NITROPACK_H
#define CODEBALL_NITROPACK_H

struct ANitroPack : public Unit {
    int id;
    int respawnTicks;

    ANitroPack() : Unit() {
        id = 0;
        respawnTicks = 0;
    }

    explicit ANitroPack(const model::NitroPack& pack) {
        id = pack.id;
        respawnTicks = pack.respawn_ticks;
        radius = NITRO_PACK_RADIUS;
        x = pack.x;
        y = pack.y;
        z = pack.z;
    }

    bool alive() const {
        return respawnTicks == 0;
    }
};

#endif //CODEBALL_NITROPACK_H
