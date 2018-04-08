#pragma once

#include <vector>
#include "PlayerFragment.hpp"
using namespace std;

struct Player
{
	int id = 0;
	vector<PlayerFragment> fragments;

	Player()
	{
		
	}

	Player(const nlohmann::json &obj)
	{
		for (auto &mine_obj : obj)
			fragments.emplace_back(mine_obj);
		
		if (fragments.size())
			id = fragments[0].playerId;
	}

	bool isPointVisible(const ::Point &p) const
	{
		auto coeff = fragments.size() <= 1 ? VIS_FACTOR : VIS_FACTOR_FR * sqrt(1.0 * fragments.size());
		for (auto &frag : fragments)
		{
			auto visionCenter = frag.getVisionCenter();
			if (visionCenter.getDistanceTo2(p) <= (frag.radius*coeff)*(frag.radius*coeff) + EPS)
				return true;
		}
		return false;
	}

	bool canEat(const PlayerFragment &unit) const
	{
		for (auto &frag : fragments)
			if (frag.canEat(unit))
				return true;
		return false;
	}

};
