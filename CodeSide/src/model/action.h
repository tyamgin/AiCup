#ifndef CODESIDE_ACTION_H
#define CODESIDE_ACTION_H

#include <vector>

class TAction {
public:
    TPoint aim;
    double velocity;
    bool jump;
    bool jumpDown;
    bool shoot;
    bool reload;
    bool swapWeapon;
    bool plantMine;

    TAction() {
        aim.x = 0;
        aim.y = 0;
        velocity = 0;
        jump = false;
        jumpDown = false;
        shoot = false;
        reload = false;
        swapWeapon = false;
        plantMine = false;
    }

    UnitAction toUnitAction() const {
        return UnitAction(velocity, jump, jumpDown, {aim.x, aim.y}, shoot, reload, swapWeapon, plantMine);
    }
};

typedef std::vector<TAction> TActionsVec;

#endif //CODESIDE_ACTION_H
