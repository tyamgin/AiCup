#ifndef CODEBALL_RENDERFIGURES_H
#define CODEBALL_RENDERFIGURES_H

#include "nlohmann.h"
#include "Unit.h"

#ifdef DEBUG
#define M_VISUALIZER 1
#endif

struct RFigureBase {
    double r, g, b, a;
    int ttl;

    RFigureBase(double r, double g, double b, double a, int ttl) : r(r), g(g), b(b), a(a), ttl(ttl) {
    }

    virtual nlohmann::json toJson() = 0;
    virtual ~RFigureBase() = default;
};

struct RSphere : public RFigureBase {
    double radius, x, y, z;

    RSphere(const Unit& unit, double r, double g, double b, double a = 1.0, int ttl = 1) : RFigureBase(r, g, b, a, ttl) {
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

    RLine(const Point& p1, const Point& p2, double width, double r, double g, double b, double a = 1.0, int ttl = 1)
        : RFigureBase(r, g, b, a, ttl), p1(p1), p2(p2), width(width) {

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

    explicit RText(const std::string& text, int ttl = 1)
            : RFigureBase(0, 0, 0, 1, ttl), text(text) {

    }

    nlohmann::json toJson() override {
        nlohmann::json ret;
        ret["Text"] = text;
        return ret;
    }
};

struct Visualizer {
    static std::vector<RFigureBase*> figures;

    template <typename ...Args>
    static void addSphere(Args && ...args) {
#if M_VISUALIZER
        figures.push_back(new RSphere(std::forward<Args>(args)...));
#endif
    }

    template <typename ...Args>
    static void addLine(Args && ...args) {
#if M_VISUALIZER
        figures.push_back(new RLine(std::forward<Args>(args)...));
#endif
    }

    template <typename ...Args>
    static void addText(Args && ...args) {
#if M_VISUALIZER
        figures.push_back(new RText(std::forward<Args>(args)...));
#endif
    }

    static std::string dumpAndClean() {
        nlohmann::json ret = nlohmann::json::array();
        std::vector<RFigureBase*> newFigures;

        for (auto& x : figures) {
            ret.push_back(x->toJson());
            x->ttl--;
            if (x->ttl > 0)
                newFigures.push_back(x);
            else
                delete x;
        }
        newFigures.swap(figures);
        return ret.dump();
    }
};

#endif //CODEBALL_RENDERFIGURES_H
