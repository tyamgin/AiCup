package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Queues;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;

@Beta
@Singleton
public final class ServiceManager
{
  private static final Logger logger = Logger.getLogger(ServiceManager.class.getName());
  private final ServiceManagerState state;
  private final ImmutableMap services;
  
  public ServiceManager(Iterable paramIterable)
  {
    ImmutableList localImmutableList = ImmutableList.copyOf(paramIterable);
    this.state = new ServiceManagerState(localImmutableList.size());
    ImmutableMap.Builder localBuilder = ImmutableMap.builder();
    ListeningExecutorService localListeningExecutorService = MoreExecutors.sameThreadExecutor();
    Iterator localIterator = localImmutableList.iterator();
    while (localIterator.hasNext())
    {
      Service localService = (Service)localIterator.next();
      ServiceListener localServiceListener = new ServiceListener(localService, this.state);
      localService.addListener(localServiceListener, localListeningExecutorService);
      Preconditions.checkArgument(localService.state() == Service.State.NEW, "Can only manage NEW services, %s", new Object[] { localService });
      localBuilder.put(localService, localServiceListener);
    }
    this.services = localBuilder.build();
  }
  
  @Inject
  ServiceManager(Set paramSet)
  {
    this(paramSet);
  }
  
  public void addListener(Listener paramListener, Executor paramExecutor)
  {
    this.state.addListener(paramListener, paramExecutor);
  }
  
  public ServiceManager startAsync()
  {
    Iterator localIterator = this.services.entrySet().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (Map.Entry)localIterator.next();
      Service localService = (Service)((Map.Entry)localObject).getKey();
      Service.State localState = localService.state();
      Preconditions.checkState(localState == Service.State.NEW, "Service %s is %s, cannot start it.", new Object[] { localService, localState });
    }
    localIterator = this.services.values().iterator();
    while (localIterator.hasNext())
    {
      localObject = (ServiceListener)localIterator.next();
      ((ServiceListener)localObject).start();
    }
    return this;
  }
  
  public void awaitHealthy()
  {
    this.state.awaitHealthy();
    Preconditions.checkState(isHealthy(), "Expected to be healthy after starting");
  }
  
  public void awaitHealthy(long paramLong, TimeUnit paramTimeUnit)
    throws TimeoutException
  {
    if (!this.state.awaitHealthy(paramLong, paramTimeUnit)) {
      throw new TimeoutException("Timeout waiting for the services to become healthy.");
    }
    Preconditions.checkState(isHealthy(), "Expected to be healthy after starting");
  }
  
  public ServiceManager stopAsync()
  {
    Iterator localIterator = this.services.keySet().iterator();
    while (localIterator.hasNext())
    {
      Service localService = (Service)localIterator.next();
      localService.stop();
    }
    return this;
  }
  
  public void awaitStopped()
  {
    this.state.awaitStopped();
  }
  
  public void awaitStopped(long paramLong, TimeUnit paramTimeUnit)
    throws TimeoutException
  {
    if (!this.state.awaitStopped(paramLong, paramTimeUnit)) {
      throw new TimeoutException("Timeout waiting for the services to stop.");
    }
  }
  
  public boolean isHealthy()
  {
    Iterator localIterator = this.services.keySet().iterator();
    while (localIterator.hasNext())
    {
      Service localService = (Service)localIterator.next();
      if (!localService.isRunning()) {
        return false;
      }
    }
    return true;
  }
  
  public ImmutableMultimap servicesByState()
  {
    ImmutableMultimap.Builder localBuilder = ImmutableMultimap.builder();
    Iterator localIterator = this.services.keySet().iterator();
    while (localIterator.hasNext())
    {
      Service localService = (Service)localIterator.next();
      localBuilder.put(localService.state(), localService);
    }
    return localBuilder.build();
  }
  
  public ImmutableMap startupTimes()
  {
    HashMap localHashMap = Maps.newHashMapWithExpectedSize(this.services.size());
    Object localObject1 = this.services.entrySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      localObject3 = ((Service)((Map.Entry)localObject2).getKey()).state();
      if ((localObject3 != Service.State.NEW) && (localObject3 != Service.State.STARTING)) {
        localHashMap.put(((Map.Entry)localObject2).getKey(), Long.valueOf(((ServiceListener)((Map.Entry)localObject2).getValue()).startupTimeMillis()));
      }
    }
    localObject1 = Ordering.natural().onResultOf(new Function()
    {
      public Long apply(Map.Entry paramAnonymousEntry)
      {
        return (Long)paramAnonymousEntry.getValue();
      }
    }).sortedCopy(localHashMap.entrySet());
    Object localObject2 = ImmutableMap.builder();
    Object localObject3 = ((List)localObject1).iterator();
    while (((Iterator)localObject3).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject3).next();
      ((ImmutableMap.Builder)localObject2).put(localEntry);
    }
    return ((ImmutableMap.Builder)localObject2).build();
  }
  
  public String toString()
  {
    return Objects.toStringHelper(ServiceManager.class).add("services", this.services.keySet()).toString();
  }
  
  private static final class ListenerExecutorPair
  {
    final ServiceManager.Listener listener;
    final Executor executor;
    
    ListenerExecutorPair(ServiceManager.Listener paramListener, Executor paramExecutor)
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
        ServiceManager.logger.log(Level.SEVERE, "Exception while executing listener " + this.listener + " with executor " + this.executor, localException);
      }
    }
  }
  
  private static final class ServiceListener
    implements Service.Listener
  {
    final Stopwatch watch = new Stopwatch();
    final Service service;
    final ServiceManager.ServiceManagerState state;
    
    ServiceListener(Service paramService, ServiceManager.ServiceManagerState paramServiceManagerState)
    {
      this.service = paramService;
      this.state = paramServiceManagerState;
    }
    
    public void starting()
    {
      startTimer();
    }
    
    public void running()
    {
      this.state.monitor.enter();
      try
      {
        finishedStarting(true);
      }
      finally
      {
        this.state.monitor.leave();
        ServiceManager.ServiceManagerState.access$000(this.state);
      }
    }
    
    public void stopping(Service.State paramState)
    {
      if (paramState == Service.State.STARTING)
      {
        this.state.monitor.enter();
        try
        {
          finishedStarting(false);
        }
        finally
        {
          this.state.monitor.leave();
          ServiceManager.ServiceManagerState.access$000(this.state);
        }
      }
    }
    
    public void terminated(Service.State paramState)
    {
      ServiceManager.logger.info("Service " + this.service + " has terminated. Previous state was " + paramState + " state.");
      this.state.monitor.enter();
      try
      {
        if (paramState == Service.State.NEW)
        {
          startTimer();
          finishedStarting(false);
        }
        ServiceManager.ServiceManagerState.access$200(this.state, this.service);
      }
      finally
      {
        this.state.monitor.leave();
        ServiceManager.ServiceManagerState.access$000(this.state);
      }
    }
    
    public void failed(Service.State paramState, Throwable paramThrowable)
    {
      ServiceManager.logger.log(Level.SEVERE, "Service " + this.service + " has failed in the " + paramState + " state.", paramThrowable);
      this.state.monitor.enter();
      try
      {
        if (paramState == Service.State.STARTING) {
          finishedStarting(false);
        }
        ServiceManager.ServiceManagerState.access$300(this.state, this.service);
      }
      finally
      {
        this.state.monitor.leave();
        ServiceManager.ServiceManagerState.access$000(this.state);
      }
    }
    
    void finishedStarting(boolean paramBoolean)
    {
      synchronized (this.watch)
      {
        this.watch.stop();
        ServiceManager.logger.log(Level.INFO, "Started " + this.service + " in " + startupTimeMillis() + " ms.");
      }
      ServiceManager.ServiceManagerState.access$400(this.state, this.service, paramBoolean);
    }
    
    void start()
    {
      startTimer();
      this.service.start();
    }
    
    void startTimer()
    {
      synchronized (this.watch)
      {
        if (!this.watch.isRunning())
        {
          this.watch.start();
          ServiceManager.logger.log(Level.INFO, "Starting {0}", this.service);
        }
      }
    }
    
    /* Error */
    synchronized long startupTimeMillis()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 27	com/google/common/util/concurrent/ServiceManager$ServiceListener:watch	Lcom/google/common/base/Stopwatch;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 27	com/google/common/util/concurrent/ServiceManager$ServiceListener:watch	Lcom/google/common/base/Stopwatch;
      //   11: getstatic 29	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
      //   14: invokevirtual 33	com/google/common/base/Stopwatch:elapsed	(Ljava/util/concurrent/TimeUnit;)J
      //   17: aload_1
      //   18: monitorexit
      //   19: lreturn
      //   20: astore_2
      //   21: aload_1
      //   22: monitorexit
      //   23: aload_2
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	ServiceListener
      //   5	17	1	Ljava/lang/Object;	Object
      //   20	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
  }
  
  private static final class ServiceManagerState
  {
    final Monitor monitor = new Monitor();
    final int numberOfServices;
    int unstartedServices;
    int unstoppedServices;
    final Monitor.Guard awaitHealthGuard = new Monitor.Guard(this.monitor)
    {
      public boolean isSatisfied()
      {
        return (ServiceManager.ServiceManagerState.this.unstartedServices == 0) || (ServiceManager.ServiceManagerState.this.unstoppedServices != ServiceManager.ServiceManagerState.this.numberOfServices);
      }
    };
    final Monitor.Guard stoppedGuard = new Monitor.Guard(this.monitor)
    {
      public boolean isSatisfied()
      {
        return ServiceManager.ServiceManagerState.this.unstoppedServices == 0;
      }
    };
    final List listeners = Lists.newArrayList();
    final Queue queuedListeners = Queues.newConcurrentLinkedQueue();
    
    ServiceManagerState(int paramInt)
    {
      this.numberOfServices = paramInt;
      this.unstoppedServices = paramInt;
      this.unstartedServices = paramInt;
    }
    
    void addListener(ServiceManager.Listener paramListener, Executor paramExecutor)
    {
      Preconditions.checkNotNull(paramListener, "listener");
      Preconditions.checkNotNull(paramExecutor, "executor");
      this.monitor.enter();
      try
      {
        if ((this.unstartedServices > 0) || (this.unstoppedServices > 0)) {
          this.listeners.add(new ServiceManager.ListenerExecutorPair(paramListener, paramExecutor));
        }
      }
      finally
      {
        this.monitor.leave();
      }
    }
    
    void awaitHealthy()
    {
      this.monitor.enter();
      try
      {
        this.monitor.waitForUninterruptibly(this.awaitHealthGuard);
      }
      finally
      {
        this.monitor.leave();
      }
    }
    
    boolean awaitHealthy(long paramLong, TimeUnit paramTimeUnit)
    {
      this.monitor.enter();
      try
      {
        if (this.monitor.waitForUninterruptibly(this.awaitHealthGuard, paramLong, paramTimeUnit))
        {
          bool = true;
          return bool;
        }
        boolean bool = false;
        return bool;
      }
      finally
      {
        this.monitor.leave();
      }
    }
    
    void awaitStopped()
    {
      this.monitor.enter();
      try
      {
        this.monitor.waitForUninterruptibly(this.stoppedGuard);
      }
      finally
      {
        this.monitor.leave();
      }
    }
    
    boolean awaitStopped(long paramLong, TimeUnit paramTimeUnit)
    {
      this.monitor.enter();
      try
      {
        boolean bool = this.monitor.waitForUninterruptibly(this.stoppedGuard, paramLong, paramTimeUnit);
        return bool;
      }
      finally
      {
        this.monitor.leave();
      }
    }
    
    private void serviceFinishedStarting(Service paramService, boolean paramBoolean)
    {
      Preconditions.checkState(this.unstartedServices > 0, "All services should have already finished starting but %s just finished.", new Object[] { paramService });
      this.unstartedServices -= 1;
      if ((paramBoolean) && (this.unstartedServices == 0) && (this.unstoppedServices == this.numberOfServices))
      {
        Iterator localIterator = this.listeners.iterator();
        while (localIterator.hasNext())
        {
          final ServiceManager.ListenerExecutorPair localListenerExecutorPair = (ServiceManager.ListenerExecutorPair)localIterator.next();
          this.queuedListeners.add(new Runnable()
          {
            public void run()
            {
              localListenerExecutorPair.execute(new Runnable()
              {
                public void run()
                {
                  ServiceManager.ServiceManagerState.3.this.val$pair.listener.healthy();
                }
              });
            }
          });
        }
      }
    }
    
    private void serviceTerminated(Service paramService)
    {
      serviceStopped(paramService);
    }
    
    private void serviceFailed(final Service paramService)
    {
      Iterator localIterator = this.listeners.iterator();
      while (localIterator.hasNext())
      {
        final ServiceManager.ListenerExecutorPair localListenerExecutorPair = (ServiceManager.ListenerExecutorPair)localIterator.next();
        this.queuedListeners.add(new Runnable()
        {
          public void run()
          {
            localListenerExecutorPair.execute(new Runnable()
            {
              public void run()
              {
                ServiceManager.ServiceManagerState.4.this.val$pair.listener.failure(ServiceManager.ServiceManagerState.4.this.val$service);
              }
            });
          }
        });
      }
      serviceStopped(paramService);
    }
    
    private void serviceStopped(Service paramService)
    {
      Preconditions.checkState(this.unstoppedServices > 0, "All services should have already stopped but %s just stopped.", new Object[] { paramService });
      this.unstoppedServices -= 1;
      if (this.unstoppedServices == 0)
      {
        Preconditions.checkState(this.unstartedServices == 0, "All services are stopped but %d services haven't finished starting", new Object[] { Integer.valueOf(this.unstartedServices) });
        Iterator localIterator = this.listeners.iterator();
        while (localIterator.hasNext())
        {
          final ServiceManager.ListenerExecutorPair localListenerExecutorPair = (ServiceManager.ListenerExecutorPair)localIterator.next();
          this.queuedListeners.add(new Runnable()
          {
            public void run()
            {
              localListenerExecutorPair.execute(new Runnable()
              {
                public void run()
                {
                  ServiceManager.ServiceManagerState.5.this.val$pair.listener.stopped();
                }
              });
            }
          });
        }
        this.listeners.clear();
      }
    }
    
    private void executeListeners()
    {
      Preconditions.checkState(!this.monitor.isOccupiedByCurrentThread(), "It is incorrect to execute listeners with the monitor held.");
      synchronized (this.queuedListeners)
      {
        Runnable localRunnable;
        while ((localRunnable = (Runnable)this.queuedListeners.poll()) != null) {
          localRunnable.run();
        }
      }
    }
  }
  
  @Beta
  public static abstract interface Listener
  {
    public abstract void healthy();
    
    public abstract void stopped();
    
    public abstract void failure(Service paramService);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\ServiceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */