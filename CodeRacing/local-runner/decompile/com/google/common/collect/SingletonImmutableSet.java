package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.Set;

@GwtCompatible(serializable=true, emulated=true)
final class SingletonImmutableSet
  extends ImmutableSet
{
  final transient Object element;
  private transient int cachedHashCode;
  
  SingletonImmutableSet(Object paramObject)
  {
    this.element = Preconditions.checkNotNull(paramObject);
  }
  
  SingletonImmutableSet(Object paramObject, int paramInt)
  {
    this.element = paramObject;
    this.cachedHashCode = paramInt;
  }
  
  public int size()
  {
    return 1;
  }
  
  public boolean isEmpty()
  {
    return false;
  }
  
  public boolean contains(Object paramObject)
  {
    return this.element.equals(paramObject);
  }
  
  public UnmodifiableIterator iterator()
  {
    return Iterators.singletonIterator(this.element);
  }
  
  boolean isPartialView()
  {
    return false;
  }
  
  public Object[] toArray()
  {
    return new Object[] { this.element };
  }
  
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject.length == 0) {
      paramArrayOfObject = ObjectArrays.newArray(paramArrayOfObject, 1);
    } else if (paramArrayOfObject.length > 1) {
      paramArrayOfObject[1] = null;
    }
    Object[] arrayOfObject = paramArrayOfObject;
    arrayOfObject[0] = this.element;
    return paramArrayOfObject;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof Set))
    {
      Set localSet = (Set)paramObject;
      return (localSet.size() == 1) && (this.element.equals(localSet.iterator().next()));
    }
    return false;
  }
  
  public final int hashCode()
  {
    int i = this.cachedHashCode;
    if (i == 0) {
      this.cachedHashCode = (i = this.element.hashCode());
    }
    return i;
  }
  
  boolean isHashCodeFast()
  {
    return this.cachedHashCode != 0;
  }
  
  public String toString()
  {
    String str = this.element.toString();
    return str.length() + 2 + '[' + str + ']';
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\SingletonImmutableSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */