package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Supplier;
import java.util.concurrent.atomic.AtomicLong;

@GwtCompatible(emulated=true)
final class LongAddables
{
  private static final Supplier SUPPLIER;
  
  public static LongAddable create()
  {
    return (LongAddable)SUPPLIER.get();
  }
  
  static
  {
    Object localObject;
    try
    {
      new LongAdder();
      localObject = new Supplier()
      {
        public LongAddable get()
        {
          return new LongAdder();
        }
      };
    }
    catch (Throwable localThrowable)
    {
      localObject = new Supplier()
      {
        public LongAddable get()
        {
          return new LongAddables.PureJavaLongAddable(null);
        }
      };
    }
    SUPPLIER = (Supplier)localObject;
  }
  
  private static final class PureJavaLongAddable
    extends AtomicLong
    implements LongAddable
  {
    public void increment()
    {
      getAndIncrement();
    }
    
    public void add(long paramLong)
    {
      getAndAdd(paramLong);
    }
    
    public long sum()
    {
      return get();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\cache\LongAddables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */