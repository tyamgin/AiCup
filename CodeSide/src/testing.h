#ifndef CODESIDE_TESTING_H
#define CODESIDE_TESTING_H

TAction _ladderLeftStrategy(const TUnit& unit, const TSandbox& env, Debug& debug) {
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

TAction _rifleTestStrategy(const TUnit& unit, const TSandbox& env, Debug& debug) {
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

TAction _jumpStrategy(const Unit& unit, const TSandbox& env, Debug& debug) {
    TAction action;
    //auto action = _strategy(unit, game, debug);
    if (env.currentTick < 100) {
        // do nothing
    } else {
        action.jump = true;
    }
    return action;
}

TAction _ladderDownStrategy(const Unit& unit, const TSandbox& env, Debug& debug) {
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


TAction _mineTestStrategy(const TUnit& unit, const TSandbox& env, Debug& debug) {
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

TAction _mineTestStrategy2(const TUnit& unit, const TSandbox& env, Debug& debug) {
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

#endif //CODESIDE_TESTING_H
