package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.Binding;

public abstract interface ExposedBinding
  extends Binding, HasDependencies
{
  public abstract PrivateElements getPrivateElements();
  
  public abstract void applyTo(Binder paramBinder);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ExposedBinding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */