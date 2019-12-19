#ifndef _MY_STRATEGY_HPP_
#define _MY_STRATEGY_HPP_

#include "Debug.hpp"
#include "model/CustomData.hpp"
#include "model/Game.hpp"
#include "model/Unit.hpp"
#include "model/UnitAction.hpp"

class MyStrategy {
public:
  MyStrategy();
  ~MyStrategy();
  std::unordered_map<int, UnitAction> getActions(int myId, const Game &game, Debug &debug);
};

#endif