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
            if (me->touch && prev->touchNormal.notEquals(me->touchNormal, 1e-6)) {
                LOG("touch_normal calculated wrong");
            }
            if (prev->radius != me->radius) {
                LOG("radius calculated wrong");
            }
            if (prev->nitroAmount != me->nitroAmount) {
                LOG("nitro_amount calculated wrong");
            }
        }
    }

    struct Metric {
        bool hasGoal = false;
        double positiveChange = 0;
        int positiveTicks = 0;
        double penalty = 0;
        int timeToShot = INT_MAX;

        bool operator <(const Metric &m) const {
            auto getComparable = [](const Metric &m) {
                double pen = (4 - std::min(4.0, m.penalty)) / 4;

                return std::make_tuple(m.hasGoal, (m.positiveChange / (m.positiveTicks + m.timeToShot)) - pen);
            };

            return getComparable(*this) < getComparable(m);
        }

        std::string toString() {
            return (hasGoal ? "GOAL" : "SHOT") + std::string(" t=") + std::to_string(timeToShot) + " p=" + std::to_string(penalty) + " " + std::to_string((positiveChange / (positiveTicks + timeToShot)));
        }
    };

    struct ActionSeqItem {
        int count = 0;
        AAction action;

        enum Type {
            Action
        };
    };

    struct ActionSeq : public std::vector<ActionSeqItem> {
        void add(const ActionSeqItem& item) {
            if (item.count > 0) {
                push_back(item);
            }
        }
    };

    std::unordered_map<int, int> lastShotTime;

    bool tryShotOutOrGoal(bool isAttacker, AAction &resAction, Metric& resMetric, Point drawVel = {}, int drawJ = -1, int drawK = -1, double drawAlpha = 1.0) {
        if (isAttacker && env.me()->getDistanceTo(env.ball) >= BALL_RADIUS + ROBOT_MAX_RADIUS + 24)
            return false;
        if (isAttacker && env.ball.z < env.me()->z && env.me()->getDistanceTo(env.ball) >= BALL_RADIUS + ROBOT_MAX_RADIUS + 12)
            return false;

        const auto myId = env.me()->id;
        Metric sel;
        Point selVel;
        int selJ = -1, selK = -1;
        AAction firstAction;

        static std::unordered_map<int, Point> prevVel;
        std::vector<Point> vels;
        Sandbox ballSnd = env;
        ballSnd.my.clear();

        for (int i = 0; i < 48; i++) {
            if (i % 6 == env.tick % 6) {
                auto ang = 2 * M_PI / 48 * i;
                vels.push_back(Helper::maxVelocityTo(*env.me(), *env.me() + Point(cos(ang), 0, sin(ang))));
            }
        }

        for (auto i = 0; i <= 50 && ballSnd.hasGoal == 0; i++) {
            if (i % 5 == 0 || i <= 6)
                vels.push_back(Helper::maxVelocityTo(*env.me(), ballSnd.ball));

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

        auto skipRobbotsCollisions = [myId](Sandbox &e) {
            if (e.ball.z > -10) {
                return false;
            }

            for (auto& item : e.robotsCollisions) {
                if (item.id1 == myId || item.id2 == myId) {
                    return true;
                }
            }
            return false;
        };

        auto counterPenalty = [isAttacker](Sandbox &e) {
            if (!isAttacker) {
                return true;
            }
            return e.ball.z < -ARENA_Z * 0.66;
        };

        const double al = 0.7 * drawAlpha;

        for (auto& mvel : vels) {
            Sandbox meSnd = env;
            auto mvAction = AAction().vel(mvel);
            auto jmpAction = mvAction;
            jmpAction.jump();

            for (auto j = 0; j <= 65; j++) {
                Sandbox meJumpSnd = meSnd;

                meJumpSnd.me()->action = jmpAction;

                if (j <= drawJ) {
                    Visualizer::addSphere(*meJumpSnd.me(), rgba(0, 0.8, 0, al));
                    Visualizer::addSphere(meJumpSnd.ball, rgba(0.4, 0.1, 0.4, al));
                    Visualizer::addSphere(meJumpSnd.opp[0], rgba(1, 0, 0, al * 0.5));
                    Visualizer::addSphere(meJumpSnd.opp[1], rgba(1, 0, 0, al * 0.5));
                }

                if (meSnd.me()->getDistanceTo(meSnd.ball) < BALL_RADIUS + ROBOT_MAX_RADIUS + 12) {
                    const int jumpMaxTicks = 20;
                    const int ballSimMaxTicks = 100;
                    for (auto k = 0; k <= jumpMaxTicks; k++) {
                        meJumpSnd.doTick(1);
                        auto tmp = meJumpSnd.me();
                        if (skipRobbotsCollisions(meJumpSnd)) {
                            break;
                        }

                        if (j == drawJ && k <= drawK) {
                            Visualizer::addSphere(*meJumpSnd.me(), rgba(0.7, 0.8, 0, al));
                            Visualizer::addSphere(meJumpSnd.ball, rgba(0.4, 0.1, 0.4, al));
                            //Visualizer::addSphere(meJumpSnd.opp[0], rgba(1, 0, 0, al * 0.5));
                            //Visualizer::addSphere(meJumpSnd.opp[1], rgba(1, 0, 0, al * 0.5));
                        }

                        double myCollisionVel = INT_MAX;
                        for (auto& item : meJumpSnd.robotBallCollisions) {
                            if (item.id1 == myId) {
                                myCollisionVel = item.velocity.z;
                            }
                        }

                        double minCounterDist2 = 10000;
                        Point md1, md2;

                        if (myCollisionVel < INT_MAX && (myCollisionVel >= 0 || isAttacker && meJumpSnd.ball.z > 0 || !isAttacker && meJumpSnd.ball.z < -30)) {

                            int shotTick = meJumpSnd.tick;

                            meJumpSnd.oppGkStrat = true;
                            double penalty = 0;
                            auto calcPenalty = counterPenalty(meJumpSnd);
                            if (calcPenalty)
                                meJumpSnd.oppCounterStrat = true;
                            int positiveTicks = 0;
                            double positiveChange = 0;
                            bool hasGk = false;
                            for (auto& x : meJumpSnd.opp) {
                                hasGk |= x.z > ARENA_Z - 5;
                            }
                            bool isFar = meJumpSnd.me()->z < 6;
                            bool noFarGoal = false;

                            for (int w = 0; w <= ballSimMaxTicks && meJumpSnd.hasGoal == 0; w++) {
                                auto prevBall = meJumpSnd.ball;
                                meJumpSnd.doTick(1);
                                if (meJumpSnd.ball.z > prevBall.z) {
                                    positiveChange += meJumpSnd.ball.z - prevBall.z;
                                    positiveTicks++;
                                }
                                if (meJumpSnd.ball.z < 0) {
                                    for (auto &o : meJumpSnd.opp) {
                                        if (o.z > meJumpSnd.ball.z) {
                                            auto dst2 = o.getDistanceTo2(meJumpSnd.ball);
                                            if (dst2 < minCounterDist2) {
                                                minCounterDist2 = dst2;
                                                md1 = o;
                                                md2 = meJumpSnd.ball;
                                            }
                                        }
                                    }
                                }
                                if (w > 10 && meJumpSnd.ball.z < BALL_RADIUS * 1.1 && hasGk) {
                                    noFarGoal = true;
                                }


                                if (j == drawJ && k <= drawK) {
                                    Visualizer::addSphere(meJumpSnd.ball, rgba(1, 1, 1, al));
                                    //Visualizer::addSphere(meJumpSnd.opp[0], rgba(1, 0, 0, al * 0.25));
                                    //Visualizer::addSphere(meJumpSnd.opp[1], rgba(1, 0, 0, al * 0.25));
                                }
                            }
                            if (calcPenalty) {
                                auto dst = sqrt(minCounterDist2) - ROBOT_RADIUS - BALL_RADIUS;
                                if (j == drawJ && k <= drawK) {
                                    Visualizer::addLine(md1, md2, 20, rgba(0, 0, 0));
                                    for (int i = 0; i <= 24; i++) {
                                        Visualizer::addSphere(md1 + (md2 - md1) / 24 * i, 0.2, rgba(0, 0, 0));
                                    }
                                }
                                penalty = dst;
                            }

                            bool hasGoal = meJumpSnd.hasGoal > 0;
                            if (hasGoal && noFarGoal && isFar) {
                                hasGoal = false;
                            }
                            if (positiveTicks > 5 && meJumpSnd.hasGoal >= 0) {

                                Metric cand = {hasGoal, positiveChange, positiveTicks, penalty,
                                               shotTick - env.tick};

                                if (selJ == -1 || sel < cand) {
                                    ActionSeq seq;
                                    seq.add({j, mvAction});
                                    seq.add({k, jmpAction});

                                    ActionSeqItem item = {};

                                    selVel = mvel;
                                    selJ = j;
                                    selK = k;
                                    sel = cand;
                                    firstAction = j == 0 ? jmpAction : mvAction;
                                }
                            }

                            break;
                        }
                    }
                }

                meSnd.me()->action = mvAction;
                meSnd.doTick(1);
                if (skipRobbotsCollisions(meSnd)) {
                    break;
                }
            }
        }

        if (selJ >= 0) {
            resMetric = sel;
            resAction = firstAction;
            auto teammateLastShotTime = lastShotTime[env.teammate1()->id];
            auto ret = sel.timeToShot < teammateLastShotTime;
#ifdef DEBUG
            if (drawJ < 0) {
                AAction t1;
                Metric t2;
                tryShotOutOrGoal(isAttacker, t1, t2, selVel, selJ, selK, ret ? 1 : 0.3);
            }
#endif
            prevVel[myId] = selVel;
            lastShotTime[myId] = sel.timeToShot;
            return ret;
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
        e.stopOnGoal = false;
        for (int i = 1; i <= 100; i++) {
            e.doTick(5);
            if (e.ball.y - BALL_RADIUS > ROBOT_MAX_RADIUS*3)
                continue;

            for (auto r : e.robots()) {
                auto t = 1.0 * i / TICKS_PER_SECOND;
                Point oppGoal(0, 0, ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH / 2);
                if (!r->isTeammate)
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

    Point oppGoal = Point(0, 0, ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH / 2);

    std::pair<std::optional<AAction>, std::optional<AAction>> moveToBallUsual(double ballHeight = BALL_RADIUS + 3) {
        Sandbox snd = env;
        snd.stopOnGoal = false;
        auto me = *snd.me();
        snd.clearRobots();
        std::optional<AAction> firstAction, secondAction;

        for (auto i = 1; i <= 12 * TICKS_PER_SECOND; i++) {
            snd.doTick(1);
            if (snd.ball.y > ballHeight)
                continue;
            auto t = 1.0 * i / TICKS_PER_SECOND;

            auto tar = snd.ball + (snd.ball - oppGoal).take(BALL_RADIUS * 3.0);
            Point delta_pos = tar - me;
            delta_pos.y = 0;
            auto need_speed = delta_pos.length() / t;
            auto target_velocity = delta_pos.take(std::min(ROBOT_MAX_GROUND_SPEED, need_speed));
            AAction act;
            act.targetVelocity = target_velocity;

            if (need_speed <= ROBOT_MAX_GROUND_SPEED) {
                firstAction = act;
                Visualizer::addLine(me, me + delta_pos, 1, rgba(0, 0, 1));
                break;
            }
            secondAction = act;
        }
        return {firstAction, secondAction};
    }

    void act(AAction &action, bool isFirst) {
        auto& ball = env.ball;
        auto& me = *env.me();

        if (env.tick == lastTick) {
            for (auto tm : env.teammates(me.id))
                tm->action = prevEnv.robot(tm->id)->action;
        } else if (env.roundTick > 0) {
            prevEnv.doTick();
            checkEvalState();
        }


        if (isFirst) {
            std::vector<ABall> cache;
            Sandbox ballEnv = env;
            ballEnv.clearRobots(); // важно
            for (int i = 0; i < 200; i++) {
                if (i % 6 == 5) {
                    Visualizer::addSphere(ballEnv.ball, rgba(1, 0, 0, 0.2));
                }
                ballEnv.doTick();
                cache.push_back(ballEnv.ball);
            }
            Sandbox::loadBallsCache(cache);
        }

        lastShotTime[me.id] = INT_MAX;

        auto firstToBall = evalToBall();

        bool is_attacker = selectGk() != me.id;
        Metric metric;
        std::optional<AAction> firstAction, secondAction;

//        if (is_attacker) return;

        if (is_attacker && env.roundTick <= 35) {
            action = AAction().vel(Helper::maxVelocityTo(me, env.ball + Point(0, 0, -3.2)));
        } else if (!is_attacker && env.roundTick <= 30) {
            action = AAction().vel(Helper::maxVelocityTo(me, Point(0, 0, -ARENA_Z)));
        } else if (is_attacker && tryShotOutOrGoal(is_attacker, action, metric)) {
            std::string msg = metric.toString();
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
                    Visualizer::addLine(me, me + action.targetVelocity * 2 * ROBOT_RADIUS, 3, rgba(1, 1, 0));
                    Visualizer::addLine(me, oppGoal, 0.2, rgba(0, 0, 1));
                } else if (!firstToBall || !firstToBall.value().isTeammate || firstToBall.value().id == me.id) {
                    std::tie(firstAction, secondAction) = moveToBallUsual();

                    if (firstAction) {
                        action = firstAction.value();
                    } else if (secondAction) {
                        action = secondAction.value();
                    } else {
                        if (ball.z > -6)
                            action.vel(Helper::maxVelocityTo(me, Point(0, 0, 17)));
                        else
                            is_attacker = false;
                    }
                } else {
                    auto opp = env.opp[0].z < env.opp[1].z ? env.opp[0] : env.opp[1];
                    action.targetVelocity = Helper::maxVelocityTo(me, opp - Point(0, 0, 7*ROBOT_RADIUS));
                }
            }

            if (!is_attacker) {
                Visualizer::addSphere(me, me.radius * 1.1, rgba(1, 0.7, 0));

                if ((firstToBall && firstToBall.value().id == me.id || ball.velocity.z < 0 && ball.z < -ARENA_DEPTH / 5 || me.getDistanceTo(ball) < BALL_RADIUS + ROBOT_RADIUS + 4)
                    && tryShotOutOrGoal(is_attacker, action, metric)) {

                    std::string msg = "(gk) " + metric.toString();
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
                        if (firstToBall && firstToBall.value().id == me.id) {
                            std::tie(firstAction, secondAction) = moveToBallUsual();
                        }
                        if (firstAction) {
                            action = firstAction.value();
                        } else {
                            action = goToGoalCenterStrat(env);
                        }
                    }
                }
            }
        }
    }
};


#endif //CODEBALL_STRAT_H
