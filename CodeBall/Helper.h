#ifndef CODEBALL_HELPER_H
#define CODEBALL_HELPER_H

class Helper {
public:
    static Point maxVelocityTo(const ARobot& a, const Point& b) {
        return maxVelocityToDir(a, b - a);
    }

    static Point maxVelocityToDir(const ARobot& a, Point dir) {
        if (!a.touch || a.touchNormal.y < EPS)
            return Point();

        dir.normalize();
        dir.y = -(dir.z * a.touchNormal.z + dir.x * a.touchNormal.x) / a.touchNormal.y;
        return dir.take(ROBOT_MAX_GROUND_SPEED);
    }
};

#endif //CODEBALL_HELPER_H
