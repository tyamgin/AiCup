package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.SortedSet;

@GwtCompatible
final class SortedIterables
{
  public static boolean hasSameComparator(Comparator paramComparator, Iterable paramIterable)
  {
    Preconditions.checkNotNull(paramComparator);
    Preconditions.checkNotNull(paramIterable);
    Comparator localComparator;
    if ((paramIterable instanceof SortedSet)) {
      localComparator = comparator((SortedSet)paramIterable);
    } else if ((paramIterable instanceof SortedIterable)) {
      localComparator = ((SortedIterable)paramIterable).comparator();
    } else {
      return false;
    }
    return paramComparator.equals(localComparator);
  }
  
  public static Comparator comparator(SortedSet paramSortedSet)
  {
    Object localObject = paramSortedSet.comparator();
    if (localObject == null) {
      localObject = Ordering.natural();
    }
    return (Comparator)localObject;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\SortedIterables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */