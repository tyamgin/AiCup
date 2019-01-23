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
            if (std::abs(prev->nitroAmount - me->nitroAmount) > EPS) {
                LOG("nitro_amount calculated wrong");
            }
        }
    }

    struct Direction {
        Point dir;
        double speedFactor;

        bool operator <(const Direction& other) const {
            if (speedFactor != other.speedFactor)
                return speedFactor < other.speedFactor;
            return dir < other.dir;
        }

        bool operator ==(const Direction& other) const {
            if (speedFactor != other.speedFactor)
                return false;
            return dir == other.dir;
        }
    };

    struct Metric {
        int j = -1, k = -1;
        Direction dir;
        bool hasGoal = false;
        bool hasShot = false;
        double positiveChange = 0;
        int positiveTicks = 0;
        double penalty = 0;
        int timeToShot = INT_MAX;
        double minBallZ = 0;
        bool hasOppTouch = false;
        double goalHeight;
        double passMinDist;


        auto getComparable() const {
            const double xx = 5;
            double pen = (xx - std::min(xx, penalty)) / xx * 1.;
            const auto goalZ = -(ARENA_Z + BALL_RADIUS);
            const auto goalSafeZ = goalZ + 0.5;
            double injPen = 0;
            if (minBallZ < goalSafeZ) {
                injPen = (minBallZ - goalSafeZ) / (goalZ - goalSafeZ) * 0.4;
            }
            double touchPen = 0;
            if (hasGoal && hasOppTouch) {
                touchPen = 0.3;
            }
            double heightAdd = 0;
            if (hasGoal) {
                heightAdd += goalHeight / ARENA_GOAL_HEIGHT / 2;
            }
            double base;
            if (hasGoal) {
                base = (positiveChange / (positiveTicks + 2*timeToShot + 1)) - pen - injPen - touchPen + dir.speedFactor + heightAdd;
            } else {
                base = -passMinDist - pen * 30;
            }
            return std::make_tuple(hasGoal, hasShot, base);
        }

        bool operator <(const Metric &m) const {
            return getComparable() < m.getComparable();
        }

        std::string toString() {
            return std::string(hasGoal ? "GOAL" : "SHOT") +
                " pow=" + std::to_string(dir.speedFactor) +
                " t=" + std::to_string(timeToShot) +
                " p=" + std::to_string(penalty) +
                " " + std::to_string(std::get<2>(getComparable())) +
                (hasOppTouch ? " (!)" : "");
        }
    };

    std::unordered_map<int, int> lastShotTime;


    bool tryShotOutOrGoal(bool isAttacker, AAction &resAction, Metric& resMetric, Metric* drawMetric = nullptr, double drawAlpha = 1.0) {
        //
        // TODO:
        // Улучшить удары по роботам
        //
        // Точнее сиимить подборы после сейвов
        //
        // Можно убрать из симы неактивных игроков (например, свой вратать после удара)
        //

        if (isAttacker && env.me()->getDistanceTo(env.ball) >= BALL_RADIUS + ROBOT_MAX_RADIUS + 24)
            return false;
        if (isAttacker && env.ball.z < env.me()->z && env.me()->getDistanceTo(env.ball) >= BALL_RADIUS + ROBOT_MAX_RADIUS + 12)
            return false;

        const auto myId = env.me()->id;
        Metric sel;
        AAction firstAction;

        static std::unordered_map<int, Metric> prevMetric;
        std::vector<Direction> dirs;
        Sandbox ballSnd = env;
        ballSnd.my.clear();

        bool sm = false;

        for (int i = 0; i < 48; i++) {
            if (i % 6 == env.tick % 6) {
                auto ang = 2 * M_PI / 48 * i;
                dirs.push_back({Point(cos(ang), 0, sin(ang)), 1});
                if (sm) {
                    if (isAttacker && env.tick % 3 == 0) {
                        dirs.push_back({Point(cos(ang), 0, sin(ang)), 0.8});
                    }
                    if (isAttacker && env.tick % 5 == 4) {
                        dirs.push_back({Point(cos(ang), 0, sin(ang)), 0.65});
                    }
                }
            }
        }

        for (auto i = 0; i <= 50 && ballSnd.hasGoal == 0; i++) {
            if (i % 5 == 0 || i <= 6) {
                dirs.push_back({ballSnd.ball - *env.me(), 1});
                if (sm) {
                    if (isAttacker && env.tick % 3 == 0) {
                        dirs.push_back({ballSnd.ball - *env.me(), 0.8});
                    }
                    if (isAttacker && env.tick % 5 == 4) {
                        dirs.push_back({ballSnd.ball - *env.me(), 0.65});
                    }
                }
            }

            ballSnd.doTick(5);
        }
        if (prevMetric.count(myId)) {
            dirs.push_back(prevMetric[myId].dir);
        }
        if (drawMetric != nullptr) {
            dirs = {drawMetric->dir};
        }
        std::sort(dirs.begin(), dirs.end());
        dirs.erase(std::unique(dirs.begin(), dirs.end()), dirs.end());

        auto skipRobotsCollisions = [myId](Sandbox &e) {
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
            if (GameInfo::isOpponentCrashed) {
                return false;
            }
            if (!isAttacker) {
                return e.ball.z < ARENA_Z * 0.5;
            }
            return e.ball.z < 5;// e.ball.z < -ARENA_Z * 0.4;
        };

        const double al = 0.7 * drawAlpha;

        for (auto& dir : dirs) {
            Sandbox meSnd = env;
            meSnd.oppCounterStrat = 1;
            AAction firstJAction;

            for (auto j = 0; j <= 65; j++) {
                Sandbox meJumpSnd = meSnd;
                auto mvAction = AAction(Helper::maxVelocityToDir(*meSnd.me(), dir.dir, dir.speedFactor));
                auto jmpAction = mvAction;
                jmpAction.jump();
                //jmpAction.nitro();

                meJumpSnd.me()->action = jmpAction;

                if (drawMetric && j <= drawMetric->j) {
                    Visualizer::addSphere(*meJumpSnd.me(), rgba(0, 0.8, 0, al));
                    Visualizer::addSphere(meJumpSnd.ball, rgba(0.4, 0.1, 0.4, al));
                    Visualizer::addSphere(meJumpSnd.opp[0], rgba(1, 0, 0, al * 0.5));
                    Visualizer::addSphere(meJumpSnd.opp[1], rgba(1, 0, 0, al * 0.5));
                }

                if (meSnd.me()->getDistanceTo(meSnd.ball) < BALL_RADIUS + ROBOT_MAX_RADIUS + 12) {
                    OP_START(K);

                    const int jumpMaxTicks = 20;
                    const int ballSimMaxTicks = 100;

                    int k;

                    int rcK = -1;
                    for (k = 0; k <= jumpMaxTicks; k++) {
                        meJumpSnd.doTick(1);

                        if (skipRobotsCollisions(meJumpSnd)) {
                            break;
                        }

                        if (drawMetric && j == drawMetric->j && k <= drawMetric->k) {
                            Visualizer::addSphere(*meJumpSnd.me(), rgba(0.7, 0.8, 0, al));
                            Visualizer::addSphere(meJumpSnd.ball, rgba(0.4, 0.1, 0.4, al));
                            //Visualizer::addSphere(meJumpSnd.opp[0], rgba(1, 0, 0, al * 0.5));
                            //Visualizer::addSphere(meJumpSnd.opp[1], rgba(1, 0, 0, al * 0.5));
                        }

                        double myCollisionVel = INT_MAX;
                        for (auto& item : meJumpSnd.robotBallCollisions) {
                            if (item.id1 == myId) {
                                myCollisionVel = item.velocity.z;
                                rcK = k;
                            }
                        }
                        if (rcK == -1) {
                            for (auto& item : meJumpSnd.robotsCollisions) {
                                if (item.id1 == myId && !GameInfo::isTeammateById[item.id2] || item.id2 == myId && !GameInfo::isTeammateById[item.id1]) {
                                    rcK = k;
                                }
                            }
                        }

                        double minCounterDist2 = 10000;
                        Point md1, md2;
                        bool hasShot = myCollisionVel < INT_MAX;

                        if (hasShot && (myCollisionVel >= 0 || /*isAttacker && meJumpSnd.ball.z > 0 ||*/ !isAttacker && meJumpSnd.ball.z < -30)
                            || !hasShot && k == jumpMaxTicks && rcK != -1 && meJumpSnd.me()->z > -10) {
                            OP_START(KW);

                            double passMinDist2 = 10000;

                            meJumpSnd = meSnd;
                            ARobot* fw = meJumpSnd.my[0].z > env.my[1].z ? &meJumpSnd.my[0] : &meJumpSnd.my[1];
                            fw->action = AAction().vel(Helper::maxVelocityTo(*fw, oppGoal));
                            meJumpSnd.me()->action = jmpAction;
                            double minZ = meJumpSnd.ball.z;

                            for (int q = 0; q <= rcK; q++) {
                                meJumpSnd.doTick(q == rcK ? MICROTICKS_PER_TICK : 1);
                                updMin(minZ, meJumpSnd.ball.z);
                            }
                            // TODO: что если коллизии после пересчёта не будет?

                            fw->action = AAction(Helper::maxVelocityTo(*fw, oppGoal));

                            int shotTick = meJumpSnd.tick;

                            meJumpSnd.oppGkStrat = true;
                            meJumpSnd.oppCounterStrat = 2;

                            int positiveTicks = 0;
                            double positiveChange = 0;
                            bool hasGk = false;
                            for (auto& x : meJumpSnd.opp) {
                                hasGk |= x.z > ARENA_Z - 8;
                            }
                            bool isFar = meJumpSnd.me()->z < -10;
                            bool noFarGoal = false;
                            bool hasOppTouch = false;

                            for (int w = 0; w <= ballSimMaxTicks && meJumpSnd.hasGoal == 0; w++) {
                                auto prevBallZ = meJumpSnd.ball.z;
                                meJumpSnd.doTick(1);
                                for (auto& item : meJumpSnd.robotBallCollisions) {
                                    if (!GameInfo::isTeammateById[item.id1]) {
                                        hasOppTouch = true;
                                    }
                                }
                                updMin(minZ, meJumpSnd.ball.z);
                                if (meJumpSnd.ball.z > prevBallZ) {
                                    positiveChange += meJumpSnd.ball.z - prevBallZ;
                                    positiveTicks++;
                                }

                                //passMinDist2 = std::min(passMinDist2, meJumpSnd.ball.getDistanceTo2(passTar1));
                                if (w > 30) {
                                    auto passTar1 = fw->_y(0) + (fw->id == myId ? Helper::goalDir(*fw, 7) + Point(0, 4, 0) : Helper::goalDir(*fw, 9) + Point(0, 10, 0));
                                    updMin(passMinDist2, meJumpSnd.ball.getDistanceTo2(passTar1));
                                }


                                auto calcPenalty = counterPenalty(meJumpSnd);
                                if (calcPenalty /*meJumpSnd.ball.z < 0*/) {
                                    for (auto& o : meJumpSnd.opp) {
                                        if (o.z + 5 > meJumpSnd.ball.z) {
                                            auto dst2 = o.getDistanceTo2(meJumpSnd.ball);
                                            if (dst2 < minCounterDist2) {
                                                minCounterDist2 = dst2;
                                                md1 = o;
                                                md2 = meJumpSnd.ball;
                                            }
                                        }
                                    }
                                }
                                if (w > 10 && meJumpSnd.ball.y < BALL_RADIUS * 1.1 && hasGk) {
                                    noFarGoal = true;
                                }


                                if (drawMetric && j == drawMetric->j && k <= drawMetric->k) {
                                    Visualizer::addSphere(meJumpSnd.ball, rgba(1, 1, drawMetric->hasGoal ? 0 : 1, al));
                                    //Visualizer::addSphere(meJumpSnd.opp[0], rgba(1, 0, 0, al * 0.25));
                                    //Visualizer::addSphere(meJumpSnd.opp[1], rgba(1, 0, 0, al * 0.25));
                                }
                            }

                            double penalty = sqrt(minCounterDist2) - ROBOT_RADIUS - BALL_RADIUS;
                            if (drawMetric && j == drawMetric->j && k <= drawMetric->k) {
                                Visualizer::addLine(md1, md2, 20, rgba(0, 0, 0));
                                for (int i = 0; i <= 24; i++) {
                                    Visualizer::addSphere(md1 + (md2 - md1) / 24 * i, 0.2, rgba(0, 0, 0));
                                }
                            }

                            double goalHeight = meJumpSnd.ball.y;
                            bool hasGoal = meJumpSnd.hasGoal > 0;
                            if (hasGoal && !GameInfo::isOpponentCrashed) {
                                if (noFarGoal && isFar && goalHeight < ARENA_GOAL_HEIGHT * 0.7) {
                                    hasGoal = false;
                                }

                                if (meJumpSnd.tick - shotTick > 30 && hasOppTouch) {
                                    hasGoal = false;
                                }
                            }

                            if (hasShot && meJumpSnd.hasGoal >= 0 || !hasShot && meJumpSnd.hasGoal > 0) {

                                //if (meJumpSnd.hasGoal > 0 || meSnd.me()->z < ARENA_Z * 0.3 || std::abs(meSnd.me()->x) > ARENA_GOAL_WIDTH/2 * 1.3)
                                //if (!isAttacker || meJumpSnd.hasGoal > 0 || tt.z < -20)
                                {

                                    Metric cand = {j, k, dir, hasGoal, hasShot, positiveChange, positiveTicks, penalty,
                                                   shotTick - env.tick, minZ, hasOppTouch, goalHeight,
                                                   sqrt(passMinDist2)};

                                    if (sel.j == -1 || sel < cand) {
                                        sel = cand;
                                        firstAction = j == 0 ? jmpAction : firstJAction;
                                    }
                                }
                            }

                            OP_END(KW);

                            break;
                        }
                    }

                    OP_END(K);
                }

                meSnd.me()->action = mvAction;
                if (j == 0) {
                    firstJAction = mvAction;
                }
                meSnd.doTick(1);
                if (skipRobotsCollisions(meSnd)) {
                    break;
                }

            }

            if (!env.me()->touch || env.me()->touchNormal.y < EPS) {
                break;
            }
        }

        if (sel.j >= 0) {
            resMetric = sel;
            resAction = firstAction;
            auto teammateLastShotTime = lastShotTime[env.teammate1()->id];
            auto ret = sel.timeToShot < teammateLastShotTime;
#ifdef DEBUG
            if (drawMetric == nullptr) {
                AAction t1;
                Metric t2;
                tryShotOutOrGoal(isAttacker, t1, t2, &sel, ret ? 1 : 0.3);
            }
#endif
            prevMetric[myId] = sel;
            lastShotTime[myId] = sel.timeToShot;
            return ret;
        }
        return false;
    }

    bool tryTakeNitro(bool isAttacker, AAction& resAction) {
        return false;

        if (!GameInfo::isNitro) {
            return false;
        }

        if (isAttacker) {
            return false;
        }
        if (env.me()->nitroAmount >= MAX_NITRO_AMOUNT) {
            return false;
        }

        Point center(0, 0, -ARENA_Z);
        AAction selAction;
        int selI = -1, selJ = -1;
        int minTm = INT_MAX;

        for (auto& pack : env.nitroPacks) {
            if (pack.z > 0) {
                continue;
            }

            double minDistToCenter = 10000;
            bool minDistGotcha = false;
            int selTm = INT_MAX;

            auto meSnd = env;
            meSnd.clearRobots(true);
            auto me = meSnd.me();
            AAction firstIAction;
            for (int i = 0; i < 60; i++) {
                auto backSnd = meSnd;

                bool intersected = false;
                bool gotcha = false;
                int gotchaTm = INT_MAX;
                int gotchaJ = -1;
                AAction firstJAction;

                for (int j = 0; j < 20; j++) {
                    if (!intersected) {
                        auto g = backSnd.getIntersectedNitroPack(*backSnd.me());
                        if (g) {
                            for (auto &item : backSnd.nitroPacksCollected) {
                                if (item.id1 == me->id && item.id2 == g->id) {
                                    gotcha = true;
                                    gotchaJ = j;
                                    gotchaTm = backSnd.tick - env.tick;
                                    break;
                                }
                            }
                            intersected = true;
                        }
                    }

                    backSnd.me()->action = AAction().vel(Helper::maxVelocityTo(*backSnd.me(), center));
                    if (j == 0)
                        firstJAction = AAction().vel(Helper::maxVelocityTo(*backSnd.me(), center));
                    backSnd.doTick(1);
                }
                if (intersected && backSnd.me()->getDistanceTo(center) < minDistToCenter) {
                    minDistToCenter = backSnd.me()->getDistanceTo(center);
                    minDistGotcha = gotcha;
                    selAction = i > 0 ? firstIAction : firstJAction;
                    selI = i;
                    selJ = gotchaJ;
                    selTm = gotchaTm;
                }

                auto act = AAction().vel(Helper::maxVelocityTo(*me, pack)).nitro();
                me->action = act;
                if (i == 0)
                    firstIAction = act;
                meSnd.doTick(1);

                if (meSnd.nitroPacksCollected.size()) {
                    break;//TODO
                }
            }

            if (minDistGotcha) {
                if (selTm < minTm) {
                    resAction = selAction;
                    minTm = selTm;
                }
            }
        }
        if (selI == -1) {
            return false;
        }
        return true;
    }

    AAction goToGoalCenterStrat(Sandbox &e) {
        AAction sAct;
        double ch = 0.8;
        double maxDeep = 2 + ch;
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
        auto snd = env;
        snd.opp.clear();
        std::vector<std::tuple<int, double, int>> gotcha(7, {INT_MAX, 100.0, INT_MAX});
        for (auto& x : snd.my) {
            std::get<1>(gotcha[x.id]) = x.z;
            std::get<2>(gotcha[x.id]) = x.id;
        }
//        for (int i = 0; i < 200; i++) {
//            for (auto& x : snd.my) {
//                x.action = AAction(Helper::maxVelocityTo(x, myGoal));
//                if (x._y(0).getDistanceTo2(myGoal) < SQR(1)) {
//                    std::get<0>(gotcha[x.id]) = std::min(std::get<0>(gotcha[x.id]), i);
//                }
//            }
//            snd.doTick(1);
//        }

        return std::get<2>(*std::min_element(gotcha.begin(), gotcha.end()));
    }

    int selectGk_old() {
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

    std::tuple<std::optional<ARobot>, std::optional<ARobot>> evalToBall() {
        Sandbox ballSnd = env;
        ballSnd.stopOnGoal = false;
        std::vector<int> minTime(7, INT_MAX);

        for (int i = 0; i <= 100; i++) {
            ballSnd.doTick(1);
            if (ballSnd.ball.y - BALL_RADIUS > ROBOT_MAX_RADIUS*5)
                continue;
            if (i % 5 != 0)
                continue;

            auto e = env;
            for (int j = 0; j < 100; j++) {
                for (auto r : e.robots()) {
                    Point oppGoal(0, 0, ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH / 2);
                    if (!r->isTeammate)
                        oppGoal.z *= -1;

                    auto tar = ballSnd.ball + (ballSnd.ball - oppGoal)._y(0).take(BALL_RADIUS * 1);
                    r->action.vel(Helper::maxVelocityTo(*r, ballSnd.ball));

                    if (r->getDistanceTo(tar) < (r->isTeammate ? 3 : 6)) {
                        updMin(minTime[r->id], j);
                    }
                }
                e.doTick(1);
            }
        }
        int minTimeMy = INT_MAX;
        int minTimeAll = INT_MAX;
        std::optional<ARobot> resMy, resAll;
        for (auto x : env.robots()) {
            if (x->isTeammate && minTime[x->id] < minTimeMy)
                minTimeMy = minTime[x->id], resMy = *x;
            if (minTime[x->id] < minTimeAll)
                minTimeAll = minTime[x->id], resAll = *x;
        }
        return {resAll, resAll}; //TODO
    }

    Point oppGoal = Point(0, 0, ARENA_Z + ARENA_GOAL_DEPTH / 2);
    Point myGoal = Point(0, 0, -(ARENA_Z + ARENA_GOAL_DEPTH / 2));

    std::pair<std::optional<AAction>, std::optional<AAction>> moveToBallUsual(double ballHeight = BALL_RADIUS + 3) {
        Sandbox snd = env;
        snd.stopOnGoal = false;
        auto me = *snd.me();
        for (int i = (int) snd.my.size() - 1; i >= 0; i--)
            if (snd.my[i].id == me.id)
                snd.my.erase(snd.my.begin() + i);

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

    bool alarm;

    void act(AAction &action, bool isFirst) {
        auto& ball = env.ball;
        auto& me = *env.me();

        if (env.tick == lastTick) {
            for (auto tm : env.teammates(me.id))
                tm->action = prevEnv.robot(tm->id)->action;
        } else if (env.roundTick > 0) {
            for (auto& opp : env.opp) {
                if (opp.nitroAmount < prevEnv.robot(opp.id)->nitroAmount) {
                    GameInfo::usedNitro[opp.id] = true;
                    for (int i = 0; i < 12; i++) {
                        double ang = 2 * M_PI / 12 * i;
                        Point pt(cos(ang), 0, sin(ang));
                        Visualizer::useNitro(opp);
                    }
                }
            }

            prevEnv.doTick();
            checkEvalState();
        }

//        if (env.tick >= 23) {
//            for (int w = 0; w < 10; w++) {
//                auto e100 = env;
//                auto e1 = env;
//                auto vel = e1.me()->velocity * 1.2;
//                e1.me()->action.jump().nitro().vel(vel);
//                e100.me()->action.jump().nitro().vel(vel);
//                e1.doTick(1);
//                e100.doTick();
//                auto me1 = e1.me();
//                auto me100 = e100.me();
//                std::cout << "";
//            }
//        }
//
//        if (env.tick >= 20) {
//            action.jump();
//            return;
//        }

        const int alarmTicks = 45;

        if (isFirst) {
            std::vector<ABall> cache;
            Sandbox ballEnv = env;
            ballEnv.stopOnGoal = false;
            ballEnv.clearRobots(); // важно
            for (int i = 0; i < 200; i++) {
                if (i % 6 == 5) {
                    Visualizer::addSphere(ballEnv.ball, rgba(1, 0, 0, 0.2));
                }
                ballEnv.doTick();
                cache.push_back(ballEnv.ball);
                if (i == alarmTicks - 1) {
                    alarm = ballEnv.hasGoal < 0;
                }
            }
            Sandbox::loadBallsCache(cache);
        }

        if (alarm) {
            Visualizer::addText("ALARM!!!");
        }

        bool hasPrevShot = lastShotTime[me.id] < INT_MAX;
        lastShotTime[me.id] = INT_MAX;

        std::optional<ARobot> firstToBall, firstToBallMy;
        std::tie(firstToBall, firstToBallMy) = evalToBall();
        if (firstToBall) {
            Visualizer::addLine(firstToBall.value(), firstToBall.value()._y(0) + Point(0, ARENA_HEIGHT, 0), 3, rgba(0, 0, 0));
        }

        bool is_attacker = selectGk() != me.id;
        Metric metric;
        std::optional<AAction> firstAction, secondAction;

//        if (is_attacker) return;

        if (!is_attacker) {
            Visualizer::addSphere(me, me.radius * 1.1, rgba(1, 0.7, 0));
        }


        if (is_attacker && env.roundTick <= 35) {
            action = AAction().vel(Helper::maxVelocityTo(me, env.ball + Point(0, 0, -3.2)));
            return;
        }
        if (!is_attacker && env.roundTick <= 30) {
            action = AAction().vel(Helper::maxVelocityTo(me, Point(0, 0, -ARENA_Z)));
            return;
        }
        if (is_attacker && tryShotOutOrGoal(is_attacker, action, metric)) {
            //if (metric.hasGoal) {
                std::string msg = metric.toString();
                Visualizer::addText(msg);
                return;
            //}
        }
        if (tryTakeNitro(is_attacker, action)) {
            Visualizer::addText("Go to nitro");
            return;
        }

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
                        action.vel(Helper::maxVelocityTo(me, Point(0, 0, 14)));
                    else
                        is_attacker = false;
                }
            } else {
                auto opp = env.opp[0].z < env.opp[1].z ? env.opp[0] : env.opp[1];
                //action.targetVelocity = Helper::maxVelocityTo(me, opp - Point(0, 0, 7*ROBOT_RADIUS));
                action.targetVelocity = Helper::maxVelocityTo(me, Point(0, 0, 10));
            }
        }


        if (!is_attacker) {
            Visualizer::addSphere(me, me.radius * 1.1, rgba(1, 0.7, 0));

            if ((firstToBallMy && firstToBallMy.value().id == me.id || ball.velocity.z < 0 && ball.z < -ARENA_Z / 2 || me.getDistanceTo(ball) < BALL_RADIUS + ROBOT_RADIUS + 5 || alarm || hasPrevShot)
                && tryShotOutOrGoal(is_attacker, action, metric)) {

                std::string msg = "(gk) " + metric.toString();
                Visualizer::addText(msg);
                LOG(msg);
            } else {
                std::optional<AAction> defend;
                Sandbox e1 = env;

                if (alarm) {
                    Sandbox e2 = env;
                    AAction act;
                    act.jumpSpeed = ROBOT_MAX_JUMP_SPEED;
                    e2.me()->action = act;
                    for (int i = 0; i < alarmTicks; i++) {
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
                            for (int i = 0; i < alarmTicks - wt; i++) {
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
                    if (firstToBallMy && firstToBallMy.value().id == me.id) {
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
};


#endif //CODEBALL_STRAT_H
