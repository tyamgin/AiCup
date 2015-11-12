package com.google.common.util.concurrent;

import com.google.common.collect.ForwardingObject;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class ForwardingExecutorService
  extends ForwardingObject
  implements ExecutorService
{
  protected abstract ExecutorService delegate();
  
  public boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return delegate().awaitTermination(paramLong, paramTimeUnit);
  }
  
  public List invokeAll(Collection paramCollection)
    throws InterruptedException
  {
    return delegate().invokeAll(paramCollection);
  }
  
  public List invokeAll(Collection paramCollection, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return delegate().invokeAll(paramCollection, paramLong, paramTimeUnit);
  }
  
  public Object invokeAny(Collection paramCollection)
    throws InterruptedException, ExecutionException
  {
    return delegate().invokeAny(paramCollection);
  }
  
  public Object invokeAny(Collection paramCollection, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    return delegate().invokeAny(paramCollection, paramLong, paramTimeUnit);
  }
  
  public boolean isShutdown()
  {
    return delegate().isShutdown();
  }
  
  public boolean isTerminated()
  {
    return delegate().isTerminated();
  }
  
  public void shutdown()
  {
    delegate().shutdown();
  }
  
  public List shutdownNow()
  {
    return delegate().shutdownNow();
  }
  
  public void execute(Runnable paramRunnable)
  {
    delegate().execute(paramRunnable);
  }
  
  public Future submit(Callable paramCallable)
  {
    return delegate().submit(paramCallable);
  }
  
  public Future submit(Runnable paramRunnable)
  {
    return delegate().submit(paramRunnable);
  }
  
  public Future submit(Runnable paramRunnable, Object paramObject)
  {
    return delegate().submit(paramRunnable, paramObject);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\ForwardingExecutorService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */