package com.google.inject.binder;

public abstract interface ConstantBindingBuilder
{
  public abstract void to(String paramString);
  
  public abstract void to(int paramInt);
  
  public abstract void to(long paramLong);
  
  public abstract void to(boolean paramBoolean);
  
  public abstract void to(double paramDouble);
  
  public abstract void to(float paramFloat);
  
  public abstract void to(short paramShort);
  
  public abstract void to(char paramChar);
  
  public abstract void to(byte paramByte);
  
  public abstract void to(Class paramClass);
  
  public abstract void to(Enum paramEnum);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\binder\ConstantBindingBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */