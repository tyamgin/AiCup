package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;

@GwtCompatible
abstract class AbstractSortedSetMultimap
  extends AbstractSetMultimap
  implements SortedSetMultimap
{
  private static final long serialVersionUID = 430848587173315748L;
  
  protected AbstractSortedSetMultimap(Map paramMap)
  {
    super(paramMap);
  }
  
  abstract SortedSet createCollection();
  
  SortedSet createUnmodifiableEmptyCollection()
  {
    Comparator localComparator = valueComparator();
    if (localComparator == null) {
      return Collections.unmodifiableSortedSet(createCollection());
    }
    return ImmutableSortedSet.emptySet(valueComparator());
  }
  
  public SortedSet get(Object paramObject)
  {
    return (SortedSet)super.get(paramObject);
  }
  
  public SortedSet removeAll(Object paramObject)
  {
    return (SortedSet)super.removeAll(paramObject);
  }
  
  public SortedSet replaceValues(Object paramObject, Iterable paramIterable)
  {
    return (SortedSet)super.replaceValues(paramObject, paramIterable);
  }
  
  public Map asMap()
  {
    return super.asMap();
  }
  
  public Collection values()
  {
    return super.values();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\AbstractSortedSetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */