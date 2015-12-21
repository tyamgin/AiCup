package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public abstract class AbstractFuture
  implements ListenableFuture
{
  private final Sync sync = new Sync();
  private final ExecutionList executionList = new ExecutionList();
  
  public Object get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, TimeoutException, ExecutionException
  {
    return this.sync.get(paramTimeUnit.toNanos(paramLong));
  }
  
  public Object get()
    throws InterruptedException, ExecutionException
  {
    return this.sync.get();
  }
  
  public boolean isDone()
  {
    return this.sync.isDone();
  }
  
  public boolean isCancelled()
  {
    return this.sync.isCancelled();
  }
  
  public boolean cancel(boolean paramBoolean)
  {
    if (!this.sync.cancel(paramBoolean)) {
      return false;
    }
    this.executionList.execute();
    if (paramBoolean) {
      interruptTask();
    }
    return true;
  }
  
  protected void interruptTask() {}
  
  protected final boolean wasInterrupted()
  {
    return this.sync.wasInterrupted();
  }
  
  public void addListener(Runnable paramRunnable, Executor paramExecutor)
  {
    this.executionList.add(paramRunnable, paramExecutor);
  }
  
  protected boolean set(Object paramObject)
  {
    boolean bool = this.sync.set(paramObject);
    if (bool) {
      this.executionList.execute();
    }
    return bool;
  }
  
  protected boolean setException(Throwable paramThrowable)
  {
    boolean bool = this.sync.setException((Throwable)Preconditions.checkNotNull(paramThrowable));
    if (bool) {
      this.executionList.execute();
    }
    if ((paramThrowable instanceof Error)) {
      throw ((Error)paramThrowable);
    }
    return bool;
  }
  
  static final CancellationException cancellationExceptionWithCause(String paramString, Throwable paramThrowable)
  {
    CancellationException localCancellationException = new CancellationException(paramString);
    localCancellationException.initCause(paramThrowable);
    return localCancellationException;
  }
  
  static final class Sync
    extends AbstractQueuedSynchronizer
  {
    private static final long serialVersionUID = 0L;
    static final int RUNNING = 0;
    static final int COMPLETING = 1;
    static final int COMPLETED = 2;
    static final int CANCELLED = 4;
    static final int INTERRUPTED = 8;
    private Object value;
    private Throwable exception;
    
    protected int tryAcquireShared(int paramInt)
    {
      if (isDone()) {
        return 1;
      }
      return -1;
    }
    
    protected boolean tryReleaseShared(int paramInt)
    {
      setState(paramInt);
      return true;
    }
    
    Object get(long paramLong)
      throws TimeoutException, CancellationException, ExecutionException, InterruptedException
    {
      if (!tryAcquireSharedNanos(-1, paramLong)) {
        throw new TimeoutException("Timeout waiting for task.");
      }
      return getValue();
    }
    
    Object get()
      throws CancellationException, ExecutionException, InterruptedException
    {
      acquireSharedInterruptibly(-1);
      return getValue();
    }
    
    private Object getValue()
      throws CancellationException, ExecutionException
    {
      int i = getState();
      switch (i)
      {
      case 2: 
        if (this.exception != null) {
          throw new ExecutionException(this.exception);
        }
        return this.value;
      case 4: 
      case 8: 
        throw AbstractFuture.cancellationExceptionWithCause("Task was cancelled.", this.exception);
      }
      throw new IllegalStateException("Error, synchronizer in invalid state: " + i);
    }
    
    boolean isDone()
    {
      return (getState() & 0xE) != 0;
    }
    
    boolean isCancelled()
    {
      return (getState() & 0xC) != 0;
    }
    
    boolean wasInterrupted()
    {
      return getState() == 8;
    }
    
    boolean set(Object paramObject)
    {
      return complete(paramObject, null, 2);
    }
    
    boolean setException(Throwable paramThrowable)
    {
      return complete(null, paramThrowable, 2);
    }
    
    boolean cancel(boolean paramBoolean)
    {
      return complete(null, null, paramBoolean ? 8 : 4);
    }
    
    private boolean complete(Object paramObject, Throwable paramThrowable, int paramInt)
    {
      boolean bool = compareAndSetState(0, 1);
      if (bool)
      {
        this.value = paramObject;
        this.exception = ((paramInt & 0xC) != 0 ? new CancellationException("Future.cancel() was called.") : paramThrowable);
        releaseShared(paramInt);
      }
      else if (getState() == 1)
      {
        acquireShared(-1);
      }
      return bool;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\AbstractFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */