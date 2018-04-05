#include "Model/World.hpp"
#include "Model/Sandbox.hpp"
#include "SpeedObserver.hpp"
#include "DangerStrategy.hpp"

#define GRID_SIZE 50

struct MyStrategy
{
	SpeedObserver speedObserver;

	int lastSeen[GRID_SIZE + 1][GRID_SIZE + 1];
	Sandbox *prevEnv = nullptr;

	MyStrategy()
	{
		memset(lastSeen, 0, sizeof(lastSeen));
	}

	Move onTick(World world)
	{
		speedObserver.update(world);

		auto res = _onTick(world);
		assert(!res.split || !res.eject);
		
		if (res.split || res.eject)
		{
			prevEnv = new Sandbox(world);
			prevEnv->move(res);
		}


		if (prevEnv != nullptr)
		{
			prevEnv = prevEnv;
		}
		return res;
	}

	Move _onTick(const World &world)
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

		for (auto &frag : world.me.fragments)
		{
			if (frag.canSplit(world.me.fragments.size()))
			{
				return MoveFactory::split();
			}

			if (frag.can_eject())
			{
				return MoveFactory::eject();
			}
		}

		bool danger = false;
		for (auto &oppFr : world.opponentFragments)
			for (auto &myFr : world.me.fragments)
				if (oppFr.canEat(myFr))
					danger = true;

		if (!world.foods.empty() || danger)
		{
			return _doPP(world);
		}
		
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

	Move _doPP(const World &world)
	{
		const int steps = 15;
		const int angles = 24;
		::Point best_dir{ 0, 0 };
		double best_danger = INT_MAX;
		Sandbox best_env(world);
		
		for (int angIdx = 0; angIdx < angles; angIdx++)
		{
			double ang = M_PI * 2 / angles * angIdx;
			Sandbox env = world;
			auto dir = ::Point::byAngle(ang);
			auto moveto = getBorderPoint(env.me.fragments[0], dir);

			for (int i = 0; i < steps; i++)
			{
				Move mv{ moveto.x, moveto.y };
				env.move(mv);

				auto d = getDanger(world, env, steps);
				if (d < best_danger)
				{
					best_danger = d;
					best_dir = dir;
					best_env = env;
				}
			}
		}
		auto to = getBorderPoint(world.me.fragments[0], best_dir);
		return Move{ to.x, to.y, "PP" };
	}

	::Point getBorderPoint(::Point center, ::Point dir)
	{
		dir = dir.normalized();
		double L = 0, R = Config::MAP_SIZE * sqrt(2.0);
		for (int it = 0; it < 30; it++)
		{
			double x = (L + R) / 2;
			auto pt = center + dir * x;
			if (0 <= pt.x && pt.x <= Config::MAP_SIZE && 0 <= pt.y && pt.y <= Config::MAP_SIZE)
				L = x;
			else
				R = x;
		}
		auto res = center + dir * ((L + R) / 2);
		res.x = max(0.0, min(res.x, 1.0 * Config::MAP_SIZE));
		res.y = max(0.0, min(res.y, 1.0 * Config::MAP_SIZE));
		return res;
	}
};