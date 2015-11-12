package com.google.inject.spi;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.matcher.Matcher;
import java.util.List;

public final class ProvisionListenerBinding
  implements Element
{
  private final Object source;
  private final Matcher bindingMatcher;
  private final List listeners;
  
  ProvisionListenerBinding(Object paramObject, Matcher paramMatcher, ProvisionListener[] paramArrayOfProvisionListener)
  {
    this.source = paramObject;
    this.bindingMatcher = paramMatcher;
    this.listeners = ImmutableList.copyOf(paramArrayOfProvisionListener);
  }
  
  public List getListeners()
  {
    return this.listeners;
  }
  
  public Matcher getBindingMatcher()
  {
    return this.bindingMatcher;
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
    paramBinder.withSource(getSource()).bindListener(this.bindingMatcher, (ProvisionListener[])this.listeners.toArray(new ProvisionListener[this.listeners.size()]));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ProvisionListenerBinding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */