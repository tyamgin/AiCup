package com.google.inject.internal;

import com.google.inject.spi.Message;

abstract interface ErrorHandler
{
  public abstract void handle(Object paramObject, Errors paramErrors);
  
  public abstract void handle(Message paramMessage);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ErrorHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */