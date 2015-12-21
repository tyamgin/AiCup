package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

@Beta
public final class JdkFutureAdapters
{
  public static ListenableFuture listenInPoolThread(Future paramFuture)
  {
    if ((paramFuture instanceof ListenableFuture)) {
      return (ListenableFuture)paramFuture;
    }
    return new ListenableFutureAdapter(paramFuture);
  }
  
  public static ListenableFuture listenInPoolThread(Future paramFuture, Executor paramExecutor)
  {
    Preconditions.checkNotNull(paramExecutor);
    if ((paramFuture instanceof ListenableFuture)) {
      return (ListenableFuture)paramFuture;
    }
    return new ListenableFutureAdapter(paramFuture, paramExecutor);
  }
  
  private static class ListenableFutureAdapter
    extends ForwardingFuture
    implements ListenableFuture
  {
    private static final ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ListenableFutureAdapter-thread-%d").build();
    private static final Executor defaultAdapterExecutor = Executors.newCachedThreadPool(threadFactory);
    private final Executor adapterExecutor;
    private final ExecutionList executionList = new ExecutionList();
    private final AtomicBoolean hasListeners = new AtomicBoolean(false);
    private final Future delegate;
    
    ListenableFutureAdapter(Future paramFuture)
    {
      this(paramFuture, defaultAdapterExecutor);
    }
    
    ListenableFutureAdapter(Future paramFuture, Executor paramExecutor)
    {
      this.delegate = ((Future)Preconditions.checkNotNull(paramFuture));
      this.adapterExecutor = ((Executor)Preconditions.checkNotNull(paramExecutor));
    }
    
    protected Future delegate()
    {
      return this.delegate;
    }
    
    public void addListener(Runnable paramRunnable, Executor paramExecutor)
    {
      this.executionList.add(paramRunnable, paramExecutor);
      if (this.hasListeners.compareAndSet(false, true))
      {
        if (this.delegate.isDone())
        {
          this.executionList.execute();
          return;
        }
        this.adapterExecutor.execute(new Runnable()
        {
          public void run()
          {
            try
            {
              JdkFutureAdapters.ListenableFutureAdapter.this.delegate.get();
            }
            catch (Error localError)
            {
              throw localError;
            }
            catch (InterruptedException localInterruptedException)
            {
              Thread.currentThread().interrupt();
              throw new AssertionError(localInterruptedException);
            }
            catch (Throwable localThrowable) {}
            JdkFutureAdapters.ListenableFutureAdapter.this.executionList.execute();
          }
        });
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\JdkFutureAdapters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */