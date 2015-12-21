package org.apache.commons.lang3;

import java.lang.reflect.Array;

public class ArrayUtils
{
  public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
  public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
  public static final String[] EMPTY_STRING_ARRAY = new String[0];
  public static final long[] EMPTY_LONG_ARRAY = new long[0];
  public static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];
  public static final int[] EMPTY_INT_ARRAY = new int[0];
  public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];
  public static final short[] EMPTY_SHORT_ARRAY = new short[0];
  public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];
  public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];
  public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
  public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];
  public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
  public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];
  public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
  public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];
  public static final char[] EMPTY_CHAR_ARRAY = new char[0];
  public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];
  
  public static Object[] clone(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject == null) {
      return null;
    }
    return (Object[])paramArrayOfObject.clone();
  }
  
  public static int getLength(Object paramObject)
  {
    if (paramObject == null) {
      return 0;
    }
    return Array.getLength(paramObject);
  }
  
  public static int indexOf(Object[] paramArrayOfObject, Object paramObject)
  {
    return indexOf(paramArrayOfObject, paramObject, 0);
  }
  
  public static int indexOf(Object[] paramArrayOfObject, Object paramObject, int paramInt)
  {
    if (paramArrayOfObject == null) {
      return -1;
    }
    if (paramInt < 0) {
      paramInt = 0;
    }
    int i;
    if (paramObject == null) {
      for (i = paramInt; i < paramArrayOfObject.length; i++) {
        if (paramArrayOfObject[i] == null) {
          return i;
        }
      }
    } else if (paramArrayOfObject.getClass().getComponentType().isInstance(paramObject)) {
      for (i = paramInt; i < paramArrayOfObject.length; i++) {
        if (paramObject.equals(paramArrayOfObject[i])) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public static boolean isEmpty(char[] paramArrayOfChar)
  {
    return (paramArrayOfChar == null) || (paramArrayOfChar.length == 0);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\ArrayUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */