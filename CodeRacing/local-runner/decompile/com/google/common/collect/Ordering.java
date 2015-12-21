package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@GwtCompatible
public abstract class Ordering
  implements Comparator
{
  static final int LEFT_IS_GREATER = 1;
  static final int RIGHT_IS_GREATER = -1;
  
  @GwtCompatible(serializable=true)
  public static Ordering natural()
  {
    return NaturalOrdering.INSTANCE;
  }
  
  @GwtCompatible(serializable=true)
  public static Ordering from(Comparator paramComparator)
  {
    return (paramComparator instanceof Ordering) ? (Ordering)paramComparator : new ComparatorOrdering(paramComparator);
  }
  
  @GwtCompatible(serializable=true)
  @Deprecated
  public static Ordering from(Ordering paramOrdering)
  {
    return (Ordering)Preconditions.checkNotNull(paramOrdering);
  }
  
  @GwtCompatible(serializable=true)
  public static Ordering explicit(List paramList)
  {
    return new ExplicitOrdering(paramList);
  }
  
  @GwtCompatible(serializable=true)
  public static Ordering explicit(Object paramObject, Object... paramVarArgs)
  {
    return explicit(Lists.asList(paramObject, paramVarArgs));
  }
  
  @GwtCompatible(serializable=true)
  public static Ordering allEqual()
  {
    return AllEqualOrdering.INSTANCE;
  }
  
  @GwtCompatible(serializable=true)
  public static Ordering usingToString()
  {
    return UsingToStringOrdering.INSTANCE;
  }
  
  public static Ordering arbitrary()
  {
    return ArbitraryOrderingHolder.ARBITRARY_ORDERING;
  }
  
  @GwtCompatible(serializable=true)
  public Ordering reverse()
  {
    return new ReverseOrdering(this);
  }
  
  @GwtCompatible(serializable=true)
  public Ordering nullsFirst()
  {
    return new NullsFirstOrdering(this);
  }
  
  @GwtCompatible(serializable=true)
  public Ordering nullsLast()
  {
    return new NullsLastOrdering(this);
  }
  
  @GwtCompatible(serializable=true)
  public Ordering onResultOf(Function paramFunction)
  {
    return new ByFunctionOrdering(paramFunction, this);
  }
  
  @GwtCompatible(serializable=true)
  public Ordering compound(Comparator paramComparator)
  {
    return new CompoundOrdering(this, (Comparator)Preconditions.checkNotNull(paramComparator));
  }
  
  @GwtCompatible(serializable=true)
  public static Ordering compound(Iterable paramIterable)
  {
    return new CompoundOrdering(paramIterable);
  }
  
  @GwtCompatible(serializable=true)
  public Ordering lexicographical()
  {
    return new LexicographicalOrdering(this);
  }
  
  public abstract int compare(Object paramObject1, Object paramObject2);
  
  public Object min(Iterator paramIterator)
  {
    for (Object localObject = paramIterator.next(); paramIterator.hasNext(); localObject = min(localObject, paramIterator.next())) {}
    return localObject;
  }
  
  public Object min(Iterable paramIterable)
  {
    return min(paramIterable.iterator());
  }
  
  public Object min(Object paramObject1, Object paramObject2)
  {
    return compare(paramObject1, paramObject2) <= 0 ? paramObject1 : paramObject2;
  }
  
  public Object min(Object paramObject1, Object paramObject2, Object paramObject3, Object... paramVarArgs)
  {
    Object localObject1 = min(min(paramObject1, paramObject2), paramObject3);
    for (Object localObject2 : paramVarArgs) {
      localObject1 = min(localObject1, localObject2);
    }
    return localObject1;
  }
  
  public Object max(Iterator paramIterator)
  {
    for (Object localObject = paramIterator.next(); paramIterator.hasNext(); localObject = max(localObject, paramIterator.next())) {}
    return localObject;
  }
  
  public Object max(Iterable paramIterable)
  {
    return max(paramIterable.iterator());
  }
  
  public Object max(Object paramObject1, Object paramObject2)
  {
    return compare(paramObject1, paramObject2) >= 0 ? paramObject1 : paramObject2;
  }
  
  public Object max(Object paramObject1, Object paramObject2, Object paramObject3, Object... paramVarArgs)
  {
    Object localObject1 = max(max(paramObject1, paramObject2), paramObject3);
    for (Object localObject2 : paramVarArgs) {
      localObject1 = max(localObject1, localObject2);
    }
    return localObject1;
  }
  
  public List leastOf(Iterable paramIterable, int paramInt)
  {
    if ((paramIterable instanceof Collection))
    {
      Collection localCollection = (Collection)paramIterable;
      if (localCollection.size() <= 2L * paramInt)
      {
        Object[] arrayOfObject = (Object[])localCollection.toArray();
        Arrays.sort(arrayOfObject, this);
        if (arrayOfObject.length > paramInt) {
          arrayOfObject = ObjectArrays.arraysCopyOf(arrayOfObject, paramInt);
        }
        return Collections.unmodifiableList(Arrays.asList(arrayOfObject));
      }
    }
    return leastOf(paramIterable.iterator(), paramInt);
  }
  
  public List leastOf(Iterator paramIterator, int paramInt)
  {
    Preconditions.checkNotNull(paramIterator);
    Preconditions.checkArgument(paramInt >= 0, "k (%s) must be nonnegative", new Object[] { Integer.valueOf(paramInt) });
    if ((paramInt == 0) || (!paramIterator.hasNext())) {
      return ImmutableList.of();
    }
    if (paramInt >= 1073741823)
    {
      ArrayList localArrayList = Lists.newArrayList(paramIterator);
      Collections.sort(localArrayList, this);
      if (localArrayList.size() > paramInt) {
        localArrayList.subList(paramInt, localArrayList.size()).clear();
      }
      localArrayList.trimToSize();
      return Collections.unmodifiableList(localArrayList);
    }
    int i = paramInt * 2;
    Object[] arrayOfObject = (Object[])new Object[i];
    Object localObject1 = paramIterator.next();
    arrayOfObject[0] = localObject1;
    int j = 1;
    Object localObject2;
    while ((j < paramInt) && (paramIterator.hasNext()))
    {
      localObject2 = paramIterator.next();
      arrayOfObject[(j++)] = localObject2;
      localObject1 = max(localObject1, localObject2);
    }
    while (paramIterator.hasNext())
    {
      localObject2 = paramIterator.next();
      if (compare(localObject2, localObject1) < 0)
      {
        arrayOfObject[(j++)] = localObject2;
        if (j == i)
        {
          int k = 0;
          int m = i - 1;
          int n = 0;
          while (k < m)
          {
            i1 = k + m + 1 >>> 1;
            int i2 = partition(arrayOfObject, k, m, i1);
            if (i2 > paramInt)
            {
              m = i2 - 1;
            }
            else
            {
              if (i2 >= paramInt) {
                break;
              }
              k = Math.max(i2, k + 1);
              n = i2;
            }
          }
          j = paramInt;
          localObject1 = arrayOfObject[n];
          for (int i1 = n + 1; i1 < j; i1++) {
            localObject1 = max(localObject1, arrayOfObject[i1]);
          }
        }
      }
    }
    Arrays.sort(arrayOfObject, 0, j, this);
    j = Math.min(j, paramInt);
    return Collections.unmodifiableList(Arrays.asList(ObjectArrays.arraysCopyOf(arrayOfObject, j)));
  }
  
  private int partition(Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject = paramArrayOfObject[paramInt3];
    paramArrayOfObject[paramInt3] = paramArrayOfObject[paramInt2];
    paramArrayOfObject[paramInt2] = localObject;
    int i = paramInt1;
    for (int j = paramInt1; j < paramInt2; j++) {
      if (compare(paramArrayOfObject[j], localObject) < 0)
      {
        ObjectArrays.swap(paramArrayOfObject, i, j);
        i++;
      }
    }
    ObjectArrays.swap(paramArrayOfObject, paramInt2, i);
    return i;
  }
  
  public List greatestOf(Iterable paramIterable, int paramInt)
  {
    return reverse().leastOf(paramIterable, paramInt);
  }
  
  public List greatestOf(Iterator paramIterator, int paramInt)
  {
    return reverse().leastOf(paramIterator, paramInt);
  }
  
  public List sortedCopy(Iterable paramIterable)
  {
    Object[] arrayOfObject = (Object[])Iterables.toArray(paramIterable);
    Arrays.sort(arrayOfObject, this);
    return Lists.newArrayList(Arrays.asList(arrayOfObject));
  }
  
  public ImmutableList immutableSortedCopy(Iterable paramIterable)
  {
    Object[] arrayOfObject1 = (Object[])Iterables.toArray(paramIterable);
    for (Object localObject : arrayOfObject1) {
      Preconditions.checkNotNull(localObject);
    }
    Arrays.sort(arrayOfObject1, this);
    return ImmutableList.asImmutableList(arrayOfObject1);
  }
  
  public boolean isOrdered(Iterable paramIterable)
  {
    Iterator localIterator = paramIterable.iterator();
    if (localIterator.hasNext())
    {
      Object localObject2;
      for (Object localObject1 = localIterator.next(); localIterator.hasNext(); localObject1 = localObject2)
      {
        localObject2 = localIterator.next();
        if (compare(localObject1, localObject2) > 0) {
          return false;
        }
      }
    }
    return true;
  }
  
  public boolean isStrictlyOrdered(Iterable paramIterable)
  {
    Iterator localIterator = paramIterable.iterator();
    if (localIterator.hasNext())
    {
      Object localObject2;
      for (Object localObject1 = localIterator.next(); localIterator.hasNext(); localObject1 = localObject2)
      {
        localObject2 = localIterator.next();
        if (compare(localObject1, localObject2) >= 0) {
          return false;
        }
      }
    }
    return true;
  }
  
  public int binarySearch(List paramList, Object paramObject)
  {
    return Collections.binarySearch(paramList, paramObject, this);
  }
  
  @VisibleForTesting
  static class IncomparableValueException
    extends ClassCastException
  {
    final Object value;
    private static final long serialVersionUID = 0L;
    
    IncomparableValueException(Object paramObject)
    {
      super();
      this.value = paramObject;
    }
  }
  
  @VisibleForTesting
  static class ArbitraryOrdering
    extends Ordering
  {
    private Map uids = Platform.tryWeakKeys(new MapMaker()).makeComputingMap(new Function()
    {
      final AtomicInteger counter = new AtomicInteger(0);
      
      public Integer apply(Object paramAnonymousObject)
      {
        return Integer.valueOf(this.counter.getAndIncrement());
      }
    });
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      if (paramObject1 == paramObject2) {
        return 0;
      }
      if (paramObject1 == null) {
        return -1;
      }
      if (paramObject2 == null) {
        return 1;
      }
      int i = identityHashCode(paramObject1);
      int j = identityHashCode(paramObject2);
      if (i != j) {
        return i < j ? -1 : 1;
      }
      int k = ((Integer)this.uids.get(paramObject1)).compareTo((Integer)this.uids.get(paramObject2));
      if (k == 0) {
        throw new AssertionError();
      }
      return k;
    }
    
    public String toString()
    {
      return "Ordering.arbitrary()";
    }
    
    int identityHashCode(Object paramObject)
    {
      return System.identityHashCode(paramObject);
    }
  }
  
  private static class ArbitraryOrderingHolder
  {
    static final Ordering ARBITRARY_ORDERING = new Ordering.ArbitraryOrdering();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Ordering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */