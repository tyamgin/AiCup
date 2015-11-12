package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;

@GwtCompatible
public abstract interface SortedSetMultimap
  extends SetMultimap
{
  public abstract SortedSet get(Object paramObject);
  
  public abstract SortedSet removeAll(Object paramObject);
  
  public abstract SortedSet replaceValues(Object paramObject, Iterable paramIterable);
  
  public abstract Map asMap();
  
  public abstract Comparator valueComparator();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\SortedSetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */