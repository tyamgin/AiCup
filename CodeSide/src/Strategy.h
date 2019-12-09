#ifndef CODESIDE_STRATEGY_H
#define CODESIDE_STRATEGY_H

#include "constants.h"
#include "sandbox.h"
#include "draw.h"
#include "findpath.h"
#include "testing.h"

#include <algorithm>
#include <cassert>

class Strategy {
    TSandbox prevEnv, prevEnv2, env;
    TPathFinder pathFinder;

    void _compareState(TSandbox& prevEnv, TSandbox& env) {
        const double eps = 1e-10;

        if (prevEnv.currentTick != env.currentTick) {
            std::cerr << "Prev state currentTick mismatch " << prevEnv.currentTick << " vs " << env.currentTick << std::endl;
        }
        // ... players
        assert(prevEnv.units.size() == env.units.size());
        for (int i = 0; i < (int) env.units.size(); i++) {
            const auto& prevUnit = prevEnv.units[i];
            const auto& curUnit = env.units[i];
            if (prevUnit.health != curUnit.health) {
                std::cerr << "Prev state unit.health mismatch " << prevUnit.health << " vs " << curUnit.health << std::endl;
            }

            if (prevUnit.playerId != TLevel::myId) {
                continue;
            }
            if (std::abs(prevUnit.x1 - curUnit.x1) > eps) {
                std::cerr << "Prev state unit.x1 mismatch " << prevUnit.x1 << " vs " << curUnit.x1 << std::endl;
            } else if (std::abs(prevUnit.x2 - curUnit.x2) > eps) {
                std::cerr << "Prev state unit.x2 mismatch " << prevUnit.x2 << " vs " << curUnit.x2 << std::endl;
            }
            if (std::abs(prevUnit.y1 - curUnit.y1) > eps) {
                std::cerr << "Prev state unit.y1 mismatch " << prevUnit.y1 << " vs " << curUnit.y1 << std::endl;
            } else if (std::abs(prevUnit.y2 - curUnit.y2) > eps) {
                std::cerr << "Prev state unit.y2 mismatch " << prevUnit.y2 << " vs " << curUnit.y2 << std::endl;
            }
            if (prevUnit.canJump != curUnit.canJump) {
                std::cerr << "Prev state unit.canJump mismatch " << prevUnit.canJump << " vs " << curUnit.canJump << std::endl;
            }
            if (std::abs(prevUnit.jumpMaxTime - curUnit.jumpMaxTime) > eps) {
                std::cerr << "Prev state unit.jumpMaxTime mismatch " << prevUnit.jumpMaxTime << " vs " << curUnit.jumpMaxTime << std::endl;
            }
            if (prevUnit.jumpCanCancel != curUnit.jumpCanCancel) {
                std::cerr << "Prev state unit.jumpCanCancel mismatch " << prevUnit.jumpCanCancel << " vs " << curUnit.jumpCanCancel << std::endl;
            }
            if (prevUnit.mines != curUnit.mines) {
                std::cerr << "Prev state unit.mines mismatch " << prevUnit.mines << " vs " << curUnit.mines << std::endl;
            }
            if (prevUnit.weapon.type != curUnit.weapon.type) {
                std::cerr << "Prev state unit.weapon.type mismatch " << (int)prevUnit.weapon.type << " vs " << (int)curUnit.weapon.type << std::endl;
            }
            if (prevUnit.weapon.magazine != curUnit.weapon.magazine) {
                std::cerr << "Prev state unit.weapon.magazine mismatch " << (int)prevUnit.weapon.magazine << " vs " << (int)curUnit.weapon.magazine << std::endl;
            }
//            if (prevUnit.weapon.wasShooting != curUnit.weapon.wasShooting) {
//                std::cerr << "Prev state unit.weapon.wasShooting mismatch " << prevUnit.weapon.wasShooting << " vs " << curUnit.weapon.wasShooting << std::endl;
//            }
            if (prevUnit.weapon.lastFireTick != curUnit.weapon.lastFireTick) {
                std::cerr << "Prev state unit.weapon.lastFireTick mismatch " << (int)prevUnit.weapon.lastFireTick << " vs " << (int)curUnit.weapon.lastFireTick << std::endl;
            }
            if (std::abs(prevUnit.weapon.spread - curUnit.weapon.spread) > eps) {
                std::cerr << "Prev state unit.weapon.spread mismatch " << prevUnit.weapon.spread << " vs " << curUnit.weapon.spread << std::endl;
            }
            if (std::abs(prevUnit.weapon.fireTimer - curUnit.weapon.fireTimer) > eps) {
                std::cerr << "Prev state unit.weapon.fireTimer mismatch " << prevUnit.weapon.fireTimer << " vs " << curUnit.weapon.fireTimer << std::endl;
            }
            if (std::abs(prevUnit.weapon.lastAngle - curUnit.weapon.lastAngle) > eps) {
                std::cerr << "Prev state unit.weapon.lastAngle mismatch " << prevUnit.weapon.lastAngle << " vs " << curUnit.weapon.lastAngle << std::endl;
            }
        }
        if (prevEnv.bullets.size() != env.bullets.size()) {
            std::cerr << "Prev state bullet.size mismatch " << prevEnv.bullets.size() << " vs " << env.bullets.size() << std::endl;
        } else {
            auto comp = [](const TBullet& a, const TBullet& b) {
                if (a.unitId != b.unitId) {
                    return a.unitId < b.unitId;
                }
                if (std::abs(a.x1 - b.x1) > EPS) {
                    return a.x1 < b.x1;
                }
                return a.y1 < b.y1;
            };
            std::sort(prevEnv.bullets.begin(), prevEnv.bullets.end(), comp);
            std::sort(env.bullets.begin(), env.bullets.end(), comp);

            for (int i = 0; i < (int) prevEnv.bullets.size(); i++) {
                auto& prevBullet = prevEnv.bullets[i];
                auto& curBullet = env.bullets[i];
                if (std::abs(prevBullet.x1 - curBullet.x1) > eps) {
                    std::cerr << "Prev state bullet.x1 mismatch " << prevBullet.x1 << " vs " << curBullet.x1 << std::endl;
                } else if (std::abs(prevBullet.x2 - curBullet.x2) > eps) {
                    std::cerr << "Prev state bullet.x2 mismatch " << prevBullet.x2 << " vs " << curBullet.x2 << std::endl;
                }
                if (std::abs(prevBullet.y1 - curBullet.y1) > eps) {
                    std::cerr << "Prev state bullet.y1 mismatch " << prevBullet.y1 << " vs " << curBullet.y1 << std::endl;
                } else if (std::abs(prevBullet.y2 - curBullet.y2) > eps) {
                    std::cerr << "Prev state bullet.y2 mismatch " << prevBullet.y2 << " vs " << curBullet.y2 << std::endl;
                }
                if (std::abs(prevBullet.velocity.x - curBullet.velocity.x) > eps) {
                    std::cerr << "Prev state bullet.velocity.x mismatch " << prevBullet.velocity.x << " vs " << curBullet.velocity.x << std::endl;
                }
                if (std::abs(prevBullet.velocity.y - curBullet.velocity.y) > eps) {
                    std::cerr << "Prev state bullet.velocity.y mismatch " << prevBullet.velocity.y << " vs " << curBullet.velocity.y << std::endl;
                }
            }
        }

        if (prevEnv.mines.size() != env.mines.size()) {
            std::cerr << "Prev state mines.size mismatch " << prevEnv.mines.size() << " vs " << env.mines.size() << std::endl;
        } else {
            auto comp = [](const TMine& a, const TMine& b) {
                if (std::abs(a.x1 - b.x1) > EPS) {
                    return a.x1 < b.x1;
                }
                return a.y1 < b.y1;
            };
            std::sort(prevEnv.mines.begin(), prevEnv.mines.end(), comp);
            std::sort(env.mines.begin(), env.mines.end(), comp);

            for (int i = 0; i < (int) prevEnv.mines.size(); i++) {
                auto &prevMine = prevEnv.mines[i];
                auto &curMine = env.mines[i];
                if (std::abs(prevMine.x1 - curMine.x1) > eps) {
                    std::cerr << "Prev state mine.x1 mismatch " << prevMine.x1 << " vs " << curMine.x1 << std::endl;
                } else if (std::abs(prevMine.x2 - curMine.x2) > eps) {
                    std::cerr << "Prev state mine.x2 mismatch " << prevMine.x2 << " vs " << curMine.x2 << std::endl;
                }
                if (std::abs(prevMine.y1 - curMine.y1) > eps) {
                    std::cerr << "Prev state mine.y1 mismatch " << prevMine.y1 << " vs " << curMine.y1 << std::endl;
                } else if (std::abs(prevMine.y2 - curMine.y2) > eps) {
                    std::cerr << "Prev state mine.y2 mismatch " << prevMine.y2 << " vs " << curMine.y2 << std::endl;
                }
                if (prevMine.state != curMine.state) {
                    std::cerr << "Prev state mine.state mismatch " << prevMine.state << " vs " << curMine.state << std::endl;
                }
                if (std::abs(prevMine.timer - curMine.timer) > eps) {
                    std::cerr << "Prev state mine.timer mismatch " << prevMine.timer << " vs " << curMine.timer << std::endl;
                }
            }
        }

        prevEnv2.doTick();
    }

    std::unordered_map<ELootType, int> weaponPriority = {
            {ELootType::PISTOL, 0},
            {ELootType::ASSAULT_RIFLE, 1},
            {ELootType::ROCKET_LAUNCHER, 2},
            {ELootType::NONE, 3},
    };

    std::optional<std::vector<TAction>> _strategyLoot(const TUnit& unit, std::set<ELootType> lootTypes) {
        std::vector<TPoint> pathPoints;
        std::vector<TAction> actions;


        double minDist = INF;
        TLootBox* selectedLb = nullptr;
        TPoint selectedPos;
        TState selectedState;
        pathFinder.traverseReachable(unit, [&](double dist, const TUnit& unit, const TState& state) {
            TLootBox* lb;
            if (dist < minDist && (lb = env.findLootBox(unit)) != nullptr) {
                if (lootTypes.count(lb->type)) {
                    minDist = dist;
                    selectedLb = lb;
                    selectedPos = unit.position();
                    selectedState = state;
                }
            }
        });
        if (selectedLb != nullptr) {
            if (pathFinder.findPath(selectedPos, pathPoints, actions) && actions.size() > 0) {
                TDrawUtil().drawPath(pathPoints);
                return actions;
            }
        }
        return {};
    }

    std::optional<std::vector<TAction>> _strategy(const TUnit& unit) {
        std::vector<TPoint> pathPoints;
        std::vector<TAction> actions;

//        auto reachable = pathFinder.getReachableForDraw();
//        for (auto& p : reachable) {
//            TDrawUtil().debug->draw(CustomData::Rect({float(p.x), float(p.y)}, {0.05, 0.05}, ColorFloat(0, 1, 0, 1)));
//        }
//
//        if (pathFinder.findPath(TPoint(30, 1), pathPoints, actions) && actions.size() > 0) {
//            TDrawUtil().drawPath(pathPoints);
//            return actions;
//        }

        if (unit.weapon.type == ELootType::NONE) {
            auto maybeAct = _strategyLoot(unit, {ELootType::PISTOL, ELootType::ROCKET_LAUNCHER, ELootType::ASSAULT_RIFLE});
            if (maybeAct) {
                return maybeAct;
            }
        }
        if (unit.health < 80) {
            auto maybeAct = _strategyLoot(unit, {ELootType::HEALTH_PACK});
            if (maybeAct) {
                return maybeAct;
            }
        }

        TUnit* target = nullptr;
        for (auto& u : env.units) {
            if (u.playerId != unit.playerId) {
                target = &u;
            }
        }

        if (target != nullptr) {
            if (pathFinder.findPath(target->position(), pathPoints, actions) && actions.size() > 0) {
                TDrawUtil().drawPath(pathPoints);
                return actions;
            }
        }

        std::set<ELootType> weapons;
        for (const auto &[type, priority] : weaponPriority) {
            if (priority < weaponPriority[unit.weapon.type]) {
                weapons.insert(type);
            }
        }

        if (weapons.size()) {
            auto maybeAct = _strategyLoot(unit, weapons);
            if (maybeAct) {
                return maybeAct.value();
            }
        }

        {
            auto maybeAct = _strategyLoot(unit, {ELootType::MINE});
            if (maybeAct) {
                return maybeAct.value();
            }
        }
        return {};
    }

public:
    UnitAction getAction(const Unit& _unit, const Game& game, Debug& debug) {
        TDrawUtil().drawGrid();
        TUnit unit(_unit);
        env = TSandbox(unit, game);
        TDrawUtil().drawMinesRadius(env);
        if (env.currentTick == 0) {
            TPathFinder::initMap();
        }
        pathFinder = TPathFinder(&env, unit);

        if (env.currentTick == 97) {
            env.currentTick += 0;
        }
        if (env.currentTick > 1) {
            prevEnv2 = prevEnv;
            prevEnv.doTick();
            _compareState(prevEnv, env);
        }

        //auto action = _mineTestStrategy2(unit, env, debug);


        auto maybeActions = _strategy(unit);
        auto actions = maybeActions ? maybeActions.value() : std::vector<TAction>();
        TAction action = actions.size() > 0 ? actions[0] : TAction();

        TSandbox notDodgeEnv = env;
        for (int i = 0; i < 40; i++) {
            notDodgeEnv.getUnit(unit.id)->action = i < actions.size() ? actions[i] : TAction();
            notDodgeEnv.doTick();
        }
        int bestScore = notDodgeEnv.getUnit(unit.id)->health;
        std::vector<TPoint> bestPath;

        for (int dirX = -1; dirX <= 1; dirX++) {
            for (int dirY = -1; dirY <= 1; dirY += 2) {
                TSandbox dodgeEnv = env;
                std::vector<TPoint> path;
                TAction act;
                if (dirY > 0)
                    act.jump = true;
                else
                    act.jumpDown = true;
                act.velocity = dirX * UNIT_MAX_HORIZONTAL_SPEED;
                for (int i = 0; i < 40; i++) {
                    dodgeEnv.getUnit(unit.id)->action = act;
                    dodgeEnv.doTick();
                    path.push_back(dodgeEnv.getUnit(unit.id)->position());
                }
                if (dodgeEnv.getUnit(unit.id)->health > bestScore) {
                    bestScore = dodgeEnv.getUnit(unit.id)->health;
                    bestPath = path;
                    action = act;
                }
            }
        }
        TDrawUtil().drawPath(bestPath, ColorFloat(1, 0, 0, 1));

        auto lb = env.findLootBox(unit);
        if (lb != nullptr && (lb->type == ELootType::PISTOL || lb->type == ELootType::ASSAULT_RIFLE || lb->type == ELootType::ROCKET_LAUNCHER)) {
            if (weaponPriority[lb->type] < weaponPriority[unit.weapon.type]) {
                action.swapWeapon = true;
            }
        }


        TUnit* target = nullptr;
        for (auto& u : env.units) {
            if (u.playerId != unit.playerId) {
                target = &u;
            }
        }
        if (target != nullptr) {
            action.aim = target->center() - unit.center();
            if (unit.weapon.fireTimer < -0.5) {
                auto testEnv = env;
                auto testNothingEnv = env;
                testEnv.getUnit(unit.id)->action = action;
                testEnv.getUnit(unit.id)->action.shoot = true;
                for (int i = 0; i < 40; i++) {
                    testEnv.doTick(3);
                    testNothingEnv.doTick(3);
                    testEnv.getUnit(unit.id)->action.shoot = false;
                }
                if (testEnv.score[0] > testNothingEnv.score[0]) {
                    action.shoot = true;
                }
            }
        }

        for (auto& u : env.units) {
            if (u.id == unit.id) {
                u.action = action;
                std::cout << action.velocity << " " << action.jump << " " << action.jumpDown << std::endl;
            }
        }
        prevEnv = env;
        return action.toUnitAction();
    }
};

#endif //CODESIDE_STRATEGY_H
