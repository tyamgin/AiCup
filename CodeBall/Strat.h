#ifndef CODEBALL_STRAT_H
#define CODEBALL_STRAT_H

#include "Sandbox.h"

const double ROBBOT_SHOT_DIST = BALL_RADIUS + ROBOT_MIN_RADIUS + (ROBOT_MAX_RADIUS - ROBOT_MIN_RADIUS) * 0.3;

class Strat {
public:
    int lastTick = -1;
    Sandbox env;
    Sandbox prevEnv;

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

    bool tryShotOutOrGoal(bool isAttacker, AAction &resAction, bool& hasGoal, Point drawVel = {}, int drawJ = -1, int drawK = -1) {
        if (isAttacker && env.me()->getDistanceTo(env.ball) >= BALL_RADIUS + ROBOT_MAX_RADIUS + 12)
            return false;
        if (!isAttacker && env.roundTick < 50)
            return false;
        // TODO: check untouched and far to ball

        const auto myId = env.me()->id;
        auto sel = std::make_tuple(false, -1e10, -1e10);
        Point selVel;
        int selJ = -1, selK = -1;
        AAction firstAction;

        static std::unordered_map<int, Point> prevVel;
        std::vector<Point> vels;
        Sandbox ballSnd = env;
        ballSnd.my.clear();

        for (auto i = 0; i <= 50 && ballSnd.hasGoal == 0; i++) {
            if (i % 5 == 0 || i <= 6)
                vels.push_back(maxVelocityTo(*env.me(), ballSnd.ball));

            ballSnd.doTick(5);
        }
        if (prevVel.count(myId)) {
            vels.push_back(prevVel[myId]);
        }
        if (drawJ >= 0) {
            vels = {drawVel};
        }
        std::sort(vels.begin(), vels.end());
        vels.erase(std::unique(vels.begin(), vels.end()), vels.end());

        for (auto& mvel : vels) {
            Sandbox meSnd = env;
            AAction firstJAct;

            for (auto j = 0; j <= 65; j++) {
                Sandbox meJumpSnd = meSnd;
                auto mvAction = AAction().vel(mvel);
                auto jmpAction = mvAction;
                jmpAction.jump();

                meJumpSnd.me()->action = jmpAction;

                if (j <= drawJ) {
                    Visualizer::addSphere(*meJumpSnd.me(), 0, 0.8, 0, 0.7);
                    Visualizer::addSphere(meJumpSnd.ball, 0.4, 0.1, 0.4, 0.7);
                }

                if (j == 0)
                    firstJAct = mvAction;

                if (meSnd.me()->getDistanceTo(meSnd.ball) < BALL_RADIUS + ROBOT_MAX_RADIUS + 7) {
                    const int jumpMaxTicks = 20;
                    const int ballSimMaxTicks = 90;
                    for (auto k = 0; k <= jumpMaxTicks; k++) {
                        meJumpSnd.doTick(1);
                        auto tmp = meJumpSnd.me();

                        if (j == drawJ && k <= drawK) {
                            Visualizer::addSphere(*meJumpSnd.me(), 0.7, 0.8, 0, 0.7);
                            Visualizer::addSphere(meJumpSnd.ball, 0.4, 0.1, 0.4, 0.7);
                        }

                        double myCollisionVel = INT_MAX;
                        for (auto& item : meJumpSnd.robotBallCollisions) {
                            if (item.id == myId) {
                                myCollisionVel = item.velocity.z;
                            }
                        }

                        double maxZ = -100;
                        double minDist = 100;

                        if (myCollisionVel < INT_MAX && myCollisionVel >= 0) {
                            //if (myCollisionVel > maxBallVel || !hasGoal)
                            {
                                double thr = ARENA_DEPTH / 2 - 0.5;
                                int minThr = INT_MAX;
                                meJumpSnd.oppGkStrat = true;
                                for (int w = 0; w <= ballSimMaxTicks && meJumpSnd.hasGoal == 0; w++) {
                                    meJumpSnd.doTick(1);
                                    maxZ = std::max(maxZ, meJumpSnd.ball.z);
                                    if (maxZ >= thr && minThr == INT_MAX)
                                        minThr = w;
//                                    Point pt2(ARENA_GOAL_WIDTH - 2, ARENA_GOAL_HEIGHT - 2, ARENA_DEPTH / 2);
//                                    Point pt1 = pt2;
//                                    pt1.x *= -1;
//
//                                    Point pt = meJumpSnd.ball;
//                                    double mdst;
//                                    if (pt.x < pt1.x)
//                                        mdst = pt1.getDistanceTo(pt);
//                                    else if (pt.x > pt2.x)
//                                        mdst = pt2.getDistanceTo(pt);
//                                    else {
//                                        pt1.x = pt.x;
//                                        mdst = pt1.getDistanceTo(pt);
//                                    }
//
//                                    minDist = std::min(minDist, mdst);
                                    if (j == drawJ && k <= drawK) {
                                        Visualizer::addSphere(meJumpSnd.ball, 1, 1, 1, 0.5);
                                    }
                                }
                                maxZ = std::min(maxZ, thr);
                                auto cand = std::make_tuple(meJumpSnd.hasGoal > 0, myCollisionVel, maxZ);

                                if (cand > sel) {
                                    selVel = mvel;
                                    selJ = j;
                                    selK = k;
                                    sel = cand;
                                    firstAction = j == 0 ? jmpAction : firstJAct;
                                }
                            }
                            break;
                        }
                    }
                }

                meSnd.me()->action = mvAction;
                meSnd.doTick(1);
            }
        }

        hasGoal = std::get<0>(sel);
        if (selJ >= 0) {
            resAction = firstAction;
#ifdef DEBUG
            if (drawJ < 0) {
                AAction t1;
                bool t2;
                tryShotOutOrGoal(isAttacker, t1, t2, selVel, selJ, selK);
            }
#endif
            prevVel[myId] = selVel;
            return true;
        }
        return false;
    }

