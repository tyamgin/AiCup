#pragma once

#include "Unit.hpp"
#include <climits>

struct CircularUnit : Unit
{
	double radius = 0;

	CircularUnit()
	{
		
	}

	CircularUnit(const nlohmann::json &obj) : Unit(obj)
	{
		if (obj.count("R"))
			radius = obj["R"].get<double>();
	}

	double eatDepth(const CircularUnit &unit) const
	{
		if (!canEat(unit))
			return -INFINITY;

		const double DIAM_EAT_FACTOR = 2.0 / 3;

		double dist = getDistanceTo(unit);
		if (dist - unit.radius + (unit.radius * 2) * DIAM_EAT_FACTOR < radius) {
			return radius - dist;
		}
		return -INFINITY;
	}

	//void moveTo(const Point &to)
	//{
	//	auto max_speed = getMaxSpeed();
	//	auto n = (to - *this).take(max_speed);
	//	speed += (n - speed) * Config::INERTION_FACTOR / mass;

	//	auto new_x = x + speed.x;
	//	auto new_y = y + speed.y;
	//	if (new_x < radius || new_y < radius || new_x > Config::MAP_SIZE - radius || new_y > Config::MAP_SIZE - radius)
	//		return;
	//		
	//	x = new_x;
	//	y = new_y;
	//}
};