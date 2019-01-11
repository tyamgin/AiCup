#ifndef CODEBALL_CONST_H
#define CODEBALL_CONST_H

#define M_NO_RANDOM 0

#if M_NO_RANDOM
#ifndef LOCAL
#error "M_NO_RANDOM is only for local"
#endif
#endif

// Константы, взятые из документации
constexpr const double ROBOT_MIN_RADIUS = 1;
constexpr const double ROBOT_MAX_RADIUS = 1.05;
constexpr const double ROBOT_MAX_JUMP_SPEED = 15;
constexpr const double ROBOT_ACCELERATION = 100;
constexpr const double ROBOT_NITRO_ACCELERATION = 30;
constexpr const double ROBOT_MAX_GROUND_SPEED = 30;
constexpr const double ROBOT_ARENA_E = 0;
constexpr const double ROBOT_RADIUS = 1;
constexpr const double ROBOT_MASS = 2;
constexpr const int TICKS_PER_SECOND = 60;
constexpr const int MICROTICKS_PER_TICK = 100;
constexpr const int RESET_TICKS = 2 * TICKS_PER_SECOND;
constexpr const double BALL_ARENA_E = 0.7;
constexpr const double BALL_RADIUS = 2;
constexpr const double BALL_MASS = 1;
constexpr const double MIN_HIT_E = 0.4;
constexpr const double MAX_HIT_E = 0.5;
constexpr const double MAX_ENTITY_SPEED = 100;
constexpr const double MAX_NITRO_AMOUNT = 100;
constexpr const double START_NITRO_AMOUNT = 50;
constexpr const double NITRO_POINT_VELOCITY_CHANGE = 0.6;
constexpr const double NITRO_PACK_X = 20;
constexpr const double NITRO_PACK_Y = 1;
constexpr const double NITRO_PACK_Z = 30;
constexpr const double NITRO_PACK_RADIUS = 0.5;
constexpr const double NITRO_PACK_AMOUNT = 100;
constexpr const int NITRO_PACK_RESPAWN_TICKS = 10 * TICKS_PER_SECOND;
constexpr const double GRAVITY = 30;

constexpr const double ARENA_WIDTH = 60;
constexpr const double ARENA_HEIGHT = 20;
constexpr const double ARENA_DEPTH = 80;
constexpr const double ARENA_BOTTOM_RADIUS = 3;
constexpr const double ARENA_TOP_RADIUS = 7;
constexpr const double ARENA_CORNER_RADIUS = 13;
constexpr const double ARENA_GOAL_TOP_RADIUS = 3;
constexpr const double ARENA_GOAL_WIDTH = 30;
constexpr const double ARENA_GOAL_HEIGHT = 10;
constexpr const double ARENA_GOAL_DEPTH = 10;
constexpr const double ARENA_GOAL_SIDE_RADIUS = 1;

constexpr const double ARENA_Z = ARENA_DEPTH / 2;
constexpr const double ARENA_X = ARENA_WIDTH / 2;

#endif //CODEBALL_CONST_H
