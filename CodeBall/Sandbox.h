#ifndef CODEBALL_SANDBOX_H
#define CODEBALL_SANDBOX_H

#include <vector>
#include <tuple>
#include <algorithm>
#include <iostream>
#include "model/Game.h"
#include "model/Arena.h"
#include "model/Rules.h"
#include "Ball.h"
#include "Robot.h"

struct Sandbox {
    ABall ball;
    std::vector<ARobot> my, opp;
    model::Arena arena;
    bool isFinal = false;
    int tick = 0;

    Sandbox() {
    }

    Sandbox(const model::Game& game, const model::Rules& rules) {
        tick = game.current_tick;
        isFinal = game.robots.size() > 4;
        arena = rules.arena;
        for (auto& r : game.robots) {
            if (r.is_teammate) {
                my.emplace_back(r);
            } else {
                opp.emplace_back(r);
            }
        }
        ball = game.ball;
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
        std::cerr << "Can't find robot by id " << id;
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

    DistanceNormalPair dan_to_plane(const Point& point, const Point& point_on_plane, const Point& plane_normal) {
        return {
            (point - point_on_plane) * plane_normal,
            plane_normal
        };
    }

    DistanceNormalPair dan_to_sphere_inner(const Point& point, const Point& sphere_center, double sphere_radius) {
        return {
            sphere_radius - (point - sphere_center).length(),
            (sphere_center - point).normalized()
        };
    }

    DistanceNormalPair dan_to_sphere_outer(const Point& point, const Point& sphere_center, double sphere_radius) {
        return {
            (point - sphere_center).length() - sphere_radius,
            (point - sphere_center).normalized()
        };
    }

    DistanceNormalPair dan_to_arena_quarter(const Point& point) {
        // Ground
        auto dan = dan_to_plane(point, Point(0, 0, 0), Point(0, 1, 0));
        // Ceiling
        dan = std::min(dan, dan_to_plane(point, Point(0, arena.height, 0), Point(0, -1, 0)));
        // Side x
        dan = std::min(dan, dan_to_plane(point, Point(arena.width / 2, 0, 0), Point(-1, 0, 0)));


        // Side z (goal)
        dan = std::min(dan, dan_to_plane(
                point,
                Point(0, 0, (arena.depth / 2) + arena.goal_depth),
                Point(0, 0, -1)));

        // Side z
        auto v = Point(point.x, point.y, 0) - Point(
                (arena.goal_width / 2) - arena.goal_top_radius,
                        arena.goal_height - arena.goal_top_radius, 0);

        if (point.x >= (arena.goal_width / 2) + arena.goal_side_radius
           or point.y >= arena.goal_height + arena.goal_side_radius
           or (
                   v.x > 0
                   and v.y > 0
                   and v.length() >= arena.goal_top_radius + arena.goal_side_radius)) {

            dan = std::min(dan, dan_to_plane(point, Point(0, 0, arena.depth / 2), Point(0, 0, -1)));
        }

        // Side x & ceiling (goal)
        if (point.z >= (arena.depth / 2) + arena.goal_side_radius) {
            // x
            dan = std::min(dan, dan_to_plane(
                point,
                Point(arena.goal_width / 2, 0, 0),
                Point(-1, 0, 0)));
            // y
            dan = std::min(dan, dan_to_plane(point, Point(0, arena.goal_height, 0), Point(0, -1, 0)));
        }

        // Goal back corners
        //assert arena.bottom_radius == arena.goal_top_radius

        if (point.z > (arena.depth / 2) + arena.goal_depth - arena.bottom_radius) {
            dan = std::min(dan, dan_to_sphere_inner(
                point,
                Point(
                        std::clamp(
                                point.x,
                                arena.bottom_radius - (arena.goal_width / 2),
                                (arena.goal_width / 2) - arena.bottom_radius
                        ),
                        std::clamp(
                                point.y,
                                arena.bottom_radius,
                                arena.goal_height - arena.goal_top_radius
                        ),
                        (arena.depth / 2) + arena.goal_depth - arena.bottom_radius
                ),
                arena.bottom_radius));
        }



        // Corner
        if (point.x > (arena.width / 2) - arena.corner_radius and point.z > (arena.depth / 2) - arena.corner_radius) {
            dan = std::min(dan, dan_to_sphere_inner(
                    point,
                    Point(
                            (arena.width / 2) - arena.corner_radius,
                            point.y,
                            (arena.depth / 2) - arena.corner_radius
                    ),
                    arena.corner_radius));
        }

        // Goal outer corner
        if (point.z < (arena.depth / 2) + arena.goal_side_radius) {
            // Side x
            if (point.x < (arena.goal_width / 2) + arena.goal_side_radius) {
                dan = std::min(dan, dan_to_sphere_outer(
                        point,
                        Point(
                                (arena.goal_width / 2) + arena.goal_side_radius,
                                point.y,
                                (arena.depth / 2) + arena.goal_side_radius
                        ),
                        arena.goal_side_radius));
            }
            // Ceiling
            if (point.y < arena.goal_height + arena.goal_side_radius) {
                dan = std::min(dan, dan_to_sphere_outer(
                        point,
                        Point(
                                point.x,
                                arena.goal_height + arena.goal_side_radius,
                                (arena.depth / 2) + arena.goal_side_radius
                        ),
                        arena.goal_side_radius));
            }
            // Top corner
            auto o = Point(
                    (arena.goal_width / 2) - arena.goal_top_radius,
                            arena.goal_height - arena.goal_top_radius,
                            0
            );
            auto v = Point(point.x, point.y, 0) - o;
            if (v.x > 0 and v.y > 0) {
                o = o + v.normalized() * (arena.goal_top_radius + arena.goal_side_radius);
                dan = std::min(dan, dan_to_sphere_outer(
                        point,
                        Point(o.x, o.y, (arena.depth / 2) + arena.goal_side_radius),
                        arena.goal_side_radius));
            }
        }

        // Goal inside top corners
        if (point.z > (arena.depth / 2) + arena.goal_side_radius
           and point.y > arena.goal_height - arena.goal_top_radius) {
            // Side x
            if (point.x > (arena.goal_width / 2) - arena.goal_top_radius) {
                dan = std::min(dan, dan_to_sphere_inner(
                        point, Point(
                                (arena.goal_width / 2) - arena.goal_top_radius,
                                arena.goal_height - arena.goal_top_radius,
                                point.z
                        ),
                        arena.goal_top_radius));
            }
            // Side z
            if (point.z > (arena.depth / 2) + arena.goal_depth - arena.goal_top_radius) {
                dan = std::min(dan, dan_to_sphere_inner(
                        point,
                        Point(
                                point.x,
                                        arena.goal_height - arena.goal_top_radius,
                                        (arena.depth / 2) + arena.goal_depth - arena.goal_top_radius
                        ),
                        arena.goal_top_radius));
            }
        }

        // Bottom corners
        if (point.y < arena.bottom_radius) {
            // Side x
            if (point.x > (arena.width / 2) - arena.bottom_radius) {
                dan = std::min(dan, dan_to_sphere_inner(
                        point,
                        Point(
                                (arena.width / 2) - arena.bottom_radius,
                                arena.bottom_radius,
                                point.z
                        ),
                        arena.bottom_radius));
            }
            // Side z
            if (point.z > (arena.depth / 2) - arena.bottom_radius
                and point.x >= (arena.goal_width / 2) + arena.goal_side_radius) {
                dan = std::min(dan, dan_to_sphere_inner(
                        point, Point(
                                point.x,
                                arena.bottom_radius,
                                (arena.depth / 2) - arena.bottom_radius
                        ),
                        arena.bottom_radius));
            }
            // Side z (goal)
            if (point.z > (arena.depth / 2) + arena.goal_depth - arena.bottom_radius) {
                dan = std::min(dan, dan_to_sphere_inner(
                        point, Point(
                                point.x,
                                arena.bottom_radius,
                                (arena.depth / 2) + arena.goal_depth - arena.bottom_radius
                        ),
                        arena.bottom_radius));
            }
            // Goal outer corner
            auto o = Point(
                    (arena.goal_width / 2) + arena.goal_side_radius,
                            (arena.depth / 2) + arena.goal_side_radius,
                            0
            );
            auto v = Point(point.x, point.z, 0) - o;
            if (v.x < 0 and v.y < 0
               and v.length() < arena.goal_side_radius + arena.bottom_radius) {
                o = o + v.normalized() * (arena.goal_side_radius + arena.bottom_radius);
                dan = std::min(dan, dan_to_sphere_inner(
                        point,
                        Point(o.x, arena.bottom_radius, o.y),
                        arena.bottom_radius));
            }
            // Side x (goal)
            if (point.z >= (arena.depth / 2) + arena.goal_side_radius
               and point.x > (arena.goal_width / 2) - arena.bottom_radius) {
                dan = std::min(dan, dan_to_sphere_inner(
                        point, Point(
                                (arena.goal_width / 2) - arena.bottom_radius,
                                arena.bottom_radius,
                                point.z
                        ),
                        arena.bottom_radius));
            }
            // Corner
            if (point.x > (arena.width / 2) - arena.corner_radius
               and point.z > (arena.depth / 2) - arena.corner_radius) {
                auto corner_o = Point(
                        (arena.width / 2) - arena.corner_radius,
                        (arena.depth / 2) - arena.corner_radius,
                        0
                );
                auto n = Point(point.x, point.z, 0) - corner_o;
                auto dist = n.length();
                if (dist > arena.corner_radius - arena.bottom_radius) {
                    n = n / dist;
                    auto o2 = corner_o + n * (arena.corner_radius - arena.bottom_radius);
                    dan = std::min(dan, dan_to_sphere_inner(
                            point,
                            Point(o2.x, arena.bottom_radius, o2.y),
                            arena.bottom_radius));
                }
            }
        }
        // Ceiling corners
        if (point.y > arena.height - arena.top_radius) {
            // Side x
            if (point.x > (arena.width / 2) - arena.top_radius) {
                dan = std::min(dan, dan_to_sphere_inner(
                        point,
                        Point(
                                (arena.width / 2) - arena.top_radius,
                                arena.height - arena.top_radius,
                                point.z
                        ),
                        arena.top_radius));
            }
            // Side z
            if (point.z > (arena.depth / 2) - arena.top_radius) {
                dan = std::min(dan, dan_to_sphere_inner(
                        point,
                        Point(
                                point.x,
                                arena.height - arena.top_radius,
                                (arena.depth / 2) - arena.top_radius
                        ),
                        arena.top_radius));
            }
            // Corner
            if (point.x > (arena.width / 2) - arena.corner_radius
                and point.z > (arena.depth / 2) - arena.corner_radius) {
                auto corner_o = Point(
                        (arena.width / 2) - arena.corner_radius,
                        (arena.depth / 2) - arena.corner_radius,
                        0
                );
                auto dv = Point(point.x, point.z, 0) - corner_o;
                if (dv.length() > arena.corner_radius - arena.top_radius) {
                    auto n = dv.normalized();
                    auto o2 = corner_o + n * (arena.corner_radius - arena.top_radius);
                    dan = std::min(dan, dan_to_sphere_inner(
                            point,
                            Point(o2.x, arena.height - arena.top_radius, o2.y),
                            arena.top_radius));
                }
            }
        }
        return dan;
    }

    DistanceNormalPair dan_to_arena(Point point) {
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
        return result;
    }

    void collide_entities(Unit& a, Unit& b) {
        auto delta_position = b - a;
        auto distance = delta_position.length();
        auto penetration = a.radius + b.radius - distance;
        if (penetration > 0) {
            auto k_a = (1 / a.mass) / ((1 / a.mass) + (1 / b.mass));
            auto k_b = (1 / b.mass) / ((1 / a.mass) + (1 / b.mass));
            auto normal = delta_position.normalized();
            a -= normal * penetration * k_a;
            b += normal * penetration * k_b;
            auto delta_velocity = (b.velocity - a.velocity) * normal
                + b.radius_change_speed - a.radius_change_speed;
            if (delta_velocity < 0) {
                hasRandomCollision = true;
                auto impulse = normal * (1 + (MIN_HIT_E + MAX_HIT_E) / 2) * delta_velocity;
                a.velocity += impulse * k_a;
                b.velocity -= impulse * k_b;
            }
        }
    }

    void update(double delta_time) {
        //shuffle(robots)
        std::vector<ARobot*> robots = {&my[0], &my[1], &opp[0], &opp[1]};
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
        for (size_t i = 0; i < robots.size(); i++)
            for (size_t j = 0; j < i; j++)
                collide_entities(*robots[i], *robots[j]);

        for (auto& robot : robots) {
            collide_entities(*robot, ball);
            auto collision_normal = collide_with_arena(*robot);
            if (collision_normal.length2() < EPS) {
                robot->touch = false;
            } else {
                robot->touch = true;
                robot->touch_normal = collision_normal;
            }
        }

        collide_with_arena(ball);
        if (abs(ball.z) > arena.depth / 2 + ball.radius) {
            //goal_scored();
        }

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

    void doTick() {
        hasRandomCollision = false;
        auto delta_time = 1.0 / TICKS_PER_SECOND;
        for (int i = 0; i < MICROTICKS_PER_TICK; i++)
            update(delta_time / MICROTICKS_PER_TICK);
//        for pack in nitro_packs:
//            if pack.alive:
//                continue
//            pack.respawn_ticks -= 1
//            if pack.respawn_ticks == 0:
//                pack.alive = true

        tick++;
    }

    bool hasRandomCollision;
};

#endif //CODEBALL_SANDBOX_H
