#include "Model/World.hpp"
#include "Model/Sandbox.hpp"
#include "SpeedObserver.hpp"
#include "DangerStrategy.hpp"
#include "Utility/Logger.h"

#define GRID_SIZE 32

struct MyStrategy
{
	SpeedObserver speedObserver;

	int lastSeen[GRID_SIZE + 1][GRID_SIZE + 1];
	Sandbox *prevEnv = nullptr;

	MyStrategy()
	{
		memset(lastSeen, 0, sizeof(lastSeen));
	}

	Move onTick(World world, const Move &debug_real_move)
	{
		speedObserver.beforeTick(world);

		auto res = _onTick(world);
		assert(!res.split || !res.eject);
		
		//if (res.split)
		//{
		//	prevEnv = new Sandbox(world);
		//}

		//if (prevEnv != nullptr)
		//{
		//	prevEnv = prevEnv;
		//}

		//if (prevEnv != nullptr)
		//{
		//	prevEnv->move(res);
		//}

		Sandbox env(world);
		env.move(res);
		speedObserver.afterTick(env);
		return res;
	}

	Move _onTick(const World &world)
	{
		if (world.me.fragments.empty())
			return Move();

//		if (world.tick < 430)
//			return Move(world.me.fragments[0]);

		for (int i = 0; i <= GRID_SIZE; i++)
		{
			for (int j = 0; j <= GRID_SIZE; j++)
			{
				::Point pt(1.0 * Config::MAP_SIZE / GRID_SIZE * i, 1.0 * Config::MAP_SIZE / GRID_SIZE * j);
				if (world.me.isPointVisible(pt))
					lastSeen[i][j] = world.tick;
			}
		}

		//int fragIdx = -1;
		auto me = world.me.fragments[0];
		auto mes = me + me.speed*6;

		//for(int i = 0; i < (int) world.opponentFragments.size(); i++)
		//{
		//	auto &oppFrag = world.opponentFragments[i];
		//	if (world.me.canEat(oppFrag))
		//	{
		//		if (fragIdx == -1 || world.opponentFragments[fragIdx].getDistanceTo2(mes) > oppFrag.getDistanceTo2(mes))
		//			fragIdx = i;
		//	}
		//}
		//if (fragIdx != -1)
		//{
		//	return Move(world.opponentFragments[fragIdx].x, world.opponentFragments[fragIdx].y);
		//}

		for (auto &frag : world.me.fragments)
		{
			//if (frag.canSplit(world.me.fragments.size()))
			//{
			//	return MoveFactory::split();
			//}

			//if (frag.can_eject())
			//{
			//	return MoveFactory::eject();
			//}
		}

		bool fight = false;
		for (auto &oppFr : world.opponentFragments)
			for (auto &myFr : world.me.fragments)
				if (oppFr.canEat(myFr) || myFr.canEat(oppFr))
					fight = true;

		if (!world.foods.empty() || fight)
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
		return Move(target.x, target.y);
		
	}

	Move _doPP(const World &world)
	{
		TIMER_START();

		int steps = 15;
		int angles = 24;
		if (world.me.fragments.size() > 5)
		{
			steps = 10;
			angles = 12;
		}

		Move best_move;
		double best_danger = INT_MAX;
		Sandbox best_env(world);
		
		bool can_split_any = false;
		for (auto &frag : world.me.fragments)
			can_split_any |= frag.canSplit(world.me.fragments.size());

		auto check_move_to = [&](bool do_split, ::Point moveto)
		{
			Sandbox env = world;
			env.opponentDummyStrategy = true;
			Move first_move;

			for (int i = 0; i < steps; i++)
			{
				Move mv{ moveto.x, moveto.y };
				mv.split = do_split && i == 0;
				env.move(mv);
				if (i == 0)
					first_move = mv;

				auto d = getDanger(world, env, steps);
				if (d < best_danger)
				{
					best_danger = d;
					best_move = first_move;
					best_env = env;
				}
			}
		};

		for (int do_split = 0; do_split <= (int)can_split_any; do_split++)
		{
			for (int angIdx = 0; angIdx < angles; angIdx++)
			{
				double ang = M_PI * 2 / angles * angIdx;
				auto dir = ::Point::byAngle(ang);
				auto moveto = getBorderPoint(world.me.fragments[0], dir);
				check_move_to(!!do_split, moveto);
			}
		}
		if (world.me.fragments.size() > 1)
		{
			auto rect = getBoundingRect(world.me.fragments);
			const int side = 3;
			for (int i = 0; i < side; i++)
				for (int j = 0; j < side; j++)
					check_move_to(false, ::Point(rect.width() / (side - 1) * i, rect.height() / (side - 1) * j));
		}

		TIMER_ENG_LOG("pp");
		return best_move;
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