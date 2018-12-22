#include "MyStrategy.h"
#include "Sandbox.h"

#include <iostream>
using namespace std;

MyStrategy::MyStrategy() { }

class Strat {
public:
    int lastTick = -1;
    Sandbox env;
    Sandbox prevEnv, prevEnv2;

    void checkEvalState() {
        if (prevEnv.hasRandomCollision) {
            LOG("Random collision. Skip check.");
            return;
        }

        if (env.ball.notEquals(prevEnv.ball, 1e-6)) {
            LOG("ball position calculated wrong");
        }
        if (env.ball.velocity.notEquals(prevEnv.ball.velocity, 1e-6)) {
            LOG("ball velocity calculated wrong");
        }

        for (auto& me : env.teammates()) {
            auto prev = prevEnv.robot(me->id);
            if (prev->notEquals(*me, 1e-6)) {
                LOG("position calculated wrong");
            }
            if (prev->velocity.notEquals(me->velocity, 1e-6)) {
                LOG("velocity calculated wrong");
            }
            if (prev->touch != me->touch) {
                LOG("touch calculated wrong");
            }
            if (me->touch && prev->touch_normal.notEquals(me->touch_normal, 1e-6)) {
                LOG("touch_normal calculated wrong");
            }
            if (prev->radius != me->radius) {
                LOG("radius calculated wrong");
            }
            if (prev->nitro_amount != me->nitro_amount) {
                LOG("nitro_amount calculated wrong");
            }
        }
    }

    bool tryShot(AAction &resAction) {
        if (env.me()->getDistanceTo(env.ball) >= BALL_RADIUS + ROBOT_MAX_RADIUS)
            return false;

        Sandbox startEnv = env;
        AAction action;
        action.jumpSpeed = ROBOT_MAX_JUMP_SPEED;
        startEnv.me()->action = action;

        double sumProbs = 0;
        int countProbs = 7;
        for (int p = 0; p < countProbs; p++) {
            auto e = startEnv;
            e.rnd = RandomGenerator(RandomGenerator::Simple, 1232u + p);
            for (int i = 0; i < 1 * TICKS_PER_SECOND; i++) {
                e.doTick(i < 5 ? MICROTICKS_PER_TICK : 1);
                if (e.hasGoal != 0) {
                    sumProbs += e.hasGoal;
                    break;
                }
            }
        }
        auto prob = sumProbs / countProbs;
        if (prob > 0.49) {
            resAction = action;
            return true;
        }
        return false;
    }

