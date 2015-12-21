package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Set;

@GwtCompatible(serializable=true, emulated=true)
final class EmptyImmutableSet
  extends ImmutableSet
{
  static final EmptyImmutableSet INSTANCE = new EmptyImmutableSet();
  private static final long serialVersionUID = 0L;
  
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
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Set))
    {
      Set localSet = (Set)paramObject;
      return localSet.isEmpty();
    }
    return false;
  }
  
  public final int hashCode()
  {
    return 0;
  }
  
  boolean isHashCodeFast()
  {
    return true;
  }
  
  public String toString()
  {
    return "[]";
  }
  
  Object readResolve()
  {
    return INSTANCE;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\EmptyImmutableSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */