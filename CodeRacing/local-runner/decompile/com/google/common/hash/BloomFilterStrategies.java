package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.math.LongMath;
import com.google.common.primitives.Ints;
import java.math.RoundingMode;
import java.util.Arrays;

 enum BloomFilterStrategies
  implements BloomFilter.Strategy
{
  MURMUR128_MITZ_32;
  
  static class BitArray
  {
    final long[] data;
    int bitCount;
    
    BitArray(long paramLong)
    {
      this(new long[Ints.checkedCast(LongMath.divide(paramLong, 64L, RoundingMode.CEILING))]);
    }
    
    BitArray(long[] paramArrayOfLong)
    {
      Preconditions.checkArgument(paramArrayOfLong.length > 0, "data length is zero!");
      this.data = paramArrayOfLong;
      int i = 0;
      for (long l : paramArrayOfLong) {
        i += Long.bitCount(l);
      }
      this.bitCount = i;
    }
    
    boolean set(int paramInt)
    {
      if (!get(paramInt))
      {
        this.data[(paramInt >> 6)] |= 1L << paramInt;
        this.bitCount += 1;
        return true;
      }
      return false;
    }
    
    boolean get(int paramInt)
    {
      return (this.data[(paramInt >> 6)] & 1L << paramInt) != 0L;
    }
    
    int size()
    {
      return this.data.length * 64;
    }
    
    int bitCount()
    {
      return this.bitCount;
    }
    
    BitArray copy()
    {
      return new BitArray((long[])this.data.clone());
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof BitArray))
      {
        BitArray localBitArray = (BitArray)paramObject;
        return Arrays.equals(this.data, localBitArray.data);
      }
      return false;
    }
    
    public int hashCode()
    {
      return Arrays.hashCode(this.data);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\hash\BloomFilterStrategies.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */