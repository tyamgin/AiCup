package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.Dependency;
import javax.inject.Provider;

abstract class ProviderInternalFactory
  implements InternalFactory
{
  protected final Object source;
  
  ProviderInternalFactory(Object paramObject)
  {
    this.source = Preconditions.checkNotNull(paramObject, "source");
  }
  
  protected Object circularGet(final Provider paramProvider, final Errors paramErrors, InternalContext paramInternalContext, final Dependency paramDependency, ProvisionListenerStackCallback paramProvisionListenerStackCallback)
    throws ErrorsException
  {
    final ConstructionContext localConstructionContext = paramInternalContext.getConstructionContext(this);
    Object localObject1;
    if (localConstructionContext.isConstructing())
    {
      localObject1 = paramDependency.getKey().getTypeLiteral().getRawType();
      Object localObject2 = localConstructionContext.createProxy(paramErrors, paramInternalContext.getInjectorOptions(), (Class)localObject1);
      return localObject2;
    }
    localConstructionContext.startConstruction();
    try
    {
      if (!paramProvisionListenerStackCallback.hasListeners())
      {
        localObject1 = provision(paramProvider, paramErrors, paramDependency, localConstructionContext);
        return localObject1;
      }
      localObject1 = paramProvisionListenerStackCallback.provision(paramErrors, paramInternalContext, new ProvisionListenerStackCallback.ProvisionCallback()
      {
        public Object call()
          throws ErrorsException
        {
          return ProviderInternalFactory.this.provision(paramProvider, paramErrors, paramDependency, localConstructionContext);
        }
      });
      return localObject1;
    }
    finally
    {
      localConstructionContext.removeCurrentReference();
      localConstructionContext.finishConstruction();
    }
  }
  
  protected Object provision(Provider paramProvider, Errors paramErrors, Dependency paramDependency, ConstructionContext paramConstructionContext)
    throws ErrorsException
  {
    Object localObject = paramErrors.checkForNull(paramProvider.get(), this.source, paramDependency);
    paramConstructionContext.setProxyDelegates(localObject);
    return localObject;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ProviderInternalFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */