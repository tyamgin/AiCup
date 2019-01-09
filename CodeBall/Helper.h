#ifndef CODEBALL_HELPER_H
#define CODEBALL_HELPER_H

class Helper {
public:
    static Point maxVelocityTo(const ARobot& a, const Point& b) {
        if (!a.touch || a.touchNormal.y < EPS)
            return Point();

        auto diff = (b - a).normalized();
        diff.y = -(diff.z * a.touchNormal.z + diff.x * a.touchNormal.x) / a.touchNormal.y;
        return diff.take(ROBOT_MAX_GROUND_SPEED);
    }
};

#endif //CODEBALL_HELPER_H
