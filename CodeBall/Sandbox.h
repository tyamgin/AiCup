#ifndef CODEBALL_SANDBOX_H
#define CODEBALL_SANDBOX_H

#include <vector>
#include <tuple>
#include <algorithm>
#include <iostream>
#include <optional>
#include "model/Game.h"
#include "model/Arena.h"
#include "model/Rules.h"
#include "Ball.h"
#include "Robot.h"
#include "Visualizer.h"
#include "RandomGenerators.h"
#include "Logger.h"

struct Sandbox {
    ABall ball;
    std::vector<ARobot> my, opp;
    bool isFinal = false;
    int tick = 0;
    int meId = 0;

    RandomGenerator rnd;

    Sandbox() {
    }

    Sandbox(const model::Game& game, const model::Rules& rules, int meId) : meId(meId) {
        tick = game.current_tick;
        isFinal = game.robots.size() > 4;
        for (auto& r : game.robots) {
            if (r.is_teammate) {
                my.emplace_back(r);
            } else {
                opp.emplace_back(r);
            }
        }
        ball = ABall(game.ball);
        checkGoal();
    }

    ARobot* me() {
        for (auto& x : my)
            if (x.id == meId)
                return &x;
        return nullptr;
    }

    ARobot* teammate1() {
        for (auto& x : my)
            if (x.id != meId)
                return &x;
        return nullptr;
    }

    ARobot* robot(int id) {
        for (size_t i = 0; i < my.size(); i++) {
            if (my[i].id == id) {
                return &my[i];
            }
            if (opp[i].id == id) {
                return &opp[i];
            }
        }
        LOG_ERROR("Can't find robot by id " + std::to_string(id));
        return nullptr;
    }

    std::vector<ARobot*> teammates(int excludeId = -1) {
        std::vector<ARobot*> ret;
        for (size_t i = 0; i < my.size(); i++)
            if (my[i].id != excludeId)
                ret.push_back(&my[i]);
        return ret;
    }

    struct DistanceNormalPair {
        double distance;
        Point normal;

        bool operator <(const DistanceNormalPair &other) const {
            return distance < other.distance;
        }
    };

    Point collide_with_arena(ABall &e) {
        auto n = dan_to_arena(e);
        auto penetration = e.radius - n.distance;
        if (penetration > 0) {
            e += n.normal * penetration;
            auto velocity = (e.velocity * n.normal); // radius_change_speed for ball = 0
            if (velocity < 0) {
                e.velocity -= n.normal * ((1 + BALL_ARENA_E) * velocity);
                return n.normal;
            }
        }
        return Point();
    }

    Point collide_with_arena(ARobot &e) {
        auto n = dan_to_arena(e);
        auto penetration = e.radius - n.distance;
        if (penetration > 0) {
            e += n.normal * penetration;
            auto velocity = (e.velocity * n.normal) - e.radius_change_speed;
            if (velocity < 0) {
                e.velocity -= n.normal * velocity; // ROBOT_ARENA_E = 0
                return n.normal;
            }
        }
        return Point();
    }

     void dan_to_plane(DistanceNormalPair& dan, const Point& point, const Point& point_on_plane, const Point& plane_normal) {
        auto dist = (point - point_on_plane) * plane_normal;
        if (dist < dan.distance) {
            dan.distance = dist;
            dan.normal = plane_normal;
        }
    }

    void dan_to_sphere_inner(DistanceNormalPair& dan, const Point& point, const Point& sphere_center, double sphere_radius) {
        auto diff = sphere_center - point;
        auto dist2 = diff.length2();
        if (SQR(sphere_radius - dan.distance) < dist2) {
            dan.distance = sphere_radius - sqrt(dist2);
            dan.normal = diff;
        }
    }

    void dan_to_sphere_outer(DistanceNormalPair& dan, const Point& point, const Point& sphere_center, double sphere_radius) {
        auto diff = point - sphere_center;
        auto dist2 = diff.length2();
        if (dist2 < SQR(dan.distance + sphere_radius)) {
            dan.distance = sqrt(dist2) - sphere_radius;
            dan.normal = diff;
        }
    }

    DistanceNormalPair dan_to_arena_quarter(const Unit& point) {
        DistanceNormalPair dan = {point.radius + EPS};

        bool aBottom = point.y < ARENA_BOTTOM_RADIUS;
        bool aTop = point.y > ARENA_TOP_RADIUS;

        // Ground
        if (point.y <= point.radius) {
            dan_to_plane(dan, point, Point(0, 0, 0), Point(0, 1, 0));
        }

        // Ceiling
        if (point.y >= ARENA_HEIGHT - point.radius) {
            dan_to_plane(dan, point, Point(0, ARENA_HEIGHT, 0), Point(0, -1, 0));
        }

        const double arenaRadius =  point.y < ARENA_HEIGHT - ARENA_TOP_RADIUS ? ARENA_BOTTOM_RADIUS : ARENA_TOP_RADIUS;
        {
            double spaceX = ARENA_WIDTH / 2 - arenaRadius;
            double spaceZ = ARENA_DEPTH / 2 - ARENA_CORNER_RADIUS;
            if (point.x <= spaceX && point.z <= spaceZ)
                return dan;
        }
        {
            double spaceX = ARENA_WIDTH / 2 - ARENA_CORNER_RADIUS;
            double spaceZ = ARENA_DEPTH / 2 - arenaRadius;
            if (point.x <= spaceX && point.z <= spaceZ)
                return dan;
        }

        // Side x
        dan_to_plane(dan, point, Point(ARENA_WIDTH / 2, 0, 0), Point(-1, 0, 0));
        // Side z (goal)
        dan_to_plane(dan, point, Point(0, 0, (ARENA_DEPTH / 2) + ARENA_GOAL_DEPTH), Point(0, 0, -1));

        // Side z
        auto v = Point(point.x, point.y, 0) - Point((ARENA_GOAL_WIDTH / 2) - ARENA_GOAL_TOP_RADIUS, ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS, 0);
        if (point.x >= (ARENA_GOAL_WIDTH / 2) + ARENA_GOAL_SIDE_RADIUS or point.y >= ARENA_GOAL_HEIGHT + ARENA_GOAL_SIDE_RADIUS
           or (v.x > 0 and v.y > 0 and v.length() >= ARENA_GOAL_TOP_RADIUS + ARENA_GOAL_SIDE_RADIUS)) {
            dan_to_plane(dan, point, Point(0, 0, ARENA_DEPTH / 2), Point(0, 0, -1));
        }

        // Side x & ceiling (goal)
        if (point.z >= (ARENA_DEPTH / 2) + ARENA_GOAL_SIDE_RADIUS) {
            // x
            dan_to_plane(dan, point, Point(ARENA_GOAL_WIDTH / 2, 0, 0), Point(-1, 0, 0));
            // y
            dan_to_plane(dan, point, Point(0, ARENA_GOAL_HEIGHT, 0), Point(0, -1, 0));
        }

        // Goal back corners
        if (point.z > (ARENA_DEPTH / 2) + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) {
            dan_to_sphere_inner(dan, 
                point,
                Point(
                        std::clamp(
                                point.x,
                                ARENA_BOTTOM_RADIUS - (ARENA_GOAL_WIDTH / 2),
                                (ARENA_GOAL_WIDTH / 2) - ARENA_BOTTOM_RADIUS
                        ),
                        std::clamp(
                                point.y,
                                ARENA_BOTTOM_RADIUS,
                                ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS
                        ),
                        (ARENA_DEPTH / 2) + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS
                ),
                ARENA_BOTTOM_RADIUS);
        }


        // Corner
        if (point.x > (ARENA_WIDTH / 2) - ARENA_CORNER_RADIUS and point.z > (ARENA_DEPTH / 2) - ARENA_CORNER_RADIUS) {
            dan_to_sphere_inner(dan, 
                    point,
                    Point(
                            (ARENA_WIDTH / 2) - ARENA_CORNER_RADIUS,
                            point.y,
                            (ARENA_DEPTH / 2) - ARENA_CORNER_RADIUS
                    ),
                    ARENA_CORNER_RADIUS);
        }

        // Goal outer corner
        if (point.z < (ARENA_DEPTH / 2) + ARENA_GOAL_SIDE_RADIUS) {
            // Side x
            if (point.x < (ARENA_GOAL_WIDTH / 2) + ARENA_GOAL_SIDE_RADIUS) {
                dan_to_sphere_outer(dan, 
                        point,
                        Point(
                                (ARENA_GOAL_WIDTH / 2) + ARENA_GOAL_SIDE_RADIUS,
                                point.y,
                                (ARENA_DEPTH / 2) + ARENA_GOAL_SIDE_RADIUS
                        ),
                        ARENA_GOAL_SIDE_RADIUS);
            }
            // Ceiling
            if (point.y < ARENA_GOAL_HEIGHT + ARENA_GOAL_SIDE_RADIUS) {
                dan_to_sphere_outer(dan, 
                        point,
                        Point(
                                point.x,
                                ARENA_GOAL_HEIGHT + ARENA_GOAL_SIDE_RADIUS,
                                (ARENA_DEPTH / 2) + ARENA_GOAL_SIDE_RADIUS
                        ),
                        ARENA_GOAL_SIDE_RADIUS);
            }
            // Top corner
            auto o = Point((ARENA_GOAL_WIDTH / 2) - ARENA_GOAL_TOP_RADIUS, ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS, 0);
            auto v = Point(point.x, point.y, 0) - o;
            if (v.x > 0 and v.y > 0) {
                o = o + v.normalized() * (ARENA_GOAL_TOP_RADIUS + ARENA_GOAL_SIDE_RADIUS);
                dan_to_sphere_outer(dan, 
                        point,
                        Point(o.x, o.y, (ARENA_DEPTH / 2) + ARENA_GOAL_SIDE_RADIUS),
                        ARENA_GOAL_SIDE_RADIUS);
            }
        }

        // Goal inside top corners
        if (point.z > (ARENA_DEPTH / 2) + ARENA_GOAL_SIDE_RADIUS
           and point.y > ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS) {
            // Side x
            if (point.x > (ARENA_GOAL_WIDTH / 2) - ARENA_GOAL_TOP_RADIUS) {
                dan_to_sphere_inner(dan, 
                        point, Point(
                                (ARENA_GOAL_WIDTH / 2) - ARENA_GOAL_TOP_RADIUS,
                                ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS,
                                point.z
                        ),
                        ARENA_GOAL_TOP_RADIUS);
            }
            // Side z
            if (point.z > (ARENA_DEPTH / 2) + ARENA_GOAL_DEPTH - ARENA_GOAL_TOP_RADIUS) {
                dan_to_sphere_inner(dan, 
                        point,
                        Point(
                                point.x,
                                        ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS,
                                        (ARENA_DEPTH / 2) + ARENA_GOAL_DEPTH - ARENA_GOAL_TOP_RADIUS
                        ),
                        ARENA_GOAL_TOP_RADIUS);
            }
        }

        // Bottom corners
        if (point.y < ARENA_BOTTOM_RADIUS) {
            // Side x
            if (point.x > (ARENA_WIDTH / 2) - ARENA_BOTTOM_RADIUS) {
                dan_to_sphere_inner(dan, 
                        point,
                        Point(
                                (ARENA_WIDTH / 2) - ARENA_BOTTOM_RADIUS,
                                ARENA_BOTTOM_RADIUS,
                                point.z
                        ),
                        ARENA_BOTTOM_RADIUS);
            }
            // Side z
            if (point.z > (ARENA_DEPTH / 2) - ARENA_BOTTOM_RADIUS
                and point.x >= (ARENA_GOAL_WIDTH / 2) + ARENA_GOAL_SIDE_RADIUS) {
                dan_to_sphere_inner(dan, 
                        point, Point(
                                point.x,
                                ARENA_BOTTOM_RADIUS,
                                (ARENA_DEPTH / 2) - ARENA_BOTTOM_RADIUS
                        ),
                        ARENA_BOTTOM_RADIUS);
            }
            // Side z (goal)
            if (point.z > (ARENA_DEPTH / 2) + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) {
                dan_to_sphere_inner(dan, 
                        point, Point(
                                point.x,
                                ARENA_BOTTOM_RADIUS,
                                (ARENA_DEPTH / 2) + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS
                        ),
                        ARENA_BOTTOM_RADIUS);
            }
            // Goal outer corner
            auto o = Point(
                    (ARENA_GOAL_WIDTH / 2) + ARENA_GOAL_SIDE_RADIUS,
                            (ARENA_DEPTH / 2) + ARENA_GOAL_SIDE_RADIUS,
                            0
            );
            auto v = Point(point.x, point.z, 0) - o;
            if (v.x < 0 and v.y < 0
               and v.length() < ARENA_GOAL_SIDE_RADIUS + ARENA_BOTTOM_RADIUS) {
                o = o + v.normalized() * (ARENA_GOAL_SIDE_RADIUS + ARENA_BOTTOM_RADIUS);
                dan_to_sphere_inner(dan, 
                        point,
                        Point(o.x, ARENA_BOTTOM_RADIUS, o.y),
                        ARENA_BOTTOM_RADIUS);
            }
            // Side x (goal)
            if (point.z >= (ARENA_DEPTH / 2) + ARENA_GOAL_SIDE_RADIUS
               and point.x > (ARENA_GOAL_WIDTH / 2) - ARENA_BOTTOM_RADIUS) {
                dan_to_sphere_inner(dan, 
                        point, Point(
                                (ARENA_GOAL_WIDTH / 2) - ARENA_BOTTOM_RADIUS,
                                ARENA_BOTTOM_RADIUS,
                                point.z
                        ),
                        ARENA_BOTTOM_RADIUS);
            }
            // Corner
            if (point.x > (ARENA_WIDTH / 2) - ARENA_CORNER_RADIUS
               and point.z > (ARENA_DEPTH / 2) - ARENA_CORNER_RADIUS) {
                auto corner_o = Point(
                        (ARENA_WIDTH / 2) - ARENA_CORNER_RADIUS,
                        (ARENA_DEPTH / 2) - ARENA_CORNER_RADIUS,
                        0
                );
                auto n = Point(point.x, point.z, 0) - corner_o;
                auto dist = n.length();
                if (dist > ARENA_CORNER_RADIUS - ARENA_BOTTOM_RADIUS) {
                    n = n / dist;
                    auto o2 = corner_o + n * (ARENA_CORNER_RADIUS - ARENA_BOTTOM_RADIUS);
                    dan_to_sphere_inner(dan, 
                            point,
                            Point(o2.x, ARENA_BOTTOM_RADIUS, o2.y),
                            ARENA_BOTTOM_RADIUS);
                }
            }
        }
        // Ceiling corners
        if (point.y > ARENA_HEIGHT - ARENA_TOP_RADIUS) {
            // Side x
            if (point.x > (ARENA_WIDTH / 2) - ARENA_TOP_RADIUS) {
                dan_to_sphere_inner(dan, 
                        point,
                        Point(
                                (ARENA_WIDTH / 2) - ARENA_TOP_RADIUS,
                                ARENA_HEIGHT - ARENA_TOP_RADIUS,
                                point.z
                        ),
                        ARENA_TOP_RADIUS);
            }
            // Side z
            if (point.z > (ARENA_DEPTH / 2) - ARENA_TOP_RADIUS) {
                dan_to_sphere_inner(dan, 
                        point,
                        Point(
                                point.x,
                                ARENA_HEIGHT - ARENA_TOP_RADIUS,
                                (ARENA_DEPTH / 2) - ARENA_TOP_RADIUS
                        ),
                        ARENA_TOP_RADIUS);
            }
            // Corner
            if (point.x > (ARENA_WIDTH / 2) - ARENA_CORNER_RADIUS
                and point.z > (ARENA_DEPTH / 2) - ARENA_CORNER_RADIUS) {
                auto corner_o = Point(
                        (ARENA_WIDTH / 2) - ARENA_CORNER_RADIUS,
                        (ARENA_DEPTH / 2) - ARENA_CORNER_RADIUS,
                        0
                );
                auto dv = Point(point.x, point.z, 0) - corner_o;
                if (dv.length() > ARENA_CORNER_RADIUS - ARENA_TOP_RADIUS) {
                    auto n = dv.normalized();
                    auto o2 = corner_o + n * (ARENA_CORNER_RADIUS - ARENA_TOP_RADIUS);
                    dan_to_sphere_inner(dan, 
                            point,
                            Point(o2.x, ARENA_HEIGHT - ARENA_TOP_RADIUS, o2.y),
                            ARENA_TOP_RADIUS);
                }
            }
        }
        return dan;
    }

    DistanceNormalPair dan_to_arena(Unit point) {
        auto negate_x = point.x < 0;
        auto negate_z = point.z < 0;
        if (negate_x)
            point.x = -point.x;
        if (negate_z)
            point.z = -point.z;
        auto result = dan_to_arena_quarter(point);
        if (negate_x)
            result.normal.x = -result.normal.x;
        if (negate_z)
            result.normal.z = -result.normal.z;
        if (result.distance <= point.radius)
            result.normal.normalize();
        return result;
    }

    template<typename T>
    void collide_entities(ARobot& a, T& b) {
        const double secondMass = std::is_same<T, ARobot>::value ? ROBOT_MASS : BALL_MASS;
        double secondRadius, secondRadiusChangeSpeed;
        if constexpr (std::is_same<T, ARobot>::value) {
            secondRadius = b.radius;
            secondRadiusChangeSpeed = b.radius_change_speed;
        } else {
            secondRadius = BALL_RADIUS;
            secondRadiusChangeSpeed = 0;
        }

        auto dist2 = a.getDistanceTo2(b);
        if (dist2 >= SQR(a.radius + secondRadius))
            return;

        auto distance = sqrt(dist2);
        auto penetration = a.radius + secondRadius - distance;

        const auto k_a = (1 / ROBOT_MASS) / ((1 / ROBOT_MASS) + (1 / secondMass));
        const auto k_b = (1 / secondMass) / ((1 / ROBOT_MASS) + (1 / secondMass));

        double normalX = (b.x - a.x) / distance;
        double normalY = (b.y - a.y) / distance;
        double normalZ = (b.z - a.z) / distance;

        a.x -= penetration * k_a * normalX;
        a.y -= penetration * k_a * normalY;
        a.z -= penetration * k_a * normalZ;

        b.x += penetration * k_b * normalX;
        b.y += penetration * k_b * normalY;
        b.z += penetration * k_b * normalZ;

        auto delta_velocity =
                (b.velocity.x - a.velocity.x) * normalX +
                (b.velocity.y - a.velocity.y) * normalY +
                (b.velocity.z - a.velocity.z) * normalZ + secondRadiusChangeSpeed - a.radius_change_speed;
        if (delta_velocity < 0) {
            hasRandomCollision = true;

#if M_NO_RANDOM
            auto rndValue = MAX_HIT_E;
#else
            auto rndValue = rnd.randDouble(MIN_HIT_E, MAX_HIT_E);
#endif
            auto impulseX = (1 + rndValue) * delta_velocity * normalX;
            auto impulseY = (1 + rndValue) * delta_velocity * normalY;
            auto impulseZ = (1 + rndValue) * delta_velocity * normalZ;

            a.velocity.x += impulseX * k_a;
            a.velocity.y += impulseY * k_a;
            a.velocity.z += impulseZ * k_a;

            b.velocity.x -= impulseX * k_b;
            b.velocity.y -= impulseY * k_b;
            b.velocity.z -= impulseZ * k_b;
        }
    }

    std::vector<ARobot*> robots() {
        std::vector<ARobot*> res;
        for (auto &x : my)
            res.push_back(&x);
        for (auto &x : opp)
            res.push_back(&x);
        return res;
    }

    void update(double delta_time) {
        std::vector<ARobot*> robots = this->robots();
#if M_NO_RANDOM
        std::sort(robots.begin(), robots.end(), [](ARobot* a, ARobot* b) {
            return a->id < b->id;
        });
#endif
        for (auto robotPtr : robots) {
            auto& robot = *robotPtr;
            if (robot.touch) {
                auto target_velocity = robot.action.targetVelocity.clamp(ROBOT_MAX_GROUND_SPEED);

                target_velocity -= robot.touch_normal * (robot.touch_normal * target_velocity);
                auto target_velocity_change = target_velocity - robot.velocity;
                if (target_velocity_change.length2() > 0) {
                    auto acceleration = ROBOT_ACCELERATION * std::max(0.0, robot.touch_normal.y);
                    robot.velocity += (
                        target_velocity_change.normalized() * acceleration * delta_time
                        ).clamp(target_velocity_change.length());
                }
            }
//            if (robot.action.use_nitro) {
//                auto target_velocity_change = (
//                    robot.action.target_velocity - robot.velocity,
//                    ).clamp(robot.nitro * NITRO_POINT_VELOCITY_CHANGE);
//                if (length(target_velocity_change) > 0) {
//                    auto acceleration = target_velocity_change.normalized() * ROBOT_NITRO_ACCELERATION;
//                    auto velocity_change = (acceleration * delta_time).clamp(target_velocity_change.length());
//                    robot.velocity += velocity_change;
//                    robot.nitro -= length(velocity_change) / NITRO_POINT_VELOCITY_CHANGE;
//                }
//            }
            robot.move(delta_time);
            robot.radius = ROBOT_MIN_RADIUS + (ROBOT_MAX_RADIUS - ROBOT_MIN_RADIUS) * robot.action.jumpSpeed / ROBOT_MAX_JUMP_SPEED;
            robot.radius_change_speed = robot.action.jumpSpeed;
        }
        ball.move(delta_time);
        for (size_t i = 0; i < robots.size(); i++) {
            for (size_t j = 0; j < i; j++) {
                collide_entities<ARobot>(*robots[i], *robots[j]);
            }
        }

        for (auto& robot : robots) {
            collide_entities<ABall>(*robot, ball);
            auto collision_normal = collide_with_arena(*robot);
            if (collision_normal.length2() < EPS2) {
                robot->touch = false;
            } else {
                robot->touch = true;
                robot->touch_normal = collision_normal;
            }
        }

        collide_with_arena(ball);
        checkGoal();

        for (auto& robot : robots) {
            if (robot->nitro_amount == MAX_NITRO_AMOUNT)
                continue;

//            for pack in nitro_packs:
//                if not pack.alive:
//                    continue
//                if length(robot.position - pack.position) <= robot.radius + pack.radius:
//                    robot.nitro = MAX_NITRO_AMOUNT
//                    pack.alive = false
//                    pack.respawn_ticks = NITRO_PACK_RESPAWN_TICKS
        }
    }

    void checkGoal() {
        if (abs(ball.z) > ARENA_DEPTH / 2 + ball.radius) {
            hasGoal = ball.z < 0 ? -1 : 1;
        }
    }

    void doTick(int microticksPerTick = MICROTICKS_PER_TICK) {
        OP_START(DO_TICK);

        hasRandomCollision = false;
        auto delta_time = 1.0 / TICKS_PER_SECOND;
        for (int i = 0; i < microticksPerTick; i++)
            update(delta_time / microticksPerTick);
//        for pack in nitro_packs:
//            if pack.alive:
//                continue
//            pack.respawn_ticks -= 1
//            if pack.respawn_ticks == 0:
//                pack.alive = true

        tick++;
        // TODO: clear actions

        OP_END(DO_TICK);
    }

    bool hasRandomCollision = false;
    int hasGoal = 0; // -1, 0, 1
};

#endif //CODEBALL_SANDBOX_H
