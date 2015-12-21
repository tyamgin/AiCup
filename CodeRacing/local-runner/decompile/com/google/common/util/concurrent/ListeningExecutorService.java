package com.google.common.util.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public abstract interface ListeningExecutorService
  extends ExecutorService
{
  public abstract ListenableFuture submit(Callable paramCallable);
  
  public abstract ListenableFuture submit(Runnable paramRunnable);
  
  public abstract ListenableFuture submit(Runnable paramRunnable, Object paramObject);
  
  public abstract List invokeAll(Collection paramCollection)
    throws InterruptedException;
  
  public abstract List invokeAll(Collection paramCollection, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException;
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\ListeningExecutorService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */