package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.Serializable;
import java.util.Map;

@GwtCompatible(emulated=true)
public abstract class CacheLoader
{
  public abstract Object load(Object paramObject)
    throws Exception;
  
  @GwtIncompatible("Futures")
  public ListenableFuture reload(Object paramObject1, Object paramObject2)
    throws Exception
  {
    Preconditions.checkNotNull(paramObject1);
    Preconditions.checkNotNull(paramObject2);
    return Futures.immediateFuture(load(paramObject1));
  }
  
  public Map loadAll(Iterable paramIterable)
    throws Exception
  {
    throw new UnsupportedLoadingOperationException();
  }
  
  @Beta
  public static CacheLoader from(Function paramFunction)
  {
    return new FunctionToCacheLoader(paramFunction);
  }
  
  @Beta
  public static CacheLoader from(Supplier paramSupplier)
  {
    return new SupplierToCacheLoader(paramSupplier);
  }
  
  public static final class InvalidCacheLoadException
    extends RuntimeException
  {
    public InvalidCacheLoadException(String paramString)
    {
      super();
    }
  }
  
  static final class UnsupportedLoadingOperationException
    extends UnsupportedOperationException
  {}
  
  private static final class SupplierToCacheLoader
    extends CacheLoader
    implements Serializable
  {
    private final Supplier computingSupplier;
    private static final long serialVersionUID = 0L;
    
    public SupplierToCacheLoader(Supplier paramSupplier)
    {
      this.computingSupplier = ((Supplier)Preconditions.checkNotNull(paramSupplier));
    }
    
    public Object load(Object paramObject)
    {
      Preconditions.checkNotNull(paramObject);
      return this.computingSupplier.get();
    }
  }
  
  private static final class FunctionToCacheLoader
    extends CacheLoader
    implements Serializable
  {
    private final Function computingFunction;
    private static final long serialVersionUID = 0L;
    
    public FunctionToCacheLoader(Function paramFunction)
    {
      this.computingFunction = ((Function)Preconditions.checkNotNull(paramFunction));
    }
    
    public Object load(Object paramObject)
    {
      return this.computingFunction.apply(Preconditions.checkNotNull(paramObject));
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\cache\CacheLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */