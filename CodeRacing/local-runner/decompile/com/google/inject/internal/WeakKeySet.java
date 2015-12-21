package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.inject.Key;
import com.google.inject.internal.util.SourceProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

final class WeakKeySet
{
  private Map backingMap;
  private final Object lock;
  private final Cache evictionCache = CacheBuilder.newBuilder().weakKeys().removalListener(new RemovalListener()
  {
    public void onRemoval(RemovalNotification paramAnonymousRemovalNotification)
    {
      Preconditions.checkState(RemovalCause.COLLECTED.equals(paramAnonymousRemovalNotification.getCause()));
      WeakKeySet.this.cleanUpForCollectedState((Set)paramAnonymousRemovalNotification.getValue());
    }
  }).build();
  
  private void cleanUpForCollectedState(Set paramSet)
  {
    synchronized (this.lock)
    {
      Iterator localIterator = paramSet.iterator();
      while (localIterator.hasNext())
      {
        KeyAndSource localKeyAndSource = (KeyAndSource)localIterator.next();
        Multiset localMultiset = (Multiset)this.backingMap.get(localKeyAndSource.key);
        if (localMultiset != null)
        {
          localMultiset.remove(localKeyAndSource.source);
          if (localMultiset.isEmpty()) {
            this.backingMap.remove(localKeyAndSource.key);
          }
        }
      }
    }
  }
  
  WeakKeySet(Object paramObject)
  {
    this.lock = paramObject;
  }
  
  public void add(Key paramKey, State paramState, Object paramObject)
  {
    if (this.backingMap == null) {
      this.backingMap = Maps.newHashMap();
    }
    if (((paramObject instanceof Class)) || (paramObject == SourceProvider.UNKNOWN_SOURCE)) {
      paramObject = null;
    }
    Object localObject1 = (Multiset)this.backingMap.get(paramKey);
    if (localObject1 == null)
    {
      localObject1 = LinkedHashMultiset.create();
      this.backingMap.put(paramKey, localObject1);
    }
    Object localObject2 = Errors.convert(paramObject);
    ((Multiset)localObject1).add(localObject2);
    if (paramState.parent() != State.NONE)
    {
      Object localObject3 = (Set)this.evictionCache.getIfPresent(paramState);
      if (localObject3 == null) {
        this.evictionCache.put(paramState, localObject3 = Sets.newHashSet());
      }
      ((Set)localObject3).add(new KeyAndSource(paramKey, localObject2));
    }
  }
  
  public boolean contains(Key paramKey)
  {
    this.evictionCache.cleanUp();
    return (this.backingMap != null) && (this.backingMap.containsKey(paramKey));
  }
  
  public Set getSources(Key paramKey)
  {
    this.evictionCache.cleanUp();
    Multiset localMultiset = this.backingMap == null ? null : (Multiset)this.backingMap.get(paramKey);
    return localMultiset == null ? null : localMultiset.elementSet();
  }
  
  private static final class KeyAndSource
  {
    final Key key;
    final Object source;
    
    KeyAndSource(Key paramKey, Object paramObject)
    {
      this.key = paramKey;
      this.source = paramObject;
    }
    
    public int hashCode()
    {
      return Objects.hashCode(new Object[] { this.key, this.source });
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof KeyAndSource)) {
        return false;
      }
      KeyAndSource localKeyAndSource = (KeyAndSource)paramObject;
      return (Objects.equal(this.key, localKeyAndSource.key)) && (Objects.equal(this.source, localKeyAndSource.source));
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\WeakKeySet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */