#pragma once

#include "Player.hpp"
#include "Food.hpp"
#include "Virus.hpp"

#include <climits>

struct World
{
	Player me;
	vector<PlayerFragment> opponentFragments;
	vector<Ejection> ejections;
	vector<Food> foods;
	vector<Virus> viruses;
	int tick;

	World()
	{
		
	}

	World(const nlohmann::json &obj)
	{
		me = Player(obj["Mine"]);
		for (auto &o : obj["Objects"])
		{
			switch (o["T"].get<string>()[0])
			{
			case 'F':
				foods.emplace_back(o);
				break;
			case 'E':
				ejections.emplace_back(o);
				break;
			case 'V':
				viruses.emplace_back(o);
				break;
			case 'P':
				opponentFragments.emplace_back(o);
				break;
			}
		}
	}
};
