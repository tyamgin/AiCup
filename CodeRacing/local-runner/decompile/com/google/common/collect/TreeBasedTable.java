package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

@GwtCompatible(serializable=true)
@Beta
public class TreeBasedTable
  extends StandardRowSortedTable
{
  private final Comparator columnComparator;
  private static final long serialVersionUID = 0L;
  
  public static TreeBasedTable create()
  {
    return new TreeBasedTable(Ordering.natural(), Ordering.natural());
  }
  
  public static TreeBasedTable create(Comparator paramComparator1, Comparator paramComparator2)
  {
    Preconditions.checkNotNull(paramComparator1);
    Preconditions.checkNotNull(paramComparator2);
    return new TreeBasedTable(paramComparator1, paramComparator2);
  }
  
  public static TreeBasedTable create(TreeBasedTable paramTreeBasedTable)
  {
    TreeBasedTable localTreeBasedTable = new TreeBasedTable(paramTreeBasedTable.rowComparator(), paramTreeBasedTable.columnComparator());
    localTreeBasedTable.putAll(paramTreeBasedTable);
    return localTreeBasedTable;
  }
  
  TreeBasedTable(Comparator paramComparator1, Comparator paramComparator2)
  {
    super(new TreeMap(paramComparator1), new Factory(paramComparator2));
    this.columnComparator = paramComparator2;
  }
  
  public Comparator rowComparator()
  {
    return rowKeySet().comparator();
  }
  
  public Comparator columnComparator()
  {
    return this.columnComparator;
  }
  
  public SortedMap row(Object paramObject)
  {
    return new TreeRow(paramObject);
  }
  
  public SortedSet rowKeySet()
  {
    return super.rowKeySet();
  }
  
  public SortedMap rowMap()
  {
    return super.rowMap();
  }
  
  public boolean contains(Object paramObject1, Object paramObject2)
  {
    return super.contains(paramObject1, paramObject2);
  }
  
  public boolean containsColumn(Object paramObject)
  {
    return super.containsColumn(paramObject);
  }
  
  public boolean containsRow(Object paramObject)
  {
    return super.containsRow(paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return super.containsValue(paramObject);
  }
  
  public Object get(Object paramObject1, Object paramObject2)
  {
    return super.get(paramObject1, paramObject2);
  }
  
  public boolean equals(Object paramObject)
  {
    return super.equals(paramObject);
  }
  
  public Object remove(Object paramObject1, Object paramObject2)
  {
    return super.remove(paramObject1, paramObject2);
  }
  
  Iterator createColumnKeyIterator()
  {
    final Comparator localComparator = columnComparator();
    final UnmodifiableIterator localUnmodifiableIterator = Iterators.mergeSorted(Iterables.transform(this.backingMap.values(), new Function()
    {
      public Iterator apply(Map paramAnonymousMap)
      {
        return paramAnonymousMap.keySet().iterator();
      }
    }), localComparator);
    new AbstractIterator()
    {
      Object lastValue;
      
      protected Object computeNext()
      {
        while (localUnmodifiableIterator.hasNext())
        {
          Object localObject = localUnmodifiableIterator.next();
          int i = (this.lastValue != null) && (localComparator.compare(localObject, this.lastValue) == 0) ? 1 : 0;
          if (i == 0)
          {
            this.lastValue = localObject;
            return this.lastValue;
          }
        }
        this.lastValue = null;
        return endOfData();
      }
    };
  }
  
  private class TreeRow
    extends StandardTable.Row
    implements SortedMap
  {
    final Object lowerBound;
    final Object upperBound;
    transient SortedMap wholeRow;
    
    TreeRow(Object paramObject)
    {
      this(paramObject, null, null);
    }
    
    TreeRow(Object paramObject1, Object paramObject2, Object paramObject3)
    {
      super(paramObject1);
      this.lowerBound = paramObject2;
      this.upperBound = paramObject3;
      Preconditions.checkArgument((paramObject2 == null) || (paramObject3 == null) || (compare(paramObject2, paramObject3) <= 0));
    }
    
    public Comparator comparator()
    {
      return TreeBasedTable.this.columnComparator();
    }
    
    int compare(Object paramObject1, Object paramObject2)
    {
      Comparator localComparator = comparator();
      return localComparator.compare(paramObject1, paramObject2);
    }
    
    boolean rangeContains(Object paramObject)
    {
      return (paramObject != null) && ((this.lowerBound == null) || (compare(this.lowerBound, paramObject) <= 0)) && ((this.upperBound == null) || (compare(this.upperBound, paramObject) > 0));
    }
    
    public SortedMap subMap(Object paramObject1, Object paramObject2)
    {
      Preconditions.checkArgument((rangeContains(Preconditions.checkNotNull(paramObject1))) && (rangeContains(Preconditions.checkNotNull(paramObject2))));
      return new TreeRow(TreeBasedTable.this, this.rowKey, paramObject1, paramObject2);
    }
    
    public SortedMap headMap(Object paramObject)
    {
      Preconditions.checkArgument(rangeContains(Preconditions.checkNotNull(paramObject)));
      return new TreeRow(TreeBasedTable.this, this.rowKey, this.lowerBound, paramObject);
    }
    
    public SortedMap tailMap(Object paramObject)
    {
      Preconditions.checkArgument(rangeContains(Preconditions.checkNotNull(paramObject)));
      return new TreeRow(TreeBasedTable.this, this.rowKey, paramObject, this.upperBound);
    }
    
    public Object firstKey()
    {
      SortedMap localSortedMap = backingRowMap();
      if (localSortedMap == null) {
        throw new NoSuchElementException();
      }
      return backingRowMap().firstKey();
    }
    
    public Object lastKey()
    {
      SortedMap localSortedMap = backingRowMap();
      if (localSortedMap == null) {
        throw new NoSuchElementException();
      }
      return backingRowMap().lastKey();
    }
    
    SortedMap wholeRow()
    {
      if ((this.wholeRow == null) || ((this.wholeRow.isEmpty()) && (TreeBasedTable.this.backingMap.containsKey(this.rowKey)))) {
        this.wholeRow = ((SortedMap)TreeBasedTable.this.backingMap.get(this.rowKey));
      }
      return this.wholeRow;
    }
    
    SortedMap backingRowMap()
    {
      return (SortedMap)super.backingRowMap();
    }
    
    SortedMap computeBackingRowMap()
    {
      SortedMap localSortedMap = wholeRow();
      if (localSortedMap != null)
      {
        if (this.lowerBound != null) {
          localSortedMap = localSortedMap.tailMap(this.lowerBound);
        }
        if (this.upperBound != null) {
          localSortedMap = localSortedMap.headMap(this.upperBound);
        }
        return localSortedMap;
      }
      return null;
    }
    
    void maintainEmptyInvariant()
    {
      if ((wholeRow() != null) && (this.wholeRow.isEmpty()))
      {
        TreeBasedTable.this.backingMap.remove(this.rowKey);
        this.wholeRow = null;
        this.backingRowMap = null;
      }
    }
    
    public boolean containsKey(Object paramObject)
    {
      return (rangeContains(paramObject)) && (super.containsKey(paramObject));
    }
    
    public Object put(Object paramObject1, Object paramObject2)
    {
      Preconditions.checkArgument(rangeContains(Preconditions.checkNotNull(paramObject1)));
      return super.put(paramObject1, paramObject2);
    }
  }
  
  private static class Factory
    implements Supplier, Serializable
  {
    final Comparator comparator;
    private static final long serialVersionUID = 0L;
    
    Factory(Comparator paramComparator)
    {
      this.comparator = paramComparator;
    }
    
    public TreeMap get()
    {
      return new TreeMap(this.comparator);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\TreeBasedTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */