#ifndef CODEBALL_RENDERFIGURES_H
#define CODEBALL_RENDERFIGURES_H

#include "nlohmann.h"
#include "Unit.h"

struct RFigureBase {
    double r, g, b, a;

    RFigureBase(double r, double g, double b, double a) : r(r), g(g), b(b), a(a) {
    }

    virtual nlohmann::json toJson() = 0;
};

struct RSphere : public RFigureBase {
    double radius, x, y, z;

    RSphere(const Unit& unit, double r, double g, double b, double a = 1.0) : RFigureBase(r, g, b, a) {
        radius = unit.radius;
        x = unit.x;
        y = unit.y;
        z = unit.z;
    }

    nlohmann::json toJson() override {
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

struct RLine : public RFigureBase {
    Point p1, p2;
    double width;

    RLine(const Point& p1, const Point& p2, double width, double r, double g, double b, double a = 1.0)
        : RFigureBase(r, g, b, a), p1(p1), p2(p2), width(width) {

    }

    nlohmann::json toJson() override {
        nlohmann::json json;
        json["r"] = r;
        json["g"] = g;
        json["b"] = b;
        json["a"] = a;
        json["x1"] = p1.x;
        json["y1"] = p1.y;
        json["z1"] = p1.z;
        json["x2"] = p2.x;
        json["y2"] = p2.y;
        json["z2"] = p2.z;
        json["width"] = width;
        nlohmann::json ret;
        ret["Line"] = json;
        return ret;
    }
};

struct RText : public RFigureBase {
    std::string text;

    explicit RText(const std::string& text)
            : RFigureBase(0, 0, 0, 1), text(text) {

    }

    nlohmann::json toJson() override {
        nlohmann::json ret;
        ret["Text"] = text;
        return ret;
    }
};

struct Visualizer {
    static std::vector<RSphere> spheres;
    static std::vector<RLine> lines;
    static std::vector<RText> texts;

    template <typename ...Args>
    static void addSphere(Args && ...args) {
        spheres.emplace_back(std::forward<Args>(args)...);
    }

    template <typename ...Args>
    static void addLine(Args && ...args) {
        lines.emplace_back(std::forward<Args>(args)...);
    }

    template <typename ...Args>
    static void addText(Args && ...args) {
        texts.emplace_back(std::forward<Args>(args)...);
    }

    static std::string dumpAndClean() {
        nlohmann::json ret = nlohmann::json::array();
        for (auto& x : spheres)
            ret.push_back(x.toJson());
        for (auto& x : lines)
            ret.push_back(x.toJson());
        for (auto& x : texts)
            ret.push_back(x.toJson());
        spheres.clear();
        lines.clear();
        texts.clear();
        return ret.dump();
    }
};

#endif //CODEBALL_RENDERFIGURES_H
