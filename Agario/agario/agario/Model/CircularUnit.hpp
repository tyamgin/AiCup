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

		double dist2 = getDistanceTo2(unit);
		//if (dist - unit.radius + (unit.radius * 2) * DIAM_EAT_FACTOR < radius)
		//if (dist + unit.radius/3 < radius)
		//if (dist < radius - unit.radius/3)
		if (dist2 < sqr(radius - unit.radius / 3))
			return radius - sqrt(dist2);

		return -INFINITY;
	}
};