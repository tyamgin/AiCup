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
	
	double operator ()(double x)
	{
		return a*exp(-x*b);
	};
};

double getDanger(const Sandbox &startEnv, const Sandbox &env, int interval)
{
	double foodScore = 15;
	Exponenter foodExp(1, foodScore * 0.5, Config::MAP_SIZE / 4.0, 0.3);

	double res = 0;
	for (auto eatenFoodTick : env.eatenFoodTicks)
		res -= foodScore * (interval - (eatenFoodTick - startEnv.tick)) / interval;

	for (auto &food : env.foods)
	{
		for (auto &frag : env.me.fragments)
		{
			auto dst = frag.getDistanceTo(food);
			auto e = foodExp(dst);
		
			res -= e / env.me.fragments.size();
		}
	}

	double oppScore = 100;
	Exponenter oppExp(20, oppScore, Config::MAP_SIZE / 4.0, 2);

	for (auto &opp : env.opponentFragments)
	{
		for (auto &frag : env.me.fragments)
		{
			if (opp.canEat(frag))
			{
				auto dst = frag.getDistanceTo(opp);
				res += oppExp(dst);
			}
		}
	}
	return res;
}