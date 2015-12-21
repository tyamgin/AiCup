package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.spi.Dependency;
import javax.inject.Provider;

final class InternalFactoryToInitializableAdapter
  extends ProviderInternalFactory
{
  private final ProvisionListenerStackCallback provisionCallback;
  private final Initializable initializable;
  
  public InternalFactoryToInitializableAdapter(Initializable paramInitializable, Object paramObject, ProvisionListenerStackCallback paramProvisionListenerStackCallback)
  {
    super(paramObject);
    this.provisionCallback = ((ProvisionListenerStackCallback)Preconditions.checkNotNull(paramProvisionListenerStackCallback, "provisionCallback"));
    this.initializable = ((Initializable)Preconditions.checkNotNull(paramInitializable, "provider"));
  }
  
  public Object get(Errors paramErrors, InternalContext paramInternalContext, Dependency paramDependency, boolean paramBoolean)
    throws ErrorsException
  {
    return circularGet((Provider)this.initializable.get(paramErrors), paramErrors, paramInternalContext, paramDependency, this.provisionCallback);
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
      throw paramErrors.withSource(this.source).errorInProvider(localRuntimeException).toException();
    }
  }
  
  public String toString()
  {
    return this.initializable.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\InternalFactoryToInitializableAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */