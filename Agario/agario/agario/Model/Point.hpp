#pragma once
#include "../nlohmann/json.hpp"
#include "../Utility/Geometry.hpp"
#define _USE_MATH_DEFINES
#include <math.h>
#define EPS 1e-9

struct Point
{
	double x = 0, y = 0;

	Point()
	{
	}

	explicit Point(double x, double y)
	{
		this->x = x;
		this->y = y;
	}

	explicit Point(const nlohmann::json &obj)
	{
		x = obj["X"].get<double>();
		y = obj["Y"].get<double>();
	}

	double length() const
	{
		return sqrt(x * x + y * y);
	}

	double length2() const
	{
		return x * x + y * y;
	}

	// Вектор длины 1 того-же направления, или (0, 0), если вектор нулевой
	Point normalized() const
	{
		auto len = length();
		if (len < EPS)
			len = 1;
		return Point(x / len, y / len);
	}

	// Вектор длины newLength того-же направления, или (0, 0), если вектор нулевой
	Point take(double newLength) const
	{
		auto len = length();
		if (len < EPS)
			len = 1;
		return Point(x / len * newLength, y / len * newLength);
	}

	/// Скалярное произведение
	double operator *(const Point &b) const
	{
		return x * b.x + y * b.y;
	}

	// Повернутый на 90* вектор
	Point operator ~() const
	{
		return Point(y, -x);
	}

	Point operator *(double b) const
	{
		return Point(x * b, y * b);
	}

	Point operator /(double b) const
	{
		return Point(x / b, y / b);
	}

	Point operator +(const Point &b) const
	{
		return Point(x + b.x, y + b.y);
	}

	Point operator -(const Point &b) const
	{
		return Point(x - b.x, y - b.y);
	}

	Point &operator +=(const Point &b)
	{
		x += b.x;
		y += b.y;
		return *this;
	}

	Point &operator *=(double b)
	{
		x *= b;
		y *= b;
		return *this;
	}

	Point &operator /=(double b)
	{
		x /= b;
		y /= b;
		return *this;
	}

	bool operator ==(const Point &other) const
	{
		return abs(x - other.x) < EPS && abs(y - other.y) < EPS;
	}

	bool operator !=(const Point &other) const
	{
		return abs(x - other.x) >= EPS || abs(y - other.y) >= EPS;
	}

	double getAngle() const
	{
		return atan2(y, x);
	}

	static double getDistanceTo2(double x1, double y1, double x2, double y2)
	{
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}

	double getDistanceTo(double x, double y) const
	{
		return sqrt((x - this->x) * (x - this->x) + (y - this->y) * (y - this->y));
	}

	/// Расстояние то точки в квадтаре
	double getDistanceTo2(double x, double y) const
	{
		return (x - this->x) * (x - this->x) + (y - this->y) * (y - this->y);
	}

	/// Расстояние до точки 
	double getDistanceTo(const Point &point) const
	{
		return sqrt((point.x - this->x) * (point.x - this->x) + (point.y - this->y) * (point.y - this->y));
	}

	/// Расстояние до точки в квадрате
	double getDistanceTo2(const Point &point) const
	{
		return (point.x - this->x) * (point.x - this->x) + (point.y - this->y) * (point.y - this->y);
	}

	static Point zero;
	static Point one;

	static Point byAngle(double angle)
	{
		return Point(cos(angle), sin(angle));
	}

	Point rotateClockwise(double angle) const
	{
		auto cos = ::cos(angle);
		auto sin = ::sin(angle);
		return Point(cos * x + sin * y, -sin * x + cos * y);
	}

	Point rotateClockwise(double angle, const Point &center) const
	{
		auto pt = *this - center;
		return pt.rotateClockwise(angle) + center;
	}

	Point rotateCounterClockwise(double angle) const
	{
		angle = M_PI * 2 - angle;
		auto cos = ::cos(angle);
		auto sin = ::sin(angle);
		return Point(cos * x + sin * y, -sin * x + cos * y);
	}

	Point rotateCounterClockwise(double angle, const Point &center) const
	{
		auto pt = *this - center;
		return pt.rotateCounterClockwise(angle) + center;
	}

	//double getDistanceToCircle(const CircularUnit &circle)
	//{
	//	auto distToCenter = getDistanceTo(circle);
	//	if (distToCenter <= circle.radius)
	//		return 0;
	//	return distToCenter - circle.radius;
	//}
};

struct Rect
{
	double x1, y1, x2, y2;

	Rect(double x1, double y1, double x2, double y2) : x1(x1), y1(y1), x2(x2), y2(y2)
	{
	}

	double width() const
	{
		return x2 - x1;
	}

	double height() const
	{
		return y2 - y1;
	}
};

template<typename Collection>
Rect getBoundingRect(const Collection &arr)
{
	double minX = INFINITY, minY = INFINITY, maxX = -INFINITY, maxY = -INFINITY;
	for (auto &point : arr)
	{
		if (point.x < minX)
			minX = point.x;
		if (point.x > maxX)
			maxX = point.x;
		if (point.y < minY)
			minY = point.y;
		if (point.y > maxY)
			maxY = point.y;
	}
	return Rect(minX, minY, maxX, maxY);
}