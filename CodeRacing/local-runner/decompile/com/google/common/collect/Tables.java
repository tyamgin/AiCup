package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible
public final class Tables
{
  private static final Function UNMODIFIABLE_WRAPPER = new Function()
  {
    public Map apply(Map paramAnonymousMap)
    {
      return Collections.unmodifiableMap(paramAnonymousMap);
    }
  };
  
  public static Table.Cell immutableCell(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    return new ImmutableCell(paramObject1, paramObject2, paramObject3);
  }
  
  public static Table transpose(Table paramTable)
  {
    return (paramTable instanceof TransposeTable) ? ((TransposeTable)paramTable).original : new TransposeTable(paramTable);
  }
  
  @Beta
  public static Table newCustomTable(Map paramMap, Supplier paramSupplier)
  {
    Preconditions.checkArgument(paramMap.isEmpty());
    Preconditions.checkNotNull(paramSupplier);
    return new StandardTable(paramMap, paramSupplier);
  }
  
  @Beta
  public static Table transformValues(Table paramTable, Function paramFunction)
  {
    return new TransformedTable(paramTable, paramFunction);
  }
  
  public static Table unmodifiableTable(Table paramTable)
  {
    return new UnmodifiableTable(paramTable);
  }
  
  @Beta
  public static RowSortedTable unmodifiableRowSortedTable(RowSortedTable paramRowSortedTable)
  {
    return new UnmodifiableRowSortedMap(paramRowSortedTable);
  }
  
  private static Function unmodifiableWrapper()
  {
    return UNMODIFIABLE_WRAPPER;
  }
  
  static final class UnmodifiableRowSortedMap
    extends Tables.UnmodifiableTable
    implements RowSortedTable
  {
    private static final long serialVersionUID = 0L;
    
    public UnmodifiableRowSortedMap(RowSortedTable paramRowSortedTable)
    {
      super();
    }
    
    protected RowSortedTable delegate()
    {
      return (RowSortedTable)super.delegate();
    }
    
    public SortedMap rowMap()
    {
      Function localFunction = Tables.access$100();
      return Collections.unmodifiableSortedMap(Maps.transformValues(delegate().rowMap(), localFunction));
    }
    
    public SortedSet rowKeySet()
    {
      return Collections.unmodifiableSortedSet(delegate().rowKeySet());
    }
  }
  
  private static class UnmodifiableTable
    extends ForwardingTable
    implements Serializable
  {
    final Table delegate;
    private static final long serialVersionUID = 0L;
    
    UnmodifiableTable(Table paramTable)
    {
      this.delegate = ((Table)Preconditions.checkNotNull(paramTable));
    }
    
    protected Table delegate()
    {
      return this.delegate;
    }
    
    public Set cellSet()
    {
      return Collections.unmodifiableSet(super.cellSet());
    }
    
    public void clear()
    {
      throw new UnsupportedOperationException();
    }
    
    public Map column(Object paramObject)
    {
      return Collections.unmodifiableMap(super.column(paramObject));
    }
    
    public Set columnKeySet()
    {
      return Collections.unmodifiableSet(super.columnKeySet());
    }
    
    public Map columnMap()
    {
      Function localFunction = Tables.access$100();
      return Collections.unmodifiableMap(Maps.transformValues(super.columnMap(), localFunction));
    }
    
    public Object put(Object paramObject1, Object paramObject2, Object paramObject3)
    {
      throw new UnsupportedOperationException();
    }
    
    public void putAll(Table paramTable)
    {
      throw new UnsupportedOperationException();
    }
    
    public Object remove(Object paramObject1, Object paramObject2)
    {
      throw new UnsupportedOperationException();
    }
    
    public Map row(Object paramObject)
    {
      return Collections.unmodifiableMap(super.row(paramObject));
    }
    
    public Set rowKeySet()
    {
      return Collections.unmodifiableSet(super.rowKeySet());
    }
    
    public Map rowMap()
    {
      Function localFunction = Tables.access$100();
      return Collections.unmodifiableMap(Maps.transformValues(super.rowMap(), localFunction));
    }
    
    public Collection values()
    {
      return Collections.unmodifiableCollection(super.values());
    }
  }
  
  private static class TransformedTable
    implements Table
  {
    final Table fromTable;
    final Function function;
    CellSet cellSet;
    Collection values;
    Map rowMap;
    Map columnMap;
    
    TransformedTable(Table paramTable, Function paramFunction)
    {
      this.fromTable = ((Table)Preconditions.checkNotNull(paramTable));
      this.function = ((Function)Preconditions.checkNotNull(paramFunction));
    }
    
    public boolean contains(Object paramObject1, Object paramObject2)
    {
      return this.fromTable.contains(paramObject1, paramObject2);
    }
    
    public boolean containsRow(Object paramObject)
    {
      return this.fromTable.containsRow(paramObject);
    }
    
    public boolean containsColumn(Object paramObject)
    {
      return this.fromTable.containsColumn(paramObject);
    }
    
    public boolean containsValue(Object paramObject)
    {
      return values().contains(paramObject);
    }
    
    public Object get(Object paramObject1, Object paramObject2)
    {
      return contains(paramObject1, paramObject2) ? this.function.apply(this.fromTable.get(paramObject1, paramObject2)) : null;
    }
    
    public boolean isEmpty()
    {
      return this.fromTable.isEmpty();
    }
    
    public int size()
    {
      return this.fromTable.size();
    }
    
    public void clear()
    {
      this.fromTable.clear();
    }
    
    public Object put(Object paramObject1, Object paramObject2, Object paramObject3)
    {
      throw new UnsupportedOperationException();
    }
    
    public void putAll(Table paramTable)
    {
      throw new UnsupportedOperationException();
    }
    
    public Object remove(Object paramObject1, Object paramObject2)
    {
      return contains(paramObject1, paramObject2) ? this.function.apply(this.fromTable.remove(paramObject1, paramObject2)) : null;
    }
    
    public Map row(Object paramObject)
    {
      return Maps.transformValues(this.fromTable.row(paramObject), this.function);
    }
    
    public Map column(Object paramObject)
    {
      return Maps.transformValues(this.fromTable.column(paramObject), this.function);
    }
    
    Function cellFunction()
    {
      new Function()
      {
        public Table.Cell apply(Table.Cell paramAnonymousCell)
        {
          return Tables.immutableCell(paramAnonymousCell.getRowKey(), paramAnonymousCell.getColumnKey(), Tables.TransformedTable.this.function.apply(paramAnonymousCell.getValue()));
        }
      };
    }
    
    public Set cellSet()
    {
      return this.cellSet == null ? (this.cellSet = new CellSet()) : this.cellSet;
    }
    
    public Set rowKeySet()
    {
      return this.fromTable.rowKeySet();
    }
    
    public Set columnKeySet()
    {
      return this.fromTable.columnKeySet();
    }
    
    public Collection values()
    {
      return this.values == null ? (this.values = Collections2.transform(this.fromTable.values(), this.function)) : this.values;
    }
    
    Map createRowMap()
    {
      Function local2 = new Function()
      {
        public Map apply(Map paramAnonymousMap)
        {
          return Maps.transformValues(paramAnonymousMap, Tables.TransformedTable.this.function);
        }
      };
      return Maps.transformValues(this.fromTable.rowMap(), local2);
    }
    
    public Map rowMap()
    {
      return this.rowMap == null ? (this.rowMap = createRowMap()) : this.rowMap;
    }
    
    Map createColumnMap()
    {
      Function local3 = new Function()
      {
        public Map apply(Map paramAnonymousMap)
        {
          return Maps.transformValues(paramAnonymousMap, Tables.TransformedTable.this.function);
        }
      };
      return Maps.transformValues(this.fromTable.columnMap(), local3);
    }
    
    public Map columnMap()
    {
      return this.columnMap == null ? (this.columnMap = createColumnMap()) : this.columnMap;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
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
    
    class CellSet
      extends Collections2.TransformedCollection
      implements Set
    {
      CellSet()
      {
        super(Tables.TransformedTable.this.cellFunction());
      }
      
      public boolean equals(Object paramObject)
      {
        return Sets.equalsImpl(this, paramObject);
      }
      
      public int hashCode()
      {
        return Sets.hashCodeImpl(this);
      }
      
      public boolean contains(Object paramObject)
      {
        if ((paramObject instanceof Table.Cell))
        {
          Table.Cell localCell = (Table.Cell)paramObject;
          if (!Objects.equal(localCell.getValue(), Tables.TransformedTable.this.get(localCell.getRowKey(), localCell.getColumnKey()))) {
            return false;
          }
          return (localCell.getValue() != null) || (Tables.TransformedTable.this.fromTable.contains(localCell.getRowKey(), localCell.getColumnKey()));
        }
        return false;
      }
      
      public boolean remove(Object paramObject)
      {
        if (contains(paramObject))
        {
          Table.Cell localCell = (Table.Cell)paramObject;
          Tables.TransformedTable.this.fromTable.remove(localCell.getRowKey(), localCell.getColumnKey());
          return true;
        }
        return false;
      }
    }
  }
  
  private static class TransposeTable
    implements Table
  {
    final Table original;
    private static final Function TRANSPOSE_CELL = new Function()
    {
      public Table.Cell apply(Table.Cell paramAnonymousCell)
      {
        return Tables.immutableCell(paramAnonymousCell.getColumnKey(), paramAnonymousCell.getRowKey(), paramAnonymousCell.getValue());
      }
    };
    CellSet cellSet;
    
    TransposeTable(Table paramTable)
    {
      this.original = ((Table)Preconditions.checkNotNull(paramTable));
    }
    
    public void clear()
    {
      this.original.clear();
    }
    
    public Map column(Object paramObject)
    {
      return this.original.row(paramObject);
    }
    
    public Set columnKeySet()
    {
      return this.original.rowKeySet();
    }
    
    public Map columnMap()
    {
      return this.original.rowMap();
    }
    
    public boolean contains(Object paramObject1, Object paramObject2)
    {
      return this.original.contains(paramObject2, paramObject1);
    }
    
    public boolean containsColumn(Object paramObject)
    {
      return this.original.containsRow(paramObject);
    }
    
    public boolean containsRow(Object paramObject)
    {
      return this.original.containsColumn(paramObject);
    }
    
    public boolean containsValue(Object paramObject)
    {
      return this.original.containsValue(paramObject);
    }
    
    public Object get(Object paramObject1, Object paramObject2)
    {
      return this.original.get(paramObject2, paramObject1);
    }
    
    public boolean isEmpty()
    {
      return this.original.isEmpty();
    }
    
    public Object put(Object paramObject1, Object paramObject2, Object paramObject3)
    {
      return this.original.put(paramObject2, paramObject1, paramObject3);
    }
    
    public void putAll(Table paramTable)
    {
      this.original.putAll(Tables.transpose(paramTable));
    }
    
    public Object remove(Object paramObject1, Object paramObject2)
    {
      return this.original.remove(paramObject2, paramObject1);
    }
    
    public Map row(Object paramObject)
    {
      return this.original.column(paramObject);
    }
    
    public Set rowKeySet()
    {
      return this.original.columnKeySet();
    }
    
    public Map rowMap()
    {
      return this.original.columnMap();
    }
    
    public int size()
    {
      return this.original.size();
    }
    
    public Collection values()
    {
      return this.original.values();
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
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
      return localCellSet == null ? (this.cellSet = new CellSet()) : localCellSet;
    }
    
    class CellSet
      extends Collections2.TransformedCollection
      implements Set
    {
      CellSet()
      {
        super(Tables.TransposeTable.TRANSPOSE_CELL);
      }
      
      public boolean equals(Object paramObject)
      {
        if (paramObject == this) {
          return true;
        }
        if (!(paramObject instanceof Set)) {
          return false;
        }
        Set localSet = (Set)paramObject;
        if (localSet.size() != size()) {
          return false;
        }
        return containsAll(localSet);
      }
      
      public int hashCode()
      {
        return Sets.hashCodeImpl(this);
      }
      
      public boolean contains(Object paramObject)
      {
        if ((paramObject instanceof Table.Cell))
        {
          Table.Cell localCell = (Table.Cell)paramObject;
          return Tables.TransposeTable.this.original.cellSet().contains(Tables.immutableCell(localCell.getColumnKey(), localCell.getRowKey(), localCell.getValue()));
        }
        return false;
      }
      
      public boolean remove(Object paramObject)
      {
        if ((paramObject instanceof Table.Cell))
        {
          Table.Cell localCell = (Table.Cell)paramObject;
          return Tables.TransposeTable.this.original.cellSet().remove(Tables.immutableCell(localCell.getColumnKey(), localCell.getRowKey(), localCell.getValue()));
        }
        return false;
      }
    }
  }
  
  static abstract class AbstractCell
    implements Table.Cell
  {
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof Table.Cell))
      {
        Table.Cell localCell = (Table.Cell)paramObject;
        return (Objects.equal(getRowKey(), localCell.getRowKey())) && (Objects.equal(getColumnKey(), localCell.getColumnKey())) && (Objects.equal(getValue(), localCell.getValue()));
      }
      return false;
    }
    
    public int hashCode()
    {
      return Objects.hashCode(new Object[] { getRowKey(), getColumnKey(), getValue() });
    }
    
    public String toString()
    {
      return "(" + getRowKey() + "," + getColumnKey() + ")=" + getValue();
    }
  }
  
  static final class ImmutableCell
    extends Tables.AbstractCell
    implements Serializable
  {
    private final Object rowKey;
    private final Object columnKey;
    private final Object value;
    private static final long serialVersionUID = 0L;
    
    ImmutableCell(Object paramObject1, Object paramObject2, Object paramObject3)
    {
      this.rowKey = paramObject1;
      this.columnKey = paramObject2;
      this.value = paramObject3;
    }
    
    public Object getRowKey()
    {
      return this.rowKey;
    }
    
    public Object getColumnKey()
    {
      return this.columnKey;
    }
    
    public Object getValue()
    {
      return this.value;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Tables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */