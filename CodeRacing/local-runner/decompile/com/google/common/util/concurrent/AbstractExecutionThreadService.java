package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Throwables;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

@Beta
public abstract class AbstractExecutionThreadService
  implements Service
{
  private static final Logger logger = Logger.getLogger(AbstractExecutionThreadService.class.getName());
  private final Service delegate = new AbstractService()
  {
    protected final void doStart()
    {
      AbstractExecutionThreadService.this.executor().execute(new Runnable()
      {
        public void run()
        {
          try
          {
            AbstractExecutionThreadService.this.startUp();
            AbstractExecutionThreadService.1.this.notifyStarted();
            if (AbstractExecutionThreadService.1.this.isRunning()) {
              try
              {
                AbstractExecutionThreadService.this.run();
              }
              catch (Throwable localThrowable1)
              {
                try
                {
                  AbstractExecutionThreadService.this.shutDown();
                }
                catch (Exception localException)
                {
                  AbstractExecutionThreadService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", localException);
                }
                throw localThrowable1;
              }
            }
            AbstractExecutionThreadService.this.shutDown();
            AbstractExecutionThreadService.1.this.notifyStopped();
          }
          catch (Throwable localThrowable2)
          {
            AbstractExecutionThreadService.1.this.notifyFailed(localThrowable2);
            throw Throwables.propagate(localThrowable2);
          }
        }
      });
    }
    
    protected void doStop()
    {
      AbstractExecutionThreadService.this.triggerShutdown();
    }
  };
  
  protected void startUp()
    throws Exception
  {}
  
  protected abstract void run()
    throws Exception;
  
  protected void shutDown()
    throws Exception
  {}
  
  protected void triggerShutdown() {}
  
  protected Executor executor()
  {
    new Executor()
    {
      public void execute(Runnable paramAnonymousRunnable)
      {
        MoreExecutors.newThread(AbstractExecutionThreadService.this.serviceName(), paramAnonymousRunnable).start();
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


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\AbstractExecutionThreadService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */