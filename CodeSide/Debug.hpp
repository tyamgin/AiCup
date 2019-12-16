#ifndef _DEBUG_HPP_
#define _DEBUG_HPP_

#include "Stream.hpp"
#include "model/CustomData.hpp"
#include "model/PlayerMessageGame.hpp"
#include <memory>

class Debug {
public:
  Debug(const std::shared_ptr<OutputStream> &outputStream) : outputStream(outputStream) {
  }
  void draw(const CustomData &customData) {
#ifdef DEBUG
    outputStream->write(PlayerMessageGame::CustomDataMessage::TAG);
    customData.writeTo(*outputStream);
    outputStream->flush();
#endif
  }

private:
  std::shared_ptr<OutputStream> outputStream;
};

#endif