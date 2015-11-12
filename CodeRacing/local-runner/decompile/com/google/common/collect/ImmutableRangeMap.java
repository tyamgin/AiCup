package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

@Beta
@GwtIncompatible("NavigableMap")
public class ImmutableRangeMap
  implements RangeMap
{
  private static final ImmutableRangeMap EMPTY = new ImmutableRangeMap(ImmutableList.of(), ImmutableList.of());
  private final ImmutableList ranges;
  private final ImmutableList values;
  
  public static final ImmutableRangeMap of()
  {
    return EMPTY;
  }
  
  public static final ImmutableRangeMap of(Range paramRange, Object paramObject)
  {
    return new ImmutableRangeMap(ImmutableList.of(paramRange), ImmutableList.of(paramObject));
  }
  
  public static final ImmutableRangeMap copyOf(RangeMap paramRangeMap)
  {
    if ((paramRangeMap instanceof ImmutableRangeMap)) {
      return (ImmutableRangeMap)paramRangeMap;
    }
    Map localMap = paramRangeMap.asMapOfRanges();
    ImmutableList.Builder localBuilder1 = new ImmutableList.Builder(localMap.size());
    ImmutableList.Builder localBuilder2 = new ImmutableList.Builder(localMap.size());
    Iterator localIterator = localMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localBuilder1.add(localEntry.getKey());
      localBuilder2.add(localEntry.getValue());
    }
    return new ImmutableRangeMap(localBuilder1.build(), localBuilder2.build());
  }
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  ImmutableRangeMap(ImmutableList paramImmutableList1, ImmutableList paramImmutableList2)
  {
    this.ranges = paramImmutableList1;
    this.values = paramImmutableList2;
  }
  
  public Object get(Comparable paramComparable)
  {
    int i = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), Cut.belowValue(paramComparable), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
    if (i == -1) {
      return null;
    }
    Range localRange = (Range)this.ranges.get(i);
    return localRange.contains(paramComparable) ? this.values.get(i) : null;
  }
  
  public Map.Entry getEntry(Comparable paramComparable)
  {
    int i = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), Cut.belowValue(paramComparable), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
    if (i == -1) {
      return null;
    }
    Range localRange = (Range)this.ranges.get(i);
    return localRange.contains(paramComparable) ? Maps.immutableEntry(localRange, this.values.get(i)) : null;
  }
  
  public Range span()
  {
    if (this.ranges.isEmpty()) {
      throw new NoSuchElementException();
    }
    Range localRange1 = (Range)this.ranges.get(0);
    Range localRange2 = (Range)this.ranges.get(this.ranges.size() - 1);
    return Range.create(localRange1.lowerBound, localRange2.upperBound);
  }
  
  public void put(Range paramRange, Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(RangeMap paramRangeMap)
  {
    throw new UnsupportedOperationException();
  }
  
  public void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  public void remove(Range paramRange)
  {
    throw new UnsupportedOperationException();
  }
  
  public ImmutableMap asMapOfRanges()
  {
    if (this.ranges.isEmpty()) {
      return ImmutableMap.of();
    }
    RegularImmutableSortedSet localRegularImmutableSortedSet = new RegularImmutableSortedSet(this.ranges, Range.RANGE_LEX_ORDERING);
    return new RegularImmutableSortedMap(localRegularImmutableSortedSet, this.values);
  }
  
  public ImmutableRangeMap subRangeMap(final Range paramRange)
  {
    if (((Range)Preconditions.checkNotNull(paramRange)).isEmpty()) {
      return of();
    }
    if ((this.ranges.isEmpty()) || (paramRange.encloses(span()))) {
      return this;
    }
    int i = SortedLists.binarySearch(this.ranges, Range.upperBoundFn(), paramRange.lowerBound, SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    int j = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), paramRange.upperBound, SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    if (i >= j) {
      return of();
    }
    final int k = i;
    final int m = j - i;
    ImmutableList local1 = new ImmutableList()
    {
      public int size()
      {
        return m;
      }
      
      public Range get(int paramAnonymousInt)
      {
        Preconditions.checkElementIndex(paramAnonymousInt, m);
        if ((paramAnonymousInt == 0) || (paramAnonymousInt == m - 1)) {
          return ((Range)ImmutableRangeMap.this.ranges.get(paramAnonymousInt + k)).intersection(paramRange);
        }
        return (Range)ImmutableRangeMap.this.ranges.get(paramAnonymousInt + k);
      }
      
      boolean isPartialView()
      {
        return true;
      }
    };
    final ImmutableRangeMap localImmutableRangeMap = this;
    new ImmutableRangeMap(local1, this.values.subList(i, j))
    {
      public ImmutableRangeMap subRangeMap(Range paramAnonymousRange)
      {
        if (paramRange.isConnected(paramAnonymousRange)) {
          return localImmutableRangeMap.subRangeMap(paramAnonymousRange.intersection(paramRange));
        }
        return ImmutableRangeMap.of();
      }
    };
  }
  
  public int hashCode()
  {
    return asMapOfRanges().hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof RangeMap))
    {
      RangeMap localRangeMap = (RangeMap)paramObject;
      return asMapOfRanges().equals(localRangeMap.asMapOfRanges());
    }
    return false;
  }
  
  public String toString()
  {
    return asMapOfRanges().toString();
  }
  
  public static final class Builder
  {
    private final RangeSet keyRanges = TreeRangeSet.create();
    private final RangeMap rangeMap = TreeRangeMap.create();
    
    public Builder put(Range paramRange, Object paramObject)
    {
      Preconditions.checkNotNull(paramRange);
      Preconditions.checkNotNull(paramObject);
      Preconditions.checkArgument(!paramRange.isEmpty(), "Range must not be empty, but was %s", new Object[] { paramRange });
      if (!this.keyRanges.complement().encloses(paramRange))
      {
        Iterator localIterator = this.rangeMap.asMapOfRanges().entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          Range localRange = (Range)localEntry.getKey();
          if ((localRange.isConnected(paramRange)) && (!localRange.intersection(paramRange).isEmpty())) {
            throw new IllegalArgumentException("Overlapping ranges: range " + paramRange + " overlaps with entry " + localEntry);
          }
        }
      }
      this.keyRanges.add(paramRange);
      this.rangeMap.put(paramRange, paramObject);
      return this;
    }
    
    public Builder putAll(RangeMap paramRangeMap)
    {
      Iterator localIterator = paramRangeMap.asMapOfRanges().entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        put((Range)localEntry.getKey(), localEntry.getValue());
      }
      return this;
    }
    
    public ImmutableRangeMap build()
    {
      Map localMap = this.rangeMap.asMapOfRanges();
      ImmutableList.Builder localBuilder1 = new ImmutableList.Builder(localMap.size());
      ImmutableList.Builder localBuilder2 = new ImmutableList.Builder(localMap.size());
      Iterator localIterator = localMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        localBuilder1.add(localEntry.getKey());
        localBuilder2.add(localEntry.getValue());
      }
      return new ImmutableRangeMap(localBuilder1.build(), localBuilder2.build());
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableRangeMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */