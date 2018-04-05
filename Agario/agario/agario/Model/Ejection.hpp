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

	void move() 
	{
		if (abs(speed.x) < EPS && abs(speed.y) < EPS)
			return;
		
		x = max(radius, min(Config::MAP_SIZE - radius, x + speed.x));
		y = max(radius, min(Config::MAP_SIZE - radius, y + speed.y));

		speed = speed.take(max(0.0, speed.length() - Config::VISCOSITY));
	}
};