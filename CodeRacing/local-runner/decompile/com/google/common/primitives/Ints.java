package com.google.common.primitives;

import com.google.common.annotations.Beta;
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
public final class Ints
{
  public static final int BYTES = 4;
  public static final int MAX_POWER_OF_TWO = 1073741824;
  
  public static int hashCode(int paramInt)
  {
    return paramInt;
  }
  
  public static int checkedCast(long paramLong)
  {
    int i = (int)paramLong;
    Preconditions.checkArgument(i == paramLong, "Out of range: %s", new Object[] { Long.valueOf(paramLong) });
    return i;
  }
  
  public static int saturatedCast(long paramLong)
  {
    if (paramLong > 2147483647L) {
      return Integer.MAX_VALUE;
    }
    if (paramLong < -2147483648L) {
      return Integer.MIN_VALUE;
    }
    return (int)paramLong;
  }
  
  public static int compare(int paramInt1, int paramInt2)
  {
    return paramInt1 > paramInt2 ? 1 : paramInt1 < paramInt2 ? -1 : 0;
  }
  
  public static boolean contains(int[] paramArrayOfInt, int paramInt)
  {
    for (int k : paramArrayOfInt) {
      if (k == paramInt) {
        return true;
      }
    }
    return false;
  }
  
  public static int indexOf(int[] paramArrayOfInt, int paramInt)
  {
    return indexOf(paramArrayOfInt, paramInt, 0, paramArrayOfInt.length);
  }
  
  private static int indexOf(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
  {
    for (int i = paramInt2; i < paramInt3; i++) {
      if (paramArrayOfInt[i] == paramInt1) {
        return i;
      }
    }
    return -1;
  }
  
  public static int indexOf(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    Preconditions.checkNotNull(paramArrayOfInt1, "array");
    Preconditions.checkNotNull(paramArrayOfInt2, "target");
    if (paramArrayOfInt2.length == 0) {
      return 0;
    }
    label64:
    for (int i = 0; i < paramArrayOfInt1.length - paramArrayOfInt2.length + 1; i++)
    {
      for (int j = 0; j < paramArrayOfInt2.length; j++) {
        if (paramArrayOfInt1[(i + j)] != paramArrayOfInt2[j]) {
          break label64;
        }
      }
      return i;
    }
    return -1;
  }
  
  public static int lastIndexOf(int[] paramArrayOfInt, int paramInt)
  {
    return lastIndexOf(paramArrayOfInt, paramInt, 0, paramArrayOfInt.length);
  }
  
  private static int lastIndexOf(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
  {
    for (int i = paramInt3 - 1; i >= paramInt2; i--) {
      if (paramArrayOfInt[i] == paramInt1) {
        return i;
      }
    }
    return -1;
  }
  
  public static int min(int... paramVarArgs)
  {
    Preconditions.checkArgument(paramVarArgs.length > 0);
    int i = paramVarArgs[0];
    for (int j = 1; j < paramVarArgs.length; j++) {
      if (paramVarArgs[j] < i) {
        i = paramVarArgs[j];
      }
    }
    return i;
  }
  
  public static int max(int... paramVarArgs)
  {
    Preconditions.checkArgument(paramVarArgs.length > 0);
    int i = paramVarArgs[0];
    for (int j = 1; j < paramVarArgs.length; j++) {
      if (paramVarArgs[j] > i) {
        i = paramVarArgs[j];
      }
    }
    return i;
  }
  
  public static int[] concat(int[]... paramVarArgs)
  {
    int i = 0;
    for (Object localObject2 : paramVarArgs) {
      i += localObject2.length;
    }
    ??? = new int[i];
    ??? = 0;
    for (int[] arrayOfInt1 : paramVarArgs)
    {
      System.arraycopy(arrayOfInt1, 0, ???, ???, arrayOfInt1.length);
      ??? += arrayOfInt1.length;
    }
    return (int[])???;
  }
  
  @GwtIncompatible("doesn't work")
  public static byte[] toByteArray(int paramInt)
  {
    return new byte[] { (byte)(paramInt >> 24), (byte)(paramInt >> 16), (byte)(paramInt >> 8), (byte)paramInt };
  }
  
  @GwtIncompatible("doesn't work")
  public static int fromByteArray(byte[] paramArrayOfByte)
  {
    Preconditions.checkArgument(paramArrayOfByte.length >= 4, "array too small: %s < %s", new Object[] { Integer.valueOf(paramArrayOfByte.length), Integer.valueOf(4) });
    return fromBytes(paramArrayOfByte[0], paramArrayOfByte[1], paramArrayOfByte[2], paramArrayOfByte[3]);
  }
  
  @GwtIncompatible("doesn't work")
  public static int fromBytes(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4)
  {
    return paramByte1 << 24 | (paramByte2 & 0xFF) << 16 | (paramByte3 & 0xFF) << 8 | paramByte4 & 0xFF;
  }
  
  public static int[] ensureCapacity(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    Preconditions.checkArgument(paramInt1 >= 0, "Invalid minLength: %s", new Object[] { Integer.valueOf(paramInt1) });
    Preconditions.checkArgument(paramInt2 >= 0, "Invalid padding: %s", new Object[] { Integer.valueOf(paramInt2) });
    return paramArrayOfInt.length < paramInt1 ? copyOf(paramArrayOfInt, paramInt1 + paramInt2) : paramArrayOfInt;
  }
  
  private static int[] copyOf(int[] paramArrayOfInt, int paramInt)
  {
    int[] arrayOfInt = new int[paramInt];
    System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, Math.min(paramArrayOfInt.length, paramInt));
    return arrayOfInt;
  }
  
  public static String join(String paramString, int... paramVarArgs)
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
  
  public static int[] toArray(Collection paramCollection)
  {
    if ((paramCollection instanceof IntArrayAsList)) {
      return ((IntArrayAsList)paramCollection).toIntArray();
    }
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    int[] arrayOfInt = new int[i];
    for (int j = 0; j < i; j++) {
      arrayOfInt[j] = ((Number)Preconditions.checkNotNull(arrayOfObject[j])).intValue();
    }
    return arrayOfInt;
  }
  
  public static List asList(int... paramVarArgs)
  {
    if (paramVarArgs.length == 0) {
      return Collections.emptyList();
    }
    return new IntArrayAsList(paramVarArgs);
  }
  
  @Beta
  @GwtIncompatible("TODO")
  public static Integer tryParse(String paramString)
  {
    return AndroidInteger.tryParse(paramString, 10);
  }
  
  @GwtCompatible
  private static class IntArrayAsList
    extends AbstractList
    implements Serializable, RandomAccess
  {
    final int[] array;
    final int start;
    final int end;
    private static final long serialVersionUID = 0L;
    
    IntArrayAsList(int[] paramArrayOfInt)
    {
      this(paramArrayOfInt, 0, paramArrayOfInt.length);
    }
    
    IntArrayAsList(int[] paramArrayOfInt, int paramInt1, int paramInt2)
    {
      this.array = paramArrayOfInt;
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
    
    public Integer get(int paramInt)
    {
      Preconditions.checkElementIndex(paramInt, size());
      return Integer.valueOf(this.array[(this.start + paramInt)]);
    }
    
    public boolean contains(Object paramObject)
    {
      return ((paramObject instanceof Integer)) && (Ints.indexOf(this.array, ((Integer)paramObject).intValue(), this.start, this.end) != -1);
    }
    
    public int indexOf(Object paramObject)
    {
      if ((paramObject instanceof Integer))
      {
        int i = Ints.indexOf(this.array, ((Integer)paramObject).intValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      }
      return -1;
    }
    
    public int lastIndexOf(Object paramObject)
    {
      if ((paramObject instanceof Integer))
      {
        int i = Ints.lastIndexOf(this.array, ((Integer)paramObject).intValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      }
      return -1;
    }
    
    public Integer set(int paramInt, Integer paramInteger)
    {
      Preconditions.checkElementIndex(paramInt, size());
      int i = this.array[(this.start + paramInt)];
      this.array[(this.start + paramInt)] = ((Integer)Preconditions.checkNotNull(paramInteger)).intValue();
      return Integer.valueOf(i);
    }
    
    public List subList(int paramInt1, int paramInt2)
    {
      int i = size();
      Preconditions.checkPositionIndexes(paramInt1, paramInt2, i);
      if (paramInt1 == paramInt2) {
        return Collections.emptyList();
      }
      return new IntArrayAsList(this.array, this.start + paramInt1, this.start + paramInt2);
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof IntArrayAsList))
      {
        IntArrayAsList localIntArrayAsList = (IntArrayAsList)paramObject;
        int i = size();
        if (localIntArrayAsList.size() != i) {
          return false;
        }
        for (int j = 0; j < i; j++) {
          if (this.array[(this.start + j)] != localIntArrayAsList.array[(localIntArrayAsList.start + j)]) {
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
        i = 31 * i + Ints.hashCode(this.array[j]);
      }
      return i;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(size() * 5);
      localStringBuilder.append('[').append(this.array[this.start]);
      for (int i = this.start + 1; i < this.end; i++) {
        localStringBuilder.append(", ").append(this.array[i]);
      }
      return ']';
    }
    
    int[] toIntArray()
    {
      int i = size();
      int[] arrayOfInt = new int[i];
      System.arraycopy(this.array, this.start, arrayOfInt, 0, i);
      return arrayOfInt;
    }
  }
  
  private static enum LexicographicalComparator
    implements Comparator
  {
    INSTANCE;
    
    public int compare(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    {
      int i = Math.min(paramArrayOfInt1.length, paramArrayOfInt2.length);
      for (int j = 0; j < i; j++)
      {
        int k = Ints.compare(paramArrayOfInt1[j], paramArrayOfInt2[j]);
        if (k != 0) {
          return k;
        }
      }
      return paramArrayOfInt1.length - paramArrayOfInt2.length;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\primitives\Ints.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */