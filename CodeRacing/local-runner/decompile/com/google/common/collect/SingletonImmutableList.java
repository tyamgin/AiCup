package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.List;

@GwtCompatible(serializable=true, emulated=true)
final class SingletonImmutableList
  extends ImmutableList
{
  final transient Object element;
  
  SingletonImmutableList(Object paramObject)
  {
    this.element = Preconditions.checkNotNull(paramObject);
  }
  
  public Object get(int paramInt)
  {
    Preconditions.checkElementIndex(paramInt, 1);
    return this.element;
  }
  
  public int indexOf(Object paramObject)
  {
    return this.element.equals(paramObject) ? 0 : -1;
  }
  
  public UnmodifiableIterator iterator()
  {
    return Iterators.singletonIterator(this.element);
  }
  
  public int lastIndexOf(Object paramObject)
  {
    return indexOf(paramObject);
  }
  
  public int size()
  {
    return 1;
  }
  
  public ImmutableList subList(int paramInt1, int paramInt2)
  {
    Preconditions.checkPositionIndexes(paramInt1, paramInt2, 1);
    return paramInt1 == paramInt2 ? ImmutableList.of() : this;
  }
  
  public ImmutableList reverse()
  {
    return this;
  }
  
  public boolean contains(Object paramObject)
  {
    return this.element.equals(paramObject);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof List))
    {
      List localList = (List)paramObject;
      return (localList.size() == 1) && (this.element.equals(localList.get(0)));
    }
    return false;
  }
  
  public int hashCode()
  {
    return 31 + this.element.hashCode();
  }
  
  public String toString()
  {
    String str = this.element.toString();
    return str.length() + 2 + '[' + str + ']';
  }
  
  public boolean isEmpty()
  {
    return false;
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
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\SingletonImmutableList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */