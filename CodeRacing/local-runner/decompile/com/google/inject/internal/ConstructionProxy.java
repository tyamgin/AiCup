package com.google.inject.internal;

import com.google.common.collect.ImmutableMap;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

abstract interface ConstructionProxy
{
  public abstract Object newInstance(Object... paramVarArgs)
    throws InvocationTargetException;
  
  public abstract InjectionPoint getInjectionPoint();
  
  public abstract Constructor getConstructor();
  
  public abstract ImmutableMap getMethodInterceptors();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ConstructionProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */