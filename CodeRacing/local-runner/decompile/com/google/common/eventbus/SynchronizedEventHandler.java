package com.google.common.eventbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class SynchronizedEventHandler
  extends EventHandler
{
  public SynchronizedEventHandler(Object paramObject, Method paramMethod)
  {
    super(paramObject, paramMethod);
  }
  
  public synchronized void handleEvent(Object paramObject)
    throws InvocationTargetException
  {
    super.handleEvent(paramObject);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\eventbus\SynchronizedEventHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */