package com.google.inject.internal;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class ConstructionContext
{
  Object currentReference;
  boolean constructing;
  List invocationHandlers;
  
  public Object getCurrentReference()
  {
    return this.currentReference;
  }
  
  public void removeCurrentReference()
  {
    this.currentReference = null;
  }
  
  public void setCurrentReference(Object paramObject)
  {
    this.currentReference = paramObject;
  }
  
  public boolean isConstructing()
  {
    return this.constructing;
  }
  
  public void startConstruction()
  {
    this.constructing = true;
  }
  
  public void finishConstruction()
  {
    this.constructing = false;
    this.invocationHandlers = null;
  }
  
  public Object createProxy(Errors paramErrors, InjectorImpl.InjectorOptions paramInjectorOptions, Class paramClass)
    throws ErrorsException
  {
    if (paramInjectorOptions.disableCircularProxies) {
      throw paramErrors.circularProxiesDisabled(paramClass).toException();
    }
    if (!paramClass.isInterface()) {
      throw paramErrors.cannotSatisfyCircularDependency(paramClass).toException();
    }
    if (this.invocationHandlers == null) {
      this.invocationHandlers = new ArrayList();
    }
    DelegatingInvocationHandler localDelegatingInvocationHandler = new DelegatingInvocationHandler();
    this.invocationHandlers.add(localDelegatingInvocationHandler);
    ClassLoader localClassLoader = BytecodeGen.getClassLoader(paramClass);
    return paramClass.cast(Proxy.newProxyInstance(localClassLoader, new Class[] { paramClass, CircularDependencyProxy.class }, localDelegatingInvocationHandler));
  }
  
  public void setProxyDelegates(Object paramObject)
  {
    if (this.invocationHandlers != null)
    {
      Iterator localIterator = this.invocationHandlers.iterator();
      while (localIterator.hasNext())
      {
        DelegatingInvocationHandler localDelegatingInvocationHandler = (DelegatingInvocationHandler)localIterator.next();
        localDelegatingInvocationHandler.setDelegate(paramObject);
      }
      this.invocationHandlers = null;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ConstructionContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */