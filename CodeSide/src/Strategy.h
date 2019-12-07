#ifndef CODESIDE_STRATEGY_H
#define CODESIDE_STRATEGY_H

#include "constants.h"
#include "sandbox.h"
#include "draw.h"
#include "findpath.h"
#include "testing.h"

#include <algorithm>

double distanceSqr(Vec2Double a, Vec2Double b) {
    return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
}

class Strategy {
    TSandbox prevEnv, prevEnv2;

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
            }
            if (std::abs(prevUnit.x2 - curUnit.x2) > eps) {
                std::cerr << "Prev state unit.x2 mismatch " << prevUnit.x2 << " vs " << curUnit.x2 << std::endl;
            }
            if (std::abs(prevUnit.y1 - curUnit.y1) > eps) {
                std::cerr << "Prev state unit.y1 mismatch " << prevUnit.y1 << " vs " << curUnit.y1 << std::endl;
            }
            if (std::abs(prevUnit.y2 - curUnit.y2) > eps) {
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
                }
                if (std::abs(prevBullet.x2 - curBullet.x2) > eps) {
                    std::cerr << "Prev state bullet.x2 mismatch " << prevBullet.x2 << " vs " << curBullet.x2 << std::endl;
                }
                if (std::abs(prevBullet.y1 - curBullet.y1) > eps) {
                    std::cerr << "Prev state bullet.y1 mismatch " << prevBullet.y1 << " vs " << curBullet.y1 << std::endl;
                }
                if (std::abs(prevBullet.y2 - curBullet.y2) > eps) {
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

        prevEnv2.doTick();
    }

    TAction _strategy(const Unit& unit, const Game& game, Debug& debug) {
        const Unit *nearestEnemy = nullptr;
        for (const Unit &other : game.units) {
            if (other.playerId != unit.playerId) {
                if (nearestEnemy == nullptr ||
                    distanceSqr(unit.position, other.position) < distanceSqr(unit.position, nearestEnemy->position)) {
                    nearestEnemy = &other;
                }
            }
        }
        const LootBox *nearestWeapon = nullptr;
        for (const LootBox &lootBox : game.lootBoxes) {
            if (std::dynamic_pointer_cast<Item::Weapon>(lootBox.item)) {
                if (nearestWeapon == nullptr ||
                    distanceSqr(unit.position, lootBox.position) < distanceSqr(unit.position, nearestWeapon->position)) {
                    nearestWeapon = &lootBox;
                }
            }
        }

        Vec2Double targetPos = unit.position;
        if (unit.weapon == nullptr && nearestWeapon != nullptr) {
            targetPos = nearestWeapon->position;
        } else if (nearestEnemy != nullptr) {
            targetPos = nearestEnemy->position;
        }
        debug.draw(CustomData::Log(
                std::string("Target pos: ") + targetPos.toString()));
        TPoint aim;
        if (nearestEnemy != nullptr) {
            aim = TPoint(nearestEnemy->position.x - unit.position.x,
                         nearestEnemy->position.y - unit.position.y);
        }
        bool jump = targetPos.y > unit.position.y;
        if (targetPos.x > unit.position.x && game.level.tiles[size_t(unit.position.x + 1)][size_t(unit.position.y)] == Tile::WALL) {
            jump = true;
        }
        if (targetPos.x < unit.position.x && game.level.tiles[size_t(unit.position.x - 1)][size_t(unit.position.y)] == Tile::WALL) {
            jump = true;
        }
        TAction action;
        action.velocity = targetPos.x - unit.position.x;
        action.jump = jump;
        action.jumpDown = !action.jump;
        action.aim = aim;
        action.shoot = true;
        action.swapWeapon = false;
        action.plantMine = false;
        return action;
    }

public:
    UnitAction getAction(const Unit& _unit, const Game& game, Debug& debug) {
        TDrawUtil().drawGrid();

        TUnit unit(_unit);
        TSandbox env(unit, game);
        if (env.currentTick == 154) {
            env.currentTick += 0;
        }
        if (env.currentTick > 1) {
            prevEnv2 = prevEnv;
            prevEnv.doTick();
            _compareState(prevEnv, env);
        }
        
        auto action = _ladderLeftStrategy(unit, env, debug);
        if (env.currentTick == 0) {
            TPathFinder::initMap();
        }

        TPathFinder pathFinder(&env, unit);
        std::vector<TPoint> path;
        std::vector<TAction> acts;
        TPoint tar(10.1, 24.1);

        debug.draw(CustomData::Rect({float(tar.x), float(tar.y)}, {0.1, 0.1}, ColorFloat(0, 0, 1, 1)));
        auto reachable = pathFinder.getReachableForDraw();
        for (auto& p : reachable) {
            debug.draw(CustomData::Rect({float(p.x), float(p.y)}, {0.05, 0.05}, ColorFloat(0, 1, 0, 1)));
        }
        if (pathFinder.findPath(tar, path, acts)) {
            for (int i = 1; i < (int)path.size(); i++) {
                float x1 = path[i - 1].x;
                float y1 = path[i - 1].y;
                float x2 = path[i].x;
                float y2 = path[i].y;
                debug.draw(CustomData::Line({x1, y1}, {x2, y2}, 0.1, ColorFloat(1, 0, 0, 1)));
            }
            if (acts.size() > 0) {
                action = acts[0];
            }
            std::cout << action.velocity << " " << action.jump << " " << action.jumpDown << std::endl;
        } else {

        }

        for (auto& u : env.units) {
            if (u.id == unit.id) {
                u.action = action;
            }
        }
        prevEnv = env;
        return action.toUnitAction();
    }
};

#endif //CODESIDE_STRATEGY_H
