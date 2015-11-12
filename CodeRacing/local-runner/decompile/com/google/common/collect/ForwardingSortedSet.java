package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

@GwtCompatible
public abstract class ForwardingSortedSet
  extends ForwardingSet
  implements SortedSet
{
  protected abstract SortedSet delegate();
  
  public Comparator comparator()
  {
    return delegate().comparator();
  }
  
  public Object first()
  {
    return delegate().first();
  }
  
  public SortedSet headSet(Object paramObject)
  {
    return delegate().headSet(paramObject);
  }
  
  public Object last()
  {
    return delegate().last();
  }
  
  public SortedSet subSet(Object paramObject1, Object paramObject2)
  {
    return delegate().subSet(paramObject1, paramObject2);
  }
  
  public SortedSet tailSet(Object paramObject)
  {
    return delegate().tailSet(paramObject);
  }
  
  private int unsafeCompare(Object paramObject1, Object paramObject2)
  {
    Comparator localComparator = comparator();
    return localComparator == null ? ((Comparable)paramObject1).compareTo(paramObject2) : localComparator.compare(paramObject1, paramObject2);
  }
  
  @Beta
  protected boolean standardContains(Object paramObject)
  {
    try
    {
      ForwardingSortedSet localForwardingSortedSet = this;
      Object localObject = localForwardingSortedSet.tailSet(paramObject).first();
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
  protected boolean standardRemove(Object paramObject)
  {
    try
    {
      ForwardingSortedSet localForwardingSortedSet = this;
      Iterator localIterator = localForwardingSortedSet.tailSet(paramObject).iterator();
      if (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if (unsafeCompare(localObject, paramObject) == 0)
        {
          localIterator.remove();
          return true;
        }
      }
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    catch (NullPointerException localNullPointerException)
    {
      return false;
    }
    return false;
  }
  
  @Beta
  protected SortedSet standardSubSet(Object paramObject1, Object paramObject2)
  {
    return tailSet(paramObject1).headSet(paramObject2);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ForwardingSortedSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */