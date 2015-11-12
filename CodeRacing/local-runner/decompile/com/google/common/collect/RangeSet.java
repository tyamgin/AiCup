package com.google.common.collect;

import com.google.common.annotations.Beta;
import java.util.Set;

@Beta
public abstract interface RangeSet
{
  public abstract boolean contains(Comparable paramComparable);
  
  public abstract Range rangeContaining(Comparable paramComparable);
  
  public abstract boolean encloses(Range paramRange);
  
  public abstract boolean enclosesAll(RangeSet paramRangeSet);
  
  public abstract boolean isEmpty();
  
  public abstract Range span();
  
  public abstract Set asRanges();
  
  public abstract RangeSet complement();
  
  public abstract RangeSet subRangeSet(Range paramRange);
  
  public abstract void add(Range paramRange);
  
  public abstract void remove(Range paramRange);
  
  public abstract void clear();
  
  public abstract void addAll(RangeSet paramRangeSet);
  
  public abstract void removeAll(RangeSet paramRangeSet);
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\RangeSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */