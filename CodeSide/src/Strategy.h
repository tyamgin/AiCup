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
//            if (std::abs(prevUnit.jumpSpeed - curUnit.jumpSpeed) > 1e-8) {
//                std::cerr << "Prev state unit.jumpSpeed mismatch " << prevUnit.jumpSpeed << " vs " << curUnit.jumpSpeed << std::endl;
//            }

            // ... jumpMaxTime
            // ... jumpCanCancel
            // ... walkedRight
            // ... stand
            // ... onGround
            // ... onLadder
            if (prevUnit.canJump != curUnit.canJump) {
                std::cerr << "Prev state unit.canJump mismatch " << prevUnit.canJump << " vs " << curUnit.canJump << std::endl;
            }
//            if (prevUnit.onGround != curUnit.onGround) {
//                std::cerr << "Prev state unit.onGround mismatch " << prevUnit.onGround << " vs " << curUnit.onGround << std::endl;
//            }
            if (prevUnit.onLadder != curUnit.onLadder) {
                std::cerr << "Prev state unit.onLadder mismatch " << prevUnit.onLadder << " vs " << curUnit.onLadder << std::endl;
            }
            if (prevUnit.mines != curUnit.mines) {
                std::cerr << "Prev state unit.mines mismatch " << prevUnit.mines << " vs " << curUnit.mines << std::endl;
            }
            // ... weapon
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
            if (env.currentTick == 128) {
                env.currentTick += 0;
            }
            prevEnv.doTick();
            _compareState(prevEnv, env);
        }

        TAction action;
        //auto action = _strategy(unit, game, debug);
        if (env.currentTick == 0) {

        } else if (env.currentTick == 1) {
            action.velocity = UNIT_MAX_HORIZONTAL_SPEED;
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
