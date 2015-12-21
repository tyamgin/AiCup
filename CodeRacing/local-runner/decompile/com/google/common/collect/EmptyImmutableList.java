package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.List;

@GwtCompatible(serializable=true, emulated=true)
final class EmptyImmutableList
  extends ImmutableList
{
  static final EmptyImmutableList INSTANCE = new EmptyImmutableList();
  private static final long serialVersionUID = 0L;
  
  public int size()
  {
    return 0;
  }
  
  public boolean isEmpty()
  {
    return true;
  }
  
  boolean isPartialView()
  {
    return false;
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
    return listIterator();
  }
  
  public Object[] toArray()
  {
    return ObjectArrays.EMPTY_ARRAY;
  }
  
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject.length > 0) {
      paramArrayOfObject[0] = null;
    }
    return paramArrayOfObject;
  }
  
  public Object get(int paramInt)
  {
    Preconditions.checkElementIndex(paramInt, 0);
    throw new AssertionError("unreachable");
  }
  
  public int indexOf(Object paramObject)
  {
    return -1;
  }
  
  public int lastIndexOf(Object paramObject)
  {
    return -1;
  }
  
  public ImmutableList subList(int paramInt1, int paramInt2)
  {
    Preconditions.checkPositionIndexes(paramInt1, paramInt2, 0);
    return this;
  }
  
  public ImmutableList reverse()
  {
    return this;
  }
  
  public UnmodifiableListIterator listIterator()
  {
    return Iterators.EMPTY_LIST_ITERATOR;
  }
  
  public UnmodifiableListIterator listIterator(int paramInt)
  {
    Preconditions.checkPositionIndex(paramInt, 0);
    return Iterators.EMPTY_LIST_ITERATOR;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof List))
    {
      List localList = (List)paramObject;
      return localList.isEmpty();
    }
    return false;
  }
  
  public int hashCode()
  {
    return 1;
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


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\EmptyImmutableList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */