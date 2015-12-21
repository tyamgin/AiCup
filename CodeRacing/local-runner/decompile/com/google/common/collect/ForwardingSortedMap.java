package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

@GwtCompatible
public abstract class ForwardingSortedMap
  extends ForwardingMap
  implements SortedMap
{
  protected abstract SortedMap delegate();
  
  public Comparator comparator()
  {
    return delegate().comparator();
  }
  
  public Object firstKey()
  {
    return delegate().firstKey();
  }
  
  public SortedMap headMap(Object paramObject)
  {
    return delegate().headMap(paramObject);
  }
  
  public Object lastKey()
  {
    return delegate().lastKey();
  }
  
  public SortedMap subMap(Object paramObject1, Object paramObject2)
  {
    return delegate().subMap(paramObject1, paramObject2);
  }
  
  public SortedMap tailMap(Object paramObject)
  {
    return delegate().tailMap(paramObject);
  }
  
  private int unsafeCompare(Object paramObject1, Object paramObject2)
  {
    Comparator localComparator = comparator();
    if (localComparator == null) {
      return ((Comparable)paramObject1).compareTo(paramObject2);
    }
    return localComparator.compare(paramObject1, paramObject2);
  }
  
  @Beta
  protected boolean standardContainsKey(Object paramObject)
  {
    try
    {
      ForwardingSortedMap localForwardingSortedMap = this;
      Object localObject = localForwardingSortedMap.tailMap(paramObject).firstKey();
      return unsafeCompare(localObject, paramObject) == 0;
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      return false;
    }
    catch (NullPointerException localNullPointerException) {}
    return false;
  }
  
  @Beta
  protected Object standardRemove(Object paramObject)
  {
    try
    {
      ForwardingSortedMap localForwardingSortedMap = this;
      Iterator localIterator = localForwardingSortedMap.tailMap(paramObject).entrySet().iterator();
      if (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        if (unsafeCompare(localEntry.getKey(), paramObject) == 0)
        {
          Object localObject = localEntry.getValue();
          localIterator.remove();
          return localObject;
        }
      }
    }
    catch (ClassCastException localClassCastException)
    {
      return null;
    }
    catch (NullPointerException localNullPointerException)
    {
      return null;
    }
    return null;
  }
  
  @Beta
  protected SortedMap standardSubMap(Object paramObject1, Object paramObject2)
  {
    return tailMap(paramObject1).headMap(paramObject2);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ForwardingSortedMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */