#include "Model/World.hpp"

#define GRID_SIZE 50

struct MyStrategy
{
	int lastSeen[GRID_SIZE + 1][GRID_SIZE + 1];

	Move onTick(const World &world)
	{
		for (int i = 0; i <= GRID_SIZE; i++)
		{
			for (int j = 0; j <= GRID_SIZE; j++)
			{
				::Point pt(1.0 * Config::MAP_SIZE / GRID_SIZE * i, 1.0 * Config::MAP_SIZE / GRID_SIZE * j);
				if (world.me.isPointVisible(pt))
					lastSeen[i][j] = world.tick;
			}
		}

		if (world.me.fragments.empty())
			return Move{ 100, 100 };

		for (auto &frag : world.me.fragments)
		{
			if (frag.mass >= Config::FRAGMENT_MIN_SPLIT_MASS)
			{
				return MoveFactory::split();
			}
		}

		int fragIdx = -1;
		auto me = world.me.fragments[0];
		auto mes = me + me.speed*6;

		for(int i = 0; i < (int) world.opponentFragments.size(); i++)
		{
			auto &oppFrag = world.opponentFragments[i];
			if (world.me.canEat(oppFrag))
			{
				if (fragIdx == -1 || world.opponentFragments[fragIdx].getDistanceTo2(mes) > oppFrag.getDistanceTo2(mes))
					fragIdx = i;
			}
		}
		if (fragIdx != -1)
		{
			return Move{ world.opponentFragments[fragIdx].x, world.opponentFragments[fragIdx].y, "Fight" };
		}

		if (world.foods.empty())
		{
			int minLastSeen = INT_MAX;
			::Point target;

			for (int i = 0; i <= GRID_SIZE; i++)
			{
				for (int j = 0; j <= GRID_SIZE; j++)
				{
					::Point pt(1.0 * Config::MAP_SIZE / GRID_SIZE * i, 1.0 * Config::MAP_SIZE / GRID_SIZE * j);
					if (mes.getDistanceTo2(pt) > sqr(Config::MAP_SIZE / 3.0))
						continue;

					if (lastSeen[i][j] < minLastSeen || 
						lastSeen[i][j] == minLastSeen && mes.getDistanceTo2(target) > mes.getDistanceTo2(pt))
					{
						minLastSeen = lastSeen[i][j];
						target = pt;
					}
				}
			}
			return Move{ target.x, target.y, "Point" };
		}

		auto food_idx = min_element(world.foods.begin(), world.foods.end(), [&](const Food &a, const Food &b)
		{
			auto frag = world.me.fragments[0];
			return frag.getDistanceTo2(a) < frag.getDistanceTo2(b);
		}) - world.foods.begin();

		auto food = world.foods[food_idx];
		return Move{ food.x, food.y, "Food" };
	}
};