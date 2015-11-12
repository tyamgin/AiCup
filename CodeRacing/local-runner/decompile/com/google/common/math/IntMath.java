package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.math.RoundingMode;

@GwtCompatible(emulated=true)
public final class IntMath
{
  @VisibleForTesting
  static final int MAX_POWER_OF_SQRT2_UNSIGNED = -1257966797;
  @VisibleForTesting
  static final byte[] maxLog10ForLeadingZeros = { 9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0, 0 };
  @VisibleForTesting
  static final int[] powersOf10 = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };
  @VisibleForTesting
  static final int[] halfPowersOf10 = { 3, 31, 316, 3162, 31622, 316227, 3162277, 31622776, 316227766, Integer.MAX_VALUE };
  @VisibleForTesting
  static final int FLOOR_SQRT_MAX_INT = 46340;
  private static final int[] factorials = { 1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800, 39916800, 479001600 };
  @VisibleForTesting
  static int[] biggestBinomials = { Integer.MAX_VALUE, Integer.MAX_VALUE, 65536, 2345, 477, 193, 110, 75, 58, 49, 43, 39, 37, 35, 34, 34, 33 };
  
  public static boolean isPowerOfTwo(int paramInt)
  {
    return (paramInt > 0 ? 1 : 0) & ((paramInt & paramInt - 1) == 0 ? 1 : 0);
  }
  
  public static int log2(int paramInt, RoundingMode paramRoundingMode)
  {
    MathPreconditions.checkPositive("x", paramInt);
    switch (paramRoundingMode)
    {
    case UNNECESSARY: 
      MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(paramInt));
    case DOWN: 
    case FLOOR: 
      return 31 - Integer.numberOfLeadingZeros(paramInt);
    case UP: 
    case CEILING: 
      return 32 - Integer.numberOfLeadingZeros(paramInt - 1);
    case HALF_DOWN: 
    case HALF_UP: 
    case HALF_EVEN: 
      int i = Integer.numberOfLeadingZeros(paramInt);
      int j = -1257966797 >>> i;
      int k = 31 - i;
      return paramInt <= j ? k : k + 1;
    }
    throw new AssertionError();
  }
  
  @GwtIncompatible("need BigIntegerMath to adequately test")
  public static int log10(int paramInt, RoundingMode paramRoundingMode)
  {
    MathPreconditions.checkPositive("x", paramInt);
    int i = log10Floor(paramInt);
    int j = powersOf10[i];
    switch (paramRoundingMode)
    {
    case UNNECESSARY: 
      MathPreconditions.checkRoundingUnnecessary(paramInt == j);
    case DOWN: 
    case FLOOR: 
      return i;
    case UP: 
    case CEILING: 
      return paramInt == j ? i : i + 1;
    case HALF_DOWN: 
    case HALF_UP: 
    case HALF_EVEN: 
      return paramInt <= halfPowersOf10[i] ? i : i + 1;
    }
    throw new AssertionError();
  }
  
  private static int log10Floor(int paramInt)
  {
    int i = maxLog10ForLeadingZeros[Integer.numberOfLeadingZeros(paramInt)];
    int j = paramInt - powersOf10[i] >>> 31;
    return i - j;
  }
  
  @GwtIncompatible("failing tests")
  public static int pow(int paramInt1, int paramInt2)
  {
    MathPreconditions.checkNonNegative("exponent", paramInt2);
    switch (paramInt1)
    {
    case 0: 
      return paramInt2 == 0 ? 1 : 0;
    case 1: 
      return 1;
    case -1: 
      return (paramInt2 & 0x1) == 0 ? 1 : -1;
    case 2: 
      return paramInt2 < 32 ? 1 << paramInt2 : 0;
    case -2: 
      if (paramInt2 < 32) {
        return (paramInt2 & 0x1) == 0 ? 1 << paramInt2 : -(1 << paramInt2);
      }
      return 0;
    }
    int i = 1;
    for (;;)
    {
      switch (paramInt2)
      {
      case 0: 
        return i;
      case 1: 
        return paramInt1 * i;
      }
      i *= ((paramInt2 & 0x1) == 0 ? 1 : paramInt1);
      paramInt1 *= paramInt1;
      paramInt2 >>= 1;
    }
  }
  
  @GwtIncompatible("need BigIntegerMath to adequately test")
  public static int sqrt(int paramInt, RoundingMode paramRoundingMode)
  {
    MathPreconditions.checkNonNegative("x", paramInt);
    int i = sqrtFloor(paramInt);
    switch (paramRoundingMode)
    {
    case UNNECESSARY: 
      MathPreconditions.checkRoundingUnnecessary(i * i == paramInt);
    case DOWN: 
    case FLOOR: 
      return i;
    case UP: 
    case CEILING: 
      return i * i == paramInt ? i : i + 1;
    case HALF_DOWN: 
    case HALF_UP: 
    case HALF_EVEN: 
      int j = i * i + i;
      return ((paramInt <= j ? 1 : 0) | (j < 0 ? 1 : 0)) != 0 ? i : i + 1;
    }
    throw new AssertionError();
  }
  
  private static int sqrtFloor(int paramInt)
  {
    return (int)Math.sqrt(paramInt);
  }
  
  public static int divide(int paramInt1, int paramInt2, RoundingMode paramRoundingMode)
  {
    Preconditions.checkNotNull(paramRoundingMode);
    if (paramInt2 == 0) {
      throw new ArithmeticException("/ by zero");
    }
    int i = paramInt1 / paramInt2;
    int j = paramInt1 - paramInt2 * i;
    if (j == 0) {
      return i;
    }
    int k = 0x1 | (paramInt1 ^ paramInt2) >> 31;
    int m;
    switch (paramRoundingMode)
    {
    case UNNECESSARY: 
      MathPreconditions.checkRoundingUnnecessary(j == 0);
    case DOWN: 
      m = 0;
      break;
    case UP: 
      m = 1;
      break;
    case CEILING: 
      m = k > 0 ? 1 : 0;
      break;
    case FLOOR: 
      m = k < 0 ? 1 : 0;
      break;
    case HALF_DOWN: 
    case HALF_UP: 
    case HALF_EVEN: 
      int n = Math.abs(j);
      int i1 = n - (Math.abs(paramInt2) - n);
      if (i1 == 0)
      {
        if (paramRoundingMode != RoundingMode.HALF_UP) {}
        m = ((paramRoundingMode == RoundingMode.HALF_EVEN ? 1 : 0) & ((i & 0x1) != 0 ? 1 : 0)) != 0 ? 1 : 0;
      }
      else
      {
        m = i1 > 0 ? 1 : 0;
      }
      break;
    default: 
      throw new AssertionError();
    }
    return m != 0 ? i + k : i;
  }
  
  public static int mod(int paramInt1, int paramInt2)
  {
    if (paramInt2 <= 0) {
      throw new ArithmeticException("Modulus " + paramInt2 + " must be > 0");
    }
    int i = paramInt1 % paramInt2;
    return i >= 0 ? i : i + paramInt2;
  }
  
  public static int gcd(int paramInt1, int paramInt2)
  {
    MathPreconditions.checkNonNegative("a", paramInt1);
    MathPreconditions.checkNonNegative("b", paramInt2);
    if (paramInt1 == 0) {
      return paramInt2;
    }
    if (paramInt2 == 0) {
      return paramInt1;
    }
    int i = Integer.numberOfTrailingZeros(paramInt1);
    paramInt1 >>= i;
    int j = Integer.numberOfTrailingZeros(paramInt2);
    paramInt2 >>= j;
    while (paramInt1 != paramInt2)
    {
      int k = paramInt1 - paramInt2;
      int m = k & k >> 31;
      paramInt1 = k - m - m;
      paramInt2 += m;
      paramInt1 >>= Integer.numberOfTrailingZeros(paramInt1);
    }
    return paramInt1 << Math.min(i, j);
  }
  
  public static int checkedAdd(int paramInt1, int paramInt2)
  {
    long l = paramInt1 + paramInt2;
    MathPreconditions.checkNoOverflow(l == (int)l);
    return (int)l;
  }
  
  public static int checkedSubtract(int paramInt1, int paramInt2)
  {
    long l = paramInt1 - paramInt2;
    MathPreconditions.checkNoOverflow(l == (int)l);
    return (int)l;
  }
  
  public static int checkedMultiply(int paramInt1, int paramInt2)
  {
    long l = paramInt1 * paramInt2;
    MathPreconditions.checkNoOverflow(l == (int)l);
    return (int)l;
  }
  
  public static int checkedPow(int paramInt1, int paramInt2)
  {
    MathPreconditions.checkNonNegative("exponent", paramInt2);
    switch (paramInt1)
    {
    case 0: 
      return paramInt2 == 0 ? 1 : 0;
    case 1: 
      return 1;
    case -1: 
      return (paramInt2 & 0x1) == 0 ? 1 : -1;
    case 2: 
      MathPreconditions.checkNoOverflow(paramInt2 < 31);
      return 1 << paramInt2;
    case -2: 
      MathPreconditions.checkNoOverflow(paramInt2 < 32);
      return (paramInt2 & 0x1) == 0 ? 1 << paramInt2 : -1 << paramInt2;
    }
    int i = 1;
    for (;;)
    {
      switch (paramInt2)
      {
      case 0: 
        return i;
      case 1: 
        return checkedMultiply(i, paramInt1);
      }
      if ((paramInt2 & 0x1) != 0) {
        i = checkedMultiply(i, paramInt1);
      }
      paramInt2 >>= 1;
      if (paramInt2 > 0)
      {
        MathPreconditions.checkNoOverflow((-46340 <= paramInt1 ? 1 : 0) & (paramInt1 <= 46340 ? 1 : 0));
        paramInt1 *= paramInt1;
      }
    }
  }
  
  public static int factorial(int paramInt)
  {
    MathPreconditions.checkNonNegative("n", paramInt);
    return paramInt < factorials.length ? factorials[paramInt] : Integer.MAX_VALUE;
  }
  
  @GwtIncompatible("need BigIntegerMath to adequately test")
  public static int binomial(int paramInt1, int paramInt2)
  {
    MathPreconditions.checkNonNegative("n", paramInt1);
    MathPreconditions.checkNonNegative("k", paramInt2);
    Preconditions.checkArgument(paramInt2 <= paramInt1, "k (%s) > n (%s)", new Object[] { Integer.valueOf(paramInt2), Integer.valueOf(paramInt1) });
    if (paramInt2 > paramInt1 >> 1) {
      paramInt2 = paramInt1 - paramInt2;
    }
    if ((paramInt2 >= biggestBinomials.length) || (paramInt1 > biggestBinomials[paramInt2])) {
      return Integer.MAX_VALUE;
    }
    switch (paramInt2)
    {
    case 0: 
      return 1;
    case 1: 
      return paramInt1;
    }
    long l = 1L;
    for (int i = 0; i < paramInt2; i++)
    {
      l *= (paramInt1 - i);
      l /= (i + 1);
    }
    return (int)l;
  }
  
  public static int mean(int paramInt1, int paramInt2)
  {
    return (paramInt1 & paramInt2) + ((paramInt1 ^ paramInt2) >> 1);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\math\IntMath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */