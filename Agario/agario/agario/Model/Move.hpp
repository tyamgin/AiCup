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

	explicit Move(const nlohmann::json &obj) : ::Point(obj)
	{
		if (obj.count("Split"))
			split = obj["Split"].get<bool>();
		if (obj.count("Eject"))
			eject = obj["Eject"].get<bool>();
		if (obj.count("Debug"))
			debug = obj["Debug"].get<string>();
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

	bool operator ==(const Move &other) const
	{
		return ::Point::operator==(other) && split == other.split && eject == other.eject; // NOTE: no need to compare "debug"
	}

	bool operator !=(const Move &other) const
	{
		return !operator==(other);
	}
};
