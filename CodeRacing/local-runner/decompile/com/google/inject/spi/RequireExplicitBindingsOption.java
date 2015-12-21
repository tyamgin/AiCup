package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;

public final class RequireExplicitBindingsOption
  implements Element
{
  private final Object source;
  
  RequireExplicitBindingsOption(Object paramObject)
  {
    this.source = Preconditions.checkNotNull(paramObject, "source");
  }
  
  public Object getSource()
  {
    return this.source;
  }
  
  public void applyTo(Binder paramBinder)
  {
    paramBinder.withSource(getSource()).requireExplicitBindings();
  }
  
  public Object acceptVisitor(ElementVisitor paramElementVisitor)
  {
    return paramElementVisitor.visit(this);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\RequireExplicitBindingsOption.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */