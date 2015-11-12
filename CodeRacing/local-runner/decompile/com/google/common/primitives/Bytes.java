package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

@GwtCompatible
public final class Bytes
{
  public static int hashCode(byte paramByte)
  {
    return paramByte;
  }
  
  public static boolean contains(byte[] paramArrayOfByte, byte paramByte)
  {
    for (byte b : paramArrayOfByte) {
      if (b == paramByte) {
        return true;
      }
    }
    return false;
  }
  
  public static int indexOf(byte[] paramArrayOfByte, byte paramByte)
  {
    return indexOf(paramArrayOfByte, paramByte, 0, paramArrayOfByte.length);
  }
  
  private static int indexOf(byte[] paramArrayOfByte, byte paramByte, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i < paramInt2; i++) {
      if (paramArrayOfByte[i] == paramByte) {
        return i;
      }
    }
    return -1;
  }
  
  public static int indexOf(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    Preconditions.checkNotNull(paramArrayOfByte1, "array");
    Preconditions.checkNotNull(paramArrayOfByte2, "target");
    if (paramArrayOfByte2.length == 0) {
      return 0;
    }
    label64:
    for (int i = 0; i < paramArrayOfByte1.length - paramArrayOfByte2.length + 1; i++)
    {
      for (int j = 0; j < paramArrayOfByte2.length; j++) {
        if (paramArrayOfByte1[(i + j)] != paramArrayOfByte2[j]) {
          break label64;
        }
      }
      return i;
    }
    return -1;
  }
  
  public static int lastIndexOf(byte[] paramArrayOfByte, byte paramByte)
  {
    return lastIndexOf(paramArrayOfByte, paramByte, 0, paramArrayOfByte.length);
  }
  
  private static int lastIndexOf(byte[] paramArrayOfByte, byte paramByte, int paramInt1, int paramInt2)
  {
    for (int i = paramInt2 - 1; i >= paramInt1; i--) {
      if (paramArrayOfByte[i] == paramByte) {
        return i;
      }
    }
    return -1;
  }
  
  public static byte[] concat(byte[]... paramVarArgs)
  {
    int i = 0;
    for (Object localObject2 : paramVarArgs) {
      i += localObject2.length;
    }
    ??? = new byte[i];
    ??? = 0;
    for (byte[] arrayOfByte1 : paramVarArgs)
    {
      System.arraycopy(arrayOfByte1, 0, ???, ???, arrayOfByte1.length);
      ??? += arrayOfByte1.length;
    }
    return (byte[])???;
  }
  
  public static byte[] ensureCapacity(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    Preconditions.checkArgument(paramInt1 >= 0, "Invalid minLength: %s", new Object[] { Integer.valueOf(paramInt1) });
    Preconditions.checkArgument(paramInt2 >= 0, "Invalid padding: %s", new Object[] { Integer.valueOf(paramInt2) });
    return paramArrayOfByte.length < paramInt1 ? copyOf(paramArrayOfByte, paramInt1 + paramInt2) : paramArrayOfByte;
  }
  
  private static byte[] copyOf(byte[] paramArrayOfByte, int paramInt)
  {
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, Math.min(paramArrayOfByte.length, paramInt));
    return arrayOfByte;
  }
  
  public static byte[] toArray(Collection paramCollection)
  {
    if ((paramCollection instanceof ByteArrayAsList)) {
      return ((ByteArrayAsList)paramCollection).toByteArray();
    }
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    byte[] arrayOfByte = new byte[i];
    for (int j = 0; j < i; j++) {
      arrayOfByte[j] = ((Number)Preconditions.checkNotNull(arrayOfObject[j])).byteValue();
    }
    return arrayOfByte;
  }
  
  public static List asList(byte... paramVarArgs)
  {
    if (paramVarArgs.length == 0) {
      return Collections.emptyList();
    }
    return new ByteArrayAsList(paramVarArgs);
  }
  
  @GwtCompatible
  private static class ByteArrayAsList
    extends AbstractList
    implements Serializable, RandomAccess
  {
    final byte[] array;
    final int start;
    final int end;
    private static final long serialVersionUID = 0L;
    
    ByteArrayAsList(byte[] paramArrayOfByte)
    {
      this(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    ByteArrayAsList(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      this.array = paramArrayOfByte;
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
    
    public Byte get(int paramInt)
    {
      Preconditions.checkElementIndex(paramInt, size());
      return Byte.valueOf(this.array[(this.start + paramInt)]);
    }
    
    public boolean contains(Object paramObject)
    {
      return ((paramObject instanceof Byte)) && (Bytes.indexOf(this.array, ((Byte)paramObject).byteValue(), this.start, this.end) != -1);
    }
    
    public int indexOf(Object paramObject)
    {
      if ((paramObject instanceof Byte))
      {
        int i = Bytes.indexOf(this.array, ((Byte)paramObject).byteValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      }
      return -1;
    }
    
    public int lastIndexOf(Object paramObject)
    {
      if ((paramObject instanceof Byte))
      {
        int i = Bytes.lastIndexOf(this.array, ((Byte)paramObject).byteValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      }
      return -1;
    }
    
    public Byte set(int paramInt, Byte paramByte)
    {
      Preconditions.checkElementIndex(paramInt, size());
      byte b = this.array[(this.start + paramInt)];
      this.array[(this.start + paramInt)] = ((Byte)Preconditions.checkNotNull(paramByte)).byteValue();
      return Byte.valueOf(b);
    }
    
    public List subList(int paramInt1, int paramInt2)
    {
      int i = size();
      Preconditions.checkPositionIndexes(paramInt1, paramInt2, i);
      if (paramInt1 == paramInt2) {
        return Collections.emptyList();
      }
      return new ByteArrayAsList(this.array, this.start + paramInt1, this.start + paramInt2);
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof ByteArrayAsList))
      {
        ByteArrayAsList localByteArrayAsList = (ByteArrayAsList)paramObject;
        int i = size();
        if (localByteArrayAsList.size() != i) {
          return false;
        }
        for (int j = 0; j < i; j++) {
          if (this.array[(this.start + j)] != localByteArrayAsList.array[(localByteArrayAsList.start + j)]) {
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
        i = 31 * i + Bytes.hashCode(this.array[j]);
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
    
    byte[] toByteArray()
    {
      int i = size();
      byte[] arrayOfByte = new byte[i];
      System.arraycopy(this.array, this.start, arrayOfByte, 0, i);
      return arrayOfByte;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\primitives\Bytes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */