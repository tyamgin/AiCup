package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Set;

@GwtCompatible(serializable=true, emulated=true)
class EmptyImmutableSortedSet
  extends ImmutableSortedSet
{
  EmptyImmutableSortedSet(Comparator paramComparator)
  {
    super(paramComparator);
  }
  
  public int size()
  {
    return 0;
  }
  
  public boolean isEmpty()
  {
    return true;
  }
  
  public boolean contains(Object paramObject)
  {
    return false;
  }
  
  public boolean containsAll(Collection paramCollection)
  {
    return paramCollection.isEmpty();
  }
  
  public UnmodifiableIterator iterator()
  {
    return Iterators.emptyIterator();
  }
  
  @GwtIncompatible("NavigableSet")
  public UnmodifiableIterator descendingIterator()
  {
    return Iterators.emptyIterator();
  }
  
  boolean isPartialView()
  {
    return false;
  }
  
  public ImmutableList asList()
  {
    return ImmutableList.of();
  }
  
  public Object[] toArray()
  {
    return ObjectArrays.EMPTY_ARRAY;
  }
  
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    return asList().toArray(paramArrayOfObject);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Set))
    {
      Set localSet = (Set)paramObject;
      return localSet.isEmpty();
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
  
  public Object first()
  {
    throw new NoSuchElementException();
  }
  
  public Object last()
  {
    throw new NoSuchElementException();
  }
  
  ImmutableSortedSet headSetImpl(Object paramObject, boolean paramBoolean)
  {
    return this;
  }
  
  ImmutableSortedSet subSetImpl(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
  {
    return this;
  }
  
  ImmutableSortedSet tailSetImpl(Object paramObject, boolean paramBoolean)
  {
    return this;
  }
  
  int indexOf(Object paramObject)
  {
    return -1;
  }
  
  ImmutableSortedSet createDescendingSet()
  {
    return new EmptyImmutableSortedSet(Ordering.from(this.comparator).reverse());
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\EmptyImmutableSortedSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */