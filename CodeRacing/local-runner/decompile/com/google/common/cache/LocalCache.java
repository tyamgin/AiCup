package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.base.Ticker;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.Uninterruptibles;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

@GwtCompatible(emulated=true)
class LocalCache
  extends AbstractMap
  implements ConcurrentMap
{
  static final int MAXIMUM_CAPACITY = 1073741824;
  static final int MAX_SEGMENTS = 65536;
  static final int CONTAINS_VALUE_RETRIES = 3;
  static final int DRAIN_THRESHOLD = 63;
  static final int DRAIN_MAX = 16;
  static final Logger logger = Logger.getLogger(LocalCache.class.getName());
  static final ListeningExecutorService sameThreadExecutor = MoreExecutors.sameThreadExecutor();
  final int segmentMask;
  final int segmentShift;
  final Segment[] segments;
  final int concurrencyLevel;
  final Equivalence keyEquivalence;
  final Equivalence valueEquivalence;
  final Strength keyStrength;
  final Strength valueStrength;
  final long maxWeight;
  final Weigher weigher;
  final long expireAfterAccessNanos;
  final long expireAfterWriteNanos;
  final long refreshNanos;
  final Queue removalNotificationQueue;
  final RemovalListener removalListener;
  final Ticker ticker;
  final EntryFactory entryFactory;
  final AbstractCache.StatsCounter globalStatsCounter;
  final CacheLoader defaultLoader;
  static final ValueReference UNSET = new ValueReference()
  {
    public Object get()
    {
      return null;
    }
    
    public int getWeight()
    {
      return 0;
    }
    
    public LocalCache.ReferenceEntry getEntry()
    {
      return null;
    }
    
    public LocalCache.ValueReference copyFor(ReferenceQueue paramAnonymousReferenceQueue, Object paramAnonymousObject, LocalCache.ReferenceEntry paramAnonymousReferenceEntry)
    {
      return this;
    }
    
    public boolean isLoading()
    {
      return false;
    }
    
    public boolean isActive()
    {
      return false;
    }
    
    public Object waitForValue()
    {
      return null;
    }
    
    public void notifyNewValue(Object paramAnonymousObject) {}
  };
  static final Queue DISCARDING_QUEUE = new AbstractQueue()
  {
    public boolean offer(Object paramAnonymousObject)
    {
      return true;
    }
    
    public Object peek()
    {
      return null;
    }
    
    public Object poll()
    {
      return null;
    }
    
    public int size()
    {
      return 0;
    }
    
    public Iterator iterator()
    {
      return Iterators.emptyIterator();
    }
  };
  Set keySet;
  Collection values;
  Set entrySet;
  
  LocalCache(CacheBuilder paramCacheBuilder, CacheLoader paramCacheLoader)
  {
    this.concurrencyLevel = Math.min(paramCacheBuilder.getConcurrencyLevel(), 65536);
    this.keyStrength = paramCacheBuilder.getKeyStrength();
    this.valueStrength = paramCacheBuilder.getValueStrength();
    this.keyEquivalence = paramCacheBuilder.getKeyEquivalence();
    this.valueEquivalence = paramCacheBuilder.getValueEquivalence();
    this.maxWeight = paramCacheBuilder.getMaximumWeight();
    this.weigher = paramCacheBuilder.getWeigher();
    this.expireAfterAccessNanos = paramCacheBuilder.getExpireAfterAccessNanos();
    this.expireAfterWriteNanos = paramCacheBuilder.getExpireAfterWriteNanos();
    this.refreshNanos = paramCacheBuilder.getRefreshNanos();
    this.removalListener = paramCacheBuilder.getRemovalListener();
    this.removalNotificationQueue = (this.removalListener == CacheBuilder.NullListener.INSTANCE ? discardingQueue() : new ConcurrentLinkedQueue());
    this.ticker = paramCacheBuilder.getTicker(recordsTime());
    this.entryFactory = EntryFactory.getFactory(this.keyStrength, usesAccessEntries(), usesWriteEntries());
    this.globalStatsCounter = ((AbstractCache.StatsCounter)paramCacheBuilder.getStatsCounterSupplier().get());
    this.defaultLoader = paramCacheLoader;
    int i = Math.min(paramCacheBuilder.getInitialCapacity(), 1073741824);
    if ((evictsBySize()) && (!customWeigher())) {
      i = Math.min(i, (int)this.maxWeight);
    }
    int j = 0;
    int k = 1;
    while ((k < this.concurrencyLevel) && ((!evictsBySize()) || (k * 20 <= this.maxWeight)))
    {
      j++;
      k <<= 1;
    }
    this.segmentShift = (32 - j);
    this.segmentMask = (k - 1);
    this.segments = newSegmentArray(k);
    int m = i / k;
    if (m * k < i) {
      m++;
    }
    int n = 1;
    while (n < m) {
      n <<= 1;
    }
    if (evictsBySize())
    {
      long l1 = this.maxWeight / k + 1L;
      long l2 = this.maxWeight % k;
      for (int i2 = 0; i2 < this.segments.length; i2++)
      {
        if (i2 == l2) {
          l1 -= 1L;
        }
        this.segments[i2] = createSegment(n, l1, (AbstractCache.StatsCounter)paramCacheBuilder.getStatsCounterSupplier().get());
      }
    }
    else
    {
      for (int i1 = 0; i1 < this.segments.length; i1++) {
        this.segments[i1] = createSegment(n, -1L, (AbstractCache.StatsCounter)paramCacheBuilder.getStatsCounterSupplier().get());
      }
    }
  }
  
  boolean evictsBySize()
  {
    return this.maxWeight >= 0L;
  }
  
  boolean customWeigher()
  {
    return this.weigher != CacheBuilder.OneWeigher.INSTANCE;
  }
  
  boolean expires()
  {
    return (expiresAfterWrite()) || (expiresAfterAccess());
  }
  
  boolean expiresAfterWrite()
  {
    return this.expireAfterWriteNanos > 0L;
  }
  
  boolean expiresAfterAccess()
  {
    return this.expireAfterAccessNanos > 0L;
  }
  
  boolean refreshes()
  {
    return this.refreshNanos > 0L;
  }
  
  boolean usesAccessQueue()
  {
    return (expiresAfterAccess()) || (evictsBySize());
  }
  
  boolean usesWriteQueue()
  {
    return expiresAfterWrite();
  }
  
  boolean recordsWrite()
  {
    return (expiresAfterWrite()) || (refreshes());
  }
  
  boolean recordsAccess()
  {
    return expiresAfterAccess();
  }
  
  boolean recordsTime()
  {
    return (recordsWrite()) || (recordsAccess());
  }
  
  boolean usesWriteEntries()
  {
    return (usesWriteQueue()) || (recordsWrite());
  }
  
  boolean usesAccessEntries()
  {
    return (usesAccessQueue()) || (recordsAccess());
  }
  
  boolean usesKeyReferences()
  {
    return this.keyStrength != Strength.STRONG;
  }
  
  boolean usesValueReferences()
  {
    return this.valueStrength != Strength.STRONG;
  }
  
  static ValueReference unset()
  {
    return UNSET;
  }
  
  static ReferenceEntry nullEntry()
  {
    return NullEntry.INSTANCE;
  }
  
  static Queue discardingQueue()
  {
    return DISCARDING_QUEUE;
  }
  
  static int rehash(int paramInt)
  {
    paramInt += (paramInt << 15 ^ 0xCD7D);
    paramInt ^= paramInt >>> 10;
    paramInt += (paramInt << 3);
    paramInt ^= paramInt >>> 6;
    paramInt += (paramInt << 2) + (paramInt << 14);
    return paramInt ^ paramInt >>> 16;
  }
  
  @VisibleForTesting
  ReferenceEntry newEntry(Object paramObject, int paramInt, ReferenceEntry paramReferenceEntry)
  {
    return segmentFor(paramInt).newEntry(paramObject, paramInt, paramReferenceEntry);
  }
  
  @VisibleForTesting
  ReferenceEntry copyEntry(ReferenceEntry paramReferenceEntry1, ReferenceEntry paramReferenceEntry2)
  {
    int i = paramReferenceEntry1.getHash();
    return segmentFor(i).copyEntry(paramReferenceEntry1, paramReferenceEntry2);
  }
  
  @VisibleForTesting
  ValueReference newValueReference(ReferenceEntry paramReferenceEntry, Object paramObject, int paramInt)
  {
    int i = paramReferenceEntry.getHash();
    return this.valueStrength.referenceValue(segmentFor(i), paramReferenceEntry, Preconditions.checkNotNull(paramObject), paramInt);
  }
  
  int hash(Object paramObject)
  {
    int i = this.keyEquivalence.hash(paramObject);
    return rehash(i);
  }
  
  void reclaimValue(ValueReference paramValueReference)
  {
    ReferenceEntry localReferenceEntry = paramValueReference.getEntry();
    int i = localReferenceEntry.getHash();
    segmentFor(i).reclaimValue(localReferenceEntry.getKey(), i, paramValueReference);
  }
  
  void reclaimKey(ReferenceEntry paramReferenceEntry)
  {
    int i = paramReferenceEntry.getHash();
    segmentFor(i).reclaimKey(paramReferenceEntry, i);
  }
  
  @VisibleForTesting
  boolean isLive(ReferenceEntry paramReferenceEntry, long paramLong)
  {
    return segmentFor(paramReferenceEntry.getHash()).getLiveValue(paramReferenceEntry, paramLong) != null;
  }
  
  Segment segmentFor(int paramInt)
  {
    return this.segments[(paramInt >>> this.segmentShift & this.segmentMask)];
  }
  
  Segment createSegment(int paramInt, long paramLong, AbstractCache.StatsCounter paramStatsCounter)
  {
    return new Segment(this, paramInt, paramLong, paramStatsCounter);
  }
  
  Object getLiveValue(ReferenceEntry paramReferenceEntry, long paramLong)
  {
    if (paramReferenceEntry.getKey() == null) {
      return null;
    }
    Object localObject = paramReferenceEntry.getValueReference().get();
    if (localObject == null) {
      return null;
    }
    if (isExpired(paramReferenceEntry, paramLong)) {
      return null;
    }
    return localObject;
  }
  
  boolean isExpired(ReferenceEntry paramReferenceEntry, long paramLong)
  {
    Preconditions.checkNotNull(paramReferenceEntry);
    if ((expiresAfterAccess()) && (paramLong - paramReferenceEntry.getAccessTime() >= this.expireAfterAccessNanos)) {
      return true;
    }
    return (expiresAfterWrite()) && (paramLong - paramReferenceEntry.getWriteTime() >= this.expireAfterWriteNanos);
  }
  
  static void connectAccessOrder(ReferenceEntry paramReferenceEntry1, ReferenceEntry paramReferenceEntry2)
  {
    paramReferenceEntry1.setNextInAccessQueue(paramReferenceEntry2);
    paramReferenceEntry2.setPreviousInAccessQueue(paramReferenceEntry1);
  }
  
  static void nullifyAccessOrder(ReferenceEntry paramReferenceEntry)
  {
    ReferenceEntry localReferenceEntry = nullEntry();
    paramReferenceEntry.setNextInAccessQueue(localReferenceEntry);
    paramReferenceEntry.setPreviousInAccessQueue(localReferenceEntry);
  }
  
  static void connectWriteOrder(ReferenceEntry paramReferenceEntry1, ReferenceEntry paramReferenceEntry2)
  {
    paramReferenceEntry1.setNextInWriteQueue(paramReferenceEntry2);
    paramReferenceEntry2.setPreviousInWriteQueue(paramReferenceEntry1);
  }
  
  static void nullifyWriteOrder(ReferenceEntry paramReferenceEntry)
  {
    ReferenceEntry localReferenceEntry = nullEntry();
    paramReferenceEntry.setNextInWriteQueue(localReferenceEntry);
    paramReferenceEntry.setPreviousInWriteQueue(localReferenceEntry);
  }
  
  void processPendingNotifications()
  {
    RemovalNotification localRemovalNotification;
    while ((localRemovalNotification = (RemovalNotification)this.removalNotificationQueue.poll()) != null) {
      try
      {
        this.removalListener.onRemoval(localRemovalNotification);
      }
      catch (Throwable localThrowable)
      {
        logger.log(Level.WARNING, "Exception thrown by removal listener", localThrowable);
      }
    }
  }
  
  final Segment[] newSegmentArray(int paramInt)
  {
    return new Segment[paramInt];
  }
  
  public void cleanUp()
  {
    for (Segment localSegment : this.segments) {
      localSegment.cleanUp();
    }
  }
  
  public boolean isEmpty()
  {
    long l = 0L;
    Segment[] arrayOfSegment = this.segments;
    for (int i = 0; i < arrayOfSegment.length; i++)
    {
      if (arrayOfSegment[i].count != 0) {
        return false;
      }
      l += arrayOfSegment[i].modCount;
    }
    if (l != 0L)
    {
      for (i = 0; i < arrayOfSegment.length; i++)
      {
        if (arrayOfSegment[i].count != 0) {
          return false;
        }
        l -= arrayOfSegment[i].modCount;
      }
      if (l != 0L) {
        return false;
      }
    }
    return true;
  }
  
  long longSize()
  {
    Segment[] arrayOfSegment = this.segments;
    long l = 0L;
    for (int i = 0; i < arrayOfSegment.length; i++) {
      l += arrayOfSegment[i].count;
    }
    return l;
  }
  
  public int size()
  {
    return Ints.saturatedCast(longSize());
  }
  
  public Object get(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    int i = hash(paramObject);
    return segmentFor(i).get(paramObject, i);
  }
  
  public Object getIfPresent(Object paramObject)
  {
    int i = hash(Preconditions.checkNotNull(paramObject));
    Object localObject = segmentFor(i).get(paramObject, i);
    if (localObject == null) {
      this.globalStatsCounter.recordMisses(1);
    } else {
      this.globalStatsCounter.recordHits(1);
    }
    return localObject;
  }
  
  Object get(Object paramObject, CacheLoader paramCacheLoader)
    throws ExecutionException
  {
    int i = hash(Preconditions.checkNotNull(paramObject));
    return segmentFor(i).get(paramObject, i, paramCacheLoader);
  }
  
  Object getOrLoad(Object paramObject)
    throws ExecutionException
  {
    return get(paramObject, this.defaultLoader);
  }
  
  ImmutableMap getAllPresent(Iterable paramIterable)
  {
    int i = 0;
    int j = 0;
    LinkedHashMap localLinkedHashMap = Maps.newLinkedHashMap();
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Object localObject1 = localIterator.next();
      Object localObject2 = get(localObject1);
      if (localObject2 == null)
      {
        j++;
      }
      else
      {
        Object localObject3 = localObject1;
        localLinkedHashMap.put(localObject3, localObject2);
        i++;
      }
    }
    this.globalStatsCounter.recordHits(i);
    this.globalStatsCounter.recordMisses(j);
    return ImmutableMap.copyOf(localLinkedHashMap);
  }
  
  ImmutableMap getAll(Iterable paramIterable)
    throws ExecutionException
  {
    int i = 0;
    int j = 0;
    LinkedHashMap localLinkedHashMap = Maps.newLinkedHashMap();
    LinkedHashSet localLinkedHashSet = Sets.newLinkedHashSet();
    Object localObject1 = paramIterable.iterator();
    Object localObject2;
    Object localObject3;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = ((Iterator)localObject1).next();
      localObject3 = get(localObject2);
      if (!localLinkedHashMap.containsKey(localObject2))
      {
        localLinkedHashMap.put(localObject2, localObject3);
        if (localObject3 == null)
        {
          j++;
          localLinkedHashSet.add(localObject2);
        }
        else
        {
          i++;
        }
      }
    }
    try
    {
      if (!localLinkedHashSet.isEmpty())
      {
        try
        {
          localObject1 = loadAll(localLinkedHashSet, this.defaultLoader);
          localObject2 = localLinkedHashSet.iterator();
          while (((Iterator)localObject2).hasNext())
          {
            localObject3 = ((Iterator)localObject2).next();
            Object localObject4 = ((Map)localObject1).get(localObject3);
            if (localObject4 == null) {
              throw new CacheLoader.InvalidCacheLoadException("loadAll failed to return a value for " + localObject3);
            }
            localLinkedHashMap.put(localObject3, localObject4);
          }
        }
        catch (CacheLoader.UnsupportedLoadingOperationException localUnsupportedLoadingOperationException)
        {
          localObject2 = localLinkedHashSet.iterator();
        }
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = ((Iterator)localObject2).next();
          j--;
          localLinkedHashMap.put(localObject3, get(localObject3, this.defaultLoader));
        }
      }
      ImmutableMap localImmutableMap = ImmutableMap.copyOf(localLinkedHashMap);
      return localImmutableMap;
    }
    finally
    {
      this.globalStatsCounter.recordHits(i);
      this.globalStatsCounter.recordMisses(j);
    }
  }
  
  Map loadAll(Set paramSet, CacheLoader paramCacheLoader)
    throws ExecutionException
  {
    Preconditions.checkNotNull(paramCacheLoader);
    Preconditions.checkNotNull(paramSet);
    Stopwatch localStopwatch = new Stopwatch().start();
    int i = 0;
    Map localMap1;
    try
    {
      Map localMap2 = paramCacheLoader.loadAll(paramSet);
      localMap1 = localMap2;
      i = 1;
    }
    catch (CacheLoader.UnsupportedLoadingOperationException localUnsupportedLoadingOperationException)
    {
      i = 1;
      throw localUnsupportedLoadingOperationException;
    }
    catch (InterruptedException localInterruptedException)
    {
      Thread.currentThread().interrupt();
      throw new ExecutionException(localInterruptedException);
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new UncheckedExecutionException(localRuntimeException);
    }
    catch (Exception localException)
    {
      throw new ExecutionException(localException);
    }
    catch (Error localError)
    {
      throw new ExecutionError(localError);
    }
    finally
    {
      if (i == 0) {
        this.globalStatsCounter.recordLoadException(localStopwatch.elapsed(TimeUnit.NANOSECONDS));
      }
    }
    if (localMap1 == null)
    {
      this.globalStatsCounter.recordLoadException(localStopwatch.elapsed(TimeUnit.NANOSECONDS));
      throw new CacheLoader.InvalidCacheLoadException(paramCacheLoader + " returned null map from loadAll");
    }
    localStopwatch.stop();
    int j = 0;
    Iterator localIterator = localMap1.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object localObject2 = localEntry.getKey();
      Object localObject3 = localEntry.getValue();
      if ((localObject2 == null) || (localObject3 == null)) {
        j = 1;
      } else {
        put(localObject2, localObject3);
      }
    }
    if (j != 0)
    {
      this.globalStatsCounter.recordLoadException(localStopwatch.elapsed(TimeUnit.NANOSECONDS));
      throw new CacheLoader.InvalidCacheLoadException(paramCacheLoader + " returned null keys or values from loadAll");
    }
    this.globalStatsCounter.recordLoadSuccess(localStopwatch.elapsed(TimeUnit.NANOSECONDS));
    return localMap1;
  }
  
  ReferenceEntry getEntry(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    int i = hash(paramObject);
    return segmentFor(i).getEntry(paramObject, i);
  }
  
  void refresh(Object paramObject)
  {
    int i = hash(Preconditions.checkNotNull(paramObject));
    segmentFor(i).refresh(paramObject, i, this.defaultLoader, false);
  }
  
  public boolean containsKey(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    int i = hash(paramObject);
    return segmentFor(i).containsKey(paramObject, i);
  }
  
  public boolean containsValue(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    long l1 = this.ticker.read();
    Segment[] arrayOfSegment1 = this.segments;
    long l2 = -1L;
    for (int i = 0; i < 3; i++)
    {
      long l3 = 0L;
      for (Segment localSegment : arrayOfSegment1)
      {
        int m = localSegment.count;
        AtomicReferenceArray localAtomicReferenceArray = localSegment.table;
        for (int n = 0; n < localAtomicReferenceArray.length(); n++) {
          for (ReferenceEntry localReferenceEntry = (ReferenceEntry)localAtomicReferenceArray.get(n); localReferenceEntry != null; localReferenceEntry = localReferenceEntry.getNext())
          {
            Object localObject = localSegment.getLiveValue(localReferenceEntry, l1);
            if ((localObject != null) && (this.valueEquivalence.equivalent(paramObject, localObject))) {
              return true;
            }
          }
        }
        l3 += localSegment.modCount;
      }
      if (l3 == l2) {
        break;
      }
      l2 = l3;
    }
    return false;
  }
  
  public Object put(Object paramObject1, Object paramObject2)
  {
    Preconditions.checkNotNull(paramObject1);
    Preconditions.checkNotNull(paramObject2);
    int i = hash(paramObject1);
    return segmentFor(i).put(paramObject1, i, paramObject2, false);
  }
  
  public Object putIfAbsent(Object paramObject1, Object paramObject2)
  {
    Preconditions.checkNotNull(paramObject1);
    Preconditions.checkNotNull(paramObject2);
    int i = hash(paramObject1);
    return segmentFor(i).put(paramObject1, i, paramObject2, true);
  }
  
  public void putAll(Map paramMap)
  {
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      put(localEntry.getKey(), localEntry.getValue());
    }
  }
  
  public Object remove(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    int i = hash(paramObject);
    return segmentFor(i).remove(paramObject, i);
  }
  
  public boolean remove(Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 == null) || (paramObject2 == null)) {
      return false;
    }
    int i = hash(paramObject1);
    return segmentFor(i).remove(paramObject1, i, paramObject2);
  }
  
  public boolean replace(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    Preconditions.checkNotNull(paramObject1);
    Preconditions.checkNotNull(paramObject3);
    if (paramObject2 == null) {
      return false;
    }
    int i = hash(paramObject1);
    return segmentFor(i).replace(paramObject1, i, paramObject2, paramObject3);
  }
  
  public Object replace(Object paramObject1, Object paramObject2)
  {
    Preconditions.checkNotNull(paramObject1);
    Preconditions.checkNotNull(paramObject2);
    int i = hash(paramObject1);
    return segmentFor(i).replace(paramObject1, i, paramObject2);
  }
  
  public void clear()
  {
    for (Segment localSegment : this.segments) {
      localSegment.clear();
    }
  }
  
  void invalidateAll(Iterable paramIterable)
  {
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      remove(localObject);
    }
  }
  
  public Set keySet()
  {
    Set localSet = this.keySet;
    return localSet != null ? localSet : (this.keySet = new KeySet(this));
  }
  
  public Collection values()
  {
    Collection localCollection = this.values;
    return localCollection != null ? localCollection : (this.values = new Values(this));
  }
  
  @GwtIncompatible("Not supported.")
  public Set entrySet()
  {
    Set localSet = this.entrySet;
    return localSet != null ? localSet : (this.entrySet = new EntrySet(this));
  }
  
  static class LocalLoadingCache
    extends LocalCache.LocalManualCache
    implements LoadingCache
  {
    private static final long serialVersionUID = 1L;
    
    LocalLoadingCache(CacheBuilder paramCacheBuilder, CacheLoader paramCacheLoader)
    {
      super(null);
    }
    
    public Object get(Object paramObject)
      throws ExecutionException
    {
      return this.localCache.getOrLoad(paramObject);
    }
    
    public Object getUnchecked(Object paramObject)
    {
      try
      {
        return get(paramObject);
      }
      catch (ExecutionException localExecutionException)
      {
        throw new UncheckedExecutionException(localExecutionException.getCause());
      }
    }
    
    public ImmutableMap getAll(Iterable paramIterable)
      throws ExecutionException
    {
      return this.localCache.getAll(paramIterable);
    }
    
    public void refresh(Object paramObject)
    {
      this.localCache.refresh(paramObject);
    }
    
    public final Object apply(Object paramObject)
    {
      return getUnchecked(paramObject);
    }
    
    Object writeReplace()
    {
      return new LocalCache.LoadingSerializationProxy(this.localCache);
    }
  }
  
  static class LocalManualCache
    implements Cache, Serializable
  {
    final LocalCache localCache;
    private static final long serialVersionUID = 1L;
    
    LocalManualCache(CacheBuilder paramCacheBuilder)
    {
      this(new LocalCache(paramCacheBuilder, null));
    }
    
    private LocalManualCache(LocalCache paramLocalCache)
    {
      this.localCache = paramLocalCache;
    }
    
    public Object getIfPresent(Object paramObject)
    {
      return this.localCache.getIfPresent(paramObject);
    }
    
    public Object get(Object paramObject, final Callable paramCallable)
      throws ExecutionException
    {
      Preconditions.checkNotNull(paramCallable);
      this.localCache.get(paramObject, new CacheLoader()
      {
        public Object load(Object paramAnonymousObject)
          throws Exception
        {
          return paramCallable.call();
        }
      });
    }
    
    public ImmutableMap getAllPresent(Iterable paramIterable)
    {
      return this.localCache.getAllPresent(paramIterable);
    }
    
    public void put(Object paramObject1, Object paramObject2)
    {
      this.localCache.put(paramObject1, paramObject2);
    }
    
    public void putAll(Map paramMap)
    {
      this.localCache.putAll(paramMap);
    }
    
    public void invalidate(Object paramObject)
    {
      Preconditions.checkNotNull(paramObject);
      this.localCache.remove(paramObject);
    }
    
    public void invalidateAll(Iterable paramIterable)
    {
      this.localCache.invalidateAll(paramIterable);
    }
    
    public void invalidateAll()
    {
      this.localCache.clear();
    }
    
    public long size()
    {
      return this.localCache.longSize();
    }
    
    public ConcurrentMap asMap()
    {
      return this.localCache;
    }
    
    public CacheStats stats()
    {
      AbstractCache.SimpleStatsCounter localSimpleStatsCounter = new AbstractCache.SimpleStatsCounter();
      localSimpleStatsCounter.incrementBy(this.localCache.globalStatsCounter);
      for (LocalCache.Segment localSegment : this.localCache.segments) {
        localSimpleStatsCounter.incrementBy(localSegment.statsCounter);
      }
      return localSimpleStatsCounter.snapshot();
    }
    
    public void cleanUp()
    {
      this.localCache.cleanUp();
    }
    
    Object writeReplace()
    {
      return new LocalCache.ManualSerializationProxy(this.localCache);
    }
  }
  
  static final class LoadingSerializationProxy
    extends LocalCache.ManualSerializationProxy
    implements LoadingCache, Serializable
  {
    private static final long serialVersionUID = 1L;
    transient LoadingCache autoDelegate;
    
    LoadingSerializationProxy(LocalCache paramLocalCache)
    {
      super();
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      CacheBuilder localCacheBuilder = recreateCacheBuilder();
      this.autoDelegate = localCacheBuilder.build(this.loader);
    }
    
    public Object get(Object paramObject)
      throws ExecutionException
    {
      return this.autoDelegate.get(paramObject);
    }
    
    public Object getUnchecked(Object paramObject)
    {
      return this.autoDelegate.getUnchecked(paramObject);
    }
    
    public ImmutableMap getAll(Iterable paramIterable)
      throws ExecutionException
    {
      return this.autoDelegate.getAll(paramIterable);
    }
    
    public final Object apply(Object paramObject)
    {
      return this.autoDelegate.apply(paramObject);
    }
    
    public void refresh(Object paramObject)
    {
      this.autoDelegate.refresh(paramObject);
    }
    
    private Object readResolve()
    {
      return this.autoDelegate;
    }
  }
  
  static class ManualSerializationProxy
    extends ForwardingCache
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    final LocalCache.Strength keyStrength;
    final LocalCache.Strength valueStrength;
    final Equivalence keyEquivalence;
    final Equivalence valueEquivalence;
    final long expireAfterWriteNanos;
    final long expireAfterAccessNanos;
    final long maxWeight;
    final Weigher weigher;
    final int concurrencyLevel;
    final RemovalListener removalListener;
    final Ticker ticker;
    final CacheLoader loader;
    transient Cache delegate;
    
    ManualSerializationProxy(LocalCache paramLocalCache)
    {
      this(paramLocalCache.keyStrength, paramLocalCache.valueStrength, paramLocalCache.keyEquivalence, paramLocalCache.valueEquivalence, paramLocalCache.expireAfterWriteNanos, paramLocalCache.expireAfterAccessNanos, paramLocalCache.maxWeight, paramLocalCache.weigher, paramLocalCache.concurrencyLevel, paramLocalCache.removalListener, paramLocalCache.ticker, paramLocalCache.defaultLoader);
    }
    
    private ManualSerializationProxy(LocalCache.Strength paramStrength1, LocalCache.Strength paramStrength2, Equivalence paramEquivalence1, Equivalence paramEquivalence2, long paramLong1, long paramLong2, long paramLong3, Weigher paramWeigher, int paramInt, RemovalListener paramRemovalListener, Ticker paramTicker, CacheLoader paramCacheLoader)
    {
      this.keyStrength = paramStrength1;
      this.valueStrength = paramStrength2;
      this.keyEquivalence = paramEquivalence1;
      this.valueEquivalence = paramEquivalence2;
      this.expireAfterWriteNanos = paramLong1;
      this.expireAfterAccessNanos = paramLong2;
      this.maxWeight = paramLong3;
      this.weigher = paramWeigher;
      this.concurrencyLevel = paramInt;
      this.removalListener = paramRemovalListener;
      this.ticker = ((paramTicker == Ticker.systemTicker()) || (paramTicker == CacheBuilder.NULL_TICKER) ? null : paramTicker);
      this.loader = paramCacheLoader;
    }
    
    CacheBuilder recreateCacheBuilder()
    {
      CacheBuilder localCacheBuilder = CacheBuilder.newBuilder().setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence(this.keyEquivalence).valueEquivalence(this.valueEquivalence).concurrencyLevel(this.concurrencyLevel).removalListener(this.removalListener);
      localCacheBuilder.strictParsing = false;
      if (this.expireAfterWriteNanos > 0L) {
        localCacheBuilder.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
      }
      if (this.expireAfterAccessNanos > 0L) {
        localCacheBuilder.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
      }
      if (this.weigher != CacheBuilder.OneWeigher.INSTANCE)
      {
        localCacheBuilder.weigher(this.weigher);
        if (this.maxWeight != -1L) {
          localCacheBuilder.maximumWeight(this.maxWeight);
        }
      }
      else if (this.maxWeight != -1L)
      {
        localCacheBuilder.maximumSize(this.maxWeight);
      }
      if (this.ticker != null) {
        localCacheBuilder.ticker(this.ticker);
      }
      return localCacheBuilder;
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      CacheBuilder localCacheBuilder = recreateCacheBuilder();
      this.delegate = localCacheBuilder.build();
    }
    
    private Object readResolve()
    {
      return this.delegate;
    }
    
    protected Cache delegate()
    {
      return this.delegate;
    }
  }
  
  final class EntrySet
    extends LocalCache.AbstractCacheSet
  {
    EntrySet(ConcurrentMap paramConcurrentMap)
    {
      super(paramConcurrentMap);
    }
    
    public Iterator iterator()
    {
      return new LocalCache.EntryIterator(LocalCache.this);
    }
    
    public boolean contains(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      Object localObject1 = localEntry.getKey();
      if (localObject1 == null) {
        return false;
      }
      Object localObject2 = LocalCache.this.get(localObject1);
      return (localObject2 != null) && (LocalCache.this.valueEquivalence.equivalent(localEntry.getValue(), localObject2));
    }
    
    public boolean remove(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      Object localObject = localEntry.getKey();
      return (localObject != null) && (LocalCache.this.remove(localObject, localEntry.getValue()));
    }
  }
  
  final class Values
    extends LocalCache.AbstractCacheSet
  {
    Values(ConcurrentMap paramConcurrentMap)
    {
      super(paramConcurrentMap);
    }
    
    public Iterator iterator()
    {
      return new LocalCache.ValueIterator(LocalCache.this);
    }
    
    public boolean contains(Object paramObject)
    {
      return this.map.containsValue(paramObject);
    }
  }
  
  final class KeySet
    extends LocalCache.AbstractCacheSet
  {
    KeySet(ConcurrentMap paramConcurrentMap)
    {
      super(paramConcurrentMap);
    }
    
    public Iterator iterator()
    {
      return new LocalCache.KeyIterator(LocalCache.this);
    }
    
    public boolean contains(Object paramObject)
    {
      return this.map.containsKey(paramObject);
    }
    
    public boolean remove(Object paramObject)
    {
      return this.map.remove(paramObject) != null;
    }
  }
  
  abstract class AbstractCacheSet
    extends AbstractSet
  {
    final ConcurrentMap map;
    
    AbstractCacheSet(ConcurrentMap paramConcurrentMap)
    {
      this.map = paramConcurrentMap;
    }
    
    public int size()
    {
      return this.map.size();
    }
    
    public boolean isEmpty()
    {
      return this.map.isEmpty();
    }
    
    public void clear()
    {
      this.map.clear();
    }
  }
  
  final class EntryIterator
    extends LocalCache.HashIterator
  {
    EntryIterator()
    {
      super();
    }
    
    public Map.Entry next()
    {
      return nextEntry();
    }
  }
  
  final class WriteThroughEntry
    implements Map.Entry
  {
    final Object key;
    Object value;
    
    WriteThroughEntry(Object paramObject1, Object paramObject2)
    {
      this.key = paramObject1;
      this.value = paramObject2;
    }
    
    public Object getKey()
    {
      return this.key;
    }
    
    public Object getValue()
    {
      return this.value;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof Map.Entry))
      {
        Map.Entry localEntry = (Map.Entry)paramObject;
        return (this.key.equals(localEntry.getKey())) && (this.value.equals(localEntry.getValue()));
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.key.hashCode() ^ this.value.hashCode();
    }
    
    public Object setValue(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public String toString()
    {
      return getKey() + "=" + getValue();
    }
  }
  
  final class ValueIterator
    extends LocalCache.HashIterator
  {
    ValueIterator()
    {
      super();
    }
    
    public Object next()
    {
      return nextEntry().getValue();
    }
  }
  
  final class KeyIterator
    extends LocalCache.HashIterator
  {
    KeyIterator()
    {
      super();
    }
    
    public Object next()
    {
      return nextEntry().getKey();
    }
  }
  
  abstract class HashIterator
    implements Iterator
  {
    int nextSegmentIndex = LocalCache.this.segments.length - 1;
    int nextTableIndex = -1;
    LocalCache.Segment currentSegment;
    AtomicReferenceArray currentTable;
    LocalCache.ReferenceEntry nextEntry;
    LocalCache.WriteThroughEntry nextExternal;
    LocalCache.WriteThroughEntry lastReturned;
    
    HashIterator()
    {
      advance();
    }
    
    public abstract Object next();
    
    final void advance()
    {
      this.nextExternal = null;
      if (nextInChain()) {
        return;
      }
      if (nextInTable()) {
        return;
      }
      while (this.nextSegmentIndex >= 0)
      {
        this.currentSegment = LocalCache.this.segments[(this.nextSegmentIndex--)];
        if (this.currentSegment.count != 0)
        {
          this.currentTable = this.currentSegment.table;
          this.nextTableIndex = (this.currentTable.length() - 1);
          if (nextInTable()) {}
        }
      }
    }
    
    boolean nextInChain()
    {
      if (this.nextEntry != null) {
        for (this.nextEntry = this.nextEntry.getNext(); this.nextEntry != null; this.nextEntry = this.nextEntry.getNext()) {
          if (advanceTo(this.nextEntry)) {
            return true;
          }
        }
      }
      return false;
    }
    
    boolean nextInTable()
    {
      while (this.nextTableIndex >= 0) {
        if (((this.nextEntry = (LocalCache.ReferenceEntry)this.currentTable.get(this.nextTableIndex--)) != null) && ((advanceTo(this.nextEntry)) || (nextInChain()))) {
          return true;
        }
      }
      return false;
    }
    
    boolean advanceTo(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      try
      {
        long l = LocalCache.this.ticker.read();
        Object localObject1 = paramReferenceEntry.getKey();
        Object localObject2 = LocalCache.this.getLiveValue(paramReferenceEntry, l);
        if (localObject2 != null)
        {
          this.nextExternal = new LocalCache.WriteThroughEntry(LocalCache.this, localObject1, localObject2);
          bool = true;
          return bool;
        }
        boolean bool = false;
        return bool;
      }
      finally
      {
        this.currentSegment.postReadCleanup();
      }
    }
    
    public boolean hasNext()
    {
      return this.nextExternal != null;
    }
    
    LocalCache.WriteThroughEntry nextEntry()
    {
      if (this.nextExternal == null) {
        throw new NoSuchElementException();
      }
      this.lastReturned = this.nextExternal;
      advance();
      return this.lastReturned;
    }
    
    public void remove()
    {
      Preconditions.checkState(this.lastReturned != null);
      LocalCache.this.remove(this.lastReturned.getKey());
      this.lastReturned = null;
    }
  }
  
  static final class AccessQueue
    extends AbstractQueue
  {
    final LocalCache.ReferenceEntry head = new LocalCache.AbstractReferenceEntry()
    {
      LocalCache.ReferenceEntry nextAccess = this;
      LocalCache.ReferenceEntry previousAccess = this;
      
      public long getAccessTime()
      {
        return Long.MAX_VALUE;
      }
      
      public void setAccessTime(long paramAnonymousLong) {}
      
      public LocalCache.ReferenceEntry getNextInAccessQueue()
      {
        return this.nextAccess;
      }
      
      public void setNextInAccessQueue(LocalCache.ReferenceEntry paramAnonymousReferenceEntry)
      {
        this.nextAccess = paramAnonymousReferenceEntry;
      }
      
      public LocalCache.ReferenceEntry getPreviousInAccessQueue()
      {
        return this.previousAccess;
      }
      
      public void setPreviousInAccessQueue(LocalCache.ReferenceEntry paramAnonymousReferenceEntry)
      {
        this.previousAccess = paramAnonymousReferenceEntry;
      }
    };
    
    public boolean offer(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      LocalCache.connectAccessOrder(paramReferenceEntry.getPreviousInAccessQueue(), paramReferenceEntry.getNextInAccessQueue());
      LocalCache.connectAccessOrder(this.head.getPreviousInAccessQueue(), paramReferenceEntry);
      LocalCache.connectAccessOrder(paramReferenceEntry, this.head);
      return true;
    }
    
    public LocalCache.ReferenceEntry peek()
    {
      LocalCache.ReferenceEntry localReferenceEntry = this.head.getNextInAccessQueue();
      return localReferenceEntry == this.head ? null : localReferenceEntry;
    }
    
    public LocalCache.ReferenceEntry poll()
    {
      LocalCache.ReferenceEntry localReferenceEntry = this.head.getNextInAccessQueue();
      if (localReferenceEntry == this.head) {
        return null;
      }
      remove(localReferenceEntry);
      return localReferenceEntry;
    }
    
    public boolean remove(Object paramObject)
    {
      LocalCache.ReferenceEntry localReferenceEntry1 = (LocalCache.ReferenceEntry)paramObject;
      LocalCache.ReferenceEntry localReferenceEntry2 = localReferenceEntry1.getPreviousInAccessQueue();
      LocalCache.ReferenceEntry localReferenceEntry3 = localReferenceEntry1.getNextInAccessQueue();
      LocalCache.connectAccessOrder(localReferenceEntry2, localReferenceEntry3);
      LocalCache.nullifyAccessOrder(localReferenceEntry1);
      return localReferenceEntry3 != LocalCache.NullEntry.INSTANCE;
    }
    
    public boolean contains(Object paramObject)
    {
      LocalCache.ReferenceEntry localReferenceEntry = (LocalCache.ReferenceEntry)paramObject;
      return localReferenceEntry.getNextInAccessQueue() != LocalCache.NullEntry.INSTANCE;
    }
    
    public boolean isEmpty()
    {
      return this.head.getNextInAccessQueue() == this.head;
    }
    
    public int size()
    {
      int i = 0;
      for (LocalCache.ReferenceEntry localReferenceEntry = this.head.getNextInAccessQueue(); localReferenceEntry != this.head; localReferenceEntry = localReferenceEntry.getNextInAccessQueue()) {
        i++;
      }
      return i;
    }
    
    public void clear()
    {
      LocalCache.ReferenceEntry localReferenceEntry;
      for (Object localObject = this.head.getNextInAccessQueue(); localObject != this.head; localObject = localReferenceEntry)
      {
        localReferenceEntry = ((LocalCache.ReferenceEntry)localObject).getNextInAccessQueue();
        LocalCache.nullifyAccessOrder((LocalCache.ReferenceEntry)localObject);
      }
      this.head.setNextInAccessQueue(this.head);
      this.head.setPreviousInAccessQueue(this.head);
    }
    
    public Iterator iterator()
    {
      new AbstractSequentialIterator(peek())
      {
        protected LocalCache.ReferenceEntry computeNext(LocalCache.ReferenceEntry paramAnonymousReferenceEntry)
        {
          LocalCache.ReferenceEntry localReferenceEntry = paramAnonymousReferenceEntry.getNextInAccessQueue();
          return localReferenceEntry == LocalCache.AccessQueue.this.head ? null : localReferenceEntry;
        }
      };
    }
  }
  
  static final class WriteQueue
    extends AbstractQueue
  {
    final LocalCache.ReferenceEntry head = new LocalCache.AbstractReferenceEntry()
    {
      LocalCache.ReferenceEntry nextWrite = this;
      LocalCache.ReferenceEntry previousWrite = this;
      
      public long getWriteTime()
      {
        return Long.MAX_VALUE;
      }
      
      public void setWriteTime(long paramAnonymousLong) {}
      
      public LocalCache.ReferenceEntry getNextInWriteQueue()
      {
        return this.nextWrite;
      }
      
      public void setNextInWriteQueue(LocalCache.ReferenceEntry paramAnonymousReferenceEntry)
      {
        this.nextWrite = paramAnonymousReferenceEntry;
      }
      
      public LocalCache.ReferenceEntry getPreviousInWriteQueue()
      {
        return this.previousWrite;
      }
      
      public void setPreviousInWriteQueue(LocalCache.ReferenceEntry paramAnonymousReferenceEntry)
      {
        this.previousWrite = paramAnonymousReferenceEntry;
      }
    };
    
    public boolean offer(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      LocalCache.connectWriteOrder(paramReferenceEntry.getPreviousInWriteQueue(), paramReferenceEntry.getNextInWriteQueue());
      LocalCache.connectWriteOrder(this.head.getPreviousInWriteQueue(), paramReferenceEntry);
      LocalCache.connectWriteOrder(paramReferenceEntry, this.head);
      return true;
    }
    
    public LocalCache.ReferenceEntry peek()
    {
      LocalCache.ReferenceEntry localReferenceEntry = this.head.getNextInWriteQueue();
      return localReferenceEntry == this.head ? null : localReferenceEntry;
    }
    
    public LocalCache.ReferenceEntry poll()
    {
      LocalCache.ReferenceEntry localReferenceEntry = this.head.getNextInWriteQueue();
      if (localReferenceEntry == this.head) {
        return null;
      }
      remove(localReferenceEntry);
      return localReferenceEntry;
    }
    
    public boolean remove(Object paramObject)
    {
      LocalCache.ReferenceEntry localReferenceEntry1 = (LocalCache.ReferenceEntry)paramObject;
      LocalCache.ReferenceEntry localReferenceEntry2 = localReferenceEntry1.getPreviousInWriteQueue();
      LocalCache.ReferenceEntry localReferenceEntry3 = localReferenceEntry1.getNextInWriteQueue();
      LocalCache.connectWriteOrder(localReferenceEntry2, localReferenceEntry3);
      LocalCache.nullifyWriteOrder(localReferenceEntry1);
      return localReferenceEntry3 != LocalCache.NullEntry.INSTANCE;
    }
    
    public boolean contains(Object paramObject)
    {
      LocalCache.ReferenceEntry localReferenceEntry = (LocalCache.ReferenceEntry)paramObject;
      return localReferenceEntry.getNextInWriteQueue() != LocalCache.NullEntry.INSTANCE;
    }
    
    public boolean isEmpty()
    {
      return this.head.getNextInWriteQueue() == this.head;
    }
    
    public int size()
    {
      int i = 0;
      for (LocalCache.ReferenceEntry localReferenceEntry = this.head.getNextInWriteQueue(); localReferenceEntry != this.head; localReferenceEntry = localReferenceEntry.getNextInWriteQueue()) {
        i++;
      }
      return i;
    }
    
    public void clear()
    {
      LocalCache.ReferenceEntry localReferenceEntry;
      for (Object localObject = this.head.getNextInWriteQueue(); localObject != this.head; localObject = localReferenceEntry)
      {
        localReferenceEntry = ((LocalCache.ReferenceEntry)localObject).getNextInWriteQueue();
        LocalCache.nullifyWriteOrder((LocalCache.ReferenceEntry)localObject);
      }
      this.head.setNextInWriteQueue(this.head);
      this.head.setPreviousInWriteQueue(this.head);
    }
    
    public Iterator iterator()
    {
      new AbstractSequentialIterator(peek())
      {
        protected LocalCache.ReferenceEntry computeNext(LocalCache.ReferenceEntry paramAnonymousReferenceEntry)
        {
          LocalCache.ReferenceEntry localReferenceEntry = paramAnonymousReferenceEntry.getNextInWriteQueue();
          return localReferenceEntry == LocalCache.WriteQueue.this.head ? null : localReferenceEntry;
        }
      };
    }
  }
  
  static class LoadingValueReference
    implements LocalCache.ValueReference
  {
    volatile LocalCache.ValueReference oldValue;
    final SettableFuture futureValue = SettableFuture.create();
    final Stopwatch stopwatch = new Stopwatch();
    
    public LoadingValueReference()
    {
      this(LocalCache.unset());
    }
    
    public LoadingValueReference(LocalCache.ValueReference paramValueReference)
    {
      this.oldValue = paramValueReference;
    }
    
    public boolean isLoading()
    {
      return true;
    }
    
    public boolean isActive()
    {
      return this.oldValue.isActive();
    }
    
    public int getWeight()
    {
      return this.oldValue.getWeight();
    }
    
    public boolean set(Object paramObject)
    {
      return this.futureValue.set(paramObject);
    }
    
    public boolean setException(Throwable paramThrowable)
    {
      return setException(this.futureValue, paramThrowable);
    }
    
    private static boolean setException(SettableFuture paramSettableFuture, Throwable paramThrowable)
    {
      try
      {
        return paramSettableFuture.setException(paramThrowable);
      }
      catch (Error localError) {}
      return false;
    }
    
    private ListenableFuture fullyFailedFuture(Throwable paramThrowable)
    {
      SettableFuture localSettableFuture = SettableFuture.create();
      setException(localSettableFuture, paramThrowable);
      return localSettableFuture;
    }
    
    public void notifyNewValue(Object paramObject)
    {
      if (paramObject != null) {
        set(paramObject);
      } else {
        this.oldValue = LocalCache.unset();
      }
    }
    
    public ListenableFuture loadFuture(Object paramObject, CacheLoader paramCacheLoader)
    {
      this.stopwatch.start();
      Object localObject1 = this.oldValue.get();
      try
      {
        if (localObject1 == null)
        {
          localObject2 = paramCacheLoader.load(paramObject);
          return set(localObject2) ? this.futureValue : Futures.immediateFuture(localObject2);
        }
        Object localObject2 = paramCacheLoader.reload(paramObject, localObject1);
        return (ListenableFuture)(localObject2 != null ? localObject2 : Futures.immediateFuture(null));
      }
      catch (Throwable localThrowable)
      {
        if ((localThrowable instanceof InterruptedException)) {
          Thread.currentThread().interrupt();
        }
        return setException(localThrowable) ? this.futureValue : fullyFailedFuture(localThrowable);
      }
    }
    
    public long elapsedNanos()
    {
      return this.stopwatch.elapsed(TimeUnit.NANOSECONDS);
    }
    
    public Object waitForValue()
      throws ExecutionException
    {
      return Uninterruptibles.getUninterruptibly(this.futureValue);
    }
    
    public Object get()
    {
      return this.oldValue.get();
    }
    
    public LocalCache.ValueReference getOldValue()
    {
      return this.oldValue;
    }
    
    public LocalCache.ReferenceEntry getEntry()
    {
      return null;
    }
    
    public LocalCache.ValueReference copyFor(ReferenceQueue paramReferenceQueue, Object paramObject, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      return this;
    }
  }
  
  static class Segment
    extends ReentrantLock
  {
    final LocalCache map;
    volatile int count;
    int totalWeight;
    int modCount;
    int threshold;
    volatile AtomicReferenceArray table;
    final long maxSegmentWeight;
    final ReferenceQueue keyReferenceQueue;
    final ReferenceQueue valueReferenceQueue;
    final Queue recencyQueue;
    final AtomicInteger readCount = new AtomicInteger();
    final Queue writeQueue;
    final Queue accessQueue;
    final AbstractCache.StatsCounter statsCounter;
    
    Segment(LocalCache paramLocalCache, int paramInt, long paramLong, AbstractCache.StatsCounter paramStatsCounter)
    {
      this.map = paramLocalCache;
      this.maxSegmentWeight = paramLong;
      this.statsCounter = ((AbstractCache.StatsCounter)Preconditions.checkNotNull(paramStatsCounter));
      initTable(newEntryArray(paramInt));
      this.keyReferenceQueue = (paramLocalCache.usesKeyReferences() ? new ReferenceQueue() : null);
      this.valueReferenceQueue = (paramLocalCache.usesValueReferences() ? new ReferenceQueue() : null);
      this.recencyQueue = (paramLocalCache.usesAccessQueue() ? new ConcurrentLinkedQueue() : LocalCache.discardingQueue());
      this.writeQueue = (paramLocalCache.usesWriteQueue() ? new LocalCache.WriteQueue() : LocalCache.discardingQueue());
      this.accessQueue = (paramLocalCache.usesAccessQueue() ? new LocalCache.AccessQueue() : LocalCache.discardingQueue());
    }
    
    AtomicReferenceArray newEntryArray(int paramInt)
    {
      return new AtomicReferenceArray(paramInt);
    }
    
    void initTable(AtomicReferenceArray paramAtomicReferenceArray)
    {
      this.threshold = (paramAtomicReferenceArray.length() * 3 / 4);
      if ((!this.map.customWeigher()) && (this.threshold == this.maxSegmentWeight)) {
        this.threshold += 1;
      }
      this.table = paramAtomicReferenceArray;
    }
    
    LocalCache.ReferenceEntry newEntry(Object paramObject, int paramInt, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      return this.map.entryFactory.newEntry(this, Preconditions.checkNotNull(paramObject), paramInt, paramReferenceEntry);
    }
    
    LocalCache.ReferenceEntry copyEntry(LocalCache.ReferenceEntry paramReferenceEntry1, LocalCache.ReferenceEntry paramReferenceEntry2)
    {
      if (paramReferenceEntry1.getKey() == null) {
        return null;
      }
      LocalCache.ValueReference localValueReference = paramReferenceEntry1.getValueReference();
      Object localObject = localValueReference.get();
      if ((localObject == null) && (localValueReference.isActive())) {
        return null;
      }
      LocalCache.ReferenceEntry localReferenceEntry = this.map.entryFactory.copyEntry(this, paramReferenceEntry1, paramReferenceEntry2);
      localReferenceEntry.setValueReference(localValueReference.copyFor(this.valueReferenceQueue, localObject, localReferenceEntry));
      return localReferenceEntry;
    }
    
    void setValue(LocalCache.ReferenceEntry paramReferenceEntry, Object paramObject1, Object paramObject2, long paramLong)
    {
      LocalCache.ValueReference localValueReference1 = paramReferenceEntry.getValueReference();
      int i = this.map.weigher.weigh(paramObject1, paramObject2);
      Preconditions.checkState(i >= 0, "Weights must be non-negative");
      LocalCache.ValueReference localValueReference2 = this.map.valueStrength.referenceValue(this, paramReferenceEntry, paramObject2, i);
      paramReferenceEntry.setValueReference(localValueReference2);
      recordWrite(paramReferenceEntry, i, paramLong);
      localValueReference1.notifyNewValue(paramObject2);
    }
    
    Object get(Object paramObject, int paramInt, CacheLoader paramCacheLoader)
      throws ExecutionException
    {
      Preconditions.checkNotNull(paramObject);
      Preconditions.checkNotNull(paramCacheLoader);
      try
      {
        if (this.count != 0)
        {
          localObject1 = getEntry(paramObject, paramInt);
          if (localObject1 != null)
          {
            long l = this.map.ticker.read();
            Object localObject2 = getLiveValue((LocalCache.ReferenceEntry)localObject1, l);
            if (localObject2 != null)
            {
              recordRead((LocalCache.ReferenceEntry)localObject1, l);
              this.statsCounter.recordHits(1);
              localObject3 = scheduleRefresh((LocalCache.ReferenceEntry)localObject1, paramObject, paramInt, localObject2, l, paramCacheLoader);
              return localObject3;
            }
            Object localObject3 = ((LocalCache.ReferenceEntry)localObject1).getValueReference();
            if (((LocalCache.ValueReference)localObject3).isLoading())
            {
              Object localObject4 = waitForLoadingValue((LocalCache.ReferenceEntry)localObject1, paramObject, (LocalCache.ValueReference)localObject3);
              return localObject4;
            }
          }
        }
        Object localObject1 = lockedGetOrLoad(paramObject, paramInt, paramCacheLoader);
        return localObject1;
      }
      catch (ExecutionException localExecutionException)
      {
        Throwable localThrowable = localExecutionException.getCause();
        if ((localThrowable instanceof Error)) {
          throw new ExecutionError((Error)localThrowable);
        }
        if ((localThrowable instanceof RuntimeException)) {
          throw new UncheckedExecutionException(localThrowable);
        }
        throw localExecutionException;
      }
      finally
      {
        postReadCleanup();
      }
    }
    
    Object lockedGetOrLoad(Object paramObject, int paramInt, CacheLoader paramCacheLoader)
      throws ExecutionException
    {
      localValueReference = null;
      LocalCache.LoadingValueReference localLoadingValueReference = null;
      int i = 1;
      lock();
      try
      {
        long l = this.map.ticker.read();
        preWriteCleanup(l);
        int j = this.count - 1;
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int k = paramInt & localAtomicReferenceArray.length() - 1;
        LocalCache.ReferenceEntry localReferenceEntry2 = (LocalCache.ReferenceEntry)localAtomicReferenceArray.get(k);
        for (localReferenceEntry1 = localReferenceEntry2; localReferenceEntry1 != null; localReferenceEntry1 = localReferenceEntry1.getNext())
        {
          Object localObject2 = localReferenceEntry1.getKey();
          if ((localReferenceEntry1.getHash() == paramInt) && (localObject2 != null) && (this.map.keyEquivalence.equivalent(paramObject, localObject2)))
          {
            localValueReference = localReferenceEntry1.getValueReference();
            if (localValueReference.isLoading())
            {
              i = 0;
              break;
            }
            Object localObject3 = localValueReference.get();
            if (localObject3 == null)
            {
              enqueueNotification(localObject2, paramInt, localValueReference, RemovalCause.COLLECTED);
            }
            else if (this.map.isExpired(localReferenceEntry1, l))
            {
              enqueueNotification(localObject2, paramInt, localValueReference, RemovalCause.EXPIRED);
            }
            else
            {
              recordLockedRead(localReferenceEntry1, l);
              this.statsCounter.recordHits(1);
              Object localObject4 = localObject3;
              return localObject4;
            }
            this.writeQueue.remove(localReferenceEntry1);
            this.accessQueue.remove(localReferenceEntry1);
            this.count = j;
            break;
          }
        }
        if (i != 0)
        {
          localLoadingValueReference = new LocalCache.LoadingValueReference();
          if (localReferenceEntry1 == null)
          {
            localReferenceEntry1 = newEntry(paramObject, paramInt, localReferenceEntry2);
            localReferenceEntry1.setValueReference(localLoadingValueReference);
            localAtomicReferenceArray.set(k, localReferenceEntry1);
          }
          else
          {
            localReferenceEntry1.setValueReference(localLoadingValueReference);
          }
        }
      }
      finally
      {
        unlock();
        postWriteCleanup();
      }
      if (i != 0) {
        try
        {
          synchronized (localReferenceEntry1)
          {
            Object localObject1 = loadSync(paramObject, paramInt, localLoadingValueReference, paramCacheLoader);
            return localObject1;
          }
          return waitForLoadingValue(localReferenceEntry1, paramObject, localValueReference);
        }
        finally
        {
          this.statsCounter.recordMisses(1);
        }
      }
    }
    
    Object waitForLoadingValue(LocalCache.ReferenceEntry paramReferenceEntry, Object paramObject, LocalCache.ValueReference paramValueReference)
      throws ExecutionException
    {
      if (!paramValueReference.isLoading()) {
        throw new AssertionError();
      }
      Preconditions.checkState(!Thread.holdsLock(paramReferenceEntry), "Recursive load");
      try
      {
        Object localObject1 = paramValueReference.waitForValue();
        if (localObject1 == null) {
          throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + paramObject + ".");
        }
        long l = this.map.ticker.read();
        recordRead(paramReferenceEntry, l);
        Object localObject2 = localObject1;
        return localObject2;
      }
      finally
      {
        this.statsCounter.recordMisses(1);
      }
    }
    
    Object loadSync(Object paramObject, int paramInt, LocalCache.LoadingValueReference paramLoadingValueReference, CacheLoader paramCacheLoader)
      throws ExecutionException
    {
      ListenableFuture localListenableFuture = paramLoadingValueReference.loadFuture(paramObject, paramCacheLoader);
      return getAndRecordStats(paramObject, paramInt, paramLoadingValueReference, localListenableFuture);
    }
    
    ListenableFuture loadAsync(final Object paramObject, final int paramInt, final LocalCache.LoadingValueReference paramLoadingValueReference, CacheLoader paramCacheLoader)
    {
      final ListenableFuture localListenableFuture = paramLoadingValueReference.loadFuture(paramObject, paramCacheLoader);
      localListenableFuture.addListener(new Runnable()
      {
        public void run()
        {
          try
          {
            Object localObject = LocalCache.Segment.this.getAndRecordStats(paramObject, paramInt, paramLoadingValueReference, localListenableFuture);
            paramLoadingValueReference.set(localObject);
          }
          catch (Throwable localThrowable)
          {
            LocalCache.logger.log(Level.WARNING, "Exception thrown during refresh", localThrowable);
            paramLoadingValueReference.setException(localThrowable);
          }
        }
      }, LocalCache.sameThreadExecutor);
      return localListenableFuture;
    }
    
    Object getAndRecordStats(Object paramObject, int paramInt, LocalCache.LoadingValueReference paramLoadingValueReference, ListenableFuture paramListenableFuture)
      throws ExecutionException
    {
      Object localObject1 = null;
      try
      {
        localObject1 = Uninterruptibles.getUninterruptibly(paramListenableFuture);
        if (localObject1 == null) {
          throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + paramObject + ".");
        }
        this.statsCounter.recordLoadSuccess(paramLoadingValueReference.elapsedNanos());
        storeLoadedValue(paramObject, paramInt, paramLoadingValueReference, localObject1);
        Object localObject2 = localObject1;
        return localObject2;
      }
      finally
      {
        if (localObject1 == null)
        {
          this.statsCounter.recordLoadException(paramLoadingValueReference.elapsedNanos());
          removeLoadingValue(paramObject, paramInt, paramLoadingValueReference);
        }
      }
    }
    
    Object scheduleRefresh(LocalCache.ReferenceEntry paramReferenceEntry, Object paramObject1, int paramInt, Object paramObject2, long paramLong, CacheLoader paramCacheLoader)
    {
      if ((this.map.refreshes()) && (paramLong - paramReferenceEntry.getWriteTime() > this.map.refreshNanos) && (!paramReferenceEntry.getValueReference().isLoading()))
      {
        Object localObject = refresh(paramObject1, paramInt, paramCacheLoader, true);
        if (localObject != null) {
          return localObject;
        }
      }
      return paramObject2;
    }
    
    Object refresh(Object paramObject, int paramInt, CacheLoader paramCacheLoader, boolean paramBoolean)
    {
      LocalCache.LoadingValueReference localLoadingValueReference = insertLoadingValueReference(paramObject, paramInt, paramBoolean);
      if (localLoadingValueReference == null) {
        return null;
      }
      ListenableFuture localListenableFuture = loadAsync(paramObject, paramInt, localLoadingValueReference, paramCacheLoader);
      if (localListenableFuture.isDone()) {
        try
        {
          return Uninterruptibles.getUninterruptibly(localListenableFuture);
        }
        catch (Throwable localThrowable) {}
      }
      return null;
    }
    
    LocalCache.LoadingValueReference insertLoadingValueReference(Object paramObject, int paramInt, boolean paramBoolean)
    {
      Object localObject1 = null;
      lock();
      try
      {
        long l = this.map.ticker.read();
        preWriteCleanup(l);
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int i = paramInt & localAtomicReferenceArray.length() - 1;
        LocalCache.ReferenceEntry localReferenceEntry = (LocalCache.ReferenceEntry)localAtomicReferenceArray.get(i);
        for (localObject1 = localReferenceEntry; localObject1 != null; localObject1 = ((LocalCache.ReferenceEntry)localObject1).getNext())
        {
          localObject2 = ((LocalCache.ReferenceEntry)localObject1).getKey();
          if ((((LocalCache.ReferenceEntry)localObject1).getHash() == paramInt) && (localObject2 != null) && (this.map.keyEquivalence.equivalent(paramObject, localObject2)))
          {
            localObject3 = ((LocalCache.ReferenceEntry)localObject1).getValueReference();
            if ((((LocalCache.ValueReference)localObject3).isLoading()) || ((paramBoolean) && (l - ((LocalCache.ReferenceEntry)localObject1).getWriteTime() < this.map.refreshNanos)))
            {
              localLoadingValueReference1 = null;
              return localLoadingValueReference1;
            }
            this.modCount += 1;
            LocalCache.LoadingValueReference localLoadingValueReference1 = new LocalCache.LoadingValueReference((LocalCache.ValueReference)localObject3);
            ((LocalCache.ReferenceEntry)localObject1).setValueReference(localLoadingValueReference1);
            LocalCache.LoadingValueReference localLoadingValueReference2 = localLoadingValueReference1;
            return localLoadingValueReference2;
          }
        }
        this.modCount += 1;
        Object localObject2 = new LocalCache.LoadingValueReference();
        localObject1 = newEntry(paramObject, paramInt, localReferenceEntry);
        ((LocalCache.ReferenceEntry)localObject1).setValueReference((LocalCache.ValueReference)localObject2);
        localAtomicReferenceArray.set(i, localObject1);
        Object localObject3 = localObject2;
        return (LocalCache.LoadingValueReference)localObject3;
      }
      finally
      {
        unlock();
        postWriteCleanup();
      }
    }
    
    void tryDrainReferenceQueues()
    {
      if (tryLock()) {
        try
        {
          drainReferenceQueues();
        }
        finally
        {
          unlock();
        }
      }
    }
    
    void drainReferenceQueues()
    {
      if (this.map.usesKeyReferences()) {
        drainKeyReferenceQueue();
      }
      if (this.map.usesValueReferences()) {
        drainValueReferenceQueue();
      }
    }
    
    void drainKeyReferenceQueue()
    {
      int i = 0;
      Reference localReference;
      while ((localReference = this.keyReferenceQueue.poll()) != null)
      {
        LocalCache.ReferenceEntry localReferenceEntry = (LocalCache.ReferenceEntry)localReference;
        this.map.reclaimKey(localReferenceEntry);
        i++;
        if (i == 16) {
          break;
        }
      }
    }
    
    void drainValueReferenceQueue()
    {
      int i = 0;
      Reference localReference;
      while ((localReference = this.valueReferenceQueue.poll()) != null)
      {
        LocalCache.ValueReference localValueReference = (LocalCache.ValueReference)localReference;
        this.map.reclaimValue(localValueReference);
        i++;
        if (i == 16) {
          break;
        }
      }
    }
    
    void clearReferenceQueues()
    {
      if (this.map.usesKeyReferences()) {
        clearKeyReferenceQueue();
      }
      if (this.map.usesValueReferences()) {
        clearValueReferenceQueue();
      }
    }
    
    void clearKeyReferenceQueue()
    {
      while (this.keyReferenceQueue.poll() != null) {}
    }
    
    void clearValueReferenceQueue()
    {
      while (this.valueReferenceQueue.poll() != null) {}
    }
    
    void recordRead(LocalCache.ReferenceEntry paramReferenceEntry, long paramLong)
    {
      if (this.map.recordsAccess()) {
        paramReferenceEntry.setAccessTime(paramLong);
      }
      this.recencyQueue.add(paramReferenceEntry);
    }
    
    void recordLockedRead(LocalCache.ReferenceEntry paramReferenceEntry, long paramLong)
    {
      if (this.map.recordsAccess()) {
        paramReferenceEntry.setAccessTime(paramLong);
      }
      this.accessQueue.add(paramReferenceEntry);
    }
    
    void recordWrite(LocalCache.ReferenceEntry paramReferenceEntry, int paramInt, long paramLong)
    {
      drainRecencyQueue();
      this.totalWeight += paramInt;
      if (this.map.recordsAccess()) {
        paramReferenceEntry.setAccessTime(paramLong);
      }
      if (this.map.recordsWrite()) {
        paramReferenceEntry.setWriteTime(paramLong);
      }
      this.accessQueue.add(paramReferenceEntry);
      this.writeQueue.add(paramReferenceEntry);
    }
    
    void drainRecencyQueue()
    {
      LocalCache.ReferenceEntry localReferenceEntry;
      while ((localReferenceEntry = (LocalCache.ReferenceEntry)this.recencyQueue.poll()) != null) {
        if (this.accessQueue.contains(localReferenceEntry)) {
          this.accessQueue.add(localReferenceEntry);
        }
      }
    }
    
    void tryExpireEntries(long paramLong)
    {
      if (tryLock()) {
        try
        {
          expireEntries(paramLong);
        }
        finally
        {
          unlock();
        }
      }
    }
    
    void expireEntries(long paramLong)
    {
      drainRecencyQueue();
      LocalCache.ReferenceEntry localReferenceEntry;
      while (((localReferenceEntry = (LocalCache.ReferenceEntry)this.writeQueue.peek()) != null) && (this.map.isExpired(localReferenceEntry, paramLong))) {
        if (!removeEntry(localReferenceEntry, localReferenceEntry.getHash(), RemovalCause.EXPIRED)) {
          throw new AssertionError();
        }
      }
      while (((localReferenceEntry = (LocalCache.ReferenceEntry)this.accessQueue.peek()) != null) && (this.map.isExpired(localReferenceEntry, paramLong))) {
        if (!removeEntry(localReferenceEntry, localReferenceEntry.getHash(), RemovalCause.EXPIRED)) {
          throw new AssertionError();
        }
      }
    }
    
    void enqueueNotification(LocalCache.ReferenceEntry paramReferenceEntry, RemovalCause paramRemovalCause)
    {
      enqueueNotification(paramReferenceEntry.getKey(), paramReferenceEntry.getHash(), paramReferenceEntry.getValueReference(), paramRemovalCause);
    }
    
    void enqueueNotification(Object paramObject, int paramInt, LocalCache.ValueReference paramValueReference, RemovalCause paramRemovalCause)
    {
      this.totalWeight -= paramValueReference.getWeight();
      if (paramRemovalCause.wasEvicted()) {
        this.statsCounter.recordEviction();
      }
      if (this.map.removalNotificationQueue != LocalCache.DISCARDING_QUEUE)
      {
        Object localObject = paramValueReference.get();
        RemovalNotification localRemovalNotification = new RemovalNotification(paramObject, localObject, paramRemovalCause);
        this.map.removalNotificationQueue.offer(localRemovalNotification);
      }
    }
    
    void evictEntries()
    {
      if (!this.map.evictsBySize()) {
        return;
      }
      drainRecencyQueue();
      while (this.totalWeight > this.maxSegmentWeight)
      {
        LocalCache.ReferenceEntry localReferenceEntry = getNextEvictable();
        if (!removeEntry(localReferenceEntry, localReferenceEntry.getHash(), RemovalCause.SIZE)) {
          throw new AssertionError();
        }
      }
    }
    
    LocalCache.ReferenceEntry getNextEvictable()
    {
      Iterator localIterator = this.accessQueue.iterator();
      while (localIterator.hasNext())
      {
        LocalCache.ReferenceEntry localReferenceEntry = (LocalCache.ReferenceEntry)localIterator.next();
        int i = localReferenceEntry.getValueReference().getWeight();
        if (i > 0) {
          return localReferenceEntry;
        }
      }
      throw new AssertionError();
    }
    
    LocalCache.ReferenceEntry getFirst(int paramInt)
    {
      AtomicReferenceArray localAtomicReferenceArray = this.table;
      return (LocalCache.ReferenceEntry)localAtomicReferenceArray.get(paramInt & localAtomicReferenceArray.length() - 1);
    }
    
    LocalCache.ReferenceEntry getEntry(Object paramObject, int paramInt)
    {
      for (LocalCache.ReferenceEntry localReferenceEntry = getFirst(paramInt); localReferenceEntry != null; localReferenceEntry = localReferenceEntry.getNext()) {
        if (localReferenceEntry.getHash() == paramInt)
        {
          Object localObject = localReferenceEntry.getKey();
          if (localObject == null) {
            tryDrainReferenceQueues();
          } else if (this.map.keyEquivalence.equivalent(paramObject, localObject)) {
            return localReferenceEntry;
          }
        }
      }
      return null;
    }
    
    LocalCache.ReferenceEntry getLiveEntry(Object paramObject, int paramInt, long paramLong)
    {
      LocalCache.ReferenceEntry localReferenceEntry = getEntry(paramObject, paramInt);
      if (localReferenceEntry == null) {
        return null;
      }
      if (this.map.isExpired(localReferenceEntry, paramLong))
      {
        tryExpireEntries(paramLong);
        return null;
      }
      return localReferenceEntry;
    }
    
    Object getLiveValue(LocalCache.ReferenceEntry paramReferenceEntry, long paramLong)
    {
      if (paramReferenceEntry.getKey() == null)
      {
        tryDrainReferenceQueues();
        return null;
      }
      Object localObject = paramReferenceEntry.getValueReference().get();
      if (localObject == null)
      {
        tryDrainReferenceQueues();
        return null;
      }
      if (this.map.isExpired(paramReferenceEntry, paramLong))
      {
        tryExpireEntries(paramLong);
        return null;
      }
      return localObject;
    }
    
    Object get(Object paramObject, int paramInt)
    {
      try
      {
        if (this.count != 0)
        {
          long l = this.map.ticker.read();
          LocalCache.ReferenceEntry localReferenceEntry = getLiveEntry(paramObject, paramInt, l);
          if (localReferenceEntry == null)
          {
            localObject2 = null;
            return localObject2;
          }
          Object localObject2 = localReferenceEntry.getValueReference().get();
          if (localObject2 != null)
          {
            recordRead(localReferenceEntry, l);
            Object localObject3 = scheduleRefresh(localReferenceEntry, localReferenceEntry.getKey(), paramInt, localObject2, l, this.map.defaultLoader);
            return localObject3;
          }
          tryDrainReferenceQueues();
        }
        Object localObject1 = null;
        return localObject1;
      }
      finally
      {
        postReadCleanup();
      }
    }
    
    boolean containsKey(Object paramObject, int paramInt)
    {
      try
      {
        if (this.count != 0)
        {
          long l = this.map.ticker.read();
          LocalCache.ReferenceEntry localReferenceEntry = getLiveEntry(paramObject, paramInt, l);
          if (localReferenceEntry == null)
          {
            bool2 = false;
            return bool2;
          }
          boolean bool2 = localReferenceEntry.getValueReference().get() != null;
          return bool2;
        }
        boolean bool1 = false;
        return bool1;
      }
      finally
      {
        postReadCleanup();
      }
    }
    
    @VisibleForTesting
    boolean containsValue(Object paramObject)
    {
      try
      {
        if (this.count != 0)
        {
          long l = this.map.ticker.read();
          AtomicReferenceArray localAtomicReferenceArray = this.table;
          int i = localAtomicReferenceArray.length();
          for (int j = 0; j < i; j++) {
            for (LocalCache.ReferenceEntry localReferenceEntry = (LocalCache.ReferenceEntry)localAtomicReferenceArray.get(j); localReferenceEntry != null; localReferenceEntry = localReferenceEntry.getNext())
            {
              Object localObject1 = getLiveValue(localReferenceEntry, l);
              if ((localObject1 != null) && (this.map.valueEquivalence.equivalent(paramObject, localObject1)))
              {
                boolean bool2 = true;
                return bool2;
              }
            }
          }
        }
        boolean bool1 = false;
        return bool1;
      }
      finally
      {
        postReadCleanup();
      }
    }
    
    Object put(Object paramObject1, int paramInt, Object paramObject2, boolean paramBoolean)
    {
      lock();
      try
      {
        long l = this.map.ticker.read();
        preWriteCleanup(l);
        int i = this.count + 1;
        if (i > this.threshold)
        {
          expand();
          i = this.count + 1;
        }
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int j = paramInt & localAtomicReferenceArray.length() - 1;
        LocalCache.ReferenceEntry localReferenceEntry1 = (LocalCache.ReferenceEntry)localAtomicReferenceArray.get(j);
        for (LocalCache.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext())
        {
          localObject1 = localReferenceEntry2.getKey();
          if ((localReferenceEntry2.getHash() == paramInt) && (localObject1 != null) && (this.map.keyEquivalence.equivalent(paramObject1, localObject1)))
          {
            LocalCache.ValueReference localValueReference = localReferenceEntry2.getValueReference();
            Object localObject2 = localValueReference.get();
            if (localObject2 == null)
            {
              this.modCount += 1;
              if (localValueReference.isActive())
              {
                enqueueNotification(paramObject1, paramInt, localValueReference, RemovalCause.COLLECTED);
                setValue(localReferenceEntry2, paramObject1, paramObject2, l);
                i = this.count;
              }
              else
              {
                setValue(localReferenceEntry2, paramObject1, paramObject2, l);
                i = this.count + 1;
              }
              this.count = i;
              evictEntries();
              localObject3 = null;
              return localObject3;
            }
            if (paramBoolean)
            {
              recordLockedRead(localReferenceEntry2, l);
              localObject3 = localObject2;
              return localObject3;
            }
            this.modCount += 1;
            enqueueNotification(paramObject1, paramInt, localValueReference, RemovalCause.REPLACED);
            setValue(localReferenceEntry2, paramObject1, paramObject2, l);
            evictEntries();
            Object localObject3 = localObject2;
            return localObject3;
          }
        }
        this.modCount += 1;
        localReferenceEntry2 = newEntry(paramObject1, paramInt, localReferenceEntry1);
        setValue(localReferenceEntry2, paramObject1, paramObject2, l);
        localAtomicReferenceArray.set(j, localReferenceEntry2);
        i = this.count + 1;
        this.count = i;
        evictEntries();
        Object localObject1 = null;
        return localObject1;
      }
      finally
      {
        unlock();
        postWriteCleanup();
      }
    }
    
    void expand()
    {
      AtomicReferenceArray localAtomicReferenceArray1 = this.table;
      int i = localAtomicReferenceArray1.length();
      if (i >= 1073741824) {
        return;
      }
      int j = this.count;
      AtomicReferenceArray localAtomicReferenceArray2 = newEntryArray(i << 1);
      this.threshold = (localAtomicReferenceArray2.length() * 3 / 4);
      int k = localAtomicReferenceArray2.length() - 1;
      for (int m = 0; m < i; m++)
      {
        LocalCache.ReferenceEntry localReferenceEntry1 = (LocalCache.ReferenceEntry)localAtomicReferenceArray1.get(m);
        if (localReferenceEntry1 != null)
        {
          LocalCache.ReferenceEntry localReferenceEntry2 = localReferenceEntry1.getNext();
          int n = localReferenceEntry1.getHash() & k;
          if (localReferenceEntry2 == null)
          {
            localAtomicReferenceArray2.set(n, localReferenceEntry1);
          }
          else
          {
            Object localObject = localReferenceEntry1;
            int i1 = n;
            int i2;
            for (LocalCache.ReferenceEntry localReferenceEntry3 = localReferenceEntry2; localReferenceEntry3 != null; localReferenceEntry3 = localReferenceEntry3.getNext())
            {
              i2 = localReferenceEntry3.getHash() & k;
              if (i2 != i1)
              {
                i1 = i2;
                localObject = localReferenceEntry3;
              }
            }
            localAtomicReferenceArray2.set(i1, localObject);
            for (localReferenceEntry3 = localReferenceEntry1; localReferenceEntry3 != localObject; localReferenceEntry3 = localReferenceEntry3.getNext())
            {
              i2 = localReferenceEntry3.getHash() & k;
              LocalCache.ReferenceEntry localReferenceEntry4 = (LocalCache.ReferenceEntry)localAtomicReferenceArray2.get(i2);
              LocalCache.ReferenceEntry localReferenceEntry5 = copyEntry(localReferenceEntry3, localReferenceEntry4);
              if (localReferenceEntry5 != null)
              {
                localAtomicReferenceArray2.set(i2, localReferenceEntry5);
              }
              else
              {
                removeCollectedEntry(localReferenceEntry3);
                j--;
              }
            }
          }
        }
      }
      this.table = localAtomicReferenceArray2;
      this.count = j;
    }
    
    boolean replace(Object paramObject1, int paramInt, Object paramObject2, Object paramObject3)
    {
      lock();
      try
      {
        long l = this.map.ticker.read();
        preWriteCleanup(l);
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int i = paramInt & localAtomicReferenceArray.length() - 1;
        LocalCache.ReferenceEntry localReferenceEntry1 = (LocalCache.ReferenceEntry)localAtomicReferenceArray.get(i);
        for (LocalCache.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext())
        {
          Object localObject1 = localReferenceEntry2.getKey();
          if ((localReferenceEntry2.getHash() == paramInt) && (localObject1 != null) && (this.map.keyEquivalence.equivalent(paramObject1, localObject1)))
          {
            LocalCache.ValueReference localValueReference = localReferenceEntry2.getValueReference();
            Object localObject2 = localValueReference.get();
            if (localObject2 == null)
            {
              if (localValueReference.isActive())
              {
                j = this.count - 1;
                this.modCount += 1;
                LocalCache.ReferenceEntry localReferenceEntry3 = removeValueFromChain(localReferenceEntry1, localReferenceEntry2, localObject1, paramInt, localValueReference, RemovalCause.COLLECTED);
                j = this.count - 1;
                localAtomicReferenceArray.set(i, localReferenceEntry3);
                this.count = j;
              }
              int j = 0;
              return j;
            }
            if (this.map.valueEquivalence.equivalent(paramObject2, localObject2))
            {
              this.modCount += 1;
              enqueueNotification(paramObject1, paramInt, localValueReference, RemovalCause.REPLACED);
              setValue(localReferenceEntry2, paramObject1, paramObject3, l);
              evictEntries();
              bool2 = true;
              return bool2;
            }
            recordLockedRead(localReferenceEntry2, l);
            boolean bool2 = false;
            return bool2;
          }
        }
        boolean bool1 = false;
        return bool1;
      }
      finally
      {
        unlock();
        postWriteCleanup();
      }
    }
    
    Object replace(Object paramObject1, int paramInt, Object paramObject2)
    {
      lock();
      try
      {
        long l = this.map.ticker.read();
        preWriteCleanup(l);
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int i = paramInt & localAtomicReferenceArray.length() - 1;
        LocalCache.ReferenceEntry localReferenceEntry1 = (LocalCache.ReferenceEntry)localAtomicReferenceArray.get(i);
        for (LocalCache.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext())
        {
          Object localObject1 = localReferenceEntry2.getKey();
          if ((localReferenceEntry2.getHash() == paramInt) && (localObject1 != null) && (this.map.keyEquivalence.equivalent(paramObject1, localObject1)))
          {
            LocalCache.ValueReference localValueReference = localReferenceEntry2.getValueReference();
            Object localObject2 = localValueReference.get();
            if (localObject2 == null)
            {
              if (localValueReference.isActive())
              {
                int j = this.count - 1;
                this.modCount += 1;
                LocalCache.ReferenceEntry localReferenceEntry3 = removeValueFromChain(localReferenceEntry1, localReferenceEntry2, localObject1, paramInt, localValueReference, RemovalCause.COLLECTED);
                j = this.count - 1;
                localAtomicReferenceArray.set(i, localReferenceEntry3);
                this.count = j;
              }
              localObject3 = null;
              return localObject3;
            }
            this.modCount += 1;
            enqueueNotification(paramObject1, paramInt, localValueReference, RemovalCause.REPLACED);
            setValue(localReferenceEntry2, paramObject1, paramObject2, l);
            evictEntries();
            Object localObject3 = localObject2;
            return localObject3;
          }
        }
        localReferenceEntry2 = null;
        return localReferenceEntry2;
      }
      finally
      {
        unlock();
        postWriteCleanup();
      }
    }
    
    Object remove(Object paramObject, int paramInt)
    {
      lock();
      try
      {
        long l = this.map.ticker.read();
        preWriteCleanup(l);
        int i = this.count - 1;
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int j = paramInt & localAtomicReferenceArray.length() - 1;
        LocalCache.ReferenceEntry localReferenceEntry1 = (LocalCache.ReferenceEntry)localAtomicReferenceArray.get(j);
        for (LocalCache.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext())
        {
          Object localObject1 = localReferenceEntry2.getKey();
          if ((localReferenceEntry2.getHash() == paramInt) && (localObject1 != null) && (this.map.keyEquivalence.equivalent(paramObject, localObject1)))
          {
            LocalCache.ValueReference localValueReference = localReferenceEntry2.getValueReference();
            Object localObject2 = localValueReference.get();
            RemovalCause localRemovalCause;
            if (localObject2 != null)
            {
              localRemovalCause = RemovalCause.EXPLICIT;
            }
            else if (localValueReference.isActive())
            {
              localRemovalCause = RemovalCause.COLLECTED;
            }
            else
            {
              localObject3 = null;
              return localObject3;
            }
            this.modCount += 1;
            Object localObject3 = removeValueFromChain(localReferenceEntry1, localReferenceEntry2, localObject1, paramInt, localValueReference, localRemovalCause);
            i = this.count - 1;
            localAtomicReferenceArray.set(j, localObject3);
            this.count = i;
            Object localObject4 = localObject2;
            return localObject4;
          }
        }
        localReferenceEntry2 = null;
        return localReferenceEntry2;
      }
      finally
      {
        unlock();
        postWriteCleanup();
      }
    }
    
    boolean storeLoadedValue(Object paramObject1, int paramInt, LocalCache.LoadingValueReference paramLoadingValueReference, Object paramObject2)
    {
      lock();
      try
      {
        long l = this.map.ticker.read();
        preWriteCleanup(l);
        int i = this.count + 1;
        if (i > this.threshold)
        {
          expand();
          i = this.count + 1;
        }
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int j = paramInt & localAtomicReferenceArray.length() - 1;
        LocalCache.ReferenceEntry localReferenceEntry1 = (LocalCache.ReferenceEntry)localAtomicReferenceArray.get(j);
        for (LocalCache.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext())
        {
          Object localObject1 = localReferenceEntry2.getKey();
          if ((localReferenceEntry2.getHash() == paramInt) && (localObject1 != null) && (this.map.keyEquivalence.equivalent(paramObject1, localObject1)))
          {
            Object localObject2 = localReferenceEntry2.getValueReference();
            Object localObject3 = ((LocalCache.ValueReference)localObject2).get();
            if ((paramLoadingValueReference == localObject2) || ((localObject3 == null) && (localObject2 != LocalCache.UNSET)))
            {
              this.modCount += 1;
              if (paramLoadingValueReference.isActive())
              {
                RemovalCause localRemovalCause = localObject3 == null ? RemovalCause.COLLECTED : RemovalCause.REPLACED;
                enqueueNotification(paramObject1, paramInt, paramLoadingValueReference, localRemovalCause);
                i--;
              }
              setValue(localReferenceEntry2, paramObject1, paramObject2, l);
              this.count = i;
              evictEntries();
              bool2 = true;
              return bool2;
            }
            localObject2 = new LocalCache.WeightedStrongValueReference(paramObject2, 0);
            enqueueNotification(paramObject1, paramInt, (LocalCache.ValueReference)localObject2, RemovalCause.REPLACED);
            boolean bool2 = false;
            return bool2;
          }
        }
        this.modCount += 1;
        localReferenceEntry2 = newEntry(paramObject1, paramInt, localReferenceEntry1);
        setValue(localReferenceEntry2, paramObject1, paramObject2, l);
        localAtomicReferenceArray.set(j, localReferenceEntry2);
        this.count = i;
        evictEntries();
        boolean bool1 = true;
        return bool1;
      }
      finally
      {
        unlock();
        postWriteCleanup();
      }
    }
    
    boolean remove(Object paramObject1, int paramInt, Object paramObject2)
    {
      lock();
      try
      {
        long l = this.map.ticker.read();
        preWriteCleanup(l);
        int i = this.count - 1;
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int j = paramInt & localAtomicReferenceArray.length() - 1;
        LocalCache.ReferenceEntry localReferenceEntry1 = (LocalCache.ReferenceEntry)localAtomicReferenceArray.get(j);
        for (LocalCache.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext())
        {
          Object localObject1 = localReferenceEntry2.getKey();
          if ((localReferenceEntry2.getHash() == paramInt) && (localObject1 != null) && (this.map.keyEquivalence.equivalent(paramObject1, localObject1)))
          {
            LocalCache.ValueReference localValueReference = localReferenceEntry2.getValueReference();
            Object localObject2 = localValueReference.get();
            RemovalCause localRemovalCause;
            if (this.map.valueEquivalence.equivalent(paramObject2, localObject2))
            {
              localRemovalCause = RemovalCause.EXPLICIT;
            }
            else if ((localObject2 == null) && (localValueReference.isActive()))
            {
              localRemovalCause = RemovalCause.COLLECTED;
            }
            else
            {
              boolean bool2 = false;
              return bool2;
            }
            this.modCount += 1;
            LocalCache.ReferenceEntry localReferenceEntry3 = removeValueFromChain(localReferenceEntry1, localReferenceEntry2, localObject1, paramInt, localValueReference, localRemovalCause);
            i = this.count - 1;
            localAtomicReferenceArray.set(j, localReferenceEntry3);
            this.count = i;
            boolean bool3 = localRemovalCause == RemovalCause.EXPLICIT;
            return bool3;
          }
        }
        boolean bool1 = false;
        return bool1;
      }
      finally
      {
        unlock();
        postWriteCleanup();
      }
    }
    
    void clear()
    {
      if (this.count != 0)
      {
        lock();
        try
        {
          AtomicReferenceArray localAtomicReferenceArray = this.table;
          for (int i = 0; i < localAtomicReferenceArray.length(); i++) {
            for (LocalCache.ReferenceEntry localReferenceEntry = (LocalCache.ReferenceEntry)localAtomicReferenceArray.get(i); localReferenceEntry != null; localReferenceEntry = localReferenceEntry.getNext()) {
              if (localReferenceEntry.getValueReference().isActive()) {
                enqueueNotification(localReferenceEntry, RemovalCause.EXPLICIT);
              }
            }
          }
          for (i = 0; i < localAtomicReferenceArray.length(); i++) {
            localAtomicReferenceArray.set(i, null);
          }
          clearReferenceQueues();
          this.writeQueue.clear();
          this.accessQueue.clear();
          this.readCount.set(0);
          this.modCount += 1;
          this.count = 0;
        }
        finally
        {
          unlock();
          postWriteCleanup();
        }
      }
    }
    
    LocalCache.ReferenceEntry removeValueFromChain(LocalCache.ReferenceEntry paramReferenceEntry1, LocalCache.ReferenceEntry paramReferenceEntry2, Object paramObject, int paramInt, LocalCache.ValueReference paramValueReference, RemovalCause paramRemovalCause)
    {
      enqueueNotification(paramObject, paramInt, paramValueReference, paramRemovalCause);
      this.writeQueue.remove(paramReferenceEntry2);
      this.accessQueue.remove(paramReferenceEntry2);
      if (paramValueReference.isLoading())
      {
        paramValueReference.notifyNewValue(null);
        return paramReferenceEntry1;
      }
      return removeEntryFromChain(paramReferenceEntry1, paramReferenceEntry2);
    }
    
    LocalCache.ReferenceEntry removeEntryFromChain(LocalCache.ReferenceEntry paramReferenceEntry1, LocalCache.ReferenceEntry paramReferenceEntry2)
    {
      int i = this.count;
      Object localObject = paramReferenceEntry2.getNext();
      for (LocalCache.ReferenceEntry localReferenceEntry1 = paramReferenceEntry1; localReferenceEntry1 != paramReferenceEntry2; localReferenceEntry1 = localReferenceEntry1.getNext())
      {
        LocalCache.ReferenceEntry localReferenceEntry2 = copyEntry(localReferenceEntry1, (LocalCache.ReferenceEntry)localObject);
        if (localReferenceEntry2 != null)
        {
          localObject = localReferenceEntry2;
        }
        else
        {
          removeCollectedEntry(localReferenceEntry1);
          i--;
        }
      }
      this.count = i;
      return (LocalCache.ReferenceEntry)localObject;
    }
    
    void removeCollectedEntry(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      enqueueNotification(paramReferenceEntry, RemovalCause.COLLECTED);
      this.writeQueue.remove(paramReferenceEntry);
      this.accessQueue.remove(paramReferenceEntry);
    }
    
    boolean reclaimKey(LocalCache.ReferenceEntry paramReferenceEntry, int paramInt)
    {
      lock();
      try
      {
        int i = this.count - 1;
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int j = paramInt & localAtomicReferenceArray.length() - 1;
        LocalCache.ReferenceEntry localReferenceEntry1 = (LocalCache.ReferenceEntry)localAtomicReferenceArray.get(j);
        for (LocalCache.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext()) {
          if (localReferenceEntry2 == paramReferenceEntry)
          {
            this.modCount += 1;
            LocalCache.ReferenceEntry localReferenceEntry3 = removeValueFromChain(localReferenceEntry1, localReferenceEntry2, localReferenceEntry2.getKey(), paramInt, localReferenceEntry2.getValueReference(), RemovalCause.COLLECTED);
            i = this.count - 1;
            localAtomicReferenceArray.set(j, localReferenceEntry3);
            this.count = i;
            boolean bool2 = true;
            return bool2;
          }
        }
        boolean bool1 = false;
        return bool1;
      }
      finally
      {
        unlock();
        postWriteCleanup();
      }
    }
    
    boolean reclaimValue(Object paramObject, int paramInt, LocalCache.ValueReference paramValueReference)
    {
      lock();
      try
      {
        int i = this.count - 1;
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int j = paramInt & localAtomicReferenceArray.length() - 1;
        LocalCache.ReferenceEntry localReferenceEntry1 = (LocalCache.ReferenceEntry)localAtomicReferenceArray.get(j);
        for (LocalCache.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext())
        {
          Object localObject1 = localReferenceEntry2.getKey();
          if ((localReferenceEntry2.getHash() == paramInt) && (localObject1 != null) && (this.map.keyEquivalence.equivalent(paramObject, localObject1)))
          {
            LocalCache.ValueReference localValueReference = localReferenceEntry2.getValueReference();
            if (localValueReference == paramValueReference)
            {
              this.modCount += 1;
              LocalCache.ReferenceEntry localReferenceEntry3 = removeValueFromChain(localReferenceEntry1, localReferenceEntry2, localObject1, paramInt, paramValueReference, RemovalCause.COLLECTED);
              i = this.count - 1;
              localAtomicReferenceArray.set(j, localReferenceEntry3);
              this.count = i;
              boolean bool3 = true;
              return bool3;
            }
            boolean bool2 = false;
            return bool2;
          }
        }
        boolean bool1 = false;
        return bool1;
      }
      finally
      {
        unlock();
        if (!isHeldByCurrentThread()) {
          postWriteCleanup();
        }
      }
    }
    
    boolean removeLoadingValue(Object paramObject, int paramInt, LocalCache.LoadingValueReference paramLoadingValueReference)
    {
      lock();
      try
      {
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int i = paramInt & localAtomicReferenceArray.length() - 1;
        LocalCache.ReferenceEntry localReferenceEntry1 = (LocalCache.ReferenceEntry)localAtomicReferenceArray.get(i);
        for (LocalCache.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext())
        {
          Object localObject1 = localReferenceEntry2.getKey();
          if ((localReferenceEntry2.getHash() == paramInt) && (localObject1 != null) && (this.map.keyEquivalence.equivalent(paramObject, localObject1)))
          {
            LocalCache.ValueReference localValueReference = localReferenceEntry2.getValueReference();
            if (localValueReference == paramLoadingValueReference)
            {
              if (paramLoadingValueReference.isActive())
              {
                localReferenceEntry2.setValueReference(paramLoadingValueReference.getOldValue());
              }
              else
              {
                LocalCache.ReferenceEntry localReferenceEntry3 = removeEntryFromChain(localReferenceEntry1, localReferenceEntry2);
                localAtomicReferenceArray.set(i, localReferenceEntry3);
              }
              bool2 = true;
              return bool2;
            }
            boolean bool2 = false;
            return bool2;
          }
        }
        boolean bool1 = false;
        return bool1;
      }
      finally
      {
        unlock();
        postWriteCleanup();
      }
    }
    
    boolean removeEntry(LocalCache.ReferenceEntry paramReferenceEntry, int paramInt, RemovalCause paramRemovalCause)
    {
      int i = this.count - 1;
      AtomicReferenceArray localAtomicReferenceArray = this.table;
      int j = paramInt & localAtomicReferenceArray.length() - 1;
      LocalCache.ReferenceEntry localReferenceEntry1 = (LocalCache.ReferenceEntry)localAtomicReferenceArray.get(j);
      for (LocalCache.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext()) {
        if (localReferenceEntry2 == paramReferenceEntry)
        {
          this.modCount += 1;
          LocalCache.ReferenceEntry localReferenceEntry3 = removeValueFromChain(localReferenceEntry1, localReferenceEntry2, localReferenceEntry2.getKey(), paramInt, localReferenceEntry2.getValueReference(), paramRemovalCause);
          i = this.count - 1;
          localAtomicReferenceArray.set(j, localReferenceEntry3);
          this.count = i;
          return true;
        }
      }
      return false;
    }
    
    void postReadCleanup()
    {
      if ((this.readCount.incrementAndGet() & 0x3F) == 0) {
        cleanUp();
      }
    }
    
    void preWriteCleanup(long paramLong)
    {
      runLockedCleanup(paramLong);
    }
    
    void postWriteCleanup()
    {
      runUnlockedCleanup();
    }
    
    void cleanUp()
    {
      long l = this.map.ticker.read();
      runLockedCleanup(l);
      runUnlockedCleanup();
    }
    
    void runLockedCleanup(long paramLong)
    {
      if (tryLock()) {
        try
        {
          drainReferenceQueues();
          expireEntries(paramLong);
          this.readCount.set(0);
        }
        finally
        {
          unlock();
        }
      }
    }
    
    void runUnlockedCleanup()
    {
      if (!isHeldByCurrentThread()) {
        this.map.processPendingNotifications();
      }
    }
  }
  
  static final class WeightedStrongValueReference
    extends LocalCache.StrongValueReference
  {
    final int weight;
    
    WeightedStrongValueReference(Object paramObject, int paramInt)
    {
      super();
      this.weight = paramInt;
    }
    
    public int getWeight()
    {
      return this.weight;
    }
  }
  
  static final class WeightedSoftValueReference
    extends LocalCache.SoftValueReference
  {
    final int weight;
    
    WeightedSoftValueReference(ReferenceQueue paramReferenceQueue, Object paramObject, LocalCache.ReferenceEntry paramReferenceEntry, int paramInt)
    {
      super(paramObject, paramReferenceEntry);
      this.weight = paramInt;
    }
    
    public int getWeight()
    {
      return this.weight;
    }
    
    public LocalCache.ValueReference copyFor(ReferenceQueue paramReferenceQueue, Object paramObject, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      return new WeightedSoftValueReference(paramReferenceQueue, paramObject, paramReferenceEntry, this.weight);
    }
  }
  
  static final class WeightedWeakValueReference
    extends LocalCache.WeakValueReference
  {
    final int weight;
    
    WeightedWeakValueReference(ReferenceQueue paramReferenceQueue, Object paramObject, LocalCache.ReferenceEntry paramReferenceEntry, int paramInt)
    {
      super(paramObject, paramReferenceEntry);
      this.weight = paramInt;
    }
    
    public int getWeight()
    {
      return this.weight;
    }
    
    public LocalCache.ValueReference copyFor(ReferenceQueue paramReferenceQueue, Object paramObject, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      return new WeightedWeakValueReference(paramReferenceQueue, paramObject, paramReferenceEntry, this.weight);
    }
  }
  
  static class StrongValueReference
    implements LocalCache.ValueReference
  {
    final Object referent;
    
    StrongValueReference(Object paramObject)
    {
      this.referent = paramObject;
    }
    
    public Object get()
    {
      return this.referent;
    }
    
    public int getWeight()
    {
      return 1;
    }
    
    public LocalCache.ReferenceEntry getEntry()
    {
      return null;
    }
    
    public LocalCache.ValueReference copyFor(ReferenceQueue paramReferenceQueue, Object paramObject, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      return this;
    }
    
    public boolean isLoading()
    {
      return false;
    }
    
    public boolean isActive()
    {
      return true;
    }
    
    public Object waitForValue()
    {
      return get();
    }
    
    public void notifyNewValue(Object paramObject) {}
  }
  
  static class SoftValueReference
    extends SoftReference
    implements LocalCache.ValueReference
  {
    final LocalCache.ReferenceEntry entry;
    
    SoftValueReference(ReferenceQueue paramReferenceQueue, Object paramObject, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      super(paramReferenceQueue);
      this.entry = paramReferenceEntry;
    }
    
    public int getWeight()
    {
      return 1;
    }
    
    public LocalCache.ReferenceEntry getEntry()
    {
      return this.entry;
    }
    
    public void notifyNewValue(Object paramObject) {}
    
    public LocalCache.ValueReference copyFor(ReferenceQueue paramReferenceQueue, Object paramObject, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      return new SoftValueReference(paramReferenceQueue, paramObject, paramReferenceEntry);
    }
    
    public boolean isLoading()
    {
      return false;
    }
    
    public boolean isActive()
    {
      return true;
    }
    
    public Object waitForValue()
    {
      return get();
    }
  }
  
  static class WeakValueReference
    extends WeakReference
    implements LocalCache.ValueReference
  {
    final LocalCache.ReferenceEntry entry;
    
    WeakValueReference(ReferenceQueue paramReferenceQueue, Object paramObject, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      super(paramReferenceQueue);
      this.entry = paramReferenceEntry;
    }
    
    public int getWeight()
    {
      return 1;
    }
    
    public LocalCache.ReferenceEntry getEntry()
    {
      return this.entry;
    }
    
    public void notifyNewValue(Object paramObject) {}
    
    public LocalCache.ValueReference copyFor(ReferenceQueue paramReferenceQueue, Object paramObject, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      return new WeakValueReference(paramReferenceQueue, paramObject, paramReferenceEntry);
    }
    
    public boolean isLoading()
    {
      return false;
    }
    
    public boolean isActive()
    {
      return true;
    }
    
    public Object waitForValue()
    {
      return get();
    }
  }
  
  static final class WeakAccessWriteEntry
    extends LocalCache.WeakEntry
    implements LocalCache.ReferenceEntry
  {
    volatile long accessTime = Long.MAX_VALUE;
    LocalCache.ReferenceEntry nextAccess = LocalCache.nullEntry();
    LocalCache.ReferenceEntry previousAccess = LocalCache.nullEntry();
    volatile long writeTime = Long.MAX_VALUE;
    LocalCache.ReferenceEntry nextWrite = LocalCache.nullEntry();
    LocalCache.ReferenceEntry previousWrite = LocalCache.nullEntry();
    
    WeakAccessWriteEntry(ReferenceQueue paramReferenceQueue, Object paramObject, int paramInt, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      super(paramObject, paramInt, paramReferenceEntry);
    }
    
    public long getAccessTime()
    {
      return this.accessTime;
    }
    
    public void setAccessTime(long paramLong)
    {
      this.accessTime = paramLong;
    }
    
    public LocalCache.ReferenceEntry getNextInAccessQueue()
    {
      return this.nextAccess;
    }
    
    public void setNextInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.nextAccess = paramReferenceEntry;
    }
    
    public LocalCache.ReferenceEntry getPreviousInAccessQueue()
    {
      return this.previousAccess;
    }
    
    public void setPreviousInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.previousAccess = paramReferenceEntry;
    }
    
    public long getWriteTime()
    {
      return this.writeTime;
    }
    
    public void setWriteTime(long paramLong)
    {
      this.writeTime = paramLong;
    }
    
    public LocalCache.ReferenceEntry getNextInWriteQueue()
    {
      return this.nextWrite;
    }
    
    public void setNextInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.nextWrite = paramReferenceEntry;
    }
    
    public LocalCache.ReferenceEntry getPreviousInWriteQueue()
    {
      return this.previousWrite;
    }
    
    public void setPreviousInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.previousWrite = paramReferenceEntry;
    }
  }
  
  static final class WeakWriteEntry
    extends LocalCache.WeakEntry
    implements LocalCache.ReferenceEntry
  {
    volatile long writeTime = Long.MAX_VALUE;
    LocalCache.ReferenceEntry nextWrite = LocalCache.nullEntry();
    LocalCache.ReferenceEntry previousWrite = LocalCache.nullEntry();
    
    WeakWriteEntry(ReferenceQueue paramReferenceQueue, Object paramObject, int paramInt, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      super(paramObject, paramInt, paramReferenceEntry);
    }
    
    public long getWriteTime()
    {
      return this.writeTime;
    }
    
    public void setWriteTime(long paramLong)
    {
      this.writeTime = paramLong;
    }
    
    public LocalCache.ReferenceEntry getNextInWriteQueue()
    {
      return this.nextWrite;
    }
    
    public void setNextInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.nextWrite = paramReferenceEntry;
    }
    
    public LocalCache.ReferenceEntry getPreviousInWriteQueue()
    {
      return this.previousWrite;
    }
    
    public void setPreviousInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.previousWrite = paramReferenceEntry;
    }
  }
  
  static final class WeakAccessEntry
    extends LocalCache.WeakEntry
    implements LocalCache.ReferenceEntry
  {
    volatile long accessTime = Long.MAX_VALUE;
    LocalCache.ReferenceEntry nextAccess = LocalCache.nullEntry();
    LocalCache.ReferenceEntry previousAccess = LocalCache.nullEntry();
    
    WeakAccessEntry(ReferenceQueue paramReferenceQueue, Object paramObject, int paramInt, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      super(paramObject, paramInt, paramReferenceEntry);
    }
    
    public long getAccessTime()
    {
      return this.accessTime;
    }
    
    public void setAccessTime(long paramLong)
    {
      this.accessTime = paramLong;
    }
    
    public LocalCache.ReferenceEntry getNextInAccessQueue()
    {
      return this.nextAccess;
    }
    
    public void setNextInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.nextAccess = paramReferenceEntry;
    }
    
    public LocalCache.ReferenceEntry getPreviousInAccessQueue()
    {
      return this.previousAccess;
    }
    
    public void setPreviousInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.previousAccess = paramReferenceEntry;
    }
  }
  
  static class WeakEntry
    extends WeakReference
    implements LocalCache.ReferenceEntry
  {
    final int hash;
    final LocalCache.ReferenceEntry next;
    volatile LocalCache.ValueReference valueReference = LocalCache.unset();
    
    WeakEntry(ReferenceQueue paramReferenceQueue, Object paramObject, int paramInt, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      super(paramReferenceQueue);
      this.hash = paramInt;
      this.next = paramReferenceEntry;
    }
    
    public Object getKey()
    {
      return get();
    }
    
    public long getAccessTime()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setAccessTime(long paramLong)
    {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ReferenceEntry getNextInAccessQueue()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setNextInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ReferenceEntry getPreviousInAccessQueue()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public long getWriteTime()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setWriteTime(long paramLong)
    {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ReferenceEntry getNextInWriteQueue()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setNextInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ReferenceEntry getPreviousInWriteQueue()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ValueReference getValueReference()
    {
      return this.valueReference;
    }
    
    public void setValueReference(LocalCache.ValueReference paramValueReference)
    {
      this.valueReference = paramValueReference;
    }
    
    public int getHash()
    {
      return this.hash;
    }
    
    public LocalCache.ReferenceEntry getNext()
    {
      return this.next;
    }
  }
  
  static final class StrongAccessWriteEntry
    extends LocalCache.StrongEntry
    implements LocalCache.ReferenceEntry
  {
    volatile long accessTime = Long.MAX_VALUE;
    LocalCache.ReferenceEntry nextAccess = LocalCache.nullEntry();
    LocalCache.ReferenceEntry previousAccess = LocalCache.nullEntry();
    volatile long writeTime = Long.MAX_VALUE;
    LocalCache.ReferenceEntry nextWrite = LocalCache.nullEntry();
    LocalCache.ReferenceEntry previousWrite = LocalCache.nullEntry();
    
    StrongAccessWriteEntry(Object paramObject, int paramInt, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      super(paramInt, paramReferenceEntry);
    }
    
    public long getAccessTime()
    {
      return this.accessTime;
    }
    
    public void setAccessTime(long paramLong)
    {
      this.accessTime = paramLong;
    }
    
    public LocalCache.ReferenceEntry getNextInAccessQueue()
    {
      return this.nextAccess;
    }
    
    public void setNextInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.nextAccess = paramReferenceEntry;
    }
    
    public LocalCache.ReferenceEntry getPreviousInAccessQueue()
    {
      return this.previousAccess;
    }
    
    public void setPreviousInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.previousAccess = paramReferenceEntry;
    }
    
    public long getWriteTime()
    {
      return this.writeTime;
    }
    
    public void setWriteTime(long paramLong)
    {
      this.writeTime = paramLong;
    }
    
    public LocalCache.ReferenceEntry getNextInWriteQueue()
    {
      return this.nextWrite;
    }
    
    public void setNextInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.nextWrite = paramReferenceEntry;
    }
    
    public LocalCache.ReferenceEntry getPreviousInWriteQueue()
    {
      return this.previousWrite;
    }
    
    public void setPreviousInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.previousWrite = paramReferenceEntry;
    }
  }
  
  static final class StrongWriteEntry
    extends LocalCache.StrongEntry
    implements LocalCache.ReferenceEntry
  {
    volatile long writeTime = Long.MAX_VALUE;
    LocalCache.ReferenceEntry nextWrite = LocalCache.nullEntry();
    LocalCache.ReferenceEntry previousWrite = LocalCache.nullEntry();
    
    StrongWriteEntry(Object paramObject, int paramInt, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      super(paramInt, paramReferenceEntry);
    }
    
    public long getWriteTime()
    {
      return this.writeTime;
    }
    
    public void setWriteTime(long paramLong)
    {
      this.writeTime = paramLong;
    }
    
    public LocalCache.ReferenceEntry getNextInWriteQueue()
    {
      return this.nextWrite;
    }
    
    public void setNextInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.nextWrite = paramReferenceEntry;
    }
    
    public LocalCache.ReferenceEntry getPreviousInWriteQueue()
    {
      return this.previousWrite;
    }
    
    public void setPreviousInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.previousWrite = paramReferenceEntry;
    }
  }
  
  static final class StrongAccessEntry
    extends LocalCache.StrongEntry
    implements LocalCache.ReferenceEntry
  {
    volatile long accessTime = Long.MAX_VALUE;
    LocalCache.ReferenceEntry nextAccess = LocalCache.nullEntry();
    LocalCache.ReferenceEntry previousAccess = LocalCache.nullEntry();
    
    StrongAccessEntry(Object paramObject, int paramInt, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      super(paramInt, paramReferenceEntry);
    }
    
    public long getAccessTime()
    {
      return this.accessTime;
    }
    
    public void setAccessTime(long paramLong)
    {
      this.accessTime = paramLong;
    }
    
    public LocalCache.ReferenceEntry getNextInAccessQueue()
    {
      return this.nextAccess;
    }
    
    public void setNextInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.nextAccess = paramReferenceEntry;
    }
    
    public LocalCache.ReferenceEntry getPreviousInAccessQueue()
    {
      return this.previousAccess;
    }
    
    public void setPreviousInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.previousAccess = paramReferenceEntry;
    }
  }
  
  static class StrongEntry
    implements LocalCache.ReferenceEntry
  {
    final Object key;
    final int hash;
    final LocalCache.ReferenceEntry next;
    volatile LocalCache.ValueReference valueReference = LocalCache.unset();
    
    StrongEntry(Object paramObject, int paramInt, LocalCache.ReferenceEntry paramReferenceEntry)
    {
      this.key = paramObject;
      this.hash = paramInt;
      this.next = paramReferenceEntry;
    }
    
    public Object getKey()
    {
      return this.key;
    }
    
    public long getAccessTime()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setAccessTime(long paramLong)
    {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ReferenceEntry getNextInAccessQueue()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setNextInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ReferenceEntry getPreviousInAccessQueue()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public long getWriteTime()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setWriteTime(long paramLong)
    {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ReferenceEntry getNextInWriteQueue()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setNextInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ReferenceEntry getPreviousInWriteQueue()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ValueReference getValueReference()
    {
      return this.valueReference;
    }
    
    public void setValueReference(LocalCache.ValueReference paramValueReference)
    {
      this.valueReference = paramValueReference;
    }
    
    public int getHash()
    {
      return this.hash;
    }
    
    public LocalCache.ReferenceEntry getNext()
    {
      return this.next;
    }
  }
  
  static abstract class AbstractReferenceEntry
    implements LocalCache.ReferenceEntry
  {
    public LocalCache.ValueReference getValueReference()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setValueReference(LocalCache.ValueReference paramValueReference)
    {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ReferenceEntry getNext()
    {
      throw new UnsupportedOperationException();
    }
    
    public int getHash()
    {
      throw new UnsupportedOperationException();
    }
    
    public Object getKey()
    {
      throw new UnsupportedOperationException();
    }
    
    public long getAccessTime()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setAccessTime(long paramLong)
    {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ReferenceEntry getNextInAccessQueue()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setNextInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ReferenceEntry getPreviousInAccessQueue()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public long getWriteTime()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setWriteTime(long paramLong)
    {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ReferenceEntry getNextInWriteQueue()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setNextInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ReferenceEntry getPreviousInWriteQueue()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private static enum NullEntry
    implements LocalCache.ReferenceEntry
  {
    INSTANCE;
    
    public LocalCache.ValueReference getValueReference()
    {
      return null;
    }
    
    public void setValueReference(LocalCache.ValueReference paramValueReference) {}
    
    public LocalCache.ReferenceEntry getNext()
    {
      return null;
    }
    
    public int getHash()
    {
      return 0;
    }
    
    public Object getKey()
    {
      return null;
    }
    
    public long getAccessTime()
    {
      return 0L;
    }
    
    public void setAccessTime(long paramLong) {}
    
    public LocalCache.ReferenceEntry getNextInAccessQueue()
    {
      return this;
    }
    
    public void setNextInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry) {}
    
    public LocalCache.ReferenceEntry getPreviousInAccessQueue()
    {
      return this;
    }
    
    public void setPreviousInAccessQueue(LocalCache.ReferenceEntry paramReferenceEntry) {}
    
    public long getWriteTime()
    {
      return 0L;
    }
    
    public void setWriteTime(long paramLong) {}
    
    public LocalCache.ReferenceEntry getNextInWriteQueue()
    {
      return this;
    }
    
    public void setNextInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry) {}
    
    public LocalCache.ReferenceEntry getPreviousInWriteQueue()
    {
      return this;
    }
    
    public void setPreviousInWriteQueue(LocalCache.ReferenceEntry paramReferenceEntry) {}
  }
  
  static abstract interface ReferenceEntry
  {
    public abstract LocalCache.ValueReference getValueReference();
    
    public abstract void setValueReference(LocalCache.ValueReference paramValueReference);
    
    public abstract ReferenceEntry getNext();
    
    public abstract int getHash();
    
    public abstract Object getKey();
    
    public abstract long getAccessTime();
    
    public abstract void setAccessTime(long paramLong);
    
    public abstract ReferenceEntry getNextInAccessQueue();
    
    public abstract void setNextInAccessQueue(ReferenceEntry paramReferenceEntry);
    
    public abstract ReferenceEntry getPreviousInAccessQueue();
    
    public abstract void setPreviousInAccessQueue(ReferenceEntry paramReferenceEntry);
    
    public abstract long getWriteTime();
    
    public abstract void setWriteTime(long paramLong);
    
    public abstract ReferenceEntry getNextInWriteQueue();
    
    public abstract void setNextInWriteQueue(ReferenceEntry paramReferenceEntry);
    
    public abstract ReferenceEntry getPreviousInWriteQueue();
    
    public abstract void setPreviousInWriteQueue(ReferenceEntry paramReferenceEntry);
  }
  
  static abstract interface ValueReference
  {
    public abstract Object get();
    
    public abstract Object waitForValue()
      throws ExecutionException;
    
    public abstract int getWeight();
    
    public abstract LocalCache.ReferenceEntry getEntry();
    
    public abstract ValueReference copyFor(ReferenceQueue paramReferenceQueue, Object paramObject, LocalCache.ReferenceEntry paramReferenceEntry);
    
    public abstract void notifyNewValue(Object paramObject);
    
    public abstract boolean isLoading();
    
    public abstract boolean isActive();
  }
  
  static abstract enum EntryFactory
  {
    STRONG,  STRONG_ACCESS,  STRONG_WRITE,  STRONG_ACCESS_WRITE,  WEAK,  WEAK_ACCESS,  WEAK_WRITE,  WEAK_ACCESS_WRITE;
    
    static final int ACCESS_MASK = 1;
    static final int WRITE_MASK = 2;
    static final int WEAK_MASK = 4;
    static final EntryFactory[] factories = { STRONG, STRONG_ACCESS, STRONG_WRITE, STRONG_ACCESS_WRITE, WEAK, WEAK_ACCESS, WEAK_WRITE, WEAK_ACCESS_WRITE };
    
    static EntryFactory getFactory(LocalCache.Strength paramStrength, boolean paramBoolean1, boolean paramBoolean2)
    {
      int i = (paramStrength == LocalCache.Strength.WEAK ? 4 : 0) | (paramBoolean1 ? 1 : 0) | (paramBoolean2 ? 2 : 0);
      return factories[i];
    }
    
    abstract LocalCache.ReferenceEntry newEntry(LocalCache.Segment paramSegment, Object paramObject, int paramInt, LocalCache.ReferenceEntry paramReferenceEntry);
    
    LocalCache.ReferenceEntry copyEntry(LocalCache.Segment paramSegment, LocalCache.ReferenceEntry paramReferenceEntry1, LocalCache.ReferenceEntry paramReferenceEntry2)
    {
      return newEntry(paramSegment, paramReferenceEntry1.getKey(), paramReferenceEntry1.getHash(), paramReferenceEntry2);
    }
    
    void copyAccessEntry(LocalCache.ReferenceEntry paramReferenceEntry1, LocalCache.ReferenceEntry paramReferenceEntry2)
    {
      paramReferenceEntry2.setAccessTime(paramReferenceEntry1.getAccessTime());
      LocalCache.connectAccessOrder(paramReferenceEntry1.getPreviousInAccessQueue(), paramReferenceEntry2);
      LocalCache.connectAccessOrder(paramReferenceEntry2, paramReferenceEntry1.getNextInAccessQueue());
      LocalCache.nullifyAccessOrder(paramReferenceEntry1);
    }
    
    void copyWriteEntry(LocalCache.ReferenceEntry paramReferenceEntry1, LocalCache.ReferenceEntry paramReferenceEntry2)
    {
      paramReferenceEntry2.setWriteTime(paramReferenceEntry1.getWriteTime());
      LocalCache.connectWriteOrder(paramReferenceEntry1.getPreviousInWriteQueue(), paramReferenceEntry2);
      LocalCache.connectWriteOrder(paramReferenceEntry2, paramReferenceEntry1.getNextInWriteQueue());
      LocalCache.nullifyWriteOrder(paramReferenceEntry1);
    }
  }
  
  static abstract enum Strength
  {
    STRONG,  SOFT,  WEAK;
    
    abstract LocalCache.ValueReference referenceValue(LocalCache.Segment paramSegment, LocalCache.ReferenceEntry paramReferenceEntry, Object paramObject, int paramInt);
    
    abstract Equivalence defaultEquivalence();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\cache\LocalCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */