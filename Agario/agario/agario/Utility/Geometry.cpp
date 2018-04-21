#include "Geometry.hpp"
#include <limits>
#include <cinttypes>
#include <cstring>

float Q_rsqrt(float number)
{
	static_assert(std::numeric_limits<float>::is_iec559,
		"fast inverse square root requires IEEE-comliant 'float'");
	static_assert(sizeof(float) == sizeof(std::uint32_t),
		"fast inverse square root requires 'float' to be 32-bit");
	float x2 = number * 0.5F, y = number;
	std::uint32_t i;
	memcpy(&i, &y, sizeof(float));
	i = 0x5f3759df - (i >> 1);
	memcpy(&y, &i, sizeof(float));
	return y * (1.5F - (x2 * y * y));
}

double fsqrt(double x)
{
	return 1 / Q_rsqrt((float) x);
}

#define EXP256_MIN_X (-22)
#define EXP256_MIN_VAL (2.7894680928689246e-10) // exp(EXP256_MIN_X)
#define EXP256_LINEAR_APPROX(x) (EXP256_MIN_VAL/(EXP256_MIN_X - (x) + 1))

double exp256(double x)
{
	if (x <= EXP256_MIN_X)
		return EXP256_LINEAR_APPROX(x);
	if (x > 5)
		return exp(x);

	double x1 = x;

	x = 1.0 + x / 256.0;
	x *= x; x *= x; x *= x; x *= x;
	if (x < EXP256_MIN_VAL)
		return EXP256_LINEAR_APPROX(x1);
	x *= x; x *= x; x *= x; x *= x;
	if (x < EXP256_MIN_VAL)
		return EXP256_LINEAR_APPROX(x1);
	return x;
}