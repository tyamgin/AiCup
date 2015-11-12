package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;

class DescendingImmutableSortedSet
  extends ImmutableSortedSet
{
  private final ImmutableSortedSet forward;
  
  DescendingImmutableSortedSet(ImmutableSortedSet paramImmutableSortedSet)
  {
    super(Ordering.from(paramImmutableSortedSet.comparator()).reverse());
    this.forward = paramImmutableSortedSet;
  }
  
  public int size()
  {
    return this.forward.size();
  }
  
  public UnmodifiableIterator iterator()
  {
    return this.forward.descendingIterator();
  }
  
  ImmutableSortedSet headSetImpl(Object paramObject, boolean paramBoolean)
  {
    return this.forward.tailSet(paramObject, paramBoolean).descendingSet();
  }
  
  ImmutableSortedSet subSetImpl(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
  {
    return this.forward.subSet(paramObject2, paramBoolean2, paramObject1, paramBoolean1).descendingSet();
  }
  
  ImmutableSortedSet tailSetImpl(Object paramObject, boolean paramBoolean)
  {
    return this.forward.headSet(paramObject, paramBoolean).descendingSet();
  }
  
  @GwtIncompatible("NavigableSet")
  public ImmutableSortedSet descendingSet()
  {
    return this.forward;
  }
  
  @GwtIncompatible("NavigableSet")
  public UnmodifiableIterator descendingIterator()
  {
    return this.forward.iterator();
  }
  
  @GwtIncompatible("NavigableSet")
  ImmutableSortedSet createDescendingSet()
  {
    throw new AssertionError("should never be called");
  }
  
  public Object lower(Object paramObject)
  {
    return this.forward.higher(paramObject);
  }
  
  public Object floor(Object paramObject)
  {
    return this.forward.ceiling(paramObject);
  }
  
  public Object ceiling(Object paramObject)
  {
    return this.forward.floor(paramObject);
  }
  
  public Object higher(Object paramObject)
  {
    return this.forward.lower(paramObject);
  }
  
  int indexOf(Object paramObject)
  {
    int i = this.forward.indexOf(paramObject);
    if (i == -1) {
      return i;
    }
    return size() - 1 - i;
  }
  
  boolean isPartialView()
  {
    return this.forward.isPartialView();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\DescendingImmutableSortedSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */