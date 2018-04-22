#pragma once

#include "Model/Sandbox.hpp"

#define FOOD_EXPIRATION_TICKS 350
#define EJECTION_EXPIRATION_TICKS 450

#define M_USE_FAST_EXP false
#define M_USE_FAST_SQRT false

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
#if M_USE_FAST_EXP
		return a*exp256(-x*b);
#else
		return a*exp(-x*b);
#endif
	}
};

#if M_USE_FAST_SQRT
#define EXP_SCORE(fn, a, b) (fn)(fsqrt((a).getDistanceTo2((b))));
#else
#define EXP_SCORE(fn, a, b) (fn)((a).getDistanceTo((b)));
#endif

bool isDangerFood(const Food &food)
{
	return Config::MAP_CENTER.getDistanceTo2(food) >= sqr(Config::MAP_SIZE*M_SAFE_RAD_FACTOR + FOOD_RADIUS);
}

bool isDangerEjection(const Ejection &ej)
{
	return Config::MAP_CENTER.getDistanceTo2(ej) >= sqr(Config::MAP_SIZE*M_SAFE_RAD_FACTOR + EJECT_RADIUS);
}

template<typename Collection>
double progressiveScore(const Collection &my_fragments, const Collection &opp_fragments, const Exponenter &dist_exp, double additional_mass)
{
	double res = 0;
	for (auto &opp : opp_fragments)
	{
		static double scores[128];
		int size = 0;
		for (auto &frag : my_fragments)
			if (frag.canEat(opp, additional_mass))
				scores[size++] = EXP_SCORE(dist_exp, frag, opp);
				
		sort(scores, scores + size, greater<double>());
		double score = 0, pw = 1;
		for (int i = 0; i < size; i++)
			score += scores[i] * pw, pw *= 0.5;
		res += score;
	}
	return res;
}

double getDanger(
	const vector<FoodInfo> &safe_invisible_foods, 
	const vector<EjectionInfo> &safe_invisible_ejections,
	const Sandbox &startEnv, 
	const Sandbox &env, 
	int interval)
{
	OP_START(DANGER_STRATEGY);

	OP_START(DANGER_STRATEGY_1);

	double foodScore = 15;
	Exponenter foodExp(1, foodScore * 0.5, Config::MAP_SIZE / 4.0, 0.3);

	double ejectScore = 40;
	Exponenter ejectExp(1, ejectScore * 0.5, Config::MAP_SIZE / 4.0, 0.3);

	double res = 0;
	for (auto &eatenFoodEvent : env.eatenFoodEvents)
		res -= foodScore * (interval - (eatenFoodEvent.tick - startEnv.tick)) / interval;
	for (auto &eatenEjectionEvent : env.eatenEjectionEvents)
		res -= ejectScore * (interval - (eatenEjectionEvent.tick - startEnv.tick)) / interval;

	double food_sum = 0;
	for (auto &food : env.foods)
		if (!isDangerFood(food))
			for (auto &frag : env.me.fragments)
				food_sum += EXP_SCORE(foodExp, frag, food);
	for (auto &food_info : safe_invisible_foods)
	{
		auto t = startEnv.tick - food_info.lastSeenTick;
		auto &food = food_info.food;
		
		for (auto &frag : env.me.fragments)
		{
			auto e = EXP_SCORE(foodExp, frag, food);
			food_sum += e * (FOOD_EXPIRATION_TICKS - t) / (double)FOOD_EXPIRATION_TICKS;
		}
	}

	if (env.me.fragments.size())
		res -= food_sum / env.me.fragments.size();

	double ej_sum = 0;
	for (auto &ej : env.ejections)
		if (!isDangerEjection(ej))
			for (auto &frag : env.me.fragments)
				ej_sum += EXP_SCORE(ejectExp, frag, ej);
	for (auto &ej_info : safe_invisible_ejections)
	{
		auto t = startEnv.tick - ej_info.lastSeenTick;
		auto &ej = ej_info.ejection;
		
		for (auto &frag : env.me.fragments)
		{
			auto e = EXP_SCORE(ejectExp, frag, ej);
			ej_sum += e * (FOOD_EXPIRATION_TICKS - t) / (double)EJECTION_EXPIRATION_TICKS;
		}
	}
	res -= ej_sum;

	OP_END(DANGER_STRATEGY_1);

	OP_START(DANGER_STRATEGY_2);

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

	OP_END(DANGER_STRATEGY_2);

	if (env.tick - startEnv.tick == interval && env.useVisionMap && env.me.fragments.size() > 0)
	{
		OP_START(DANGER_STRATEGY_3);

		int vis_sum = 0;
		for (int i = 0; i <= VISION_GRID_SIZE; i++)
			for (int j = 0; j <= VISION_GRID_SIZE; j++)
				vis_sum += min(500, max(0, env.lastSeen[i][j] - startEnv.lastSeen[i][j]));

		double sumArea = 0;
		for (auto &frag : env.me.fragments)
			sumArea += frag.radius*frag.radius*M_PI;
		
		res -= vis_sum / sumArea / 10000;

		OP_END(DANGER_STRATEGY_3);
	}

	res += env.ejectEvents.size() * 11;

	OP_END(DANGER_STRATEGY);

	if (res != res || res <= -INFINITY || res >= INFINITY)
	{
		LOG("danger is not finite");
		exit(0);
	}

	return res;
}