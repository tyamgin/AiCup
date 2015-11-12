package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Beta
@GwtCompatible(emulated=true)
public final class ArrayTable
  implements Table, Serializable
{
  private final ImmutableList rowList;
  private final ImmutableList columnList;
  private final ImmutableMap rowKeyToIndex;
  private final ImmutableMap columnKeyToIndex;
  private final Object[][] array;
  private transient CellSet cellSet;
  private transient ColumnMap columnMap;
  private transient RowMap rowMap;
  private transient Collection values;
  private static final long serialVersionUID = 0L;
  
  public static ArrayTable create(Iterable paramIterable1, Iterable paramIterable2)
  {
    return new ArrayTable(paramIterable1, paramIterable2);
  }
  
  public static ArrayTable create(Table paramTable)
  {
    return new ArrayTable(paramTable);
  }
  
  public static ArrayTable create(ArrayTable paramArrayTable)
  {
    return new ArrayTable(paramArrayTable);
  }
  
  private ArrayTable(Iterable paramIterable1, Iterable paramIterable2)
  {
    this.rowList = ImmutableList.copyOf(paramIterable1);
    this.columnList = ImmutableList.copyOf(paramIterable2);
    Preconditions.checkArgument(!this.rowList.isEmpty());
    Preconditions.checkArgument(!this.columnList.isEmpty());
    this.rowKeyToIndex = index(this.rowList);
    this.columnKeyToIndex = index(this.columnList);
    Object[][] arrayOfObject = (Object[][])new Object[this.rowList.size()][this.columnList.size()];
    this.array = arrayOfObject;
    eraseAll();
  }
  
  private static ImmutableMap index(List paramList)
  {
    ImmutableMap.Builder localBuilder = ImmutableMap.builder();
    for (int i = 0; i < paramList.size(); i++) {
      localBuilder.put(paramList.get(i), Integer.valueOf(i));
    }
    return localBuilder.build();
  }
  
  private ArrayTable(Table paramTable)
  {
    this(paramTable.rowKeySet(), paramTable.columnKeySet());
    putAll(paramTable);
  }
  
  private ArrayTable(ArrayTable paramArrayTable)
  {
    this.rowList = paramArrayTable.rowList;
    this.columnList = paramArrayTable.columnList;
    this.rowKeyToIndex = paramArrayTable.rowKeyToIndex;
    this.columnKeyToIndex = paramArrayTable.columnKeyToIndex;
    Object[][] arrayOfObject = (Object[][])new Object[this.rowList.size()][this.columnList.size()];
    this.array = arrayOfObject;
    eraseAll();
    for (int i = 0; i < this.rowList.size(); i++) {
      System.arraycopy(paramArrayTable.array[i], 0, arrayOfObject[i], 0, paramArrayTable.array[i].length);
    }
  }
  
  public ImmutableList rowKeyList()
  {
    return this.rowList;
  }
  
  public ImmutableList columnKeyList()
  {
    return this.columnList;
  }
  
  public Object at(int paramInt1, int paramInt2)
  {
    Preconditions.checkElementIndex(paramInt1, this.rowList.size());
    Preconditions.checkElementIndex(paramInt2, this.columnList.size());
    return this.array[paramInt1][paramInt2];
  }
  
  public Object set(int paramInt1, int paramInt2, Object paramObject)
  {
    Preconditions.checkElementIndex(paramInt1, this.rowList.size());
    Preconditions.checkElementIndex(paramInt2, this.columnList.size());
    Object localObject = this.array[paramInt1][paramInt2];
    this.array[paramInt1][paramInt2] = paramObject;
    return localObject;
  }
  
  @GwtIncompatible("reflection")
  public Object[][] toArray(Class paramClass)
  {
    Object[][] arrayOfObject = (Object[][])Array.newInstance(paramClass, new int[] { this.rowList.size(), this.columnList.size() });
    for (int i = 0; i < this.rowList.size(); i++) {
      System.arraycopy(this.array[i], 0, arrayOfObject[i], 0, this.array[i].length);
    }
    return arrayOfObject;
  }
  
  @Deprecated
  public void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  public void eraseAll()
  {
    for (Object[] arrayOfObject1 : this.array) {
      Arrays.fill(arrayOfObject1, null);
    }
  }
  
  public boolean contains(Object paramObject1, Object paramObject2)
  {
    return (containsRow(paramObject1)) && (containsColumn(paramObject2));
  }
  
  public boolean containsColumn(Object paramObject)
  {
    return this.columnKeyToIndex.containsKey(paramObject);
  }
  
  public boolean containsRow(Object paramObject)
  {
    return this.rowKeyToIndex.containsKey(paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    for (Object[] arrayOfObject1 : this.array) {
      for (Object localObject : arrayOfObject1) {
        if (Objects.equal(paramObject, localObject)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public Object get(Object paramObject1, Object paramObject2)
  {
    Integer localInteger1 = (Integer)this.rowKeyToIndex.get(paramObject1);
    Integer localInteger2 = (Integer)this.columnKeyToIndex.get(paramObject2);
    return (localInteger1 == null) || (localInteger2 == null) ? null : at(localInteger1.intValue(), localInteger2.intValue());
  }
  
  public boolean isEmpty()
  {
    return false;
  }
  
  public Object put(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    Preconditions.checkNotNull(paramObject1);
    Preconditions.checkNotNull(paramObject2);
    Integer localInteger1 = (Integer)this.rowKeyToIndex.get(paramObject1);
    Preconditions.checkArgument(localInteger1 != null, "Row %s not in %s", new Object[] { paramObject1, this.rowList });
    Integer localInteger2 = (Integer)this.columnKeyToIndex.get(paramObject2);
    Preconditions.checkArgument(localInteger2 != null, "Column %s not in %s", new Object[] { paramObject2, this.columnList });
    return set(localInteger1.intValue(), localInteger2.intValue(), paramObject3);
  }
  
  public void putAll(Table paramTable)
  {
    Iterator localIterator = paramTable.cellSet().iterator();
    while (localIterator.hasNext())
    {
      Table.Cell localCell = (Table.Cell)localIterator.next();
      put(localCell.getRowKey(), localCell.getColumnKey(), localCell.getValue());
    }
  }
  
  @Deprecated
  public Object remove(Object paramObject1, Object paramObject2)
  {
    throw new UnsupportedOperationException();
  }
  
  public Object erase(Object paramObject1, Object paramObject2)
  {
    Integer localInteger1 = (Integer)this.rowKeyToIndex.get(paramObject1);
    Integer localInteger2 = (Integer)this.columnKeyToIndex.get(paramObject2);
    if ((localInteger1 == null) || (localInteger2 == null)) {
      return null;
    }
    return set(localInteger1.intValue(), localInteger2.intValue(), null);
  }
  
  public int size()
  {
    return this.rowList.size() * this.columnList.size();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Table))
    {
      Table localTable = (Table)paramObject;
      return cellSet().equals(localTable.cellSet());
    }
    return false;
  }
  
  public int hashCode()
  {
    return cellSet().hashCode();
  }
  
  public String toString()
  {
    return rowMap().toString();
  }
  
  public Set cellSet()
  {
    CellSet localCellSet = this.cellSet;
    return localCellSet == null ? (this.cellSet = new CellSet(null)) : localCellSet;
  }
  
  public Map column(Object paramObject)
  {
    Preconditions.checkNotNull(paramObject);
    Integer localInteger = (Integer)this.columnKeyToIndex.get(paramObject);
    return localInteger == null ? ImmutableMap.of() : new Column(localInteger.intValue());
  }
  
  public ImmutableSet columnKeySet()
  {
    return this.columnKeyToIndex.keySet();
  }
  
  public Map columnMap()
  {
    ColumnMap localColumnMap = this.columnMap;
    return localColumnMap == null ? (this.columnMap = new ColumnMap(null)) : localColumnMap;
  }
  
  public Map row(Object paramObject)
  {
    Preconditions.checkNotNull(paramObject);
    Integer localInteger = (Integer)this.rowKeyToIndex.get(paramObject);
    return localInteger == null ? ImmutableMap.of() : new Row(localInteger.intValue());
  }
  
  public ImmutableSet rowKeySet()
  {
    return this.rowKeyToIndex.keySet();
  }
  
  public Map rowMap()
  {
    RowMap localRowMap = this.rowMap;
    return localRowMap == null ? (this.rowMap = new RowMap(null)) : localRowMap;
  }
  
  public Collection values()
  {
    Collection localCollection = this.values;
    return localCollection == null ? (this.values = new Values(null)) : localCollection;
  }
  
  private class Values
    extends AbstractCollection
  {
    private Values() {}
    
    public Iterator iterator()
    {
      new TransformedIterator(ArrayTable.this.cellSet().iterator())
      {
        Object transform(Table.Cell paramAnonymousCell)
        {
          return paramAnonymousCell.getValue();
        }
      };
    }
    
    public int size()
    {
      return ArrayTable.this.size();
    }
  }
  
  private class RowMap
    extends ArrayTable.ArrayMap
  {
    private RowMap()
    {
      super(null);
    }
    
    String getKeyRole()
    {
      return "Row";
    }
    
    Map getValue(int paramInt)
    {
      return new ArrayTable.Row(ArrayTable.this, paramInt);
    }
    
    Map setValue(int paramInt, Map paramMap)
    {
      throw new UnsupportedOperationException();
    }
    
    public Map put(Object paramObject, Map paramMap)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private class Row
    extends ArrayTable.ArrayMap
  {
    final int rowIndex;
    
    Row(int paramInt)
    {
      super(null);
      this.rowIndex = paramInt;
    }
    
    String getKeyRole()
    {
      return "Column";
    }
    
    Object getValue(int paramInt)
    {
      return ArrayTable.this.at(this.rowIndex, paramInt);
    }
    
    Object setValue(int paramInt, Object paramObject)
    {
      return ArrayTable.this.set(this.rowIndex, paramInt, paramObject);
    }
  }
  
  private class ColumnMap
    extends ArrayTable.ArrayMap
  {
    private ColumnMap()
    {
      super(null);
    }
    
    String getKeyRole()
    {
      return "Column";
    }
    
    Map getValue(int paramInt)
    {
      return new ArrayTable.Column(ArrayTable.this, paramInt);
    }
    
    Map setValue(int paramInt, Map paramMap)
    {
      throw new UnsupportedOperationException();
    }
    
    public Map put(Object paramObject, Map paramMap)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private class Column
    extends ArrayTable.ArrayMap
  {
    final int columnIndex;
    
    Column(int paramInt)
    {
      super(null);
      this.columnIndex = paramInt;
    }
    
    String getKeyRole()
    {
      return "Row";
    }
    
    Object getValue(int paramInt)
    {
      return ArrayTable.this.at(paramInt, this.columnIndex);
    }
    
    Object setValue(int paramInt, Object paramObject)
    {
      return ArrayTable.this.set(paramInt, this.columnIndex, paramObject);
    }
  }
  
  private class CellSet
    extends AbstractSet
  {
    private CellSet() {}
    
    public Iterator iterator()
    {
      new AbstractIndexedListIterator(size())
      {
        protected Table.Cell get(final int paramAnonymousInt)
        {
          new Tables.AbstractCell()
          {
            final int rowIndex = paramAnonymousInt / ArrayTable.this.columnList.size();
            final int columnIndex = paramAnonymousInt % ArrayTable.this.columnList.size();
            
            public Object getRowKey()
            {
              return ArrayTable.this.rowList.get(this.rowIndex);
            }
            
            public Object getColumnKey()
            {
              return ArrayTable.this.columnList.get(this.columnIndex);
            }
            
            public Object getValue()
            {
              return ArrayTable.this.at(this.rowIndex, this.columnIndex);
            }
          };
        }
      };
    }
    
    public int size()
    {
      return ArrayTable.this.size();
    }
    
    public boolean contains(Object paramObject)
    {
      if ((paramObject instanceof Table.Cell))
      {
        Table.Cell localCell = (Table.Cell)paramObject;
        Integer localInteger1 = (Integer)ArrayTable.this.rowKeyToIndex.get(localCell.getRowKey());
        Integer localInteger2 = (Integer)ArrayTable.this.columnKeyToIndex.get(localCell.getColumnKey());
        return (localInteger1 != null) && (localInteger2 != null) && (Objects.equal(ArrayTable.this.at(localInteger1.intValue(), localInteger2.intValue()), localCell.getValue()));
      }
      return false;
    }
  }
  
  private static abstract class ArrayMap
    extends Maps.ImprovedAbstractMap
  {
    private final ImmutableMap keyIndex;
    
    private ArrayMap(ImmutableMap paramImmutableMap)
    {
      this.keyIndex = paramImmutableMap;
    }
    
    public Set keySet()
    {
      return this.keyIndex.keySet();
    }
    
    Object getKey(int paramInt)
    {
      return this.keyIndex.keySet().asList().get(paramInt);
    }
    
    abstract String getKeyRole();
    
    abstract Object getValue(int paramInt);
    
    abstract Object setValue(int paramInt, Object paramObject);
    
    public int size()
    {
      return this.keyIndex.size();
    }
    
    public boolean isEmpty()
    {
      return this.keyIndex.isEmpty();
    }
    
    protected Set createEntrySet()
    {
      new Maps.EntrySet()
      {
        Map map()
        {
          return ArrayTable.ArrayMap.this;
        }
        
        public Iterator iterator()
        {
          new AbstractIndexedListIterator(size())
          {
            protected Map.Entry get(final int paramAnonymous2Int)
            {
              new AbstractMapEntry()
              {
                public Object getKey()
                {
                  return ArrayTable.ArrayMap.this.getKey(paramAnonymous2Int);
                }
                
                public Object getValue()
                {
                  return ArrayTable.ArrayMap.this.getValue(paramAnonymous2Int);
                }
                
                public Object setValue(Object paramAnonymous3Object)
                {
                  return ArrayTable.ArrayMap.this.setValue(paramAnonymous2Int, paramAnonymous3Object);
                }
              };
            }
          };
        }
      };
    }
    
    public boolean containsKey(Object paramObject)
    {
      return this.keyIndex.containsKey(paramObject);
    }
    
    public Object get(Object paramObject)
    {
      Integer localInteger = (Integer)this.keyIndex.get(paramObject);
      if (localInteger == null) {
        return null;
      }
      return getValue(localInteger.intValue());
    }
    
    public Object put(Object paramObject1, Object paramObject2)
    {
      Integer localInteger = (Integer)this.keyIndex.get(paramObject1);
      if (localInteger == null) {
        throw new IllegalArgumentException(getKeyRole() + " " + paramObject1 + " not in " + this.keyIndex.keySet());
      }
      return setValue(localInteger.intValue(), paramObject2);
    }
    
    public Object remove(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public void clear()
    {
      throw new UnsupportedOperationException();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ArrayTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */