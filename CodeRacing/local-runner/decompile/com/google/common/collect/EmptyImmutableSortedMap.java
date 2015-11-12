package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;

@GwtCompatible(emulated=true)
final class EmptyImmutableSortedMap
  extends ImmutableSortedMap
{
  private final transient ImmutableSortedSet keySet;
  
  EmptyImmutableSortedMap(Comparator paramComparator)
  {
    this.keySet = ImmutableSortedSet.emptySet(paramComparator);
  }
  
  EmptyImmutableSortedMap(Comparator paramComparator, ImmutableSortedMap paramImmutableSortedMap)
  {
    super(paramImmutableSortedMap);
    this.keySet = ImmutableSortedSet.emptySet(paramComparator);
  }
  
  public Object get(Object paramObject)
  {
    return null;
  }
  
  public ImmutableSortedSet keySet()
  {
    return this.keySet;
  }
  
  public int size()
  {
    return 0;
  }
  
  public boolean isEmpty()
  {
    return true;
  }
  
  public ImmutableCollection values()
  {
    return ImmutableList.of();
  }
  
  public String toString()
  {
    return "{}";
  }
  
  boolean isPartialView()
  {
    return false;
  }
  
  public ImmutableSet entrySet()
  {
    return ImmutableSet.of();
  }
  
  ImmutableSet createEntrySet()
  {
    throw new AssertionError("should never be called");
  }
  
  public ImmutableSortedMap headMap(Object paramObject, boolean paramBoolean)
  {
    Preconditions.checkNotNull(paramObject);
    return this;
  }
  
  public ImmutableSortedMap tailMap(Object paramObject, boolean paramBoolean)
  {
    Preconditions.checkNotNull(paramObject);
    return this;
  }
  
  ImmutableSortedMap createDescendingMap()
  {
    return new EmptyImmutableSortedMap(Ordering.from(comparator()).reverse(), this);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\EmptyImmutableSortedMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */