package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

@Beta
public final class ImmutableRangeSet
  extends AbstractRangeSet
  implements Serializable
{
  private static final ImmutableRangeSet EMPTY = new ImmutableRangeSet(ImmutableList.of());
  private static final ImmutableRangeSet ALL = new ImmutableRangeSet(ImmutableList.of(Range.all()));
  private final transient ImmutableList ranges;
  private transient ImmutableRangeSet complement;
  
  public static ImmutableRangeSet of()
  {
    return EMPTY;
  }
  
  static ImmutableRangeSet all()
  {
    return ALL;
  }
  
  public static ImmutableRangeSet of(Range paramRange)
  {
    Preconditions.checkNotNull(paramRange);
    if (paramRange.isEmpty()) {
      return of();
    }
    if (paramRange.equals(Range.all())) {
      return all();
    }
    return new ImmutableRangeSet(ImmutableList.of(paramRange));
  }
  
  public static ImmutableRangeSet copyOf(RangeSet paramRangeSet)
  {
    Preconditions.checkNotNull(paramRangeSet);
    if (paramRangeSet.isEmpty()) {
      return of();
    }
    if (paramRangeSet.encloses(Range.all())) {
      return all();
    }
    if ((paramRangeSet instanceof ImmutableRangeSet))
    {
      ImmutableRangeSet localImmutableRangeSet = (ImmutableRangeSet)paramRangeSet;
      if (!localImmutableRangeSet.isPartialView()) {
        return localImmutableRangeSet;
      }
    }
    return new ImmutableRangeSet(ImmutableList.copyOf(paramRangeSet.asRanges()));
  }
  
  ImmutableRangeSet(ImmutableList paramImmutableList)
  {
    this.ranges = paramImmutableList;
  }
  
  private ImmutableRangeSet(ImmutableList paramImmutableList, ImmutableRangeSet paramImmutableRangeSet)
  {
    this.ranges = paramImmutableList;
    this.complement = paramImmutableRangeSet;
  }
  
  public boolean encloses(Range paramRange)
  {
    int i = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), paramRange.lowerBound, Ordering.natural(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
    return (i != -1) && (((Range)this.ranges.get(i)).encloses(paramRange));
  }
  
  public Range rangeContaining(Comparable paramComparable)
  {
    int i = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), Cut.belowValue(paramComparable), Ordering.natural(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
    if (i != -1)
    {
      Range localRange = (Range)this.ranges.get(i);
      return localRange.contains(paramComparable) ? localRange : null;
    }
    return null;
  }
  
  public Range span()
  {
    if (this.ranges.isEmpty()) {
      throw new NoSuchElementException();
    }
    return Range.create(((Range)this.ranges.get(0)).lowerBound, ((Range)this.ranges.get(this.ranges.size() - 1)).upperBound);
  }
  
  public boolean isEmpty()
  {
    return this.ranges.isEmpty();
  }
  
  public void add(Range paramRange)
  {
    throw new UnsupportedOperationException();
  }
  
  public void addAll(RangeSet paramRangeSet)
  {
    throw new UnsupportedOperationException();
  }
  
  public void remove(Range paramRange)
  {
    throw new UnsupportedOperationException();
  }
  
  public void removeAll(RangeSet paramRangeSet)
  {
    throw new UnsupportedOperationException();
  }
  
  public ImmutableSet asRanges()
  {
    if (this.ranges.isEmpty()) {
      return ImmutableSet.of();
    }
    return new RegularImmutableSortedSet(this.ranges, Range.RANGE_LEX_ORDERING);
  }
  
  public ImmutableRangeSet complement()
  {
    ImmutableRangeSet localImmutableRangeSet = this.complement;
    if (localImmutableRangeSet != null) {
      return localImmutableRangeSet;
    }
    if (this.ranges.isEmpty()) {
      return this.complement = all();
    }
    if ((this.ranges.size() == 1) && (((Range)this.ranges.get(0)).equals(Range.all()))) {
      return this.complement = of();
    }
    ComplementRanges localComplementRanges = new ComplementRanges();
    localImmutableRangeSet = this.complement = new ImmutableRangeSet(localComplementRanges, this);
    return localImmutableRangeSet;
  }
  
  private ImmutableList intersectRanges(final Range paramRange)
  {
    if ((this.ranges.isEmpty()) || (paramRange.isEmpty())) {
      return ImmutableList.of();
    }
    if (paramRange.encloses(span())) {
      return this.ranges;
    }
    final int i;
    if (paramRange.hasLowerBound()) {
      i = SortedLists.binarySearch(this.ranges, Range.upperBoundFn(), paramRange.lowerBound, SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    } else {
      i = 0;
    }
    int j;
    if (paramRange.hasUpperBound()) {
      j = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), paramRange.upperBound, SortedLists.KeyPresentBehavior.FIRST_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    } else {
      j = this.ranges.size();
    }
    final int k = j - i;
    if (k == 0) {
      return ImmutableList.of();
    }
    new ImmutableList()
    {
      public int size()
      {
        return k;
      }
      
      public Range get(int paramAnonymousInt)
      {
        Preconditions.checkElementIndex(paramAnonymousInt, k);
        if ((paramAnonymousInt == 0) || (paramAnonymousInt == k - 1)) {
          return ((Range)ImmutableRangeSet.this.ranges.get(paramAnonymousInt + i)).intersection(paramRange);
        }
        return (Range)ImmutableRangeSet.this.ranges.get(paramAnonymousInt + i);
      }
      
      boolean isPartialView()
      {
        return true;
      }
    };
  }
  
  public ImmutableRangeSet subRangeSet(Range paramRange)
  {
    if (!isEmpty())
    {
      Range localRange = span();
      if (paramRange.encloses(localRange)) {
        return this;
      }
      if (paramRange.isConnected(localRange)) {
        return new ImmutableRangeSet(intersectRanges(paramRange));
      }
    }
    return of();
  }
  
  public ImmutableSortedSet asSet(DiscreteDomain paramDiscreteDomain)
  {
    Preconditions.checkNotNull(paramDiscreteDomain);
    if (isEmpty()) {
      return ImmutableSortedSet.of();
    }
    Range localRange = span().canonical(paramDiscreteDomain);
    if (!localRange.hasLowerBound()) {
      throw new IllegalArgumentException("Neither the DiscreteDomain nor this range set are bounded below");
    }
    if (!localRange.hasUpperBound()) {
      try
      {
        paramDiscreteDomain.maxValue();
      }
      catch (NoSuchElementException localNoSuchElementException)
      {
        throw new IllegalArgumentException("Neither the DiscreteDomain nor this range set are bounded above");
      }
    }
    return new AsSet(paramDiscreteDomain);
  }
  
  boolean isPartialView()
  {
    return this.ranges.isPartialView();
  }
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  Object writeReplace()
  {
    return new SerializedForm(this.ranges);
  }
  
  private static final class SerializedForm
    implements Serializable
  {
    private final ImmutableList ranges;
    
    SerializedForm(ImmutableList paramImmutableList)
    {
      this.ranges = paramImmutableList;
    }
    
    Object readResolve()
    {
      if (this.ranges.isEmpty()) {
        return ImmutableRangeSet.of();
      }
      if (this.ranges.equals(ImmutableList.of(Range.all()))) {
        return ImmutableRangeSet.all();
      }
      return new ImmutableRangeSet(this.ranges);
    }
  }
  
  public static class Builder
  {
    private final RangeSet rangeSet = TreeRangeSet.create();
    
    public Builder add(Range paramRange)
    {
      if (paramRange.isEmpty()) {
        throw new IllegalArgumentException("range must not be empty, but was " + paramRange);
      }
      if (!this.rangeSet.complement().encloses(paramRange))
      {
        Iterator localIterator = this.rangeSet.asRanges().iterator();
        while (localIterator.hasNext())
        {
          Range localRange = (Range)localIterator.next();
          Preconditions.checkArgument((!localRange.isConnected(paramRange)) || (localRange.intersection(paramRange).isEmpty()), "Ranges may not overlap, but received %s and %s", new Object[] { localRange, paramRange });
        }
        throw new AssertionError("should have thrown an IAE above");
      }
      this.rangeSet.add(paramRange);
      return this;
    }
    
    public Builder addAll(RangeSet paramRangeSet)
    {
      Iterator localIterator = paramRangeSet.asRanges().iterator();
      while (localIterator.hasNext())
      {
        Range localRange = (Range)localIterator.next();
        add(localRange);
      }
      return this;
    }
    
    public ImmutableRangeSet build()
    {
      return ImmutableRangeSet.copyOf(this.rangeSet);
    }
  }
  
  private static class AsSetSerializedForm
    implements Serializable
  {
    private final ImmutableList ranges;
    private final DiscreteDomain domain;
    
    AsSetSerializedForm(ImmutableList paramImmutableList, DiscreteDomain paramDiscreteDomain)
    {
      this.ranges = paramImmutableList;
      this.domain = paramDiscreteDomain;
    }
    
    Object readResolve()
    {
      return new ImmutableRangeSet(this.ranges).asSet(this.domain);
    }
  }
  
  private final class AsSet
    extends ImmutableSortedSet
  {
    private final DiscreteDomain domain;
    private transient Integer size;
    
    AsSet(DiscreteDomain paramDiscreteDomain)
    {
      super();
      this.domain = paramDiscreteDomain;
    }
    
    public int size()
    {
      Integer localInteger = this.size;
      if (localInteger == null)
      {
        long l = 0L;
        Iterator localIterator = ImmutableRangeSet.this.ranges.iterator();
        while (localIterator.hasNext())
        {
          Range localRange = (Range)localIterator.next();
          l += localRange.asSet(this.domain).size();
          if (l >= 2147483647L) {
            break;
          }
        }
        localInteger = this.size = Integer.valueOf(Ints.saturatedCast(l));
      }
      return localInteger.intValue();
    }
    
    public UnmodifiableIterator iterator()
    {
      new AbstractIterator()
      {
        final Iterator rangeItr = ImmutableRangeSet.this.ranges.iterator();
        Iterator elemItr = Iterators.emptyIterator();
        
        protected Comparable computeNext()
        {
          while (!this.elemItr.hasNext()) {
            if (this.rangeItr.hasNext()) {
              this.elemItr = ((Range)this.rangeItr.next()).asSet(ImmutableRangeSet.AsSet.this.domain).iterator();
            } else {
              return (Comparable)endOfData();
            }
          }
          return (Comparable)this.elemItr.next();
        }
      };
    }
    
    @GwtIncompatible("NavigableSet")
    public UnmodifiableIterator descendingIterator()
    {
      new AbstractIterator()
      {
        final Iterator rangeItr = ImmutableRangeSet.this.ranges.reverse().iterator();
        Iterator elemItr = Iterators.emptyIterator();
        
        protected Comparable computeNext()
        {
          while (!this.elemItr.hasNext()) {
            if (this.rangeItr.hasNext()) {
              this.elemItr = ((Range)this.rangeItr.next()).asSet(ImmutableRangeSet.AsSet.this.domain).descendingIterator();
            } else {
              return (Comparable)endOfData();
            }
          }
          return (Comparable)this.elemItr.next();
        }
      };
    }
    
    ImmutableSortedSet subSet(Range paramRange)
    {
      return ImmutableRangeSet.this.subRangeSet(paramRange).asSet(this.domain);
    }
    
    ImmutableSortedSet headSetImpl(Comparable paramComparable, boolean paramBoolean)
    {
      return subSet(Range.upTo(paramComparable, BoundType.forBoolean(paramBoolean)));
    }
    
    ImmutableSortedSet subSetImpl(Comparable paramComparable1, boolean paramBoolean1, Comparable paramComparable2, boolean paramBoolean2)
    {
      if ((!paramBoolean1) && (!paramBoolean2) && (Range.compareOrThrow(paramComparable1, paramComparable2) == 0)) {
        return ImmutableSortedSet.of();
      }
      return subSet(Range.range(paramComparable1, BoundType.forBoolean(paramBoolean1), paramComparable2, BoundType.forBoolean(paramBoolean2)));
    }
    
    ImmutableSortedSet tailSetImpl(Comparable paramComparable, boolean paramBoolean)
    {
      return subSet(Range.downTo(paramComparable, BoundType.forBoolean(paramBoolean)));
    }
    
    public boolean contains(Object paramObject)
    {
      if (paramObject == null) {
        return false;
      }
      try
      {
        Comparable localComparable = (Comparable)paramObject;
        return ImmutableRangeSet.this.contains(localComparable);
      }
      catch (ClassCastException localClassCastException) {}
      return false;
    }
    
    int indexOf(Object paramObject)
    {
      if (contains(paramObject))
      {
        Comparable localComparable = (Comparable)paramObject;
        long l = 0L;
        Iterator localIterator = ImmutableRangeSet.this.ranges.iterator();
        while (localIterator.hasNext())
        {
          Range localRange = (Range)localIterator.next();
          if (localRange.contains(localComparable)) {
            return Ints.saturatedCast(l + localRange.asSet(this.domain).indexOf(localComparable));
          }
          l += localRange.asSet(this.domain).size();
        }
        throw new AssertionError("impossible");
      }
      return -1;
    }
    
    boolean isPartialView()
    {
      return ImmutableRangeSet.this.ranges.isPartialView();
    }
    
    public String toString()
    {
      return ImmutableRangeSet.this.ranges.toString();
    }
    
    Object writeReplace()
    {
      return new ImmutableRangeSet.AsSetSerializedForm(ImmutableRangeSet.this.ranges, this.domain);
    }
  }
  
  private final class ComplementRanges
    extends ImmutableList
  {
    private final boolean positiveBoundedBelow = ((Range)ImmutableRangeSet.this.ranges.get(0)).hasLowerBound();
    private final boolean positiveBoundedAbove = ((Range)Iterables.getLast(ImmutableRangeSet.this.ranges)).hasUpperBound();
    private final int size;
    
    ComplementRanges()
    {
      int i = ImmutableRangeSet.this.ranges.size() - 1;
      if (this.positiveBoundedBelow) {
        i++;
      }
      if (this.positiveBoundedAbove) {
        i++;
      }
      this.size = i;
    }
    
    public int size()
    {
      return this.size;
    }
    
    public Range get(int paramInt)
    {
      Preconditions.checkElementIndex(paramInt, this.size);
      Cut localCut1;
      if (this.positiveBoundedBelow) {
        localCut1 = paramInt == 0 ? Cut.belowAll() : ((Range)ImmutableRangeSet.this.ranges.get(paramInt - 1)).upperBound;
      } else {
        localCut1 = ((Range)ImmutableRangeSet.this.ranges.get(paramInt)).upperBound;
      }
      Cut localCut2;
      if ((this.positiveBoundedAbove) && (paramInt == this.size - 1)) {
        localCut2 = Cut.aboveAll();
      } else {
        localCut2 = ((Range)ImmutableRangeSet.this.ranges.get(paramInt + (this.positiveBoundedBelow ? 0 : 1))).lowerBound;
      }
      return Range.create(localCut1, localCut2);
    }
    
    boolean isPartialView()
    {
      return true;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableRangeSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */