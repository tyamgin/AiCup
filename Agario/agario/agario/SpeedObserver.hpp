#pragma once
#include "Model/World.hpp"

struct SpeedObserver
{
	void beforeTick(World &world)
	{
		for (auto &frag : world.me.fragments)
		{
			bool found = false;
			for (auto &prev_frag : _prevWorld.me.fragments)
			{
				if (_isSameFrag(frag, prev_frag))
				{
					frag.is_fast = prev_frag.is_fast;
					found = true;
					break;
				}
			}
			if (!found && world.tick > 0)
			{
				found = found;
			}
		}
		
		for (auto &frag : world.opponentFragments)
		{
			auto it = _prevTickOpponentFragments.find({ frag.playerId, frag.fragmentId });
			if (it != _prevTickOpponentFragments.end())
			{
				// можем узнать скорость
				frag.speed = frag - it->second;
			}
		}

		for (auto &ej : world.ejections)
		{
			//if (_prevTickEjections.count(ej.id))
			// TODO: update ejections
		}

		_prevTickOpponentFragments.clear();
		for (auto &frag : world.opponentFragments)
			_prevTickOpponentFragments[pair<int, int>(frag.playerId, frag.fragmentId)] = frag;
	}

	void adterTick(const World &world)
	{
		_prevWorld = world;
	}

private:
	World _prevWorld;
	map<pair<int, int>, PlayerFragment> _prevTickOpponentFragments;
	map<int, PlayerFragment> _prevTickEjections;

	bool _isSameFrag(const PlayerFragment &a, const PlayerFragment &b)
	{
		return a.playerId == b.playerId && a.fragmentId == b.fragmentId;
		//const double eps = 1e-4;
		//return abs(a.x - b.x) < eps && abs(a.y - b.y) < eps && abs(a.mass - b.mass) < eps && abs(a.radius - b.radius) < eps;
		// TODO: maybe compare ids, ttf
	}
};