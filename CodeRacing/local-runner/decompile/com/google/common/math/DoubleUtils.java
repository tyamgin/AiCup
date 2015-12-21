package com.google.common.math;

import com.google.common.base.Preconditions;
import java.math.BigInteger;

final class DoubleUtils
{
  static final long SIGNIFICAND_MASK = 4503599627370495L;
  static final long EXPONENT_MASK = 9218868437227405312L;
  static final long SIGN_MASK = Long.MIN_VALUE;
  static final int SIGNIFICAND_BITS = 52;
  static final int EXPONENT_BIAS = 1023;
  static final long IMPLICIT_BIT = 4503599627370496L;
  private static final long ONE_BITS = Double.doubleToRawLongBits(1.0D);
  
  static double nextDown(double paramDouble)
  {
    return -Math.nextUp(-paramDouble);
  }
  
  static long getSignificand(double paramDouble)
  {
    Preconditions.checkArgument(isFinite(paramDouble), "not a normal value");
    int i = Math.getExponent(paramDouble);
    long l = Double.doubleToRawLongBits(paramDouble);
    l &= 0xFFFFFFFFFFFFF;
    return i == 64513 ? l << 1 : l | 0x10000000000000;
  }
  
  static boolean isFinite(double paramDouble)
  {
    return Math.getExponent(paramDouble) <= 1023;
  }
  
  static boolean isNormal(double paramDouble)
  {
    return Math.getExponent(paramDouble) >= 64514;
  }
  
  static double scaleNormalize(double paramDouble)
  {
    long l = Double.doubleToRawLongBits(paramDouble) & 0xFFFFFFFFFFFFF;
    return Double.longBitsToDouble(l | ONE_BITS);
  }
  
  static double bigToDouble(BigInteger paramBigInteger)
  {
    BigInteger localBigInteger = paramBigInteger.abs();
    int i = localBigInteger.bitLength() - 1;
    if (i < 63) {
      return paramBigInteger.longValue();
    }
    if (i > 1023) {
      return paramBigInteger.signum() * Double.POSITIVE_INFINITY;
    }
    int j = i - 52 - 1;
    long l1 = localBigInteger.shiftRight(j).longValue();
    long l2 = l1 >> 1;
    l2 &= 0xFFFFFFFFFFFFF;
    int k = ((l1 & 1L) != 0L) && (((l2 & 1L) != 0L) || (localBigInteger.getLowestSetBit() < j)) ? 1 : 0;
    long l3 = k != 0 ? l2 + 1L : l2;
    long l4 = i + 1023 << 52;
    l4 += l3;
    l4 |= paramBigInteger.signum() & 0x8000000000000000;
    return Double.longBitsToDouble(l4);
  }
  
  static double ensureNonNegative(double paramDouble)
  {
    Preconditions.checkArgument(!Double.isNaN(paramDouble));
    if (paramDouble > 0.0D) {
      return paramDouble;
    }
    return 0.0D;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\math\DoubleUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */