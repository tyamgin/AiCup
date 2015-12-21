package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.inject.Key;
import com.google.inject.spi.Dependency;

final class FactoryProxy
  implements CreationListener, InternalFactory
{
  private final InjectorImpl injector;
  private final Key key;
  private final Key targetKey;
  private final Object source;
  private InternalFactory targetFactory;
  
  FactoryProxy(InjectorImpl paramInjectorImpl, Key paramKey1, Key paramKey2, Object paramObject)
  {
    this.injector = paramInjectorImpl;
    this.key = paramKey1;
    this.targetKey = paramKey2;
    this.source = paramObject;
  }
  
  public void notify(Errors paramErrors)
  {
    try
    {
      this.targetFactory = this.injector.getInternalFactory(this.targetKey, paramErrors.withSource(this.source), InjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT);
    }
    catch (ErrorsException localErrorsException)
    {
      paramErrors.merge(localErrorsException.getErrors());
    }
  }
  
  public Object get(Errors paramErrors, InternalContext paramInternalContext, Dependency paramDependency, boolean paramBoolean)
    throws ErrorsException
  {
    paramInternalContext.pushState(this.targetKey, this.source);
    try
    {
      Object localObject1 = this.targetFactory.get(paramErrors.withSource(this.targetKey), paramInternalContext, paramDependency, true);
      return localObject1;
    }
    finally
    {
      paramInternalContext.popState();
    }
  }
  
  public String toString()
  {
    return Objects.toStringHelper(FactoryProxy.class).add("key", this.key).add("provider", this.targetFactory).toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\FactoryProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */