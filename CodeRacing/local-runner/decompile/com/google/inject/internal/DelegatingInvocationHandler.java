package com.google.inject.internal;

import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class DelegatingInvocationHandler
  implements InvocationHandler
{
  private volatile boolean initialized;
  private Object delegate;
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable
  {
    try
    {
      Preconditions.checkState(this.initialized, "This is a proxy used to support circular references. The object we're proxying is not constructed yet. Please wait until after injection has completed to use this object.");
      Preconditions.checkNotNull(this.delegate, "This is a proxy used to support circular references. The object we're  proxying is initialized to null. No methods can be called.");
      return paramMethod.invoke(this.delegate, paramArrayOfObject);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new RuntimeException(localIllegalArgumentException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw localInvocationTargetException.getTargetException();
    }
  }
  
  void setDelegate(Object paramObject)
  {
    this.delegate = paramObject;
    this.initialized = true;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\DelegatingInvocationHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */