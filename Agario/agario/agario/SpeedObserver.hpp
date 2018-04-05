#pragma once
#include "Model/World.hpp"

struct SpeedObserver
{
	void update(World &world)
	{
		for (auto &frag : world.me.fragments)
			frag.is_fast = frag.speed.length() > frag.getMaxSpeed();
		
		for (auto &frag : world.opponentFragments)
		{
			frag.is_fast = frag.speed.length() > frag.getMaxSpeed();

			// TODO: update speeds
		}

		for (auto &ej : world.ejections)
		{
			
		}
	}

private:
	map<pair<int, int>, PlayerFragment> _prevTickFragments;
	map<int, PlayerFragment> _prevTickEjections;
};