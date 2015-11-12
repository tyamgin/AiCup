package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Ticker;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@GwtCompatible(emulated=true)
public final class CacheBuilder
{
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
  private static final int DEFAULT_EXPIRATION_NANOS = 0;
  private static final int DEFAULT_REFRESH_NANOS = 0;
  static final Supplier NULL_STATS_COUNTER = Suppliers.ofInstance(new AbstractCache.StatsCounter()
  {
    public void recordHits(int paramAnonymousInt) {}
    
    public void recordMisses(int paramAnonymousInt) {}
    
    public void recordLoadSuccess(long paramAnonymousLong) {}
    
    public void recordLoadException(long paramAnonymousLong) {}
    
    public void recordEviction() {}
    
    public CacheStats snapshot()
    {
      return CacheBuilder.EMPTY_STATS;
    }
  });
  static final CacheStats EMPTY_STATS = new CacheStats(0L, 0L, 0L, 0L, 0L, 0L);
  static final Supplier CACHE_STATS_COUNTER = new Supplier()
  {
    public AbstractCache.StatsCounter get()
    {
      return new AbstractCache.SimpleStatsCounter();
    }
  };
  static final Ticker NULL_TICKER = new Ticker()
  {
    public long read()
    {
      return 0L;
    }
  };
  private static final Logger logger = Logger.getLogger(CacheBuilder.class.getName());
  static final int UNSET_INT = -1;
  boolean strictParsing = true;
  int initialCapacity = -1;
  int concurrencyLevel = -1;
  long maximumSize = -1L;
  long maximumWeight = -1L;
  Weigher weigher;
  LocalCache.Strength keyStrength;
  LocalCache.Strength valueStrength;
  long expireAfterWriteNanos = -1L;
  long expireAfterAccessNanos = -1L;
  long refreshNanos = -1L;
  Equivalence keyEquivalence;
  Equivalence valueEquivalence;
  RemovalListener removalListener;
  Ticker ticker;
  Supplier statsCounterSupplier = NULL_STATS_COUNTER;
  
  public static CacheBuilder newBuilder()
  {
    return new CacheBuilder();
  }
  
  @Beta
  @GwtIncompatible("To be supported")
  public static CacheBuilder from(CacheBuilderSpec paramCacheBuilderSpec)
  {
    return paramCacheBuilderSpec.toCacheBuilder().lenientParsing();
  }
  
  @Beta
  @GwtIncompatible("To be supported")
  public static CacheBuilder from(String paramString)
  {
    return from(CacheBuilderSpec.parse(paramString));
  }
  
  @GwtIncompatible("To be supported")
  CacheBuilder lenientParsing()
  {
    this.strictParsing = false;
    return this;
  }
  
  @GwtIncompatible("To be supported")
  CacheBuilder keyEquivalence(Equivalence paramEquivalence)
  {
    Preconditions.checkState(this.keyEquivalence == null, "key equivalence was already set to %s", new Object[] { this.keyEquivalence });
    this.keyEquivalence = ((Equivalence)Preconditions.checkNotNull(paramEquivalence));
    return this;
  }
  
  Equivalence getKeyEquivalence()
  {
    return (Equivalence)Objects.firstNonNull(this.keyEquivalence, getKeyStrength().defaultEquivalence());
  }
  
  @GwtIncompatible("To be supported")
  CacheBuilder valueEquivalence(Equivalence paramEquivalence)
  {
    Preconditions.checkState(this.valueEquivalence == null, "value equivalence was already set to %s", new Object[] { this.valueEquivalence });
    this.valueEquivalence = ((Equivalence)Preconditions.checkNotNull(paramEquivalence));
    return this;
  }
  
  Equivalence getValueEquivalence()
  {
    return (Equivalence)Objects.firstNonNull(this.valueEquivalence, getValueStrength().defaultEquivalence());
  }
  
  public CacheBuilder initialCapacity(int paramInt)
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
  
  public CacheBuilder concurrencyLevel(int paramInt)
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
  
  public CacheBuilder maximumSize(long paramLong)
  {
    Preconditions.checkState(this.maximumSize == -1L, "maximum size was already set to %s", new Object[] { Long.valueOf(this.maximumSize) });
    Preconditions.checkState(this.maximumWeight == -1L, "maximum weight was already set to %s", new Object[] { Long.valueOf(this.maximumWeight) });
    Preconditions.checkState(this.weigher == null, "maximum size can not be combined with weigher");
    Preconditions.checkArgument(paramLong >= 0L, "maximum size must not be negative");
    this.maximumSize = paramLong;
    return this;
  }
  
  @GwtIncompatible("To be supported")
  public CacheBuilder maximumWeight(long paramLong)
  {
    Preconditions.checkState(this.maximumWeight == -1L, "maximum weight was already set to %s", new Object[] { Long.valueOf(this.maximumWeight) });
    Preconditions.checkState(this.maximumSize == -1L, "maximum size was already set to %s", new Object[] { Long.valueOf(this.maximumSize) });
    this.maximumWeight = paramLong;
    Preconditions.checkArgument(paramLong >= 0L, "maximum weight must not be negative");
    return this;
  }
  
  @GwtIncompatible("To be supported")
  public CacheBuilder weigher(Weigher paramWeigher)
  {
    Preconditions.checkState(this.weigher == null);
    if (this.strictParsing) {
      Preconditions.checkState(this.maximumSize == -1L, "weigher can not be combined with maximum size", new Object[] { Long.valueOf(this.maximumSize) });
    }
    CacheBuilder localCacheBuilder = this;
    localCacheBuilder.weigher = ((Weigher)Preconditions.checkNotNull(paramWeigher));
    return localCacheBuilder;
  }
  
  long getMaximumWeight()
  {
    if ((this.expireAfterWriteNanos == 0L) || (this.expireAfterAccessNanos == 0L)) {
      return 0L;
    }
    return this.weigher == null ? this.maximumSize : this.maximumWeight;
  }
  
  Weigher getWeigher()
  {
    return (Weigher)Objects.firstNonNull(this.weigher, OneWeigher.INSTANCE);
  }
  
  @GwtIncompatible("java.lang.ref.WeakReference")
  public CacheBuilder weakKeys()
  {
    return setKeyStrength(LocalCache.Strength.WEAK);
  }
  
  CacheBuilder setKeyStrength(LocalCache.Strength paramStrength)
  {
    Preconditions.checkState(this.keyStrength == null, "Key strength was already set to %s", new Object[] { this.keyStrength });
    this.keyStrength = ((LocalCache.Strength)Preconditions.checkNotNull(paramStrength));
    return this;
  }
  
  LocalCache.Strength getKeyStrength()
  {
    return (LocalCache.Strength)Objects.firstNonNull(this.keyStrength, LocalCache.Strength.STRONG);
  }
  
  @GwtIncompatible("java.lang.ref.WeakReference")
  public CacheBuilder weakValues()
  {
    return setValueStrength(LocalCache.Strength.WEAK);
  }
  
  @GwtIncompatible("java.lang.ref.SoftReference")
  public CacheBuilder softValues()
  {
    return setValueStrength(LocalCache.Strength.SOFT);
  }
  
  CacheBuilder setValueStrength(LocalCache.Strength paramStrength)
  {
    Preconditions.checkState(this.valueStrength == null, "Value strength was already set to %s", new Object[] { this.valueStrength });
    this.valueStrength = ((LocalCache.Strength)Preconditions.checkNotNull(paramStrength));
    return this;
  }
  
  LocalCache.Strength getValueStrength()
  {
    return (LocalCache.Strength)Objects.firstNonNull(this.valueStrength, LocalCache.Strength.STRONG);
  }
  
  public CacheBuilder expireAfterWrite(long paramLong, TimeUnit paramTimeUnit)
  {
    Preconditions.checkState(this.expireAfterWriteNanos == -1L, "expireAfterWrite was already set to %s ns", new Object[] { Long.valueOf(this.expireAfterWriteNanos) });
    Preconditions.checkArgument(paramLong >= 0L, "duration cannot be negative: %s %s", new Object[] { Long.valueOf(paramLong), paramTimeUnit });
    this.expireAfterWriteNanos = paramTimeUnit.toNanos(paramLong);
    return this;
  }
  
  long getExpireAfterWriteNanos()
  {
    return this.expireAfterWriteNanos == -1L ? 0L : this.expireAfterWriteNanos;
  }
  
  public CacheBuilder expireAfterAccess(long paramLong, TimeUnit paramTimeUnit)
  {
    Preconditions.checkState(this.expireAfterAccessNanos == -1L, "expireAfterAccess was already set to %s ns", new Object[] { Long.valueOf(this.expireAfterAccessNanos) });
    Preconditions.checkArgument(paramLong >= 0L, "duration cannot be negative: %s %s", new Object[] { Long.valueOf(paramLong), paramTimeUnit });
    this.expireAfterAccessNanos = paramTimeUnit.toNanos(paramLong);
    return this;
  }
  
  long getExpireAfterAccessNanos()
  {
    return this.expireAfterAccessNanos == -1L ? 0L : this.expireAfterAccessNanos;
  }
  
  @Beta
  @GwtIncompatible("To be supported (synchronously).")
  public CacheBuilder refreshAfterWrite(long paramLong, TimeUnit paramTimeUnit)
  {
    Preconditions.checkNotNull(paramTimeUnit);
    Preconditions.checkState(this.refreshNanos == -1L, "refresh was already set to %s ns", new Object[] { Long.valueOf(this.refreshNanos) });
    Preconditions.checkArgument(paramLong > 0L, "duration must be positive: %s %s", new Object[] { Long.valueOf(paramLong), paramTimeUnit });
    this.refreshNanos = paramTimeUnit.toNanos(paramLong);
    return this;
  }
  
  long getRefreshNanos()
  {
    return this.refreshNanos == -1L ? 0L : this.refreshNanos;
  }
  
  public CacheBuilder ticker(Ticker paramTicker)
  {
    Preconditions.checkState(this.ticker == null);
    this.ticker = ((Ticker)Preconditions.checkNotNull(paramTicker));
    return this;
  }
  
  Ticker getTicker(boolean paramBoolean)
  {
    if (this.ticker != null) {
      return this.ticker;
    }
    return paramBoolean ? Ticker.systemTicker() : NULL_TICKER;
  }
  
  public CacheBuilder removalListener(RemovalListener paramRemovalListener)
  {
    Preconditions.checkState(this.removalListener == null);
    CacheBuilder localCacheBuilder = this;
    localCacheBuilder.removalListener = ((RemovalListener)Preconditions.checkNotNull(paramRemovalListener));
    return localCacheBuilder;
  }
  
  RemovalListener getRemovalListener()
  {
    return (RemovalListener)Objects.firstNonNull(this.removalListener, NullListener.INSTANCE);
  }
  
  public CacheBuilder recordStats()
  {
    this.statsCounterSupplier = CACHE_STATS_COUNTER;
    return this;
  }
  
  Supplier getStatsCounterSupplier()
  {
    return this.statsCounterSupplier;
  }
  
  public LoadingCache build(CacheLoader paramCacheLoader)
  {
    checkWeightWithWeigher();
    return new LocalCache.LocalLoadingCache(this, paramCacheLoader);
  }
  
  public Cache build()
  {
    checkWeightWithWeigher();
    checkNonLoadingCache();
    return new LocalCache.LocalManualCache(this);
  }
  
  private void checkNonLoadingCache()
  {
    Preconditions.checkState(this.refreshNanos == -1L, "refreshAfterWrite requires a LoadingCache");
  }
  
  private void checkWeightWithWeigher()
  {
    if (this.weigher == null) {
      Preconditions.checkState(this.maximumWeight == -1L, "maximumWeight requires weigher");
    } else if (this.strictParsing) {
      Preconditions.checkState(this.maximumWeight != -1L, "weigher requires maximumWeight");
    } else if (this.maximumWeight == -1L) {
      logger.log(Level.WARNING, "ignoring weigher specified without maximumWeight");
    }
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
    if (this.maximumSize != -1L) {
      localToStringHelper.add("maximumSize", this.maximumSize);
    }
    if (this.maximumWeight != -1L) {
      localToStringHelper.add("maximumWeight", this.maximumWeight);
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
    if (this.valueEquivalence != null) {
      localToStringHelper.addValue("valueEquivalence");
    }
    if (this.removalListener != null) {
      localToStringHelper.addValue("removalListener");
    }
    return localToStringHelper.toString();
  }
  
  static enum OneWeigher
    implements Weigher
  {
    INSTANCE;
    
    public int weigh(Object paramObject1, Object paramObject2)
    {
      return 1;
    }
  }
  
  static enum NullListener
    implements RemovalListener
  {
    INSTANCE;
    
    public void onRemoval(RemovalNotification paramRemovalNotification) {}
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\cache\CacheBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */