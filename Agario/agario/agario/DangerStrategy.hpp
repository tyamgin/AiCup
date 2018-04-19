#pragma once

#include "Model/Sandbox.hpp"

#define FOOD_EXPIRATION_TICKS 200

struct Exponenter
{
	double x1, y1, x2, y2, a, b;

	Exponenter(double x1, double y1, double x2, double y2) : x1(x1), y1(y1), x2(x2), y2(y2)
	{
		b = log(y1 / y2) / (x2 - x1);
		a = y1 / exp(-b*x1);
	}
	
	double operator ()(double x) const
	{
		return a*exp(-x*b);
	};
};

template<typename Collection>
double progressiveScore(const Collection &my_fragments, const Collection &opp_fragments, const Exponenter &dist_exp, double additional_mass)
{
	double res = 0;
	for (auto &opp : opp_fragments)
	{
		vector<double> scores;
		for (auto &frag : my_fragments)
		{
			if (frag.canEat(opp, additional_mass))
			{
				auto dst = frag.getDistanceTo(opp);
				scores.push_back(dist_exp(dst));
			}
		}
		sort(scores.begin(), scores.end(), greater<double>());
		double score = 0, pw = 1;
		for (auto x : scores)
			score += x * pw, pw *= 0.5;
		res += score;
	}
	return res;
}

double getDanger(const vector<FoodInfo> &foods, const World &startEnv, const Sandbox &env, int interval, int lastSeen[][VISION_GRID_SIZE + 1])
{
	OP_START(DANGER_STRATEGY);

	double foodScore = 15;
	Exponenter foodExp(1, foodScore * 0.5, Config::MAP_SIZE / 4.0, 0.3);

	double ejectScore = 40;
	Exponenter ejectExp(1, ejectScore * 0.5, Config::MAP_SIZE / 4.0, 0.3);

	double res = 0;
	for (auto &eatenFoodEvent : env.eatenFoodEvents)
		res -= foodScore * (interval - (eatenFoodEvent.tick - startEnv.tick)) / interval;
	for (auto &eatenEjectionEvent : env.eatenEjectionEvents)
		res -= ejectScore * (interval - (eatenEjectionEvent.tick - startEnv.tick)) / interval;

	int itemsCount = 0;
	double food_sum = 0;
	for (auto &food_info : foods)
	{
		auto &food = food_info.food;
		if (Config::MAP_CENTER.getDistanceTo2(food) < sqr(Config::MAP_SIZE*M_SAFE_RAD_FACTOR + FOOD_RADIUS))
		{
			for (auto &frag : env.me.fragments)
			{
				auto dst = frag.getDistanceTo(food);
				auto e = foodExp(dst);

				auto t = startEnv.tick - food_info.lastSeenTick;

				food_sum += e * (FOOD_EXPIRATION_TICKS - t) / (double)FOOD_EXPIRATION_TICKS;
			}
		}
	}
	if (env.me.fragments.size())
		res -= food_sum / env.me.fragments.size();

	for (auto &ej : env.ejections)
	{
		if (Config::MAP_CENTER.getDistanceTo2(ej) < sqr(Config::MAP_SIZE*M_SAFE_RAD_FACTOR + EJECT_RADIUS))
		{
			for (auto &frag : env.me.fragments)
			{
				auto dst = frag.getDistanceTo(ej);
				auto e = ejectExp(dst);

				res -= e / env.me.fragments.size();
			}
		}
	}

	double oppScore = 120;
	Exponenter oppExp(20, oppScore, Config::MAP_SIZE / 4.0, 2);

	res -= progressiveScore(env.me.fragments, env.opponentFragments, oppExp, 0);
	res += progressiveScore(env.opponentFragments, env.me.fragments, oppExp, Config::FOOD_MASS);

	res -= env.eatenFragmentEvents.size() * oppScore * 2;
	res += env.lostFragmentEvents.size() * oppScore * 2;

	auto safe_r = Config::MAP_SIZE * M_SAFE_RAD_FACTOR;
	auto max_r = Config::MAP_SIZE / 2 * M_SQRT2;
	for (auto &frag : env.me.fragments)
	{
		auto dst = frag.getDistanceTo(Config::MAP_CENTER);
		if (dst > safe_r)
			res += (dst - safe_r) / (max_r - safe_r) * 35;
	}

	if (env.tick - startEnv.tick == interval && env.useVisionMap && env.me.fragments.size() > 0)
	{
		int vis_sum = 0;
		for (int i = 0; i <= VISION_GRID_SIZE; i++)
			for (int j = 0; j <= VISION_GRID_SIZE; j++)
				vis_sum += min(500, max(0, env.lastSeen[i][j] - lastSeen[i][j]));

		double sumArea = 0;
		for (auto &frag : env.me.fragments)
			sumArea += frag.radius*frag.radius*M_PI;
		
		res -= vis_sum / sumArea / 10000;
	}

	if (res != res || res <= -INFINITY || res >= INFINITY)
	{
		LOG("danger is not finite");
		exit(0);
	}

	OP_END(DANGER_STRATEGY);

	return res;
}