package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.security.MessageDigest;

@Beta
public abstract class HashCode
{
  private static final char[] hexDigits = "0123456789abcdef".toCharArray();
  
  public abstract int asInt();
  
  public abstract long asLong();
  
  public abstract long padToLong();
  
  public abstract byte[] asBytes();
  
  public int writeBytesTo(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte = asBytes();
    paramInt2 = Ints.min(new int[] { paramInt2, arrayOfByte.length });
    Preconditions.checkPositionIndexes(paramInt1, paramInt1 + paramInt2, paramArrayOfByte.length);
    System.arraycopy(arrayOfByte, 0, paramArrayOfByte, paramInt1, paramInt2);
    return paramInt2;
  }
  
  public abstract int bits();
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof HashCode))
    {
      HashCode localHashCode = (HashCode)paramObject;
      return MessageDigest.isEqual(asBytes(), localHashCode.asBytes());
    }
    return false;
  }
  
  public int hashCode()
  {
    return asInt();
  }
  
  public String toString()
  {
    byte[] arrayOfByte1 = asBytes();
    StringBuilder localStringBuilder = new StringBuilder(2 * arrayOfByte1.length);
    for (int k : arrayOfByte1) {
      localStringBuilder.append(hexDigits[(k >> 4 & 0xF)]).append(hexDigits[(k & 0xF)]);
    }
    return localStringBuilder.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\hash\HashCode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */