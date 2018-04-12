#pragma once

#include <math.h>

template<typename T>
inline T sqr(T x)
{
	return x * x;
}

inline bool isWhole(double x, double eps = 1e-9)
{
	return abs(int(x + eps) - x) < eps;
}