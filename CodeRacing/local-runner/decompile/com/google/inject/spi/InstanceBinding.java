package com.google.inject.spi;

import com.google.inject.Binding;
import java.util.Set;

public abstract interface InstanceBinding
  extends Binding, HasDependencies
{
  public abstract Object getInstance();
  
  public abstract Set getInjectionPoints();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\InstanceBinding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */