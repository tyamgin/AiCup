package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.math.BigInteger;

@GwtCompatible(emulated=true)
public final class UnsignedInteger
  extends Number
  implements Comparable
{
  public static final UnsignedInteger ZERO = asUnsigned(0);
  public static final UnsignedInteger ONE = asUnsigned(1);
  public static final UnsignedInteger MAX_VALUE = asUnsigned(-1);
  private final int value;
  
  private UnsignedInteger(int paramInt)
  {
    this.value = (paramInt & 0xFFFFFFFF);
  }
  
  @Beta
  @Deprecated
  public static UnsignedInteger asUnsigned(int paramInt)
  {
    return fromIntBits(paramInt);
  }
  
  public static UnsignedInteger fromIntBits(int paramInt)
  {
    return new UnsignedInteger(paramInt);
  }
  
  public static UnsignedInteger valueOf(long paramLong)
  {
    Preconditions.checkArgument((paramLong & 0xFFFFFFFF) == paramLong, "value (%s) is outside the range for an unsigned integer value", new Object[] { Long.valueOf(paramLong) });
    return fromIntBits((int)paramLong);
  }
  
  public static UnsignedInteger valueOf(BigInteger paramBigInteger)
  {
    Preconditions.checkNotNull(paramBigInteger);
    Preconditions.checkArgument((paramBigInteger.signum() >= 0) && (paramBigInteger.bitLength() <= 32), "value (%s) is outside the range for an unsigned integer value", new Object[] { paramBigInteger });
    return fromIntBits(paramBigInteger.intValue());
  }
  
  public static UnsignedInteger valueOf(String paramString)
  {
    return valueOf(paramString, 10);
  }
  
  public static UnsignedInteger valueOf(String paramString, int paramInt)
  {
    return fromIntBits(UnsignedInts.parseUnsignedInt(paramString, paramInt));
  }
  
  @Beta
  @Deprecated
  public UnsignedInteger add(UnsignedInteger paramUnsignedInteger)
  {
    return plus(paramUnsignedInteger);
  }
  
  public UnsignedInteger plus(UnsignedInteger paramUnsignedInteger)
  {
    return fromIntBits(this.value + ((UnsignedInteger)Preconditions.checkNotNull(paramUnsignedInteger)).value);
  }
  
  @Beta
  @Deprecated
  public UnsignedInteger subtract(UnsignedInteger paramUnsignedInteger)
  {
    return minus(paramUnsignedInteger);
  }
  
  public UnsignedInteger minus(UnsignedInteger paramUnsignedInteger)
  {
    return fromIntBits(this.value - ((UnsignedInteger)Preconditions.checkNotNull(paramUnsignedInteger)).value);
  }
  
  @Beta
  @GwtIncompatible("Does not truncate correctly")
  @Deprecated
  public UnsignedInteger multiply(UnsignedInteger paramUnsignedInteger)
  {
    return times(paramUnsignedInteger);
  }
  
  @GwtIncompatible("Does not truncate correctly")
  public UnsignedInteger times(UnsignedInteger paramUnsignedInteger)
  {
    return fromIntBits(this.value * ((UnsignedInteger)Preconditions.checkNotNull(paramUnsignedInteger)).value);
  }
  
  @Beta
  @Deprecated
  public UnsignedInteger divide(UnsignedInteger paramUnsignedInteger)
  {
    return dividedBy(paramUnsignedInteger);
  }
  
  public UnsignedInteger dividedBy(UnsignedInteger paramUnsignedInteger)
  {
    return fromIntBits(UnsignedInts.divide(this.value, ((UnsignedInteger)Preconditions.checkNotNull(paramUnsignedInteger)).value));
  }
  
  @Beta
  @Deprecated
  public UnsignedInteger remainder(UnsignedInteger paramUnsignedInteger)
  {
    return mod(paramUnsignedInteger);
  }
  
  public UnsignedInteger mod(UnsignedInteger paramUnsignedInteger)
  {
    return fromIntBits(UnsignedInts.remainder(this.value, ((UnsignedInteger)Preconditions.checkNotNull(paramUnsignedInteger)).value));
  }
  
  public int intValue()
  {
    return this.value;
  }
  
  public long longValue()
  {
    return UnsignedInts.toLong(this.value);
  }
  
  public float floatValue()
  {
    return (float)longValue();
  }
  
  public double doubleValue()
  {
    return longValue();
  }
  
  public BigInteger bigIntegerValue()
  {
    return BigInteger.valueOf(longValue());
  }
  
  public int compareTo(UnsignedInteger paramUnsignedInteger)
  {
    Preconditions.checkNotNull(paramUnsignedInteger);
    return UnsignedInts.compare(this.value, paramUnsignedInteger.value);
  }
  
  public int hashCode()
  {
    return this.value;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof UnsignedInteger))
    {
      UnsignedInteger localUnsignedInteger = (UnsignedInteger)paramObject;
      return this.value == localUnsignedInteger.value;
    }
    return false;
  }
  
  public String toString()
  {
    return toString(10);
  }
  
  public String toString(int paramInt)
  {
    return UnsignedInts.toString(this.value, paramInt);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\primitives\UnsignedInteger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */