#pragma once
#include "Model/Sandbox.hpp"

struct VisionObserver
{
	void beforeTick(Sandbox &env)
	{
		env.updateVisionMap();

		if (env.tick < ADD_FOOD_DELAY)
		{
			auto coeff = env.me.fragments.size() <= 1 ? VIS_FACTOR : VIS_FACTOR_FR * sqrt(1.0 * env.me.fragments.size());

			for (int flip_x = 0; flip_x <= 1; flip_x++)
			{
				for (int flip_y = 0; flip_y <= 1; flip_y++)
				{
					if (!flip_x && !flip_y)
						continue;

					for (const auto &frag : env.me.fragments)
					{
						auto visionCenter = frag.getVisionCenter();

						if (flip_x)
							visionCenter.x = Config::MAP_SIZE - visionCenter.x;

						if (flip_y)
							visionCenter.y = Config::MAP_SIZE - visionCenter.y;

						for (int i = 0; i <= VISION_GRID_SIZE; i++)
							for (int j = 0; j <= VISION_GRID_SIZE; j++)
								if (visionCenter.getDistanceTo2(1.0 * Config::MAP_SIZE / VISION_GRID_SIZE * i, 1.0 * Config::MAP_SIZE / VISION_GRID_SIZE * j) <= sqr(frag.radius*coeff))
									env.lastSeen[i][j] = env.tick;
					}
				}
			}
		}
	}
};