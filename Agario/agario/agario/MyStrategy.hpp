#include "Model/World.hpp"
#include "Model/Sandbox.hpp"
#include "SpeedObserver.hpp"
#include "DangerStrategy.hpp"
#include "Utility/Logger.h"

#if M_VISUAL
#include "Visualizer/Visualizer.hpp"
#endif

struct MyStrategy
{
	SpeedObserver speedObserver;
	int lastSeen[VISION_GRID_SIZE + 1][VISION_GRID_SIZE + 1];

	MyStrategy()
	{
		memset(lastSeen, 0, sizeof(lastSeen));
	}

	Move onTick(World world, const Move &debug_real_move)
	{
		speedObserver.beforeTick(world);
#if M_VISUAL
		Visualizer::update(world);
#endif
		TIMER_START();

		auto res = _onTick(world);
		assert(!res.split || !res.eject);
		
		Sandbox env(world, lastSeen);
		env.move(res);
		speedObserver.afterTick(env);

		TIMER_ENG_LOG("all");
		return res;
	}

	Move _onTick(const World &world)
	{
		if (world.me.fragments.empty())
			return Move();
		//if (world.tick < 100)
		//	return Move();

		//static bool asd = false;
		//if (world.me.fragments[0].canEject())
		//{
		//	Move move;
		//	move.eject = true;
		//	//asd = true;
		//	return move;
		//}
		//if (asd)
		//	return Move(world.me.fragments[0] - world.me.fragments[0].speed);

		for (int i = 0; i <= VISION_GRID_SIZE; i++)
		{
			for (int j = 0; j <= VISION_GRID_SIZE; j++)
			{
				::Point pt(1.0 * Config::MAP_SIZE / VISION_GRID_SIZE * i, 1.0 * Config::MAP_SIZE / VISION_GRID_SIZE * j);
				if (world.me.isPointVisible(pt))
					lastSeen[i][j] = world.tick;
			}
		}

		auto me = world.me.fragments[0];
		auto mes = me + me.speed*6;

		bool fight = false;
		for (auto &oppFr : world.opponentFragments)
			for (auto &myFr : world.me.fragments)
				if (oppFr.canEat(myFr, Config::FOOD_MASS) || myFr.canEat(oppFr))
					fight = true;

		return _doPP(world, !fight);
	}

	Move _doPP(const World &world, bool food_mode)
	{
		TIMER_START();

		int steps = 15;
		int angles = 24;
		int angles2 = 12;
		if (world.me.fragments.size() > 5)
		{
			steps = 10;
			angles = angles2;
		}

		Move best_move;
		double best_danger = INT_MAX;
		Sandbox best_env(world, lastSeen);
		
		bool can_split_any = false;
		for (auto &frag : world.me.fragments)
			can_split_any |= frag.canSplit(world.me.fragments.size());

		int foods_count = 0;
		for (auto &food : world.foods)
			if (Config::MAP_CENTER.getDistanceTo2(food) < sqr(Config::MAP_SIZE*M_SAFE_RAD_FACTOR + FOOD_RADIUS))
				foods_count++;

		vector<Virus> closestViruses;
		for (auto &virus : world.viruses)
		{
			for (auto &frag : world.me.fragments)
			{
				if (virus.getDistanceTo(frag) - virus.radius - frag.radius < frag.getMaxSpeed() * steps)
				{
					closestViruses.push_back(virus);
					break;
				}
			}
		}

		auto check_move_to = [&](bool do_split, ::Point moveto)
		{
			Sandbox env(world, lastSeen);
			env.opponentDummyStrategy = true;
			env.opponentFuseStrategy = world.me.fragments.size() <= 3 || world.tick <= 1000;
			env.viruses = closestViruses;
			env.useVisionMap = foods_count <= 1;
			for (auto &frag : env.opponentFragments)
				frag.isFast = frag.isFast2();
			Move first_move;

			for (int i = 0; i < steps; i++)
			{
				Move mv{ moveto.x, moveto.y };
				mv.split = do_split && i == 0;
				env.move(mv);
				if (i == 0)
					first_move = mv;

				if (!food_mode && i < steps - 1)
					continue;

				auto d = getDanger(world, env, steps, lastSeen);
				if (d < best_danger)
				{
					if (env.opponentFuseStrategy && i == steps - 1)
					{
						Sandbox env2(world, lastSeen);
						env2.opponentDummyStrategy = true;
						env2.opponentFuseStrategy = true;
						env2.viruses = closestViruses;
						env2.useVisionMap = env.useVisionMap;
						bool changed = false;
						for (auto &frag : env2.opponentFragments)
						{
							frag.isFast = frag.isFast2();
							if (frag.ttf == 0)
								frag.ttf = Config::TICKS_TIL_FUSION, changed = true;
						}

						if (changed)
						{
							for (int i = 0; i < steps; i++)
							{
								Move mv{ moveto.x, moveto.y };
								mv.split = do_split && i == 0;
								env2.move(mv);
							}
							auto d2 = getDanger(world, env2, steps, lastSeen);
							if (d2 > d)
							{
								d = d2;
								env = env2;
							}
						}
					}

					if (d < best_danger)
					{
						best_danger = d;
						best_move = first_move;
						best_env = env;
					}
				}
			}
		};

		auto cen = avg(world.me.fragments);
		for (int do_split = 0; do_split <= (int)can_split_any; do_split++)
		{
			for (int angIdx = 0; angIdx < angles; angIdx++)
			{
				double ang = M_PI * 2 / angles * angIdx;
				auto dir = ::Point::byAngle(ang);
				auto moveto = getBorderPoint(cen, dir);
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
			for (auto &opp : world.opponentFragments)
				check_move_to(false, opp);
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