#pragma once

#include "Point.hpp"
using namespace std;

struct Unit : ::Point
{
	double mass = 0;
	::Point speed;

	Unit()
	{
		
	}

	Unit(const nlohmann::json &obj) : Point(obj)
	{
		if (obj.count("M"))
			mass = obj["M"].get<double>();
		if (obj.count("SX"))
			speed.x = obj["SX"].get<double>();
		if (obj.count("SY"))
			speed.y = obj["SY"].get<double>();
	}

	bool canEat(const Unit &unit) const
	{
		return mass + EPS >= 1.2*unit.mass;
	}

	double getMaxSpeed() const
	{
		return Config::SPEED_FACTOR / sqrt(mass);
	}

	string toString() const
	{
		return "test";
	}
};