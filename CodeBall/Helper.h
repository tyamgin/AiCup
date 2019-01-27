#ifndef CODEBALL_HELPER_H
#define CODEBALL_HELPER_H

class Helper {
public:
    static Point maxVelocityTo(const ARobot& a, const Point& b) {
        return maxVelocityToDir(a, b - a);
    }

    static Point maxVelocityToDir(const ARobot& a, Point dir, double speedFactor = 1) {
        if (speedFactor >= 1) {
            speedFactor = 10;
        }

        dir.normalize();

        if (a.isDetouched()) {

        } else {
            dir.y = -(dir.z * a.touchNormal.z + dir.x * a.touchNormal.x) / a.touchNormal.y;
        }

        return dir.take(ROBOT_MAX_GROUND_SPEED * speedFactor);
    }

    static Point goalDir(const Point& pt, double len) {
        static const Point oppGoal = Point(0, 0, ARENA_DEPTH / 2 + ARENA_GOAL_DEPTH / 2);
        return (oppGoal - pt)._y(0).take(len);
    }

    static size_t whichMaxZ(const std::vector<ARobot>& arr) {
        size_t whichMaxZ = 0;
        for (size_t i = 1; i < arr.size(); i++) {
            if (arr[i].z > arr[whichMaxZ].z) {
                whichMaxZ = i;
            }
        }
        return whichMaxZ;
    }
};

template<typename T>
inline void updMin(T& dst, const T& src) {
    if (src < dst) {
        dst = src;
    }
}

#endif //CODEBALL_HELPER_H
