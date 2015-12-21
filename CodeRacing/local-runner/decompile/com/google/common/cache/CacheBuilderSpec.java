package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Beta
public final class CacheBuilderSpec
{
  private static final Splitter KEYS_SPLITTER = Splitter.on(',').trimResults();
  private static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').trimResults();
  private static final ImmutableMap VALUE_PARSERS = ImmutableMap.builder().put("initialCapacity", new InitialCapacityParser()).put("maximumSize", new MaximumSizeParser()).put("maximumWeight", new MaximumWeightParser()).put("concurrencyLevel", new ConcurrencyLevelParser()).put("weakKeys", new KeyStrengthParser(LocalCache.Strength.WEAK)).put("softValues", new ValueStrengthParser(LocalCache.Strength.SOFT)).put("weakValues", new ValueStrengthParser(LocalCache.Strength.WEAK)).put("expireAfterAccess", new AccessDurationParser()).put("expireAfterWrite", new WriteDurationParser()).put("refreshAfterWrite", new RefreshDurationParser()).put("refreshInterval", new RefreshDurationParser()).build();
  @VisibleForTesting
  Integer initialCapacity;
  @VisibleForTesting
  Long maximumSize;
  @VisibleForTesting
  Long maximumWeight;
  @VisibleForTesting
  Integer concurrencyLevel;
  @VisibleForTesting
  LocalCache.Strength keyStrength;
  @VisibleForTesting
  LocalCache.Strength valueStrength;
  @VisibleForTesting
  long writeExpirationDuration;
  @VisibleForTesting
  TimeUnit writeExpirationTimeUnit;
  @VisibleForTesting
  long accessExpirationDuration;
  @VisibleForTesting
  TimeUnit accessExpirationTimeUnit;
  @VisibleForTesting
  long refreshDuration;
  @VisibleForTesting
  TimeUnit refreshTimeUnit;
  private final String specification;
  
  private CacheBuilderSpec(String paramString)
  {
    this.specification = paramString;
  }
  
  public static CacheBuilderSpec parse(String paramString)
  {
    CacheBuilderSpec localCacheBuilderSpec = new CacheBuilderSpec(paramString);
    if (!paramString.isEmpty())
    {
      Iterator localIterator = KEYS_SPLITTER.split(paramString).iterator();
      while (localIterator.hasNext())
      {
        String str1 = (String)localIterator.next();
        ImmutableList localImmutableList = ImmutableList.copyOf(KEY_VALUE_SPLITTER.split(str1));
        Preconditions.checkArgument(!localImmutableList.isEmpty(), "blank key-value pair");
        Preconditions.checkArgument(localImmutableList.size() <= 2, "key-value pair %s with more than one equals sign", new Object[] { str1 });
        String str2 = (String)localImmutableList.get(0);
        ValueParser localValueParser = (ValueParser)VALUE_PARSERS.get(str2);
        Preconditions.checkArgument(localValueParser != null, "unknown key %s", new Object[] { str2 });
        String str3 = localImmutableList.size() == 1 ? null : (String)localImmutableList.get(1);
        localValueParser.parse(localCacheBuilderSpec, str2, str3);
      }
    }
    return localCacheBuilderSpec;
  }
  
  public static CacheBuilderSpec disableCaching()
  {
    return parse("maximumSize=0");
  }
  
  CacheBuilder toCacheBuilder()
  {
    CacheBuilder localCacheBuilder = CacheBuilder.newBuilder();
    if (this.initialCapacity != null) {
      localCacheBuilder.initialCapacity(this.initialCapacity.intValue());
    }
    if (this.maximumSize != null) {
      localCacheBuilder.maximumSize(this.maximumSize.longValue());
    }
    if (this.maximumWeight != null) {
      localCacheBuilder.maximumWeight(this.maximumWeight.longValue());
    }
    if (this.concurrencyLevel != null) {
      localCacheBuilder.concurrencyLevel(this.concurrencyLevel.intValue());
    }
    if (this.keyStrength != null) {
      switch (this.keyStrength)
      {
      case WEAK: 
        localCacheBuilder.weakKeys();
        break;
      default: 
        throw new AssertionError();
      }
    }
    if (this.valueStrength != null) {
      switch (this.valueStrength)
      {
      case SOFT: 
        localCacheBuilder.softValues();
        break;
      case WEAK: 
        localCacheBuilder.weakValues();
        break;
      default: 
        throw new AssertionError();
      }
    }
    if (this.writeExpirationTimeUnit != null) {
      localCacheBuilder.expireAfterWrite(this.writeExpirationDuration, this.writeExpirationTimeUnit);
    }
    if (this.accessExpirationTimeUnit != null) {
      localCacheBuilder.expireAfterAccess(this.accessExpirationDuration, this.accessExpirationTimeUnit);
    }
    if (this.refreshTimeUnit != null) {
      localCacheBuilder.refreshAfterWrite(this.refreshDuration, this.refreshTimeUnit);
    }
    return localCacheBuilder;
  }
  
  public String toParsableString()
  {
    return this.specification;
  }
  
  public String toString()
  {
    return Objects.toStringHelper(this).addValue(toParsableString()).toString();
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { this.initialCapacity, this.maximumSize, this.maximumWeight, this.concurrencyLevel, this.keyStrength, this.valueStrength, durationInNanos(this.writeExpirationDuration, this.writeExpirationTimeUnit), durationInNanos(this.accessExpirationDuration, this.accessExpirationTimeUnit), durationInNanos(this.refreshDuration, this.refreshTimeUnit) });
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof CacheBuilderSpec)) {
      return false;
    }
    CacheBuilderSpec localCacheBuilderSpec = (CacheBuilderSpec)paramObject;
    return (Objects.equal(this.initialCapacity, localCacheBuilderSpec.initialCapacity)) && (Objects.equal(this.maximumSize, localCacheBuilderSpec.maximumSize)) && (Objects.equal(this.maximumWeight, localCacheBuilderSpec.maximumWeight)) && (Objects.equal(this.concurrencyLevel, localCacheBuilderSpec.concurrencyLevel)) && (Objects.equal(this.keyStrength, localCacheBuilderSpec.keyStrength)) && (Objects.equal(this.valueStrength, localCacheBuilderSpec.valueStrength)) && (Objects.equal(durationInNanos(this.writeExpirationDuration, this.writeExpirationTimeUnit), durationInNanos(localCacheBuilderSpec.writeExpirationDuration, localCacheBuilderSpec.writeExpirationTimeUnit))) && (Objects.equal(durationInNanos(this.accessExpirationDuration, this.accessExpirationTimeUnit), durationInNanos(localCacheBuilderSpec.accessExpirationDuration, localCacheBuilderSpec.accessExpirationTimeUnit))) && (Objects.equal(durationInNanos(this.refreshDuration, this.refreshTimeUnit), durationInNanos(localCacheBuilderSpec.refreshDuration, localCacheBuilderSpec.refreshTimeUnit)));
  }
  
  private static Long durationInNanos(long paramLong, TimeUnit paramTimeUnit)
  {
    return paramTimeUnit == null ? null : Long.valueOf(paramTimeUnit.toNanos(paramLong));
  }
  
  static class RefreshDurationParser
    extends CacheBuilderSpec.DurationParser
  {
    protected void parseDuration(CacheBuilderSpec paramCacheBuilderSpec, long paramLong, TimeUnit paramTimeUnit)
    {
      Preconditions.checkArgument(paramCacheBuilderSpec.refreshTimeUnit == null, "refreshAfterWrite already set");
      paramCacheBuilderSpec.refreshDuration = paramLong;
      paramCacheBuilderSpec.refreshTimeUnit = paramTimeUnit;
    }
  }
  
  static class WriteDurationParser
    extends CacheBuilderSpec.DurationParser
  {
    protected void parseDuration(CacheBuilderSpec paramCacheBuilderSpec, long paramLong, TimeUnit paramTimeUnit)
    {
      Preconditions.checkArgument(paramCacheBuilderSpec.writeExpirationTimeUnit == null, "expireAfterWrite already set");
      paramCacheBuilderSpec.writeExpirationDuration = paramLong;
      paramCacheBuilderSpec.writeExpirationTimeUnit = paramTimeUnit;
    }
  }
  
  static class AccessDurationParser
    extends CacheBuilderSpec.DurationParser
  {
    protected void parseDuration(CacheBuilderSpec paramCacheBuilderSpec, long paramLong, TimeUnit paramTimeUnit)
    {
      Preconditions.checkArgument(paramCacheBuilderSpec.accessExpirationTimeUnit == null, "expireAfterAccess already set");
      paramCacheBuilderSpec.accessExpirationDuration = paramLong;
      paramCacheBuilderSpec.accessExpirationTimeUnit = paramTimeUnit;
    }
  }
  
  static abstract class DurationParser
    implements CacheBuilderSpec.ValueParser
  {
    protected abstract void parseDuration(CacheBuilderSpec paramCacheBuilderSpec, long paramLong, TimeUnit paramTimeUnit);
    
    public void parse(CacheBuilderSpec paramCacheBuilderSpec, String paramString1, String paramString2)
    {
      Preconditions.checkArgument((paramString2 != null) && (!paramString2.isEmpty()), "value of key %s omitted", new Object[] { paramString1 });
      try
      {
        int i = paramString2.charAt(paramString2.length() - 1);
        TimeUnit localTimeUnit;
        switch (i)
        {
        case 100: 
          localTimeUnit = TimeUnit.DAYS;
          break;
        case 104: 
          localTimeUnit = TimeUnit.HOURS;
          break;
        case 109: 
          localTimeUnit = TimeUnit.MINUTES;
          break;
        case 115: 
          localTimeUnit = TimeUnit.SECONDS;
          break;
        default: 
          throw new IllegalArgumentException(String.format("key %s invalid format.  was %s, must end with one of [dDhHmMsS]", new Object[] { paramString1, paramString2 }));
        }
        long l = Long.parseLong(paramString2.substring(0, paramString2.length() - 1));
        parseDuration(paramCacheBuilderSpec, l, localTimeUnit);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new IllegalArgumentException(String.format("key %s value set to %s, must be integer", new Object[] { paramString1, paramString2 }));
      }
    }
  }
  
  static class ValueStrengthParser
    implements CacheBuilderSpec.ValueParser
  {
    private final LocalCache.Strength strength;
    
    public ValueStrengthParser(LocalCache.Strength paramStrength)
    {
      this.strength = paramStrength;
    }
    
    public void parse(CacheBuilderSpec paramCacheBuilderSpec, String paramString1, String paramString2)
    {
      Preconditions.checkArgument(paramString2 == null, "key %s does not take values", new Object[] { paramString1 });
      Preconditions.checkArgument(paramCacheBuilderSpec.valueStrength == null, "%s was already set to %s", new Object[] { paramString1, paramCacheBuilderSpec.valueStrength });
      paramCacheBuilderSpec.valueStrength = this.strength;
    }
  }
  
  static class KeyStrengthParser
    implements CacheBuilderSpec.ValueParser
  {
    private final LocalCache.Strength strength;
    
    public KeyStrengthParser(LocalCache.Strength paramStrength)
    {
      this.strength = paramStrength;
    }
    
    public void parse(CacheBuilderSpec paramCacheBuilderSpec, String paramString1, String paramString2)
    {
      Preconditions.checkArgument(paramString2 == null, "key %s does not take values", new Object[] { paramString1 });
      Preconditions.checkArgument(paramCacheBuilderSpec.keyStrength == null, "%s was already set to %s", new Object[] { paramString1, paramCacheBuilderSpec.keyStrength });
      paramCacheBuilderSpec.keyStrength = this.strength;
    }
  }
  
  static class ConcurrencyLevelParser
    extends CacheBuilderSpec.IntegerParser
  {
    protected void parseInteger(CacheBuilderSpec paramCacheBuilderSpec, int paramInt)
    {
      Preconditions.checkArgument(paramCacheBuilderSpec.concurrencyLevel == null, "concurrency level was already set to ", new Object[] { paramCacheBuilderSpec.concurrencyLevel });
      paramCacheBuilderSpec.concurrencyLevel = Integer.valueOf(paramInt);
    }
  }
  
  static class MaximumWeightParser
    extends CacheBuilderSpec.LongParser
  {
    protected void parseLong(CacheBuilderSpec paramCacheBuilderSpec, long paramLong)
    {
      Preconditions.checkArgument(paramCacheBuilderSpec.maximumWeight == null, "maximum weight was already set to ", new Object[] { paramCacheBuilderSpec.maximumWeight });
      Preconditions.checkArgument(paramCacheBuilderSpec.maximumSize == null, "maximum size was already set to ", new Object[] { paramCacheBuilderSpec.maximumSize });
      paramCacheBuilderSpec.maximumWeight = Long.valueOf(paramLong);
    }
  }
  
  static class MaximumSizeParser
    extends CacheBuilderSpec.LongParser
  {
    protected void parseLong(CacheBuilderSpec paramCacheBuilderSpec, long paramLong)
    {
      Preconditions.checkArgument(paramCacheBuilderSpec.maximumSize == null, "maximum size was already set to ", new Object[] { paramCacheBuilderSpec.maximumSize });
      Preconditions.checkArgument(paramCacheBuilderSpec.maximumWeight == null, "maximum weight was already set to ", new Object[] { paramCacheBuilderSpec.maximumWeight });
      paramCacheBuilderSpec.maximumSize = Long.valueOf(paramLong);
    }
  }
  
  static class InitialCapacityParser
    extends CacheBuilderSpec.IntegerParser
  {
    protected void parseInteger(CacheBuilderSpec paramCacheBuilderSpec, int paramInt)
    {
      Preconditions.checkArgument(paramCacheBuilderSpec.initialCapacity == null, "initial capacity was already set to ", new Object[] { paramCacheBuilderSpec.initialCapacity });
      paramCacheBuilderSpec.initialCapacity = Integer.valueOf(paramInt);
    }
  }
  
  static abstract class LongParser
    implements CacheBuilderSpec.ValueParser
  {
    protected abstract void parseLong(CacheBuilderSpec paramCacheBuilderSpec, long paramLong);
    
    public void parse(CacheBuilderSpec paramCacheBuilderSpec, String paramString1, String paramString2)
    {
      Preconditions.checkArgument((paramString2 != null) && (!paramString2.isEmpty()), "value of key %s omitted", new Object[] { paramString1 });
      try
      {
        parseLong(paramCacheBuilderSpec, Long.parseLong(paramString2));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new IllegalArgumentException(String.format("key %s value set to %s, must be integer", new Object[] { paramString1, paramString2 }), localNumberFormatException);
      }
    }
  }
  
  static abstract class IntegerParser
    implements CacheBuilderSpec.ValueParser
  {
    protected abstract void parseInteger(CacheBuilderSpec paramCacheBuilderSpec, int paramInt);
    
    public void parse(CacheBuilderSpec paramCacheBuilderSpec, String paramString1, String paramString2)
    {
      Preconditions.checkArgument((paramString2 != null) && (!paramString2.isEmpty()), "value of key %s omitted", new Object[] { paramString1 });
      try
      {
        parseInteger(paramCacheBuilderSpec, Integer.parseInt(paramString2));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new IllegalArgumentException(String.format("key %s value set to %s, must be integer", new Object[] { paramString1, paramString2 }), localNumberFormatException);
      }
    }
  }
  
  private static abstract interface ValueParser
  {
    public abstract void parse(CacheBuilderSpec paramCacheBuilderSpec, String paramString1, String paramString2);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\cache\CacheBuilderSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */