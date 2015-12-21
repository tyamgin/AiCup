package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.math.RoundingMode;

@GwtCompatible(emulated=true)
public final class LongMath
{
  @VisibleForTesting
  static final long MAX_POWER_OF_SQRT2_UNSIGNED = -5402926248376769404L;
  @VisibleForTesting
  static final byte[] maxLog10ForLeadingZeros = { 19, 18, 18, 18, 18, 17, 17, 17, 16, 16, 16, 15, 15, 15, 15, 14, 14, 14, 13, 13, 13, 12, 12, 12, 12, 11, 11, 11, 10, 10, 10, 9, 9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0 };
  @GwtIncompatible("TODO")
  @VisibleForTesting
  static final long[] powersOf10 = { 1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L };
  @GwtIncompatible("TODO")
  @VisibleForTesting
  static final long[] halfPowersOf10 = { 3L, 31L, 316L, 3162L, 31622L, 316227L, 3162277L, 31622776L, 316227766L, 3162277660L, 31622776601L, 316227766016L, 3162277660168L, 31622776601683L, 316227766016837L, 3162277660168379L, 31622776601683793L, 316227766016837933L, 3162277660168379331L };
  @GwtIncompatible("TODO")
  @VisibleForTesting
  static final long FLOOR_SQRT_MAX_LONG = 3037000499L;
  static final long[] factorials = { 1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L };
  static final int[] biggestBinomials = { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3810779, 121977, 16175, 4337, 1733, 887, 534, 361, 265, 206, 169, 143, 125, 111, 101, 94, 88, 83, 79, 76, 74, 72, 70, 69, 68, 67, 67, 66, 66, 66, 66 };
  @VisibleForTesting
  static final int[] biggestSimpleBinomials = { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2642246, 86251, 11724, 3218, 1313, 684, 419, 287, 214, 169, 139, 119, 105, 95, 87, 81, 76, 73, 70, 68, 66, 64, 63, 62, 62, 61, 61, 61 };
  
  public static boolean isPowerOfTwo(long paramLong)
  {
    return (paramLong > 0L ? 1 : 0) & ((paramLong & paramLong - 1L) == 0L ? 1 : 0);
  }
  
  public static int log2(long paramLong, RoundingMode paramRoundingMode)
  {
    MathPreconditions.checkPositive("x", paramLong);
    switch (paramRoundingMode)
    {
    case UNNECESSARY: 
      MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(paramLong));
    case DOWN: 
    case FLOOR: 
      return 63 - Long.numberOfLeadingZeros(paramLong);
    case UP: 
    case CEILING: 
      return 64 - Long.numberOfLeadingZeros(paramLong - 1L);
    case HALF_DOWN: 
    case HALF_UP: 
    case HALF_EVEN: 
      int i = Long.numberOfLeadingZeros(paramLong);
      long l = -5402926248376769404L >>> i;
      int j = 63 - i;
      return paramLong <= l ? j : j + 1;
    }
    throw new AssertionError("impossible");
  }
  
  @GwtIncompatible("TODO")
  public static int log10(long paramLong, RoundingMode paramRoundingMode)
  {
    MathPreconditions.checkPositive("x", paramLong);
    if (fitsInInt(paramLong)) {
      return IntMath.log10((int)paramLong, paramRoundingMode);
    }
    int i = log10Floor(paramLong);
    long l = powersOf10[i];
    switch (paramRoundingMode)
    {
    case UNNECESSARY: 
      MathPreconditions.checkRoundingUnnecessary(paramLong == l);
    case DOWN: 
    case FLOOR: 
      return i;
    case UP: 
    case CEILING: 
      return paramLong == l ? i : i + 1;
    case HALF_DOWN: 
    case HALF_UP: 
    case HALF_EVEN: 
      return paramLong <= halfPowersOf10[i] ? i : i + 1;
    }
    throw new AssertionError();
  }
  
  @GwtIncompatible("TODO")
  static int log10Floor(long paramLong)
  {
    int i = maxLog10ForLeadingZeros[Long.numberOfLeadingZeros(paramLong)];
    long l = paramLong - powersOf10[i] >>> 63;
    return i - (int)l;
  }
  
  @GwtIncompatible("TODO")
  public static long pow(long paramLong, int paramInt)
  {
    MathPreconditions.checkNonNegative("exponent", paramInt);
    if ((-2L <= paramLong) && (paramLong <= 2L))
    {
      switch ((int)paramLong)
      {
      case 0: 
        return paramInt == 0 ? 1L : 0L;
      case 1: 
        return 1L;
      case -1: 
        return (paramInt & 0x1) == 0 ? 1L : -1L;
      case 2: 
        return paramInt < 64 ? 1L << paramInt : 0L;
      case -2: 
        if (paramInt < 64) {
          return (paramInt & 0x1) == 0 ? 1L << paramInt : -(1L << paramInt);
        }
        return 0L;
      }
      throw new AssertionError();
    }
    long l = 1L;
    for (;;)
    {
      switch (paramInt)
      {
      case 0: 
        return l;
      case 1: 
        return l * paramLong;
      }
      l *= ((paramInt & 0x1) == 0 ? 1L : paramLong);
      paramLong *= paramLong;
      paramInt >>= 1;
    }
  }
  
  @GwtIncompatible("TODO")
  public static long sqrt(long paramLong, RoundingMode paramRoundingMode)
  {
    MathPreconditions.checkNonNegative("x", paramLong);
    if (fitsInInt(paramLong)) {
      return IntMath.sqrt((int)paramLong, paramRoundingMode);
    }
    long l1 = sqrtFloor(paramLong);
    switch (paramRoundingMode)
    {
    case UNNECESSARY: 
      MathPreconditions.checkRoundingUnnecessary(l1 * l1 == paramLong);
    case DOWN: 
    case FLOOR: 
      return l1;
    case UP: 
    case CEILING: 
      return l1 * l1 == paramLong ? l1 : l1 + 1L;
    case HALF_DOWN: 
    case HALF_UP: 
    case HALF_EVEN: 
      long l2 = l1 * l1 + l1;
      return ((l2 >= paramLong ? 1 : 0) | (l2 < 0L ? 1 : 0)) != 0 ? l1 : l1 + 1L;
    }
    throw new AssertionError();
  }
  
  @GwtIncompatible("TODO")
  private static long sqrtFloor(long paramLong)
  {
    long l1 = Math.sqrt(paramLong);
    long l2 = l1 * l1;
    if (paramLong - l2 >= l1 + l1 + 1L) {
      l1 += 1L;
    } else if (paramLong < l2) {
      l1 -= 1L;
    }
    return l1;
  }
  
  @GwtIncompatible("TODO")
  public static long divide(long paramLong1, long paramLong2, RoundingMode paramRoundingMode)
  {
    Preconditions.checkNotNull(paramRoundingMode);
    long l1 = paramLong1 / paramLong2;
    long l2 = paramLong1 - paramLong2 * l1;
    if (l2 == 0L) {
      return l1;
    }
    int i = 0x1 | (int)((paramLong1 ^ paramLong2) >> 63);
    int j;
    switch (paramRoundingMode)
    {
    case UNNECESSARY: 
      MathPreconditions.checkRoundingUnnecessary(l2 == 0L);
    case DOWN: 
      j = 0;
      break;
    case UP: 
      j = 1;
      break;
    case CEILING: 
      j = i > 0 ? 1 : 0;
      break;
    case FLOOR: 
      j = i < 0 ? 1 : 0;
      break;
    case HALF_DOWN: 
    case HALF_UP: 
    case HALF_EVEN: 
      long l3 = Math.abs(l2);
      long l4 = l3 - (Math.abs(paramLong2) - l3);
      if (l4 == 0L) {
        j = (paramRoundingMode == RoundingMode.HALF_UP ? 1 : 0) | (paramRoundingMode == RoundingMode.HALF_EVEN ? 1 : 0) & ((l1 & 1L) != 0L ? 1 : 0);
      } else {
        j = l4 > 0L ? 1 : 0;
      }
      break;
    default: 
      throw new AssertionError();
    }
    return j != 0 ? l1 + i : l1;
  }
  
  @GwtIncompatible("TODO")
  public static int mod(long paramLong, int paramInt)
  {
    return (int)mod(paramLong, paramInt);
  }
  
  @GwtIncompatible("TODO")
  public static long mod(long paramLong1, long paramLong2)
  {
    if (paramLong2 <= 0L) {
      throw new ArithmeticException("Modulus " + paramLong2 + " must be > 0");
    }
    long l = paramLong1 % paramLong2;
    return l >= 0L ? l : l + paramLong2;
  }
  
  public static long gcd(long paramLong1, long paramLong2)
  {
    MathPreconditions.checkNonNegative("a", paramLong1);
    MathPreconditions.checkNonNegative("b", paramLong2);
    if (paramLong1 == 0L) {
      return paramLong2;
    }
    if (paramLong2 == 0L) {
      return paramLong1;
    }
    int i = Long.numberOfTrailingZeros(paramLong1);
    paramLong1 >>= i;
    int j = Long.numberOfTrailingZeros(paramLong2);
    paramLong2 >>= j;
    while (paramLong1 != paramLong2)
    {
      long l1 = paramLong1 - paramLong2;
      long l2 = l1 & l1 >> 63;
      paramLong1 = l1 - l2 - l2;
      paramLong2 += l2;
      paramLong1 >>= Long.numberOfTrailingZeros(paramLong1);
    }
    return paramLong1 << Math.min(i, j);
  }
  
  @GwtIncompatible("TODO")
  public static long checkedAdd(long paramLong1, long paramLong2)
  {
    long l = paramLong1 + paramLong2;
    MathPreconditions.checkNoOverflow(((paramLong1 ^ paramLong2) < 0L ? 1 : 0) | ((paramLong1 ^ l) >= 0L ? 1 : 0));
    return l;
  }
  
  @GwtIncompatible("TODO")
  public static long checkedSubtract(long paramLong1, long paramLong2)
  {
    long l = paramLong1 - paramLong2;
    MathPreconditions.checkNoOverflow(((paramLong1 ^ paramLong2) >= 0L ? 1 : 0) | ((paramLong1 ^ l) >= 0L ? 1 : 0));
    return l;
  }
  
  @GwtIncompatible("TODO")
  public static long checkedMultiply(long paramLong1, long paramLong2)
  {
    int i = Long.numberOfLeadingZeros(paramLong1) + Long.numberOfLeadingZeros(paramLong1 ^ 0xFFFFFFFFFFFFFFFF) + Long.numberOfLeadingZeros(paramLong2) + Long.numberOfLeadingZeros(paramLong2 ^ 0xFFFFFFFFFFFFFFFF);
    if (i > 65) {
      return paramLong1 * paramLong2;
    }
    MathPreconditions.checkNoOverflow(i >= 64);
    MathPreconditions.checkNoOverflow((paramLong1 >= 0L ? 1 : 0) | (paramLong2 != Long.MIN_VALUE ? 1 : 0));
    long l = paramLong1 * paramLong2;
    MathPreconditions.checkNoOverflow((paramLong1 == 0L) || (l / paramLong1 == paramLong2));
    return l;
  }
  
  @GwtIncompatible("TODO")
  public static long checkedPow(long paramLong, int paramInt)
  {
    MathPreconditions.checkNonNegative("exponent", paramInt);
    if (((paramLong >= -2L ? 1 : 0) & (paramLong <= 2L ? 1 : 0)) != 0)
    {
      switch ((int)paramLong)
      {
      case 0: 
        return paramInt == 0 ? 1L : 0L;
      case 1: 
        return 1L;
      case -1: 
        return (paramInt & 0x1) == 0 ? 1L : -1L;
      case 2: 
        MathPreconditions.checkNoOverflow(paramInt < 63);
        return 1L << paramInt;
      case -2: 
        MathPreconditions.checkNoOverflow(paramInt < 64);
        return (paramInt & 0x1) == 0 ? 1L << paramInt : -1L << paramInt;
      }
      throw new AssertionError();
    }
    long l = 1L;
    for (;;)
    {
      switch (paramInt)
      {
      case 0: 
        return l;
      case 1: 
        return checkedMultiply(l, paramLong);
      }
      if ((paramInt & 0x1) != 0) {
        l = checkedMultiply(l, paramLong);
      }
      paramInt >>= 1;
      if (paramInt > 0)
      {
        MathPreconditions.checkNoOverflow(paramLong <= 3037000499L);
        paramLong *= paramLong;
      }
    }
  }
  
  @GwtIncompatible("TODO")
  public static long factorial(int paramInt)
  {
    MathPreconditions.checkNonNegative("n", paramInt);
    return paramInt < factorials.length ? factorials[paramInt] : Long.MAX_VALUE;
  }
  
  public static long binomial(int paramInt1, int paramInt2)
  {
    MathPreconditions.checkNonNegative("n", paramInt1);
    MathPreconditions.checkNonNegative("k", paramInt2);
    Preconditions.checkArgument(paramInt2 <= paramInt1, "k (%s) > n (%s)", new Object[] { Integer.valueOf(paramInt2), Integer.valueOf(paramInt1) });
    if (paramInt2 > paramInt1 >> 1) {
      paramInt2 = paramInt1 - paramInt2;
    }
    switch (paramInt2)
    {
    case 0: 
      return 1L;
    case 1: 
      return paramInt1;
    }
    if (paramInt1 < factorials.length) {
      return factorials[paramInt1] / (factorials[paramInt2] * factorials[(paramInt1 - paramInt2)]);
    }
    if ((paramInt2 >= biggestBinomials.length) || (paramInt1 > biggestBinomials[paramInt2])) {
      return Long.MAX_VALUE;
    }
    if ((paramInt2 < biggestSimpleBinomials.length) && (paramInt1 <= biggestSimpleBinomials[paramInt2]))
    {
      long l1 = paramInt1--;
      for (int j = 2; j <= paramInt2; j++)
      {
        l1 *= paramInt1;
        l1 /= j;
        paramInt1--;
      }
      return l1;
    }
    int i = log2(paramInt1, RoundingMode.CEILING);
    long l2 = 1L;
    long l3 = paramInt1--;
    long l4 = 1L;
    int k = i;
    int m = 2;
    while (m <= paramInt2)
    {
      if (k + i < 63)
      {
        l3 *= paramInt1;
        l4 *= m;
        k += i;
      }
      else
      {
        l2 = multiplyFraction(l2, l3, l4);
        l3 = paramInt1;
        l4 = m;
        k = i;
      }
      m++;
      paramInt1--;
    }
    return multiplyFraction(l2, l3, l4);
  }
  
  static long multiplyFraction(long paramLong1, long paramLong2, long paramLong3)
  {
    if (paramLong1 == 1L) {
      return paramLong2 / paramLong3;
    }
    long l = gcd(paramLong1, paramLong3);
    paramLong1 /= l;
    paramLong3 /= l;
    return paramLong1 * (paramLong2 / paramLong3);
  }
  
  @GwtIncompatible("TODO")
  static boolean fitsInInt(long paramLong)
  {
    return (int)paramLong == paramLong;
  }
  
  public static long mean(long paramLong1, long paramLong2)
  {
    return (paramLong1 & paramLong2) + ((paramLong1 ^ paramLong2) >> 1);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\math\LongMath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */