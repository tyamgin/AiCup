package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
class RegularImmutableAsList
  extends ImmutableAsList
{
  private final ImmutableCollection delegate;
  private final ImmutableList delegateList;
  
  RegularImmutableAsList(ImmutableCollection paramImmutableCollection, ImmutableList paramImmutableList)
  {
    this.delegate = paramImmutableCollection;
    this.delegateList = paramImmutableList;
  }
  
  RegularImmutableAsList(ImmutableCollection paramImmutableCollection, Object[] paramArrayOfObject)
  {
    this(paramImmutableCollection, ImmutableList.asImmutableList(paramArrayOfObject));
  }
  
  ImmutableCollection delegateCollection()
  {
    return this.delegate;
  }
  
  ImmutableList delegateList()
  {
    return this.delegateList;
  }
  
  public UnmodifiableListIterator listIterator(int paramInt)
  {
    return this.delegateList.listIterator(paramInt);
  }
  
  public Object[] toArray()
  {
    return this.delegateList.toArray();
  }
  
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    return this.delegateList.toArray(paramArrayOfObject);
  }
  
  public int indexOf(Object paramObject)
  {
    return this.delegateList.indexOf(paramObject);
  }
  
  public int lastIndexOf(Object paramObject)
  {
    return this.delegateList.lastIndexOf(paramObject);
  }
  
  public boolean equals(Object paramObject)
  {
    return this.delegateList.equals(paramObject);
  }
  
  public int hashCode()
  {
    return this.delegateList.hashCode();
  }
  
  public Object get(int paramInt)
  {
    return this.delegateList.get(paramInt);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\RegularImmutableAsList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */