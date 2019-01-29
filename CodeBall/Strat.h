#ifndef CODEBALL_STRAT_H
#define CODEBALL_STRAT_H

#include "Sandbox.h"

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

    struct Correction {
        double fx = 1, fy = 1, fz = 1;

        std::string toString() const {
            std::stringstream out;
            out << fx << " " << fy << " " << fz;
            return out.str();
        }
    };

    struct Direction {
        Point dir;
        double speedFactor;
        Correction correction;
        bool toBallAfterJump = false;

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
        int tick = 0;
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
        ABall goalBall;
        std::optional<ARobot> gkRobot;
        double passMinDist = 0;
        int touchFloorCount = 0;
        bool _debugFalsePositive = false;

        double getHeightAdd() const {
            if (hasGoal) {
                return goalBall.y / ARENA_GOAL_HEIGHT / 2;
            }
            return 0;
        }

        double getWidthAdd() const {
            return 0; // TODO

            if (hasGoal && gkRobot) {
                auto& gk = gkRobot.value();
                auto diff = std::abs(goalBall.x - gk.x);
                //if (diff > 1.15 && gk.isDetouched()) {
                    return diff / ARENA_GOAL_WIDTH / 2;
                //}
            }
            return 0;
        }

        double getCounterPenalty() const {
            const double xx = 5;
            return (xx - std::min(xx, penalty)) / xx * 1.;
        }

        double getGoalInjPen() const {
            const auto goalZ = -(ARENA_Z + BALL_RADIUS);
            const auto goalSafeZ = goalZ + 0.5;

            if (minBallZ < goalSafeZ) {
                return (minBallZ - goalSafeZ) / (goalZ - goalSafeZ) * 0.4;
            }
            return 0;
        }

        double getOpponentTouchPen() const {
            if (hasGoal && hasOppTouch) {
                return 0.3;
            }
            return 0;
        }

        auto getComparable() const {
            double base;
            if (hasGoal) {
                base = (positiveChange / (positiveTicks + 2*timeToShot + 1)) - getCounterPenalty() - getGoalInjPen() - getOpponentTouchPen() + dir.speedFactor + getHeightAdd() + getWidthAdd() - touchFloorCount * 0.4;
            } else {
                base = -passMinDist - getCounterPenalty() * 30 - touchFloorCount * 7;
            }
            return std::make_tuple(hasGoal, hasShot, base);
        }

        bool operator <(const Metric &m) const {
            return getComparable() < m.getComparable();
        }

        std::string toString() {
            return std::string(hasGoal ? "GOAL" : "SHOT") +
                //" pow=" + std::to_string(dir.speedFactor) +
                " t=" + std::to_string(timeToShot) +
                " p=" + std::to_string(penalty) +
                " " + std::to_string(std::get<2>(getComparable())) +
                " qp=" + std::to_string(touchFloorCount) +
                (hasGoal ? " h=" + std::to_string(getHeightAdd()) + " w=" + std::to_string(getWidthAdd()) : "") +
                (hasOppTouch ? " (!)" : "") +
                " pf=" + dir.correction.toString();
        }
    };

    std::unordered_map<int, Metric> prevMetric;

    int wildCardCounts = 0;

    bool tryShotOutOrGoal(int isAttacker, AAction &resAction, Metric& resMetric, Metric* drawMetric = nullptr, double drawAlpha = 1.0) {
        //
        // TODO:
        // Улучшить удары по роботам
        //
        // Точнее симить подборы после сейвов
        //
        // Можно убрать из симы неактивных игроков (например, свой вратать после удара)
        //
        // First to goal: учитывать tryShotOutOrGoal
        //
        // penalty по убывающей
        //

        if (isAttacker && env.me()->getDistanceTo(env.ball) >= BALL_RADIUS + ROBOT_MAX_RADIUS + 24)
            return false;
        if (isAttacker && env.ball.z < env.me()->z && env.me()->getDistanceTo(env.ball) >= BALL_RADIUS + ROBOT_MAX_RADIUS + 12)
            return false;
        if (isAttacker && env.me()->z > -ARENA_Z / 2 && env.me()->z > env.ball.z + 3 && env.ball.velocity.z < -1)
            return false;

        int leafsCount = 0;

        const auto myId = env.me()->id;
        Metric sel, cand;
        AAction firstAction;

        std::vector<Direction> dirs;
        Sandbox ballSnd = env;
        ballSnd.clearMyRobots();

        if (prevMetric.count(myId)) {
            dirs.push_back(prevMetric[myId].dir);
        }

        static const std::vector<std::vector<double>> yCorrs = {
                {0.99, 0.995, 1.01, 1.03},
                {1.02},
                {0.95, 0.98},
                {0.9, 1.05},
        };
        static const std::vector<std::vector<double>> xCorrs = {
                {0.8, 0.9, 1.1, 1.2},
                {0.99, 1.01},
                {0.7, 1.3},
                {0.85, 1.15},
                {0.98, 1.02}
        };

        bool skipOpt = GameInfo::isFinal && env.me()->id % 3 == env.tick % 3;

        if (!dirs.empty() && prevMetric[myId].tick >= env.tick - 1 && env.me()->isDetouched() && env.me()->nitroAmount > EPS) {
            auto prv = dirs[0];
            for (size_t yCorrsIdx = 0; yCorrsIdx < yCorrs.size(); yCorrsIdx++) {
                if (yCorrsIdx == 0 || (yCorrsIdx % yCorrs.size() == env.tick % yCorrs.size() && !skipOpt)) {
                    for (double fy : yCorrs[yCorrsIdx]) {
                        dirs.push_back({prv.dir._y(prv.dir.y * fy), prv.speedFactor, {1, fy, 1}});
                    }
                }
            }
            if (!skipOpt) {
                for (size_t xCorrsIdx = 0; xCorrsIdx < xCorrs.size(); xCorrsIdx++) {
                    if (xCorrsIdx % xCorrs.size() == env.tick % xCorrs.size()) {
                        for (double fx : xCorrs[xCorrsIdx]) {
                            dirs.push_back({prv.dir._x(prv.dir.x * fx), prv.speedFactor, {fx, 1, 1}});
                        }
                    }
                }
            }
        } else if (!skipOpt) {
            for (int i = 0; i < 48; i++) {
                if (i % 6 == env.tick % 6) {
                    auto ang = 2 * M_PI / 48 * i;
                    dirs.push_back({Point(cos(ang), 0, sin(ang)), 1, Correction(), true});
                }
            }
        }

        if (!skipOpt) {
            const int startI = env.me()->getDistanceTo(env.ball) - env.me()->radius - BALL_RADIUS > 14 ? 20 : 0;
            for (auto i = 0; i <= 54 && ballSnd.hasGoal == 0; i++) {
                if (i >= startI) {
                    if (i % 6 == 0 || i <= 1) {
                        dirs.push_back({ballSnd.ball - *env.me(), 1});
                    }
                }

                ballSnd.doTick(5);
            }
        }

        if (drawMetric != nullptr) {
            dirs = {drawMetric->dir};
        }
        std::sort(dirs.begin(), dirs.end());
        dirs.erase(std::unique(dirs.begin(), dirs.end()), dirs.end());

        auto skipRobotsCollisions = [myId](Sandbox& e) {
            if (e.ball.z > -10) {
                return false;
            }

            return bool(e.robotsCollisions & Sandbox::myAnyMask[myId]);
        };

        auto counterPenalty = [isAttacker](Sandbox &e) {
            if (GameInfo::isOpponentCrashed) {
                return false;
            }
            if (!isAttacker) {
                return e.ball.z < ARENA_Z * 0.5;
            }
            return e.ball.z < 13;// e.ball.z < -ARENA_Z * 0.4;
        };

        const double al = 0.7 * drawAlpha;

        bool ballFloorTouch = !isAttacker && hasGkFloorTouch();

        for (auto& dir : dirs) {
            Sandbox meSnd = env;
            meSnd.oppCounterStrat = 1;
            AAction firstJAction;

            bool isInGoal = false;

            const int forKMaxDist = 14;
            int wildcard = 0;
            for (auto j = 0; j + wildcard <= 60; j++) {
                auto mvAction = AAction(Helper::maxVelocityToDir(*meSnd.me(), dir.dir, dir.speedFactor));

                if (drawMetric && j <= drawMetric->j) {
                    Visualizer::addSphere(*meSnd.me(), rgba(0, 0.8, 0, al));
                    Visualizer::addSphere(meSnd.ball, rgba(0.4, 0.1, 0.4, al));
                    Visualizer::addSphere(meSnd.opp[0], rgba(1, 0, 0, al * 0.5));
                    Visualizer::addSphere(meSnd.opp[1], rgba(1, 0, 0, al * 0.5));
                }

                if (wildcard == 0 && meSnd.me()->getDistanceTo2(meSnd.ball) < SQR(BALL_RADIUS + ROBOT_MAX_RADIUS + forKMaxDist)) {
                    OP_START(K);

                    Sandbox meJumpSnd = meSnd;
                    auto jmpAction = mvAction;
                    jmpAction.jump();
                    jmpAction.nitro();

                    AAction firstKAction;

                    meJumpSnd.me()->action = jmpAction;

                    const int jumpMaxTicks = 21 + (meJumpSnd.me()->nitroAmount > EPS) * (!isAttacker);

                    int k;
                    int rcK = -1;
                    bool qwe = false;

                    double minDist2ToBall = meSnd.me()->getDistanceTo2(meSnd.ball);
                    bool hasShot = false;
                    for (k = 0; k <= jumpMaxTicks && !hasShot; k++) {
//                        if (k == 0) {
//                            if (wildcard > 0) {
//                                wildCardCounts++;
//                                qwe = true;
//                            }
//                        }

//                        if (k == jumpMaxTicks / 2 && meSnd.me()->getDistanceTo2(meSnd.ball) >= SQR(BALL_RADIUS + ROBOT_MAX_RADIUS + 9)) {
//                            //qwe = true;
//                            break;
//                        }

                        if (dir.toBallAfterJump && meJumpSnd.me()->nitroAmount > EPS) {
                            meJumpSnd.me()->action.vel(Helper::maxVelocityTo(*meJumpSnd.me(), meJumpSnd.ball));
                        }
                        if (k == 0) {
                            firstKAction = meJumpSnd.me()->action;
                        }
                        meJumpSnd.doTick(1);
                        updMin(minDist2ToBall, meJumpSnd.me()->getDistanceTo2(meJumpSnd.ball));

                        if (skipRobotsCollisions(meJumpSnd)) {
                            break;
                        }

                        if (drawMetric && j == drawMetric->j && k <= drawMetric->k) {
                            Visualizer::addSphere(*meJumpSnd.me(), rgba(0.7, 0.8, 0, al));
                            Visualizer::addSphere(meJumpSnd.ball, rgba(0.4, 0.1, 0.4, al));
                            //Visualizer::addSphere(meJumpSnd.opp[0], rgba(1, 0, 0, al * 0.5));
                            //Visualizer::addSphere(meJumpSnd.opp[1], rgba(1, 0, 0, al * 0.5));
                        }

                        bool hasPositiveShot = false;
                        if (meJumpSnd.robotBallCollisions & M_COLL_MASK(myId)) {
                            hasShot = true;
                            rcK = k;
                        }
                        if (meJumpSnd.robotBallPositiveCollisions & M_COLL_MASK(myId)) {
                            hasPositiveShot = true;
                        }

                        if (rcK == -1 && (meJumpSnd.robotsCollisions & Sandbox::myOppMask[myId])) {
                            rcK = k;
                        }

                        double minCounterDist2 = 10000;
                        Point md1, md2;

                        int thr = meJumpSnd.me()->nitroAmount > EPS ? -30 : -30;

                        if (hasShot && (hasPositiveShot || !isAttacker && meJumpSnd.ball.z < thr)
                            || !hasShot && k == jumpMaxTicks && rcK != -1 && meJumpSnd.me()->z > -10) {
                            OP_START(KW);

                            double passMinDist2 = 10000;

                            meJumpSnd = meSnd;
                            ARobot* forward = &meJumpSnd.my[Helper::whichMaxZ(meJumpSnd.my)];
                            forward->action = AAction(Helper::maxVelocityTo(*forward, oppGoal));
                            meJumpSnd.me()->action = jmpAction;
                            double minZ = meJumpSnd.ball.z;

                            for (int q = 0; q <= rcK; q++) {
                                if (dir.toBallAfterJump && meJumpSnd.me()->nitroAmount > EPS) {
                                    meJumpSnd.me()->action.vel(Helper::maxVelocityTo(*meJumpSnd.me(), meJumpSnd.ball));
                                }
                                meJumpSnd.doTick(q == rcK ? MICROTICKS_PER_TICK : 1);
                                updMin(minZ, meJumpSnd.ball.z);
                            }
                            // TODO: что если коллизии после пересчёта не будет?

                            forward->action = AAction(Helper::maxVelocityTo(*forward, oppGoal));

                            int shotTick = meJumpSnd.tick;

                            const int ballSimMaxTicks = std::max(std::min(int((ARENA_Z - meJumpSnd.me()->z) / (ARENA_Z - (-ARENA_Z * 0.5)) * 85), 85), 40);
                            meJumpSnd.oppGkStrat = true;
                            meJumpSnd.oppCounterStrat = 2;

//                            meJumpSnd.oppCounterStrat = 3;
//                            ARobot* opp = meJumpSnd.opp[0].z < meJumpSnd.opp[1].z ? &meJumpSnd.opp[0] : &meJumpSnd.opp[1];
//                            auto ss = meJumpSnd;
//                            ss.clearRobots();
//                            Point ptar;
//                            auto pp = moveToBallCounter(ss, *opp, ballSimMaxTicks, ptar);
//                            if (pp) {
//                                meJumpSnd.deduceOppSimple = false;
//                                opp->action = pp.value();
//                                if (drawMetric && j == drawMetric->j && k <= drawMetric->k) {
//                                    Visualizer::addSphere(ptar, 0.5, rgba(0, 0, 0));
//                                }
//                            }

                            int positiveTicks = 0;
                            double positiveChange = 0;
                            std::optional<ARobot> oppGk;
                            for (auto& x : meJumpSnd.opp) {
                                if (x.z > ARENA_Z - 8) {
                                    if (!oppGk || oppGk.value().z < x.z) {
                                        oppGk = x;
                                    }
                                }
                            }
                            bool isFar = meJumpSnd.me()->z < -10;
                            bool noFarGoal = false;
                            bool hasOppTouch = false;
                            int touchFloorCount = 0;

                            for (int w = 0; w <= ballSimMaxTicks && meJumpSnd.hasGoal == 0; w++) {
                                auto prevBall = meJumpSnd.ball;
                                meJumpSnd.doTick(1);
                                hasOppTouch |= Sandbox::oppMask & meJumpSnd.robotBallCollisions;

                                updMin(minZ, meJumpSnd.ball.z);
                                if (meJumpSnd.ball.z > prevBall.z) {
                                    positiveChange += meJumpSnd.ball.z - prevBall.z;
                                    positiveTicks++;
                                }
                                if (prevBall.y < 5 && prevBall.velocity.y * meJumpSnd.ball.velocity.y < 0) {
                                    touchFloorCount++;
                                }

                                if (w > 30) {
                                    auto passTar1 = forward->_y(0) + (forward->id == myId ? Helper::goalDir(*forward, 7) + Point(0, 4, 0) : Helper::goalDir(*forward, 9) + Point(0, 10, 0));
                                    updMin(passMinDist2, meJumpSnd.ball.getDistanceTo2(passTar1));
                                }


                                if (counterPenalty(meJumpSnd)) {
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
                                if (w > 10 && meJumpSnd.ball.y < BALL_RADIUS * 1.1 && oppGk) {
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
                                Visualizer::addChain(md1, md2);
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

                            leafsCount++;

                            if (hasShot && meJumpSnd.hasGoal >= 0 || !hasShot && meJumpSnd.hasGoal > 0) {
                                if (!(ballFloorTouch && isInGoal)) {

                                    cand = {env.tick, j, k, dir,
                                            hasGoal, hasShot, positiveChange, positiveTicks, penalty,
                                            shotTick - env.tick, minZ, hasOppTouch, meJumpSnd.ball, oppGk,
                                            sqrt(passMinDist2), touchFloorCount, qwe};

                                    if (sel.j == -1 || sel < cand) {
                                        sel = cand;
                                        firstAction = j == 0 ? firstKAction : firstJAction;
                                    }
                                }
                            }

                            OP_END(KW);

                            break;
                        }
                    }
                    if (wildcard > 0) {
                        wildcard--;
                    }

                    if (hasShot) {
                        wildcard = 0;
                    } else if (k >= jumpMaxTicks) { // TODO: check robots collisions
                        for (int s = 10; s >= 2; s--) {
                            if (minDist2ToBall > SQR(s + ROBOT_MAX_RADIUS + BALL_RADIUS)) {
                                wildcard = s == 2 ? 1 : 2 * (s - 2);
                                break;
                            }
                        }
                    }

                    OP_END(K);
                } else if (wildcard > 0) {
                    wildcard--;
                }

                meSnd.me()->action = mvAction;
                if (j == 0) {
                    firstJAction = mvAction;
                }
                meSnd.doTick(1);
                if (skipRobotsCollisions(meSnd)) {
                    break;
                }

                if (meSnd.me()->z < -ARENA_Z - ARENA_GOAL_DEPTH + ROBOT_MAX_RADIUS + 0.01) {
                    isInGoal = true;
                }
            }

            auto me = env.me();
            if (me->isDetouched() && me->nitroAmount < EPS) {
                break;
            }
        }

        if (sel.j >= 0) {
            resMetric = sel;
            resAction = firstAction;
            bool hasTeammateShot = false;
            for (auto&x : env.my) {
                if (x.id != myId && prevMetric.count(x.id) && prevMetric[x.id].tick >= env.tick - 1) { // TODO: prevTick may be the same
                    auto m = prevMetric[x.id];
                    if (std::abs(sel.timeToShot - m.timeToShot) <= 10) {
                        hasTeammateShot |= sel < m;
                    } else {
                        hasTeammateShot |= sel.timeToShot > m.timeToShot;
                    }
                }
            }
            auto ret = !hasTeammateShot;
//            if (sel._debugFalsePositive) {
//                std::cout << "AAAAAAA\n";
//            }

            if (drawMetric == nullptr) {
                Logger::instance()->corrXYZStat[0][sel.dir.correction.fx]++;
                Logger::instance()->corrXYZStat[1][sel.dir.correction.fy]++;
                Logger::instance()->corrXYZStat[2][sel.dir.correction.fz]++;
                LOG("Leafs: " + std::to_string(leafsCount));
#ifdef DEBUG
                AAction t1;
                Metric t2;
                tryShotOutOrGoal(isAttacker, t1, t2, &sel, ret ? 1 : 0.3);
#endif

                prevMetric[myId] = sel;
            }

            return ret;
        }
        return false;
    }



    bool tryTakeNitro(int isAttacker, const Point& afterNitroTarget, AAction& resAction, ANitroPack& resNitroPack) {
        OP_START(TAKE_NITRO);
        auto ret = _tryTakeNitro(isAttacker, afterNitroTarget, resAction, resNitroPack);
        OP_END(TAKE_NITRO);
        return ret;
    }

    bool _tryTakeNitro(int isAttacker, const Point& afterNitroTarget, AAction& resAction, ANitroPack& resNitroPack) {
        if (!GameInfo::isNitro) {
            return false;
        }

        if (env.me()->nitroAmount >= MAX_NITRO_AMOUNT) {
            return false;
        }

        AAction selAction;
        int selI = -1, selJ = -1;
        int minTm = INT_MAX;

        for (auto& pack : env.nitroPacks) {
            if (!isAttacker && pack.z > 0) {
                continue;
            }
            if (isAttacker && env.me()->z * pack.z <= 0) {
                continue;
            }

            double minDistToTarget = 10000;
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
                    if (isAttacker && backSnd.tick - env.tick > 13) { // берем только очень близкие
                        break;
                    }

                    if (!intersected) {
                        auto g = backSnd.getIntersectedNitroPack(*backSnd.me());
                        if (g) {
                            if (backSnd.nitroPacksCollected & M_COLL_MASK2(me->id, g->id)) {
                                gotcha = true;
                                gotchaJ = j;
                                gotchaTm = backSnd.tick - env.tick;
                            }
                            intersected = true;
                        }
                    }

                    backSnd.me()->action = AAction(Helper::maxVelocityTo(*backSnd.me(), afterNitroTarget));
                    if (j == 0) {
                        firstJAction = backSnd.me()->action;
                    }
                    backSnd.doTick(1);
                }
                if (intersected && backSnd.me()->getDistanceTo(afterNitroTarget) < minDistToTarget) {
                    minDistToTarget = backSnd.me()->getDistanceTo(afterNitroTarget);
                    minDistGotcha = gotcha;
                    selAction = i > 0 ? firstIAction : firstJAction;
                    selI = i;
                    selJ = gotchaJ;
                    selTm = gotchaTm;
                }

                me->action = AAction(Helper::maxVelocityTo(*me, pack));
                if (i == 0) {
                    firstIAction = me->action;
                }
                meSnd.doTick(1);

                if (meSnd.nitroPacksCollected) {
                    break;//TODO
                }
            }

            if (minDistGotcha) {
                if (selTm < minTm) {
                    resAction = selAction;
                    resNitroPack = pack;
                    minTm = selTm;
                }
            }
        }
        return minTm < INT_MAX;
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
        if (e.me()->z < -ARENA_DEPTH / 2 + 2 && std::abs(e.me()->x) < ARENA_GOAL_WIDTH/2 - 1 && tt > 0)// чтобы не сльно быстро шататься
            speed = delta.length() / tt;
        auto target_velocity = delta.take(speed);
        sAct.targetVelocity = target_velocity;

        return sAct;
    }

    int selectGk() {
        auto snd = env;
        snd.clearOppRobots();
        std::vector<std::tuple<int, double, int>> gotcha(7, {INT_MAX, 100.0, INT_MAX});
        for (auto& x : snd.my) {
            std::get<1>(gotcha[x.id]) = x.z;
            std::get<2>(gotcha[x.id]) = x.id;
        }
        //if (GameInfo::isNitro) {
        if (false) {
            for (int i = 0; i < 200; i++) {
                for (auto &x : snd.my) {
                    x.action = AAction(Helper::maxVelocityTo(x, myGoal));
                    if (x._y(0).getDistanceTo2(myGoal) < SQR(1)) {
                        std::get<0>(gotcha[x.id]) = std::min(std::get<0>(gotcha[x.id]), i);
                    }
                }
                snd.doTick(1);
            }
        }

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


    std::optional<AAction> moveToBallCounter(Sandbox& snd, const ARobot& ct, int ticks, Point& ptar) {
        double ballHeight = BALL_RADIUS + 6;
        const int dt = 5;

        for (auto i = 0; i <= ticks; i += dt) {
            snd.doTicksBallFast(dt);
            if (snd.ball.y > ballHeight)
                continue;
            auto t = 1.0 * i / TICKS_PER_SECOND;

            auto tar = snd.ball + (snd.ball - myGoal).take(BALL_RADIUS * 3.0);
            tar.y = 0;
            Point delta_pos = tar - ct;
            delta_pos.y = 0;
            auto need_speed = delta_pos.length() / t;

            if (need_speed <= ROBOT_MAX_GROUND_SPEED) {
                ptar = tar;
                auto target_velocity = delta_pos.take(need_speed);
                return AAction(target_velocity);
            }
        }
        return {};
    }

    std::tuple<std::optional<ARobot>, std::optional<ARobot>> evalToBall() {
        Sandbox ballSnd = env;
        ballSnd.stopOnGoal = GameInfo::isNitro;
        std::vector<int> minTime(7, INT_MAX);
        // учитываем результаты tryShotOutOrGoal
        for (const auto& item : prevMetric) {
            auto& id = item.first;
            auto& metric = item.second;
            if (metric.tick >= env.tick - 1) {
                minTime[id] = metric.timeToShot;
            }
        }

        double ballHeight = BALL_RADIUS + ROBOT_MAX_RADIUS*5;
//        if (GameInfo::isNitro && ballSnd.me()->z + 2 < ballSnd.ball.z) {
//            ballHeight += 4;
//            if (ballSnd.me()->nitroAmount > EPS) {
//                ballHeight += 2;
//            }
//        }

        for (int i = 0; i <= 100; i++) {
            ballSnd.doTick(1);
            if (ballSnd.ball.y > ballHeight)
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

                    if (r->getDistanceTo(tar) < (r->isTeammate ? 3 : 6 + GameInfo::isNitro * 0)) {
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

    bool hasGkFloorTouch() {
        // коснется ли мяч пола перед тем, как оттолкнуться от передней стенки или пересечь ворота
        // костыль от выкрутасов вратаря
        for (int i = 0; i < 80; i++) {
            const auto& pos = Sandbox::_ballsCache[i];
            if (pos.y < BALL_RADIUS + 1) {
                return true;
            }
            if (pos.z < -ARENA_Z + BALL_RADIUS + 0.3) {
                break;
            }
        }
        return false;
    }

    Point oppGoal = Point(0, 0, ARENA_Z + ARENA_GOAL_DEPTH / 2);
    Point myGoal = Point(0, 0, -(ARENA_Z + ARENA_GOAL_DEPTH / 2));

    struct TargetItem {
        AAction action;
        Point target;
    };

    std::pair<std::optional<TargetItem>, std::optional<TargetItem>> moveToBallUsual(int isAttacker) {
        Sandbox snd = env;
        snd.stopOnGoal = false;
        auto me = *snd.me();
        double ballHeight = BALL_RADIUS + 3;
//        if (GameInfo::isNitro && me.z + 2 < snd.ball.z) {
//            ballHeight += 4;
//            if (me.nitroAmount > EPS) {
//                ballHeight += 2;
//            }
//        }

        snd.clearMe();

        std::optional<TargetItem> firstAction, secondAction;

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
                firstAction = {act, me + delta_pos};
                break;
            }
            secondAction = {act, me + delta_pos};
        }
        return {firstAction, secondAction};
    }

    bool alarm;

    void act(AAction& action, bool isFirst) {
        auto& ball = env.ball;
        auto& me = *env.me();

        if (env.tick == lastTick) {
            for (auto tm : env.teammates(me.id))
                tm->action = prevEnv.robot(tm->id)->action;
        } else if (env.roundTick > 0) {
            for (auto& opp : env.opp) {
                if (opp.nitroAmount < prevEnv.robot(opp.id)->nitroAmount) {
                    GameInfo::usedNitro[opp.id] = true;
                    Visualizer::useNitro(opp);
                }
            }

            prevEnv.doTick();
            checkEvalState();
        }

        const int alarmTicks = 45;

        if (isFirst) {
#ifdef LOCAL
            if (env.tick % 500 == 0) {
                //std::cout << env.tick << " wildcards " << wildCardCounts << std::endl;
            }
#endif

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

        bool hasPrevShot = prevMetric.count(me.id) && prevMetric[me.id].tick >= env.tick - 1;

        std::optional<ARobot> firstToBall, firstToBallMy;
        std::tie(firstToBall, firstToBallMy) = evalToBall();
        if (firstToBall) {
            Visualizer::markFirstToBall(firstToBall.value());
        }

        int gkId = selectGk();
        int isAttacker = gkId != me.id;
        int secondAttackerId = -1;
        for (auto& x : env.my) {
            if (x.id != me.id && x.id != gkId) {
                secondAttackerId = x.id;
            }
        }

        Metric metric;
        std::optional<TargetItem> firstAction, secondAction;
        ANitroPack nitroPackSelected;

//        if (is_attacker) return;

        if (!isAttacker) {
            Visualizer::addSphere(me, me.radius * 1.1, rgba(1, 0.7, 0));
        }


        if (isAttacker && env.roundTick <= 35) {
            action = AAction(Helper::maxVelocityTo(me, env.ball + Point(0, 0, -3.2)));
            return;
        }
        if (!isAttacker && env.roundTick <= 30) {
            action = AAction().vel(Helper::maxVelocityTo(me, Point(0, 0, -ARENA_Z)));
            return;
        }
        if (isAttacker && tryShotOutOrGoal(isAttacker, action, metric)) {
            std::string msg = metric.toString();
            Visualizer::addText(msg);
            return;
        }

        if (isAttacker) {
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
                std::tie(firstAction, secondAction) = moveToBallUsual(isAttacker);
                auto firstOrSecond = firstAction;
                if (!firstOrSecond) {
                    firstOrSecond = secondAction;
                }

                if (firstOrSecond) {
                    auto target = firstOrSecond.value().target;

                    if (tryTakeNitro(isAttacker, target, action, nitroPackSelected)) {
                        Visualizer::addTargetLines(me, nitroPackSelected);
                    } else {
                        action = firstOrSecond.value().action;
                    }
                    Visualizer::addTargetLines(me, target);
                } else {
                    // TODO: когда это достижимо?
                    if (ball.z > -6) {
                        Point target(0, 0, 14);
                        action.vel(Helper::maxVelocityTo(me, target));
                        Visualizer::addTargetLines(me, target);
                    } else {
                        isAttacker = 0; // TODO
                    }
                }
            } else {
                Point target(0, 0, 10);
                if (GameInfo::isFinal) {
                    Point t1(-4, 0, 1), t2(4, 0, 12);
                    if (me.getDistanceTo(t1) + env.robot(secondAttackerId)->getDistanceTo(t2) <
                        me.getDistanceTo(t2) + env.robot(secondAttackerId)->getDistanceTo(t1))
                    {
                        target = t1;
                    } else {
                        target = t2;
                    }
                }
                Visualizer::addTargetLines(me, target);
                action.targetVelocity = Helper::maxVelocityTo(me, target);
            }
        }


        if (!isAttacker) {
            Visualizer::addSphere(me, me.radius * 1.1, rgba(1, 0.7, 0));

            bool condToShot;
            condToShot = firstToBallMy && firstToBallMy.value().id == me.id
                    || ball.velocity.z < 0 && ball.z < -ARENA_Z * (GameInfo::isNitro ? 0.5 : 0.5)
                    || me.getDistanceTo(ball) < BALL_RADIUS + ROBOT_RADIUS + 5
                    || alarm
                    || hasPrevShot;

            if (condToShot && tryShotOutOrGoal(isAttacker, action, metric)) {

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
                        std::tie(firstAction, secondAction) = moveToBallUsual(isAttacker);
                    }
                    if (firstAction) {
                        action = firstAction.value().action;
                        Visualizer::addTargetLines(me, firstAction.value().target);
                    } else if (ball.z > -ARENA_Z + 10 && Sandbox::_ballsCache[20].z > 15 && tryTakeNitro(isAttacker, Point(0, 0, -ARENA_Z), action, nitroPackSelected)) {
                        Visualizer::addTargetLines(me, nitroPackSelected);
                    } else {
                        action = goToGoalCenterStrat(env);
                    }
                }
            }
        }

    }
};


#endif //CODEBALL_STRAT_H
