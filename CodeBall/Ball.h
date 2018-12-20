#ifndef CODEBALL_BALL_H
#define CODEBALL_BALL_H

#include "Unit.h"
#include "model/Ball.h"

struct ABall : public Unit {
    ABall() {
    }

    ABall(model::Ball robot) {
        x = robot.x;
        y = robot.y;
        z = robot.z;
        velocity.x = robot.velocity_x;
        velocity.y = robot.velocity_y;
        velocity.z = robot.velocity_z;
        radius = robot.radius;
        mass = BALL_MASS;
    }
};

#endif //CODEBALL_BALL_H
