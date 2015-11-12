package org.apache.commons.lang3.math;

public class NumberUtils
{
  public static final Long LONG_ZERO = Long.valueOf(0L);
  public static final Long LONG_ONE = Long.valueOf(1L);
  public static final Long LONG_MINUS_ONE = Long.valueOf(-1L);
  public static final Integer INTEGER_ZERO = Integer.valueOf(0);
  public static final Integer INTEGER_ONE = Integer.valueOf(1);
  public static final Integer INTEGER_MINUS_ONE = Integer.valueOf(-1);
  public static final Short SHORT_ZERO = Short.valueOf((short)0);
  public static final Short SHORT_ONE = Short.valueOf((short)1);
  public static final Short SHORT_MINUS_ONE = Short.valueOf((short)-1);
  public static final Byte BYTE_ZERO = Byte.valueOf((byte)0);
  public static final Byte BYTE_ONE = Byte.valueOf((byte)1);
  public static final Byte BYTE_MINUS_ONE = Byte.valueOf((byte)-1);
  public static final Double DOUBLE_ZERO = Double.valueOf(0.0D);
  public static final Double DOUBLE_ONE = Double.valueOf(1.0D);
  public static final Double DOUBLE_MINUS_ONE = Double.valueOf(-1.0D);
  public static final Float FLOAT_ZERO = Float.valueOf(0.0F);
  public static final Float FLOAT_ONE = Float.valueOf(1.0F);
  public static final Float FLOAT_MINUS_ONE = Float.valueOf(-1.0F);
  
  public static int compare(int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2) {
      return 0;
    }
    if (paramInt1 < paramInt2) {
      return -1;
    }
    return 1;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\math\NumberUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */