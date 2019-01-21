#ifndef CODEBALL_UNIT_H
#define CODEBALL_UNIT_H

#include "Point.h"
#include "Const.h"

struct Unit : public Point {
    Point velocity;
    double radius = 0;
    //double mass = 0;

    void move(double delta_time) {
        velocity.clamp(MAX_ENTITY_SPEED);
        x += velocity.x * delta_time;
        z += velocity.z * delta_time;
        y += (velocity.y - GRAVITY / 2 * delta_time) * delta_time;
        velocity.y -= GRAVITY * delta_time;
    }
};

#endif //CODEBALL_UNIT_H
