package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingObject;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@Beta
public abstract class ForwardingCache
  extends ForwardingObject
  implements Cache
{
  protected abstract Cache delegate();
  
  public Object getIfPresent(Object paramObject)
  {
    return delegate().getIfPresent(paramObject);
  }
  
  public Object get(Object paramObject, Callable paramCallable)
    throws ExecutionException
  {
    return delegate().get(paramObject, paramCallable);
  }
  
  public ImmutableMap getAllPresent(Iterable paramIterable)
  {
    return delegate().getAllPresent(paramIterable);
  }
  
  public void put(Object paramObject1, Object paramObject2)
  {
    delegate().put(paramObject1, paramObject2);
  }
  
  public void putAll(Map paramMap)
  {
    delegate().putAll(paramMap);
  }
  
  public void invalidate(Object paramObject)
  {
    delegate().invalidate(paramObject);
  }
  
  public void invalidateAll(Iterable paramIterable)
  {
    delegate().invalidateAll(paramIterable);
  }
  
  public void invalidateAll()
  {
    delegate().invalidateAll();
  }
  
  public long size()
  {
    return delegate().size();
  }
  
  public CacheStats stats()
  {
    return delegate().stats();
  }
  
  public ConcurrentMap asMap()
  {
    return delegate().asMap();
  }
  
  public void cleanUp()
  {
    delegate().cleanUp();
  }
  
  @Beta
  public static abstract class SimpleForwardingCache
    extends ForwardingCache
  {
    private final Cache delegate;
    
    protected SimpleForwardingCache(Cache paramCache)
    {
      this.delegate = ((Cache)Preconditions.checkNotNull(paramCache));
    }
    
    protected final Cache delegate()
    {
      return this.delegate;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\cache\ForwardingCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */