package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.NoSuchElementException;

@Beta
@GwtCompatible(emulated=true)
public abstract class ContiguousSet
  extends ImmutableSortedSet
{
  final DiscreteDomain domain;
  
  public static ContiguousSet create(Range paramRange, DiscreteDomain paramDiscreteDomain)
  {
    Preconditions.checkNotNull(paramRange);
    Preconditions.checkNotNull(paramDiscreteDomain);
    Range localRange = paramRange;
    try
    {
      if (!paramRange.hasLowerBound()) {
        localRange = localRange.intersection(Range.atLeast(paramDiscreteDomain.minValue()));
      }
      if (!paramRange.hasUpperBound()) {
        localRange = localRange.intersection(Range.atMost(paramDiscreteDomain.maxValue()));
      }
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      throw new IllegalArgumentException(localNoSuchElementException);
    }
    int i = (localRange.isEmpty()) || (Range.compareOrThrow(paramRange.lowerBound.leastValueAbove(paramDiscreteDomain), paramRange.upperBound.greatestValueBelow(paramDiscreteDomain)) > 0) ? 1 : 0;
    return i != 0 ? new EmptyContiguousSet(paramDiscreteDomain) : new RegularContiguousSet(localRange, paramDiscreteDomain);
  }
  
  ContiguousSet(DiscreteDomain paramDiscreteDomain)
  {
    super(Ordering.natural());
    this.domain = paramDiscreteDomain;
  }
  
  public ContiguousSet headSet(Comparable paramComparable)
  {
    return headSetImpl((Comparable)Preconditions.checkNotNull(paramComparable), false);
  }
  
  @GwtIncompatible("NavigableSet")
  public ContiguousSet headSet(Comparable paramComparable, boolean paramBoolean)
  {
    return headSetImpl((Comparable)Preconditions.checkNotNull(paramComparable), paramBoolean);
  }
  
  public ContiguousSet subSet(Comparable paramComparable1, Comparable paramComparable2)
  {
    Preconditions.checkNotNull(paramComparable1);
    Preconditions.checkNotNull(paramComparable2);
    Preconditions.checkArgument(comparator().compare(paramComparable1, paramComparable2) <= 0);
    return subSetImpl(paramComparable1, true, paramComparable2, false);
  }
  
  @GwtIncompatible("NavigableSet")
  public ContiguousSet subSet(Comparable paramComparable1, boolean paramBoolean1, Comparable paramComparable2, boolean paramBoolean2)
  {
    Preconditions.checkNotNull(paramComparable1);
    Preconditions.checkNotNull(paramComparable2);
    Preconditions.checkArgument(comparator().compare(paramComparable1, paramComparable2) <= 0);
    return subSetImpl(paramComparable1, paramBoolean1, paramComparable2, paramBoolean2);
  }
  
  public ContiguousSet tailSet(Comparable paramComparable)
  {
    return tailSetImpl((Comparable)Preconditions.checkNotNull(paramComparable), true);
  }
  
  @GwtIncompatible("NavigableSet")
  public ContiguousSet tailSet(Comparable paramComparable, boolean paramBoolean)
  {
    return tailSetImpl((Comparable)Preconditions.checkNotNull(paramComparable), paramBoolean);
  }
  
  abstract ContiguousSet headSetImpl(Comparable paramComparable, boolean paramBoolean);
  
  abstract ContiguousSet subSetImpl(Comparable paramComparable1, boolean paramBoolean1, Comparable paramComparable2, boolean paramBoolean2);
  
  abstract ContiguousSet tailSetImpl(Comparable paramComparable, boolean paramBoolean);
  
  public abstract ContiguousSet intersection(ContiguousSet paramContiguousSet);
  
  public abstract Range range();
  
  public abstract Range range(BoundType paramBoundType1, BoundType paramBoundType2);
  
  public String toString()
  {
    return range().toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ContiguousSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */