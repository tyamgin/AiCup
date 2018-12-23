#include "MyStrategy.h"
#include "Strat.h"
#include <iostream>
using namespace std;

/*
 * TODO:
 * - чтобы вратарь останавливал
 * - чтобы выбивал в сторону, а не прямо
 */

Strat strat;
int waitForTick = -1;

void doAction(const model::Robot& me, const model::Rules& rules, const model::Game& game, model::Action& action) {
    AAction a;
    strat.env = Sandbox(game, rules, me.id);
    auto& env = strat.env;
    if (env.tick < waitForTick) {
        return;
    }
    if (env.hasGoal) {
        waitForTick = env.tick + RESET_TICKS - 1;
        return;
    }
    waitForTick = -1;

    TIMER_START();

    strat.act(a);
    action.use_nitro = a.useNitro;
    action.jump_speed = a.jumpSpeed;
    action.target_velocity_x = a.targetVelocity.x;
    action.target_velocity_y = a.targetVelocity.y;
    action.target_velocity_z = a.targetVelocity.z;

    strat.env.robot(me.id)->action = a;
    strat.prevEnv = strat.env;
    strat.lastTick = game.current_tick;

    TIMER_ENG_LOG("Tick");
}

MyStrategy::MyStrategy() = default;

void MyStrategy::act(const model::Robot& me, const model::Rules& rules, const model::Game& game, model::Action& action) {
    Logger::instance()->tick = game.current_tick;
    LOG((string)"(" + to_string(me.id) + ") Tick " + to_string(game.current_tick));

    Logger::instance()->cumulativeTimerStart(Logger::ALL);

    doAction(me, rules, game, action);

    Logger::instance()->cumulativeTimerEnd(Logger::ALL);

    if (game.current_tick % 500 == 0 && game.current_tick > 0) {
        cout << Logger::instance()->getSummary() << endl;
    }
}

std::string MyStrategy::custom_rendering() {
    return Visualizer::dumpAndClean();
}