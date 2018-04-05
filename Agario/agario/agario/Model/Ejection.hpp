#pragma once

#include "CircularUnit.hpp"
#include "../Config.hpp"

struct Ejection : CircularUnit
{
	int ownerPlayerId = 0;

	Ejection()
	{
	}

	Ejection(const nlohmann::json &obj) : CircularUnit(obj)
	{
		mass = Config::EJECTION_MASS;
		radius = Config::EJECTION_RADIUS;
		ownerPlayerId = obj["pId"].get<int>();
	}

	bool move() 
	{
		if (abs(speed.x) < EPS && abs(speed.y) < EPS)
			return false;
		
		double new_x = max(radius, min(Config::MAP_SIZE - radius, x + speed.x));
		bool changed = (x != new_x);
		x = new_x;

		double new_y = max(radius, min(Config::MAP_SIZE - radius, y + speed.y));
		changed |= (y != new_y);
		y = new_y;

		speed = speed.take(max(0.0, speed.length() - Config::VISCOSITY));
		return changed;
	}
};