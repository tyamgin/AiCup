package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;

@GwtCompatible
final class EmptyImmutableTable
  extends ImmutableTable
{
  static final EmptyImmutableTable INSTANCE = new EmptyImmutableTable();
  private static final long serialVersionUID = 0L;
  
  public int size()
  {
    return 0;
  }
  
  public Object get(Object paramObject1, Object paramObject2)
  {
    return null;
  }
  
  public boolean isEmpty()
  {
    return true;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof Table))
    {
      Table localTable = (Table)paramObject;
      return localTable.isEmpty();
    }
    return false;
  }
  
  public int hashCode()
  {
    return 0;
  }
  
  public ImmutableSet cellSet()
  {
    return ImmutableSet.of();
  }
  
  public ImmutableMap column(Object paramObject)
  {
    Preconditions.checkNotNull(paramObject);
    return ImmutableMap.of();
  }
  
  public ImmutableSet columnKeySet()
  {
    return ImmutableSet.of();
  }
  
  public ImmutableMap columnMap()
  {
    return ImmutableMap.of();
  }
  
  public boolean contains(Object paramObject1, Object paramObject2)
  {
    return false;
  }
  
  public boolean containsColumn(Object paramObject)
  {
    return false;
  }
  
  public boolean containsRow(Object paramObject)
  {
    return false;
  }
  
  public boolean containsValue(Object paramObject)
  {
    return false;
  }
  
  public ImmutableMap row(Object paramObject)
  {
    Preconditions.checkNotNull(paramObject);
    return ImmutableMap.of();
  }
  
  public ImmutableSet rowKeySet()
  {
    return ImmutableSet.of();
  }
  
  public ImmutableMap rowMap()
  {
    return ImmutableMap.of();
  }
  
  public String toString()
  {
    return "{}";
  }
  
  public ImmutableCollection values()
  {
    return ImmutableSet.of();
  }
  
  Object readResolve()
  {
    return INSTANCE;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\EmptyImmutableTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */