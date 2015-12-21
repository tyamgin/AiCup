package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible
class StandardRowSortedTable
  extends StandardTable
  implements RowSortedTable
{
  private transient SortedSet rowKeySet;
  private transient RowSortedMap rowMap;
  private static final long serialVersionUID = 0L;
  
  StandardRowSortedTable(SortedMap paramSortedMap, Supplier paramSupplier)
  {
    super(paramSortedMap, paramSupplier);
  }
  
  private SortedMap sortedBackingMap()
  {
    return (SortedMap)this.backingMap;
  }
  
  public SortedSet rowKeySet()
  {
    SortedSet localSortedSet = this.rowKeySet;
    return localSortedSet == null ? (this.rowKeySet = new RowKeySortedSet(null)) : localSortedSet;
  }
  
  public SortedMap rowMap()
  {
    RowSortedMap localRowSortedMap = this.rowMap;
    return localRowSortedMap == null ? (this.rowMap = new RowSortedMap(null)) : localRowSortedMap;
  }
  
  private class RowSortedMap
    extends StandardTable.RowMap
    implements SortedMap
  {
    private RowSortedMap()
    {
      super();
    }
    
    public Comparator comparator()
    {
      return StandardRowSortedTable.this.sortedBackingMap().comparator();
    }
    
    public Object firstKey()
    {
      return StandardRowSortedTable.this.sortedBackingMap().firstKey();
    }
    
    public Object lastKey()
    {
      return StandardRowSortedTable.this.sortedBackingMap().lastKey();
    }
    
    public SortedMap headMap(Object paramObject)
    {
      Preconditions.checkNotNull(paramObject);
      return new StandardRowSortedTable(StandardRowSortedTable.this.sortedBackingMap().headMap(paramObject), StandardRowSortedTable.this.factory).rowMap();
    }
    
    public SortedMap subMap(Object paramObject1, Object paramObject2)
    {
      Preconditions.checkNotNull(paramObject1);
      Preconditions.checkNotNull(paramObject2);
      return new StandardRowSortedTable(StandardRowSortedTable.this.sortedBackingMap().subMap(paramObject1, paramObject2), StandardRowSortedTable.this.factory).rowMap();
    }
    
    public SortedMap tailMap(Object paramObject)
    {
      Preconditions.checkNotNull(paramObject);
      return new StandardRowSortedTable(StandardRowSortedTable.this.sortedBackingMap().tailMap(paramObject), StandardRowSortedTable.this.factory).rowMap();
    }
  }
  
  private class RowKeySortedSet
    extends StandardTable.RowKeySet
    implements SortedSet
  {
    private RowKeySortedSet()
    {
      super();
    }
    
    public Comparator comparator()
    {
      return StandardRowSortedTable.this.sortedBackingMap().comparator();
    }
    
    public Object first()
    {
      return StandardRowSortedTable.this.sortedBackingMap().firstKey();
    }
    
    public Object last()
    {
      return StandardRowSortedTable.this.sortedBackingMap().lastKey();
    }
    
    public SortedSet headSet(Object paramObject)
    {
      Preconditions.checkNotNull(paramObject);
      return new StandardRowSortedTable(StandardRowSortedTable.this.sortedBackingMap().headMap(paramObject), StandardRowSortedTable.this.factory).rowKeySet();
    }
    
    public SortedSet subSet(Object paramObject1, Object paramObject2)
    {
      Preconditions.checkNotNull(paramObject1);
      Preconditions.checkNotNull(paramObject2);
      return new StandardRowSortedTable(StandardRowSortedTable.this.sortedBackingMap().subMap(paramObject1, paramObject2), StandardRowSortedTable.this.factory).rowKeySet();
    }
    
    public SortedSet tailSet(Object paramObject)
    {
      Preconditions.checkNotNull(paramObject);
      return new StandardRowSortedTable(StandardRowSortedTable.this.sortedBackingMap().tailMap(paramObject), StandardRowSortedTable.this.factory).rowKeySet();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\StandardRowSortedTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */