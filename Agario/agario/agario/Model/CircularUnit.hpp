#pragma once

#include "Unit.hpp"

struct CircularUnit : Unit
{
	double radius;

	CircularUnit()
	{
		
	}

	CircularUnit(const nlohmann::json &obj) : Unit(obj)
	{
		if (obj.count("R"))
			radius = obj["R"].get<double>();
	}
};