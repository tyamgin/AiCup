package com.google.common.collect;

import com.google.common.annotations.Beta;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public abstract class ForwardingNavigableMap
  extends ForwardingSortedMap
  implements NavigableMap
{
  protected abstract NavigableMap delegate();
  
  public Map.Entry lowerEntry(Object paramObject)
  {
    return delegate().lowerEntry(paramObject);
  }
  
  protected Map.Entry standardLowerEntry(Object paramObject)
  {
    return headMap(paramObject, false).lastEntry();
  }
  
  public Object lowerKey(Object paramObject)
  {
    return delegate().lowerKey(paramObject);
  }
  
  protected Object standardLowerKey(Object paramObject)
  {
    return Maps.keyOrNull(lowerEntry(paramObject));
  }
  
  public Map.Entry floorEntry(Object paramObject)
  {
    return delegate().floorEntry(paramObject);
  }
  
  protected Map.Entry standardFloorEntry(Object paramObject)
  {
    return headMap(paramObject, true).lastEntry();
  }
  
  public Object floorKey(Object paramObject)
  {
    return delegate().floorKey(paramObject);
  }
  
  protected Object standardFloorKey(Object paramObject)
  {
    return Maps.keyOrNull(floorEntry(paramObject));
  }
  
  public Map.Entry ceilingEntry(Object paramObject)
  {
    return delegate().ceilingEntry(paramObject);
  }
  
  protected Map.Entry standardCeilingEntry(Object paramObject)
  {
    return tailMap(paramObject, true).firstEntry();
  }
  
  public Object ceilingKey(Object paramObject)
  {
    return delegate().ceilingKey(paramObject);
  }
  
  protected Object standardCeilingKey(Object paramObject)
  {
    return Maps.keyOrNull(ceilingEntry(paramObject));
  }
  
  public Map.Entry higherEntry(Object paramObject)
  {
    return delegate().higherEntry(paramObject);
  }
  
  protected Map.Entry standardHigherEntry(Object paramObject)
  {
    return tailMap(paramObject, false).firstEntry();
  }
  
  public Object higherKey(Object paramObject)
  {
    return delegate().higherKey(paramObject);
  }
  
  protected Object standardHigherKey(Object paramObject)
  {
    return Maps.keyOrNull(higherEntry(paramObject));
  }
  
  public Map.Entry firstEntry()
  {
    return delegate().firstEntry();
  }
  
  protected Map.Entry standardFirstEntry()
  {
    return (Map.Entry)Iterables.getFirst(entrySet(), null);
  }
  
  protected Object standardFirstKey()
  {
    Map.Entry localEntry = firstEntry();
    if (localEntry == null) {
      throw new NoSuchElementException();
    }
    return localEntry.getKey();
  }
  
  public Map.Entry lastEntry()
  {
    return delegate().lastEntry();
  }
  
  protected Map.Entry standardLastEntry()
  {
    return (Map.Entry)Iterables.getFirst(descendingMap().entrySet(), null);
  }
  
  protected Object standardLastKey()
  {
    Map.Entry localEntry = lastEntry();
    if (localEntry == null) {
      throw new NoSuchElementException();
    }
    return localEntry.getKey();
  }
  
  public Map.Entry pollFirstEntry()
  {
    return delegate().pollFirstEntry();
  }
  
  protected Map.Entry standardPollFirstEntry()
  {
    return (Map.Entry)Iterators.pollNext(entrySet().iterator());
  }
  
  public Map.Entry pollLastEntry()
  {
    return delegate().pollLastEntry();
  }
  
  protected Map.Entry standardPollLastEntry()
  {
    return (Map.Entry)Iterators.pollNext(descendingMap().entrySet().iterator());
  }
  
  public NavigableMap descendingMap()
  {
    return delegate().descendingMap();
  }
  
  public NavigableSet navigableKeySet()
  {
    return delegate().navigableKeySet();
  }
  
  public NavigableSet descendingKeySet()
  {
    return delegate().descendingKeySet();
  }
  
  @Beta
  protected NavigableSet standardDescendingKeySet()
  {
    return descendingMap().navigableKeySet();
  }
  
  protected SortedMap standardSubMap(Object paramObject1, Object paramObject2)
  {
    return subMap(paramObject1, true, paramObject2, false);
  }
  
  public NavigableMap subMap(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
  {
    return delegate().subMap(paramObject1, paramBoolean1, paramObject2, paramBoolean2);
  }
  
  public NavigableMap headMap(Object paramObject, boolean paramBoolean)
  {
    return delegate().headMap(paramObject, paramBoolean);
  }
  
  public NavigableMap tailMap(Object paramObject, boolean paramBoolean)
  {
    return delegate().tailMap(paramObject, paramBoolean);
  }
  
  protected SortedMap standardHeadMap(Object paramObject)
  {
    return headMap(paramObject, false);
  }
  
  protected SortedMap standardTailMap(Object paramObject)
  {
    return tailMap(paramObject, true);
  }
  
  @Beta
  protected class StandardNavigableKeySet
    extends Maps.NavigableKeySet
  {
    public StandardNavigableKeySet()
    {
      super();
    }
  }
  
  @Beta
  protected class StandardDescendingMap
    extends Maps.DescendingMap
  {
    public StandardDescendingMap() {}
    
    NavigableMap forward()
    {
      return ForwardingNavigableMap.this;
    }
    
    protected Iterator entryIterator()
    {
      new Iterator()
      {
        private Map.Entry toRemove = null;
        private Map.Entry nextOrNull = ForwardingNavigableMap.StandardDescendingMap.this.forward().lastEntry();
        
        public boolean hasNext()
        {
          return this.nextOrNull != null;
        }
        
        public Map.Entry next()
        {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          try
          {
            Map.Entry localEntry = this.nextOrNull;
            return localEntry;
          }
          finally
          {
            this.toRemove = this.nextOrNull;
            this.nextOrNull = ForwardingNavigableMap.StandardDescendingMap.this.forward().lowerEntry(this.nextOrNull.getKey());
          }
        }
        
        public void remove()
        {
          Iterators.checkRemove(this.toRemove != null);
          ForwardingNavigableMap.StandardDescendingMap.this.forward().remove(this.toRemove.getKey());
          this.toRemove = null;
        }
      };
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ForwardingNavigableMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */