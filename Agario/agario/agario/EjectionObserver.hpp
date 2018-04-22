#pragma once
#include "Model/World.hpp"

struct EjectionInfo
{
	Ejection ejection;
	int lastSeenTick;
};

struct EjectionObserver
{
	map<int, EjectionInfo> ejections;

	void beforeTick(World &world)
	{
		for (auto &p : ejections)
			p.second.ejection.move();

		map<int, EjectionInfo> newMap;
		for (auto &ej : world.ejections)
		{
			auto prev_it = ejections.find(ej.id);
			if (prev_it != ejections.end())
			{
				auto &prev_ej = prev_it->second.ejection;
				if (prev_ej == ej)
					ej.speed = prev_ej.speed;
				else
					LOG("ejection coordinates mismatch");
			}

			newMap[ej.id] = { ej, world.tick };
		}
	

		for (auto &p : ejections)
		{
			if (newMap.count(p.first))
				continue;

			auto &ej_info = p.second;
			auto &ej = ej_info.ejection;
			
			if (world.me.isUnitVisible(ej)) // в предполагаемой точке его нет, значит его съели
				continue;

			newMap[ej.id] = { ej, ej_info.lastSeenTick };
		}

		ejections.swap(newMap);
	}
};