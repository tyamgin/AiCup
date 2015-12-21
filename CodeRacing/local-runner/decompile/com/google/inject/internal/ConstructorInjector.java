package com.google.inject.internal;

import com.google.common.collect.ImmutableSet;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

final class ConstructorInjector
{
  private final ImmutableSet injectableMembers;
  private final SingleParameterInjector[] parameterInjectors;
  private final ConstructionProxy constructionProxy;
  private final MembersInjectorImpl membersInjector;
  
  ConstructorInjector(Set paramSet, ConstructionProxy paramConstructionProxy, SingleParameterInjector[] paramArrayOfSingleParameterInjector, MembersInjectorImpl paramMembersInjectorImpl)
  {
    this.injectableMembers = ImmutableSet.copyOf(paramSet);
    this.constructionProxy = paramConstructionProxy;
    this.parameterInjectors = paramArrayOfSingleParameterInjector;
    this.membersInjector = paramMembersInjectorImpl;
  }
  
  public ImmutableSet getInjectableMembers()
  {
    return this.injectableMembers;
  }
  
  ConstructionProxy getConstructionProxy()
  {
    return this.constructionProxy;
  }
  
  Object construct(final Errors paramErrors, final InternalContext paramInternalContext, Class paramClass, ProvisionListenerStackCallback paramProvisionListenerStackCallback)
    throws ErrorsException
  {
    final ConstructionContext localConstructionContext = paramInternalContext.getConstructionContext(this);
    if (localConstructionContext.isConstructing()) {
      return localConstructionContext.createProxy(paramErrors, paramInternalContext.getInjectorOptions(), paramClass);
    }
    Object localObject1 = localConstructionContext.getCurrentReference();
    if (localObject1 != null) {
      return localObject1;
    }
    localConstructionContext.startConstruction();
    try
    {
      if (!paramProvisionListenerStackCallback.hasListeners())
      {
        localObject2 = provision(paramErrors, paramInternalContext, localConstructionContext);
        return localObject2;
      }
      Object localObject2 = paramProvisionListenerStackCallback.provision(paramErrors, paramInternalContext, new ProvisionListenerStackCallback.ProvisionCallback()
      {
        public Object call()
          throws ErrorsException
        {
          return ConstructorInjector.this.provision(paramErrors, paramInternalContext, localConstructionContext);
        }
      });
      return localObject2;
    }
    finally
    {
      localConstructionContext.finishConstruction();
    }
  }
  
  private Object provision(Errors paramErrors, InternalContext paramInternalContext, ConstructionContext paramConstructionContext)
    throws ErrorsException
  {
    try
    {
      Object localObject2;
      try
      {
        localObject1 = SingleParameterInjector.getAll(paramErrors, paramInternalContext, this.parameterInjectors);
        localObject2 = this.constructionProxy.newInstance((Object[])localObject1);
        paramConstructionContext.setProxyDelegates(localObject2);
      }
      finally {}
      paramConstructionContext.setCurrentReference(localObject2);
      this.membersInjector.injectMembers(localObject2, paramErrors, paramInternalContext, false);
      this.membersInjector.notifyListeners(localObject2, paramErrors);
      localObject1 = localObject2;
      return localObject1;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Object localObject1 = localInvocationTargetException.getCause() != null ? localInvocationTargetException.getCause() : localInvocationTargetException;
      throw paramErrors.withSource(this.constructionProxy.getInjectionPoint()).errorInjectingConstructor((Throwable)localObject1).toException();
    }
    finally
    {
      paramConstructionContext.removeCurrentReference();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ConstructorInjector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */