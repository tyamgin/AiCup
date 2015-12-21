package com.google.inject.spi;

import com.google.inject.Binding;
import java.util.Set;

public abstract interface ProviderInstanceBinding
  extends Binding, HasDependencies
{
  @Deprecated
  public abstract com.google.inject.Provider getProviderInstance();
  
  public abstract javax.inject.Provider getUserSuppliedProvider();
  
  public abstract Set getInjectionPoints();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ProviderInstanceBinding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */