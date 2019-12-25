#ifndef CODESIDE_MINE_H
#define CODESIDE_MINE_H

#include "rectangle.h"

class TMine : public TRectangle {
public:
    int playerIdx;
    double timer; // -1
    MineState state;

    TMine() : TRectangle(0, 0, 0, 0) {
        playerIdx = 0;
        timer = MINE_PREPARE_TIME;
        state = MineState::PREPARING;
    }

    explicit TMine(const Mine& mine) : TRectangle(mine.position, MINE_SIZE, MINE_SIZE) {
        playerIdx = mine.playerId == TLevel::myId ? 0 : 1;
        timer = mine.timer ? *mine.timer : -1;
        state = mine.state;
    }

    TMine(const TMine& mine) : TRectangle(mine) {
        playerIdx = mine.playerIdx;
        timer = mine.timer;
        state = mine.state;
    }

    bool isTriggerOn(const TRectangle& unit) const {
        auto d = MINE_TRIGGER_RADIUS;
        return unit.intersectsWith(x1 - d, y1 - d, x2 + d, y2 + d);
    }

    bool isTouch(const TRectangle& unitOrMine, double radiusChange = 0) const {
        auto d = MINE_EXPLOSION_RADIUS - MINE_SIZE / 2 + radiusChange;
        return unitOrMine.intersectsWith(x1 - d, y1 - d, x2 + d, y2 + d);
    }

    bool isNearBullet(const TBullet& bullet) const {
        const double d = 0.8;
        return bullet.x1 < x2 + d && bullet.x2 > x1 - d &&
               bullet.y1 < y2 + d && bullet.y2 > y1 - d;
    }
};

#endif //CODESIDE_MINE_H
