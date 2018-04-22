#pragma once
#include "Model/Sandbox.hpp"

struct VisionObserver
{
	void beforeTick(Sandbox &env)
	{
		env.updateVisionMap();
	}
};