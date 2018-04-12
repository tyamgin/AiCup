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
					frag.isFast = prev_frag.isFast;
					found = true;
					break;
				}
			}
			if (!found && world.tick > 1)
			{
				found = found;
			}
		}
		
		vector<CircularUnit*> targets;
		for (auto &frag : world.opponentFragments)
		{
			auto it = _prevTickOpponentFragments.find({ frag.playerId, frag.fragmentId });
			if (it != _prevTickOpponentFragments.end())
			{
				// можем узнать скорость
				frag.speed = frag - it->second; // это скорость на предыдущий тик, но это лучшее приближение
			}
			else
			{
				// считаем, что он движется с максимальной скоростью на ближайшую цель (я, еда, выброс)
				if (targets.empty())
				{
					for (auto &my : world.me.fragments)
						targets.push_back(&my);
					for (auto &food : world.foods)
						targets.push_back(&food);
					for (auto &ej : world.ejections)
						targets.push_back(&ej);
				}

				pair<double, ::Point> nearest(INFINITY, ::Point());
				for (auto tar : targets)
					if (frag.canEat(*tar))
						nearest = min(nearest, { frag.getDistanceTo2(*tar), *tar });

				if (nearest.first < INFINITY)
					frag.speed = (nearest.second - frag).take(frag.getMaxSpeed());
			}
		}

		guessTtf(world.opponentFragments);

		map<int, int> computed_ejections_map;
		for (int i = 0; i < (int)_prevWorld.ejections.size(); i++)
			computed_ejections_map[_prevWorld.ejections[i].id] = i;

		for (auto &ej : world.ejections)
		{
			auto it = computed_ejections_map.find(ej.id);
			if (it == computed_ejections_map.end())
			{
				// проверяем, что этот выброс создался только что
				auto owner = _findEjectionOwner(world, ej);
				if (owner != nullptr)
					ej.speed = owner->speed.take(EJECT_START_SPEED);
			}
			else
			{
				// осталась инфа о выбросе с предыдущего тика
				// его перемещение смоделировано, и переходит на текущий тик
				auto &computed = _prevWorld.ejections[it->second];
#ifdef _DEBUG
				if (ej.ownerPlayerId != computed.ownerPlayerId)
					LOG("Ejection computed wrong (0)");
#endif
				if (_prevTickEjections.count(ej.id))
				{
					auto d_speed = ej - _prevTickEjections[ej.id];
					Ejection::slowDown(d_speed);
					if (d_speed != computed.speed)
					{
						// fix computed state
						computed.speed = d_speed;
						computed.x = ej.x;
						computed.y = ej.y;
					}
				}
#ifdef _DEBUG
				if (computed != ej)
					LOG("Ejection computed wrong");
#endif
				ej.speed = computed.speed;
			}
		}

		_prevTickOpponentFragments.clear();
		for (auto &frag : world.opponentFragments)
			_prevTickOpponentFragments[pair<int, int>(frag.playerId, frag.fragmentId)] = frag;
		for (auto &ej : world.ejections)
			_prevTickEjections[ej.id] = ej;
	}

	const PlayerFragment* _findEjectionOwner(const vector<PlayerFragment> &frags, const Ejection &ej)
	{
		for (auto &frag : frags)
		{
			if (frag + frag.speed.normalized() * (RADIUS_FACTOR * sqrt(frag.mass + EJECT_MASS) + 1) == ej)
				return &frag;
		}
		return nullptr;
	}

	const PlayerFragment* _findEjectionOwner(const World &world, const Ejection &ej)
	{
		if (ej.ownerPlayerId == world.me.id)
			return _findEjectionOwner(world.me.fragments, ej);
		return _findEjectionOwner(world.opponentFragments, ej);
	}

	map<pair<int, int>, int> _ttf;

	void guessTtf(vector<PlayerFragment> &predictiveCollection)
	{
		for (auto &p : _ttf)
			if (p.second > 0)
				p.second--;

		for (auto &frag : predictiveCollection)
		{
			frag.ttf = _ttf[pair<int, int>(frag.playerId, frag.fragmentId)];

			bool splitted = false;
			for (auto &frag2 : predictiveCollection)
			{
				if (frag.playerId == frag2.playerId && frag.fragmentId != frag2.fragmentId && frag == frag2)
				{
					frag.ttf = Config::TICKS_TIL_FUSION;
					splitted = true;
					break;
				}
			}

			if (!splitted && frag.isFast2())
			{
				auto speed = frag.speed.length();
				auto ms = frag.getMaxSpeed();
				static_assert((int)SPLIT_START_SPEED == 9, "Expected SPLIT_START_SPEED to be 9");
				static_assert((int)BURST_START_SPEED == 8, "Expected BURST_START_SPEED to be 8");
				auto if_split_ticks = (SPLIT_START_SPEED - speed) / Config::VISCOSITY;
				auto if_burst_ticks = (BURST_START_SPEED - speed) / Config::VISCOSITY;
				if (isWhole(if_split_ticks))
					frag.ttf = Config::TICKS_TIL_FUSION - int(if_split_ticks + EPS) - 1;
				else if (isWhole(if_burst_ticks))
					frag.ttf = Config::TICKS_TIL_FUSION - int(if_burst_ticks + EPS) - 1;
				else
					LOG("Can't guess ttf");
			}

			_ttf[pair<int, int>(frag.playerId, frag.fragmentId)] = frag.ttf;
		}
	}

	void afterTick(const World &world)
	{
		_prevWorld = world;
	}

	

private:
	World _prevWorld;
	map<pair<int, int>, PlayerFragment> _prevTickOpponentFragments;
	map<int, Ejection> _prevTickEjections;

	bool _isSameFrag(const PlayerFragment &a, const PlayerFragment &b)
	{
		return a.playerId == b.playerId && a.fragmentId == b.fragmentId;
		//const double eps = 1e-4;
		//return abs(a.x - b.x) < eps && abs(a.y - b.y) < eps && abs(a.mass - b.mass) < eps && abs(a.radius - b.radius) < eps;
		// TODO: maybe compare ids, ttf
	}
};