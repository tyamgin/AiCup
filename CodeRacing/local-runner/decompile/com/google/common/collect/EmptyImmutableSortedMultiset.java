package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Comparator;

final class EmptyImmutableSortedMultiset
  extends ImmutableSortedMultiset
{
  private final ImmutableSortedSet elementSet;
  
  EmptyImmutableSortedMultiset(Comparator paramComparator)
  {
    this.elementSet = ImmutableSortedSet.emptySet(paramComparator);
  }
  
  public Multiset.Entry firstEntry()
  {
    return null;
  }
  
  public Multiset.Entry lastEntry()
  {
    return null;
  }
  
  public int count(Object paramObject)
  {
    return 0;
  }
  
  public boolean contains(Object paramObject)
  {
    return false;
  }
  
  public boolean containsAll(Collection paramCollection)
  {
    return paramCollection.isEmpty();
  }
  
  public int size()
  {
    return 0;
  }
  
  public ImmutableSortedSet elementSet()
  {
    return this.elementSet;
  }
  
  public ImmutableSet entrySet()
  {
    return ImmutableSet.of();
  }
  
  ImmutableSet createEntrySet()
  {
    throw new AssertionError("should never be called");
  }
  
  public ImmutableSortedMultiset headMultiset(Object paramObject, BoundType paramBoundType)
  {
    Preconditions.checkNotNull(paramObject);
    Preconditions.checkNotNull(paramBoundType);
    return this;
  }
  
  public ImmutableSortedMultiset tailMultiset(Object paramObject, BoundType paramBoundType)
  {
    Preconditions.checkNotNull(paramObject);
    Preconditions.checkNotNull(paramBoundType);
    return this;
  }
  
  public UnmodifiableIterator iterator()
  {
    return Iterators.emptyIterator();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Multiset))
    {
      Multiset localMultiset = (Multiset)paramObject;
      return localMultiset.isEmpty();
    }
    return false;
  }
  
  public int hashCode()
  {
    return 0;
  }
  
  public String toString()
  {
    return "[]";
  }
  
  boolean isPartialView()
  {
    return false;
  }
  
  public Object[] toArray()
  {
    return ObjectArrays.EMPTY_ARRAY;
  }
  
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    return asList().toArray(paramArrayOfObject);
  }
  
  public ImmutableList asList()
  {
    return ImmutableList.of();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\EmptyImmutableSortedMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */