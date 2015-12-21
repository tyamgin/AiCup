package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

@GwtCompatible(emulated=true)
public final class Shorts
{
  public static final int BYTES = 2;
  public static final short MAX_POWER_OF_TWO = 16384;
  
  public static int hashCode(short paramShort)
  {
    return paramShort;
  }
  
  public static short checkedCast(long paramLong)
  {
    short s = (short)(int)paramLong;
    Preconditions.checkArgument(s == paramLong, "Out of range: %s", new Object[] { Long.valueOf(paramLong) });
    return s;
  }
  
  public static short saturatedCast(long paramLong)
  {
    if (paramLong > 32767L) {
      return Short.MAX_VALUE;
    }
    if (paramLong < -32768L) {
      return Short.MIN_VALUE;
    }
    return (short)(int)paramLong;
  }
  
  public static int compare(short paramShort1, short paramShort2)
  {
    return paramShort1 - paramShort2;
  }
  
  public static boolean contains(short[] paramArrayOfShort, short paramShort)
  {
    for (short s : paramArrayOfShort) {
      if (s == paramShort) {
        return true;
      }
    }
    return false;
  }
  
  public static int indexOf(short[] paramArrayOfShort, short paramShort)
  {
    return indexOf(paramArrayOfShort, paramShort, 0, paramArrayOfShort.length);
  }
  
  private static int indexOf(short[] paramArrayOfShort, short paramShort, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i < paramInt2; i++) {
      if (paramArrayOfShort[i] == paramShort) {
        return i;
      }
    }
    return -1;
  }
  
  public static int indexOf(short[] paramArrayOfShort1, short[] paramArrayOfShort2)
  {
    Preconditions.checkNotNull(paramArrayOfShort1, "array");
    Preconditions.checkNotNull(paramArrayOfShort2, "target");
    if (paramArrayOfShort2.length == 0) {
      return 0;
    }
    label64:
    for (int i = 0; i < paramArrayOfShort1.length - paramArrayOfShort2.length + 1; i++)
    {
      for (int j = 0; j < paramArrayOfShort2.length; j++) {
        if (paramArrayOfShort1[(i + j)] != paramArrayOfShort2[j]) {
          break label64;
        }
      }
      return i;
    }
    return -1;
  }
  
  public static int lastIndexOf(short[] paramArrayOfShort, short paramShort)
  {
    return lastIndexOf(paramArrayOfShort, paramShort, 0, paramArrayOfShort.length);
  }
  
  private static int lastIndexOf(short[] paramArrayOfShort, short paramShort, int paramInt1, int paramInt2)
  {
    for (int i = paramInt2 - 1; i >= paramInt1; i--) {
      if (paramArrayOfShort[i] == paramShort) {
        return i;
      }
    }
    return -1;
  }
  
  public static short min(short... paramVarArgs)
  {
    Preconditions.checkArgument(paramVarArgs.length > 0);
    short s = paramVarArgs[0];
    for (int i = 1; i < paramVarArgs.length; i++) {
      if (paramVarArgs[i] < s) {
        s = paramVarArgs[i];
      }
    }
    return s;
  }
  
  public static short max(short... paramVarArgs)
  {
    Preconditions.checkArgument(paramVarArgs.length > 0);
    short s = paramVarArgs[0];
    for (int i = 1; i < paramVarArgs.length; i++) {
      if (paramVarArgs[i] > s) {
        s = paramVarArgs[i];
      }
    }
    return s;
  }
  
  public static short[] concat(short[]... paramVarArgs)
  {
    int i = 0;
    for (Object localObject2 : paramVarArgs) {
      i += localObject2.length;
    }
    ??? = new short[i];
    ??? = 0;
    for (short[] arrayOfShort1 : paramVarArgs)
    {
      System.arraycopy(arrayOfShort1, 0, ???, ???, arrayOfShort1.length);
      ??? += arrayOfShort1.length;
    }
    return (short[])???;
  }
  
  @GwtIncompatible("doesn't work")
  public static byte[] toByteArray(short paramShort)
  {
    return new byte[] { (byte)(paramShort >> 8), (byte)paramShort };
  }
  
  @GwtIncompatible("doesn't work")
  public static short fromByteArray(byte[] paramArrayOfByte)
  {
    Preconditions.checkArgument(paramArrayOfByte.length >= 2, "array too small: %s < %s", new Object[] { Integer.valueOf(paramArrayOfByte.length), Integer.valueOf(2) });
    return fromBytes(paramArrayOfByte[0], paramArrayOfByte[1]);
  }
  
  @GwtIncompatible("doesn't work")
  public static short fromBytes(byte paramByte1, byte paramByte2)
  {
    return (short)(paramByte1 << 8 | paramByte2 & 0xFF);
  }
  
  public static short[] ensureCapacity(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    Preconditions.checkArgument(paramInt1 >= 0, "Invalid minLength: %s", new Object[] { Integer.valueOf(paramInt1) });
    Preconditions.checkArgument(paramInt2 >= 0, "Invalid padding: %s", new Object[] { Integer.valueOf(paramInt2) });
    return paramArrayOfShort.length < paramInt1 ? copyOf(paramArrayOfShort, paramInt1 + paramInt2) : paramArrayOfShort;
  }
  
  private static short[] copyOf(short[] paramArrayOfShort, int paramInt)
  {
    short[] arrayOfShort = new short[paramInt];
    System.arraycopy(paramArrayOfShort, 0, arrayOfShort, 0, Math.min(paramArrayOfShort.length, paramInt));
    return arrayOfShort;
  }
  
  public static String join(String paramString, short... paramVarArgs)
  {
    Preconditions.checkNotNull(paramString);
    if (paramVarArgs.length == 0) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder(paramVarArgs.length * 6);
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
  
  public static short[] toArray(Collection paramCollection)
  {
    if ((paramCollection instanceof ShortArrayAsList)) {
      return ((ShortArrayAsList)paramCollection).toShortArray();
    }
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    short[] arrayOfShort = new short[i];
    for (int j = 0; j < i; j++) {
      arrayOfShort[j] = ((Number)Preconditions.checkNotNull(arrayOfObject[j])).shortValue();
    }
    return arrayOfShort;
  }
  
  public static List asList(short... paramVarArgs)
  {
    if (paramVarArgs.length == 0) {
      return Collections.emptyList();
    }
    return new ShortArrayAsList(paramVarArgs);
  }
  
  @GwtCompatible
  private static class ShortArrayAsList
    extends AbstractList
    implements Serializable, RandomAccess
  {
    final short[] array;
    final int start;
    final int end;
    private static final long serialVersionUID = 0L;
    
    ShortArrayAsList(short[] paramArrayOfShort)
    {
      this(paramArrayOfShort, 0, paramArrayOfShort.length);
    }
    
    ShortArrayAsList(short[] paramArrayOfShort, int paramInt1, int paramInt2)
    {
      this.array = paramArrayOfShort;
      this.start = paramInt1;
      this.end = paramInt2;
    }
    
    public int size()
    {
      return this.end - this.start;
    }
    
    public boolean isEmpty()
    {
      return false;
    }
    
    public Short get(int paramInt)
    {
      Preconditions.checkElementIndex(paramInt, size());
      return Short.valueOf(this.array[(this.start + paramInt)]);
    }
    
    public boolean contains(Object paramObject)
    {
      return ((paramObject instanceof Short)) && (Shorts.indexOf(this.array, ((Short)paramObject).shortValue(), this.start, this.end) != -1);
    }
    
    public int indexOf(Object paramObject)
    {
      if ((paramObject instanceof Short))
      {
        int i = Shorts.indexOf(this.array, ((Short)paramObject).shortValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      }
      return -1;
    }
    
    public int lastIndexOf(Object paramObject)
    {
      if ((paramObject instanceof Short))
      {
        int i = Shorts.lastIndexOf(this.array, ((Short)paramObject).shortValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      }
      return -1;
    }
    
    public Short set(int paramInt, Short paramShort)
    {
      Preconditions.checkElementIndex(paramInt, size());
      short s = this.array[(this.start + paramInt)];
      this.array[(this.start + paramInt)] = ((Short)Preconditions.checkNotNull(paramShort)).shortValue();
      return Short.valueOf(s);
    }
    
    public List subList(int paramInt1, int paramInt2)
    {
      int i = size();
      Preconditions.checkPositionIndexes(paramInt1, paramInt2, i);
      if (paramInt1 == paramInt2) {
        return Collections.emptyList();
      }
      return new ShortArrayAsList(this.array, this.start + paramInt1, this.start + paramInt2);
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof ShortArrayAsList))
      {
        ShortArrayAsList localShortArrayAsList = (ShortArrayAsList)paramObject;
        int i = size();
        if (localShortArrayAsList.size() != i) {
          return false;
        }
        for (int j = 0; j < i; j++) {
          if (this.array[(this.start + j)] != localShortArrayAsList.array[(localShortArrayAsList.start + j)]) {
            return false;
          }
        }
        return true;
      }
      return super.equals(paramObject);
    }
    
    public int hashCode()
    {
      int i = 1;
      for (int j = this.start; j < this.end; j++) {
        i = 31 * i + Shorts.hashCode(this.array[j]);
      }
      return i;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(size() * 6);
      localStringBuilder.append('[').append(this.array[this.start]);
      for (int i = this.start + 1; i < this.end; i++) {
        localStringBuilder.append(", ").append(this.array[i]);
      }
      return ']';
    }
    
    short[] toShortArray()
    {
      int i = size();
      short[] arrayOfShort = new short[i];
      System.arraycopy(this.array, this.start, arrayOfShort, 0, i);
      return arrayOfShort;
    }
  }
  
  private static enum LexicographicalComparator
    implements Comparator
  {
    INSTANCE;
    
    public int compare(short[] paramArrayOfShort1, short[] paramArrayOfShort2)
    {
      int i = Math.min(paramArrayOfShort1.length, paramArrayOfShort2.length);
      for (int j = 0; j < i; j++)
      {
        int k = Shorts.compare(paramArrayOfShort1[j], paramArrayOfShort2[j]);
        if (k != 0) {
          return k;
        }
      }
      return paramArrayOfShort1.length - paramArrayOfShort2.length;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\primitives\Shorts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */