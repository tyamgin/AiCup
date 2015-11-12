package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Set;

@GwtCompatible(emulated=true)
final class EmptyContiguousSet
  extends ContiguousSet
{
  EmptyContiguousSet(DiscreteDomain paramDiscreteDomain)
  {
    super(paramDiscreteDomain);
  }
  
  public Comparable first()
  {
    throw new NoSuchElementException();
  }
  
  public Comparable last()
  {
    throw new NoSuchElementException();
  }
  
  public int size()
  {
    return 0;
  }
  
  public ContiguousSet intersection(ContiguousSet paramContiguousSet)
  {
    return this;
  }
  
  public Range range()
  {
    throw new NoSuchElementException();
  }
  
  public Range range(BoundType paramBoundType1, BoundType paramBoundType2)
  {
    throw new NoSuchElementException();
  }
  
  ContiguousSet headSetImpl(Comparable paramComparable, boolean paramBoolean)
  {
    return this;
  }
  
  ContiguousSet subSetImpl(Comparable paramComparable1, boolean paramBoolean1, Comparable paramComparable2, boolean paramBoolean2)
  {
    return this;
  }
  
  ContiguousSet tailSetImpl(Comparable paramComparable, boolean paramBoolean)
  {
    return this;
  }
  
  @GwtIncompatible("not used by GWT emulation")
  int indexOf(Object paramObject)
  {
    return -1;
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
  
  public boolean isEmpty()
  {
    return true;
  }
  
  public ImmutableList asList()
  {
    return ImmutableList.of();
  }
  
  public String toString()
  {
    return "[]";
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
  
  @GwtIncompatible("serialization")
  Object writeReplace()
  {
    return new SerializedForm(this.domain, null);
  }
  
  @GwtIncompatible("NavigableSet")
  ImmutableSortedSet createDescendingSet()
  {
    return new EmptyImmutableSortedSet(Ordering.natural().reverse());
  }
  
  @GwtIncompatible("serialization")
  private static final class SerializedForm
    implements Serializable
  {
    private final DiscreteDomain domain;
    private static final long serialVersionUID = 0L;
    
    private SerializedForm(DiscreteDomain paramDiscreteDomain)
    {
      this.domain = paramDiscreteDomain;
    }
    
    private Object readResolve()
    {
      return new EmptyContiguousSet(this.domain);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\EmptyContiguousSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */