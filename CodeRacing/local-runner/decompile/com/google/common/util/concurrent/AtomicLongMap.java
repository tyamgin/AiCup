package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@GwtCompatible
public final class AtomicLongMap
{
  private final ConcurrentHashMap map;
  private transient Map asMap;
  
  private AtomicLongMap(ConcurrentHashMap paramConcurrentHashMap)
  {
    this.map = ((ConcurrentHashMap)Preconditions.checkNotNull(paramConcurrentHashMap));
  }
  
  public static AtomicLongMap create()
  {
    return new AtomicLongMap(new ConcurrentHashMap());
  }
  
  public static AtomicLongMap create(Map paramMap)
  {
    AtomicLongMap localAtomicLongMap = create();
    localAtomicLongMap.putAll(paramMap);
    return localAtomicLongMap;
  }
  
  public long get(Object paramObject)
  {
    AtomicLong localAtomicLong = (AtomicLong)this.map.get(paramObject);
    return localAtomicLong == null ? 0L : localAtomicLong.get();
  }
  
  public long incrementAndGet(Object paramObject)
  {
    return addAndGet(paramObject, 1L);
  }
  
  public long decrementAndGet(Object paramObject)
  {
    return addAndGet(paramObject, -1L);
  }
  
  public long addAndGet(Object paramObject, long paramLong)
  {
    AtomicLong localAtomicLong = (AtomicLong)this.map.get(paramObject);
    if (localAtomicLong == null)
    {
      localAtomicLong = (AtomicLong)this.map.putIfAbsent(paramObject, new AtomicLong(paramLong));
      if (localAtomicLong == null) {
        return paramLong;
      }
    }
    for (;;)
    {
      long l1 = localAtomicLong.get();
      if (l1 == 0L)
      {
        if (!this.map.replace(paramObject, localAtomicLong, new AtomicLong(paramLong))) {
          break;
        }
        return paramLong;
      }
      long l2 = l1 + paramLong;
      if (localAtomicLong.compareAndSet(l1, l2)) {
        return l2;
      }
    }
  }
  
  public long getAndIncrement(Object paramObject)
  {
    return getAndAdd(paramObject, 1L);
  }
  
  public long getAndDecrement(Object paramObject)
  {
    return getAndAdd(paramObject, -1L);
  }
  
  public long getAndAdd(Object paramObject, long paramLong)
  {
    AtomicLong localAtomicLong = (AtomicLong)this.map.get(paramObject);
    if (localAtomicLong == null)
    {
      localAtomicLong = (AtomicLong)this.map.putIfAbsent(paramObject, new AtomicLong(paramLong));
      if (localAtomicLong == null) {
        return 0L;
      }
    }
    for (;;)
    {
      long l1 = localAtomicLong.get();
      if (l1 == 0L)
      {
        if (!this.map.replace(paramObject, localAtomicLong, new AtomicLong(paramLong))) {
          break;
        }
        return 0L;
      }
      long l2 = l1 + paramLong;
      if (localAtomicLong.compareAndSet(l1, l2)) {
        return l1;
      }
    }
  }
  
  public long put(Object paramObject, long paramLong)
  {
    AtomicLong localAtomicLong = (AtomicLong)this.map.get(paramObject);
    if (localAtomicLong == null)
    {
      localAtomicLong = (AtomicLong)this.map.putIfAbsent(paramObject, new AtomicLong(paramLong));
      if (localAtomicLong == null) {
        return 0L;
      }
    }
    for (;;)
    {
      long l = localAtomicLong.get();
      if (l == 0L)
      {
        if (!this.map.replace(paramObject, localAtomicLong, new AtomicLong(paramLong))) {
          break;
        }
        return 0L;
      }
      if (localAtomicLong.compareAndSet(l, paramLong)) {
        return l;
      }
    }
  }
  
  public void putAll(Map paramMap)
  {
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      put(localEntry.getKey(), ((Long)localEntry.getValue()).longValue());
    }
  }
  
  public long remove(Object paramObject)
  {
    AtomicLong localAtomicLong = (AtomicLong)this.map.get(paramObject);
    if (localAtomicLong == null) {
      return 0L;
    }
    for (;;)
    {
      long l = localAtomicLong.get();
      if ((l == 0L) || (localAtomicLong.compareAndSet(l, 0L)))
      {
        this.map.remove(paramObject, localAtomicLong);
        return l;
      }
    }
  }
  
  public void removeAllZeros()
  {
    Iterator localIterator = this.map.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      AtomicLong localAtomicLong = (AtomicLong)this.map.get(localObject);
      if ((localAtomicLong != null) && (localAtomicLong.get() == 0L)) {
        this.map.remove(localObject, localAtomicLong);
      }
    }
  }
  
  public long sum()
  {
    long l = 0L;
    Iterator localIterator = this.map.values().iterator();
    while (localIterator.hasNext())
    {
      AtomicLong localAtomicLong = (AtomicLong)localIterator.next();
      l += localAtomicLong.get();
    }
    return l;
  }
  
  public Map asMap()
  {
    Map localMap = this.asMap;
    return localMap == null ? (this.asMap = createAsMap()) : localMap;
  }
  
  private Map createAsMap()
  {
    Collections.unmodifiableMap(Maps.transformValues(this.map, new Function()
    {
      public Long apply(AtomicLong paramAnonymousAtomicLong)
      {
        return Long.valueOf(paramAnonymousAtomicLong.get());
      }
    }));
  }
  
  public boolean containsKey(Object paramObject)
  {
    return this.map.containsKey(paramObject);
  }
  
  public int size()
  {
    return this.map.size();
  }
  
  public boolean isEmpty()
  {
    return this.map.isEmpty();
  }
  
  public void clear()
  {
    this.map.clear();
  }
  
  public String toString()
  {
    return this.map.toString();
  }
  
  long putIfAbsent(Object paramObject, long paramLong)
  {
    AtomicLong localAtomicLong;
    long l;
    do
    {
      localAtomicLong = (AtomicLong)this.map.get(paramObject);
      if (localAtomicLong == null)
      {
        localAtomicLong = (AtomicLong)this.map.putIfAbsent(paramObject, new AtomicLong(paramLong));
        if (localAtomicLong == null) {
          return 0L;
        }
      }
      l = localAtomicLong.get();
      if (l != 0L) {
        break;
      }
    } while (!this.map.replace(paramObject, localAtomicLong, new AtomicLong(paramLong)));
    return 0L;
    return l;
  }
  
  boolean replace(Object paramObject, long paramLong1, long paramLong2)
  {
    if (paramLong1 == 0L) {
      return putIfAbsent(paramObject, paramLong2) == 0L;
    }
    AtomicLong localAtomicLong = (AtomicLong)this.map.get(paramObject);
    return localAtomicLong == null ? false : localAtomicLong.compareAndSet(paramLong1, paramLong2);
  }
  
  boolean remove(Object paramObject, long paramLong)
  {
    AtomicLong localAtomicLong = (AtomicLong)this.map.get(paramObject);
    if (localAtomicLong == null) {
      return false;
    }
    long l = localAtomicLong.get();
    if (l != paramLong) {
      return false;
    }
    if ((l == 0L) || (localAtomicLong.compareAndSet(l, 0L)))
    {
      this.map.remove(paramObject, localAtomicLong);
      return true;
    }
    return false;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\AtomicLongMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */