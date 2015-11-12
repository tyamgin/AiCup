package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

final class RegularImmutableSortedMultiset
  extends ImmutableSortedMultiset
{
  private final transient RegularImmutableSortedSet elementSet;
  private final transient int[] counts;
  private final transient long[] cumulativeCounts;
  private final transient int offset;
  private final transient int length;
  
  RegularImmutableSortedMultiset(RegularImmutableSortedSet paramRegularImmutableSortedSet, int[] paramArrayOfInt, long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    this.elementSet = paramRegularImmutableSortedSet;
    this.counts = paramArrayOfInt;
    this.cumulativeCounts = paramArrayOfLong;
    this.offset = paramInt1;
    this.length = paramInt2;
  }
  
  private Multiset.Entry getEntry(int paramInt)
  {
    return Multisets.immutableEntry(this.elementSet.asList().get(paramInt), this.counts[(this.offset + paramInt)]);
  }
  
  public Multiset.Entry firstEntry()
  {
    return getEntry(0);
  }
  
  public Multiset.Entry lastEntry()
  {
    return getEntry(this.length - 1);
  }
  
  public int count(Object paramObject)
  {
    int i = this.elementSet.indexOf(paramObject);
    return i == -1 ? 0 : this.counts[(i + this.offset)];
  }
  
  public int size()
  {
    long l = this.cumulativeCounts[(this.offset + this.length)] - this.cumulativeCounts[this.offset];
    return Ints.saturatedCast(l);
  }
  
  public ImmutableSortedSet elementSet()
  {
    return this.elementSet;
  }
  
  public ImmutableSortedMultiset headMultiset(Object paramObject, BoundType paramBoundType)
  {
    return getSubMultiset(0, this.elementSet.headIndex(paramObject, Preconditions.checkNotNull(paramBoundType) == BoundType.CLOSED));
  }
  
  public ImmutableSortedMultiset tailMultiset(Object paramObject, BoundType paramBoundType)
  {
    return getSubMultiset(this.elementSet.tailIndex(paramObject, Preconditions.checkNotNull(paramBoundType) == BoundType.CLOSED), this.length);
  }
  
  ImmutableSortedMultiset getSubMultiset(int paramInt1, int paramInt2)
  {
    Preconditions.checkPositionIndexes(paramInt1, paramInt2, this.length);
    if (paramInt1 == paramInt2) {
      return emptyMultiset(comparator());
    }
    if ((paramInt1 == 0) && (paramInt2 == this.length)) {
      return this;
    }
    RegularImmutableSortedSet localRegularImmutableSortedSet = (RegularImmutableSortedSet)this.elementSet.getSubSet(paramInt1, paramInt2);
    return new RegularImmutableSortedMultiset(localRegularImmutableSortedSet, this.counts, this.cumulativeCounts, this.offset + paramInt1, paramInt2 - paramInt1);
  }
  
  ImmutableSet createEntrySet()
  {
    return new EntrySet(null);
  }
  
  boolean isPartialView()
  {
    return (this.offset > 0) || (this.length < this.counts.length);
  }
  
  private final class EntrySet
    extends ImmutableMultiset.EntrySet
  {
    private EntrySet()
    {
      super();
    }
    
    public int size()
    {
      return RegularImmutableSortedMultiset.this.length;
    }
    
    public UnmodifiableIterator iterator()
    {
      return asList().iterator();
    }
    
    ImmutableList createAsList()
    {
      new ImmutableAsList()
      {
        public Multiset.Entry get(int paramAnonymousInt)
        {
          return RegularImmutableSortedMultiset.this.getEntry(paramAnonymousInt);
        }
        
        ImmutableCollection delegateCollection()
        {
          return RegularImmutableSortedMultiset.EntrySet.this;
        }
      };
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\RegularImmutableSortedMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */