package com.google.inject.spi;

import com.google.inject.Injector;
import com.google.inject.Key;
import java.util.List;
import java.util.Set;

public abstract interface PrivateElements
  extends Element
{
  public abstract List getElements();
  
  public abstract Injector getInjector();
  
  public abstract Set getExposedKeys();
  
  public abstract Object getExposedSource(Key paramKey);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\PrivateElements.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */