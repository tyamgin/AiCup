package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;

@GwtCompatible(emulated=true)
public final class Multimaps
{
  public static Multimap newMultimap(Map paramMap, Supplier paramSupplier)
  {
    return new CustomMultimap(paramMap, paramSupplier);
  }
  
  public static ListMultimap newListMultimap(Map paramMap, Supplier paramSupplier)
  {
    return new CustomListMultimap(paramMap, paramSupplier);
  }
  
  public static SetMultimap newSetMultimap(Map paramMap, Supplier paramSupplier)
  {
    return new CustomSetMultimap(paramMap, paramSupplier);
  }
  
  public static SortedSetMultimap newSortedSetMultimap(Map paramMap, Supplier paramSupplier)
  {
    return new CustomSortedSetMultimap(paramMap, paramSupplier);
  }
  
  public static Multimap invertFrom(Multimap paramMultimap1, Multimap paramMultimap2)
  {
    Preconditions.checkNotNull(paramMultimap2);
    Iterator localIterator = paramMultimap1.entries().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      paramMultimap2.put(localEntry.getValue(), localEntry.getKey());
    }
    return paramMultimap2;
  }
  
  public static Multimap synchronizedMultimap(Multimap paramMultimap)
  {
    return Synchronized.multimap(paramMultimap, null);
  }
  
  public static Multimap unmodifiableMultimap(Multimap paramMultimap)
  {
    if (((paramMultimap instanceof UnmodifiableMultimap)) || ((paramMultimap instanceof ImmutableMultimap))) {
      return paramMultimap;
    }
    return new UnmodifiableMultimap(paramMultimap);
  }
  
  @Deprecated
  public static Multimap unmodifiableMultimap(ImmutableMultimap paramImmutableMultimap)
  {
    return (Multimap)Preconditions.checkNotNull(paramImmutableMultimap);
  }
  
  public static SetMultimap synchronizedSetMultimap(SetMultimap paramSetMultimap)
  {
    return Synchronized.setMultimap(paramSetMultimap, null);
  }
  
  public static SetMultimap unmodifiableSetMultimap(SetMultimap paramSetMultimap)
  {
    if (((paramSetMultimap instanceof UnmodifiableSetMultimap)) || ((paramSetMultimap instanceof ImmutableSetMultimap))) {
      return paramSetMultimap;
    }
    return new UnmodifiableSetMultimap(paramSetMultimap);
  }
  
  @Deprecated
  public static SetMultimap unmodifiableSetMultimap(ImmutableSetMultimap paramImmutableSetMultimap)
  {
    return (SetMultimap)Preconditions.checkNotNull(paramImmutableSetMultimap);
  }
  
  public static SortedSetMultimap synchronizedSortedSetMultimap(SortedSetMultimap paramSortedSetMultimap)
  {
    return Synchronized.sortedSetMultimap(paramSortedSetMultimap, null);
  }
  
  public static SortedSetMultimap unmodifiableSortedSetMultimap(SortedSetMultimap paramSortedSetMultimap)
  {
    if ((paramSortedSetMultimap instanceof UnmodifiableSortedSetMultimap)) {
      return paramSortedSetMultimap;
    }
    return new UnmodifiableSortedSetMultimap(paramSortedSetMultimap);
  }
  
  public static ListMultimap synchronizedListMultimap(ListMultimap paramListMultimap)
  {
    return Synchronized.listMultimap(paramListMultimap, null);
  }
  
  public static ListMultimap unmodifiableListMultimap(ListMultimap paramListMultimap)
  {
    if (((paramListMultimap instanceof UnmodifiableListMultimap)) || ((paramListMultimap instanceof ImmutableListMultimap))) {
      return paramListMultimap;
    }
    return new UnmodifiableListMultimap(paramListMultimap);
  }
  
  @Deprecated
  public static ListMultimap unmodifiableListMultimap(ImmutableListMultimap paramImmutableListMultimap)
  {
    return (ListMultimap)Preconditions.checkNotNull(paramImmutableListMultimap);
  }
  
  private static Collection unmodifiableValueCollection(Collection paramCollection)
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
  
  private static Map.Entry unmodifiableAsMapEntry(Map.Entry paramEntry)
  {
    Preconditions.checkNotNull(paramEntry);
    new AbstractMapEntry()
    {
      public Object getKey()
      {
        return this.val$entry.getKey();
      }
      
      public Collection getValue()
      {
        return Multimaps.unmodifiableValueCollection((Collection)this.val$entry.getValue());
      }
    };
  }
  
  private static Collection unmodifiableEntries(Collection paramCollection)
  {
    if ((paramCollection instanceof Set)) {
      return Maps.unmodifiableEntrySet((Set)paramCollection);
    }
    return new Maps.UnmodifiableEntries(Collections.unmodifiableCollection(paramCollection));
  }
  
  private static Set unmodifiableAsMapEntries(Set paramSet)
  {
    return new UnmodifiableAsMapEntries(Collections.unmodifiableSet(paramSet));
  }
  
  public static SetMultimap forMap(Map paramMap)
  {
    return new MapMultimap(paramMap);
  }
  
  public static Multimap transformValues(Multimap paramMultimap, Function paramFunction)
  {
    Preconditions.checkNotNull(paramFunction);
    Maps.EntryTransformer local2 = new Maps.EntryTransformer()
    {
      public Object transformEntry(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        return this.val$function.apply(paramAnonymousObject2);
      }
    };
    return transformEntries(paramMultimap, local2);
  }
  
  public static Multimap transformEntries(Multimap paramMultimap, Maps.EntryTransformer paramEntryTransformer)
  {
    return new TransformedEntriesMultimap(paramMultimap, paramEntryTransformer);
  }
  
  public static ListMultimap transformValues(ListMultimap paramListMultimap, Function paramFunction)
  {
    Preconditions.checkNotNull(paramFunction);
    Maps.EntryTransformer local3 = new Maps.EntryTransformer()
    {
      public Object transformEntry(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        return this.val$function.apply(paramAnonymousObject2);
      }
    };
    return transformEntries(paramListMultimap, local3);
  }
  
  public static ListMultimap transformEntries(ListMultimap paramListMultimap, Maps.EntryTransformer paramEntryTransformer)
  {
    return new TransformedEntriesListMultimap(paramListMultimap, paramEntryTransformer);
  }
  
  public static ImmutableListMultimap index(Iterable paramIterable, Function paramFunction)
  {
    return index(paramIterable.iterator(), paramFunction);
  }
  
  public static ImmutableListMultimap index(Iterator paramIterator, Function paramFunction)
  {
    Preconditions.checkNotNull(paramFunction);
    ImmutableListMultimap.Builder localBuilder = ImmutableListMultimap.builder();
    while (paramIterator.hasNext())
    {
      Object localObject = paramIterator.next();
      Preconditions.checkNotNull(localObject, paramIterator);
      localBuilder.put(paramFunction.apply(localObject), localObject);
    }
    return localBuilder.build();
  }
  
  @GwtIncompatible("untested")
  public static Multimap filterKeys(Multimap paramMultimap, Predicate paramPredicate)
  {
    Object localObject;
    if ((paramMultimap instanceof FilteredKeyMultimap))
    {
      localObject = (FilteredKeyMultimap)paramMultimap;
      return new FilteredKeyMultimap(((FilteredKeyMultimap)localObject).unfiltered, Predicates.and(((FilteredKeyMultimap)localObject).keyPredicate, paramPredicate));
    }
    if ((paramMultimap instanceof FilteredMultimap))
    {
      localObject = (FilteredMultimap)paramMultimap;
      return new FilteredEntryMultimap(((FilteredMultimap)localObject).unfiltered, Predicates.and(((FilteredMultimap)localObject).entryPredicate(), Predicates.compose(paramPredicate, Maps.keyFunction())));
    }
    return new FilteredKeyMultimap(paramMultimap, paramPredicate);
  }
  
  @GwtIncompatible("untested")
  public static Multimap filterValues(Multimap paramMultimap, Predicate paramPredicate)
  {
    return filterEntries(paramMultimap, Predicates.compose(paramPredicate, Maps.valueFunction()));
  }
  
  @GwtIncompatible("untested")
  public static Multimap filterEntries(Multimap paramMultimap, Predicate paramPredicate)
  {
    Preconditions.checkNotNull(paramPredicate);
    return (paramMultimap instanceof FilteredMultimap) ? filterFiltered((FilteredMultimap)paramMultimap, paramPredicate) : new FilteredEntryMultimap((Multimap)Preconditions.checkNotNull(paramMultimap), paramPredicate);
  }
  
  private static Multimap filterFiltered(FilteredMultimap paramFilteredMultimap, Predicate paramPredicate)
  {
    Predicate localPredicate = Predicates.and(paramFilteredMultimap.entryPredicate(), paramPredicate);
    return new FilteredEntryMultimap(paramFilteredMultimap.unfiltered, localPredicate);
  }
  
  static abstract class AsMap
    extends Maps.ImprovedAbstractMap
  {
    abstract Multimap multimap();
    
    public abstract int size();
    
    abstract Iterator entryIterator();
    
    protected Set createEntrySet()
    {
      return new EntrySet();
    }
    
    void removeValuesForKey(Object paramObject)
    {
      multimap().removeAll(paramObject);
    }
    
    public Collection get(Object paramObject)
    {
      return containsKey(paramObject) ? multimap().get(paramObject) : null;
    }
    
    public Collection remove(Object paramObject)
    {
      return containsKey(paramObject) ? multimap().removeAll(paramObject) : null;
    }
    
    public Set keySet()
    {
      return multimap().keySet();
    }
    
    public boolean isEmpty()
    {
      return multimap().isEmpty();
    }
    
    public boolean containsKey(Object paramObject)
    {
      return multimap().containsKey(paramObject);
    }
    
    public void clear()
    {
      multimap().clear();
    }
    
    class EntrySet
      extends Maps.EntrySet
    {
      EntrySet() {}
      
      Map map()
      {
        return Multimaps.AsMap.this;
      }
      
      public Iterator iterator()
      {
        return Multimaps.AsMap.this.entryIterator();
      }
      
      public boolean remove(Object paramObject)
      {
        if (!contains(paramObject)) {
          return false;
        }
        Map.Entry localEntry = (Map.Entry)paramObject;
        Multimaps.AsMap.this.removeValuesForKey(localEntry.getKey());
        return true;
      }
    }
  }
  
  static abstract class EntrySet
    extends Multimaps.Entries
    implements Set
  {
    public int hashCode()
    {
      return Sets.hashCodeImpl(this);
    }
    
    public boolean equals(Object paramObject)
    {
      return Sets.equalsImpl(this, paramObject);
    }
  }
  
  static abstract class Entries
    extends AbstractCollection
  {
    abstract Multimap multimap();
    
    public int size()
    {
      return multimap().size();
    }
    
    public boolean contains(Object paramObject)
    {
      if ((paramObject instanceof Map.Entry))
      {
        Map.Entry localEntry = (Map.Entry)paramObject;
        return multimap().containsEntry(localEntry.getKey(), localEntry.getValue());
      }
      return false;
    }
    
    public boolean remove(Object paramObject)
    {
      if ((paramObject instanceof Map.Entry))
      {
        Map.Entry localEntry = (Map.Entry)paramObject;
        return multimap().remove(localEntry.getKey(), localEntry.getValue());
      }
      return false;
    }
    
    public void clear()
    {
      multimap().clear();
    }
  }
  
  static class Values
    extends AbstractCollection
  {
    final Multimap multimap;
    
    Values(Multimap paramMultimap)
    {
      this.multimap = paramMultimap;
    }
    
    public Iterator iterator()
    {
      return Maps.valueIterator(this.multimap.entries().iterator());
    }
    
    public int size()
    {
      return this.multimap.size();
    }
    
    public boolean contains(Object paramObject)
    {
      return this.multimap.containsValue(paramObject);
    }
    
    public void clear()
    {
      this.multimap.clear();
    }
  }
  
  static class Keys
    extends AbstractMultiset
  {
    final Multimap multimap;
    
    Keys(Multimap paramMultimap)
    {
      this.multimap = paramMultimap;
    }
    
    Iterator entryIterator()
    {
      new TransformedIterator(this.multimap.asMap().entrySet().iterator())
      {
        Multiset.Entry transform(final Map.Entry paramAnonymousEntry)
        {
          new Multisets.AbstractEntry()
          {
            public Object getElement()
            {
              return paramAnonymousEntry.getKey();
            }
            
            public int getCount()
            {
              return ((Collection)paramAnonymousEntry.getValue()).size();
            }
          };
        }
      };
    }
    
    int distinctElements()
    {
      return this.multimap.asMap().size();
    }
    
    Set createEntrySet()
    {
      return new KeysEntrySet();
    }
    
    public boolean contains(Object paramObject)
    {
      return this.multimap.containsKey(paramObject);
    }
    
    public Iterator iterator()
    {
      return Maps.keyIterator(this.multimap.entries().iterator());
    }
    
    public int count(Object paramObject)
    {
      Collection localCollection = (Collection)Maps.safeGet(this.multimap.asMap(), paramObject);
      return localCollection == null ? 0 : localCollection.size();
    }
    
    public int remove(Object paramObject, int paramInt)
    {
      Preconditions.checkArgument(paramInt >= 0);
      if (paramInt == 0) {
        return count(paramObject);
      }
      Collection localCollection = (Collection)Maps.safeGet(this.multimap.asMap(), paramObject);
      if (localCollection == null) {
        return 0;
      }
      int i = localCollection.size();
      if (paramInt >= i)
      {
        localCollection.clear();
      }
      else
      {
        Iterator localIterator = localCollection.iterator();
        for (int j = 0; j < paramInt; j++)
        {
          localIterator.next();
          localIterator.remove();
        }
      }
      return i;
    }
    
    public void clear()
    {
      this.multimap.clear();
    }
    
    public Set elementSet()
    {
      return this.multimap.keySet();
    }
    
    class KeysEntrySet
      extends Multisets.EntrySet
    {
      KeysEntrySet() {}
      
      Multiset multiset()
      {
        return Multimaps.Keys.this;
      }
      
      public Iterator iterator()
      {
        return Multimaps.Keys.this.entryIterator();
      }
      
      public int size()
      {
        return Multimaps.Keys.this.distinctElements();
      }
      
      public boolean isEmpty()
      {
        return Multimaps.Keys.this.multimap.isEmpty();
      }
      
      public boolean contains(Object paramObject)
      {
        if ((paramObject instanceof Multiset.Entry))
        {
          Multiset.Entry localEntry = (Multiset.Entry)paramObject;
          Collection localCollection = (Collection)Multimaps.Keys.this.multimap.asMap().get(localEntry.getElement());
          return (localCollection != null) && (localCollection.size() == localEntry.getCount());
        }
        return false;
      }
      
      public boolean remove(Object paramObject)
      {
        if ((paramObject instanceof Multiset.Entry))
        {
          Multiset.Entry localEntry = (Multiset.Entry)paramObject;
          Collection localCollection = (Collection)Multimaps.Keys.this.multimap.asMap().get(localEntry.getElement());
          if ((localCollection != null) && (localCollection.size() == localEntry.getCount()))
          {
            localCollection.clear();
            return true;
          }
        }
        return false;
      }
    }
  }
  
  private static final class TransformedEntriesListMultimap
    extends Multimaps.TransformedEntriesMultimap
    implements ListMultimap
  {
    TransformedEntriesListMultimap(ListMultimap paramListMultimap, Maps.EntryTransformer paramEntryTransformer)
    {
      super(paramEntryTransformer);
    }
    
    List transform(final Object paramObject, Collection paramCollection)
    {
      Lists.transform((List)paramCollection, new Function()
      {
        public Object apply(Object paramAnonymousObject)
        {
          return Multimaps.TransformedEntriesListMultimap.this.transformer.transformEntry(paramObject, paramAnonymousObject);
        }
      });
    }
    
    public List get(Object paramObject)
    {
      return transform(paramObject, this.fromMultimap.get(paramObject));
    }
    
    public List removeAll(Object paramObject)
    {
      return transform(paramObject, this.fromMultimap.removeAll(paramObject));
    }
    
    public List replaceValues(Object paramObject, Iterable paramIterable)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private static class TransformedEntriesMultimap
    extends AbstractMultimap
  {
    final Multimap fromMultimap;
    final Maps.EntryTransformer transformer;
    
    TransformedEntriesMultimap(Multimap paramMultimap, Maps.EntryTransformer paramEntryTransformer)
    {
      this.fromMultimap = ((Multimap)Preconditions.checkNotNull(paramMultimap));
      this.transformer = ((Maps.EntryTransformer)Preconditions.checkNotNull(paramEntryTransformer));
    }
    
    Collection transform(Object paramObject, Collection paramCollection)
    {
      Multimaps.ValueFunction localValueFunction = new Multimaps.ValueFunction(paramObject, this.transformer);
      if ((paramCollection instanceof List)) {
        return Lists.transform((List)paramCollection, localValueFunction);
      }
      return Collections2.transform(paramCollection, localValueFunction);
    }
    
    Map createAsMap()
    {
      Maps.transformEntries(this.fromMultimap.asMap(), new Maps.EntryTransformer()
      {
        public Collection transformEntry(Object paramAnonymousObject, Collection paramAnonymousCollection)
        {
          return Multimaps.TransformedEntriesMultimap.this.transform(paramAnonymousObject, paramAnonymousCollection);
        }
      });
    }
    
    public void clear()
    {
      this.fromMultimap.clear();
    }
    
    public boolean containsEntry(Object paramObject1, Object paramObject2)
    {
      Collection localCollection = get(paramObject1);
      return localCollection.contains(paramObject2);
    }
    
    public boolean containsKey(Object paramObject)
    {
      return this.fromMultimap.containsKey(paramObject);
    }
    
    public boolean containsValue(Object paramObject)
    {
      return values().contains(paramObject);
    }
    
    Iterator entryIterator()
    {
      Iterators.transform(this.fromMultimap.entries().iterator(), new Function()
      {
        public Map.Entry apply(final Map.Entry paramAnonymousEntry)
        {
          new AbstractMapEntry()
          {
            public Object getKey()
            {
              return paramAnonymousEntry.getKey();
            }
            
            public Object getValue()
            {
              return Multimaps.TransformedEntriesMultimap.this.transformer.transformEntry(paramAnonymousEntry.getKey(), paramAnonymousEntry.getValue());
            }
          };
        }
      });
    }
    
    public Collection get(Object paramObject)
    {
      return transform(paramObject, this.fromMultimap.get(paramObject));
    }
    
    public boolean isEmpty()
    {
      return this.fromMultimap.isEmpty();
    }
    
    public Set keySet()
    {
      return this.fromMultimap.keySet();
    }
    
    public Multiset keys()
    {
      return this.fromMultimap.keys();
    }
    
    public boolean put(Object paramObject1, Object paramObject2)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean putAll(Object paramObject, Iterable paramIterable)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean putAll(Multimap paramMultimap)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(Object paramObject1, Object paramObject2)
    {
      return get(paramObject1).remove(paramObject2);
    }
    
    public Collection removeAll(Object paramObject)
    {
      return transform(paramObject, this.fromMultimap.removeAll(paramObject));
    }
    
    public Collection replaceValues(Object paramObject, Iterable paramIterable)
    {
      throw new UnsupportedOperationException();
    }
    
    public int size()
    {
      return this.fromMultimap.size();
    }
    
    Collection createValues()
    {
      Collections2.transform(this.fromMultimap.entries(), new Function()
      {
        public Object apply(Map.Entry paramAnonymousEntry)
        {
          return Multimaps.TransformedEntriesMultimap.this.transformer.transformEntry(paramAnonymousEntry.getKey(), paramAnonymousEntry.getValue());
        }
      });
    }
  }
  
  static final class ValueFunction
    implements Function
  {
    private final Object key;
    private final Maps.EntryTransformer transformer;
    
    ValueFunction(Object paramObject, Maps.EntryTransformer paramEntryTransformer)
    {
      this.key = paramObject;
      this.transformer = paramEntryTransformer;
    }
    
    public Object apply(Object paramObject)
    {
      return this.transformer.transformEntry(this.key, paramObject);
    }
  }
  
  private static class MapMultimap
    implements SetMultimap, Serializable
  {
    final Map map;
    transient Map asMap;
    private static final Joiner.MapJoiner JOINER = Joiner.on("], ").withKeyValueSeparator("=[").useForNull("null");
    private static final long serialVersionUID = 7845222491160860175L;
    
    MapMultimap(Map paramMap)
    {
      this.map = ((Map)Preconditions.checkNotNull(paramMap));
    }
    
    public int size()
    {
      return this.map.size();
    }
    
    public boolean isEmpty()
    {
      return this.map.isEmpty();
    }
    
    public boolean containsKey(Object paramObject)
    {
      return this.map.containsKey(paramObject);
    }
    
    public boolean containsValue(Object paramObject)
    {
      return this.map.containsValue(paramObject);
    }
    
    public boolean containsEntry(Object paramObject1, Object paramObject2)
    {
      return this.map.entrySet().contains(Maps.immutableEntry(paramObject1, paramObject2));
    }
    
    public Set get(final Object paramObject)
    {
      new Sets.ImprovedAbstractSet()
      {
        public Iterator iterator()
        {
          new Iterator()
          {
            int i;
            
            public boolean hasNext()
            {
              return (this.i == 0) && (Multimaps.MapMultimap.this.map.containsKey(Multimaps.MapMultimap.1.this.val$key));
            }
            
            public Object next()
            {
              if (!hasNext()) {
                throw new NoSuchElementException();
              }
              this.i += 1;
              return Multimaps.MapMultimap.this.map.get(Multimaps.MapMultimap.1.this.val$key);
            }
            
            public void remove()
            {
              Preconditions.checkState(this.i == 1);
              this.i = -1;
              Multimaps.MapMultimap.this.map.remove(Multimaps.MapMultimap.1.this.val$key);
            }
          };
        }
        
        public int size()
        {
          return Multimaps.MapMultimap.this.map.containsKey(paramObject) ? 1 : 0;
        }
      };
    }
    
    public boolean put(Object paramObject1, Object paramObject2)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean putAll(Object paramObject, Iterable paramIterable)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean putAll(Multimap paramMultimap)
    {
      throw new UnsupportedOperationException();
    }
    
    public Set replaceValues(Object paramObject, Iterable paramIterable)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(Object paramObject1, Object paramObject2)
    {
      return this.map.entrySet().remove(Maps.immutableEntry(paramObject1, paramObject2));
    }
    
    public Set removeAll(Object paramObject)
    {
      HashSet localHashSet = new HashSet(2);
      if (!this.map.containsKey(paramObject)) {
        return localHashSet;
      }
      localHashSet.add(this.map.remove(paramObject));
      return localHashSet;
    }
    
    public void clear()
    {
      this.map.clear();
    }
    
    public Set keySet()
    {
      return this.map.keySet();
    }
    
    public Multiset keys()
    {
      return new Multimaps.Keys(this);
    }
    
    public Collection values()
    {
      return this.map.values();
    }
    
    public Set entries()
    {
      return this.map.entrySet();
    }
    
    public Map asMap()
    {
      Object localObject = this.asMap;
      if (localObject == null) {
        this.asMap = (localObject = new AsMap());
      }
      return (Map)localObject;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof Multimap))
      {
        Multimap localMultimap = (Multimap)paramObject;
        return (size() == localMultimap.size()) && (asMap().equals(localMultimap.asMap()));
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.map.hashCode();
    }
    
    public String toString()
    {
      if (this.map.isEmpty()) {
        return "{}";
      }
      StringBuilder localStringBuilder = Collections2.newStringBuilderForCollection(this.map.size()).append('{');
      JOINER.appendTo(localStringBuilder, this.map);
      return "]}";
    }
    
    class AsMap
      extends Maps.ImprovedAbstractMap
    {
      AsMap() {}
      
      protected Set createEntrySet()
      {
        return new Multimaps.MapMultimap.AsMapEntries(Multimaps.MapMultimap.this);
      }
      
      public boolean containsKey(Object paramObject)
      {
        return Multimaps.MapMultimap.this.map.containsKey(paramObject);
      }
      
      public Collection get(Object paramObject)
      {
        Set localSet = Multimaps.MapMultimap.this.get(paramObject);
        return localSet.isEmpty() ? null : localSet;
      }
      
      public Collection remove(Object paramObject)
      {
        Set localSet = Multimaps.MapMultimap.this.removeAll(paramObject);
        return localSet.isEmpty() ? null : localSet;
      }
    }
    
    class AsMapEntries
      extends Sets.ImprovedAbstractSet
    {
      AsMapEntries() {}
      
      public int size()
      {
        return Multimaps.MapMultimap.this.map.size();
      }
      
      public Iterator iterator()
      {
        new TransformedIterator(Multimaps.MapMultimap.this.map.keySet().iterator())
        {
          Map.Entry transform(final Object paramAnonymousObject)
          {
            new AbstractMapEntry()
            {
              public Object getKey()
              {
                return paramAnonymousObject;
              }
              
              public Collection getValue()
              {
                return Multimaps.MapMultimap.this.get(paramAnonymousObject);
              }
            };
          }
        };
      }
      
      public boolean contains(Object paramObject)
      {
        if (!(paramObject instanceof Map.Entry)) {
          return false;
        }
        Map.Entry localEntry = (Map.Entry)paramObject;
        if (!(localEntry.getValue() instanceof Set)) {
          return false;
        }
        Set localSet = (Set)localEntry.getValue();
        return (localSet.size() == 1) && (Multimaps.MapMultimap.this.containsEntry(localEntry.getKey(), localSet.iterator().next()));
      }
      
      public boolean remove(Object paramObject)
      {
        if (!(paramObject instanceof Map.Entry)) {
          return false;
        }
        Map.Entry localEntry = (Map.Entry)paramObject;
        if (!(localEntry.getValue() instanceof Set)) {
          return false;
        }
        Set localSet = (Set)localEntry.getValue();
        return (localSet.size() == 1) && (Multimaps.MapMultimap.this.map.entrySet().remove(Maps.immutableEntry(localEntry.getKey(), localSet.iterator().next())));
      }
    }
  }
  
  static class UnmodifiableAsMapEntries
    extends ForwardingSet
  {
    private final Set delegate;
    
    UnmodifiableAsMapEntries(Set paramSet)
    {
      this.delegate = paramSet;
    }
    
    protected Set delegate()
    {
      return this.delegate;
    }
    
    public Iterator iterator()
    {
      final Iterator localIterator = this.delegate.iterator();
      new ForwardingIterator()
      {
        protected Iterator delegate()
        {
          return localIterator;
        }
        
        public Map.Entry next()
        {
          return Multimaps.unmodifiableAsMapEntry((Map.Entry)localIterator.next());
        }
      };
    }
    
    public Object[] toArray()
    {
      return standardToArray();
    }
    
    public Object[] toArray(Object[] paramArrayOfObject)
    {
      return standardToArray(paramArrayOfObject);
    }
    
    public boolean contains(Object paramObject)
    {
      return Maps.containsEntryImpl(delegate(), paramObject);
    }
    
    public boolean containsAll(Collection paramCollection)
    {
      return standardContainsAll(paramCollection);
    }
    
    public boolean equals(Object paramObject)
    {
      return standardEquals(paramObject);
    }
  }
  
  private static class UnmodifiableSortedSetMultimap
    extends Multimaps.UnmodifiableSetMultimap
    implements SortedSetMultimap
  {
    private static final long serialVersionUID = 0L;
    
    UnmodifiableSortedSetMultimap(SortedSetMultimap paramSortedSetMultimap)
    {
      super();
    }
    
    public SortedSetMultimap delegate()
    {
      return (SortedSetMultimap)super.delegate();
    }
    
    public SortedSet get(Object paramObject)
    {
      return Collections.unmodifiableSortedSet(delegate().get(paramObject));
    }
    
    public SortedSet removeAll(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public SortedSet replaceValues(Object paramObject, Iterable paramIterable)
    {
      throw new UnsupportedOperationException();
    }
    
    public Comparator valueComparator()
    {
      return delegate().valueComparator();
    }
  }
  
  private static class UnmodifiableSetMultimap
    extends Multimaps.UnmodifiableMultimap
    implements SetMultimap
  {
    private static final long serialVersionUID = 0L;
    
    UnmodifiableSetMultimap(SetMultimap paramSetMultimap)
    {
      super();
    }
    
    public SetMultimap delegate()
    {
      return (SetMultimap)super.delegate();
    }
    
    public Set get(Object paramObject)
    {
      return Collections.unmodifiableSet(delegate().get(paramObject));
    }
    
    public Set entries()
    {
      return Maps.unmodifiableEntrySet(delegate().entries());
    }
    
    public Set removeAll(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public Set replaceValues(Object paramObject, Iterable paramIterable)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private static class UnmodifiableListMultimap
    extends Multimaps.UnmodifiableMultimap
    implements ListMultimap
  {
    private static final long serialVersionUID = 0L;
    
    UnmodifiableListMultimap(ListMultimap paramListMultimap)
    {
      super();
    }
    
    public ListMultimap delegate()
    {
      return (ListMultimap)super.delegate();
    }
    
    public List get(Object paramObject)
    {
      return Collections.unmodifiableList(delegate().get(paramObject));
    }
    
    public List removeAll(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public List replaceValues(Object paramObject, Iterable paramIterable)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private static class UnmodifiableAsMapValues
    extends ForwardingCollection
  {
    final Collection delegate;
    
    UnmodifiableAsMapValues(Collection paramCollection)
    {
      this.delegate = Collections.unmodifiableCollection(paramCollection);
    }
    
    protected Collection delegate()
    {
      return this.delegate;
    }
    
    public Iterator iterator()
    {
      final Iterator localIterator = this.delegate.iterator();
      new UnmodifiableIterator()
      {
        public boolean hasNext()
        {
          return localIterator.hasNext();
        }
        
        public Collection next()
        {
          return Multimaps.unmodifiableValueCollection((Collection)localIterator.next());
        }
      };
    }
    
    public Object[] toArray()
    {
      return standardToArray();
    }
    
    public Object[] toArray(Object[] paramArrayOfObject)
    {
      return standardToArray(paramArrayOfObject);
    }
    
    public boolean contains(Object paramObject)
    {
      return standardContains(paramObject);
    }
    
    public boolean containsAll(Collection paramCollection)
    {
      return standardContainsAll(paramCollection);
    }
  }
  
  private static class UnmodifiableMultimap
    extends ForwardingMultimap
    implements Serializable
  {
    final Multimap delegate;
    transient Collection entries;
    transient Multiset keys;
    transient Set keySet;
    transient Collection values;
    transient Map map;
    private static final long serialVersionUID = 0L;
    
    UnmodifiableMultimap(Multimap paramMultimap)
    {
      this.delegate = ((Multimap)Preconditions.checkNotNull(paramMultimap));
    }
    
    protected Multimap delegate()
    {
      return this.delegate;
    }
    
    public void clear()
    {
      throw new UnsupportedOperationException();
    }
    
    public Map asMap()
    {
      Object localObject = this.map;
      if (localObject == null)
      {
        final Map localMap = Collections.unmodifiableMap(this.delegate.asMap());
        this.map = ( = new ForwardingMap()
        {
          Set entrySet;
          Collection asMapValues;
          
          protected Map delegate()
          {
            return localMap;
          }
          
          public Set entrySet()
          {
            Set localSet = this.entrySet;
            return localSet == null ? (this.entrySet = Multimaps.unmodifiableAsMapEntries(localMap.entrySet())) : localSet;
          }
          
          public Collection get(Object paramAnonymousObject)
          {
            Collection localCollection = (Collection)localMap.get(paramAnonymousObject);
            return localCollection == null ? null : Multimaps.unmodifiableValueCollection(localCollection);
          }
          
          public Collection values()
          {
            Collection localCollection = this.asMapValues;
            return localCollection == null ? (this.asMapValues = new Multimaps.UnmodifiableAsMapValues(localMap.values())) : localCollection;
          }
          
          public boolean containsValue(Object paramAnonymousObject)
          {
            return values().contains(paramAnonymousObject);
          }
        });
      }
      return (Map)localObject;
    }
    
    public Collection entries()
    {
      Collection localCollection = this.entries;
      if (localCollection == null) {
        this.entries = (localCollection = Multimaps.unmodifiableEntries(this.delegate.entries()));
      }
      return localCollection;
    }
    
    public Collection get(Object paramObject)
    {
      return Multimaps.unmodifiableValueCollection(this.delegate.get(paramObject));
    }
    
    public Multiset keys()
    {
      Multiset localMultiset = this.keys;
      if (localMultiset == null) {
        this.keys = (localMultiset = Multisets.unmodifiableMultiset(this.delegate.keys()));
      }
      return localMultiset;
    }
    
    public Set keySet()
    {
      Set localSet = this.keySet;
      if (localSet == null) {
        this.keySet = (localSet = Collections.unmodifiableSet(this.delegate.keySet()));
      }
      return localSet;
    }
    
    public boolean put(Object paramObject1, Object paramObject2)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean putAll(Object paramObject, Iterable paramIterable)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean putAll(Multimap paramMultimap)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(Object paramObject1, Object paramObject2)
    {
      throw new UnsupportedOperationException();
    }
    
    public Collection removeAll(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public Collection replaceValues(Object paramObject, Iterable paramIterable)
    {
      throw new UnsupportedOperationException();
    }
    
    public Collection values()
    {
      Collection localCollection = this.values;
      if (localCollection == null) {
        this.values = (localCollection = Collections.unmodifiableCollection(this.delegate.values()));
      }
      return localCollection;
    }
  }
  
  private static class CustomSortedSetMultimap
    extends AbstractSortedSetMultimap
  {
    transient Supplier factory;
    transient Comparator valueComparator;
    @GwtIncompatible("not needed in emulated source")
    private static final long serialVersionUID = 0L;
    
    CustomSortedSetMultimap(Map paramMap, Supplier paramSupplier)
    {
      super();
      this.factory = ((Supplier)Preconditions.checkNotNull(paramSupplier));
      this.valueComparator = ((SortedSet)paramSupplier.get()).comparator();
    }
    
    protected SortedSet createCollection()
    {
      return (SortedSet)this.factory.get();
    }
    
    public Comparator valueComparator()
    {
      return this.valueComparator;
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
      paramObjectOutputStream.writeObject(this.factory);
      paramObjectOutputStream.writeObject(backingMap());
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      this.factory = ((Supplier)paramObjectInputStream.readObject());
      this.valueComparator = ((SortedSet)this.factory.get()).comparator();
      Map localMap = (Map)paramObjectInputStream.readObject();
      setMap(localMap);
    }
  }
  
  private static class CustomSetMultimap
    extends AbstractSetMultimap
  {
    transient Supplier factory;
    @GwtIncompatible("not needed in emulated source")
    private static final long serialVersionUID = 0L;
    
    CustomSetMultimap(Map paramMap, Supplier paramSupplier)
    {
      super();
      this.factory = ((Supplier)Preconditions.checkNotNull(paramSupplier));
    }
    
    protected Set createCollection()
    {
      return (Set)this.factory.get();
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
      paramObjectOutputStream.writeObject(this.factory);
      paramObjectOutputStream.writeObject(backingMap());
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      this.factory = ((Supplier)paramObjectInputStream.readObject());
      Map localMap = (Map)paramObjectInputStream.readObject();
      setMap(localMap);
    }
  }
  
  private static class CustomListMultimap
    extends AbstractListMultimap
  {
    transient Supplier factory;
    @GwtIncompatible("java serialization not supported")
    private static final long serialVersionUID = 0L;
    
    CustomListMultimap(Map paramMap, Supplier paramSupplier)
    {
      super();
      this.factory = ((Supplier)Preconditions.checkNotNull(paramSupplier));
    }
    
    protected List createCollection()
    {
      return (List)this.factory.get();
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
      paramObjectOutputStream.writeObject(this.factory);
      paramObjectOutputStream.writeObject(backingMap());
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      this.factory = ((Supplier)paramObjectInputStream.readObject());
      Map localMap = (Map)paramObjectInputStream.readObject();
      setMap(localMap);
    }
  }
  
  private static class CustomMultimap
    extends AbstractMapBasedMultimap
  {
    transient Supplier factory;
    @GwtIncompatible("java serialization not supported")
    private static final long serialVersionUID = 0L;
    
    CustomMultimap(Map paramMap, Supplier paramSupplier)
    {
      super();
      this.factory = ((Supplier)Preconditions.checkNotNull(paramSupplier));
    }
    
    protected Collection createCollection()
    {
      return (Collection)this.factory.get();
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
      paramObjectOutputStream.writeObject(this.factory);
      paramObjectOutputStream.writeObject(backingMap());
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      this.factory = ((Supplier)paramObjectInputStream.readObject());
      Map localMap = (Map)paramObjectInputStream.readObject();
      setMap(localMap);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Multimaps.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */