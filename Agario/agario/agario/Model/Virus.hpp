#pragma once

#include "CircularUnit.hpp"
#include "../Config.hpp"

struct Virus : CircularUnit
{
	int id;

	Virus(const nlohmann::json &obj) : CircularUnit(obj)
	{
		radius = Config::VIRUS_RADIUS;
		id = stoi(obj["Id"].get<string>());
	}

	double hurtDepth(const CircularUnit &circle) const 
	{
		if (circle.radius < radius)
			return INFINITY;
		
		double dist2 = getDistanceTo2(circle);
		double tR = radius * RAD_HURT_FACTOR + circle.radius;
		if (dist2 < tR * tR)
			return dist2;
		
		return INFINITY;
	}
};