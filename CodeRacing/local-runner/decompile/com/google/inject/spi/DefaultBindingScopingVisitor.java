package com.google.inject.spi;

import com.google.inject.Scope;

public class DefaultBindingScopingVisitor
  implements BindingScopingVisitor
{
  protected Object visitOther()
  {
    return null;
  }
  
  public Object visitEagerSingleton()
  {
    return visitOther();
  }
  
  public Object visitScope(Scope paramScope)
  {
    return visitOther();
  }
  
  public Object visitScopeAnnotation(Class paramClass)
  {
    return visitOther();
  }
  
  public Object visitNoScoping()
  {
    return visitOther();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\DefaultBindingScopingVisitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */