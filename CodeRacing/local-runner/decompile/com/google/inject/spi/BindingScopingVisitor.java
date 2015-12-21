package com.google.inject.spi;

import com.google.inject.Scope;

public abstract interface BindingScopingVisitor
{
  public abstract Object visitEagerSingleton();
  
  public abstract Object visitScope(Scope paramScope);
  
  public abstract Object visitScopeAnnotation(Class paramClass);
  
  public abstract Object visitNoScoping();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\BindingScopingVisitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */