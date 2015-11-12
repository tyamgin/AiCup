package com.google.common.math;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Booleans;
import java.math.BigInteger;
import java.math.RoundingMode;

public final class DoubleMath
{
  private static final double MIN_INT_AS_DOUBLE = -2.147483648E9D;
  private static final double MAX_INT_AS_DOUBLE = 2.147483647E9D;
  private static final double MIN_LONG_AS_DOUBLE = -9.223372036854776E18D;
  private static final double MAX_LONG_AS_DOUBLE_PLUS_ONE = 9.223372036854776E18D;
  private static final double LN_2 = Math.log(2.0D);
  @VisibleForTesting
  static final int MAX_FACTORIAL = 170;
  @VisibleForTesting
  static final double[] everySixteenthFactorial = { 1.0D, 2.0922789888E13D, 2.631308369336935E35D, 1.2413915592536073E61D, 1.2688693218588417E89D, 7.156945704626381E118D, 9.916779348709496E149D, 1.974506857221074E182D, 3.856204823625804E215D, 5.5502938327393044E249D, 4.7147236359920616E284D };
  
  static double roundIntermediate(double paramDouble, RoundingMode paramRoundingMode)
  {
    if (!DoubleUtils.isFinite(paramDouble)) {
      throw new ArithmeticException("input is infinite or NaN");
    }
    double d;
    switch (paramRoundingMode)
    {
    case UNNECESSARY: 
      MathPreconditions.checkRoundingUnnecessary(isMathematicalInteger(paramDouble));
      return paramDouble;
    case FLOOR: 
      if ((paramDouble >= 0.0D) || (isMathematicalInteger(paramDouble))) {
        return paramDouble;
      }
      return paramDouble - 1.0D;
    case CEILING: 
      if ((paramDouble <= 0.0D) || (isMathematicalInteger(paramDouble))) {
        return paramDouble;
      }
      return paramDouble + 1.0D;
    case DOWN: 
      return paramDouble;
    case UP: 
      if (isMathematicalInteger(paramDouble)) {
        return paramDouble;
      }
      return paramDouble + Math.copySign(1.0D, paramDouble);
    case HALF_EVEN: 
      return Math.rint(paramDouble);
    case HALF_UP: 
      d = Math.rint(paramDouble);
      if (Math.abs(paramDouble - d) == 0.5D) {
        return paramDouble + Math.copySign(0.5D, paramDouble);
      }
      return d;
    case HALF_DOWN: 
      d = Math.rint(paramDouble);
      if (Math.abs(paramDouble - d) == 0.5D) {
        return paramDouble;
      }
      return d;
    }
    throw new AssertionError();
  }
  
  public static int roundToInt(double paramDouble, RoundingMode paramRoundingMode)
  {
    double d = roundIntermediate(paramDouble, paramRoundingMode);
    MathPreconditions.checkInRange((d > -2.147483649E9D ? 1 : 0) & (d < 2.147483648E9D ? 1 : 0));
    return (int)d;
  }
  
  public static long roundToLong(double paramDouble, RoundingMode paramRoundingMode)
  {
    double d = roundIntermediate(paramDouble, paramRoundingMode);
    MathPreconditions.checkInRange((-9.223372036854776E18D - d < 1.0D ? 1 : 0) & (d < 9.223372036854776E18D ? 1 : 0));
    return d;
  }
  
  public static BigInteger roundToBigInteger(double paramDouble, RoundingMode paramRoundingMode)
  {
    paramDouble = roundIntermediate(paramDouble, paramRoundingMode);
    if (((-9.223372036854776E18D - paramDouble < 1.0D ? 1 : 0) & (paramDouble < 9.223372036854776E18D ? 1 : 0)) != 0) {
      return BigInteger.valueOf(paramDouble);
    }
    int i = Math.getExponent(paramDouble);
    long l = DoubleUtils.getSignificand(paramDouble);
    BigInteger localBigInteger = BigInteger.valueOf(l).shiftLeft(i - 52);
    return paramDouble < 0.0D ? localBigInteger.negate() : localBigInteger;
  }
  
  public static boolean isPowerOfTwo(double paramDouble)
  {
    return (paramDouble > 0.0D) && (DoubleUtils.isFinite(paramDouble)) && (LongMath.isPowerOfTwo(DoubleUtils.getSignificand(paramDouble)));
  }
  
  public static double log2(double paramDouble)
  {
    return Math.log(paramDouble) / LN_2;
  }
  
  public static int log2(double paramDouble, RoundingMode paramRoundingMode)
  {
    Preconditions.checkArgument((paramDouble > 0.0D) && (DoubleUtils.isFinite(paramDouble)), "x must be positive and finite");
    int i = Math.getExponent(paramDouble);
    if (!DoubleUtils.isNormal(paramDouble)) {
      return log2(paramDouble * 4.503599627370496E15D, paramRoundingMode) - 52;
    }
    int j;
    switch (paramRoundingMode)
    {
    case UNNECESSARY: 
      MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(paramDouble));
    case FLOOR: 
      j = 0;
      break;
    case CEILING: 
      j = !isPowerOfTwo(paramDouble) ? 1 : 0;
      break;
    case DOWN: 
      j = (i < 0 ? 1 : 0) & (!isPowerOfTwo(paramDouble) ? 1 : 0);
      break;
    case UP: 
      j = (i >= 0 ? 1 : 0) & (!isPowerOfTwo(paramDouble) ? 1 : 0);
      break;
    case HALF_EVEN: 
    case HALF_UP: 
    case HALF_DOWN: 
      double d = DoubleUtils.scaleNormalize(paramDouble);
      j = d * d > 2.0D ? 1 : 0;
      break;
    default: 
      throw new AssertionError();
    }
    return j != 0 ? i + 1 : i;
  }
  
  public static boolean isMathematicalInteger(double paramDouble)
  {
    return (DoubleUtils.isFinite(paramDouble)) && ((paramDouble == 0.0D) || (52 - Long.numberOfTrailingZeros(DoubleUtils.getSignificand(paramDouble)) <= Math.getExponent(paramDouble)));
  }
  
  public static double factorial(int paramInt)
  {
    MathPreconditions.checkNonNegative("n", paramInt);
    if (paramInt > 170) {
      return Double.POSITIVE_INFINITY;
    }
    double d = 1.0D;
    for (int i = 1 + (paramInt & 0xFFFFFFF0); i <= paramInt; i++) {
      d *= i;
    }
    return d * everySixteenthFactorial[(paramInt >> 4)];
  }
  
  public static boolean fuzzyEquals(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    MathPreconditions.checkNonNegative("tolerance", paramDouble3);
    return (Math.copySign(paramDouble1 - paramDouble2, 1.0D) <= paramDouble3) || (paramDouble1 == paramDouble2) || ((paramDouble1 != paramDouble1) && (paramDouble2 != paramDouble2));
  }
  
  public static int fuzzyCompare(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    if (fuzzyEquals(paramDouble1, paramDouble2, paramDouble3)) {
      return 0;
    }
    if (paramDouble1 < paramDouble2) {
      return -1;
    }
    if (paramDouble1 > paramDouble2) {
      return 1;
    }
    return Booleans.compare(Double.isNaN(paramDouble1), Double.isNaN(paramDouble2));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\math\DoubleMath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */