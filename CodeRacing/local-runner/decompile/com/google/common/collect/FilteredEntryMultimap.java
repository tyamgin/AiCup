package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible
class FilteredEntryMultimap
  extends FilteredMultimap
{
  final Predicate predicate;
  
  FilteredEntryMultimap(Multimap paramMultimap, Predicate paramPredicate)
  {
    super(paramMultimap);
    this.predicate = ((Predicate)Preconditions.checkNotNull(paramPredicate));
  }
  
  Predicate entryPredicate()
  {
    return this.predicate;
  }
  
  public int size()
  {
    return entries().size();
  }
  
  private boolean satisfies(Object paramObject1, Object paramObject2)
  {
    return this.predicate.apply(Maps.immutableEntry(paramObject1, paramObject2));
  }
  
  static Collection filterCollection(Collection paramCollection, Predicate paramPredicate)
  {
    if ((paramCollection instanceof Set)) {
      return Sets.filter((Set)paramCollection, paramPredicate);
    }
    return Collections2.filter(paramCollection, paramPredicate);
  }
  
  public boolean containsKey(Object paramObject)
  {
    return asMap().get(paramObject) != null;
  }
  
  public Collection removeAll(Object paramObject)
  {
    return (Collection)Objects.firstNonNull(asMap().remove(paramObject), unmodifiableEmptyCollection());
  }
  
  Collection unmodifiableEmptyCollection()
  {
    return (this.unfiltered instanceof SetMultimap) ? Collections.emptySet() : Collections.emptyList();
  }
  
  public void clear()
  {
    entries().clear();
  }
  
  public Collection get(Object paramObject)
  {
    return filterCollection(this.unfiltered.get(paramObject), new ValuePredicate(paramObject));
  }
  
  Collection createEntries()
  {
    return filterCollection(this.unfiltered.entries(), this.predicate);
  }
  
  Iterator entryIterator()
  {
    throw new AssertionError("should never be called");
  }
  
  Map createAsMap()
  {
    return new AsMap();
  }
  
  public Set keySet()
  {
    return asMap().keySet();
  }
  
  boolean removeIf(Predicate paramPredicate)
  {
    Iterator localIterator = this.unfiltered.asMap().entrySet().iterator();
    for (boolean bool = false; localIterator.hasNext(); bool = true)
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object localObject = localEntry.getKey();
      Collection localCollection = filterCollection((Collection)localEntry.getValue(), new ValuePredicate(localObject));
      if ((!localCollection.isEmpty()) && (paramPredicate.apply(Maps.immutableEntry(localObject, localCollection)))) {
        if (localCollection.size() == ((Collection)localEntry.getValue()).size()) {
          localIterator.remove();
        } else {
          localCollection.clear();
        }
      }
    }
    return bool;
  }
  
  Multiset createKeys()
  {
    return new Keys();
  }
  
  class Keys
    extends Multimaps.Keys
  {
    Keys()
    {
      super();
    }
    
    public int remove(Object paramObject, int paramInt)
    {
      Multisets.checkNonnegative(paramInt, "occurrences");
      if (paramInt == 0) {
        return count(paramObject);
      }
      Collection localCollection = (Collection)FilteredEntryMultimap.this.unfiltered.asMap().get(paramObject);
      if (localCollection == null) {
        return 0;
      }
      Object localObject1 = paramObject;
      int i = 0;
      Iterator localIterator = localCollection.iterator();
      while (localIterator.hasNext())
      {
        Object localObject2 = localIterator.next();
        if (FilteredEntryMultimap.this.satisfies(localObject1, localObject2))
        {
          i++;
          if (i <= paramInt) {
            localIterator.remove();
          }
        }
      }
      return i;
    }
    
    public Set entrySet()
    {
      new Multisets.EntrySet()
      {
        Multiset multiset()
        {
          return FilteredEntryMultimap.Keys.this;
        }
        
        public Iterator iterator()
        {
          return FilteredEntryMultimap.Keys.this.entryIterator();
        }
        
        public int size()
        {
          return FilteredEntryMultimap.this.keySet().size();
        }
        
        private boolean removeIf(final Predicate paramAnonymousPredicate)
        {
          FilteredEntryMultimap.this.removeIf(new Predicate()
          {
            public boolean apply(Map.Entry paramAnonymous2Entry)
            {
              return paramAnonymousPredicate.apply(Multisets.immutableEntry(paramAnonymous2Entry.getKey(), ((Collection)paramAnonymous2Entry.getValue()).size()));
            }
          });
        }
        
        public boolean removeAll(Collection paramAnonymousCollection)
        {
          return removeIf(Predicates.in(paramAnonymousCollection));
        }
        
        public boolean retainAll(Collection paramAnonymousCollection)
        {
          return removeIf(Predicates.not(Predicates.in(paramAnonymousCollection)));
        }
      };
    }
  }
  
  class AsMap
    extends AbstractMap
  {
    private Set keySet;
    
    AsMap() {}
    
    public boolean containsKey(Object paramObject)
    {
      return get(paramObject) != null;
    }
    
    public void clear()
    {
      FilteredEntryMultimap.this.clear();
    }
    
    public Collection get(Object paramObject)
    {
      Collection localCollection = (Collection)FilteredEntryMultimap.this.unfiltered.asMap().get(paramObject);
      if (localCollection == null) {
        return null;
      }
      Object localObject = paramObject;
      localCollection = FilteredEntryMultimap.filterCollection(localCollection, new FilteredEntryMultimap.ValuePredicate(FilteredEntryMultimap.this, localObject));
      return localCollection.isEmpty() ? null : localCollection;
    }
    
    public Collection remove(Object paramObject)
    {
      Collection localCollection = (Collection)FilteredEntryMultimap.this.unfiltered.asMap().get(paramObject);
      if (localCollection == null) {
        return null;
      }
      Object localObject1 = paramObject;
      ArrayList localArrayList = Lists.newArrayList();
      Iterator localIterator = localCollection.iterator();
      while (localIterator.hasNext())
      {
        Object localObject2 = localIterator.next();
        if (FilteredEntryMultimap.this.satisfies(localObject1, localObject2))
        {
          localIterator.remove();
          localArrayList.add(localObject2);
        }
      }
      if (localArrayList.isEmpty()) {
        return null;
      }
      if ((FilteredEntryMultimap.this.unfiltered instanceof SetMultimap)) {
        return Collections.unmodifiableSet(Sets.newLinkedHashSet(localArrayList));
      }
      return Collections.unmodifiableList(localArrayList);
    }
    
    public Set keySet()
    {
      Set localSet = this.keySet;
      if (localSet == null) {
        this. = new Maps.KeySet()
        {
          Map map()
          {
            return FilteredEntryMultimap.AsMap.this;
          }
          
          public boolean removeAll(Collection paramAnonymousCollection)
          {
            return FilteredEntryMultimap.this.removeIf(Predicates.compose(Predicates.in(paramAnonymousCollection), Maps.keyFunction()));
          }
          
          public boolean retainAll(Collection paramAnonymousCollection)
          {
            return FilteredEntryMultimap.this.removeIf(Predicates.compose(Predicates.not(Predicates.in(paramAnonymousCollection)), Maps.keyFunction()));
          }
          
          public boolean remove(Object paramAnonymousObject)
          {
            return FilteredEntryMultimap.AsMap.this.remove(paramAnonymousObject) != null;
          }
        };
      }
      return localSet;
    }
    
    public Set entrySet()
    {
      new Maps.EntrySet()
      {
        Map map()
        {
          return FilteredEntryMultimap.AsMap.this;
        }
        
        public Iterator iterator()
        {
          new AbstractIterator()
          {
            final Iterator backingIterator = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();
            
            protected Map.Entry computeNext()
            {
              while (this.backingIterator.hasNext())
              {
                Map.Entry localEntry = (Map.Entry)this.backingIterator.next();
                Object localObject = localEntry.getKey();
                Collection localCollection = FilteredEntryMultimap.filterCollection((Collection)localEntry.getValue(), new FilteredEntryMultimap.ValuePredicate(FilteredEntryMultimap.this, localObject));
                if (!localCollection.isEmpty()) {
                  return Maps.immutableEntry(localObject, localCollection);
                }
              }
              return (Map.Entry)endOfData();
            }
          };
        }
        
        public boolean removeAll(Collection paramAnonymousCollection)
        {
          return FilteredEntryMultimap.this.removeIf(Predicates.in(paramAnonymousCollection));
        }
        
        public boolean retainAll(Collection paramAnonymousCollection)
        {
          return FilteredEntryMultimap.this.removeIf(Predicates.not(Predicates.in(paramAnonymousCollection)));
        }
        
        public int size()
        {
          return Iterators.size(iterator());
        }
      };
    }
    
    public Collection values()
    {
      new Maps.Values()
      {
        Map map()
        {
          return FilteredEntryMultimap.AsMap.this;
        }
        
        public boolean remove(Object paramAnonymousObject)
        {
          if ((paramAnonymousObject instanceof Collection))
          {
            Collection localCollection1 = (Collection)paramAnonymousObject;
            Iterator localIterator = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();
            while (localIterator.hasNext())
            {
              Map.Entry localEntry = (Map.Entry)localIterator.next();
              Object localObject = localEntry.getKey();
              Collection localCollection2 = FilteredEntryMultimap.filterCollection((Collection)localEntry.getValue(), new FilteredEntryMultimap.ValuePredicate(FilteredEntryMultimap.this, localObject));
              if ((!localCollection2.isEmpty()) && (localCollection1.equals(localCollection2)))
              {
                if (localCollection2.size() == ((Collection)localEntry.getValue()).size()) {
                  localIterator.remove();
                } else {
                  localCollection2.clear();
                }
                return true;
              }
            }
          }
          return false;
        }
        
        public boolean removeAll(Collection paramAnonymousCollection)
        {
          return FilteredEntryMultimap.this.removeIf(Predicates.compose(Predicates.in(paramAnonymousCollection), Maps.valueFunction()));
        }
        
        public boolean retainAll(Collection paramAnonymousCollection)
        {
          return FilteredEntryMultimap.this.removeIf(Predicates.compose(Predicates.not(Predicates.in(paramAnonymousCollection)), Maps.valueFunction()));
        }
      };
    }
  }
  
  final class ValuePredicate
    implements Predicate
  {
    private final Object key;
    
    ValuePredicate(Object paramObject)
    {
      this.key = paramObject;
    }
    
    public boolean apply(Object paramObject)
    {
      return FilteredEntryMultimap.this.satisfies(this.key, paramObject);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\FilteredEntryMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */