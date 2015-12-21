package com.google.inject.binder;

import com.google.inject.Scope;

public abstract interface ScopedBindingBuilder
{
  public abstract void in(Class paramClass);
  
  public abstract void in(Scope paramScope);
  
  public abstract void asEagerSingleton();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\binder\ScopedBindingBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */