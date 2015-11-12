package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible(emulated=true)
final class Synchronized
{
  private static Collection collection(Collection paramCollection, Object paramObject)
  {
    return new SynchronizedCollection(paramCollection, paramObject, null);
  }
  
  @VisibleForTesting
  static Set set(Set paramSet, Object paramObject)
  {
    return new SynchronizedSet(paramSet, paramObject);
  }
  
  private static SortedSet sortedSet(SortedSet paramSortedSet, Object paramObject)
  {
    return new SynchronizedSortedSet(paramSortedSet, paramObject);
  }
  
  private static List list(List paramList, Object paramObject)
  {
    return (paramList instanceof RandomAccess) ? new SynchronizedRandomAccessList(paramList, paramObject) : new SynchronizedList(paramList, paramObject);
  }
  
  static Multiset multiset(Multiset paramMultiset, Object paramObject)
  {
    if (((paramMultiset instanceof SynchronizedMultiset)) || ((paramMultiset instanceof ImmutableMultiset))) {
      return paramMultiset;
    }
    return new SynchronizedMultiset(paramMultiset, paramObject);
  }
  
  static Multimap multimap(Multimap paramMultimap, Object paramObject)
  {
    if (((paramMultimap instanceof SynchronizedMultimap)) || ((paramMultimap instanceof ImmutableMultimap))) {
      return paramMultimap;
    }
    return new SynchronizedMultimap(paramMultimap, paramObject);
  }
  
  static ListMultimap listMultimap(ListMultimap paramListMultimap, Object paramObject)
  {
    if (((paramListMultimap instanceof SynchronizedListMultimap)) || ((paramListMultimap instanceof ImmutableListMultimap))) {
      return paramListMultimap;
    }
    return new SynchronizedListMultimap(paramListMultimap, paramObject);
  }
  
  static SetMultimap setMultimap(SetMultimap paramSetMultimap, Object paramObject)
  {
    if (((paramSetMultimap instanceof SynchronizedSetMultimap)) || ((paramSetMultimap instanceof ImmutableSetMultimap))) {
      return paramSetMultimap;
    }
    return new SynchronizedSetMultimap(paramSetMultimap, paramObject);
  }
  
  static SortedSetMultimap sortedSetMultimap(SortedSetMultimap paramSortedSetMultimap, Object paramObject)
  {
    if ((paramSortedSetMultimap instanceof SynchronizedSortedSetMultimap)) {
      return paramSortedSetMultimap;
    }
    return new SynchronizedSortedSetMultimap(paramSortedSetMultimap, paramObject);
  }
  
  private static Collection typePreservingCollection(Collection paramCollection, Object paramObject)
  {
    if ((paramCollection instanceof SortedSet)) {
      return sortedSet((SortedSet)paramCollection, paramObject);
    }
    if ((paramCollection instanceof Set)) {
      return set((Set)paramCollection, paramObject);
    }
    if ((paramCollection instanceof List)) {
      return list((List)paramCollection, paramObject);
    }
    return collection(paramCollection, paramObject);
  }
  
  private static Set typePreservingSet(Set paramSet, Object paramObject)
  {
    if ((paramSet instanceof SortedSet)) {
      return sortedSet((SortedSet)paramSet, paramObject);
    }
    return set(paramSet, paramObject);
  }
  
  @VisibleForTesting
  static Map map(Map paramMap, Object paramObject)
  {
    return new SynchronizedMap(paramMap, paramObject);
  }
  
  static SortedMap sortedMap(SortedMap paramSortedMap, Object paramObject)
  {
    return new SynchronizedSortedMap(paramSortedMap, paramObject);
  }
  
  static BiMap biMap(BiMap paramBiMap, Object paramObject)
  {
    if (((paramBiMap instanceof SynchronizedBiMap)) || ((paramBiMap instanceof ImmutableBiMap))) {
      return paramBiMap;
    }
    return new SynchronizedBiMap(paramBiMap, paramObject, null, null);
  }
  
  @GwtIncompatible("NavigableSet")
  static NavigableSet navigableSet(NavigableSet paramNavigableSet, Object paramObject)
  {
    return new SynchronizedNavigableSet(paramNavigableSet, paramObject);
  }
  
  @GwtIncompatible("NavigableSet")
  static NavigableSet navigableSet(NavigableSet paramNavigableSet)
  {
    return navigableSet(paramNavigableSet, null);
  }
  
  @GwtIncompatible("NavigableMap")
  static NavigableMap navigableMap(NavigableMap paramNavigableMap)
  {
    return navigableMap(paramNavigableMap, null);
  }
  
  @GwtIncompatible("NavigableMap")
  static NavigableMap navigableMap(NavigableMap paramNavigableMap, Object paramObject)
  {
    return new SynchronizedNavigableMap(paramNavigableMap, paramObject);
  }
  
  @GwtIncompatible("works but is needed only for NavigableMap")
  private static Map.Entry nullableSynchronizedEntry(Map.Entry paramEntry, Object paramObject)
  {
    if (paramEntry == null) {
      return null;
    }
    return new SynchronizedEntry(paramEntry, paramObject);
  }
  
  static Queue queue(Queue paramQueue, Object paramObject)
  {
    return (paramQueue instanceof SynchronizedQueue) ? paramQueue : new SynchronizedQueue(paramQueue, paramObject);
  }
  
  private static class SynchronizedQueue
    extends Synchronized.SynchronizedCollection
    implements Queue
  {
    private static final long serialVersionUID = 0L;
    
    SynchronizedQueue(Queue paramQueue, Object paramObject)
    {
      super(paramObject, null);
    }
    
    Queue delegate()
    {
      return (Queue)super.delegate();
    }
    
    /* Error */
    public Object element()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedQueue:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedQueue:delegate	()Ljava/util/Queue;
      //   11: invokeinterface 14 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedQueue
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public boolean offer(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedQueue:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedQueue:delegate	()Ljava/util/Queue;
      //   11: aload_1
      //   12: invokeinterface 15 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedQueue
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public Object peek()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedQueue:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedQueue:delegate	()Ljava/util/Queue;
      //   11: invokeinterface 16 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedQueue
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public Object poll()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedQueue:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedQueue:delegate	()Ljava/util/Queue;
      //   11: invokeinterface 17 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedQueue
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public Object remove()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedQueue:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedQueue:delegate	()Ljava/util/Queue;
      //   11: invokeinterface 18 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedQueue
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
  }
  
  @GwtIncompatible("works but is needed only for NavigableMap")
  private static class SynchronizedEntry
    extends Synchronized.SynchronizedObject
    implements Map.Entry
  {
    private static final long serialVersionUID = 0L;
    
    SynchronizedEntry(Map.Entry paramEntry, Object paramObject)
    {
      super(paramObject);
    }
    
    Map.Entry delegate()
    {
      return (Map.Entry)super.delegate();
    }
    
    /* Error */
    public boolean equals(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedEntry:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 11	com/google/common/collect/Synchronized$SynchronizedEntry:delegate	()Ljava/util/Map$Entry;
      //   11: aload_1
      //   12: invokeinterface 14 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedEntry
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public int hashCode()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedEntry:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 11	com/google/common/collect/Synchronized$SynchronizedEntry:delegate	()Ljava/util/Map$Entry;
      //   11: invokeinterface 17 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: ireturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedEntry
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public Object getKey()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedEntry:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 11	com/google/common/collect/Synchronized$SynchronizedEntry:delegate	()Ljava/util/Map$Entry;
      //   11: invokeinterface 15 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedEntry
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public Object getValue()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedEntry:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 11	com/google/common/collect/Synchronized$SynchronizedEntry:delegate	()Ljava/util/Map$Entry;
      //   11: invokeinterface 16 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedEntry
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public Object setValue(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedEntry:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 11	com/google/common/collect/Synchronized$SynchronizedEntry:delegate	()Ljava/util/Map$Entry;
      //   11: aload_1
      //   12: invokeinterface 18 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedEntry
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
  }
  
  @GwtIncompatible("NavigableMap")
  @VisibleForTesting
  static class SynchronizedNavigableMap
    extends Synchronized.SynchronizedSortedMap
    implements NavigableMap
  {
    transient NavigableSet descendingKeySet;
    transient NavigableMap descendingMap;
    transient NavigableSet navigableKeySet;
    private static final long serialVersionUID = 0L;
    
    SynchronizedNavigableMap(NavigableMap paramNavigableMap, Object paramObject)
    {
      super(paramObject);
    }
    
    NavigableMap delegate()
    {
      return (NavigableMap)super.delegate();
    }
    
    /* Error */
    public Map.Entry ceilingEntry(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 18	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
      //   11: aload_1
      //   12: invokeinterface 25 2 0
      //   17: aload_0
      //   18: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   21: invokestatic 15	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
      //   24: aload_2
      //   25: monitorexit
      //   26: areturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedNavigableMap
      //   0	32	1	paramObject	Object
      //   5	24	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public Object ceilingKey(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 18	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
      //   11: aload_1
      //   12: invokeinterface 26 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedNavigableMap
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    public NavigableSet descendingKeySet()
    {
      synchronized (this.mutex)
      {
        if (this.descendingKeySet == null) {
          return this.descendingKeySet = Synchronized.navigableSet(delegate().descendingKeySet(), this.mutex);
        }
        return this.descendingKeySet;
      }
    }
    
    public NavigableMap descendingMap()
    {
      synchronized (this.mutex)
      {
        if (this.descendingMap == null) {
          return this.descendingMap = Synchronized.navigableMap(delegate().descendingMap(), this.mutex);
        }
        return this.descendingMap;
      }
    }
    
    /* Error */
    public Map.Entry firstEntry()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 18	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
      //   11: invokeinterface 29 1 0
      //   16: aload_0
      //   17: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   20: invokestatic 15	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
      //   23: aload_1
      //   24: monitorexit
      //   25: areturn
      //   26: astore_2
      //   27: aload_1
      //   28: monitorexit
      //   29: aload_2
      //   30: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	31	0	this	SynchronizedNavigableMap
      //   5	23	1	Ljava/lang/Object;	Object
      //   26	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	25	26	finally
      //   26	29	26	finally
    }
    
    /* Error */
    public Map.Entry floorEntry(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 18	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
      //   11: aload_1
      //   12: invokeinterface 30 2 0
      //   17: aload_0
      //   18: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   21: invokestatic 15	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
      //   24: aload_2
      //   25: monitorexit
      //   26: areturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedNavigableMap
      //   0	32	1	paramObject	Object
      //   5	24	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public Object floorKey(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 18	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
      //   11: aload_1
      //   12: invokeinterface 31 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedNavigableMap
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public NavigableMap headMap(Object paramObject, boolean paramBoolean)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 18	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
      //   11: aload_1
      //   12: iload_2
      //   13: invokeinterface 32 3 0
      //   18: aload_0
      //   19: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   22: invokestatic 16	com/google/common/collect/Synchronized:navigableMap	(Ljava/util/NavigableMap;Ljava/lang/Object;)Ljava/util/NavigableMap;
      //   25: aload_3
      //   26: monitorexit
      //   27: areturn
      //   28: astore 4
      //   30: aload_3
      //   31: monitorexit
      //   32: aload 4
      //   34: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	35	0	this	SynchronizedNavigableMap
      //   0	35	1	paramObject	Object
      //   0	35	2	paramBoolean	boolean
      //   5	26	3	Ljava/lang/Object;	Object
      //   28	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	27	28	finally
      //   28	32	28	finally
    }
    
    /* Error */
    public Map.Entry higherEntry(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 18	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
      //   11: aload_1
      //   12: invokeinterface 33 2 0
      //   17: aload_0
      //   18: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   21: invokestatic 15	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
      //   24: aload_2
      //   25: monitorexit
      //   26: areturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedNavigableMap
      //   0	32	1	paramObject	Object
      //   5	24	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public Object higherKey(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 18	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
      //   11: aload_1
      //   12: invokeinterface 34 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedNavigableMap
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public Map.Entry lastEntry()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 18	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
      //   11: invokeinterface 35 1 0
      //   16: aload_0
      //   17: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   20: invokestatic 15	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
      //   23: aload_1
      //   24: monitorexit
      //   25: areturn
      //   26: astore_2
      //   27: aload_1
      //   28: monitorexit
      //   29: aload_2
      //   30: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	31	0	this	SynchronizedNavigableMap
      //   5	23	1	Ljava/lang/Object;	Object
      //   26	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	25	26	finally
      //   26	29	26	finally
    }
    
    /* Error */
    public Map.Entry lowerEntry(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 18	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
      //   11: aload_1
      //   12: invokeinterface 36 2 0
      //   17: aload_0
      //   18: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   21: invokestatic 15	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
      //   24: aload_2
      //   25: monitorexit
      //   26: areturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedNavigableMap
      //   0	32	1	paramObject	Object
      //   5	24	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public Object lowerKey(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 18	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
      //   11: aload_1
      //   12: invokeinterface 37 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedNavigableMap
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    public Set keySet()
    {
      return navigableKeySet();
    }
    
    public NavigableSet navigableKeySet()
    {
      synchronized (this.mutex)
      {
        if (this.navigableKeySet == null) {
          return this.navigableKeySet = Synchronized.navigableSet(delegate().navigableKeySet(), this.mutex);
        }
        return this.navigableKeySet;
      }
    }
    
    /* Error */
    public Map.Entry pollFirstEntry()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 18	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
      //   11: invokeinterface 39 1 0
      //   16: aload_0
      //   17: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   20: invokestatic 15	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
      //   23: aload_1
      //   24: monitorexit
      //   25: areturn
      //   26: astore_2
      //   27: aload_1
      //   28: monitorexit
      //   29: aload_2
      //   30: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	31	0	this	SynchronizedNavigableMap
      //   5	23	1	Ljava/lang/Object;	Object
      //   26	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	25	26	finally
      //   26	29	26	finally
    }
    
    /* Error */
    public Map.Entry pollLastEntry()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 18	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
      //   11: invokeinterface 40 1 0
      //   16: aload_0
      //   17: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   20: invokestatic 15	com/google/common/collect/Synchronized:access$700	(Ljava/util/Map$Entry;Ljava/lang/Object;)Ljava/util/Map$Entry;
      //   23: aload_1
      //   24: monitorexit
      //   25: areturn
      //   26: astore_2
      //   27: aload_1
      //   28: monitorexit
      //   29: aload_2
      //   30: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	31	0	this	SynchronizedNavigableMap
      //   5	23	1	Ljava/lang/Object;	Object
      //   26	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	25	26	finally
      //   26	29	26	finally
    }
    
    /* Error */
    public NavigableMap subMap(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore 5
      //   7: monitorenter
      //   8: aload_0
      //   9: invokevirtual 18	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
      //   12: aload_1
      //   13: iload_2
      //   14: aload_3
      //   15: iload 4
      //   17: invokeinterface 41 5 0
      //   22: aload_0
      //   23: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   26: invokestatic 16	com/google/common/collect/Synchronized:navigableMap	(Ljava/util/NavigableMap;Ljava/lang/Object;)Ljava/util/NavigableMap;
      //   29: aload 5
      //   31: monitorexit
      //   32: areturn
      //   33: astore 6
      //   35: aload 5
      //   37: monitorexit
      //   38: aload 6
      //   40: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	41	0	this	SynchronizedNavigableMap
      //   0	41	1	paramObject1	Object
      //   0	41	2	paramBoolean1	boolean
      //   0	41	3	paramObject2	Object
      //   0	41	4	paramBoolean2	boolean
      //   5	31	5	Ljava/lang/Object;	Object
      //   33	6	6	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   8	32	33	finally
      //   33	38	33	finally
    }
    
    /* Error */
    public NavigableMap tailMap(Object paramObject, boolean paramBoolean)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 18	com/google/common/collect/Synchronized$SynchronizedNavigableMap:delegate	()Ljava/util/NavigableMap;
      //   11: aload_1
      //   12: iload_2
      //   13: invokeinterface 42 3 0
      //   18: aload_0
      //   19: getfield 13	com/google/common/collect/Synchronized$SynchronizedNavigableMap:mutex	Ljava/lang/Object;
      //   22: invokestatic 16	com/google/common/collect/Synchronized:navigableMap	(Ljava/util/NavigableMap;Ljava/lang/Object;)Ljava/util/NavigableMap;
      //   25: aload_3
      //   26: monitorexit
      //   27: areturn
      //   28: astore 4
      //   30: aload_3
      //   31: monitorexit
      //   32: aload 4
      //   34: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	35	0	this	SynchronizedNavigableMap
      //   0	35	1	paramObject	Object
      //   0	35	2	paramBoolean	boolean
      //   5	26	3	Ljava/lang/Object;	Object
      //   28	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	27	28	finally
      //   28	32	28	finally
    }
    
    public SortedMap headMap(Object paramObject)
    {
      return headMap(paramObject, false);
    }
    
    public SortedMap subMap(Object paramObject1, Object paramObject2)
    {
      return subMap(paramObject1, true, paramObject2, false);
    }
    
    public SortedMap tailMap(Object paramObject)
    {
      return tailMap(paramObject, true);
    }
  }
  
  @GwtIncompatible("NavigableSet")
  @VisibleForTesting
  static class SynchronizedNavigableSet
    extends Synchronized.SynchronizedSortedSet
    implements NavigableSet
  {
    transient NavigableSet descendingSet;
    private static final long serialVersionUID = 0L;
    
    SynchronizedNavigableSet(NavigableSet paramNavigableSet, Object paramObject)
    {
      super(paramObject);
    }
    
    NavigableSet delegate()
    {
      return (NavigableSet)super.delegate();
    }
    
    /* Error */
    public Object ceiling(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 12	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
      //   11: aload_1
      //   12: invokeinterface 18 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedNavigableSet
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    public Iterator descendingIterator()
    {
      return delegate().descendingIterator();
    }
    
    public NavigableSet descendingSet()
    {
      synchronized (this.mutex)
      {
        if (this.descendingSet == null)
        {
          NavigableSet localNavigableSet = Synchronized.navigableSet(delegate().descendingSet(), this.mutex);
          this.descendingSet = localNavigableSet;
          return localNavigableSet;
        }
        return this.descendingSet;
      }
    }
    
    /* Error */
    public Object floor(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 12	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
      //   11: aload_1
      //   12: invokeinterface 21 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedNavigableSet
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public NavigableSet headSet(Object paramObject, boolean paramBoolean)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 12	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
      //   11: aload_1
      //   12: iload_2
      //   13: invokeinterface 22 3 0
      //   18: aload_0
      //   19: getfield 10	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
      //   22: invokestatic 11	com/google/common/collect/Synchronized:navigableSet	(Ljava/util/NavigableSet;Ljava/lang/Object;)Ljava/util/NavigableSet;
      //   25: aload_3
      //   26: monitorexit
      //   27: areturn
      //   28: astore 4
      //   30: aload_3
      //   31: monitorexit
      //   32: aload 4
      //   34: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	35	0	this	SynchronizedNavigableSet
      //   0	35	1	paramObject	Object
      //   0	35	2	paramBoolean	boolean
      //   5	26	3	Ljava/lang/Object;	Object
      //   28	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	27	28	finally
      //   28	32	28	finally
    }
    
    /* Error */
    public Object higher(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 12	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
      //   11: aload_1
      //   12: invokeinterface 23 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedNavigableSet
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public Object lower(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 12	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
      //   11: aload_1
      //   12: invokeinterface 24 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedNavigableSet
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public Object pollFirst()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 12	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
      //   11: invokeinterface 25 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedNavigableSet
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public Object pollLast()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 12	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
      //   11: invokeinterface 26 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedNavigableSet
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public NavigableSet subSet(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore 5
      //   7: monitorenter
      //   8: aload_0
      //   9: invokevirtual 12	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
      //   12: aload_1
      //   13: iload_2
      //   14: aload_3
      //   15: iload 4
      //   17: invokeinterface 27 5 0
      //   22: aload_0
      //   23: getfield 10	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
      //   26: invokestatic 11	com/google/common/collect/Synchronized:navigableSet	(Ljava/util/NavigableSet;Ljava/lang/Object;)Ljava/util/NavigableSet;
      //   29: aload 5
      //   31: monitorexit
      //   32: areturn
      //   33: astore 6
      //   35: aload 5
      //   37: monitorexit
      //   38: aload 6
      //   40: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	41	0	this	SynchronizedNavigableSet
      //   0	41	1	paramObject1	Object
      //   0	41	2	paramBoolean1	boolean
      //   0	41	3	paramObject2	Object
      //   0	41	4	paramBoolean2	boolean
      //   5	31	5	Ljava/lang/Object;	Object
      //   33	6	6	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   8	32	33	finally
      //   33	38	33	finally
    }
    
    /* Error */
    public NavigableSet tailSet(Object paramObject, boolean paramBoolean)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 12	com/google/common/collect/Synchronized$SynchronizedNavigableSet:delegate	()Ljava/util/NavigableSet;
      //   11: aload_1
      //   12: iload_2
      //   13: invokeinterface 28 3 0
      //   18: aload_0
      //   19: getfield 10	com/google/common/collect/Synchronized$SynchronizedNavigableSet:mutex	Ljava/lang/Object;
      //   22: invokestatic 11	com/google/common/collect/Synchronized:navigableSet	(Ljava/util/NavigableSet;Ljava/lang/Object;)Ljava/util/NavigableSet;
      //   25: aload_3
      //   26: monitorexit
      //   27: areturn
      //   28: astore 4
      //   30: aload_3
      //   31: monitorexit
      //   32: aload 4
      //   34: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	35	0	this	SynchronizedNavigableSet
      //   0	35	1	paramObject	Object
      //   0	35	2	paramBoolean	boolean
      //   5	26	3	Ljava/lang/Object;	Object
      //   28	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	27	28	finally
      //   28	32	28	finally
    }
    
    public SortedSet headSet(Object paramObject)
    {
      return headSet(paramObject, false);
    }
    
    public SortedSet subSet(Object paramObject1, Object paramObject2)
    {
      return subSet(paramObject1, true, paramObject2, false);
    }
    
    public SortedSet tailSet(Object paramObject)
    {
      return tailSet(paramObject, true);
    }
  }
  
  private static class SynchronizedAsMapValues
    extends Synchronized.SynchronizedCollection
  {
    private static final long serialVersionUID = 0L;
    
    SynchronizedAsMapValues(Collection paramCollection, Object paramObject)
    {
      super(paramObject, null);
    }
    
    public Iterator iterator()
    {
      final Iterator localIterator = super.iterator();
      new ForwardingIterator()
      {
        protected Iterator delegate()
        {
          return localIterator;
        }
        
        public Collection next()
        {
          return Synchronized.typePreservingCollection((Collection)super.next(), Synchronized.SynchronizedAsMapValues.this.mutex);
        }
      };
    }
  }
  
  private static class SynchronizedAsMap
    extends Synchronized.SynchronizedMap
  {
    transient Set asMapEntrySet;
    transient Collection asMapValues;
    private static final long serialVersionUID = 0L;
    
    SynchronizedAsMap(Map paramMap, Object paramObject)
    {
      super(paramObject);
    }
    
    public Collection get(Object paramObject)
    {
      synchronized (this.mutex)
      {
        Collection localCollection = (Collection)super.get(paramObject);
        return localCollection == null ? null : Synchronized.typePreservingCollection(localCollection, this.mutex);
      }
    }
    
    public Set entrySet()
    {
      synchronized (this.mutex)
      {
        if (this.asMapEntrySet == null) {
          this.asMapEntrySet = new Synchronized.SynchronizedAsMapEntries(delegate().entrySet(), this.mutex);
        }
        return this.asMapEntrySet;
      }
    }
    
    public Collection values()
    {
      synchronized (this.mutex)
      {
        if (this.asMapValues == null) {
          this.asMapValues = new Synchronized.SynchronizedAsMapValues(delegate().values(), this.mutex);
        }
        return this.asMapValues;
      }
    }
    
    public boolean containsValue(Object paramObject)
    {
      return values().contains(paramObject);
    }
  }
  
  @VisibleForTesting
  static class SynchronizedBiMap
    extends Synchronized.SynchronizedMap
    implements BiMap, Serializable
  {
    private transient Set valueSet;
    private transient BiMap inverse;
    private static final long serialVersionUID = 0L;
    
    private SynchronizedBiMap(BiMap paramBiMap1, Object paramObject, BiMap paramBiMap2)
    {
      super(paramObject);
      this.inverse = paramBiMap2;
    }
    
    BiMap delegate()
    {
      return (BiMap)super.delegate();
    }
    
    public Set values()
    {
      synchronized (this.mutex)
      {
        if (this.valueSet == null) {
          this.valueSet = Synchronized.set(delegate().values(), this.mutex);
        }
        return this.valueSet;
      }
    }
    
    /* Error */
    public Object forcePut(Object paramObject1, Object paramObject2)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	com/google/common/collect/Synchronized$SynchronizedBiMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 16	com/google/common/collect/Synchronized$SynchronizedBiMap:delegate	()Lcom/google/common/collect/BiMap;
      //   11: aload_1
      //   12: aload_2
      //   13: invokeinterface 20 3 0
      //   18: aload_3
      //   19: monitorexit
      //   20: areturn
      //   21: astore 4
      //   23: aload_3
      //   24: monitorexit
      //   25: aload 4
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedBiMap
      //   0	28	1	paramObject1	Object
      //   0	28	2	paramObject2	Object
      //   5	19	3	Ljava/lang/Object;	Object
      //   21	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	20	21	finally
      //   21	25	21	finally
    }
    
    public BiMap inverse()
    {
      synchronized (this.mutex)
      {
        if (this.inverse == null) {
          this.inverse = new SynchronizedBiMap(delegate().inverse(), this.mutex, this);
        }
        return this.inverse;
      }
    }
  }
  
  static class SynchronizedSortedMap
    extends Synchronized.SynchronizedMap
    implements SortedMap
  {
    private static final long serialVersionUID = 0L;
    
    SynchronizedSortedMap(SortedMap paramSortedMap, Object paramObject)
    {
      super(paramObject);
    }
    
    SortedMap delegate()
    {
      return (SortedMap)super.delegate();
    }
    
    /* Error */
    public java.util.Comparator comparator()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedSortedMap:delegate	()Ljava/util/SortedMap;
      //   11: invokeinterface 14 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedSortedMap
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public Object firstKey()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedSortedMap:delegate	()Ljava/util/SortedMap;
      //   11: invokeinterface 15 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedSortedMap
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public SortedMap headMap(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedSortedMap:delegate	()Ljava/util/SortedMap;
      //   11: aload_1
      //   12: invokeinterface 16 2 0
      //   17: aload_0
      //   18: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
      //   21: invokestatic 10	com/google/common/collect/Synchronized:sortedMap	(Ljava/util/SortedMap;Ljava/lang/Object;)Ljava/util/SortedMap;
      //   24: aload_2
      //   25: monitorexit
      //   26: areturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedSortedMap
      //   0	32	1	paramObject	Object
      //   5	24	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public Object lastKey()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedSortedMap:delegate	()Ljava/util/SortedMap;
      //   11: invokeinterface 17 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedSortedMap
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public SortedMap subMap(Object paramObject1, Object paramObject2)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedSortedMap:delegate	()Ljava/util/SortedMap;
      //   11: aload_1
      //   12: aload_2
      //   13: invokeinterface 18 3 0
      //   18: aload_0
      //   19: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
      //   22: invokestatic 10	com/google/common/collect/Synchronized:sortedMap	(Ljava/util/SortedMap;Ljava/lang/Object;)Ljava/util/SortedMap;
      //   25: aload_3
      //   26: monitorexit
      //   27: areturn
      //   28: astore 4
      //   30: aload_3
      //   31: monitorexit
      //   32: aload 4
      //   34: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	35	0	this	SynchronizedSortedMap
      //   0	35	1	paramObject1	Object
      //   0	35	2	paramObject2	Object
      //   5	26	3	Ljava/lang/Object;	Object
      //   28	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	27	28	finally
      //   28	32	28	finally
    }
    
    /* Error */
    public SortedMap tailMap(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedSortedMap:delegate	()Ljava/util/SortedMap;
      //   11: aload_1
      //   12: invokeinterface 19 2 0
      //   17: aload_0
      //   18: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedMap:mutex	Ljava/lang/Object;
      //   21: invokestatic 10	com/google/common/collect/Synchronized:sortedMap	(Ljava/util/SortedMap;Ljava/lang/Object;)Ljava/util/SortedMap;
      //   24: aload_2
      //   25: monitorexit
      //   26: areturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedSortedMap
      //   0	32	1	paramObject	Object
      //   5	24	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	27	finally
      //   27	30	27	finally
    }
  }
  
  private static class SynchronizedMap
    extends Synchronized.SynchronizedObject
    implements Map
  {
    transient Set keySet;
    transient Collection values;
    transient Set entrySet;
    private static final long serialVersionUID = 0L;
    
    SynchronizedMap(Map paramMap, Object paramObject)
    {
      super(paramObject);
    }
    
    Map delegate()
    {
      return (Map)super.delegate();
    }
    
    public void clear()
    {
      synchronized (this.mutex)
      {
        delegate().clear();
      }
    }
    
    /* Error */
    public boolean containsKey(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 16	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
      //   11: aload_1
      //   12: invokeinterface 20 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedMap
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public boolean containsValue(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 16	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
      //   11: aload_1
      //   12: invokeinterface 21 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedMap
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    public Set entrySet()
    {
      synchronized (this.mutex)
      {
        if (this.entrySet == null) {
          this.entrySet = Synchronized.set(delegate().entrySet(), this.mutex);
        }
        return this.entrySet;
      }
    }
    
    /* Error */
    public Object get(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 16	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
      //   11: aload_1
      //   12: invokeinterface 24 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedMap
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public boolean isEmpty()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 16	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
      //   11: invokeinterface 26 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: ireturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedMap
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    public Set keySet()
    {
      synchronized (this.mutex)
      {
        if (this.keySet == null) {
          this.keySet = Synchronized.set(delegate().keySet(), this.mutex);
        }
        return this.keySet;
      }
    }
    
    /* Error */
    public Object put(Object paramObject1, Object paramObject2)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 16	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
      //   11: aload_1
      //   12: aload_2
      //   13: invokeinterface 28 3 0
      //   18: aload_3
      //   19: monitorexit
      //   20: areturn
      //   21: astore 4
      //   23: aload_3
      //   24: monitorexit
      //   25: aload 4
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedMap
      //   0	28	1	paramObject1	Object
      //   0	28	2	paramObject2	Object
      //   5	19	3	Ljava/lang/Object;	Object
      //   21	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	20	21	finally
      //   21	25	21	finally
    }
    
    public void putAll(Map paramMap)
    {
      synchronized (this.mutex)
      {
        delegate().putAll(paramMap);
      }
    }
    
    /* Error */
    public Object remove(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 16	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
      //   11: aload_1
      //   12: invokeinterface 30 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedMap
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public int size()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 16	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
      //   11: invokeinterface 31 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: ireturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedMap
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    public Collection values()
    {
      synchronized (this.mutex)
      {
        if (this.values == null) {
          this.values = Synchronized.collection(delegate().values(), this.mutex);
        }
        return this.values;
      }
    }
    
    /* Error */
    public boolean equals(Object paramObject)
    {
      // Byte code:
      //   0: aload_1
      //   1: aload_0
      //   2: if_acmpne +5 -> 7
      //   5: iconst_1
      //   6: ireturn
      //   7: aload_0
      //   8: getfield 12	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
      //   11: dup
      //   12: astore_2
      //   13: monitorenter
      //   14: aload_0
      //   15: invokevirtual 16	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
      //   18: aload_1
      //   19: invokeinterface 23 2 0
      //   24: aload_2
      //   25: monitorexit
      //   26: ireturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedMap
      //   0	32	1	paramObject	Object
      //   12	17	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   14	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public int hashCode()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	com/google/common/collect/Synchronized$SynchronizedMap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 16	com/google/common/collect/Synchronized$SynchronizedMap:delegate	()Ljava/util/Map;
      //   11: invokeinterface 25 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: ireturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedMap
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
  }
  
  private static class SynchronizedAsMapEntries
    extends Synchronized.SynchronizedSet
  {
    private static final long serialVersionUID = 0L;
    
    SynchronizedAsMapEntries(Set paramSet, Object paramObject)
    {
      super(paramObject);
    }
    
    public Iterator iterator()
    {
      final Iterator localIterator = super.iterator();
      new ForwardingIterator()
      {
        protected Iterator delegate()
        {
          return localIterator;
        }
        
        public Map.Entry next()
        {
          final Map.Entry localEntry = (Map.Entry)super.next();
          new ForwardingMapEntry()
          {
            protected Map.Entry delegate()
            {
              return localEntry;
            }
            
            public Collection getValue()
            {
              return Synchronized.typePreservingCollection((Collection)localEntry.getValue(), Synchronized.SynchronizedAsMapEntries.this.mutex);
            }
          };
        }
      };
    }
    
    /* Error */
    public Object[] toArray()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 19	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 28	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
      //   11: invokestatic 25	com/google/common/collect/ObjectArrays:toArrayImpl	(Ljava/util/Collection;)[Ljava/lang/Object;
      //   14: aload_1
      //   15: monitorexit
      //   16: areturn
      //   17: astore_2
      //   18: aload_1
      //   19: monitorexit
      //   20: aload_2
      //   21: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	22	0	this	SynchronizedAsMapEntries
      //   5	14	1	Ljava/lang/Object;	Object
      //   17	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	16	17	finally
      //   17	20	17	finally
    }
    
    /* Error */
    public Object[] toArray(Object[] paramArrayOfObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 19	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 28	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
      //   11: aload_1
      //   12: invokestatic 26	com/google/common/collect/ObjectArrays:toArrayImpl	(Ljava/util/Collection;[Ljava/lang/Object;)[Ljava/lang/Object;
      //   15: aload_2
      //   16: monitorexit
      //   17: areturn
      //   18: astore_3
      //   19: aload_2
      //   20: monitorexit
      //   21: aload_3
      //   22: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	23	0	this	SynchronizedAsMapEntries
      //   0	23	1	paramArrayOfObject	Object[]
      //   5	15	2	Ljava/lang/Object;	Object
      //   18	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	17	18	finally
      //   18	21	18	finally
    }
    
    /* Error */
    public boolean contains(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 19	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 28	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
      //   11: aload_1
      //   12: invokestatic 23	com/google/common/collect/Maps:containsEntryImpl	(Ljava/util/Collection;Ljava/lang/Object;)Z
      //   15: aload_2
      //   16: monitorexit
      //   17: ireturn
      //   18: astore_3
      //   19: aload_2
      //   20: monitorexit
      //   21: aload_3
      //   22: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	23	0	this	SynchronizedAsMapEntries
      //   0	23	1	paramObject	Object
      //   5	15	2	Ljava/lang/Object;	Object
      //   18	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	17	18	finally
      //   18	21	18	finally
    }
    
    /* Error */
    public boolean containsAll(Collection paramCollection)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 19	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 28	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
      //   11: aload_1
      //   12: invokestatic 20	com/google/common/collect/Collections2:containsAllImpl	(Ljava/util/Collection;Ljava/util/Collection;)Z
      //   15: aload_2
      //   16: monitorexit
      //   17: ireturn
      //   18: astore_3
      //   19: aload_2
      //   20: monitorexit
      //   21: aload_3
      //   22: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	23	0	this	SynchronizedAsMapEntries
      //   0	23	1	paramCollection	Collection
      //   5	15	2	Ljava/lang/Object;	Object
      //   18	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	17	18	finally
      //   18	21	18	finally
    }
    
    /* Error */
    public boolean equals(Object paramObject)
    {
      // Byte code:
      //   0: aload_1
      //   1: aload_0
      //   2: if_acmpne +5 -> 7
      //   5: iconst_1
      //   6: ireturn
      //   7: aload_0
      //   8: getfield 19	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
      //   11: dup
      //   12: astore_2
      //   13: monitorenter
      //   14: aload_0
      //   15: invokevirtual 28	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
      //   18: aload_1
      //   19: invokestatic 27	com/google/common/collect/Sets:equalsImpl	(Ljava/util/Set;Ljava/lang/Object;)Z
      //   22: aload_2
      //   23: monitorexit
      //   24: ireturn
      //   25: astore_3
      //   26: aload_2
      //   27: monitorexit
      //   28: aload_3
      //   29: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	30	0	this	SynchronizedAsMapEntries
      //   0	30	1	paramObject	Object
      //   12	15	2	Ljava/lang/Object;	Object
      //   25	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   14	24	25	finally
      //   25	28	25	finally
    }
    
    /* Error */
    public boolean remove(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 19	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 28	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
      //   11: aload_1
      //   12: invokestatic 24	com/google/common/collect/Maps:removeEntryImpl	(Ljava/util/Collection;Ljava/lang/Object;)Z
      //   15: aload_2
      //   16: monitorexit
      //   17: ireturn
      //   18: astore_3
      //   19: aload_2
      //   20: monitorexit
      //   21: aload_3
      //   22: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	23	0	this	SynchronizedAsMapEntries
      //   0	23	1	paramObject	Object
      //   5	15	2	Ljava/lang/Object;	Object
      //   18	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	17	18	finally
      //   18	21	18	finally
    }
    
    /* Error */
    public boolean removeAll(Collection paramCollection)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 19	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 28	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
      //   11: invokeinterface 32 1 0
      //   16: aload_1
      //   17: invokestatic 21	com/google/common/collect/Iterators:removeAll	(Ljava/util/Iterator;Ljava/util/Collection;)Z
      //   20: aload_2
      //   21: monitorexit
      //   22: ireturn
      //   23: astore_3
      //   24: aload_2
      //   25: monitorexit
      //   26: aload_3
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedAsMapEntries
      //   0	28	1	paramCollection	Collection
      //   5	20	2	Ljava/lang/Object;	Object
      //   23	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	22	23	finally
      //   23	26	23	finally
    }
    
    /* Error */
    public boolean retainAll(Collection paramCollection)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 19	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 28	com/google/common/collect/Synchronized$SynchronizedAsMapEntries:delegate	()Ljava/util/Set;
      //   11: invokeinterface 32 1 0
      //   16: aload_1
      //   17: invokestatic 22	com/google/common/collect/Iterators:retainAll	(Ljava/util/Iterator;Ljava/util/Collection;)Z
      //   20: aload_2
      //   21: monitorexit
      //   22: ireturn
      //   23: astore_3
      //   24: aload_2
      //   25: monitorexit
      //   26: aload_3
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedAsMapEntries
      //   0	28	1	paramCollection	Collection
      //   5	20	2	Ljava/lang/Object;	Object
      //   23	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	22	23	finally
      //   23	26	23	finally
    }
  }
  
  private static class SynchronizedSortedSetMultimap
    extends Synchronized.SynchronizedSetMultimap
    implements SortedSetMultimap
  {
    private static final long serialVersionUID = 0L;
    
    SynchronizedSortedSetMultimap(SortedSetMultimap paramSortedSetMultimap, Object paramObject)
    {
      super(paramObject);
    }
    
    SortedSetMultimap delegate()
    {
      return (SortedSetMultimap)super.delegate();
    }
    
    /* Error */
    public SortedSet get(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 14	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:delegate	()Lcom/google/common/collect/SortedSetMultimap;
      //   11: aload_1
      //   12: invokeinterface 18 2 0
      //   17: aload_0
      //   18: getfield 10	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:mutex	Ljava/lang/Object;
      //   21: invokestatic 11	com/google/common/collect/Synchronized:access$100	(Ljava/util/SortedSet;Ljava/lang/Object;)Ljava/util/SortedSet;
      //   24: aload_2
      //   25: monitorexit
      //   26: areturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedSortedSetMultimap
      //   0	32	1	paramObject	Object
      //   5	24	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public SortedSet removeAll(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 14	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:delegate	()Lcom/google/common/collect/SortedSetMultimap;
      //   11: aload_1
      //   12: invokeinterface 19 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedSortedSetMultimap
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public SortedSet replaceValues(Object paramObject, Iterable paramIterable)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 14	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:delegate	()Lcom/google/common/collect/SortedSetMultimap;
      //   11: aload_1
      //   12: aload_2
      //   13: invokeinterface 20 3 0
      //   18: aload_3
      //   19: monitorexit
      //   20: areturn
      //   21: astore 4
      //   23: aload_3
      //   24: monitorexit
      //   25: aload 4
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedSortedSetMultimap
      //   0	28	1	paramObject	Object
      //   0	28	2	paramIterable	Iterable
      //   5	19	3	Ljava/lang/Object;	Object
      //   21	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	20	21	finally
      //   21	25	21	finally
    }
    
    /* Error */
    public java.util.Comparator valueComparator()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 14	com/google/common/collect/Synchronized$SynchronizedSortedSetMultimap:delegate	()Lcom/google/common/collect/SortedSetMultimap;
      //   11: invokeinterface 21 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedSortedSetMultimap
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
  }
  
  private static class SynchronizedSetMultimap
    extends Synchronized.SynchronizedMultimap
    implements SetMultimap
  {
    transient Set entrySet;
    private static final long serialVersionUID = 0L;
    
    SynchronizedSetMultimap(SetMultimap paramSetMultimap, Object paramObject)
    {
      super(paramObject);
    }
    
    SetMultimap delegate()
    {
      return (SetMultimap)super.delegate();
    }
    
    /* Error */
    public Set get(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedSetMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 17	com/google/common/collect/Synchronized$SynchronizedSetMultimap:delegate	()Lcom/google/common/collect/SetMultimap;
      //   11: aload_1
      //   12: invokeinterface 23 2 0
      //   17: aload_0
      //   18: getfield 13	com/google/common/collect/Synchronized$SynchronizedSetMultimap:mutex	Ljava/lang/Object;
      //   21: invokestatic 14	com/google/common/collect/Synchronized:set	(Ljava/util/Set;Ljava/lang/Object;)Ljava/util/Set;
      //   24: aload_2
      //   25: monitorexit
      //   26: areturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedSetMultimap
      //   0	32	1	paramObject	Object
      //   5	24	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public Set removeAll(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedSetMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 17	com/google/common/collect/Synchronized$SynchronizedSetMultimap:delegate	()Lcom/google/common/collect/SetMultimap;
      //   11: aload_1
      //   12: invokeinterface 24 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedSetMultimap
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public Set replaceValues(Object paramObject, Iterable paramIterable)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedSetMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 17	com/google/common/collect/Synchronized$SynchronizedSetMultimap:delegate	()Lcom/google/common/collect/SetMultimap;
      //   11: aload_1
      //   12: aload_2
      //   13: invokeinterface 25 3 0
      //   18: aload_3
      //   19: monitorexit
      //   20: areturn
      //   21: astore 4
      //   23: aload_3
      //   24: monitorexit
      //   25: aload 4
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedSetMultimap
      //   0	28	1	paramObject	Object
      //   0	28	2	paramIterable	Iterable
      //   5	19	3	Ljava/lang/Object;	Object
      //   21	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	20	21	finally
      //   21	25	21	finally
    }
    
    public Set entries()
    {
      synchronized (this.mutex)
      {
        if (this.entrySet == null) {
          this.entrySet = Synchronized.set(delegate().entries(), this.mutex);
        }
        return this.entrySet;
      }
    }
  }
  
  private static class SynchronizedListMultimap
    extends Synchronized.SynchronizedMultimap
    implements ListMultimap
  {
    private static final long serialVersionUID = 0L;
    
    SynchronizedListMultimap(ListMultimap paramListMultimap, Object paramObject)
    {
      super(paramObject);
    }
    
    ListMultimap delegate()
    {
      return (ListMultimap)super.delegate();
    }
    
    /* Error */
    public List get(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedListMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 12	com/google/common/collect/Synchronized$SynchronizedListMultimap:delegate	()Lcom/google/common/collect/ListMultimap;
      //   11: aload_1
      //   12: invokeinterface 18 2 0
      //   17: aload_0
      //   18: getfield 10	com/google/common/collect/Synchronized$SynchronizedListMultimap:mutex	Ljava/lang/Object;
      //   21: invokestatic 11	com/google/common/collect/Synchronized:access$200	(Ljava/util/List;Ljava/lang/Object;)Ljava/util/List;
      //   24: aload_2
      //   25: monitorexit
      //   26: areturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedListMultimap
      //   0	32	1	paramObject	Object
      //   5	24	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public List removeAll(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedListMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 12	com/google/common/collect/Synchronized$SynchronizedListMultimap:delegate	()Lcom/google/common/collect/ListMultimap;
      //   11: aload_1
      //   12: invokeinterface 19 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedListMultimap
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public List replaceValues(Object paramObject, Iterable paramIterable)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedListMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 12	com/google/common/collect/Synchronized$SynchronizedListMultimap:delegate	()Lcom/google/common/collect/ListMultimap;
      //   11: aload_1
      //   12: aload_2
      //   13: invokeinterface 20 3 0
      //   18: aload_3
      //   19: monitorexit
      //   20: areturn
      //   21: astore 4
      //   23: aload_3
      //   24: monitorexit
      //   25: aload 4
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedListMultimap
      //   0	28	1	paramObject	Object
      //   0	28	2	paramIterable	Iterable
      //   5	19	3	Ljava/lang/Object;	Object
      //   21	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	20	21	finally
      //   21	25	21	finally
    }
  }
  
  private static class SynchronizedMultimap
    extends Synchronized.SynchronizedObject
    implements Multimap
  {
    transient Set keySet;
    transient Collection valuesCollection;
    transient Collection entries;
    transient Map asMap;
    transient Multiset keys;
    private static final long serialVersionUID = 0L;
    
    Multimap delegate()
    {
      return (Multimap)super.delegate();
    }
    
    SynchronizedMultimap(Multimap paramMultimap, Object paramObject)
    {
      super(paramObject);
    }
    
    /* Error */
    public int size()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 24	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
      //   11: invokeinterface 45 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: ireturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedMultimap
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public boolean isEmpty()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 24	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
      //   11: invokeinterface 36 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: ireturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedMultimap
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public boolean containsKey(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 24	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
      //   11: aload_1
      //   12: invokeinterface 30 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedMultimap
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public boolean containsValue(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 24	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
      //   11: aload_1
      //   12: invokeinterface 31 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedMultimap
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public boolean containsEntry(Object paramObject1, Object paramObject2)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 24	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
      //   11: aload_1
      //   12: aload_2
      //   13: invokeinterface 29 3 0
      //   18: aload_3
      //   19: monitorexit
      //   20: ireturn
      //   21: astore 4
      //   23: aload_3
      //   24: monitorexit
      //   25: aload 4
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedMultimap
      //   0	28	1	paramObject1	Object
      //   0	28	2	paramObject2	Object
      //   5	19	3	Ljava/lang/Object;	Object
      //   21	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	20	21	finally
      //   21	25	21	finally
    }
    
    /* Error */
    public Collection get(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 24	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
      //   11: aload_1
      //   12: invokeinterface 34 2 0
      //   17: aload_0
      //   18: getfield 17	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
      //   21: invokestatic 20	com/google/common/collect/Synchronized:access$400	(Ljava/util/Collection;Ljava/lang/Object;)Ljava/util/Collection;
      //   24: aload_2
      //   25: monitorexit
      //   26: areturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedMultimap
      //   0	32	1	paramObject	Object
      //   5	24	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public boolean put(Object paramObject1, Object paramObject2)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 24	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
      //   11: aload_1
      //   12: aload_2
      //   13: invokeinterface 39 3 0
      //   18: aload_3
      //   19: monitorexit
      //   20: ireturn
      //   21: astore 4
      //   23: aload_3
      //   24: monitorexit
      //   25: aload 4
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedMultimap
      //   0	28	1	paramObject1	Object
      //   0	28	2	paramObject2	Object
      //   5	19	3	Ljava/lang/Object;	Object
      //   21	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	20	21	finally
      //   21	25	21	finally
    }
    
    /* Error */
    public boolean putAll(Object paramObject, Iterable paramIterable)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 24	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
      //   11: aload_1
      //   12: aload_2
      //   13: invokeinterface 41 3 0
      //   18: aload_3
      //   19: monitorexit
      //   20: ireturn
      //   21: astore 4
      //   23: aload_3
      //   24: monitorexit
      //   25: aload 4
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedMultimap
      //   0	28	1	paramObject	Object
      //   0	28	2	paramIterable	Iterable
      //   5	19	3	Ljava/lang/Object;	Object
      //   21	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	20	21	finally
      //   21	25	21	finally
    }
    
    /* Error */
    public boolean putAll(Multimap paramMultimap)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 24	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
      //   11: aload_1
      //   12: invokeinterface 40 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedMultimap
      //   0	25	1	paramMultimap	Multimap
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public Collection replaceValues(Object paramObject, Iterable paramIterable)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 24	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
      //   11: aload_1
      //   12: aload_2
      //   13: invokeinterface 44 3 0
      //   18: aload_3
      //   19: monitorexit
      //   20: areturn
      //   21: astore 4
      //   23: aload_3
      //   24: monitorexit
      //   25: aload 4
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedMultimap
      //   0	28	1	paramObject	Object
      //   0	28	2	paramIterable	Iterable
      //   5	19	3	Ljava/lang/Object;	Object
      //   21	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	20	21	finally
      //   21	25	21	finally
    }
    
    /* Error */
    public boolean remove(Object paramObject1, Object paramObject2)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 24	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
      //   11: aload_1
      //   12: aload_2
      //   13: invokeinterface 42 3 0
      //   18: aload_3
      //   19: monitorexit
      //   20: ireturn
      //   21: astore 4
      //   23: aload_3
      //   24: monitorexit
      //   25: aload 4
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedMultimap
      //   0	28	1	paramObject1	Object
      //   0	28	2	paramObject2	Object
      //   5	19	3	Ljava/lang/Object;	Object
      //   21	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	20	21	finally
      //   21	25	21	finally
    }
    
    /* Error */
    public Collection removeAll(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 24	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
      //   11: aload_1
      //   12: invokeinterface 43 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedMultimap
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    public void clear()
    {
      synchronized (this.mutex)
      {
        delegate().clear();
      }
    }
    
    public Set keySet()
    {
      synchronized (this.mutex)
      {
        if (this.keySet == null) {
          this.keySet = Synchronized.typePreservingSet(delegate().keySet(), this.mutex);
        }
        return this.keySet;
      }
    }
    
    public Collection values()
    {
      synchronized (this.mutex)
      {
        if (this.valuesCollection == null) {
          this.valuesCollection = Synchronized.collection(delegate().values(), this.mutex);
        }
        return this.valuesCollection;
      }
    }
    
    public Collection entries()
    {
      synchronized (this.mutex)
      {
        if (this.entries == null) {
          this.entries = Synchronized.typePreservingCollection(delegate().entries(), this.mutex);
        }
        return this.entries;
      }
    }
    
    public Map asMap()
    {
      synchronized (this.mutex)
      {
        if (this.asMap == null) {
          this.asMap = new Synchronized.SynchronizedAsMap(delegate().asMap(), this.mutex);
        }
        return this.asMap;
      }
    }
    
    public Multiset keys()
    {
      synchronized (this.mutex)
      {
        if (this.keys == null) {
          this.keys = Synchronized.multiset(delegate().keys(), this.mutex);
        }
        return this.keys;
      }
    }
    
    /* Error */
    public boolean equals(Object paramObject)
    {
      // Byte code:
      //   0: aload_1
      //   1: aload_0
      //   2: if_acmpne +5 -> 7
      //   5: iconst_1
      //   6: ireturn
      //   7: aload_0
      //   8: getfield 17	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
      //   11: dup
      //   12: astore_2
      //   13: monitorenter
      //   14: aload_0
      //   15: invokevirtual 24	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
      //   18: aload_1
      //   19: invokeinterface 33 2 0
      //   24: aload_2
      //   25: monitorexit
      //   26: ireturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedMultimap
      //   0	32	1	paramObject	Object
      //   12	17	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   14	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public int hashCode()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 17	com/google/common/collect/Synchronized$SynchronizedMultimap:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 24	com/google/common/collect/Synchronized$SynchronizedMultimap:delegate	()Lcom/google/common/collect/Multimap;
      //   11: invokeinterface 35 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: ireturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedMultimap
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
  }
  
  private static class SynchronizedMultiset
    extends Synchronized.SynchronizedCollection
    implements Multiset
  {
    transient Set elementSet;
    transient Set entrySet;
    private static final long serialVersionUID = 0L;
    
    SynchronizedMultiset(Multiset paramMultiset, Object paramObject)
    {
      super(paramObject, null);
    }
    
    Multiset delegate()
    {
      return (Multiset)super.delegate();
    }
    
    /* Error */
    public int count(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedMultiset:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 17	com/google/common/collect/Synchronized$SynchronizedMultiset:delegate	()Lcom/google/common/collect/Multiset;
      //   11: aload_1
      //   12: invokeinterface 19 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedMultiset
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public int add(Object paramObject, int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedMultiset:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 17	com/google/common/collect/Synchronized$SynchronizedMultiset:delegate	()Lcom/google/common/collect/Multiset;
      //   11: aload_1
      //   12: iload_2
      //   13: invokeinterface 18 3 0
      //   18: aload_3
      //   19: monitorexit
      //   20: ireturn
      //   21: astore 4
      //   23: aload_3
      //   24: monitorexit
      //   25: aload 4
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedMultiset
      //   0	28	1	paramObject	Object
      //   0	28	2	paramInt	int
      //   5	19	3	Ljava/lang/Object;	Object
      //   21	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	20	21	finally
      //   21	25	21	finally
    }
    
    /* Error */
    public int remove(Object paramObject, int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedMultiset:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 17	com/google/common/collect/Synchronized$SynchronizedMultiset:delegate	()Lcom/google/common/collect/Multiset;
      //   11: aload_1
      //   12: iload_2
      //   13: invokeinterface 24 3 0
      //   18: aload_3
      //   19: monitorexit
      //   20: ireturn
      //   21: astore 4
      //   23: aload_3
      //   24: monitorexit
      //   25: aload 4
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedMultiset
      //   0	28	1	paramObject	Object
      //   0	28	2	paramInt	int
      //   5	19	3	Ljava/lang/Object;	Object
      //   21	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	20	21	finally
      //   21	25	21	finally
    }
    
    /* Error */
    public int setCount(Object paramObject, int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedMultiset:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 17	com/google/common/collect/Synchronized$SynchronizedMultiset:delegate	()Lcom/google/common/collect/Multiset;
      //   11: aload_1
      //   12: iload_2
      //   13: invokeinterface 25 3 0
      //   18: aload_3
      //   19: monitorexit
      //   20: ireturn
      //   21: astore 4
      //   23: aload_3
      //   24: monitorexit
      //   25: aload 4
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedMultiset
      //   0	28	1	paramObject	Object
      //   0	28	2	paramInt	int
      //   5	19	3	Ljava/lang/Object;	Object
      //   21	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	20	21	finally
      //   21	25	21	finally
    }
    
    /* Error */
    public boolean setCount(Object paramObject, int paramInt1, int paramInt2)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedMultiset:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore 4
      //   7: monitorenter
      //   8: aload_0
      //   9: invokevirtual 17	com/google/common/collect/Synchronized$SynchronizedMultiset:delegate	()Lcom/google/common/collect/Multiset;
      //   12: aload_1
      //   13: iload_2
      //   14: iload_3
      //   15: invokeinterface 26 4 0
      //   20: aload 4
      //   22: monitorexit
      //   23: ireturn
      //   24: astore 5
      //   26: aload 4
      //   28: monitorexit
      //   29: aload 5
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedMultiset
      //   0	32	1	paramObject	Object
      //   0	32	2	paramInt1	int
      //   0	32	3	paramInt2	int
      //   5	22	4	Ljava/lang/Object;	Object
      //   24	6	5	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   8	23	24	finally
      //   24	29	24	finally
    }
    
    public Set elementSet()
    {
      synchronized (this.mutex)
      {
        if (this.elementSet == null) {
          this.elementSet = Synchronized.typePreservingSet(delegate().elementSet(), this.mutex);
        }
        return this.elementSet;
      }
    }
    
    public Set entrySet()
    {
      synchronized (this.mutex)
      {
        if (this.entrySet == null) {
          this.entrySet = Synchronized.typePreservingSet(delegate().entrySet(), this.mutex);
        }
        return this.entrySet;
      }
    }
    
    /* Error */
    public boolean equals(Object paramObject)
    {
      // Byte code:
      //   0: aload_1
      //   1: aload_0
      //   2: if_acmpne +5 -> 7
      //   5: iconst_1
      //   6: ireturn
      //   7: aload_0
      //   8: getfield 13	com/google/common/collect/Synchronized$SynchronizedMultiset:mutex	Ljava/lang/Object;
      //   11: dup
      //   12: astore_2
      //   13: monitorenter
      //   14: aload_0
      //   15: invokevirtual 17	com/google/common/collect/Synchronized$SynchronizedMultiset:delegate	()Lcom/google/common/collect/Multiset;
      //   18: aload_1
      //   19: invokeinterface 22 2 0
      //   24: aload_2
      //   25: monitorexit
      //   26: ireturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedMultiset
      //   0	32	1	paramObject	Object
      //   12	17	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   14	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public int hashCode()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/google/common/collect/Synchronized$SynchronizedMultiset:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 17	com/google/common/collect/Synchronized$SynchronizedMultiset:delegate	()Lcom/google/common/collect/Multiset;
      //   11: invokeinterface 23 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: ireturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedMultiset
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
  }
  
  private static class SynchronizedRandomAccessList
    extends Synchronized.SynchronizedList
    implements RandomAccess
  {
    private static final long serialVersionUID = 0L;
    
    SynchronizedRandomAccessList(List paramList, Object paramObject)
    {
      super(paramObject);
    }
  }
  
  private static class SynchronizedList
    extends Synchronized.SynchronizedCollection
    implements List
  {
    private static final long serialVersionUID = 0L;
    
    SynchronizedList(List paramList, Object paramObject)
    {
      super(paramObject, null);
    }
    
    List delegate()
    {
      return (List)super.delegate();
    }
    
    public void add(int paramInt, Object paramObject)
    {
      synchronized (this.mutex)
      {
        delegate().add(paramInt, paramObject);
      }
    }
    
    /* Error */
    public boolean addAll(int paramInt, Collection paramCollection)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 15	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
      //   11: iload_1
      //   12: aload_2
      //   13: invokeinterface 17 3 0
      //   18: aload_3
      //   19: monitorexit
      //   20: ireturn
      //   21: astore 4
      //   23: aload_3
      //   24: monitorexit
      //   25: aload 4
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedList
      //   0	28	1	paramInt	int
      //   0	28	2	paramCollection	Collection
      //   5	19	3	Ljava/lang/Object;	Object
      //   21	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	20	21	finally
      //   21	25	21	finally
    }
    
    /* Error */
    public Object get(int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 15	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
      //   11: iload_1
      //   12: invokeinterface 19 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedList
      //   0	25	1	paramInt	int
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public int indexOf(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 15	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
      //   11: aload_1
      //   12: invokeinterface 21 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedList
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public int lastIndexOf(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 15	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
      //   11: aload_1
      //   12: invokeinterface 22 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedList
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    public ListIterator listIterator()
    {
      return delegate().listIterator();
    }
    
    public ListIterator listIterator(int paramInt)
    {
      return delegate().listIterator(paramInt);
    }
    
    /* Error */
    public Object remove(int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 15	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
      //   11: iload_1
      //   12: invokeinterface 25 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedList
      //   0	25	1	paramInt	int
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public Object set(int paramInt, Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 15	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
      //   11: iload_1
      //   12: aload_2
      //   13: invokeinterface 26 3 0
      //   18: aload_3
      //   19: monitorexit
      //   20: areturn
      //   21: astore 4
      //   23: aload_3
      //   24: monitorexit
      //   25: aload 4
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	SynchronizedList
      //   0	28	1	paramInt	int
      //   0	28	2	paramObject	Object
      //   5	19	3	Ljava/lang/Object;	Object
      //   21	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	20	21	finally
      //   21	25	21	finally
    }
    
    /* Error */
    public List subList(int paramInt1, int paramInt2)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 15	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
      //   11: iload_1
      //   12: iload_2
      //   13: invokeinterface 27 3 0
      //   18: aload_0
      //   19: getfield 11	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
      //   22: invokestatic 12	com/google/common/collect/Synchronized:access$200	(Ljava/util/List;Ljava/lang/Object;)Ljava/util/List;
      //   25: aload_3
      //   26: monitorexit
      //   27: areturn
      //   28: astore 4
      //   30: aload_3
      //   31: monitorexit
      //   32: aload 4
      //   34: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	35	0	this	SynchronizedList
      //   0	35	1	paramInt1	int
      //   0	35	2	paramInt2	int
      //   5	26	3	Ljava/lang/Object;	Object
      //   28	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	27	28	finally
      //   28	32	28	finally
    }
    
    /* Error */
    public boolean equals(Object paramObject)
    {
      // Byte code:
      //   0: aload_1
      //   1: aload_0
      //   2: if_acmpne +5 -> 7
      //   5: iconst_1
      //   6: ireturn
      //   7: aload_0
      //   8: getfield 11	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
      //   11: dup
      //   12: astore_2
      //   13: monitorenter
      //   14: aload_0
      //   15: invokevirtual 15	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
      //   18: aload_1
      //   19: invokeinterface 18 2 0
      //   24: aload_2
      //   25: monitorexit
      //   26: ireturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedList
      //   0	32	1	paramObject	Object
      //   12	17	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   14	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public int hashCode()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedList:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 15	com/google/common/collect/Synchronized$SynchronizedList:delegate	()Ljava/util/List;
      //   11: invokeinterface 20 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: ireturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedList
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
  }
  
  static class SynchronizedSortedSet
    extends Synchronized.SynchronizedSet
    implements SortedSet
  {
    private static final long serialVersionUID = 0L;
    
    SynchronizedSortedSet(SortedSet paramSortedSet, Object paramObject)
    {
      super(paramObject);
    }
    
    SortedSet delegate()
    {
      return (SortedSet)super.delegate();
    }
    
    /* Error */
    public java.util.Comparator comparator()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedSortedSet:delegate	()Ljava/util/SortedSet;
      //   11: invokeinterface 14 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedSortedSet
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public SortedSet subSet(Object paramObject1, Object paramObject2)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_3
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedSortedSet:delegate	()Ljava/util/SortedSet;
      //   11: aload_1
      //   12: aload_2
      //   13: invokeinterface 18 3 0
      //   18: aload_0
      //   19: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
      //   22: invokestatic 10	com/google/common/collect/Synchronized:access$100	(Ljava/util/SortedSet;Ljava/lang/Object;)Ljava/util/SortedSet;
      //   25: aload_3
      //   26: monitorexit
      //   27: areturn
      //   28: astore 4
      //   30: aload_3
      //   31: monitorexit
      //   32: aload 4
      //   34: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	35	0	this	SynchronizedSortedSet
      //   0	35	1	paramObject1	Object
      //   0	35	2	paramObject2	Object
      //   5	26	3	Ljava/lang/Object;	Object
      //   28	5	4	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	27	28	finally
      //   28	32	28	finally
    }
    
    /* Error */
    public SortedSet headSet(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedSortedSet:delegate	()Ljava/util/SortedSet;
      //   11: aload_1
      //   12: invokeinterface 16 2 0
      //   17: aload_0
      //   18: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
      //   21: invokestatic 10	com/google/common/collect/Synchronized:access$100	(Ljava/util/SortedSet;Ljava/lang/Object;)Ljava/util/SortedSet;
      //   24: aload_2
      //   25: monitorexit
      //   26: areturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedSortedSet
      //   0	32	1	paramObject	Object
      //   5	24	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public SortedSet tailSet(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedSortedSet:delegate	()Ljava/util/SortedSet;
      //   11: aload_1
      //   12: invokeinterface 19 2 0
      //   17: aload_0
      //   18: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
      //   21: invokestatic 10	com/google/common/collect/Synchronized:access$100	(Ljava/util/SortedSet;Ljava/lang/Object;)Ljava/util/SortedSet;
      //   24: aload_2
      //   25: monitorexit
      //   26: areturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedSortedSet
      //   0	32	1	paramObject	Object
      //   5	24	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public Object first()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedSortedSet:delegate	()Ljava/util/SortedSet;
      //   11: invokeinterface 15 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedSortedSet
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public Object last()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 9	com/google/common/collect/Synchronized$SynchronizedSortedSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedSortedSet:delegate	()Ljava/util/SortedSet;
      //   11: invokeinterface 17 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedSortedSet
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
  }
  
  static class SynchronizedSet
    extends Synchronized.SynchronizedCollection
    implements Set
  {
    private static final long serialVersionUID = 0L;
    
    SynchronizedSet(Set paramSet, Object paramObject)
    {
      super(paramObject, null);
    }
    
    Set delegate()
    {
      return (Set)super.delegate();
    }
    
    /* Error */
    public boolean equals(Object paramObject)
    {
      // Byte code:
      //   0: aload_1
      //   1: aload_0
      //   2: if_acmpne +5 -> 7
      //   5: iconst_1
      //   6: ireturn
      //   7: aload_0
      //   8: getfield 10	com/google/common/collect/Synchronized$SynchronizedSet:mutex	Ljava/lang/Object;
      //   11: dup
      //   12: astore_2
      //   13: monitorenter
      //   14: aload_0
      //   15: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedSet:delegate	()Ljava/util/Set;
      //   18: aload_1
      //   19: invokeinterface 14 2 0
      //   24: aload_2
      //   25: monitorexit
      //   26: ireturn
      //   27: astore_3
      //   28: aload_2
      //   29: monitorexit
      //   30: aload_3
      //   31: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	32	0	this	SynchronizedSet
      //   0	32	1	paramObject	Object
      //   12	17	2	Ljava/lang/Object;	Object
      //   27	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   14	26	27	finally
      //   27	30	27	finally
    }
    
    /* Error */
    public int hashCode()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 10	com/google/common/collect/Synchronized$SynchronizedSet:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedSet:delegate	()Ljava/util/Set;
      //   11: invokeinterface 15 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: ireturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedSet
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
  }
  
  @VisibleForTesting
  static class SynchronizedCollection
    extends Synchronized.SynchronizedObject
    implements Collection
  {
    private static final long serialVersionUID = 0L;
    
    private SynchronizedCollection(Collection paramCollection, Object paramObject)
    {
      super(paramObject);
    }
    
    Collection delegate()
    {
      return (Collection)super.delegate();
    }
    
    /* Error */
    public boolean add(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
      //   11: aload_1
      //   12: invokeinterface 16 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedCollection
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public boolean addAll(Collection paramCollection)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
      //   11: aload_1
      //   12: invokeinterface 17 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedCollection
      //   0	25	1	paramCollection	Collection
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    public void clear()
    {
      synchronized (this.mutex)
      {
        delegate().clear();
      }
    }
    
    /* Error */
    public boolean contains(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
      //   11: aload_1
      //   12: invokeinterface 19 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedCollection
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public boolean containsAll(Collection paramCollection)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
      //   11: aload_1
      //   12: invokeinterface 20 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedCollection
      //   0	25	1	paramCollection	Collection
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public boolean isEmpty()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
      //   11: invokeinterface 21 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: ireturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedCollection
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    public Iterator iterator()
    {
      return delegate().iterator();
    }
    
    /* Error */
    public boolean remove(Object paramObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
      //   11: aload_1
      //   12: invokeinterface 23 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedCollection
      //   0	25	1	paramObject	Object
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public boolean removeAll(Collection paramCollection)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
      //   11: aload_1
      //   12: invokeinterface 24 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedCollection
      //   0	25	1	paramCollection	Collection
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public boolean retainAll(Collection paramCollection)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
      //   11: aload_1
      //   12: invokeinterface 25 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: ireturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedCollection
      //   0	25	1	paramCollection	Collection
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public int size()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
      //   11: invokeinterface 26 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: ireturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedCollection
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public Object[] toArray()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
      //   11: invokeinterface 27 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	SynchronizedCollection
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    /* Error */
    public Object[] toArray(Object[] paramArrayOfObject)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 11	com/google/common/collect/Synchronized$SynchronizedCollection:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: invokevirtual 13	com/google/common/collect/Synchronized$SynchronizedCollection:delegate	()Ljava/util/Collection;
      //   11: aload_1
      //   12: invokeinterface 28 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedCollection
      //   0	25	1	paramArrayOfObject	Object[]
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
  }
  
  static class SynchronizedObject
    implements Serializable
  {
    final Object delegate;
    final Object mutex;
    @GwtIncompatible("not needed in emulated source")
    private static final long serialVersionUID = 0L;
    
    SynchronizedObject(Object paramObject1, Object paramObject2)
    {
      this.delegate = Preconditions.checkNotNull(paramObject1);
      this.mutex = (paramObject2 == null ? this : paramObject2);
    }
    
    Object delegate()
    {
      return this.delegate;
    }
    
    /* Error */
    public String toString()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	com/google/common/collect/Synchronized$SynchronizedObject:mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 11	com/google/common/collect/Synchronized$SynchronizedObject:delegate	Ljava/lang/Object;
      //   11: invokevirtual 16	java/lang/Object:toString	()Ljava/lang/String;
      //   14: aload_1
      //   15: monitorexit
      //   16: areturn
      //   17: astore_2
      //   18: aload_1
      //   19: monitorexit
      //   20: aload_2
      //   21: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	22	0	this	SynchronizedObject
      //   5	14	1	Ljava/lang/Object;	Object
      //   17	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	16	17	finally
      //   17	20	17	finally
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      synchronized (this.mutex)
      {
        paramObjectOutputStream.defaultWriteObject();
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Synchronized.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */