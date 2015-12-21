package com.google.inject.binder;

import java.lang.annotation.Annotation;

public abstract interface AnnotatedConstantBindingBuilder
{
  public abstract ConstantBindingBuilder annotatedWith(Class paramClass);
  
  public abstract ConstantBindingBuilder annotatedWith(Annotation paramAnnotation);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\binder\AnnotatedConstantBindingBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */