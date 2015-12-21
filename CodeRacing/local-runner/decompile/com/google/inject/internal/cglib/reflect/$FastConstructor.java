package com.google.inject.internal.cglib.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class $FastConstructor
  extends .FastMember
{
  $FastConstructor(.FastClass paramFastClass, Constructor paramConstructor)
  {
    super(paramFastClass, paramConstructor, paramFastClass.getIndex(paramConstructor.getParameterTypes()));
  }
  
  public Class[] getParameterTypes()
  {
    return ((Constructor)this.member).getParameterTypes();
  }
  
  public Class[] getExceptionTypes()
  {
    return ((Constructor)this.member).getExceptionTypes();
  }
  
  public Object newInstance()
    throws InvocationTargetException
  {
    return this.fc.newInstance(this.index, null);
  }
  
  public Object newInstance(Object[] paramArrayOfObject)
    throws InvocationTargetException
  {
    return this.fc.newInstance(this.index, paramArrayOfObject);
  }
  
  public Constructor getJavaConstructor()
  {
    return (Constructor)this.member;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\reflect\$FastConstructor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */