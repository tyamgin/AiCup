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
};
