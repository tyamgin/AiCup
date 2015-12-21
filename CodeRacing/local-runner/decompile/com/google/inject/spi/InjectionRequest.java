package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import com.google.inject.TypeLiteral;
import java.util.Set;

public final class InjectionRequest
  implements Element
{
  private final Object source;
  private final TypeLiteral type;
  private final Object instance;
  
  public InjectionRequest(Object paramObject1, TypeLiteral paramTypeLiteral, Object paramObject2)
  {
    this.source = Preconditions.checkNotNull(paramObject1, "source");
    this.type = ((TypeLiteral)Preconditions.checkNotNull(paramTypeLiteral, "type"));
    this.instance = Preconditions.checkNotNull(paramObject2, "instance");
  }
  
  public Object getSource()
  {
    return this.source;
  }
  
  public Object getInstance()
  {
    return this.instance;
  }
  
  public TypeLiteral getType()
  {
    return this.type;
  }
  
  public Set getInjectionPoints()
    throws ConfigurationException
  {
    return InjectionPoint.forInstanceMethodsAndFields(this.instance.getClass());
  }
  
  public Object acceptVisitor(ElementVisitor paramElementVisitor)
  {
    return paramElementVisitor.visit(this);
  }
  
  public void applyTo(Binder paramBinder)
  {
    paramBinder.withSource(getSource()).requestInjection(this.type, this.instance);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\InjectionRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */