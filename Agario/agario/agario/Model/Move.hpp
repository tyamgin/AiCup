#pragma once

#include "../nlohmann/json.hpp"
#include <string>
using namespace std;

struct Move
{
	double x = 0, y = 0;
	string debug;
	bool split = false;
	bool eject = false;

	Move()
	{
	}

	Move(double x, double y) : x(x), y(y)
	{
	}

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
