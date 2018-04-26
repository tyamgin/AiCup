#include "Model/World.hpp"
#include "Model/Sandbox.hpp"
#include "SpeedObserver.hpp"
#include "FoodObserver.hpp"
#include "EjectionObserver.hpp"
#include "VisionObserver.hpp"
#include "DangerStrategy.hpp"
#include "Utility/Logger.hpp"

#if M_VISUAL
#include "Visualizer/Visualizer.hpp"
#endif

struct MyStrategy
{
	SpeedObserver speedObserver;
	FoodObserver foodObserver;
	EjectionObserver ejectionObserver;
	VisionObserver visionObserver;
	Sandbox environment;
	vector<FoodInfo> foods;
	vector<EjectionInfo> ejections;

	Move onTick(World world, const Move &debug_real_move)
	{
		environment = Sandbox(world, environment.lastSeen);
		speedObserver.beforeTick(environment);
		foodObserver.beforeTick(environment);
		ejectionObserver.beforeTick(environment);
		visionObserver.beforeTick(environment);

		foods.clear();
		for (auto &p : foodObserver.foods)
			for (auto &fi : p.second)
				if (world.tick - fi.lastSeenTick < FOOD_EXPIRATION_TICKS)
					foods.emplace_back(fi);
		ejections.clear();
		for (auto &p : ejectionObserver.ejections)
			if (world.tick - p.second.lastSeenTick < EJECTION_EXPIRATION_TICKS)
				ejections.emplace_back(p.second);

#if M_VISUAL
		Visualizer::update(world, foods, ejections);
		System::Threading::Thread::Sleep(2);
#endif
		TIMER_START();

		foods.erase(remove_if(foods.begin(), foods.end(), [&world](const FoodInfo &x)
		{
			return x.lastSeenTick == world.tick || isDangerFood(x.food);
		}), foods.end());

		ejections.erase(remove_if(ejections.begin(), ejections.end(), [&world](const EjectionInfo &x)
		{
			return x.lastSeenTick == world.tick || isDangerEjection(x.ejection);
		}), ejections.end());

		auto res = _onTick(environment);
		assert(!res.split || !res.eject);
		
		environment.forbidEating = true;
		environment.move(res);
		environment.forbidEating = false;
		speedObserver.afterTick(environment);

		TIMER_ENG_LOG("all");
		return res;
	}

	Move _onTick(const Sandbox &world)
	{
		if (world.me.fragments.empty())
			return Move();

		bool allow_partial = true;
		for (auto &oppFr : world.opponentFragments)
		{
			double addMass = 0;
			for (auto &opp2 : world.opponentFragments)
				if (opp2.playerId == oppFr.playerId && opp2.fragmentId != oppFr.fragmentId && oppFr.ttf <= 20 && opp2.ttf <= 20)
					addMass += opp2.mass;

			for (auto &myFr : world.me.fragments)
				if (oppFr.canEat(myFr, addMass + Config::FOOD_MASS) || myFr.canEat(oppFr))
					allow_partial = false;
		}

		return _doPP(world, allow_partial);
	}


	Move _doPP(const Sandbox &world, bool allow_partial)
	{
		TIMER_START();

		int steps = 15;
		int angles = 24;
		int angles2 = 12;
		if (world.me.fragments.size() > 5)
		{
			steps = 12;
			angles = angles2;
		}

		Move best_move;
		double best_danger = INT_MAX;
		Sandbox best_env = world;
		double need_try_eject = false;
		
		bool can_split_any = false;
		for (auto &frag : world.me.fragments)
			can_split_any |= frag.canSplit((int)world.me.fragments.size());

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

		auto check_move_to = [&](bool do_split, int do_eject_count, ::Point moveto)
		{
			Sandbox env = world;
			env.opponentDummyStrategy = true;
			env.viruses = closestViruses;
			env.useVisionMap = foods_count <= 1;
			for (auto &frag : env.opponentFragments)
				frag.isFast = frag.isFast2();
			Move first_move;

			auto try_strategy = [&](double &d, Sandbox &env2)
			{
				for (int i = 0; i < steps; i++)
				{
					Move mv{ moveto.x, moveto.y };
					mv.split = do_split && i == 0;
					mv.eject = i < do_eject_count;
					env2.move(mv);
				}
				auto d2 = getDanger(foods, ejections, world, env2, steps);
				if (d2 > d)
				{
					d = d2;
					env = env2;
				}
			};

			for (int i = 0; i < steps; i++)
			{
				Move mv{ moveto.x, moveto.y };
				mv.split = do_split && i == 0;
				mv.eject = i < do_eject_count;
				env.move(mv);
				if (i == 0)
					first_move = mv;

				if (!allow_partial && i < steps - 1)
					continue;

				auto d = getDanger(foods, ejections, world, env, steps);
				if (d < best_danger)
				{
					if (env.opponentFuseStrategy && i == steps - 1)
					{
						Sandbox env2 = world;
						env2.opponentDummyStrategy = true;
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
							try_strategy(d, env2);


						int can_fuse_count = 0;
						for (auto &opp : world.opponentFragments)
							can_fuse_count += opp.ttf <= 20;

						if (d < best_danger && can_fuse_count > 1)
						{
							Sandbox env3 = world;
							env3.opponentDummyStrategy = true;
							env3.viruses = closestViruses;
							env3.useVisionMap = env.useVisionMap;
							env3.opponentForseFuseStrategy = true;
							try_strategy(d, env3);
						}

						if (d < best_danger && can_fuse_count > 1)
						{
							Sandbox env3 = world;
							env3.opponentDummyStrategy = true;
							env3.viruses = closestViruses;
							env3.useVisionMap = env.useVisionMap;
							env3.opponentForseFuseStrategy2 = true;
							try_strategy(d, env3);
						}
					}
				}

				if (d < best_danger)
				{
					best_danger = d;
					best_move = first_move;
					need_try_eject = !env.lostFragmentEvents.empty();
					best_env = env;
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
				check_move_to(!!do_split, 0, moveto);
			}
		}
		
		if (world.me.fragments.size() > 1)
		{
			auto rect = getBoundingRect(world.me.fragments);
			const int side = 3;
			for (int i = 0; i < side; i++)
				for (int j = 0; j < side; j++)
					check_move_to(false, 0, ::Point(rect.x1 + rect.width() / (side - 1) * i, rect.y1 + rect.height() / (side - 1) * j));
			for (auto &opp : world.opponentFragments)
				check_move_to(false, 0, opp);
		}

		if (need_try_eject && world.me.fragments.size() <= 2)
		{
			angles *= 2;
			for (int ejects_count = 1; ejects_count <= 7; ejects_count++)
			{
				for (int angIdx = 0; angIdx < angles; angIdx++)
				{
					double ang = M_PI * 2 / angles * angIdx;
					auto dir = ::Point::byAngle(ang);
					auto moveto = getBorderPoint(cen, dir);
					check_move_to(false, ejects_count, moveto);
				}
			}
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