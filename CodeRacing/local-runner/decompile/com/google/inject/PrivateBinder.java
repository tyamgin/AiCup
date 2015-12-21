package com.google.inject;

import com.google.inject.binder.AnnotatedElementBuilder;

public abstract interface PrivateBinder
  extends Binder
{
  public abstract void expose(Key paramKey);
  
  public abstract AnnotatedElementBuilder expose(Class paramClass);
  
  public abstract AnnotatedElementBuilder expose(TypeLiteral paramTypeLiteral);
  
  public abstract PrivateBinder withSource(Object paramObject);
  
  public abstract PrivateBinder skipSources(Class... paramVarArgs);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\PrivateBinder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */