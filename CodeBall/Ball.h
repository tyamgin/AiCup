#ifndef CODEBALL_BALL_H
#define CODEBALL_BALL_H

#include "Unit.h"
#include "model/Ball.h"

struct ABall : public Unit {
    ABall() : Unit() {
        radius = BALL_RADIUS;
        mass = BALL_MASS;
    }

    explicit ABall(model::Ball ball) {
        x = ball.x;
        y = ball.y;
        z = ball.z;
        velocity.x = ball.velocity_x;
        velocity.y = ball.velocity_y;
        velocity.z = ball.velocity_z;
        radius = BALL_RADIUS;
        mass = BALL_MASS;
    }
};

#endif //CODEBALL_BALL_H
