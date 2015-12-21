package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible
abstract class AbstractMultimap
  implements Multimap
{
  private transient Collection entries;
  private transient Set keySet;
  private transient Multiset keys;
  private transient Collection values;
  private transient Map asMap;
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  public boolean containsValue(Object paramObject)
  {
    Iterator localIterator = asMap().values().iterator();
    while (localIterator.hasNext())
    {
      Collection localCollection = (Collection)localIterator.next();
      if (localCollection.contains(paramObject)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean containsEntry(Object paramObject1, Object paramObject2)
  {
    Collection localCollection = (Collection)asMap().get(paramObject1);
    return (localCollection != null) && (localCollection.contains(paramObject2));
  }
  
  public boolean remove(Object paramObject1, Object paramObject2)
  {
    Collection localCollection = (Collection)asMap().get(paramObject1);
    return (localCollection != null) && (localCollection.remove(paramObject2));
  }
  
  public boolean put(Object paramObject1, Object paramObject2)
  {
    return get(paramObject1).add(paramObject2);
  }
  
  public boolean putAll(Object paramObject, Iterable paramIterable)
  {
    Preconditions.checkNotNull(paramIterable);
    return (paramIterable.iterator().hasNext()) && (Iterables.addAll(get(paramObject), paramIterable));
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
    Preconditions.checkNotNull(paramIterable);
    Collection localCollection = removeAll(paramObject);
    putAll(paramObject, paramIterable);
    return localCollection;
  }
  
  public Collection entries()
  {
    Collection localCollection = this.entries;
    return localCollection == null ? (this.entries = createEntries()) : localCollection;
  }
  
  Collection createEntries()
  {
    if ((this instanceof SetMultimap)) {
      new Multimaps.EntrySet()
      {
        Multimap multimap()
        {
          return AbstractMultimap.this;
        }
        
        public Iterator iterator()
        {
          return AbstractMultimap.this.entryIterator();
        }
      };
    }
    new Multimaps.Entries()
    {
      Multimap multimap()
      {
        return AbstractMultimap.this;
      }
      
      public Iterator iterator()
      {
        return AbstractMultimap.this.entryIterator();
      }
    };
  }
  
  abstract Iterator entryIterator();
  
  public Set keySet()
  {
    Set localSet = this.keySet;
    return localSet == null ? (this.keySet = createKeySet()) : localSet;
  }
  
  Set createKeySet()
  {
    new Maps.KeySet()
    {
      Map map()
      {
        return AbstractMultimap.this.asMap();
      }
    };
  }
  
  public Multiset keys()
  {
    Multiset localMultiset = this.keys;
    return localMultiset == null ? (this.keys = createKeys()) : localMultiset;
  }
  
  Multiset createKeys()
  {
    return new Multimaps.Keys(this);
  }
  
  public Collection values()
  {
    Collection localCollection = this.values;
    return localCollection == null ? (this.values = createValues()) : localCollection;
  }
  
  Collection createValues()
  {
    return new Multimaps.Values(this);
  }
  
  public Map asMap()
  {
    Map localMap = this.asMap;
    return localMap == null ? (this.asMap = createAsMap()) : localMap;
  }
  
  abstract Map createAsMap();
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof Multimap))
    {
      Multimap localMultimap = (Multimap)paramObject;
      return asMap().equals(localMultimap.asMap());
    }
    return false;
  }
  
  public int hashCode()
  {
    return asMap().hashCode();
  }
  
  public String toString()
  {
    return asMap().toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\AbstractMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */