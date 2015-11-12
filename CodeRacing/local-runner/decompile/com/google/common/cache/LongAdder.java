package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@GwtCompatible(emulated=true)
final class LongAdder
  extends Striped64
  implements LongAddable, Serializable
{
  private static final long serialVersionUID = 7249069246863182397L;
  
  final long fn(long paramLong1, long paramLong2)
  {
    return paramLong1 + paramLong2;
  }
  
  public void add(long paramLong)
  {
    Striped64.Cell[] arrayOfCell;
    long l1;
    if (((arrayOfCell = this.cells) != null) || (!casBase(l1 = this.base, l1 + paramLong)))
    {
      boolean bool = true;
      Striped64.HashCode localHashCode;
      int j = (localHashCode = (Striped64.HashCode)threadHashCode.get()).code;
      int i;
      Striped64.Cell localCell;
      long l2;
      if ((arrayOfCell == null) || ((i = arrayOfCell.length) < 1) || ((localCell = arrayOfCell[(i - 1 & j)]) == null) || (!(bool = localCell.cas(l2 = localCell.value, l2 + paramLong)))) {
        retryUpdate(paramLong, localHashCode, bool);
      }
    }
  }
  
  public void increment()
  {
    add(1L);
  }
  
  public void decrement()
  {
    add(-1L);
  }
  
  public long sum()
  {
    long l = this.base;
    Striped64.Cell[] arrayOfCell = this.cells;
    if (arrayOfCell != null)
    {
      int i = arrayOfCell.length;
      for (int j = 0; j < i; j++)
      {
        Striped64.Cell localCell = arrayOfCell[j];
        if (localCell != null) {
          l += localCell.value;
        }
      }
    }
    return l;
  }
  
  public void reset()
  {
    internalReset(0L);
  }
  
  public long sumThenReset()
  {
    long l = this.base;
    Striped64.Cell[] arrayOfCell = this.cells;
    this.base = 0L;
    if (arrayOfCell != null)
    {
      int i = arrayOfCell.length;
      for (int j = 0; j < i; j++)
      {
        Striped64.Cell localCell = arrayOfCell[j];
        if (localCell != null)
        {
          l += localCell.value;
          localCell.value = 0L;
        }
      }
    }
    return l;
  }
  
  public String toString()
  {
    return Long.toString(sum());
  }
  
  public long longValue()
  {
    return sum();
  }
  
  public int intValue()
  {
    return (int)sum();
  }
  
  public float floatValue()
  {
    return (float)sum();
  }
  
  public double doubleValue()
  {
    return sum();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeLong(sum());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    this.busy = 0;
    this.cells = null;
    this.base = paramObjectInputStream.readLong();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\cache\LongAdder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */