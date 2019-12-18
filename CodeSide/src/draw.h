#ifndef CODESIDE_DRAW_H
#define CODESIDE_DRAW_H

#include <string>

#define M_DRAW_GRID 1

#ifdef DEBUG
#define M_DRAW_REACHABILITY_X 3
#define M_DRAW_REACHABILITY_Y 0
#define M_DRAW_PENALTY 1
#else
#define M_DRAW_REACHABILITY_X 0
#define M_DRAW_REACHABILITY_Y 0
#define M_DRAW_PENALTY 0
#endif


class TDrawUtil {
public:
    static std::shared_ptr<Debug> debug;

    void drawGrid() {
#ifdef DEBUG
#if M_DRAW_GRID
        for (int i = 0; i < TLevel::width; i++) {
            float x = i;
            debug->draw(CustomData::Line(Vec2Float{x, 0}, Vec2Float{x, (float)TLevel::height}, 0.05, ColorFloat(0, 1, 0, 0.1)));
            debug->draw(CustomData::PlacedText(std::to_string(i), Vec2Float{x + 0.5f, 0}, TextAlignment::CENTER, 15, ColorFloat(0, 1, 0, 1)));
        }
        for (int j = 0; j < TLevel::height; j++) {
            float y = j;
            debug->draw(CustomData::Line(Vec2Float{0, y}, Vec2Float{(float)TLevel::width, y}, 0.05, ColorFloat(0, 1, 0, 0.1)));
            debug->draw(CustomData::PlacedText(std::to_string(j), Vec2Float{0.3f, y + 0.5f}, TextAlignment::CENTER, 15, ColorFloat(0, 1, 0, 1)));
        }
#endif
#endif
    }

    void debugPoint(const TPoint& point, ColorFloat color = ColorFloat(1, 0, 0, 1)) {
#ifdef DEBUG
        debug->draw(CustomData::Rect(Vec2Float{float(point.x), float(point.y)},
                                     Vec2Float{0.05, 0.05},
                                     color));
#endif
    }

    void drawPath(const std::vector<TPoint>& path, ColorFloat color = ColorFloat(0, 1, 0, 1)) {
#ifdef DEBUG
        for (int i = 1; i < (int) path.size(); i++) {
            auto x1 = (float) path[i - 1].x;
            auto y1 = (float) path[i - 1].y;
            auto x2 = (float) path[i].x;
            auto y2 = (float) path[i].y;
            debug->draw(CustomData::Line({x1, y1}, {x2, y2}, 0.1, color));
        }
#endif
    }

    void drawMinesRadius(const TSandbox& env) {
#ifdef DEBUG
        for (const auto& mine : env.mines) {
            double radius = 0;
            ColorFloat color;
            if (mine.state == IDLE) {
                color = ColorFloat(0, 0, 1, 0.9);
                radius = MINE_TRIGGER_RADIUS + MINE_SIZE / 2;
            } else if (mine.state == TRIGGERED) {
                color = ColorFloat(1, 0, 0, 1);
                radius = MINE_EXPLOSION_RADIUS;
            } else if (mine.state == PREPARING) {
                color = ColorFloat(0, 1, 0, 0.2);
                radius = MINE_TRIGGER_RADIUS + MINE_SIZE / 2;
            }
            if (radius > 0) {
                auto r = float(radius);
                float x = float(mine.x1 + mine.x2) / 2;
                float y = float(mine.y1 + mine.y2) / 2;
                debug->draw(CustomData::Line({x + r, y + r}, {x + r, y - r}, 0.05, color));
                debug->draw(CustomData::Line({x + r, y - r}, {x - r, y - r}, 0.05, color));
                debug->draw(CustomData::Line({x - r, y - r}, {x - r, y + r}, 0.05, color));
                debug->draw(CustomData::Line({x - r, y + r}, {x + r, y + r}, 0.05, color));
            }
        }
#endif
    }

    void drawUnits(const TSandbox& env) {
#ifdef DEBUG
        for (const auto& unit : env.units) {
            if (unit.weapon.type == ELootType::NONE) {
                continue;
            }
            auto center = unit.center();
            auto x = float(center.x);
            debug->draw(CustomData::PlacedText(std::to_string(unit.weapon.magazine),
                                               Vec2Float{x, float(center.y + 0.4)}, TextAlignment::CENTER, 24,
                                               ColorFloat(0, 0, 1, 1)));
            if (unit.weapon.fireTimer > -0.5) {
                debug->draw(CustomData::PlacedText(std::to_string(unit.weapon.fireTimer * TICKS_PER_SECOND).substr(0, 3),
                                                   Vec2Float{x, float(center.y - 0.4)}, TextAlignment::CENTER, 24,
                                                   ColorFloat(1, 0, 0, 1)));
            }
        }
#endif
    }

    void drawAim(const TUnit& unit, const TAction& action) {
#ifdef DEBUG
        if (action.reload) {
            debug->draw(CustomData::PlacedText("RELOAD",
                                               Vec2Float{float(unit.x1), float(unit.y2 + 1)}, TextAlignment::CENTER, 35,
                                               ColorFloat(0, 0, 1, 0.8)));
        }
        if (action.swapWeapon) {
            debug->draw(CustomData::PlacedText("SWAP",
                                               Vec2Float{float(unit.x1), float(unit.y2 + 1)}, TextAlignment::CENTER, 35,
                                               ColorFloat(0, 0, 1, 0.8)));
        }
        if (action.aim.length2() < SQR(0.5)) {
            return;
        }
        auto aim = action.aim.normalized();
        auto cen = unit.center();
        float x1 = float(cen.x),
              y1 = float(cen.y),
              x2 = float(cen.x + aim.x * 20),
              y2 = float(cen.y + aim.y * 20);
        auto color = action.shoot ? ColorFloat(1, 0, 0, 0.9) : ColorFloat(0, 1, 0, 0.5);
        debug->draw(CustomData::Line(Vec2Float{x1, y1}, Vec2Float{x2, y2}, 0.05, color));
#endif
    }
};

#endif //CODESIDE_DRAW_H
