package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Throwables;
import java.util.concurrent.Executor;

@Beta
public abstract class AbstractIdleService
  implements Service
{
  private final Service delegate = new AbstractService()
  {
    protected final void doStart()
    {
      AbstractIdleService.this.executor().execute(new Runnable()
      {
        public void run()
        {
          try
          {
            AbstractIdleService.this.startUp();
            AbstractIdleService.1.this.notifyStarted();
          }
          catch (Throwable localThrowable)
          {
            AbstractIdleService.1.this.notifyFailed(localThrowable);
            throw Throwables.propagate(localThrowable);
          }
        }
      });
    }
    
    protected final void doStop()
    {
      AbstractIdleService.this.executor().execute(new Runnable()
      {
        public void run()
        {
          try
          {
            AbstractIdleService.this.shutDown();
            AbstractIdleService.1.this.notifyStopped();
          }
          catch (Throwable localThrowable)
          {
            AbstractIdleService.1.this.notifyFailed(localThrowable);
            throw Throwables.propagate(localThrowable);
          }
        }
      });
    }
  };
  
  protected abstract void startUp()
    throws Exception;
  
  protected abstract void shutDown()
    throws Exception;
  
  protected Executor executor()
  {
    final Service.State localState = state();
    new Executor()
    {
      public void execute(Runnable paramAnonymousRunnable)
      {
        MoreExecutors.newThread(AbstractIdleService.this.serviceName() + " " + localState, paramAnonymousRunnable).start();
      }
    };
  }
  
  public String toString()
  {
    return serviceName() + " [" + state() + "]";
  }
  
  public final ListenableFuture start()
  {
    return this.delegate.start();
  }
  
  public final Service.State startAndWait()
  {
    return this.delegate.startAndWait();
  }
  
  public final boolean isRunning()
  {
    return this.delegate.isRunning();
  }
  
  public final Service.State state()
  {
    return this.delegate.state();
  }
  
  public final ListenableFuture stop()
  {
    return this.delegate.stop();
  }
  
  public final Service.State stopAndWait()
  {
    return this.delegate.stopAndWait();
  }
  
  public final void addListener(Service.Listener paramListener, Executor paramExecutor)
  {
    this.delegate.addListener(paramListener, paramExecutor);
  }
  
  public final Throwable failureCause()
  {
    return this.delegate.failureCause();
  }
  
  protected String serviceName()
  {
    return getClass().getSimpleName();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\AbstractIdleService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */