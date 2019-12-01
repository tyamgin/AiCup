#ifndef CODESIDE_STRATEGY_H
#define CODESIDE_STRATEGY_H

#include "constants.h"
#include "sandbox.h"

double distanceSqr(Vec2Double a, Vec2Double b) {
    return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
}

class Strategy {
    TSandbox prevEnv;

    void _compareState(const TSandbox& prevEnv, const TSandbox& env) {
        if (prevEnv.currentTick != env.currentTick) {
            std::cerr << "Prev state currentTick mismatch " << prevEnv.currentTick << " vs " << env.currentTick << std::endl;
        }
        // ... players
        assert(prevEnv.units.size() == env.units.size());
        for (int i = 0; i < (int) env.units.size(); i++) {
            const auto& prevUnit = prevEnv.units[i];
            const auto& curUnit = env.units[i];
            if (prevUnit.playerId != TLevel::myId) {
                continue;
            }
            if (std::abs(prevUnit.x1 - curUnit.x1) > 1e-8) {
                std::cerr << "Prev state unit.x1 mismatch " << prevUnit.x1 << " vs " << curUnit.x1 << std::endl;
            }
            if (std::abs(prevUnit.y1 - curUnit.y1) > 1e-8) { //  NOTE: после первого тика y почему-то сдвинули на 1e-9
                std::cerr << "Prev state unit.y1 mismatch " << prevUnit.y1 << " vs " << curUnit.y1 << std::endl;
            }
            if (prevUnit.health != curUnit.health) {
                std::cerr << "Prev state unit.health mismatch " << prevUnit.health << " vs " << curUnit.health << std::endl;
            }
            if (prevUnit.canJump != curUnit.canJump) {
                // not critical
                std::cerr << "Prev state unit.canJump mismatch " << prevUnit.canJump << " vs " << curUnit.canJump << std::endl;
            } else {
                if (prevUnit.canJump && std::abs(prevUnit.jumpMaxTime - curUnit.jumpMaxTime) > 1e-8) {
                    std::cerr << "Prev state unit.jumpMaxTime mismatch " << prevUnit.jumpMaxTime << " vs " << curUnit.jumpMaxTime << std::endl;
                }
            }

            if (prevUnit.onLadder != curUnit.onLadder) {
                std::cerr << "Prev state unit.onLadder mismatch " << prevUnit.onLadder << " vs " << curUnit.onLadder << std::endl;
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
            if (prevUnit.weapon.wasShooting != curUnit.weapon.wasShooting) {
                std::cerr << "Prev state unit.weapon.wasShooting mismatch " << (int)prevUnit.weapon.wasShooting << " vs " << (int)curUnit.weapon.wasShooting << std::endl;
            }
            if (prevUnit.weapon.lastFireTick != curUnit.weapon.lastFireTick) {
                std::cerr << "Prev state unit.weapon.lastFireTick mismatch " << (int)prevUnit.weapon.lastFireTick << " vs " << (int)curUnit.weapon.lastFireTick << std::endl;
            }
            if (std::abs(prevUnit.weapon.spread - curUnit.weapon.spread) > 1e-8) {
                std::cerr << "Prev state unit.weapon.spread mismatch " << (int)prevUnit.weapon.spread << " vs " << (int)curUnit.weapon.spread << std::endl;
            }
            if (std::abs(prevUnit.weapon.fireTimer - curUnit.weapon.fireTimer) > 1e-8) {
                std::cerr << "Prev state unit.weapon.fireTimer mismatch " << (int)prevUnit.weapon.fireTimer << " vs " << (int)curUnit.weapon.fireTimer << std::endl;
            }
            if (std::abs(prevUnit.weapon.lastAngle - curUnit.weapon.lastAngle) > 1e-8) {
                std::cerr << "Prev state unit.weapon.lastAngle mismatch " << (int)prevUnit.weapon.lastAngle << " vs " << (int)curUnit.weapon.lastAngle << std::endl;
            }
        }
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
    UnitAction getAction(const Unit& unit, const Game& game, Debug& debug) {
        TSandbox env(TUnit(unit), game);
        if (env.currentTick > 1) {
            if (env.currentTick == 68) {
                env.currentTick += 0;
            }
            prevEnv.doTick();
            _compareState(prevEnv, env);
        }

        TAction action;
        //auto action = _strategy(unit, game, debug);
        if (env.currentTick == 0) {
            // do nothing
        } else if (env.currentTick <= 300) {
            action.velocity = 10;
        } else {
            action.jump = true;
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
