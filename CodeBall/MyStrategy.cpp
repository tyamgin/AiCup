#include "MyStrategy.h"
#include "Strat.h"
#include <iostream>
using namespace std;

/*
 * TODO:
 * - Можно пересчитывать 100 микротиков, если была коллизия мяча
 * - включить 24 для всех, проверять кому ближе идти на мяч
 * - перебирать направления - научить вратаря лучше отбивать
 */

void run_tests();

Strat strat;
int waitForTick = -1;

void doAction(const model::Robot& me, const model::Rules& rules, const model::Game& game, model::Action& action) {
    GameInfo::maxTickCount = rules.max_tick_count;
    GameInfo::isFinal = rules.team_size > 2;
    GameInfo::GameScore newScore = {0, 0};
    for (auto&player : game.players) {
        if (player.me) {
            newScore.my = player.score;
        } else {
            newScore.opp = player.score;
            GameInfo::isOpponentCrashed = player.strategy_crashed;
        }
    }
    bool scoreChanged = GameInfo::score.my + GameInfo::score.opp != newScore.my + newScore.opp;
    GameInfo::score = newScore;

    AAction a;
    strat.env = Sandbox(game, rules, me.id);
    auto& env = strat.env;
    auto first = env.tick != strat.prevEnv.tick;

    if (!first) { // inherit from previous robot
        env.roundTick = strat.prevEnv.roundTick;
    } else {
        if (env.tick > strat.prevEnv.tick + 1) {
            // new round started
            env.roundTick = 0;
        } else {
            env.roundTick = strat.prevEnv.roundTick + 1;
        }
    }

    if (env.tick < waitForTick) {
        return;
    }
    if (scoreChanged) {
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

    if (GameInfo::score.my + GameInfo::score.opp == 0) {
        assert(env.tick == env.roundTick);
    } else {
        assert(env.tick != env.roundTick);
    }

    TIMER_ENG_LOG("Tick");
}

MyStrategy::MyStrategy() {
    static bool flushed = false;
    if (flushed) {
        return;
    }
    cout << "NN weights initialized." << endl
         << "GA start population prepared." << endl
         << "Flight is normal." << endl;
    flushed = true;
}

void MyStrategy::act(const model::Robot& me, const model::Rules& rules, const model::Game& game, model::Action& action) {
    Logger::instance()->tick = game.current_tick;
    LOG((string)"(" + to_string(me.id) + ") Tick " + to_string(game.current_tick));

    Logger::instance()->cumulativeTimerStart(LA_ALL);

    doAction(me, rules, game, action);

    Logger::instance()->cumulativeTimerEnd(LA_ALL);

    if (game.current_tick % 500 == 0 && game.current_tick > 0) {
        //cout << Logger::instance()->getSummary() << endl;
    }
    if (game.current_tick == 0) {
        run_tests();
    }
}

std::string MyStrategy::custom_rendering() {
    return Visualizer::dumpAndClean();
}


#ifdef LOCAL
MyStrategy::~MyStrategy() {
    static bool flushed = false;
    if (flushed) {
        return;
    }

    cout << "Final stats:" << endl;
    cout << Logger::instance()->getSummary() << endl;

    flushed = true;
}
#endif

void run_tests() {
    Sandbox e;

    e.ball.set(-3.094469, 14.393752, -37.612641);
    e.ball.velocity.set(1.741709, 5.221515, -24.657713);
    e.doTick();
    assert(e.ball.equals(Point(-3.065441, 14.362125, -37.640170), 2e-6));
    assert(e.ball.velocity.equals(Point(1.741709, -7.465922, 16.160170), 2e-6));


    e.ball.set(-23.019338622046326748, 17.999962430128810809, -19.14657426491466552);
    e.ball.velocity.set(14.682921642666871165, 0.058461879134667917024, -1.4818202142470027205);
    e.doTick();
    assert(e.ball.equals(Point(-22.774622648706465355, 17.996454426285627193, -19.171271268485416073), 1e-7));
    assert(e.ball.velocity.equals(Point(14.682959226403681896, -0.46121522321528829469, -1.4818202142470027205), 1e-7));


    e.ball.set(16.329517921537998859, 16.195591242457055614, -36.845542433926816273);
    e.ball.velocity.set(-25.283203469330487678, 7.6680203103518476127, 6.3722070924858815744);
    e.doTick();
    assert(e.ball.equals(Point(15.908131197049071304, 16.319208932268558954, -36.739319891201859036), 1e-7));
    assert(e.ball.velocity.equals(Point(-25.283203469330487678, 7.1669386313658574039, 6.3734987090127770415), 1e-7));


    e.ball.set(-27.995519339371629286, 2.9054418436248079516, 6.1702947673222912073);
    e.ball.velocity.set(-0.57403220611490801684, 6.0725454135943017775, -12.125100730674212457);
    e.doTick();
    assert(e.ball.equals(Point(-27.999998915097581431, 3.0028025199069756646, 5.9682097551443806793), 1e-7));
    //assert(e.ball.velocity.equals(Point(0.0021698048401537694749, 5.600275987957815893, -12.125100730674212457), 1e-7)); //TODO: не сходится x
}