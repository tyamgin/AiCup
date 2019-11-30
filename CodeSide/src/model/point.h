#ifndef CODESIDE_POINT_H
#define CODESIDE_POINT_H

#include <cmath>

#define EPS 1e-9
#define EPS2 (EPS*EPS)
#define SQR(x) ((x)*(x))

struct TPoint {
    double x;
    double y;

    TPoint() {
        x = y = 0;
    }

    explicit TPoint(double x, double y) : x(x), y(y) {
    }

    void set(double x, double y) {
        this->x = x;
        this->y = y;
    }

    double length() const {
        return sqrt(x * x + y * y);
    }

    double length2() const {
        return x * x + y * y;
    }

    // Вектор длины 1 того-же направления, или (0, 0), если вектор нулевой
    [[nodiscard]] TPoint normalized() const {
        auto len = length();
        if (len < EPS)
            len = 1;
        return TPoint(x / len, y / len);
    }

    void normalize() {
        auto len = length();
        if (len > EPS) {
            x /= len;
            y /= len;
        }
    }

    // Скалярное произведение
    double operator *(const TPoint &b) const {
        return x * b.x + y * b.y;
    }

    TPoint operator *(double b) const {
        return TPoint(x * b, y * b);
    }

    TPoint operator /(double b) const {
        return TPoint(x / b, y / b);
    }

    TPoint operator +(const TPoint &b) const {
        return TPoint(x + b.x, y + b.y);
    }

    TPoint operator -(const TPoint &b) const {
        return TPoint(x - b.x, y - b.y);
    }

    TPoint &operator +=(const TPoint &b) {
        x += b.x;
        y += b.y;
        return *this;
    }

    TPoint &operator -=(const TPoint &b) {
        x -= b.x;
        y -= b.y;
        return *this;
    }

    TPoint &operator *=(double b) {
        x *= b;
        y *= b;
        return *this;
    }

    TPoint &operator /=(double b) {
        x /= b;
        y /= b;
        return *this;
    }
};

#endif //CODESIDE_POINT_H
