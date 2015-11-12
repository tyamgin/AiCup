package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingObject;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class ForwardingFuture
  extends ForwardingObject
  implements Future
{
  protected abstract Future delegate();
  
  public boolean cancel(boolean paramBoolean)
  {
    return delegate().cancel(paramBoolean);
  }
  
  public boolean isCancelled()
  {
    return delegate().isCancelled();
  }
  
  public boolean isDone()
  {
    return delegate().isDone();
  }
  
  public Object get()
    throws InterruptedException, ExecutionException
  {
    return delegate().get();
  }
  
  public Object get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    return delegate().get(paramLong, paramTimeUnit);
  }
  
  public static abstract class SimpleForwardingFuture
    extends ForwardingFuture
  {
    private final Future delegate;
    
    protected SimpleForwardingFuture(Future paramFuture)
    {
      this.delegate = ((Future)Preconditions.checkNotNull(paramFuture));
    }
    
    protected final Future delegate()
    {
      return this.delegate;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\ForwardingFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */