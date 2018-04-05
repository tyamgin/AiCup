#pragma once

#include <vector>
#include "PlayerFragment.hpp"
using namespace std;

struct Player
{
	vector<PlayerFragment> fragments;

	Player()
	{
		
	}

	Player(const nlohmann::json &obj)
	{
		for (auto &mine_obj : obj)
		{
			fragments.emplace_back(mine_obj);
		}
	}

	bool isPointVisible(const ::Point &p) const
	{
		auto coeff = fragments.size() <= 1 ? 4.0 : 2.5 * sqrt(1.0 * fragments.size());
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

	void moveTo(const Move &move)
	{
		for (auto &frag : fragments)
			frag.moveTo(move);
	}


};
