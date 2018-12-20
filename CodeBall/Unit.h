#ifndef CODEBALL_UNIT_H
#define CODEBALL_UNIT_H

#include "Point.h"
#include "Const.h"

struct Unit : public Point {
    Point velocity;
    double radius;
    double mass;
    double radius_change_speed; // tmp field for simulator

    void move(double delta_time) {
        if (velocity.length2() > MAX_ENTITY_SPEED*MAX_ENTITY_SPEED)
            velocity = velocity.take(MAX_ENTITY_SPEED);

        *this += velocity * delta_time;
        y -= GRAVITY * delta_time * delta_time / 2;
        velocity.y -= GRAVITY * delta_time;
    }

};

#endif //CODEBALL_UNIT_H
