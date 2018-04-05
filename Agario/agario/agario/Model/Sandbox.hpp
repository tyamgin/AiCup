#pragma once

#include "World.hpp"

#define SHRINK_EVERY_TICK 50

struct Sandbox : public World 
{
	vector<int> eatenFoodTicks;

	Sandbox(const World &world) : World(world)
	{
		// TODO: fuse, viruses
		// seed = 111
		
	}

	void move(Move move)
	{
		_doApply(move);
		tick++;
		_doMove(move);
		_doEject(move);
		_doSplit(move);
		_doShrink();
		_doEat();

		//fuse_players();
		//burst_on_viruses();

		_doFixes();
		//update_scores();
		//split_viruses();
	}

	

private:
	void _doApply(const Move &move)
	{
		for (auto &frag : me.fragments)
			frag.applyDirect(move);
	}

	void _doMove(const Move &move)
	{
		for (auto &ej : ejections)
			ej.move();
		
		for (int i = 0; i < (int) me.fragments.size(); i++)
		{
			for (int j = i + 1; j < (int) me.fragments.size(); j++)
			{
				me.fragments[i].collisionCalc(me.fragments[j]);
			}
		}

		for (auto &frag : me.fragments)
			frag.move();
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

		int max_id = 0;
		for (auto &frag : me.fragments)
			max_id = max(max_id, frag.fragmentId);

		for (int i = 0; i < size; i++)
		{
			auto &frag = me.fragments[i];
			if (frag.canSplit(yet_cnt)) 
			{
				auto new_frag = frag.split(max_id);
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

	void _doFix(PlayerFragment &frag)
	{
		double new_speed = frag.getMaxSpeed();
		if (!frag.is_fast) 
		{
			auto length2 = frag.speed.length2();
			if (length2 > new_speed*new_speed)
			{
				auto length_factor = new_speed / sqrt(length2);
				frag.speed *= length_factor;
			}
		}
		
		auto mx = Config::MAP_SIZE;

		if (frag.x - frag.radius < 0)
			frag.x += (frag.radius - frag.x);
		
		if (frag.y - frag.radius < 0)
			frag.y += (frag.radius - frag.y);
		
		if (frag.x + frag.radius > mx)
			frag.x -= (frag.radius + frag.x - mx);
		
		if (frag.y + frag.radius > mx)
			frag.y -= (frag.radius + frag.y - mx);
	}

	void _doFixes() 
	{
		for (auto &frag : me.fragments)
			_doFix(frag);
		for (auto &frag : opponentFragments)
			_doFix(frag);
	}
};
