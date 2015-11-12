package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible
abstract class RegularImmutableTable
  extends ImmutableTable
{
  private transient ImmutableCollection values;
  private transient ImmutableSet cellSet;
  
  public final ImmutableCollection values()
  {
    ImmutableCollection localImmutableCollection = this.values;
    return localImmutableCollection == null ? (this.values = createValues()) : localImmutableCollection;
  }
  
  abstract ImmutableCollection createValues();
  
  public abstract int size();
  
  public final boolean containsValue(Object paramObject)
  {
    return values().contains(paramObject);
  }
  
  public final ImmutableSet cellSet()
  {
    ImmutableSet localImmutableSet = this.cellSet;
    return localImmutableSet == null ? (this.cellSet = createCellSet()) : localImmutableSet;
  }
  
  abstract ImmutableSet createCellSet();
  
  public final boolean isEmpty()
  {
    return false;
  }
  
  static final RegularImmutableTable forCells(List paramList, Comparator paramComparator1, final Comparator paramComparator2)
  {
    Preconditions.checkNotNull(paramList);
    if ((paramComparator1 != null) || (paramComparator2 != null))
    {
      Comparator local1 = new Comparator()
      {
        public int compare(Table.Cell paramAnonymousCell1, Table.Cell paramAnonymousCell2)
        {
          int i = this.val$rowComparator == null ? 0 : this.val$rowComparator.compare(paramAnonymousCell1.getRowKey(), paramAnonymousCell2.getRowKey());
          if (i != 0) {
            return i;
          }
          return paramComparator2 == null ? 0 : paramComparator2.compare(paramAnonymousCell1.getColumnKey(), paramAnonymousCell2.getColumnKey());
        }
      };
      Collections.sort(paramList, local1);
    }
    return forCellsInternal(paramList, paramComparator1, paramComparator2);
  }
  
  static final RegularImmutableTable forCells(Iterable paramIterable)
  {
    return forCellsInternal(paramIterable, null, null);
  }
  
  private static final RegularImmutableTable forCellsInternal(Iterable paramIterable, Comparator paramComparator1, Comparator paramComparator2)
  {
    ImmutableSet.Builder localBuilder1 = ImmutableSet.builder();
    ImmutableSet.Builder localBuilder2 = ImmutableSet.builder();
    ImmutableList localImmutableList = ImmutableList.copyOf(paramIterable);
    Object localObject1 = localImmutableList.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Table.Cell)((Iterator)localObject1).next();
      localBuilder1.add(((Table.Cell)localObject2).getRowKey());
      localBuilder2.add(((Table.Cell)localObject2).getColumnKey());
    }
    localObject1 = localBuilder1.build();
    if (paramComparator1 != null)
    {
      localObject2 = Lists.newArrayList((Iterable)localObject1);
      Collections.sort((List)localObject2, paramComparator1);
      localObject1 = ImmutableSet.copyOf((Collection)localObject2);
    }
    Object localObject2 = localBuilder2.build();
    if (paramComparator2 != null)
    {
      ArrayList localArrayList = Lists.newArrayList((Iterable)localObject2);
      Collections.sort(localArrayList, paramComparator2);
      localObject2 = ImmutableSet.copyOf(localArrayList);
    }
    return localImmutableList.size() > ((ImmutableSet)localObject1).size() * ((ImmutableSet)localObject2).size() / 2 ? new DenseImmutableTable(localImmutableList, (ImmutableSet)localObject1, (ImmutableSet)localObject2) : new SparseImmutableTable(localImmutableList, (ImmutableSet)localObject1, (ImmutableSet)localObject2);
  }
  
  @VisibleForTesting
  static final class DenseImmutableTable
    extends RegularImmutableTable
  {
    private final ImmutableMap rowKeyToIndex;
    private final ImmutableMap columnKeyToIndex;
    private final ImmutableMap rowMap;
    private final ImmutableMap columnMap;
    private final int[] rowCounts;
    private final int[] columnCounts;
    private final Object[][] values;
    private final int[] iterationOrderRow;
    private final int[] iterationOrderColumn;
    
    private static ImmutableMap makeIndex(ImmutableSet paramImmutableSet)
    {
      ImmutableMap.Builder localBuilder = ImmutableMap.builder();
      int i = 0;
      Iterator localIterator = paramImmutableSet.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        localBuilder.put(localObject, Integer.valueOf(i));
        i++;
      }
      return localBuilder.build();
    }
    
    DenseImmutableTable(ImmutableList paramImmutableList, ImmutableSet paramImmutableSet1, ImmutableSet paramImmutableSet2)
    {
      super();
      Object[][] arrayOfObject = (Object[][])new Object[paramImmutableSet1.size()][paramImmutableSet2.size()];
      this.values = arrayOfObject;
      this.rowKeyToIndex = makeIndex(paramImmutableSet1);
      this.columnKeyToIndex = makeIndex(paramImmutableSet2);
      this.rowCounts = new int[this.rowKeyToIndex.size()];
      this.columnCounts = new int[this.columnKeyToIndex.size()];
      int[] arrayOfInt1 = new int[paramImmutableList.size()];
      int[] arrayOfInt2 = new int[paramImmutableList.size()];
      for (int i = 0; i < paramImmutableList.size(); i++)
      {
        Table.Cell localCell = (Table.Cell)paramImmutableList.get(i);
        Object localObject1 = localCell.getRowKey();
        Object localObject2 = localCell.getColumnKey();
        int j = ((Integer)this.rowKeyToIndex.get(localObject1)).intValue();
        int k = ((Integer)this.columnKeyToIndex.get(localObject2)).intValue();
        Object localObject3 = this.values[j][k];
        Preconditions.checkArgument(localObject3 == null, "duplicate key: (%s, %s)", new Object[] { localObject1, localObject2 });
        this.values[j][k] = localCell.getValue();
        this.rowCounts[j] += 1;
        this.columnCounts[k] += 1;
        arrayOfInt1[i] = j;
        arrayOfInt2[i] = k;
      }
      this.iterationOrderRow = arrayOfInt1;
      this.iterationOrderColumn = arrayOfInt2;
      this.rowMap = new RowMap(null);
      this.columnMap = new ColumnMap(null);
    }
    
    public ImmutableMap column(Object paramObject)
    {
      Integer localInteger = (Integer)this.columnKeyToIndex.get(Preconditions.checkNotNull(paramObject));
      if (localInteger == null) {
        return ImmutableMap.of();
      }
      return new Column(localInteger.intValue());
    }
    
    public ImmutableSet columnKeySet()
    {
      return this.columnKeyToIndex.keySet();
    }
    
    public ImmutableMap columnMap()
    {
      return this.columnMap;
    }
    
    public boolean contains(Object paramObject1, Object paramObject2)
    {
      return get(paramObject1, paramObject2) != null;
    }
    
    public boolean containsColumn(Object paramObject)
    {
      return this.columnKeyToIndex.containsKey(paramObject);
    }
    
    public boolean containsRow(Object paramObject)
    {
      return this.rowKeyToIndex.containsKey(paramObject);
    }
    
    public Object get(Object paramObject1, Object paramObject2)
    {
      Integer localInteger1 = (Integer)this.rowKeyToIndex.get(paramObject1);
      Integer localInteger2 = (Integer)this.columnKeyToIndex.get(paramObject2);
      return (localInteger1 == null) || (localInteger2 == null) ? null : this.values[localInteger1.intValue()][localInteger2.intValue()];
    }
    
    public ImmutableMap row(Object paramObject)
    {
      Preconditions.checkNotNull(paramObject);
      Integer localInteger = (Integer)this.rowKeyToIndex.get(paramObject);
      if (localInteger == null) {
        return ImmutableMap.of();
      }
      return new Row(localInteger.intValue());
    }
    
    public ImmutableSet rowKeySet()
    {
      return this.rowKeyToIndex.keySet();
    }
    
    public ImmutableMap rowMap()
    {
      return this.rowMap;
    }
    
    ImmutableCollection createValues()
    {
      new ImmutableList()
      {
        public int size()
        {
          return RegularImmutableTable.DenseImmutableTable.this.iterationOrderRow.length;
        }
        
        public Object get(int paramAnonymousInt)
        {
          return RegularImmutableTable.DenseImmutableTable.this.values[RegularImmutableTable.DenseImmutableTable.this.iterationOrderRow[paramAnonymousInt]][RegularImmutableTable.DenseImmutableTable.this.iterationOrderColumn[paramAnonymousInt]];
        }
        
        boolean isPartialView()
        {
          return true;
        }
      };
    }
    
    public int size()
    {
      return this.iterationOrderRow.length;
    }
    
    ImmutableSet createCellSet()
    {
      return new DenseCellSet();
    }
    
    class DenseCellSet
      extends RegularImmutableTable.CellSet
    {
      DenseCellSet()
      {
        super();
      }
      
      public UnmodifiableIterator iterator()
      {
        return asList().iterator();
      }
      
      ImmutableList createAsList()
      {
        new ImmutableAsList()
        {
          public Table.Cell get(int paramAnonymousInt)
          {
            int i = RegularImmutableTable.DenseImmutableTable.this.iterationOrderRow[paramAnonymousInt];
            int j = RegularImmutableTable.DenseImmutableTable.this.iterationOrderColumn[paramAnonymousInt];
            Object localObject1 = RegularImmutableTable.DenseImmutableTable.this.rowKeySet().asList().get(i);
            Object localObject2 = RegularImmutableTable.DenseImmutableTable.this.columnKeySet().asList().get(j);
            Object localObject3 = RegularImmutableTable.DenseImmutableTable.this.values[i][j];
            return Tables.immutableCell(localObject1, localObject2, localObject3);
          }
          
          ImmutableCollection delegateCollection()
          {
            return RegularImmutableTable.DenseImmutableTable.DenseCellSet.this;
          }
        };
      }
    }
    
    private final class ColumnMap
      extends RegularImmutableTable.ImmutableArrayMap
    {
      private ColumnMap()
      {
        super();
      }
      
      ImmutableMap keyToIndex()
      {
        return RegularImmutableTable.DenseImmutableTable.this.columnKeyToIndex;
      }
      
      Map getValue(int paramInt)
      {
        return new RegularImmutableTable.DenseImmutableTable.Column(RegularImmutableTable.DenseImmutableTable.this, paramInt);
      }
      
      boolean isPartialView()
      {
        return false;
      }
    }
    
    private final class RowMap
      extends RegularImmutableTable.ImmutableArrayMap
    {
      private RowMap()
      {
        super();
      }
      
      ImmutableMap keyToIndex()
      {
        return RegularImmutableTable.DenseImmutableTable.this.rowKeyToIndex;
      }
      
      Map getValue(int paramInt)
      {
        return new RegularImmutableTable.DenseImmutableTable.Row(RegularImmutableTable.DenseImmutableTable.this, paramInt);
      }
      
      boolean isPartialView()
      {
        return false;
      }
    }
    
    private final class Column
      extends RegularImmutableTable.ImmutableArrayMap
    {
      private final int columnIndex;
      
      Column(int paramInt)
      {
        super();
        this.columnIndex = paramInt;
      }
      
      ImmutableMap keyToIndex()
      {
        return RegularImmutableTable.DenseImmutableTable.this.rowKeyToIndex;
      }
      
      Object getValue(int paramInt)
      {
        return RegularImmutableTable.DenseImmutableTable.this.values[paramInt][this.columnIndex];
      }
      
      boolean isPartialView()
      {
        return true;
      }
    }
    
    private final class Row
      extends RegularImmutableTable.ImmutableArrayMap
    {
      private final int rowIndex;
      
      Row(int paramInt)
      {
        super();
        this.rowIndex = paramInt;
      }
      
      ImmutableMap keyToIndex()
      {
        return RegularImmutableTable.DenseImmutableTable.this.columnKeyToIndex;
      }
      
      Object getValue(int paramInt)
      {
        return RegularImmutableTable.DenseImmutableTable.this.values[this.rowIndex][paramInt];
      }
      
      boolean isPartialView()
      {
        return true;
      }
    }
  }
  
  private static abstract class ImmutableArrayMap
    extends ImmutableMap
  {
    private final int size;
    
    ImmutableArrayMap(int paramInt)
    {
      this.size = paramInt;
    }
    
    abstract ImmutableMap keyToIndex();
    
    private boolean isFull()
    {
      return this.size == keyToIndex().size();
    }
    
    Object getKey(int paramInt)
    {
      return keyToIndex().keySet().asList().get(paramInt);
    }
    
    abstract Object getValue(int paramInt);
    
    ImmutableSet createKeySet()
    {
      return isFull() ? keyToIndex().keySet() : super.createKeySet();
    }
    
    public int size()
    {
      return this.size;
    }
    
    public Object get(Object paramObject)
    {
      Integer localInteger = (Integer)keyToIndex().get(paramObject);
      return localInteger == null ? null : getValue(localInteger.intValue());
    }
    
    ImmutableSet createEntrySet()
    {
      if (isFull()) {
        new ImmutableMapEntrySet()
        {
          ImmutableMap map()
          {
            return RegularImmutableTable.ImmutableArrayMap.this;
          }
          
          public UnmodifiableIterator iterator()
          {
            new AbstractIndexedListIterator(size())
            {
              protected Map.Entry get(int paramAnonymous2Int)
              {
                return Maps.immutableEntry(RegularImmutableTable.ImmutableArrayMap.this.getKey(paramAnonymous2Int), RegularImmutableTable.ImmutableArrayMap.this.getValue(paramAnonymous2Int));
              }
            };
          }
        };
      }
      new ImmutableMapEntrySet()
      {
        ImmutableMap map()
        {
          return RegularImmutableTable.ImmutableArrayMap.this;
        }
        
        public UnmodifiableIterator iterator()
        {
          new AbstractIterator()
          {
            private int index = -1;
            private final int maxIndex = RegularImmutableTable.ImmutableArrayMap.this.keyToIndex().size();
            
            protected Map.Entry computeNext()
            {
              for (this.index += 1; this.index < this.maxIndex; this.index += 1)
              {
                Object localObject = RegularImmutableTable.ImmutableArrayMap.this.getValue(this.index);
                if (localObject != null) {
                  return Maps.immutableEntry(RegularImmutableTable.ImmutableArrayMap.this.getKey(this.index), localObject);
                }
              }
              return (Map.Entry)endOfData();
            }
          };
        }
      };
    }
  }
  
  @VisibleForTesting
  static final class SparseImmutableTable
    extends RegularImmutableTable
  {
    private final ImmutableMap rowMap;
    private final ImmutableMap columnMap;
    private final int[] iterationOrderRow;
    private final int[] iterationOrderColumn;
    
    SparseImmutableTable(ImmutableList paramImmutableList, ImmutableSet paramImmutableSet1, ImmutableSet paramImmutableSet2)
    {
      super();
      HashMap localHashMap = Maps.newHashMap();
      LinkedHashMap localLinkedHashMap = Maps.newLinkedHashMap();
      Object localObject1 = paramImmutableSet1.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = ((Iterator)localObject1).next();
        localHashMap.put(localObject2, Integer.valueOf(localLinkedHashMap.size()));
        localLinkedHashMap.put(localObject2, new LinkedHashMap());
      }
      localObject1 = Maps.newLinkedHashMap();
      Object localObject2 = paramImmutableSet2.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = ((Iterator)localObject2).next();
        ((Map)localObject1).put(localObject3, new LinkedHashMap());
      }
      localObject2 = new int[paramImmutableList.size()];
      Object localObject3 = new int[paramImmutableList.size()];
      Object localObject6;
      for (int i = 0; i < paramImmutableList.size(); i++)
      {
        localObject4 = (Table.Cell)paramImmutableList.get(i);
        localObject5 = ((Table.Cell)localObject4).getRowKey();
        localObject6 = ((Table.Cell)localObject4).getColumnKey();
        Object localObject7 = ((Table.Cell)localObject4).getValue();
        localObject2[i] = ((Integer)localHashMap.get(localObject5)).intValue();
        Map localMap = (Map)localLinkedHashMap.get(localObject5);
        localObject3[i] = localMap.size();
        Object localObject8 = localMap.put(localObject6, localObject7);
        if (localObject8 != null) {
          throw new IllegalArgumentException("Duplicate value for row=" + localObject5 + ", column=" + localObject6 + ": " + localObject7 + ", " + localObject8);
        }
        ((Map)((Map)localObject1).get(localObject6)).put(localObject5, localObject7);
      }
      this.iterationOrderRow = ((int[])localObject2);
      this.iterationOrderColumn = ((int[])localObject3);
      ImmutableMap.Builder localBuilder = ImmutableMap.builder();
      Object localObject4 = localLinkedHashMap.entrySet().iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (Map.Entry)((Iterator)localObject4).next();
        localBuilder.put(((Map.Entry)localObject5).getKey(), ImmutableMap.copyOf((Map)((Map.Entry)localObject5).getValue()));
      }
      this.rowMap = localBuilder.build();
      localObject4 = ImmutableMap.builder();
      Object localObject5 = ((Map)localObject1).entrySet().iterator();
      while (((Iterator)localObject5).hasNext())
      {
        localObject6 = (Map.Entry)((Iterator)localObject5).next();
        ((ImmutableMap.Builder)localObject4).put(((Map.Entry)localObject6).getKey(), ImmutableMap.copyOf((Map)((Map.Entry)localObject6).getValue()));
      }
      this.columnMap = ((ImmutableMap.Builder)localObject4).build();
    }
    
    public ImmutableMap column(Object paramObject)
    {
      Preconditions.checkNotNull(paramObject);
      return (ImmutableMap)Objects.firstNonNull((ImmutableMap)this.columnMap.get(paramObject), ImmutableMap.of());
    }
    
    public ImmutableSet columnKeySet()
    {
      return this.columnMap.keySet();
    }
    
    public ImmutableMap columnMap()
    {
      return this.columnMap;
    }
    
    public ImmutableMap row(Object paramObject)
    {
      Preconditions.checkNotNull(paramObject);
      return (ImmutableMap)Objects.firstNonNull((ImmutableMap)this.rowMap.get(paramObject), ImmutableMap.of());
    }
    
    public ImmutableSet rowKeySet()
    {
      return this.rowMap.keySet();
    }
    
    public ImmutableMap rowMap()
    {
      return this.rowMap;
    }
    
    public boolean contains(Object paramObject1, Object paramObject2)
    {
      Map localMap = (Map)this.rowMap.get(paramObject1);
      return (localMap != null) && (localMap.containsKey(paramObject2));
    }
    
    public boolean containsColumn(Object paramObject)
    {
      return this.columnMap.containsKey(paramObject);
    }
    
    public boolean containsRow(Object paramObject)
    {
      return this.rowMap.containsKey(paramObject);
    }
    
    public Object get(Object paramObject1, Object paramObject2)
    {
      Map localMap = (Map)this.rowMap.get(paramObject1);
      return localMap == null ? null : localMap.get(paramObject2);
    }
    
    ImmutableCollection createValues()
    {
      new ImmutableList()
      {
        public int size()
        {
          return RegularImmutableTable.SparseImmutableTable.this.iterationOrderRow.length;
        }
        
        public Object get(int paramAnonymousInt)
        {
          int i = RegularImmutableTable.SparseImmutableTable.this.iterationOrderRow[paramAnonymousInt];
          ImmutableMap localImmutableMap = (ImmutableMap)RegularImmutableTable.SparseImmutableTable.this.rowMap.values().asList().get(i);
          int j = RegularImmutableTable.SparseImmutableTable.this.iterationOrderColumn[paramAnonymousInt];
          return localImmutableMap.values().asList().get(j);
        }
        
        boolean isPartialView()
        {
          return true;
        }
      };
    }
    
    public int size()
    {
      return this.iterationOrderRow.length;
    }
    
    ImmutableSet createCellSet()
    {
      return new SparseCellSet();
    }
    
    class SparseCellSet
      extends RegularImmutableTable.CellSet
    {
      SparseCellSet()
      {
        super();
      }
      
      public UnmodifiableIterator iterator()
      {
        return asList().iterator();
      }
      
      ImmutableList createAsList()
      {
        new ImmutableAsList()
        {
          public Table.Cell get(int paramAnonymousInt)
          {
            int i = RegularImmutableTable.SparseImmutableTable.this.iterationOrderRow[paramAnonymousInt];
            Map.Entry localEntry1 = (Map.Entry)RegularImmutableTable.SparseImmutableTable.this.rowMap.entrySet().asList().get(i);
            ImmutableMap localImmutableMap = (ImmutableMap)localEntry1.getValue();
            int j = RegularImmutableTable.SparseImmutableTable.this.iterationOrderColumn[paramAnonymousInt];
            Map.Entry localEntry2 = (Map.Entry)localImmutableMap.entrySet().asList().get(j);
            return Tables.immutableCell(localEntry1.getKey(), localEntry2.getKey(), localEntry2.getValue());
          }
          
          ImmutableCollection delegateCollection()
          {
            return RegularImmutableTable.SparseImmutableTable.SparseCellSet.this;
          }
        };
      }
    }
  }
  
  abstract class CellSet
    extends ImmutableSet
  {
    CellSet() {}
    
    public int size()
    {
      return RegularImmutableTable.this.size();
    }
    
    public boolean contains(Object paramObject)
    {
      if ((paramObject instanceof Table.Cell))
      {
        Table.Cell localCell = (Table.Cell)paramObject;
        Object localObject = RegularImmutableTable.this.get(localCell.getRowKey(), localCell.getColumnKey());
        return (localObject != null) && (localObject.equals(localCell.getValue()));
      }
      return false;
    }
    
    boolean isPartialView()
    {
      return false;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\RegularImmutableTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */