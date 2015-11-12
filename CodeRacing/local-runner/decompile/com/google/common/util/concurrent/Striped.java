package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import com.google.common.math.IntMath;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Beta
public abstract class Striped
{
  private static final Supplier READ_WRITE_LOCK_SUPPLIER = new Supplier()
  {
    public ReadWriteLock get()
    {
      return new ReentrantReadWriteLock();
    }
  };
  private static final int ALL_SET = -1;
  
  public abstract Object get(Object paramObject);
  
  public abstract Object getAt(int paramInt);
  
  abstract int indexFor(Object paramObject);
  
  public abstract int size();
  
  public Iterable bulkGet(Iterable paramIterable)
  {
    Object[] arrayOfObject = Iterables.toArray(paramIterable, Object.class);
    int[] arrayOfInt = new int[arrayOfObject.length];
    for (int i = 0; i < arrayOfObject.length; i++) {
      arrayOfInt[i] = indexFor(arrayOfObject[i]);
    }
    Arrays.sort(arrayOfInt);
    for (i = 0; i < arrayOfObject.length; i++) {
      arrayOfObject[i] = getAt(arrayOfInt[i]);
    }
    List localList = Arrays.asList(arrayOfObject);
    return Collections.unmodifiableList(localList);
  }
  
  public static Striped lock(int paramInt)
  {
    new CompactStriped(paramInt, new Supplier()
    {
      public Lock get()
      {
        return new Striped.PaddedLock();
      }
    }, null);
  }
  
  public static Striped lazyWeakLock(int paramInt)
  {
    new LazyStriped(paramInt, new Supplier()
    {
      public Lock get()
      {
        return new ReentrantLock(false);
      }
    });
  }
  
  public static Striped semaphore(int paramInt1, int paramInt2)
  {
    new CompactStriped(paramInt1, new Supplier()
    {
      public Semaphore get()
      {
        return new Striped.PaddedSemaphore(this.val$permits);
      }
    }, null);
  }
  
  public static Striped lazyWeakSemaphore(int paramInt1, int paramInt2)
  {
    new LazyStriped(paramInt1, new Supplier()
    {
      public Semaphore get()
      {
        return new Semaphore(this.val$permits, false);
      }
    });
  }
  
  public static Striped readWriteLock(int paramInt)
  {
    return new CompactStriped(paramInt, READ_WRITE_LOCK_SUPPLIER, null);
  }
  
  public static Striped lazyWeakReadWriteLock(int paramInt)
  {
    return new LazyStriped(paramInt, READ_WRITE_LOCK_SUPPLIER);
  }
  
  private static int ceilToPowerOfTwo(int paramInt)
  {
    return 1 << IntMath.log2(paramInt, RoundingMode.CEILING);
  }
  
  private static int smear(int paramInt)
  {
    paramInt ^= paramInt >>> 20 ^ paramInt >>> 12;
    return paramInt ^ paramInt >>> 7 ^ paramInt >>> 4;
  }
  
  private static class PaddedSemaphore
    extends Semaphore
  {
    long q1;
    long q2;
    long q3;
    
    PaddedSemaphore(int paramInt)
    {
      super(false);
    }
  }
  
  private static class PaddedLock
    extends ReentrantLock
  {
    long q1;
    long q2;
    long q3;
    
    PaddedLock()
    {
      super();
    }
  }
  
  private static class LazyStriped
    extends Striped.PowerOfTwoStriped
  {
    final ConcurrentMap cache;
    final int size = this.mask == -1 ? Integer.MAX_VALUE : this.mask + 1;
    
    LazyStriped(int paramInt, Supplier paramSupplier)
    {
      super();
      this.cache = new MapMaker().weakValues().makeComputingMap(Functions.forSupplier(paramSupplier));
    }
    
    public Object getAt(int paramInt)
    {
      Preconditions.checkElementIndex(paramInt, size());
      return this.cache.get(Integer.valueOf(paramInt));
    }
    
    public int size()
    {
      return this.size;
    }
  }
  
  private static class CompactStriped
    extends Striped.PowerOfTwoStriped
  {
    private final Object[] array;
    
    private CompactStriped(int paramInt, Supplier paramSupplier)
    {
      super();
      Preconditions.checkArgument(paramInt <= 1073741824, "Stripes must be <= 2^30)");
      this.array = new Object[this.mask + 1];
      for (int i = 0; i < this.array.length; i++) {
        this.array[i] = paramSupplier.get();
      }
    }
    
    public Object getAt(int paramInt)
    {
      return this.array[paramInt];
    }
    
    public int size()
    {
      return this.array.length;
    }
  }
  
  private static abstract class PowerOfTwoStriped
    extends Striped
  {
    final int mask;
    
    PowerOfTwoStriped(int paramInt)
    {
      super();
      Preconditions.checkArgument(paramInt > 0, "Stripes must be positive");
      this.mask = (paramInt > 1073741824 ? -1 : Striped.ceilToPowerOfTwo(paramInt) - 1);
    }
    
    final int indexFor(Object paramObject)
    {
      int i = Striped.smear(paramObject.hashCode());
      return i & this.mask;
    }
    
    public final Object get(Object paramObject)
    {
      return getAt(indexFor(paramObject));
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\Striped.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */