package com.google.common.eventbus;

import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class EventHandler
{
  private final Object target;
  private final Method method;
  
  EventHandler(Object paramObject, Method paramMethod)
  {
    Preconditions.checkNotNull(paramObject, "EventHandler target cannot be null.");
    Preconditions.checkNotNull(paramMethod, "EventHandler method cannot be null.");
    this.target = paramObject;
    this.method = paramMethod;
    paramMethod.setAccessible(true);
  }
  
  public void handleEvent(Object paramObject)
    throws InvocationTargetException
  {
    Preconditions.checkNotNull(paramObject);
    try
    {
      this.method.invoke(this.target, new Object[] { paramObject });
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new Error("Method rejected target/argument: " + paramObject, localIllegalArgumentException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new Error("Method became inaccessible: " + paramObject, localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      if ((localInvocationTargetException.getCause() instanceof Error)) {
        throw ((Error)localInvocationTargetException.getCause());
      }
      throw localInvocationTargetException;
    }
  }
  
  public String toString()
  {
    return "[wrapper " + this.method + "]";
  }
  
  public int hashCode()
  {
    int i = 31;
    return (31 + this.method.hashCode()) * 31 + System.identityHashCode(this.target);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof EventHandler))
    {
      EventHandler localEventHandler = (EventHandler)paramObject;
      return (this.target == localEventHandler.target) && (this.method.equals(localEventHandler.method));
    }
    return false;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\eventbus\EventHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */