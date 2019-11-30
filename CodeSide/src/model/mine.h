#ifndef CODESIDE_MINE_H
#define CODESIDE_MINE_H

#include "rectangle.h"

class TMine : public TRectangle {
public:
    int playerId;
    double timer;
    double triggerRadius;
    MineState state;

    explicit TMine(const Mine& mine) : TRectangle(mine.position, MINE_SIZE, MINE_SIZE) {
        playerId = mine.playerId;
        timer = mine.timer ? *mine.timer : -1;
        triggerRadius = mine.triggerRadius;
        state = mine.state;
    }

    TMine(const TMine& mine) : TRectangle(mine) {
        playerId = mine.playerId;
        timer = mine.timer;
        triggerRadius = mine.triggerRadius;
        state = mine.state;
    }
};

#endif //CODESIDE_MINE_H
