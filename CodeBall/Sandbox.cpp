#include "Sandbox.h"

std::vector<ABall> Sandbox::_ballsCache;
uint64_t Sandbox::oppMask = 0;
uint64_t Sandbox::myOppMask[7];
uint64_t Sandbox::myAnyMask[7];