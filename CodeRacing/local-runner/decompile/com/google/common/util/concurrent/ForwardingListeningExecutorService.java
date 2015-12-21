package com.google.common.util.concurrent;

import java.util.concurrent.Callable;

public abstract class ForwardingListeningExecutorService
  extends ForwardingExecutorService
  implements ListeningExecutorService
{
  protected abstract ListeningExecutorService delegate();
  
  public ListenableFuture submit(Callable paramCallable)
  {
    return delegate().submit(paramCallable);
  }
  
  public ListenableFuture submit(Runnable paramRunnable)
  {
    return delegate().submit(paramRunnable);
  }
  
  public ListenableFuture submit(Runnable paramRunnable, Object paramObject)
  {
    return delegate().submit(paramRunnable, paramObject);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\ForwardingListeningExecutorService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */