package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.io.Serializable;
import java.util.Iterator;
import java.util.SortedSet;

@GwtCompatible
public final class Range
  implements Predicate, Serializable
{
  private static final Function LOWER_BOUND_FN = new Function()
  {
    public Cut apply(Range paramAnonymousRange)
    {
      return paramAnonymousRange.lowerBound;
    }
  };
  private static final Function UPPER_BOUND_FN = new Function()
  {
    public Cut apply(Range paramAnonymousRange)
    {
      return paramAnonymousRange.upperBound;
    }
  };
  static final Ordering RANGE_LEX_ORDERING = new Ordering()
  {
    public int compare(Range paramAnonymousRange1, Range paramAnonymousRange2)
    {
      return ComparisonChain.start().compare(paramAnonymousRange1.lowerBound, paramAnonymousRange2.lowerBound).compare(paramAnonymousRange1.upperBound, paramAnonymousRange2.upperBound).result();
    }
  };
  private static final Range ALL = new Range(Cut.belowAll(), Cut.aboveAll());
  final Cut lowerBound;
  final Cut upperBound;
  private static final long serialVersionUID = 0L;
  
  static Function lowerBoundFn()
  {
    return LOWER_BOUND_FN;
  }
  
  static Function upperBoundFn()
  {
    return UPPER_BOUND_FN;
  }
  
  static Range create(Cut paramCut1, Cut paramCut2)
  {
    return new Range(paramCut1, paramCut2);
  }
  
  public static Range open(Comparable paramComparable1, Comparable paramComparable2)
  {
    return create(Cut.aboveValue(paramComparable1), Cut.belowValue(paramComparable2));
  }
  
  public static Range closed(Comparable paramComparable1, Comparable paramComparable2)
  {
    return create(Cut.belowValue(paramComparable1), Cut.aboveValue(paramComparable2));
  }
  
  public static Range closedOpen(Comparable paramComparable1, Comparable paramComparable2)
  {
    return create(Cut.belowValue(paramComparable1), Cut.belowValue(paramComparable2));
  }
  
  public static Range openClosed(Comparable paramComparable1, Comparable paramComparable2)
  {
    return create(Cut.aboveValue(paramComparable1), Cut.aboveValue(paramComparable2));
  }
  
  public static Range range(Comparable paramComparable1, BoundType paramBoundType1, Comparable paramComparable2, BoundType paramBoundType2)
  {
    Preconditions.checkNotNull(paramBoundType1);
    Preconditions.checkNotNull(paramBoundType2);
    Cut localCut1 = paramBoundType1 == BoundType.OPEN ? Cut.aboveValue(paramComparable1) : Cut.belowValue(paramComparable1);
    Cut localCut2 = paramBoundType2 == BoundType.OPEN ? Cut.belowValue(paramComparable2) : Cut.aboveValue(paramComparable2);
    return create(localCut1, localCut2);
  }
  
  public static Range lessThan(Comparable paramComparable)
  {
    return create(Cut.belowAll(), Cut.belowValue(paramComparable));
  }
  
  public static Range atMost(Comparable paramComparable)
  {
    return create(Cut.belowAll(), Cut.aboveValue(paramComparable));
  }
  
  public static Range upTo(Comparable paramComparable, BoundType paramBoundType)
  {
    switch (paramBoundType)
    {
    case OPEN: 
      return lessThan(paramComparable);
    case CLOSED: 
      return atMost(paramComparable);
    }
    throw new AssertionError();
  }
  
  public static Range greaterThan(Comparable paramComparable)
  {
    return create(Cut.aboveValue(paramComparable), Cut.aboveAll());
  }
  
  public static Range atLeast(Comparable paramComparable)
  {
    return create(Cut.belowValue(paramComparable), Cut.aboveAll());
  }
  
  public static Range downTo(Comparable paramComparable, BoundType paramBoundType)
  {
    switch (paramBoundType)
    {
    case OPEN: 
      return greaterThan(paramComparable);
    case CLOSED: 
      return atLeast(paramComparable);
    }
    throw new AssertionError();
  }
  
  public static Range all()
  {
    return ALL;
  }
  
  public static Range singleton(Comparable paramComparable)
  {
    return closed(paramComparable, paramComparable);
  }
  
  public static Range encloseAll(Iterable paramIterable)
  {
    Preconditions.checkNotNull(paramIterable);
    if ((paramIterable instanceof ContiguousSet)) {
      return ((ContiguousSet)paramIterable).range();
    }
    Iterator localIterator = paramIterable.iterator();
    Comparable localComparable1 = (Comparable)Preconditions.checkNotNull(localIterator.next());
    Comparable localComparable3;
    for (Comparable localComparable2 = localComparable1; localIterator.hasNext(); localComparable2 = (Comparable)Ordering.natural().max(localComparable2, localComparable3))
    {
      localComparable3 = (Comparable)Preconditions.checkNotNull(localIterator.next());
      localComparable1 = (Comparable)Ordering.natural().min(localComparable1, localComparable3);
    }
    return closed(localComparable1, localComparable2);
  }
  
  private Range(Cut paramCut1, Cut paramCut2)
  {
    if ((paramCut1.compareTo(paramCut2) > 0) || (paramCut1 == Cut.aboveAll()) || (paramCut2 == Cut.belowAll())) {
      throw new IllegalArgumentException("Invalid range: " + toString(paramCut1, paramCut2));
    }
    this.lowerBound = ((Cut)Preconditions.checkNotNull(paramCut1));
    this.upperBound = ((Cut)Preconditions.checkNotNull(paramCut2));
  }
  
  public boolean hasLowerBound()
  {
    return this.lowerBound != Cut.belowAll();
  }
  
  public Comparable lowerEndpoint()
  {
    return this.lowerBound.endpoint();
  }
  
  public BoundType lowerBoundType()
  {
    return this.lowerBound.typeAsLowerBound();
  }
  
  public boolean hasUpperBound()
  {
    return this.upperBound != Cut.aboveAll();
  }
  
  public Comparable upperEndpoint()
  {
    return this.upperBound.endpoint();
  }
  
  public BoundType upperBoundType()
  {
    return this.upperBound.typeAsUpperBound();
  }
  
  public boolean isEmpty()
  {
    return this.lowerBound.equals(this.upperBound);
  }
  
  public boolean contains(Comparable paramComparable)
  {
    Preconditions.checkNotNull(paramComparable);
    return (this.lowerBound.isLessThan(paramComparable)) && (!this.upperBound.isLessThan(paramComparable));
  }
  
  public boolean apply(Comparable paramComparable)
  {
    return contains(paramComparable);
  }
  
  public boolean containsAll(Iterable paramIterable)
  {
    if (Iterables.isEmpty(paramIterable)) {
      return true;
    }
    Object localObject2;
    if ((paramIterable instanceof SortedSet))
    {
      localObject1 = cast(paramIterable);
      localObject2 = ((SortedSet)localObject1).comparator();
      if ((Ordering.natural().equals(localObject2)) || (localObject2 == null)) {
        return (contains((Comparable)((SortedSet)localObject1).first())) && (contains((Comparable)((SortedSet)localObject1).last()));
      }
    }
    Object localObject1 = paramIterable.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Comparable)((Iterator)localObject1).next();
      if (!contains((Comparable)localObject2)) {
        return false;
      }
    }
    return true;
  }
  
  public boolean encloses(Range paramRange)
  {
    return (this.lowerBound.compareTo(paramRange.lowerBound) <= 0) && (this.upperBound.compareTo(paramRange.upperBound) >= 0);
  }
  
  public boolean isConnected(Range paramRange)
  {
    return (this.lowerBound.compareTo(paramRange.upperBound) <= 0) && (paramRange.lowerBound.compareTo(this.upperBound) <= 0);
  }
  
  public Range intersection(Range paramRange)
  {
    int i = this.lowerBound.compareTo(paramRange.lowerBound);
    int j = this.upperBound.compareTo(paramRange.upperBound);
    if ((i >= 0) && (j <= 0)) {
      return this;
    }
    if ((i <= 0) && (j >= 0)) {
      return paramRange;
    }
    Cut localCut1 = i >= 0 ? this.lowerBound : paramRange.lowerBound;
    Cut localCut2 = j <= 0 ? this.upperBound : paramRange.upperBound;
    return create(localCut1, localCut2);
  }
  
  public Range span(Range paramRange)
  {
    int i = this.lowerBound.compareTo(paramRange.lowerBound);
    int j = this.upperBound.compareTo(paramRange.upperBound);
    if ((i <= 0) && (j >= 0)) {
      return this;
    }
    if ((i >= 0) && (j <= 0)) {
      return paramRange;
    }
    Cut localCut1 = i <= 0 ? this.lowerBound : paramRange.lowerBound;
    Cut localCut2 = j >= 0 ? this.upperBound : paramRange.upperBound;
    return create(localCut1, localCut2);
  }
  
  @Beta
  @GwtCompatible(serializable=false)
  @Deprecated
  public ContiguousSet asSet(DiscreteDomain paramDiscreteDomain)
  {
    return ContiguousSet.create(this, paramDiscreteDomain);
  }
  
  public Range canonical(DiscreteDomain paramDiscreteDomain)
  {
    Preconditions.checkNotNull(paramDiscreteDomain);
    Cut localCut1 = this.lowerBound.canonical(paramDiscreteDomain);
    Cut localCut2 = this.upperBound.canonical(paramDiscreteDomain);
    return (localCut1 == this.lowerBound) && (localCut2 == this.upperBound) ? this : create(localCut1, localCut2);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Range))
    {
      Range localRange = (Range)paramObject;
      return (this.lowerBound.equals(localRange.lowerBound)) && (this.upperBound.equals(localRange.upperBound));
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.lowerBound.hashCode() * 31 + this.upperBound.hashCode();
  }
  
  public String toString()
  {
    return toString(this.lowerBound, this.upperBound);
  }
  
  private static String toString(Cut paramCut1, Cut paramCut2)
  {
    StringBuilder localStringBuilder = new StringBuilder(16);
    paramCut1.describeAsLowerBound(localStringBuilder);
    localStringBuilder.append('‥');
    paramCut2.describeAsUpperBound(localStringBuilder);
    return localStringBuilder.toString();
  }
  
  private static SortedSet cast(Iterable paramIterable)
  {
    return (SortedSet)paramIterable;
  }
  
  Object readResolve()
  {
    if (equals(ALL)) {
      return all();
    }
    return this;
  }
  
  static int compareOrThrow(Comparable paramComparable1, Comparable paramComparable2)
  {
    return paramComparable1.compareTo(paramComparable2);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Range.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */