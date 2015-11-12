package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@Beta
@GwtCompatible
public abstract class AbstractCache
  implements Cache
{
  public Object get(Object paramObject, Callable paramCallable)
    throws ExecutionException
  {
    throw new UnsupportedOperationException();
  }
  
  public ImmutableMap getAllPresent(Iterable paramIterable)
  {
    LinkedHashMap localLinkedHashMap = Maps.newLinkedHashMap();
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Object localObject1 = localIterator.next();
      if (!localLinkedHashMap.containsKey(localObject1))
      {
        Object localObject2 = localObject1;
        localLinkedHashMap.put(localObject2, getIfPresent(localObject1));
      }
    }
    return ImmutableMap.copyOf(localLinkedHashMap);
  }
  
  public void put(Object paramObject1, Object paramObject2)
  {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map paramMap)
  {
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      put(localEntry.getKey(), localEntry.getValue());
    }
  }
  
  public void cleanUp() {}
  
  public long size()
  {
    throw new UnsupportedOperationException();
  }
  
  public void invalidate(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  public void invalidateAll(Iterable paramIterable)
  {
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      invalidate(localObject);
    }
  }
  
  public void invalidateAll()
  {
    throw new UnsupportedOperationException();
  }
  
  public CacheStats stats()
  {
    throw new UnsupportedOperationException();
  }
  
  public ConcurrentMap asMap()
  {
    throw new UnsupportedOperationException();
  }
  
  @Beta
  public static final class SimpleStatsCounter
    implements AbstractCache.StatsCounter
  {
    private final LongAddable hitCount = LongAddables.create();
    private final LongAddable missCount = LongAddables.create();
    private final LongAddable loadSuccessCount = LongAddables.create();
    private final LongAddable loadExceptionCount = LongAddables.create();
    private final LongAddable totalLoadTime = LongAddables.create();
    private final LongAddable evictionCount = LongAddables.create();
    
    public void recordHits(int paramInt)
    {
      this.hitCount.add(paramInt);
    }
    
    public void recordMisses(int paramInt)
    {
      this.missCount.add(paramInt);
    }
    
    public void recordLoadSuccess(long paramLong)
    {
      this.loadSuccessCount.increment();
      this.totalLoadTime.add(paramLong);
    }
    
    public void recordLoadException(long paramLong)
    {
      this.loadExceptionCount.increment();
      this.totalLoadTime.add(paramLong);
    }
    
    public void recordEviction()
    {
      this.evictionCount.increment();
    }
    
    public CacheStats snapshot()
    {
      return new CacheStats(this.hitCount.sum(), this.missCount.sum(), this.loadSuccessCount.sum(), this.loadExceptionCount.sum(), this.totalLoadTime.sum(), this.evictionCount.sum());
    }
    
    public void incrementBy(AbstractCache.StatsCounter paramStatsCounter)
    {
      CacheStats localCacheStats = paramStatsCounter.snapshot();
      this.hitCount.add(localCacheStats.hitCount());
      this.missCount.add(localCacheStats.missCount());
      this.loadSuccessCount.add(localCacheStats.loadSuccessCount());
      this.loadExceptionCount.add(localCacheStats.loadExceptionCount());
      this.totalLoadTime.add(localCacheStats.totalLoadTime());
      this.evictionCount.add(localCacheStats.evictionCount());
    }
  }
  
  @Beta
  public static abstract interface StatsCounter
  {
    public abstract void recordHits(int paramInt);
    
    public abstract void recordMisses(int paramInt);
    
    public abstract void recordLoadSuccess(long paramLong);
    
    public abstract void recordLoadException(long paramLong);
    
    public abstract void recordEviction();
    
    public abstract CacheStats snapshot();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\cache\AbstractCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */