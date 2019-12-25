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
#include <unordered_set>

/**
 * TODO:
 * - пп от стен, если соперник с базукой
 * - пп в пользу стояния на лестнице (легко уклоняться)
 * - в findpath поощрять за бонусы
 * - ?? предсказывать dx противника
 * - ?? к базучнику близко не подходить
 * - расшатывать прицел как в https://russianaicup.ru/game/view/285088
 * - !!! anti needTakeRL https://russianaicup.ru/game/view/296676
 * - не использовал батут https://russianaicup.ru/game/view/297786
 */

/**
 * CHECK:
 * - aim round
 * - коэффициент score (b) 27.4
 * [801, 14, 877]
 * -76
 * 0.47735399284862934
 *
 * - уменьшение длины в dodge (a) 27.3
 * [869, 17, 918]
 * -49
 * 0.48628987129266926
 *
 * - _applyOppStrategy не только RL
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

    std::unordered_set<int> blockedIds;
    std::unordered_map<int, std::shared_ptr<TActionsVec>> actionsWillBe;

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
                TSandbox blockTestSnd = env;
                if (actionsWillBe.count(other.id)) {
                    blockTestSnd.setUnitActionsSuggest(other.id, actionsWillBe[other.id]);
                }

                for (int i = 0; i < 5 && i < (int) pathPoints.size(); i++) {
                    auto pt = pathPoints[i].getPoint();
                    blockTestSnd.doTick();
                    if (blockTestSnd.getUnit(other.id)->intersectsWith(pt.x - UNIT_HALF_WIDTH, pt.y, pt.x + UNIT_HALF_WIDTH, pt.y + UNIT_HEIGHT)) {
                        blocked = true;
                        break;
                    }
                }
            }
            if (blocked) {
                if (blockedIds.empty() || unit.id < *blockedIds.begin()) {
                    actions[0].jump = true;
                    TDrawUtil::debug->draw(CustomData::PlacedText("JUMP",
                                                                  Vec2Float{float(unit.x1), float(unit.y2 + 0.2)},
                                                                  TextAlignment::LEFT,
                                                                  25,
                                                                  ColorFloat(0, 1, 0, 0.5)));
                } else {
                    TDrawUtil::debug->draw(CustomData::PlacedText("SKIP",
                                                                  Vec2Float{float(unit.x1), float(unit.y2 + 0.2)},
                                                                  TextAlignment::LEFT,
                                                                  25,
                                                                  ColorFloat(0, 1, 0, 0.5)));
                }
                blockedIds.insert(unit.id);
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
        blockedIds.erase(unit.id);
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

        if (_needTakeRocketLauncher(unit) && !unit.weapon.isRocketLauncher()) {
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
            if (!lb.isRocketLauncher() && _needTakeRocketLauncher(unit)) {
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

        bool needGoOut = unit.weapon.fireTimer > 0.5;
        if (unit.health - target->getPossibleShotDamage() <= 0 && unit.weapon.fireTimer > target->weapon.fireTimer) {
            needGoOut = true;
        }

        if (needGoOut) {
            double rangeMin = 7, rangeMax = 12;

            double minDist = INF;
            TPoint selectedPoint;
            pathFinder.traverseReachable(unit, [&](double dist, const TUnit& unit, const TState& state) {
                if (!unit.approxIsStand()) {
                    dist += 10;
                }
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
                actionsWillBe.erase(unit.id);
                auto actions = getAction(unit);
                if (actions.empty()) {
                    result[unit.id] = TAction();
                } else {
                    result[unit.id] = actions[0];
                    actions[0].shoot = false; // TODO
                }
                actionsWillBe[unit.id] = std::make_shared<TActionsVec>(actions);
            }
        }
        for (auto& unit : env.units) {
            if (unit.isMy()) {
                unit.action = result[unit.id];
                auto& acts = actionsWillBe[unit.id];
                if (!acts->empty()) {
                    acts->erase(acts->begin());
                }
            }
        }
        prevEnv = env;
        return result;
    }

    TActionsVec getAction(const TUnit& unit) {
        auto maybeActions = _strategy(unit);
        auto actions = maybeActions ? maybeActions.value() : TActionsVec();

        auto startOppScore = env.score[1];
        auto startMyScore = env.score[0];
        auto scorer = [&](TSandbox& env) {
            int sumOppHealth = 0;
            for (auto& u : env.units) {
                if (!u.isMy()) {
                    sumOppHealth += u.health;
                }
            }
            sumOppHealth = 0; // TODO
            return env.getUnit(unit.id)->health - (env.score[1] - startOppScore) / 2 + (env.score[0] - startMyScore) / 2 - sumOppHealth / 8;
            // минус его жизни для https://russianaicup.ru/game/view/323645
        };
        //         score health not_do dist_to_stand
        std::tuple<int,  int,   bool,  int> bestScore(-INF, unit.health, true, INF);
        TPointsVec bestPath;
        TActionsVec bestActions;

        OP_START(DODGE);
        for (bool doSmth : {false, true}) {
            for (int dirX = -1; dirX <= 1; dirX++) {
                for (int dirY = -1; dirY <= 1; dirY += 1) {
                    if (!doSmth && (dirX || dirY)) {
                        continue;
                    }

                    TSandbox dodgeEnv = env;
                    if (TLevel::teamSize > 1) {
                        dodgeEnv.oppShotSimpleStrategy = {unit.id, env.currentTick + 5};
                    }
                    auto dodged = dodgeEnv.getUnit(unit.id);
                    TPointsVec path;
                    const int simulateTicks = 27;
                    int distToStand = simulateTicks;
                    TAction act;
                    if (doSmth) {
                        if (dirY > 0) {
                            act.jump = true;
                        } else if (dirY < 0) {
                            act.jumpDown = true;
                        }
                        act.velocity = dirX * UNIT_MAX_HORIZONTAL_SPEED;
                    }

                    OP_START(DODGE_OPP);
                    for (auto& u : dodgeEnv.units) {
                        if (u.id != unit.id) {
                            if (actionsWillBe.count(u.id)) {
                                dodgeEnv.setUnitActionsSuggest(u.id, actionsWillBe[u.id]);
                            } else {
                                auto dact = _dodgeSimpleStrategy(dodgeEnv, u.id, simulateTicks);
                                dodgeEnv.setUnitActionsSuggest(u.id, dact, simulateTicks);
                            }
                        }
                    }
                    OP_END(DODGE_OPP);


                    for (int i = 0; i < simulateTicks; i++) {
                        dodged->action = doSmth || i >= actions.size() ? act : actions[i];
                        dodgeEnv.doTick();
                        path.push_back(dodged->position());
                        if (dodged->approxIsStand()) {
                            distToStand = std::min(distToStand, i);
                        }
                    }
                    std::tuple<int, int, bool, int> score(scorer(dodgeEnv), dodged->health, !doSmth, distToStand);
                    if (score > bestScore) {
                        bestScore = score;
                        bestPath = path;
                        bestActions = doSmth ? TActionsVec(simulateTicks, act) : actions;
                    }
                }
            }
        }
        actions = bestActions;
        if (!std::get<2>(bestScore)) {
            TDrawUtil().drawPath(bestPath, ColorFloat(1, 0, 0, 1));
        }
        OP_END(DODGE);

        TAction action = actions.size() > 0 ? actions[0] : TAction();

        auto maybeShot = shotStrategy(unit, actions);
        if (maybeShot) {
            action = maybeShot.value();
            for (auto& a : actions) {
                a.aim = action.aim;
            }
        }

        auto reloadSwapAction = _reloadSwap(unit);
        if (!action.shoot && reloadSwapAction.reload) {
            action.reload = true;
        }
        if (!action.shoot && reloadSwapAction.swapWeapon) {
            action.swapWeapon = true;
        }
        TDrawUtil().drawAim(unit, action);

        if (actions.empty()) {
            actions.emplace_back(action);
        } else {
            actions[0] = action;
        }
        return actions;
    }

    TAction _dodgeSimpleStrategy(TSandbox& afterShotEnv, int unitId, int simulateTicks) {
        int maxHealth = 0;
        int minScore = INF;
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
                auto sc = dodgeEnv.score[1 - TLevel::unitIdToPlayerIdx[unitId]];
                if (opp->health > maxHealth || (opp->health == maxHealth && sc < minScore)) {
                    maxHealth = opp->health;
                    minScore = sc;
                    bestAction = act;
                    if (opp->health == initialHealth) {
                        return bestAction;
                    }
                }
            }
        }
        return bestAction;
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

        struct TShotStratResult {
            bool verdict;
            double scoreExp;
            double successProbability;
            TAction action;

            bool operator <(const TShotStratResult& other) const {
                if (verdict != other.verdict) {
                    return verdict > other.verdict;
                }
                return scoreExp > other.scoreExp;
            }
        };

        auto regularShotStrat = [&](int drawDx, const TPoint& aim) {
            TShotStratResult result{false, 0, 1.0, actions.empty() ? TAction() : actions[0]};
            result.action.aim = aim;
            if (unit.canShotInCurrentTick()) {
                const int simulateTicks = unit.weapon.isRocketLauncher() ? 30 : 15;
                const int itersCount = 6;
                auto testNothingEnv = env;
                for (int i = 0; i < simulateTicks; i++) {
                    testNothingEnv.doTick();
                    if (i == 0) {
                        for (auto& u : testNothingEnv.units) {
                            if (u.id != unit.id || unit.weapon.isRocketLauncher()) {
                                u.action = _dodgeSimpleStrategy(testNothingEnv, u.id, simulateTicks - 1);
                            }
                        }
                    }
                }

                int successCount = 0;
                int friendlyFails = 0;
                double scoreExp = 0;
                for (int it = -itersCount; it <= itersCount; it++) {
                    auto testEnv = env;
                    testEnv.getUnit(unit.id)->action = result.action;
                    testEnv.getUnit(unit.id)->action.shoot = true;
                    testEnv.shotSpreadToss = 1.0 * it / itersCount;
                    for (int i = 0; i < simulateTicks; i++) {
                        testEnv.doTick();
                        testEnv.getUnit(unit.id)->action.shoot = false;
                        if (i == 0) {
                            for (auto& u : testEnv.units) {
                                if (u.id != unit.id || unit.weapon.isRocketLauncher()) {
                                    u.action = _dodgeSimpleStrategy(testEnv, u.id, simulateTicks - 1);
                                }
                            }
                        }
                    }
                    auto add = std::max(0, testEnv.score[0] - testNothingEnv.score[0]);
                    auto loss = std::max(0, testEnv.friendlyLoss[0] - testNothingEnv.friendlyLoss[0]);
                    auto score = add - loss*3/2;
                    if ((score > 0 && loss <= 50)
                        // победа
                        || (testEnv.isWin() && !testNothingEnv.isWin())
                        // убился только 1, и не задел партнера, при этом кого-то убив
                        || (loss >= KILL_SCORE && loss <= KILL_SCORE + 50 && add >= KILL_SCORE && !testEnv.isLose())) {
                        successCount++;
                    } else if (score < 0) {
                        friendlyFails++;
                    }
                    scoreExp += score;
                }
                scoreExp /= (itersCount*2 + 1.0);
                double probability = successCount / (itersCount*2 + 1.0);
#ifdef DEBUG
                double failProbability = friendlyFails / (itersCount*2 + 1.0);
                TDrawUtil::debug->draw(CustomData::PlacedText(std::to_string(probability).substr(0, 4),
                                                               Vec2Float{float(unit.x1 + drawDx), float(unit.y2 + 2)},
                                                               TextAlignment::LEFT,
                                                               22,
                                                               ColorFloat(0, 0, 1, 1)));
                if (friendlyFails > 0) {
                    TDrawUtil::debug->draw(CustomData::PlacedText(std::to_string(failProbability).substr(0, 4),
                                                                  Vec2Float{float(unit.x1 + drawDx), float(unit.y2 + 2.5)},
                                                                  TextAlignment::LEFT,
                                                                  22,
                                                                  ColorFloat(1, 0, 0, 1)));
                }
#endif
                bool doShot = false;
                if (friendlyFails <= 2) {
                    if (probability >= 0.5) {
                        doShot = true;
                    }
                    if (friendlyFails <= 1 && probability >= 0.4 && unit.weapon.isRocketLauncher()) {
                        doShot = true;
                    }
                    if (friendlyFails == 0) {
                        if (probability >= 0.4 && unit.getDistanceTo(*target) > 5) {
                            doShot = true;
                        }
                        if (probability >= 0.3 && unit.getDistanceTo(*target) > 7) {
                            doShot = true;
                        }
                        if (probability >= 0.3 && unit.weapon.type == ELootType::ASSAULT_RIFLE) {
                            doShot = true;
                        }
                    }
                }
                result.verdict = doShot;
                result.action.shoot = doShot;
                result.successProbability = probability;
                result.scoreExp = scoreExp;
            }
            return result;
        };

        auto mineKamikadzeStrat = [&](int plantMinesCount) {
            TShotStratResult result{false, 0, 1.0, TAction()};
            result.action.aim = TPoint(0, -1);
            return result;

            const int simulateTicks = 5;
            auto testNothingEnv = env;
            for (int i = 0; i < simulateTicks; i++) {
                testNothingEnv.doTick();
                if (i == 0) {
                    for (auto& u : testNothingEnv.units) {
                        if (u.id != unit.id) {
                            u.action = _dodgeSimpleStrategy(testNothingEnv, u.id, simulateTicks - 1);
                        }
                    }
                }
            }

            auto testEnv = env;
            auto testUnit = testEnv.getUnit(unit.id);
            testUnit->action = result.action;
            for (int i = 0; i < simulateTicks; i++) {
                testUnit->action.plantMine = i < plantMinesCount;
                testUnit->action.shoot = i == plantMinesCount;
                if (testUnit->action.plantMine && !testUnit->isStandOnGround()) {
                    return result;
                }
                if (testUnit->action.shoot && !testUnit->canShotInCurrentTick()) {
                    return result;
                }

                testEnv.doTick();
                if (i == 0) {
                    for (auto& u : testEnv.units) {
                        if (u.id != unit.id) {
                            u.action = _dodgeSimpleStrategy(testEnv, u.id, simulateTicks - 1);
                        }
                    }
                }
            }
            int successCount = 0;
            int friendlyFails = 0;
            auto add = std::max(0, testEnv.score[0] - testNothingEnv.score[0]);
            auto loss = std::max(0, testEnv.friendlyLoss[0] - testNothingEnv.friendlyLoss[0]);
            auto score = add - loss*3/2;
            if ((score > 0 && loss <= 50)
                // победа
                || (testEnv.isWin() && !testNothingEnv.isWin())
                // убился только 1, и не задел партнера, при этом кого-то убив
                || (loss >= KILL_SCORE && loss <= KILL_SCORE + 50 && add >= KILL_SCORE && !testEnv.isLose())) {
                successCount++;
            } else if (score < 0) {
                friendlyFails++;
            }

            if (successCount > 0) {
                result.verdict = true;
                result.successProbability = 1;
                result.scoreExp = score;
                result.action.shoot = plantMinesCount == 0;
                result.action.plantMine = plantMinesCount > 0;
            }
            return result;
        };

        auto aims = calcAim(unit, *target, actions);
        aims[0] = roundAim(aims[0], unit, *target);

        auto result = regularShotStrat(0, aims[0]);
        if (aims.size() > 1) {
            auto cand = regularShotStrat(1, aims[1]);
            if (cand.verdict && cand < result) {
                result = cand;
            }
        }

        for (int plantMines = 0; plantMines <= 2 && plantMines <= unit.mines; plantMines++) {
            auto cand = mineKamikadzeStrat(plantMines);
            if (cand.verdict && cand < result) {
                result = cand;
#ifdef DEBUG
                TDrawUtil::debug->draw(CustomData::PlacedText("KAMIKADZE " + std::to_string(plantMines),
                                                              Vec2Float{float(unit.x1), float(unit.y2 + 2)},
                                                              TextAlignment::LEFT,
                                                              50,
                                                              ColorFloat(0, 0, 1, 1)));
#endif
            }
        }

        OP_END(SHOT_STRAT);
        return result.action;
    }

    std::vector<TPoint> calcAim(const TUnit& unit, const TUnit& target, const TActionsVec& actions) {
        auto selAim = getAimCenter(unit, target) - unit.center();
        int minTimeDiff = INT_MAX;
        if (target.canJump && target.jumpCanCancel) {// || target.getDistanceTo(unit) > 11) {
            return {selAim};
        }

        TPathFinder pf(&env, target);
        std::tuple<double, double, TPoint> minDist(INF, -INF, TPoint());
        pf.traverseReachable(target, [&](double dist, const TUnit& tg, const TState& state) {
            if (isStand[state.x][state.y]) {
                TDrawUtil().debugPoint(state.getPoint(), ColorFloat(0, 0, 1, 0.5));
                minDist = std::min(minDist, std::make_tuple(dist, -tg.getDistanceTo(unit), tg.position()));
            }
        });
        TPoint sumWithMinDist(0, 0);
        int countWithMinDist = 0;
        pf.traverseReachable(target, [&](double dist, const TUnit& tg, const TState& state) {
            if (isStand[state.x][state.y]) {
                if (std::abs(std::get<0>(minDist) - dist) < 0.1) {
                    countWithMinDist++;
                    sumWithMinDist += tg.position();
                }
            }
        });
        sumWithMinDist /= countWithMinDist;

        TStatesVec tarStates;
        TActionsVec tarActions;
        if (std::get<0>(minDist) < INF && pf.findPath(std::get<2>(minDist), tarStates, tarActions) && tarStates.size()) {
            TDrawUtil().drawPath(statesToPoints(tarStates), ColorFloat(0, 0, 1, 0.5));
        } else {
            return {selAim};
        }

        for (int t = 0; t < 70; t++) {
            TUnit tarClone = target;
            tarClone.setPosition((t < tarStates.size() ? tarStates[t] : tarStates.back()).getPoint());
            auto aim = getAimCenter(unit, tarClone) - unit.center();
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
        }
        std::vector<TPoint> ret = {selAim};
        if (sumWithMinDist.length() > 0) {
            TDrawUtil().debugPoint(sumWithMinDist, ColorFloat(1, 1, 1, 0.8));
            ret.push_back(sumWithMinDist - unit.center());
        }
        return ret;
    }

    TPoint roundAim(const TPoint& aim, const TUnit& unit, const TUnit& target) {
        return aim;

        auto angle = aim.getAngle();
        // 1 рад. = 57 град.
        // при n=100 дискретизация будет по 0.57 градуса
        const int n = unit.getDistanceTo(target) <= 3.5 ? 20 : (unit.getDistanceTo(target) <= 5.5 ? 35 : (unit.getDistanceTo(target) <= 9 ? 60 : 120));
        auto floored = floor(angle * n) / n;
        auto ceiled = ceil(angle * n) / n;
        angle = std::abs(floored - angle) <= std::abs(ceiled - angle) ? floored : ceiled;
        return TPoint::byAngle(angle);
    }

    TAction _reloadSwap(const TUnit& unit) {
        TAction act;

        auto lb = env.findLootBox(unit);
        if (lb != nullptr && lb->isWeapon()) {
            if (weaponPriority[lb->type] < weaponPriority[unit.weapon.type]) {
                bool free = true;
                for (const auto &opp : env.units) {
                    if (!opp.isMy()) {
                        if (unit.getManhattanDistTo(opp) >= 6.9) {
                            continue;
                        }
                        if (unit.getManhattanDistTo(opp) < 5 && opp.weapon.fireTimer > 0.5) {
                            continue;
                        }
                        if (opp.weapon.fireTimer > 0.8) {
                            continue;
                        }
                        free = false;
                    }
                }
                act.swapWeapon = free;
                if (_needTakeRocketLauncher(unit) && unit.weapon.isRocketLauncher()) {
                    act.swapWeapon = false;
                }
            }
            if (!unit.weapon.isRocketLauncher() && lb->isRocketLauncher() && env.myCount == 1) {
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
                auto state = TPathFinder::getPointState(weapons[i]->position());

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

    static TPoint getAimCenter(const TUnit& unit, const TUnit& target) {
        if (unit.weapon.isRocketLauncher()) {
            double dx = 0;
            double dy = 0;
            if (TLevel::getTileType(target.x2 + 1, (target.y1 + target.y2) / 2) == ETile::WALL) {
                dx += (1 - (int(target.x2 + 1) - target.x2)) * UNIT_HALF_WIDTH;
            }
            if (TLevel::getTileType(target.x1 - 1, (target.y1 + target.y2) / 2) == ETile::WALL) {
                dx -= (1 - (target.x1 - int(target.x1))) * UNIT_HALF_WIDTH;
            }
            if (TLevel::getTileType((target.x1 + target.x2) / 2, target.y2 + 1) == ETile::WALL) {
                dy += (1 - (int(target.y2 + 1) - target.y2)) * UNIT_HALF_HEIGHT;
            }
            if (TLevel::getTileType((target.x1 + target.x2) / 2, target.y1 - 1) == ETile::WALL) {
                dy -= (1 - (target.y1 - int(target.y1))) * UNIT_HALF_HEIGHT;
            }
            auto d = 2.0;
            auto c = std::min(d, std::max(0.0, unit.getDistanceTo(target) - 2)) / d;
            dx *= c;
            dy *= c;
            auto ret = target.center() + TPoint(dx, dy);
            return ret;
        }
        return target.center();
    }
};

#endif //CODESIDE_STRATEGY_H
