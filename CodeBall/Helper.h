#ifndef CODEBALL_HELPER_H
#define CODEBALL_HELPER_H

class Helper {
public:
    static Point maxVelocityTo(const ARobot& a, const Point& b) {
        if (!a.touch || a.touch_normal.y < EPS)
            return Point();

        auto diff = (b - a).normalized();
        diff.y = -(diff.z * a.touch_normal.z + diff.x * a.touch_normal.x) / a.touch_normal.y;
        return diff.take(ROBOT_MAX_GROUND_SPEED);
    }
};

#endif //CODEBALL_HELPER_H
