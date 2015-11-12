package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@GwtCompatible
public abstract interface Multimap
{
  public abstract int size();
  
  public abstract boolean isEmpty();
  
  public abstract boolean containsKey(Object paramObject);
  
  public abstract boolean containsValue(Object paramObject);
  
  public abstract boolean containsEntry(Object paramObject1, Object paramObject2);
  
  public abstract boolean put(Object paramObject1, Object paramObject2);
  
  public abstract boolean remove(Object paramObject1, Object paramObject2);
  
  public abstract boolean putAll(Object paramObject, Iterable paramIterable);
  
  public abstract boolean putAll(Multimap paramMultimap);
  
  public abstract Collection replaceValues(Object paramObject, Iterable paramIterable);
  
  public abstract Collection removeAll(Object paramObject);
  
  public abstract void clear();
  
  public abstract Collection get(Object paramObject);
  
  public abstract Set keySet();
  
  public abstract Multiset keys();
  
  public abstract Collection values();
  
  public abstract Collection entries();
  
  public abstract Map asMap();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Multimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */