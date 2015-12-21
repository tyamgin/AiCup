package com.codeforces.commons.math;

import com.codeforces.commons.text.StringUtil;

public final class NumberUtil
{
  public static byte toByte(int paramInt)
  {
    int i = (byte)paramInt;
    if (i == paramInt) {
      return i;
    }
    throw new IllegalArgumentException("Can't convert int " + paramInt + " to byte.");
  }
  
  public static Integer toInt(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof Byte)) {
      return Integer.valueOf(((Byte)paramObject).byteValue());
    }
    if ((paramObject instanceof Short)) {
      return Integer.valueOf(((Short)paramObject).shortValue());
    }
    if ((paramObject instanceof Integer)) {
      return (Integer)paramObject;
    }
    if ((paramObject instanceof Long)) {
      return Integer.valueOf(toInt(((Long)paramObject).longValue()));
    }
    if ((paramObject instanceof Float)) {
      return Integer.valueOf(toInt(((Float)paramObject).floatValue()));
    }
    if ((paramObject instanceof Double)) {
      return Integer.valueOf(toInt(((Double)paramObject).doubleValue()));
    }
    if ((paramObject instanceof Number)) {
      return Integer.valueOf(toInt(((Number)paramObject).doubleValue()));
    }
    return Integer.valueOf(toInt(Double.parseDouble(StringUtil.trim(paramObject.toString()))));
  }
  
  public static int toInt(long paramLong)
  {
    int i = (int)paramLong;
    if (i == paramLong) {
      return i;
    }
    throw new IllegalArgumentException("Can't convert long " + paramLong + " to int.");
  }
  
  public static int toInt(float paramFloat)
  {
    int i = (int)paramFloat;
    if (Math.abs(i - paramFloat) < 1.0F) {
      return i;
    }
    throw new IllegalArgumentException("Can't convert float " + paramFloat + " to int.");
  }
  
  public static int toInt(double paramDouble)
  {
    int i = (int)paramDouble;
    if (Math.abs(i - paramDouble) < 1.0D) {
      return i;
    }
    throw new IllegalArgumentException("Can't convert double " + paramDouble + " to int.");
  }
  
  public static boolean equals(Integer paramInteger1, Integer paramInteger2)
  {
    return paramInteger1 == null ? false : paramInteger2 == null ? true : paramInteger1.equals(paramInteger2);
  }
  
  public static boolean equals(Long paramLong1, Long paramLong2)
  {
    return paramLong1 == null ? false : paramLong2 == null ? true : paramLong1.equals(paramLong2);
  }
  
  public static boolean equals(Double paramDouble1, Double paramDouble2)
  {
    return paramDouble1 == null ? false : paramDouble2 == null ? true : paramDouble1.equals(paramDouble2);
  }
  
  public static boolean nearlyEquals(Double paramDouble1, Double paramDouble2, double paramDouble)
  {
    if (paramDouble1 == null) {
      return paramDouble2 == null;
    }
    if (paramDouble1.equals(paramDouble2)) {
      return true;
    }
    if ((Double.isInfinite(paramDouble1.doubleValue())) || (Double.isNaN(paramDouble1.doubleValue())) || (Double.isInfinite(paramDouble2.doubleValue())) || (Double.isNaN(paramDouble2.doubleValue()))) {
      return false;
    }
    return Math.abs(paramDouble1.doubleValue() - paramDouble2.doubleValue()) < paramDouble;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\math\NumberUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */