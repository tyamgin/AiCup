#pragma once
#include "nlohmann/json.hpp"
#include "Model/Point.hpp"

#define FOOD_RADIUS 2.5
#define MIN_SPLIT_MASS 120
#define RAD_HURT_FACTOR (2.0/3)
#define MIN_BURST_MASS 60
#define VIS_FACTOR 4.0  // vision = radius * VF
#define VIS_FACTOR_FR 2.5 // vision = radius * VFF * qSqrt(fragments.count())
#define RADIUS_FACTOR 2.0
#define SPLIT_START_SPEED 9.0
#define COLLISION_POWER 20.0
#define MIN_EJECT_MASS 40.0
#define EJECT_START_SPEED 8.0
#define EJECT_RADIUS 4.0
#define EJECT_MASS 15.0
#define MIN_SHRINK_MASS 100
#define SHRINK_FACTOR 0.01
#define SHRINK_EVERY_TICK 50
#define BURST_BONUS 5.0
#define BURST_START_SPEED 8.0
#define BURST_ANGLE_SPECTRUM M_PI
#define MASS_EAT_FACTOR 1.2
#define ADD_FOOD_DELAY 40

struct Config
{
	static int MAP_SIZE;
	static int GAME_TICKS;
	static double FOOD_MASS;
	static double INERTION_FACTOR;
	static int MAX_FRAGS_CNT;
	static double SPEED_FACTOR;
	static int TICKS_TIL_FUSION;
	static double VIRUS_RADIUS;
	static double VIRUS_SPLIT_MASS;
	static double VISCOSITY;

	static ::Point MAP_CENTER;

	static void parse(const nlohmann::json &config_json)
	{
		auto game_height = config_json["GAME_HEIGHT"].get<int>();
		auto game_width = config_json["GAME_WIDTH"].get<int>();
		assert(game_height == game_width);
		Config::MAP_SIZE = game_height;
		Config::FOOD_MASS = config_json["FOOD_MASS"].get<double>();
		Config::GAME_TICKS = config_json["GAME_TICKS"].get<int>();
		Config::INERTION_FACTOR = config_json["INERTION_FACTOR"].get<double>();
		Config::MAX_FRAGS_CNT = config_json["MAX_FRAGS_CNT"].get<int>();
		Config::SPEED_FACTOR = config_json["SPEED_FACTOR"].get<double>();
		Config::TICKS_TIL_FUSION = config_json["TICKS_TIL_FUSION"].get<int>();
		Config::VIRUS_RADIUS = config_json["VIRUS_RADIUS"].get<double>();
		Config::VIRUS_SPLIT_MASS = config_json["VIRUS_SPLIT_MASS"].get<double>();
		Config::VISCOSITY = config_json["VISCOSITY"].get<double>();

		Config::MAP_CENTER.x = Config::MAP_CENTER.y = Config::MAP_SIZE / 2;
	}
};

#define M_SAFE_RAD_FACTOR (0.60)