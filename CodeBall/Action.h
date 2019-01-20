#ifndef CODEBALL_ACTION_H
#define CODEBALL_ACTION_H

#include "Point.h"
#include "model/Action.h"

struct AAction {
    Point targetVelocity;
    double jumpSpeed;
    bool useNitro;

    AAction() {
        jumpSpeed = 0;
        useNitro = false;
    }

    AAction(const Point& vel, double jumpSpeed = 0.0, bool useNitro = false) : targetVelocity(vel), jumpSpeed(jumpSpeed), useNitro(useNitro) {
    }

    AAction& jump(double jumpSpeed = ROBOT_MAX_JUMP_SPEED) {
        this->jumpSpeed = jumpSpeed;
        return *this;
    }

    AAction& nitro() {
        this->useNitro = true;
        return *this;
    }

    AAction& vel(const Point& vel) {
        this->targetVelocity = vel;
        return *this;
    }
};

#endif //CODEBALL_ACTION_H
