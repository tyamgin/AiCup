package com.google.common.collect;

import com.google.common.annotations.Beta;
import java.util.Map;
import java.util.Map.Entry;

@Beta
public abstract interface RangeMap
{
  public abstract Object get(Comparable paramComparable);
  
  public abstract Map.Entry getEntry(Comparable paramComparable);
  
  public abstract Range span();
  
  public abstract void put(Range paramRange, Object paramObject);
  
  public abstract void putAll(RangeMap paramRangeMap);
  
  public abstract void clear();
  
  public abstract void remove(Range paramRange);
  
  public abstract Map asMapOfRanges();
  
  public abstract RangeMap subRangeMap(Range paramRange);
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\RangeMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */