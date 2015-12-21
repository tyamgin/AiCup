package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@GwtCompatible(serializable=true)
final class ComparatorOrdering
  extends Ordering
  implements Serializable
{
  final Comparator comparator;
  private static final long serialVersionUID = 0L;
  
  ComparatorOrdering(Comparator paramComparator)
  {
    this.comparator = ((Comparator)Preconditions.checkNotNull(paramComparator));
  }
  
  public int compare(Object paramObject1, Object paramObject2)
  {
    return this.comparator.compare(paramObject1, paramObject2);
  }
  
  public int binarySearch(List paramList, Object paramObject)
  {
    return Collections.binarySearch(paramList, paramObject, this.comparator);
  }
  
  public List sortedCopy(Iterable paramIterable)
  {
    ArrayList localArrayList = Lists.newArrayList(paramIterable);
    Collections.sort(localArrayList, this.comparator);
    return localArrayList;
  }
  
  public ImmutableList immutableSortedCopy(Iterable paramIterable)
  {
    Object[] arrayOfObject1 = (Object[])Iterables.toArray(paramIterable);
    for (Object localObject : arrayOfObject1) {
      Preconditions.checkNotNull(localObject);
    }
    Arrays.sort(arrayOfObject1, this.comparator);
    return ImmutableList.asImmutableList(arrayOfObject1);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof ComparatorOrdering))
    {
      ComparatorOrdering localComparatorOrdering = (ComparatorOrdering)paramObject;
      return this.comparator.equals(localComparatorOrdering.comparator);
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.comparator.hashCode();
  }
  
  public String toString()
  {
    return this.comparator.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ComparatorOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */