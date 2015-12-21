package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.DependencyAndSource;
import com.google.inject.spi.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SingletonScope
  implements Scope
{
  private static final Object NULL = new Object();
  private static final CycleDetectingLock.CycleDetectingLockFactory cycleDetectingLockFactory = new CycleDetectingLock.CycleDetectingLockFactory();
  
  public Provider scope(final Key paramKey, final Provider paramProvider)
  {
    new Provider()
    {
      volatile Object instance;
      final ConstructionContext constructionContext = new ConstructionContext();
      final CycleDetectingLock creationLock = SingletonScope.cycleDetectingLockFactory.create(paramKey);
      
      public Object get()
      {
        Object localObject1 = this.instance;
        if (localObject1 == null)
        {
          localObject2 = this.creationLock.lockOrDetectPotentialLocksCycle();
          if (((ListMultimap)localObject2).isEmpty()) {
            try
            {
              if (this.instance == null)
              {
                Object localObject3 = paramProvider.get();
                Object localObject4 = localObject3 == null ? SingletonScope.NULL : localObject3;
                if (this.instance == null)
                {
                  if (Scopes.isCircularProxy(localObject3))
                  {
                    Object localObject5 = localObject3;
                    return localObject5;
                  }
                  synchronized (this.constructionContext)
                  {
                    this.instance = localObject4;
                    this.constructionContext.setProxyDelegates(localObject3);
                  }
                }
                else
                {
                  Preconditions.checkState(this.instance == localObject4, "Singleton is called recursively returning different results");
                }
              }
            }
            catch (RuntimeException localRuntimeException)
            {
              synchronized (this.constructionContext)
              {
                this.constructionContext.finishConstruction();
              }
              throw localRuntimeException;
            }
            finally
            {
              this.creationLock.unlock();
            }
          } else {
            synchronized (this.constructionContext)
            {
              if (this.instance == null)
              {
                ??? = InjectorImpl.getGlobalInternalContext();
                ??? = (InternalContext)((Map)???).get(Thread.currentThread());
                Dependency localDependency = (Dependency)Preconditions.checkNotNull(((InternalContext)???).getDependency(), "globalInternalContext.get(currentThread()).getDependency()");
                Class localClass = localDependency.getKey().getTypeLiteral().getRawType();
                try
                {
                  Object localObject9 = this.constructionContext.createProxy(new Errors(), ((InternalContext)???).getInjectorOptions(), localClass);
                  return localObject9;
                }
                catch (ErrorsException localErrorsException)
                {
                  List localList = localErrorsException.getErrors().getMessages();
                  Preconditions.checkState(localList.size() == 1);
                  Message localMessage = createCycleDependenciesMessage(ImmutableMap.copyOf((Map)???), (ListMultimap)localObject2, (Message)localList.get(0));
                  throw new ProvisionException(ImmutableList.of(localMessage, localList.get(0)));
                }
              }
            }
          }
          ??? = this.instance;
          Preconditions.checkState(??? != null, "Internal error: Singleton is not initialized contrary to our expectations");
          ??? = ???;
          return ??? == SingletonScope.NULL ? null : ???;
        }
        Object localObject2 = localObject1;
        return localObject1 == SingletonScope.NULL ? null : localObject2;
      }
      
      private Message createCycleDependenciesMessage(Map paramAnonymousMap, ListMultimap paramAnonymousListMultimap, Message paramAnonymousMessage)
      {
        ArrayList localArrayList = Lists.newArrayList();
        localArrayList.add(Thread.currentThread());
        HashMap localHashMap = Maps.newHashMap();
        Iterator localIterator1 = paramAnonymousMap.keySet().iterator();
        while (localIterator1.hasNext())
        {
          Thread localThread1 = (Thread)localIterator1.next();
          localHashMap.put(Long.valueOf(localThread1.getId()), localThread1);
        }
        localIterator1 = paramAnonymousListMultimap.keySet().iterator();
        while (localIterator1.hasNext())
        {
          long l = ((Long)localIterator1.next()).longValue();
          Thread localThread2 = (Thread)localHashMap.get(Long.valueOf(l));
          List localList1 = Collections.unmodifiableList(paramAnonymousListMultimap.get(Long.valueOf(l)));
          if (localThread2 != null)
          {
            List localList2 = null;
            int i = 0;
            InternalContext localInternalContext = (InternalContext)paramAnonymousMap.get(localThread2);
            Object localObject1;
            Object localObject2;
            Object localObject3;
            if (localInternalContext != null)
            {
              localList2 = localInternalContext.getDependencyChain();
              localObject1 = Lists.newLinkedList(localList1);
              Iterator localIterator2 = localList2.iterator();
              while (localIterator2.hasNext())
              {
                localObject2 = (DependencyAndSource)localIterator2.next();
                localObject3 = ((DependencyAndSource)localObject2).getDependency();
                if (localObject3 != null) {
                  if (((Dependency)localObject3).getKey().equals(((List)localObject1).get(0)))
                  {
                    ((List)localObject1).remove(0);
                    if (((List)localObject1).isEmpty())
                    {
                      i = 1;
                      break;
                    }
                  }
                }
              }
            }
            if (i != 0)
            {
              localObject1 = (Key)localList1.get(0);
              int j = 0;
              localObject2 = localList2.iterator();
              while (((Iterator)localObject2).hasNext())
              {
                localObject3 = (DependencyAndSource)((Iterator)localObject2).next();
                Dependency localDependency = ((DependencyAndSource)localObject3).getDependency();
                if (localDependency != null) {
                  if (j != 0)
                  {
                    localArrayList.add(localDependency);
                    localArrayList.add(((DependencyAndSource)localObject3).getBindingSource());
                  }
                  else if (localDependency.getKey().equals(localObject1))
                  {
                    j = 1;
                    localArrayList.add(((DependencyAndSource)localObject3).getBindingSource());
                  }
                }
              }
            }
            else
            {
              localArrayList.addAll(localList1);
            }
            localArrayList.add(localThread2);
          }
        }
        return new Message(localArrayList, String.format("Encountered circular dependency spanning several threads. %s", new Object[] { paramAnonymousMessage.getMessage() }), null);
      }
      
      public String toString()
      {
        return String.format("%s[%s]", new Object[] { paramProvider, Scopes.SINGLETON });
      }
    };
  }
  
  public String toString()
  {
    return "Scopes.SINGLETON";
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\SingletonScope.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */