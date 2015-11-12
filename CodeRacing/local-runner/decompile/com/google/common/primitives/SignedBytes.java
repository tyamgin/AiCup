package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;

@GwtCompatible
public final class SignedBytes
{
  public static final byte MAX_POWER_OF_TWO = 64;
  
  public static byte checkedCast(long paramLong)
  {
    byte b = (byte)(int)paramLong;
    Preconditions.checkArgument(b == paramLong, "Out of range: %s", new Object[] { Long.valueOf(paramLong) });
    return b;
  }
  
  public static byte saturatedCast(long paramLong)
  {
    if (paramLong > 127L) {
      return Byte.MAX_VALUE;
    }
    if (paramLong < -128L) {
      return Byte.MIN_VALUE;
    }
    return (byte)(int)paramLong;
  }
  
  public static int compare(byte paramByte1, byte paramByte2)
  {
    return paramByte1 - paramByte2;
  }
  
  public static byte min(byte... paramVarArgs)
  {
    Preconditions.checkArgument(paramVarArgs.length > 0);
    byte b = paramVarArgs[0];
    for (int i = 1; i < paramVarArgs.length; i++) {
      if (paramVarArgs[i] < b) {
        b = paramVarArgs[i];
      }
    }
    return b;
  }
  
  public static byte max(byte... paramVarArgs)
  {
    Preconditions.checkArgument(paramVarArgs.length > 0);
    byte b = paramVarArgs[0];
    for (int i = 1; i < paramVarArgs.length; i++) {
      if (paramVarArgs[i] > b) {
        b = paramVarArgs[i];
      }
    }
    return b;
  }
  
  public static String join(String paramString, byte... paramVarArgs)
  {
    Preconditions.checkNotNull(paramString);
    if (paramVarArgs.length == 0) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder(paramVarArgs.length * 5);
    localStringBuilder.append(paramVarArgs[0]);
    for (int i = 1; i < paramVarArgs.length; i++) {
      localStringBuilder.append(paramString).append(paramVarArgs[i]);
    }
    return localStringBuilder.toString();
  }
  
  public static Comparator lexicographicalComparator()
  {
    return LexicographicalComparator.INSTANCE;
  }
  
  private static enum LexicographicalComparator
    implements Comparator
  {
    INSTANCE;
    
    public int compare(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    {
      int i = Math.min(paramArrayOfByte1.length, paramArrayOfByte2.length);
      for (int j = 0; j < i; j++)
      {
        int k = SignedBytes.compare(paramArrayOfByte1[j], paramArrayOfByte2[j]);
        if (k != 0) {
          return k;
        }
      }
      return paramArrayOfByte1.length - paramArrayOfByte2.length;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\primitives\SignedBytes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */