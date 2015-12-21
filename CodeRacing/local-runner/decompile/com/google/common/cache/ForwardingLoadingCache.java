package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;

@Beta
public abstract class ForwardingLoadingCache
  extends ForwardingCache
  implements LoadingCache
{
  protected abstract LoadingCache delegate();
  
  public Object get(Object paramObject)
    throws ExecutionException
  {
    return delegate().get(paramObject);
  }
  
  public Object getUnchecked(Object paramObject)
  {
    return delegate().getUnchecked(paramObject);
  }
  
  public ImmutableMap getAll(Iterable paramIterable)
    throws ExecutionException
  {
    return delegate().getAll(paramIterable);
  }
  
  public Object apply(Object paramObject)
  {
    return delegate().apply(paramObject);
  }
  
  public void refresh(Object paramObject)
  {
    delegate().refresh(paramObject);
  }
  
  @Beta
  public static abstract class SimpleForwardingLoadingCache
    extends ForwardingLoadingCache
  {
    private final LoadingCache delegate;
    
    protected SimpleForwardingLoadingCache(LoadingCache paramLoadingCache)
    {
      this.delegate = ((LoadingCache)Preconditions.checkNotNull(paramLoadingCache));
    }
    
    protected final LoadingCache delegate()
    {
      return this.delegate;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\cache\ForwardingLoadingCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */