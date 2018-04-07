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

	double getMaxSpeed2() const
	{
		return Config::SPEED_FACTOR * Config::SPEED_FACTOR / mass;
	}

	void dropSpeed()
	{
		auto length2 = speed.length2();
		auto max_speed2 = getMaxSpeed2();
		if (length2 > max_speed2)
		{
			auto length_factor = sqrt(max_speed2 / length2);
			speed.x *= length_factor;
			speed.y *= length_factor;
		}
	}

	string toString() const
	{
		return "test";
	}
};