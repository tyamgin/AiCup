package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Key;
import com.google.inject.spi.Dependency;

class ProvidedByInternalFactory
  extends ProviderInternalFactory
  implements DelayedInitialize
{
  private final Class rawType;
  private final Class providerType;
  private final Key providerKey;
  private BindingImpl providerBinding;
  private ProvisionListenerStackCallback provisionCallback;
  
  ProvidedByInternalFactory(Class paramClass1, Class paramClass2, Key paramKey)
  {
    super(paramKey);
    this.rawType = paramClass1;
    this.providerType = paramClass2;
    this.providerKey = paramKey;
  }
  
  void setProvisionListenerCallback(ProvisionListenerStackCallback paramProvisionListenerStackCallback)
  {
    this.provisionCallback = paramProvisionListenerStackCallback;
  }
  
  public void initialize(InjectorImpl paramInjectorImpl, Errors paramErrors)
    throws ErrorsException
  {
    this.providerBinding = paramInjectorImpl.getBindingOrThrow(this.providerKey, paramErrors, InjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT);
  }
  
  public Object get(Errors paramErrors, InternalContext paramInternalContext, Dependency paramDependency, boolean paramBoolean)
    throws ErrorsException
  {
    Preconditions.checkState(this.providerBinding != null, "not initialized");
    paramInternalContext.pushState(this.providerKey, this.providerBinding.getSource());
    try
    {
      paramErrors = paramErrors.withSource(this.providerKey);
      com.google.inject.Provider localProvider = (com.google.inject.Provider)this.providerBinding.getInternalFactory().get(paramErrors, paramInternalContext, paramDependency, true);
      Object localObject1 = circularGet(localProvider, paramErrors, paramInternalContext, paramDependency, this.provisionCallback);
      return localObject1;
    }
    finally
    {
      paramInternalContext.popState();
    }
  }
  
  protected Object provision(javax.inject.Provider paramProvider, Errors paramErrors, Dependency paramDependency, ConstructionContext paramConstructionContext)
    throws ErrorsException
  {
    try
    {
      Object localObject1 = super.provision(paramProvider, paramErrors, paramDependency, paramConstructionContext);
      if ((localObject1 != null) && (!this.rawType.isInstance(localObject1))) {
        throw paramErrors.subtypeNotProvided(this.providerType, this.rawType).toException();
      }
      Object localObject2 = localObject1;
      return localObject2;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw paramErrors.errorInProvider(localRuntimeException).toException();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ProvidedByInternalFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */