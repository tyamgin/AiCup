#pragma once

#include "../nlohmann/json.hpp"
#include <string>
using namespace std;

struct Move : ::Point
{
	string debug;
	bool split = false;
	bool eject = false;

	Move()
	{
		x = y = 0;
	}

	Move(double x, double y) : ::Point(x, y)
	{
	}

	Move(const ::Point &p) : ::Point(p)
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
