package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@Beta
@GwtCompatible
public abstract interface LoadingCache
  extends Function, Cache
{
  public abstract Object get(Object paramObject)
    throws ExecutionException;
  
  public abstract Object getUnchecked(Object paramObject);
  
  public abstract ImmutableMap getAll(Iterable paramIterable)
    throws ExecutionException;
  
  public abstract Object apply(Object paramObject);
  
  public abstract void refresh(Object paramObject);
  
  public abstract ConcurrentMap asMap();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\cache\LoadingCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */