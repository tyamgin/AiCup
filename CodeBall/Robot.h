#ifndef CODEBALL_ROBOT_H
#define CODEBALL_ROBOT_H

#include "Unit.h"
#include "Action.h"
#include "model/Robot.h"

struct ARobot : public Unit {
    int id;
    int player_id;
    bool is_teammate;
    double nitro_amount;
    bool touch;
    Point touch_normal;
    AAction action; // field for simulator

    ARobot() {
        touch = false;
        nitro_amount = 0;
        player_id = 0;
        id = 0;
        is_teammate = false;
    }

    explicit ARobot(model::Robot robot) {
        id = robot.id;
        player_id = robot.player_id;
        is_teammate = robot.is_teammate;
        x = robot.x;
        y = robot.y;
        z = robot.z;
        velocity.x = robot.velocity_x;
        velocity.y = robot.velocity_y;
        velocity.z = robot.velocity_z;
        radius = robot.radius;
        nitro_amount = robot.nitro_amount;
        touch = robot.touch;
        touch_normal.x = robot.touch_normal_x;
        touch_normal.y = robot.touch_normal_y;
        touch_normal.z = robot.touch_normal_z;
        mass = ROBOT_MASS;
    }
};

#endif //CODEBALL_ROBOT_H
