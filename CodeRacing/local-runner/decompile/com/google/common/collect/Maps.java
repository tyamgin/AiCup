package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;

@GwtCompatible(emulated=true)
public final class Maps
{
  static final Joiner.MapJoiner STANDARD_JOINER = Collections2.STANDARD_JOINER.withKeyValueSeparator("=");
  
  static Function keyFunction()
  {
    return EntryFunction.KEY;
  }
  
  static Function valueFunction()
  {
    return EntryFunction.VALUE;
  }
  
  @GwtCompatible(serializable=true)
  @Beta
  public static ImmutableMap immutableEnumMap(Map paramMap)
  {
    if ((paramMap instanceof ImmutableEnumMap)) {
      return (ImmutableEnumMap)paramMap;
    }
    if (paramMap.isEmpty()) {
      return ImmutableMap.of();
    }
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Preconditions.checkNotNull(localEntry.getKey());
      Preconditions.checkNotNull(localEntry.getValue());
    }
    return ImmutableEnumMap.asImmutable(new EnumMap(paramMap));
  }
  
  public static HashMap newHashMap()
  {
    return new HashMap();
  }
  
  public static HashMap newHashMapWithExpectedSize(int paramInt)
  {
    return new HashMap(capacity(paramInt));
  }
  
  static int capacity(int paramInt)
  {
    if (paramInt < 3)
    {
      Preconditions.checkArgument(paramInt >= 0);
      return paramInt + 1;
    }
    if (paramInt < 1073741824) {
      return paramInt + paramInt / 3;
    }
    return Integer.MAX_VALUE;
  }
  
  public static HashMap newHashMap(Map paramMap)
  {
    return new HashMap(paramMap);
  }
  
  public static LinkedHashMap newLinkedHashMap()
  {
    return new LinkedHashMap();
  }
  
  public static LinkedHashMap newLinkedHashMap(Map paramMap)
  {
    return new LinkedHashMap(paramMap);
  }
  
  public static ConcurrentMap newConcurrentMap()
  {
    return new MapMaker().makeMap();
  }
  
  public static TreeMap newTreeMap()
  {
    return new TreeMap();
  }
  
  public static TreeMap newTreeMap(SortedMap paramSortedMap)
  {
    return new TreeMap(paramSortedMap);
  }
  
  public static TreeMap newTreeMap(Comparator paramComparator)
  {
    return new TreeMap(paramComparator);
  }
  
  public static EnumMap newEnumMap(Class paramClass)
  {
    return new EnumMap((Class)Preconditions.checkNotNull(paramClass));
  }
  
  public static EnumMap newEnumMap(Map paramMap)
  {
    return new EnumMap(paramMap);
  }
  
  public static IdentityHashMap newIdentityHashMap()
  {
    return new IdentityHashMap();
  }
  
  public static MapDifference difference(Map paramMap1, Map paramMap2)
  {
    if ((paramMap1 instanceof SortedMap))
    {
      SortedMap localSortedMap = (SortedMap)paramMap1;
      SortedMapDifference localSortedMapDifference = difference(localSortedMap, paramMap2);
      return localSortedMapDifference;
    }
    return difference(paramMap1, paramMap2, Equivalence.equals());
  }
  
  @Beta
  public static MapDifference difference(Map paramMap1, Map paramMap2, Equivalence paramEquivalence)
  {
    Preconditions.checkNotNull(paramEquivalence);
    HashMap localHashMap1 = newHashMap();
    HashMap localHashMap2 = new HashMap(paramMap2);
    HashMap localHashMap3 = newHashMap();
    HashMap localHashMap4 = newHashMap();
    int i = 1;
    Iterator localIterator = paramMap1.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object localObject1 = localEntry.getKey();
      Object localObject2 = localEntry.getValue();
      if (paramMap2.containsKey(localObject1))
      {
        Object localObject3 = localHashMap2.remove(localObject1);
        if (paramEquivalence.equivalent(localObject2, localObject3))
        {
          localHashMap3.put(localObject1, localObject2);
        }
        else
        {
          i = 0;
          localHashMap4.put(localObject1, ValueDifferenceImpl.create(localObject2, localObject3));
        }
      }
      else
      {
        i = 0;
        localHashMap1.put(localObject1, localObject2);
      }
    }
    boolean bool = (i != 0) && (localHashMap2.isEmpty());
    return mapDifference(bool, localHashMap1, localHashMap2, localHashMap3, localHashMap4);
  }
  
  private static MapDifference mapDifference(boolean paramBoolean, Map paramMap1, Map paramMap2, Map paramMap3, Map paramMap4)
  {
    return new MapDifferenceImpl(paramBoolean, Collections.unmodifiableMap(paramMap1), Collections.unmodifiableMap(paramMap2), Collections.unmodifiableMap(paramMap3), Collections.unmodifiableMap(paramMap4));
  }
  
  public static SortedMapDifference difference(SortedMap paramSortedMap, Map paramMap)
  {
    Preconditions.checkNotNull(paramSortedMap);
    Preconditions.checkNotNull(paramMap);
    Comparator localComparator = orNaturalOrder(paramSortedMap.comparator());
    TreeMap localTreeMap1 = newTreeMap(localComparator);
    TreeMap localTreeMap2 = newTreeMap(localComparator);
    localTreeMap2.putAll(paramMap);
    TreeMap localTreeMap3 = newTreeMap(localComparator);
    TreeMap localTreeMap4 = newTreeMap(localComparator);
    int i = 1;
    Iterator localIterator = paramSortedMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object localObject1 = localEntry.getKey();
      Object localObject2 = localEntry.getValue();
      if (paramMap.containsKey(localObject1))
      {
        Object localObject3 = localTreeMap2.remove(localObject1);
        if (Objects.equal(localObject2, localObject3))
        {
          localTreeMap3.put(localObject1, localObject2);
        }
        else
        {
          i = 0;
          localTreeMap4.put(localObject1, ValueDifferenceImpl.create(localObject2, localObject3));
        }
      }
      else
      {
        i = 0;
        localTreeMap1.put(localObject1, localObject2);
      }
    }
    boolean bool = (i != 0) && (localTreeMap2.isEmpty());
    return sortedMapDifference(bool, localTreeMap1, localTreeMap2, localTreeMap3, localTreeMap4);
  }
  
  private static SortedMapDifference sortedMapDifference(boolean paramBoolean, SortedMap paramSortedMap1, SortedMap paramSortedMap2, SortedMap paramSortedMap3, SortedMap paramSortedMap4)
  {
    return new SortedMapDifferenceImpl(paramBoolean, Collections.unmodifiableSortedMap(paramSortedMap1), Collections.unmodifiableSortedMap(paramSortedMap2), Collections.unmodifiableSortedMap(paramSortedMap3), Collections.unmodifiableSortedMap(paramSortedMap4));
  }
  
  static Comparator orNaturalOrder(Comparator paramComparator)
  {
    if (paramComparator != null) {
      return paramComparator;
    }
    return Ordering.natural();
  }
  
  @Beta
  public static Map asMap(Set paramSet, Function paramFunction)
  {
    if ((paramSet instanceof SortedSet)) {
      return asMap((SortedSet)paramSet, paramFunction);
    }
    return new AsMapView(paramSet, paramFunction);
  }
  
  @Beta
  public static SortedMap asMap(SortedSet paramSortedSet, Function paramFunction)
  {
    return Platform.mapsAsMapSortedSet(paramSortedSet, paramFunction);
  }
  
  static SortedMap asMapSortedIgnoreNavigable(SortedSet paramSortedSet, Function paramFunction)
  {
    return new SortedAsMapView(paramSortedSet, paramFunction);
  }
  
  @Beta
  @GwtIncompatible("NavigableMap")
  public static NavigableMap asMap(NavigableSet paramNavigableSet, Function paramFunction)
  {
    return new NavigableAsMapView(paramNavigableSet, paramFunction);
  }
  
  private static Iterator asSetEntryIterator(Set paramSet, final Function paramFunction)
  {
    new TransformedIterator(paramSet.iterator())
    {
      Map.Entry transform(Object paramAnonymousObject)
      {
        return Maps.immutableEntry(paramAnonymousObject, paramFunction.apply(paramAnonymousObject));
      }
    };
  }
  
  private static Set removeOnlySet(Set paramSet)
  {
    new ForwardingSet()
    {
      protected Set delegate()
      {
        return this.val$set;
      }
      
      public boolean add(Object paramAnonymousObject)
      {
        throw new UnsupportedOperationException();
      }
      
      public boolean addAll(Collection paramAnonymousCollection)
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  private static SortedSet removeOnlySortedSet(SortedSet paramSortedSet)
  {
    new ForwardingSortedSet()
    {
      protected SortedSet delegate()
      {
        return this.val$set;
      }
      
      public boolean add(Object paramAnonymousObject)
      {
        throw new UnsupportedOperationException();
      }
      
      public boolean addAll(Collection paramAnonymousCollection)
      {
        throw new UnsupportedOperationException();
      }
      
      public SortedSet headSet(Object paramAnonymousObject)
      {
        return Maps.removeOnlySortedSet(super.headSet(paramAnonymousObject));
      }
      
      public SortedSet subSet(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        return Maps.removeOnlySortedSet(super.subSet(paramAnonymousObject1, paramAnonymousObject2));
      }
      
      public SortedSet tailSet(Object paramAnonymousObject)
      {
        return Maps.removeOnlySortedSet(super.tailSet(paramAnonymousObject));
      }
    };
  }
  
  @GwtIncompatible("NavigableSet")
  private static NavigableSet removeOnlyNavigableSet(NavigableSet paramNavigableSet)
  {
    new ForwardingNavigableSet()
    {
      protected NavigableSet delegate()
      {
        return this.val$set;
      }
      
      public boolean add(Object paramAnonymousObject)
      {
        throw new UnsupportedOperationException();
      }
      
      public boolean addAll(Collection paramAnonymousCollection)
      {
        throw new UnsupportedOperationException();
      }
      
      public SortedSet headSet(Object paramAnonymousObject)
      {
        return Maps.removeOnlySortedSet(super.headSet(paramAnonymousObject));
      }
      
      public SortedSet subSet(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        return Maps.removeOnlySortedSet(super.subSet(paramAnonymousObject1, paramAnonymousObject2));
      }
      
      public SortedSet tailSet(Object paramAnonymousObject)
      {
        return Maps.removeOnlySortedSet(super.tailSet(paramAnonymousObject));
      }
      
      public NavigableSet headSet(Object paramAnonymousObject, boolean paramAnonymousBoolean)
      {
        return Maps.removeOnlyNavigableSet(super.headSet(paramAnonymousObject, paramAnonymousBoolean));
      }
      
      public NavigableSet tailSet(Object paramAnonymousObject, boolean paramAnonymousBoolean)
      {
        return Maps.removeOnlyNavigableSet(super.tailSet(paramAnonymousObject, paramAnonymousBoolean));
      }
      
      public NavigableSet subSet(Object paramAnonymousObject1, boolean paramAnonymousBoolean1, Object paramAnonymousObject2, boolean paramAnonymousBoolean2)
      {
        return Maps.removeOnlyNavigableSet(super.subSet(paramAnonymousObject1, paramAnonymousBoolean1, paramAnonymousObject2, paramAnonymousBoolean2));
      }
      
      public NavigableSet descendingSet()
      {
        return Maps.removeOnlyNavigableSet(super.descendingSet());
      }
    };
  }
  
  @Beta
  public static ImmutableMap toMap(Iterable paramIterable, Function paramFunction)
  {
    return toMap(paramIterable.iterator(), paramFunction);
  }
  
  @Beta
  public static ImmutableMap toMap(Iterator paramIterator, Function paramFunction)
  {
    Preconditions.checkNotNull(paramFunction);
    LinkedHashMap localLinkedHashMap = newLinkedHashMap();
    while (paramIterator.hasNext())
    {
      Object localObject = paramIterator.next();
      localLinkedHashMap.put(localObject, paramFunction.apply(localObject));
    }
    return ImmutableMap.copyOf(localLinkedHashMap);
  }
  
  public static ImmutableMap uniqueIndex(Iterable paramIterable, Function paramFunction)
  {
    return uniqueIndex(paramIterable.iterator(), paramFunction);
  }
  
  public static ImmutableMap uniqueIndex(Iterator paramIterator, Function paramFunction)
  {
    Preconditions.checkNotNull(paramFunction);
    ImmutableMap.Builder localBuilder = ImmutableMap.builder();
    while (paramIterator.hasNext())
    {
      Object localObject = paramIterator.next();
      localBuilder.put(paramFunction.apply(localObject), localObject);
    }
    return localBuilder.build();
  }
  
  @GwtIncompatible("java.util.Properties")
  public static ImmutableMap fromProperties(Properties paramProperties)
  {
    ImmutableMap.Builder localBuilder = ImmutableMap.builder();
    Enumeration localEnumeration = paramProperties.propertyNames();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      localBuilder.put(str, paramProperties.getProperty(str));
    }
    return localBuilder.build();
  }
  
  @GwtCompatible(serializable=true)
  public static Map.Entry immutableEntry(Object paramObject1, Object paramObject2)
  {
    return new ImmutableEntry(paramObject1, paramObject2);
  }
  
  static Set unmodifiableEntrySet(Set paramSet)
  {
    return new UnmodifiableEntrySet(Collections.unmodifiableSet(paramSet));
  }
  
  static Map.Entry unmodifiableEntry(Map.Entry paramEntry)
  {
    Preconditions.checkNotNull(paramEntry);
    new AbstractMapEntry()
    {
      public Object getKey()
      {
        return this.val$entry.getKey();
      }
      
      public Object getValue()
      {
        return this.val$entry.getValue();
      }
    };
  }
  
  public static BiMap synchronizedBiMap(BiMap paramBiMap)
  {
    return Synchronized.biMap(paramBiMap, null);
  }
  
  public static BiMap unmodifiableBiMap(BiMap paramBiMap)
  {
    return new UnmodifiableBiMap(paramBiMap, null);
  }
  
  public static Map transformValues(Map paramMap, Function paramFunction)
  {
    return transformEntries(paramMap, asEntryTransformer(paramFunction));
  }
  
  public static SortedMap transformValues(SortedMap paramSortedMap, Function paramFunction)
  {
    return transformEntries(paramSortedMap, asEntryTransformer(paramFunction));
  }
  
  @GwtIncompatible("NavigableMap")
  public static NavigableMap transformValues(NavigableMap paramNavigableMap, Function paramFunction)
  {
    return transformEntries(paramNavigableMap, asEntryTransformer(paramFunction));
  }
  
  private static EntryTransformer asEntryTransformer(Function paramFunction)
  {
    Preconditions.checkNotNull(paramFunction);
    new EntryTransformer()
    {
      public Object transformEntry(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        return this.val$function.apply(paramAnonymousObject2);
      }
    };
  }
  
  public static Map transformEntries(Map paramMap, EntryTransformer paramEntryTransformer)
  {
    if ((paramMap instanceof SortedMap)) {
      return transformEntries((SortedMap)paramMap, paramEntryTransformer);
    }
    return new TransformedEntriesMap(paramMap, paramEntryTransformer);
  }
  
  public static SortedMap transformEntries(SortedMap paramSortedMap, EntryTransformer paramEntryTransformer)
  {
    return Platform.mapsTransformEntriesSortedMap(paramSortedMap, paramEntryTransformer);
  }
  
  @GwtIncompatible("NavigableMap")
  public static NavigableMap transformEntries(NavigableMap paramNavigableMap, EntryTransformer paramEntryTransformer)
  {
    return new TransformedEntriesNavigableMap(paramNavigableMap, paramEntryTransformer);
  }
  
  static SortedMap transformEntriesIgnoreNavigable(SortedMap paramSortedMap, EntryTransformer paramEntryTransformer)
  {
    return new TransformedEntriesSortedMap(paramSortedMap, paramEntryTransformer);
  }
  
  public static Map filterKeys(Map paramMap, Predicate paramPredicate)
  {
    if ((paramMap instanceof SortedMap)) {
      return filterKeys((SortedMap)paramMap, paramPredicate);
    }
    if ((paramMap instanceof BiMap)) {
      return filterKeys((BiMap)paramMap, paramPredicate);
    }
    Preconditions.checkNotNull(paramPredicate);
    KeyPredicate localKeyPredicate = new KeyPredicate(paramPredicate);
    return (paramMap instanceof AbstractFilteredMap) ? filterFiltered((AbstractFilteredMap)paramMap, localKeyPredicate) : new FilteredKeyMap((Map)Preconditions.checkNotNull(paramMap), paramPredicate, localKeyPredicate);
  }
  
  public static SortedMap filterKeys(SortedMap paramSortedMap, Predicate paramPredicate)
  {
    return filterEntries(paramSortedMap, new KeyPredicate(paramPredicate));
  }
  
  @GwtIncompatible("NavigableMap")
  public static NavigableMap filterKeys(NavigableMap paramNavigableMap, Predicate paramPredicate)
  {
    return filterEntries(paramNavigableMap, new KeyPredicate(paramPredicate));
  }
  
  public static BiMap filterKeys(BiMap paramBiMap, Predicate paramPredicate)
  {
    Preconditions.checkNotNull(paramPredicate);
    return filterEntries(paramBiMap, new KeyPredicate(paramPredicate));
  }
  
  public static Map filterValues(Map paramMap, Predicate paramPredicate)
  {
    if ((paramMap instanceof SortedMap)) {
      return filterValues((SortedMap)paramMap, paramPredicate);
    }
    if ((paramMap instanceof BiMap)) {
      return filterValues((BiMap)paramMap, paramPredicate);
    }
    return filterEntries(paramMap, new ValuePredicate(paramPredicate));
  }
  
  public static SortedMap filterValues(SortedMap paramSortedMap, Predicate paramPredicate)
  {
    return filterEntries(paramSortedMap, new ValuePredicate(paramPredicate));
  }
  
  @GwtIncompatible("NavigableMap")
  public static NavigableMap filterValues(NavigableMap paramNavigableMap, Predicate paramPredicate)
  {
    return filterEntries(paramNavigableMap, new ValuePredicate(paramPredicate));
  }
  
  public static BiMap filterValues(BiMap paramBiMap, Predicate paramPredicate)
  {
    return filterEntries(paramBiMap, new ValuePredicate(paramPredicate));
  }
  
  public static Map filterEntries(Map paramMap, Predicate paramPredicate)
  {
    if ((paramMap instanceof SortedMap)) {
      return filterEntries((SortedMap)paramMap, paramPredicate);
    }
    if ((paramMap instanceof BiMap)) {
      return filterEntries((BiMap)paramMap, paramPredicate);
    }
    Preconditions.checkNotNull(paramPredicate);
    return (paramMap instanceof AbstractFilteredMap) ? filterFiltered((AbstractFilteredMap)paramMap, paramPredicate) : new FilteredEntryMap((Map)Preconditions.checkNotNull(paramMap), paramPredicate);
  }
  
  public static SortedMap filterEntries(SortedMap paramSortedMap, Predicate paramPredicate)
  {
    return Platform.mapsFilterSortedMap(paramSortedMap, paramPredicate);
  }
  
  static SortedMap filterSortedIgnoreNavigable(SortedMap paramSortedMap, Predicate paramPredicate)
  {
    Preconditions.checkNotNull(paramPredicate);
    return (paramSortedMap instanceof FilteredEntrySortedMap) ? filterFiltered((FilteredEntrySortedMap)paramSortedMap, paramPredicate) : new FilteredEntrySortedMap((SortedMap)Preconditions.checkNotNull(paramSortedMap), paramPredicate);
  }
  
  @GwtIncompatible("NavigableMap")
  public static NavigableMap filterEntries(NavigableMap paramNavigableMap, Predicate paramPredicate)
  {
    Preconditions.checkNotNull(paramPredicate);
    return (paramNavigableMap instanceof FilteredEntryNavigableMap) ? filterFiltered((FilteredEntryNavigableMap)paramNavigableMap, paramPredicate) : new FilteredEntryNavigableMap((NavigableMap)Preconditions.checkNotNull(paramNavigableMap), paramPredicate);
  }
  
  public static BiMap filterEntries(BiMap paramBiMap, Predicate paramPredicate)
  {
    Preconditions.checkNotNull(paramBiMap);
    Preconditions.checkNotNull(paramPredicate);
    return (paramBiMap instanceof FilteredEntryBiMap) ? filterFiltered((FilteredEntryBiMap)paramBiMap, paramPredicate) : new FilteredEntryBiMap(paramBiMap, paramPredicate);
  }
  
  private static Map filterFiltered(AbstractFilteredMap paramAbstractFilteredMap, Predicate paramPredicate)
  {
    Predicate localPredicate = Predicates.and(paramAbstractFilteredMap.predicate, paramPredicate);
    return new FilteredEntryMap(paramAbstractFilteredMap.unfiltered, localPredicate);
  }
  
  private static SortedMap filterFiltered(FilteredEntrySortedMap paramFilteredEntrySortedMap, Predicate paramPredicate)
  {
    Predicate localPredicate = Predicates.and(paramFilteredEntrySortedMap.predicate, paramPredicate);
    return new FilteredEntrySortedMap(paramFilteredEntrySortedMap.sortedMap(), localPredicate);
  }
  
  @GwtIncompatible("NavigableMap")
  private static NavigableMap filterFiltered(FilteredEntryNavigableMap paramFilteredEntryNavigableMap, Predicate paramPredicate)
  {
    Predicate localPredicate = Predicates.and(paramFilteredEntryNavigableMap.predicate, paramPredicate);
    return new FilteredEntryNavigableMap(paramFilteredEntryNavigableMap.sortedMap(), localPredicate);
  }
  
  private static BiMap filterFiltered(FilteredEntryBiMap paramFilteredEntryBiMap, Predicate paramPredicate)
  {
    Predicate localPredicate = Predicates.and(paramFilteredEntryBiMap.predicate, paramPredicate);
    return new FilteredEntryBiMap(paramFilteredEntryBiMap.unfiltered(), localPredicate);
  }
  
  @GwtIncompatible("NavigableMap")
  public static NavigableMap unmodifiableNavigableMap(NavigableMap paramNavigableMap)
  {
    Preconditions.checkNotNull(paramNavigableMap);
    if ((paramNavigableMap instanceof UnmodifiableNavigableMap)) {
      return paramNavigableMap;
    }
    return new UnmodifiableNavigableMap(paramNavigableMap);
  }
  
  private static Map.Entry unmodifiableOrNull(Map.Entry paramEntry)
  {
    return paramEntry == null ? null : unmodifiableEntry(paramEntry);
  }
  
  @GwtIncompatible("NavigableMap")
  public static NavigableMap synchronizedNavigableMap(NavigableMap paramNavigableMap)
  {
    return Synchronized.navigableMap(paramNavigableMap);
  }
  
  static Object safeGet(Map paramMap, Object paramObject)
  {
    Preconditions.checkNotNull(paramMap);
    try
    {
      return paramMap.get(paramObject);
    }
    catch (ClassCastException localClassCastException)
    {
      return null;
    }
    catch (NullPointerException localNullPointerException) {}
    return null;
  }
  
  static boolean safeContainsKey(Map paramMap, Object paramObject)
  {
    Preconditions.checkNotNull(paramMap);
    try
    {
      return paramMap.containsKey(paramObject);
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    catch (NullPointerException localNullPointerException) {}
    return false;
  }
  
  static Object safeRemove(Map paramMap, Object paramObject)
  {
    Preconditions.checkNotNull(paramMap);
    try
    {
      return paramMap.remove(paramObject);
    }
    catch (ClassCastException localClassCastException)
    {
      return null;
    }
    catch (NullPointerException localNullPointerException) {}
    return null;
  }
  
  static boolean containsEntryImpl(Collection paramCollection, Object paramObject)
  {
    if (!(paramObject instanceof Map.Entry)) {
      return false;
    }
    return paramCollection.contains(unmodifiableEntry((Map.Entry)paramObject));
  }
  
  static boolean removeEntryImpl(Collection paramCollection, Object paramObject)
  {
    if (!(paramObject instanceof Map.Entry)) {
      return false;
    }
    return paramCollection.remove(unmodifiableEntry((Map.Entry)paramObject));
  }
  
  static boolean equalsImpl(Map paramMap, Object paramObject)
  {
    if (paramMap == paramObject) {
      return true;
    }
    if ((paramObject instanceof Map))
    {
      Map localMap = (Map)paramObject;
      return paramMap.entrySet().equals(localMap.entrySet());
    }
    return false;
  }
  
  static String toStringImpl(Map paramMap)
  {
    StringBuilder localStringBuilder = Collections2.newStringBuilderForCollection(paramMap.size()).append('{');
    STANDARD_JOINER.appendTo(localStringBuilder, paramMap);
    return '}';
  }
  
  static void putAllImpl(Map paramMap1, Map paramMap2)
  {
    Iterator localIterator = paramMap2.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      paramMap1.put(localEntry.getKey(), localEntry.getValue());
    }
  }
  
  static boolean containsKeyImpl(Map paramMap, Object paramObject)
  {
    return Iterators.contains(keyIterator(paramMap.entrySet().iterator()), paramObject);
  }
  
  static boolean containsValueImpl(Map paramMap, Object paramObject)
  {
    return Iterators.contains(valueIterator(paramMap.entrySet().iterator()), paramObject);
  }
  
  static Iterator keyIterator(Iterator paramIterator)
  {
    new TransformedIterator(paramIterator)
    {
      Object transform(Map.Entry paramAnonymousEntry)
      {
        return paramAnonymousEntry.getKey();
      }
    };
  }
  
  static Object keyOrNull(Map.Entry paramEntry)
  {
    return paramEntry == null ? null : paramEntry.getKey();
  }
  
  static Object valueOrNull(Map.Entry paramEntry)
  {
    return paramEntry == null ? null : paramEntry.getValue();
  }
  
  static Iterator valueIterator(Iterator paramIterator)
  {
    new TransformedIterator(paramIterator)
    {
      Object transform(Map.Entry paramAnonymousEntry)
      {
        return paramAnonymousEntry.getValue();
      }
    };
  }
  
  static UnmodifiableIterator valueIterator(UnmodifiableIterator paramUnmodifiableIterator)
  {
    new UnmodifiableIterator()
    {
      public boolean hasNext()
      {
        return this.val$entryIterator.hasNext();
      }
      
      public Object next()
      {
        return ((Map.Entry)this.val$entryIterator.next()).getValue();
      }
    };
  }
  
  @GwtIncompatible("NavigableMap")
  static abstract class DescendingMap
    extends ForwardingMap
    implements NavigableMap
  {
    private transient Comparator comparator;
    private transient Set entrySet;
    private transient NavigableSet navigableKeySet;
    
    abstract NavigableMap forward();
    
    protected final Map delegate()
    {
      return forward();
    }
    
    public Comparator comparator()
    {
      Object localObject1 = this.comparator;
      if (localObject1 == null)
      {
        Object localObject2 = forward().comparator();
        if (localObject2 == null) {
          localObject2 = Ordering.natural();
        }
        localObject1 = this.comparator = reverse((Comparator)localObject2);
      }
      return (Comparator)localObject1;
    }
    
    private static Ordering reverse(Comparator paramComparator)
    {
      return Ordering.from(paramComparator).reverse();
    }
    
    public Object firstKey()
    {
      return forward().lastKey();
    }
    
    public Object lastKey()
    {
      return forward().firstKey();
    }
    
    public Map.Entry lowerEntry(Object paramObject)
    {
      return forward().higherEntry(paramObject);
    }
    
    public Object lowerKey(Object paramObject)
    {
      return forward().higherKey(paramObject);
    }
    
    public Map.Entry floorEntry(Object paramObject)
    {
      return forward().ceilingEntry(paramObject);
    }
    
    public Object floorKey(Object paramObject)
    {
      return forward().ceilingKey(paramObject);
    }
    
    public Map.Entry ceilingEntry(Object paramObject)
    {
      return forward().floorEntry(paramObject);
    }
    
    public Object ceilingKey(Object paramObject)
    {
      return forward().floorKey(paramObject);
    }
    
    public Map.Entry higherEntry(Object paramObject)
    {
      return forward().lowerEntry(paramObject);
    }
    
    public Object higherKey(Object paramObject)
    {
      return forward().lowerKey(paramObject);
    }
    
    public Map.Entry firstEntry()
    {
      return forward().lastEntry();
    }
    
    public Map.Entry lastEntry()
    {
      return forward().firstEntry();
    }
    
    public Map.Entry pollFirstEntry()
    {
      return forward().pollLastEntry();
    }
    
    public Map.Entry pollLastEntry()
    {
      return forward().pollFirstEntry();
    }
    
    public NavigableMap descendingMap()
    {
      return forward();
    }
    
    public Set entrySet()
    {
      Set localSet = this.entrySet;
      return localSet == null ? (this.entrySet = createEntrySet()) : localSet;
    }
    
    abstract Iterator entryIterator();
    
    Set createEntrySet()
    {
      new Maps.EntrySet()
      {
        Map map()
        {
          return Maps.DescendingMap.this;
        }
        
        public Iterator iterator()
        {
          return Maps.DescendingMap.this.entryIterator();
        }
      };
    }
    
    public Set keySet()
    {
      return navigableKeySet();
    }
    
    public NavigableSet navigableKeySet()
    {
      NavigableSet localNavigableSet = this.navigableKeySet;
      return localNavigableSet == null ? (this.navigableKeySet = new Maps.NavigableKeySet(this)) : localNavigableSet;
    }
    
    public NavigableSet descendingKeySet()
    {
      return forward().navigableKeySet();
    }
    
    public NavigableMap subMap(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
    {
      return forward().subMap(paramObject2, paramBoolean2, paramObject1, paramBoolean1).descendingMap();
    }
    
    public NavigableMap headMap(Object paramObject, boolean paramBoolean)
    {
      return forward().tailMap(paramObject, paramBoolean).descendingMap();
    }
    
    public NavigableMap tailMap(Object paramObject, boolean paramBoolean)
    {
      return forward().headMap(paramObject, paramBoolean).descendingMap();
    }
    
    public SortedMap subMap(Object paramObject1, Object paramObject2)
    {
      return subMap(paramObject1, true, paramObject2, false);
    }
    
    public SortedMap headMap(Object paramObject)
    {
      return headMap(paramObject, false);
    }
    
    public SortedMap tailMap(Object paramObject)
    {
      return tailMap(paramObject, true);
    }
    
    public Collection values()
    {
      new Maps.Values()
      {
        Map map()
        {
          return Maps.DescendingMap.this;
        }
      };
    }
  }
  
  static abstract class EntrySet
    extends Sets.ImprovedAbstractSet
  {
    abstract Map map();
    
    public int size()
    {
      return map().size();
    }
    
    public void clear()
    {
      map().clear();
    }
    
    public boolean contains(Object paramObject)
    {
      if ((paramObject instanceof Map.Entry))
      {
        Map.Entry localEntry = (Map.Entry)paramObject;
        Object localObject1 = localEntry.getKey();
        Object localObject2 = map().get(localObject1);
        return (Objects.equal(localObject2, localEntry.getValue())) && ((localObject2 != null) || (map().containsKey(localObject1)));
      }
      return false;
    }
    
    public boolean isEmpty()
    {
      return map().isEmpty();
    }
    
    public boolean remove(Object paramObject)
    {
      if (contains(paramObject))
      {
        Map.Entry localEntry = (Map.Entry)paramObject;
        return map().keySet().remove(localEntry.getKey());
      }
      return false;
    }
    
    public boolean removeAll(Collection paramCollection)
    {
      try
      {
        return super.removeAll((Collection)Preconditions.checkNotNull(paramCollection));
      }
      catch (UnsupportedOperationException localUnsupportedOperationException)
      {
        boolean bool = true;
        Iterator localIterator = paramCollection.iterator();
        while (localIterator.hasNext())
        {
          Object localObject = localIterator.next();
          bool |= remove(localObject);
        }
        return bool;
      }
    }
    
    public boolean retainAll(Collection paramCollection)
    {
      try
      {
        return super.retainAll((Collection)Preconditions.checkNotNull(paramCollection));
      }
      catch (UnsupportedOperationException localUnsupportedOperationException)
      {
        HashSet localHashSet = Sets.newHashSetWithExpectedSize(paramCollection.size());
        Iterator localIterator = paramCollection.iterator();
        while (localIterator.hasNext())
        {
          Object localObject = localIterator.next();
          if (contains(localObject))
          {
            Map.Entry localEntry = (Map.Entry)localObject;
            localHashSet.add(localEntry.getKey());
          }
        }
        return map().keySet().retainAll(localHashSet);
      }
    }
  }
  
  static abstract class Values
    extends AbstractCollection
  {
    abstract Map map();
    
    public Iterator iterator()
    {
      return Maps.valueIterator(map().entrySet().iterator());
    }
    
    public boolean remove(Object paramObject)
    {
      try
      {
        return super.remove(paramObject);
      }
      catch (UnsupportedOperationException localUnsupportedOperationException)
      {
        Iterator localIterator = map().entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if (Objects.equal(paramObject, localEntry.getValue()))
          {
            map().remove(localEntry.getKey());
            return true;
          }
        }
      }
      return false;
    }
    
    public boolean removeAll(Collection paramCollection)
    {
      try
      {
        return super.removeAll((Collection)Preconditions.checkNotNull(paramCollection));
      }
      catch (UnsupportedOperationException localUnsupportedOperationException)
      {
        HashSet localHashSet = Sets.newHashSet();
        Iterator localIterator = map().entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if (paramCollection.contains(localEntry.getValue())) {
            localHashSet.add(localEntry.getKey());
          }
        }
        return map().keySet().removeAll(localHashSet);
      }
    }
    
    public boolean retainAll(Collection paramCollection)
    {
      try
      {
        return super.retainAll((Collection)Preconditions.checkNotNull(paramCollection));
      }
      catch (UnsupportedOperationException localUnsupportedOperationException)
      {
        HashSet localHashSet = Sets.newHashSet();
        Iterator localIterator = map().entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if (paramCollection.contains(localEntry.getValue())) {
            localHashSet.add(localEntry.getKey());
          }
        }
        return map().keySet().retainAll(localHashSet);
      }
    }
    
    public int size()
    {
      return map().size();
    }
    
    public boolean isEmpty()
    {
      return map().isEmpty();
    }
    
    public boolean contains(Object paramObject)
    {
      return map().containsValue(paramObject);
    }
    
    public void clear()
    {
      map().clear();
    }
  }
  
  @GwtIncompatible("NavigableMap")
  static class NavigableKeySet
    extends Maps.KeySet
    implements NavigableSet
  {
    private final NavigableMap map;
    
    NavigableKeySet(NavigableMap paramNavigableMap)
    {
      this.map = ((NavigableMap)Preconditions.checkNotNull(paramNavigableMap));
    }
    
    NavigableMap map()
    {
      return this.map;
    }
    
    public Comparator comparator()
    {
      return map().comparator();
    }
    
    public Object first()
    {
      return map().firstKey();
    }
    
    public Object last()
    {
      return map().lastKey();
    }
    
    public Object lower(Object paramObject)
    {
      return map().lowerKey(paramObject);
    }
    
    public Object floor(Object paramObject)
    {
      return map().floorKey(paramObject);
    }
    
    public Object ceiling(Object paramObject)
    {
      return map().ceilingKey(paramObject);
    }
    
    public Object higher(Object paramObject)
    {
      return map().higherKey(paramObject);
    }
    
    public Object pollFirst()
    {
      return Maps.keyOrNull(map().pollFirstEntry());
    }
    
    public Object pollLast()
    {
      return Maps.keyOrNull(map().pollLastEntry());
    }
    
    public NavigableSet descendingSet()
    {
      return map().descendingKeySet();
    }
    
    public Iterator descendingIterator()
    {
      return descendingSet().iterator();
    }
    
    public NavigableSet subSet(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
    {
      return map().subMap(paramObject1, paramBoolean1, paramObject2, paramBoolean2).navigableKeySet();
    }
    
    public NavigableSet headSet(Object paramObject, boolean paramBoolean)
    {
      return map().headMap(paramObject, paramBoolean).navigableKeySet();
    }
    
    public NavigableSet tailSet(Object paramObject, boolean paramBoolean)
    {
      return map().tailMap(paramObject, paramBoolean).navigableKeySet();
    }
    
    public SortedSet subSet(Object paramObject1, Object paramObject2)
    {
      return subSet(paramObject1, true, paramObject2, false);
    }
    
    public SortedSet headSet(Object paramObject)
    {
      return headSet(paramObject, false);
    }
    
    public SortedSet tailSet(Object paramObject)
    {
      return tailSet(paramObject, true);
    }
  }
  
  static abstract class KeySet
    extends Sets.ImprovedAbstractSet
  {
    abstract Map map();
    
    public Iterator iterator()
    {
      return Maps.keyIterator(map().entrySet().iterator());
    }
    
    public int size()
    {
      return map().size();
    }
    
    public boolean isEmpty()
    {
      return map().isEmpty();
    }
    
    public boolean contains(Object paramObject)
    {
      return map().containsKey(paramObject);
    }
    
    public boolean remove(Object paramObject)
    {
      if (contains(paramObject))
      {
        map().remove(paramObject);
        return true;
      }
      return false;
    }
    
    public void clear()
    {
      map().clear();
    }
  }
  
  @GwtCompatible
  static abstract class ImprovedAbstractMap
    extends AbstractMap
  {
    private Set entrySet;
    private Set keySet;
    private Collection values;
    
    protected abstract Set createEntrySet();
    
    public Set entrySet()
    {
      Set localSet = this.entrySet;
      if (localSet == null) {
        this.entrySet = (localSet = createEntrySet());
      }
      return localSet;
    }
    
    public Set keySet()
    {
      Set localSet = this.keySet;
      if (localSet == null) {
        this. = new Maps.KeySet()
        {
          Map map()
          {
            return Maps.ImprovedAbstractMap.this;
          }
        };
      }
      return localSet;
    }
    
    public Collection values()
    {
      Collection localCollection = this.values;
      if (localCollection == null) {
        this. = new Maps.Values()
        {
          Map map()
          {
            return Maps.ImprovedAbstractMap.this;
          }
        };
      }
      return localCollection;
    }
  }
  
  @GwtIncompatible("NavigableMap")
  static class UnmodifiableNavigableMap
    extends ForwardingSortedMap
    implements Serializable, NavigableMap
  {
    private final NavigableMap delegate;
    private transient UnmodifiableNavigableMap descendingMap;
    
    UnmodifiableNavigableMap(NavigableMap paramNavigableMap)
    {
      this.delegate = paramNavigableMap;
    }
    
    protected SortedMap delegate()
    {
      return Collections.unmodifiableSortedMap(this.delegate);
    }
    
    public Map.Entry lowerEntry(Object paramObject)
    {
      return Maps.unmodifiableOrNull(this.delegate.lowerEntry(paramObject));
    }
    
    public Object lowerKey(Object paramObject)
    {
      return this.delegate.lowerKey(paramObject);
    }
    
    public Map.Entry floorEntry(Object paramObject)
    {
      return Maps.unmodifiableOrNull(this.delegate.floorEntry(paramObject));
    }
    
    public Object floorKey(Object paramObject)
    {
      return this.delegate.floorKey(paramObject);
    }
    
    public Map.Entry ceilingEntry(Object paramObject)
    {
      return Maps.unmodifiableOrNull(this.delegate.ceilingEntry(paramObject));
    }
    
    public Object ceilingKey(Object paramObject)
    {
      return this.delegate.ceilingKey(paramObject);
    }
    
    public Map.Entry higherEntry(Object paramObject)
    {
      return Maps.unmodifiableOrNull(this.delegate.higherEntry(paramObject));
    }
    
    public Object higherKey(Object paramObject)
    {
      return this.delegate.higherKey(paramObject);
    }
    
    public Map.Entry firstEntry()
    {
      return Maps.unmodifiableOrNull(this.delegate.firstEntry());
    }
    
    public Map.Entry lastEntry()
    {
      return Maps.unmodifiableOrNull(this.delegate.lastEntry());
    }
    
    public final Map.Entry pollFirstEntry()
    {
      throw new UnsupportedOperationException();
    }
    
    public final Map.Entry pollLastEntry()
    {
      throw new UnsupportedOperationException();
    }
    
    public NavigableMap descendingMap()
    {
      UnmodifiableNavigableMap localUnmodifiableNavigableMap = this.descendingMap;
      if (localUnmodifiableNavigableMap == null)
      {
        this.descendingMap = (localUnmodifiableNavigableMap = new UnmodifiableNavigableMap(this.delegate.descendingMap()));
        localUnmodifiableNavigableMap.descendingMap = this;
      }
      return localUnmodifiableNavigableMap;
    }
    
    public Set keySet()
    {
      return navigableKeySet();
    }
    
    public NavigableSet navigableKeySet()
    {
      return Sets.unmodifiableNavigableSet(this.delegate.navigableKeySet());
    }
    
    public NavigableSet descendingKeySet()
    {
      return Sets.unmodifiableNavigableSet(this.delegate.descendingKeySet());
    }
    
    public SortedMap subMap(Object paramObject1, Object paramObject2)
    {
      return subMap(paramObject1, true, paramObject2, false);
    }
    
    public SortedMap headMap(Object paramObject)
    {
      return headMap(paramObject, false);
    }
    
    public SortedMap tailMap(Object paramObject)
    {
      return tailMap(paramObject, true);
    }
    
    public NavigableMap subMap(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
    {
      return Maps.unmodifiableNavigableMap(this.delegate.subMap(paramObject1, paramBoolean1, paramObject2, paramBoolean2));
    }
    
    public NavigableMap headMap(Object paramObject, boolean paramBoolean)
    {
      return Maps.unmodifiableNavigableMap(this.delegate.headMap(paramObject, paramBoolean));
    }
    
    public NavigableMap tailMap(Object paramObject, boolean paramBoolean)
    {
      return Maps.unmodifiableNavigableMap(this.delegate.tailMap(paramObject, paramBoolean));
    }
  }
  
  static class FilteredEntryMap
    extends Maps.AbstractFilteredMap
  {
    final Set filteredEntrySet = Sets.filter(paramMap.entrySet(), this.predicate);
    Set entrySet;
    Set keySet;
    
    FilteredEntryMap(Map paramMap, Predicate paramPredicate)
    {
      super(paramPredicate);
    }
    
    public Set entrySet()
    {
      Set localSet = this.entrySet;
      return localSet == null ? (this.entrySet = new EntrySet(null)) : localSet;
    }
    
    public Set keySet()
    {
      Set localSet = this.keySet;
      return localSet == null ? (this.keySet = createKeySet()) : localSet;
    }
    
    Set createKeySet()
    {
      return new KeySet(null);
    }
    
    private class KeySet
      extends Sets.ImprovedAbstractSet
    {
      private KeySet() {}
      
      public Iterator iterator()
      {
        final Iterator localIterator = Maps.FilteredEntryMap.this.filteredEntrySet.iterator();
        new UnmodifiableIterator()
        {
          public boolean hasNext()
          {
            return localIterator.hasNext();
          }
          
          public Object next()
          {
            return ((Map.Entry)localIterator.next()).getKey();
          }
        };
      }
      
      public int size()
      {
        return Maps.FilteredEntryMap.this.filteredEntrySet.size();
      }
      
      public void clear()
      {
        Maps.FilteredEntryMap.this.filteredEntrySet.clear();
      }
      
      public boolean contains(Object paramObject)
      {
        return Maps.FilteredEntryMap.this.containsKey(paramObject);
      }
      
      public boolean remove(Object paramObject)
      {
        if (Maps.FilteredEntryMap.this.containsKey(paramObject))
        {
          Maps.FilteredEntryMap.this.unfiltered.remove(paramObject);
          return true;
        }
        return false;
      }
      
      public boolean retainAll(Collection paramCollection)
      {
        Preconditions.checkNotNull(paramCollection);
        boolean bool = false;
        Iterator localIterator = Maps.FilteredEntryMap.this.unfiltered.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if ((Maps.FilteredEntryMap.this.predicate.apply(localEntry)) && (!paramCollection.contains(localEntry.getKey())))
          {
            localIterator.remove();
            bool = true;
          }
        }
        return bool;
      }
      
      public Object[] toArray()
      {
        return Lists.newArrayList(iterator()).toArray();
      }
      
      public Object[] toArray(Object[] paramArrayOfObject)
      {
        return Lists.newArrayList(iterator()).toArray(paramArrayOfObject);
      }
    }
    
    private class EntrySet
      extends ForwardingSet
    {
      private EntrySet() {}
      
      protected Set delegate()
      {
        return Maps.FilteredEntryMap.this.filteredEntrySet;
      }
      
      public Iterator iterator()
      {
        final Iterator localIterator = Maps.FilteredEntryMap.this.filteredEntrySet.iterator();
        new UnmodifiableIterator()
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
                Preconditions.checkArgument(Maps.FilteredEntryMap.this.apply(localEntry.getKey(), paramAnonymous2Object));
                return super.setValue(paramAnonymous2Object);
              }
            };
          }
        };
      }
    }
  }
  
  private static class FilteredKeyMap
    extends Maps.AbstractFilteredMap
  {
    Predicate keyPredicate;
    Set entrySet;
    Set keySet;
    
    FilteredKeyMap(Map paramMap, Predicate paramPredicate1, Predicate paramPredicate2)
    {
      super(paramPredicate2);
      this.keyPredicate = paramPredicate1;
    }
    
    public Set entrySet()
    {
      Set localSet = this.entrySet;
      return localSet == null ? (this.entrySet = Sets.filter(this.unfiltered.entrySet(), this.predicate)) : localSet;
    }
    
    public Set keySet()
    {
      Set localSet = this.keySet;
      return localSet == null ? (this.keySet = Sets.filter(this.unfiltered.keySet(), this.keyPredicate)) : localSet;
    }
    
    public boolean containsKey(Object paramObject)
    {
      return (this.unfiltered.containsKey(paramObject)) && (this.keyPredicate.apply(paramObject));
    }
  }
  
  static final class FilteredEntryBiMap
    extends Maps.FilteredEntryMap
    implements BiMap
  {
    private final BiMap inverse;
    
    private static Predicate inversePredicate(Predicate paramPredicate)
    {
      new Predicate()
      {
        public boolean apply(Map.Entry paramAnonymousEntry)
        {
          return this.val$forwardPredicate.apply(Maps.immutableEntry(paramAnonymousEntry.getValue(), paramAnonymousEntry.getKey()));
        }
      };
    }
    
    FilteredEntryBiMap(BiMap paramBiMap, Predicate paramPredicate)
    {
      super(paramPredicate);
      this.inverse = new FilteredEntryBiMap(paramBiMap.inverse(), inversePredicate(paramPredicate), this);
    }
    
    private FilteredEntryBiMap(BiMap paramBiMap1, Predicate paramPredicate, BiMap paramBiMap2)
    {
      super(paramPredicate);
      this.inverse = paramBiMap2;
    }
    
    BiMap unfiltered()
    {
      return (BiMap)this.unfiltered;
    }
    
    public Object forcePut(Object paramObject1, Object paramObject2)
    {
      Preconditions.checkArgument(this.predicate.apply(Maps.immutableEntry(paramObject1, paramObject2)));
      return unfiltered().forcePut(paramObject1, paramObject2);
    }
    
    public BiMap inverse()
    {
      return this.inverse;
    }
    
    public Set values()
    {
      return this.inverse.keySet();
    }
  }
  
  @GwtIncompatible("NavigableMap")
  private static class FilteredEntryNavigableMap
    extends Maps.FilteredEntrySortedMap
    implements NavigableMap
  {
    FilteredEntryNavigableMap(NavigableMap paramNavigableMap, Predicate paramPredicate)
    {
      super(paramPredicate);
    }
    
    NavigableMap sortedMap()
    {
      return (NavigableMap)super.sortedMap();
    }
    
    public Map.Entry lowerEntry(Object paramObject)
    {
      return headMap(paramObject, false).lastEntry();
    }
    
    public Object lowerKey(Object paramObject)
    {
      return Maps.keyOrNull(lowerEntry(paramObject));
    }
    
    public Map.Entry floorEntry(Object paramObject)
    {
      return headMap(paramObject, true).lastEntry();
    }
    
    public Object floorKey(Object paramObject)
    {
      return Maps.keyOrNull(floorEntry(paramObject));
    }
    
    public Map.Entry ceilingEntry(Object paramObject)
    {
      return tailMap(paramObject, true).firstEntry();
    }
    
    public Object ceilingKey(Object paramObject)
    {
      return Maps.keyOrNull(ceilingEntry(paramObject));
    }
    
    public Map.Entry higherEntry(Object paramObject)
    {
      return tailMap(paramObject, false).firstEntry();
    }
    
    public Object higherKey(Object paramObject)
    {
      return Maps.keyOrNull(higherEntry(paramObject));
    }
    
    public Map.Entry firstEntry()
    {
      return (Map.Entry)Iterables.getFirst(entrySet(), null);
    }
    
    public Map.Entry lastEntry()
    {
      return (Map.Entry)Iterables.getFirst(descendingMap().entrySet(), null);
    }
    
    public Map.Entry pollFirstEntry()
    {
      return pollFirstSatisfyingEntry(sortedMap().entrySet().iterator());
    }
    
    public Map.Entry pollLastEntry()
    {
      return pollFirstSatisfyingEntry(sortedMap().descendingMap().entrySet().iterator());
    }
    
    Map.Entry pollFirstSatisfyingEntry(Iterator paramIterator)
    {
      while (paramIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramIterator.next();
        if (this.predicate.apply(localEntry))
        {
          paramIterator.remove();
          return localEntry;
        }
      }
      return null;
    }
    
    public NavigableMap descendingMap()
    {
      return Maps.filterEntries(sortedMap().descendingMap(), this.predicate);
    }
    
    public NavigableSet keySet()
    {
      return (NavigableSet)super.keySet();
    }
    
    NavigableSet createKeySet()
    {
      new Maps.NavigableKeySet(this)
      {
        public boolean removeAll(Collection paramAnonymousCollection)
        {
          boolean bool = false;
          Iterator localIterator = Maps.FilteredEntryNavigableMap.this.sortedMap().entrySet().iterator();
          while (localIterator.hasNext())
          {
            Map.Entry localEntry = (Map.Entry)localIterator.next();
            if ((paramAnonymousCollection.contains(localEntry.getKey())) && (Maps.FilteredEntryNavigableMap.this.predicate.apply(localEntry)))
            {
              localIterator.remove();
              bool = true;
            }
          }
          return bool;
        }
        
        public boolean retainAll(Collection paramAnonymousCollection)
        {
          boolean bool = false;
          Iterator localIterator = Maps.FilteredEntryNavigableMap.this.sortedMap().entrySet().iterator();
          while (localIterator.hasNext())
          {
            Map.Entry localEntry = (Map.Entry)localIterator.next();
            if ((!paramAnonymousCollection.contains(localEntry.getKey())) && (Maps.FilteredEntryNavigableMap.this.predicate.apply(localEntry)))
            {
              localIterator.remove();
              bool = true;
            }
          }
          return bool;
        }
      };
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
      return Maps.filterEntries(sortedMap().subMap(paramObject1, paramBoolean1, paramObject2, paramBoolean2), this.predicate);
    }
    
    public NavigableMap headMap(Object paramObject)
    {
      return headMap(paramObject, false);
    }
    
    public NavigableMap headMap(Object paramObject, boolean paramBoolean)
    {
      return Maps.filterEntries(sortedMap().headMap(paramObject, paramBoolean), this.predicate);
    }
    
    public NavigableMap tailMap(Object paramObject)
    {
      return tailMap(paramObject, true);
    }
    
    public NavigableMap tailMap(Object paramObject, boolean paramBoolean)
    {
      return Maps.filterEntries(sortedMap().tailMap(paramObject, paramBoolean), this.predicate);
    }
  }
  
  private static class FilteredEntrySortedMap
    extends Maps.FilteredEntryMap
    implements SortedMap
  {
    FilteredEntrySortedMap(SortedMap paramSortedMap, Predicate paramPredicate)
    {
      super(paramPredicate);
    }
    
    SortedMap sortedMap()
    {
      return (SortedMap)this.unfiltered;
    }
    
    public Comparator comparator()
    {
      return sortedMap().comparator();
    }
    
    public Object firstKey()
    {
      return keySet().iterator().next();
    }
    
    public Object lastKey()
    {
      Object localObject;
      for (SortedMap localSortedMap = sortedMap();; localSortedMap = sortedMap().headMap(localObject))
      {
        localObject = localSortedMap.lastKey();
        if (apply(localObject, this.unfiltered.get(localObject))) {
          return localObject;
        }
      }
    }
    
    public SortedMap headMap(Object paramObject)
    {
      return new FilteredEntrySortedMap(sortedMap().headMap(paramObject), this.predicate);
    }
    
    public SortedMap subMap(Object paramObject1, Object paramObject2)
    {
      return new FilteredEntrySortedMap(sortedMap().subMap(paramObject1, paramObject2), this.predicate);
    }
    
    public SortedMap tailMap(Object paramObject)
    {
      return new FilteredEntrySortedMap(sortedMap().tailMap(paramObject), this.predicate);
    }
  }
  
  private static abstract class AbstractFilteredMap
    extends AbstractMap
  {
    final Map unfiltered;
    final Predicate predicate;
    Collection values;
    
    AbstractFilteredMap(Map paramMap, Predicate paramPredicate)
    {
      this.unfiltered = paramMap;
      this.predicate = paramPredicate;
    }
    
    boolean apply(Object paramObject1, Object paramObject2)
    {
      Object localObject = paramObject1;
      return this.predicate.apply(Maps.immutableEntry(localObject, paramObject2));
    }
    
    public Object put(Object paramObject1, Object paramObject2)
    {
      Preconditions.checkArgument(apply(paramObject1, paramObject2));
      return this.unfiltered.put(paramObject1, paramObject2);
    }
    
    public void putAll(Map paramMap)
    {
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Preconditions.checkArgument(apply(localEntry.getKey(), localEntry.getValue()));
      }
      this.unfiltered.putAll(paramMap);
    }
    
    public boolean containsKey(Object paramObject)
    {
      return (this.unfiltered.containsKey(paramObject)) && (apply(paramObject, this.unfiltered.get(paramObject)));
    }
    
    public Object get(Object paramObject)
    {
      Object localObject = this.unfiltered.get(paramObject);
      return (localObject != null) && (apply(paramObject, localObject)) ? localObject : null;
    }
    
    public boolean isEmpty()
    {
      return entrySet().isEmpty();
    }
    
    public Object remove(Object paramObject)
    {
      return containsKey(paramObject) ? this.unfiltered.remove(paramObject) : null;
    }
    
    public Collection values()
    {
      Collection localCollection = this.values;
      return localCollection == null ? (this.values = new Values()) : localCollection;
    }
    
    class Values
      extends AbstractCollection
    {
      Values() {}
      
      public Iterator iterator()
      {
        final Iterator localIterator = Maps.AbstractFilteredMap.this.entrySet().iterator();
        new UnmodifiableIterator()
        {
          public boolean hasNext()
          {
            return localIterator.hasNext();
          }
          
          public Object next()
          {
            return ((Map.Entry)localIterator.next()).getValue();
          }
        };
      }
      
      public int size()
      {
        return Maps.AbstractFilteredMap.this.entrySet().size();
      }
      
      public void clear()
      {
        Maps.AbstractFilteredMap.this.entrySet().clear();
      }
      
      public boolean isEmpty()
      {
        return Maps.AbstractFilteredMap.this.entrySet().isEmpty();
      }
      
      public boolean remove(Object paramObject)
      {
        Iterator localIterator = Maps.AbstractFilteredMap.this.unfiltered.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if ((Objects.equal(paramObject, localEntry.getValue())) && (Maps.AbstractFilteredMap.this.predicate.apply(localEntry)))
          {
            localIterator.remove();
            return true;
          }
        }
        return false;
      }
      
      public boolean removeAll(Collection paramCollection)
      {
        Preconditions.checkNotNull(paramCollection);
        boolean bool = false;
        Iterator localIterator = Maps.AbstractFilteredMap.this.unfiltered.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if ((paramCollection.contains(localEntry.getValue())) && (Maps.AbstractFilteredMap.this.predicate.apply(localEntry)))
          {
            localIterator.remove();
            bool = true;
          }
        }
        return bool;
      }
      
      public boolean retainAll(Collection paramCollection)
      {
        Preconditions.checkNotNull(paramCollection);
        boolean bool = false;
        Iterator localIterator = Maps.AbstractFilteredMap.this.unfiltered.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if ((!paramCollection.contains(localEntry.getValue())) && (Maps.AbstractFilteredMap.this.predicate.apply(localEntry)))
          {
            localIterator.remove();
            bool = true;
          }
        }
        return bool;
      }
      
      public Object[] toArray()
      {
        return Lists.newArrayList(iterator()).toArray();
      }
      
      public Object[] toArray(Object[] paramArrayOfObject)
      {
        return Lists.newArrayList(iterator()).toArray(paramArrayOfObject);
      }
    }
  }
  
  private static final class ValuePredicate
    implements Predicate
  {
    private final Predicate valuePredicate;
    
    ValuePredicate(Predicate paramPredicate)
    {
      this.valuePredicate = ((Predicate)Preconditions.checkNotNull(paramPredicate));
    }
    
    public boolean apply(Map.Entry paramEntry)
    {
      return this.valuePredicate.apply(paramEntry.getValue());
    }
  }
  
  private static final class KeyPredicate
    implements Predicate
  {
    private final Predicate keyPredicate;
    
    KeyPredicate(Predicate paramPredicate)
    {
      this.keyPredicate = ((Predicate)Preconditions.checkNotNull(paramPredicate));
    }
    
    public boolean apply(Map.Entry paramEntry)
    {
      return this.keyPredicate.apply(paramEntry.getKey());
    }
  }
  
  @GwtIncompatible("NavigableMap")
  private static class TransformedEntriesNavigableMap
    extends Maps.TransformedEntriesSortedMap
    implements NavigableMap
  {
    TransformedEntriesNavigableMap(NavigableMap paramNavigableMap, Maps.EntryTransformer paramEntryTransformer)
    {
      super(paramEntryTransformer);
    }
    
    public Map.Entry ceilingEntry(Object paramObject)
    {
      return transformEntry(fromMap().ceilingEntry(paramObject));
    }
    
    public Object ceilingKey(Object paramObject)
    {
      return fromMap().ceilingKey(paramObject);
    }
    
    public NavigableSet descendingKeySet()
    {
      return fromMap().descendingKeySet();
    }
    
    public NavigableMap descendingMap()
    {
      return Maps.transformEntries(fromMap().descendingMap(), this.transformer);
    }
    
    public Map.Entry firstEntry()
    {
      return transformEntry(fromMap().firstEntry());
    }
    
    public Map.Entry floorEntry(Object paramObject)
    {
      return transformEntry(fromMap().floorEntry(paramObject));
    }
    
    public Object floorKey(Object paramObject)
    {
      return fromMap().floorKey(paramObject);
    }
    
    public NavigableMap headMap(Object paramObject)
    {
      return headMap(paramObject, false);
    }
    
    public NavigableMap headMap(Object paramObject, boolean paramBoolean)
    {
      return Maps.transformEntries(fromMap().headMap(paramObject, paramBoolean), this.transformer);
    }
    
    public Map.Entry higherEntry(Object paramObject)
    {
      return transformEntry(fromMap().higherEntry(paramObject));
    }
    
    public Object higherKey(Object paramObject)
    {
      return fromMap().higherKey(paramObject);
    }
    
    public Map.Entry lastEntry()
    {
      return transformEntry(fromMap().lastEntry());
    }
    
    public Map.Entry lowerEntry(Object paramObject)
    {
      return transformEntry(fromMap().lowerEntry(paramObject));
    }
    
    public Object lowerKey(Object paramObject)
    {
      return fromMap().lowerKey(paramObject);
    }
    
    public NavigableSet navigableKeySet()
    {
      return fromMap().navigableKeySet();
    }
    
    public Map.Entry pollFirstEntry()
    {
      return transformEntry(fromMap().pollFirstEntry());
    }
    
    public Map.Entry pollLastEntry()
    {
      return transformEntry(fromMap().pollLastEntry());
    }
    
    public NavigableMap subMap(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
    {
      return Maps.transformEntries(fromMap().subMap(paramObject1, paramBoolean1, paramObject2, paramBoolean2), this.transformer);
    }
    
    public NavigableMap subMap(Object paramObject1, Object paramObject2)
    {
      return subMap(paramObject1, true, paramObject2, false);
    }
    
    public NavigableMap tailMap(Object paramObject)
    {
      return tailMap(paramObject, true);
    }
    
    public NavigableMap tailMap(Object paramObject, boolean paramBoolean)
    {
      return Maps.transformEntries(fromMap().tailMap(paramObject, paramBoolean), this.transformer);
    }
    
    private Map.Entry transformEntry(Map.Entry paramEntry)
    {
      if (paramEntry == null) {
        return null;
      }
      Object localObject1 = paramEntry.getKey();
      Object localObject2 = this.transformer.transformEntry(localObject1, paramEntry.getValue());
      return Maps.immutableEntry(localObject1, localObject2);
    }
    
    protected NavigableMap fromMap()
    {
      return (NavigableMap)super.fromMap();
    }
  }
  
  static class TransformedEntriesSortedMap
    extends Maps.TransformedEntriesMap
    implements SortedMap
  {
    protected SortedMap fromMap()
    {
      return (SortedMap)this.fromMap;
    }
    
    TransformedEntriesSortedMap(SortedMap paramSortedMap, Maps.EntryTransformer paramEntryTransformer)
    {
      super(paramEntryTransformer);
    }
    
    public Comparator comparator()
    {
      return fromMap().comparator();
    }
    
    public Object firstKey()
    {
      return fromMap().firstKey();
    }
    
    public SortedMap headMap(Object paramObject)
    {
      return Maps.transformEntries(fromMap().headMap(paramObject), this.transformer);
    }
    
    public Object lastKey()
    {
      return fromMap().lastKey();
    }
    
    public SortedMap subMap(Object paramObject1, Object paramObject2)
    {
      return Maps.transformEntries(fromMap().subMap(paramObject1, paramObject2), this.transformer);
    }
    
    public SortedMap tailMap(Object paramObject)
    {
      return Maps.transformEntries(fromMap().tailMap(paramObject), this.transformer);
    }
  }
  
  static class TransformedEntriesMap
    extends AbstractMap
  {
    final Map fromMap;
    final Maps.EntryTransformer transformer;
    Set entrySet;
    Collection values;
    
    TransformedEntriesMap(Map paramMap, Maps.EntryTransformer paramEntryTransformer)
    {
      this.fromMap = ((Map)Preconditions.checkNotNull(paramMap));
      this.transformer = ((Maps.EntryTransformer)Preconditions.checkNotNull(paramEntryTransformer));
    }
    
    public int size()
    {
      return this.fromMap.size();
    }
    
    public boolean containsKey(Object paramObject)
    {
      return this.fromMap.containsKey(paramObject);
    }
    
    public Object get(Object paramObject)
    {
      Object localObject = this.fromMap.get(paramObject);
      return (localObject != null) || (this.fromMap.containsKey(paramObject)) ? this.transformer.transformEntry(paramObject, localObject) : null;
    }
    
    public Object remove(Object paramObject)
    {
      return this.fromMap.containsKey(paramObject) ? this.transformer.transformEntry(paramObject, this.fromMap.remove(paramObject)) : null;
    }
    
    public void clear()
    {
      this.fromMap.clear();
    }
    
    public Set keySet()
    {
      return this.fromMap.keySet();
    }
    
    public Set entrySet()
    {
      Object localObject = this.entrySet;
      if (localObject == null) {
        this.entrySet = ( = new Maps.EntrySet()
        {
          Map map()
          {
            return Maps.TransformedEntriesMap.this;
          }
          
          public Iterator iterator()
          {
            new TransformedIterator(Maps.TransformedEntriesMap.this.fromMap.entrySet().iterator())
            {
              Map.Entry transform(final Map.Entry paramAnonymous2Entry)
              {
                new AbstractMapEntry()
                {
                  public Object getKey()
                  {
                    return paramAnonymous2Entry.getKey();
                  }
                  
                  public Object getValue()
                  {
                    return Maps.TransformedEntriesMap.this.transformer.transformEntry(paramAnonymous2Entry.getKey(), paramAnonymous2Entry.getValue());
                  }
                };
              }
            };
          }
        });
      }
      return (Set)localObject;
    }
    
    public Collection values()
    {
      Collection localCollection = this.values;
      if (localCollection == null) {
        this. = new Maps.Values()
        {
          Map map()
          {
            return Maps.TransformedEntriesMap.this;
          }
        };
      }
      return localCollection;
    }
  }
  
  public static abstract interface EntryTransformer
  {
    public abstract Object transformEntry(Object paramObject1, Object paramObject2);
  }
  
  private static class UnmodifiableBiMap
    extends ForwardingMap
    implements BiMap, Serializable
  {
    final Map unmodifiableMap;
    final BiMap delegate;
    BiMap inverse;
    transient Set values;
    private static final long serialVersionUID = 0L;
    
    UnmodifiableBiMap(BiMap paramBiMap1, BiMap paramBiMap2)
    {
      this.unmodifiableMap = Collections.unmodifiableMap(paramBiMap1);
      this.delegate = paramBiMap1;
      this.inverse = paramBiMap2;
    }
    
    protected Map delegate()
    {
      return this.unmodifiableMap;
    }
    
    public Object forcePut(Object paramObject1, Object paramObject2)
    {
      throw new UnsupportedOperationException();
    }
    
    public BiMap inverse()
    {
      BiMap localBiMap = this.inverse;
      return localBiMap == null ? (this.inverse = new UnmodifiableBiMap(this.delegate.inverse(), this)) : localBiMap;
    }
    
    public Set values()
    {
      Set localSet = this.values;
      return localSet == null ? (this.values = Collections.unmodifiableSet(this.delegate.values())) : localSet;
    }
  }
  
  static class UnmodifiableEntrySet
    extends Maps.UnmodifiableEntries
    implements Set
  {
    UnmodifiableEntrySet(Set paramSet)
    {
      super();
    }
    
    public boolean equals(Object paramObject)
    {
      return Sets.equalsImpl(this, paramObject);
    }
    
    public int hashCode()
    {
      return Sets.hashCodeImpl(this);
    }
  }
  
  static class UnmodifiableEntries
    extends ForwardingCollection
  {
    private final Collection entries;
    
    UnmodifiableEntries(Collection paramCollection)
    {
      this.entries = paramCollection;
    }
    
    protected Collection delegate()
    {
      return this.entries;
    }
    
    public Iterator iterator()
    {
      final Iterator localIterator = super.iterator();
      new ForwardingIterator()
      {
        public Map.Entry next()
        {
          return Maps.unmodifiableEntry((Map.Entry)super.next());
        }
        
        public void remove()
        {
          throw new UnsupportedOperationException();
        }
        
        protected Iterator delegate()
        {
          return localIterator;
        }
      };
    }
    
    public boolean add(Map.Entry paramEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(Collection paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public void clear()
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean removeAll(Collection paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public Object[] toArray()
    {
      return standardToArray();
    }
    
    public Object[] toArray(Object[] paramArrayOfObject)
    {
      return standardToArray(paramArrayOfObject);
    }
  }
  
  @GwtIncompatible("NavigableMap")
  private static final class NavigableAsMapView
    extends AbstractNavigableMap
  {
    private final NavigableSet set;
    private final Function function;
    
    NavigableAsMapView(NavigableSet paramNavigableSet, Function paramFunction)
    {
      this.set = ((NavigableSet)Preconditions.checkNotNull(paramNavigableSet));
      this.function = ((Function)Preconditions.checkNotNull(paramFunction));
    }
    
    public NavigableMap subMap(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
    {
      return Maps.asMap(this.set.subSet(paramObject1, paramBoolean1, paramObject2, paramBoolean2), this.function);
    }
    
    public NavigableMap headMap(Object paramObject, boolean paramBoolean)
    {
      return Maps.asMap(this.set.headSet(paramObject, paramBoolean), this.function);
    }
    
    public NavigableMap tailMap(Object paramObject, boolean paramBoolean)
    {
      return Maps.asMap(this.set.tailSet(paramObject, paramBoolean), this.function);
    }
    
    public Comparator comparator()
    {
      return this.set.comparator();
    }
    
    public Object get(Object paramObject)
    {
      if (this.set.contains(paramObject))
      {
        Object localObject = paramObject;
        return this.function.apply(localObject);
      }
      return null;
    }
    
    public void clear()
    {
      this.set.clear();
    }
    
    Iterator entryIterator()
    {
      return Maps.asSetEntryIterator(this.set, this.function);
    }
    
    Iterator descendingEntryIterator()
    {
      return descendingMap().entrySet().iterator();
    }
    
    public NavigableSet navigableKeySet()
    {
      return Maps.removeOnlyNavigableSet(this.set);
    }
    
    public int size()
    {
      return this.set.size();
    }
    
    public NavigableMap descendingMap()
    {
      return Maps.asMap(this.set.descendingSet(), this.function);
    }
  }
  
  private static class SortedAsMapView
    extends Maps.AsMapView
    implements SortedMap
  {
    SortedAsMapView(SortedSet paramSortedSet, Function paramFunction)
    {
      super(paramFunction);
    }
    
    SortedSet backingSet()
    {
      return (SortedSet)super.backingSet();
    }
    
    public Comparator comparator()
    {
      return backingSet().comparator();
    }
    
    public Set keySet()
    {
      return Maps.removeOnlySortedSet(backingSet());
    }
    
    public SortedMap subMap(Object paramObject1, Object paramObject2)
    {
      return Maps.asMap(backingSet().subSet(paramObject1, paramObject2), this.function);
    }
    
    public SortedMap headMap(Object paramObject)
    {
      return Maps.asMap(backingSet().headSet(paramObject), this.function);
    }
    
    public SortedMap tailMap(Object paramObject)
    {
      return Maps.asMap(backingSet().tailSet(paramObject), this.function);
    }
    
    public Object firstKey()
    {
      return backingSet().first();
    }
    
    public Object lastKey()
    {
      return backingSet().last();
    }
  }
  
  private static class AsMapView
    extends Maps.ImprovedAbstractMap
  {
    private final Set set;
    final Function function;
    
    Set backingSet()
    {
      return this.set;
    }
    
    AsMapView(Set paramSet, Function paramFunction)
    {
      this.set = ((Set)Preconditions.checkNotNull(paramSet));
      this.function = ((Function)Preconditions.checkNotNull(paramFunction));
    }
    
    public Set keySet()
    {
      return Maps.removeOnlySet(backingSet());
    }
    
    public Collection values()
    {
      return Collections2.transform(this.set, this.function);
    }
    
    public int size()
    {
      return backingSet().size();
    }
    
    public boolean containsKey(Object paramObject)
    {
      return backingSet().contains(paramObject);
    }
    
    public Object get(Object paramObject)
    {
      if (backingSet().contains(paramObject))
      {
        Object localObject = paramObject;
        return this.function.apply(localObject);
      }
      return null;
    }
    
    public Object remove(Object paramObject)
    {
      if (backingSet().remove(paramObject))
      {
        Object localObject = paramObject;
        return this.function.apply(localObject);
      }
      return null;
    }
    
    public void clear()
    {
      backingSet().clear();
    }
    
    protected Set createEntrySet()
    {
      new Maps.EntrySet()
      {
        Map map()
        {
          return Maps.AsMapView.this;
        }
        
        public Iterator iterator()
        {
          return Maps.asSetEntryIterator(Maps.AsMapView.this.backingSet(), Maps.AsMapView.this.function);
        }
      };
    }
  }
  
  static class SortedMapDifferenceImpl
    extends Maps.MapDifferenceImpl
    implements SortedMapDifference
  {
    SortedMapDifferenceImpl(boolean paramBoolean, SortedMap paramSortedMap1, SortedMap paramSortedMap2, SortedMap paramSortedMap3, SortedMap paramSortedMap4)
    {
      super(paramSortedMap1, paramSortedMap2, paramSortedMap3, paramSortedMap4);
    }
    
    public SortedMap entriesDiffering()
    {
      return (SortedMap)super.entriesDiffering();
    }
    
    public SortedMap entriesInCommon()
    {
      return (SortedMap)super.entriesInCommon();
    }
    
    public SortedMap entriesOnlyOnLeft()
    {
      return (SortedMap)super.entriesOnlyOnLeft();
    }
    
    public SortedMap entriesOnlyOnRight()
    {
      return (SortedMap)super.entriesOnlyOnRight();
    }
  }
  
  static class ValueDifferenceImpl
    implements MapDifference.ValueDifference
  {
    private final Object left;
    private final Object right;
    
    static MapDifference.ValueDifference create(Object paramObject1, Object paramObject2)
    {
      return new ValueDifferenceImpl(paramObject1, paramObject2);
    }
    
    private ValueDifferenceImpl(Object paramObject1, Object paramObject2)
    {
      this.left = paramObject1;
      this.right = paramObject2;
    }
    
    public Object leftValue()
    {
      return this.left;
    }
    
    public Object rightValue()
    {
      return this.right;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof MapDifference.ValueDifference))
      {
        MapDifference.ValueDifference localValueDifference = (MapDifference.ValueDifference)paramObject;
        return (Objects.equal(this.left, localValueDifference.leftValue())) && (Objects.equal(this.right, localValueDifference.rightValue()));
      }
      return false;
    }
    
    public int hashCode()
    {
      return Objects.hashCode(new Object[] { this.left, this.right });
    }
    
    public String toString()
    {
      return "(" + this.left + ", " + this.right + ")";
    }
  }
  
  static class MapDifferenceImpl
    implements MapDifference
  {
    final boolean areEqual;
    final Map onlyOnLeft;
    final Map onlyOnRight;
    final Map onBoth;
    final Map differences;
    
    MapDifferenceImpl(boolean paramBoolean, Map paramMap1, Map paramMap2, Map paramMap3, Map paramMap4)
    {
      this.areEqual = paramBoolean;
      this.onlyOnLeft = paramMap1;
      this.onlyOnRight = paramMap2;
      this.onBoth = paramMap3;
      this.differences = paramMap4;
    }
    
    public boolean areEqual()
    {
      return this.areEqual;
    }
    
    public Map entriesOnlyOnLeft()
    {
      return this.onlyOnLeft;
    }
    
    public Map entriesOnlyOnRight()
    {
      return this.onlyOnRight;
    }
    
    public Map entriesInCommon()
    {
      return this.onBoth;
    }
    
    public Map entriesDiffering()
    {
      return this.differences;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof MapDifference))
      {
        MapDifference localMapDifference = (MapDifference)paramObject;
        return (entriesOnlyOnLeft().equals(localMapDifference.entriesOnlyOnLeft())) && (entriesOnlyOnRight().equals(localMapDifference.entriesOnlyOnRight())) && (entriesInCommon().equals(localMapDifference.entriesInCommon())) && (entriesDiffering().equals(localMapDifference.entriesDiffering()));
      }
      return false;
    }
    
    public int hashCode()
    {
      return Objects.hashCode(new Object[] { entriesOnlyOnLeft(), entriesOnlyOnRight(), entriesInCommon(), entriesDiffering() });
    }
    
    public String toString()
    {
      if (this.areEqual) {
        return "equal";
      }
      StringBuilder localStringBuilder = new StringBuilder("not equal");
      if (!this.onlyOnLeft.isEmpty()) {
        localStringBuilder.append(": only on left=").append(this.onlyOnLeft);
      }
      if (!this.onlyOnRight.isEmpty()) {
        localStringBuilder.append(": only on right=").append(this.onlyOnRight);
      }
      if (!this.differences.isEmpty()) {
        localStringBuilder.append(": value differences=").append(this.differences);
      }
      return localStringBuilder.toString();
    }
  }
  
  private static abstract enum EntryFunction
    implements Function
  {
    KEY,  VALUE;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Maps.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */