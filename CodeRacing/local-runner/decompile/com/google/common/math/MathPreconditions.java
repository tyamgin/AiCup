package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import java.math.BigInteger;

@GwtCompatible
final class MathPreconditions
{
  static int checkPositive(String paramString, int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException(paramString + " (" + paramInt + ") must be > 0");
    }
    return paramInt;
  }
  
  static long checkPositive(String paramString, long paramLong)
  {
    if (paramLong <= 0L) {
      throw new IllegalArgumentException(paramString + " (" + paramLong + ") must be > 0");
    }
    return paramLong;
  }
  
  static BigInteger checkPositive(String paramString, BigInteger paramBigInteger)
  {
    if (paramBigInteger.signum() <= 0) {
      throw new IllegalArgumentException(paramString + " (" + paramBigInteger + ") must be > 0");
    }
    return paramBigInteger;
  }
  
  static int checkNonNegative(String paramString, int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException(paramString + " (" + paramInt + ") must be >= 0");
    }
    return paramInt;
  }
  
  static long checkNonNegative(String paramString, long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException(paramString + " (" + paramLong + ") must be >= 0");
    }
    return paramLong;
  }
  
  static BigInteger checkNonNegative(String paramString, BigInteger paramBigInteger)
  {
    if (paramBigInteger.signum() < 0) {
      throw new IllegalArgumentException(paramString + " (" + paramBigInteger + ") must be >= 0");
    }
    return paramBigInteger;
  }
  
  static double checkNonNegative(String paramString, double paramDouble)
  {
    if (paramDouble < 0.0D) {
      throw new IllegalArgumentException(paramString + " (" + paramDouble + ") must be >= 0");
    }
    return paramDouble;
  }
  
  static void checkRoundingUnnecessary(boolean paramBoolean)
  {
    if (!paramBoolean) {
      throw new ArithmeticException("mode was UNNECESSARY, but rounding was necessary");
    }
  }
  
  static void checkInRange(boolean paramBoolean)
  {
    if (!paramBoolean) {
      throw new ArithmeticException("not in range");
    }
  }
  
  static void checkNoOverflow(boolean paramBoolean)
  {
    if (!paramBoolean) {
      throw new ArithmeticException("overflow");
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\math\MathPreconditions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */