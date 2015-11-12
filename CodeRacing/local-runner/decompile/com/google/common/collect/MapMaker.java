package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.base.Ticker;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@GwtCompatible(emulated=true)
public final class MapMaker
  extends GenericMapMaker
{
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
  private static final int DEFAULT_EXPIRATION_NANOS = 0;
  static final int UNSET_INT = -1;
  boolean useCustomMap;
  int initialCapacity = -1;
  int concurrencyLevel = -1;
  int maximumSize = -1;
  MapMakerInternalMap.Strength keyStrength;
  MapMakerInternalMap.Strength valueStrength;
  long expireAfterWriteNanos = -1L;
  long expireAfterAccessNanos = -1L;
  RemovalCause nullRemovalCause;
  Equivalence keyEquivalence;
  Ticker ticker;
  
  @GwtIncompatible("To be supported")
  MapMaker keyEquivalence(Equivalence paramEquivalence)
  {
    Preconditions.checkState(this.keyEquivalence == null, "key equivalence was already set to %s", new Object[] { this.keyEquivalence });
    this.keyEquivalence = ((Equivalence)Preconditions.checkNotNull(paramEquivalence));
    this.useCustomMap = true;
    return this;
  }
  
  Equivalence getKeyEquivalence()
  {
    return (Equivalence)Objects.firstNonNull(this.keyEquivalence, getKeyStrength().defaultEquivalence());
  }
  
  public MapMaker initialCapacity(int paramInt)
  {
    Preconditions.checkState(this.initialCapacity == -1, "initial capacity was already set to %s", new Object[] { Integer.valueOf(this.initialCapacity) });
    Preconditions.checkArgument(paramInt >= 0);
    this.initialCapacity = paramInt;
    return this;
  }
  
  int getInitialCapacity()
  {
    return this.initialCapacity == -1 ? 16 : this.initialCapacity;
  }
  
  @Deprecated
  MapMaker maximumSize(int paramInt)
  {
    Preconditions.checkState(this.maximumSize == -1, "maximum size was already set to %s", new Object[] { Integer.valueOf(this.maximumSize) });
    Preconditions.checkArgument(paramInt >= 0, "maximum size must not be negative");
    this.maximumSize = paramInt;
    this.useCustomMap = true;
    if (this.maximumSize == 0) {
      this.nullRemovalCause = RemovalCause.SIZE;
    }
    return this;
  }
  
  public MapMaker concurrencyLevel(int paramInt)
  {
    Preconditions.checkState(this.concurrencyLevel == -1, "concurrency level was already set to %s", new Object[] { Integer.valueOf(this.concurrencyLevel) });
    Preconditions.checkArgument(paramInt > 0);
    this.concurrencyLevel = paramInt;
    return this;
  }
  
  int getConcurrencyLevel()
  {
    return this.concurrencyLevel == -1 ? 4 : this.concurrencyLevel;
  }
  
  @GwtIncompatible("java.lang.ref.WeakReference")
  public MapMaker weakKeys()
  {
    return setKeyStrength(MapMakerInternalMap.Strength.WEAK);
  }
  
  MapMaker setKeyStrength(MapMakerInternalMap.Strength paramStrength)
  {
    Preconditions.checkState(this.keyStrength == null, "Key strength was already set to %s", new Object[] { this.keyStrength });
    this.keyStrength = ((MapMakerInternalMap.Strength)Preconditions.checkNotNull(paramStrength));
    Preconditions.checkArgument(this.keyStrength != MapMakerInternalMap.Strength.SOFT, "Soft keys are not supported");
    if (paramStrength != MapMakerInternalMap.Strength.STRONG) {
      this.useCustomMap = true;
    }
    return this;
  }
  
  MapMakerInternalMap.Strength getKeyStrength()
  {
    return (MapMakerInternalMap.Strength)Objects.firstNonNull(this.keyStrength, MapMakerInternalMap.Strength.STRONG);
  }
  
  @GwtIncompatible("java.lang.ref.WeakReference")
  public MapMaker weakValues()
  {
    return setValueStrength(MapMakerInternalMap.Strength.WEAK);
  }
  
  @GwtIncompatible("java.lang.ref.SoftReference")
  public MapMaker softValues()
  {
    return setValueStrength(MapMakerInternalMap.Strength.SOFT);
  }
  
  MapMaker setValueStrength(MapMakerInternalMap.Strength paramStrength)
  {
    Preconditions.checkState(this.valueStrength == null, "Value strength was already set to %s", new Object[] { this.valueStrength });
    this.valueStrength = ((MapMakerInternalMap.Strength)Preconditions.checkNotNull(paramStrength));
    if (paramStrength != MapMakerInternalMap.Strength.STRONG) {
      this.useCustomMap = true;
    }
    return this;
  }
  
  MapMakerInternalMap.Strength getValueStrength()
  {
    return (MapMakerInternalMap.Strength)Objects.firstNonNull(this.valueStrength, MapMakerInternalMap.Strength.STRONG);
  }
  
  @Deprecated
  MapMaker expireAfterWrite(long paramLong, TimeUnit paramTimeUnit)
  {
    checkExpiration(paramLong, paramTimeUnit);
    this.expireAfterWriteNanos = paramTimeUnit.toNanos(paramLong);
    if ((paramLong == 0L) && (this.nullRemovalCause == null)) {
      this.nullRemovalCause = RemovalCause.EXPIRED;
    }
    this.useCustomMap = true;
    return this;
  }
  
  private void checkExpiration(long paramLong, TimeUnit paramTimeUnit)
  {
    Preconditions.checkState(this.expireAfterWriteNanos == -1L, "expireAfterWrite was already set to %s ns", new Object[] { Long.valueOf(this.expireAfterWriteNanos) });
    Preconditions.checkState(this.expireAfterAccessNanos == -1L, "expireAfterAccess was already set to %s ns", new Object[] { Long.valueOf(this.expireAfterAccessNanos) });
    Preconditions.checkArgument(paramLong >= 0L, "duration cannot be negative: %s %s", new Object[] { Long.valueOf(paramLong), paramTimeUnit });
  }
  
  long getExpireAfterWriteNanos()
  {
    return this.expireAfterWriteNanos == -1L ? 0L : this.expireAfterWriteNanos;
  }
  
  @GwtIncompatible("To be supported")
  @Deprecated
  MapMaker expireAfterAccess(long paramLong, TimeUnit paramTimeUnit)
  {
    checkExpiration(paramLong, paramTimeUnit);
    this.expireAfterAccessNanos = paramTimeUnit.toNanos(paramLong);
    if ((paramLong == 0L) && (this.nullRemovalCause == null)) {
      this.nullRemovalCause = RemovalCause.EXPIRED;
    }
    this.useCustomMap = true;
    return this;
  }
  
  long getExpireAfterAccessNanos()
  {
    return this.expireAfterAccessNanos == -1L ? 0L : this.expireAfterAccessNanos;
  }
  
  Ticker getTicker()
  {
    return (Ticker)Objects.firstNonNull(this.ticker, Ticker.systemTicker());
  }
  
  @GwtIncompatible("To be supported")
  @Deprecated
  GenericMapMaker removalListener(RemovalListener paramRemovalListener)
  {
    Preconditions.checkState(this.removalListener == null);
    MapMaker localMapMaker = this;
    localMapMaker.removalListener = ((RemovalListener)Preconditions.checkNotNull(paramRemovalListener));
    this.useCustomMap = true;
    return localMapMaker;
  }
  
  public ConcurrentMap makeMap()
  {
    if (!this.useCustomMap) {
      return new ConcurrentHashMap(getInitialCapacity(), 0.75F, getConcurrencyLevel());
    }
    return (ConcurrentMap)(this.nullRemovalCause == null ? new MapMakerInternalMap(this) : new NullConcurrentMap(this));
  }
  
  @GwtIncompatible("MapMakerInternalMap")
  MapMakerInternalMap makeCustomMap()
  {
    return new MapMakerInternalMap(this);
  }
  
  @Deprecated
  public ConcurrentMap makeComputingMap(Function paramFunction)
  {
    return (ConcurrentMap)(this.nullRemovalCause == null ? new ComputingMapAdapter(this, paramFunction) : new NullComputingConcurrentMap(this, paramFunction));
  }
  
  public String toString()
  {
    Objects.ToStringHelper localToStringHelper = Objects.toStringHelper(this);
    if (this.initialCapacity != -1) {
      localToStringHelper.add("initialCapacity", this.initialCapacity);
    }
    if (this.concurrencyLevel != -1) {
      localToStringHelper.add("concurrencyLevel", this.concurrencyLevel);
    }
    if (this.maximumSize != -1) {
      localToStringHelper.add("maximumSize", this.maximumSize);
    }
    if (this.expireAfterWriteNanos != -1L) {
      localToStringHelper.add("expireAfterWrite", this.expireAfterWriteNanos + "ns");
    }
    if (this.expireAfterAccessNanos != -1L) {
      localToStringHelper.add("expireAfterAccess", this.expireAfterAccessNanos + "ns");
    }
    if (this.keyStrength != null) {
      localToStringHelper.add("keyStrength", Ascii.toLowerCase(this.keyStrength.toString()));
    }
    if (this.valueStrength != null) {
      localToStringHelper.add("valueStrength", Ascii.toLowerCase(this.valueStrength.toString()));
    }
    if (this.keyEquivalence != null) {
      localToStringHelper.addValue("keyEquivalence");
    }
    if (this.removalListener != null) {
      localToStringHelper.addValue("removalListener");
    }
    return localToStringHelper.toString();
  }
  
  static final class ComputingMapAdapter
    extends ComputingConcurrentHashMap
    implements Serializable
  {
    private static final long serialVersionUID = 0L;
    
    ComputingMapAdapter(MapMaker paramMapMaker, Function paramFunction)
    {
      super(paramFunction);
    }
    
    public Object get(Object paramObject)
    {
      Object localObject;
      try
      {
        localObject = getOrCompute(paramObject);
      }
      catch (ExecutionException localExecutionException)
      {
        Throwable localThrowable = localExecutionException.getCause();
        Throwables.propagateIfInstanceOf(localThrowable, ComputationException.class);
        throw new ComputationException(localThrowable);
      }
      if (localObject == null) {
        throw new NullPointerException(this.computingFunction + " returned null for key " + paramObject + ".");
      }
      return localObject;
    }
  }
  
  static final class NullComputingConcurrentMap
    extends MapMaker.NullConcurrentMap
  {
    private static final long serialVersionUID = 0L;
    final Function computingFunction;
    
    NullComputingConcurrentMap(MapMaker paramMapMaker, Function paramFunction)
    {
      super();
      this.computingFunction = ((Function)Preconditions.checkNotNull(paramFunction));
    }
    
    public Object get(Object paramObject)
    {
      Object localObject1 = paramObject;
      Object localObject2 = compute(localObject1);
      Preconditions.checkNotNull(localObject2, this.computingFunction + " returned null for key " + localObject1 + ".");
      notifyRemoval(localObject1, localObject2);
      return localObject2;
    }
    
    private Object compute(Object paramObject)
    {
      Preconditions.checkNotNull(paramObject);
      try
      {
        return this.computingFunction.apply(paramObject);
      }
      catch (ComputationException localComputationException)
      {
        throw localComputationException;
      }
      catch (Throwable localThrowable)
      {
        throw new ComputationException(localThrowable);
      }
    }
  }
  
  static class NullConcurrentMap
    extends AbstractMap
    implements Serializable, ConcurrentMap
  {
    private static final long serialVersionUID = 0L;
    private final MapMaker.RemovalListener removalListener;
    private final MapMaker.RemovalCause removalCause;
    
    NullConcurrentMap(MapMaker paramMapMaker)
    {
      this.removalListener = paramMapMaker.getRemovalListener();
      this.removalCause = paramMapMaker.nullRemovalCause;
    }
    
    public boolean containsKey(Object paramObject)
    {
      return false;
    }
    
    public boolean containsValue(Object paramObject)
    {
      return false;
    }
    
    public Object get(Object paramObject)
    {
      return null;
    }
    
    void notifyRemoval(Object paramObject1, Object paramObject2)
    {
      MapMaker.RemovalNotification localRemovalNotification = new MapMaker.RemovalNotification(paramObject1, paramObject2, this.removalCause);
      this.removalListener.onRemoval(localRemovalNotification);
    }
    
    public Object put(Object paramObject1, Object paramObject2)
    {
      Preconditions.checkNotNull(paramObject1);
      Preconditions.checkNotNull(paramObject2);
      notifyRemoval(paramObject1, paramObject2);
      return null;
    }
    
    public Object putIfAbsent(Object paramObject1, Object paramObject2)
    {
      return put(paramObject1, paramObject2);
    }
    
    public Object remove(Object paramObject)
    {
      return null;
    }
    
    public boolean remove(Object paramObject1, Object paramObject2)
    {
      return false;
    }
    
    public Object replace(Object paramObject1, Object paramObject2)
    {
      Preconditions.checkNotNull(paramObject1);
      Preconditions.checkNotNull(paramObject2);
      return null;
    }
    
    public boolean replace(Object paramObject1, Object paramObject2, Object paramObject3)
    {
      Preconditions.checkNotNull(paramObject1);
      Preconditions.checkNotNull(paramObject3);
      return false;
    }
    
    public Set entrySet()
    {
      return Collections.emptySet();
    }
  }
  
  static abstract enum RemovalCause
  {
    EXPLICIT,  REPLACED,  COLLECTED,  EXPIRED,  SIZE;
    
    abstract boolean wasEvicted();
  }
  
  static final class RemovalNotification
    extends ImmutableEntry
  {
    private static final long serialVersionUID = 0L;
    private final MapMaker.RemovalCause cause;
    
    RemovalNotification(Object paramObject1, Object paramObject2, MapMaker.RemovalCause paramRemovalCause)
    {
      super(paramObject2);
      this.cause = paramRemovalCause;
    }
    
    public MapMaker.RemovalCause getCause()
    {
      return this.cause;
    }
    
    public boolean wasEvicted()
    {
      return this.cause.wasEvicted();
    }
  }
  
  static abstract interface RemovalListener
  {
    public abstract void onRemoval(MapMaker.RemovalNotification paramRemovalNotification);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\MapMaker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */