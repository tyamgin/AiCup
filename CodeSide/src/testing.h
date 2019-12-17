#ifndef CODESIDE_TESTING_H
#define CODESIDE_TESTING_H

void _compareState(TSandbox& prevEnv, TSandbox& env, TSandbox& prevEnv2) {
    const double eps = 1e-10;

    if (prevEnv.currentTick != env.currentTick) {
        LOG_ERROR("Prev state currentTick mismatch " << prevEnv.currentTick << " vs " << env.currentTick);
    }
    if (prevEnv.units.size() != env.units.size()) {
        return;
    }
    for (int i = 0; i < (int) env.units.size(); i++) {
        const auto& prevUnit = prevEnv.units[i];
        const auto& curUnit = env.units[i];
        if (prevUnit.health != curUnit.health) {
            LOG_ERROR("Prev state unit.health mismatch " << prevUnit.health << " vs " << curUnit.health);
        }

        if (!prevUnit.isMy()) {
            continue;
        }
        if (std::abs(prevUnit.x1 - curUnit.x1) > eps) {
            LOG_ERROR("Prev state unit.x1 mismatch " << prevUnit.x1 << " vs " << curUnit.x1);
        } else if (std::abs(prevUnit.x2 - curUnit.x2) > eps) {
            LOG_ERROR("Prev state unit.x2 mismatch " << prevUnit.x2 << " vs " << curUnit.x2);
        }
        if (std::abs(prevUnit.y1 - curUnit.y1) > eps) {
            LOG_ERROR("Prev state unit.y1 mismatch " << prevUnit.y1 << " vs " << curUnit.y1);
        } else if (std::abs(prevUnit.y2 - curUnit.y2) > eps) {
            LOG_ERROR("Prev state unit.y2 mismatch " << prevUnit.y2 << " vs " << curUnit.y2);
        }
        if (prevUnit.canJump != curUnit.canJump) {
            LOG_ERROR("Prev state unit.canJump mismatch " << prevUnit.canJump << " vs " << curUnit.canJump);
        }
        if (std::abs(prevUnit.jumpMaxTime - curUnit.jumpMaxTime) > eps) {
            LOG_ERROR("Prev state unit.jumpMaxTime mismatch " << prevUnit.jumpMaxTime << " vs " << curUnit.jumpMaxTime);
        }
        if (prevUnit.jumpCanCancel != curUnit.jumpCanCancel) {
            LOG_ERROR("Prev state unit.jumpCanCancel mismatch " << prevUnit.jumpCanCancel << " vs " << curUnit.jumpCanCancel);
        }
        if (prevUnit.mines != curUnit.mines) {
            LOG_ERROR("Prev state unit.mines mismatch " << prevUnit.mines << " vs " << curUnit.mines);
        }
        if (prevUnit.weapon.type != curUnit.weapon.type) {
            LOG_ERROR("Prev state unit.weapon.type mismatch " << (int)prevUnit.weapon.type << " vs " << (int)curUnit.weapon.type);
        }
        if (prevUnit.weapon.magazine != curUnit.weapon.magazine) {
            LOG_ERROR("Prev state unit.weapon.magazine mismatch " << (int)prevUnit.weapon.magazine << " vs " << (int)curUnit.weapon.magazine);
        }
        if (prevUnit.weapon.lastFireTick != curUnit.weapon.lastFireTick) {
            LOG_ERROR("Prev state unit.weapon.lastFireTick mismatch " << (int)prevUnit.weapon.lastFireTick << " vs " << (int)curUnit.weapon.lastFireTick);
        }
        if (std::abs(prevUnit.weapon.spread - curUnit.weapon.spread) > eps) {
            LOG_ERROR("Prev state unit.weapon.spread mismatch " << prevUnit.weapon.spread << " vs " << curUnit.weapon.spread);
        }
        if (std::abs(prevUnit.weapon.fireTimer - curUnit.weapon.fireTimer) > eps) {
            LOG_ERROR("Prev state unit.weapon.fireTimer mismatch " << prevUnit.weapon.fireTimer << " vs " << curUnit.weapon.fireTimer);
        }
        if (std::abs(prevUnit.weapon.lastAngle - curUnit.weapon.lastAngle) > eps) {
            LOG_ERROR("Prev state unit.weapon.lastAngle mismatch " << prevUnit.weapon.lastAngle << " vs " << curUnit.weapon.lastAngle);
        }
    }
    if (prevEnv.bullets.size() != env.bullets.size()) {
        LOG_ERROR("Prev state bullet.size mismatch " << prevEnv.bullets.size() << " vs " << env.bullets.size());
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
                LOG_ERROR("Prev state bullet.x1 mismatch " << prevBullet.x1 << " vs " << curBullet.x1);
            } else if (std::abs(prevBullet.x2 - curBullet.x2) > eps) {
                LOG_ERROR("Prev state bullet.x2 mismatch " << prevBullet.x2 << " vs " << curBullet.x2);
            }
            if (std::abs(prevBullet.y1 - curBullet.y1) > eps) {
                LOG_ERROR("Prev state bullet.y1 mismatch " << prevBullet.y1 << " vs " << curBullet.y1);
            } else if (std::abs(prevBullet.y2 - curBullet.y2) > eps) {
                LOG_ERROR("Prev state bullet.y2 mismatch " << prevBullet.y2 << " vs " << curBullet.y2);
            }
            if (std::abs(prevBullet.velocity.x - curBullet.velocity.x) > eps) {
                LOG_ERROR("Prev state bullet.velocity.x mismatch " << prevBullet.velocity.x << " vs " << curBullet.velocity.x);
            }
            if (std::abs(prevBullet.velocity.y - curBullet.velocity.y) > eps) {
                LOG_ERROR("Prev state bullet.velocity.y mismatch " << prevBullet.velocity.y << " vs " << curBullet.velocity.y);
            }
        }
    }

    if (prevEnv.mines.size() != env.mines.size()) {
        LOG_ERROR("Prev state mines.size mismatch " << prevEnv.mines.size() << " vs " << env.mines.size());
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
                LOG_ERROR("Prev state mine.x1 mismatch " << prevMine.x1 << " vs " << curMine.x1);
            } else if (std::abs(prevMine.x2 - curMine.x2) > eps) {
                LOG_ERROR("Prev state mine.x2 mismatch " << prevMine.x2 << " vs " << curMine.x2);
            }
            if (std::abs(prevMine.y1 - curMine.y1) > eps) {
                LOG_ERROR("Prev state mine.y1 mismatch " << prevMine.y1 << " vs " << curMine.y1);
            } else if (std::abs(prevMine.y2 - curMine.y2) > eps) {
                LOG_ERROR("Prev state mine.y2 mismatch " << prevMine.y2 << " vs " << curMine.y2);
            }
            if (prevMine.state != curMine.state) {
                LOG_ERROR("Prev state mine.state mismatch " << prevMine.state << " vs " << curMine.state);
            }
            if (std::abs(prevMine.timer - curMine.timer) > eps) {
                LOG_ERROR("Prev state mine.timer mismatch " << prevMine.timer << " vs " << curMine.timer);
            }
        }
    }

    prevEnv2.doTick();
}

TAction _ladderLeftStrategy(const TUnit& unit, const TSandbox& env) {
    TAction action;
    //auto action = _strategy(unit, game, debug);
    if (env.currentTick < 100 / UPDATES_PER_TICK) {
        action.velocity = 1;
    } else if (env.currentTick < 1800 / UPDATES_PER_TICK) {
        action.velocity = 10;
    } else if (env.currentTick < 4000 / UPDATES_PER_TICK) {
        action.jump = true;
    } else if (env.currentTick < 4700 / UPDATES_PER_TICK) {
        action.velocity = -10;
    } else if (env.currentTick < 5400 / UPDATES_PER_TICK) {
        action.velocity = 10;
    } else if (env.currentTick < 6000 / UPDATES_PER_TICK) {
        action.jump = true;
    } else if (env.currentTick < 6200 / UPDATES_PER_TICK) {
        action.jumpDown = true;
    } else if (env.currentTick < 10000 / UPDATES_PER_TICK) {
        action.jumpDown = true;
        action.velocity = 1;
    } else if (env.currentTick < 39800 / UPDATES_PER_TICK) {
        action.velocity = 6;
    } else if (env.currentTick < 59600 / UPDATES_PER_TICK) {
        action.velocity = -10;
    } else if (env.currentTick < 61900 / UPDATES_PER_TICK) {
        action.velocity = 10;
    } else if (env.currentTick < 70000 / UPDATES_PER_TICK) {
        action.jump = true;
    } else if (env.currentTick < 72400 / UPDATES_PER_TICK) {
        action.velocity = 10;
    } else if (env.currentTick < 76900 / UPDATES_PER_TICK) {
        action.velocity = -10;
        action.jump = true;
    } else if (env.currentTick < 80000 / UPDATES_PER_TICK) {
        action.velocity = 10;
        action.jump = true;
    } else if (env.currentTick < 80900 / UPDATES_PER_TICK) {
        action.velocity = -10;
        action.jump = true;
    } else if (env.currentTick < 81300 / UPDATES_PER_TICK) {
        action.jump = true;
    } else {
        action.velocity = 10;
        action.jump = true;
    }
    return action;
}

TAction _ladderLeftStrategy2(const TUnit& unit, const TSandbox& env) {
    TAction action;
    //auto action = _strategy(unit, game, debug);
    if (env.currentTick < 100 / UPDATES_PER_TICK) {
        action.velocity = 1;
    } else if (env.currentTick < 24800 / UPDATES_PER_TICK) {
        action.velocity = 10;
    } else if (env.currentTick < 28800 / UPDATES_PER_TICK) {
        action.velocity = 3;
        action.jump = true;
    } else if (env.currentTick < 35700 / UPDATES_PER_TICK) {
        action.velocity = -2;
    } else if (env.currentTick < 38000 / UPDATES_PER_TICK) {
        action.velocity = 10;
        action.jump = true;
    } else if (env.currentTick < 40000 / UPDATES_PER_TICK) {
    } else {
        action.velocity = -4;
    }
    return action;
}



TAction _rifleTestStrategy(const TUnit& unit, const TSandbox& env) {
    // requires seed=12
    TAction action;
    //auto action = _strategy(unit, game, debug);
    if (env.currentTick < 100 / UPDATES_PER_TICK) {
        action.velocity = 1;
    } else if (env.currentTick < 1000 / UPDATES_PER_TICK) {
        action.velocity = 10;
        action.aim = TPoint(15, 1);
    } else {
        action.shoot = true;
        action.aim = TPoint(1, 0.0);
    }
    return action;
}

TAction _jumpStrategy(const Unit& unit, const TSandbox& env) {
    TAction action;
    //auto action = _strategy(unit, game, debug);
    if (env.currentTick < 100) {
        // do nothing
    } else {
        action.jump = true;
    }
    return action;
}

TAction _ladderDownStrategy(const Unit& unit, const TSandbox& env) {
    TAction action;
    //auto action = _strategy(unit, game, debug);
    if (env.currentTick < 100) {
        // do nothing
    } else if (env.currentTick < 1800) {
        action.velocity = 10;
    } else {
        action.jump = true;
    }
    return action;
}


TAction _mineTestStrategy(const TUnit& unit, const TSandbox& env) {
    // requires seed=27
    TAction action;
    if (env.currentTick < 3000 / UPDATES_PER_TICK) {
        action.velocity = 10;
    } else if (env.currentTick < 7000 / UPDATES_PER_TICK) {
        action.jump = true;
        action.velocity = 10;
    } else {
        action.velocity = 10;
        action.plantMine = true;
    }
    return action;
}

TAction _mineTestStrategy2(const TUnit& unit, const TSandbox& env) {
    // requires seed=27
    TAction action;
    if (env.currentTick < 3000 / UPDATES_PER_TICK) {
        action.velocity = 10;
    } else if (env.currentTick < 7000 / UPDATES_PER_TICK) {
        action.jump = true;
        action.velocity = 10;
    } else if (env.currentTick < 16000 / UPDATES_PER_TICK) {
        action.velocity = 10;
    } else if (env.currentTick < 25600 / UPDATES_PER_TICK) {
        action.velocity = 10;
        action.jump = true;
        action.plantMine = true;
    } else if (env.currentTick < 25900 / UPDATES_PER_TICK) {
        action.velocity = 10;
    } else if (env.currentTick < 26800 / UPDATES_PER_TICK) {
        action.plantMine = true;
        action.velocity = 10;
    } else if (env.currentTick < 31900 / UPDATES_PER_TICK) {

    } else if (env.currentTick < 33100 / UPDATES_PER_TICK) {
        action.velocity = 10;
    }
    return action;
}

TAction _mineGunTestStrategy(const TUnit& unit, const TSandbox& env) {
    // requires seed=27
    // взрыв ракетой
    TAction action;
    if (env.currentTick < 3000 / UPDATES_PER_TICK) {
        action.velocity = 10;
    } else if (env.currentTick < 7000 / UPDATES_PER_TICK) {
        action.jump = true;
        action.velocity = 10;
    } else if (env.currentTick < 11200 / UPDATES_PER_TICK) {
        action.velocity = 10;
        action.plantMine = true;
    } else {
        action.aim = TPoint(7, 22) - unit.center();
        action.shoot = true;
        action.velocity = 10;
    }
    return action;
}

TAction _mineGunTestStrategy2(const TUnit& unit, const TSandbox& env) {
    // requires seed=27
    // прямым попаданием
    TAction action;
    if (env.currentTick < 3000 / UPDATES_PER_TICK) {
        action.velocity = 10;
    } else if (env.currentTick < 7000 / UPDATES_PER_TICK) {
        action.jump = true;
        action.velocity = 10;
    } else if (env.currentTick < 11000 / UPDATES_PER_TICK) {
        action.velocity = 10;
        action.plantMine = true;
    } else {
        action.aim = TPoint(7, 22) - unit.center();
        action.shoot = true;
        action.velocity = 10;
    }
    return action;
}

TAction _rocketTestStrategy(const TUnit& unit, const TSandbox& env) {
    // requires seed=8140, empty map
    TAction action;
    if (env.currentTick < 7700 / UPDATES_PER_TICK) {
        action.velocity = 10;
        action.swapWeapon = true;
    } else if (env.currentTick < 20000 / UPDATES_PER_TICK) {
        action.velocity = 10;
        action.aim = TPoint(1, 0);
    } else {
        action.aim = TPoint(1, 0);
        action.shoot = true;
    }
    return action;
}


#endif //CODESIDE_TESTING_H
