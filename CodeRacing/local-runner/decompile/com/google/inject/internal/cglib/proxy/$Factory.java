package com.google.inject.internal.cglib.proxy;

public abstract interface $Factory
{
  public abstract Object newInstance(.Callback paramCallback);
  
  public abstract Object newInstance(.Callback[] paramArrayOfCallback);
  
  public abstract Object newInstance(Class[] paramArrayOfClass, Object[] paramArrayOfObject, .Callback[] paramArrayOfCallback);
  
  public abstract .Callback getCallback(int paramInt);
  
  public abstract void setCallback(int paramInt, .Callback paramCallback);
  
  public abstract void setCallbacks(.Callback[] paramArrayOfCallback);
  
  public abstract .Callback[] getCallbacks();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\proxy\$Factory.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */