#pragma once

#include "Model/Sandbox.hpp"



int x_vis_min[VISION_GRID_SIZE + 1];
int x_vis_max[VISION_GRID_SIZE + 1];

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

double getDanger(const World &startEnv, const Sandbox &env, int interval, int lastSeen[][VISION_GRID_SIZE + 1])
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

	int itemsCount = 0;
	for (auto &food : env.foods)
	{
		if (Config::MAP_CENTER.getDistanceTo2(food) < sqr(Config::MAP_SIZE*M_SAFE_RAD_FACTOR + FOOD_RADIUS))
		{
			for (auto &frag : env.me.fragments)
			{
				auto dst = frag.getDistanceTo(food);
				auto e = foodExp(dst);

				res -= e / env.me.fragments.size();
			}
		}
	}
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
		//memset(x_vis_min, 63, sizeof(x_vis_min));
		//memset(x_vis_max, 0, sizeof(x_vis_max));

		//auto vis_d = Config::MAP_SIZE / VISION_GRID_SIZE;
		//double vis_sum = 0;
		//int vis_cells = 0;
		//for (auto &frag : env.me.fragments)
		//{
		//	auto vis = frag.getVisionCenter();
		//	auto vis_rad = frag.radius * (env.me.fragments.size() <= 1 ? VIS_FACTOR : VIS_FACTOR_FR * sqrt(1.0 * env.me.fragments.size()));

		//	double x, y;
		//	for (int j = max(0, int((vis.y - vis_rad) / vis_d - EPS) + 1);
		//		j <= VISION_GRID_SIZE && (y = j * vis_d) <= vis.y + vis_rad;
		//		j++)
		//	{
		//		auto sqrt_val = vis_rad*vis_rad - (y - vis.y)*(y - vis.y);
		//		if (sqrt_val < 0)
		//			continue;
		//		sqrt_val = sqrt(sqrt_val);
		//		x_vis_min[j] = min(x_vis_min[j], int((vis.x - sqrt_val) / vis_d - EPS) + 1);
		//		x_vis_max[j] = max(x_vis_max[j], int((vis.x + sqrt_val) / vis_d + EPS));
		//	}
		//}
	
		//vector<tuple<int, int, int> > pts;
		//for (int j = 0; j <= VISION_GRID_SIZE; j++)
		//{
		//	for (int i = max(0, x_vis_min[j]); i <= VISION_GRID_SIZE && i <= x_vis_max[j]; i++)
		//	{
		//		vis_cells++;
		//		auto v = env.tick - interval - lastSeen[i][j] - 30;
		//		if (v < 0)
		//			v = 0;
		//		else if (v > 500)
		//			v = 500;

		//		vis_sum += v;
		//		// TODO: не учитываются пробелы
		//		pts.push_back({ i, j, v });
		//	}
		//}
		int vis_sum = 0;
		for (int i = 0; i <= VISION_GRID_SIZE; i++)
			for (int j = 0; j <= VISION_GRID_SIZE; j++)
				vis_sum += env.tick - min(500, max(0, env.tick - (env.lastSeen[i][j] - lastSeen[i][j])));

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

	return res;
}