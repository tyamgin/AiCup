#ifndef CODEBALL_STRAT_H
#define CODEBALL_STRAT_H

#include "Sandbox.h"

const double ROBBOT_SHOT_DIST = BALL_RADIUS + ROBOT_MIN_RADIUS + (ROBOT_MAX_RADIUS - ROBOT_MIN_RADIUS) * 0.3;

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
        if (env.me()->getDistanceTo(env.ball) >= BALL_RADIUS + ROBOT_MAX_RADIUS + 2)
            return false;
        if (env.me()->radius >= ROBOT_MAX_RADIUS)
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
            for (int i = 0; i < 2.5 * TICKS_PER_SECOND; i++) {
                e.doTick(i <= 6 ? MICROTICKS_PER_TICK : 1);
                if (e.hasGoal != 0) {
                    sumProbs += e.hasGoal;
                    break;
                }
            }
        }
        auto prob = sumProbs / countProbs;
        if (prob > 0.59) {
            resAction = action;
            return true;
        }
        return false;
    }



    std::optional<ARobot> evalToBall() {
        Sandbox e = env;
        for (int i = 1; i <= 100; i++) {
            e.doTick(5);
            if (e.ball.y > BALL_RADIUS + ROBOT_MAX_RADIUS)
                continue;

            for (auto r : e.robots()) {
                auto t = 1.0 * i / TICKS_PER_SECOND;
                Point oppGoal(0, 0, ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH / 2);
                if (!r->is_teammate)
                    oppGoal.z *= -1;

                auto tar = e.ball + (e.ball - oppGoal).take(BALL_RADIUS * 0.9);
                Point delta_pos = tar - *r;
                delta_pos.y = 0;
                auto need_speed = delta_pos.length() / t;
                auto target_velocity = delta_pos.take(std::min(ROBOT_MAX_GROUND_SPEED, need_speed));

                if (need_speed <= ROBOT_MAX_GROUND_SPEED) {
                    return *r;
                }
            }
        }
        return {};
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
                if (i == 199) {
                    Visualizer::addSphere(ballEnv.ball, 0.3, 0, 0.5, 0.5);
                }
                if (i % 10 == 9) {
                    Visualizer::addSphere(ballEnv.ball, 1, 0, 0, 0.2);
                }
                ballEnv.doTick();
            }

            Sandbox ballEnv2 = env;
            ballEnv2.my.clear();
            ballEnv2.opp.clear();
            for (int i = 0; i < 200; i++) {
                ballEnv2.doTick(1);
                if (i == 199) {
                    Visualizer::addSphere(ballEnv2.ball, 0, 0, 1, 0.5);
                }
            }

        }

        Point oppGoal(0, 0, ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH / 2);
        auto firstToBall = evalToBall();

        auto catchGkStrat = [&](AAction& resAction, int& resTicks) {
            Sandbox snd = env;
            snd.my.clear();
            snd.opp.clear();

            if (me.getDistanceTo(snd.ball) < ROBBOT_SHOT_DIST) {
                resAction.jumpSpeed = ROBOT_MAX_JUMP_SPEED;
                resTicks = 0;
                return true;
            }

            for (auto i = 1; i <= 1 * TICKS_PER_SECOND; i++) {
                snd.doTick(5);
                if (snd.ball.y > BALL_RADIUS * 1.1) //TODO
                    continue;
                if (snd.hasGoal < 0)
                    return false;

                auto t = 1.0 * i / TICKS_PER_SECOND;

                ARobot* nearest = nullptr;
                for(auto r : env.opp)
                    if (nearest == nullptr || r.getDistanceTo(me) < nearest->getDistanceTo(me))
                        nearest = &r;


                auto tar = snd.ball + (snd.ball - (oppGoal + Point(ARENA_GOAL_WIDTH/4*(nearest->x < me.x ? 1 : -1), 0, 0))).take(BALL_RADIUS * 0.9);
                        //Point(BALL_RADIUS*0.7*(nearest->x > me.x ? 1 : -1), 0, -BALL_RADIUS*0.7);// : )
                Point delta_pos = tar - me;
                delta_pos.y = 0;
                auto need_speed = delta_pos.length() / t;
                auto target_velocity = delta_pos.take(std::min(ROBOT_MAX_GROUND_SPEED, need_speed));
                AAction act;
                act.targetVelocity = target_velocity;

                if (need_speed <= ROBOT_MAX_GROUND_SPEED) {
                    resTicks = i;
                    resAction = act;
                    return true;
                }

            }
            return false;
        };

        if (tryShot(action)) {
            Visualizer::addText("SHOT", 20);
            LOG("SHOT");
        } else {
            bool is_attacker = env.teammate1()->z < me.z;

            if (is_attacker) {
                if (0) {

                } else if (env.ball.getDistanceTo(me) < BALL_RADIUS + ROBOT_RADIUS + 0.1) {
                    int angles = 8;
                    Point bestV;
                    double minDist = 1e10;
                    for (int i = 0; i < angles; i++) {
                        double ang = 2 * M_PI / angles + i;
                        Sandbox s = env;
                        auto v = Point(cos(ang), 0, sin(ang)) * ROBOT_MAX_GROUND_SPEED;

                        for (int tk = 0; tk < 15; tk++) {
                            s.me()->action.targetVelocity = v;
                            s.doTick(10);
                        }
                        if (s.ball.getDistanceTo(oppGoal) < minDist)
                            minDist = s.ball.getDistanceTo(oppGoal), bestV = v;
                    }

                    action.targetVelocity = bestV;//(oppGoal - me).take(ROBOT_MAX_GROUND_SPEED);
                    Visualizer::addLine(me, me + action.targetVelocity * 2 * ROBOT_RADIUS, 3, 1, 1, 0);
                    Visualizer::addLine(me, oppGoal, 0.2, 0, 0, 1);
                } else if (!firstToBall || !firstToBall.value().is_teammate || firstToBall.value().id == me.id) {
                    Sandbox snd = env;
                    snd.my.clear();
                    snd.opp.clear();
                    std::optional<AAction> firstAction, secondAction;

                    for (auto i = 1; i <= 12 * TICKS_PER_SECOND; i++) {
                        snd.doTick(1);
                        if (snd.ball.y > BALL_RADIUS * 1.1) //TODO
                            continue;
                        auto t = 1.0 * i / TICKS_PER_SECOND;

                        auto tar = snd.ball + (snd.ball - oppGoal).take(BALL_RADIUS * 0.9);
                        Point delta_pos = tar - me;
                        delta_pos.y = 0;
                        auto need_speed = delta_pos.length() / t;
                        auto target_velocity = delta_pos.take(std::min(ROBOT_MAX_GROUND_SPEED, need_speed));
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


                auto goToGoalCenterStrat = [](Sandbox &e) {
                    AAction sAct;
                    auto target_pos = Point(0.0, 0.0, -(ARENA_DEPTH / 2.0));
                    double t = 1;
                    // Причем, если мяч движется в сторону наших ворот
                    if (e.ball.velocity.z < -EPS) {
                        // Найдем время и место, в котором мяч пересечет линию ворот
                        t = (target_pos.z - e.ball.z) / e.ball.velocity.z;
                        auto x = e.ball.x + e.ball.velocity.x * t;

                        target_pos.x = std::clamp(x, -ARENA_GOAL_WIDTH / 2.0, ARENA_GOAL_WIDTH / 2.0);
                    }

                    // Установка нужных полей для желаемого действия
                    auto target_velocity = Point(target_pos.x - e.me()->x, 0.0, target_pos.z - e.me()->z) / t;
                    sAct.targetVelocity = target_velocity;

                    if (e.me()->getDistanceTo(e.ball) < ROBBOT_SHOT_DIST) {
                        sAct.jumpSpeed = ROBOT_MAX_JUMP_SPEED;
                    }

                    return sAct;
                };

                AAction catchAction;
                int catchTime;
                if (firstToBall && firstToBall.value().id == me.id && catchGkStrat(catchAction, catchTime)) {
                    action = catchAction;
                } else {
                    std::optional<AAction> defend;
                    Sandbox e1 = env;
                    for (int i = 0; i < 30; i++) {
                        e1.doTick(5);
                        if (e1.hasGoal < 0)
                            break;
                    }
                    if (e1.hasGoal < 0) {
                        Visualizer::addText("ALARM!!!", 15);
                        Sandbox e2 = env;
                        AAction act;
                        act.jumpSpeed = ROBOT_MAX_JUMP_SPEED;
                        e2.me()->action = act;
                        for (int i = 0; i < 30; i++) {
                            e2.doTick();
                            if (e2.hasGoal < 0)
                                break;
                        }
                        if (e2.hasGoal >= 0) { // если есть спасение
                            defend = act;
                            for (int wt = 1; wt <= 7; wt++) {
                                Sandbox e3 = env;
                                auto firstAction = goToGoalCenterStrat(e3);
                                e3.me()->action = firstAction;
                                e3.doTick();

                                for (int j = 1; j < wt; j++) {
                                    e3.me()->action = goToGoalCenterStrat(e3);
                                    e3.doTick();
                                }

                                e3.me()->action = act;
                                for (int i = 0; i < 30 - wt; i++) {
                                    e3.doTick();
                                    if (e3.hasGoal < 0)
                                        break;
                                }
                                if (e3.hasGoal >= 0 && e3.ball.getDistanceTo(Point(0.0, 0.0, -(ARENA_DEPTH / 2.0))) >
                                                       e2.ball.getDistanceTo(Point(0.0, 0.0, -(ARENA_DEPTH / 2.0)))) {
                                    defend = firstAction;
                                    break;
                                }
                            }
                        }
                    }

                    if (defend) {
                        action = defend.value();
                    } else {
                        action = goToGoalCenterStrat(env);
                    }
                }
            }
        }
    }
};


#endif //CODEBALL_STRAT_H
