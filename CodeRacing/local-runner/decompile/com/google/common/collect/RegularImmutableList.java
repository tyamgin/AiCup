package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.List;

@GwtCompatible(serializable=true, emulated=true)
class RegularImmutableList
  extends ImmutableList
{
  private final transient int offset;
  private final transient int size;
  private final transient Object[] array;
  
  RegularImmutableList(Object[] paramArrayOfObject, int paramInt1, int paramInt2)
  {
    this.offset = paramInt1;
    this.size = paramInt2;
    this.array = paramArrayOfObject;
  }
  
  RegularImmutableList(Object[] paramArrayOfObject)
  {
    this(paramArrayOfObject, 0, paramArrayOfObject.length);
  }
  
  public int size()
  {
    return this.size;
  }
  
  public boolean isEmpty()
  {
    return false;
  }
  
  boolean isPartialView()
  {
    return (this.offset != 0) || (this.size != this.array.length);
  }
  
  public Object[] toArray()
  {
    Object[] arrayOfObject = new Object[size()];
    System.arraycopy(this.array, this.offset, arrayOfObject, 0, this.size);
    return arrayOfObject;
  }
  
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject.length < this.size) {
      paramArrayOfObject = ObjectArrays.newArray(paramArrayOfObject, this.size);
    } else if (paramArrayOfObject.length > this.size) {
      paramArrayOfObject[this.size] = null;
    }
    System.arraycopy(this.array, this.offset, paramArrayOfObject, 0, this.size);
    return paramArrayOfObject;
  }
  
  public Object get(int paramInt)
  {
    Preconditions.checkElementIndex(paramInt, this.size);
    return this.array[(paramInt + this.offset)];
  }
  
  ImmutableList subListUnchecked(int paramInt1, int paramInt2)
  {
    return new RegularImmutableList(this.array, this.offset + paramInt1, paramInt2 - paramInt1);
  }
  
  public UnmodifiableListIterator listIterator(int paramInt)
  {
    return Iterators.forArray(this.array, this.offset, this.size, paramInt);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof List)) {
      return false;
    }
    List localList = (List)paramObject;
    if (size() != localList.size()) {
      return false;
    }
    int i = this.offset;
    Object localObject1;
    if ((paramObject instanceof RegularImmutableList))
    {
      localObject1 = (RegularImmutableList)paramObject;
      for (int j = ((RegularImmutableList)localObject1).offset; j < ((RegularImmutableList)localObject1).offset + ((RegularImmutableList)localObject1).size; j++) {
        if (!this.array[(i++)].equals(localObject1.array[j])) {
          return false;
        }
      }
    }
    else
    {
      localObject1 = localList.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Object localObject2 = ((Iterator)localObject1).next();
        if (!this.array[(i++)].equals(localObject2)) {
          return false;
        }
      }
    }
    return true;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = Collections2.newStringBuilderForCollection(size()).append('[').append(this.array[this.offset]);
    for (int i = this.offset + 1; i < this.offset + this.size; i++) {
      localStringBuilder.append(", ").append(this.array[i]);
    }
    return ']';
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\RegularImmutableList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */