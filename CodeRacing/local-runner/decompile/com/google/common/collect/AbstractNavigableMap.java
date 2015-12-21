package com.google.common.collect;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

abstract class AbstractNavigableMap
  extends AbstractMap
  implements NavigableMap
{
  public abstract Object get(Object paramObject);
  
  public Map.Entry firstEntry()
  {
    return (Map.Entry)Iterators.getNext(entryIterator(), null);
  }
  
  public Map.Entry lastEntry()
  {
    return (Map.Entry)Iterators.getNext(descendingEntryIterator(), null);
  }
  
  public Map.Entry pollFirstEntry()
  {
    return (Map.Entry)Iterators.pollNext(entryIterator());
  }
  
  public Map.Entry pollLastEntry()
  {
    return (Map.Entry)Iterators.pollNext(descendingEntryIterator());
  }
  
  public Object firstKey()
  {
    Map.Entry localEntry = firstEntry();
    if (localEntry == null) {
      throw new NoSuchElementException();
    }
    return localEntry.getKey();
  }
  
  public Object lastKey()
  {
    Map.Entry localEntry = lastEntry();
    if (localEntry == null) {
      throw new NoSuchElementException();
    }
    return localEntry.getKey();
  }
  
  public Map.Entry lowerEntry(Object paramObject)
  {
    return headMap(paramObject, false).lastEntry();
  }
  
  public Map.Entry floorEntry(Object paramObject)
  {
    return headMap(paramObject, true).lastEntry();
  }
  
  public Map.Entry ceilingEntry(Object paramObject)
  {
    return tailMap(paramObject, true).firstEntry();
  }
  
  public Map.Entry higherEntry(Object paramObject)
  {
    return tailMap(paramObject, false).firstEntry();
  }
  
  public Object lowerKey(Object paramObject)
  {
    return Maps.keyOrNull(lowerEntry(paramObject));
  }
  
  public Object floorKey(Object paramObject)
  {
    return Maps.keyOrNull(floorEntry(paramObject));
  }
  
  public Object ceilingKey(Object paramObject)
  {
    return Maps.keyOrNull(ceilingEntry(paramObject));
  }
  
  public Object higherKey(Object paramObject)
  {
    return Maps.keyOrNull(higherEntry(paramObject));
  }
  
  abstract Iterator entryIterator();
  
  abstract Iterator descendingEntryIterator();
  
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
  
  public NavigableSet navigableKeySet()
  {
    return new Maps.NavigableKeySet(this);
  }
  
  public Set keySet()
  {
    return navigableKeySet();
  }
  
  public abstract int size();
  
  public Set entrySet()
  {
    new Maps.EntrySet()
    {
      Map map()
      {
        return AbstractNavigableMap.this;
      }
      
      public Iterator iterator()
      {
        return AbstractNavigableMap.this.entryIterator();
      }
    };
  }
  
  public NavigableSet descendingKeySet()
  {
    return descendingMap().navigableKeySet();
  }
  
  public NavigableMap descendingMap()
  {
    return new DescendingMap(null);
  }
  
  private final class DescendingMap
    extends Maps.DescendingMap
  {
    private DescendingMap() {}
    
    NavigableMap forward()
    {
      return AbstractNavigableMap.this;
    }
    
    Iterator entryIterator()
    {
      return AbstractNavigableMap.this.descendingEntryIterator();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\AbstractNavigableMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */