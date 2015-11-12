package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@Beta
public final class Reflection
{
  public static String getPackageName(Class paramClass)
  {
    return getPackageName(paramClass.getName());
  }
  
  public static String getPackageName(String paramString)
  {
    int i = paramString.lastIndexOf('.');
    return i < 0 ? "" : paramString.substring(0, i);
  }
  
  public static void initialize(Class... paramVarArgs)
  {
    for (Class localClass : paramVarArgs) {
      try
      {
        Class.forName(localClass.getName(), true, localClass.getClassLoader());
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new AssertionError(localClassNotFoundException);
      }
    }
  }
  
  public static Object newProxy(Class paramClass, InvocationHandler paramInvocationHandler)
  {
    Preconditions.checkNotNull(paramInvocationHandler);
    Preconditions.checkArgument(paramClass.isInterface(), "%s is not an interface", new Object[] { paramClass });
    Object localObject = Proxy.newProxyInstance(paramClass.getClassLoader(), new Class[] { paramClass }, paramInvocationHandler);
    return paramClass.cast(localObject);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\reflect\Reflection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */