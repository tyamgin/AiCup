#ifndef CODEBALL_HELPER_H
#define CODEBALL_HELPER_H

class Helper {
public:
    static Point maxVelocityTo(const ARobot& a, const Point& b) {
        return maxVelocityToDir(a, b - a);
    }

    static Point maxVelocityToDir(const ARobot& a, Point dir, double speedFactor = 1) {
        if (!a.touch || a.touchNormal.y < EPS) { // TODO: nitro
            return Point();
        }

        if (speedFactor >= 1) {
            speedFactor = 10;
        }

        dir.normalize();
        dir.y = -(dir.z * a.touchNormal.z + dir.x * a.touchNormal.x) / a.touchNormal.y;
        return dir.take(ROBOT_MAX_GROUND_SPEED * speedFactor);
    }

    static Point goalDir(const Point& pt, double len) {
        static const Point oppGoal = Point(0, 0, ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH / 2);
        return (oppGoal - pt)._y(0).take(len);
    }
};

template<typename T>
inline void updMin(T& dst, const T& src) {
    if (src < dst) {
        dst = src;
    }
}

#endif //CODEBALL_HELPER_H
