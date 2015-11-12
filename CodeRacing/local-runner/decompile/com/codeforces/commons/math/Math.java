package com.codeforces.commons.math;

import org.apache.commons.math3.util.FastMath;

public final class Math
{
  public static final double SQRT_2 = sqrt(2.0D);
  public static final double SQRT_3 = sqrt(3.0D);
  public static final double SQRT_5 = sqrt(5.0D);
  public static final double SQRT_6 = sqrt(6.0D);
  public static final double SQRT_7 = sqrt(7.0D);
  public static final double SQRT_8 = sqrt(8.0D);
  public static final double CBRT_2 = cbrt(2.0D);
  public static final double CBRT_3 = cbrt(3.0D);
  public static final double CBRT_4 = cbrt(4.0D);
  public static final double CBRT_5 = cbrt(5.0D);
  public static final double CBRT_6 = cbrt(6.0D);
  public static final double CBRT_7 = cbrt(7.0D);
  public static final double CBRT_9 = cbrt(9.0D);
  
  public static double sqr(double paramDouble)
  {
    return paramDouble * paramDouble;
  }
  
  public static double sumSqr(double paramDouble1, double paramDouble2)
  {
    return paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2;
  }
  
  public static double pow(double paramDouble1, double paramDouble2)
  {
    return StrictMath.pow(paramDouble1, paramDouble2);
  }
  
  public static int min(int paramInt1, int paramInt2)
  {
    return paramInt1 <= paramInt2 ? paramInt1 : paramInt2;
  }
  
  public static double min(double paramDouble1, double paramDouble2)
  {
    return java.lang.Math.min(paramDouble1, paramDouble2);
  }
  
  public static int max(int paramInt1, int paramInt2)
  {
    return paramInt1 >= paramInt2 ? paramInt1 : paramInt2;
  }
  
  public static long max(long paramLong1, long paramLong2)
  {
    return paramLong1 >= paramLong2 ? paramLong1 : paramLong2;
  }
  
  public static double max(double paramDouble1, double paramDouble2)
  {
    return java.lang.Math.max(paramDouble1, paramDouble2);
  }
  
  public static int abs(int paramInt)
  {
    return paramInt < 0 ? -paramInt : paramInt;
  }
  
  public static float abs(float paramFloat)
  {
    return java.lang.Math.abs(paramFloat);
  }
  
  public static double abs(double paramDouble)
  {
    return java.lang.Math.abs(paramDouble);
  }
  
  public static double sqrt(double paramDouble)
  {
    return StrictMath.sqrt(paramDouble);
  }
  
  public static double cbrt(double paramDouble)
  {
    return StrictMath.cbrt(paramDouble);
  }
  
  public static float round(float paramFloat)
  {
    return java.lang.Math.round(paramFloat);
  }
  
  public static double round(double paramDouble)
  {
    return java.lang.Math.round(paramDouble);
  }
  
  public static double floor(double paramDouble)
  {
    return FastMath.floor(paramDouble);
  }
  
  public static double hypot(double paramDouble1, double paramDouble2)
  {
    return FastMath.hypot(paramDouble1, paramDouble2);
  }
  
  public static double sin(double paramDouble)
  {
    return FastMath.sin(paramDouble);
  }
  
  public static double cos(double paramDouble)
  {
    return FastMath.cos(paramDouble);
  }
  
  public static double atan2(double paramDouble1, double paramDouble2)
  {
    return StrictMath.atan2(paramDouble1, paramDouble2);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\math\Math.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */