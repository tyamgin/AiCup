package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.inject.spi.Dependency;

final class ConstantFactory
  implements InternalFactory
{
  private final Initializable initializable;
  
  public ConstantFactory(Initializable paramInitializable)
  {
    this.initializable = paramInitializable;
  }
  
  public Object get(Errors paramErrors, InternalContext paramInternalContext, Dependency paramDependency, boolean paramBoolean)
    throws ErrorsException
  {
    return this.initializable.get(paramErrors);
  }
  
  public String toString()
  {
    return Objects.toStringHelper(ConstantFactory.class).add("value", this.initializable).toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ConstantFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */