package com.google.common.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class AtomicDouble
  extends Number
  implements Serializable
{
  private static final long serialVersionUID = 0L;
  private volatile transient long value;
  private static final AtomicLongFieldUpdater updater = AtomicLongFieldUpdater.newUpdater(AtomicDouble.class, "value");
  
  public AtomicDouble(double paramDouble)
  {
    this.value = Double.doubleToRawLongBits(paramDouble);
  }
  
  public AtomicDouble() {}
  
  public final double get()
  {
    return Double.longBitsToDouble(this.value);
  }
  
  public final void set(double paramDouble)
  {
    long l = Double.doubleToRawLongBits(paramDouble);
    this.value = l;
  }
  
  public final void lazySet(double paramDouble)
  {
    set(paramDouble);
  }
  
  public final double getAndSet(double paramDouble)
  {
    long l = Double.doubleToRawLongBits(paramDouble);
    return Double.longBitsToDouble(updater.getAndSet(this, l));
  }
  
  public final boolean compareAndSet(double paramDouble1, double paramDouble2)
  {
    return updater.compareAndSet(this, Double.doubleToRawLongBits(paramDouble1), Double.doubleToRawLongBits(paramDouble2));
  }
  
  public final boolean weakCompareAndSet(double paramDouble1, double paramDouble2)
  {
    return updater.weakCompareAndSet(this, Double.doubleToRawLongBits(paramDouble1), Double.doubleToRawLongBits(paramDouble2));
  }
  
  public final double getAndAdd(double paramDouble)
  {
    for (;;)
    {
      long l1 = this.value;
      double d1 = Double.longBitsToDouble(l1);
      double d2 = d1 + paramDouble;
      long l2 = Double.doubleToRawLongBits(d2);
      if (updater.compareAndSet(this, l1, l2)) {
        return d1;
      }
    }
  }
  
  public final double addAndGet(double paramDouble)
  {
    for (;;)
    {
      long l1 = this.value;
      double d1 = Double.longBitsToDouble(l1);
      double d2 = d1 + paramDouble;
      long l2 = Double.doubleToRawLongBits(d2);
      if (updater.compareAndSet(this, l1, l2)) {
        return d2;
      }
    }
  }
  
  public String toString()
  {
    return Double.toString(get());
  }
  
  public int intValue()
  {
    return (int)get();
  }
  
  public long longValue()
  {
    return get();
  }
  
  public float floatValue()
  {
    return (float)get();
  }
  
  public double doubleValue()
  {
    return get();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeDouble(get());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    set(paramObjectInputStream.readDouble());
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\AtomicDouble.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */