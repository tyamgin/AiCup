#pragma once

#include "CircularUnit.hpp"

struct PlayerFragment : CircularUnit
{
	int ttf;

	PlayerFragment(const nlohmann::json &obj) : CircularUnit(obj)
	{
		if (obj.count("TTF"))
			ttf = obj["TTF"].get<int>(); 
		else
			ttf = 0;
	}
};