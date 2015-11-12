package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;

@GwtCompatible(serializable=true, emulated=true)
public abstract class ImmutableSortedMap
  extends ImmutableSortedMapFauxverideShim
  implements NavigableMap
{
  private static final Comparator NATURAL_ORDER = ;
  private static final ImmutableSortedMap NATURAL_EMPTY_MAP = new EmptyImmutableSortedMap(NATURAL_ORDER);
  private transient ImmutableSortedMap descendingMap;
  private static final long serialVersionUID = 0L;
  
  static ImmutableSortedMap emptyMap(Comparator paramComparator)
  {
    if (Ordering.natural().equals(paramComparator)) {
      return of();
    }
    return new EmptyImmutableSortedMap(paramComparator);
  }
  
  static ImmutableSortedMap fromSortedEntries(Comparator paramComparator, Collection paramCollection)
  {
    if (paramCollection.isEmpty()) {
      return emptyMap(paramComparator);
    }
    ImmutableList.Builder localBuilder1 = ImmutableList.builder();
    ImmutableList.Builder localBuilder2 = ImmutableList.builder();
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localBuilder1.add(localEntry.getKey());
      localBuilder2.add(localEntry.getValue());
    }
    return new RegularImmutableSortedMap(new RegularImmutableSortedSet(localBuilder1.build(), paramComparator), localBuilder2.build());
  }
  
  static ImmutableSortedMap from(ImmutableSortedSet paramImmutableSortedSet, ImmutableList paramImmutableList)
  {
    if (paramImmutableSortedSet.isEmpty()) {
      return emptyMap(paramImmutableSortedSet.comparator());
    }
    return new RegularImmutableSortedMap((RegularImmutableSortedSet)paramImmutableSortedSet, paramImmutableList);
  }
  
  public static ImmutableSortedMap of()
  {
    return NATURAL_EMPTY_MAP;
  }
  
  public static ImmutableSortedMap of(Comparable paramComparable, Object paramObject)
  {
    return from(ImmutableSortedSet.of(paramComparable), ImmutableList.of(paramObject));
  }
  
  public static ImmutableSortedMap of(Comparable paramComparable1, Object paramObject1, Comparable paramComparable2, Object paramObject2)
  {
    return new Builder(Ordering.natural()).put(paramComparable1, paramObject1).put(paramComparable2, paramObject2).build();
  }
  
  public static ImmutableSortedMap of(Comparable paramComparable1, Object paramObject1, Comparable paramComparable2, Object paramObject2, Comparable paramComparable3, Object paramObject3)
  {
    return new Builder(Ordering.natural()).put(paramComparable1, paramObject1).put(paramComparable2, paramObject2).put(paramComparable3, paramObject3).build();
  }
  
  public static ImmutableSortedMap of(Comparable paramComparable1, Object paramObject1, Comparable paramComparable2, Object paramObject2, Comparable paramComparable3, Object paramObject3, Comparable paramComparable4, Object paramObject4)
  {
    return new Builder(Ordering.natural()).put(paramComparable1, paramObject1).put(paramComparable2, paramObject2).put(paramComparable3, paramObject3).put(paramComparable4, paramObject4).build();
  }
  
  public static ImmutableSortedMap of(Comparable paramComparable1, Object paramObject1, Comparable paramComparable2, Object paramObject2, Comparable paramComparable3, Object paramObject3, Comparable paramComparable4, Object paramObject4, Comparable paramComparable5, Object paramObject5)
  {
    return new Builder(Ordering.natural()).put(paramComparable1, paramObject1).put(paramComparable2, paramObject2).put(paramComparable3, paramObject3).put(paramComparable4, paramObject4).put(paramComparable5, paramObject5).build();
  }
  
  public static ImmutableSortedMap copyOf(Map paramMap)
  {
    Ordering localOrdering = Ordering.natural();
    return copyOfInternal(paramMap, localOrdering);
  }
  
  public static ImmutableSortedMap copyOf(Map paramMap, Comparator paramComparator)
  {
    return copyOfInternal(paramMap, (Comparator)Preconditions.checkNotNull(paramComparator));
  }
  
  public static ImmutableSortedMap copyOfSorted(SortedMap paramSortedMap)
  {
    Comparator localComparator = paramSortedMap.comparator();
    if (localComparator == null) {
      localComparator = NATURAL_ORDER;
    }
    return copyOfInternal(paramSortedMap, localComparator);
  }
  
  private static ImmutableSortedMap copyOfInternal(Map paramMap, Comparator paramComparator)
  {
    boolean bool = false;
    if ((paramMap instanceof SortedMap))
    {
      localObject1 = (SortedMap)paramMap;
      Comparator localComparator = ((SortedMap)localObject1).comparator();
      bool = localComparator == null ? false : paramComparator == NATURAL_ORDER ? true : paramComparator.equals(localComparator);
    }
    if ((bool) && ((paramMap instanceof ImmutableSortedMap)))
    {
      localObject1 = (ImmutableSortedMap)paramMap;
      if (!((ImmutableSortedMap)localObject1).isPartialView()) {
        return (ImmutableSortedMap)localObject1;
      }
    }
    Object localObject1 = (Map.Entry[])paramMap.entrySet().toArray(new Map.Entry[0]);
    for (int i = 0; i < localObject1.length; i++)
    {
      Object localObject2 = localObject1[i];
      localObject1[i] = entryOf(((Map.Entry)localObject2).getKey(), ((Map.Entry)localObject2).getValue());
    }
    List localList = Arrays.asList((Object[])localObject1);
    if (!bool)
    {
      sortEntries(localList, paramComparator);
      validateEntries(localList, paramComparator);
    }
    return fromSortedEntries(paramComparator, localList);
  }
  
  private static void sortEntries(List paramList, Comparator paramComparator)
  {
    Comparator local1 = new Comparator()
    {
      public int compare(Map.Entry paramAnonymousEntry1, Map.Entry paramAnonymousEntry2)
      {
        return this.val$comparator.compare(paramAnonymousEntry1.getKey(), paramAnonymousEntry2.getKey());
      }
    };
    Collections.sort(paramList, local1);
  }
  
  private static void validateEntries(List paramList, Comparator paramComparator)
  {
    for (int i = 1; i < paramList.size(); i++) {
      if (paramComparator.compare(((Map.Entry)paramList.get(i - 1)).getKey(), ((Map.Entry)paramList.get(i)).getKey()) == 0) {
        throw new IllegalArgumentException("Duplicate keys in mappings " + paramList.get(i - 1) + " and " + paramList.get(i));
      }
    }
  }
  
  public static Builder naturalOrder()
  {
    return new Builder(Ordering.natural());
  }
  
  public static Builder orderedBy(Comparator paramComparator)
  {
    return new Builder(paramComparator);
  }
  
  public static Builder reverseOrder()
  {
    return new Builder(Ordering.natural().reverse());
  }
  
  ImmutableSortedMap() {}
  
  ImmutableSortedMap(ImmutableSortedMap paramImmutableSortedMap)
  {
    this.descendingMap = paramImmutableSortedMap;
  }
  
  public int size()
  {
    return values().size();
  }
  
  public boolean containsValue(Object paramObject)
  {
    return values().contains(paramObject);
  }
  
  boolean isPartialView()
  {
    return (keySet().isPartialView()) || (values().isPartialView());
  }
  
  public ImmutableSet entrySet()
  {
    return super.entrySet();
  }
  
  public abstract ImmutableSortedSet keySet();
  
  public abstract ImmutableCollection values();
  
  public Comparator comparator()
  {
    return keySet().comparator();
  }
  
  public Object firstKey()
  {
    return keySet().first();
  }
  
  public Object lastKey()
  {
    return keySet().last();
  }
  
  public ImmutableSortedMap headMap(Object paramObject)
  {
    return headMap(paramObject, false);
  }
  
  public abstract ImmutableSortedMap headMap(Object paramObject, boolean paramBoolean);
  
  public ImmutableSortedMap subMap(Object paramObject1, Object paramObject2)
  {
    return subMap(paramObject1, true, paramObject2, false);
  }
  
  public ImmutableSortedMap subMap(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
  {
    Preconditions.checkNotNull(paramObject1);
    Preconditions.checkNotNull(paramObject2);
    Preconditions.checkArgument(comparator().compare(paramObject1, paramObject2) <= 0, "expected fromKey <= toKey but %s > %s", new Object[] { paramObject1, paramObject2 });
    return headMap(paramObject2, paramBoolean2).tailMap(paramObject1, paramBoolean1);
  }
  
  public ImmutableSortedMap tailMap(Object paramObject)
  {
    return tailMap(paramObject, true);
  }
  
  public abstract ImmutableSortedMap tailMap(Object paramObject, boolean paramBoolean);
  
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
    return isEmpty() ? null : (Map.Entry)entrySet().asList().get(0);
  }
  
  public Map.Entry lastEntry()
  {
    return isEmpty() ? null : (Map.Entry)entrySet().asList().get(size() - 1);
  }
  
  @Deprecated
  public final Map.Entry pollFirstEntry()
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final Map.Entry pollLastEntry()
  {
    throw new UnsupportedOperationException();
  }
  
  public ImmutableSortedMap descendingMap()
  {
    ImmutableSortedMap localImmutableSortedMap = this.descendingMap;
    if (localImmutableSortedMap == null) {
      localImmutableSortedMap = this.descendingMap = createDescendingMap();
    }
    return localImmutableSortedMap;
  }
  
  abstract ImmutableSortedMap createDescendingMap();
  
  public ImmutableSortedSet navigableKeySet()
  {
    return keySet();
  }
  
  public ImmutableSortedSet descendingKeySet()
  {
    return keySet().descendingSet();
  }
  
  Object writeReplace()
  {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm
    extends ImmutableMap.SerializedForm
  {
    private final Comparator comparator;
    private static final long serialVersionUID = 0L;
    
    SerializedForm(ImmutableSortedMap paramImmutableSortedMap)
    {
      super();
      this.comparator = paramImmutableSortedMap.comparator();
    }
    
    Object readResolve()
    {
      ImmutableSortedMap.Builder localBuilder = new ImmutableSortedMap.Builder(this.comparator);
      return createMap(localBuilder);
    }
  }
  
  public static class Builder
    extends ImmutableMap.Builder
  {
    private final Comparator comparator;
    
    public Builder(Comparator paramComparator)
    {
      this.comparator = ((Comparator)Preconditions.checkNotNull(paramComparator));
    }
    
    public Builder put(Object paramObject1, Object paramObject2)
    {
      this.entries.add(ImmutableMap.entryOf(paramObject1, paramObject2));
      return this;
    }
    
    public Builder put(Map.Entry paramEntry)
    {
      super.put(paramEntry);
      return this;
    }
    
    public Builder putAll(Map paramMap)
    {
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        put(localEntry.getKey(), localEntry.getValue());
      }
      return this;
    }
    
    public ImmutableSortedMap build()
    {
      ImmutableSortedMap.sortEntries(this.entries, this.comparator);
      ImmutableSortedMap.validateEntries(this.entries, this.comparator);
      return ImmutableSortedMap.fromSortedEntries(this.comparator, this.entries);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableSortedMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */