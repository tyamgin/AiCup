#ifndef CODESIDE_RECTANGLE_H
#define CODESIDE_RECTANGLE_H

#include "point.h"

inline bool isIn(double l, double r, double x) {
    return l - EPS <= x && x <= r + EPS;
}

class TRectangle {
public:
    double x1, y1, x2, y2;

    TRectangle(double x1, double y1, double x2, double y2) : x1(x1), y1(y1), x2(x2), y2(y2) {
    }

    template<typename TPosition>
    TRectangle(const TPosition& position, double width, double height) {
        x1 = position.x - width / 2;
        x2 = x1 + width;
        y1 = position.y;
        y2 = y1 + height;
    }

    TRectangle(const TRectangle& rect) {
        x1 = rect.x1;
        x2 = rect.x2;
        y1 = rect.y1;
        y2 = rect.y2;
    }

    bool intersectsWith(const TRectangle& rect) const {
        return (isIn(x1, x2, rect.x1) || isIn(x1, x2, rect.x2)) &&
               (isIn(y1, y2, rect.y1) || isIn(y1, y2, rect.y2));
    }
};

#endif //CODESIDE_RECTANGLE_H
