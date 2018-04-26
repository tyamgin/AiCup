#pragma once

#include "Point.hpp"

struct Unit : Point
{
	double mass;
	Point speed;

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
};