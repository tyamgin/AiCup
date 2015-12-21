package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@GwtCompatible(emulated=true)
public final class BigIntegerMath
{
  @VisibleForTesting
  static final int SQRT2_PRECOMPUTE_THRESHOLD = 256;
  @VisibleForTesting
  static final BigInteger SQRT2_PRECOMPUTED_BITS = new BigInteger("16a09e667f3bcc908b2fb1366ea957d3e3adec17512775099da2f590b0667322a", 16);
  private static final double LN_10 = Math.log(10.0D);
  private static final double LN_2 = Math.log(2.0D);
  
  public static boolean isPowerOfTwo(BigInteger paramBigInteger)
  {
    Preconditions.checkNotNull(paramBigInteger);
    return (paramBigInteger.signum() > 0) && (paramBigInteger.getLowestSetBit() == paramBigInteger.bitLength() - 1);
  }
  
  public static int log2(BigInteger paramBigInteger, RoundingMode paramRoundingMode)
  {
    MathPreconditions.checkPositive("x", (BigInteger)Preconditions.checkNotNull(paramBigInteger));
    int i = paramBigInteger.bitLength() - 1;
    switch (paramRoundingMode)
    {
    case UNNECESSARY: 
      MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(paramBigInteger));
    case DOWN: 
    case FLOOR: 
      return i;
    case UP: 
    case CEILING: 
      return isPowerOfTwo(paramBigInteger) ? i : i + 1;
    case HALF_DOWN: 
    case HALF_UP: 
    case HALF_EVEN: 
      if (i < 256)
      {
        localBigInteger = SQRT2_PRECOMPUTED_BITS.shiftRight(256 - i);
        if (paramBigInteger.compareTo(localBigInteger) <= 0) {
          return i;
        }
        return i + 1;
      }
      BigInteger localBigInteger = paramBigInteger.pow(2);
      int j = localBigInteger.bitLength() - 1;
      return j < 2 * i + 1 ? i : i + 1;
    }
    throw new AssertionError();
  }
  
  @GwtIncompatible("TODO")
  public static int log10(BigInteger paramBigInteger, RoundingMode paramRoundingMode)
  {
    MathPreconditions.checkPositive("x", paramBigInteger);
    if (fitsInLong(paramBigInteger)) {
      return LongMath.log10(paramBigInteger.longValue(), paramRoundingMode);
    }
    BigInteger localBigInteger1 = (int)(log2(paramBigInteger, RoundingMode.FLOOR) * LN_2 / LN_10);
    Object localObject1 = BigInteger.TEN.pow(localBigInteger1);
    int i = ((BigInteger)localObject1).compareTo(paramBigInteger);
    if (i > 0)
    {
      do
      {
        localBigInteger1--;
        localObject1 = ((BigInteger)localObject1).divide(BigInteger.TEN);
        i = ((BigInteger)localObject1).compareTo(paramBigInteger);
      } while (i > 0);
    }
    else
    {
      localBigInteger2 = BigInteger.TEN.multiply((BigInteger)localObject1);
      for (int j = localBigInteger2.compareTo(paramBigInteger); j <= 0; j = localBigInteger2.compareTo(paramBigInteger))
      {
        localBigInteger1++;
        localObject1 = localBigInteger2;
        i = j;
        localBigInteger2 = BigInteger.TEN.multiply((BigInteger)localObject1);
      }
    }
    BigInteger localBigInteger2 = localBigInteger1;
    Object localObject2 = localObject1;
    int k = i;
    switch (paramRoundingMode)
    {
    case UNNECESSARY: 
      MathPreconditions.checkRoundingUnnecessary(k == 0);
    case DOWN: 
    case FLOOR: 
      return localBigInteger2;
    case UP: 
    case CEILING: 
      return ((BigInteger)localObject2).equals(paramBigInteger) ? localBigInteger2 : localBigInteger2 + 1;
    case HALF_DOWN: 
    case HALF_UP: 
    case HALF_EVEN: 
      BigInteger localBigInteger3 = paramBigInteger.pow(2);
      BigInteger localBigInteger4 = ((BigInteger)localObject2).pow(2).multiply(BigInteger.TEN);
      return localBigInteger3.compareTo(localBigInteger4) <= 0 ? localBigInteger2 : localBigInteger2 + 1;
    }
    throw new AssertionError();
  }
  
  @GwtIncompatible("TODO")
  public static BigInteger sqrt(BigInteger paramBigInteger, RoundingMode paramRoundingMode)
  {
    MathPreconditions.checkNonNegative("x", paramBigInteger);
    if (fitsInLong(paramBigInteger)) {
      return BigInteger.valueOf(LongMath.sqrt(paramBigInteger.longValue(), paramRoundingMode));
    }
    BigInteger localBigInteger1 = sqrtFloor(paramBigInteger);
    switch (paramRoundingMode)
    {
    case UNNECESSARY: 
      MathPreconditions.checkRoundingUnnecessary(localBigInteger1.pow(2).equals(paramBigInteger));
    case DOWN: 
    case FLOOR: 
      return localBigInteger1;
    case UP: 
    case CEILING: 
      return localBigInteger1.pow(2).equals(paramBigInteger) ? localBigInteger1 : localBigInteger1.add(BigInteger.ONE);
    case HALF_DOWN: 
    case HALF_UP: 
    case HALF_EVEN: 
      BigInteger localBigInteger2 = localBigInteger1.pow(2).add(localBigInteger1);
      return localBigInteger2.compareTo(paramBigInteger) >= 0 ? localBigInteger1 : localBigInteger1.add(BigInteger.ONE);
    }
    throw new AssertionError();
  }
  
  @GwtIncompatible("TODO")
  private static BigInteger sqrtFloor(BigInteger paramBigInteger)
  {
    int i = log2(paramBigInteger, RoundingMode.FLOOR);
    Object localObject;
    if (i < 1023)
    {
      localObject = sqrtApproxWithDoubles(paramBigInteger);
    }
    else
    {
      int j = i - 52 & 0xFFFFFFFE;
      localObject = sqrtApproxWithDoubles(paramBigInteger.shiftRight(j)).shiftLeft(j >> 1);
    }
    BigInteger localBigInteger = ((BigInteger)localObject).add(paramBigInteger.divide((BigInteger)localObject)).shiftRight(1);
    if (((BigInteger)localObject).equals(localBigInteger)) {
      return (BigInteger)localObject;
    }
    do
    {
      localObject = localBigInteger;
      localBigInteger = ((BigInteger)localObject).add(paramBigInteger.divide((BigInteger)localObject)).shiftRight(1);
    } while (localBigInteger.compareTo((BigInteger)localObject) < 0);
    return (BigInteger)localObject;
  }
  
  @GwtIncompatible("TODO")
  private static BigInteger sqrtApproxWithDoubles(BigInteger paramBigInteger)
  {
    return DoubleMath.roundToBigInteger(Math.sqrt(DoubleUtils.bigToDouble(paramBigInteger)), RoundingMode.HALF_EVEN);
  }
  
  @GwtIncompatible("TODO")
  public static BigInteger divide(BigInteger paramBigInteger1, BigInteger paramBigInteger2, RoundingMode paramRoundingMode)
  {
    BigDecimal localBigDecimal1 = new BigDecimal(paramBigInteger1);
    BigDecimal localBigDecimal2 = new BigDecimal(paramBigInteger2);
    return localBigDecimal1.divide(localBigDecimal2, 0, paramRoundingMode).toBigIntegerExact();
  }
  
  public static BigInteger factorial(int paramInt)
  {
    MathPreconditions.checkNonNegative("n", paramInt);
    if (paramInt < LongMath.factorials.length) {
      return BigInteger.valueOf(LongMath.factorials[paramInt]);
    }
    int i = IntMath.divide(paramInt * IntMath.log2(paramInt, RoundingMode.CEILING), 64, RoundingMode.CEILING);
    ArrayList localArrayList = new ArrayList(i);
    int j = LongMath.factorials.length;
    long l1 = LongMath.factorials[(j - 1)];
    int k = Long.numberOfTrailingZeros(l1);
    l1 >>= k;
    int m = LongMath.log2(l1, RoundingMode.FLOOR) + 1;
    int n = LongMath.log2(j, RoundingMode.FLOOR) + 1;
    int i1 = 1 << n - 1;
    for (long l2 = j; l2 <= paramInt; l2 += 1L)
    {
      if ((l2 & i1) != 0L)
      {
        i1 <<= 1;
        n++;
      }
      int i2 = Long.numberOfTrailingZeros(l2);
      long l3 = l2 >> i2;
      k += i2;
      int i3 = n - i2;
      if (i3 + m >= 64)
      {
        localArrayList.add(BigInteger.valueOf(l1));
        l1 = 1L;
        m = 0;
      }
      l1 *= l3;
      m = LongMath.log2(l1, RoundingMode.FLOOR) + 1;
    }
    if (l1 > 1L) {
      localArrayList.add(BigInteger.valueOf(l1));
    }
    return listProduct(localArrayList).shiftLeft(k);
  }
  
  static BigInteger listProduct(List paramList)
  {
    return listProduct(paramList, 0, paramList.size());
  }
  
  static BigInteger listProduct(List paramList, int paramInt1, int paramInt2)
  {
    switch (paramInt2 - paramInt1)
    {
    case 0: 
      return BigInteger.ONE;
    case 1: 
      return (BigInteger)paramList.get(paramInt1);
    case 2: 
      return ((BigInteger)paramList.get(paramInt1)).multiply((BigInteger)paramList.get(paramInt1 + 1));
    case 3: 
      return ((BigInteger)paramList.get(paramInt1)).multiply((BigInteger)paramList.get(paramInt1 + 1)).multiply((BigInteger)paramList.get(paramInt1 + 2));
    }
    int i = paramInt2 + paramInt1 >>> 1;
    return listProduct(paramList, paramInt1, i).multiply(listProduct(paramList, i, paramInt2));
  }
  
  public static BigInteger binomial(int paramInt1, int paramInt2)
  {
    MathPreconditions.checkNonNegative("n", paramInt1);
    MathPreconditions.checkNonNegative("k", paramInt2);
    Preconditions.checkArgument(paramInt2 <= paramInt1, "k (%s) > n (%s)", new Object[] { Integer.valueOf(paramInt2), Integer.valueOf(paramInt1) });
    if (paramInt2 > paramInt1 >> 1) {
      paramInt2 = paramInt1 - paramInt2;
    }
    if ((paramInt2 < LongMath.biggestBinomials.length) && (paramInt1 <= LongMath.biggestBinomials[paramInt2])) {
      return BigInteger.valueOf(LongMath.binomial(paramInt1, paramInt2));
    }
    BigInteger localBigInteger = BigInteger.ONE;
    long l1 = paramInt1;
    long l2 = 1L;
    int i = LongMath.log2(paramInt1, RoundingMode.CEILING);
    int j = i;
    for (int k = 1; k < paramInt2; k++)
    {
      int m = paramInt1 - k;
      int n = k + 1;
      if (j + i >= 63)
      {
        localBigInteger = localBigInteger.multiply(BigInteger.valueOf(l1)).divide(BigInteger.valueOf(l2));
        l1 = m;
        l2 = n;
        j = i;
      }
      else
      {
        l1 *= m;
        l2 *= n;
        j += i;
      }
    }
    return localBigInteger.multiply(BigInteger.valueOf(l1)).divide(BigInteger.valueOf(l2));
  }
  
  @GwtIncompatible("TODO")
  static boolean fitsInLong(BigInteger paramBigInteger)
  {
    return paramBigInteger.bitLength() <= 63;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\math\BigIntegerMath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */