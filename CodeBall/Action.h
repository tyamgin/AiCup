#ifndef CODEBALL_ACTION_H
#define CODEBALL_ACTION_H

#include "Point.h"
#include "model/Action.h"

struct AAction {
    Point targetVelocity;
    double jumpSpeed;
    bool useNitro;

    AAction() {
    }

    AAction(const model::Action& action) {
        targetVelocity.x = action.target_velocity_x;
        targetVelocity.y = action.target_velocity_y;
        targetVelocity.z = action.target_velocity_z;
        jumpSpeed = action.jump_speed;
        useNitro = action.use_nitro;
    }
};

#endif //CODEBALL_ACTION_H
