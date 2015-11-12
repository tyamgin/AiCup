package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.math.BigInteger;

@GwtCompatible(serializable=true)
public final class UnsignedLong
  extends Number
  implements Serializable, Comparable
{
  private static final long UNSIGNED_MASK = Long.MAX_VALUE;
  public static final UnsignedLong ZERO = new UnsignedLong(0L);
  public static final UnsignedLong ONE = new UnsignedLong(1L);
  public static final UnsignedLong MAX_VALUE = new UnsignedLong(-1L);
  private final long value;
  
  private UnsignedLong(long paramLong)
  {
    this.value = paramLong;
  }
  
  @Beta
  @Deprecated
  public static UnsignedLong asUnsigned(long paramLong)
  {
    return fromLongBits(paramLong);
  }
  
  public static UnsignedLong fromLongBits(long paramLong)
  {
    return new UnsignedLong(paramLong);
  }
  
  public static UnsignedLong valueOf(long paramLong)
  {
    Preconditions.checkArgument(paramLong >= 0L, "value (%s) is outside the range for an unsigned long value", new Object[] { Long.valueOf(paramLong) });
    return fromLongBits(paramLong);
  }
  
  public static UnsignedLong valueOf(BigInteger paramBigInteger)
  {
    Preconditions.checkNotNull(paramBigInteger);
    Preconditions.checkArgument((paramBigInteger.signum() >= 0) && (paramBigInteger.bitLength() <= 64), "value (%s) is outside the range for an unsigned long value", new Object[] { paramBigInteger });
    return fromLongBits(paramBigInteger.longValue());
  }
  
  public static UnsignedLong valueOf(String paramString)
  {
    return valueOf(paramString, 10);
  }
  
  public static UnsignedLong valueOf(String paramString, int paramInt)
  {
    return fromLongBits(UnsignedLongs.parseUnsignedLong(paramString, paramInt));
  }
  
  @Beta
  @Deprecated
  public UnsignedLong add(UnsignedLong paramUnsignedLong)
  {
    return plus(paramUnsignedLong);
  }
  
  public UnsignedLong plus(UnsignedLong paramUnsignedLong)
  {
    return fromLongBits(this.value + ((UnsignedLong)Preconditions.checkNotNull(paramUnsignedLong)).value);
  }
  
  @Beta
  @Deprecated
  public UnsignedLong subtract(UnsignedLong paramUnsignedLong)
  {
    return minus(paramUnsignedLong);
  }
  
  public UnsignedLong minus(UnsignedLong paramUnsignedLong)
  {
    return fromLongBits(this.value - ((UnsignedLong)Preconditions.checkNotNull(paramUnsignedLong)).value);
  }
  
  @Beta
  @Deprecated
  public UnsignedLong multiply(UnsignedLong paramUnsignedLong)
  {
    return times(paramUnsignedLong);
  }
  
  public UnsignedLong times(UnsignedLong paramUnsignedLong)
  {
    return fromLongBits(this.value * ((UnsignedLong)Preconditions.checkNotNull(paramUnsignedLong)).value);
  }
  
  @Beta
  @Deprecated
  public UnsignedLong divide(UnsignedLong paramUnsignedLong)
  {
    return dividedBy(paramUnsignedLong);
  }
  
  public UnsignedLong dividedBy(UnsignedLong paramUnsignedLong)
  {
    return fromLongBits(UnsignedLongs.divide(this.value, ((UnsignedLong)Preconditions.checkNotNull(paramUnsignedLong)).value));
  }
  
  @Beta
  @Deprecated
  public UnsignedLong remainder(UnsignedLong paramUnsignedLong)
  {
    return mod(paramUnsignedLong);
  }
  
  public UnsignedLong mod(UnsignedLong paramUnsignedLong)
  {
    return fromLongBits(UnsignedLongs.remainder(this.value, ((UnsignedLong)Preconditions.checkNotNull(paramUnsignedLong)).value));
  }
  
  public int intValue()
  {
    return (int)this.value;
  }
  
  public long longValue()
  {
    return this.value;
  }
  
  public float floatValue()
  {
    float f = (float)(this.value & 0x7FFFFFFFFFFFFFFF);
    if (this.value < 0L) {
      f += 9.223372E18F;
    }
    return f;
  }
  
  public double doubleValue()
  {
    double d = this.value & 0x7FFFFFFFFFFFFFFF;
    if (this.value < 0L) {
      d += 9.223372036854776E18D;
    }
    return d;
  }
  
  public BigInteger bigIntegerValue()
  {
    BigInteger localBigInteger = BigInteger.valueOf(this.value & 0x7FFFFFFFFFFFFFFF);
    if (this.value < 0L) {
      localBigInteger = localBigInteger.setBit(63);
    }
    return localBigInteger;
  }
  
  public int compareTo(UnsignedLong paramUnsignedLong)
  {
    Preconditions.checkNotNull(paramUnsignedLong);
    return UnsignedLongs.compare(this.value, paramUnsignedLong.value);
  }
  
  public int hashCode()
  {
    return Longs.hashCode(this.value);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof UnsignedLong))
    {
      UnsignedLong localUnsignedLong = (UnsignedLong)paramObject;
      return this.value == localUnsignedLong.value;
    }
    return false;
  }
  
  public String toString()
  {
    return UnsignedLongs.toString(this.value);
  }
  
  public String toString(int paramInt)
  {
    return UnsignedLongs.toString(this.value, paramInt);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\primitives\UnsignedLong.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */