package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Comparator;
import sun.misc.Unsafe;

public final class UnsignedBytes
{
  public static final byte MAX_POWER_OF_TWO = -128;
  public static final byte MAX_VALUE = -1;
  private static final int UNSIGNED_MASK = 255;
  
  public static int toInt(byte paramByte)
  {
    return paramByte & 0xFF;
  }
  
  public static byte checkedCast(long paramLong)
  {
    Preconditions.checkArgument(paramLong >> 8 == 0L, "out of range: %s", new Object[] { Long.valueOf(paramLong) });
    return (byte)(int)paramLong;
  }
  
  public static byte saturatedCast(long paramLong)
  {
    if (paramLong > toInt((byte)-1)) {
      return -1;
    }
    if (paramLong < 0L) {
      return 0;
    }
    return (byte)(int)paramLong;
  }
  
  public static int compare(byte paramByte1, byte paramByte2)
  {
    return toInt(paramByte1) - toInt(paramByte2);
  }
  
  public static byte min(byte... paramVarArgs)
  {
    Preconditions.checkArgument(paramVarArgs.length > 0);
    int i = toInt(paramVarArgs[0]);
    for (int j = 1; j < paramVarArgs.length; j++)
    {
      int k = toInt(paramVarArgs[j]);
      if (k < i) {
        i = k;
      }
    }
    return (byte)i;
  }
  
  public static byte max(byte... paramVarArgs)
  {
    Preconditions.checkArgument(paramVarArgs.length > 0);
    int i = toInt(paramVarArgs[0]);
    for (int j = 1; j < paramVarArgs.length; j++)
    {
      int k = toInt(paramVarArgs[j]);
      if (k > i) {
        i = k;
      }
    }
    return (byte)i;
  }
  
  @Beta
  public static String toString(byte paramByte)
  {
    return toString(paramByte, 10);
  }
  
  @Beta
  public static String toString(byte paramByte, int paramInt)
  {
    Preconditions.checkArgument((paramInt >= 2) && (paramInt <= 36), "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", new Object[] { Integer.valueOf(paramInt) });
    return Integer.toString(toInt(paramByte), paramInt);
  }
  
  @Beta
  public static byte parseUnsignedByte(String paramString)
  {
    return parseUnsignedByte(paramString, 10);
  }
  
  @Beta
  public static byte parseUnsignedByte(String paramString, int paramInt)
  {
    int i = Integer.parseInt((String)Preconditions.checkNotNull(paramString), paramInt);
    if (i >> 8 == 0) {
      return (byte)i;
    }
    throw new NumberFormatException("out of range: " + i);
  }
  
  public static String join(String paramString, byte... paramVarArgs)
  {
    Preconditions.checkNotNull(paramString);
    if (paramVarArgs.length == 0) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder(paramVarArgs.length * (3 + paramString.length()));
    localStringBuilder.append(toInt(paramVarArgs[0]));
    for (int i = 1; i < paramVarArgs.length; i++) {
      localStringBuilder.append(paramString).append(toString(paramVarArgs[i]));
    }
    return localStringBuilder.toString();
  }
  
  public static Comparator lexicographicalComparator()
  {
    return LexicographicalComparatorHolder.BEST_COMPARATOR;
  }
  
  @VisibleForTesting
  static Comparator lexicographicalComparatorJavaImpl()
  {
    return UnsignedBytes.LexicographicalComparatorHolder.PureJavaComparator.INSTANCE;
  }
  
  @VisibleForTesting
  static class LexicographicalComparatorHolder
  {
    static final String UNSAFE_COMPARATOR_NAME = LexicographicalComparatorHolder.class.getName() + "$UnsafeComparator";
    static final Comparator BEST_COMPARATOR = getBestComparator();
    
    static Comparator getBestComparator()
    {
      try
      {
        Class localClass = Class.forName(UNSAFE_COMPARATOR_NAME);
        Comparator localComparator = (Comparator)localClass.getEnumConstants()[0];
        return localComparator;
      }
      catch (Throwable localThrowable) {}
      return UnsignedBytes.lexicographicalComparatorJavaImpl();
    }
    
    static enum PureJavaComparator
      implements Comparator
    {
      INSTANCE;
      
      public int compare(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
      {
        int i = Math.min(paramArrayOfByte1.length, paramArrayOfByte2.length);
        for (int j = 0; j < i; j++)
        {
          int k = UnsignedBytes.compare(paramArrayOfByte1[j], paramArrayOfByte2[j]);
          if (k != 0) {
            return k;
          }
        }
        return paramArrayOfByte1.length - paramArrayOfByte2.length;
      }
    }
    
    @VisibleForTesting
    static enum UnsafeComparator
      implements Comparator
    {
      INSTANCE;
      
      static final boolean littleEndian;
      static final Unsafe theUnsafe;
      static final int BYTE_ARRAY_BASE_OFFSET;
      
      private static Unsafe getUnsafe()
      {
        try
        {
          return Unsafe.getUnsafe();
        }
        catch (SecurityException localSecurityException)
        {
          try
          {
            (Unsafe)AccessController.doPrivileged(new PrivilegedExceptionAction()
            {
              public Unsafe run()
                throws Exception
              {
                Class localClass = Unsafe.class;
                for (Field localField : localClass.getDeclaredFields())
                {
                  localField.setAccessible(true);
                  Object localObject = localField.get(null);
                  if (localClass.isInstance(localObject)) {
                    return (Unsafe)localClass.cast(localObject);
                  }
                }
                throw new NoSuchFieldError("the Unsafe");
              }
            });
          }
          catch (PrivilegedActionException localPrivilegedActionException)
          {
            throw new RuntimeException("Could not initialize intrinsics", localPrivilegedActionException.getCause());
          }
        }
      }
      
      public int compare(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
      {
        int i = Math.min(paramArrayOfByte1.length, paramArrayOfByte2.length);
        int j = i / 8;
        for (int k = 0; k < j * 8; k += 8)
        {
          long l1 = theUnsafe.getLong(paramArrayOfByte1, BYTE_ARRAY_BASE_OFFSET + k);
          long l2 = theUnsafe.getLong(paramArrayOfByte2, BYTE_ARRAY_BASE_OFFSET + k);
          long l3 = l1 ^ l2;
          if (l3 != 0L)
          {
            if (!littleEndian) {
              return UnsignedLongs.compare(l1, l2);
            }
            int n = 0;
            int i2 = (int)l3;
            if (i2 == 0)
            {
              i2 = (int)(l3 >>> 32);
              n = 32;
            }
            int i1 = i2 << 16;
            if (i1 == 0) {
              n += 16;
            } else {
              i2 = i1;
            }
            i1 = i2 << 8;
            if (i1 == 0) {
              n += 8;
            }
            return (int)((l1 >>> n & 0xFF) - (l2 >>> n & 0xFF));
          }
        }
        for (k = j * 8; k < i; k++)
        {
          int m = UnsignedBytes.compare(paramArrayOfByte1[k], paramArrayOfByte2[k]);
          if (m != 0) {
            return m;
          }
        }
        return paramArrayOfByte1.length - paramArrayOfByte2.length;
      }
      
      static
      {
        littleEndian = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN);
        theUnsafe = getUnsafe();
        BYTE_ARRAY_BASE_OFFSET = theUnsafe.arrayBaseOffset(byte[].class);
        if (theUnsafe.arrayIndexScale(byte[].class) != 1) {
          throw new AssertionError();
        }
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\primitives\UnsignedBytes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */