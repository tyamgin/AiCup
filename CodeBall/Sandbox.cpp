#include "Sandbox.h"

std::vector<ABall> Sandbox::_ballsCache;
std::vector<AAction> Sandbox::_actionsCache[7];
uint64_t Sandbox::oppMask = 0;
uint64_t Sandbox::myOppMask[7];
uint64_t Sandbox::myAnyMask[7];