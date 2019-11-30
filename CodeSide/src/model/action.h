#ifndef CODESIDE_ACTION_H
#define CODESIDE_ACTION_H

class TAction {
public:
    TPoint aim;
    double velocity;
    bool jump;
    bool jumpDown;
    bool shoot;
    bool swapWeapon;
    bool plantMine;

    UnitAction toUnitAction() const {
        return UnitAction(velocity, jump, jumpDown, {aim.x, aim.y}, shoot, swapWeapon, plantMine);
    }
};

#endif //CODESIDE_ACTION_H
