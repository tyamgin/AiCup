#ifndef CODEBALL_RENDERFIGURES_H
#define CODEBALL_RENDERFIGURES_H

#include "nlohmann.h"

struct RFigureBase {
    double r, g, b, a;

    RFigureBase(double r, double g, double b, double a) : r(r), g(g), b(b), a(a) {
    }
};

struct RSphere : public RFigureBase {
    double radius, x, y, z;

    RSphere(const Unit& unit, double r, double g, double b, double a = 1.0) : RFigureBase(r, g, b, a) {
        radius = unit.radius;
        x = unit.x;
        y = unit.y;
        z = unit.z;
    }

    nlohmann::json toJson() {
        nlohmann::json json;
        json["r"] = r;
        json["g"] = g;
        json["b"] = b;
        json["a"] = a;
        json["x"] = x;
        json["y"] = y;
        json["z"] = z;
        json["radius"] = radius;
        nlohmann::json ret;
        ret["Sphere"] = json;
        return ret;
    }
};

#endif //CODEBALL_RENDERFIGURES_H
