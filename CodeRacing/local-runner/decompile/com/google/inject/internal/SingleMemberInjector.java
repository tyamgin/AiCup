package com.google.inject.internal;

import com.google.inject.spi.InjectionPoint;

abstract interface SingleMemberInjector
{
  public abstract void inject(Errors paramErrors, InternalContext paramInternalContext, Object paramObject);
  
  public abstract InjectionPoint getInjectionPoint();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\SingleMemberInjector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */