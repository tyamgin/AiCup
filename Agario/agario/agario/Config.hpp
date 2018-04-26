#pragma once
#include "nlohmann/json.hpp"

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

	static double FOOD_RADIUS;
	static double EJECTION_RADIUS;
	static double EJECTION_MASS;

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
	}
};