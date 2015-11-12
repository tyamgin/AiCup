package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;

@GwtCompatible(serializable=true)
final class EmptyImmutableMultiset
  extends ImmutableMultiset
{
  static final EmptyImmutableMultiset INSTANCE = new EmptyImmutableMultiset();
  private static final long serialVersionUID = 0L;
  
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
  
  public ImmutableSet elementSet()
  {
    return ImmutableSet.of();
  }
  
  public ImmutableSet entrySet()
  {
    return ImmutableSet.of();
  }
  
  ImmutableSet createEntrySet()
  {
    throw new AssertionError("should never be called");
  }
  
  public int size()
  {
    return 0;
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
  
  Object readResolve()
  {
    return INSTANCE;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\EmptyImmutableMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */