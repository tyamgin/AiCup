#pragma once

#include "CircularUnit.hpp"

struct PlayerFragment : CircularUnit
{
	int ttf;

	PlayerFragment()
	{

	}

	PlayerFragment(const nlohmann::json &obj) : CircularUnit(obj)
	{
		if (obj.count("TTF"))
			ttf = obj["TTF"].get<int>();
		else
			ttf = 0;
	}

	Point getVisionCenter() const
	{
		return *this + speed.take(10);
	}

	void addMass(double add)
	{
		mass += add;
		radius = 2 * sqrt(mass);
	}
};