package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.io.Serializable;

@Beta
public final class BloomFilter
  implements Predicate, Serializable
{
  private final BloomFilterStrategies.BitArray bits;
  private final int numHashFunctions;
  private final Funnel funnel;
  private final Strategy strategy;
  
  private BloomFilter(BloomFilterStrategies.BitArray paramBitArray, int paramInt, Funnel paramFunnel, Strategy paramStrategy)
  {
    Preconditions.checkArgument(paramInt > 0, "numHashFunctions (%s) must be > 0", new Object[] { Integer.valueOf(paramInt) });
    Preconditions.checkArgument(paramInt <= 255, "numHashFunctions (%s) must be <= 255", new Object[] { Integer.valueOf(paramInt) });
    this.bits = ((BloomFilterStrategies.BitArray)Preconditions.checkNotNull(paramBitArray));
    this.numHashFunctions = paramInt;
    this.funnel = ((Funnel)Preconditions.checkNotNull(paramFunnel));
    this.strategy = ((Strategy)Preconditions.checkNotNull(paramStrategy));
  }
  
  public BloomFilter copy()
  {
    return new BloomFilter(this.bits.copy(), this.numHashFunctions, this.funnel, this.strategy);
  }
  
  public boolean mightContain(Object paramObject)
  {
    return this.strategy.mightContain(paramObject, this.funnel, this.numHashFunctions, this.bits);
  }
  
  public boolean apply(Object paramObject)
  {
    return mightContain(paramObject);
  }
  
  public boolean put(Object paramObject)
  {
    return this.strategy.put(paramObject, this.funnel, this.numHashFunctions, this.bits);
  }
  
  public double expectedFpp()
  {
    return Math.pow(this.bits.bitCount() / this.bits.size(), this.numHashFunctions);
  }
  
  @Deprecated
  public double expectedFalsePositiveProbability()
  {
    return expectedFpp();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof BloomFilter))
    {
      BloomFilter localBloomFilter = (BloomFilter)paramObject;
      return (this.numHashFunctions == localBloomFilter.numHashFunctions) && (this.funnel.equals(localBloomFilter.funnel)) && (this.bits.equals(localBloomFilter.bits)) && (this.strategy.equals(localBloomFilter.strategy));
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { Integer.valueOf(this.numHashFunctions), this.funnel, this.strategy, this.bits });
  }
  
  public static BloomFilter create(Funnel paramFunnel, int paramInt, double paramDouble)
  {
    Preconditions.checkNotNull(paramFunnel);
    Preconditions.checkArgument(paramInt >= 0, "Expected insertions (%s) must be >= 0", new Object[] { Integer.valueOf(paramInt) });
    Preconditions.checkArgument(paramDouble > 0.0D, "False positive probability (%s) must be > 0.0", new Object[] { Double.valueOf(paramDouble) });
    Preconditions.checkArgument(paramDouble < 1.0D, "False positive probability (%s) must be < 1.0", new Object[] { Double.valueOf(paramDouble) });
    if (paramInt == 0) {
      paramInt = 1;
    }
    long l = optimalNumOfBits(paramInt, paramDouble);
    int i = optimalNumOfHashFunctions(paramInt, l);
    try
    {
      return new BloomFilter(new BloomFilterStrategies.BitArray(l), i, paramFunnel, BloomFilterStrategies.MURMUR128_MITZ_32);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new IllegalArgumentException("Could not create BloomFilter of " + l + " bits", localIllegalArgumentException);
    }
  }
  
  public static BloomFilter create(Funnel paramFunnel, int paramInt)
  {
    return create(paramFunnel, paramInt, 0.03D);
  }
  
  @VisibleForTesting
  static int optimalNumOfHashFunctions(long paramLong1, long paramLong2)
  {
    return Math.max(1, (int)Math.round(paramLong2 / paramLong1 * Math.log(2.0D)));
  }
  
  @VisibleForTesting
  static long optimalNumOfBits(long paramLong, double paramDouble)
  {
    if (paramDouble == 0.0D) {
      paramDouble = Double.MIN_VALUE;
    }
    return (-paramLong * Math.log(paramDouble) / (Math.log(2.0D) * Math.log(2.0D)));
  }
  
  private Object writeReplace()
  {
    return new SerialForm(this);
  }
  
  private static class SerialForm
    implements Serializable
  {
    final long[] data;
    final int numHashFunctions;
    final Funnel funnel;
    final BloomFilter.Strategy strategy;
    private static final long serialVersionUID = 1L;
    
    SerialForm(BloomFilter paramBloomFilter)
    {
      this.data = paramBloomFilter.bits.data;
      this.numHashFunctions = paramBloomFilter.numHashFunctions;
      this.funnel = paramBloomFilter.funnel;
      this.strategy = paramBloomFilter.strategy;
    }
    
    Object readResolve()
    {
      return new BloomFilter(new BloomFilterStrategies.BitArray(this.data), this.numHashFunctions, this.funnel, this.strategy, null);
    }
  }
  
  static abstract interface Strategy
    extends Serializable
  {
    public abstract boolean put(Object paramObject, Funnel paramFunnel, int paramInt, BloomFilterStrategies.BitArray paramBitArray);
    
    public abstract boolean mightContain(Object paramObject, Funnel paramFunnel, int paramInt, BloomFilterStrategies.BitArray paramBitArray);
    
    public abstract int ordinal();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\hash\BloomFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */