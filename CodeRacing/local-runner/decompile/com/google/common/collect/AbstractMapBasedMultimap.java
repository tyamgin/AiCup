package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible(emulated=true)
abstract class AbstractMapBasedMultimap
  extends AbstractMultimap
  implements Serializable
{
  private transient Map map;
  private transient int totalSize;
  private static final long serialVersionUID = 2447537837011683357L;
  
  protected AbstractMapBasedMultimap(Map paramMap)
  {
    Preconditions.checkArgument(paramMap.isEmpty());
    this.map = paramMap;
  }
  
  final void setMap(Map paramMap)
  {
    this.map = paramMap;
    this.totalSize = 0;
    Iterator localIterator = paramMap.values().iterator();
    while (localIterator.hasNext())
    {
      Collection localCollection = (Collection)localIterator.next();
      Preconditions.checkArgument(!localCollection.isEmpty());
      this.totalSize += localCollection.size();
    }
  }
  
  Collection createUnmodifiableEmptyCollection()
  {
    return unmodifiableCollectionSubclass(createCollection());
  }
  
  abstract Collection createCollection();
  
  Collection createCollection(Object paramObject)
  {
    return createCollection();
  }
  
  Map backingMap()
  {
    return this.map;
  }
  
  public int size()
  {
    return this.totalSize;
  }
  
  public boolean containsKey(Object paramObject)
  {
    return this.map.containsKey(paramObject);
  }
  
  public boolean put(Object paramObject1, Object paramObject2)
  {
    Collection localCollection = (Collection)this.map.get(paramObject1);
    if (localCollection == null)
    {
      localCollection = createCollection(paramObject1);
      if (localCollection.add(paramObject2))
      {
        this.totalSize += 1;
        this.map.put(paramObject1, localCollection);
        return true;
      }
      throw new AssertionError("New Collection violated the Collection spec");
    }
    if (localCollection.add(paramObject2))
    {
      this.totalSize += 1;
      return true;
    }
    return false;
  }
  
  private Collection getOrCreateCollection(Object paramObject)
  {
    Collection localCollection = (Collection)this.map.get(paramObject);
    if (localCollection == null)
    {
      localCollection = createCollection(paramObject);
      this.map.put(paramObject, localCollection);
    }
    return localCollection;
  }
  
  public Collection replaceValues(Object paramObject, Iterable paramIterable)
  {
    Iterator localIterator = paramIterable.iterator();
    if (!localIterator.hasNext()) {
      return removeAll(paramObject);
    }
    Collection localCollection1 = getOrCreateCollection(paramObject);
    Collection localCollection2 = createCollection();
    localCollection2.addAll(localCollection1);
    this.totalSize -= localCollection1.size();
    localCollection1.clear();
    while (localIterator.hasNext()) {
      if (localCollection1.add(localIterator.next())) {
        this.totalSize += 1;
      }
    }
    return unmodifiableCollectionSubclass(localCollection2);
  }
  
  public Collection removeAll(Object paramObject)
  {
    Collection localCollection1 = (Collection)this.map.remove(paramObject);
    if (localCollection1 == null) {
      return createUnmodifiableEmptyCollection();
    }
    Collection localCollection2 = createCollection();
    localCollection2.addAll(localCollection1);
    this.totalSize -= localCollection1.size();
    localCollection1.clear();
    return unmodifiableCollectionSubclass(localCollection2);
  }
  
  Collection unmodifiableCollectionSubclass(Collection paramCollection)
  {
    if ((paramCollection instanceof SortedSet)) {
      return Collections.unmodifiableSortedSet((SortedSet)paramCollection);
    }
    if ((paramCollection instanceof Set)) {
      return Collections.unmodifiableSet((Set)paramCollection);
    }
    if ((paramCollection instanceof List)) {
      return Collections.unmodifiableList((List)paramCollection);
    }
    return Collections.unmodifiableCollection(paramCollection);
  }
  
  public void clear()
  {
    Iterator localIterator = this.map.values().iterator();
    while (localIterator.hasNext())
    {
      Collection localCollection = (Collection)localIterator.next();
      localCollection.clear();
    }
    this.map.clear();
    this.totalSize = 0;
  }
  
  public Collection get(Object paramObject)
  {
    Collection localCollection = (Collection)this.map.get(paramObject);
    if (localCollection == null) {
      localCollection = createCollection(paramObject);
    }
    return wrapCollection(paramObject, localCollection);
  }
  
  Collection wrapCollection(Object paramObject, Collection paramCollection)
  {
    if ((paramCollection instanceof SortedSet)) {
      return new WrappedSortedSet(paramObject, (SortedSet)paramCollection, null);
    }
    if ((paramCollection instanceof Set)) {
      return new WrappedSet(paramObject, (Set)paramCollection);
    }
    if ((paramCollection instanceof List)) {
      return wrapList(paramObject, (List)paramCollection, null);
    }
    return new WrappedCollection(paramObject, paramCollection, null);
  }
  
  private List wrapList(Object paramObject, List paramList, WrappedCollection paramWrappedCollection)
  {
    return (paramList instanceof RandomAccess) ? new RandomAccessWrappedList(paramObject, paramList, paramWrappedCollection) : new WrappedList(paramObject, paramList, paramWrappedCollection);
  }
  
  private Iterator iteratorOrListIterator(Collection paramCollection)
  {
    return (paramCollection instanceof List) ? ((List)paramCollection).listIterator() : paramCollection.iterator();
  }
  
  Set createKeySet()
  {
    return (this.map instanceof SortedMap) ? new SortedKeySet((SortedMap)this.map) : new KeySet(this.map);
  }
  
  private int removeValuesForKey(Object paramObject)
  {
    Collection localCollection = (Collection)Maps.safeRemove(this.map, paramObject);
    int i = 0;
    if (localCollection != null)
    {
      i = localCollection.size();
      localCollection.clear();
      this.totalSize -= i;
    }
    return i;
  }
  
  public Collection values()
  {
    return super.values();
  }
  
  public Collection entries()
  {
    return super.entries();
  }
  
  Iterator entryIterator()
  {
    return new EntryIterator();
  }
  
  Map createAsMap()
  {
    return (this.map instanceof SortedMap) ? new SortedAsMap((SortedMap)this.map) : new AsMap(this.map);
  }
  
  @GwtIncompatible("NavigableAsMap")
  class NavigableAsMap
    extends AbstractMapBasedMultimap.SortedAsMap
    implements NavigableMap
  {
    NavigableAsMap(NavigableMap paramNavigableMap)
    {
      super(paramNavigableMap);
    }
    
    NavigableMap sortedMap()
    {
      return (NavigableMap)super.sortedMap();
    }
    
    public Map.Entry lowerEntry(Object paramObject)
    {
      Map.Entry localEntry = sortedMap().lowerEntry(paramObject);
      return localEntry == null ? null : wrapEntry(localEntry);
    }
    
    public Object lowerKey(Object paramObject)
    {
      return sortedMap().lowerKey(paramObject);
    }
    
    public Map.Entry floorEntry(Object paramObject)
    {
      Map.Entry localEntry = sortedMap().floorEntry(paramObject);
      return localEntry == null ? null : wrapEntry(localEntry);
    }
    
    public Object floorKey(Object paramObject)
    {
      return sortedMap().floorKey(paramObject);
    }
    
    public Map.Entry ceilingEntry(Object paramObject)
    {
      Map.Entry localEntry = sortedMap().ceilingEntry(paramObject);
      return localEntry == null ? null : wrapEntry(localEntry);
    }
    
    public Object ceilingKey(Object paramObject)
    {
      return sortedMap().ceilingKey(paramObject);
    }
    
    public Map.Entry higherEntry(Object paramObject)
    {
      Map.Entry localEntry = sortedMap().higherEntry(paramObject);
      return localEntry == null ? null : wrapEntry(localEntry);
    }
    
    public Object higherKey(Object paramObject)
    {
      return sortedMap().higherKey(paramObject);
    }
    
    public Map.Entry firstEntry()
    {
      Map.Entry localEntry = sortedMap().firstEntry();
      return localEntry == null ? null : wrapEntry(localEntry);
    }
    
    public Map.Entry lastEntry()
    {
      Map.Entry localEntry = sortedMap().lastEntry();
      return localEntry == null ? null : wrapEntry(localEntry);
    }
    
    public Map.Entry pollFirstEntry()
    {
      return pollAsMapEntry(entrySet().iterator());
    }
    
    public Map.Entry pollLastEntry()
    {
      return pollAsMapEntry(descendingMap().entrySet().iterator());
    }
    
    Map.Entry pollAsMapEntry(Iterator paramIterator)
    {
      if (!paramIterator.hasNext()) {
        return null;
      }
      Map.Entry localEntry = (Map.Entry)paramIterator.next();
      Collection localCollection = AbstractMapBasedMultimap.this.createCollection();
      localCollection.addAll((Collection)localEntry.getValue());
      paramIterator.remove();
      return Maps.immutableEntry(localEntry.getKey(), AbstractMapBasedMultimap.this.unmodifiableCollectionSubclass(localCollection));
    }
    
    public NavigableMap descendingMap()
    {
      return new NavigableAsMap(AbstractMapBasedMultimap.this, sortedMap().descendingMap());
    }
    
    public NavigableSet keySet()
    {
      return (NavigableSet)super.keySet();
    }
    
    NavigableSet createKeySet()
    {
      return new AbstractMapBasedMultimap.NavigableKeySet(AbstractMapBasedMultimap.this, sortedMap());
    }
    
    public NavigableSet navigableKeySet()
    {
      return keySet();
    }
    
    public NavigableSet descendingKeySet()
    {
      return descendingMap().navigableKeySet();
    }
    
    public NavigableMap subMap(Object paramObject1, Object paramObject2)
    {
      return subMap(paramObject1, true, paramObject2, false);
    }
    
    public NavigableMap subMap(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
    {
      return new NavigableAsMap(AbstractMapBasedMultimap.this, sortedMap().subMap(paramObject1, paramBoolean1, paramObject2, paramBoolean2));
    }
    
    public NavigableMap headMap(Object paramObject)
    {
      return headMap(paramObject, false);
    }
    
    public NavigableMap headMap(Object paramObject, boolean paramBoolean)
    {
      return new NavigableAsMap(AbstractMapBasedMultimap.this, sortedMap().headMap(paramObject, false));
    }
    
    public NavigableMap tailMap(Object paramObject)
    {
      return tailMap(paramObject, true);
    }
    
    public NavigableMap tailMap(Object paramObject, boolean paramBoolean)
    {
      return new NavigableAsMap(AbstractMapBasedMultimap.this, sortedMap().tailMap(paramObject, paramBoolean));
    }
  }
  
  private class SortedAsMap
    extends AbstractMapBasedMultimap.AsMap
    implements SortedMap
  {
    SortedSet sortedKeySet;
    
    SortedAsMap(SortedMap paramSortedMap)
    {
      super(paramSortedMap);
    }
    
    SortedMap sortedMap()
    {
      return (SortedMap)this.submap;
    }
    
    public Comparator comparator()
    {
      return sortedMap().comparator();
    }
    
    public Object firstKey()
    {
      return sortedMap().firstKey();
    }
    
    public Object lastKey()
    {
      return sortedMap().lastKey();
    }
    
    public SortedMap headMap(Object paramObject)
    {
      return new SortedAsMap(AbstractMapBasedMultimap.this, sortedMap().headMap(paramObject));
    }
    
    public SortedMap subMap(Object paramObject1, Object paramObject2)
    {
      return new SortedAsMap(AbstractMapBasedMultimap.this, sortedMap().subMap(paramObject1, paramObject2));
    }
    
    public SortedMap tailMap(Object paramObject)
    {
      return new SortedAsMap(AbstractMapBasedMultimap.this, sortedMap().tailMap(paramObject));
    }
    
    public SortedSet keySet()
    {
      SortedSet localSortedSet = this.sortedKeySet;
      return localSortedSet == null ? (this.sortedKeySet = createKeySet()) : localSortedSet;
    }
    
    SortedSet createKeySet()
    {
      return new AbstractMapBasedMultimap.SortedKeySet(AbstractMapBasedMultimap.this, sortedMap());
    }
  }
  
  private class AsMap
    extends AbstractMap
  {
    final transient Map submap;
    transient Set entrySet;
    
    AsMap(Map paramMap)
    {
      this.submap = paramMap;
    }
    
    public Set entrySet()
    {
      Set localSet = this.entrySet;
      return localSet == null ? (this.entrySet = new AsMapEntries()) : localSet;
    }
    
    public boolean containsKey(Object paramObject)
    {
      return Maps.safeContainsKey(this.submap, paramObject);
    }
    
    public Collection get(Object paramObject)
    {
      Collection localCollection = (Collection)Maps.safeGet(this.submap, paramObject);
      if (localCollection == null) {
        return null;
      }
      Object localObject = paramObject;
      return AbstractMapBasedMultimap.this.wrapCollection(localObject, localCollection);
    }
    
    public Set keySet()
    {
      return AbstractMapBasedMultimap.this.keySet();
    }
    
    public int size()
    {
      return this.submap.size();
    }
    
    public Collection remove(Object paramObject)
    {
      Collection localCollection1 = (Collection)this.submap.remove(paramObject);
      if (localCollection1 == null) {
        return null;
      }
      Collection localCollection2 = AbstractMapBasedMultimap.this.createCollection();
      localCollection2.addAll(localCollection1);
      AbstractMapBasedMultimap.access$220(AbstractMapBasedMultimap.this, localCollection1.size());
      localCollection1.clear();
      return localCollection2;
    }
    
    public boolean equals(Object paramObject)
    {
      return (this == paramObject) || (this.submap.equals(paramObject));
    }
    
    public int hashCode()
    {
      return this.submap.hashCode();
    }
    
    public String toString()
    {
      return this.submap.toString();
    }
    
    public void clear()
    {
      if (this.submap == AbstractMapBasedMultimap.this.map) {
        AbstractMapBasedMultimap.this.clear();
      } else {
        Iterators.clear(new AsMapIterator());
      }
    }
    
    Map.Entry wrapEntry(Map.Entry paramEntry)
    {
      Object localObject = paramEntry.getKey();
      return Maps.immutableEntry(localObject, AbstractMapBasedMultimap.this.wrapCollection(localObject, (Collection)paramEntry.getValue()));
    }
    
    class AsMapIterator
      implements Iterator
    {
      final Iterator delegateIterator = AbstractMapBasedMultimap.AsMap.this.submap.entrySet().iterator();
      Collection collection;
      
      AsMapIterator() {}
      
      public boolean hasNext()
      {
        return this.delegateIterator.hasNext();
      }
      
      public Map.Entry next()
      {
        Map.Entry localEntry = (Map.Entry)this.delegateIterator.next();
        this.collection = ((Collection)localEntry.getValue());
        return AbstractMapBasedMultimap.AsMap.this.wrapEntry(localEntry);
      }
      
      public void remove()
      {
        this.delegateIterator.remove();
        AbstractMapBasedMultimap.access$220(AbstractMapBasedMultimap.this, this.collection.size());
        this.collection.clear();
      }
    }
    
    class AsMapEntries
      extends Maps.EntrySet
    {
      AsMapEntries() {}
      
      Map map()
      {
        return AbstractMapBasedMultimap.AsMap.this;
      }
      
      public Iterator iterator()
      {
        return new AbstractMapBasedMultimap.AsMap.AsMapIterator(AbstractMapBasedMultimap.AsMap.this);
      }
      
      public boolean contains(Object paramObject)
      {
        return Collections2.safeContains(AbstractMapBasedMultimap.AsMap.this.submap.entrySet(), paramObject);
      }
      
      public boolean remove(Object paramObject)
      {
        if (!contains(paramObject)) {
          return false;
        }
        Map.Entry localEntry = (Map.Entry)paramObject;
        AbstractMapBasedMultimap.this.removeValuesForKey(localEntry.getKey());
        return true;
      }
    }
  }
  
  private class EntryIterator
    implements Iterator
  {
    final Iterator keyIterator = AbstractMapBasedMultimap.this.map.entrySet().iterator();
    Object key;
    Collection collection;
    Iterator valueIterator;
    
    EntryIterator()
    {
      if (this.keyIterator.hasNext()) {
        findValueIteratorAndKey();
      } else {
        this.valueIterator = Iterators.emptyModifiableIterator();
      }
    }
    
    void findValueIteratorAndKey()
    {
      Map.Entry localEntry = (Map.Entry)this.keyIterator.next();
      this.key = localEntry.getKey();
      this.collection = ((Collection)localEntry.getValue());
      this.valueIterator = this.collection.iterator();
    }
    
    public boolean hasNext()
    {
      return (this.keyIterator.hasNext()) || (this.valueIterator.hasNext());
    }
    
    public Map.Entry next()
    {
      if (!this.valueIterator.hasNext()) {
        findValueIteratorAndKey();
      }
      return Maps.immutableEntry(this.key, this.valueIterator.next());
    }
    
    public void remove()
    {
      this.valueIterator.remove();
      if (this.collection.isEmpty()) {
        this.keyIterator.remove();
      }
      AbstractMapBasedMultimap.access$210(AbstractMapBasedMultimap.this);
    }
  }
  
  @GwtIncompatible("NavigableSet")
  class NavigableKeySet
    extends AbstractMapBasedMultimap.SortedKeySet
    implements NavigableSet
  {
    NavigableKeySet(NavigableMap paramNavigableMap)
    {
      super(paramNavigableMap);
    }
    
    NavigableMap sortedMap()
    {
      return (NavigableMap)super.sortedMap();
    }
    
    public Object lower(Object paramObject)
    {
      return sortedMap().lowerKey(paramObject);
    }
    
    public Object floor(Object paramObject)
    {
      return sortedMap().floorKey(paramObject);
    }
    
    public Object ceiling(Object paramObject)
    {
      return sortedMap().ceilingKey(paramObject);
    }
    
    public Object higher(Object paramObject)
    {
      return sortedMap().higherKey(paramObject);
    }
    
    public Object pollFirst()
    {
      return Iterators.pollNext(iterator());
    }
    
    public Object pollLast()
    {
      return Iterators.pollNext(descendingIterator());
    }
    
    public NavigableSet descendingSet()
    {
      return new NavigableKeySet(AbstractMapBasedMultimap.this, sortedMap().descendingMap());
    }
    
    public Iterator descendingIterator()
    {
      return descendingSet().iterator();
    }
    
    public NavigableSet headSet(Object paramObject)
    {
      return headSet(paramObject, false);
    }
    
    public NavigableSet headSet(Object paramObject, boolean paramBoolean)
    {
      return new NavigableKeySet(AbstractMapBasedMultimap.this, sortedMap().headMap(paramObject, paramBoolean));
    }
    
    public NavigableSet subSet(Object paramObject1, Object paramObject2)
    {
      return subSet(paramObject1, true, paramObject2, false);
    }
    
    public NavigableSet subSet(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
    {
      return new NavigableKeySet(AbstractMapBasedMultimap.this, sortedMap().subMap(paramObject1, paramBoolean1, paramObject2, paramBoolean2));
    }
    
    public NavigableSet tailSet(Object paramObject)
    {
      return tailSet(paramObject, true);
    }
    
    public NavigableSet tailSet(Object paramObject, boolean paramBoolean)
    {
      return new NavigableKeySet(AbstractMapBasedMultimap.this, sortedMap().tailMap(paramObject, paramBoolean));
    }
  }
  
  private class SortedKeySet
    extends AbstractMapBasedMultimap.KeySet
    implements SortedSet
  {
    SortedKeySet(SortedMap paramSortedMap)
    {
      super(paramSortedMap);
    }
    
    SortedMap sortedMap()
    {
      return (SortedMap)this.subMap;
    }
    
    public Comparator comparator()
    {
      return sortedMap().comparator();
    }
    
    public Object first()
    {
      return sortedMap().firstKey();
    }
    
    public SortedSet headSet(Object paramObject)
    {
      return new SortedKeySet(AbstractMapBasedMultimap.this, sortedMap().headMap(paramObject));
    }
    
    public Object last()
    {
      return sortedMap().lastKey();
    }
    
    public SortedSet subSet(Object paramObject1, Object paramObject2)
    {
      return new SortedKeySet(AbstractMapBasedMultimap.this, sortedMap().subMap(paramObject1, paramObject2));
    }
    
    public SortedSet tailSet(Object paramObject)
    {
      return new SortedKeySet(AbstractMapBasedMultimap.this, sortedMap().tailMap(paramObject));
    }
  }
  
  private class KeySet
    extends Maps.KeySet
  {
    final Map subMap;
    
    KeySet(Map paramMap)
    {
      this.subMap = paramMap;
    }
    
    Map map()
    {
      return this.subMap;
    }
    
    public Iterator iterator()
    {
      final Iterator localIterator = this.subMap.entrySet().iterator();
      new Iterator()
      {
        Map.Entry entry;
        
        public boolean hasNext()
        {
          return localIterator.hasNext();
        }
        
        public Object next()
        {
          this.entry = ((Map.Entry)localIterator.next());
          return this.entry.getKey();
        }
        
        public void remove()
        {
          Iterators.checkRemove(this.entry != null);
          Collection localCollection = (Collection)this.entry.getValue();
          localIterator.remove();
          AbstractMapBasedMultimap.access$220(AbstractMapBasedMultimap.this, localCollection.size());
          localCollection.clear();
        }
      };
    }
    
    public boolean remove(Object paramObject)
    {
      int i = 0;
      Collection localCollection = (Collection)this.subMap.remove(paramObject);
      if (localCollection != null)
      {
        i = localCollection.size();
        localCollection.clear();
        AbstractMapBasedMultimap.access$220(AbstractMapBasedMultimap.this, i);
      }
      return i > 0;
    }
    
    public void clear()
    {
      Iterators.clear(iterator());
    }
    
    public boolean containsAll(Collection paramCollection)
    {
      return this.subMap.keySet().containsAll(paramCollection);
    }
    
    public boolean equals(Object paramObject)
    {
      return (this == paramObject) || (this.subMap.keySet().equals(paramObject));
    }
    
    public int hashCode()
    {
      return this.subMap.keySet().hashCode();
    }
  }
  
  private class RandomAccessWrappedList
    extends AbstractMapBasedMultimap.WrappedList
    implements RandomAccess
  {
    RandomAccessWrappedList(Object paramObject, List paramList, AbstractMapBasedMultimap.WrappedCollection paramWrappedCollection)
    {
      super(paramObject, paramList, paramWrappedCollection);
    }
  }
  
  private class WrappedList
    extends AbstractMapBasedMultimap.WrappedCollection
    implements List
  {
    WrappedList(Object paramObject, List paramList, AbstractMapBasedMultimap.WrappedCollection paramWrappedCollection)
    {
      super(paramObject, paramList, paramWrappedCollection);
    }
    
    List getListDelegate()
    {
      return (List)getDelegate();
    }
    
    public boolean addAll(int paramInt, Collection paramCollection)
    {
      if (paramCollection.isEmpty()) {
        return false;
      }
      int i = size();
      boolean bool = getListDelegate().addAll(paramInt, paramCollection);
      if (bool)
      {
        int j = getDelegate().size();
        AbstractMapBasedMultimap.access$212(AbstractMapBasedMultimap.this, j - i);
        if (i == 0) {
          addToMap();
        }
      }
      return bool;
    }
    
    public Object get(int paramInt)
    {
      refreshIfEmpty();
      return getListDelegate().get(paramInt);
    }
    
    public Object set(int paramInt, Object paramObject)
    {
      refreshIfEmpty();
      return getListDelegate().set(paramInt, paramObject);
    }
    
    public void add(int paramInt, Object paramObject)
    {
      refreshIfEmpty();
      boolean bool = getDelegate().isEmpty();
      getListDelegate().add(paramInt, paramObject);
      AbstractMapBasedMultimap.access$208(AbstractMapBasedMultimap.this);
      if (bool) {
        addToMap();
      }
    }
    
    public Object remove(int paramInt)
    {
      refreshIfEmpty();
      Object localObject = getListDelegate().remove(paramInt);
      AbstractMapBasedMultimap.access$210(AbstractMapBasedMultimap.this);
      removeIfEmpty();
      return localObject;
    }
    
    public int indexOf(Object paramObject)
    {
      refreshIfEmpty();
      return getListDelegate().indexOf(paramObject);
    }
    
    public int lastIndexOf(Object paramObject)
    {
      refreshIfEmpty();
      return getListDelegate().lastIndexOf(paramObject);
    }
    
    public ListIterator listIterator()
    {
      refreshIfEmpty();
      return new WrappedListIterator();
    }
    
    public ListIterator listIterator(int paramInt)
    {
      refreshIfEmpty();
      return new WrappedListIterator(paramInt);
    }
    
    public List subList(int paramInt1, int paramInt2)
    {
      refreshIfEmpty();
      return AbstractMapBasedMultimap.this.wrapList(getKey(), getListDelegate().subList(paramInt1, paramInt2), getAncestor() == null ? this : getAncestor());
    }
    
    private class WrappedListIterator
      extends AbstractMapBasedMultimap.WrappedCollection.WrappedIterator
      implements ListIterator
    {
      WrappedListIterator()
      {
        super();
      }
      
      public WrappedListIterator(int paramInt)
      {
        super(AbstractMapBasedMultimap.WrappedList.this.getListDelegate().listIterator(paramInt));
      }
      
      private ListIterator getDelegateListIterator()
      {
        return (ListIterator)getDelegateIterator();
      }
      
      public boolean hasPrevious()
      {
        return getDelegateListIterator().hasPrevious();
      }
      
      public Object previous()
      {
        return getDelegateListIterator().previous();
      }
      
      public int nextIndex()
      {
        return getDelegateListIterator().nextIndex();
      }
      
      public int previousIndex()
      {
        return getDelegateListIterator().previousIndex();
      }
      
      public void set(Object paramObject)
      {
        getDelegateListIterator().set(paramObject);
      }
      
      public void add(Object paramObject)
      {
        boolean bool = AbstractMapBasedMultimap.WrappedList.this.isEmpty();
        getDelegateListIterator().add(paramObject);
        AbstractMapBasedMultimap.access$208(AbstractMapBasedMultimap.this);
        if (bool) {
          AbstractMapBasedMultimap.WrappedList.this.addToMap();
        }
      }
    }
  }
  
  @GwtIncompatible("NavigableSet")
  class WrappedNavigableSet
    extends AbstractMapBasedMultimap.WrappedSortedSet
    implements NavigableSet
  {
    WrappedNavigableSet(Object paramObject, NavigableSet paramNavigableSet, AbstractMapBasedMultimap.WrappedCollection paramWrappedCollection)
    {
      super(paramObject, paramNavigableSet, paramWrappedCollection);
    }
    
    NavigableSet getSortedSetDelegate()
    {
      return (NavigableSet)super.getSortedSetDelegate();
    }
    
    public Object lower(Object paramObject)
    {
      return getSortedSetDelegate().lower(paramObject);
    }
    
    public Object floor(Object paramObject)
    {
      return getSortedSetDelegate().floor(paramObject);
    }
    
    public Object ceiling(Object paramObject)
    {
      return getSortedSetDelegate().ceiling(paramObject);
    }
    
    public Object higher(Object paramObject)
    {
      return getSortedSetDelegate().higher(paramObject);
    }
    
    public Object pollFirst()
    {
      return Iterators.pollNext(iterator());
    }
    
    public Object pollLast()
    {
      return Iterators.pollNext(descendingIterator());
    }
    
    private NavigableSet wrap(NavigableSet paramNavigableSet)
    {
      return new WrappedNavigableSet(AbstractMapBasedMultimap.this, this.key, paramNavigableSet, getAncestor() == null ? this : getAncestor());
    }
    
    public NavigableSet descendingSet()
    {
      return wrap(getSortedSetDelegate().descendingSet());
    }
    
    public Iterator descendingIterator()
    {
      return new AbstractMapBasedMultimap.WrappedCollection.WrappedIterator(this, getSortedSetDelegate().descendingIterator());
    }
    
    public NavigableSet subSet(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
    {
      return wrap(getSortedSetDelegate().subSet(paramObject1, paramBoolean1, paramObject2, paramBoolean2));
    }
    
    public NavigableSet headSet(Object paramObject, boolean paramBoolean)
    {
      return wrap(getSortedSetDelegate().headSet(paramObject, paramBoolean));
    }
    
    public NavigableSet tailSet(Object paramObject, boolean paramBoolean)
    {
      return wrap(getSortedSetDelegate().tailSet(paramObject, paramBoolean));
    }
  }
  
  private class WrappedSortedSet
    extends AbstractMapBasedMultimap.WrappedCollection
    implements SortedSet
  {
    WrappedSortedSet(Object paramObject, SortedSet paramSortedSet, AbstractMapBasedMultimap.WrappedCollection paramWrappedCollection)
    {
      super(paramObject, paramSortedSet, paramWrappedCollection);
    }
    
    SortedSet getSortedSetDelegate()
    {
      return (SortedSet)getDelegate();
    }
    
    public Comparator comparator()
    {
      return getSortedSetDelegate().comparator();
    }
    
    public Object first()
    {
      refreshIfEmpty();
      return getSortedSetDelegate().first();
    }
    
    public Object last()
    {
      refreshIfEmpty();
      return getSortedSetDelegate().last();
    }
    
    public SortedSet headSet(Object paramObject)
    {
      refreshIfEmpty();
      return new WrappedSortedSet(AbstractMapBasedMultimap.this, getKey(), getSortedSetDelegate().headSet(paramObject), getAncestor() == null ? this : getAncestor());
    }
    
    public SortedSet subSet(Object paramObject1, Object paramObject2)
    {
      refreshIfEmpty();
      return new WrappedSortedSet(AbstractMapBasedMultimap.this, getKey(), getSortedSetDelegate().subSet(paramObject1, paramObject2), getAncestor() == null ? this : getAncestor());
    }
    
    public SortedSet tailSet(Object paramObject)
    {
      refreshIfEmpty();
      return new WrappedSortedSet(AbstractMapBasedMultimap.this, getKey(), getSortedSetDelegate().tailSet(paramObject), getAncestor() == null ? this : getAncestor());
    }
  }
  
  private class WrappedSet
    extends AbstractMapBasedMultimap.WrappedCollection
    implements Set
  {
    WrappedSet(Object paramObject, Set paramSet)
    {
      super(paramObject, paramSet, null);
    }
    
    public boolean removeAll(Collection paramCollection)
    {
      if (paramCollection.isEmpty()) {
        return false;
      }
      int i = size();
      boolean bool = Sets.removeAllImpl((Set)this.delegate, paramCollection);
      if (bool)
      {
        int j = this.delegate.size();
        AbstractMapBasedMultimap.access$212(AbstractMapBasedMultimap.this, j - i);
        removeIfEmpty();
      }
      return bool;
    }
  }
  
  private class WrappedCollection
    extends AbstractCollection
  {
    final Object key;
    Collection delegate;
    final WrappedCollection ancestor;
    final Collection ancestorDelegate;
    
    WrappedCollection(Object paramObject, Collection paramCollection, WrappedCollection paramWrappedCollection)
    {
      this.key = paramObject;
      this.delegate = paramCollection;
      this.ancestor = paramWrappedCollection;
      this.ancestorDelegate = (paramWrappedCollection == null ? null : paramWrappedCollection.getDelegate());
    }
    
    void refreshIfEmpty()
    {
      if (this.ancestor != null)
      {
        this.ancestor.refreshIfEmpty();
        if (this.ancestor.getDelegate() != this.ancestorDelegate) {
          throw new ConcurrentModificationException();
        }
      }
      else if (this.delegate.isEmpty())
      {
        Collection localCollection = (Collection)AbstractMapBasedMultimap.this.map.get(this.key);
        if (localCollection != null) {
          this.delegate = localCollection;
        }
      }
    }
    
    void removeIfEmpty()
    {
      if (this.ancestor != null) {
        this.ancestor.removeIfEmpty();
      } else if (this.delegate.isEmpty()) {
        AbstractMapBasedMultimap.this.map.remove(this.key);
      }
    }
    
    Object getKey()
    {
      return this.key;
    }
    
    void addToMap()
    {
      if (this.ancestor != null) {
        this.ancestor.addToMap();
      } else {
        AbstractMapBasedMultimap.this.map.put(this.key, this.delegate);
      }
    }
    
    public int size()
    {
      refreshIfEmpty();
      return this.delegate.size();
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      refreshIfEmpty();
      return this.delegate.equals(paramObject);
    }
    
    public int hashCode()
    {
      refreshIfEmpty();
      return this.delegate.hashCode();
    }
    
    public String toString()
    {
      refreshIfEmpty();
      return this.delegate.toString();
    }
    
    Collection getDelegate()
    {
      return this.delegate;
    }
    
    public Iterator iterator()
    {
      refreshIfEmpty();
      return new WrappedIterator();
    }
    
    public boolean add(Object paramObject)
    {
      refreshIfEmpty();
      boolean bool1 = this.delegate.isEmpty();
      boolean bool2 = this.delegate.add(paramObject);
      if (bool2)
      {
        AbstractMapBasedMultimap.access$208(AbstractMapBasedMultimap.this);
        if (bool1) {
          addToMap();
        }
      }
      return bool2;
    }
    
    WrappedCollection getAncestor()
    {
      return this.ancestor;
    }
    
    public boolean addAll(Collection paramCollection)
    {
      if (paramCollection.isEmpty()) {
        return false;
      }
      int i = size();
      boolean bool = this.delegate.addAll(paramCollection);
      if (bool)
      {
        int j = this.delegate.size();
        AbstractMapBasedMultimap.access$212(AbstractMapBasedMultimap.this, j - i);
        if (i == 0) {
          addToMap();
        }
      }
      return bool;
    }
    
    public boolean contains(Object paramObject)
    {
      refreshIfEmpty();
      return this.delegate.contains(paramObject);
    }
    
    public boolean containsAll(Collection paramCollection)
    {
      refreshIfEmpty();
      return this.delegate.containsAll(paramCollection);
    }
    
    public void clear()
    {
      int i = size();
      if (i == 0) {
        return;
      }
      this.delegate.clear();
      AbstractMapBasedMultimap.access$220(AbstractMapBasedMultimap.this, i);
      removeIfEmpty();
    }
    
    public boolean remove(Object paramObject)
    {
      refreshIfEmpty();
      boolean bool = this.delegate.remove(paramObject);
      if (bool)
      {
        AbstractMapBasedMultimap.access$210(AbstractMapBasedMultimap.this);
        removeIfEmpty();
      }
      return bool;
    }
    
    public boolean removeAll(Collection paramCollection)
    {
      if (paramCollection.isEmpty()) {
        return false;
      }
      int i = size();
      boolean bool = this.delegate.removeAll(paramCollection);
      if (bool)
      {
        int j = this.delegate.size();
        AbstractMapBasedMultimap.access$212(AbstractMapBasedMultimap.this, j - i);
        removeIfEmpty();
      }
      return bool;
    }
    
    public boolean retainAll(Collection paramCollection)
    {
      Preconditions.checkNotNull(paramCollection);
      int i = size();
      boolean bool = this.delegate.retainAll(paramCollection);
      if (bool)
      {
        int j = this.delegate.size();
        AbstractMapBasedMultimap.access$212(AbstractMapBasedMultimap.this, j - i);
        removeIfEmpty();
      }
      return bool;
    }
    
    class WrappedIterator
      implements Iterator
    {
      final Iterator delegateIterator;
      final Collection originalDelegate = AbstractMapBasedMultimap.WrappedCollection.this.delegate;
      
      WrappedIterator()
      {
        this.delegateIterator = AbstractMapBasedMultimap.this.iteratorOrListIterator(AbstractMapBasedMultimap.WrappedCollection.this.delegate);
      }
      
      WrappedIterator(Iterator paramIterator)
      {
        this.delegateIterator = paramIterator;
      }
      
      void validateIterator()
      {
        AbstractMapBasedMultimap.WrappedCollection.this.refreshIfEmpty();
        if (AbstractMapBasedMultimap.WrappedCollection.this.delegate != this.originalDelegate) {
          throw new ConcurrentModificationException();
        }
      }
      
      public boolean hasNext()
      {
        validateIterator();
        return this.delegateIterator.hasNext();
      }
      
      public Object next()
      {
        validateIterator();
        return this.delegateIterator.next();
      }
      
      public void remove()
      {
        this.delegateIterator.remove();
        AbstractMapBasedMultimap.access$210(AbstractMapBasedMultimap.this);
        AbstractMapBasedMultimap.WrappedCollection.this.removeIfEmpty();
      }
      
      Iterator getDelegateIterator()
      {
        validateIterator();
        return this.delegateIterator;
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\AbstractMapBasedMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */