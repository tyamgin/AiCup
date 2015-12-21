package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import java.util.Set;

public final class StaticInjectionRequest
  implements Element
{
  private final Object source;
  private final Class type;
  
  StaticInjectionRequest(Object paramObject, Class paramClass)
  {
    this.source = Preconditions.checkNotNull(paramObject, "source");
    this.type = ((Class)Preconditions.checkNotNull(paramClass, "type"));
  }
  
  public Object getSource()
  {
    return this.source;
  }
  
  public Class getType()
  {
    return this.type;
  }
  
  public Set getInjectionPoints()
    throws ConfigurationException
  {
    return InjectionPoint.forStaticMethodsAndFields(this.type);
  }
  
  public void applyTo(Binder paramBinder)
  {
    paramBinder.withSource(getSource()).requestStaticInjection(new Class[] { this.type });
  }
  
  public Object acceptVisitor(ElementVisitor paramElementVisitor)
  {
    return paramElementVisitor.visit(this);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\StaticInjectionRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */