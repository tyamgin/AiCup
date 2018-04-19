#pragma once
#include "Model/World.hpp"
#include <list>
#include <set>

struct FoodInfo
{
	Food food;
	int lastSeenTick;
	bool isMirror;
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
	typedef map<Cell, vector<FoodInfo>> FoodsMap;

	int historyMaxLength = 400;
	FoodsMap foods;
	list<pair<int, Player>> _myHistory;
	list<pair<int, set<Cell>>> _foodsHistory;

	void beforeTick(World &world)
	{
		FoodsMap newMap;
		for (auto &food : world.foods)
		{
			auto cell = Cell(food);
			newMap[cell].push_back({ food, world.tick });
			if (!foods.count(cell))
			{
				// еды не было на предыдущем тике (либо была не видна)
				// значит пытаемся добавить зеркальные отображения

				int mayAppearTick = max(0, world.tick - historyMaxLength); // левая граница, когда могла появиться еда
				for (auto &history : _myHistory)
				{
					if (history.second.isUnitVisible(food))
					{
						mayAppearTick = history.first + 1;
						break;
					}
				}
				// [mayAppearTick, world.tick] - промежуток, когда могла появиться еда food

				for (int flip_x = 0; flip_x <= 1; flip_x++)
				{
					for (int flip_y = 0; flip_y <= 1; flip_y++)
					{
						if (!flip_x && !flip_y)
							continue;

						Food clone = food;
						if (flip_x)
							clone.x = Config::MAP_SIZE - clone.x;
						if (flip_y)
							clone.y = Config::MAP_SIZE - clone.y;

						Cell clone_cell(clone);
						
						if (world.me.isUnitVisible(clone) || foods.count(clone_cell))
							continue;

						bool seen = false;
						for (auto &history : _foodsHistory)
						{
							if (history.first < mayAppearTick)
								break;
							if (history.second.count(clone_cell))
							{
								seen = true;
								break;
							}
						}

						if (seen)
							continue;

						newMap[clone_cell].push_back({ clone, world.tick, true });
					}
				}
			}
		}

		for (auto &p : foods)
		{
			auto &cell = p.first;
			for (auto &food_info : p.second)
			{
				if (world.me.isUnitVisible(food_info.food))
				{
					
				}
				else
				{
					// если еда не видна, то считаем что она ещё есть
					newMap[cell].push_back(food_info);
				}
			}
		}
		foods.swap(newMap);

		_myHistory.push_front({ world.tick, world.me });
		while ((int) _myHistory.size() > historyMaxLength)
			_myHistory.pop_back();

		pair<int, set<Cell> > historyItem;
		historyItem.first = world.tick;
		for (auto &food : world.foods)
			historyItem.second.insert(Cell(food));

		_foodsHistory.emplace_front(historyItem);
		while ((int) _foodsHistory.size() > historyMaxLength)
			_foodsHistory.pop_back();
	}


};