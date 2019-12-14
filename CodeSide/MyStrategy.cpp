#include "MyStrategy.hpp"
#include "src/Strategy.h"
#include <iostream>
#include <unordered_map>
#include <vector>

using namespace std;

MyStrategy::MyStrategy() = default;

Strategy strategy;

#define CAT_(a, b) a##b
#define CAT(a, b) CAT_(a, b)

void debugCheckGameParams(const Game& game, bool print) {
#define PRINT_GAME_PROP(type, const_str, p) do {                                                                \
        auto val = game.properties. p;                                                                          \
        if (print) {                                                                                            \
            cout << "constexpr const " << #type << " " << #const_str << " = " << val << ";\n";                  \
        }                                                                                                       \
        if (std::abs(const_str - val) > 1e-9) {                                                                 \
            cerr << #const_str << " wrong constant value " << const_str << " (right is " << val << ")\n";       \
            exit(1);                                                                                            \
        }                                                                                                       \
    } while(0)

    PRINT_GAME_PROP(int, MAX_TICK_COUNT, maxTickCount);
    PRINT_GAME_PROP(double, TICKS_PER_SECOND, ticksPerSecond);
    PRINT_GAME_PROP(int, UPDATES_PER_TICK, updatesPerTick);
    PRINT_GAME_PROP(double, LOOT_BOX_SIZE, lootBoxSize.x);
    PRINT_GAME_PROP(double, LOOT_BOX_SIZE, lootBoxSize.y);
    PRINT_GAME_PROP(double, UNIT_WIDTH, unitSize.x);
    PRINT_GAME_PROP(double, UNIT_HEIGHT, unitSize.y);
    PRINT_GAME_PROP(double, UNIT_MAX_HORIZONTAL_SPEED, unitMaxHorizontalSpeed);
    PRINT_GAME_PROP(double, UNIT_FALL_SPEED, unitFallSpeed);
    PRINT_GAME_PROP(double, UNIT_JUMP_TIME, unitJumpTime);
    PRINT_GAME_PROP(double, UNIT_JUMP_SPEED, unitJumpSpeed);
    PRINT_GAME_PROP(double, JUMP_PAD_JUMP_TIME, jumpPadJumpTime);
    PRINT_GAME_PROP(double, JUMP_PAD_JUMP_SPEED, jumpPadJumpSpeed);
    PRINT_GAME_PROP(int, UNIT_MAX_HEALTH, unitMaxHealth);
    PRINT_GAME_PROP(int, HEALTH_PACK_HEALTH, healthPackHealth);
    PRINT_GAME_PROP(double, MINE_SIZE, mineSize.x);
    PRINT_GAME_PROP(double, MINE_SIZE, mineSize.y);
    PRINT_GAME_PROP(double, MINE_PREPARE_TIME, minePrepareTime);
    PRINT_GAME_PROP(double, MINE_TRIGGER_TIME, mineTriggerTime);
    PRINT_GAME_PROP(double, MINE_TRIGGER_RADIUS, mineTriggerRadius);
    PRINT_GAME_PROP(int, KILL_SCORE, killScore);
    PRINT_GAME_PROP(double, MINE_EXPLOSION_RADIUS, mineExplosionParams.radius);
    PRINT_GAME_PROP(int, MINE_EXPLOSION_DAMAGE, mineExplosionParams.damage);
    PRINT_GAME_PROP(int, ROCKET_LAUNCHER_EXPLOSION_DAMAGE, weaponParams.find(ROCKET_LAUNCHER)->second.explosion->damage);
    PRINT_GAME_PROP(double, ROCKET_LAUNCHER_EXPLOSION_RADIUS, weaponParams.find(ROCKET_LAUNCHER)->second.explosion->radius);

#define PRINT_WEAPON_PROP(type, weapon_idx, weapon_type, p, const_str) do {\
            auto val = game.properties.weaponParams.find((WeaponType)weapon_idx)->second. p;\
            if (print) cout << "constexpr const " << #type << " " << #weapon_type << "_" << #const_str << " = " << val << ";\n";\
            if (std::abs(CAT(weapon_type, CAT(_, const_str)) - val) > 1e-9) {\
                cerr << #const_str << " wrong constant value " << CAT(weapon_type, CAT(_, const_str)) << " (right is " << val << ")\n";\
                exit(1);\
            }\
        } while(0)

#define PRINT_WEAPON_PROPS(weapon_idx, weapon_type) \
        PRINT_WEAPON_PROP(int, weapon_idx, weapon_type, magazineSize, MAGAZINE_SIZE);\
        PRINT_WEAPON_PROP(double, weapon_idx, weapon_type, fireRate, FIRE_RATE);\
        PRINT_WEAPON_PROP(double, weapon_idx, weapon_type, reloadTime, RELOAD_TIME);\
        PRINT_WEAPON_PROP(double, weapon_idx, weapon_type, minSpread, MIN_SPREAD);\
        PRINT_WEAPON_PROP(double, weapon_idx, weapon_type, maxSpread, MAX_SPREAD);\
        PRINT_WEAPON_PROP(double, weapon_idx, weapon_type, recoil, RECOIL);\
        PRINT_WEAPON_PROP(double, weapon_idx, weapon_type, aimSpeed, AIM_SPEED);\
        PRINT_WEAPON_PROP(double, weapon_idx, weapon_type, bullet.speed, BULLET_SPEED);\
        PRINT_WEAPON_PROP(double, weapon_idx, weapon_type, bullet.size, BULLET_SIZE);\
        PRINT_WEAPON_PROP(int, weapon_idx, weapon_type, bullet.damage, BULLET_DAMAGE);\

    PRINT_WEAPON_PROPS(0, PISTOL);
    PRINT_WEAPON_PROPS(1, ASSAULT_RIFLE);
    PRINT_WEAPON_PROPS(2, ROCKET_LAUNCHER);


#undef PRINT_GAME_PROP
#undef PRINT_WEAPON_PROP
#undef PRINT_WEAPON_PROPS
}

UnitAction MyStrategy::getAction(const Unit& unit, const Game& game, Debug& debug) {
    OP_START(ALL);
    TIMER_START();
    TLevel::init(unit, game);
    TDrawUtil::debug = std::make_shared<Debug>(debug);
    if (game.currentTick == 0) {
        LOG(TLevel::toString());
    }

    if (game.currentTick <= 1) {
        debugCheckGameParams(game, false);
    }
    auto ret = strategy.getAction(unit, game, debug);
    TIMER_ENG_LOG("Tick");
    OP_END(ALL);

#if M_TIME_LOGS
    if (game.currentTick % 100 == 0) {
        std::cout << Logger::instance()->getSummary() << std::endl;
    }
#endif
    return ret;
}

int TLevel::width = 0;
int TLevel::height = 0;
std::vector<std::vector<ETile>> TLevel::tiles;
int TLevel::myId = 0;
std::vector<int> TLevel::unitIdToPlayerId;

std::shared_ptr<Debug> TDrawUtil::debug;