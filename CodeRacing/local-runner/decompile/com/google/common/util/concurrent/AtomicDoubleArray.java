package com.google.common.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLongArray;

public class AtomicDoubleArray
  implements Serializable
{
  private static final long serialVersionUID = 0L;
  private transient AtomicLongArray longs;
  
  public AtomicDoubleArray(int paramInt)
  {
    this.longs = new AtomicLongArray(paramInt);
  }
  
  public AtomicDoubleArray(double[] paramArrayOfDouble)
  {
    int i = paramArrayOfDouble.length;
    long[] arrayOfLong = new long[i];
    for (int j = 0; j < i; j++) {
      arrayOfLong[j] = Double.doubleToRawLongBits(paramArrayOfDouble[j]);
    }
    this.longs = new AtomicLongArray(arrayOfLong);
  }
  
  public final int length()
  {
    return this.longs.length();
  }
  
  public final double get(int paramInt)
  {
    return Double.longBitsToDouble(this.longs.get(paramInt));
  }
  
  public final void set(int paramInt, double paramDouble)
  {
    long l = Double.doubleToRawLongBits(paramDouble);
    this.longs.set(paramInt, l);
  }
  
  public final void lazySet(int paramInt, double paramDouble)
  {
    set(paramInt, paramDouble);
  }
  
  public final double getAndSet(int paramInt, double paramDouble)
  {
    long l = Double.doubleToRawLongBits(paramDouble);
    return Double.longBitsToDouble(this.longs.getAndSet(paramInt, l));
  }
  
  public final boolean compareAndSet(int paramInt, double paramDouble1, double paramDouble2)
  {
    return this.longs.compareAndSet(paramInt, Double.doubleToRawLongBits(paramDouble1), Double.doubleToRawLongBits(paramDouble2));
  }
  
  public final boolean weakCompareAndSet(int paramInt, double paramDouble1, double paramDouble2)
  {
    return this.longs.weakCompareAndSet(paramInt, Double.doubleToRawLongBits(paramDouble1), Double.doubleToRawLongBits(paramDouble2));
  }
  
  public final double getAndAdd(int paramInt, double paramDouble)
  {
    for (;;)
    {
      long l1 = this.longs.get(paramInt);
      double d1 = Double.longBitsToDouble(l1);
      double d2 = d1 + paramDouble;
      long l2 = Double.doubleToRawLongBits(d2);
      if (this.longs.compareAndSet(paramInt, l1, l2)) {
        return d1;
      }
    }
  }
  
  public double addAndGet(int paramInt, double paramDouble)
  {
    for (;;)
    {
      long l1 = this.longs.get(paramInt);
      double d1 = Double.longBitsToDouble(l1);
      double d2 = d1 + paramDouble;
      long l2 = Double.doubleToRawLongBits(d2);
      if (this.longs.compareAndSet(paramInt, l1, l2)) {
        return d2;
      }
    }
  }
  
  public String toString()
  {
    int i = length() - 1;
    if (i == -1) {
      return "[]";
    }
    StringBuilder localStringBuilder = new StringBuilder(19 * (i + 1));
    localStringBuilder.append('[');
    for (int j = 0;; j++)
    {
      localStringBuilder.append(Double.longBitsToDouble(this.longs.get(j)));
      if (j == i) {
        return ']';
      }
      localStringBuilder.append(',').append(' ');
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    int i = length();
    paramObjectOutputStream.writeInt(i);
    for (int j = 0; j < i; j++) {
      paramObjectOutputStream.writeDouble(get(j));
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    this.longs = new AtomicLongArray(i);
    for (int j = 0; j < i; j++) {
      set(j, paramObjectInputStream.readDouble());
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\AtomicDoubleArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */