#ifndef CODEBALL_RENDERFIGURES_H
#define CODEBALL_RENDERFIGURES_H

#include "nlohmann.h"
#include "Unit.h"
#include "Robot.h"

#ifdef DEBUG
#define M_VISUALIZER 1
#endif

struct RColor {
    double r, g, b, a;

    RColor(double r, double g, double b, double a = 1.0) : r(r), g(g), b(b), a(a) {
    }
};

#define rgba RColor

struct RFigureBase {
    RColor color;
    int ttl;

    RFigureBase(RColor color, int ttl) : color(color) , ttl(ttl) {
    }

    virtual nlohmann::json toJson() = 0;
    virtual ~RFigureBase() = default;

    static bool invertedMode;
};

struct RSphere : public RFigureBase {
    double radius, x, y, z;

    RSphere(const Unit& unit, RColor color) : RFigureBase(color, 1) {
        radius = unit.radius;
        x = unit.x * (invertedMode ? -1 : 1);
        y = unit.y;
        z = unit.z * (invertedMode ? -1 : 1);
    }

    RSphere(const Point& unit, double radius, RColor color) : RFigureBase(color, 1), radius(radius) {
        x = unit.x * (invertedMode ? -1 : 1);
        y = unit.y;
        z = unit.z * (invertedMode ? -1 : 1);
    }

    nlohmann::json toJson() override {
        nlohmann::json json;
        json["r"] = color.r;
        json["g"] = color.g;
        json["b"] = color.b;
        json["a"] = color.a;
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

    RLine(const Point& p1, const Point& p2, double width, RColor color, int ttl = 1) : RFigureBase(color, ttl), width(width) {
        this->p1 = p1;
        this->p2 = p2;
        if (invertedMode) {
            this->p1.x *= -1;
            this->p1.z *= -1;
            this->p2.x *= -1;
            this->p2.z *= -1;
        }
    }

    nlohmann::json toJson() override {
        nlohmann::json json;
        json["r"] = color.r;
        json["g"] = color.g;
        json["b"] = color.b;
        json["a"] = color.a;
        json["x1"] = p1.x * (invertedMode ? -1 : 1);
        json["y1"] = p1.y;
        json["z1"] = p1.z * (invertedMode ? -1 : 1);
        json["x2"] = p2.x * (invertedMode ? -1 : 1);
        json["y2"] = p2.y;
        json["z2"] = p2.z * (invertedMode ? -1 : 1);
        json["width"] = width;
        nlohmann::json ret;
        ret["Line"] = json;
        return ret;
    }
};

struct RText : public RFigureBase {
    std::string text;

    explicit RText(const std::string& text, int ttl = 1)
            : RFigureBase(RColor(0, 0, 0, 0), ttl), text(text) {

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

    static void useNitro(const ARobot& robot) {
#if M_VISUALIZER
        for (int i = 0; i < 12; i++) {
            double ang = 2 * M_PI / 12 * i;
            Point pt(cos(ang), 0, sin(ang));
            addLine(robot + pt, (robot + pt)._y(0), 4, rgba(1, 1, 1));
        }
#endif
    }

    static void markFirstToBall(const ARobot& robot) {
#if M_VISUALIZER
        for (int i = 0; i < 12; i++) {
            double ang = 2 * M_PI / 12 * i;
            Point pt(cos(ang), 0, sin(ang));
            addLine(robot + pt, (robot + pt)._y(ARENA_HEIGHT), 4, rgba(0, 0, 1));
        }
#endif
    }

    static void addTargetLines(const ARobot& robot, const Point& target, RColor color) {
#if M_VISUALIZER
        for (int i = 0; i < 12; i++) {
            double ang = 2 * M_PI / 12 * i;
            Point pt(cos(ang), 0, sin(ang));
            addLine(robot + pt, target, 4, color);
        }
#endif
    }

    static void addChain(const Point& md1, const Point& md2) {
#if M_VISUALIZER
        addLine(md1, md2, 20, rgba(0, 0, 0));
        for (int i = 0; i <= 24; i++) {
            addSphere(md1 + (md2 - md1) / 24 * i, 0.2, rgba(0, 0, 0));
        }
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
