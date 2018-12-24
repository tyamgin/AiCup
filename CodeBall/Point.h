#ifndef CODEBALL_POINT_H
#define CODEBALL_POINT_H

#include <cmath>

#define EPS 1e-9
#define EPS2 (EPS*EPS)

#define SQR(x) ((x)*(x))

struct Point {
    double x;
    double y;
    double z;

    Point() {
        x = y = z = 0;
    }

    explicit Point(double x, double y, double z) : x(x), y(y), z(z) {
    }

    void set(double x, double y, double z) {
        this->x = x;
        this->y = y;
        this->z = z;
    }

    double length() const {
        return sqrt(x * x + y * y + z * z);
    }

    double length2() const {
        return x * x + y * y + z * z;
    }

    // Вектор длины 1 того-же направления, или (0, 0), если вектор нулевой
    [[nodiscard]] Point normalized() const {
        auto len = length();
        if (len < EPS)
            len = 1;
        return Point(x / len, y / len, z / len);
    }

    void normalize() {
        auto len = length();
        if (len > EPS) {
            x /= len;
            y /= len;
            z /= len;
        }
    }

    // Вектор длины newLength того-же направления, или (0, 0), если вектор нулевой
    Point take(double newLength) const {
        auto len = length();
        if (len < EPS)
            len = 1;
        auto factor = newLength / len;
        return Point(x * factor, y * factor, z * factor);
    }

    Point clamp(double maxLength) const {
        auto len2 = length2();
        if (len2 < EPS2) {
            return Point();
        }

        auto maxLen2 = maxLength * maxLength;


        if (len2 > maxLen2) {
            auto factor = maxLength / sqrt(len2);
            return *this * factor;
        }
        return *this;
    }

    // Скалярное произведение
    double operator *(const Point &b) const {
        return x * b.x + y * b.y + z * b.z;
    }

    Point operator *(double b) const {
        return Point(x * b, y * b, z * b);
    }

    Point operator /(double b) const {
        return Point(x / b, y / b, z / b);
    }

    Point operator +(const Point &b) const {
        return Point(x + b.x, y + b.y, z + b.z);
    }

    Point operator -(const Point &b) const {
        return Point(x - b.x, y - b.y, z - b.z);
    }

    Point &operator +=(const Point &b) {
        x += b.x;
        y += b.y;
        z += b.z;
        return *this;
    }

    Point &operator -=(const Point &b) {
        x -= b.x;
        y -= b.y;
        z -= b.z;
        return *this;
    }

    Point &operator *=(double b) {
        x *= b;
        y *= b;
        z *= b;
        return *this;
    }

    Point &operator /=(double b) {
        x /= b;
        y /= b;
        z /= b;
        return *this;
    }

    bool equals(const Point &other, double eps = EPS) const {
        return abs(x - other.x) < eps && abs(y - other.y) < eps && abs(z - other.z) < eps;
    }

    bool notEquals(const Point &other, double eps = EPS) const {
        return abs(x - other.x) >= eps || abs(y - other.y) >= eps || abs(z - other.z) >= eps;
    }

    bool operator ==(const Point &other) const {
        return abs(x - other.x) < EPS && abs(y - other.y) < EPS && abs(z - other.z) < EPS;
    }

    bool operator !=(const Point &other) const {
        return abs(x - other.x) >= EPS || abs(y - other.y) >= EPS || abs(z - other.z) >= EPS;
    }

    static double getDistanceTo2(double x1, double y1, double x2, double y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    static double getDistanceTo2(double x1, double y1, double z1, double x2, double y2, double z2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2);
    }

    double getDistanceTo(double x, double y, double z) const {
        return sqrt((x - this->x) * (x - this->x) + (y - this->y) * (y - this->y) + (z - this->z) * (z - this->z));
    }

    double getDistanceTo2(double x, double y, double z) const {
        return (x - this->x) * (x - this->x) + (y - this->y) * (y - this->y) + (z - this->z) * (z - this->z);
    }

    double getDistanceTo(const Point &point) const {
        return sqrt((point.x - this->x) * (point.x - this->x) + (point.y - this->y) * (point.y - this->y) + (point.z - this->z) * (point.z - this->z));
    }

    double getDistanceTo2(const Point &point) const {
        return (point.x - this->x) * (point.x - this->x) + (point.y - this->y) * (point.y - this->y) + (point.z - this->z) * (point.z - this->z);
    }

    bool operator <(const Point &other) const {
        if (x != other.x)
            return x < other.x;
        if (y != other.y)
            return y < other.y;
        return z < other.z;
    }
};

#endif //CODEBALL_POINT_H
