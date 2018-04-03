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

	bool canBurst(int yet_cnt)  const
	{
		if (mass < Config::MIN_BURST_MASS * 2)
			return false;
		
		int frags_cnt = int(mass / Config::MIN_BURST_MASS);
		return frags_cnt > 1 && yet_cnt + 1 <= Config::MAX_FRAGS_CNT;
	}

	bool canSplit(int yet_cnt) const
	{
		if (yet_cnt + 1 <= Config::MAX_FRAGS_CNT)
			if (mass > Config::MIN_SPLIT_MASS)
				return true;
		
		return false;
	}
};