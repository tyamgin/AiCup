#pragma once

#include "World.hpp"

struct Sandbox : public World 
{
	int eatenFoods = 0;

	Sandbox(const World &world) : World(world)
	{
		
	}

	void moveTo(const ::Point &to)
	{
		// move all
		me.moveTo(to);

		//eat all
		for (int i = 0; i < (int) foods.size(); i++)
		{
			auto &food = foods[i];
			double max_eat_depth = 0;
			PlayerFragment *eat_frag = nullptr;
			for (auto &frag : me.fragments)
			{
				auto eat_depth = frag.eatDepth(food);
				if (eat_depth > max_eat_depth)
				{
					max_eat_depth = eat_depth;
					eat_frag = &frag;
				}
			}
			if (eat_frag != nullptr)
			{
				foods.erase(foods.begin() + i);
				eatenFoods++;
				eat_frag->addMass(Config::FOOD_MASS);
				i--;
			}
		}

		tick++;
	}

	
};
