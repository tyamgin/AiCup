package com.google.common.cache;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Random;
import sun.misc.Unsafe;

abstract class Striped64
  extends Number
{
  static final ThreadHashCode threadHashCode = new ThreadHashCode();
  static final int NCPU = Runtime.getRuntime().availableProcessors();
  volatile transient Cell[] cells;
  volatile transient long base;
  volatile transient int busy;
  private static final Unsafe UNSAFE;
  private static final long baseOffset;
  private static final long busyOffset;
  
  final boolean casBase(long paramLong1, long paramLong2)
  {
    return UNSAFE.compareAndSwapLong(this, baseOffset, paramLong1, paramLong2);
  }
  
  final boolean casBusy()
  {
    return UNSAFE.compareAndSwapInt(this, busyOffset, 0, 1);
  }
  
  abstract long fn(long paramLong1, long paramLong2);
  
  final void retryUpdate(long paramLong, HashCode paramHashCode, boolean paramBoolean)
  {
    int i = paramHashCode.code;
    int j = 0;
    for (;;)
    {
      Cell[] arrayOfCell1;
      int k;
      long l;
      if (((arrayOfCell1 = this.cells) != null) && ((k = arrayOfCell1.length) > 0))
      {
        Cell localCell;
        Object localObject1;
        int n;
        if ((localCell = arrayOfCell1[(k - 1 & i)]) == null)
        {
          if (this.busy == 0)
          {
            localObject1 = new Cell(paramLong);
            if ((this.busy == 0) && (casBusy()))
            {
              n = 0;
              try
              {
                Cell[] arrayOfCell3;
                int i1;
                int i2;
                if (((arrayOfCell3 = this.cells) != null) && ((i1 = arrayOfCell3.length) > 0) && (arrayOfCell3[(i2 = i1 - 1 & i)] == null))
                {
                  arrayOfCell3[i2] = localObject1;
                  n = 1;
                }
              }
              finally
              {
                this.busy = 0;
              }
              if (n == 0) {
                continue;
              }
              break;
            }
          }
          j = 0;
        }
        else if (!paramBoolean)
        {
          paramBoolean = true;
        }
        else
        {
          if (localCell.cas(l = localCell.value, fn(l, paramLong))) {
            break;
          }
          if ((k >= NCPU) || (this.cells != arrayOfCell1))
          {
            j = 0;
          }
          else if (j == 0)
          {
            j = 1;
          }
          else if ((this.busy == 0) && (casBusy()))
          {
            try
            {
              if (this.cells == arrayOfCell1)
              {
                localObject1 = new Cell[k << 1];
                for (n = 0; n < k; n++) {
                  localObject1[n] = arrayOfCell1[n];
                }
                this.cells = ((Cell[])localObject1);
              }
            }
            finally
            {
              this.busy = 0;
            }
            j = 0;
            continue;
          }
        }
        i ^= i << 13;
        i ^= i >>> 17;
        i ^= i << 5;
      }
      else if ((this.busy == 0) && (this.cells == arrayOfCell1) && (casBusy()))
      {
        int m = 0;
        try
        {
          if (this.cells == arrayOfCell1)
          {
            Cell[] arrayOfCell2 = new Cell[2];
            arrayOfCell2[(i & 0x1)] = new Cell(paramLong);
            this.cells = arrayOfCell2;
            m = 1;
          }
        }
        finally
        {
          this.busy = 0;
        }
        if (m != 0) {
          break;
        }
      }
      else
      {
        if (casBase(l = this.base, fn(l, paramLong))) {
          break;
        }
      }
    }
    paramHashCode.code = i;
  }
  
  final void internalReset(long paramLong)
  {
    Cell[] arrayOfCell = this.cells;
    this.base = paramLong;
    if (arrayOfCell != null)
    {
      int i = arrayOfCell.length;
      for (int j = 0; j < i; j++)
      {
        Cell localCell = arrayOfCell[j];
        if (localCell != null) {
          localCell.value = paramLong;
        }
      }
    }
  }
  
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
  
  static
  {
    try
    {
      UNSAFE = getUnsafe();
      Class localClass = Striped64.class;
      baseOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("base"));
      busyOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("busy"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  static final class ThreadHashCode
    extends ThreadLocal
  {
    public Striped64.HashCode initialValue()
    {
      return new Striped64.HashCode();
    }
  }
  
  static final class HashCode
  {
    static final Random rng = new Random();
    int code;
    
    HashCode()
    {
      int i = rng.nextInt();
      this.code = (i == 0 ? 1 : i);
    }
  }
  
  static final class Cell
  {
    volatile long p0;
    volatile long p1;
    volatile long p2;
    volatile long p3;
    volatile long p4;
    volatile long p5;
    volatile long p6;
    volatile long value;
    volatile long q0;
    volatile long q1;
    volatile long q2;
    volatile long q3;
    volatile long q4;
    volatile long q5;
    volatile long q6;
    private static final Unsafe UNSAFE;
    private static final long valueOffset;
    
    Cell(long paramLong)
    {
      this.value = paramLong;
    }
    
    final boolean cas(long paramLong1, long paramLong2)
    {
      return UNSAFE.compareAndSwapLong(this, valueOffset, paramLong1, paramLong2);
    }
    
    static
    {
      try
      {
        UNSAFE = Striped64.access$000();
        Class localClass = Cell.class;
        valueOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("value"));
      }
      catch (Exception localException)
      {
        throw new Error(localException);
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\cache\Striped64.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */