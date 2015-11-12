package com.google.common.collect;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

class MapMakerInternalMap
  extends AbstractMap
  implements Serializable, ConcurrentMap
{
  static final int MAXIMUM_CAPACITY = 1073741824;
  static final int MAX_SEGMENTS = 65536;
  static final int CONTAINS_VALUE_RETRIES = 3;
  static final int DRAIN_THRESHOLD = 63;
  static final int DRAIN_MAX = 16;
  static final long CLEANUP_EXECUTOR_DELAY_SECS = 60L;
  private static final Logger logger = Logger.getLogger(MapMakerInternalMap.class.getName());
  final transient int segmentMask;
  final transient int segmentShift;
  final transient Segment[] segments;
  final int concurrencyLevel;
  final Equivalence keyEquivalence;
  final Equivalence valueEquivalence;
  final Strength keyStrength;
  final Strength valueStrength;
  final int maximumSize;
  final long expireAfterAccessNanos;
  final long expireAfterWriteNanos;
  final Queue removalNotificationQueue;
  final MapMaker.RemovalListener removalListener;
  final transient EntryFactory entryFactory;
  final Ticker ticker;
  static final ValueReference UNSET = new ValueReference()
  {
    public Object get()
    {
      return null;
    }
    
    public MapMakerInternalMap.ReferenceEntry getEntry()
    {
      return null;
    }
    
    public MapMakerInternalMap.ValueReference copyFor(ReferenceQueue paramAnonymousReferenceQueue, Object paramAnonymousObject, MapMakerInternalMap.ReferenceEntry paramAnonymousReferenceEntry)
    {
      return this;
    }
    
    public boolean isComputingReference()
    {
      return false;
    }
    
    public Object waitForValue()
    {
      return null;
    }
    
    public void clear(MapMakerInternalMap.ValueReference paramAnonymousValueReference) {}
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
  transient Set keySet;
  transient Collection values;
  transient Set entrySet;
  private static final long serialVersionUID = 5L;
  
  MapMakerInternalMap(MapMaker paramMapMaker)
  {
    this.concurrencyLevel = Math.min(paramMapMaker.getConcurrencyLevel(), 65536);
    this.keyStrength = paramMapMaker.getKeyStrength();
    this.valueStrength = paramMapMaker.getValueStrength();
    this.keyEquivalence = paramMapMaker.getKeyEquivalence();
    this.valueEquivalence = this.valueStrength.defaultEquivalence();
    this.maximumSize = paramMapMaker.maximumSize;
    this.expireAfterAccessNanos = paramMapMaker.getExpireAfterAccessNanos();
    this.expireAfterWriteNanos = paramMapMaker.getExpireAfterWriteNanos();
    this.entryFactory = EntryFactory.getFactory(this.keyStrength, expires(), evictsBySize());
    this.ticker = paramMapMaker.getTicker();
    this.removalListener = paramMapMaker.getRemovalListener();
    this.removalNotificationQueue = (this.removalListener == GenericMapMaker.NullListener.INSTANCE ? discardingQueue() : new ConcurrentLinkedQueue());
    int i = Math.min(paramMapMaker.getInitialCapacity(), 1073741824);
    if (evictsBySize()) {
      i = Math.min(i, this.maximumSize);
    }
    int j = 0;
    int k = 1;
    while ((k < this.concurrencyLevel) && ((!evictsBySize()) || (k * 2 <= this.maximumSize)))
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
    int i1;
    if (evictsBySize())
    {
      i1 = this.maximumSize / k + 1;
      int i2 = this.maximumSize % k;
      for (int i3 = 0; i3 < this.segments.length; i3++)
      {
        if (i3 == i2) {
          i1--;
        }
        this.segments[i3] = createSegment(n, i1);
      }
    }
    else
    {
      for (i1 = 0; i1 < this.segments.length; i1++) {
        this.segments[i1] = createSegment(n, -1);
      }
    }
  }
  
  boolean evictsBySize()
  {
    return this.maximumSize != -1;
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
  ValueReference newValueReference(ReferenceEntry paramReferenceEntry, Object paramObject)
  {
    int i = paramReferenceEntry.getHash();
    return this.valueStrength.referenceValue(segmentFor(i), paramReferenceEntry, paramObject);
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
  boolean isLive(ReferenceEntry paramReferenceEntry)
  {
    return segmentFor(paramReferenceEntry.getHash()).getLiveValue(paramReferenceEntry) != null;
  }
  
  Segment segmentFor(int paramInt)
  {
    return this.segments[(paramInt >>> this.segmentShift & this.segmentMask)];
  }
  
  Segment createSegment(int paramInt1, int paramInt2)
  {
    return new Segment(this, paramInt1, paramInt2);
  }
  
  Object getLiveValue(ReferenceEntry paramReferenceEntry)
  {
    if (paramReferenceEntry.getKey() == null) {
      return null;
    }
    Object localObject = paramReferenceEntry.getValueReference().get();
    if (localObject == null) {
      return null;
    }
    if ((expires()) && (isExpired(paramReferenceEntry))) {
      return null;
    }
    return localObject;
  }
  
  boolean isExpired(ReferenceEntry paramReferenceEntry)
  {
    return isExpired(paramReferenceEntry, this.ticker.read());
  }
  
  boolean isExpired(ReferenceEntry paramReferenceEntry, long paramLong)
  {
    return paramLong - paramReferenceEntry.getExpirationTime() > 0L;
  }
  
  static void connectExpirables(ReferenceEntry paramReferenceEntry1, ReferenceEntry paramReferenceEntry2)
  {
    paramReferenceEntry1.setNextExpirable(paramReferenceEntry2);
    paramReferenceEntry2.setPreviousExpirable(paramReferenceEntry1);
  }
  
  static void nullifyExpirable(ReferenceEntry paramReferenceEntry)
  {
    ReferenceEntry localReferenceEntry = nullEntry();
    paramReferenceEntry.setNextExpirable(localReferenceEntry);
    paramReferenceEntry.setPreviousExpirable(localReferenceEntry);
  }
  
  void processPendingNotifications()
  {
    MapMaker.RemovalNotification localRemovalNotification;
    while ((localRemovalNotification = (MapMaker.RemovalNotification)this.removalNotificationQueue.poll()) != null) {
      try
      {
        this.removalListener.onRemoval(localRemovalNotification);
      }
      catch (Exception localException)
      {
        logger.log(Level.WARNING, "Exception thrown by removal listener", localException);
      }
    }
  }
  
  static void connectEvictables(ReferenceEntry paramReferenceEntry1, ReferenceEntry paramReferenceEntry2)
  {
    paramReferenceEntry1.setNextEvictable(paramReferenceEntry2);
    paramReferenceEntry2.setPreviousEvictable(paramReferenceEntry1);
  }
  
  static void nullifyEvictable(ReferenceEntry paramReferenceEntry)
  {
    ReferenceEntry localReferenceEntry = nullEntry();
    paramReferenceEntry.setNextEvictable(localReferenceEntry);
    paramReferenceEntry.setPreviousEvictable(localReferenceEntry);
  }
  
  final Segment[] newSegmentArray(int paramInt)
  {
    return new Segment[paramInt];
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
  
  public int size()
  {
    Segment[] arrayOfSegment = this.segments;
    long l = 0L;
    for (int i = 0; i < arrayOfSegment.length; i++) {
      l += arrayOfSegment[i].count;
    }
    return Ints.saturatedCast(l);
  }
  
  public Object get(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    int i = hash(paramObject);
    return segmentFor(i).get(paramObject, i);
  }
  
  ReferenceEntry getEntry(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    int i = hash(paramObject);
    return segmentFor(i).getEntry(paramObject, i);
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
    Segment[] arrayOfSegment1 = this.segments;
    long l1 = -1L;
    for (int i = 0; i < 3; i++)
    {
      long l2 = 0L;
      for (Segment localSegment : arrayOfSegment1)
      {
        int m = localSegment.count;
        AtomicReferenceArray localAtomicReferenceArray = localSegment.table;
        for (int n = 0; n < localAtomicReferenceArray.length(); n++) {
          for (ReferenceEntry localReferenceEntry = (ReferenceEntry)localAtomicReferenceArray.get(n); localReferenceEntry != null; localReferenceEntry = localReferenceEntry.getNext())
          {
            Object localObject = localSegment.getLiveValue(localReferenceEntry);
            if ((localObject != null) && (this.valueEquivalence.equivalent(paramObject, localObject))) {
              return true;
            }
          }
        }
        l2 += localSegment.modCount;
      }
      if (l2 == l1) {
        break;
      }
      l1 = l2;
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
  
  public Set keySet()
  {
    Set localSet = this.keySet;
    return localSet != null ? localSet : (this.keySet = new KeySet());
  }
  
  public Collection values()
  {
    Collection localCollection = this.values;
    return localCollection != null ? localCollection : (this.values = new Values());
  }
  
  public Set entrySet()
  {
    Set localSet = this.entrySet;
    return localSet != null ? localSet : (this.entrySet = new EntrySet());
  }
  
  Object writeReplace()
  {
    return new SerializationProxy(this.keyStrength, this.valueStrength, this.keyEquivalence, this.valueEquivalence, this.expireAfterWriteNanos, this.expireAfterAccessNanos, this.maximumSize, this.concurrencyLevel, this.removalListener, this);
  }
  
  private static final class SerializationProxy
    extends MapMakerInternalMap.AbstractSerializationProxy
  {
    private static final long serialVersionUID = 3L;
    
    SerializationProxy(MapMakerInternalMap.Strength paramStrength1, MapMakerInternalMap.Strength paramStrength2, Equivalence paramEquivalence1, Equivalence paramEquivalence2, long paramLong1, long paramLong2, int paramInt1, int paramInt2, MapMaker.RemovalListener paramRemovalListener, ConcurrentMap paramConcurrentMap)
    {
      super(paramStrength2, paramEquivalence1, paramEquivalence2, paramLong1, paramLong2, paramInt1, paramInt2, paramRemovalListener, paramConcurrentMap);
    }
    
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
      writeMapTo(paramObjectOutputStream);
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      MapMaker localMapMaker = readMapMaker(paramObjectInputStream);
      this.delegate = localMapMaker.makeMap();
      readEntries(paramObjectInputStream);
    }
    
    private Object readResolve()
    {
      return this.delegate;
    }
  }
  
  static abstract class AbstractSerializationProxy
    extends ForwardingConcurrentMap
    implements Serializable
  {
    private static final long serialVersionUID = 3L;
    final MapMakerInternalMap.Strength keyStrength;
    final MapMakerInternalMap.Strength valueStrength;
    final Equivalence keyEquivalence;
    final Equivalence valueEquivalence;
    final long expireAfterWriteNanos;
    final long expireAfterAccessNanos;
    final int maximumSize;
    final int concurrencyLevel;
    final MapMaker.RemovalListener removalListener;
    transient ConcurrentMap delegate;
    
    AbstractSerializationProxy(MapMakerInternalMap.Strength paramStrength1, MapMakerInternalMap.Strength paramStrength2, Equivalence paramEquivalence1, Equivalence paramEquivalence2, long paramLong1, long paramLong2, int paramInt1, int paramInt2, MapMaker.RemovalListener paramRemovalListener, ConcurrentMap paramConcurrentMap)
    {
      this.keyStrength = paramStrength1;
      this.valueStrength = paramStrength2;
      this.keyEquivalence = paramEquivalence1;
      this.valueEquivalence = paramEquivalence2;
      this.expireAfterWriteNanos = paramLong1;
      this.expireAfterAccessNanos = paramLong2;
      this.maximumSize = paramInt1;
      this.concurrencyLevel = paramInt2;
      this.removalListener = paramRemovalListener;
      this.delegate = paramConcurrentMap;
    }
    
    protected ConcurrentMap delegate()
    {
      return this.delegate;
    }
    
    void writeMapTo(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.writeInt(this.delegate.size());
      Iterator localIterator = this.delegate.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        paramObjectOutputStream.writeObject(localEntry.getKey());
        paramObjectOutputStream.writeObject(localEntry.getValue());
      }
      paramObjectOutputStream.writeObject(null);
    }
    
    MapMaker readMapMaker(ObjectInputStream paramObjectInputStream)
      throws IOException
    {
      int i = paramObjectInputStream.readInt();
      MapMaker localMapMaker = new MapMaker().initialCapacity(i).setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence(this.keyEquivalence).concurrencyLevel(this.concurrencyLevel);
      localMapMaker.removalListener(this.removalListener);
      if (this.expireAfterWriteNanos > 0L) {
        localMapMaker.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
      }
      if (this.expireAfterAccessNanos > 0L) {
        localMapMaker.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
      }
      if (this.maximumSize != -1) {
        localMapMaker.maximumSize(this.maximumSize);
      }
      return localMapMaker;
    }
    
    void readEntries(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      for (;;)
      {
        Object localObject1 = paramObjectInputStream.readObject();
        if (localObject1 == null) {
          break;
        }
        Object localObject2 = paramObjectInputStream.readObject();
        this.delegate.put(localObject1, localObject2);
      }
    }
  }
  
  final class EntrySet
    extends AbstractSet
  {
    EntrySet() {}
    
    public Iterator iterator()
    {
      return new MapMakerInternalMap.EntryIterator(MapMakerInternalMap.this);
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
      Object localObject2 = MapMakerInternalMap.this.get(localObject1);
      return (localObject2 != null) && (MapMakerInternalMap.this.valueEquivalence.equivalent(localEntry.getValue(), localObject2));
    }
    
    public boolean remove(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      Object localObject = localEntry.getKey();
      return (localObject != null) && (MapMakerInternalMap.this.remove(localObject, localEntry.getValue()));
    }
    
    public int size()
    {
      return MapMakerInternalMap.this.size();
    }
    
    public boolean isEmpty()
    {
      return MapMakerInternalMap.this.isEmpty();
    }
    
    public void clear()
    {
      MapMakerInternalMap.this.clear();
    }
  }
  
  final class Values
    extends AbstractCollection
  {
    Values() {}
    
    public Iterator iterator()
    {
      return new MapMakerInternalMap.ValueIterator(MapMakerInternalMap.this);
    }
    
    public int size()
    {
      return MapMakerInternalMap.this.size();
    }
    
    public boolean isEmpty()
    {
      return MapMakerInternalMap.this.isEmpty();
    }
    
    public boolean contains(Object paramObject)
    {
      return MapMakerInternalMap.this.containsValue(paramObject);
    }
    
    public void clear()
    {
      MapMakerInternalMap.this.clear();
    }
  }
  
  final class KeySet
    extends AbstractSet
  {
    KeySet() {}
    
    public Iterator iterator()
    {
      return new MapMakerInternalMap.KeyIterator(MapMakerInternalMap.this);
    }
    
    public int size()
    {
      return MapMakerInternalMap.this.size();
    }
    
    public boolean isEmpty()
    {
      return MapMakerInternalMap.this.isEmpty();
    }
    
    public boolean contains(Object paramObject)
    {
      return MapMakerInternalMap.this.containsKey(paramObject);
    }
    
    public boolean remove(Object paramObject)
    {
      return MapMakerInternalMap.this.remove(paramObject) != null;
    }
    
    public void clear()
    {
      MapMakerInternalMap.this.clear();
    }
  }
  
  final class EntryIterator
    extends MapMakerInternalMap.HashIterator
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
    extends AbstractMapEntry
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
      Object localObject = MapMakerInternalMap.this.put(this.key, paramObject);
      this.value = paramObject;
      return localObject;
    }
  }
  
  final class ValueIterator
    extends MapMakerInternalMap.HashIterator
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
    extends MapMakerInternalMap.HashIterator
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
    int nextSegmentIndex = MapMakerInternalMap.this.segments.length - 1;
    int nextTableIndex = -1;
    MapMakerInternalMap.Segment currentSegment;
    AtomicReferenceArray currentTable;
    MapMakerInternalMap.ReferenceEntry nextEntry;
    MapMakerInternalMap.WriteThroughEntry nextExternal;
    MapMakerInternalMap.WriteThroughEntry lastReturned;
    
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
        this.currentSegment = MapMakerInternalMap.this.segments[(this.nextSegmentIndex--)];
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
        if (((this.nextEntry = (MapMakerInternalMap.ReferenceEntry)this.currentTable.get(this.nextTableIndex--)) != null) && ((advanceTo(this.nextEntry)) || (nextInChain()))) {
          return true;
        }
      }
      return false;
    }
    
    boolean advanceTo(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      try
      {
        Object localObject1 = paramReferenceEntry.getKey();
        Object localObject2 = MapMakerInternalMap.this.getLiveValue(paramReferenceEntry);
        if (localObject2 != null)
        {
          this.nextExternal = new MapMakerInternalMap.WriteThroughEntry(MapMakerInternalMap.this, localObject1, localObject2);
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
    
    MapMakerInternalMap.WriteThroughEntry nextEntry()
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
      MapMakerInternalMap.this.remove(this.lastReturned.getKey());
      this.lastReturned = null;
    }
  }
  
  static final class CleanupMapTask
    implements Runnable
  {
    final WeakReference mapReference;
    
    public CleanupMapTask(MapMakerInternalMap paramMapMakerInternalMap)
    {
      this.mapReference = new WeakReference(paramMapMakerInternalMap);
    }
    
    public void run()
    {
      MapMakerInternalMap localMapMakerInternalMap = (MapMakerInternalMap)this.mapReference.get();
      if (localMapMakerInternalMap == null) {
        throw new CancellationException();
      }
      for (MapMakerInternalMap.Segment localSegment : localMapMakerInternalMap.segments) {
        localSegment.runCleanup();
      }
    }
  }
  
  static final class ExpirationQueue
    extends AbstractQueue
  {
    final MapMakerInternalMap.ReferenceEntry head = new MapMakerInternalMap.AbstractReferenceEntry()
    {
      MapMakerInternalMap.ReferenceEntry nextExpirable = this;
      MapMakerInternalMap.ReferenceEntry previousExpirable = this;
      
      public long getExpirationTime()
      {
        return Long.MAX_VALUE;
      }
      
      public void setExpirationTime(long paramAnonymousLong) {}
      
      public MapMakerInternalMap.ReferenceEntry getNextExpirable()
      {
        return this.nextExpirable;
      }
      
      public void setNextExpirable(MapMakerInternalMap.ReferenceEntry paramAnonymousReferenceEntry)
      {
        this.nextExpirable = paramAnonymousReferenceEntry;
      }
      
      public MapMakerInternalMap.ReferenceEntry getPreviousExpirable()
      {
        return this.previousExpirable;
      }
      
      public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry paramAnonymousReferenceEntry)
      {
        this.previousExpirable = paramAnonymousReferenceEntry;
      }
    };
    
    public boolean offer(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      MapMakerInternalMap.connectExpirables(paramReferenceEntry.getPreviousExpirable(), paramReferenceEntry.getNextExpirable());
      MapMakerInternalMap.connectExpirables(this.head.getPreviousExpirable(), paramReferenceEntry);
      MapMakerInternalMap.connectExpirables(paramReferenceEntry, this.head);
      return true;
    }
    
    public MapMakerInternalMap.ReferenceEntry peek()
    {
      MapMakerInternalMap.ReferenceEntry localReferenceEntry = this.head.getNextExpirable();
      return localReferenceEntry == this.head ? null : localReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry poll()
    {
      MapMakerInternalMap.ReferenceEntry localReferenceEntry = this.head.getNextExpirable();
      if (localReferenceEntry == this.head) {
        return null;
      }
      remove(localReferenceEntry);
      return localReferenceEntry;
    }
    
    public boolean remove(Object paramObject)
    {
      MapMakerInternalMap.ReferenceEntry localReferenceEntry1 = (MapMakerInternalMap.ReferenceEntry)paramObject;
      MapMakerInternalMap.ReferenceEntry localReferenceEntry2 = localReferenceEntry1.getPreviousExpirable();
      MapMakerInternalMap.ReferenceEntry localReferenceEntry3 = localReferenceEntry1.getNextExpirable();
      MapMakerInternalMap.connectExpirables(localReferenceEntry2, localReferenceEntry3);
      MapMakerInternalMap.nullifyExpirable(localReferenceEntry1);
      return localReferenceEntry3 != MapMakerInternalMap.NullEntry.INSTANCE;
    }
    
    public boolean contains(Object paramObject)
    {
      MapMakerInternalMap.ReferenceEntry localReferenceEntry = (MapMakerInternalMap.ReferenceEntry)paramObject;
      return localReferenceEntry.getNextExpirable() != MapMakerInternalMap.NullEntry.INSTANCE;
    }
    
    public boolean isEmpty()
    {
      return this.head.getNextExpirable() == this.head;
    }
    
    public int size()
    {
      int i = 0;
      for (MapMakerInternalMap.ReferenceEntry localReferenceEntry = this.head.getNextExpirable(); localReferenceEntry != this.head; localReferenceEntry = localReferenceEntry.getNextExpirable()) {
        i++;
      }
      return i;
    }
    
    public void clear()
    {
      MapMakerInternalMap.ReferenceEntry localReferenceEntry;
      for (Object localObject = this.head.getNextExpirable(); localObject != this.head; localObject = localReferenceEntry)
      {
        localReferenceEntry = ((MapMakerInternalMap.ReferenceEntry)localObject).getNextExpirable();
        MapMakerInternalMap.nullifyExpirable((MapMakerInternalMap.ReferenceEntry)localObject);
      }
      this.head.setNextExpirable(this.head);
      this.head.setPreviousExpirable(this.head);
    }
    
    public Iterator iterator()
    {
      new AbstractSequentialIterator(peek())
      {
        protected MapMakerInternalMap.ReferenceEntry computeNext(MapMakerInternalMap.ReferenceEntry paramAnonymousReferenceEntry)
        {
          MapMakerInternalMap.ReferenceEntry localReferenceEntry = paramAnonymousReferenceEntry.getNextExpirable();
          return localReferenceEntry == MapMakerInternalMap.ExpirationQueue.this.head ? null : localReferenceEntry;
        }
      };
    }
  }
  
  static final class EvictionQueue
    extends AbstractQueue
  {
    final MapMakerInternalMap.ReferenceEntry head = new MapMakerInternalMap.AbstractReferenceEntry()
    {
      MapMakerInternalMap.ReferenceEntry nextEvictable = this;
      MapMakerInternalMap.ReferenceEntry previousEvictable = this;
      
      public MapMakerInternalMap.ReferenceEntry getNextEvictable()
      {
        return this.nextEvictable;
      }
      
      public void setNextEvictable(MapMakerInternalMap.ReferenceEntry paramAnonymousReferenceEntry)
      {
        this.nextEvictable = paramAnonymousReferenceEntry;
      }
      
      public MapMakerInternalMap.ReferenceEntry getPreviousEvictable()
      {
        return this.previousEvictable;
      }
      
      public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry paramAnonymousReferenceEntry)
      {
        this.previousEvictable = paramAnonymousReferenceEntry;
      }
    };
    
    public boolean offer(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      MapMakerInternalMap.connectEvictables(paramReferenceEntry.getPreviousEvictable(), paramReferenceEntry.getNextEvictable());
      MapMakerInternalMap.connectEvictables(this.head.getPreviousEvictable(), paramReferenceEntry);
      MapMakerInternalMap.connectEvictables(paramReferenceEntry, this.head);
      return true;
    }
    
    public MapMakerInternalMap.ReferenceEntry peek()
    {
      MapMakerInternalMap.ReferenceEntry localReferenceEntry = this.head.getNextEvictable();
      return localReferenceEntry == this.head ? null : localReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry poll()
    {
      MapMakerInternalMap.ReferenceEntry localReferenceEntry = this.head.getNextEvictable();
      if (localReferenceEntry == this.head) {
        return null;
      }
      remove(localReferenceEntry);
      return localReferenceEntry;
    }
    
    public boolean remove(Object paramObject)
    {
      MapMakerInternalMap.ReferenceEntry localReferenceEntry1 = (MapMakerInternalMap.ReferenceEntry)paramObject;
      MapMakerInternalMap.ReferenceEntry localReferenceEntry2 = localReferenceEntry1.getPreviousEvictable();
      MapMakerInternalMap.ReferenceEntry localReferenceEntry3 = localReferenceEntry1.getNextEvictable();
      MapMakerInternalMap.connectEvictables(localReferenceEntry2, localReferenceEntry3);
      MapMakerInternalMap.nullifyEvictable(localReferenceEntry1);
      return localReferenceEntry3 != MapMakerInternalMap.NullEntry.INSTANCE;
    }
    
    public boolean contains(Object paramObject)
    {
      MapMakerInternalMap.ReferenceEntry localReferenceEntry = (MapMakerInternalMap.ReferenceEntry)paramObject;
      return localReferenceEntry.getNextEvictable() != MapMakerInternalMap.NullEntry.INSTANCE;
    }
    
    public boolean isEmpty()
    {
      return this.head.getNextEvictable() == this.head;
    }
    
    public int size()
    {
      int i = 0;
      for (MapMakerInternalMap.ReferenceEntry localReferenceEntry = this.head.getNextEvictable(); localReferenceEntry != this.head; localReferenceEntry = localReferenceEntry.getNextEvictable()) {
        i++;
      }
      return i;
    }
    
    public void clear()
    {
      MapMakerInternalMap.ReferenceEntry localReferenceEntry;
      for (Object localObject = this.head.getNextEvictable(); localObject != this.head; localObject = localReferenceEntry)
      {
        localReferenceEntry = ((MapMakerInternalMap.ReferenceEntry)localObject).getNextEvictable();
        MapMakerInternalMap.nullifyEvictable((MapMakerInternalMap.ReferenceEntry)localObject);
      }
      this.head.setNextEvictable(this.head);
      this.head.setPreviousEvictable(this.head);
    }
    
    public Iterator iterator()
    {
      new AbstractSequentialIterator(peek())
      {
        protected MapMakerInternalMap.ReferenceEntry computeNext(MapMakerInternalMap.ReferenceEntry paramAnonymousReferenceEntry)
        {
          MapMakerInternalMap.ReferenceEntry localReferenceEntry = paramAnonymousReferenceEntry.getNextEvictable();
          return localReferenceEntry == MapMakerInternalMap.EvictionQueue.this.head ? null : localReferenceEntry;
        }
      };
    }
  }
  
  static class Segment
    extends ReentrantLock
  {
    final MapMakerInternalMap map;
    volatile int count;
    int modCount;
    int threshold;
    volatile AtomicReferenceArray table;
    final int maxSegmentSize;
    final ReferenceQueue keyReferenceQueue;
    final ReferenceQueue valueReferenceQueue;
    final Queue recencyQueue;
    final AtomicInteger readCount = new AtomicInteger();
    final Queue evictionQueue;
    final Queue expirationQueue;
    
    Segment(MapMakerInternalMap paramMapMakerInternalMap, int paramInt1, int paramInt2)
    {
      this.map = paramMapMakerInternalMap;
      this.maxSegmentSize = paramInt2;
      initTable(newEntryArray(paramInt1));
      this.keyReferenceQueue = (paramMapMakerInternalMap.usesKeyReferences() ? new ReferenceQueue() : null);
      this.valueReferenceQueue = (paramMapMakerInternalMap.usesValueReferences() ? new ReferenceQueue() : null);
      this.recencyQueue = ((paramMapMakerInternalMap.evictsBySize()) || (paramMapMakerInternalMap.expiresAfterAccess()) ? new ConcurrentLinkedQueue() : MapMakerInternalMap.discardingQueue());
      this.evictionQueue = (paramMapMakerInternalMap.evictsBySize() ? new MapMakerInternalMap.EvictionQueue() : MapMakerInternalMap.discardingQueue());
      this.expirationQueue = (paramMapMakerInternalMap.expires() ? new MapMakerInternalMap.ExpirationQueue() : MapMakerInternalMap.discardingQueue());
    }
    
    AtomicReferenceArray newEntryArray(int paramInt)
    {
      return new AtomicReferenceArray(paramInt);
    }
    
    void initTable(AtomicReferenceArray paramAtomicReferenceArray)
    {
      this.threshold = (paramAtomicReferenceArray.length() * 3 / 4);
      if (this.threshold == this.maxSegmentSize) {
        this.threshold += 1;
      }
      this.table = paramAtomicReferenceArray;
    }
    
    MapMakerInternalMap.ReferenceEntry newEntry(Object paramObject, int paramInt, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      return this.map.entryFactory.newEntry(this, paramObject, paramInt, paramReferenceEntry);
    }
    
    MapMakerInternalMap.ReferenceEntry copyEntry(MapMakerInternalMap.ReferenceEntry paramReferenceEntry1, MapMakerInternalMap.ReferenceEntry paramReferenceEntry2)
    {
      if (paramReferenceEntry1.getKey() == null) {
        return null;
      }
      MapMakerInternalMap.ValueReference localValueReference = paramReferenceEntry1.getValueReference();
      Object localObject = localValueReference.get();
      if ((localObject == null) && (!localValueReference.isComputingReference())) {
        return null;
      }
      MapMakerInternalMap.ReferenceEntry localReferenceEntry = this.map.entryFactory.copyEntry(this, paramReferenceEntry1, paramReferenceEntry2);
      localReferenceEntry.setValueReference(localValueReference.copyFor(this.valueReferenceQueue, localObject, localReferenceEntry));
      return localReferenceEntry;
    }
    
    void setValue(MapMakerInternalMap.ReferenceEntry paramReferenceEntry, Object paramObject)
    {
      MapMakerInternalMap.ValueReference localValueReference = this.map.valueStrength.referenceValue(this, paramReferenceEntry, paramObject);
      paramReferenceEntry.setValueReference(localValueReference);
      recordWrite(paramReferenceEntry);
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
        MapMakerInternalMap.ReferenceEntry localReferenceEntry = (MapMakerInternalMap.ReferenceEntry)localReference;
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
        MapMakerInternalMap.ValueReference localValueReference = (MapMakerInternalMap.ValueReference)localReference;
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
    
    void recordRead(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      if (this.map.expiresAfterAccess()) {
        recordExpirationTime(paramReferenceEntry, this.map.expireAfterAccessNanos);
      }
      this.recencyQueue.add(paramReferenceEntry);
    }
    
    void recordLockedRead(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.evictionQueue.add(paramReferenceEntry);
      if (this.map.expiresAfterAccess())
      {
        recordExpirationTime(paramReferenceEntry, this.map.expireAfterAccessNanos);
        this.expirationQueue.add(paramReferenceEntry);
      }
    }
    
    void recordWrite(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      drainRecencyQueue();
      this.evictionQueue.add(paramReferenceEntry);
      if (this.map.expires())
      {
        long l = this.map.expiresAfterAccess() ? this.map.expireAfterAccessNanos : this.map.expireAfterWriteNanos;
        recordExpirationTime(paramReferenceEntry, l);
        this.expirationQueue.add(paramReferenceEntry);
      }
    }
    
    void drainRecencyQueue()
    {
      MapMakerInternalMap.ReferenceEntry localReferenceEntry;
      while ((localReferenceEntry = (MapMakerInternalMap.ReferenceEntry)this.recencyQueue.poll()) != null)
      {
        if (this.evictionQueue.contains(localReferenceEntry)) {
          this.evictionQueue.add(localReferenceEntry);
        }
        if ((this.map.expiresAfterAccess()) && (this.expirationQueue.contains(localReferenceEntry))) {
          this.expirationQueue.add(localReferenceEntry);
        }
      }
    }
    
    void recordExpirationTime(MapMakerInternalMap.ReferenceEntry paramReferenceEntry, long paramLong)
    {
      paramReferenceEntry.setExpirationTime(this.map.ticker.read() + paramLong);
    }
    
    void tryExpireEntries()
    {
      if (tryLock()) {
        try
        {
          expireEntries();
        }
        finally
        {
          unlock();
        }
      }
    }
    
    void expireEntries()
    {
      drainRecencyQueue();
      if (this.expirationQueue.isEmpty()) {
        return;
      }
      long l = this.map.ticker.read();
      MapMakerInternalMap.ReferenceEntry localReferenceEntry;
      while (((localReferenceEntry = (MapMakerInternalMap.ReferenceEntry)this.expirationQueue.peek()) != null) && (this.map.isExpired(localReferenceEntry, l))) {
        if (!removeEntry(localReferenceEntry, localReferenceEntry.getHash(), MapMaker.RemovalCause.EXPIRED)) {
          throw new AssertionError();
        }
      }
    }
    
    void enqueueNotification(MapMakerInternalMap.ReferenceEntry paramReferenceEntry, MapMaker.RemovalCause paramRemovalCause)
    {
      enqueueNotification(paramReferenceEntry.getKey(), paramReferenceEntry.getHash(), paramReferenceEntry.getValueReference().get(), paramRemovalCause);
    }
    
    void enqueueNotification(Object paramObject1, int paramInt, Object paramObject2, MapMaker.RemovalCause paramRemovalCause)
    {
      if (this.map.removalNotificationQueue != MapMakerInternalMap.DISCARDING_QUEUE)
      {
        MapMaker.RemovalNotification localRemovalNotification = new MapMaker.RemovalNotification(paramObject1, paramObject2, paramRemovalCause);
        this.map.removalNotificationQueue.offer(localRemovalNotification);
      }
    }
    
    boolean evictEntries()
    {
      if ((this.map.evictsBySize()) && (this.count >= this.maxSegmentSize))
      {
        drainRecencyQueue();
        MapMakerInternalMap.ReferenceEntry localReferenceEntry = (MapMakerInternalMap.ReferenceEntry)this.evictionQueue.remove();
        if (!removeEntry(localReferenceEntry, localReferenceEntry.getHash(), MapMaker.RemovalCause.SIZE)) {
          throw new AssertionError();
        }
        return true;
      }
      return false;
    }
    
    MapMakerInternalMap.ReferenceEntry getFirst(int paramInt)
    {
      AtomicReferenceArray localAtomicReferenceArray = this.table;
      return (MapMakerInternalMap.ReferenceEntry)localAtomicReferenceArray.get(paramInt & localAtomicReferenceArray.length() - 1);
    }
    
    MapMakerInternalMap.ReferenceEntry getEntry(Object paramObject, int paramInt)
    {
      if (this.count != 0) {
        for (MapMakerInternalMap.ReferenceEntry localReferenceEntry = getFirst(paramInt); localReferenceEntry != null; localReferenceEntry = localReferenceEntry.getNext()) {
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
      }
      return null;
    }
    
    MapMakerInternalMap.ReferenceEntry getLiveEntry(Object paramObject, int paramInt)
    {
      MapMakerInternalMap.ReferenceEntry localReferenceEntry = getEntry(paramObject, paramInt);
      if (localReferenceEntry == null) {
        return null;
      }
      if ((this.map.expires()) && (this.map.isExpired(localReferenceEntry)))
      {
        tryExpireEntries();
        return null;
      }
      return localReferenceEntry;
    }
    
    Object get(Object paramObject, int paramInt)
    {
      try
      {
        MapMakerInternalMap.ReferenceEntry localReferenceEntry = getLiveEntry(paramObject, paramInt);
        if (localReferenceEntry == null)
        {
          localObject1 = null;
          return localObject1;
        }
        Object localObject1 = localReferenceEntry.getValueReference().get();
        if (localObject1 != null) {
          recordRead(localReferenceEntry);
        } else {
          tryDrainReferenceQueues();
        }
        Object localObject2 = localObject1;
        return localObject2;
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
          MapMakerInternalMap.ReferenceEntry localReferenceEntry = getLiveEntry(paramObject, paramInt);
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
          AtomicReferenceArray localAtomicReferenceArray = this.table;
          int i = localAtomicReferenceArray.length();
          for (int j = 0; j < i; j++) {
            for (MapMakerInternalMap.ReferenceEntry localReferenceEntry = (MapMakerInternalMap.ReferenceEntry)localAtomicReferenceArray.get(j); localReferenceEntry != null; localReferenceEntry = localReferenceEntry.getNext())
            {
              Object localObject1 = getLiveValue(localReferenceEntry);
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
        preWriteCleanup();
        int i = this.count + 1;
        if (i > this.threshold)
        {
          expand();
          i = this.count + 1;
        }
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int j = paramInt & localAtomicReferenceArray.length() - 1;
        MapMakerInternalMap.ReferenceEntry localReferenceEntry1 = (MapMakerInternalMap.ReferenceEntry)localAtomicReferenceArray.get(j);
        for (MapMakerInternalMap.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext())
        {
          localObject1 = localReferenceEntry2.getKey();
          if ((localReferenceEntry2.getHash() == paramInt) && (localObject1 != null) && (this.map.keyEquivalence.equivalent(paramObject1, localObject1)))
          {
            MapMakerInternalMap.ValueReference localValueReference = localReferenceEntry2.getValueReference();
            Object localObject2 = localValueReference.get();
            if (localObject2 == null)
            {
              this.modCount += 1;
              setValue(localReferenceEntry2, paramObject2);
              if (!localValueReference.isComputingReference())
              {
                enqueueNotification(paramObject1, paramInt, localObject2, MapMaker.RemovalCause.COLLECTED);
                i = this.count;
              }
              else if (evictEntries())
              {
                i = this.count + 1;
              }
              this.count = i;
              localObject3 = null;
              return localObject3;
            }
            if (paramBoolean)
            {
              recordLockedRead(localReferenceEntry2);
              localObject3 = localObject2;
              return localObject3;
            }
            this.modCount += 1;
            enqueueNotification(paramObject1, paramInt, localObject2, MapMaker.RemovalCause.REPLACED);
            setValue(localReferenceEntry2, paramObject2);
            Object localObject3 = localObject2;
            return localObject3;
          }
        }
        this.modCount += 1;
        localReferenceEntry2 = newEntry(paramObject1, paramInt, localReferenceEntry1);
        setValue(localReferenceEntry2, paramObject2);
        localAtomicReferenceArray.set(j, localReferenceEntry2);
        if (evictEntries()) {
          i = this.count + 1;
        }
        this.count = i;
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
        MapMakerInternalMap.ReferenceEntry localReferenceEntry1 = (MapMakerInternalMap.ReferenceEntry)localAtomicReferenceArray1.get(m);
        if (localReferenceEntry1 != null)
        {
          MapMakerInternalMap.ReferenceEntry localReferenceEntry2 = localReferenceEntry1.getNext();
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
            for (MapMakerInternalMap.ReferenceEntry localReferenceEntry3 = localReferenceEntry2; localReferenceEntry3 != null; localReferenceEntry3 = localReferenceEntry3.getNext())
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
              MapMakerInternalMap.ReferenceEntry localReferenceEntry4 = (MapMakerInternalMap.ReferenceEntry)localAtomicReferenceArray2.get(i2);
              MapMakerInternalMap.ReferenceEntry localReferenceEntry5 = copyEntry(localReferenceEntry3, localReferenceEntry4);
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
        preWriteCleanup();
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int i = paramInt & localAtomicReferenceArray.length() - 1;
        MapMakerInternalMap.ReferenceEntry localReferenceEntry1 = (MapMakerInternalMap.ReferenceEntry)localAtomicReferenceArray.get(i);
        for (MapMakerInternalMap.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext())
        {
          Object localObject1 = localReferenceEntry2.getKey();
          if ((localReferenceEntry2.getHash() == paramInt) && (localObject1 != null) && (this.map.keyEquivalence.equivalent(paramObject1, localObject1)))
          {
            MapMakerInternalMap.ValueReference localValueReference = localReferenceEntry2.getValueReference();
            Object localObject2 = localValueReference.get();
            if (localObject2 == null)
            {
              if (isCollected(localValueReference))
              {
                j = this.count - 1;
                this.modCount += 1;
                enqueueNotification(localObject1, paramInt, localObject2, MapMaker.RemovalCause.COLLECTED);
                MapMakerInternalMap.ReferenceEntry localReferenceEntry3 = removeFromChain(localReferenceEntry1, localReferenceEntry2);
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
              enqueueNotification(paramObject1, paramInt, localObject2, MapMaker.RemovalCause.REPLACED);
              setValue(localReferenceEntry2, paramObject3);
              bool2 = true;
              return bool2;
            }
            recordLockedRead(localReferenceEntry2);
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
        preWriteCleanup();
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int i = paramInt & localAtomicReferenceArray.length() - 1;
        MapMakerInternalMap.ReferenceEntry localReferenceEntry1 = (MapMakerInternalMap.ReferenceEntry)localAtomicReferenceArray.get(i);
        for (MapMakerInternalMap.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext())
        {
          Object localObject1 = localReferenceEntry2.getKey();
          if ((localReferenceEntry2.getHash() == paramInt) && (localObject1 != null) && (this.map.keyEquivalence.equivalent(paramObject1, localObject1)))
          {
            MapMakerInternalMap.ValueReference localValueReference = localReferenceEntry2.getValueReference();
            Object localObject2 = localValueReference.get();
            if (localObject2 == null)
            {
              if (isCollected(localValueReference))
              {
                int j = this.count - 1;
                this.modCount += 1;
                enqueueNotification(localObject1, paramInt, localObject2, MapMaker.RemovalCause.COLLECTED);
                MapMakerInternalMap.ReferenceEntry localReferenceEntry3 = removeFromChain(localReferenceEntry1, localReferenceEntry2);
                j = this.count - 1;
                localAtomicReferenceArray.set(i, localReferenceEntry3);
                this.count = j;
              }
              localObject3 = null;
              return localObject3;
            }
            this.modCount += 1;
            enqueueNotification(paramObject1, paramInt, localObject2, MapMaker.RemovalCause.REPLACED);
            setValue(localReferenceEntry2, paramObject2);
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
        preWriteCleanup();
        int i = this.count - 1;
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int j = paramInt & localAtomicReferenceArray.length() - 1;
        MapMakerInternalMap.ReferenceEntry localReferenceEntry1 = (MapMakerInternalMap.ReferenceEntry)localAtomicReferenceArray.get(j);
        for (MapMakerInternalMap.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext())
        {
          Object localObject1 = localReferenceEntry2.getKey();
          if ((localReferenceEntry2.getHash() == paramInt) && (localObject1 != null) && (this.map.keyEquivalence.equivalent(paramObject, localObject1)))
          {
            MapMakerInternalMap.ValueReference localValueReference = localReferenceEntry2.getValueReference();
            Object localObject2 = localValueReference.get();
            MapMaker.RemovalCause localRemovalCause;
            if (localObject2 != null)
            {
              localRemovalCause = MapMaker.RemovalCause.EXPLICIT;
            }
            else if (isCollected(localValueReference))
            {
              localRemovalCause = MapMaker.RemovalCause.COLLECTED;
            }
            else
            {
              localObject3 = null;
              return localObject3;
            }
            this.modCount += 1;
            enqueueNotification(localObject1, paramInt, localObject2, localRemovalCause);
            Object localObject3 = removeFromChain(localReferenceEntry1, localReferenceEntry2);
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
    
    boolean remove(Object paramObject1, int paramInt, Object paramObject2)
    {
      lock();
      try
      {
        preWriteCleanup();
        int i = this.count - 1;
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int j = paramInt & localAtomicReferenceArray.length() - 1;
        MapMakerInternalMap.ReferenceEntry localReferenceEntry1 = (MapMakerInternalMap.ReferenceEntry)localAtomicReferenceArray.get(j);
        for (MapMakerInternalMap.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext())
        {
          Object localObject1 = localReferenceEntry2.getKey();
          if ((localReferenceEntry2.getHash() == paramInt) && (localObject1 != null) && (this.map.keyEquivalence.equivalent(paramObject1, localObject1)))
          {
            MapMakerInternalMap.ValueReference localValueReference = localReferenceEntry2.getValueReference();
            Object localObject2 = localValueReference.get();
            MapMaker.RemovalCause localRemovalCause;
            if (this.map.valueEquivalence.equivalent(paramObject2, localObject2))
            {
              localRemovalCause = MapMaker.RemovalCause.EXPLICIT;
            }
            else if (isCollected(localValueReference))
            {
              localRemovalCause = MapMaker.RemovalCause.COLLECTED;
            }
            else
            {
              boolean bool2 = false;
              return bool2;
            }
            this.modCount += 1;
            enqueueNotification(localObject1, paramInt, localObject2, localRemovalCause);
            MapMakerInternalMap.ReferenceEntry localReferenceEntry3 = removeFromChain(localReferenceEntry1, localReferenceEntry2);
            i = this.count - 1;
            localAtomicReferenceArray.set(j, localReferenceEntry3);
            this.count = i;
            boolean bool3 = localRemovalCause == MapMaker.RemovalCause.EXPLICIT;
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
          if (this.map.removalNotificationQueue != MapMakerInternalMap.DISCARDING_QUEUE) {
            for (i = 0; i < localAtomicReferenceArray.length(); i++) {
              for (MapMakerInternalMap.ReferenceEntry localReferenceEntry = (MapMakerInternalMap.ReferenceEntry)localAtomicReferenceArray.get(i); localReferenceEntry != null; localReferenceEntry = localReferenceEntry.getNext()) {
                if (!localReferenceEntry.getValueReference().isComputingReference()) {
                  enqueueNotification(localReferenceEntry, MapMaker.RemovalCause.EXPLICIT);
                }
              }
            }
          }
          for (int i = 0; i < localAtomicReferenceArray.length(); i++) {
            localAtomicReferenceArray.set(i, null);
          }
          clearReferenceQueues();
          this.evictionQueue.clear();
          this.expirationQueue.clear();
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
    
    MapMakerInternalMap.ReferenceEntry removeFromChain(MapMakerInternalMap.ReferenceEntry paramReferenceEntry1, MapMakerInternalMap.ReferenceEntry paramReferenceEntry2)
    {
      this.evictionQueue.remove(paramReferenceEntry2);
      this.expirationQueue.remove(paramReferenceEntry2);
      int i = this.count;
      Object localObject = paramReferenceEntry2.getNext();
      for (MapMakerInternalMap.ReferenceEntry localReferenceEntry1 = paramReferenceEntry1; localReferenceEntry1 != paramReferenceEntry2; localReferenceEntry1 = localReferenceEntry1.getNext())
      {
        MapMakerInternalMap.ReferenceEntry localReferenceEntry2 = copyEntry(localReferenceEntry1, (MapMakerInternalMap.ReferenceEntry)localObject);
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
      return (MapMakerInternalMap.ReferenceEntry)localObject;
    }
    
    void removeCollectedEntry(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      enqueueNotification(paramReferenceEntry, MapMaker.RemovalCause.COLLECTED);
      this.evictionQueue.remove(paramReferenceEntry);
      this.expirationQueue.remove(paramReferenceEntry);
    }
    
    boolean reclaimKey(MapMakerInternalMap.ReferenceEntry paramReferenceEntry, int paramInt)
    {
      lock();
      try
      {
        int i = this.count - 1;
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int j = paramInt & localAtomicReferenceArray.length() - 1;
        MapMakerInternalMap.ReferenceEntry localReferenceEntry1 = (MapMakerInternalMap.ReferenceEntry)localAtomicReferenceArray.get(j);
        for (MapMakerInternalMap.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext()) {
          if (localReferenceEntry2 == paramReferenceEntry)
          {
            this.modCount += 1;
            enqueueNotification(localReferenceEntry2.getKey(), paramInt, localReferenceEntry2.getValueReference().get(), MapMaker.RemovalCause.COLLECTED);
            MapMakerInternalMap.ReferenceEntry localReferenceEntry3 = removeFromChain(localReferenceEntry1, localReferenceEntry2);
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
    
    boolean reclaimValue(Object paramObject, int paramInt, MapMakerInternalMap.ValueReference paramValueReference)
    {
      lock();
      try
      {
        int i = this.count - 1;
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int j = paramInt & localAtomicReferenceArray.length() - 1;
        MapMakerInternalMap.ReferenceEntry localReferenceEntry1 = (MapMakerInternalMap.ReferenceEntry)localAtomicReferenceArray.get(j);
        for (MapMakerInternalMap.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext())
        {
          Object localObject1 = localReferenceEntry2.getKey();
          if ((localReferenceEntry2.getHash() == paramInt) && (localObject1 != null) && (this.map.keyEquivalence.equivalent(paramObject, localObject1)))
          {
            MapMakerInternalMap.ValueReference localValueReference = localReferenceEntry2.getValueReference();
            if (localValueReference == paramValueReference)
            {
              this.modCount += 1;
              enqueueNotification(paramObject, paramInt, paramValueReference.get(), MapMaker.RemovalCause.COLLECTED);
              MapMakerInternalMap.ReferenceEntry localReferenceEntry3 = removeFromChain(localReferenceEntry1, localReferenceEntry2);
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
    
    boolean clearValue(Object paramObject, int paramInt, MapMakerInternalMap.ValueReference paramValueReference)
    {
      lock();
      try
      {
        AtomicReferenceArray localAtomicReferenceArray = this.table;
        int i = paramInt & localAtomicReferenceArray.length() - 1;
        MapMakerInternalMap.ReferenceEntry localReferenceEntry1 = (MapMakerInternalMap.ReferenceEntry)localAtomicReferenceArray.get(i);
        for (MapMakerInternalMap.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext())
        {
          Object localObject1 = localReferenceEntry2.getKey();
          if ((localReferenceEntry2.getHash() == paramInt) && (localObject1 != null) && (this.map.keyEquivalence.equivalent(paramObject, localObject1)))
          {
            MapMakerInternalMap.ValueReference localValueReference = localReferenceEntry2.getValueReference();
            if (localValueReference == paramValueReference)
            {
              MapMakerInternalMap.ReferenceEntry localReferenceEntry3 = removeFromChain(localReferenceEntry1, localReferenceEntry2);
              localAtomicReferenceArray.set(i, localReferenceEntry3);
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
        postWriteCleanup();
      }
    }
    
    boolean removeEntry(MapMakerInternalMap.ReferenceEntry paramReferenceEntry, int paramInt, MapMaker.RemovalCause paramRemovalCause)
    {
      int i = this.count - 1;
      AtomicReferenceArray localAtomicReferenceArray = this.table;
      int j = paramInt & localAtomicReferenceArray.length() - 1;
      MapMakerInternalMap.ReferenceEntry localReferenceEntry1 = (MapMakerInternalMap.ReferenceEntry)localAtomicReferenceArray.get(j);
      for (MapMakerInternalMap.ReferenceEntry localReferenceEntry2 = localReferenceEntry1; localReferenceEntry2 != null; localReferenceEntry2 = localReferenceEntry2.getNext()) {
        if (localReferenceEntry2 == paramReferenceEntry)
        {
          this.modCount += 1;
          enqueueNotification(localReferenceEntry2.getKey(), paramInt, localReferenceEntry2.getValueReference().get(), paramRemovalCause);
          MapMakerInternalMap.ReferenceEntry localReferenceEntry3 = removeFromChain(localReferenceEntry1, localReferenceEntry2);
          i = this.count - 1;
          localAtomicReferenceArray.set(j, localReferenceEntry3);
          this.count = i;
          return true;
        }
      }
      return false;
    }
    
    boolean isCollected(MapMakerInternalMap.ValueReference paramValueReference)
    {
      if (paramValueReference.isComputingReference()) {
        return false;
      }
      return paramValueReference.get() == null;
    }
    
    Object getLiveValue(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
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
      if ((this.map.expires()) && (this.map.isExpired(paramReferenceEntry)))
      {
        tryExpireEntries();
        return null;
      }
      return localObject;
    }
    
    void postReadCleanup()
    {
      if ((this.readCount.incrementAndGet() & 0x3F) == 0) {
        runCleanup();
      }
    }
    
    void preWriteCleanup()
    {
      runLockedCleanup();
    }
    
    void postWriteCleanup()
    {
      runUnlockedCleanup();
    }
    
    void runCleanup()
    {
      runLockedCleanup();
      runUnlockedCleanup();
    }
    
    void runLockedCleanup()
    {
      if (tryLock()) {
        try
        {
          drainReferenceQueues();
          expireEntries();
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
  
  static final class StrongValueReference
    implements MapMakerInternalMap.ValueReference
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
    
    public MapMakerInternalMap.ReferenceEntry getEntry()
    {
      return null;
    }
    
    public MapMakerInternalMap.ValueReference copyFor(ReferenceQueue paramReferenceQueue, Object paramObject, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      return this;
    }
    
    public boolean isComputingReference()
    {
      return false;
    }
    
    public Object waitForValue()
    {
      return get();
    }
    
    public void clear(MapMakerInternalMap.ValueReference paramValueReference) {}
  }
  
  static final class SoftValueReference
    extends SoftReference
    implements MapMakerInternalMap.ValueReference
  {
    final MapMakerInternalMap.ReferenceEntry entry;
    
    SoftValueReference(ReferenceQueue paramReferenceQueue, Object paramObject, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      super(paramReferenceQueue);
      this.entry = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getEntry()
    {
      return this.entry;
    }
    
    public void clear(MapMakerInternalMap.ValueReference paramValueReference)
    {
      clear();
    }
    
    public MapMakerInternalMap.ValueReference copyFor(ReferenceQueue paramReferenceQueue, Object paramObject, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      return new SoftValueReference(paramReferenceQueue, paramObject, paramReferenceEntry);
    }
    
    public boolean isComputingReference()
    {
      return false;
    }
    
    public Object waitForValue()
    {
      return get();
    }
  }
  
  static final class WeakValueReference
    extends WeakReference
    implements MapMakerInternalMap.ValueReference
  {
    final MapMakerInternalMap.ReferenceEntry entry;
    
    WeakValueReference(ReferenceQueue paramReferenceQueue, Object paramObject, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      super(paramReferenceQueue);
      this.entry = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getEntry()
    {
      return this.entry;
    }
    
    public void clear(MapMakerInternalMap.ValueReference paramValueReference)
    {
      clear();
    }
    
    public MapMakerInternalMap.ValueReference copyFor(ReferenceQueue paramReferenceQueue, Object paramObject, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      return new WeakValueReference(paramReferenceQueue, paramObject, paramReferenceEntry);
    }
    
    public boolean isComputingReference()
    {
      return false;
    }
    
    public Object waitForValue()
    {
      return get();
    }
  }
  
  static final class WeakExpirableEvictableEntry
    extends MapMakerInternalMap.WeakEntry
    implements MapMakerInternalMap.ReferenceEntry
  {
    volatile long time = Long.MAX_VALUE;
    MapMakerInternalMap.ReferenceEntry nextExpirable = MapMakerInternalMap.nullEntry();
    MapMakerInternalMap.ReferenceEntry previousExpirable = MapMakerInternalMap.nullEntry();
    MapMakerInternalMap.ReferenceEntry nextEvictable = MapMakerInternalMap.nullEntry();
    MapMakerInternalMap.ReferenceEntry previousEvictable = MapMakerInternalMap.nullEntry();
    
    WeakExpirableEvictableEntry(ReferenceQueue paramReferenceQueue, Object paramObject, int paramInt, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      super(paramObject, paramInt, paramReferenceEntry);
    }
    
    public long getExpirationTime()
    {
      return this.time;
    }
    
    public void setExpirationTime(long paramLong)
    {
      this.time = paramLong;
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextExpirable()
    {
      return this.nextExpirable;
    }
    
    public void setNextExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.nextExpirable = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousExpirable()
    {
      return this.previousExpirable;
    }
    
    public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.previousExpirable = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextEvictable()
    {
      return this.nextEvictable;
    }
    
    public void setNextEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.nextEvictable = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousEvictable()
    {
      return this.previousEvictable;
    }
    
    public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.previousEvictable = paramReferenceEntry;
    }
  }
  
  static final class WeakEvictableEntry
    extends MapMakerInternalMap.WeakEntry
    implements MapMakerInternalMap.ReferenceEntry
  {
    MapMakerInternalMap.ReferenceEntry nextEvictable = MapMakerInternalMap.nullEntry();
    MapMakerInternalMap.ReferenceEntry previousEvictable = MapMakerInternalMap.nullEntry();
    
    WeakEvictableEntry(ReferenceQueue paramReferenceQueue, Object paramObject, int paramInt, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      super(paramObject, paramInt, paramReferenceEntry);
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextEvictable()
    {
      return this.nextEvictable;
    }
    
    public void setNextEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.nextEvictable = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousEvictable()
    {
      return this.previousEvictable;
    }
    
    public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.previousEvictable = paramReferenceEntry;
    }
  }
  
  static final class WeakExpirableEntry
    extends MapMakerInternalMap.WeakEntry
    implements MapMakerInternalMap.ReferenceEntry
  {
    volatile long time = Long.MAX_VALUE;
    MapMakerInternalMap.ReferenceEntry nextExpirable = MapMakerInternalMap.nullEntry();
    MapMakerInternalMap.ReferenceEntry previousExpirable = MapMakerInternalMap.nullEntry();
    
    WeakExpirableEntry(ReferenceQueue paramReferenceQueue, Object paramObject, int paramInt, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      super(paramObject, paramInt, paramReferenceEntry);
    }
    
    public long getExpirationTime()
    {
      return this.time;
    }
    
    public void setExpirationTime(long paramLong)
    {
      this.time = paramLong;
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextExpirable()
    {
      return this.nextExpirable;
    }
    
    public void setNextExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.nextExpirable = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousExpirable()
    {
      return this.previousExpirable;
    }
    
    public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.previousExpirable = paramReferenceEntry;
    }
  }
  
  static class WeakEntry
    extends WeakReference
    implements MapMakerInternalMap.ReferenceEntry
  {
    final int hash;
    final MapMakerInternalMap.ReferenceEntry next;
    volatile MapMakerInternalMap.ValueReference valueReference = MapMakerInternalMap.unset();
    
    WeakEntry(ReferenceQueue paramReferenceQueue, Object paramObject, int paramInt, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      super(paramReferenceQueue);
      this.hash = paramInt;
      this.next = paramReferenceEntry;
    }
    
    public Object getKey()
    {
      return get();
    }
    
    public long getExpirationTime()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setExpirationTime(long paramLong)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextExpirable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setNextExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousExpirable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextEvictable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setNextEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousEvictable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ValueReference getValueReference()
    {
      return this.valueReference;
    }
    
    public void setValueReference(MapMakerInternalMap.ValueReference paramValueReference)
    {
      MapMakerInternalMap.ValueReference localValueReference = this.valueReference;
      this.valueReference = paramValueReference;
      localValueReference.clear(paramValueReference);
    }
    
    public int getHash()
    {
      return this.hash;
    }
    
    public MapMakerInternalMap.ReferenceEntry getNext()
    {
      return this.next;
    }
  }
  
  static final class SoftExpirableEvictableEntry
    extends MapMakerInternalMap.SoftEntry
    implements MapMakerInternalMap.ReferenceEntry
  {
    volatile long time = Long.MAX_VALUE;
    MapMakerInternalMap.ReferenceEntry nextExpirable = MapMakerInternalMap.nullEntry();
    MapMakerInternalMap.ReferenceEntry previousExpirable = MapMakerInternalMap.nullEntry();
    MapMakerInternalMap.ReferenceEntry nextEvictable = MapMakerInternalMap.nullEntry();
    MapMakerInternalMap.ReferenceEntry previousEvictable = MapMakerInternalMap.nullEntry();
    
    SoftExpirableEvictableEntry(ReferenceQueue paramReferenceQueue, Object paramObject, int paramInt, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      super(paramObject, paramInt, paramReferenceEntry);
    }
    
    public long getExpirationTime()
    {
      return this.time;
    }
    
    public void setExpirationTime(long paramLong)
    {
      this.time = paramLong;
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextExpirable()
    {
      return this.nextExpirable;
    }
    
    public void setNextExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.nextExpirable = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousExpirable()
    {
      return this.previousExpirable;
    }
    
    public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.previousExpirable = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextEvictable()
    {
      return this.nextEvictable;
    }
    
    public void setNextEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.nextEvictable = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousEvictable()
    {
      return this.previousEvictable;
    }
    
    public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.previousEvictable = paramReferenceEntry;
    }
  }
  
  static final class SoftEvictableEntry
    extends MapMakerInternalMap.SoftEntry
    implements MapMakerInternalMap.ReferenceEntry
  {
    MapMakerInternalMap.ReferenceEntry nextEvictable = MapMakerInternalMap.nullEntry();
    MapMakerInternalMap.ReferenceEntry previousEvictable = MapMakerInternalMap.nullEntry();
    
    SoftEvictableEntry(ReferenceQueue paramReferenceQueue, Object paramObject, int paramInt, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      super(paramObject, paramInt, paramReferenceEntry);
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextEvictable()
    {
      return this.nextEvictable;
    }
    
    public void setNextEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.nextEvictable = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousEvictable()
    {
      return this.previousEvictable;
    }
    
    public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.previousEvictable = paramReferenceEntry;
    }
  }
  
  static final class SoftExpirableEntry
    extends MapMakerInternalMap.SoftEntry
    implements MapMakerInternalMap.ReferenceEntry
  {
    volatile long time = Long.MAX_VALUE;
    MapMakerInternalMap.ReferenceEntry nextExpirable = MapMakerInternalMap.nullEntry();
    MapMakerInternalMap.ReferenceEntry previousExpirable = MapMakerInternalMap.nullEntry();
    
    SoftExpirableEntry(ReferenceQueue paramReferenceQueue, Object paramObject, int paramInt, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      super(paramObject, paramInt, paramReferenceEntry);
    }
    
    public long getExpirationTime()
    {
      return this.time;
    }
    
    public void setExpirationTime(long paramLong)
    {
      this.time = paramLong;
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextExpirable()
    {
      return this.nextExpirable;
    }
    
    public void setNextExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.nextExpirable = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousExpirable()
    {
      return this.previousExpirable;
    }
    
    public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.previousExpirable = paramReferenceEntry;
    }
  }
  
  static class SoftEntry
    extends SoftReference
    implements MapMakerInternalMap.ReferenceEntry
  {
    final int hash;
    final MapMakerInternalMap.ReferenceEntry next;
    volatile MapMakerInternalMap.ValueReference valueReference = MapMakerInternalMap.unset();
    
    SoftEntry(ReferenceQueue paramReferenceQueue, Object paramObject, int paramInt, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      super(paramReferenceQueue);
      this.hash = paramInt;
      this.next = paramReferenceEntry;
    }
    
    public Object getKey()
    {
      return get();
    }
    
    public long getExpirationTime()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setExpirationTime(long paramLong)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextExpirable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setNextExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousExpirable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextEvictable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setNextEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousEvictable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ValueReference getValueReference()
    {
      return this.valueReference;
    }
    
    public void setValueReference(MapMakerInternalMap.ValueReference paramValueReference)
    {
      MapMakerInternalMap.ValueReference localValueReference = this.valueReference;
      this.valueReference = paramValueReference;
      localValueReference.clear(paramValueReference);
    }
    
    public int getHash()
    {
      return this.hash;
    }
    
    public MapMakerInternalMap.ReferenceEntry getNext()
    {
      return this.next;
    }
  }
  
  static final class StrongExpirableEvictableEntry
    extends MapMakerInternalMap.StrongEntry
    implements MapMakerInternalMap.ReferenceEntry
  {
    volatile long time = Long.MAX_VALUE;
    MapMakerInternalMap.ReferenceEntry nextExpirable = MapMakerInternalMap.nullEntry();
    MapMakerInternalMap.ReferenceEntry previousExpirable = MapMakerInternalMap.nullEntry();
    MapMakerInternalMap.ReferenceEntry nextEvictable = MapMakerInternalMap.nullEntry();
    MapMakerInternalMap.ReferenceEntry previousEvictable = MapMakerInternalMap.nullEntry();
    
    StrongExpirableEvictableEntry(Object paramObject, int paramInt, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      super(paramInt, paramReferenceEntry);
    }
    
    public long getExpirationTime()
    {
      return this.time;
    }
    
    public void setExpirationTime(long paramLong)
    {
      this.time = paramLong;
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextExpirable()
    {
      return this.nextExpirable;
    }
    
    public void setNextExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.nextExpirable = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousExpirable()
    {
      return this.previousExpirable;
    }
    
    public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.previousExpirable = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextEvictable()
    {
      return this.nextEvictable;
    }
    
    public void setNextEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.nextEvictable = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousEvictable()
    {
      return this.previousEvictable;
    }
    
    public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.previousEvictable = paramReferenceEntry;
    }
  }
  
  static final class StrongEvictableEntry
    extends MapMakerInternalMap.StrongEntry
    implements MapMakerInternalMap.ReferenceEntry
  {
    MapMakerInternalMap.ReferenceEntry nextEvictable = MapMakerInternalMap.nullEntry();
    MapMakerInternalMap.ReferenceEntry previousEvictable = MapMakerInternalMap.nullEntry();
    
    StrongEvictableEntry(Object paramObject, int paramInt, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      super(paramInt, paramReferenceEntry);
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextEvictable()
    {
      return this.nextEvictable;
    }
    
    public void setNextEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.nextEvictable = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousEvictable()
    {
      return this.previousEvictable;
    }
    
    public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.previousEvictable = paramReferenceEntry;
    }
  }
  
  static final class StrongExpirableEntry
    extends MapMakerInternalMap.StrongEntry
    implements MapMakerInternalMap.ReferenceEntry
  {
    volatile long time = Long.MAX_VALUE;
    MapMakerInternalMap.ReferenceEntry nextExpirable = MapMakerInternalMap.nullEntry();
    MapMakerInternalMap.ReferenceEntry previousExpirable = MapMakerInternalMap.nullEntry();
    
    StrongExpirableEntry(Object paramObject, int paramInt, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      super(paramInt, paramReferenceEntry);
    }
    
    public long getExpirationTime()
    {
      return this.time;
    }
    
    public void setExpirationTime(long paramLong)
    {
      this.time = paramLong;
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextExpirable()
    {
      return this.nextExpirable;
    }
    
    public void setNextExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.nextExpirable = paramReferenceEntry;
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousExpirable()
    {
      return this.previousExpirable;
    }
    
    public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.previousExpirable = paramReferenceEntry;
    }
  }
  
  static class StrongEntry
    implements MapMakerInternalMap.ReferenceEntry
  {
    final Object key;
    final int hash;
    final MapMakerInternalMap.ReferenceEntry next;
    volatile MapMakerInternalMap.ValueReference valueReference = MapMakerInternalMap.unset();
    
    StrongEntry(Object paramObject, int paramInt, MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      this.key = paramObject;
      this.hash = paramInt;
      this.next = paramReferenceEntry;
    }
    
    public Object getKey()
    {
      return this.key;
    }
    
    public long getExpirationTime()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setExpirationTime(long paramLong)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextExpirable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setNextExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousExpirable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextEvictable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setNextEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousEvictable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ValueReference getValueReference()
    {
      return this.valueReference;
    }
    
    public void setValueReference(MapMakerInternalMap.ValueReference paramValueReference)
    {
      MapMakerInternalMap.ValueReference localValueReference = this.valueReference;
      this.valueReference = paramValueReference;
      localValueReference.clear(paramValueReference);
    }
    
    public int getHash()
    {
      return this.hash;
    }
    
    public MapMakerInternalMap.ReferenceEntry getNext()
    {
      return this.next;
    }
  }
  
  static abstract class AbstractReferenceEntry
    implements MapMakerInternalMap.ReferenceEntry
  {
    public MapMakerInternalMap.ValueReference getValueReference()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setValueReference(MapMakerInternalMap.ValueReference paramValueReference)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getNext()
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
    
    public long getExpirationTime()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setExpirationTime(long paramLong)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextExpirable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setNextExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousExpirable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getNextEvictable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setNextEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public MapMakerInternalMap.ReferenceEntry getPreviousEvictable()
    {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private static enum NullEntry
    implements MapMakerInternalMap.ReferenceEntry
  {
    INSTANCE;
    
    public MapMakerInternalMap.ValueReference getValueReference()
    {
      return null;
    }
    
    public void setValueReference(MapMakerInternalMap.ValueReference paramValueReference) {}
    
    public MapMakerInternalMap.ReferenceEntry getNext()
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
    
    public long getExpirationTime()
    {
      return 0L;
    }
    
    public void setExpirationTime(long paramLong) {}
    
    public MapMakerInternalMap.ReferenceEntry getNextExpirable()
    {
      return this;
    }
    
    public void setNextExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry) {}
    
    public MapMakerInternalMap.ReferenceEntry getPreviousExpirable()
    {
      return this;
    }
    
    public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry) {}
    
    public MapMakerInternalMap.ReferenceEntry getNextEvictable()
    {
      return this;
    }
    
    public void setNextEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry) {}
    
    public MapMakerInternalMap.ReferenceEntry getPreviousEvictable()
    {
      return this;
    }
    
    public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry paramReferenceEntry) {}
  }
  
  static abstract interface ReferenceEntry
  {
    public abstract MapMakerInternalMap.ValueReference getValueReference();
    
    public abstract void setValueReference(MapMakerInternalMap.ValueReference paramValueReference);
    
    public abstract ReferenceEntry getNext();
    
    public abstract int getHash();
    
    public abstract Object getKey();
    
    public abstract long getExpirationTime();
    
    public abstract void setExpirationTime(long paramLong);
    
    public abstract ReferenceEntry getNextExpirable();
    
    public abstract void setNextExpirable(ReferenceEntry paramReferenceEntry);
    
    public abstract ReferenceEntry getPreviousExpirable();
    
    public abstract void setPreviousExpirable(ReferenceEntry paramReferenceEntry);
    
    public abstract ReferenceEntry getNextEvictable();
    
    public abstract void setNextEvictable(ReferenceEntry paramReferenceEntry);
    
    public abstract ReferenceEntry getPreviousEvictable();
    
    public abstract void setPreviousEvictable(ReferenceEntry paramReferenceEntry);
  }
  
  static abstract interface ValueReference
  {
    public abstract Object get();
    
    public abstract Object waitForValue()
      throws ExecutionException;
    
    public abstract MapMakerInternalMap.ReferenceEntry getEntry();
    
    public abstract ValueReference copyFor(ReferenceQueue paramReferenceQueue, Object paramObject, MapMakerInternalMap.ReferenceEntry paramReferenceEntry);
    
    public abstract void clear(ValueReference paramValueReference);
    
    public abstract boolean isComputingReference();
  }
  
  static abstract enum EntryFactory
  {
    STRONG,  STRONG_EXPIRABLE,  STRONG_EVICTABLE,  STRONG_EXPIRABLE_EVICTABLE,  WEAK,  WEAK_EXPIRABLE,  WEAK_EVICTABLE,  WEAK_EXPIRABLE_EVICTABLE;
    
    static final int EXPIRABLE_MASK = 1;
    static final int EVICTABLE_MASK = 2;
    static final EntryFactory[][] factories = { { STRONG, STRONG_EXPIRABLE, STRONG_EVICTABLE, STRONG_EXPIRABLE_EVICTABLE }, new EntryFactory[0], { WEAK, WEAK_EXPIRABLE, WEAK_EVICTABLE, WEAK_EXPIRABLE_EVICTABLE } };
    
    static EntryFactory getFactory(MapMakerInternalMap.Strength paramStrength, boolean paramBoolean1, boolean paramBoolean2)
    {
      int i = (paramBoolean1 ? 1 : 0) | (paramBoolean2 ? 2 : 0);
      return factories[paramStrength.ordinal()][i];
    }
    
    abstract MapMakerInternalMap.ReferenceEntry newEntry(MapMakerInternalMap.Segment paramSegment, Object paramObject, int paramInt, MapMakerInternalMap.ReferenceEntry paramReferenceEntry);
    
    MapMakerInternalMap.ReferenceEntry copyEntry(MapMakerInternalMap.Segment paramSegment, MapMakerInternalMap.ReferenceEntry paramReferenceEntry1, MapMakerInternalMap.ReferenceEntry paramReferenceEntry2)
    {
      return newEntry(paramSegment, paramReferenceEntry1.getKey(), paramReferenceEntry1.getHash(), paramReferenceEntry2);
    }
    
    void copyExpirableEntry(MapMakerInternalMap.ReferenceEntry paramReferenceEntry1, MapMakerInternalMap.ReferenceEntry paramReferenceEntry2)
    {
      paramReferenceEntry2.setExpirationTime(paramReferenceEntry1.getExpirationTime());
      MapMakerInternalMap.connectExpirables(paramReferenceEntry1.getPreviousExpirable(), paramReferenceEntry2);
      MapMakerInternalMap.connectExpirables(paramReferenceEntry2, paramReferenceEntry1.getNextExpirable());
      MapMakerInternalMap.nullifyExpirable(paramReferenceEntry1);
    }
    
    void copyEvictableEntry(MapMakerInternalMap.ReferenceEntry paramReferenceEntry1, MapMakerInternalMap.ReferenceEntry paramReferenceEntry2)
    {
      MapMakerInternalMap.connectEvictables(paramReferenceEntry1.getPreviousEvictable(), paramReferenceEntry2);
      MapMakerInternalMap.connectEvictables(paramReferenceEntry2, paramReferenceEntry1.getNextEvictable());
      MapMakerInternalMap.nullifyEvictable(paramReferenceEntry1);
    }
  }
  
  static abstract enum Strength
  {
    STRONG,  SOFT,  WEAK;
    
    abstract MapMakerInternalMap.ValueReference referenceValue(MapMakerInternalMap.Segment paramSegment, MapMakerInternalMap.ReferenceEntry paramReferenceEntry, Object paramObject);
    
    abstract Equivalence defaultEquivalence();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\MapMakerInternalMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */