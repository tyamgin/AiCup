package com.google.inject.internal.cglib.reflect;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class $FastMethod
  extends .FastMember
{
  $FastMethod(.FastClass paramFastClass, Method paramMethod)
  {
    super(paramFastClass, paramMethod, helper(paramFastClass, paramMethod));
  }
  
  private static int helper(.FastClass paramFastClass, Method paramMethod)
  {
    int i = paramFastClass.getIndex(paramMethod.getName(), paramMethod.getParameterTypes());
    if (i < 0)
    {
      Class[] arrayOfClass = paramMethod.getParameterTypes();
      System.err.println("hash=" + paramMethod.getName().hashCode() + " size=" + arrayOfClass.length);
      for (int j = 0; j < arrayOfClass.length; j++) {
        System.err.println("  types[" + j + "]=" + arrayOfClass[j].getName());
      }
      throw new IllegalArgumentException("Cannot find method " + paramMethod);
    }
    return i;
  }
  
  public Class getReturnType()
  {
    return ((Method)this.member).getReturnType();
  }
  
  public Class[] getParameterTypes()
  {
    return ((Method)this.member).getParameterTypes();
  }
  
  public Class[] getExceptionTypes()
  {
    return ((Method)this.member).getExceptionTypes();
  }
  
  public Object invoke(Object paramObject, Object[] paramArrayOfObject)
    throws InvocationTargetException
  {
    return this.fc.invoke(this.index, paramObject, paramArrayOfObject);
  }
  
  public Method getJavaMethod()
  {
    return (Method)this.member;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\reflect\$FastMethod.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */