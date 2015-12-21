package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;

@Beta
@GwtCompatible(emulated=true)
public abstract interface SortedMultiset
  extends SortedIterable, SortedMultisetBridge
{
  public abstract Comparator comparator();
  
  public abstract Multiset.Entry firstEntry();
  
  public abstract Multiset.Entry lastEntry();
  
  public abstract Multiset.Entry pollFirstEntry();
  
  public abstract Multiset.Entry pollLastEntry();
  
  public abstract NavigableSet elementSet();
  
  public abstract Iterator iterator();
  
  public abstract SortedMultiset descendingMultiset();
  
  public abstract SortedMultiset headMultiset(Object paramObject, BoundType paramBoundType);
  
  public abstract SortedMultiset subMultiset(Object paramObject1, BoundType paramBoundType1, Object paramObject2, BoundType paramBoundType2);
  
  public abstract SortedMultiset tailMultiset(Object paramObject, BoundType paramBoundType);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\SortedMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */