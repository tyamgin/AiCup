package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

@Beta
public abstract class AbstractScheduledService
  implements Service
{
  private static final Logger logger = Logger.getLogger(AbstractScheduledService.class.getName());
  private final AbstractService delegate = new AbstractService()
  {
    private volatile Future runningTask;
    private volatile ScheduledExecutorService executorService;
    private final ReentrantLock lock = new ReentrantLock();
    private final Runnable task = new Runnable()
    {
      public void run()
      {
        AbstractScheduledService.1.this.lock.lock();
        try
        {
          AbstractScheduledService.this.runOneIteration();
        }
        catch (Throwable localThrowable)
        {
          try
          {
            AbstractScheduledService.this.shutDown();
          }
          catch (Exception localException)
          {
            AbstractScheduledService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", localException);
          }
          AbstractScheduledService.1.this.notifyFailed(localThrowable);
          throw Throwables.propagate(localThrowable);
        }
        finally
        {
          AbstractScheduledService.1.this.lock.unlock();
        }
      }
    };
    
    protected final void doStart()
    {
      this.executorService = AbstractScheduledService.this.executor();
      this.executorService.execute(new Runnable()
      {
        public void run()
        {
          AbstractScheduledService.1.this.lock.lock();
          try
          {
            AbstractScheduledService.this.startUp();
            AbstractScheduledService.1.this.runningTask = AbstractScheduledService.this.scheduler().schedule(AbstractScheduledService.this.delegate, AbstractScheduledService.1.this.executorService, AbstractScheduledService.1.this.task);
            AbstractScheduledService.1.this.notifyStarted();
          }
          catch (Throwable localThrowable)
          {
            AbstractScheduledService.1.this.notifyFailed(localThrowable);
            throw Throwables.propagate(localThrowable);
          }
          finally
          {
            AbstractScheduledService.1.this.lock.unlock();
          }
        }
      });
    }
    
    protected final void doStop()
    {
      this.runningTask.cancel(false);
      this.executorService.execute(new Runnable()
      {
        public void run()
        {
          try
          {
            AbstractScheduledService.1.this.lock.lock();
            try
            {
              if (AbstractScheduledService.1.this.state() != Service.State.STOPPING) {
                return;
              }
              AbstractScheduledService.this.shutDown();
            }
            finally
            {
              AbstractScheduledService.1.this.lock.unlock();
            }
            AbstractScheduledService.1.this.notifyStopped();
          }
          catch (Throwable localThrowable)
          {
            AbstractScheduledService.1.this.notifyFailed(localThrowable);
            throw Throwables.propagate(localThrowable);
          }
        }
      });
    }
  };
  
  protected abstract void runOneIteration()
    throws Exception;
  
  protected void startUp()
    throws Exception
  {}
  
  protected void shutDown()
    throws Exception
  {}
  
  protected abstract Scheduler scheduler();
  
  protected ScheduledExecutorService executor()
  {
    final ScheduledExecutorService localScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory()
    {
      public Thread newThread(Runnable paramAnonymousRunnable)
      {
        return MoreExecutors.newThread(AbstractScheduledService.this.serviceName(), paramAnonymousRunnable);
      }
    });
    addListener(new Service.Listener()
    {
      public void starting() {}
      
      public void running() {}
      
      public void stopping(Service.State paramAnonymousState) {}
      
      public void terminated(Service.State paramAnonymousState)
      {
        localScheduledExecutorService.shutdown();
      }
      
      public void failed(Service.State paramAnonymousState, Throwable paramAnonymousThrowable)
      {
        localScheduledExecutorService.shutdown();
      }
    }, MoreExecutors.sameThreadExecutor());
    return localScheduledExecutorService;
  }
  
  protected String serviceName()
  {
    return getClass().getSimpleName();
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
  
  @Beta
  public static abstract class CustomScheduler
    extends AbstractScheduledService.Scheduler
  {
    public CustomScheduler()
    {
      super();
    }
    
    final Future schedule(AbstractService paramAbstractService, ScheduledExecutorService paramScheduledExecutorService, Runnable paramRunnable)
    {
      ReschedulableCallable localReschedulableCallable = new ReschedulableCallable(paramAbstractService, paramScheduledExecutorService, paramRunnable);
      localReschedulableCallable.reschedule();
      return localReschedulableCallable;
    }
    
    protected abstract Schedule getNextSchedule()
      throws Exception;
    
    @Beta
    protected static final class Schedule
    {
      private final long delay;
      private final TimeUnit unit;
      
      public Schedule(long paramLong, TimeUnit paramTimeUnit)
      {
        this.delay = paramLong;
        this.unit = ((TimeUnit)Preconditions.checkNotNull(paramTimeUnit));
      }
    }
    
    private class ReschedulableCallable
      extends ForwardingFuture
      implements Callable
    {
      private final Runnable wrappedRunnable;
      private final ScheduledExecutorService executor;
      private final AbstractService service;
      private final ReentrantLock lock = new ReentrantLock();
      private Future currentFuture;
      
      ReschedulableCallable(AbstractService paramAbstractService, ScheduledExecutorService paramScheduledExecutorService, Runnable paramRunnable)
      {
        this.wrappedRunnable = paramRunnable;
        this.executor = paramScheduledExecutorService;
        this.service = paramAbstractService;
      }
      
      public Void call()
        throws Exception
      {
        this.wrappedRunnable.run();
        reschedule();
        return null;
      }
      
      public void reschedule()
      {
        this.lock.lock();
        try
        {
          if ((this.currentFuture == null) || (!this.currentFuture.isCancelled()))
          {
            AbstractScheduledService.CustomScheduler.Schedule localSchedule = AbstractScheduledService.CustomScheduler.this.getNextSchedule();
            this.currentFuture = this.executor.schedule(this, localSchedule.delay, localSchedule.unit);
          }
        }
        catch (Throwable localThrowable)
        {
          this.service.notifyFailed(localThrowable);
        }
        finally
        {
          this.lock.unlock();
        }
      }
      
      public boolean cancel(boolean paramBoolean)
      {
        this.lock.lock();
        try
        {
          boolean bool = this.currentFuture.cancel(paramBoolean);
          return bool;
        }
        finally
        {
          this.lock.unlock();
        }
      }
      
      protected Future delegate()
      {
        throw new UnsupportedOperationException("Only cancel is supported by this future");
      }
    }
  }
  
  public static abstract class Scheduler
  {
    public static Scheduler newFixedDelaySchedule(long paramLong1, long paramLong2, final TimeUnit paramTimeUnit)
    {
      new Scheduler(paramLong1)
      {
        public Future schedule(AbstractService paramAnonymousAbstractService, ScheduledExecutorService paramAnonymousScheduledExecutorService, Runnable paramAnonymousRunnable)
        {
          return paramAnonymousScheduledExecutorService.scheduleWithFixedDelay(paramAnonymousRunnable, this.val$initialDelay, paramTimeUnit, this.val$unit);
        }
      };
    }
    
    public static Scheduler newFixedRateSchedule(long paramLong1, long paramLong2, final TimeUnit paramTimeUnit)
    {
      new Scheduler(paramLong1)
      {
        public Future schedule(AbstractService paramAnonymousAbstractService, ScheduledExecutorService paramAnonymousScheduledExecutorService, Runnable paramAnonymousRunnable)
        {
          return paramAnonymousScheduledExecutorService.scheduleAtFixedRate(paramAnonymousRunnable, this.val$initialDelay, paramTimeUnit, this.val$unit);
        }
      };
    }
    
    abstract Future schedule(AbstractService paramAbstractService, ScheduledExecutorService paramScheduledExecutorService, Runnable paramRunnable);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\AbstractScheduledService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */