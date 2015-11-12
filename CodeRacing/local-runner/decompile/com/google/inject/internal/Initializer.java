package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

final class Initializer
{
  private final Thread creatingThread = Thread.currentThread();
  private final CountDownLatch ready = new CountDownLatch(1);
  private final Map pendingMembersInjectors = Maps.newIdentityHashMap();
  private final Map pendingInjection = Maps.newIdentityHashMap();
  
  Initializable requestInjection(InjectorImpl paramInjectorImpl, Object paramObject1, Binding paramBinding, Object paramObject2, Set paramSet)
  {
    Preconditions.checkNotNull(paramObject2);
    ProvisionListenerStackCallback localProvisionListenerStackCallback = paramBinding == null ? null : paramInjectorImpl.provisionListenerStore.get(paramBinding);
    if ((paramObject1 == null) || ((paramSet.isEmpty()) && (!paramInjectorImpl.membersInjectorStore.hasTypeListeners()) && ((localProvisionListenerStackCallback == null) || (!localProvisionListenerStackCallback.hasListeners())))) {
      return Initializables.of(paramObject1);
    }
    InjectableReference localInjectableReference = new InjectableReference(paramInjectorImpl, paramObject1, paramBinding == null ? null : paramBinding.getKey(), localProvisionListenerStackCallback, paramObject2);
    this.pendingInjection.put(paramObject1, localInjectableReference);
    return localInjectableReference;
  }
  
  void validateOustandingInjections(Errors paramErrors)
  {
    Iterator localIterator = this.pendingInjection.values().iterator();
    while (localIterator.hasNext())
    {
      InjectableReference localInjectableReference = (InjectableReference)localIterator.next();
      try
      {
        this.pendingMembersInjectors.put(localInjectableReference.instance, localInjectableReference.validate(paramErrors));
      }
      catch (ErrorsException localErrorsException)
      {
        paramErrors.merge(localErrorsException.getErrors());
      }
    }
  }
  
  void injectAll(Errors paramErrors)
  {
    Object localObject = Lists.newArrayList(this.pendingInjection.values()).iterator();
    while (((Iterator)localObject).hasNext())
    {
      InjectableReference localInjectableReference = (InjectableReference)((Iterator)localObject).next();
      try
      {
        localInjectableReference.get(paramErrors);
      }
      catch (ErrorsException localErrorsException)
      {
        paramErrors.merge(localErrorsException.getErrors());
      }
    }
    if (!this.pendingInjection.isEmpty())
    {
      localObject = String.valueOf(String.valueOf(this.pendingInjection));
      throw new AssertionError(18 + ((String)localObject).length() + "Failed to satisfy " + (String)localObject);
    }
    this.ready.countDown();
  }
  
  private class InjectableReference
    implements Initializable
  {
    private final InjectorImpl injector;
    private final Object instance;
    private final Object source;
    private final Key key;
    private final ProvisionListenerStackCallback provisionCallback;
    
    public InjectableReference(InjectorImpl paramInjectorImpl, Object paramObject1, Key paramKey, ProvisionListenerStackCallback paramProvisionListenerStackCallback, Object paramObject2)
    {
      this.injector = paramInjectorImpl;
      this.key = paramKey;
      this.provisionCallback = paramProvisionListenerStackCallback;
      this.instance = Preconditions.checkNotNull(paramObject1, "instance");
      this.source = Preconditions.checkNotNull(paramObject2, "source");
    }
    
    public MembersInjectorImpl validate(Errors paramErrors)
      throws ErrorsException
    {
      TypeLiteral localTypeLiteral = TypeLiteral.get(this.instance.getClass());
      return this.injector.membersInjectorStore.get(localTypeLiteral, paramErrors.withSource(this.source));
    }
    
    public Object get(Errors paramErrors)
      throws ErrorsException
    {
      if (Initializer.this.ready.getCount() == 0L) {
        return this.instance;
      }
      if (Thread.currentThread() != Initializer.this.creatingThread) {
        try
        {
          Initializer.this.ready.await();
          return this.instance;
        }
        catch (InterruptedException localInterruptedException)
        {
          throw new RuntimeException(localInterruptedException);
        }
      }
      if (Initializer.this.pendingInjection.remove(this.instance) != null)
      {
        MembersInjectorImpl localMembersInjectorImpl = (MembersInjectorImpl)Initializer.this.pendingMembersInjectors.remove(this.instance);
        Preconditions.checkState(localMembersInjectorImpl != null, "No membersInjector available for instance: %s, from key: %s", new Object[] { this.instance, this.key });
        localMembersInjectorImpl.injectAndNotify(this.instance, paramErrors.withSource(this.source), this.key, this.provisionCallback, this.source, this.injector.options.stage == Stage.TOOL);
      }
      return this.instance;
    }
    
    public String toString()
    {
      return this.instance.toString();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\Initializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */