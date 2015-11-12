package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible(emulated=true)
abstract class AbstractMapBasedMultiset
  extends AbstractMultiset
  implements Serializable
{
  private transient Map backingMap;
  private transient long size;
  @GwtIncompatible("not needed in emulated source.")
  private static final long serialVersionUID = -2250766705698539974L;
  
  protected AbstractMapBasedMultiset(Map paramMap)
  {
    this.backingMap = ((Map)Preconditions.checkNotNull(paramMap));
    this.size = super.size();
  }
  
  void setBackingMap(Map paramMap)
  {
    this.backingMap = paramMap;
  }
  
  public Set entrySet()
  {
    return super.entrySet();
  }
  
  Iterator entryIterator()
  {
    final Iterator localIterator = this.backingMap.entrySet().iterator();
    new Iterator()
    {
      Map.Entry toRemove;
      
      public boolean hasNext()
      {
        return localIterator.hasNext();
      }
      
      public Multiset.Entry next()
      {
        final Map.Entry localEntry = (Map.Entry)localIterator.next();
        this.toRemove = localEntry;
        new Multisets.AbstractEntry()
        {
          public Object getElement()
          {
            return localEntry.getKey();
          }
          
          public int getCount()
          {
            int i = ((Count)localEntry.getValue()).get();
            if (i == 0)
            {
              Count localCount = (Count)AbstractMapBasedMultiset.this.backingMap.get(getElement());
              if (localCount != null) {
                i = localCount.get();
              }
            }
            return i;
          }
        };
      }
      
      public void remove()
      {
        Iterators.checkRemove(this.toRemove != null);
        AbstractMapBasedMultiset.access$122(AbstractMapBasedMultiset.this, ((Count)this.toRemove.getValue()).getAndSet(0));
        localIterator.remove();
        this.toRemove = null;
      }
    };
  }
  
  public void clear()
  {
    Iterator localIterator = this.backingMap.values().iterator();
    while (localIterator.hasNext())
    {
      Count localCount = (Count)localIterator.next();
      localCount.set(0);
    }
    this.backingMap.clear();
    this.size = 0L;
  }
  
  int distinctElements()
  {
    return this.backingMap.size();
  }
  
  public int size()
  {
    return Ints.saturatedCast(this.size);
  }
  
  public Iterator iterator()
  {
    return new MapBasedMultisetIterator();
  }
  
  public int count(Object paramObject)
  {
    Count localCount = (Count)Maps.safeGet(this.backingMap, paramObject);
    return localCount == null ? 0 : localCount.get();
  }
  
  public int add(Object paramObject, int paramInt)
  {
    if (paramInt == 0) {
      return count(paramObject);
    }
    Preconditions.checkArgument(paramInt > 0, "occurrences cannot be negative: %s", new Object[] { Integer.valueOf(paramInt) });
    Count localCount = (Count)this.backingMap.get(paramObject);
    int i;
    if (localCount == null)
    {
      i = 0;
      this.backingMap.put(paramObject, new Count(paramInt));
    }
    else
    {
      i = localCount.get();
      long l = i + paramInt;
      Preconditions.checkArgument(l <= 2147483647L, "too many occurrences: %s", new Object[] { Long.valueOf(l) });
      localCount.getAndAdd(paramInt);
    }
    this.size += paramInt;
    return i;
  }
  
  public int remove(Object paramObject, int paramInt)
  {
    if (paramInt == 0) {
      return count(paramObject);
    }
    Preconditions.checkArgument(paramInt > 0, "occurrences cannot be negative: %s", new Object[] { Integer.valueOf(paramInt) });
    Count localCount = (Count)this.backingMap.get(paramObject);
    if (localCount == null) {
      return 0;
    }
    int i = localCount.get();
    int j;
    if (i > paramInt)
    {
      j = paramInt;
    }
    else
    {
      j = i;
      this.backingMap.remove(paramObject);
    }
    localCount.addAndGet(-j);
    this.size -= j;
    return i;
  }
  
  public int setCount(Object paramObject, int paramInt)
  {
    Multisets.checkNonnegative(paramInt, "count");
    Count localCount;
    int i;
    if (paramInt == 0)
    {
      localCount = (Count)this.backingMap.remove(paramObject);
      i = getAndSet(localCount, paramInt);
    }
    else
    {
      localCount = (Count)this.backingMap.get(paramObject);
      i = getAndSet(localCount, paramInt);
      if (localCount == null) {
        this.backingMap.put(paramObject, new Count(paramInt));
      }
    }
    this.size += paramInt - i;
    return i;
  }
  
  private static int getAndSet(Count paramCount, int paramInt)
  {
    if (paramCount == null) {
      return 0;
    }
    return paramCount.getAndSet(paramInt);
  }
  
  @GwtIncompatible("java.io.ObjectStreamException")
  private void readObjectNoData()
    throws ObjectStreamException
  {
    throw new InvalidObjectException("Stream data required");
  }
  
  private class MapBasedMultisetIterator
    implements Iterator
  {
    final Iterator entryIterator = AbstractMapBasedMultiset.this.backingMap.entrySet().iterator();
    Map.Entry currentEntry;
    int occurrencesLeft;
    boolean canRemove;
    
    MapBasedMultisetIterator() {}
    
    public boolean hasNext()
    {
      return (this.occurrencesLeft > 0) || (this.entryIterator.hasNext());
    }
    
    public Object next()
    {
      if (this.occurrencesLeft == 0)
      {
        this.currentEntry = ((Map.Entry)this.entryIterator.next());
        this.occurrencesLeft = ((Count)this.currentEntry.getValue()).get();
      }
      this.occurrencesLeft -= 1;
      this.canRemove = true;
      return this.currentEntry.getKey();
    }
    
    public void remove()
    {
      Preconditions.checkState(this.canRemove, "no calls to next() since the last call to remove()");
      int i = ((Count)this.currentEntry.getValue()).get();
      if (i <= 0) {
        throw new ConcurrentModificationException();
      }
      if (((Count)this.currentEntry.getValue()).addAndGet(-1) == 0) {
        this.entryIterator.remove();
      }
      AbstractMapBasedMultiset.access$110(AbstractMapBasedMultiset.this);
      this.canRemove = false;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\AbstractMapBasedMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */