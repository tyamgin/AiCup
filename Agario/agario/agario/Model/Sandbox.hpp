#pragma once

#include "World.hpp"

#define SHRINK_EVERY_TICK 50

struct Sandbox : public World 
{
	vector<int> eatenFoodTicks;

	Sandbox(const World &world) : World(world)
	{
		
	}

	void move(Move move)
	{
		_doMove(move);
		tick++;
		_doEject(move);
		_doSplit(move);
		_doShrink();
		_doEat();

		//fuse_players();
		//burst_on_viruses();

		//update_players_radius();
		//update_scores();
		//split_viruses();
	}

	

private:
	void _doMove(const Move &move)
	{
		me.moveTo(move);

		for (auto &ej : ejections)
		{
			ej.move();
		}
	}

	void _doEat()
	{
		for (int i = 0; i < (int)foods.size(); i++)
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
				eatenFoodTicks.push_back(tick);
				eat_frag->addMass(Config::FOOD_MASS);
				i--;
			}
		}
	}

	void _doShrink() 
	{
		if (tick % SHRINK_EVERY_TICK)
			return;

		for (auto &frag : me.fragments)
			if (frag.can_shrink())
				frag.shrink_now();
	}

	void _doSplit(const Move &move)
	{
		if (!move.split)
			return;

		int yet_cnt = me.fragments.size();
		int size = yet_cnt;

		for (int i = 0; i < size; i++)
		{
			auto &frag = me.fragments[i];
			if (frag.canSplit(yet_cnt)) 
			{
				auto new_frag = frag.split();
				me.fragments.push_back(new_frag);
				yet_cnt++;
			}
		}
	}

	void _doEject(const Move &move)
	{
		if (!move.eject)
			return;

		for (auto &frag : me.fragments)
		{
			if (frag.can_eject())
			{
				auto new_ej = frag.eject_now();
				ejections.push_back(new_ej);
			}
		}
	}
};
