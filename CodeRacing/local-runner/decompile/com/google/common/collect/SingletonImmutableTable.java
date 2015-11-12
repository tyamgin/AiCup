package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.Set;

@GwtCompatible
final class SingletonImmutableTable
  extends ImmutableTable
{
  private final Object singleRowKey;
  private final Object singleColumnKey;
  private final Object singleValue;
  
  SingletonImmutableTable(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    this.singleRowKey = Preconditions.checkNotNull(paramObject1);
    this.singleColumnKey = Preconditions.checkNotNull(paramObject2);
    this.singleValue = Preconditions.checkNotNull(paramObject3);
  }
  
  SingletonImmutableTable(Table.Cell paramCell)
  {
    this(paramCell.getRowKey(), paramCell.getColumnKey(), paramCell.getValue());
  }
  
  public ImmutableSet cellSet()
  {
    return ImmutableSet.of(Tables.immutableCell(this.singleRowKey, this.singleColumnKey, this.singleValue));
  }
  
  public ImmutableMap column(Object paramObject)
  {
    Preconditions.checkNotNull(paramObject);
    return containsColumn(paramObject) ? ImmutableMap.of(this.singleRowKey, this.singleValue) : ImmutableMap.of();
  }
  
  public ImmutableSet columnKeySet()
  {
    return ImmutableSet.of(this.singleColumnKey);
  }
  
  public ImmutableMap columnMap()
  {
    return ImmutableMap.of(this.singleColumnKey, ImmutableMap.of(this.singleRowKey, this.singleValue));
  }
  
  public boolean contains(Object paramObject1, Object paramObject2)
  {
    return (containsRow(paramObject1)) && (containsColumn(paramObject2));
  }
  
  public boolean containsColumn(Object paramObject)
  {
    return Objects.equal(this.singleColumnKey, paramObject);
  }
  
  public boolean containsRow(Object paramObject)
  {
    return Objects.equal(this.singleRowKey, paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return Objects.equal(this.singleValue, paramObject);
  }
  
  public Object get(Object paramObject1, Object paramObject2)
  {
    return contains(paramObject1, paramObject2) ? this.singleValue : null;
  }
  
  public boolean isEmpty()
  {
    return false;
  }
  
  public ImmutableMap row(Object paramObject)
  {
    Preconditions.checkNotNull(paramObject);
    return containsRow(paramObject) ? ImmutableMap.of(this.singleColumnKey, this.singleValue) : ImmutableMap.of();
  }
  
  public ImmutableSet rowKeySet()
  {
    return ImmutableSet.of(this.singleRowKey);
  }
  
  public ImmutableMap rowMap()
  {
    return ImmutableMap.of(this.singleRowKey, ImmutableMap.of(this.singleColumnKey, this.singleValue));
  }
  
  public int size()
  {
    return 1;
  }
  
  public ImmutableCollection values()
  {
    return ImmutableSet.of(this.singleValue);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof Table))
    {
      Table localTable = (Table)paramObject;
      if (localTable.size() == 1)
      {
        Table.Cell localCell = (Table.Cell)localTable.cellSet().iterator().next();
        return (Objects.equal(this.singleRowKey, localCell.getRowKey())) && (Objects.equal(this.singleColumnKey, localCell.getColumnKey())) && (Objects.equal(this.singleValue, localCell.getValue()));
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { this.singleRowKey, this.singleColumnKey, this.singleValue });
  }
  
  public String toString()
  {
    return '{' + this.singleRowKey + "={" + this.singleColumnKey + '=' + this.singleValue + "}}";
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\SingletonImmutableTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */