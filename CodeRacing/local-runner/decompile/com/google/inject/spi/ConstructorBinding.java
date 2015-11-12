package com.google.inject.spi;

import com.google.inject.Binding;
import java.util.Map;
import java.util.Set;

public abstract interface ConstructorBinding
  extends Binding, HasDependencies
{
  public abstract InjectionPoint getConstructor();
  
  public abstract Set getInjectableMembers();
  
  public abstract Map getMethodInterceptors();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ConstructorBinding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */