#ifndef CODESIDE_DRAW_H
#define CODESIDE_DRAW_H

#include <string>

#define M_DRAW_GRID 1
#define M_DRAW_REACHABILITY_X 3
#define M_DRAW_REACHABILITY_Y 6

class TDrawUtil {
public:
    static std::shared_ptr<Debug> debug;

    void drawGrid() {
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
    }

    void debugPoint(const TPoint& point) {
        debug->draw(CustomData::Rect(Vec2Float{float(point.x), float(point.y)},
                                     Vec2Float{0.05, 0.05},
                                     ColorFloat(1, 0, 0, 1)));
    }
};

#endif //CODESIDE_DRAW_H