package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

@Beta
@GwtCompatible
public final class MapConstraints
{
  public static MapConstraint notNull()
  {
    return NotNullMapConstraint.INSTANCE;
  }
  
  public static Map constrainedMap(Map paramMap, MapConstraint paramMapConstraint)
  {
    return new ConstrainedMap(paramMap, paramMapConstraint);
  }
  
  public static Multimap constrainedMultimap(Multimap paramMultimap, MapConstraint paramMapConstraint)
  {
    return new ConstrainedMultimap(paramMultimap, paramMapConstraint);
  }
  
  public static ListMultimap constrainedListMultimap(ListMultimap paramListMultimap, MapConstraint paramMapConstraint)
  {
    return new ConstrainedListMultimap(paramListMultimap, paramMapConstraint);
  }
  
  public static SetMultimap constrainedSetMultimap(SetMultimap paramSetMultimap, MapConstraint paramMapConstraint)
  {
    return new ConstrainedSetMultimap(paramSetMultimap, paramMapConstraint);
  }
  
  public static SortedSetMultimap constrainedSortedSetMultimap(SortedSetMultimap paramSortedSetMultimap, MapConstraint paramMapConstraint)
  {
    return new ConstrainedSortedSetMultimap(paramSortedSetMultimap, paramMapConstraint);
  }
  
  private static Map.Entry constrainedEntry(Map.Entry paramEntry, final MapConstraint paramMapConstraint)
  {
    Preconditions.checkNotNull(paramEntry);
    Preconditions.checkNotNull(paramMapConstraint);
    new ForwardingMapEntry()
    {
      protected Map.Entry delegate()
      {
        return this.val$entry;
      }
      
      public Object setValue(Object paramAnonymousObject)
      {
        paramMapConstraint.checkKeyValue(getKey(), paramAnonymousObject);
        return this.val$entry.setValue(paramAnonymousObject);
      }
    };
  }
  
  private static Map.Entry constrainedAsMapEntry(Map.Entry paramEntry, final MapConstraint paramMapConstraint)
  {
    Preconditions.checkNotNull(paramEntry);
    Preconditions.checkNotNull(paramMapConstraint);
    new ForwardingMapEntry()
    {
      protected Map.Entry delegate()
      {
        return this.val$entry;
      }
      
      public Collection getValue()
      {
        Constraints.constrainedTypePreservingCollection((Collection)this.val$entry.getValue(), new Constraint()
        {
          public Object checkElement(Object paramAnonymous2Object)
          {
            MapConstraints.2.this.val$constraint.checkKeyValue(MapConstraints.2.this.getKey(), paramAnonymous2Object);
            return paramAnonymous2Object;
          }
        });
      }
    };
  }
  
  private static Set constrainedAsMapEntries(Set paramSet, MapConstraint paramMapConstraint)
  {
    return new ConstrainedAsMapEntries(paramSet, paramMapConstraint);
  }
  
  private static Collection constrainedEntries(Collection paramCollection, MapConstraint paramMapConstraint)
  {
    if ((paramCollection instanceof Set)) {
      return constrainedEntrySet((Set)paramCollection, paramMapConstraint);
    }
    return new ConstrainedEntries(paramCollection, paramMapConstraint);
  }
  
  private static Set constrainedEntrySet(Set paramSet, MapConstraint paramMapConstraint)
  {
    return new ConstrainedEntrySet(paramSet, paramMapConstraint);
  }
  
  public static BiMap constrainedBiMap(BiMap paramBiMap, MapConstraint paramMapConstraint)
  {
    return new ConstrainedBiMap(paramBiMap, null, paramMapConstraint);
  }
  
  private static Collection checkValues(Object paramObject, Iterable paramIterable, MapConstraint paramMapConstraint)
  {
    ArrayList localArrayList = Lists.newArrayList(paramIterable);
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      paramMapConstraint.checkKeyValue(paramObject, localObject);
    }
    return localArrayList;
  }
  
  private static Map checkMap(Map paramMap, MapConstraint paramMapConstraint)
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap(paramMap);
    Iterator localIterator = localLinkedHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      paramMapConstraint.checkKeyValue(localEntry.getKey(), localEntry.getValue());
    }
    return localLinkedHashMap;
  }
  
  private static class ConstrainedSortedSetMultimap
    extends MapConstraints.ConstrainedSetMultimap
    implements SortedSetMultimap
  {
    ConstrainedSortedSetMultimap(SortedSetMultimap paramSortedSetMultimap, MapConstraint paramMapConstraint)
    {
      super(paramMapConstraint);
    }
    
    public SortedSet get(Object paramObject)
    {
      return (SortedSet)super.get(paramObject);
    }
    
    public SortedSet removeAll(Object paramObject)
    {
      return (SortedSet)super.removeAll(paramObject);
    }
    
    public SortedSet replaceValues(Object paramObject, Iterable paramIterable)
    {
      return (SortedSet)super.replaceValues(paramObject, paramIterable);
    }
    
    public Comparator valueComparator()
    {
      return ((SortedSetMultimap)delegate()).valueComparator();
    }
  }
  
  private static class ConstrainedSetMultimap
    extends MapConstraints.ConstrainedMultimap
    implements SetMultimap
  {
    ConstrainedSetMultimap(SetMultimap paramSetMultimap, MapConstraint paramMapConstraint)
    {
      super(paramMapConstraint);
    }
    
    public Set get(Object paramObject)
    {
      return (Set)super.get(paramObject);
    }
    
    public Set entries()
    {
      return (Set)super.entries();
    }
    
    public Set removeAll(Object paramObject)
    {
      return (Set)super.removeAll(paramObject);
    }
    
    public Set replaceValues(Object paramObject, Iterable paramIterable)
    {
      return (Set)super.replaceValues(paramObject, paramIterable);
    }
  }
  
  private static class ConstrainedListMultimap
    extends MapConstraints.ConstrainedMultimap
    implements ListMultimap
  {
    ConstrainedListMultimap(ListMultimap paramListMultimap, MapConstraint paramMapConstraint)
    {
      super(paramMapConstraint);
    }
    
    public List get(Object paramObject)
    {
      return (List)super.get(paramObject);
    }
    
    public List removeAll(Object paramObject)
    {
      return (List)super.removeAll(paramObject);
    }
    
    public List replaceValues(Object paramObject, Iterable paramIterable)
    {
      return (List)super.replaceValues(paramObject, paramIterable);
    }
  }
  
  static class ConstrainedAsMapEntries
    extends ForwardingSet
  {
    private final MapConstraint constraint;
    private final Set entries;
    
    ConstrainedAsMapEntries(Set paramSet, MapConstraint paramMapConstraint)
    {
      this.entries = paramSet;
      this.constraint = paramMapConstraint;
    }
    
    protected Set delegate()
    {
      return this.entries;
    }
    
    public Iterator iterator()
    {
      final Iterator localIterator = this.entries.iterator();
      new ForwardingIterator()
      {
        public Map.Entry next()
        {
          return MapConstraints.constrainedAsMapEntry((Map.Entry)localIterator.next(), MapConstraints.ConstrainedAsMapEntries.this.constraint);
        }
        
        protected Iterator delegate()
        {
          return localIterator;
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
    
    public int hashCode()
    {
      return standardHashCode();
    }
    
    public boolean remove(Object paramObject)
    {
      return Maps.removeEntryImpl(delegate(), paramObject);
    }
    
    public boolean removeAll(Collection paramCollection)
    {
      return standardRemoveAll(paramCollection);
    }
    
    public boolean retainAll(Collection paramCollection)
    {
      return standardRetainAll(paramCollection);
    }
  }
  
  static class ConstrainedEntrySet
    extends MapConstraints.ConstrainedEntries
    implements Set
  {
    ConstrainedEntrySet(Set paramSet, MapConstraint paramMapConstraint)
    {
      super(paramMapConstraint);
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
  
  private static class ConstrainedEntries
    extends ForwardingCollection
  {
    final MapConstraint constraint;
    final Collection entries;
    
    ConstrainedEntries(Collection paramCollection, MapConstraint paramMapConstraint)
    {
      this.entries = paramCollection;
      this.constraint = paramMapConstraint;
    }
    
    protected Collection delegate()
    {
      return this.entries;
    }
    
    public Iterator iterator()
    {
      final Iterator localIterator = this.entries.iterator();
      new ForwardingIterator()
      {
        public Map.Entry next()
        {
          return MapConstraints.constrainedEntry((Map.Entry)localIterator.next(), MapConstraints.ConstrainedEntries.this.constraint);
        }
        
        protected Iterator delegate()
        {
          return localIterator;
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
    
    public boolean remove(Object paramObject)
    {
      return Maps.removeEntryImpl(delegate(), paramObject);
    }
    
    public boolean removeAll(Collection paramCollection)
    {
      return standardRemoveAll(paramCollection);
    }
    
    public boolean retainAll(Collection paramCollection)
    {
      return standardRetainAll(paramCollection);
    }
  }
  
  private static class ConstrainedAsMapValues
    extends ForwardingCollection
  {
    final Collection delegate;
    final Set entrySet;
    
    ConstrainedAsMapValues(Collection paramCollection, Set paramSet)
    {
      this.delegate = paramCollection;
      this.entrySet = paramSet;
    }
    
    protected Collection delegate()
    {
      return this.delegate;
    }
    
    public Iterator iterator()
    {
      final Iterator localIterator = this.entrySet.iterator();
      new Iterator()
      {
        public boolean hasNext()
        {
          return localIterator.hasNext();
        }
        
        public Collection next()
        {
          return (Collection)((Map.Entry)localIterator.next()).getValue();
        }
        
        public void remove()
        {
          localIterator.remove();
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
    
    public boolean remove(Object paramObject)
    {
      return standardRemove(paramObject);
    }
    
    public boolean removeAll(Collection paramCollection)
    {
      return standardRemoveAll(paramCollection);
    }
    
    public boolean retainAll(Collection paramCollection)
    {
      return standardRetainAll(paramCollection);
    }
  }
  
  private static class ConstrainedMultimap
    extends ForwardingMultimap
    implements Serializable
  {
    final MapConstraint constraint;
    final Multimap delegate;
    transient Collection entries;
    transient Map asMap;
    
    public ConstrainedMultimap(Multimap paramMultimap, MapConstraint paramMapConstraint)
    {
      this.delegate = ((Multimap)Preconditions.checkNotNull(paramMultimap));
      this.constraint = ((MapConstraint)Preconditions.checkNotNull(paramMapConstraint));
    }
    
    protected Multimap delegate()
    {
      return this.delegate;
    }
    
    public Map asMap()
    {
      Object localObject = this.asMap;
      if (localObject == null)
      {
        final Map localMap = this.delegate.asMap();
        this.asMap = ( = new ForwardingMap()
        {
          Set entrySet;
          Collection values;
          
          protected Map delegate()
          {
            return localMap;
          }
          
          public Set entrySet()
          {
            Set localSet = this.entrySet;
            if (localSet == null) {
              this.entrySet = (localSet = MapConstraints.constrainedAsMapEntries(localMap.entrySet(), MapConstraints.ConstrainedMultimap.this.constraint));
            }
            return localSet;
          }
          
          public Collection get(Object paramAnonymousObject)
          {
            try
            {
              Collection localCollection = MapConstraints.ConstrainedMultimap.this.get(paramAnonymousObject);
              return localCollection.isEmpty() ? null : localCollection;
            }
            catch (ClassCastException localClassCastException) {}
            return null;
          }
          
          public Collection values()
          {
            Object localObject = this.values;
            if (localObject == null) {
              this.values = (localObject = new MapConstraints.ConstrainedAsMapValues(delegate().values(), entrySet()));
            }
            return (Collection)localObject;
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
        this.entries = (localCollection = MapConstraints.constrainedEntries(this.delegate.entries(), this.constraint));
      }
      return localCollection;
    }
    
    public Collection get(final Object paramObject)
    {
      Constraints.constrainedTypePreservingCollection(this.delegate.get(paramObject), new Constraint()
      {
        public Object checkElement(Object paramAnonymousObject)
        {
          MapConstraints.ConstrainedMultimap.this.constraint.checkKeyValue(paramObject, paramAnonymousObject);
          return paramAnonymousObject;
        }
      });
    }
    
    public boolean put(Object paramObject1, Object paramObject2)
    {
      this.constraint.checkKeyValue(paramObject1, paramObject2);
      return this.delegate.put(paramObject1, paramObject2);
    }
    
    public boolean putAll(Object paramObject, Iterable paramIterable)
    {
      return this.delegate.putAll(paramObject, MapConstraints.checkValues(paramObject, paramIterable, this.constraint));
    }
    
    public boolean putAll(Multimap paramMultimap)
    {
      boolean bool = false;
      Iterator localIterator = paramMultimap.entries().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        bool |= put(localEntry.getKey(), localEntry.getValue());
      }
      return bool;
    }
    
    public Collection replaceValues(Object paramObject, Iterable paramIterable)
    {
      return this.delegate.replaceValues(paramObject, MapConstraints.checkValues(paramObject, paramIterable, this.constraint));
    }
  }
  
  private static class InverseConstraint
    implements MapConstraint
  {
    final MapConstraint constraint;
    
    public InverseConstraint(MapConstraint paramMapConstraint)
    {
      this.constraint = ((MapConstraint)Preconditions.checkNotNull(paramMapConstraint));
    }
    
    public void checkKeyValue(Object paramObject1, Object paramObject2)
    {
      this.constraint.checkKeyValue(paramObject2, paramObject1);
    }
  }
  
  private static class ConstrainedBiMap
    extends MapConstraints.ConstrainedMap
    implements BiMap
  {
    volatile BiMap inverse;
    
    ConstrainedBiMap(BiMap paramBiMap1, BiMap paramBiMap2, MapConstraint paramMapConstraint)
    {
      super(paramMapConstraint);
      this.inverse = paramBiMap2;
    }
    
    protected BiMap delegate()
    {
      return (BiMap)super.delegate();
    }
    
    public Object forcePut(Object paramObject1, Object paramObject2)
    {
      this.constraint.checkKeyValue(paramObject1, paramObject2);
      return delegate().forcePut(paramObject1, paramObject2);
    }
    
    public BiMap inverse()
    {
      if (this.inverse == null) {
        this.inverse = new ConstrainedBiMap(delegate().inverse(), this, new MapConstraints.InverseConstraint(this.constraint));
      }
      return this.inverse;
    }
    
    public Set values()
    {
      return delegate().values();
    }
  }
  
  static class ConstrainedMap
    extends ForwardingMap
  {
    private final Map delegate;
    final MapConstraint constraint;
    private transient Set entrySet;
    
    ConstrainedMap(Map paramMap, MapConstraint paramMapConstraint)
    {
      this.delegate = ((Map)Preconditions.checkNotNull(paramMap));
      this.constraint = ((MapConstraint)Preconditions.checkNotNull(paramMapConstraint));
    }
    
    protected Map delegate()
    {
      return this.delegate;
    }
    
    public Set entrySet()
    {
      Set localSet = this.entrySet;
      if (localSet == null) {
        this.entrySet = (localSet = MapConstraints.constrainedEntrySet(this.delegate.entrySet(), this.constraint));
      }
      return localSet;
    }
    
    public Object put(Object paramObject1, Object paramObject2)
    {
      this.constraint.checkKeyValue(paramObject1, paramObject2);
      return this.delegate.put(paramObject1, paramObject2);
    }
    
    public void putAll(Map paramMap)
    {
      this.delegate.putAll(MapConstraints.checkMap(paramMap, this.constraint));
    }
  }
  
  private static enum NotNullMapConstraint
    implements MapConstraint
  {
    INSTANCE;
    
    public void checkKeyValue(Object paramObject1, Object paramObject2)
    {
      Preconditions.checkNotNull(paramObject1);
      Preconditions.checkNotNull(paramObject2);
    }
    
    public String toString()
    {
      return "Not null";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\MapConstraints.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */