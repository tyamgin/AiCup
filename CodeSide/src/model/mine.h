#ifndef CODESIDE_MINE_H
#define CODESIDE_MINE_H

#include "rectangle.h"

class TMine : public TRectangle {
public:
    int playerId;
    double timer; // -1
    MineState state;

    TMine() : TRectangle(0, 0, 0, 0) {
        playerId = -1;
        timer = MINE_PREPARE_TIME;
        state = MineState::PREPARING;
    }

    explicit TMine(const Mine& mine) : TRectangle(mine.position, MINE_SIZE, MINE_SIZE) {
        playerId = mine.playerId;
        timer = mine.timer ? *mine.timer : -1;
        state = mine.state;
    }

    TMine(const TMine& mine) : TRectangle(mine) {
        playerId = mine.playerId;
        timer = mine.timer;
        state = mine.state;
    }

    bool isTriggerOn(const TRectangle& unit) const {
        auto d = MINE_TRIGGER_RADIUS;
        return unit.intersectsWith(x1 - d, y1 - d, x2 + d, y2 + d);
    }

    bool isTouch(const TRectangle& unitOrMine) const {
        auto d = MINE_EXPLOSION_RADIUS - MINE_SIZE / 2;
        return unitOrMine.intersectsWith(x1 - d, y1 - d, x2 + d, y2 + d);
    }
};

#endif //CODESIDE_MINE_H
