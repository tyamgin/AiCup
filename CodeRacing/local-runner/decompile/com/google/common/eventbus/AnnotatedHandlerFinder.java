package com.google.common.eventbus;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.common.reflect.TypeToken.TypeSet;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

class AnnotatedHandlerFinder
  implements HandlerFindingStrategy
{
  private static final LoadingCache handlerMethodsCache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader()
  {
    public ImmutableList load(Class paramAnonymousClass)
      throws Exception
    {
      return AnnotatedHandlerFinder.getAnnotatedMethodsInternal(paramAnonymousClass);
    }
  });
  
  public Multimap findAllHandlers(Object paramObject)
  {
    HashMultimap localHashMultimap = HashMultimap.create();
    Class localClass1 = paramObject.getClass();
    Iterator localIterator = getAnnotatedMethods(localClass1).iterator();
    while (localIterator.hasNext())
    {
      Method localMethod = (Method)localIterator.next();
      Class[] arrayOfClass = localMethod.getParameterTypes();
      Class localClass2 = arrayOfClass[0];
      EventHandler localEventHandler = makeHandler(paramObject, localMethod);
      localHashMultimap.put(localClass2, localEventHandler);
    }
    return localHashMultimap;
  }
  
  private static ImmutableList getAnnotatedMethods(Class paramClass)
  {
    try
    {
      return (ImmutableList)handlerMethodsCache.getUnchecked(paramClass);
    }
    catch (UncheckedExecutionException localUncheckedExecutionException)
    {
      throw Throwables.propagate(localUncheckedExecutionException.getCause());
    }
  }
  
  private static ImmutableList getAnnotatedMethodsInternal(Class paramClass)
  {
    Set localSet = TypeToken.of(paramClass).getTypes().rawTypes();
    ImmutableList.Builder localBuilder = ImmutableList.builder();
    for (Method localMethod1 : paramClass.getMethods())
    {
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        Class localClass1 = (Class)localIterator.next();
        try
        {
          Method localMethod2 = localClass1.getMethod(localMethod1.getName(), localMethod1.getParameterTypes());
          if (localMethod2.isAnnotationPresent(Subscribe.class))
          {
            Class[] arrayOfClass = localMethod1.getParameterTypes();
            if (arrayOfClass.length != 1) {
              throw new IllegalArgumentException("Method " + localMethod1 + " has @Subscribe annotation, but requires " + arrayOfClass.length + " arguments.  Event handler methods must require a single argument.");
            }
            Class localClass2 = arrayOfClass[0];
            localBuilder.add(localMethod1);
            break;
          }
        }
        catch (NoSuchMethodException localNoSuchMethodException) {}
      }
    }
    return localBuilder.build();
  }
  
  private static EventHandler makeHandler(Object paramObject, Method paramMethod)
  {
    Object localObject;
    if (methodIsDeclaredThreadSafe(paramMethod)) {
      localObject = new EventHandler(paramObject, paramMethod);
    } else {
      localObject = new SynchronizedEventHandler(paramObject, paramMethod);
    }
    return (EventHandler)localObject;
  }
  
  private static boolean methodIsDeclaredThreadSafe(Method paramMethod)
  {
    return paramMethod.getAnnotation(AllowConcurrentEvents.class) != null;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\eventbus\AnnotatedHandlerFinder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */