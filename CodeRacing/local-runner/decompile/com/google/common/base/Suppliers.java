package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@GwtCompatible
public final class Suppliers
{
  public static Supplier compose(Function paramFunction, Supplier paramSupplier)
  {
    Preconditions.checkNotNull(paramFunction);
    Preconditions.checkNotNull(paramSupplier);
    return new SupplierComposition(paramFunction, paramSupplier);
  }
  
  public static Supplier memoize(Supplier paramSupplier)
  {
    return (paramSupplier instanceof MemoizingSupplier) ? paramSupplier : new MemoizingSupplier((Supplier)Preconditions.checkNotNull(paramSupplier));
  }
  
  public static Supplier memoizeWithExpiration(Supplier paramSupplier, long paramLong, TimeUnit paramTimeUnit)
  {
    return new ExpiringMemoizingSupplier(paramSupplier, paramLong, paramTimeUnit);
  }
  
  public static Supplier ofInstance(Object paramObject)
  {
    return new SupplierOfInstance(paramObject);
  }
  
  public static Supplier synchronizedSupplier(Supplier paramSupplier)
  {
    return new ThreadSafeSupplier((Supplier)Preconditions.checkNotNull(paramSupplier));
  }
  
  @Beta
  public static Function supplierFunction()
  {
    return SupplierFunction.INSTANCE;
  }
  
  private static enum SupplierFunction
    implements Function
  {
    INSTANCE;
    
    public Object apply(Supplier paramSupplier)
    {
      return paramSupplier.get();
    }
    
    public String toString()
    {
      return "Suppliers.supplierFunction()";
    }
  }
  
  private static class ThreadSafeSupplier
    implements Supplier, Serializable
  {
    final Supplier delegate;
    private static final long serialVersionUID = 0L;
    
    ThreadSafeSupplier(Supplier paramSupplier)
    {
      this.delegate = paramSupplier;
    }
    
    /* Error */
    public Object get()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	com/google/common/base/Suppliers$ThreadSafeSupplier:delegate	Lcom/google/common/base/Supplier;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 12	com/google/common/base/Suppliers$ThreadSafeSupplier:delegate	Lcom/google/common/base/Supplier;
      //   11: invokeinterface 18 1 0
      //   16: aload_1
      //   17: monitorexit
      //   18: areturn
      //   19: astore_2
      //   20: aload_1
      //   21: monitorexit
      //   22: aload_2
      //   23: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	24	0	this	ThreadSafeSupplier
      //   5	16	1	Ljava/lang/Object;	Object
      //   19	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	18	19	finally
      //   19	22	19	finally
    }
    
    public String toString()
    {
      return "Suppliers.synchronizedSupplier(" + this.delegate + ")";
    }
  }
  
  private static class SupplierOfInstance
    implements Supplier, Serializable
  {
    final Object instance;
    private static final long serialVersionUID = 0L;
    
    SupplierOfInstance(Object paramObject)
    {
      this.instance = paramObject;
    }
    
    public Object get()
    {
      return this.instance;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof SupplierOfInstance))
      {
        SupplierOfInstance localSupplierOfInstance = (SupplierOfInstance)paramObject;
        return Objects.equal(this.instance, localSupplierOfInstance.instance);
      }
      return false;
    }
    
    public int hashCode()
    {
      return Objects.hashCode(new Object[] { this.instance });
    }
    
    public String toString()
    {
      return "Suppliers.ofInstance(" + this.instance + ")";
    }
  }
  
  @VisibleForTesting
  static class ExpiringMemoizingSupplier
    implements Supplier, Serializable
  {
    final Supplier delegate;
    final long durationNanos;
    volatile transient Object value;
    volatile transient long expirationNanos;
    private static final long serialVersionUID = 0L;
    
    ExpiringMemoizingSupplier(Supplier paramSupplier, long paramLong, TimeUnit paramTimeUnit)
    {
      this.delegate = ((Supplier)Preconditions.checkNotNull(paramSupplier));
      this.durationNanos = paramTimeUnit.toNanos(paramLong);
      Preconditions.checkArgument(paramLong > 0L);
    }
    
    public Object get()
    {
      long l1 = this.expirationNanos;
      long l2 = Platform.systemNanoTime();
      if ((l1 == 0L) || (l2 - l1 >= 0L)) {
        synchronized (this)
        {
          if (l1 == this.expirationNanos)
          {
            Object localObject1 = this.delegate.get();
            this.value = localObject1;
            l1 = l2 + this.durationNanos;
            this.expirationNanos = (l1 == 0L ? 1L : l1);
            return localObject1;
          }
        }
      }
      return this.value;
    }
    
    public String toString()
    {
      return "Suppliers.memoizeWithExpiration(" + this.delegate + ", " + this.durationNanos + ", NANOS)";
    }
  }
  
  @VisibleForTesting
  static class MemoizingSupplier
    implements Supplier, Serializable
  {
    final Supplier delegate;
    volatile transient boolean initialized;
    transient Object value;
    private static final long serialVersionUID = 0L;
    
    MemoizingSupplier(Supplier paramSupplier)
    {
      this.delegate = paramSupplier;
    }
    
    public Object get()
    {
      if (!this.initialized) {
        synchronized (this)
        {
          if (!this.initialized)
          {
            Object localObject1 = this.delegate.get();
            this.value = localObject1;
            this.initialized = true;
            return localObject1;
          }
        }
      }
      return this.value;
    }
    
    public String toString()
    {
      return "Suppliers.memoize(" + this.delegate + ")";
    }
  }
  
  private static class SupplierComposition
    implements Supplier, Serializable
  {
    final Function function;
    final Supplier supplier;
    private static final long serialVersionUID = 0L;
    
    SupplierComposition(Function paramFunction, Supplier paramSupplier)
    {
      this.function = paramFunction;
      this.supplier = paramSupplier;
    }
    
    public Object get()
    {
      return this.function.apply(this.supplier.get());
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof SupplierComposition))
      {
        SupplierComposition localSupplierComposition = (SupplierComposition)paramObject;
        return (this.function.equals(localSupplierComposition.function)) && (this.supplier.equals(localSupplierComposition.supplier));
      }
      return false;
    }
    
    public int hashCode()
    {
      return Objects.hashCode(new Object[] { this.function, this.supplier });
    }
    
    public String toString()
    {
      return "Suppliers.compose(" + this.function + ", " + this.supplier + ")";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\Suppliers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */