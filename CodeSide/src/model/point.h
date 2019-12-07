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

    double getAngleTo(const TPoint& to) const {
        return atan2(to.y - y, to.x - x);
    }

    double getAngle() const {
        return atan2(y, x);
    }

    double getDistanceTo(const TPoint& point) const {
        return sqrt(SQR(x - point.x) + SQR(y - point.y));
    }

    double getDistanceTo2(const TPoint& point) const {
        return SQR(x - point.x) + SQR(y - point.y);
    }
};

struct TCell {
    int x, y;

    TCell() : x(0), y(0) {
    }

    TCell(int x, int y) : x(x), y(y) {
    }

    explicit TCell(const TPoint& point) {
        x = int(point.x);
        y = int(point.y);
    }

    bool operator ==(const TCell& cell) const {
        return x == cell.x && y == cell.y;
    }

    bool operator !=(const TCell& cell) const {
        return x != cell.x || y != cell.y;
    }

    bool operator <(const TCell& cell) const {
        if (x != cell.x) {
            return x < cell.x;
        }
        return y < cell.y;
    }
};

#endif //CODESIDE_POINT_H