    void act(AAction &action) {
        auto& ball = env.ball;
        auto& me = *env.me();

        if (env.tick == lastTick) {
            for (auto tm : env.teammates(me.id))
                tm->action = prevEnv.robot(tm->id)->action;
        } else if (env.tick > 0) {
            prevEnv2 = prevEnv;
            prevEnv.doTick();
            checkEvalState();
        }


        if (me.id == 2) {
            Sandbox ballEnv = env;
            ballEnv.my.clear();
            ballEnv.opp.clear();
            for (int i = 0; i < 200; i++) {
                if (i == 199)
                    Visualizer::addSphere(ballEnv.ball, 0.3, 0, 0.5, 0.5);
                if (i % 10 == 9)
                    Visualizer::addSphere(ballEnv.ball, 1, 0, 0, 0.2);
                ballEnv.doTick();
            }

            Sandbox ballEnv2 = env;
            ballEnv2.my.clear();
            ballEnv2.opp.clear();
            for (int i = 0; i < 200; i++) {
                ballEnv2.doTick(1);
                if (i == 199)
                    Visualizer::addSphere(ballEnv2.ball, 0, 0, 1, 0.5);
            }

        }

        Point oppGoal(0, 0, ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH / 2);

        if (tryShot(action)) {
            LOG("SHOT");
        } else {
            bool is_attacker = env.teammate1()->z < me.z;

            if (is_attacker) {
                if (env.ball.getDistanceTo(me) < BALL_RADIUS + ROBOT_RADIUS + 0.1) {
                    int angles = 8;
                    Point bestV;
                    double minDist = 1e10;
                    for (int i = 0; i < angles; i++) {
                        double ang = 2 * M_PI / angles + i;
                        Sandbox s = env;
                        auto v = Point(cos(ang), 0, sin(ang)) * ROBOT_MAX_GROUND_SPEED;

                        for (int tk = 0; tk < 10; tk++) {
                            s.me()->action.targetVelocity = v;
                            s.doTick(10);
                        }
                        if (s.ball.getDistanceTo(oppGoal) < minDist)
                            minDist = s.ball.getDistanceTo(oppGoal), bestV = v;
                    }

                    action.targetVelocity = bestV;//(oppGoal - me).take(ROBOT_MAX_GROUND_SPEED);
                    Visualizer::addLine(me, me + action.targetVelocity * 2 * ROBOT_RADIUS, 3, 1, 1, 0);
                    Visualizer::addLine(me, oppGoal, 0.2, 0, 0, 1);
                } else {
                    Sandbox snd = env;
                    snd.my.clear();
                    snd.opp.clear();
                    optional<AAction> firstAction, secondAction;

                    for (auto i = 1; i <= 12 * TICKS_PER_SECOND; i++) {
                        snd.doTick(1);
                        if (snd.ball.y > BALL_RADIUS * 1.1) //TODO
                            continue;
                        auto t = 1.0 * i / TICKS_PER_SECOND;

                        auto tar = snd.ball + (snd.ball - oppGoal).take(BALL_RADIUS * 0.9);
                        Point delta_pos = tar - me;
                        delta_pos.y = 0;
                        auto need_speed = delta_pos.length() / t;
                        auto target_velocity = delta_pos.take(min(ROBOT_MAX_GROUND_SPEED, need_speed));
                        AAction act;
                        act.targetVelocity = target_velocity;

                        if (need_speed <= ROBOT_MAX_GROUND_SPEED) {
                            firstAction = act;
                            Visualizer::addLine(me, me + delta_pos, 0.3, 0, 0, 1);
                            break;
                        }
                        secondAction = act;
                    }
                    if (firstAction) {
                        action = firstAction.value();
                    } else if (secondAction) {
                        action = secondAction.value();
                    } else {
                        is_attacker = false;
                    }
                }
            }

            if (!is_attacker) {
                RSphere sp(me, 1, 0.7, 0);
                sp.radius *= 1.1;
                Visualizer::addSphere(sp);

                auto target_pos = Point(0.0, 0.0, -(ARENA_DEPTH / 2.0) + ARENA_BOTTOM_RADIUS);
                double t = 1;
                // Причем, если мяч движется в сторону наших ворот
                if (ball.velocity.z < -EPS) {
                    // Найдем время и место, в котором мяч пересечет линию ворот
                    t = (target_pos.z - ball.z) / ball.velocity.z;
                    auto x = ball.x + ball.velocity.x * t;

                    target_pos.x = clamp(x, -ARENA_GOAL_WIDTH / 2.0, ARENA_GOAL_WIDTH / 2.0);
                }

                // Установка нужных полей для желаемого действия
                auto target_velocity = Point(target_pos.x - me.x, 0.0, target_pos.z - me.z) / t;
                action.targetVelocity = target_velocity;

                if (me.getDistanceTo(env.ball) < BALL_RADIUS + ROBOT_MAX_RADIUS) {
                    action.jumpSpeed = ROBOT_MAX_JUMP_SPEED;
                }
            }
        }
    }
};

Strat strat;
int waitForTick = -1;

void MyStrategy::act(const model::Robot& me, const model::Rules& rules, const model::Game& game, model::Action& action) {
    LOG((string)"(" + to_string(me.id) + ") Tick " + to_string(game.current_tick));

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

    Logger::instance()->cumulativeTimerStart(Logger::ALL);

    strat.act(a);
    action.use_nitro = a.useNitro;
    action.jump_speed = a.jumpSpeed;
    action.target_velocity_x = a.targetVelocity.x;
    action.target_velocity_y = a.targetVelocity.y;
    action.target_velocity_z = a.targetVelocity.z;

    strat.env.robot(me.id)->action = a;
    strat.prevEnv = strat.env;
    strat.lastTick = game.current_tick;

    Logger::instance()->cumulativeTimerEnd(Logger::ALL);
}

std::string MyStrategy::custom_rendering() {
    return Visualizer::dumpAndClean();
}