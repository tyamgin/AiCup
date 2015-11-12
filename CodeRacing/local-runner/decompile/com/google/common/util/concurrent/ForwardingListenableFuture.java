package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;

public abstract class ForwardingListenableFuture
  extends ForwardingFuture
  implements ListenableFuture
{
  protected abstract ListenableFuture delegate();
  
  public void addListener(Runnable paramRunnable, Executor paramExecutor)
  {
    delegate().addListener(paramRunnable, paramExecutor);
  }
  
  public static abstract class SimpleForwardingListenableFuture
    extends ForwardingListenableFuture
  {
    private final ListenableFuture delegate;
    
    protected SimpleForwardingListenableFuture(ListenableFuture paramListenableFuture)
    {
      this.delegate = ((ListenableFuture)Preconditions.checkNotNull(paramListenableFuture));
    }
    
    protected final ListenableFuture delegate()
    {
      return this.delegate;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\ForwardingListenableFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */