package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

@Beta
public abstract class AbstractService
  implements Service
{
  private static final Logger logger = Logger.getLogger(AbstractService.class.getName());
  private final ReentrantLock lock = new ReentrantLock();
  private final Transition startup = new Transition(null);
  private final Transition shutdown = new Transition(null);
  private final List listeners = Lists.newArrayList();
  private final Queue queuedListeners = Queues.newConcurrentLinkedQueue();
  private volatile StateSnapshot snapshot = new StateSnapshot(Service.State.NEW);
  
  protected AbstractService()
  {
    addListener(new Service.Listener()
    {
      public void starting() {}
      
      public void running()
      {
        AbstractService.this.startup.set(Service.State.RUNNING);
      }
      
      public void stopping(Service.State paramAnonymousState)
      {
        if (paramAnonymousState == Service.State.STARTING) {
          AbstractService.this.startup.set(Service.State.STOPPING);
        }
      }
      
      public void terminated(Service.State paramAnonymousState)
      {
        if (paramAnonymousState == Service.State.NEW) {
          AbstractService.this.startup.set(Service.State.TERMINATED);
        }
        AbstractService.this.shutdown.set(Service.State.TERMINATED);
      }
      
      public void failed(Service.State paramAnonymousState, Throwable paramAnonymousThrowable)
      {
        switch (AbstractService.7.$SwitchMap$com$google$common$util$concurrent$Service$State[paramAnonymousState.ordinal()])
        {
        case 1: 
          AbstractService.this.startup.setException(paramAnonymousThrowable);
          AbstractService.this.shutdown.setException(new Exception("Service failed to start.", paramAnonymousThrowable));
          break;
        case 2: 
          AbstractService.this.shutdown.setException(new Exception("Service failed while running", paramAnonymousThrowable));
          break;
        case 3: 
          AbstractService.this.shutdown.setException(paramAnonymousThrowable);
          break;
        case 4: 
        case 5: 
        case 6: 
        default: 
          throw new AssertionError("Unexpected from state: " + paramAnonymousState);
        }
      }
    }, MoreExecutors.sameThreadExecutor());
  }
  
  protected abstract void doStart();
  
  protected abstract void doStop();
  
  public final ListenableFuture start()
  {
    this.lock.lock();
    try
    {
      if (this.snapshot.state == Service.State.NEW)
      {
        this.snapshot = new StateSnapshot(Service.State.STARTING);
        starting();
        doStart();
      }
    }
    catch (Throwable localThrowable)
    {
      notifyFailed(localThrowable);
    }
    finally
    {
      this.lock.unlock();
      executeListeners();
    }
    return this.startup;
  }
  
  public final ListenableFuture stop()
  {
    this.lock.lock();
    try
    {
      switch (this.snapshot.state)
      {
      case NEW: 
        this.snapshot = new StateSnapshot(Service.State.TERMINATED);
        terminated(Service.State.NEW);
        break;
      case STARTING: 
        this.snapshot = new StateSnapshot(Service.State.STARTING, true, null);
        stopping(Service.State.STARTING);
        break;
      case RUNNING: 
        this.snapshot = new StateSnapshot(Service.State.STOPPING);
        stopping(Service.State.RUNNING);
        doStop();
        break;
      case STOPPING: 
      case TERMINATED: 
      case FAILED: 
        break;
      default: 
        throw new AssertionError("Unexpected state: " + this.snapshot.state);
      }
    }
    catch (Throwable localThrowable)
    {
      notifyFailed(localThrowable);
    }
    finally
    {
      this.lock.unlock();
      executeListeners();
    }
    return this.shutdown;
  }
  
  public Service.State startAndWait()
  {
    return (Service.State)Futures.getUnchecked(start());
  }
  
  public Service.State stopAndWait()
  {
    return (Service.State)Futures.getUnchecked(stop());
  }
  
  protected final void notifyStarted()
  {
    this.lock.lock();
    try
    {
      if (this.snapshot.state != Service.State.STARTING)
      {
        IllegalStateException localIllegalStateException = new IllegalStateException("Cannot notifyStarted() when the service is " + this.snapshot.state);
        notifyFailed(localIllegalStateException);
        throw localIllegalStateException;
      }
      if (this.snapshot.shutdownWhenStartupFinishes)
      {
        this.snapshot = new StateSnapshot(Service.State.STOPPING);
        doStop();
      }
      else
      {
        this.snapshot = new StateSnapshot(Service.State.RUNNING);
        running();
      }
    }
    finally
    {
      this.lock.unlock();
      executeListeners();
    }
  }
  
  protected final void notifyStopped()
  {
    this.lock.lock();
    try
    {
      if ((this.snapshot.state != Service.State.STOPPING) && (this.snapshot.state != Service.State.RUNNING))
      {
        localObject1 = new IllegalStateException("Cannot notifyStopped() when the service is " + this.snapshot.state);
        notifyFailed((Throwable)localObject1);
        throw ((Throwable)localObject1);
      }
      Object localObject1 = this.snapshot.state;
      this.snapshot = new StateSnapshot(Service.State.TERMINATED);
      terminated((Service.State)localObject1);
    }
    finally
    {
      this.lock.unlock();
      executeListeners();
    }
  }
  
  protected final void notifyFailed(Throwable paramThrowable)
  {
    Preconditions.checkNotNull(paramThrowable);
    this.lock.lock();
    try
    {
      switch (this.snapshot.state)
      {
      case TERMINATED: 
      case NEW: 
        throw new IllegalStateException("Failed while in state:" + this.snapshot.state, paramThrowable);
      case STARTING: 
      case RUNNING: 
      case STOPPING: 
        Service.State localState = this.snapshot.state;
        this.snapshot = new StateSnapshot(Service.State.FAILED, false, paramThrowable);
        failed(localState, paramThrowable);
        break;
      case FAILED: 
        break;
      default: 
        throw new AssertionError("Unexpected state: " + this.snapshot.state);
      }
    }
    finally
    {
      this.lock.unlock();
      executeListeners();
    }
  }
  
  public final boolean isRunning()
  {
    return state() == Service.State.RUNNING;
  }
  
  public final Service.State state()
  {
    return this.snapshot.externalState();
  }
  
  public final Throwable failureCause()
  {
    return this.snapshot.failureCause();
  }
  
  public final void addListener(Service.Listener paramListener, Executor paramExecutor)
  {
    Preconditions.checkNotNull(paramListener, "listener");
    Preconditions.checkNotNull(paramExecutor, "executor");
    this.lock.lock();
    try
    {
      if ((this.snapshot.state != Service.State.TERMINATED) && (this.snapshot.state != Service.State.FAILED)) {
        this.listeners.add(new ListenerExecutorPair(paramListener, paramExecutor));
      }
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  public String toString()
  {
    return getClass().getSimpleName() + " [" + state() + "]";
  }
  
  private void executeListeners()
  {
    if (!this.lock.isHeldByCurrentThread()) {
      synchronized (this.queuedListeners)
      {
        Runnable localRunnable;
        while ((localRunnable = (Runnable)this.queuedListeners.poll()) != null) {
          localRunnable.run();
        }
      }
    }
  }
  
  private void starting()
  {
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      final ListenerExecutorPair localListenerExecutorPair = (ListenerExecutorPair)localIterator.next();
      this.queuedListeners.add(new Runnable()
      {
        public void run()
        {
          localListenerExecutorPair.execute(new Runnable()
          {
            public void run()
            {
              AbstractService.2.this.val$pair.listener.starting();
            }
          });
        }
      });
    }
  }
  
  private void running()
  {
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      final ListenerExecutorPair localListenerExecutorPair = (ListenerExecutorPair)localIterator.next();
      this.queuedListeners.add(new Runnable()
      {
        public void run()
        {
          localListenerExecutorPair.execute(new Runnable()
          {
            public void run()
            {
              AbstractService.3.this.val$pair.listener.running();
            }
          });
        }
      });
    }
  }
  
  private void stopping(final Service.State paramState)
  {
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      final ListenerExecutorPair localListenerExecutorPair = (ListenerExecutorPair)localIterator.next();
      this.queuedListeners.add(new Runnable()
      {
        public void run()
        {
          localListenerExecutorPair.execute(new Runnable()
          {
            public void run()
            {
              AbstractService.4.this.val$pair.listener.stopping(AbstractService.4.this.val$from);
            }
          });
        }
      });
    }
  }
  
  private void terminated(final Service.State paramState)
  {
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      final ListenerExecutorPair localListenerExecutorPair = (ListenerExecutorPair)localIterator.next();
      this.queuedListeners.add(new Runnable()
      {
        public void run()
        {
          localListenerExecutorPair.execute(new Runnable()
          {
            public void run()
            {
              AbstractService.5.this.val$pair.listener.terminated(AbstractService.5.this.val$from);
            }
          });
        }
      });
    }
    this.listeners.clear();
  }
  
  private void failed(final Service.State paramState, final Throwable paramThrowable)
  {
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      final ListenerExecutorPair localListenerExecutorPair = (ListenerExecutorPair)localIterator.next();
      this.queuedListeners.add(new Runnable()
      {
        public void run()
        {
          localListenerExecutorPair.execute(new Runnable()
          {
            public void run()
            {
              AbstractService.6.this.val$pair.listener.failed(AbstractService.6.this.val$from, AbstractService.6.this.val$cause);
            }
          });
        }
      });
    }
    this.listeners.clear();
  }
  
  private static final class StateSnapshot
  {
    final Service.State state;
    final boolean shutdownWhenStartupFinishes;
    final Throwable failure;
    
    StateSnapshot(Service.State paramState)
    {
      this(paramState, false, null);
    }
    
    StateSnapshot(Service.State paramState, boolean paramBoolean, Throwable paramThrowable)
    {
      Preconditions.checkArgument((!paramBoolean) || (paramState == Service.State.STARTING), "shudownWhenStartupFinishes can only be set if state is STARTING. Got %s instead.", new Object[] { paramState });
      Preconditions.checkArgument(((paramThrowable != null ? 1 : 0) ^ (paramState == Service.State.FAILED ? 1 : 0)) == 0, "A failure cause should be set if and only if the state is failed.  Got %s and %s instead.", new Object[] { paramState, paramThrowable });
      this.state = paramState;
      this.shutdownWhenStartupFinishes = paramBoolean;
      this.failure = paramThrowable;
    }
    
    Service.State externalState()
    {
      if ((this.shutdownWhenStartupFinishes) && (this.state == Service.State.STARTING)) {
        return Service.State.STOPPING;
      }
      return this.state;
    }
    
    Throwable failureCause()
    {
      Preconditions.checkState(this.state == Service.State.FAILED, "failureCause() is only valid if the service has failed, service is %s", new Object[] { this.state });
      return this.failure;
    }
  }
  
  private static class ListenerExecutorPair
  {
    final Service.Listener listener;
    final Executor executor;
    
    ListenerExecutorPair(Service.Listener paramListener, Executor paramExecutor)
    {
      this.listener = paramListener;
      this.executor = paramExecutor;
    }
    
    void execute(Runnable paramRunnable)
    {
      try
      {
        this.executor.execute(paramRunnable);
      }
      catch (Exception localException)
      {
        AbstractService.logger.log(Level.SEVERE, "Exception while executing listener " + this.listener + " with executor " + this.executor, localException);
      }
    }
  }
  
  private class Transition
    extends AbstractFuture
  {
    private Transition() {}
    
    public Service.State get(long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException, TimeoutException, ExecutionException
    {
      try
      {
        return (Service.State)super.get(paramLong, paramTimeUnit);
      }
      catch (TimeoutException localTimeoutException)
      {
        throw new TimeoutException(AbstractService.this.toString());
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\AbstractService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */