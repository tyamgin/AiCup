package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible
class FilteredKeyMultimap
  extends FilteredMultimap
{
  final Predicate keyPredicate;
  
  FilteredKeyMultimap(Multimap paramMultimap, Predicate paramPredicate)
  {
    super(paramMultimap);
    this.keyPredicate = ((Predicate)Preconditions.checkNotNull(paramPredicate));
  }
  
  Predicate entryPredicate()
  {
    return Predicates.compose(this.keyPredicate, Maps.keyFunction());
  }
  
  public int size()
  {
    int i = 0;
    Iterator localIterator = asMap().values().iterator();
    while (localIterator.hasNext())
    {
      Collection localCollection = (Collection)localIterator.next();
      i += localCollection.size();
    }
    return i;
  }
  
  public boolean containsKey(Object paramObject)
  {
    if (this.unfiltered.containsKey(paramObject))
    {
      Object localObject = paramObject;
      return this.keyPredicate.apply(localObject);
    }
    return false;
  }
  
  public Collection removeAll(Object paramObject)
  {
    return containsKey(paramObject) ? this.unfiltered.removeAll(paramObject) : unmodifiableEmptyCollection();
  }
  
  Collection unmodifiableEmptyCollection()
  {
    if ((this.unfiltered instanceof SetMultimap)) {
      return ImmutableSet.of();
    }
    return ImmutableList.of();
  }
  
  public void clear()
  {
    keySet().clear();
  }
  
  Set createKeySet()
  {
    return Sets.filter(this.unfiltered.keySet(), this.keyPredicate);
  }
  
  public Collection get(Object paramObject)
  {
    if (this.keyPredicate.apply(paramObject)) {
      return this.unfiltered.get(paramObject);
    }
    if ((this.unfiltered instanceof SetMultimap)) {
      return new AddRejectingSet(paramObject);
    }
    return new AddRejectingList(paramObject);
  }
  
  Iterator entryIterator()
  {
    return Iterators.filter(this.unfiltered.entries().iterator(), Predicates.compose(this.keyPredicate, Maps.keyFunction()));
  }
  
  Collection createEntries()
  {
    new Multimaps.Entries()
    {
      Multimap multimap()
      {
        return FilteredKeyMultimap.this;
      }
      
      public Iterator iterator()
      {
        return FilteredKeyMultimap.this.entryIterator();
      }
      
      public boolean remove(Object paramAnonymousObject)
      {
        if ((paramAnonymousObject instanceof Map.Entry))
        {
          Map.Entry localEntry = (Map.Entry)paramAnonymousObject;
          if ((FilteredKeyMultimap.this.unfiltered.containsEntry(localEntry.getKey(), localEntry.getValue())) && (FilteredKeyMultimap.this.keyPredicate.apply(localEntry.getKey()))) {
            return FilteredKeyMultimap.this.unfiltered.remove(localEntry.getKey(), localEntry.getValue());
          }
        }
        return false;
      }
      
      public boolean removeAll(Collection paramAnonymousCollection)
      {
        Predicate localPredicate = Predicates.and(Predicates.compose(FilteredKeyMultimap.this.keyPredicate, Maps.keyFunction()), Predicates.in(paramAnonymousCollection));
        return Iterators.removeIf(FilteredKeyMultimap.this.unfiltered.entries().iterator(), localPredicate);
      }
      
      public boolean retainAll(Collection paramAnonymousCollection)
      {
        Predicate localPredicate = Predicates.and(Predicates.compose(FilteredKeyMultimap.this.keyPredicate, Maps.keyFunction()), Predicates.not(Predicates.in(paramAnonymousCollection)));
        return Iterators.removeIf(FilteredKeyMultimap.this.unfiltered.entries().iterator(), localPredicate);
      }
    };
  }
  
  Map createAsMap()
  {
    return Maps.filterKeys(this.unfiltered.asMap(), this.keyPredicate);
  }
  
  Multiset createKeys()
  {
    return Multisets.filter(this.unfiltered.keys(), this.keyPredicate);
  }
  
  static class AddRejectingList
    extends ForwardingList
  {
    final Object key;
    
    AddRejectingList(Object paramObject)
    {
      this.key = paramObject;
    }
    
    public boolean add(Object paramObject)
    {
      add(0, paramObject);
      return true;
    }
    
    public boolean addAll(Collection paramCollection)
    {
      addAll(0, paramCollection);
      return true;
    }
    
    public void add(int paramInt, Object paramObject)
    {
      Preconditions.checkPositionIndex(paramInt, 0);
      throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
    }
    
    public boolean addAll(int paramInt, Collection paramCollection)
    {
      Preconditions.checkNotNull(paramCollection);
      Preconditions.checkPositionIndex(paramInt, 0);
      throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
    }
    
    protected List delegate()
    {
      return Collections.emptyList();
    }
  }
  
  static class AddRejectingSet
    extends ForwardingSet
  {
    final Object key;
    
    AddRejectingSet(Object paramObject)
    {
      this.key = paramObject;
    }
    
    public boolean add(Object paramObject)
    {
      throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
    }
    
    public boolean addAll(Collection paramCollection)
    {
      Preconditions.checkNotNull(paramCollection);
      throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
    }
    
    protected Set delegate()
    {
      return Collections.emptySet();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\FilteredKeyMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */