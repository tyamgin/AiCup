package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible
class StandardTable
  implements Table, Serializable
{
  @GwtTransient
  final Map backingMap;
  @GwtTransient
  final Supplier factory;
  private transient CellSet cellSet;
  private transient RowKeySet rowKeySet;
  private transient Set columnKeySet;
  private transient Values values;
  private transient RowMap rowMap;
  private transient ColumnMap columnMap;
  private static final long serialVersionUID = 0L;
  
  StandardTable(Map paramMap, Supplier paramSupplier)
  {
    this.backingMap = paramMap;
    this.factory = paramSupplier;
  }
  
  public boolean contains(Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 == null) || (paramObject2 == null)) {
      return false;
    }
    Map localMap = (Map)Maps.safeGet(this.backingMap, paramObject1);
    return (localMap != null) && (Maps.safeContainsKey(localMap, paramObject2));
  }
  
  public boolean containsColumn(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    Iterator localIterator = this.backingMap.values().iterator();
    while (localIterator.hasNext())
    {
      Map localMap = (Map)localIterator.next();
      if (Maps.safeContainsKey(localMap, paramObject)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean containsRow(Object paramObject)
  {
    return (paramObject != null) && (Maps.safeContainsKey(this.backingMap, paramObject));
  }
  
  public boolean containsValue(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    Iterator localIterator = this.backingMap.values().iterator();
    while (localIterator.hasNext())
    {
      Map localMap = (Map)localIterator.next();
      if (localMap.containsValue(paramObject)) {
        return true;
      }
    }
    return false;
  }
  
  public Object get(Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 == null) || (paramObject2 == null)) {
      return null;
    }
    Map localMap = (Map)Maps.safeGet(this.backingMap, paramObject1);
    return localMap == null ? null : Maps.safeGet(localMap, paramObject2);
  }
  
  public boolean isEmpty()
  {
    return this.backingMap.isEmpty();
  }
  
  public int size()
  {
    int i = 0;
    Iterator localIterator = this.backingMap.values().iterator();
    while (localIterator.hasNext())
    {
      Map localMap = (Map)localIterator.next();
      i += localMap.size();
    }
    return i;
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
  
  public void clear()
  {
    this.backingMap.clear();
  }
  
  private Map getOrCreate(Object paramObject)
  {
    Map localMap = (Map)this.backingMap.get(paramObject);
    if (localMap == null)
    {
      localMap = (Map)this.factory.get();
      this.backingMap.put(paramObject, localMap);
    }
    return localMap;
  }
  
  public Object put(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    Preconditions.checkNotNull(paramObject1);
    Preconditions.checkNotNull(paramObject2);
    Preconditions.checkNotNull(paramObject3);
    return getOrCreate(paramObject1).put(paramObject2, paramObject3);
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
  
  public Object remove(Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 == null) || (paramObject2 == null)) {
      return null;
    }
    Map localMap = (Map)Maps.safeGet(this.backingMap, paramObject1);
    if (localMap == null) {
      return null;
    }
    Object localObject = localMap.remove(paramObject2);
    if (localMap.isEmpty()) {
      this.backingMap.remove(paramObject1);
    }
    return localObject;
  }
  
  private Map removeColumn(Object paramObject)
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    Iterator localIterator = this.backingMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object localObject = ((Map)localEntry.getValue()).remove(paramObject);
      if (localObject != null)
      {
        localLinkedHashMap.put(localEntry.getKey(), localObject);
        if (((Map)localEntry.getValue()).isEmpty()) {
          localIterator.remove();
        }
      }
    }
    return localLinkedHashMap;
  }
  
  private boolean containsMapping(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    return (paramObject3 != null) && (paramObject3.equals(get(paramObject1, paramObject2)));
  }
  
  private boolean removeMapping(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    if (containsMapping(paramObject1, paramObject2, paramObject3))
    {
      remove(paramObject1, paramObject2);
      return true;
    }
    return false;
  }
  
  public Set cellSet()
  {
    CellSet localCellSet = this.cellSet;
    return localCellSet == null ? (this.cellSet = new CellSet(null)) : localCellSet;
  }
  
  public Map row(Object paramObject)
  {
    return new Row(paramObject);
  }
  
  public Map column(Object paramObject)
  {
    return new Column(paramObject);
  }
  
  public Set rowKeySet()
  {
    RowKeySet localRowKeySet = this.rowKeySet;
    return localRowKeySet == null ? (this.rowKeySet = new RowKeySet()) : localRowKeySet;
  }
  
  public Set columnKeySet()
  {
    Set localSet = this.columnKeySet;
    return localSet == null ? (this.columnKeySet = new ColumnKeySet(null)) : localSet;
  }
  
  Iterator createColumnKeyIterator()
  {
    return new ColumnKeyIterator(null);
  }
  
  public Collection values()
  {
    Values localValues = this.values;
    return localValues == null ? (this.values = new Values(null)) : localValues;
  }
  
  public Map rowMap()
  {
    RowMap localRowMap = this.rowMap;
    return localRowMap == null ? (this.rowMap = new RowMap()) : localRowMap;
  }
  
  public Map columnMap()
  {
    ColumnMap localColumnMap = this.columnMap;
    return localColumnMap == null ? (this.columnMap = new ColumnMap(null)) : localColumnMap;
  }
  
  private class ColumnMap
    extends Maps.ImprovedAbstractMap
  {
    ColumnMapValues columnMapValues;
    
    private ColumnMap() {}
    
    public Map get(Object paramObject)
    {
      return StandardTable.this.containsColumn(paramObject) ? StandardTable.this.column(paramObject) : null;
    }
    
    public boolean containsKey(Object paramObject)
    {
      return StandardTable.this.containsColumn(paramObject);
    }
    
    public Map remove(Object paramObject)
    {
      return StandardTable.this.containsColumn(paramObject) ? StandardTable.this.removeColumn(paramObject) : null;
    }
    
    public Set createEntrySet()
    {
      return new ColumnMapEntrySet();
    }
    
    public Set keySet()
    {
      return StandardTable.this.columnKeySet();
    }
    
    public Collection values()
    {
      ColumnMapValues localColumnMapValues = this.columnMapValues;
      return localColumnMapValues == null ? (this.columnMapValues = new ColumnMapValues(null)) : localColumnMapValues;
    }
    
    private class ColumnMapValues
      extends StandardTable.TableCollection
    {
      private ColumnMapValues()
      {
        super(null);
      }
      
      public Iterator iterator()
      {
        return Maps.valueIterator(StandardTable.ColumnMap.this.entrySet().iterator());
      }
      
      public boolean remove(Object paramObject)
      {
        Iterator localIterator = StandardTable.ColumnMap.this.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if (((Map)localEntry.getValue()).equals(paramObject))
          {
            StandardTable.this.removeColumn(localEntry.getKey());
            return true;
          }
        }
        return false;
      }
      
      public boolean removeAll(Collection paramCollection)
      {
        Preconditions.checkNotNull(paramCollection);
        boolean bool = false;
        Iterator localIterator = Lists.newArrayList(StandardTable.this.columnKeySet().iterator()).iterator();
        while (localIterator.hasNext())
        {
          Object localObject = localIterator.next();
          if (paramCollection.contains(StandardTable.this.column(localObject)))
          {
            StandardTable.this.removeColumn(localObject);
            bool = true;
          }
        }
        return bool;
      }
      
      public boolean retainAll(Collection paramCollection)
      {
        Preconditions.checkNotNull(paramCollection);
        boolean bool = false;
        Iterator localIterator = Lists.newArrayList(StandardTable.this.columnKeySet().iterator()).iterator();
        while (localIterator.hasNext())
        {
          Object localObject = localIterator.next();
          if (!paramCollection.contains(StandardTable.this.column(localObject)))
          {
            StandardTable.this.removeColumn(localObject);
            bool = true;
          }
        }
        return bool;
      }
      
      public int size()
      {
        return StandardTable.this.columnKeySet().size();
      }
    }
    
    class ColumnMapEntrySet
      extends StandardTable.TableSet
    {
      ColumnMapEntrySet()
      {
        super(null);
      }
      
      public Iterator iterator()
      {
        new TransformedIterator(StandardTable.this.columnKeySet().iterator())
        {
          Map.Entry transform(Object paramAnonymousObject)
          {
            return new ImmutableEntry(paramAnonymousObject, StandardTable.this.column(paramAnonymousObject));
          }
        };
      }
      
      public int size()
      {
        return StandardTable.this.columnKeySet().size();
      }
      
      public boolean contains(Object paramObject)
      {
        if ((paramObject instanceof Map.Entry))
        {
          Map.Entry localEntry = (Map.Entry)paramObject;
          if (StandardTable.this.containsColumn(localEntry.getKey()))
          {
            Object localObject = localEntry.getKey();
            return StandardTable.ColumnMap.this.get(localObject).equals(localEntry.getValue());
          }
        }
        return false;
      }
      
      public boolean remove(Object paramObject)
      {
        if (contains(paramObject))
        {
          Map.Entry localEntry = (Map.Entry)paramObject;
          StandardTable.this.removeColumn(localEntry.getKey());
          return true;
        }
        return false;
      }
      
      public boolean removeAll(Collection paramCollection)
      {
        boolean bool = false;
        Iterator localIterator = paramCollection.iterator();
        while (localIterator.hasNext())
        {
          Object localObject = localIterator.next();
          bool |= remove(localObject);
        }
        return bool;
      }
      
      public boolean retainAll(Collection paramCollection)
      {
        boolean bool = false;
        Iterator localIterator = Lists.newArrayList(StandardTable.this.columnKeySet().iterator()).iterator();
        while (localIterator.hasNext())
        {
          Object localObject = localIterator.next();
          if (!paramCollection.contains(new ImmutableEntry(localObject, StandardTable.this.column(localObject))))
          {
            StandardTable.this.removeColumn(localObject);
            bool = true;
          }
        }
        return bool;
      }
    }
  }
  
  class RowMap
    extends Maps.ImprovedAbstractMap
  {
    RowMap() {}
    
    public boolean containsKey(Object paramObject)
    {
      return StandardTable.this.containsRow(paramObject);
    }
    
    public Map get(Object paramObject)
    {
      return StandardTable.this.containsRow(paramObject) ? StandardTable.this.row(paramObject) : null;
    }
    
    public Set keySet()
    {
      return StandardTable.this.rowKeySet();
    }
    
    public Map remove(Object paramObject)
    {
      return paramObject == null ? null : (Map)StandardTable.this.backingMap.remove(paramObject);
    }
    
    protected Set createEntrySet()
    {
      return new EntrySet();
    }
    
    class EntrySet
      extends StandardTable.TableSet
    {
      EntrySet()
      {
        super(null);
      }
      
      public Iterator iterator()
      {
        new TransformedIterator(StandardTable.this.backingMap.keySet().iterator())
        {
          Map.Entry transform(Object paramAnonymousObject)
          {
            return new ImmutableEntry(paramAnonymousObject, StandardTable.this.row(paramAnonymousObject));
          }
        };
      }
      
      public int size()
      {
        return StandardTable.this.backingMap.size();
      }
      
      public boolean contains(Object paramObject)
      {
        if ((paramObject instanceof Map.Entry))
        {
          Map.Entry localEntry = (Map.Entry)paramObject;
          return (localEntry.getKey() != null) && ((localEntry.getValue() instanceof Map)) && (Collections2.safeContains(StandardTable.this.backingMap.entrySet(), localEntry));
        }
        return false;
      }
      
      public boolean remove(Object paramObject)
      {
        if ((paramObject instanceof Map.Entry))
        {
          Map.Entry localEntry = (Map.Entry)paramObject;
          return (localEntry.getKey() != null) && ((localEntry.getValue() instanceof Map)) && (StandardTable.this.backingMap.entrySet().remove(localEntry));
        }
        return false;
      }
    }
  }
  
  private class Values
    extends StandardTable.TableCollection
  {
    private Values()
    {
      super(null);
    }
    
    public Iterator iterator()
    {
      new TransformedIterator(StandardTable.this.cellSet().iterator())
      {
        Object transform(Table.Cell paramAnonymousCell)
        {
          return paramAnonymousCell.getValue();
        }
      };
    }
    
    public int size()
    {
      return StandardTable.this.size();
    }
  }
  
  private class ColumnKeyIterator
    extends AbstractIterator
  {
    final Map seen = (Map)StandardTable.this.factory.get();
    final Iterator mapIterator = StandardTable.this.backingMap.values().iterator();
    Iterator entryIterator = Iterators.emptyIterator();
    
    private ColumnKeyIterator() {}
    
    protected Object computeNext()
    {
      for (;;)
      {
        if (this.entryIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)this.entryIterator.next();
          if (!this.seen.containsKey(localEntry.getKey()))
          {
            this.seen.put(localEntry.getKey(), localEntry.getValue());
            return localEntry.getKey();
          }
        }
        else
        {
          if (!this.mapIterator.hasNext()) {
            break;
          }
          this.entryIterator = ((Map)this.mapIterator.next()).entrySet().iterator();
        }
      }
      return endOfData();
    }
  }
  
  private class ColumnKeySet
    extends StandardTable.TableSet
  {
    private ColumnKeySet()
    {
      super(null);
    }
    
    public Iterator iterator()
    {
      return StandardTable.this.createColumnKeyIterator();
    }
    
    public int size()
    {
      return Iterators.size(iterator());
    }
    
    public boolean remove(Object paramObject)
    {
      if (paramObject == null) {
        return false;
      }
      boolean bool = false;
      Iterator localIterator = StandardTable.this.backingMap.values().iterator();
      while (localIterator.hasNext())
      {
        Map localMap = (Map)localIterator.next();
        if (localMap.keySet().remove(paramObject))
        {
          bool = true;
          if (localMap.isEmpty()) {
            localIterator.remove();
          }
        }
      }
      return bool;
    }
    
    public boolean removeAll(Collection paramCollection)
    {
      Preconditions.checkNotNull(paramCollection);
      boolean bool = false;
      Iterator localIterator = StandardTable.this.backingMap.values().iterator();
      while (localIterator.hasNext())
      {
        Map localMap = (Map)localIterator.next();
        if (Iterators.removeAll(localMap.keySet().iterator(), paramCollection))
        {
          bool = true;
          if (localMap.isEmpty()) {
            localIterator.remove();
          }
        }
      }
      return bool;
    }
    
    public boolean retainAll(Collection paramCollection)
    {
      Preconditions.checkNotNull(paramCollection);
      boolean bool = false;
      Iterator localIterator = StandardTable.this.backingMap.values().iterator();
      while (localIterator.hasNext())
      {
        Map localMap = (Map)localIterator.next();
        if (localMap.keySet().retainAll(paramCollection))
        {
          bool = true;
          if (localMap.isEmpty()) {
            localIterator.remove();
          }
        }
      }
      return bool;
    }
    
    public boolean contains(Object paramObject)
    {
      if (paramObject == null) {
        return false;
      }
      Iterator localIterator = StandardTable.this.backingMap.values().iterator();
      while (localIterator.hasNext())
      {
        Map localMap = (Map)localIterator.next();
        if (localMap.containsKey(paramObject)) {
          return true;
        }
      }
      return false;
    }
  }
  
  class RowKeySet
    extends StandardTable.TableSet
  {
    RowKeySet()
    {
      super(null);
    }
    
    public Iterator iterator()
    {
      return Maps.keyIterator(StandardTable.this.rowMap().entrySet().iterator());
    }
    
    public int size()
    {
      return StandardTable.this.backingMap.size();
    }
    
    public boolean contains(Object paramObject)
    {
      return StandardTable.this.containsRow(paramObject);
    }
    
    public boolean remove(Object paramObject)
    {
      return (paramObject != null) && (StandardTable.this.backingMap.remove(paramObject) != null);
    }
  }
  
  private class Column
    extends Maps.ImprovedAbstractMap
  {
    final Object columnKey;
    Values columnValues;
    KeySet keySet;
    
    Column(Object paramObject)
    {
      this.columnKey = Preconditions.checkNotNull(paramObject);
    }
    
    public Object put(Object paramObject1, Object paramObject2)
    {
      return StandardTable.this.put(paramObject1, this.columnKey, paramObject2);
    }
    
    public Object get(Object paramObject)
    {
      return StandardTable.this.get(paramObject, this.columnKey);
    }
    
    public boolean containsKey(Object paramObject)
    {
      return StandardTable.this.contains(paramObject, this.columnKey);
    }
    
    public Object remove(Object paramObject)
    {
      return StandardTable.this.remove(paramObject, this.columnKey);
    }
    
    public Set createEntrySet()
    {
      return new EntrySet();
    }
    
    public Collection values()
    {
      Values localValues = this.columnValues;
      return localValues == null ? (this.columnValues = new Values()) : localValues;
    }
    
    boolean removePredicate(Predicate paramPredicate)
    {
      boolean bool = false;
      Iterator localIterator = StandardTable.this.backingMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Map localMap = (Map)localEntry.getValue();
        Object localObject = localMap.get(this.columnKey);
        if ((localObject != null) && (paramPredicate.apply(new ImmutableEntry(localEntry.getKey(), localObject))))
        {
          localMap.remove(this.columnKey);
          bool = true;
          if (localMap.isEmpty()) {
            localIterator.remove();
          }
        }
      }
      return bool;
    }
    
    public Set keySet()
    {
      KeySet localKeySet = this.keySet;
      return localKeySet == null ? (this.keySet = new KeySet()) : localKeySet;
    }
    
    class Values
      extends AbstractCollection
    {
      Values() {}
      
      public Iterator iterator()
      {
        return Maps.valueIterator(StandardTable.Column.this.entrySet().iterator());
      }
      
      public int size()
      {
        return StandardTable.Column.this.entrySet().size();
      }
      
      public boolean isEmpty()
      {
        return !StandardTable.this.containsColumn(StandardTable.Column.this.columnKey);
      }
      
      public void clear()
      {
        StandardTable.Column.this.entrySet().clear();
      }
      
      public boolean remove(Object paramObject)
      {
        if (paramObject == null) {
          return false;
        }
        Iterator localIterator = StandardTable.this.backingMap.values().iterator();
        while (localIterator.hasNext())
        {
          Map localMap = (Map)localIterator.next();
          if (localMap.entrySet().remove(new ImmutableEntry(StandardTable.Column.this.columnKey, paramObject)))
          {
            if (localMap.isEmpty()) {
              localIterator.remove();
            }
            return true;
          }
        }
        return false;
      }
      
      public boolean removeAll(final Collection paramCollection)
      {
        Preconditions.checkNotNull(paramCollection);
        Predicate local1 = new Predicate()
        {
          public boolean apply(Map.Entry paramAnonymousEntry)
          {
            return paramCollection.contains(paramAnonymousEntry.getValue());
          }
        };
        return StandardTable.Column.this.removePredicate(local1);
      }
      
      public boolean retainAll(final Collection paramCollection)
      {
        Preconditions.checkNotNull(paramCollection);
        Predicate local2 = new Predicate()
        {
          public boolean apply(Map.Entry paramAnonymousEntry)
          {
            return !paramCollection.contains(paramAnonymousEntry.getValue());
          }
        };
        return StandardTable.Column.this.removePredicate(local2);
      }
    }
    
    class KeySet
      extends Sets.ImprovedAbstractSet
    {
      KeySet() {}
      
      public Iterator iterator()
      {
        return Maps.keyIterator(StandardTable.Column.this.entrySet().iterator());
      }
      
      public int size()
      {
        return StandardTable.Column.this.entrySet().size();
      }
      
      public boolean isEmpty()
      {
        return !StandardTable.this.containsColumn(StandardTable.Column.this.columnKey);
      }
      
      public boolean contains(Object paramObject)
      {
        return StandardTable.this.contains(paramObject, StandardTable.Column.this.columnKey);
      }
      
      public boolean remove(Object paramObject)
      {
        return StandardTable.this.remove(paramObject, StandardTable.Column.this.columnKey) != null;
      }
      
      public void clear()
      {
        StandardTable.Column.this.entrySet().clear();
      }
      
      public boolean retainAll(final Collection paramCollection)
      {
        Preconditions.checkNotNull(paramCollection);
        Predicate local1 = new Predicate()
        {
          public boolean apply(Map.Entry paramAnonymousEntry)
          {
            return !paramCollection.contains(paramAnonymousEntry.getKey());
          }
        };
        return StandardTable.Column.this.removePredicate(local1);
      }
    }
    
    class EntrySetIterator
      extends AbstractIterator
    {
      final Iterator iterator = StandardTable.this.backingMap.entrySet().iterator();
      
      EntrySetIterator() {}
      
      protected Map.Entry computeNext()
      {
        while (this.iterator.hasNext())
        {
          final Map.Entry localEntry = (Map.Entry)this.iterator.next();
          if (((Map)localEntry.getValue()).containsKey(StandardTable.Column.this.columnKey)) {
            new AbstractMapEntry()
            {
              public Object getKey()
              {
                return localEntry.getKey();
              }
              
              public Object getValue()
              {
                return ((Map)localEntry.getValue()).get(StandardTable.Column.this.columnKey);
              }
              
              public Object setValue(Object paramAnonymousObject)
              {
                return ((Map)localEntry.getValue()).put(StandardTable.Column.this.columnKey, Preconditions.checkNotNull(paramAnonymousObject));
              }
            };
          }
        }
        return (Map.Entry)endOfData();
      }
    }
    
    class EntrySet
      extends Sets.ImprovedAbstractSet
    {
      EntrySet() {}
      
      public Iterator iterator()
      {
        return new StandardTable.Column.EntrySetIterator(StandardTable.Column.this);
      }
      
      public int size()
      {
        int i = 0;
        Iterator localIterator = StandardTable.this.backingMap.values().iterator();
        while (localIterator.hasNext())
        {
          Map localMap = (Map)localIterator.next();
          if (localMap.containsKey(StandardTable.Column.this.columnKey)) {
            i++;
          }
        }
        return i;
      }
      
      public boolean isEmpty()
      {
        return !StandardTable.this.containsColumn(StandardTable.Column.this.columnKey);
      }
      
      public void clear()
      {
        Predicate localPredicate = Predicates.alwaysTrue();
        StandardTable.Column.this.removePredicate(localPredicate);
      }
      
      public boolean contains(Object paramObject)
      {
        if ((paramObject instanceof Map.Entry))
        {
          Map.Entry localEntry = (Map.Entry)paramObject;
          return StandardTable.this.containsMapping(localEntry.getKey(), StandardTable.Column.this.columnKey, localEntry.getValue());
        }
        return false;
      }
      
      public boolean remove(Object paramObject)
      {
        if ((paramObject instanceof Map.Entry))
        {
          Map.Entry localEntry = (Map.Entry)paramObject;
          return StandardTable.this.removeMapping(localEntry.getKey(), StandardTable.Column.this.columnKey, localEntry.getValue());
        }
        return false;
      }
      
      public boolean retainAll(Collection paramCollection)
      {
        return StandardTable.Column.this.removePredicate(Predicates.not(Predicates.in(paramCollection)));
      }
    }
  }
  
  class Row
    extends AbstractMap
  {
    final Object rowKey;
    Map backingRowMap;
    Set keySet;
    Set entrySet;
    
    Row(Object paramObject)
    {
      this.rowKey = Preconditions.checkNotNull(paramObject);
    }
    
    Map backingRowMap()
    {
      return (this.backingRowMap == null) || ((this.backingRowMap.isEmpty()) && (StandardTable.this.backingMap.containsKey(this.rowKey))) ? (this.backingRowMap = computeBackingRowMap()) : this.backingRowMap;
    }
    
    Map computeBackingRowMap()
    {
      return (Map)StandardTable.this.backingMap.get(this.rowKey);
    }
    
    void maintainEmptyInvariant()
    {
      if ((backingRowMap() != null) && (this.backingRowMap.isEmpty()))
      {
        StandardTable.this.backingMap.remove(this.rowKey);
        this.backingRowMap = null;
      }
    }
    
    public boolean containsKey(Object paramObject)
    {
      Map localMap = backingRowMap();
      return (paramObject != null) && (localMap != null) && (Maps.safeContainsKey(localMap, paramObject));
    }
    
    public Object get(Object paramObject)
    {
      Map localMap = backingRowMap();
      return (paramObject != null) && (localMap != null) ? Maps.safeGet(localMap, paramObject) : null;
    }
    
    public Object put(Object paramObject1, Object paramObject2)
    {
      Preconditions.checkNotNull(paramObject1);
      Preconditions.checkNotNull(paramObject2);
      if ((this.backingRowMap != null) && (!this.backingRowMap.isEmpty())) {
        return this.backingRowMap.put(paramObject1, paramObject2);
      }
      return StandardTable.this.put(this.rowKey, paramObject1, paramObject2);
    }
    
    public Object remove(Object paramObject)
    {
      Map localMap = backingRowMap();
      if (localMap == null) {
        return null;
      }
      Object localObject = Maps.safeRemove(localMap, paramObject);
      maintainEmptyInvariant();
      return localObject;
    }
    
    public void clear()
    {
      Map localMap = backingRowMap();
      if (localMap != null) {
        localMap.clear();
      }
      maintainEmptyInvariant();
    }
    
    public Set keySet()
    {
      Set localSet = this.keySet;
      if (localSet == null) {
        this. = new Maps.KeySet()
        {
          Map map()
          {
            return StandardTable.Row.this;
          }
        };
      }
      return localSet;
    }
    
    public Set entrySet()
    {
      Set localSet = this.entrySet;
      if (localSet == null) {
        return this.entrySet = new RowEntrySet(null);
      }
      return localSet;
    }
    
    private class RowEntrySet
      extends Maps.EntrySet
    {
      private RowEntrySet() {}
      
      Map map()
      {
        return StandardTable.Row.this;
      }
      
      public int size()
      {
        Map localMap = StandardTable.Row.this.backingRowMap();
        return localMap == null ? 0 : localMap.size();
      }
      
      public Iterator iterator()
      {
        Map localMap = StandardTable.Row.this.backingRowMap();
        if (localMap == null) {
          return Iterators.emptyModifiableIterator();
        }
        final Iterator localIterator = localMap.entrySet().iterator();
        new Iterator()
        {
          public boolean hasNext()
          {
            return localIterator.hasNext();
          }
          
          public Map.Entry next()
          {
            final Map.Entry localEntry = (Map.Entry)localIterator.next();
            new ForwardingMapEntry()
            {
              protected Map.Entry delegate()
              {
                return localEntry;
              }
              
              public Object setValue(Object paramAnonymous2Object)
              {
                return super.setValue(Preconditions.checkNotNull(paramAnonymous2Object));
              }
              
              public boolean equals(Object paramAnonymous2Object)
              {
                return standardEquals(paramAnonymous2Object);
              }
            };
          }
          
          public void remove()
          {
            localIterator.remove();
            StandardTable.Row.this.maintainEmptyInvariant();
          }
        };
      }
    }
  }
  
  private class CellIterator
    implements Iterator
  {
    final Iterator rowIterator = StandardTable.this.backingMap.entrySet().iterator();
    Map.Entry rowEntry;
    Iterator columnIterator = Iterators.emptyModifiableIterator();
    
    private CellIterator() {}
    
    public boolean hasNext()
    {
      return (this.rowIterator.hasNext()) || (this.columnIterator.hasNext());
    }
    
    public Table.Cell next()
    {
      if (!this.columnIterator.hasNext())
      {
        this.rowEntry = ((Map.Entry)this.rowIterator.next());
        this.columnIterator = ((Map)this.rowEntry.getValue()).entrySet().iterator();
      }
      Map.Entry localEntry = (Map.Entry)this.columnIterator.next();
      return Tables.immutableCell(this.rowEntry.getKey(), localEntry.getKey(), localEntry.getValue());
    }
    
    public void remove()
    {
      this.columnIterator.remove();
      if (((Map)this.rowEntry.getValue()).isEmpty()) {
        this.rowIterator.remove();
      }
    }
  }
  
  private class CellSet
    extends StandardTable.TableSet
  {
    private CellSet()
    {
      super(null);
    }
    
    public Iterator iterator()
    {
      return new StandardTable.CellIterator(StandardTable.this, null);
    }
    
    public int size()
    {
      return StandardTable.this.size();
    }
    
    public boolean contains(Object paramObject)
    {
      if ((paramObject instanceof Table.Cell))
      {
        Table.Cell localCell = (Table.Cell)paramObject;
        return StandardTable.this.containsMapping(localCell.getRowKey(), localCell.getColumnKey(), localCell.getValue());
      }
      return false;
    }
    
    public boolean remove(Object paramObject)
    {
      if ((paramObject instanceof Table.Cell))
      {
        Table.Cell localCell = (Table.Cell)paramObject;
        return StandardTable.this.removeMapping(localCell.getRowKey(), localCell.getColumnKey(), localCell.getValue());
      }
      return false;
    }
  }
  
  private abstract class TableSet
    extends AbstractSet
  {
    private TableSet() {}
    
    public boolean isEmpty()
    {
      return StandardTable.this.backingMap.isEmpty();
    }
    
    public void clear()
    {
      StandardTable.this.backingMap.clear();
    }
  }
  
  private abstract class TableCollection
    extends AbstractCollection
  {
    private TableCollection() {}
    
    public boolean isEmpty()
    {
      return StandardTable.this.backingMap.isEmpty();
    }
    
    public void clear()
    {
      StandardTable.this.backingMap.clear();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\StandardTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */