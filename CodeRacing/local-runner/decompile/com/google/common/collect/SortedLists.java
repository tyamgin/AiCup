package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

@GwtCompatible
@Beta
final class SortedLists
{
  public static int binarySearch(List paramList, Comparable paramComparable, KeyPresentBehavior paramKeyPresentBehavior, KeyAbsentBehavior paramKeyAbsentBehavior)
  {
    Preconditions.checkNotNull(paramComparable);
    return binarySearch(paramList, Preconditions.checkNotNull(paramComparable), Ordering.natural(), paramKeyPresentBehavior, paramKeyAbsentBehavior);
  }
  
  public static int binarySearch(List paramList, Function paramFunction, Comparable paramComparable, KeyPresentBehavior paramKeyPresentBehavior, KeyAbsentBehavior paramKeyAbsentBehavior)
  {
    return binarySearch(paramList, paramFunction, paramComparable, Ordering.natural(), paramKeyPresentBehavior, paramKeyAbsentBehavior);
  }
  
  public static int binarySearch(List paramList, Function paramFunction, Object paramObject, Comparator paramComparator, KeyPresentBehavior paramKeyPresentBehavior, KeyAbsentBehavior paramKeyAbsentBehavior)
  {
    return binarySearch(Lists.transform(paramList, paramFunction), paramObject, paramComparator, paramKeyPresentBehavior, paramKeyAbsentBehavior);
  }
  
  public static int binarySearch(List paramList, Object paramObject, Comparator paramComparator, KeyPresentBehavior paramKeyPresentBehavior, KeyAbsentBehavior paramKeyAbsentBehavior)
  {
    Preconditions.checkNotNull(paramComparator);
    Preconditions.checkNotNull(paramList);
    Preconditions.checkNotNull(paramKeyPresentBehavior);
    Preconditions.checkNotNull(paramKeyAbsentBehavior);
    if (!(paramList instanceof RandomAccess)) {
      paramList = Lists.newArrayList(paramList);
    }
    int i = 0;
    int j = paramList.size() - 1;
    while (i <= j)
    {
      int k = i + j >>> 1;
      int m = paramComparator.compare(paramObject, paramList.get(k));
      if (m < 0) {
        j = k - 1;
      } else if (m > 0) {
        i = k + 1;
      } else {
        return i + paramKeyPresentBehavior.resultIndex(paramComparator, paramObject, paramList.subList(i, j + 1), k - i);
      }
    }
    return paramKeyAbsentBehavior.resultIndex(i);
  }
  
  public static abstract enum KeyAbsentBehavior
  {
    NEXT_LOWER,  NEXT_HIGHER,  INVERTED_INSERTION_INDEX;
    
    abstract int resultIndex(int paramInt);
  }
  
  public static abstract enum KeyPresentBehavior
  {
    ANY_PRESENT,  LAST_PRESENT,  FIRST_PRESENT,  FIRST_AFTER,  LAST_BEFORE;
    
    abstract int resultIndex(Comparator paramComparator, Object paramObject, List paramList, int paramInt);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\SortedLists.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */