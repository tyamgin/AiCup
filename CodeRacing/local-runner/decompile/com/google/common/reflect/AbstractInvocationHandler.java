package com.google.common.reflect;

import com.google.common.annotations.Beta;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Beta
public abstract class AbstractInvocationHandler
  implements InvocationHandler
{
  private static final Object[] NO_ARGS = new Object[0];
  
  public final Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable
  {
    if (paramArrayOfObject == null) {
      paramArrayOfObject = NO_ARGS;
    }
    if ((paramArrayOfObject.length == 0) && (paramMethod.getName().equals("hashCode"))) {
      return Integer.valueOf(hashCode());
    }
    if ((paramArrayOfObject.length == 1) && (paramMethod.getName().equals("equals")) && (paramMethod.getParameterTypes()[0] == Object.class))
    {
      Object localObject = paramArrayOfObject[0];
      return Boolean.valueOf((paramObject.getClass().isInstance(localObject)) && (equals(Proxy.getInvocationHandler(localObject))));
    }
    if ((paramArrayOfObject.length == 0) && (paramMethod.getName().equals("toString"))) {
      return toString();
    }
    return handleInvocation(paramObject, paramMethod, paramArrayOfObject);
  }
  
  protected abstract Object handleInvocation(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable;
  
  public boolean equals(Object paramObject)
  {
    return super.equals(paramObject);
  }
  
  public int hashCode()
  {
    return super.hashCode();
  }
  
  public String toString()
  {
    return super.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\reflect\AbstractInvocationHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */