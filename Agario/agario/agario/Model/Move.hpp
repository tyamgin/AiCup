#pragma once

#include "../nlohmann/json.hpp"

struct Move
{
	double x, y;
	string debug;

	nlohmann::json toJson()
	{
		nlohmann::json result = { { "X", x }, { "Y", y } };
		if (debug.size())
			result["Debug"] = debug;
		return result;
	}
};
