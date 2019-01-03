#ifndef CODEBALL_SANDBOX_H
#define CODEBALL_SANDBOX_H

#include <vector>
#include <tuple>
#include <algorithm>
#include <iostream>
#include <optional>
#include "GameInfo.h"
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
    int tick = 0, roundTick = 0;
    int meId = 0;

    RandomGenerator rnd;

    Sandbox() {
    }

    Sandbox(const model::Game& game, const model::Rules& rules, int meId) : meId(meId) {
        tick = game.current_tick;
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
        Point normal;
        double penetration;
        if (dan_to_arena_new(e, penetration, normal)) {
            e += normal * penetration;
            auto velocity = e.velocity * normal; // radius_change_speed for ball = 0
            if (velocity < 0) {
                e.velocity -= normal * ((1 + BALL_ARENA_E) * velocity);
                return normal;
            }
        }
        return Point();
    }

    Point collide_with_arena(ARobot &e) {
        Point normal;
        double penetration;
        if (dan_to_arena_new(e, penetration, normal)) {
            e += normal * penetration;
            auto velocity = (e.velocity * normal) - e.radius_change_speed;
            if (velocity < 0) {
                e.velocity -= normal * velocity; // ROBOT_ARENA_E = 0
                return normal;
            }
        }
        return Point();
    }

    bool dan_to_arena_quarter(const Unit& point, double& distance, Point& normal) {

#define DAN_TO_PLANE(side, point_on_plane, plane_normal) {\
    distance = (point. side - (point_on_plane)) * (plane_normal);\
    if (distance > point.radius) return false;\
    normal. side = (plane_normal);\
    return true;\
}

#define DAN_TO_SPHERE_INNER(sphere_center_x, sphere_center_y, sphere_center_z, sphere_radius) {\
    const auto diff_x = (sphere_center_x) - point.x;\
    const auto diff_y = (sphere_center_y) - point.y;\
    const auto diff_z = (sphere_center_z) - point.z;\
    auto dist2 = SQR(diff_x) + SQR(diff_y) + SQR(diff_z);\
    distance = (sphere_radius) - sqrt(dist2);\
    if (distance > point.radius) return false;\
    normal.set(diff_x, diff_y, diff_z);\
    return true;\
}

#define DAN_TO_SPHERE_OUTER(sphere_center_x, sphere_center_y, sphere_center_z, sphere_radius) {\
    auto diff_x = point.x - (sphere_center_x);\
    auto diff_y = point.y - (sphere_center_y);\
    auto diff_z = point.z - (sphere_center_z);\
    auto dist2 = SQR(diff_x) + SQR(diff_y) + SQR(diff_z);\
    distance = sqrt(dist2) - (sphere_radius);\
    if (distance > point.radius) return false;\
    normal.set(diff_x, diff_y, diff_z);\
    return true;\
}

        if (point.y <= ARENA_BOTTOM_RADIUS) { // Bottom
            if (point.z <= ARENA_DEPTH/2 - ARENA_CORNER_RADIUS) {
                if (point.x <= ARENA_WIDTH/2 - ARENA_BOTTOM_RADIUS) { // Ground
                    DAN_TO_PLANE(y, 0, 1);
                }
                // Bottom right corner
                DAN_TO_SPHERE_INNER(
                        (ARENA_WIDTH / 2) - ARENA_BOTTOM_RADIUS,
                        ARENA_BOTTOM_RADIUS,
                        point.z,
                        ARENA_BOTTOM_RADIUS);
            }
            if (point.x >= ARENA_WIDTH/2 - ARENA_CORNER_RADIUS) { // Corner
                constexpr const auto corner_x = (ARENA_WIDTH / 2) - ARENA_CORNER_RADIUS;
                constexpr const auto corner_y = (ARENA_DEPTH / 2) - ARENA_CORNER_RADIUS;
                constexpr const auto groundRad = ARENA_CORNER_RADIUS - ARENA_BOTTOM_RADIUS;

                auto nx = point.x - corner_x;
                auto ny = point.z - corner_y;
                auto dist2 = SQR(nx) + SQR(ny);
                if (dist2 > SQR(groundRad)) {
                    auto dist = sqrt(dist2);
                    nx /= dist;
                    ny /= dist;
                    DAN_TO_SPHERE_INNER(
                            corner_x + nx * groundRad, ARENA_BOTTOM_RADIUS, corner_y + ny * groundRad,
                            ARENA_BOTTOM_RADIUS);
                }
                DAN_TO_PLANE(y, 0, 1);
            }
            if (point.z <= ARENA_DEPTH/2 - ARENA_BOTTOM_RADIUS) {
                DAN_TO_PLANE(y, 0, 1);
            }
            if (point.x <= ARENA_GOAL_WIDTH/2 - ARENA_BOTTOM_RADIUS) {
                if (point.z <= ARENA_DEPTH/2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) {
                    DAN_TO_PLANE(y, 0, 1);
                }
                // Side z (goal)
                DAN_TO_SPHERE_INNER(
                        point.x,
                        ARENA_BOTTOM_RADIUS,
                        (ARENA_DEPTH / 2) + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS,
                        ARENA_BOTTOM_RADIUS);
            }

            if (point.z <= ARENA_DEPTH/2 + ARENA_GOAL_SIDE_RADIUS) {
                if (point.x >= ARENA_GOAL_WIDTH/2 + ARENA_GOAL_SIDE_RADIUS) { // Side z
                    DAN_TO_SPHERE_INNER(
                            point.x,
                            ARENA_BOTTOM_RADIUS,
                            (ARENA_DEPTH / 2) - ARENA_BOTTOM_RADIUS,
                            ARENA_BOTTOM_RADIUS);
                }
                // Goal outer corner
                constexpr const auto ox = ARENA_GOAL_WIDTH / 2 + ARENA_GOAL_SIDE_RADIUS;
                constexpr const auto oy = ARENA_DEPTH / 2 + ARENA_GOAL_SIDE_RADIUS;
                constexpr const auto rad = ARENA_GOAL_SIDE_RADIUS + ARENA_BOTTOM_RADIUS;
                auto vx = point.x - ox;
                auto vy = point.z - oy;
                const auto vlen2 = SQR(vx) + SQR(vy);
                if (vlen2 < SQR(rad)) {
                    const auto vlen = sqrt(vlen2);
                    vx /= vlen;
                    vy /= vlen;
                    DAN_TO_SPHERE_INNER(
                            ox + vx * rad, ARENA_BOTTOM_RADIUS, oy + vy * rad,
                            ARENA_BOTTOM_RADIUS);
                }
                DAN_TO_PLANE(y, 0, 1);
            }

            if (point.z <= ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) { // Side x (goal)
                DAN_TO_SPHERE_INNER(
                        ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS,
                        ARENA_BOTTOM_RADIUS,
                        point.z,
                        ARENA_BOTTOM_RADIUS);
            }
            // Goal inner bottom corner
            DAN_TO_SPHERE_INNER(
                    ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS,
                    ARENA_BOTTOM_RADIUS,
                    ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS,
                    ARENA_BOTTOM_RADIUS);
        }
        if (point.y >= ARENA_HEIGHT - ARENA_TOP_RADIUS) { // Top
            if (point.z <= ARENA_DEPTH/2 - ARENA_CORNER_RADIUS) {
                if (point.x <= ARENA_WIDTH/2 - ARENA_TOP_RADIUS) { // Ceiling
                    DAN_TO_PLANE(y, ARENA_HEIGHT, -1);
                }
                // Top right corner
                DAN_TO_SPHERE_INNER(
                        (ARENA_WIDTH / 2) - ARENA_TOP_RADIUS,
                        ARENA_HEIGHT - ARENA_TOP_RADIUS,
                        point.z,
                        ARENA_TOP_RADIUS);
            }

            if (point.x >= ARENA_WIDTH/2 - ARENA_CORNER_RADIUS) { // Corner
                constexpr const auto corner_x = (ARENA_WIDTH / 2) - ARENA_CORNER_RADIUS;
                constexpr const auto corner_y = (ARENA_DEPTH / 2) - ARENA_CORNER_RADIUS;
                constexpr const auto groundRad = ARENA_CORNER_RADIUS - ARENA_TOP_RADIUS;

                auto nx = point.x - corner_x;
                auto ny = point.z - corner_y;
                auto dist2 = SQR(nx) + SQR(ny);
                if (dist2 > SQR(groundRad)) {
                    auto dist = sqrt(dist2);
                    nx /= dist;
                    ny /= dist;
                    DAN_TO_SPHERE_INNER(
                            corner_x + nx * groundRad, ARENA_HEIGHT - ARENA_TOP_RADIUS, corner_y + ny * groundRad,
                            ARENA_TOP_RADIUS);
                }
                DAN_TO_PLANE(y, ARENA_HEIGHT, -1);
            }
            if (point.z <= ARENA_DEPTH/2 - ARENA_TOP_RADIUS) {
                DAN_TO_PLANE(y, ARENA_HEIGHT, -1);
            }
            // Top upper corner
            DAN_TO_SPHERE_INNER(
                    point.x,
                    ARENA_HEIGHT - ARENA_TOP_RADIUS,
                    (ARENA_DEPTH / 2) - ARENA_TOP_RADIUS,
                    ARENA_TOP_RADIUS);
        }
        // Middle

        if (point.z <= ARENA_DEPTH/2 - ARENA_CORNER_RADIUS) { // Side x
            DAN_TO_PLANE(x, ARENA_WIDTH / 2, -1);
        }
        if (point.x >= ARENA_WIDTH/2 - ARENA_CORNER_RADIUS) { // Corner
            DAN_TO_SPHERE_INNER(
                    (ARENA_WIDTH / 2) - ARENA_CORNER_RADIUS,
                    point.y,
                    (ARENA_DEPTH / 2) - ARENA_CORNER_RADIUS,
                    ARENA_CORNER_RADIUS);
        }
        if (point.z < ARENA_DEPTH/2 - point.radius) {
            return false;
        }

        if (point.z >= ARENA_DEPTH/2 + ARENA_GOAL_SIDE_RADIUS) { // Goal
            if (point.y > ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS) {
                if (point.x > ARENA_GOAL_WIDTH/2 - ARENA_BOTTOM_RADIUS) {
                    if (point.z <= ARENA_DEPTH/2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) { // Side x top (goal)
                        DAN_TO_SPHERE_INNER(
                                ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS,
                                ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS,
                                point.z,
                                ARENA_BOTTOM_RADIUS);
                    }
                    // Goal inner top corner
                    DAN_TO_SPHERE_INNER(
                            ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS,
                            ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS,
                            ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS,
                            ARENA_BOTTOM_RADIUS);
                }
                if (point.z > ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) { // Side z top (goal)
                    DAN_TO_SPHERE_INNER(
                            point.x,
                            ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS,
                            ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS,
                            ARENA_BOTTOM_RADIUS);
                }
                // Ceiling (goal)
                DAN_TO_PLANE(y, ARENA_GOAL_HEIGHT, -1);
            }
            if (point.x > ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS) {
                if (point.z > ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) {
                    DAN_TO_SPHERE_INNER(
                            ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS,
                            point.y,
                            ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS,
                            ARENA_BOTTOM_RADIUS);
                }
                // Size x (goal)
                DAN_TO_PLANE(x, ARENA_GOAL_WIDTH / 2, -1);
            }
            // Side z (goal)
            DAN_TO_PLANE(z, ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH, -1);
        }
        if (point.x <= ARENA_GOAL_WIDTH/2 - ARENA_BOTTOM_RADIUS) {
            if (point.y >= ARENA_GOAL_HEIGHT + ARENA_GOAL_SIDE_RADIUS) {
                DAN_TO_PLANE(z, ARENA_DEPTH / 2, -1);
            }
            // Goal ceiling
            DAN_TO_SPHERE_OUTER(
                    point.x,
                    ARENA_GOAL_HEIGHT + ARENA_GOAL_SIDE_RADIUS,
                    ARENA_DEPTH / 2 + ARENA_GOAL_SIDE_RADIUS,
                    ARENA_GOAL_SIDE_RADIUS);
        }
        if (point.y <= ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS) {
            if (point.x >= ARENA_GOAL_WIDTH/2 + ARENA_GOAL_SIDE_RADIUS) {
                DAN_TO_PLANE(z, ARENA_DEPTH / 2, -1);
            }
            // Goal right
            DAN_TO_SPHERE_OUTER(
                    ARENA_GOAL_WIDTH / 2 + ARENA_GOAL_SIDE_RADIUS,
                    point.y,
                    ARENA_DEPTH / 2 + ARENA_GOAL_SIDE_RADIUS,
                    ARENA_GOAL_SIDE_RADIUS);
        }
        // Top right corner
        constexpr const auto ox = ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS;
        constexpr const auto oy = ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS;
        constexpr const auto rad = ARENA_GOAL_TOP_RADIUS + ARENA_GOAL_SIDE_RADIUS;
        auto vx = point.x - ox;
        auto vy = point.y - oy;
        const auto len2 = SQR(vx) + SQR(vy);

        if (len2 >= SQR(rad)) {
            DAN_TO_PLANE(z, ARENA_DEPTH / 2, -1);
        }
        const auto len = sqrt(len2);
        vx /= len;
        vy /= len;
        DAN_TO_SPHERE_OUTER(
                ox + vx * rad, oy + vy * rad, ARENA_DEPTH / 2 + ARENA_GOAL_SIDE_RADIUS,
                ARENA_GOAL_SIDE_RADIUS);

#undef DAN_TO_PLANE
#undef DAN_TO_SPHERE_INNER
#undef DAN_TO_SPHERE_OUTER
    }

    bool dan_to_arena_quarter2(const Unit& point, double& distance, Point& normal) {

#if M_LOG_DANS
#define LOG_DAN(id) Logger::instance()->dans[id]++;
#else
#define LOG_DAN(id)
#endif

#define DAN_TO_PLANE(side, point_on_plane, plane_normal) {\
    LOG_DAN(__LINE__);\
    distance = (point. side - (point_on_plane)) * (plane_normal);\
    if (distance > point.radius) return false;\
    normal. side = (plane_normal);\
    return true;\
}

#define DAN_TO_SPHERE_INNER(sphere_center_x, sphere_center_y, sphere_center_z, sphere_radius) {\
    LOG_DAN(__LINE__);\
    const auto diff_x = (sphere_center_x) - point.x;\
    const auto diff_y = (sphere_center_y) - point.y;\
    const auto diff_z = (sphere_center_z) - point.z;\
    auto dist2 = SQR(diff_x) + SQR(diff_y) + SQR(diff_z);\
    distance = (sphere_radius) - sqrt(dist2);\
    if (distance > point.radius) return false;\
    normal.set(diff_x, diff_y, diff_z);\
    return true;\
}

#define DAN_TO_CYLINDER_Z_INNER(sphere_center_x, sphere_center_y, sphere_radius) {\
    LOG_DAN(__LINE__);\
    const auto diff_x = (sphere_center_x) - point.x;\
    const auto diff_y = (sphere_center_y) - point.y;\
    auto dist2 = SQR(diff_x) + SQR(diff_y);\
    distance = (sphere_radius) - sqrt(dist2);\
    if (distance > point.radius) return false;\
    normal.set(diff_x, diff_y, 0);\
    return true;\
}

#define DAN_TO_CYLINDER_X_INNER(sphere_center_y, sphere_center_z, sphere_radius) {\
    LOG_DAN(__LINE__);\
    const auto diff_y = (sphere_center_y) - point.y;\
    const auto diff_z = (sphere_center_z) - point.z;\
    auto dist2 = SQR(diff_y) + SQR(diff_z);\
    distance = (sphere_radius) - sqrt(dist2);\
    if (distance > point.radius) return false;\
    normal.set(0, diff_y, diff_z);\
    return true;\
}

#define DAN_TO_CYLINDER_Y_INNER(sphere_center_x, sphere_center_z, sphere_radius) {\
    LOG_DAN(__LINE__);\
    const auto diff_x = (sphere_center_x) - point.x;\
    const auto diff_z = (sphere_center_z) - point.z;\
    auto dist2 = SQR(diff_x) + SQR(diff_z);\
    distance = (sphere_radius) - sqrt(dist2);\
    if (distance > point.radius) return false;\
    normal.set(diff_x, 0, diff_z);\
    return true;\
}

#define DAN_TO_SPHERE_OUTER(sphere_center_x, sphere_center_y, sphere_center_z, sphere_radius) {\
    LOG_DAN(__LINE__);\
    auto diff_x = point.x - (sphere_center_x);\
    auto diff_y = point.y - (sphere_center_y);\
    auto diff_z = point.z - (sphere_center_z);\
    auto dist2 = SQR(diff_x) + SQR(diff_y) + SQR(diff_z);\
    distance = sqrt(dist2) - (sphere_radius);\
    if (distance > point.radius) return false;\
    normal.set(diff_x, diff_y, diff_z);\
    return true;\
}

#define DAN_TO_CYLINDER_X_OUTER(sphere_center_y, sphere_center_z, sphere_radius) {\
    LOG_DAN(__LINE__);\
    auto diff_y = point.y - (sphere_center_y);\
    auto diff_z = point.z - (sphere_center_z);\
    auto dist2 = SQR(diff_y) + SQR(diff_z);\
    distance = sqrt(dist2) - (sphere_radius);\
    if (distance > point.radius) return false;\
    normal.set(0, diff_y, diff_z);\
    return true;\
}

#define DAN_TO_CYLINDER_Y_OUTER(sphere_center_x, sphere_center_z, sphere_radius) {\
    LOG_DAN(__LINE__);\
    auto diff_x = point.x - (sphere_center_x);\
    auto diff_z = point.z - (sphere_center_z);\
    auto dist2 = SQR(diff_x) + SQR(diff_z);\
    distance = sqrt(dist2) - (sphere_radius);\
    if (distance > point.radius) return false;\
    normal.set(diff_x, 0, diff_z);\
    return true;\
}

        if (point.y <= ARENA_BOTTOM_RADIUS) { // Bottom
            if (point.z <= ARENA_DEPTH/2 - ARENA_CORNER_RADIUS) {
                if (point.x <= ARENA_WIDTH/2 - ARENA_BOTTOM_RADIUS) { // Ground
                    DAN_TO_PLANE(y, 0, 1);
                }
                // Bottom right corner
                DAN_TO_CYLINDER_Z_INNER(
                        (ARENA_WIDTH / 2) - ARENA_BOTTOM_RADIUS,
                        ARENA_BOTTOM_RADIUS,
                        ARENA_BOTTOM_RADIUS);
            }
            if (point.x >= ARENA_WIDTH/2 - ARENA_CORNER_RADIUS) { // Corner
                constexpr const auto corner_x = (ARENA_WIDTH / 2) - ARENA_CORNER_RADIUS;
                constexpr const auto corner_y = (ARENA_DEPTH / 2) - ARENA_CORNER_RADIUS;
                constexpr const auto groundRad = ARENA_CORNER_RADIUS - ARENA_BOTTOM_RADIUS;

                auto nx = point.x - corner_x;
                auto ny = point.z - corner_y;
                auto dist2 = SQR(nx) + SQR(ny);
                if (dist2 > SQR(groundRad)) {
                    auto dist = sqrt(dist2);
                    DAN_TO_SPHERE_INNER(
                            corner_x + nx / dist * groundRad, ARENA_BOTTOM_RADIUS, corner_y + ny / dist * groundRad,
                            ARENA_BOTTOM_RADIUS);
                }
                DAN_TO_PLANE(y, 0, 1);
            }
            if (point.z <= ARENA_DEPTH/2 - ARENA_BOTTOM_RADIUS) {
                DAN_TO_PLANE(y, 0, 1);
            }
            if (point.x <= ARENA_GOAL_WIDTH/2 - ARENA_BOTTOM_RADIUS) {
                if (point.z <= ARENA_DEPTH/2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) {
                    DAN_TO_PLANE(y, 0, 1);
                }
                // Side z (goal)
                DAN_TO_CYLINDER_X_INNER(
                        ARENA_BOTTOM_RADIUS,
                        (ARENA_DEPTH / 2) + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS,
                        ARENA_BOTTOM_RADIUS);
            }

            if (point.z <= ARENA_DEPTH/2 + ARENA_GOAL_SIDE_RADIUS) {
                if (point.x >= ARENA_GOAL_WIDTH/2 + ARENA_GOAL_SIDE_RADIUS) { // Side z
                    DAN_TO_CYLINDER_X_INNER(
                            ARENA_BOTTOM_RADIUS,
                            (ARENA_DEPTH / 2) - ARENA_BOTTOM_RADIUS,
                            ARENA_BOTTOM_RADIUS);
                }
                // Goal outer corner
                constexpr const auto ox = ARENA_GOAL_WIDTH / 2 + ARENA_GOAL_SIDE_RADIUS;
                constexpr const auto oy = ARENA_DEPTH / 2 + ARENA_GOAL_SIDE_RADIUS;
                constexpr const auto rad = ARENA_GOAL_SIDE_RADIUS + ARENA_BOTTOM_RADIUS;
                auto vx = point.x - ox;
                auto vy = point.z - oy;
                const auto vlen2 = SQR(vx) + SQR(vy);
                if (vlen2 < SQR(rad)) {
                    const auto vlen = sqrt(vlen2);
                    DAN_TO_SPHERE_INNER(
                            ox + vx / vlen * rad, ARENA_BOTTOM_RADIUS, oy + vy / vlen * rad,
                            ARENA_BOTTOM_RADIUS);
                }
                DAN_TO_PLANE(y, 0, 1);
            }

            if (point.z <= ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) { // Side x (goal)
                DAN_TO_CYLINDER_Z_INNER(
                        ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS,
                        ARENA_BOTTOM_RADIUS,
                        ARENA_BOTTOM_RADIUS);
            }
            // Goal inner bottom corner
            DAN_TO_SPHERE_INNER(
                    ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS,
                    ARENA_BOTTOM_RADIUS,
                    ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS,
                    ARENA_BOTTOM_RADIUS);
        }
        if (point.y >= ARENA_HEIGHT - ARENA_TOP_RADIUS) { // Top
            if (point.z <= ARENA_DEPTH/2 - ARENA_CORNER_RADIUS) {
                if (point.x <= ARENA_WIDTH/2 - ARENA_TOP_RADIUS) { // Ceiling
                    DAN_TO_PLANE(y, ARENA_HEIGHT, -1);
                }
                // Top right corner
                DAN_TO_CYLINDER_Z_INNER(
                        (ARENA_WIDTH / 2) - ARENA_TOP_RADIUS,
                        ARENA_HEIGHT - ARENA_TOP_RADIUS,
                        ARENA_TOP_RADIUS);
            }

            if (point.x >= ARENA_WIDTH/2 - ARENA_CORNER_RADIUS) { // Corner
                constexpr const auto corner_x = (ARENA_WIDTH / 2) - ARENA_CORNER_RADIUS;
                constexpr const auto corner_y = (ARENA_DEPTH / 2) - ARENA_CORNER_RADIUS;
                constexpr const auto groundRad = ARENA_CORNER_RADIUS - ARENA_TOP_RADIUS;

                auto nx = point.x - corner_x;
                auto ny = point.z - corner_y;
                auto dist2 = SQR(nx) + SQR(ny);
                if (dist2 > SQR(groundRad)) {
                    auto dist = sqrt(dist2);
                    DAN_TO_SPHERE_INNER(
                            corner_x + nx / dist * groundRad, ARENA_HEIGHT - ARENA_TOP_RADIUS, corner_y + ny / dist * groundRad,
                            ARENA_TOP_RADIUS);
                }
                DAN_TO_PLANE(y, ARENA_HEIGHT, -1);
            }
            if (point.z <= ARENA_DEPTH/2 - ARENA_TOP_RADIUS) {
                DAN_TO_PLANE(y, ARENA_HEIGHT, -1);
            }
            // Top upper corner
            DAN_TO_CYLINDER_X_INNER(
                    ARENA_HEIGHT - ARENA_TOP_RADIUS,
                    (ARENA_DEPTH / 2) - ARENA_TOP_RADIUS,
                    ARENA_TOP_RADIUS);
        }
        // Middle

        if (point.z <= ARENA_DEPTH/2 - ARENA_CORNER_RADIUS) { // Side x
            DAN_TO_PLANE(x, ARENA_WIDTH / 2, -1);
        }
        if (point.x >= ARENA_WIDTH/2 - ARENA_CORNER_RADIUS) { // Corner
            DAN_TO_CYLINDER_Y_INNER(
                    (ARENA_WIDTH / 2) - ARENA_CORNER_RADIUS,
                    (ARENA_DEPTH / 2) - ARENA_CORNER_RADIUS,
                    ARENA_CORNER_RADIUS);
        }
        if (point.z < ARENA_DEPTH/2 - point.radius) {
            return false;
        }

        if (point.z >= ARENA_DEPTH/2 + ARENA_GOAL_SIDE_RADIUS) { // Goal
            if (point.y > ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS) {
                if (point.x > ARENA_GOAL_WIDTH/2 - ARENA_BOTTOM_RADIUS) {
                    if (point.z <= ARENA_DEPTH/2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) { // Side x top (goal)
                        DAN_TO_CYLINDER_Z_INNER(
                                ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS,
                                ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS,
                                ARENA_BOTTOM_RADIUS);
                    }
                    // Goal inner top corner
                    DAN_TO_SPHERE_INNER(
                            ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS,
                            ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS,
                            ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS,
                            ARENA_BOTTOM_RADIUS);
                }
                if (point.z > ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) { // Side z top (goal)
                    DAN_TO_CYLINDER_X_INNER(
                            ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS,
                            ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS,
                            ARENA_BOTTOM_RADIUS);
                }
                // Ceiling (goal)
                DAN_TO_PLANE(y, ARENA_GOAL_HEIGHT, -1);
            }
            if (point.x > ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS) {
                if (point.z > ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) {
                    DAN_TO_CYLINDER_Y_INNER(
                            ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS,
                            ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS,
                            ARENA_BOTTOM_RADIUS);
                }
                // Size x (goal)
                DAN_TO_PLANE(x, ARENA_GOAL_WIDTH / 2, -1);
            }
            // Side z (goal)
            DAN_TO_PLANE(z, ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH, -1);
        }
        if (point.x <= ARENA_GOAL_WIDTH/2 - ARENA_BOTTOM_RADIUS) {
            if (point.y >= ARENA_GOAL_HEIGHT + ARENA_GOAL_SIDE_RADIUS) {
                DAN_TO_PLANE(z, ARENA_DEPTH / 2, -1);
            }
            // Goal ceiling
            DAN_TO_CYLINDER_X_OUTER(
                    ARENA_GOAL_HEIGHT + ARENA_GOAL_SIDE_RADIUS,
                    ARENA_DEPTH / 2 + ARENA_GOAL_SIDE_RADIUS,
                    ARENA_GOAL_SIDE_RADIUS);
        }
        if (point.y <= ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS) {
            if (point.x >= ARENA_GOAL_WIDTH/2 + ARENA_GOAL_SIDE_RADIUS) {
                DAN_TO_PLANE(z, ARENA_DEPTH / 2, -1);
            }
            // Goal right
            DAN_TO_CYLINDER_Y_OUTER(
                    ARENA_GOAL_WIDTH / 2 + ARENA_GOAL_SIDE_RADIUS,
                    ARENA_DEPTH / 2 + ARENA_GOAL_SIDE_RADIUS,
                    ARENA_GOAL_SIDE_RADIUS);
        }
        // Top right corner
        constexpr const auto ox = ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS;
        constexpr const auto oy = ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS;
        constexpr const auto rad = ARENA_GOAL_TOP_RADIUS + ARENA_GOAL_SIDE_RADIUS;
        auto vx = point.x - ox;
        auto vy = point.y - oy;
        const auto len2 = SQR(vx) + SQR(vy);

        if (len2 >= SQR(rad)) {
            DAN_TO_PLANE(z, ARENA_DEPTH / 2, -1);
        }
        const auto len = sqrt(len2);
        DAN_TO_SPHERE_OUTER(
                ox + vx / len * rad, oy + vy / len * rad, ARENA_DEPTH / 2 + ARENA_GOAL_SIDE_RADIUS,
                ARENA_GOAL_SIDE_RADIUS);

#undef DAN_TO_PLANE
#undef DAN_TO_SPHERE_INNER
#undef DAN_TO_SPHERE_OUTER
#undef DAN_TO_CYLINDER_X_INNER
#undef DAN_TO_CYLINDER_Y_INNER
#undef DAN_TO_CYLINDER_Z_INNER
#undef DAN_TO_CYLINDER_X_OUTER
#undef DAN_TO_CYLINDER_Y_OUTER
    }


    bool dan_to_arena_old(Unit point, double& penetration, Point& normal) {
        auto negate_x = point.x < 0;
        auto negate_z = point.z < 0;
        if (negate_x)
            point.x = -point.x;
        if (negate_z)
            point.z = -point.z;
        double distance;
        if (!dan_to_arena_quarter(point, distance, normal)) {
            return false;
        }

        if (negate_x)
            normal.x = -normal.x;
        if (negate_z)
            normal.z = -normal.z;

        normal.normalize();
        penetration = point.radius - distance;
        return true;
    }

    bool dan_to_arena_new(Unit point, double& penetration, Point& normal) {
        auto negate_x = point.x < 0;
        auto negate_z = point.z < 0;
        if (negate_x)
            point.x = -point.x;
        if (negate_z)
            point.z = -point.z;
        double distance;
        if (!dan_to_arena_quarter2(point, distance, normal)) {
            return false;
        }

        if (negate_x)
            normal.x = -normal.x;
        if (negate_z)
            normal.z = -normal.z;

        normal.normalize();
        penetration = point.radius - distance;
        return true;
    }

    bool dan_to_arena(Unit point, double& penetration, Point& normal) {
        Point no, nn;
        double po, pn;
        auto r_old = dan_to_arena_old(point, po, no);
        auto r_new = dan_to_arena_new(point, pn, nn);

        if (r_old != r_new) {
            std::cerr << "err\n";
            dan_to_arena_old(point, po, no);
            dan_to_arena_new(point, pn, nn);
            dan_to_arena_old(point, po, no);
            dan_to_arena_new(point, pn, nn);
            exit(1);
        }
        if (r_new && !no.equals(nn, 1e-7)) {
            std::cerr << "err2\n";
            dan_to_arena_old(point, po, no);
            dan_to_arena_new(point, pn, nn);
            dan_to_arena_old(point, po, no);
            dan_to_arena_new(point, pn, nn);
            exit(1);
        }
        penetration = pn;
        normal = nn;
        return r_new;
    }

    struct CollisionInfo {
        int id;
        Point velocity;
    };

    std::vector<CollisionInfo> robotBallCollisions;
    
    template<typename T>
    void collide_entities(ARobot& a, T& b) {
        constexpr const double secondMass = std::is_same<T, ARobot>::value ? ROBOT_MASS : BALL_MASS;
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

        constexpr const auto k_a = (1.0 / ROBOT_MASS) / ((1.0 / ROBOT_MASS) + (1.0 / secondMass));
        constexpr const auto k_b = (1.0 / secondMass) / ((1.0 / ROBOT_MASS) + (1.0 / secondMass));

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
                (b.velocity.z - a.velocity.z) * normalZ - secondRadiusChangeSpeed - a.radius_change_speed;
        if (delta_velocity < 0) {
#if M_NO_RANDOM
            auto rndValue = MAX_HIT_E;
#else
            hasRandomCollision = true;
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

        if constexpr (std::is_same<T, ABall>::value) {
            robotBallCollisions.push_back({a.id, b.velocity});
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
                auto targetVelocity = robot.action.targetVelocity;
                targetVelocity.clamp(ROBOT_MAX_GROUND_SPEED);

                auto dot = robot.touch_normal * targetVelocity;
                targetVelocity.x -= robot.touch_normal.x * dot + robot.velocity.x;
                targetVelocity.y -= robot.touch_normal.y * dot + robot.velocity.y;
                targetVelocity.z -= robot.touch_normal.z * dot + robot.velocity.z;

                auto len2 = targetVelocity.length2();
                if (len2 > 0) {
                    auto acceleration = robot.touch_normal.y <= 0 ? 0 : ROBOT_ACCELERATION * delta_time * robot.touch_normal.y;

                    if (SQR(acceleration) < len2) {
                        targetVelocity *= acceleration / sqrt(len2);
                    }
                    robot.velocity += targetVelocity;
                }
            }
//            if (robot.action.use_nitro) {
//                auto target_velocity_change = (
//                    robot.action.target_velocity - robot.velocity,
//                    ).clamped(robot.nitro * NITRO_POINT_VELOCITY_CHANGE);
//                if (length(target_velocity_change) > 0) {
//                    auto acceleration = target_velocity_change.normalized() * ROBOT_NITRO_ACCELERATION;
//                    auto velocity_change = (acceleration * delta_time).clamped(target_velocity_change.length());
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

    void clearRobots() {
        my.clear();
        opp.clear();
    }

    void doTick(int microticksPerTick = MICROTICKS_PER_TICK) {
        OP_START(DO_TICK);

        hasRandomCollision = false;
        robotBallCollisions.clear();
        auto delta_time = 1.0 / TICKS_PER_SECOND;
        if (tick < GameInfo::maxTickCount) {
            for (int i = 0; i < microticksPerTick && hasGoal == 0; i++)
                update(delta_time / microticksPerTick);
//        for pack in nitro_packs:
//            if pack.alive:
//                continue
//            pack.respawn_ticks -= 1
//            if pack.respawn_ticks == 0:
//                pack.alive = true
        }

        tick++;
        roundTick++;
        // TODO: clear actions

        OP_END(DO_TICK);
    }

    bool hasRandomCollision = false;
    int hasGoal = 0; // -1, 0, 1
};

#endif //CODEBALL_SANDBOX_H
