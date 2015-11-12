package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.Key;
import java.lang.annotation.Annotation;
import java.util.Set;

public abstract class ModuleAnnotatedMethodScanner
{
  public abstract Set annotationClasses();
  
  public abstract Key prepareMethod(Binder paramBinder, Annotation paramAnnotation, Key paramKey, InjectionPoint paramInjectionPoint);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ModuleAnnotatedMethodScanner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */