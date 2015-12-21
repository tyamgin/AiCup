package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.matcher.Matcher;

public final class TypeListenerBinding
  implements Element
{
  private final Object source;
  private final Matcher typeMatcher;
  private final TypeListener listener;
  
  TypeListenerBinding(Object paramObject, TypeListener paramTypeListener, Matcher paramMatcher)
  {
    this.source = paramObject;
    this.listener = paramTypeListener;
    this.typeMatcher = paramMatcher;
  }
  
  public TypeListener getListener()
  {
    return this.listener;
  }
  
  public Matcher getTypeMatcher()
  {
    return this.typeMatcher;
  }
  
  public Object getSource()
  {
    return this.source;
  }
  
  public Object acceptVisitor(ElementVisitor paramElementVisitor)
  {
    return paramElementVisitor.visit(this);
  }
  
  public void applyTo(Binder paramBinder)
  {
    paramBinder.withSource(getSource()).bindListener(this.typeMatcher, this.listener);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\TypeListenerBinding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */