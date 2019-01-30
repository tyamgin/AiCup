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
#include "NitroPack.h"
#include "Visualizer.h"
#include "RandomGenerators.h"
#include "Logger.h"
#include "Helper.h"

#define M_COLL_MASK(id) (1ull << uint64_t((id) - 1))
#define M_COLL_MASK2(id1, id2) (1ull << uint64_t(((id1) - 1) * 6 + (id2) - 1))

struct Sandbox {
    ABall ball;
    std::vector<ARobot> my, opp;
    std::vector<ANitroPack> nitroPacks;
    int tick = 0, roundTick = 0;
    int meIdx = 0;

    RandomGenerator rnd;
    bool stopOnGoal = true;
    bool deduceOppSimple = true;
    bool oppGkStrat = false;
    int oppCounterStrat = 0; // 0, 1, 2, 3

    static std::vector<ABall> _ballsCache;
    int _cacheIterator = 0;
    bool _ballsCacheValid = true;

    static void loadBallsCache(const std::vector<ABall>& cache) {
        _ballsCache = cache;
    }

    static std::vector<AAction> _actionsCache[7];

    Sandbox() {
    }

    Sandbox(const model::Game& game, const model::Rules& rules, int meId) {
        tick = game.current_tick;
        for (auto& r : game.robots) {
            if (r.is_teammate) {
                if (r.id == meId) {
                    meIdx = my.size();
                }
                my.emplace_back(r);
            } else {
                opp.emplace_back(r);
            }
        }
        for (auto& p : game.nitro_packs) {
            nitroPacks.emplace_back(p);
        }
        std::sort(nitroPacks.begin(), nitroPacks.end());
        ball = ABall(game.ball);
        checkGoal();
    }

    ARobot* me() {
#ifdef LOCAL
        if ((int) my.size() <= meIdx) {
            std::cerr << "Sandbox::me() illegal call" << std::endl;
            exit(1);
        }
#endif
        return &my[meIdx];
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


    static void collide_with_arena(ABall& e) {
        Point normal;
        double penetration;
        if (dan_to_arena_new(e, penetration, normal)) {
            e += normal * penetration;
            auto velocity = e.velocity * normal; // radius_change_speed for ball = 0
            if (velocity < 0) {
                e.velocity -= normal * ((1 + BALL_ARENA_E) * velocity);
            }
        }
    }

    static bool collide_with_arena(ARobot& e, Point& normal) {
        double penetration;
        if (dan_to_arena_new(e, penetration, normal)) {
            e += normal * penetration;
            auto velocity = (e.velocity * normal) - e.radius_change_speed;
            if (velocity < 0) {
                e.velocity -= normal * velocity; // ROBOT_ARENA_E = 0
                return true;
            }
        }
        return false;
    }

    static bool dan_to_arena_quarter2(const Unit& point, double& distance, Point& normal) {

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
    if (SQR((sphere_radius) - point.radius) > dist2) return false;\
    distance = (sphere_radius) - sqrt(dist2);\
    normal.set(diff_x, diff_y, diff_z);\
    return true;\
}

#define DAN_TO_CYLINDER_Z_INNER(sphere_center_x, sphere_center_y, sphere_radius) {\
    LOG_DAN(__LINE__);\
    const auto diff_x = (sphere_center_x) - point.x;\
    const auto diff_y = (sphere_center_y) - point.y;\
    auto dist2 = SQR(diff_x) + SQR(diff_y);\
    if (SQR((sphere_radius) - point.radius) > dist2) return false;\
    distance = (sphere_radius) - sqrt(dist2);\
    normal.set(diff_x, diff_y, 0);\
    return true;\
}

#define DAN_TO_CYLINDER_X_INNER(sphere_center_y, sphere_center_z, sphere_radius) {\
    LOG_DAN(__LINE__);\
    const auto diff_y = (sphere_center_y) - point.y;\
    const auto diff_z = (sphere_center_z) - point.z;\
    auto dist2 = SQR(diff_y) + SQR(diff_z);\
    if (SQR((sphere_radius) - point.radius) > dist2) return false;\
    distance = (sphere_radius) - sqrt(dist2);\
    normal.set(0, diff_y, diff_z);\
    return true;\
}

#define DAN_TO_CYLINDER_Y_INNER(sphere_center_x, sphere_center_z, sphere_radius) {\
    LOG_DAN(__LINE__);\
    const auto diff_x = (sphere_center_x) - point.x;\
    const auto diff_z = (sphere_center_z) - point.z;\
    auto dist2 = SQR(diff_x) + SQR(diff_z);\
    if (SQR((sphere_radius) - point.radius) > dist2) return false;\
    distance = (sphere_radius) - sqrt(dist2);\
    normal.set(diff_x, 0, diff_z);\
    return true;\
}

#define DAN_TO_SPHERE_OUTER(sphere_center_x, sphere_center_y, sphere_center_z, sphere_radius) {\
    LOG_DAN(__LINE__);\
    auto diff_x = point.x - (sphere_center_x);\
    auto diff_y = point.y - (sphere_center_y);\
    auto diff_z = point.z - (sphere_center_z);\
    auto dist2 = SQR(diff_x) + SQR(diff_y) + SQR(diff_z);\
    if (dist2 > SQR(point.radius + (sphere_radius))) return false;\
    distance = sqrt(dist2) - (sphere_radius);\
    normal.set(diff_x, diff_y, diff_z);\
    return true;\
}

#define DAN_TO_CYLINDER_X_OUTER(sphere_center_y, sphere_center_z, sphere_radius) {\
    LOG_DAN(__LINE__);\
    auto diff_y = point.y - (sphere_center_y);\
    auto diff_z = point.z - (sphere_center_z);\
    auto dist2 = SQR(diff_y) + SQR(diff_z);\
    if (dist2 > SQR(point.radius + (sphere_radius))) return false;\
    distance = sqrt(dist2) - (sphere_radius);\
    normal.set(0, diff_y, diff_z);\
    return true;\
}

#define DAN_TO_CYLINDER_Y_OUTER(sphere_center_x, sphere_center_z, sphere_radius) {\
    LOG_DAN(__LINE__);\
    auto diff_x = point.x - (sphere_center_x);\
    auto diff_z = point.z - (sphere_center_z);\
    auto dist2 = SQR(diff_x) + SQR(diff_z);\
    if (dist2 > SQR(point.radius + (sphere_radius))) return false;\
    distance = sqrt(dist2) - (sphere_radius);\
    normal.set(diff_x, 0, diff_z);\
    return true;\
}

        if (point.y <= ARENA_BOTTOM_RADIUS) { // Bottom
            if (point.z <= ARENA_Z - ARENA_CORNER_RADIUS) {
                if (point.x <= ARENA_X - ARENA_BOTTOM_RADIUS) { // Ground
                    DAN_TO_PLANE(y, 0, 1);
                }
                // Bottom right corner
                DAN_TO_CYLINDER_Z_INNER(
                        ARENA_X - ARENA_BOTTOM_RADIUS,
                        ARENA_BOTTOM_RADIUS,
                        ARENA_BOTTOM_RADIUS);
            }
            if (point.x >= ARENA_X - ARENA_CORNER_RADIUS) { // Corner
                constexpr const auto corner_x = ARENA_X - ARENA_CORNER_RADIUS;
                constexpr const auto corner_y = ARENA_Z - ARENA_CORNER_RADIUS;
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
            if (point.z <= ARENA_Z - ARENA_BOTTOM_RADIUS) {
                DAN_TO_PLANE(y, 0, 1);
            }
            if (point.x <= ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS) {
                if (point.z <= ARENA_Z + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) {
                    DAN_TO_PLANE(y, 0, 1);
                }
                // Side z (goal)
                DAN_TO_CYLINDER_X_INNER(
                        ARENA_BOTTOM_RADIUS,
                        ARENA_Z + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS,
                        ARENA_BOTTOM_RADIUS);
            }

            if (point.z <= ARENA_Z + ARENA_GOAL_SIDE_RADIUS) {
                if (point.x >= ARENA_GOAL_WIDTH / 2 + ARENA_GOAL_SIDE_RADIUS) { // Side z
                    DAN_TO_CYLINDER_X_INNER(
                            ARENA_BOTTOM_RADIUS,
                            ARENA_Z - ARENA_BOTTOM_RADIUS,
                            ARENA_BOTTOM_RADIUS);
                }
                // Goal outer corner
                constexpr const auto ox = ARENA_GOAL_WIDTH / 2 + ARENA_GOAL_SIDE_RADIUS;
                constexpr const auto oy = ARENA_Z + ARENA_GOAL_SIDE_RADIUS;
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

            if (point.z <= ARENA_Z + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) { // Side x (goal)
                DAN_TO_CYLINDER_Z_INNER(
                        ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS,
                        ARENA_BOTTOM_RADIUS,
                        ARENA_BOTTOM_RADIUS);
            }
            // Goal inner bottom corner
            DAN_TO_SPHERE_INNER(
                    ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS,
                    ARENA_BOTTOM_RADIUS,
                    ARENA_Z + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS,
                    ARENA_BOTTOM_RADIUS);
        }
        if (point.y >= ARENA_HEIGHT - ARENA_TOP_RADIUS) { // Top
            if (point.z <= ARENA_Z - ARENA_CORNER_RADIUS) {
                if (point.x <= ARENA_X - ARENA_TOP_RADIUS) { // Ceiling
                    DAN_TO_PLANE(y, ARENA_HEIGHT, -1);
                }
                // Top right corner
                DAN_TO_CYLINDER_Z_INNER(
                        ARENA_X - ARENA_TOP_RADIUS,
                        ARENA_HEIGHT - ARENA_TOP_RADIUS,
                        ARENA_TOP_RADIUS);
            }

            if (point.x >= ARENA_X - ARENA_CORNER_RADIUS) { // Corner
                constexpr const auto corner_x = ARENA_X - ARENA_CORNER_RADIUS;
                constexpr const auto corner_y = ARENA_Z - ARENA_CORNER_RADIUS;
                constexpr const auto groundRad = ARENA_CORNER_RADIUS - ARENA_TOP_RADIUS;

                auto nx = point.x - corner_x;
                auto ny = point.z - corner_y;
                auto dist2 = SQR(nx) + SQR(ny);
                if (dist2 > SQR(groundRad)) {
                    auto dist = sqrt(dist2);
                    DAN_TO_SPHERE_INNER(
                            corner_x + nx / dist * groundRad, ARENA_HEIGHT - ARENA_TOP_RADIUS,
                            corner_y + ny / dist * groundRad,
                            ARENA_TOP_RADIUS);
                }
                DAN_TO_PLANE(y, ARENA_HEIGHT, -1);
            }
            if (point.z <= ARENA_Z - ARENA_TOP_RADIUS) {
                DAN_TO_PLANE(y, ARENA_HEIGHT, -1);
            }
            // Top upper corner
            DAN_TO_CYLINDER_X_INNER(
                    ARENA_HEIGHT - ARENA_TOP_RADIUS,
                    ARENA_Z - ARENA_TOP_RADIUS,
                    ARENA_TOP_RADIUS);
        }
        // Middle

        if (point.z <= ARENA_Z - ARENA_CORNER_RADIUS) { // Side x
            DAN_TO_PLANE(x, ARENA_X, -1);
        }
        if (point.x >= ARENA_X - ARENA_CORNER_RADIUS) { // Corner
            DAN_TO_CYLINDER_Y_INNER(
                    ARENA_X - ARENA_CORNER_RADIUS,
                    ARENA_Z - ARENA_CORNER_RADIUS,
                    ARENA_CORNER_RADIUS);
        }
        if (point.z < ARENA_Z - point.radius) {
            return false;
        }

        if (point.z >= ARENA_Z + ARENA_GOAL_SIDE_RADIUS) { // Goal
            if (point.y > ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS) {
                if (point.x > ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS) {
                    if (point.z <= ARENA_Z + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) { // Side x top (goal)
                        DAN_TO_CYLINDER_Z_INNER(
                                ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS,
                                ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS,
                                ARENA_BOTTOM_RADIUS);
                    }
                    // Goal inner top corner
                    DAN_TO_SPHERE_INNER(
                            ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS,
                            ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS,
                            ARENA_Z + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS,
                            ARENA_BOTTOM_RADIUS);
                }
                if (point.z > ARENA_Z + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) { // Side z top (goal)
                    DAN_TO_CYLINDER_X_INNER(
                            ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS,
                            ARENA_Z + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS,
                            ARENA_BOTTOM_RADIUS);
                }
                // Ceiling (goal)
                DAN_TO_PLANE(y, ARENA_GOAL_HEIGHT, -1);
            }
            if (point.x > ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS) {
                if (point.z > ARENA_Z + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS) {
                    DAN_TO_CYLINDER_Y_INNER(
                            ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS,
                            ARENA_Z + ARENA_GOAL_DEPTH - ARENA_BOTTOM_RADIUS,
                            ARENA_BOTTOM_RADIUS);
                }
                // Size x (goal)
                DAN_TO_PLANE(x, ARENA_GOAL_WIDTH / 2, -1);
            }
            // Side z (goal)
            DAN_TO_PLANE(z, ARENA_Z + ARENA_GOAL_DEPTH, -1);
        }
        if (point.x <= ARENA_GOAL_WIDTH / 2 - ARENA_BOTTOM_RADIUS) {
            if (point.y >= ARENA_GOAL_HEIGHT + ARENA_GOAL_SIDE_RADIUS) {
                DAN_TO_PLANE(z, ARENA_Z, -1);
            }
            // Goal ceiling
            DAN_TO_CYLINDER_X_OUTER(
                    ARENA_GOAL_HEIGHT + ARENA_GOAL_SIDE_RADIUS,
                    ARENA_Z + ARENA_GOAL_SIDE_RADIUS,
                    ARENA_GOAL_SIDE_RADIUS);
        }
        if (point.y <= ARENA_GOAL_HEIGHT - ARENA_GOAL_TOP_RADIUS) {
            if (point.x >= ARENA_GOAL_WIDTH / 2 + ARENA_GOAL_SIDE_RADIUS) {
                DAN_TO_PLANE(z, ARENA_Z, -1);
            }
            // Goal right
            DAN_TO_CYLINDER_Y_OUTER(
                    ARENA_GOAL_WIDTH / 2 + ARENA_GOAL_SIDE_RADIUS,
                    ARENA_Z + ARENA_GOAL_SIDE_RADIUS,
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
            DAN_TO_PLANE(z, ARENA_Z, -1);
        }
        const auto len = sqrt(len2);
        DAN_TO_SPHERE_OUTER(
                ox + vx / len * rad, oy + vy / len * rad, ARENA_Z + ARENA_GOAL_SIDE_RADIUS,
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

    static bool dan_to_arena_old(Unit point, double& penetration, Point& normal) {
        return false;
    }

    static bool dan_to_arena_new(Unit point, double& penetration, Point& normal) {
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

    static bool dan_to_arena(Unit point, double& penetration, Point& normal) {
        Point no, nn;
        double po, pn;
        auto r_old = dan_to_arena_old(point, po, no);
        auto r_new = dan_to_arena_old(point, pn, nn);

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
        return r_old;
        return r_new;
    }

    static uint64_t oppMask, myOppMask[7], myAnyMask[7];
    uint64_t robotBallCollisions = 0, robotBallPositiveCollisions = 0, robotsCollisions = 0, nitroPacksCollected = 0;

    template<typename T>
    void collide_entities(ARobot& a, T& b) {
        constexpr const double secondMass = std::is_same<T, ARobot>::value ? ROBOT_MASS : BALL_MASS;
        double secondRadius = 0, secondRadiusChangeSpeed = 0;
        if constexpr (std::is_same<T, ARobot>::value) {
            secondRadius = b.radius;
            secondRadiusChangeSpeed = b.radius_change_speed;
        }

        if constexpr (std::is_same<T, ABall>::value) {
            secondRadius = BALL_RADIUS;
            secondRadiusChangeSpeed = 0;
        }

        auto dist2 = a.getDistanceTo2(b);
        if (dist2 >= SQR(a.radius + secondRadius)) {
            return;
        }

        auto distance = sqrt(dist2);
        auto penetration = a.radius + secondRadius - distance;

        constexpr const auto k_a = (1.0 / ROBOT_MASS) / ((1.0 / ROBOT_MASS) + (1.0 / secondMass));
        constexpr const auto k_b = (1.0 / secondMass) / ((1.0 / ROBOT_MASS) + (1.0 / secondMass));

        const double normalX = (b.x - a.x) / distance;
        const double normalY = (b.y - a.y) / distance;
        const double normalZ = (b.z - a.z) / distance;

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
            robotBallCollisions |= M_COLL_MASK(a.id);
            if (b.velocity.z >= 0) {
                robotBallPositiveCollisions |= M_COLL_MASK(a.id);
            }
        }
        if constexpr (std::is_same<T, ARobot>::value) {
            robotsCollisions |= M_COLL_MASK2(a.id, b.id);
        }
    }


    std::vector<ARobot*> robots() { // TODO: переписать на iterator
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
                // TODO: подавать сразу готовую
                targetVelocity.clamp(ROBOT_MAX_GROUND_SPEED);

                auto dot = robot.touchNormal * targetVelocity;
                targetVelocity.x -= robot.touchNormal.x * dot + robot.velocity.x;
                targetVelocity.y -= robot.touchNormal.y * dot + robot.velocity.y;
                targetVelocity.z -= robot.touchNormal.z * dot + robot.velocity.z;

                auto len2 = targetVelocity.length2();
                if (len2 > 0) {
                    auto acceleration = robot.touchNormal.y <= 0 ? 0 : ROBOT_ACCELERATION * delta_time * robot.touchNormal.y;

                    if (SQR(acceleration) < len2) {
                        targetVelocity *= acceleration / sqrt(len2);
                    }
                    robot.velocity += targetVelocity;
                }
            }
            if (robot.action.useNitro && robot.nitroAmount > EPS) {
                auto targetVelocityChange = robot.action.targetVelocity - robot.velocity;
                auto changeLen2 = targetVelocityChange.length2();
                if (changeLen2 > EPS2) {
                    const auto maxLen = robot.nitroAmount * NITRO_POINT_VELOCITY_CHANGE;
                    double changeLen;
                    if (changeLen2 > SQR(maxLen)) {
                        targetVelocityChange *= maxLen / sqrt(changeLen2);
                        changeLen = maxLen;
                        changeLen2 = SQR(maxLen);
                    } else {
                        changeLen = sqrt(changeLen2);
                    }

                    auto velocityChange = targetVelocityChange * (ROBOT_NITRO_ACCELERATION / changeLen * delta_time);
                    auto velocityChangeLength2 = velocityChange.length2();
                    double velocityChangeLength;
                    if (velocityChangeLength2 > changeLen2) {
                        velocityChange *= changeLen / sqrt(velocityChangeLength2);
                        velocityChangeLength = changeLen;
                    } else {
                        velocityChangeLength = sqrt(velocityChangeLength2);
                    }

                    robot.velocity += velocityChange;
                    robot.nitroAmount -= velocityChangeLength / NITRO_POINT_VELOCITY_CHANGE;
                }
            }
            robot.move(delta_time);
            robot.radius = radiusByJumpSpeed(robot.action.jumpSpeed);
            robot.radius_change_speed = robot.action.jumpSpeed;
        }
        ball.move(delta_time);

        for (size_t i = 0; i < robots.size(); i++) {
            for (size_t j = 0; j < i; j++) {
                collide_entities<ARobot>(*robots[i], *robots[j]);
            }
        }

        for (auto& robot : robots) {
            Point collision_normal;
            collide_entities<ABall>(*robot, ball);
            if (collide_with_arena(*robot, collision_normal)) {
                robot->touch = true;
                robot->touchNormal = collision_normal;
            } else {
                robot->touch = false;
            }
        }

        collide_with_arena(ball);
        checkGoal();

        for (auto& robot : robots) {
            if (robot->nitroAmount >= MAX_NITRO_AMOUNT)
                continue;

            auto pack = getIntersectedNitroPack(*robot);
            if (pack && pack->respawnTicks == 0) {
                robot->nitroAmount = MAX_NITRO_AMOUNT;
                pack->respawnTicks = NITRO_PACK_RESPAWN_TICKS;
                nitroPacksCollected |= M_COLL_MASK2(robot->id, pack->id);
            }
        }
    }

    ANitroPack* getIntersectedNitroPack(const ARobot& robot) {
        if (!GameInfo::isNitro) {
            return nullptr;
        }

        auto& pack = nitroPacks[robot.x < 0 ? (robot.z < 0 ? 0 : 1) : (robot.z < 0 ? 2 : 3)];
        if (pack.getDistanceTo2(robot) <= SQR(robot.radius + NITRO_PACK_RADIUS)) {
            return &pack;
        }
        return nullptr;
    }

    static double radiusByJumpSpeed(double jumpSpeed) {
        return ROBOT_MIN_RADIUS + (ROBOT_MAX_RADIUS - ROBOT_MIN_RADIUS) * jumpSpeed / ROBOT_MAX_JUMP_SPEED;
    }

    void checkGoal() {
        if (hasGoal != 0) {
            // already have a goal
            return;
        }

        if (ball.z > ARENA_Z + BALL_RADIUS) {
            hasGoal = 1;
        } else if (ball.z < -ARENA_Z - BALL_RADIUS) {
            hasGoal = -1;
        }
    }

    void clearMyRobots() {
        my.clear();
    }

    void clearOppRobots() {
        opp.clear();
    }

    void clearMe() {
#ifdef LOCAL
        if ((int) my.size() <= meIdx) {
            std::cerr << "Sandbox::clearMe() illegal call" << std::endl;
            exit(1);
        }
#endif
        my.erase(my.begin() + meIdx);
    }

    void clearRobots(bool keepMe = false) {
        if (keepMe) {
            for (int i = (int) my.size() - 1; i >= 0; i--) {
                if (i != meIdx) {
                    my.erase(my.begin() + i);
                }
            }
            meIdx = 0;
        } else {
            my.clear();
        }
        opp.clear();
    }

    void _oppGkStrat(ARobot& gk) {
        if (!gk.touch) {
            return;
        }

        constexpr const auto w = ARENA_GOAL_WIDTH/2 - ROBOT_MAX_RADIUS - 1.2;

        Point target_pos;
        double tt = -1;

        if (ball.velocity.z > EPS && ball.z > 1) {
            // Найдем время и место, в котором мяч пересечет линию ворот
            tt = (ARENA_Z - ball.z) / ball.velocity.z;
            auto x = ball.x + ball.velocity.x * tt;
            target_pos.x = std::clamp(x, -w, w);
        }
        target_pos.z = ARENA_Z + 1;

        auto delta = Point(target_pos.x - gk.x, 0.0, target_pos.z - gk.z);
        auto deltaLength2 = delta.length2();
        const double dd = SQR(4);
        double speed;

        if (tt > 0 && gk.z > ARENA_Z - 2 && std::abs(gk.x) < ARENA_GOAL_WIDTH/2 - 1)// чтобы не сильно быстро шататься
            speed = deltaLength2 / SQR(tt);
        else
            speed = deltaLength2 > dd ? ROBOT_MAX_GROUND_SPEED : ROBOT_MAX_GROUND_SPEED / dd * deltaLength2;

        gk.action.targetVelocity = delta.take(speed);

        if (gk.getDistanceTo2(ball) < SQR(ROBOT_RADIUS + BALL_RADIUS + 4)) {
            gk.action.jumpSpeed = ROBOT_MAX_JUMP_SPEED;
        }
    }

    void _oppCounterStrat(int exceptId) {
        for (auto& ct : opp) {
            if (ct.id == exceptId)
                continue;
//            if (!ct.touch) // TODO: try this
//                continue;

            if (_cacheIterator < (int) _actionsCache[ct.id].size()) {
                ct.action = _actionsCache[ct.id][_cacheIterator];
                if (ct.action._toBall) {
                    ct.action.vel(Helper::maxVelocityTo(ct, ball));
                }
                continue;
            }

            if (oppCounterStrat < 3) {
                ct.action.targetVelocity = Helper::maxVelocityTo(ct, ball);
            }

            if (oppCounterStrat > 1 && ct.getDistanceTo2(ball) < SQR(ROBOT_RADIUS + BALL_RADIUS + 4)) {
                ct.action.jumpSpeed = ROBOT_MAX_JUMP_SPEED;
            }
        }
    }

    void oppStrat() {
        if (GameInfo::isOpponentCrashed) {
            for (auto&x : opp) {
                x.action = AAction();
            }
            return;
        }

        if (deduceOppSimple) {
            for (auto &x : opp) {
                x.action.jumpSpeed = x.touch ? 0 : ROBOT_MAX_JUMP_SPEED;
                if (!x.touch && x.nitroAmount > EPS && GameInfo::usedNitro[x.id]) {
                    x.action.targetVelocity = x.velocity * 10;
                    x.action.useNitro = true;
                } else {
                    x.action.targetVelocity = x.velocity;
                }
            }
        }

        if (!opp.empty()) {
            if (oppGkStrat || oppCounterStrat) {

                size_t gkIdx = Helper::whichMaxZ(opp);

                if (oppGkStrat) {
                    _oppGkStrat(opp[gkIdx]);
                }

                if (oppCounterStrat) {
                    _oppCounterStrat(opp[gkIdx].id);
                }
            }
        }
    }

    void doTick(int microticksPerTick = MICROTICKS_PER_TICK) {
        OP_START(DO_TICK);

        oppStrat();

        bool firstMicrotickSeparate = false;
        if (microticksPerTick < MICROTICKS_PER_TICK) {
            for (auto& x : my) {
                if (std::abs(x.radius - radiusByJumpSpeed(x.action.jumpSpeed)) > EPS) {
                    firstMicrotickSeparate = true;
                    break;
                }
            }
        }

#if M_TIME_LOGS
        Logger::instance()->sandboxTicksCount++;
        Logger::instance()->sandbox3TicksCount += firstMicrotickSeparate;
#endif

        hasRandomCollision = false;
        robotBallCollisions = 0;
        robotBallPositiveCollisions = 0;
        robotsCollisions  = 0;
        nitroPacksCollected = 0;
        constexpr const auto deltaTimeTick = 1.0 / TICKS_PER_SECOND;
        if (tick < GameInfo::maxTickCount && needDoTick()) {
            double deltaTime = deltaTimeTick / microticksPerTick;
            if (firstMicrotickSeparate) {
                const int tt = 2;
                for (int i = 0; i < tt && needDoTick(); i++) {
                    update(deltaTimeTick / MICROTICKS_PER_TICK);
                }
                deltaTime = (deltaTimeTick - tt * deltaTimeTick / MICROTICKS_PER_TICK) / microticksPerTick;
            }
            for (int i = 0; i < microticksPerTick && needDoTick(); i++) {
                update(deltaTime);
            }
            for (auto& pack : nitroPacks) {
                if (pack.respawnTicks > 0) {
                    pack.respawnTicks--;
                }
            }
        }
        if (robotBallCollisions) {
            _ballsCacheValid = false;
        }
        if (microticksPerTick < MICROTICKS_PER_TICK && _ballsCacheValid && _cacheIterator < (int) _ballsCache.size() && !isInverted) {
            auto& cached = _ballsCache[_cacheIterator];
            ball.velocity = cached.velocity;
            ball.x = cached.x;
            ball.y = cached.y;
            ball.z = cached.z;
        }
        _cacheIterator++;

        tick++;
        roundTick++;
        // TODO: need clear actions?

        OP_END(DO_TICK);
    }

    void doTicksBallFast(int ticks) {
        constexpr const auto deltaTimeTick = 1.0 / TICKS_PER_SECOND;
        double deltaTime = deltaTimeTick * ticks;
        update(deltaTime);

        tick += ticks;
        roundTick += ticks;
    }


    bool needDoTick() const {
        return hasGoal == 0 || !stopOnGoal;
    }

    bool hasRandomCollision = false;
    int hasGoal = 0; // -1, 0, 1
    bool isInverted = false;

    void invert(int newMeId) {
        assert(!isInverted);
        isInverted = true;
        ball.invert();
        for (auto& x : my)
            x.invert();
        for (auto& x : opp)
            x.invert();
        my.swap(opp);
        for (auto& x : nitroPacks)
            x.invert();
        resetMeIdx(newMeId);
    }

    void resetMeIdx(int id) {
        for (int i = 0; i < (int) my.size(); i++) {
            if (my[i].id == id) {
                meIdx = i;
                break;
            }
        }
    }

};

#endif //CODEBALL_SANDBOX_H
