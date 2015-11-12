package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.spi.InjectionPoint;

final class ConstructorInjectorStore
{
  private final InjectorImpl injector;
  private final FailableCache cache = new FailableCache()
  {
    protected ConstructorInjector create(InjectionPoint paramAnonymousInjectionPoint, Errors paramAnonymousErrors)
      throws ErrorsException
    {
      return ConstructorInjectorStore.this.createConstructor(paramAnonymousInjectionPoint, paramAnonymousErrors);
    }
  };
  
  ConstructorInjectorStore(InjectorImpl paramInjectorImpl)
  {
    this.injector = paramInjectorImpl;
  }
  
  public ConstructorInjector get(InjectionPoint paramInjectionPoint, Errors paramErrors)
    throws ErrorsException
  {
    return (ConstructorInjector)this.cache.get(paramInjectionPoint, paramErrors);
  }
  
  boolean remove(InjectionPoint paramInjectionPoint)
  {
    return this.cache.remove(paramInjectionPoint);
  }
  
  private ConstructorInjector createConstructor(InjectionPoint paramInjectionPoint, Errors paramErrors)
    throws ErrorsException
  {
    int i = paramErrors.size();
    SingleParameterInjector[] arrayOfSingleParameterInjector = this.injector.getParametersInjectors(paramInjectionPoint.getDependencies(), paramErrors);
    MembersInjectorImpl localMembersInjectorImpl = this.injector.membersInjectorStore.get(paramInjectionPoint.getDeclaringType(), paramErrors);
    ImmutableList localImmutableList1 = this.injector.state.getMethodAspects();
    ImmutableList localImmutableList2 = localMembersInjectorImpl.getAddedAspects().isEmpty() ? localImmutableList1 : ImmutableList.copyOf(Iterables.concat(localImmutableList1, localMembersInjectorImpl.getAddedAspects()));
    ProxyFactory localProxyFactory = new ProxyFactory(paramInjectionPoint, localImmutableList2);
    paramErrors.throwIfNewErrors(i);
    return new ConstructorInjector(localMembersInjectorImpl.getInjectionPoints(), localProxyFactory.create(), arrayOfSingleParameterInjector, localMembersInjectorImpl);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ConstructorInjectorStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */