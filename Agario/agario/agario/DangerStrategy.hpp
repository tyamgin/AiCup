#pragma once

#include "Model/Sandbox.hpp"

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

double getDanger(const Sandbox &startEnv, const Sandbox &env, int interval)
{
	double foodScore = 15;
	Exponenter foodExp(1, foodScore * 0.5, Config::MAP_SIZE / 4.0, 0.3);

	double ejectScore = 40;
	Exponenter ejectExp(1, ejectScore * 0.5, Config::MAP_SIZE / 4.0, 0.3);

	double res = 0;
	for (auto &eatenFoodEvent : env.eatenFoodEvents)
		res -= foodScore * (interval - (eatenFoodEvent.tick - startEnv.tick)) / interval;
	for (auto &eatenEjectionEvent : env.eatenEjectionEvents)
		res -= ejectScore * (interval - (eatenEjectionEvent.tick - startEnv.tick)) / interval;

	for (auto &food : env.foods)
	{
		for (auto &frag : env.me.fragments)
		{
			auto dst = frag.getDistanceTo(food);
			auto e = foodExp(dst);
		
			res -= e / env.me.fragments.size();
		}
	}
	for (auto &ej : env.ejections)
	{
		for (auto &frag : env.me.fragments)
		{
			auto dst = frag.getDistanceTo(ej);
			auto e = ejectExp(dst);

			res -= e / env.me.fragments.size();
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

	return res;
}