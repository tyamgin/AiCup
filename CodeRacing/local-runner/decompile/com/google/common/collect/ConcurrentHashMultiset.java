package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class ConcurrentHashMultiset
  extends AbstractMultiset
  implements Serializable
{
  private final transient ConcurrentMap countMap;
  private transient EntrySet entrySet;
  private static final long serialVersionUID = 1L;
  
  public static ConcurrentHashMultiset create()
  {
    return new ConcurrentHashMultiset(new ConcurrentHashMap());
  }
  
  public static ConcurrentHashMultiset create(Iterable paramIterable)
  {
    ConcurrentHashMultiset localConcurrentHashMultiset = create();
    Iterables.addAll(localConcurrentHashMultiset, paramIterable);
    return localConcurrentHashMultiset;
  }
  
  @Beta
  public static ConcurrentHashMultiset create(GenericMapMaker paramGenericMapMaker)
  {
    return new ConcurrentHashMultiset(paramGenericMapMaker.makeMap());
  }
  
  @VisibleForTesting
  ConcurrentHashMultiset(ConcurrentMap paramConcurrentMap)
  {
    Preconditions.checkArgument(paramConcurrentMap.isEmpty());
    this.countMap = paramConcurrentMap;
  }
  
  public int count(Object paramObject)
  {
    AtomicInteger localAtomicInteger = (AtomicInteger)Maps.safeGet(this.countMap, paramObject);
    return localAtomicInteger == null ? 0 : localAtomicInteger.get();
  }
  
  public int size()
  {
    long l = 0L;
    Iterator localIterator = this.countMap.values().iterator();
    while (localIterator.hasNext())
    {
      AtomicInteger localAtomicInteger = (AtomicInteger)localIterator.next();
      l += localAtomicInteger.get();
    }
    return Ints.saturatedCast(l);
  }
  
  public Object[] toArray()
  {
    return snapshot().toArray();
  }
  
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    return snapshot().toArray(paramArrayOfObject);
  }
  
  private List snapshot()
  {
    ArrayList localArrayList = Lists.newArrayListWithExpectedSize(size());
    Iterator localIterator = entrySet().iterator();
    while (localIterator.hasNext())
    {
      Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
      Object localObject = localEntry.getElement();
      for (int i = localEntry.getCount(); i > 0; i--) {
        localArrayList.add(localObject);
      }
    }
    return localArrayList;
  }
  
  public int add(Object paramObject, int paramInt)
  {
    Preconditions.checkNotNull(paramObject);
    if (paramInt == 0) {
      return count(paramObject);
    }
    Preconditions.checkArgument(paramInt > 0, "Invalid occurrences: %s", new Object[] { Integer.valueOf(paramInt) });
    for (;;)
    {
      AtomicInteger localAtomicInteger1 = (AtomicInteger)Maps.safeGet(this.countMap, paramObject);
      if (localAtomicInteger1 == null)
      {
        localAtomicInteger1 = (AtomicInteger)this.countMap.putIfAbsent(paramObject, new AtomicInteger(paramInt));
        if (localAtomicInteger1 == null) {
          return 0;
        }
      }
      for (;;)
      {
        int i = localAtomicInteger1.get();
        if (i != 0) {
          try
          {
            int j = IntMath.checkedAdd(i, paramInt);
            if (localAtomicInteger1.compareAndSet(i, j)) {
              return i;
            }
          }
          catch (ArithmeticException localArithmeticException)
          {
            throw new IllegalArgumentException("Overflow adding " + paramInt + " occurrences to a count of " + i);
          }
        }
        AtomicInteger localAtomicInteger2 = new AtomicInteger(paramInt);
        if ((this.countMap.putIfAbsent(paramObject, localAtomicInteger2) != null) && (!this.countMap.replace(paramObject, localAtomicInteger1, localAtomicInteger2))) {
          break;
        }
        return 0;
      }
    }
  }
  
  public int remove(Object paramObject, int paramInt)
  {
    if (paramInt == 0) {
      return count(paramObject);
    }
    Preconditions.checkArgument(paramInt > 0, "Invalid occurrences: %s", new Object[] { Integer.valueOf(paramInt) });
    AtomicInteger localAtomicInteger = (AtomicInteger)Maps.safeGet(this.countMap, paramObject);
    if (localAtomicInteger == null) {
      return 0;
    }
    for (;;)
    {
      int i = localAtomicInteger.get();
      if (i != 0)
      {
        int j = Math.max(0, i - paramInt);
        if (localAtomicInteger.compareAndSet(i, j))
        {
          if (j == 0) {
            this.countMap.remove(paramObject, localAtomicInteger);
          }
          return i;
        }
      }
      else
      {
        return 0;
      }
    }
  }
  
  public boolean removeExactly(Object paramObject, int paramInt)
  {
    if (paramInt == 0) {
      return true;
    }
    Preconditions.checkArgument(paramInt > 0, "Invalid occurrences: %s", new Object[] { Integer.valueOf(paramInt) });
    AtomicInteger localAtomicInteger = (AtomicInteger)Maps.safeGet(this.countMap, paramObject);
    if (localAtomicInteger == null) {
      return false;
    }
    for (;;)
    {
      int i = localAtomicInteger.get();
      if (i < paramInt) {
        return false;
      }
      int j = i - paramInt;
      if (localAtomicInteger.compareAndSet(i, j))
      {
        if (j == 0) {
          this.countMap.remove(paramObject, localAtomicInteger);
        }
        return true;
      }
    }
  }
  
  public int setCount(Object paramObject, int paramInt)
  {
    Preconditions.checkNotNull(paramObject);
    Multisets.checkNonnegative(paramInt, "count");
    for (;;)
    {
      AtomicInteger localAtomicInteger1 = (AtomicInteger)Maps.safeGet(this.countMap, paramObject);
      if (localAtomicInteger1 == null)
      {
        if (paramInt == 0) {
          return 0;
        }
        localAtomicInteger1 = (AtomicInteger)this.countMap.putIfAbsent(paramObject, new AtomicInteger(paramInt));
        if (localAtomicInteger1 == null) {
          return 0;
        }
      }
      for (;;)
      {
        int i = localAtomicInteger1.get();
        if (i == 0)
        {
          if (paramInt == 0) {
            return 0;
          }
          AtomicInteger localAtomicInteger2 = new AtomicInteger(paramInt);
          if ((this.countMap.putIfAbsent(paramObject, localAtomicInteger2) == null) || (this.countMap.replace(paramObject, localAtomicInteger1, localAtomicInteger2))) {
            return 0;
          }
          break;
        }
        if (localAtomicInteger1.compareAndSet(i, paramInt))
        {
          if (paramInt == 0) {
            this.countMap.remove(paramObject, localAtomicInteger1);
          }
          return i;
        }
      }
    }
  }
  
  public boolean setCount(Object paramObject, int paramInt1, int paramInt2)
  {
    Preconditions.checkNotNull(paramObject);
    Multisets.checkNonnegative(paramInt1, "oldCount");
    Multisets.checkNonnegative(paramInt2, "newCount");
    AtomicInteger localAtomicInteger1 = (AtomicInteger)Maps.safeGet(this.countMap, paramObject);
    if (localAtomicInteger1 == null)
    {
      if (paramInt1 != 0) {
        return false;
      }
      if (paramInt2 == 0) {
        return true;
      }
      return this.countMap.putIfAbsent(paramObject, new AtomicInteger(paramInt2)) == null;
    }
    int i = localAtomicInteger1.get();
    if (i == paramInt1)
    {
      if (i == 0)
      {
        if (paramInt2 == 0)
        {
          this.countMap.remove(paramObject, localAtomicInteger1);
          return true;
        }
        AtomicInteger localAtomicInteger2 = new AtomicInteger(paramInt2);
        return (this.countMap.putIfAbsent(paramObject, localAtomicInteger2) == null) || (this.countMap.replace(paramObject, localAtomicInteger1, localAtomicInteger2));
      }
      if (localAtomicInteger1.compareAndSet(i, paramInt2))
      {
        if (paramInt2 == 0) {
          this.countMap.remove(paramObject, localAtomicInteger1);
        }
        return true;
      }
    }
    return false;
  }
  
  Set createElementSet()
  {
    final Set localSet = this.countMap.keySet();
    new ForwardingSet()
    {
      protected Set delegate()
      {
        return localSet;
      }
      
      public boolean contains(Object paramAnonymousObject)
      {
        return (paramAnonymousObject != null) && (Collections2.safeContains(localSet, paramAnonymousObject));
      }
      
      public boolean containsAll(Collection paramAnonymousCollection)
      {
        return standardContainsAll(paramAnonymousCollection);
      }
      
      public boolean remove(Object paramAnonymousObject)
      {
        return (paramAnonymousObject != null) && (Collections2.safeRemove(localSet, paramAnonymousObject));
      }
      
      public boolean removeAll(Collection paramAnonymousCollection)
      {
        return standardRemoveAll(paramAnonymousCollection);
      }
    };
  }
  
  public Set entrySet()
  {
    EntrySet localEntrySet = this.entrySet;
    if (localEntrySet == null) {
      this.entrySet = (localEntrySet = new EntrySet(null));
    }
    return localEntrySet;
  }
  
  int distinctElements()
  {
    return this.countMap.size();
  }
  
  public boolean isEmpty()
  {
    return this.countMap.isEmpty();
  }
  
  Iterator entryIterator()
  {
    final AbstractIterator local2 = new AbstractIterator()
    {
      private Iterator mapEntries = ConcurrentHashMultiset.this.countMap.entrySet().iterator();
      
      protected Multiset.Entry computeNext()
      {
        for (;;)
        {
          if (!this.mapEntries.hasNext()) {
            return (Multiset.Entry)endOfData();
          }
          Map.Entry localEntry = (Map.Entry)this.mapEntries.next();
          int i = ((AtomicInteger)localEntry.getValue()).get();
          if (i != 0) {
            return Multisets.immutableEntry(localEntry.getKey(), i);
          }
        }
      }
    };
    new ForwardingIterator()
    {
      private Multiset.Entry last;
      
      protected Iterator delegate()
      {
        return local2;
      }
      
      public Multiset.Entry next()
      {
        this.last = ((Multiset.Entry)super.next());
        return this.last;
      }
      
      public void remove()
      {
        Preconditions.checkState(this.last != null);
        ConcurrentHashMultiset.this.setCount(this.last.getElement(), 0);
        this.last = null;
      }
    };
  }
  
  public void clear()
  {
    this.countMap.clear();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(this.countMap);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    ConcurrentMap localConcurrentMap = (ConcurrentMap)paramObjectInputStream.readObject();
    FieldSettersHolder.COUNT_MAP_FIELD_SETTER.set(this, localConcurrentMap);
  }
  
  private class EntrySet
    extends AbstractMultiset.EntrySet
  {
    private EntrySet()
    {
      super();
    }
    
    ConcurrentHashMultiset multiset()
    {
      return ConcurrentHashMultiset.this;
    }
    
    public Object[] toArray()
    {
      return snapshot().toArray();
    }
    
    public Object[] toArray(Object[] paramArrayOfObject)
    {
      return snapshot().toArray(paramArrayOfObject);
    }
    
    private List snapshot()
    {
      ArrayList localArrayList = Lists.newArrayListWithExpectedSize(size());
      Iterators.addAll(localArrayList, iterator());
      return localArrayList;
    }
  }
  
  private static class FieldSettersHolder
  {
    static final Serialization.FieldSetter COUNT_MAP_FIELD_SETTER = Serialization.getFieldSetter(ConcurrentHashMultiset.class, "countMap");
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ConcurrentHashMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */