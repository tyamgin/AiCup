package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Key;
import com.google.inject.spi.Dependency;
import javax.inject.Provider;

final class BoundProviderFactory
  extends ProviderInternalFactory
  implements CreationListener
{
  private final ProvisionListenerStackCallback provisionCallback;
  private final InjectorImpl injector;
  final Key providerKey;
  private InternalFactory providerFactory;
  
  BoundProviderFactory(InjectorImpl paramInjectorImpl, Key paramKey, Object paramObject, ProvisionListenerStackCallback paramProvisionListenerStackCallback)
  {
    super(paramObject);
    this.provisionCallback = ((ProvisionListenerStackCallback)Preconditions.checkNotNull(paramProvisionListenerStackCallback, "provisionCallback"));
    this.injector = paramInjectorImpl;
    this.providerKey = paramKey;
  }
  
  public void notify(Errors paramErrors)
  {
    try
    {
      this.providerFactory = this.injector.getInternalFactory(this.providerKey, paramErrors.withSource(this.source), InjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT);
    }
    catch (ErrorsException localErrorsException)
    {
      paramErrors.merge(localErrorsException.getErrors());
    }
  }
  
  public Object get(Errors paramErrors, InternalContext paramInternalContext, Dependency paramDependency, boolean paramBoolean)
    throws ErrorsException
  {
    paramInternalContext.pushState(this.providerKey, this.source);
    try
    {
      paramErrors = paramErrors.withSource(this.providerKey);
      Provider localProvider = (Provider)this.providerFactory.get(paramErrors, paramInternalContext, paramDependency, true);
      Object localObject1 = circularGet(localProvider, paramErrors, paramInternalContext, paramDependency, this.provisionCallback);
      return localObject1;
    }
    finally
    {
      paramInternalContext.popState();
    }
  }
  
  protected Object provision(Provider paramProvider, Errors paramErrors, Dependency paramDependency, ConstructionContext paramConstructionContext)
    throws ErrorsException
  {
    try
    {
      return super.provision(paramProvider, paramErrors, paramDependency, paramConstructionContext);
    }
    catch (RuntimeException localRuntimeException)
    {
      throw paramErrors.errorInProvider(localRuntimeException).toException();
    }
  }
  
  public String toString()
  {
    return this.providerKey.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\BoundProviderFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */