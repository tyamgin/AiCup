package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import com.google.inject.spi.Dependency;

final class InternalFactoryToProviderAdapter
  implements InternalFactory
{
  private final Provider provider;
  private final Object source;
  
  public InternalFactoryToProviderAdapter(Provider paramProvider, Object paramObject)
  {
    this.provider = ((Provider)Preconditions.checkNotNull(paramProvider, "provider"));
    this.source = Preconditions.checkNotNull(paramObject, "source");
  }
  
  public Object get(Errors paramErrors, InternalContext paramInternalContext, Dependency paramDependency, boolean paramBoolean)
    throws ErrorsException
  {
    try
    {
      return paramErrors.checkForNull(this.provider.get(), this.source, paramDependency);
    }
    catch (RuntimeException localRuntimeException)
    {
      throw paramErrors.withSource(this.source).errorInProvider(localRuntimeException).toException();
    }
  }
  
  public String toString()
  {
    return this.provider.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\InternalFactoryToProviderAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */