#pragma once

#include "CircularUnit.hpp"
#include "../Config.hpp"

struct Virus : CircularUnit
{
	Virus(const nlohmann::json &obj) : CircularUnit(obj)
	{
		radius = Config::VIRUS_RADIUS;
	}
};