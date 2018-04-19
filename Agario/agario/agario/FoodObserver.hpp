#pragma once
#include "Model/World.hpp"

struct FoodInfo
{
	Food food;
	int lastSeenTick;
};

struct Cell
{
	int x, y;

	bool operator <(const Cell &other) const
	{
		if (x != other.x)
			return x < other.x;
		return y < other.y;
	}

	explicit Cell(const ::Point &pt)
	{
		x = int(pt.x + EPS);
		y = int(pt.y + EPS);
	}
};

struct FoodObserver
{
	void beforeTick(World &world)
	{
		map<Cell, vector<FoodInfo>> newMap;
		for (auto &food : world.foods)
			newMap[Cell(food)].push_back({ food, world.tick });

		for (auto &p : foods)
		{
			auto &cell = p.first;
			for (auto &food_info : p.second)
			{
				if (!world.me.isUnitVisible(food_info.food))
				{
					newMap[cell].push_back(food_info);
				}
				else
				{
					// TODO
				}
			}
		}
		foods.swap(newMap);
	}

	int wrongStat;

	map<Cell, vector<FoodInfo>> foods;
};