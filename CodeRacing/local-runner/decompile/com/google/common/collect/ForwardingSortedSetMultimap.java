package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.SortedSet;

@GwtCompatible
public abstract class ForwardingSortedSetMultimap
  extends ForwardingSetMultimap
  implements SortedSetMultimap
{
  protected abstract SortedSetMultimap delegate();
  
  public SortedSet get(Object paramObject)
  {
    return delegate().get(paramObject);
  }
  
  public SortedSet removeAll(Object paramObject)
  {
    return delegate().removeAll(paramObject);
  }
  
  public SortedSet replaceValues(Object paramObject, Iterable paramIterable)
  {
    return delegate().replaceValues(paramObject, paramIterable);
  }
  
  public Comparator valueComparator()
  {
    return delegate().valueComparator();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ForwardingSortedSetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */