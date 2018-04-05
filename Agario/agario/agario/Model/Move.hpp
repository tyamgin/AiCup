#pragma once

#include "../nlohmann/json.hpp"
#include <string>
using namespace std;

struct Move
{
	double x, y;
	string debug;
	bool split;
	bool eject;

	nlohmann::json toJson()
	{
		nlohmann::json result = { { "X", x }, { "Y", y } };
		if (debug.size())
			result["Debug"] = debug;
		if (split)
			result["Split"] = true;
		if (eject)
			result["Eject"] = true;
		return result;
	}
};

struct MoveFactory
{
	static Move eject()
	{
		auto move = Move();
		move.eject = true;
		return move;
	}

	static Move split()
	{
		auto move = Move();
		move.split = true;
		return move;
	}
};
