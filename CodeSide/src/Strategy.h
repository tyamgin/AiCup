#ifndef CODESIDE_STRATEGY_H
#define CODESIDE_STRATEGY_H

#include "constants.h"
#include "logger.h"
#include "sandbox.h"
#include "draw.h"
#include "findpath.h"
#include "testing.h"

#include <algorithm>
#include <cassert>

/**
 * TODO:
 * - пошел за аптечкой напролом через противника, не прошел, проиграл
 * - собрал все аптечки, не осталось аптечки соперницу, выиграл
 * - собрал аптечку с минимумом урона, проиграл, т.к. у соперника аптечка осталась
 * - не учитывается падение соперника при прицеливании
 * - пп от соперника и отклонения с учетом этого
 * - урон от ракеты суммируется => вплотную против неё лучше не становиться
 * - пп от стен, если соперник с базукой
 * - пп в пользу стояния на лестнице (легко уклоняться)
 * - стрелдять можно посреди тика, даже если unit.canShot() изначально false
 * - в findpath поощрять за бонусы
 * - предсказывать dx противника
 * - к базучнику близко не подходить
 */

class Strategy {
    TSandbox prevEnv, prevEnv2, env;
    std::unordered_map<int, TPathFinder> pathFinders;
    std::unordered_map<int, TLootBox*> _selLootbox;

    std::unordered_map<ELootType, int> weaponPriority = {
            {ELootType::PISTOL, 0},
            {ELootType::ROCKET_LAUNCHER, 1},
            {ELootType::ASSAULT_RIFLE, 2},
            {ELootType::NONE, 3},
    };

    std::optional<TActionsVec> findPathWrapper(const TUnit& unit, const TPoint& target) {
        auto& pathFinder = pathFinders[unit.id];
        TStatesVec pathPoints;
        TActionsVec actions;
        if (pathFinder.findPath(target, pathPoints, actions) && actions.size() > 0) {
            bool blocked = false;
            for (auto& other : env.units) {
                if (!other.isMy() || other.id == unit.id) {
                    continue;
                }
                for (int i = 0; i < 5 && i < (int) pathPoints.size(); i++) {
                    auto pt = pathPoints[i].getPoint();
                    if (other.intersectsWith(pt.x - UNIT_HALF_WIDTH, pt.y, pt.x + UNIT_HALF_WIDTH, pt.y + UNIT_HEIGHT)) {
                        blocked = true;
                        break;
                    }
                }
            }
            if (blocked) {
                actions[0].jump = true;
                TDrawUtil::debug->draw(CustomData::PlacedText("JUMP",
                                                              Vec2Float{float(unit.x1), float(unit.y2 + 0.2)},
                                                              TextAlignment::LEFT,
                                                              25,
                                                              ColorFloat(0, 1, 0, 0.5)));
            }
            TDrawUtil().drawPath(statesToPoints(pathPoints));
            return actions;
        }
        return {};
    }

    std::optional<TActionsVec> _strategyLoot(const TUnit& unit, std::set<ELootType> lootTypes) {
        auto& pathFinder = pathFinders[unit.id];
        std::map<std::pair<int, int>, double> lbPathPenalty;
        pathFinder.traverseReachable(unit, [&](double dist, const TUnit& unit, const TState& state) {
            TLootBox* lb;
            if ((lb = env.findLootBox(unit)) != nullptr) {
                if (lootTypes.count(lb->type)) {
                    if (!lbPathPenalty.count({lb->getRow(), lb->getCol()})) {
                        TStatesVec pts;
                        TActionsVec acts;
                        double penalty = 0;
                        if (pathFinder.findPath(unit.position(), pts, acts) && !acts.empty()) {
                            for (const auto& s : pts) {
                                penalty += pathFinder.penalty[s.x][s.y];
                            }
                        }
                        TDrawUtil::debug->draw(CustomData::PlacedText(std::to_string(penalty + dist),
                                                                      Vec2Float{float(lb->x1), float(lb->y2 + 1)},
                                                                      TextAlignment::LEFT,
                                                                      30,
                                                                      ColorFloat(0, 1, 0, 0.75)));
                        lbPathPenalty[{lb->getRow(), lb->getCol()}] = penalty;
                    }
                }
            }
        });


        double minDist = INF;
        TLootBox* selectedLb = nullptr;
        TPoint selectedPos;
        TState selectedState;

        pathFinder.traverseReachable(unit, [&](double dist, const TUnit& unit, const TState& state) {
            TLootBox* lb;
            if ((lb = env.findLootBox(unit)) != nullptr) {
                if (lootTypes.count(lb->type)) {
                    dist += lbPathPenalty[{lb->getRow(), lb->getCol()}];
                    if (dist < minDist) {
                        minDist = dist;
                        selectedLb = lb;
                        selectedPos = unit.position();
                        selectedState = state;
                    }
                }
            }
        });
        if (selectedLb != nullptr) {
            if (auto actions = findPathWrapper(unit, selectedPos)) {
                return actions;
            }
        }
        return {};
    }

    std::optional<TActionsVec> _strategy(const TUnit& unit) {
        auto& pathFinder = pathFinders[unit.id];

#if M_DRAW_REACHABILITY_X > 0 && M_DRAW_REACHABILITY_Y > 0
        pathFinder.traverseReachable(unit, [&](double dist, const TUnit& unit, const TState& state) {
            if (state.x % M_DRAW_REACHABILITY_X == 0 && state.y % M_DRAW_REACHABILITY_Y == 0) {
                auto p = state.getPoint();
                TDrawUtil::debug->draw(CustomData::Rect({float(p.x), float(p.y)}, {0.05, 0.05}, ColorFloat(0, 1, 0, 1)));
            }
        });
#endif

        if (unit.noWeapon() && _selLootbox.count(unit.id)) {
            TPoint pos = _selLootbox[unit.id]->position();

            if (auto actions = findPathWrapper(unit, pos)) {
                return actions;
            }
        }

        if (unit.health <= 80) {
            auto maybeAct = _strategyLoot(unit, {ELootType::HEALTH_PACK});
            if (maybeAct) {
                return maybeAct;
            }
        }

        TUnit* target = nullptr;
        for (auto& u : env.units) {
            if (!u.isMy() && (target == nullptr || u.getDistanceTo(unit) < target->getDistanceTo(unit))) {
                target = &u;
            }
        }
        if (target == nullptr) { // it's impossible
            return{};
        }

        if (_needTakeRocketLauncher(unit) && unit.weapon.type != ELootType::ROCKET_LAUNCHER) {
            auto maybeAct = _strategyLoot(unit, {ELootType::ROCKET_LAUNCHER});
            if (maybeAct) {
                return maybeAct;
            }
        }

        std::tuple<int, double, TPoint> bestChangeWeapon(weaponPriority[unit.weapon.type], 0.0, TPoint());
        for (const auto& lb : env.lootBoxes) {
            if (!lb.isWeapon()) {
                continue;
            }
            bool skip = false;
            for (auto& [unitId, slb] : _selLootbox) {
                if (std::make_pair(slb->getRow(), slb->getCol()) == std::make_pair(lb.getRow(), lb.getCol())) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                continue;
            }

            double oppMinDist = INF;
            for (auto& opp : env.units) {
                if (!opp.isMy()) {
                    oppMinDist = std::min(oppMinDist, opp.position().getDistanceTo(lb.position()));
                }
            }
            double myDist = unit.position().getDistanceTo(lb.position());
            if (myDist < oppMinDist) {
                bestChangeWeapon = std::min(bestChangeWeapon, {weaponPriority[lb.type], myDist, lb.position()});
            }
        }
        if (std::get<0>(bestChangeWeapon) < weaponPriority[unit.weapon.type]) {
            if (auto actions = findPathWrapper(unit, std::get<2>(bestChangeWeapon))) {
                return actions;
            }
        }


        if (unit.weapon.fireTimer > 0.5) {
            double rangeMin = 5, rangeMax = 7;
//            if (unit.weapon.fireTimer > 0.5) {
                rangeMin = 10;
                rangeMax = 14;
//            }


            double minDist = INF;
            TPoint selectedPoint;
            pathFinder.traverseReachable(unit, [&](double dist, const TUnit& unit, const TState& state) {
                auto dist2ToTarget = unit.center().getDistanceTo2(target->center());
                if (dist < minDist && isIn(SQR(rangeMin), SQR(rangeMax), dist2ToTarget)) {
                    minDist = dist;
                    selectedPoint = unit.position();
                }
            });
            if (minDist < 1e-9) {
                return TActionsVec(1, TAction());
            }
            if (minDist < INF) {
                if (auto actions = findPathWrapper(unit, selectedPoint)) {
                    return actions;
                }
            }
        }


        if (auto actions = findPathWrapper(unit, target->position())) {
            return actions;
        }

        return {};
    }

    bool _needTakeRocketLauncher(const TUnit& unit) {
        if (env.myCount == 1) {
            for (const auto &opp : env.units) {
                if (!opp.isMy()) {
                    if (opp.health <= 80 && unit.health <= opp.health) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

public:
    std::unordered_map<int, TAction> getActions(const Game& game) {
        TDrawUtil().drawGrid();
        env = TSandbox(game);
        TDrawUtil().drawMinesRadius(env);
        TDrawUtil().drawUnits(env);
        if (env.currentTick == 0) {
            TPathFinder::initMap();
        }
        for (auto &unit : env.units) {
            if (unit.isMy()) {
                pathFinders[unit.id] = TPathFinder(&env, unit);
            }
        }

        if (env.currentTick == 667) {
            env.currentTick += 0;
        }
        if (env.currentTick > 1) {
            prevEnv2 = prevEnv;
            prevEnv.doTick();
            _compareState(prevEnv, env, prevEnv2);
        }

        std::unordered_map<int, TAction> result;

//        for (auto& unit : env.units) {
//            if (unit.isMy()) {
//                result[unit.id] = _mineGunTestStrategy(unit, env);
//            }
//        }

        _initialWeaponDistribute();

        for (const auto& unit : env.units) {
            if (unit.isMy()) {
                result[unit.id] = getAction(unit);
                // TODO: может заполнять, чтобы следующий учитывал ход?
            }
        }
        for (auto& unit : env.units) {
            if (unit.isMy()) {
                unit.action = result[unit.id];
            }
        }
        prevEnv = env;
        return result;
    }

    TAction getAction(const TUnit& unit) {
        //auto action = _ladderLeftStrategy(unit, env, debug);


        auto maybeActions = _strategy(unit);
        auto actions = maybeActions ? maybeActions.value() : TActionsVec();

        TSandbox notDodgeEnv = env;
        //notDodgeEnv.oppShotSimpleStrategy = true;
        for (int i = 0; i < 40; i++) {
            notDodgeEnv.getUnit(unit.id)->action = i < actions.size() ? actions[i] : TAction();
            notDodgeEnv.doTick();
        }
        auto startOppScore = env.score[1];
        auto scorer = [&](TSandbox& env) {
            return env.getUnit(unit.id)->health - (env.score[1] - startOppScore) / 2;
        };
        auto bestScore = scorer(notDodgeEnv);
        std::optional<std::vector<TPoint>> bestPath;

        OP_START(DODGE);
        for (int dirX = -1; dirX <= 1; dirX++) {
            for (int dirY = -1; dirY <= 1; dirY += 1) {
                TSandbox dodgeEnv = env;
                //dodgeEnv.oppShotSimpleStrategy = true;
                std::vector<TPoint> path;
                TAction act;
                if (dirY > 0)
                    act.jump = true;
                else if (dirY < 0)
                    act.jumpDown = true;
                act.velocity = dirX * UNIT_MAX_HORIZONTAL_SPEED;
                const int simulateTicks = 27;
                for (int i = 0; i < simulateTicks; i++) {
                    dodgeEnv.getUnit(unit.id)->action = act;
                    dodgeEnv.doTick();
                    path.push_back(dodgeEnv.getUnit(unit.id)->position());
                }
                if (scorer(dodgeEnv) > bestScore || (scorer(dodgeEnv) == bestScore && dodgeEnv.getUnit(unit.id)->health > env.getUnit(unit.id)->health)) {
                    bestScore = scorer(dodgeEnv);
                    bestPath = path;
                    actions = TActionsVec(simulateTicks, act);
                }
            }
        }
        OP_END(DODGE);
        if (bestPath) {
            TDrawUtil().drawPath(bestPath.value(), ColorFloat(1, 0, 0, 1));
        }

        TAction action = actions.size() > 0 ? actions[0] : TAction();

        auto maybeShot = shotStrategy(unit, actions);
        if (maybeShot) {
            action.shoot = maybeShot.value().shoot;
            action.aim = maybeShot.value().aim;
        }

        auto reloadSwapAction = _reloadSwap(unit);
        if (!action.shoot && reloadSwapAction.reload) {
            action.reload = true;
        }
        if (!action.shoot && reloadSwapAction.swapWeapon) {
            action.swapWeapon = true;
        }
        TDrawUtil().drawAim(unit, action);

        return action;
    }

    std::optional<TAction> shotStrategy(const TUnit& unit, const TActionsVec& actions) {
        if (unit.noWeapon()) {
            return {};
        }

        TUnit* target = nullptr;
        if (unit.weapon.hasLastAngle()) {
            for (auto& u : env.units) {
                if (u.isMy() || unit.getDistanceTo(u) > 4) {
                    continue;
                }
                if (target == nullptr || (std::abs(TPoint::getAngleBetween(TPoint::byAngle(unit.weapon.lastAngle), u.center() - unit.center())) <
                                          std::abs(TPoint::getAngleBetween(TPoint::byAngle(unit.weapon.lastAngle), target->center() - unit.center())))) {
                    target = &u;
                }
            }
        }
        for (auto& u : env.units) {
            if (u.isMy()) {
                continue;
            }
            if (target == nullptr || u.getDistanceTo(unit) < target->getDistanceTo(unit)) {
                target = &u;
            }
        }
        if (target == nullptr) {
            return {};
        }


        OP_START(SHOT_STRAT);

        auto oppDodgeStrategy = [](TSandbox& afterShotEnv, int unitId, int simulateTicks) {
            int maxHealth = 0;
            int initialHealth = afterShotEnv.getUnit(unitId)->health;
            TAction bestAction;
            for (int dirX = -1; dirX <= 1; dirX++) {
                for (int dirY = -1; dirY <= 1; dirY += 1) {
                    TSandbox dodgeEnv = afterShotEnv;
                    auto opp = dodgeEnv.getUnit(unitId);
                    TAction act;
                    if (dirY > 0) {
                        act.jump = true;
                    } else if (dirY < 0) {
                        act.jumpDown = true;
                    }
                    act.velocity = dirX * UNIT_MAX_HORIZONTAL_SPEED;
                    opp->action = act;
                    for (int i = 0; i < simulateTicks && opp->health > maxHealth; i++) {
                        dodgeEnv.doTick(5);
                    }
                    if (opp->health > maxHealth) {
                        maxHealth = opp->health;
                        bestAction = act;
                        if (opp->health == initialHealth) {
                            return bestAction;
                        }
                    }
                }
            }
            return bestAction;
        };

        TAction action = actions.empty() ? TAction() : actions[0];
        action.aim = calcAim(unit, *target, actions);
        if (unit.canShot()) {
            const int itersCount = 6;
            int successCount = 0;
            int friendlyFails = 0;
            for (int it = -itersCount; it <= itersCount; it++) {
                auto testEnv = env;
                auto testNothingEnv = env;
                testEnv.getUnit(unit.id)->action = action;
                testEnv.getUnit(unit.id)->action.shoot = true;
                testEnv.shotSpreadToss = 1.0 * it / itersCount;
                const int simulateTicks = 15;
                for (int i = 0; i < simulateTicks; i++) {
                    testEnv.doTick();
                    testNothingEnv.doTick();
                    testEnv.getUnit(unit.id)->action.shoot = false;
                    if (i == 0) {
                        for (auto& u : testEnv.units) {
                            if (u.id != unit.id || unit.weapon.type == ELootType::ROCKET_LAUNCHER) {
                                u.action = oppDodgeStrategy(testEnv, u.id, simulateTicks - 1);
                            }
                        }
                    }
                }
                auto score = std::max(0, testEnv.score[0] - testNothingEnv.score[0])
                        - std::max(0, testEnv.friendlyLoss[0] - testNothingEnv.friendlyLoss[0])*3/2;
                if (score > 0 || (testEnv.score[0] - testNothingEnv.score[0] > KILL_SCORE && testEnv.score[0] > testEnv.score[1])) {
                    successCount++;
                } else if (score < 0) {
                    friendlyFails++;
                }
            }
            double probability = successCount / (itersCount*2 + 1.0);
            double failProbability = friendlyFails / (itersCount*2 + 1.0);
#ifdef DEBUG
            TDrawUtil::debug->draw(CustomData::PlacedText(std::to_string(probability),
                                                           Vec2Float{float(unit.x1), float(unit.y2 + 2)},
                                                           TextAlignment::LEFT,
                                                           30,
                                                           ColorFloat(0, 0, 1, 1)));
            if (failProbability > 0.01) {
                TDrawUtil::debug->draw(CustomData::PlacedText(std::to_string(failProbability),
                                                              Vec2Float{float(unit.x1), float(unit.y2 + 2.5)},
                                                              TextAlignment::LEFT,
                                                              30,
                                                              ColorFloat(1, 0, 0, 1)));
            }
#endif
            if (failProbability < 0.01) {
                if (probability >= 0.5) {
                    action.shoot = true;
                }
//                if (probability >= 0.4 && unit.getDistanceTo(*target) > 5) {
//                    action.shoot = true;
//                }
//                if (probability >= 0.3 && unit.getDistanceTo(*target) > 7) {
//                    action.shoot = true;
//                }
            }
        }

        OP_END(SHOT_STRAT);
        return action;
    }

    TPoint calcAim(const TUnit& unit, const TUnit& target, const TActionsVec& actions) {
        //return target.center() - unit.center(); // TODO

        auto selAim = target.center() - unit.center();
        int minTimeDiff = INT_MAX;
        if (target.canJump) {// || target.getDistanceTo(unit) > 11) {
            return selAim;
        }

        TPathFinder pf(&env, target);
        std::tuple<double, double, TPoint> minDist(INF, -INF, TPoint());
        pf.traverseReachable(target, [&](double dist, const TUnit& tg, const TState& state) {
            if (isStand[state.x][state.y]) {
                minDist = std::min(minDist, std::make_tuple(dist, -tg.getDistanceTo(unit), tg.position()));
            }
        });
        TStatesVec tarStates;
        TActionsVec tarActions;
        if (std::get<0>(minDist) < INF && pf.findPath(std::get<2>(minDist), tarStates, tarActions) && tarStates.size()) {
            TDrawUtil().drawPath(statesToPoints(tarStates), ColorFloat(0, 0, 1, 0.5));
        } else {
            return selAim;
        }

        //auto snd = env;
        //snd.oppFallFreeze = true;
        for (int t = 0; t < 70; t++) {
            //snd.getUnit(unit.id)->action = t < actions.size() ? actions[t] : TAction();
            //auto aim = snd.getUnit(target.id)->center() - unit.center();
            auto aim = (t < tarStates.size() ? tarStates[t] : tarStates.back()).getPoint() + TPoint(0, UNIT_HALF_HEIGHT) - unit.center();
            TSandboxCloneOptions copyOptions;
            copyOptions.needLootboxes = false;
            copyOptions.needMines = false;
            copyOptions.needBullets = false;
            copyOptions.unitIds = {unit.id, target.id};
            TSandbox testSnd(env, copyOptions);
            testSnd.oppTotalFreeze = true;
            auto testUnit = testSnd.getUnit(unit.id);
            testUnit->weapon.fireTimer = -1;
            int timeToShot = -1;
            for (int z = 0; z < 30; z++) {
                testUnit->action = z < actions.size() ? actions[z] : TAction();
                testUnit->action.shoot = z == 0;
                testUnit->action.aim = aim;
                testSnd.doTick(10);
                if (testSnd.bullets.empty()) {
                    timeToShot = z;
                    break;
                }
            }
            if (timeToShot != -1 && std::abs(t - timeToShot) < minTimeDiff) {
                minTimeDiff = std::abs(t - timeToShot);
                selAim = aim;
            }
            //snd.doTick(10);
        }
        return selAim;


        auto midAngle = unit.center().getAngleTo(target.center());
        double L = -M_PI / 4, R = M_PI / 4;
        for (int it = 0; it < 30; it++) {
            double m1 = L + (R - L) / 3, m2 = R - (R - L) / 3;
            if (_calcAimDist(unit, target, midAngle + m1, 0, actions) < _calcAimDist(unit, target, midAngle + m2, 0, actions)) {
                R = m2;
            } else {
                L = m1;
            }
        }
        double aimAngle = (L + R) / 2 + midAngle;
        return TPoint::byAngle(aimAngle);
    }

    double _calcAimDist(const TUnit& startUnit, const TUnit& target, double aimAngle, int waitAdditional, const TActionsVec& actions) {
        TSandboxCloneOptions copyOptions;
        copyOptions.needLootboxes = false;
        copyOptions.needMines = false;
        copyOptions.needBullets = false;
        copyOptions.unitIds = {startUnit.id, target.id};
        TSandbox snd(env, copyOptions);
        snd.oppFallFreeze = true;

        auto u = snd.getUnit(startUnit.id);
        auto aim = TPoint::byAngle(aimAngle);
        for (int i = 0; i < 61 + waitAdditional; i++) {
            u->action = i < actions.size() ? actions[i] : TAction();
            u->action.aim = aim;
            if (u->canShot()) {
                if (waitAdditional == 0) {
                    const int itersCount = 6;
                    int successCount = 0;
                    for (int it = -itersCount; it <= itersCount; it++) {
                        auto testEnv = snd;
                        testEnv.getUnit(startUnit.id)->action.shoot = true;
                        testEnv.shotSpreadToss = 1.0 * it / itersCount;
                        const int simulateTicks = 15;
                        for (int j = 0; j < simulateTicks; j++) {
                            testEnv.doTick(10);
                            testEnv.getUnit(startUnit.id)->action.shoot = false;
                        }
                        auto score = testEnv.score[0] - env.score[0];
                        if (score > 0) {
                            successCount++;
                        }
                    }
                    // угол в вомент выстрела
                    auto tar = snd.getUnit(target.id)->center();
                    auto my = u->center();
                    auto angleTo = std::abs(TPoint::getAngleBetween(aim, tar - my));

                    double probability = successCount / (itersCount*2 + 1.0);
                    return (1 - probability) * 100 + angleTo;
                } else {
                    waitAdditional--;
                }
            }
            snd.doTick(10);
        }
        return 100 + M_PI; // impossible state, but...
    }

    TAction _reloadSwap(const TUnit& unit) {
        TAction act;

        auto lb = env.findLootBox(unit);
        if (lb != nullptr && lb->isWeapon()) {
            if (weaponPriority[lb->type] < weaponPriority[unit.weapon.type]) {
                bool free = true;
                for (const auto &opp : env.units) {
                    if (!opp.isMy()) {
                        if (unit.getManhattanDistTo(opp) >= 11) {
                            continue;
                        }
                        if (unit.getManhattanDistTo(opp) < 7 && opp.weapon.fireTimer > 0.5) {
                            continue;
                        }
                        if (opp.weapon.fireTimer > 0.8) {
                            continue;
                        }
                        free = false;
                    }
                }
                act.swapWeapon = free;
                if (_needTakeRocketLauncher(unit) && unit.weapon.type == ELootType::ROCKET_LAUNCHER) {
                    act.swapWeapon = false;
                }
            }
            if (unit.weapon.type != ELootType::ROCKET_LAUNCHER && lb->type == ELootType::ROCKET_LAUNCHER && env.myCount == 1) {
                for (const auto &opp : env.units) {
                    if (!opp.isMy()) {
                        if (opp.getManhattanDistTo(unit) <= 4 && unit.health > 20 && opp.health <= 80) {
                            act.swapWeapon = true;
                        }
                        int cnt = 0;
                        for (auto& b : env.lootBoxes) {
                            cnt += b.type == ELootType::HEALTH_PACK;
                        }
                        if (cnt == 0 && opp.health <= 80 && unit.health <= opp.health) {
                            act.swapWeapon = true;
                        }
                    }
                }
            }
        }

        if (!unit.isMagazineFull()) {
            double minDist = INF;
            for (const auto &opp : env.units) {
                if (opp.isMy()) {
                    continue;
                }
                minDist = std::min(minDist, unit.getManhattanDistTo(opp));
                // TODO: проверять через traverseReachable, когда не будет штрафа
            }
            act.reload = minDist > 17;
        }
        return act;
    }

    void _initialWeaponDistribute() {
        std::vector<TUnit> units;
        for (auto& unit : env.units) {
            if (unit.isMy() && unit.noWeapon()) {
                units.push_back(unit);
            }
        }
        _selLootbox.clear();
        if (units.empty()) {
            return;
        }

        std::vector<int> takeWeapon;
        std::vector<TLootBox*> weapons;
        for (auto& lb : env.lootBoxes) {
            if (lb.isWeapon()) {
                takeWeapon.push_back(-1);
                weapons.push_back(&lb);
            }
        }
        for (int i = 0; i < (int) units.size(); i++) {
            if (i < weapons.size()) {
                takeWeapon[i] = i;
            }
        }
        for (auto& [unitId, pathFinder] : pathFinders) {
            pathFinder.run();
        }
        std::sort(takeWeapon.begin(), takeWeapon.end());
        std::pair<int, double> best(INF, INF);
        std::vector<TLootBox*> res;
        do {
            int sumPriority = 0;
            int cnt = 0;
            double maxDist = 0;
            std::vector<TLootBox*> r(units.size());
            for (int i = 0; i < (int) takeWeapon.size(); i++) {
                auto& weapon = weapons[i];
                auto unitIdx = takeWeapon[i];
                if (unitIdx == -1) {
                    continue;
                }
                auto& pathFinder = pathFinders[units[unitIdx].id];
                auto state = pathFinder.getPointState(weapons[i]->position());

                if (pathFinder.dist[state.x][state.y] < INF) {
                    sumPriority += weaponPriority[weapon->type] + (TLevel::isMyLeft != weapon->isLeft()) * 10;
                    cnt++;
                    maxDist = std::max(maxDist, pathFinder.dist[state.x][state.y]);
                    r[unitIdx] = weapon;
                }
            }
            std::pair<int, double> d(sumPriority, maxDist);
            if (d < best) {
                best = d;
                res = r;
            }
        } while (std::next_permutation(takeWeapon.begin(), takeWeapon.end()));

        for (int i = 0; i < (int) res.size(); i++) {
            if (res[i] != nullptr) {
                _selLootbox[units[i].id] = res[i];
            }
        }
    }
};

#endif //CODESIDE_STRATEGY_H