    AAction goToGoalCenterStrat(Sandbox &e) {
        AAction sAct;
        double ch = 0.8;
        double maxDeep = 2.0 + ch;
        auto w = ARENA_GOAL_WIDTH/2 - ROBOT_MAX_RADIUS - 1.2;

        Point target_pos;
        double tt = -1;

        // Причем, если мяч движется в сторону наших ворот
        if (e.ball.velocity.z < -EPS && e.ball.z < -1) {
            // Найдем время и место, в котором мяч пересечет линию ворот
            tt = (-ARENA_DEPTH / 2.0 - e.ball.z) / e.ball.velocity.z;
            auto x = e.ball.x + e.ball.velocity.x * tt;
            target_pos.x = std::clamp(x, -w, w);
        }
        auto deepCoeff = (1 - std::min(std::abs(target_pos.x), w) / w) * maxDeep - ch;
        target_pos.z = -(ARENA_DEPTH / 2.0 + deepCoeff);

        // Установка нужных полей для желаемого действия
        auto delta = Point(target_pos.x - e.me()->x, 0.0, target_pos.z - e.me()->z);
        auto speed = ROBOT_MAX_GROUND_SPEED / 4 * std::min(delta.length(), 4.0);
        if (e.me()->z < -ARENA_DEPTH / 2 + 2 && std::abs(e.me()->x) < ARENA_GOAL_WIDTH/2 - 1 && tt >= 0)// чтобы не сльно быстро шататься
            speed = delta.length() / tt;
        auto target_velocity = delta.take(speed);
        sAct.targetVelocity = target_velocity;

        if (e.me()->getDistanceTo(e.ball) < ROBBOT_SHOT_DIST) {
            sAct.jumpSpeed = ROBOT_MAX_JUMP_SPEED;
        }

        return sAct;
    }


    Point maxVelocityTo(const ARobot& a, const Point& b) {
        if (!a.touch || a.touch_normal.y < EPS)
            return Point();

        auto diff = (b - a).normalized();
        diff.y = -(diff.z * a.touch_normal.z + diff.x * a.touch_normal.x) / a.touch_normal.y;
        return diff.take(ROBOT_MAX_GROUND_SPEED);
    }

    int selectGk() {
        std::vector<std::pair<double, int>> distToGoal;
        for (auto& r : env.my) {
            Point pt = r;
            pt.y = 0;
            if (pt.x > ARENA_GOAL_WIDTH / 2)
                distToGoal.emplace_back(pt.getDistanceTo(ARENA_GOAL_WIDTH / 2, 0, -(ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH / 2)), r.id);
            else if (pt.x < -ARENA_GOAL_WIDTH / 2)
                distToGoal.emplace_back(pt.getDistanceTo(-ARENA_GOAL_WIDTH / 2, 0, -(ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH / 2)), r.id);
            else
                distToGoal.emplace_back(pt.getDistanceTo(pt.x, 0, -(ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH / 2)), r.id);
        }
        return std::min_element(distToGoal.begin(), distToGoal.end())->second;
    }

    std::optional<ARobot> evalToBall() {
        Sandbox e = env;
        for (int i = 1; i <= 100; i++) {
            e.doTick(5);
            if (e.ball.y - BALL_RADIUS > ROBOT_MAX_RADIUS*3)
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
        } else if (env.roundTick > 0) {
            prevEnv.doTick();
            checkEvalState();
        }

#ifdef DEBUG
        if (me.id == 2) {
            Sandbox ballEnv = env;
            for (int i = 0; i < 200; i++) {
                if (i == 199) {
                    Visualizer::addSphere(ballEnv.ball, 0.3, 0, 0.5, 0.5);
                }
                if (i % 6 == 5) {
                    Visualizer::addSphere(ballEnv.ball, 1, 0, 0, 0.2);
                }
                ballEnv.doTick();
            }
        }
#endif

        Point oppGoal(0, 0, ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH / 2);
        auto firstToBall = evalToBall();

        bool is_attacker = selectGk() != me.id;
        bool hasGoal = false;

        if (is_attacker && env.roundTick <= 35) {
            action = AAction().vel(maxVelocityTo(me, env.ball + Point(0, 0, -3.2)));
        } else if (is_attacker && tryShotOutOrGoal(is_attacker, action, hasGoal)) {
            std::string msg = hasGoal ? "MOVE TO SHOT!!!!!!" : "SHOT OUT";
            Visualizer::addText(msg);
            LOG(msg);
        } else {
            if (is_attacker) {
                if (env.ball.getDistanceTo(me) < BALL_RADIUS + ROBOT_RADIUS + 0.1) {
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

                        auto tar = snd.ball + (snd.ball - oppGoal).take(BALL_RADIUS * 2.0);
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
                        if (ball.z > -6)
                            action.vel(maxVelocityTo(me, Point(0, 0, ARENA_DEPTH / 4)));
                        else
                            is_attacker = false;
                    }
                } else {
                    auto opp = env.opp[0].z < env.opp[1].z ? env.opp[0] : env.opp[1];
                    action.targetVelocity = maxVelocityTo(me, opp - Point(0, 0, 7*ROBOT_RADIUS));
                }
            }

            if (!is_attacker) {
                RSphere sp(me, 1, 0.7, 0);
                sp.radius *= 1.1;
                Visualizer::addSphere(sp);

                if ((firstToBall && firstToBall.value().id == me.id || ball.velocity.z < 0 && ball.z < -ARENA_DEPTH / 5)
                    && tryShotOutOrGoal(is_attacker, action, hasGoal)) {

                    std::string msg = hasGoal ? "(gk) MOVE TO SHOT!!!!!!" : "(gk) SHOT OUT";
                    Visualizer::addText(msg);
                    LOG(msg);
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
