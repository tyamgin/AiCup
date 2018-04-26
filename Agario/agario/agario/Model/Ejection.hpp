#pragma once

#include "CircularUnit.hpp"
#include "../Config.hpp"

struct Ejection : CircularUnit
{
	Ejection(const nlohmann::json &obj) : CircularUnit(obj)
	{
		mass = Config::EJECTION_MASS;
		radius = Config::EJECTION_RADIUS;
	}
};