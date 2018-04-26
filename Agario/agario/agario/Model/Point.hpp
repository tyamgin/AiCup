#pragma once
#include "../nlohmann/json.hpp"

struct Point
{
	double x, y;

	Point()
	{
		
	}

	Point(const nlohmann::json &obj)
	{
		x = obj["X"].get<double>();
		y = obj["Y"].get<double>();
	}
};