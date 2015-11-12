package com.google.inject.spi;

import com.google.inject.Key;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract interface ProvidesMethodBinding
  extends HasDependencies
{
  public abstract Method getMethod();
  
  public abstract Object getEnclosingInstance();
  
  public abstract Key getKey();
  
  public abstract Annotation getAnnotation();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ProvidesMethodBinding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */