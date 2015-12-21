package com.google.inject.internal;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ConcurrentMap;

public abstract class FailableCache
{
  private final LoadingCache delegate = CacheBuilder.newBuilder().build(new CacheLoader()
  {
    public Object load(Object paramAnonymousObject)
    {
      Errors localErrors = new Errors();
      Object localObject = null;
      try
      {
        localObject = FailableCache.this.create(paramAnonymousObject, localErrors);
      }
      catch (ErrorsException localErrorsException)
      {
        localErrors.merge(localErrorsException.getErrors());
      }
      return localErrors.hasErrors() ? localErrors : localObject;
    }
  });
  
  protected abstract Object create(Object paramObject, Errors paramErrors)
    throws ErrorsException;
  
  public Object get(Object paramObject, Errors paramErrors)
    throws ErrorsException
  {
    Object localObject1 = this.delegate.getUnchecked(paramObject);
    if ((localObject1 instanceof Errors))
    {
      paramErrors.merge((Errors)localObject1);
      throw paramErrors.toException();
    }
    Object localObject2 = localObject1;
    return localObject2;
  }
  
  boolean remove(Object paramObject)
  {
    return this.delegate.asMap().remove(paramObject) != null;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\FailableCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */